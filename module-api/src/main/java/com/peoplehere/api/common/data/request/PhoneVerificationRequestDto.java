package com.peoplehere.api.common.data.request;

import jakarta.validation.constraints.NotBlank;

/**
 * 전화번호 인증 번호 요청 DTO
 * @param phoneNumber
 */
public record PhoneVerificationRequestDto(@NotBlank String phoneNumber) {
}
