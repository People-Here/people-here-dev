package com.peoplehere.shared.common.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.peoplehere.shared.common.entity.Consent;

@Repository
public interface ConsentRepository extends JpaRepository<Consent, Long> {

	Optional<Consent> findByAccountId(Long accountId);

	void deleteAllByAccountIdIn(List<Long> accountIdList);
}
