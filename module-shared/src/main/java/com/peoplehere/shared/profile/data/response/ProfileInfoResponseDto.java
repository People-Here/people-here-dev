package com.peoplehere.shared.profile.data.response;

import java.time.LocalDate;
import java.util.List;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.peoplehere.shared.common.enums.LangCode;
import com.peoplehere.shared.common.enums.Region;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProfileInfoResponseDto {
	private long id;
	private String firstName;
	private String lastName;
	private String profileImageUrl;
	@JsonIgnore
	private String optimizedProfileImageUrl;
	private Region region;
	private List<LangCode> languages;
	private String favorite;
	private String hobby;
	private String pet;
	private String introduce;
	private String job;
	private String school;
	private String address;
	@JsonFormat(pattern = "yyyyMMdd")
	private LocalDate birthDate;
	private LangCode langCode;
	private boolean showBirth;

	public String getProfileImageUrl() {
		if (StringUtils.hasText(optimizedProfileImageUrl)) {
			return optimizedProfileImageUrl;
		}
		return profileImageUrl;
	}

	public LocalDate getBirthDate() {
		if (showBirth) {
			return this.birthDate;
		}
		return null;
	}
}
