package com.peoplehere.api.common.config.aspect;

import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

/**
 * 요청 전 제한 값들(인증 번호 발급 제한, 유/무료 사용자의 총 요청 제한)을 체크하고 필요한 값을 설정하는 Aspect
 */
@Component
@Aspect
@RequiredArgsConstructor
public class RequestLimitAspect {
}
