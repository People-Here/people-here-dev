package com.peoplehere.shared.common.data.response;

import lombok.Builder;

@Builder
public record LanguageResponseDto(String langCode, String englishName, String koreanName, String code) {
}
