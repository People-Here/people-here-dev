package com.peoplehere.api.common.service;

import static com.peoplehere.shared.common.data.request.SignUpRequestDto.*;
import static com.peoplehere.shared.common.enums.LangCode.*;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.peoplehere.api.common.config.security.Token;
import com.peoplehere.api.common.config.security.TokenProvider;
import com.peoplehere.api.common.exception.AccountIdNotFoundException;
import com.peoplehere.api.common.exception.DuplicateException;
import com.peoplehere.shared.common.data.request.AccountEmailRequestDto;
import com.peoplehere.shared.common.data.request.AccountNameRequestDto;
import com.peoplehere.shared.common.data.request.AlarmConsentRequestDto;
import com.peoplehere.shared.common.data.request.PasswordRequestDto;
import com.peoplehere.shared.common.data.request.SignInRequestDto;
import com.peoplehere.shared.common.data.request.SignUpRequestDto;
import com.peoplehere.shared.common.data.response.AccountResponseDto;
import com.peoplehere.shared.common.entity.Account;
import com.peoplehere.shared.common.entity.Consent;
import com.peoplehere.shared.common.repository.AccountRepository;
import com.peoplehere.shared.common.repository.ConsentRepository;
import com.peoplehere.shared.common.repository.CustomAccountRepository;
import com.peoplehere.shared.common.webhook.AlertWebhook;
import com.peoplehere.shared.profile.entity.AccountInfo;
import com.peoplehere.shared.profile.repository.AccountInfoRepository;
import com.peoplehere.shared.tour.repository.TourRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {

	private final AccountRepository accountRepository;
	private final AccountInfoRepository accountInfoRepository;
	private final CustomAccountRepository customAccountRepository;
	private final ConsentRepository consentRepository;
	private final TourRepository tourRepository;
	private final TokenProvider tokenProvider;
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationManagerBuilder authenticationManagerBuilder;
	private final RedisTaskService redisTaskService;
	private final AlertWebhook alertWebhook;

	/**
	 * 회원가입시 사용자 정보를 저장하고, 동의 정보를 저장한다
	 * @param requestDto
	 */
	@Transactional
	public void signUp(SignUpRequestDto requestDto) {
		if (accountRepository.existsByEmail(requestDto.getEmail())) {
			throw new DuplicateException(requestDto.getEmail());
		}
		String encodedPassword = passwordEncoder.encode(requestDto.getPassword());

		Account account = accountRepository.save(toClientAccount(requestDto, encodedPassword));
		AccountInfo accountInfo = AccountInfo.builder()
			.accountId(account.getId())
			.langCode(ORIGIN)
			.build();
		accountInfoRepository.save(accountInfo);
		consentRepository.save(toConsent(requestDto, account));
	}

	/**
	 * 사용자 로그인을 시도하고, 토큰을 생성 후 redis에 저장하고 반환
	 * TODO: 현재는 자동 로그인으로 구현, 추후에 일반 로그인, 자동 로그인으로 분리될 수 있음
	 * @param requestDto
	 * @return
	 */
	@Transactional
	public AccountResponseDto signIn(SignInRequestDto requestDto) {
		Account account = accountRepository.findByEmail(requestDto.getEmail())
			.orElseThrow(() -> new AccountIdNotFoundException(requestDto.getEmail()));

		if (!account.isActive()) {
			// Reactivate the account
			account.setActive(true);
			accountRepository.save(account);
		}

		Authentication authentication = attemptAuthentication(requestDto);

		Token token = tokenProvider.generateToken(authentication);
		redisTaskService.setRefreshToken(token, authentication.getName());
		return AccountResponseDto.builder()
			.id(account.getId())
			.accessToken(token.accessToken())
			.refreshToken(token.refreshToken())
			.build();
	}

	@Transactional
	public void updatePassword(PasswordRequestDto requestDto) {
		Account account = accountRepository.findByEmail(requestDto.getEmail())
			.orElseThrow(() -> new AccountIdNotFoundException(requestDto.getEmail()));

		account.updatePassword(passwordEncoder.encode(requestDto.getNewPassword()));
	}

	@Transactional(readOnly = true)
	public boolean checkEmailExist(String email) {
		return accountRepository.existsByEmail(email);
	}

	@Transactional
	public void updateAlarmConsent(String userId, AlarmConsentRequestDto requestDto) {
		Account account = accountRepository.findByEmail(userId)
			.orElseThrow(() -> new AccountIdNotFoundException(userId));
		Consent consent = consentRepository.findByAccountId(account.getId())
			.orElseThrow(() -> new AccountIdNotFoundException(userId));

		consent.setAlarmConsent(requestDto.getAlarm(), requestDto.isConsent());
	}

	/**
	 * accessToken의 만료 여부, refreshToken의 유효성을 검사하고, 새로운 accessToken을 발급
	 * @param accessToken
	 * @param refreshToken
	 * @return
	 */
	@Transactional
	public String reissueToken(String accessToken, String refreshToken) {
		if (!tokenProvider.isAccessTokenCanBeReissued(accessToken)) {
			throw new IllegalArgumentException("토큰 재발급에 실패하였습니다.");
		}
		Authentication authentication = tokenProvider.getAuthenticationFromRef(refreshToken);
		return tokenProvider.generateToken(authentication).accessToken();
	}

	/**
	 * 사용자 계정을 비활성화한다
	 * 비활성화된 계정은 스케줄러를 돌며 30일 후 삭제된다
	 * @param accountId
	 *
	 */
	@Transactional
	public void deactivateAccount(long accountId) {
		Account account = accountRepository.findById(accountId)
			.orElseThrow(() -> new EntityNotFoundException("존재하지않는 계정: [%s]".formatted(accountId)));
		account.deactivate();
	}

	/**
	 * 삭제일이 기준일 이전이고 비활성화된 계정을 삭제한다
	 * @param baseDateTime 기준일
	 */
	@Transactional
	public void deleteAccountByBaseDateTime(LocalDateTime baseDateTime) {
		List<Long> staleAccountIdList = customAccountRepository.findAccountIdListToDelete(baseDateTime);

		if (!staleAccountIdList.isEmpty()) {
			log.info("비활성화 계정: {} 삭제", staleAccountIdList);
			long start = System.currentTimeMillis();

			accountRepository.deleteAllById(staleAccountIdList);
			consentRepository.deleteAllByAccountIdIn(staleAccountIdList);
			tourRepository.deleteAllByAccountIdIn(staleAccountIdList);

			long end = System.currentTimeMillis() - start;
			log.info("비활성화 계정 삭제 완료: {}ms", end);
			alertWebhook.alertInfo("비활성화 계정 삭제 완료",
				"삭제된 계정 수: [%s] 실행 시간: [%s]ms".formatted(staleAccountIdList.size(), end));
		}
	}

	/**
	 * 사용자 이름을 변경한다
	 * @param userId
	 * @param requestDto
	 */
	@Transactional
	public void updateName(String userId, AccountNameRequestDto requestDto) {
		Account account = accountRepository.findByEmail(userId)
			.orElseThrow(() -> new AccountIdNotFoundException(userId));
		account.updateName(requestDto);
	}

	/**
	 * 사용자 이메일과 아이디를 변경한다
	 * @param userId
	 * @param requestDto
	 */
	@Transactional
	public void updateEmail(String userId, AccountEmailRequestDto requestDto) {
		Account account = accountRepository.findByEmail(userId)
			.orElseThrow(() -> new AccountIdNotFoundException(userId));
		account.updateEmail(requestDto);
		// redis에 저장된 기존 유저id에 대한 refreshToken 삭제
		redisTaskService.expireRefreshToken(requestDto.email());
	}

	@Transactional(readOnly = true)
	public boolean checkPhoneNumberExist(String phoneNumber) {
		return accountRepository.existsByPhoneNumber(phoneNumber);
	}


	/**
	 * 사용자 인증을 시도하고, 인증된 Authentication 객체를 반환
	 * @param requestDto 사용자 로그인 요청 정보
	 * @return
	 */
	private Authentication attemptAuthentication(SignInRequestDto requestDto) {
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
			requestDto.getEmail(), requestDto.getPassword());
		return authenticationManagerBuilder.getObject().authenticate(authenticationToken);
	}

}
