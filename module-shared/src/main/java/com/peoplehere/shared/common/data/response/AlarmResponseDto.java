package com.peoplehere.shared.common.data.response;

import lombok.Builder;

@Builder
public record AlarmResponseDto(String alarmType, String description) {
}
