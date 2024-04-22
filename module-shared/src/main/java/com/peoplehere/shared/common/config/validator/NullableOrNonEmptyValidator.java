package com.peoplehere.shared.common.config.validator;

import org.springframework.web.multipart.MultipartFile;

import com.peoplehere.shared.common.annotation.NullableOrNonEmpty;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NullableOrNonEmptyValidator implements ConstraintValidator<NullableOrNonEmpty, MultipartFile> {

	@Override
	public void initialize(NullableOrNonEmpty constraintAnnotation) {
		ConstraintValidator.super.initialize(constraintAnnotation);
	}

	@Override
	public boolean isValid(MultipartFile value, ConstraintValidatorContext context) {
		return value == null || value.getSize() > 0;

	}
}
