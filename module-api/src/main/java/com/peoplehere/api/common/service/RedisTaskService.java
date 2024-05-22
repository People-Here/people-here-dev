package com.peoplehere.api.common.service;

import static com.peoplehere.shared.common.config.redis.RedisKeyProperties.*;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import com.peoplehere.api.common.config.security.Token;
import com.peoplehere.api.common.config.security.TokenProperties;
import com.peoplehere.api.common.config.security.VerifyCodeProperties;
import com.peoplehere.api.common.data.response.MailVerificationResponseDto;
import com.peoplehere.api.common.data.response.PhoneVerificationResponseDto;
import com.peoplehere.shared.common.webhook.AlertWebhook;
import com.peoplehere.shared.tour.data.response.PlaceInfoHistoryResponseDto;
import com.peoplehere.shared.tour.data.response.PlaceInfoResponseDto;

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
	private final RedisTemplate<String, PlaceInfoResponseDto> placeInfoRedisTemplate;
	private final AlertWebhook alertWebhook;

	/**
	 * 사용자의 refresh token을 저장
	 * @param token refreshToken
	 * @param userId 사용자 id
	 */
	public void setRefreshToken(Token token, String userId) {
		String key = generateRefreshTokenKey(stage, userId);
		redisTemplate.opsForValue()
			.set(key, token.refreshToken(), tokenProperties.getRefreshTime(), TimeUnit.MILLISECONDS);
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
	 * 전화번호 인증 코드 레디스에 저장
	 * @param phoneNumber 전화번호
	 * @param code 랜덤 인증 코드
	 */
	public PhoneVerificationResponseDto setPhoneVerifyCode(String phoneNumber, String code) {
		String key = generatePhoneVerifyCodeKey(stage, phoneNumber);
		redisTemplate.opsForValue()
			.set(key, code, verifyCodeProperties.getPhoneTimeout(), TimeUnit.SECONDS);
		log.debug("전화번호 verify code 저장 성공 - phoneNumber: {}", phoneNumber);
		return new PhoneVerificationResponseDto(verifyCodeProperties.getPhoneTimeout());
	}

	/**
	 * 전화번호 인증 코드 레디스에 있는지 확인
	 * @param phoneNumber 전화번호
	 * @return 인증 코드
	 */
	public boolean checkPhoneVerifyCode(String phoneNumber, String code) {
		String key = generatePhoneVerifyCodeKey(stage, phoneNumber);
		boolean isMatch = checkValueMatch(key, code);
		if (isMatch) {
			redisTemplate.unlink(key);
			log.debug("전화번호 verify code 삭제 성공 - phoneNumber: {}", phoneNumber);
		}
		return isMatch;
	}

	/**
	 * refresh token 만료
	 * @param userId
	 */
	public void expireRefreshToken(String userId) {
		String key = generateRefreshTokenKey(stage, userId);
		redisTemplate.expire(key, 0, TimeUnit.MILLISECONDS);
		log.info("refresh token 만료 성공 - userId: {}", userId);
	}

	/**
	 * 유저의 최근 검색어 내역을 저장합니다.
	 *
	 * @param userId 유저의 계정 ID
	 * @param responseDto 검색한 장소 정보
	 */
	public void addRecentSearchPlaceInfo(String userId, PlaceInfoResponseDto responseDto) {
		try {
			String key = generateRecentSearchPlaceKey(stage, userId);
			long score = System.currentTimeMillis(); // 현재 시간을 점수로 사용
			ZSetOperations<String, PlaceInfoResponseDto> zSetOperations = placeInfoRedisTemplate.opsForZSet();

			// 새로운 검색어를 추가하거나 업데이트합니다. 중복된 placeId가 있을 경우 자동으로 업데이트
			zSetOperations.add(key, responseDto, score);

			// set 크기가 10을 초과하는지 확인하고 가장 오래된 데이터
			Long size = zSetOperations.size(key);
			if (size != null && size > 10) {
				zSetOperations.removeRange(key, 0, size - 11);
			}
		} catch (Exception e) {
			log.error("redis 최근 검색어 추가 실패 - userId: {}, responseDto: {}", userId, responseDto, e);
			alertWebhook.alertError(
				"redis 최근 검색어 추가 실패 로직의 영향 없도록 skip",
				"userId: [%s], responseDto: [%s], error: [%s]".formatted(userId, responseDto, e.getMessage()));
		}
	}

	/**
	 * 유저의 최근 검색어 내역을 조회합니다.
	 * 최근 검색어 내역은 최대 10개까지 조회 가능
	 * @param userId
	 * @return
	 */
	public PlaceInfoHistoryResponseDto getRecentSearchPlaceInfo(String userId) {
		String key = generateRecentSearchPlaceKey(stage, userId);
		ZSetOperations<String, PlaceInfoResponseDto> zSetOperations = placeInfoRedisTemplate.opsForZSet();
		Set<PlaceInfoResponseDto> responseDtoSet = zSetOperations.reverseRange(key, 0, 9);

		if (responseDtoSet == null) {
			return null;
		} else {
			return PlaceInfoHistoryResponseDto.builder()
				.places(new ArrayList<>(responseDtoSet))
				.build();
		}
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
