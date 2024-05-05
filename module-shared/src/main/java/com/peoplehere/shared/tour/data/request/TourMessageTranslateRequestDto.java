package com.peoplehere.shared.tour.data.request;

import com.peoplehere.shared.common.enums.LangCode;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TourMessageTranslateRequestDto(@NotBlank String content, @NotNull LangCode language) {
}
