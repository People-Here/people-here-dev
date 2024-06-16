package com.peoplehere.shared.common.data.request;

import jakarta.validation.constraints.NotBlank;

public record AccountDeactivateRequestDto(long id, @NotBlank String reason) {
}
