package com.peoplehere.shared.common.event;

import com.peoplehere.shared.tour.data.request.TourInfoTranslateRequestDto;

public record TourInfoTranslatedEvent(TourInfoTranslateRequestDto requestDto) implements TranslatedEvent {

	@Override
	public long id() {
		return requestDto().getTourId();
	}
}
