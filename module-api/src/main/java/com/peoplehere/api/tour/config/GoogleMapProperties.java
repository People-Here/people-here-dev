package com.peoplehere.api.tour.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@Component
@ConfigurationProperties(prefix = "map.google")
public class GoogleMapProperties {

	private String projectId;
	private String key;
	private String baseUrl;
	private String placeUri;
	private String placeDetailUri;
	private String geocodeUri;

	@PostConstruct
	public void print() {
		log.info("google place api project-id: {}", projectId);
		log.info("google map base url: {}", baseUrl);
		log.info("google map place uri: {}", placeUri);
		log.info("google map place-detail uri: {}", placeDetailUri);
		log.info("google map geocode uri: {}", geocodeUri);
	}
}
