package com.peoplehere.api.common.service;

import static com.peoplehere.api.common.util.MessageUtils.*;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.peoplehere.api.common.config.PhoneVerificationProperties;
import com.peoplehere.api.common.data.request.MailVerifyRequestDto;
import com.peoplehere.api.common.data.request.PhoneVerifyRequestDto;
import com.peoplehere.api.common.data.response.MailVerificationResponseDto;
import com.peoplehere.api.common.data.response.PhoneVerificationResponseDto;
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
	 * @param email 이메일
	 * @return 인증 코드 만료 시간
	 */
	public MailVerificationResponseDto sendEmailVerificationCode(String email) {
		try {
			long start = System.currentTimeMillis();
			// 1. 이메일 인증 코드 생성 및 전송
			SimpleMailMessage message = new SimpleMailMessage();
			message.setTo(email);
			message.setSubject("[PEOPLE-HERE] 이메일 인증을 위한 인증 코드 발송");
			message.setText(generateRandomEmailVerifyCode());
			sender.send(message);

			// 2. redis에 인증 코드 만료시간 포함해서 저장 후 만료시간 반환
			MailVerificationResponseDto dto = redisTaskService.setEmailVerifyCode(email, message.getText());

			// 3. 이메일 인증 코드 전송 성공 알림
			alertWebhook.alertInfo("이메일 인증 코드 전송 성공",
				"이메일: [%s], 소요시간: [%d]ms".formatted(email, System.currentTimeMillis() - start));
			return dto;
		} catch (Exception e) {
			String errorMessage = "이메일 인증 코드 전송 실패 - email: [%s]".formatted(email);
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
	 * @param sendNumber  전화번호
	 * @return 인증 코드 만료 시간
	 */
	public PhoneVerificationResponseDto sendPhoneVerificationCode(String sendNumber) {
		try {
			long start = System.currentTimeMillis();
			// 1. 전화번호 인증 코드 생성 및 전송
			String code = generateRandomEmailVerifyCode();
			String response = webClient.post()
				.bodyValue(
					"To=" + sendNumber + "&From=" + phoneVerificationProperties.getFromNumber() + "&Body="
						+ "[PEOPLE-HERE] 전화번호 인증을 위한 인증 코드 발송: " + code)
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

}
