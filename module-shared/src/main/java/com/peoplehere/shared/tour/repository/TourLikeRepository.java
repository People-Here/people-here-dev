package com.peoplehere.shared.tour.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.peoplehere.shared.tour.entity.TourLike;

public interface TourLikeRepository extends JpaRepository<TourLike, Long> {

	void deleteAllByTourId(Long tourId);

	Optional<TourLike> findByTourIdAndAccountId(Long tourId, Long accountId);
}
