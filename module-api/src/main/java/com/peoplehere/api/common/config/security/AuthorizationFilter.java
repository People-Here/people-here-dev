package com.peoplehere.api.common.config.security;

import static com.peoplehere.api.common.util.RequestUtils.*;
import static org.springframework.http.HttpHeaders.*;

import java.io.IOException;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthorizationFilter extends OncePerRequestFilter {

	private final TokenProvider tokenProvider;
	private final RedisTemplate<String, String> redisTemplate;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain chain) throws ServletException, IOException {
		String token = request.getHeader(AUTHORIZATION);
		String ip = getIp(request);
		String uri = request.getRequestURI();

		if (!StringUtils.hasText(token)) {
			log.debug("토큰 없음, ip: {}, uri: {}", ip, uri);
			chain.doFilter(request, response);
			return;
		}
		try {
			Authentication authentication = tokenProvider.getAuthenticationFromAcs(token);
			SecurityContextHolder.getContext().setAuthentication(authentication);
			log.debug("{}: 인증 정보 security context 저장, uri: {}", authentication.getName(), uri);

		} catch (Exception e) {
			log.debug("인가 처리 실패 기록 : ip: {}, uri: {} - {}", ip, uri, e.getMessage());
		}
		chain.doFilter(request, response);
	}
}
