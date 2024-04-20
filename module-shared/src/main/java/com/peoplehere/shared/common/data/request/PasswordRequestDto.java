package com.peoplehere.shared.common.data.request;

import static com.peoplehere.shared.common.util.PatternUtils.*;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PasswordRequestDto {

	@NotBlank
	private String email;

	@NotBlank
	@Pattern(
		regexp = PASSWORD_REGEX,
		message = "패스워드 형식을 지켜주세요.")
	private String newPassword;
}
