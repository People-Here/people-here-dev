package com.peoplehere.shared.common.data.response;

import lombok.Builder;

@Builder
public record AccountResponseDto(String accessToken, String refreshToken) {
}
