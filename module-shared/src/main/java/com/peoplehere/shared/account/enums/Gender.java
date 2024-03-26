package com.peoplehere.shared.account.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Gender {

	MALE,
	FEMALE,
	OTHER;

	public String getValue() {
		return this.name();
	}
}
