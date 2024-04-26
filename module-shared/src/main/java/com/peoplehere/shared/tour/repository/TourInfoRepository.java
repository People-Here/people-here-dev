package com.peoplehere.shared.tour.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.peoplehere.shared.common.enums.LangCode;
import com.peoplehere.shared.tour.entity.TourInfo;

@Repository
public interface TourInfoRepository extends JpaRepository<TourInfo, Long> {
	void deleteAllByTourId(Long tourId);

	Optional<TourInfo> findByTourIdAndLangCode(Long tourId, LangCode langCode);
}
