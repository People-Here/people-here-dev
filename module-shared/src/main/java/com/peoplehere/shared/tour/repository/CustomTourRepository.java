package com.peoplehere.shared.tour.repository;

import static com.peoplehere.shared.common.entity.QAccount.*;
import static com.peoplehere.shared.profile.entity.QAccountInfo.*;
import static com.peoplehere.shared.tour.entity.QPlace.*;
import static com.peoplehere.shared.tour.entity.QPlaceInfo.*;
import static com.peoplehere.shared.tour.entity.QTour.*;
import static com.peoplehere.shared.tour.entity.QTourImage.*;
import static com.peoplehere.shared.tour.entity.QTourInfo.*;
import static com.peoplehere.shared.tour.entity.QTourLike.*;
import static com.querydsl.core.group.GroupBy.*;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.peoplehere.shared.common.enums.LangCode;
import com.peoplehere.shared.common.enums.Region;
import com.peoplehere.shared.profile.data.ProfileInfoDto;
import com.peoplehere.shared.tour.data.request.TourListRequestDto;
import com.peoplehere.shared.tour.data.response.TourResponseDto;
import com.peoplehere.shared.tour.entity.Tour;
import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QBean;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@RequiredArgsConstructor
public class CustomTourRepository {

	private final JPAQueryFactory queryFactory;

	public List<TourResponseDto> findTourList(Long accountId, Region region, LangCode langCode) {
		BooleanExpression langCodeCondition = tourInfo.langCode.eq(langCode)
			.and(accountInfo.langCode.eq(langCode));

		return findTourWithJoinData(accountId, region)
			.where(langCodeCondition)
			.transform(groupBy(tour.id).list(tourResponseDtoQBean(accountId)));
	}

	public List<TourResponseDto> findTourListByKeyword(Long accountId, TourListRequestDto requestDto) {
		BooleanExpression langCodeCondition = tourInfo.langCode.eq(requestDto.langCode())
			.and(accountInfo.langCode.eq(requestDto.langCode()));
		BooleanExpression searchCondition = ((placeInfo.name.contains(requestDto.keyword())).or(
			placeInfo.address.contains(requestDto.keyword())));

		return findTourWithJoinData(accountId, requestDto.region())
			.where(langCodeCondition.and(searchCondition))
			.transform(groupBy(tour.id).list(tourResponseDtoQBean(accountId)));
	}

	public Optional<TourResponseDto> findTourDetail(long tourId, Long accountId, Region region, LangCode langCode) {
		BooleanExpression langCodeCondition = tourInfo.langCode.eq(langCode)
			.and(accountInfo.langCode.eq(langCode));

		return findTourWithJoinData(accountId, region)
			.where((tour.id.eq(tourId)).and(langCodeCondition))
			.transform(groupBy(tour.id).list(tourResponseDtoQBean(accountId)))
			.stream()
			.findFirst();
	}

	public List<TourResponseDto> findLikeTourList(Long accountId, Region region, LangCode langCode) {
		BooleanExpression langCodeCondition = tourInfo.langCode.eq(langCode)
			.and(accountInfo.langCode.eq(langCode));
		BooleanExpression likeCondition = tourLike.isLike.eq(true);

		return findTourWithJoinData(accountId, region)
			.where(langCodeCondition.and(likeCondition))
			.transform(groupBy(tour.id).list(tourResponseDtoQBean(accountId)));
	}

	public List<TourResponseDto> findTourListByAccount(Long requesterId, long targetAccountId, Region region,
		LangCode langCode) {
		BooleanExpression langCodeCondition = tourInfo.langCode.eq(langCode)
			.and(accountInfo.langCode.eq(langCode));
		BooleanExpression targetAccountCondition = tour.accountId.eq(targetAccountId);

		return findTourWithJoinData(requesterId, region)
			.where(langCodeCondition.and(targetAccountCondition))
			.transform(groupBy(tour.id).list(tourResponseDtoQBean(requesterId)));
	}

	private JPAQuery<Tour> findTourWithJoinData(Long accountId, Region region) {
		BooleanExpression likeCondition = accountId != null ? tourLike.accountId.eq(accountId) : Expressions.TRUE;

		return queryFactory.select(tour)
			.from(tour)
			.leftJoin(tourInfo).on(tour.id.eq(tourInfo.tourId))
			.leftJoin(account).on(tour.accountId.eq(account.id))
			.leftJoin(accountInfo).on(account.id.eq(accountInfo.accountId))
			.leftJoin(place).on(tour.placeId.eq(place.placeId))
			.leftJoin(placeInfo).on(place.placeId.eq(placeInfo.placeId)
				.and(placeInfo.langCode.eq(region.getMapLangCode()).or(placeInfo.langCode.isNull())))
			.leftJoin(tourImage).on(tour.id.eq(tourImage.tourId))
			.leftJoin(tourLike).on(tour.id.eq(tourLike.tourId).and(likeCondition))
			.orderBy(tour.id.desc())
			.distinct();
	}

	private QBean<TourResponseDto> tourResponseDtoQBean(Long accountId) {
		BooleanExpression likeExpression = Optional.ofNullable(accountId)
			.map(id -> tourLike.accountId.eq(id).and(tourLike.isLike))
			.orElse(Expressions.FALSE);

		return Projections.bean(
			TourResponseDto.class,
			tour.id.as("id"),
			tourInfo.title.as("title"),
			tourInfo.description.as("description"),
			likeExpression.as("like"),
			tour.theme.as("theme"),
			Projections.bean(
				TourResponseDto.PlaceInfo.class,
				place.placeId.as("placeId"),
				placeInfo.name.as("name"),
				tour.isDefaultImage.as("isDefaultImage"),
				GroupBy.list(Projections.bean(
					TourResponseDto.PlaceImageInfo.class,
					tourImage.thumbnailUrl.as("placeImageUrl"),
					tourImage.optimizedThumbnailUrl.as("optimizedPlaceImageUrl")
				).skipNulls()).as("imageUrlList"),
				placeInfo.district.as("district"),
				placeInfo.address.as("address")
			).as("placeInfo"),
			Projections.bean(
				ProfileInfoDto.class,
				account.id.as("accountId"),
				account.firstName.as("firstName"),
				account.lastName.as("lastName"),
				accountInfo.introduce.as("introduce"),
				account.profileImageUrl.as("profileImageUrl"),
				account.optimizedProfileImageUrl.as("optimizedProfileImageUrl"),
				account.directMessageStatus.as("directMessageStatus"),
				account.langCodeList.as("languages")
			).as("userInfo")
		);
	}

}
