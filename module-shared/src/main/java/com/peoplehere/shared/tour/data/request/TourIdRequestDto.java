package com.peoplehere.shared.tour.data.request;

import jakarta.validation.constraints.NotNull;

public record TourIdRequestDto(@NotNull long id) {
}
