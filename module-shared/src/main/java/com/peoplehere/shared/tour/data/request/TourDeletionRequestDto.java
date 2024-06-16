package com.peoplehere.shared.tour.data.request;

import jakarta.validation.constraints.NotBlank;

public record TourDeletionRequestDto(long id, @NotBlank String reason) {
}
