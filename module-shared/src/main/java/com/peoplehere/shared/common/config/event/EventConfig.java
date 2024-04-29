package com.peoplehere.shared.common.config.event;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.transaction.event.TransactionalEventListenerFactory;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class EventConfig {

	@Bean
	public ApplicationEventMulticaster applicationEventMulticaster(
		EventListenerErrorHandler eventListenerErrorHandler) {
		var eventMulticaster = new SimpleApplicationEventMulticaster();
		eventMulticaster.setErrorHandler(eventListenerErrorHandler);
		return eventMulticaster;
	}

	/**
	 * TransactionalEventListener 내부에서 발생하는 이벤트를 핸들링하기 위한 커스텀 콜백을 등록하기 위한 커스텀 팩토리 빈을 생성
	 * @param transactionalEventListenerErrorHandler 예외 핸들링용 콜백
	 * @return 커스텀 팩토리 빈
	 */
	@Bean
	public TransactionalEventListenerFactory transactionalEventListenerFactory(
		TransactionalEventListenerErrorHandler transactionalEventListenerErrorHandler) {
		return new CustomTransactionalEventListenerFactory(transactionalEventListenerErrorHandler);
	}

}

