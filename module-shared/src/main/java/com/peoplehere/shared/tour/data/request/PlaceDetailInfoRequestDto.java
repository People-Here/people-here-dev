package com.peoplehere.shared.tour.data.request;

import com.peoplehere.shared.common.enums.LangCode;

public record PlaceDetailInfoRequestDto(String placeId, LangCode langCode) {

	public static PlaceDetailInfoRequestDto toDetailRequestDto(PlaceInfoRequestDto requestDto) {
		return new PlaceDetailInfoRequestDto(requestDto.placeId(), requestDto.region().getMapLangCode());
	}
}
