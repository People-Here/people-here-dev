package com.peoplehere.shared.common.config.event;

import org.springframework.stereotype.Component;
import org.springframework.util.ErrorHandler;

import com.peoplehere.shared.common.webhook.AlertWebhook;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventListenerErrorHandler implements ErrorHandler {

	private final AlertWebhook alertWebhook;

	@Override
	public void handleError(@NonNull Throwable throwable) {
		log.warn("이벤트 리스너의 이벤트 처리 중 예외 발생", throwable);
		alertWebhook.alertError("이벤트 리스너의 이벤트 처리 중 예외 발생", throwable.getMessage());
	}

}
