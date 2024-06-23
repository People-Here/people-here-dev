package com.peoplehere.shared.tour.data.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.querydsl.core.annotations.QueryProjection;

import lombok.Builder;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record PlaceInfoResponseDto(String placeId, String name, String address, Double latitude, Double longitude) {

	@QueryProjection
	public PlaceInfoResponseDto {
	}
}
