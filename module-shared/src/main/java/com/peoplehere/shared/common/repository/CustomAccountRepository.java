package com.peoplehere.shared.common.repository;

import static com.peoplehere.shared.common.entity.QAccount.*;
import static com.peoplehere.shared.common.entity.QConsent.*;
import static com.peoplehere.shared.profile.entity.QAccountInfo.*;
import static com.peoplehere.shared.tour.entity.QLocation.*;
import static com.peoplehere.shared.tour.entity.QLocationInfo.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.peoplehere.shared.common.enums.LangCode;
import com.peoplehere.shared.common.enums.Region;
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

	public Optional<ProfileInfoResponseDto> findProfileInfo(Long accountId, Region region, LangCode langCode) {
		return Optional.ofNullable(queryFactory
			.select(Projections.bean(
				ProfileInfoResponseDto.class,
				account.id.as("id"),
				account.email.as("email"),
				account.firstName.as("firstName"),
				account.lastName.as("lastName"),
				account.phoneNumber.as("phoneNumber"),
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
				locationInfo.address.as("address"),
				account.birthDate.as("birthDate"),
				Expressions.asEnum(langCode).as("langCode"),
				account.showBirth.as("showBirth"),
				Projections.bean(
					ProfileInfoResponseDto.ConsentInfo.class,
					consent.privacyConsent.as("privacyConsent"),
					consent.marketingConsent.as("marketingConsent"),
					consent.messageAlarmConsent.as("messageAlarmConsent"),
					consent.meetingAlarmConsent.as("meetingAlarmConsent")
				).as("consentInfo")
			))
			.from(account)
			.leftJoin(accountInfo).on(account.id.eq(accountInfo.accountId))
			.leftJoin(consent).on(account.id.eq(consent.accountId))
			.leftJoin(location).on(account.placeId.eq(location.placeId))
			.leftJoin(locationInfo).on(location.placeId.eq(locationInfo.placeId)
				.and(locationInfo.langCode.eq(region.getMapLangCode()).or(locationInfo.langCode.isNull())))
			.where(account.id.eq(accountId)
				.and(accountInfo.langCode.eq(langCode)))
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
