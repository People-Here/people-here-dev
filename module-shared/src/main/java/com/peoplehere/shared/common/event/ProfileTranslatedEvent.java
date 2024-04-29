package com.peoplehere.shared.common.event;

import com.peoplehere.shared.profile.data.request.ProfileTranslateRequestDto;

public record ProfileTranslatedEvent(ProfileTranslateRequestDto requestDto) implements TranslatedEvent {
	@Override
	public long id() {
		return requestDto.getAccountId();
	}
}
