package com.peoplehere.shared.common.config.event;

import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.PayloadApplicationEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalApplicationListener;

import com.peoplehere.shared.common.webhook.AlertWebhook;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TransactionalEventListenerErrorHandler
	implements TransactionalApplicationListener.SynchronizationCallback {

	private AlertWebhook alertWebhook;

	/**
	 * 커스텀 팩토리 빈이 생성되는 시점에는 해당 빈이 주입받는 빈들이 아직 초기화되지 않았기 때문에, Application Context가 초기화된 시점에 수동 주입할 수 있도록 이벤트 리스너를 등록
	 * @param event 애플리케이션 시작 이벤트
	 */
	@EventListener(ApplicationStartedEvent.class)
	public void onStart(final ApplicationStartedEvent event) {
		alertWebhook = event.getApplicationContext()
			.getBeansOfType(AlertWebhook.class)
			.values()
			.stream()
			.findFirst()
			.orElseThrow(() -> new IllegalStateException("AlertWebhook 빈이 없습니다"));

		log.info("TransactionalEventListenerErrorHandlerCallback 초기화됨. 알림채널 {}", alertWebhook);
	}

	@Override
	public void postProcessEvent(@NonNull ApplicationEvent event, Throwable ex) {
		// 리스너에서 에러가 발생한 경우 예외가 null 일 수 없으므로 무시
		if (ex == null) {
			return;
		}

		log.error("트랜잭션 이벤트 리스너의 {} 이벤트 처리 중 예외 발생 | event: {}", getEventName(event), event, ex);

		var errorTitle = "트랜잭션 이벤트 리스너의 %s 이벤트 처리 중 예외 발생".formatted(getEventName(event));
		alertWebhook.alertError(errorTitle, ex.getMessage());
	}

	private String getEventName(ApplicationEvent event) {
		if (event instanceof PayloadApplicationEvent<?> payloadApplicationEvent) {
			return payloadApplicationEvent.getPayload().getClass().getSimpleName();
		}
		return event.getClass().getSimpleName();
	}

}

