package com.peoplehere.api.common.data.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * 메일 인증 코드 검증 요청 DTO
 */
public record MailVerifyRequestDto(@Email String email, @NotBlank String code) {
}
