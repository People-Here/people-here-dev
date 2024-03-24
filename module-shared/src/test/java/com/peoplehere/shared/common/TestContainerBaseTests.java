package com.peoplehere.shared.common;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Testcontainers;

@Import({TestConfig.class})
@EnableConfigurationProperties
@Testcontainers
public class TestContainerBaseTests {

	static final PostgreSQLContainer postgres;

	static {
		postgres = new PostgreSQLContainer<>("postgres:15-alpine")
			.withExposedPorts(5432)
			.waitingFor(Wait.forLogMessage(".*ready to accept connections.*\\n", 1));
		postgres.start();
	}

}
