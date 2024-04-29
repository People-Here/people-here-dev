package com.peoplehere.shared.common.repository;

import static com.peoplehere.shared.common.entity.QAccount.*;
import static com.peoplehere.shared.profile.entity.QAccountInfo.*;
import static com.peoplehere.shared.tour.entity.QPlace.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.peoplehere.shared.common.enums.LangCode;
import com.peoplehere.shared.profile.data.response.ProfileInfoResponseDto;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@RequiredArgsConstructor
public class CustomAccountRepository {

	private final JPAQueryFactory queryFactory;

	public Optional<ProfileInfoResponseDto> findProfileInfo(Long accountId, LangCode langCode) {
		return Optional.ofNullable(queryFactory
			.select(Projections.bean(ProfileInfoResponseDto.class,
				account.id.as("id"),
				account.firstName.as("firstName"),
				account.lastName.as("lastName"),
				account.profileImageUrl.as("profileImageUrl"),
				account.optimizedProfileImageUrl.as("optimizedProfileImageUrl"),
				account.region.as("region"),
				account.langCodeList.as("languages"),
				accountInfo.favorite.as("favorite"),
				accountInfo.hobby.as("hobby"),
				accountInfo.pet.as("pet"),
				accountInfo.introduce.as("introduce"),
				accountInfo.job.as("job"),
				accountInfo.school.as("school"),
				place.address.as("address"),
				account.birthDate.as("birthDate"),
				Expressions.asEnum(langCode).as("langCode")))
			.from(account)
			.leftJoin(accountInfo).on(account.id.eq(accountInfo.accountId))
			.leftJoin(place).on(account.placeId.eq(place.placeId))
			.where(account.id.eq(accountId).and(accountInfo.langCode.eq(langCode)))
			.fetchOne());
	}

	/**
	 * 보존기간이 지난 비활성화 계정 삭제
	 * @param baseDateTime
	 * @return
	 */
	public List<Long> findAccountIdListToDelete(LocalDateTime baseDateTime) {
		return queryFactory
			.select(account.id)
			.from(account)
			.where(account.active.isFalse()
				.and(account.deletedAt.loe(baseDateTime)))
			.fetch();
	}
}
