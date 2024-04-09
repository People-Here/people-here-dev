package com.peoplehere.shared.common.config.redis;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "spring.data.redis")
public class RedisProperties {
	protected String host;

	protected int port;

	protected boolean sslEnabled;

	protected int database;
}
