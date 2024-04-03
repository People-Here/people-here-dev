package com.peoplehere.api.common.service;

import static com.peoplehere.shared.common.config.redis.RedisKeyProperties.*;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import com.peoplehere.api.common.config.security.Token;
import com.peoplehere.api.common.config.security.TokenProperties;
import com.peoplehere.api.common.config.security.VerifyCodeProperties;
import com.peoplehere.api.common.data.response.MailVerificationResponseDto;
import com.peoplehere.shared.common.webhook.AlertWebhook;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisTaskService {

	@Value("${spring.profiles.active:}")
	private String stage;

	private final TokenProperties tokenProperties;
	private final VerifyCodeProperties verifyCodeProperties;
	private final RedisTemplate<String, String> redisTemplate;
	private final AlertWebhook alertWebhook;

	/**
	 * 사용자의 refresh token을 저장
	 * @param token refreshToken
	 * @param userId 사용자 id
	 */
	public void setRefreshToken(Token token, String userId) {
		String key = generateRefreshTokenKey(stage, userId);
		redisTemplate.opsForValue()
			.set(key, token.refreshToken(), tokenProperties.getRefreshTime(), TimeUnit.MICROSECONDS);
		log.info("refresh token 저장 성공 - userId: {}", userId);
	}

	/**
	 * 이메일 인증 코드 레디스에 저장
	 * @param email 이메일
	 * @param code 랜덤 인증 코드
	 */
	public MailVerificationResponseDto setEmailVerifyCode(String email, String code) {
		String key = generateEmailVerifyCodeKey(stage, email);
		redisTemplate.opsForValue()
			.set(key, code, verifyCodeProperties.getEmailTimeout(), TimeUnit.SECONDS);
		log.debug("email verify code 저장 성공 - email: {}", email);
		return new MailVerificationResponseDto(verifyCodeProperties.getEmailTimeout());
	}

	/**
	 * 이메일 인증 코드 레디스에 있는지 확인
	 * @param email 이메일
	 * @return 인증 코드
	 */
	public boolean checkEmailVerifyCode(String email, String code) {
		String key = generateEmailVerifyCodeKey(stage, email);
		boolean isMatch = checkValueMatch(key, code);
		if (isMatch) {
			redisTemplate.unlink(key);
			log.debug("email verify code 삭제 성공 - email: {}", email);
		}
		return isMatch;
	}

	/**
	 * key에 해당하는 value가 일치하는지 확인
	 * @param key email verify code key
	 * @param value email verify code
	 * @return 일치 여부
	 */
	private boolean checkValueMatch(String key, String value) {
		try {
			ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
			return Objects.requireNonNull(value).equals(valueOperations.get(key));
		} catch (Exception e) {
			log.error("redis에서 값 가져오기 실패 - key: {}", key, e);
			alertWebhook.alertError("redis에서 값 가져오기 실패 key - [%s]. 우선은 false 반환 체크 필요".formatted(key), e.getMessage());
			return false;
		}
	}

}
