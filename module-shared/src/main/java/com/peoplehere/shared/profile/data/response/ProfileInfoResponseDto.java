package com.peoplehere.shared.profile.data.response;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

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
	private String email;
	private String firstName;
	private String lastName;
	private String phoneNumber;
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
	private ConsentInfo consentInfo;

	public String getProfileImageUrl() {
		if (StringUtils.hasText(optimizedProfileImageUrl)) {
			return optimizedProfileImageUrl;
		}
		return profileImageUrl;
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ConsentInfo {
		private boolean privacyConsent;
		private boolean marketingConsent;
		private boolean messageAlarmConsent;
		private boolean meetingAlarmConsent;
	}

	/**
	 * 사용자의 생일 정보를 필터링합니다. - 본인이거나 생일 정보를 공개한 경우에만 반환합니다.
	 * @param userId 사용자 id
	 * @return
	 */
	public ProfileInfoResponseDto filterBirthDate(String userId) {
		if (Objects.requireNonNull(this.email).equals(userId)) {
			return this;
		}
		if (showBirth) {
			return this;
		}
		this.birthDate = null;
		return this;
	}
}
