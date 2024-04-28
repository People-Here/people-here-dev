package com.peoplehere.api.common.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.peoplehere.shared.common.data.response.TranslationUsageResponseDto;
import com.peoplehere.shared.common.enums.LangCode;
import com.peoplehere.shared.common.service.TranslateService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
public class StatusController {

	private final TranslateService translateService;

	@Value("${server.port}")
	int port;

	@Value("${app.ip.public:#{null}}")
	String publicIp;

	@GetMapping("/api/local/delay")
	public ResponseEntity<String> delayGet() throws InterruptedException {
		log.info("30초간 작업하는척 ");

		for (int i = 0; i < 30; i++) {
			Thread.sleep(1000);
			if (i % 5 == 0) {
				log.info("{}, {} 초 지남", port, i);
			}
		}

		return ResponseEntity.ok("success");
	}

	@GetMapping("/api/health")
	public ResponseEntity<String> health() {
		return ResponseEntity.status(200).body("hello!, im " + publicIp);
	}

	/**
	 * metric 에러로그 수집 테스트
	 * @return
	 */
	@GetMapping("/error-test")
	public ResponseEntity<Long> errorTest() {
		try {
			List<String> list = new ArrayList<>();
			list.get(2).toString();
		} catch (Exception e) {
			log.error("에러로그 테스트중. 리스트에서 잘못된 접근!", e);
		}

		var date = System.currentTimeMillis();
		return ResponseEntity.ok(date);
	}

	/**
	 * 번역 요청 로컬용 테스트
	 * @param langCode
	 * @param message
	 * @return
	 */
	@GetMapping("/api/local/translate/{langCode}/{message}")
	public ResponseEntity<String> translate(@PathVariable LangCode langCode, @PathVariable String message) {
		log.info("번역 요청: langCode: {}, message: {}", langCode, message);

		try {
			long start = System.currentTimeMillis();
			String result = translateService.translate(List.of(message), langCode);
			log.info("번역 결과: {}, 소요시간: {}ms", result, System.currentTimeMillis() - start);
			return ResponseEntity.ok(result);
		} catch (Exception e) {
			log.error("번역 실패", e);
			return ResponseEntity.internalServerError().body("번역 실패");
		}
	}

	@GetMapping("/api/local/translate/usage")
	public ResponseEntity<TranslationUsageResponseDto> getTranslateUsage() {

		return ResponseEntity.ok(translateService.getUsage());
	}
}
