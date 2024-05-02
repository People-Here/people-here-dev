package com.peoplehere.shared.tour.data.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record PlaceInfoResponseDto(String placeId, String name, String address) {
}
