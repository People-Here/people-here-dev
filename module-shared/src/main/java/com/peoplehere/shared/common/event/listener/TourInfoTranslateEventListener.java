package com.peoplehere.shared.common.event.listener;

import static com.peoplehere.shared.common.enums.LangCode.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.util.StringUtils;

import com.peoplehere.shared.common.entity.TranslateText;
import com.peoplehere.shared.common.enums.LangCode;
import com.peoplehere.shared.common.event.TourInfoTranslatedEvent;
import com.peoplehere.shared.common.exception.TranslateException;
import com.peoplehere.shared.common.repository.TranslateTextRepository;
import com.peoplehere.shared.common.service.TranslateService;
import com.peoplehere.shared.common.webhook.AlertWebhook;
import com.peoplehere.shared.tour.data.request.TourInfoTranslateRequestDto;
import com.peoplehere.shared.tour.data.response.TourInfoTranslateResponseDto;
import com.peoplehere.shared.tour.entity.TourInfo;
import com.peoplehere.shared.tour.repository.TourInfoRepository;

import jakarta.transaction.Transactional;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class TourInfoTranslateEventListener {

	private final TranslateService translateService;
	private final TourInfoRepository tourInfoRepository;
	private final TranslateTextRepository translateTextRepository;
	private static final List<LangCode> SUPPORTED_LANG_CODES = List.of(KOREAN, ENGLISH);
	private final AlertWebhook alertWebhook;

	/**
	 * 투어 정보 번역 요청 이벤트 수신
	 * @param event
	 */
	@Async("tour-translate")
	@Transactional
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleTourInfoTranslatedEvent(TourInfoTranslatedEvent event) {
		log.info("투어 정보 번역 요청 이벤트 수신 | id: {}", event.id());
		List<TranslateText> newTranslateTextList = new ArrayList<>();

		try {
			for (LangCode supportedLangCode : SUPPORTED_LANG_CODES) {
				updateTourInfo(supportedLangCode, event, newTranslateTextList);
			}

			log.info("번역 결과 모두 저장 | translateTextList: {}", newTranslateTextList);
			translateTextRepository.saveAll(newTranslateTextList);
		} catch (Exception exception) {
			log.error("번역 결과 저장 중 오류 발생 체크 필요 | list: {}", newTranslateTextList, exception);
			alertWebhook.alertError("번역 결과 저장 중 오류 발생 일단 skip",
				"list: [%s], 에러메시지: [%s]".formatted(newTranslateTextList, exception.getMessage()));
		}
	}

	/**
	 * 지원하는 언어 코드에 대해 번역 처리 후 투어 정보 저장
	 * 번역 오류 발생시 원문 정보와 다른 정보 유지를 막기위해 해당 tourInfo 삭제
	 * @param supportedLangCode
	 * @param event
	 * @param newTranslateTextList
	 */
	private void updateTourInfo(LangCode supportedLangCode, TourInfoTranslatedEvent event,
		List<TranslateText> newTranslateTextList) {
		try {
			TourInfoTranslateRequestDto requestDto = event.requestDto();
			Map<String, String> translationMap = new HashMap<>();

			TourInfoTranslateEventListener.TranslationContext context = TranslationContext.builder()
				.requestDto(requestDto)
				.targetLangCode(supportedLangCode)
				.translationMap(translationMap)
				.build();

			List<TranslateText> translateTextList = translateTextRepository.findByTargetLangCodeAndSrcIn(
				supportedLangCode, List.of(requestDto.getTitle(), requestDto.getDescription()));
			translateTextList.forEach(text -> translationMap.putIfAbsent(text.getSrc(), text.getDest()));

			TourInfo tourInfo = tourInfoRepository.findByTourIdAndLangCode(event.id(),
					supportedLangCode)
				.orElse(null);

			if (tourInfo == null) {
				tourInfo = TourInfo.builder()
					.tourId(event.id())
					.langCode(supportedLangCode)
					.build();
			}

			tourInfo.updateTranslatedInfo(getTourInfoTranslateResponseDto(context, newTranslateTextList));
			tourInfoRepository.save(tourInfo);
		} catch (TranslateException translateException) {
			log.warn("프로필 번역 실패 해당 accountInfo 저장하지 않음 | id: {}, dto: {}, langCode: {}", event.id(),
				event.requestDto(), supportedLangCode, translateException);
			alertWebhook.alertError("프로필 번역 실패 해당 accountInfo 저장하지 않음. 일단 skip",
				"id: [%s], dto: [%s], langCode: [%s], 에러메시지: [%s]".formatted(event.id(), event.requestDto(),
					supportedLangCode, translateException.getMessage()));
			tourInfoRepository.deleteByTourIdAndLangCode(event.id(), supportedLangCode);
		}
	}

	/**
	 * 번역된 결과값을 반환
	 * @param context
	 * @return
	 * @throws TranslateException 번역 중 오류 발생시
	 */
	private TourInfoTranslateResponseDto getTourInfoTranslateResponseDto(
		TourInfoTranslateEventListener.TranslationContext context,
		List<TranslateText> newTranslateTextList) throws TranslateException {
		try {
			log.info("투어 정보 번역 | dto: {}, langCode: {}", context.requestDto, context.targetLangCode);
			long start = System.currentTimeMillis();
			TourInfoTranslateResponseDto dto = TourInfoTranslateResponseDto.builder()
				.langCode(context.targetLangCode)
				.title(translate(context.requestDto.getTitle(), context, newTranslateTextList))
				.description(translate(context.requestDto.getDescription(), context, newTranslateTextList))
				.build();
			log.info("투어 정보 번역 완료 | dto: {}, langCode: {}, 실행시간: {}ms", dto, context.targetLangCode,
				System.currentTimeMillis() - start);
			return dto;
		} catch (Exception exception) {
			throw new TranslateException("투어 정보 번역 중 오류 발생", exception);
		}

	}

	/**
	 * 번역된 텍스트가 없다면 번역 서비스를 통해 번역하고 저장
	 * @param srcText
	 * @param context
	 * @return
	 */
	private String translate(String srcText, TourInfoTranslateEventListener.TranslationContext context,
		List<TranslateText> newTranslateTextList) {
		if (!StringUtils.hasText(srcText)) {
			return srcText;
		}

		// 기존 번역된 텍스트가 있는지 확인
		String translatedText = context.translationMap.get(srcText);
		if (translatedText == null) {
			// 번역 서비스를 통해 새로 번역
			translatedText = translateService.translate(List.of(srcText), context.targetLangCode);
			// 새로운 번역 결과를 저장
			TranslateText newTranslation = TranslateText.builder()
				.src(srcText)
				.dest(translatedText)
				.targetLangCode(context.targetLangCode)
				.build();
			newTranslateTextList.add(newTranslation);
		}
		return translatedText;
	}

	/**
	 * 번역할 투어 정보 dto
	 * @param requestDto
	 * @param targetLangCode
	 * @param translationMap
	 */
	@Builder
	private record TranslationContext(TourInfoTranslateRequestDto requestDto, LangCode targetLangCode,
										Map<String, String> translationMap) {
	}

}
