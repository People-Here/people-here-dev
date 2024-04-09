package com.peoplehere.shared.tour.data.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public record PlaceInfoListResponseDto(List<Prediction> predictions, String status) {

	public record Prediction(
		String description,
		String placeId,
		StructuredFormatting structuredFormatting) {

		@JsonCreator
		public Prediction(
			@JsonProperty("description") String description,
			@JsonProperty("place_id") String placeId,
			@JsonProperty("structured_formatting") StructuredFormatting structuredFormatting) {
			this.description = description;
			this.placeId = placeId;
			this.structuredFormatting = structuredFormatting;
		}
	}

	public record StructuredFormatting(String mainText) {
		@JsonCreator
		public StructuredFormatting(@JsonProperty("main_text") String mainText) {
			this.mainText = mainText;
		}
	}
}
