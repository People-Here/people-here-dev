package com.peoplehere.shared.common.data.request;

import static com.peoplehere.shared.common.util.PatternUtils.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

// todo: 정규표현식 중복 제거
@Data
public class PasswordRequestDto {

	@NotBlank
	private String email;

	@NotBlank
	@Pattern(
		regexp = PASSWORD_REGEX,
		message = "패스워드 형식을 지켜주세요.")
	private String newPassword;
}
