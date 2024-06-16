package com.peoplehere.shared.tour.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.peoplehere.shared.tour.entity.TourDeletionReason;

public interface TourDeletionReasonRepository extends JpaRepository<TourDeletionReason, Long> {
}
