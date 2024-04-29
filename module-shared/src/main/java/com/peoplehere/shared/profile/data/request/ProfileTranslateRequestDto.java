package com.peoplehere.shared.profile.data.request;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.util.StringUtils;

import com.peoplehere.shared.common.entity.Account;
import com.peoplehere.shared.common.enums.LangCode;
import com.peoplehere.shared.profile.entity.AccountInfo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileTranslateRequestDto {
	private long accountId;
	private List<LangCode> languages;
	private String introduce;
	private String favorite;
	private String hobby;
	private String pet;
	private String school;
	private String job;

	public static ProfileTranslateRequestDto toTranslateRequestDto(Account account, AccountInfo accountInfo) {
		return ProfileTranslateRequestDto.builder()
			.accountId(account.getId())
			.languages(account.getLangCodeList())
			.introduce(accountInfo.getIntroduce())
			.favorite(accountInfo.getFavorite())
			.hobby(accountInfo.getHobby())
			.pet(accountInfo.getPet())
			.school(accountInfo.getSchool())
			.job(accountInfo.getJob())
			.build();
	}

	public static List<String> getInfoList(ProfileTranslateRequestDto dto) {
		return Stream.of(dto.getIntroduce(), dto.getFavorite(), dto.getHobby(), dto.getPet(), dto.getSchool(),
				dto.getJob())
			.filter(StringUtils::hasText)
			.collect(Collectors.toList());
	}
}
