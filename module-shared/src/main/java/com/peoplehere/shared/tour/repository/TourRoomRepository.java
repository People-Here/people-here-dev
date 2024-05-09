package com.peoplehere.shared.tour.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.peoplehere.shared.tour.entity.TourRoom;

public interface TourRoomRepository extends JpaRepository<TourRoom, Long> {

	Optional<TourRoom> findByTourIdAndOwnerIdAndGuestId(long tourId, long ownerId, long guestId);
}
