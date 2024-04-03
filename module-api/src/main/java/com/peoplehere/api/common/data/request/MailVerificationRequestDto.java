package com.peoplehere.api.common.data.request;

import jakarta.validation.constraints.Email;

/**
 * 이메일 인증 번호 요청 DTO
 * @param email
 */
public record MailVerificationRequestDto(@Email String email) {
}
