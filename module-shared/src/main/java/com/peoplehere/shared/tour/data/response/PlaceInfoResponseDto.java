package com.peoplehere.shared.tour.data.response;

import lombok.Builder;

@Builder
public record PlaceInfoResponseDto(String placeId, String name, String address) {
}
