package com.peoplehere.shared.common.service;

import java.time.Duration;
import java.util.LinkedHashSet;
import java.util.NoSuchElementException;
import java.util.Objects;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.peoplehere.shared.common.config.map.GoogleMapProperties;
import com.peoplehere.shared.common.enums.LangCode;
import com.peoplehere.shared.common.exception.HttpRequestRetryException;
import com.peoplehere.shared.common.webhook.AlertWebhook;
import com.peoplehere.shared.tour.data.request.PlaceDetailInfoRequestDto;
import com.peoplehere.shared.tour.data.response.PlaceDetailResponseDto;
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
@Component
@RequiredArgsConstructor
class GoogleMapComponentImpl implements MapComponent {

	private WebClient webClient;
	private final AlertWebhook alertWebhook;
	private final GoogleMapProperties googleMapProperties;

	private static final String POINT_COUNTRY = "country:KR";
	private static final String PLACE_DETAIL_FIELDS =
		"name,"
			+ "place_id,"
			+ "geometry/location,"
			+ "formatted_address,"
			+ "address_components";

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
	 * @param langCode 언어 코드
	 * @return 장소 정보
	 */
	@Override
	public PlaceInfoListResponseDto fetchPlaceInfoList(String name, LangCode langCode) {
		var errorLogSet = new LinkedHashSet<String>();

		return webClient.get()
			.uri(uri -> uri
				.path(googleMapProperties.getPlaceUri())
				.queryParam("key", googleMapProperties.getKey())
				.queryParam("input", name)
				.queryParam("language", langCode.getCode())
				.queryParam("components", POINT_COUNTRY)
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

	/**
	 * 장소ID를 통해 장소 상세 정보 조회
	 * @param requestDto
	 * @return
	 * @throws NoSuchElementException
	 */
	@Override
	public PlaceDetailResponseDto fetchPlaceDetailInfo(PlaceDetailInfoRequestDto requestDto) throws
		NoSuchElementException {
		LangCode langCode = requestDto.langCode();

		try {
			log.info("장소 상세 정보 조회 요청 - placeId: {}, langCode: {}", requestDto.placeId(), langCode);

			PlaceDetailResponseDto responseDto = webClient.get()
				.uri(uriBuilder -> uriBuilder
					.path("/place/details/json")
					.queryParam("place_id", requestDto.placeId())
					.queryParam("fields", PLACE_DETAIL_FIELDS)
					.queryParam("key", googleMapProperties.getKey())
					.queryParam("language", langCode.getCode())
					.build())
				.retrieve()
				.onStatus(status -> !status.is2xxSuccessful(),
					response -> Mono.error(new RuntimeException("Failed to fetch place details")))
				.bodyToMono(PlaceDetailResponseDto.class)
				.retryWhen(Retry.fixedDelay(1, Duration.ofSeconds(1)))
				.block();

			log.info("장소 상세 정보 조회 성공 - responseDto: {}, langCode: {}", responseDto, langCode);
			return Objects.requireNonNull(responseDto);

		} catch (Exception exception) {
			log.error("장소 id: {}, langCode: {} 상세 정보 조회 중 오류 발생", requestDto.placeId(), requestDto.langCode(),
				exception);
			alertWebhook.alertError("Google Place 장소 상세 정보 조회 실패",
				"placeId: %s, langCode: %s, 에러메시지: %s".formatted(requestDto.placeId(), langCode,
					exception.getMessage()));
			throw new NoSuchElementException("장소 상세 정보 조회 실패", exception);
		}
	}

}

