package com.peoplehere.shared.profile.entity;

import org.hibernate.annotations.Comment;

import com.peoplehere.shared.common.entity.BaseTimeEntity;
import com.peoplehere.shared.common.enums.LangCode;
import com.peoplehere.shared.profile.data.request.ProfileInfoRequestDto;
import com.peoplehere.shared.profile.data.response.ProfileTranslateResponseDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@Table(name = "account_info")
@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class AccountInfo extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@NotNull
	@Column(name = "account_id", nullable = false)
	private long accountId;

	@Column(name = "language")
	@Enumerated(EnumType.STRING)
	@Comment("언어")
	private LangCode langCode;

	@Column
	@Comment("자기소개")
	private String introduce;

	@Column
	@Comment("좋아하는 것")
	private String favorite;

	@Column
	@Comment("취미")
	private String hobby;

	@Column
	@Comment("반려동물")
	private String pet;

	@Column
	@Comment("학교")
	private String school;

	@Column
	@Comment("직업")
	private String job;

	public void updateOriginalInfo(ProfileInfoRequestDto requestDto) {
		this.introduce = requestDto.introduce();
		this.favorite = requestDto.favorite();
		this.hobby = requestDto.hobby();
		this.pet = requestDto.pet();
		this.school = requestDto.school();
		this.job = requestDto.job();
	}

	public void updateInfo(ProfileTranslateResponseDto responseDto) {
		this.introduce = responseDto.introduce();
		this.favorite = responseDto.favorite();
		this.hobby = responseDto.hobby();
		this.pet = responseDto.pet();
		this.school = responseDto.school();
		this.job = responseDto.job();
		this.langCode = responseDto.langCode();
	}
}
