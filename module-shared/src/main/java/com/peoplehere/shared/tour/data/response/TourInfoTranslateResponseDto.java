package com.peoplehere.shared.tour.data.response;

import com.peoplehere.shared.common.enums.LangCode;

import lombok.Builder;

@Builder
public record TourInfoTranslateResponseDto(String title, String description, LangCode langCode) {
}
