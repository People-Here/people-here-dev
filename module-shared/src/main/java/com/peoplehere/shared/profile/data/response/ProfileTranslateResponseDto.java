package com.peoplehere.shared.profile.data.response;

import com.peoplehere.shared.common.enums.LangCode;

import lombok.Builder;

@Builder
public record ProfileTranslateResponseDto(String introduce, String favorite, String hobby, String pet, String school,
											String job, LangCode langCode) {
}
