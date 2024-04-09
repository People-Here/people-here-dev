package com.peoplehere.api.common.config.interceptor;

import static com.peoplehere.api.common.util.RequestUtils.*;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import com.peoplehere.api.common.annotation.CheckAbusing;
import com.peoplehere.api.common.exception.AbusingException;
import com.peoplehere.shared.common.config.redis.RedisKeyProperties;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 호출 횟수 제한(초당 3회)에 걸릴 경우 에러 CheckAbusing 어노테이션이 붙은 메소드에 대해 초당 요청 제한을 적용
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AbusingInterceptor implements HandlerInterceptor {

	@Value("${spring.profiles.active:#{null}}")
	private String stage;

	private final RedisTemplate<String, Integer> redisTemplate;
	private static final int ABUSING_LIMIT = 3; // 초당 요청 제한 횟수

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
		throws Exception {
		if (!(handler instanceof HandlerMethod)) {
			log.debug("check handler is not instance of HandlerMethod");
			return true;
		}

		// CheckAbusing 어노테이션이 붙은 메소드의 경우에만 초당 요청 제한 적용
		if (isTarget((HandlerMethod)handler)) {
			// TODO: 추후 기기id로 abusing check
			// String key = request.getHeader("d-id");
			String key = null;

			// 혹시라도 토큰이 없거나 토큰에서 사원번호를 가져오지 못한 경우 ip 주소로라도 어뷰징 체크
			if (!StringUtils.hasText(key)) {
				key = getIp(request);
			}

			String countKey = RedisKeyProperties.generateRequestCountKey(stage, getUriPrefix(request), key);
			Long count = redisTemplate.opsForValue().increment(countKey);

			if (count == null) {
				throw new AbusingException("requestCount 키 초기화 실패: " + countKey);
			}

			// 최초 요청인 경우 만료 시간 초기화
			if (count == 1) {
				redisTemplate.expire(countKey, 1, TimeUnit.SECONDS);
			}

			// N 번 이상의 요청인 경우 429 응답
			if (count > ABUSING_LIMIT) {
				throw new AbusingException("Abusing 에러 발생: " + countKey);
			}

		}
		return true;
	}

	private boolean isTarget(HandlerMethod method) {
		return method.hasMethodAnnotation(CheckAbusing.class) || method.getMethod().getDeclaringClass()
			.isAnnotationPresent(CheckAbusing.class);
	}

}

