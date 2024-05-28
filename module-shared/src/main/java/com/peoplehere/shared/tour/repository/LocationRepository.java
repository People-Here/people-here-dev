package com.peoplehere.shared.tour.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.peoplehere.shared.tour.entity.Location;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

	Optional<Location> findByPlaceId(String placeId);
}
