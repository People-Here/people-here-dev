package com.peoplehere.api.common.controller;

import static com.peoplehere.shared.common.util.PatternUtils.*;
import static org.springframework.http.HttpStatus.*;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.peoplehere.api.common.annotation.CheckAbusing;
import com.peoplehere.api.common.annotation.CheckEmailVerificationLimit;
import com.peoplehere.api.common.annotation.CheckEmailVerifyLimit;
import com.peoplehere.api.common.config.authorize.UpdateProfileAuthorize;
import com.peoplehere.api.common.data.request.MailVerificationRequestDto;
import com.peoplehere.api.common.data.request.MailVerifyRequestDto;
import com.peoplehere.api.common.data.response.MailVerificationResponseDto;
import com.peoplehere.api.common.exception.ClientBindException;
import com.peoplehere.api.common.exception.DuplicateException;
import com.peoplehere.api.common.service.AccountService;
import com.peoplehere.api.common.service.VerifyService;
import com.peoplehere.shared.common.data.request.AccountEmailRequestDto;
import com.peoplehere.shared.common.data.request.AccountNameRequestDto;
import com.peoplehere.shared.common.data.request.AlarmConsentRequestDto;
import com.peoplehere.shared.common.data.request.PasswordRequestDto;
import com.peoplehere.shared.common.data.request.SignInRequestDto;
import com.peoplehere.shared.common.data.request.SignUpRequestDto;
import com.peoplehere.shared.common.data.request.TokenRequestDto;
import com.peoplehere.shared.common.data.response.AccountResponseDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/account")
public class AccountController {

	private final AccountService accountService;
	private final VerifyService verifyService;

	/**
	 * client 회원가입
	 * @param requestDto 회원가입 요청 정보
	 * @param result 바인딩 결과
	 * @return
	 * @throws ClientBindException client 바인딩 오류
	 */
	@PostMapping("/sign-up")
	public ResponseEntity<String> signUp(@Validated @RequestBody SignUpRequestDto requestDto,
		BindingResult result) throws
		ClientBindException {

		if (result.hasErrors()) {
			throw new ClientBindException(result);
		}
		accountService.signUp(requestDto);
		log.info("client: {} 회원가입 성공", requestDto.getEmail());
		return ResponseEntity.ok().body("success");
	}

	/**
	 * client 로그인
	 * @param requestDto 로그인 요청 정보
	 * @param result 바인딩 결과
	 * @return 로그인 성공 시 토큰 반환
	 * @throws ClientBindException client 바인딩 오류
	 */
	@PostMapping("/sign-in")
	public ResponseEntity<AccountResponseDto> signIn(@Validated @RequestBody SignInRequestDto requestDto,
		BindingResult result) throws
		ClientBindException {

		if (result.hasErrors()) {
			throw new ClientBindException(result);
		}
		AccountResponseDto responseDto = accountService.signIn(requestDto);
		log.debug("client: {} 로그인 성공", requestDto.getEmail());
		return ResponseEntity.ok().body(responseDto);
	}

	/**
	 * 비밀번호 재설정
	 * @param requestDto 비밀번호 재설정 요청 정보
	 * @param result 바인딩 결과
	 * @return
	 * @throws ClientBindException client 바인딩 오류
	 */
	@PutMapping("/password")
	public ResponseEntity<String> modifyPassword(@Validated @RequestBody PasswordRequestDto requestDto,
		BindingResult result) throws
		ClientBindException {

		if (result.hasErrors()) {
			throw new ClientBindException(result);
		}
		accountService.updatePassword(requestDto);
		return ResponseEntity.ok().body("success");
	}

	/**
	 * 이메일 유효성(중복, 패턴) 체크
	 * @param email 이메일
	 * @return
	 */
	@GetMapping("/email/check")
	public ResponseEntity<String> checkEmailDuplicate(@RequestParam String email) {
		if (!EMAIL_PATTERN.matcher(email).matches()) {
			log.error("이메일 형식 오류: {}", email);
			return ResponseEntity.badRequest().build();
		}
		if (accountService.checkEmailExist(email)) {
			throw new DuplicateException(email);
		}
		return ResponseEntity.ok().body("success");
	}

	/**
	 * 비밀번호 재설정을 위한 이메일 유효성(db에 존재하는지) 체크
	 * @param email 이메일
	 * @return
	 */
	@GetMapping("/email/exist")
	public ResponseEntity<String> checkEmailExist(@RequestParam String email) {
		if (!EMAIL_PATTERN.matcher(email).matches()) {
			log.error("이메일 형식 오류: {}", email);
			return ResponseEntity.badRequest().build();
		}
		if (accountService.checkEmailExist(email)) {
			return ResponseEntity.ok().body("success");
		}
		return ResponseEntity.status(NOT_FOUND).body("해당 이메일: [%s]이 존재하지 않습니다".formatted(email));
	}

