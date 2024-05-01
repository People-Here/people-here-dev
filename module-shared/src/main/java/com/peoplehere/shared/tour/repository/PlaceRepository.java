package com.peoplehere.shared.tour.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.peoplehere.shared.tour.entity.Place;

@Repository
public interface PlaceRepository extends JpaRepository<Place, Long> {

	Optional<Place> findByPlaceId(String placeId);
}
