package com.peoplehere.api.profile.controller;

import java.security.Principal;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.peoplehere.api.common.annotation.CheckAbusing;
import com.peoplehere.api.common.config.authorize.UpdateProfileAuthorize;
import com.peoplehere.api.profile.service.ProfileService;
import com.peoplehere.shared.common.enums.Region;
import com.peoplehere.shared.profile.data.request.ProfileInfoRequestDto;
import com.peoplehere.shared.profile.data.response.ProfileInfoResponseDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class ProfileController {

	private final ProfileService profileService;

	/**
	 * 프로필 정보 조회 - 현재는 원문, 한국어, 영어만 지원
	 * @param accountId 유저 pk
	 * @param region 국가 코드
	 * @return
	 */
	@GetMapping(value = "/{accountId}/{region}")
	public ResponseEntity<ProfileInfoResponseDto> getProfileInfo(@PathVariable Long accountId,
		@PathVariable Region region) {
		return ResponseEntity.ok(profileService.getProfileInfo(accountId, region));
	}

	/**
	 * 프로필 수정
	 * 수정에 대한 어뷰징 방지 및 수정 권한 체크
	 * @param requestDto 수정 요청 DTO
	 * @param principal 유저 정보
	 * @param bindingResult 바인딩 결과
	 * @return 수정 완료
	 * @throws BindException
	 */
	@CheckAbusing
	@UpdateProfileAuthorize
	@PutMapping(value = "", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
	public ResponseEntity<Void> modifyAccountInfo(@ModelAttribute @Validated ProfileInfoRequestDto requestDto,
		Principal principal,
		BindingResult bindingResult) throws BindException {

		if (bindingResult.hasErrors()) {
			throw new BindException(bindingResult);
		}
		log.info("[@{}] 프로필 수정 요청", principal.getName());

		long start = System.currentTimeMillis();
		profileService.updateAccountInfo(requestDto);
		log.info("[@{}] 프로필 수정 완료 {}ms", principal.getName(), System.currentTimeMillis() - start);

		return ResponseEntity.ok().build();
	}
}
