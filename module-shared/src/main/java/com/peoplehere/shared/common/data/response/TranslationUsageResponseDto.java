package com.peoplehere.shared.common.data.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TranslationUsageResponseDto(@JsonProperty("character_count") long character_count,
											@JsonProperty("character_limit") long characterLimit) {
}
