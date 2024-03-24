package com.peoplehere.shared.common;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

/**
 * 테스트 설정중 전역설정으로 포함될 값들을 정의
 */
@TestConfiguration
public class TestConfig {

	@Bean
	public DataSource dataSource(@Value("${spring.datasource.hikari.jdbcUrl}") String url,
		@Value("${spring.datasource.hikari.username}") String username,
		@Value("${spring.datasource.hikari.password}") String password) {

		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setUrl(url);
		dataSource.setUsername(username);
		dataSource.setPassword(password);
		return dataSource;
	}

}