	/**
	 * 알람 동의 여부를 저장함
	 * @param requestDto 알람 동의 여부
	 * @return
	 */
	@PostMapping("/alarm")
	public ResponseEntity<String> modifyAlarmConsent(Principal principal,
		@Validated @RequestBody AlarmConsentRequestDto requestDto, BindingResult result) throws BindException {
		if (result.hasErrors()) {
			throw new BindException(result);
		}
		accountService.updateAlarmConsent(principal.getName(), requestDto);
		return ResponseEntity.ok().body("success");
	}

	/**
	 * 토큰 재발급
	 * accessToken의 만료 유무 확인 후 만료시 재발급
	 * @param requestDto 토큰 재발급 요청 정보
	 * @return
	 */
	@PostMapping("/token")
	public ResponseEntity<String> reissueToken(@Validated @RequestBody TokenRequestDto requestDto,
		BindingResult result) throws ClientBindException {
		if (result.hasErrors()) {
			throw new ClientBindException(result);
		}
		try {
			return ResponseEntity.ok(
				accountService.reissueToken(requestDto.getAccessToken(), requestDto.getRefreshToken()));
		} catch (Exception exception) {
			log.error("토큰 재발급 실패", exception);
			return ResponseEntity.badRequest().build();
		}
	}

	/**
	 * 이메일 인증 번호 요청
	 * @param requestDto 이메일
	 * @param result
	 * @return 인증번호 만료시간
	 * @throws ClientBindException
	 */
	@CheckAbusing
	@CheckEmailVerificationLimit
	@PostMapping("/email/verification")
	public ResponseEntity<MailVerificationResponseDto> sendEmailVerificationCode(
		@Validated @RequestBody MailVerificationRequestDto requestDto,
		BindingResult result) throws ClientBindException {
		if (result.hasErrors()) {
			throw new ClientBindException(result);
		}
		long start = System.currentTimeMillis();
		MailVerificationResponseDto responseDto = verifyService.sendEmailVerificationCode(requestDto.email());
		log.info("이메일 인증번호 전송 성공 - {}ms, email: {}", System.currentTimeMillis() - start, requestDto.email());
		return ResponseEntity.ok().body(responseDto);
	}

	/**
	 * 이메일 인증 번호 검증
	 * @param requestDto 이메일, 인증번호
	 * @return
	 */
	@CheckEmailVerifyLimit
	@PostMapping("/email/verify")
	public ResponseEntity<Boolean> checkEmailVerifyCode(@Validated @RequestBody MailVerifyRequestDto requestDto) {
		return ResponseEntity.ok().body(verifyService.checkEmailVerifyCode(requestDto));
	}

	@DeleteMapping("/{accountId}")
	public ResponseEntity<String> deleteAccount(@PathVariable long accountId) {
		try {
			accountService.deactivateAccount(accountId);
			return ResponseEntity.ok().body("success");
		} catch (Exception e) {
			log.error("계정: {} 비활성화 중 오류 발생", accountId, e);
			return ResponseEntity.internalServerError().build();
		}
	}

	/**
	 * 유저 이름 수정
	 * @param requestDto 이름 재설정 요청 정보
	 * @param result 바인딩 결과
	 * @return
	 * @throws BindException 바인딩 오류
	 */
	@CheckAbusing
	@UpdateProfileAuthorize
	@PutMapping("/name")
	public ResponseEntity<String> modifyAccountName(@Validated @RequestBody AccountNameRequestDto requestDto,
		Principal principal, BindingResult result) throws
		BindException {

		if (result.hasErrors()) {
			throw new BindException(result);
		}
		accountService.updateName(principal.getName(), requestDto);
		return ResponseEntity.ok().body("success");
	}

	/**
	 * 유저 이메일 재설정
	 * @param requestDto 이메일 재설정 요청 정보
	 * @param result 바인딩 결과
	 * @return
	 * @throws BindException 바인딩 오류
	 */
	@CheckAbusing
	@UpdateProfileAuthorize
	@PutMapping("/email")
	public ResponseEntity<String> modifyAccountEmail(@Validated @RequestBody AccountEmailRequestDto requestDto,
		Principal principal, BindingResult result) throws
		BindException {

		if (result.hasErrors()) {
			throw new BindException(result);
		}
		accountService.updateEmail(principal.getName(), requestDto);
		return ResponseEntity.ok().body("success");
	}
}
