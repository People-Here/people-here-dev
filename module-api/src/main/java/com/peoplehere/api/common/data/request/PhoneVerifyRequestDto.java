package com.peoplehere.api.common.data.request;

import jakarta.validation.constraints.NotBlank;

/**
 * 전화번호 인증 코드 검증 요청 DTO
 */
public record PhoneVerifyRequestDto(@NotBlank String phoneNumber, @NotBlank String code) {
}
