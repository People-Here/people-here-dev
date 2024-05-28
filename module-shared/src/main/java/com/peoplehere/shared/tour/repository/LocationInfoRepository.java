package com.peoplehere.shared.tour.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.peoplehere.shared.common.enums.LangCode;
import com.peoplehere.shared.tour.entity.LocationInfo;

@Repository
public interface LocationInfoRepository extends JpaRepository<LocationInfo, Long> {

	Optional<LocationInfo> findByPlaceIdAndLangCode(String placeId, LangCode langCode);

	boolean existsByPlaceIdAndLangCode(String placeId, LangCode langCode);
}
