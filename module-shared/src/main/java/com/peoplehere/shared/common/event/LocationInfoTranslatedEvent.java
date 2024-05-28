package com.peoplehere.shared.common.event;

public record LocationInfoTranslatedEvent(String placeId) {

	public String id() {
		return placeId;
	}
}
