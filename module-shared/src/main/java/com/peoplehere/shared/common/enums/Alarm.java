package com.peoplehere.shared.common.enums;

import static java.util.stream.Collectors.*;

import java.util.List;
import java.util.stream.Stream;

import com.peoplehere.shared.common.data.response.AlarmResponseDto;

import lombok.Getter;

@Getter
public enum Alarm {
	MARKETING("마케팅 정보 수신 동의"),
	MESSAGE("쪽지 알람 수신 동의"),
	MEETING("약속 알람 수신 동의");

	private final String description;

	Alarm(String description) {
		this.description = description;
	}

	public static final Alarm[] VALUES = values();
	public static final List<AlarmResponseDto> ALARM_INFO_LIST = Stream.of(VALUES)
		.map(alarm -> AlarmResponseDto.builder()
			.alarmType(alarm.name())
			.description(alarm.getDescription())
			.build())
		.collect(toList());
}
