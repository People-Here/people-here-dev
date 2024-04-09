package com.peoplehere.shared.common.data.request;

import lombok.Data;

@Data
public class TokenRequestDto {
	private String accessToken;
	private String refreshToken;
}
