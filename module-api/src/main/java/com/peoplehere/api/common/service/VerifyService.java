package com.peoplehere.api.common.service;

import static com.peoplehere.api.common.util.MessageUtils.*;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.peoplehere.api.common.data.request.MailVerifyRequestDto;
import com.peoplehere.api.common.data.response.MailVerificationResponseDto;
import com.peoplehere.shared.common.webhook.AlertWebhook;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class VerifyService {
	private final JavaMailSender sender;
	private final RedisTaskService redisTaskService;
	private final AlertWebhook alertWebhook;

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

}
