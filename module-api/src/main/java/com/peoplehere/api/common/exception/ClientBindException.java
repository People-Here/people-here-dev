package com.peoplehere.api.common.exception;

import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;

/**
 * Bind 예외 중 클라이언트로부터의 요청 처리 중 발생한 예외.
 * 클라이언트에게는 예외의 세부 정보를 숨겨야 한다.
 * 따라서 별도로 예외처리하기 위해 예외 클래스를 분리
 */
public class ClientBindException extends BindException {
	public ClientBindException(BindingResult bindingResult) {
		super(bindingResult);
	}
}
