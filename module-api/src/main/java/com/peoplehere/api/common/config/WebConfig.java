package com.peoplehere.api.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.peoplehere.api.common.config.interceptor.AbusingInterceptor;
import com.peoplehere.api.common.config.interceptor.PrivateNetworkInterceptor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

	private final PrivateNetworkInterceptor privateNetworkInterceptor;
	private final AbusingInterceptor abusingInterceptor;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		WebMvcConfigurer.super.addInterceptors(registry);
		registry.addInterceptor(privateNetworkInterceptor);
		registry.addInterceptor(abusingInterceptor);
	}
}
