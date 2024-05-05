package com.peoplehere.api.common.service;

import java.util.Collections;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.peoplehere.shared.common.entity.TranslateText;
import com.peoplehere.shared.common.enums.LangCode;
import com.peoplehere.shared.common.repository.TranslateTextRepository;
import com.peoplehere.shared.common.service.TranslateService;
import com.peoplehere.shared.common.webhook.AlertWebhook;
import com.peoplehere.shared.tour.data.request.TourMessageTranslateRequestDto;
import com.peoplehere.shared.tour.data.response.TourMessageTranslateResponseDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {
	private final TranslateTextRepository translateTextRepository;
	private final TranslateService translateService;
	private final AlertWebhook alertWebhook;

	@Transactional
	public TourMessageTranslateResponseDto translateTourMessage(TourMessageTranslateRequestDto requestDto) {
		log.info("투어 메시지 번역 요청 | requestDto: {}", requestDto);

		TranslateText translateText;
		LangCode langCode = requestDto.language();
		String src = requestDto.content();
		try {
			translateText = translateTextRepository.findByTargetLangCodeAndSrc(langCode, src)
				.orElseGet(() -> {
					String dest = translateService.translate(Collections.singletonList(src), langCode);
					TranslateText newTranslateText = TranslateText.builder()
						.targetLangCode(langCode)
						.src(src)
						.dest(dest)
						.build();
					translateTextRepository.save(newTranslateText);
					log.info("투어 쪽지 번역 완료 - src: {}, dest: {}", requestDto.content(), dest);
					return newTranslateText;
				});
		} catch (RuntimeException e) {
			log.error("투어 쪽지 번역중 오류 발생 - 요청 정보: {}", requestDto, e);
			alertWebhook.alertError("투어 쪽지 번역중 오류 발생",
				"요청 정보: [%s], 에러메시지: [%s]".formatted(requestDto, e.getMessage()));
			throw new RuntimeException("투어 쪽지 번역중 오류 발생", e);
		}

		return TourMessageTranslateResponseDto.builder()
			.content(translateText.getDest())
			.language(requestDto.language())
			.build();
	}
}
