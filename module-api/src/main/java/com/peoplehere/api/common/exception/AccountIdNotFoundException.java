package com.peoplehere.api.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import jakarta.persistence.EntityNotFoundException;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class AccountIdNotFoundException extends EntityNotFoundException {
	public AccountIdNotFoundException(String accountId) {
		super("계정 ID (%s)에 해당하는 유저를 찾을 수 없습니다.".formatted(accountId));
	}
}
