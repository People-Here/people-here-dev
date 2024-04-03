package com.peoplehere.api.common.config.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@Component
@ConfigurationProperties(prefix = "verify")
public class VerifyCodeProperties {

	private int emailTimeout;

	private int phoneTimeout;

	@PostConstruct
	public void log() {
		log.info("email verify code 만료시간: [{} ms]", this.emailTimeout);
		log.info("phone verify code 만료시간: [{} ms]", this.phoneTimeout);
	}
}
