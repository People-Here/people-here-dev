package com.peoplehere.shared.tour.data.request;

import com.peoplehere.shared.common.enums.LangCode;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TourListRequestDto(@NotNull LangCode langCode, @NotBlank String keyword) {
}
