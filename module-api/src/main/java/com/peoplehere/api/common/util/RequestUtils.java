package com.peoplehere.api.common.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.experimental.UtilityClass;

@UtilityClass
public class RequestUtils {

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
}
