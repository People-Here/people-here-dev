package com.peoplehere.shared.tour.repository;

import static com.peoplehere.shared.tour.entity.QTour.*;
import static com.peoplehere.shared.tour.entity.QTourInfo.*;
import static com.peoplehere.shared.tour.entity.QTourMessage.*;
import static com.peoplehere.shared.tour.entity.QTourRoom.*;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.peoplehere.shared.common.entity.QAccount;
import com.peoplehere.shared.common.enums.LangCode;
import com.peoplehere.shared.profile.data.ProfileInfoDto;
import com.peoplehere.shared.profile.entity.QAccountInfo;
import com.peoplehere.shared.tour.data.response.TourRoomListResponseDto;
import com.peoplehere.shared.tour.entity.QTourMessage;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@RequiredArgsConstructor
public class CustomTourRoomRepository {

	private final JPAQueryFactory queryFactory;
	private final QAccount ownerAccount = new QAccount("ownerAccount");
	private final QAccountInfo ownerAccountInfo = new QAccountInfo("ownerAccountInfo");
	private final QAccount guestAccount = new QAccount("guestAccount");
	private final QAccountInfo guestAccountInfo = new QAccountInfo("guestAccountInfo");
	private final QTourMessage nextTourMessage = new QTourMessage("nextTourMessage");

	public TourRoomListResponseDto findTourRoomList(long accountId, LangCode langCode) {
		BooleanExpression langCodeCondition = tourInfo.langCode.eq(langCode)
			.and(ownerAccountInfo.langCode.eq(langCode))
			.and(guestAccountInfo.langCode.eq(langCode));

		List<TourRoomListResponseDto.TourRoomResponseDto> dtoList = queryFactory.select(
				Projections.bean(
					TourRoomListResponseDto.TourRoomResponseDto.class,
					tourRoom.id.as("id"),
					tour.id.as("tourId"),
					tourInfo.title.as("title"),
					tourMessage.message.as("lastMessage"),
					Projections.constructor(
						ProfileInfoDto.class,
						ownerAccount.id,
						ownerAccount.firstName,
						ownerAccount.lastName,
						ownerAccountInfo.introduce,
						ownerAccount.profileImageUrl,
						ownerAccount.optimizedProfileImageUrl,
						ownerAccount.directMessageStatus
					).as("ownerInfo"),
					Projections.constructor(
						ProfileInfoDto.class,
						guestAccount.id,
						guestAccount.firstName,
						guestAccount.lastName,
						guestAccountInfo.introduce,
						guestAccount.profileImageUrl,
						guestAccount.optimizedProfileImageUrl,
						guestAccount.directMessageStatus
					).as("guestInfo")
				))
			.from(tourRoom)
			.join(tour)
			.on(tourRoom.tourId.eq(tour.id))
			.leftJoin(tourInfo)
			.on(tour.id.eq(tourInfo.tourId))
			.leftJoin(tourMessage)
			.on(tourRoom.id.eq(tourMessage.tourRoomId))
			.leftJoin(nextTourMessage)
			.on(tourMessage.tourRoomId.eq(nextTourMessage.tourRoomId).and(tourMessage.id.lt(nextTourMessage.id)))
			.leftJoin(ownerAccount)
			.on(tourRoom.ownerId.eq(ownerAccount.id))
			.leftJoin(ownerAccountInfo)
			.on(ownerAccount.id.eq(ownerAccountInfo.accountId))
			.leftJoin(guestAccount)
			.on(tourRoom.guestId.eq(guestAccount.id))
			.leftJoin(guestAccountInfo)
			.on(guestAccount.id.eq(guestAccountInfo.accountId))
			.where(langCodeCondition
				.and(tourRoom.ownerId.eq(accountId).or(tourRoom.guestId.eq(accountId)))
				.and(nextTourMessage.id.isNull()))
			.orderBy(tourMessage.id.desc())
			.fetch();

		return TourRoomListResponseDto.builder()
			.tourRoomList(dtoList)
			.accountId(accountId)
			.build();
	}
}
