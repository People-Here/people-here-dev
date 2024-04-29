package com.peoplehere.shared.common.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.peoplehere.shared.common.entity.TranslateText;
import com.peoplehere.shared.common.enums.LangCode;

@Repository
public interface TranslateTextRepository extends JpaRepository<TranslateText, Long> {

	List<TranslateText> findByTargetLangCodeAndSrcIn(LangCode targetLangCode, List<String> srcList);
}
