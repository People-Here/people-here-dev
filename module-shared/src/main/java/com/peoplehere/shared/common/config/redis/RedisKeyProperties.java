package com.peoplehere.shared.common.config.redis;

import lombok.experimental.UtilityClass;

@UtilityClass
public class RedisKeyProperties {

	public static String generateRefreshTokenKey(String prefix, String identifier) {
		return "%s:spring:refresh:token:%s".formatted(prefix, identifier);
	}

	public static String generateEmailVerifyCodeKey(String prefix, String email) {
		return "%s:spring:email:verify:code:%s".formatted(prefix, email);
	}
}
