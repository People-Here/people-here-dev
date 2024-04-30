package com.peoplehere.shared.tour.data.request;

import com.peoplehere.shared.tour.entity.Tour;
import com.peoplehere.shared.tour.entity.TourInfo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TourInfoTranslateRequestDto {
	private long tourId;
	private String title;
	private String description;

	public static TourInfoTranslateRequestDto toTranslateRequestDto(Tour tour, TourInfo tourInfo) {
		return TourInfoTranslateRequestDto.builder()
			.tourId(tour.getId())
			.title(tourInfo.getTitle())
			.description(tourInfo.getDescription())
			.build();
	}
}
