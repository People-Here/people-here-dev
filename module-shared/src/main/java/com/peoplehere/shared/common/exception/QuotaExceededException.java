package com.peoplehere.shared.common.exception;

public class QuotaExceededException extends RuntimeException {

	public QuotaExceededException(String message) {
		super("할당량 초과: [%s]".formatted(message));
	}

}
