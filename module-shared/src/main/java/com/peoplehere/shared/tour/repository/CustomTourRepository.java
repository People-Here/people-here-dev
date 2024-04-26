package com.peoplehere.shared.tour.repository;

import static com.peoplehere.shared.common.entity.QAccount.*;
import static com.peoplehere.shared.tour.entity.QPlace.*;
import static com.peoplehere.shared.tour.entity.QTour.*;
import static com.peoplehere.shared.tour.entity.QTourImage.*;
import static com.peoplehere.shared.tour.entity.QTourInfo.*;
import static com.querydsl.core.group.GroupBy.*;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.peoplehere.shared.common.enums.LangCode;
import com.peoplehere.shared.tour.data.request.TourListRequestDto;
import com.peoplehere.shared.tour.data.response.TourResponseDto;
import com.peoplehere.shared.tour.entity.Tour;
import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QBean;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@RequiredArgsConstructor
public class CustomTourRepository {

	private final JPAQueryFactory queryFactory;

	public List<TourResponseDto> findTourListByLangCode(LangCode langCode) {
		return findTourWithJoinData()
			.where(tourInfo.langCode.eq(langCode))
			.transform(groupBy(tour.id).list(tourResponseDtoQBean()));
	}

	public List<TourResponseDto> findTourListByKeyword(TourListRequestDto requestDto) {
		BooleanExpression searchCondition = (tourInfo.langCode.eq(requestDto.langCode()))
			.and((place.name.contains(requestDto.keyword())).or(place.address.contains(requestDto.keyword())));

		return findTourWithJoinData()
			.where(searchCondition)
			.transform(groupBy(tour.id).list(tourResponseDtoQBean()));
	}

	private JPAQuery<Tour> findTourWithJoinData() {
		return queryFactory.select(tour)
			.from(tour)
			.leftJoin(tourInfo).on(tour.id.eq(tourInfo.tourId))
			.leftJoin(account).on(tour.accountId.eq(account.id))
			.leftJoin(place).on(tour.placeId.eq(place.placeId))
			.leftJoin(tourImage).on(tour.id.eq(tourImage.tourId))
			.orderBy(tour.id.desc())
			.distinct();
	}

	private QBean<TourResponseDto> tourResponseDtoQBean() {
		return Projections.bean(
			TourResponseDto.class,
			tour.id.as("id"),
			tourInfo.title.as("title"),
			Projections.bean(
				TourResponseDto.PlaceInfo.class,
				place.placeId.as("placeId"),
				place.name.as("name"),
				tour.isDefaultImage.as("isDefaultImage"),
				GroupBy.list(Projections.bean(
					TourResponseDto.PlaceImageInfo.class,
					tourImage.thumbnailUrl.as("placeImageUrl"),
					tourImage.optimizedThumbnailUrl.as("optimizedPlaceImageUrl")
				).skipNulls()).as("imageUrlList"),
				place.district.as("district")
			).as("placeInfo"),
			Projections.bean(
				TourResponseDto.UserInfo.class,
				account.id.as("accountId"),
				account.firstName.as("firstName"),
				account.lastName.as("lastName"),
				account.profileImageUrl.as("profileImageUrl"),
				account.optimizedProfileImageUrl.as("optimizedProfileImageUrl"),
				account.directMessageStatus.as("directMessageStatus")
			).as("userInfo")
		);
	}

}
