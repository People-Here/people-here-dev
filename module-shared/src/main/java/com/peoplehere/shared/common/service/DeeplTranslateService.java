package com.peoplehere.shared.common.service;

import java.time.Duration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;

import com.peoplehere.shared.common.data.request.TranslationTextRequestDto;
import com.peoplehere.shared.common.data.response.TranslationTextResponseDto;
import com.peoplehere.shared.common.data.response.TranslationUsageResponseDto;
import com.peoplehere.shared.common.enums.LangCode;
import com.peoplehere.shared.common.exception.QuotaExceededException;
import com.peoplehere.shared.common.webhook.AlertWebhook;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SignalType;
import reactor.util.retry.Retry;

@Profile("!test")
@Slf4j
@Service
@RequiredArgsConstructor
public class DeeplTranslateService implements TranslateService {

	@Value("${service.translate.deepl.url}")
	private String url;

	@Value("${service.translate.deepl.auth-key}")
	private String key;

	private WebClient webClient;
	private final AlertWebhook alertWebhook;
	private static final String DEEPL_AUTH_KEY = "DeepL-Auth-Key %s";

	@PostConstruct
	void init() {
		this.webClient = WebClient.builder()
			.baseUrl(url)
			.defaultHeader(HttpHeaders.AUTHORIZATION, DEEPL_AUTH_KEY.formatted(key))
			.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
			.build();
	}

	/**
	 * 번역 서비스를 이용해 번역 결과를 반환합니다.
	 * TODO: 현재는 Deepl 번역 서비스만 사용 추후 할당량 초과시 작업 핸들링을 위해 api key 추가 및 다른 번역 서비스 추가 가능
	 * @param srcList 번역할 텍스트 리스트
	 * @param langCode 번역할 언어 코드
	 * @see <a href="https://www.deepl.com/ko/docs-api/translate-text">Deepl Supported Languages</a>
	 * @return 번역 결과
	 */
	@Override
	public String translate(List<String> srcList, LangCode langCode) {
		TranslationTextRequestDto request = TranslationTextRequestDto.builder()
			.srcList(srcList)
			.targetLang(langCode.getCode())
			.build();

		LinkedHashSet<String> errorLogSet = new LinkedHashSet<>();
		try {
			var translationResponse = webClient
				.post()
				.uri("/translate")
				.bodyValue(request)
				.retrieve()
				.onStatus(
					status -> status.equals(HttpStatus.TOO_MANY_REQUESTS),
					response -> {
						log.info("요청 제한 초과 - 요청 정보: {}", request.getSrcList());
						return Mono.error(new RuntimeException("Too many requests, will retry..."));
					}
				)
				.onStatus(
					status -> status.value() == 456,
					response -> {
						log.error("할당량 초과 - 요청 정보: {}", request.getSrcList());
						alertWebhook.alertError("Deepl 번역 서비스 할당량 초과, 개발자 체크 필요", request.getSrcList().toString());
						return Mono.error(new QuotaExceededException(request.getSrcList().toString()));
					}
				)
				.onStatus(
					DeeplTranslateService::isNonSuccessfulStatusCode,
					response -> {
						var statusCode = response.statusCode().value();
						var requestInfo = request.getSrcList();
						return response.bodyToMono(Exception.class).flatMap(errorResponse -> {
							log.warn(
								"Deepl 번역 서비스 실패 - 요청 정보: %s, 상태 코드: %s, 에러 응답: %s".formatted(requestInfo, statusCode,
									errorResponse), errorResponse);
							var errorLog = "[code: %s | message: %s]".formatted(statusCode, errorResponse.getMessage());
							errorLogSet.add(errorLog);
							return Mono.error(errorResponse);
						});
					}
				)
				.bodyToMono(TranslationTextResponseDto.class)
				.retryWhen(Retry.backoff(2, Duration.ofSeconds(1))
					.maxBackoff(Duration.ofSeconds(10))
					.filter(throwable -> {
						// 토큰별 요청 가능 횟수 부족일 때 응답 코드인 456로 실패한 경우 재시도 하지 않음
						return !(throwable instanceof QuotaExceededException);
					}))
				.onErrorResume(error -> {
					errorLogSet.add(error.getMessage());
					return Mono.error(error);
				})
				.doFinally(signalType -> {
					if (SignalType.ON_COMPLETE.equals(signalType) && !errorLogSet.isEmpty()) {
						log.info("Deepl 번역 서비스 실패 후 재시도 성공: {}", errorLogSet);
						return;
					}
					if (SignalType.ON_ERROR.equals(signalType)) {
						log.warn("Deepl 번역 서비스 재시도 실패: {}", errorLogSet);
					}
				})
				.block();

			String result = getTranslateText(translationResponse);
			if (!StringUtils.hasText(result)) {
				alertWebhook.alertError("번역 결과가 공란으로 나와버렸어요. 고쳐줘요", "요청 정보: [%s], 결과: [%s]".formatted(request, result));
			}
			return result;
		} catch (Exception exception) {
			log.error("번역 실패 - 요청 정보: {}", request.getSrcList(), exception);
			alertWebhook.alertError("Deepl 번역 서비스 실패",
				"요청 정보: [%s], 에러 정보: [%s] 에러 메시지: [%s]".formatted(request.getSrcList(), errorLogSet,
					exception.getMessage()));
			throw new RuntimeException("번역 실패 - 요청 정보: %s".formatted(request.getSrcList()), exception);
		}
	}

	/**
	 * 번역 서비스 사용량을 반환합니다.
	 * @return
	 */
	@Override
	public TranslationUsageResponseDto getUsage() {
		return webClient
			.get()
			.uri("/usage")
			.retrieve()
			.bodyToMono(TranslationUsageResponseDto.class)
			.block();
	}

	private static String getTranslateText(TranslationTextResponseDto translationResponse) {
		String result = "";
		for (var i = 0; i < Objects.requireNonNull(translationResponse).getTranslations().size(); i++) {
			var translation = translationResponse.getTranslations().get(i);
			log.debug("translatedText: {}", translation.getText());
			result = translation.getText();
		}
		return result;
	}

	private static boolean isNonSuccessfulStatusCode(HttpStatusCode status) {
		return !status.is2xxSuccessful()
			&& !status.equals(HttpStatus.TOO_MANY_REQUESTS)
			&& !status.equals(HttpStatus.valueOf(456));
	}

}
