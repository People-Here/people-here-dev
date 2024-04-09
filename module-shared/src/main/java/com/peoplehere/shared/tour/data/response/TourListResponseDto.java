package com.peoplehere.shared.tour.data.response;

import java.util.List;

import lombok.Builder;

@Builder
public record TourListResponseDto(List<TourResponseDto> tourList) {
}

