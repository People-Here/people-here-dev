package com.peoplehere.shared.common.exception;

import java.util.Set;

public class HttpRequestRetryException extends RuntimeException {

	public HttpRequestRetryException(Set<String> invalidMessages) {
		super(invalidMessages.toString());
	}
}
