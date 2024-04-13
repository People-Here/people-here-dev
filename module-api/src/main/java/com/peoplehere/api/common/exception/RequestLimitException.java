package com.peoplehere.api.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class RequestLimitException extends RuntimeException {
	public RequestLimitException(String message) {
		super("사용자의 요청 횟수 초과: [%s]".formatted(message));
	}

}
