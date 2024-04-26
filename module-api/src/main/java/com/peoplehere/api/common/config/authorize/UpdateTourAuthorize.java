package com.peoplehere.api.common.config.authorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.security.access.prepost.PreAuthorize;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasAuthority(T(com.peoplehere.shared.common.enums.AccountAuthority).CREATE_TOUR.name())")
public @interface UpdateTourAuthorize {
}
