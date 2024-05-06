package com.peoplehere.api.tour.service;

import java.time.Duration;
import java.util.LinkedHashSet;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;

import com.peoplehere.api.common.exception.HttpRequestRetryException;
import com.peoplehere.api.common.service.RedisTaskService;
import com.peoplehere.api.tour.config.GoogleMapProperties;
import com.peoplehere.shared.common.enums.LangCode;
import com.peoplehere.shared.common.enums.Region;
import com.peoplehere.shared.common.webhook.AlertWebhook;
import com.peoplehere.shared.tour.data.request.PlaceInfoRequestDto;
import com.peoplehere.shared.tour.data.response.PlaceDetailResponseDto;
import com.peoplehere.shared.tour.data.response.PlaceInfoListResponseDto;
import com.peoplehere.shared.tour.data.response.PlaceInfoResponseDto;
import com.peoplehere.shared.tour.entity.Place;
import com.peoplehere.shared.tour.entity.PlaceInfo;
import com.peoplehere.shared.tour.repository.PlaceInfoRepository;
import com.peoplehere.shared.tour.repository.PlaceRepository;

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
	private final PlaceRepository placeRepository;
	private final PlaceInfoRepository placeInfoRepository;
	private final RedisTaskService redisTaskService;
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
	 * 장소Id로 장소 상세 정보를 조회한 후 없으면 추가 후 반환
	 * 최근 검색어 내역 저장을 위해 추가 TODO: 이벤트 방식으로 빼자
	 * @param userId 사용자id
	 * @param requestDto 장소 상세 정보
	 * @return
	 */
	@Override
	@Transactional
	public PlaceInfoResponseDto getPlaceDetailInfo(String userId, PlaceInfoRequestDto requestDto) throws
		NoSuchElementException {
		Optional<PlaceInfo> placeInfo = placeInfoRepository.findByPlaceIdAndLangCode(requestDto.placeId(),
			requestDto.region().getMapLangCode());

		PlaceInfoResponseDto placeInfoResponseDto = placeInfo.map(info -> PlaceInfoResponseDto.builder()
				.placeId(info.getPlaceId())
				.name(info.getName())
				.address(info.getAddress())
				.build())
			.orElseGet(() -> addPlaceDetailInfo(requestDto));

		// 3. 검색어 내역에 저장
		if (StringUtils.hasText(userId)) {
			redisTaskService.addRecentSearchPlaceInfo(userId, placeInfoResponseDto);
		}
		return placeInfoResponseDto;
	}

	/**
	 * Google Place API를 사용해 장소 상세 정보 조회
	 * 재시도 1회 1초 간격
	 * @param requestDto
	 * @return
	 */
	private PlaceInfoResponseDto addPlaceDetailInfo(PlaceInfoRequestDto requestDto) throws NoSuchElementException {
		LangCode langCode = requestDto.region().getMapLangCode();
		try {
			log.info("장소 상세 정보 추가 요청 - placeId: {}, langCode: {}", requestDto.placeId(), langCode);
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

			log.info("장소 상세 정보 추가 성공 - responseDto: {}, langCode: {}", responseDto, langCode);
			Place place = convertPlaceEntity(Objects.requireNonNull(responseDto));
			PlaceInfo placeInfo = convertPlaceInfoEntity(Objects.requireNonNull(responseDto), langCode);

			return PlaceInfoResponseDto.builder()
				.placeId(place.getPlaceId())
				.name(placeInfo.getName())
				.address(placeInfo.getAddress())
				.build();

		} catch (Exception exception) {
			log.error("장소 id: {}, region: {} 상세 정보 추가 중 오류 발생", requestDto.placeId(), requestDto.region(), exception);
			alertWebhook.alertError("Google Place 장소 상세 정보 조회 실패",
				"placeId: %s, langCode: %s, 에러메시지: %s".formatted(requestDto.placeId(), langCode,
					exception.getMessage()));
			throw new NoSuchElementException("장소 상세 정보 조회 실패", exception);
		}
	}

	private Place convertPlaceEntity(PlaceDetailResponseDto responseDto) {
		String placeId = responseDto.getPlaceDetails().getPlaceId();

		return placeRepository.findByPlaceId(placeId).orElseGet(() -> {
			Place newPlace = Place.builder()
				.placeId(placeId)
				.latitude(responseDto.getPlaceDetails().getGeometry().getLocation().getLat())
				.longitude(responseDto.getPlaceDetails().getGeometry().getLocation().getLng())
				.build();
			return placeRepository.save(newPlace);
		});
	}

	/**
	 * TODO: 나중에 좀 고치기
	 * @param responseDto
	 * @param langCode
	 * @return
	 */
	private PlaceInfo convertPlaceInfoEntity(PlaceDetailResponseDto responseDto, LangCode langCode) {
		PlaceDetailResponseDto.PlaceDetails details = responseDto.getPlaceDetails();
		String placeId = details.getPlaceId();

		return placeInfoRepository.findByPlaceIdAndLangCode(placeId, langCode).orElseGet(() -> {
			PlaceInfo newPlaceInfo = PlaceInfo.builder()
				.placeId(placeId)
				.langCode(langCode)
				.name(details.getName())
				.address(details.getFormattedAddress())
				.country(details.getAddressComponents().stream()
					.filter(c -> c.getTypes().contains("country") && c.getTypes().contains("political"))
					.findFirst()
					.map(PlaceDetailResponseDto.AddressComponent::getLongName)
					.orElse(null))
				.city(details.getAddressComponents().stream()
					.filter(
						c -> c.getTypes().contains("administrative_area_level_1"))
					.findFirst()
					.map(PlaceDetailResponseDto.AddressComponent::getLongName)
					.orElse(null))
				.district(details.getAddressComponents().stream()
					.filter(c -> c.getTypes().contains("sublocality_level_1") && c.getTypes().contains("sublocality"))
					.findFirst()
					.map(PlaceDetailResponseDto.AddressComponent::getLongName)
					.orElse(null))
				.streetAddress(details.getAddressComponents().stream()
					.filter(c -> c.getTypes().contains("route"))
					.findFirst()
					.map(PlaceDetailResponseDto.AddressComponent::getLongName)
					.orElse(null))
				.build();
			return placeInfoRepository.save(newPlaceInfo);
		});
	}

}

