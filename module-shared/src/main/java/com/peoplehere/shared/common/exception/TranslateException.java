package com.peoplehere.shared.common.exception;

public class TranslateException extends RuntimeException {

	public TranslateException(String message, Throwable cause) {
		super("번역 실패: [%s]".formatted(message), cause);
	}

}
