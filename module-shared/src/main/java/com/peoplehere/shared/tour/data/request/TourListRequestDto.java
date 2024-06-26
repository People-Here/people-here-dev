package com.peoplehere.shared.tour.data.request;

import com.peoplehere.shared.common.enums.LangCode;
import com.peoplehere.shared.common.enums.Region;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TourListRequestDto(@NotNull Region region, @NotNull LangCode langCode, @NotBlank String keyword) {
}
