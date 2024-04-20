package com.peoplehere.api.common.service;

import java.time.LocalDateTime;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.peoplehere.shared.common.webhook.AlertWebhook;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@EnableScheduling
@RequiredArgsConstructor
public class AccountScheduler {

	private final AccountService accountService;
	private final AlertWebhook alertWebhook;

	/**
	 * 보존기간이 지난 비활성화 계정 삭제
	 * 매일 새벽 3시에 실행
	 */
	@Scheduled(cron = "0 0 3 * * *", zone = "Asia/Seoul")
	public void deleteDeactivatedAccount() {
		try {
			LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
			accountService.deleteAccountByBaseDateTime(thirtyDaysAgo);
		} catch (Exception exception) {
			log.error("비활성화 계정 삭제 스케줄링 도중 실패", exception);
			alertWebhook.alertError("비활성화 계정 삭제 스케줄링 도중 실패",
				"발생 시간: [%s] 에러 메시지: [%s]".formatted(LocalDateTime.now(), exception.getMessage()));
		}
	}

}
