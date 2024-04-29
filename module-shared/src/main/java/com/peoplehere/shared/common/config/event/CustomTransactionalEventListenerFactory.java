package com.peoplehere.shared.common.config.event;

import java.lang.reflect.Method;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.transaction.event.TransactionalApplicationListenerMethodAdapter;
import org.springframework.transaction.event.TransactionalEventListenerFactory;

import lombok.NonNull;

public class CustomTransactionalEventListenerFactory extends TransactionalEventListenerFactory implements Ordered {

	private final TransactionalEventListenerErrorHandler callback;

	public CustomTransactionalEventListenerFactory(TransactionalEventListenerErrorHandler callback) {
		super();
		this.callback = callback;
	}

	@NonNull
	@Override
	public ApplicationListener<?> createApplicationListener(@NonNull String beanName, @NonNull Class<?> type,
		@NonNull Method method) {
		return new CustomTransactionalApplicationListenerMethodAdapter(beanName, type, method, callback);
	}

	static class CustomTransactionalApplicationListenerMethodAdapter extends
		TransactionalApplicationListenerMethodAdapter {
		public CustomTransactionalApplicationListenerMethodAdapter(String beanName, Class<?> targetClass, Method method,
			TransactionalEventListenerErrorHandler callback) {
			super(beanName, targetClass, method);
			addCallback(callback);
		}

		@Override
		public void onApplicationEvent(@NonNull ApplicationEvent event) {
			super.onApplicationEvent(event);
		}
	}

}
