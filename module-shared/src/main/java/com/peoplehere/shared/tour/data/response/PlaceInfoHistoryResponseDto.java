package com.peoplehere.shared.tour.data.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record PlaceInfoHistoryResponseDto(List<PlaceInfoResponseDto> places) {
}
