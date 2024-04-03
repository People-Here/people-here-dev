package com.peoplehere.api.common.util;

import java.security.SecureRandom;

import lombok.experimental.UtilityClass;

@UtilityClass
public class MessageUtils {
	private static final SecureRandom random = new SecureRandom();

	/**
	 * 메일 인증 번호를 위한 6자리 랜덤 숫자 생성
	 * @return
	 */
	public static String generateRandomEmailVerifyCode() {
		int sixDigitNumber = 100_000 + random.nextInt(900_000);
		return String.valueOf(sixDigitNumber);
	}
}
