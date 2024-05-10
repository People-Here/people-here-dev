package com.peoplehere.shared.profile.data;

import java.util.List;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.peoplehere.shared.common.enums.LangCode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProfileInfoDto {

	@JsonProperty("id")
	private long accountId;
	private String firstName;
	private String lastName;
	private String introduce;
	private String profileImageUrl;
	@JsonIgnore
	private String optimizedProfileImageUrl;
	private boolean directMessageStatus;
	private List<LangCode> languages;

	public ProfileInfoDto(long accountId, String firstName, String lastName, String introduce, String profileImageUrl,
		String optimizedProfileImageUrl, boolean directMessageStatus) {
		this.accountId = accountId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.introduce = introduce;
		this.profileImageUrl = profileImageUrl;
		this.optimizedProfileImageUrl = optimizedProfileImageUrl;
		this.directMessageStatus = directMessageStatus;
	}

	public String getProfileImageUrl() {
		if (StringUtils.hasText(optimizedProfileImageUrl)) {
			return optimizedProfileImageUrl;
		}
		return profileImageUrl;
	}

}
