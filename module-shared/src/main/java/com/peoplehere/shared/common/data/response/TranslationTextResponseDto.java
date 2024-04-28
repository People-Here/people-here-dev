package com.peoplehere.shared.common.data.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TranslationTextResponseDto {

	private List<Translation> translations;

	@Builder
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Translation {

		private String text;

		@JsonProperty("detected_source_language")
		private String detectedSourceLanguage;
	}
}
