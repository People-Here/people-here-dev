package com.peoplehere.shared.tour.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.peoplehere.shared.tour.entity.TourMessage;

public interface TourMessageRepository extends JpaRepository<TourMessage, Long> {
}
