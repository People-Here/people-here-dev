package com.peoplehere.api.common.data.request;

import com.peoplehere.shared.common.enums.LangCode;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

/**
 * 이메일 인증 번호 요청 DTO
 * @param email
 */
public record MailVerificationRequestDto(@NotNull LangCode langCode, @Email String email) {
}
