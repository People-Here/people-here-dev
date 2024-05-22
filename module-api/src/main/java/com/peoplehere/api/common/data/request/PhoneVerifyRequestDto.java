package com.peoplehere.api.common.data.request;

import com.peoplehere.shared.common.enums.Region;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 전화번호 인증 코드 검증 요청 DTO
 */
public record PhoneVerifyRequestDto(@NotNull Region region, @NotBlank String phoneNumber, @NotBlank String code) {

	public String getVerifyPhoneNumber() {
		return region.getRegionPhoneNumber(phoneNumber);
	}
}
