package com.peoplehere.shared.tour.repository;

import static com.peoplehere.shared.tour.entity.QTourMessage.*;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@RequiredArgsConstructor
public class CustomTourMessageRepository {

	private final JPAQueryFactory queryFactory;

	/**
	 * 주어진 투어에 대해서 두 사용자 간에 존재하는 투어 메시지가 있는지 확인
	 * @param tourId
	 * @param senderId
	 * @return
	 */
	public boolean existsTourMessage(long tourId, long senderId, long receiverId) {
		return queryFactory
			.selectOne()
			.from(tourMessage)
			.where(
				tourMessage.tourId.eq(tourId)
					.and(
						(tourMessage.senderId.eq(senderId).and(tourMessage.receiverId.eq(receiverId)))
							.or(tourMessage.senderId.eq(receiverId).and(tourMessage.receiverId.eq(senderId)))
					))
			.fetchFirst() != null;
	}
}
