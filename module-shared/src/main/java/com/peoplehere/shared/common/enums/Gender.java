package com.peoplehere.shared.common.enums;

import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Gender {

	MALE,
	FEMALE,
	OTHER;

	public static final Gender[] VALUES = values();

	@JsonValue
	public String getGender() {
		return this.name();
	}
}
