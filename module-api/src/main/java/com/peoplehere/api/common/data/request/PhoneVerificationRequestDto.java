package com.peoplehere.api.common.data.request;

import com.peoplehere.shared.common.enums.Region;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 전화번호 인증 번호 요청 DTO
 * @param phoneNumber
 */
public record PhoneVerificationRequestDto(@NotNull Region region, @NotBlank String phoneNumber) {

	public String getPhoneNumber() {
		return region.getDialCodeString() + phoneNumber.substring(1);
	}
}
