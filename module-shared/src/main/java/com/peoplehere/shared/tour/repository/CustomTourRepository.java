package com.peoplehere.shared.tour.repository;

import static com.peoplehere.shared.common.entity.QAccount.*;
import static com.peoplehere.shared.tour.entity.QCategory.*;
import static com.peoplehere.shared.tour.entity.QPlace.*;
import static com.peoplehere.shared.tour.entity.QTour.*;
import static com.peoplehere.shared.tour.entity.QTourCategory.*;
import static com.peoplehere.shared.tour.entity.QTourImage.*;
import static com.querydsl.core.group.GroupBy.*;

import java.util.List;

import org.springframework.stereotype.Repository;

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

	public List<TourResponseDto> findTourList() {
		return findTourWithJoinData()
			.transform(groupBy(tour.id).list(tourResponseDtoQBean()));
	}

	public List<TourResponseDto> findTourListByKeyword(String keyword) {
		BooleanExpression searchCondition = (place.name.contains(keyword)).or(place.address.contains(keyword));

		return findTourWithJoinData()
			.where(searchCondition)
			.transform(groupBy(tour.id).list(tourResponseDtoQBean()));
	}

	private JPAQuery<Tour> findTourWithJoinData() {
		return queryFactory.select(tour)
			.from(tour)
			.join(account).on(tour.accountId.eq(account.id))
			.join(place).on(tour.placeId.eq(place.id))
			.leftJoin(tourCategory).on(tour.id.eq(tourCategory.tourId))
			.leftJoin(category).on(tourCategory.categoryId.eq(category.id))
			.leftJoin(tourImage).on(tour.id.eq(tourImage.tourId))
			.orderBy(tour.id.desc());

	}

	private QBean<TourResponseDto> tourResponseDtoQBean() {
		return Projections.bean(
			TourResponseDto.class,
			tour.id.as("id"),
			tour.title.as("title"),
			GroupBy.list(Projections.bean(
				TourResponseDto.CategoryInfo.class,
				category.name.as("categoryName")
			).skipNulls()).as("categoryList"),
			Projections.bean(
				TourResponseDto.PlaceInfo.class,
				place.placeId.as("placeId"),
				place.name.as("name"),
				GroupBy.list(Projections.bean(
					TourResponseDto.PlaceImageInfo.class,
					tourImage.thumbnailUrl.as("placeImageUrl")
				).skipNulls()).as("imageUrlList"),
				place.district.as("district")
			).as("placeInfo"),
			Projections.bean(
				TourResponseDto.UserInfo.class,
				account.id.as("accountId"),
				account.firstName.as("firstName"),
				account.lastName.as("lastName"),
				account.profileImageUrl.as("profileImageUrl"),
				account.directMessageStatus.as("directMessageStatus")
			).as("userInfo")
		);
	}

}
