package com.peoplehere.shared.tour.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.peoplehere.shared.common.enums.LangCode;
import com.peoplehere.shared.tour.entity.PlaceInfo;

@Repository
public interface PlaceInfoRepository extends JpaRepository<PlaceInfo, Long> {

	Optional<PlaceInfo> findByPlaceIdAndLangCode(String placeId, LangCode langCode);
}
