package com.peoplehere.shared.common.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.peoplehere.shared.common.entity.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
	Optional<Account> findByEmail(String email);

	Optional<Account> findByUserId(String userId);

	Boolean existsByEmail(String email);

	Boolean existsByPhoneNumber(String phoneNumber);
}
