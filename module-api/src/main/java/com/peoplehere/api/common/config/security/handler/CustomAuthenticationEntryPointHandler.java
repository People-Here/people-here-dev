package com.peoplehere.api.common.config.security.handler;

import static com.peoplehere.api.common.util.RequestUtils.*;
import static org.springframework.util.MimeTypeUtils.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPointHandler implements AuthenticationEntryPoint {

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException authException) throws IOException, ServletException {
		if (log.isDebugEnabled()) {
			log.debug("[{}] [{}] [{}] {}", getIp(request), HttpStatus.UNAUTHORIZED, request.getMethod(),
				request.getRequestURI());
		}

		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		response.setCharacterEncoding(StandardCharsets.UTF_8.name());
		response.setContentType(APPLICATION_JSON_VALUE);
		// todo: 추후 response body에 에러 메시지 필요하다면 추가
	}
}
