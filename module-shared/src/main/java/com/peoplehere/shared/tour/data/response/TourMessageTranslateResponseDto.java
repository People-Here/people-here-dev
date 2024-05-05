package com.peoplehere.shared.tour.data.response;

import com.peoplehere.shared.common.enums.LangCode;

import lombok.Builder;

@Builder
public record TourMessageTranslateResponseDto(String content, LangCode language) {
}
