package com.peoplehere.api.common.config;

import static com.peoplehere.api.common.util.RequestUtils.*;

import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import com.peoplehere.api.common.annotation.PrivateNetwork;
import com.peoplehere.api.common.exception.ForbiddenException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class PrivateNetworkInterceptor implements HandlerInterceptor {

	private final IpAccessManager ipAccessManager;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		if (!(handler instanceof HandlerMethod)) {
			return true;
		}
		if (isTarget((HandlerMethod)handler) && !ipAccessManager.contains(request)) {
			throw new ForbiddenException(getIp(request), request.getRequestURI());
		}

		return true;
	}

	private boolean isTarget(HandlerMethod method) {
		return method.hasMethodAnnotation(PrivateNetwork.class) || method.getMethod()
			.getDeclaringClass()
			.isAnnotationPresent(PrivateNetwork.class);
	}
}
