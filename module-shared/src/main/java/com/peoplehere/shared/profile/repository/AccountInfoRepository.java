package com.peoplehere.shared.profile.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.peoplehere.shared.common.enums.LangCode;
import com.peoplehere.shared.profile.entity.AccountInfo;

public interface AccountInfoRepository extends JpaRepository<AccountInfo, Long> {

	Optional<AccountInfo> findByAccountIdAndLangCode(Long accountId, LangCode langCode);

	void deleteByAccountIdAndLangCode(Long accountId, LangCode langCode);
}
