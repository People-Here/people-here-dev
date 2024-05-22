package com.peoplehere.shared.common.data.response;

import lombok.Builder;

@Builder
public record RegionResponseDto(String countryCode, String englishName, String koreanName, String dialCode) {
}
