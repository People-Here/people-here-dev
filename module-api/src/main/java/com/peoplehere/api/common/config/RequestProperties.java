package com.peoplehere.api.common.config;

public class RequestProperties {

	private static final int EMAIL_VERIFICATION_COUNT_LIMIT = 5; // 이메일 인증 번호 전송 총 제한 횟수
	private static final int EMAIL_VERIFICATION_TIME_LIMIT_DAY = 1; // 이메일 인증 번호 전송 제한 시간
	private static final int EMAIL_VERIFY_COUNT_LIMIT = 10; // 이메일 인증 번호 검증 총 제한 횟수
	private static final int EMAIL_VERIFY_TIME_LIMIT_DAY = 1; // 이메일 인증 번호 전송 제한 시간
	private static final int ABUSING_COUNT_LIMIT = 3; // 어뷰징 요청 제한 횟수
	private static final int ABUSING_TIME_LIMIT_SECONDS = 1; // 어뷰징 요청 제한 시간

	public static int getEmailVerificationCountLimit() {
		return EMAIL_VERIFICATION_COUNT_LIMIT;
	}

	public static int getEmailVerificationTimeLimitDay() {
		return EMAIL_VERIFICATION_TIME_LIMIT_DAY;
	}

	public static int getEmailVerifyCountLimit() {
		return EMAIL_VERIFY_COUNT_LIMIT;
	}

	public static int getEmailVerifyTimeLimitDay() {
		return EMAIL_VERIFY_TIME_LIMIT_DAY;
	}

	public static int getAbusingCountLimit() {
		return ABUSING_COUNT_LIMIT;
	}

	public static int getAbusingTimeLimitSeconds() {
		return ABUSING_TIME_LIMIT_SECONDS;
	}

}
