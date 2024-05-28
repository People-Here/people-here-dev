package com.peoplehere.shared.common.event.listener;

import static com.peoplehere.shared.common.enums.LangCode.*;

import java.util.List;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.peoplehere.shared.common.enums.LangCode;
import com.peoplehere.shared.common.event.LocationInfoTranslatedEvent;
import com.peoplehere.shared.common.service.LocationManager;
import com.peoplehere.shared.common.service.MapComponent;
import com.peoplehere.shared.common.webhook.AlertWebhook;
import com.peoplehere.shared.tour.data.request.PlaceDetailInfoRequestDto;
import com.peoplehere.shared.tour.data.response.PlaceDetailResponseDto;
import com.peoplehere.shared.tour.repository.LocationInfoRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 장소 번역의 경우 번역 api 사용 대신 map api 언어 옵션 변경해서 요청
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LocationInfoTranslateEventListener {

	private final MapComponent mapComponent;
	private final LocationManager locationManager;
	private final LocationInfoRepository locationInfoRepository;
	private static final List<LangCode> SUPPORTED_LANG_CODES = List.of(KOREAN, ENGLISH);
	private final AlertWebhook alertWebhook;

	/**
	 * 장소 정보 번역 요청 이벤트 수신
	 * @param event
	 */
	@Async("map-translate")
	@Transactional
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleTourInfoTranslatedEvent(LocationInfoTranslatedEvent event) {
		String placeId = event.id();
		log.info("장소 정보 번역 요청 이벤트 수신 | id: {}", placeId);

		try {
			for (LangCode supportedLangCode : SUPPORTED_LANG_CODES) {
				updateLocationInfo(placeId, supportedLangCode);
			}
		} catch (Exception exception) {
			log.error("번역 결과 저장 중 오류 발생 id: {}", placeId, exception);
		}
	}

	private void updateLocationInfo(String placeId, LangCode langCode) {
		try {
			if (!locationInfoRepository.existsByPlaceIdAndLangCode(placeId, langCode)) {
				log.info("장소 정보 번역 요청 | placeId: {}, langCode: {}", placeId, langCode);
				PlaceDetailResponseDto placeDetailResponseDto = mapComponent.fetchPlaceDetailInfo(
					new PlaceDetailInfoRequestDto(placeId, langCode));
				locationManager.convertPlaceInfoResponseDto(placeDetailResponseDto, langCode);
			}
		} catch (Exception exception) {
			log.error("장소 번역 요청 중 오류 발생 id: {}, langCode: {}", placeId, langCode, exception);
			alertWebhook.alertError("장소 번역 요청 중 오류 발생 id: %s, langCode: %s".formatted(placeId, langCode),
				exception.getMessage());
			throw exception;
		}
	}
}
