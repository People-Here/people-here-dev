package com.peoplehere.shared.tour.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.peoplehere.shared.tour.entity.TourImage;

@Repository
public interface TourImageRepository extends JpaRepository<TourImage, Long> {
	void deleteAllByTourId(Long tourId);
}
