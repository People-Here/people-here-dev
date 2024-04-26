package com.peoplehere.shared.tour.data.request;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.peoplehere.shared.common.annotation.NullableOrNonEmptyList;
import com.peoplehere.shared.common.enums.LangCode;
import com.peoplehere.shared.tour.entity.Tour;
import com.peoplehere.shared.tour.entity.TourInfo;

import jakarta.validation.constraints.NotBlank;

public record TourCreateRequestDto(@NotBlank String title, String description, @NotBlank String placeId, String theme,
									@NullableOrNonEmptyList List<MultipartFile> images) {

	public static Tour toTourEntity(TourCreateRequestDto requestDto, long accountId, boolean isDefaultImage) {
		return Tour.builder()
			.accountId(accountId)
			.placeId(requestDto.placeId())
			.theme(requestDto.theme())
			.isDefaultImage(isDefaultImage)
			.build();
	}

	/**
	 * 투어 정보의 경우 기본적으로 원문 저장 후 번역을 진행하므로 langCode는 ORIGIN으로 설정
	 * @param requestDto
	 * @param tourId
	 * @return TourInfo
	 */
	public static TourInfo toTourInfoEntity(TourCreateRequestDto requestDto, long tourId) {
		return TourInfo.builder()
			.tourId(tourId)
			.langCode(LangCode.ORIGIN)
			.title(requestDto.title())
			.description(requestDto.description())
			.build();
	}
}
