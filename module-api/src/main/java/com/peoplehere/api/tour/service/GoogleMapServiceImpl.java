package com.peoplehere.api.tour.service;

import java.time.Duration;
import java.util.LinkedHashSet;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.peoplehere.api.common.exception.HttpRequestRetryException;
import com.peoplehere.api.common.service.RedisTaskService;
import com.peoplehere.api.tour.config.GoogleMapProperties;
import com.peoplehere.shared.common.enums.Region;
import com.peoplehere.shared.common.webhook.AlertWebhook;
import com.peoplehere.shared.tour.data.response.PlaceInfoListResponseDto;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

/**
 * 구글 서비스를 사용한 장소 관련 서비스
 */
@Slf4j
@Order(1)
@Service
@RequiredArgsConstructor
public class GoogleMapServiceImpl implements MapService {

	private WebClient webClient;
	private final AlertWebhook alertWebhook;
	private final GoogleMapProperties googleMapProperties;
	private final RedisTaskService redisTaskService;

	@PostConstruct
	void init() {
		this.webClient = WebClient.builder()
			.baseUrl(googleMapProperties.getBaseUrl())
			.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
			.build();
	}

	/**
	 * Google place api 사용해 장소명으로 관련 장소 목록 조회
	 * 재시도 1회 1초 간격 최종 실패시 알림
	 * timeout 10초
	 * @param name 장소명
	 * @param region 국가 코드
	 * @return 장소 정보
	 */
	@Override
	public PlaceInfoListResponseDto getPlaceInfoList(String name, Region region) {
		var errorLogSet = new LinkedHashSet<String>();

		return webClient.get()
			.uri(uri -> uri
				.path(googleMapProperties.getPlaceUri())
				.queryParam("key", googleMapProperties.getKey())
				.queryParam("input", name)
				.queryParam("language", region.getMapLangCode().getCode())
				.build())
			.retrieve()
			.onStatus(
				status -> !status.is2xxSuccessful(),
				response -> {
					var statusCode = response.statusCode().value();
					return response.bodyToMono(Exception.class).flatMap(errorResponse -> {
						log.warn("Google Place 장소 조회 실패 - 요청 정보: %s, 상태 코드: %s, 에러 응답: %s".formatted(name,
							statusCode,
							errorResponse), errorResponse);
						var errorLog = "[code: %s | message: %s]".formatted(statusCode, errorResponse.getMessage());
						errorLogSet.add(errorLog);
						return Mono.error(errorResponse);
					});
				}
			)
			.bodyToMono(PlaceInfoListResponseDto.class)
			.retryWhen(Retry.fixedDelay(1, Duration.ofSeconds(1)))
			.onErrorResume(err -> {
				errorLogSet.add(err.getMessage());
				alertWebhook.alertError("Google Place 장소 조회 최종 실패", errorLogSet.toString());
				return Mono.error(new HttpRequestRetryException(errorLogSet));
			})
			.timeout(Duration.ofSeconds(10))
			.block();
	}
}

