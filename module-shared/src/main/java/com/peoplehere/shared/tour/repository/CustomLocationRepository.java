package com.peoplehere.shared.tour.repository;

import static com.peoplehere.shared.tour.entity.QLocation.*;
import static com.peoplehere.shared.tour.entity.QLocationInfo.*;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.peoplehere.shared.common.enums.LangCode;
import com.peoplehere.shared.tour.data.response.PlaceInfoResponseDto;
import com.peoplehere.shared.tour.data.response.QPlaceInfoResponseDto;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@RequiredArgsConstructor
public class CustomLocationRepository {

	private final JPAQueryFactory queryFactory;

	public Optional<PlaceInfoResponseDto> findPlaceInfoResponseDto(String placeId, LangCode langCode) {
		return Optional.ofNullable(queryFactory
			.select(new QPlaceInfoResponseDto(
				location.placeId,
				locationInfo.name,
				locationInfo.address,
				location.latitude,
				location.longitude))
			.from(location)
			.join(locationInfo).on(location.placeId.eq(locationInfo.placeId))
			.where(location.placeId.eq(placeId)
				.and(locationInfo.langCode.eq(langCode)))
			.fetchOne());
	}
}
