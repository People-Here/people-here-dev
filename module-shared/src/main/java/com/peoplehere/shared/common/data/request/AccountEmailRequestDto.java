package com.peoplehere.shared.common.data.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AccountEmailRequestDto(@NotBlank @Email(message = "이메일 형식을 지켜주세요.") String email) {
}
