package com.peoplehere.shared.common.enums;

import java.util.Arrays;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 유저의 역할을 정의한 Enum 클래스
 */
@Getter
@AllArgsConstructor
public enum AccountRole {

	ADMIN("ROLE_ADMIN", Arrays.asList(
		AccountAuthority.CHANGE_ROLE,
		AccountAuthority.ACTIVE_USER,
		AccountAuthority.READ_TOUR_POST,
		AccountAuthority.CREATE_TOUR,
		AccountAuthority.UPDATE_PROFILE,
		AccountAuthority.CREATE_MESSAGE
	)),

	USER("ROLE_USER", Arrays.asList(
		AccountAuthority.READ_TOUR_POST,
		AccountAuthority.CREATE_TOUR,
		AccountAuthority.UPDATE_PROFILE,
		AccountAuthority.CREATE_MESSAGE
	));

	private final String value;
	private final List<AccountAuthority> authorities;

	private static final AccountRole[] VALUES = values();

	public boolean hasAuthority(AccountAuthority authority) {
		return authorities.contains(authority);
	}

	public boolean match(String value) {
		return this.value.equals(value);
	}

	public static AccountRole toAccountRole(String roleName) {
		for (var role : VALUES) {
			if (role.value.equals(roleName)) {
				return role;
			}
		}
		throw new IllegalArgumentException("유효하지 않은 ROLE 이름: " + roleName);
	}

}
