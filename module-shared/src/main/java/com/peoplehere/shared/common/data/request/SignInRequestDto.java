package com.peoplehere.shared.common.data.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SignInRequestDto {

	@NotBlank
	private String email;
	@NotBlank
	private String password;
}
