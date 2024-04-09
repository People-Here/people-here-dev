package com.peoplehere.shared.common.util;

import java.util.regex.Pattern;

import lombok.experimental.UtilityClass;

@UtilityClass
public class PatternUtils {

	public static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[\\d!@#~^*&%]).{8,}$";
	public static final Pattern EMAIL_PATTERN = Pattern.compile(
		"^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$"
	);
}
