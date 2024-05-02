package com.peoplehere.shared.common.data.request;

import jakarta.validation.constraints.NotBlank;

public record AccountNameRequestDto(@NotBlank String firstName, @NotBlank String lastName) {
}
