package com.peoplehere.api.common.data.request;

import com.peoplehere.shared.common.enums.LangCode;
import com.peoplehere.shared.common.enums.Region;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 전화번호 인증 번호 요청 DTO
 * @param phoneNumber
 */
public record PhoneVerificationRequestDto(@NotNull Region region, @NotBlank String phoneNumber,
											@NotNull LangCode langCode) {

	public String getSendNumber() {
		return region.getRegionPhoneNumber(phoneNumber);
	}
}
