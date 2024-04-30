package com.peoplehere.api.profile.service;

import static com.peoplehere.shared.common.enums.LangCode.*;
import static com.peoplehere.shared.profile.data.request.ProfileTranslateRequestDto.*;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.peoplehere.shared.common.entity.Account;
import com.peoplehere.shared.common.enums.LangCode;
import com.peoplehere.shared.common.enums.Region;
import com.peoplehere.shared.common.event.ProfileTranslatedEvent;
import com.peoplehere.shared.common.repository.AccountRepository;
import com.peoplehere.shared.common.repository.CustomAccountRepository;
import com.peoplehere.shared.common.service.FileService;
import com.peoplehere.shared.common.webhook.AlertWebhook;
import com.peoplehere.shared.profile.data.request.ProfileInfoRequestDto;
import com.peoplehere.shared.profile.data.response.ProfileInfoResponseDto;
import com.peoplehere.shared.profile.entity.AccountInfo;
import com.peoplehere.shared.profile.repository.AccountInfoRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileService {

	private final AccountRepository accountRepository;
	private final CustomAccountRepository customAccountRepository;
	private final AccountInfoRepository accountInfoRepository;
	private final FileService fileService;
	private final AlertWebhook alertWebhook;
	private final ApplicationEventPublisher eventPublisher;

	/**
	 * 프로필 정보 조회
	 * 기기 OS 언어에 따라서 한국인 경우 원문 반환, 그 외의 나라의 경우 영어로 번역된 정보 반환
	 * 영어 반환시 정보 없는경우 원문 반환
	 * @param accountId pk
	 * @param region 지역 정보
	 * @return
	 */
	@Transactional(readOnly = true)
	public ProfileInfoResponseDto getProfileInfo(Long accountId, Region region) {
		try {

			LangCode langCode = region.getLangCode();
			if (ORIGIN.equals(langCode)) {
				return customAccountRepository.findProfileInfo(accountId, langCode).orElse(null);
			}
			return customAccountRepository.findProfileInfo(accountId, langCode).orElseGet(
				() -> customAccountRepository.findProfileInfo(accountId, ORIGIN).orElse(null));

		} catch (Exception exception) {
			log.error("프로필 정보 조회 중 오류 발생 accountId: [{}], region: [{}]", accountId, region, exception);
			alertWebhook.alertError("프로필 정보 조회 중 오류 발생",
				"accountId: [%s], region: [%s], error: [%s]".formatted(accountId, region, exception.getMessage()));
			throw new RuntimeException("프로필 정보 조회 중 오류 발생 accountId: %s, region: %s".formatted(accountId, region),
				exception);
		}
	}

	/**
	 * 프로필 수정
	 * 원문 먼저 저장 후, 다른 언어 정보로 번역 작업 추가
	 * TODO: 이미지의 경우 비동기로 최적화
	 * @param requestDto 프로필 수정 요청 DTO
	 */
	@Transactional
	public void updateAccountInfo(ProfileInfoRequestDto requestDto) {
		try {
			Account account = accountRepository.findById(requestDto.id())
				.orElseThrow(
					() -> new EntityNotFoundException("해당 계정이 존재하지 않습니다. id: %s".formatted(requestDto.id())));

			// 1. 프로필 이미지 저장 후 url 반환
			if (requestDto.profileImage() != null) {
				String profileImageUrl = fileService.uploadFileAndGetFileInfo(requestDto.id(),
					requestDto.profileImage());
				account.updateProfileImageUrl(profileImageUrl);
			}

			// 2. url + 프로필 정보로 account 업데이트 (국가 정보 파악해야할듯 kr, en) + accountId에 해당하는 정보가 있는지 체크해야할듯
			account.updateInfo(requestDto);

			AccountInfo accountInfo = accountInfoRepository.findByAccountIdAndLangCode(
				account.getId(), ORIGIN).orElse(null);

			if (accountInfo != null) {
				accountInfo.updateOriginalInfo(requestDto);
			} else {
				accountInfo = AccountInfo.builder()
					.accountId(account.getId())
					.langCode(ORIGIN)
					.introduce(requestDto.introduce())
					.favorite(requestDto.favorite())
					.hobby(requestDto.hobby())
					.pet(requestDto.pet())
					.build();
			}

			accountInfoRepository.save(accountInfo);
			// 3. 이벤트 발행
			eventPublisher.publishEvent(new ProfileTranslatedEvent(toTranslateRequestDto(account, accountInfo)));
		} catch (Exception exception) {
			log.error("프로필 수정 요청 중 오류 발생 request: [{}]", requestDto, exception);
			alertWebhook.alertError("프로필 수정 요청 중 오류 발생",
				"request: [%s], error: [%s]".formatted(requestDto, exception.getMessage()));
			throw new RuntimeException("프로필 수정 요청 중 오류 발생: %s".formatted(requestDto), exception);
		}

	}
}
