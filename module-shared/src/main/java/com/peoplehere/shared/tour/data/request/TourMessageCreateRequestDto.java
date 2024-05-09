package com.peoplehere.shared.tour.data.request;

import com.peoplehere.shared.tour.entity.TourMessage;

import jakarta.validation.constraints.NotBlank;

public record TourMessageCreateRequestDto(long tourId, long receiverId, @NotBlank String message) {

	public static TourMessage toTourMessageEntity(TourMessageCreateRequestDto requestDto, long tourId, long senderId) {
		return TourMessage.builder()
			.tourRoomId(tourId)
			.senderId(senderId)
			.receiverId(requestDto.receiverId)
			.message(requestDto.message)
			.build();
	}
}
