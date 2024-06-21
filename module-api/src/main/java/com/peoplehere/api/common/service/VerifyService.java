package com.peoplehere.api.common.service;

import static com.peoplehere.api.common.util.MessageUtils.*;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.peoplehere.api.common.config.PhoneVerificationProperties;
import com.peoplehere.api.common.data.request.MailVerificationRequestDto;
import com.peoplehere.api.common.data.request.MailVerifyRequestDto;
import com.peoplehere.api.common.data.request.PhoneVerificationRequestDto;
import com.peoplehere.api.common.data.request.PhoneVerifyRequestDto;
import com.peoplehere.api.common.data.response.MailVerificationResponseDto;
import com.peoplehere.api.common.data.response.PhoneVerificationResponseDto;
import com.peoplehere.shared.common.enums.LangCode;
import com.peoplehere.shared.common.webhook.AlertWebhook;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class VerifyService {
	private final JavaMailSender sender;
	private final RedisTaskService redisTaskService;
	private final AlertWebhook alertWebhook;
	private final PhoneVerificationProperties phoneVerificationProperties;
	private WebClient webClient;

	@PostConstruct
	void init() {
		this.webClient = WebClient.builder()
			.baseUrl(phoneVerificationProperties.getSmsUrl())
			.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
			.defaultHeader(HttpHeaders.AUTHORIZATION, phoneVerificationProperties.getEncodedAuthStr())
			.build();
	}

	/**
	 * 이메일 인증 코드 생성 및 전송
	 * @param requestDto 인증 코드 생성 요청 정보
	 * @return 인증 코드 만료 시간
	 */
	public MailVerificationResponseDto sendEmailVerificationCode(MailVerificationRequestDto requestDto) {
		try {
			long start = System.currentTimeMillis();
			// 1. 이메일 인증 코드 생성 및 전송
			SimpleMailMessage message = new SimpleMailMessage();
			message.setTo(requestDto.email());
			message.setSubject(generateRandomEmailVerificationSubject(requestDto.langCode()));
			message.setText(generateRandomEmailVerificationText(requestDto, generateRandomEmailVerifyCode()));
			sender.send(message);

			// 2. redis에 인증 코드 만료시간 포함해서 저장 후 만료시간 반환
			MailVerificationResponseDto dto = redisTaskService.setEmailVerifyCode(requestDto.email(),
				message.getText());

			// 3. 이메일 인증 코드 전송 성공 알림
			alertWebhook.alertInfo("이메일 인증 코드 전송 성공",
				"요청: [%s], 소요시간: [%d]ms".formatted(requestDto, System.currentTimeMillis() - start));
			return dto;
		} catch (Exception e) {
			String errorMessage = "이메일 인증 코드 전송 실패 - request: [%s]".formatted(requestDto);
			log.error(errorMessage, e);
			alertWebhook.alertError(errorMessage, e.getMessage());
			throw new RuntimeException(errorMessage);
		}
	}

	/**
	 * 이메일 인증 코드 검증
	 * @param requestDto 이메일 인증 코드 검증 요청 정보
	 * @return 인증 성공 여부
	 */
	public boolean checkEmailVerifyCode(MailVerifyRequestDto requestDto) {
		return redisTaskService.checkEmailVerifyCode(requestDto.email(), requestDto.code());
	}

	/**
	 * 전화번호 인증 코드 생성 및 전송
	 * @param requestDto 인증 코드 생성 요청 정보
	 * @return 인증 코드 만료 시간
	 */
	public PhoneVerificationResponseDto sendPhoneVerificationCode(PhoneVerificationRequestDto requestDto) {
		String sendNumber = requestDto.getSendNumber();
		try {
			long start = System.currentTimeMillis();
			// 1. 전화번호 인증 코드 생성 및 전송
			String code = generateRandomEmailVerifyCode();
			String response = webClient.post()
				.bodyValue(
					"To=" + sendNumber + "&From=" + phoneVerificationProperties.getFromNumber() + "&Body="
						+ generatePhoneVerificationMessage(requestDto, code))
				.retrieve()
				.bodyToMono(String.class)
				.block();

			// 2. redis에 인증 코드 만료시간 포함해서 저장 후 만료시간 반환
			PhoneVerificationResponseDto dto = redisTaskService.setPhoneVerifyCode(sendNumber, code);

			// 3. 전화번호 인증 코드 전송 성공 알림
			alertWebhook.alertInfo("전화번호 인증 코드 전송 성공",
				"전화번호: [%s], 소요시간: [%d]ms".formatted(sendNumber, System.currentTimeMillis() - start));
			return dto;
		} catch (Exception e) {
			String errorMessage = "전화번호 인증 코드 전송 실패 - phoneNumber: [%s]".formatted(sendNumber);
			log.error(errorMessage, e);
			alertWebhook.alertError(errorMessage, e.getMessage());
			throw new RuntimeException(errorMessage);
		}
	}

	/**
	 * 전화번호 인증 코드 검증
	 * @param requestDto 전화번호 인증 코드 검증 요청 정보
	 * @return 인증 성공 여부
	 */
	public boolean checkPhoneVerifyCode(PhoneVerifyRequestDto requestDto) {
		return redisTaskService.checkPhoneVerifyCode(requestDto.getVerifyPhoneNumber(), requestDto.code());
	}

	private static String generateRandomEmailVerificationSubject(LangCode langCode) {
		if (LangCode.KOREAN.equals(langCode)) {
			return "[PeopleHere] 이메일 인증 코드";
		} else {
			return "[PeopleHere] Email address verification code";
		}
	}

	private static String generateRandomEmailVerificationText(MailVerificationRequestDto requestDto, String code) {
		if (LangCode.KOREAN.equals(requestDto.langCode())) {
			return code + "\n다른 사람과 공유하지 마세요.";
		} else {
			return code + "\nDon’t share it.";
		}
	}

	private static String generatePhoneVerificationMessage(PhoneVerificationRequestDto requestDto, String code) {
		if (LangCode.KOREAN.equals(requestDto.langCode())) {
			return code + ": PeopleHere 본인인증 코드";
		}
		return code + " is your PeopleHere verification code.";
	}
}
