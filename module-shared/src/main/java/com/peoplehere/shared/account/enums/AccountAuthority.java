package com.peoplehere.shared.account.enums;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AccountAuthority {

	CHANGE_ROLE,
	ACTIVE_USER,
	READ_TOUR_POST,
	CREATE_TOUR;

	public String getValue() {
		return this.name();
	}

	private final GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(this.name());

}
