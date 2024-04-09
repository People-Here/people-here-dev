package com.peoplehere.api.common.config.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TokenType {
	ACCESS, REFRESH
}
