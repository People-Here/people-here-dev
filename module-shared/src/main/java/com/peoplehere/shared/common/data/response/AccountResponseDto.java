package com.peoplehere.shared.common.data.response;

import lombok.Builder;

@Builder
public record AccountResponseDto(long id, String accessToken, String refreshToken) {
}
