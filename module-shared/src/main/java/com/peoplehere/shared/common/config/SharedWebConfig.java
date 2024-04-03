package com.peoplehere.shared.common.config;

import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.MediaType.*;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.zaxxer.hikari.HikariDataSource;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class SharedWebConfig implements WebMvcConfigurer {

	/**
	 * hikariCp 설정을 위한 Config
	 * @return
	 */
	@Bean(name = "datasource")
	@Profile("!test")
	@ConfigurationProperties("spring.datasource.hikari")
	public DataSource dataSourceProperties() {
		return DataSourceBuilder.create()
			.type(HikariDataSource.class)
			.build();
	}

	@Profile("!test")
	@Bean
	public FlywayMigrationStrategy cleanMigrateStrategy() {
		return flyway -> {
			flyway.repair();
			flyway.migrate();
		};
	}

	@Profile("test")
	@Bean
	public FlywayMigrationStrategy cleanMigrateStrategyForTest() {
		return flyway -> {
			flyway.clean();
			flyway.repair();
			flyway.migrate();
		};
	}

	/**
	 * 기본적인 RestClient timeout 설정
	 * 추가적인 설정 필요시 해당 클래스에서 설정
	 * @return
	 */
	@Bean
	RestClient restClient() {
		SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
		factory.setConnectTimeout(10_000);
		factory.setReadTimeout(10_000);
		return RestClient.builder()
			.defaultHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
			.requestFactory(factory)
			.build();
	}

}
