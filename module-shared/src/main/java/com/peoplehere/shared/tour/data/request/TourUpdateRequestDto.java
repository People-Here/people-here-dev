package com.peoplehere.shared.tour.data.request;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.peoplehere.shared.common.annotation.NullableOrNonEmptyList;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TourUpdateRequestDto(@NotNull long id,
									@NotBlank String title, String description,
									@NotBlank String placeId, String theme,
									@NullableOrNonEmptyList List<MultipartFile> images,
									@NotNull boolean isDefaultImage) {
}
