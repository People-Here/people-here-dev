package com.peoplehere.shared.tour.data.request;

import jakarta.validation.constraints.NotBlank;

public record TourListRequestDto(@NotBlank String keyword) {
}
