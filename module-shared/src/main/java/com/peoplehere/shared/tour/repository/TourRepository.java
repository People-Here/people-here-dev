package com.peoplehere.shared.tour.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.peoplehere.shared.tour.entity.Tour;

@Repository
public interface TourRepository extends JpaRepository<Tour, Long> {
}
