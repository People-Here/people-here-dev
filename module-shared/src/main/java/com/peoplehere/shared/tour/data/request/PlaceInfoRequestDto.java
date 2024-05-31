package com.peoplehere.shared.tour.data.request;

import com.peoplehere.shared.common.enums.PageType;
import com.peoplehere.shared.common.enums.Region;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PlaceInfoRequestDto(@NotBlank String placeId, @NotNull Region region, @NotNull PageType type) {
}
