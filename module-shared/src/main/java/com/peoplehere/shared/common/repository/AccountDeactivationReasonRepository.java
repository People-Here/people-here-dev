package com.peoplehere.shared.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.peoplehere.shared.common.entity.AccountDeactivationReason;

public interface AccountDeactivationReasonRepository extends JpaRepository<AccountDeactivationReason, Long> {

	void deleteByAccountId(long accountId);
}
