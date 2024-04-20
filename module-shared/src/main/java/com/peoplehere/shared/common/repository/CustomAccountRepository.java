package com.peoplehere.shared.common.repository;

import static com.peoplehere.shared.common.entity.QAccount.*;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@RequiredArgsConstructor
public class CustomAccountRepository {

	private final JPAQueryFactory queryFactory;

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
