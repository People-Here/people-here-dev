package com.peoplehere.shared.common.config.redis;

import lombok.experimental.UtilityClass;

@UtilityClass
public class RedisKeyProperties {

	/**
	 * refresh token key 생성
	 * {stage}:spring:refresh:token:{identifier}
	 * @param prefix
	 * @param identifier
	 * @return
	 */
	public static String generateRefreshTokenKey(String prefix, String identifier) {
		return "%s:spring:refresh:token:%s".formatted(prefix, identifier);
	}

	/**
	 * email 인증 코드 저장을 위한 key 생성
	 * @param prefix
	 * @param email
	 * @return
	 */
	public static String generateEmailVerifyCodeKey(String prefix, String email) {
		return "%s:spring:email:verify:code:%s".formatted(prefix, email);
	}

	/**
	 * 사용자의 abusing check를 위한 key 생성
	 * @param prefix
	 * @param requestUri
	 * @param key
	 * @return
	 */
	public static String generateAbusingRequestCountKey(String prefix, String requestUri, String key) {
		return String.format("%s:spring:abusingCheck:%s:%s", prefix, requestUri, key);
	}

	/**
	 * 사용자의 email 인증 번호 전송 횟수를 체크하기 위한 key 생성
	 * @param prefix
	 * @param key
	 * @return
	 */
	public static String generateEmailVerificationRequestCountKey(String prefix, String key) {
		return String.format("%s:spring:email:verification:request:%s", prefix, key);
	}

	/**
	 * 사용자의 email 인증 번호 검증 횟수를 체크하기 위한 key 생성
	 * @param prefix
	 * @param email
	 * @return
	 */
	public static String generateEmailVerifyRequestCountKey(String prefix, String email) {
		return String.format("%s:spring:email:verify:request:%s", prefix, email);
	}

	/**
	 * 사용자의 최근 검색 장소를 저장하기 위한 key 생성
	 * @param prefix
	 * @param userId
	 * @return
	 */
	public static String generateRecentSearchPlaceKey(String prefix, String userId) {
		return String.format("%s:spring:recent:search:%s:place", prefix, userId);
	}

}
