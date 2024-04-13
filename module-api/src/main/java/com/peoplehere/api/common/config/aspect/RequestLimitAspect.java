package com.peoplehere.api.common.config.aspect;

import static com.peoplehere.api.common.config.RequestProperties.*;
import static com.peoplehere.api.common.util.RequestUtils.*;
import static com.peoplehere.shared.common.config.redis.RedisKeyProperties.*;

import java.util.concurrent.TimeUnit;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.peoplehere.api.common.data.request.MailVerifyRequestDto;
import com.peoplehere.api.common.exception.RequestLimitException;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

/**
 * 요청 전 제한 값들(유료 사용자의 빠른 요청 처리, 무료 사용자의 총 요청 제한)을 체크하고 필요한 값을 설정하는 Aspect
 */
@Component
@Aspect
@RequiredArgsConstructor
public class RequestLimitAspect {

	@Value("${spring.profiles.active:#{null}}")
	private String stage;

	private final RedisTemplate<String, Integer> redisTemplate;

	/**
	 * 이메일 인증 번호 전송에 대한 제한을 체크하는 Aspect
	 * @param joinPoint
	 * @return
	 */
	@Before("@annotation(com.peoplehere.api.common.annotation.CheckEmailVerificationLimit)")
	public void checkEmailVerificationLimit(JoinPoint joinPoint) {
		HttpServletRequest request = getRequest();
		String countKey = generateEmailVerificationRequestCountKey(stage, getRequestKey(request));
		processRequestLimit(countKey, getEmailVerificationTimeLimitDay(), getEmailVerificationCountLimit());
	}

	/**
	 * 이메일 인증 번호 검증에 대한 제한을 체크하는 Aspect
	 * @param joinPoint
	 * @return
	 * @throws Throwable
	 */
	@Before("@annotation(com.peoplehere.api.common.annotation.CheckEmailVerifyLimit)")
	public void checkEmailVerifyLimit(JoinPoint joinPoint) {
		var args = joinPoint.getArgs();
		for (var arg : args) {
			if (arg instanceof MailVerifyRequestDto dto) {
				String email = dto.email();
				String countKey = generateEmailVerifyRequestCountKey(stage, email);
				processRequestLimit(countKey, getEmailVerifyTimeLimitDay(), getEmailVerifyCountLimit());
			}
		}
	}

	private void processRequestLimit(String countKey, int timeLimitDays, int maxLimit) {
		Long count = redisTemplate.opsForValue().increment(countKey);

		if (count == null) {
			throw new RuntimeException("requestCount 키 초기화 실패: " + countKey);
		}

		if (count == 1) {
			redisTemplate.expire(countKey, timeLimitDays, TimeUnit.DAYS);
		}

		if (count > maxLimit) {
			throw new RequestLimitException(countKey);
		}
	}

	private static HttpServletRequest getRequest() {
		return ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();
	}

}
