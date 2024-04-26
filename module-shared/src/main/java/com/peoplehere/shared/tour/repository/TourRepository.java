package com.peoplehere.shared.tour.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.peoplehere.shared.tour.entity.Tour;

@Repository
public interface TourRepository extends JpaRepository<Tour, Long> {

	void deleteAllByAccountIdIn(List<Long> accountIdList);

	Optional<Tour> findByIdAndAccountId(long id, long accountId);
}
