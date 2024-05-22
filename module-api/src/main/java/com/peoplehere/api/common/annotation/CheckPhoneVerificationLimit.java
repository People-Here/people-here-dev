package com.peoplehere.api.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * 전화번호 인증 번호 전송에 대한 제한을 체크하는 어노테이션
 */
@Target({ElementType.METHOD})
public @interface CheckPhoneVerificationLimit {
}
