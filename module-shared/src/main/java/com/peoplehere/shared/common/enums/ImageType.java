package com.peoplehere.shared.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 이미지 업로드 요청 타입
 */
@Getter
@AllArgsConstructor
public enum ImageType {
	/**
	 * 프로필 이미지 업로드 타입
	 */
	PROFILE_IMAGE("profile"),

	/**
	 * 투어 이미지 업로드 타입
	 */
	TOUR_IMAGE("tour");

	private final String filePathPrefix;
}
