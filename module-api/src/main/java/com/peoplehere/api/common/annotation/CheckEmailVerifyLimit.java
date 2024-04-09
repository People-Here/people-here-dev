package com.peoplehere.api.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * 이메일 인증 번호 검증에 대한 제한을 체크하는 어노테이션
 */
@Target({ElementType.METHOD})
public @interface CheckEmailVerifyLimit {
}
