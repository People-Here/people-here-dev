package com.peoplehere.shared.profile.data.request;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.peoplehere.shared.common.annotation.NullableOrNonEmpty;
import com.peoplehere.shared.common.enums.LangCode;
import com.peoplehere.shared.common.enums.Region;

import jakarta.validation.constraints.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ProfileInfoRequestDto(
	@NotNull(message = "id는 필수값입니다.") long id,
	@NullableOrNonEmpty MultipartFile profileImage,
	String introduce,
	@NotNull Region region,
	List<LangCode> languages,
	String favorite,
	String hobby,
	String pet,
	@DateTimeFormat(pattern = "yyyyMMdd")
	LocalDate birthDate,
	String placeId,
	String job,
	String school,
	boolean showBirth
) {
}

