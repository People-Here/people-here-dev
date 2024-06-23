package com.peoplehere.api.tour.service;

import static com.peoplehere.shared.tour.data.request.PlaceDetailInfoRequestDto.*;

import java.util.Objects;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.peoplehere.api.common.service.RedisTaskService;
import com.peoplehere.shared.common.enums.LangCode;
import com.peoplehere.shared.common.enums.PageType;
import com.peoplehere.shared.common.enums.Region;
import com.peoplehere.shared.common.event.LocationInfoTranslatedEvent;
import com.peoplehere.shared.common.service.LocationManager;
import com.peoplehere.shared.common.service.MapComponent;
import com.peoplehere.shared.tour.data.request.PlaceInfoRequestDto;
import com.peoplehere.shared.tour.data.response.PlaceDetailResponseDto;
import com.peoplehere.shared.tour.data.response.PlaceInfoHistoryResponseDto;
import com.peoplehere.shared.tour.data.response.PlaceInfoListResponseDto;
import com.peoplehere.shared.tour.data.response.PlaceInfoResponseDto;
import com.peoplehere.shared.tour.repository.CustomLocationRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocationService {

	private final MapComponent mapService;
	private final CustomLocationRepository customLocationRepository;
	private final RedisTaskService redisTaskService;
	private final LocationManager locationManager;
	private final ApplicationEventPublisher eventPublisher;

	/**
	 * 장소명으로 관련 장소 목록 조회
	 * @param name
	 * @param region
	 * @return
	 */
	public PlaceInfoListResponseDto getPlaceInfoList(String name, Region region) {
		return mapService.fetchPlaceInfoList(name, region.getMapLangCode());
	}

	/**
	 * 장소 상세 정보 추가
	 * @param userId 사용자 id - email
	 * @param requestDto
	 * @return
	 */
	@Transactional
	public PlaceInfoResponseDto addPlaceInfo(String userId, PlaceInfoRequestDto requestDto) {
		LangCode mapLangCode = requestDto.region().getMapLangCode();
		try {
			PlaceInfoResponseDto responseDto = customLocationRepository.findPlaceInfoResponseDto(requestDto.placeId(),
					mapLangCode)
				.orElseGet(() -> {
					PlaceDetailResponseDto detailResponseDto = mapService.fetchPlaceDetailInfo(
						toDetailRequestDto(requestDto));
					return locationManager.convertPlaceInfoResponseDto(detailResponseDto, mapLangCode);
				});

			// 최근 검색어 내역에 저장
			if (StringUtils.hasText(userId)) {
				redisTaskService.addRecentSearchPlaceInfo(requestDto.type(), userId, responseDto);
			}

			// 번역 이벤트 발행
			eventPublisher.publishEvent(new LocationInfoTranslatedEvent(requestDto.placeId()));
			return responseDto;

		} catch (Exception exception) {
			log.error("장소 상세 정보 추가 중 오류 발생", exception);
			throw new RuntimeException("장소 상세 정보 추가 중 오류 발생", exception);
		}
	}

	/**
	 * 사용자의 최근 장소 검색 기록 조회
	 * @param type
	 * @param userId
	 * @return
	 */
	public PlaceInfoHistoryResponseDto getSearchHistory(PageType type, String userId) {
		try {
			return redisTaskService.getRecentSearchPlaceInfo(type, Objects.requireNonNull(userId));
		} catch (Exception exception) {
			log.error("유저: {} 장소 검색 기록 조회 중 오류 발생", userId, exception);
			throw exception;
		}
	}

}
