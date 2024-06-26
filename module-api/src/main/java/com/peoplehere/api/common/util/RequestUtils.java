package com.peoplehere.api.common.util;

import org.springframework.util.StringUtils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.experimental.UtilityClass;

@UtilityClass
public class RequestUtils {

	private static final String DEVICE_IDENTIFIER = "Identifier";

	/**
	 * @param request
	 * @return ip 종류 여러개 셋팅되어 올 수 있음
	 */
	public static String getIp(HttpServletRequest request) {
		return request.getHeader("X-Forwarded-For") != null ? request.getHeader("X-Forwarded-For") :
			request.getRemoteAddr();
	}

	/**
	 * abusing check를 위한 키 생성에 필요한 uri prefix 반환
	 * @param request
	 * @return
	 * uri prefix: "api-health"
	 */
	public static String getUriPrefix(HttpServletRequest request) {
		String uri = request.getRequestURI();
		String trimmedUri = uri.startsWith("/") ? uri.substring(1) : uri;
		return trimmedUri.replace("/", "-");
	}

	/**
	 * 사용자의 요청에 대한 제한을 위한 key 생성
	 * 기가 id로 체크 후 없으면 ip로 체크
	 * @param request
	 * @return
	 */
	public static String getRequestKey(HttpServletRequest request) {
		String key = request.getHeader(DEVICE_IDENTIFIER);

		// 혹시라도 가져오지 못한 경우 ip 주소로라도 체크
		if (!StringUtils.hasText(key)) {
			key = getIp(request);
		}

		return key;
	}
}
