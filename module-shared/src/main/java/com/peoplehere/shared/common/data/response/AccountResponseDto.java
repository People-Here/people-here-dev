package com.peoplehere.shared.common.data.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record AccountResponseDto(long id, String accessToken, String refreshToken) {
}
