package com.peoplehere.shared.common.data.request;

import static com.peoplehere.shared.common.util.PatternUtils.*;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.peoplehere.shared.common.entity.Account;
import com.peoplehere.shared.common.entity.Consent;
import com.peoplehere.shared.common.enums.AccountRole;
import com.peoplehere.shared.common.enums.Gender;
import com.peoplehere.shared.common.enums.Region;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class SignUpRequestDto {

	@NotBlank
	private String firstName;

	@NotBlank
	private String lastName;

	@JsonFormat(pattern = "yyyyMMdd")
	private LocalDate birthDate;

	private Gender gender;

	@NotBlank
	@Email(message = "이메일 형식을 지켜주세요.")
	private String email;

	@Pattern(
		regexp = PASSWORD_REGEX,
		message = "패스워드 형식을 지켜주세요.")
	private String password;

	private Region region;

	private String phoneNumber;

	private boolean marketingConsent;

	private boolean privacyConsent;

	public static Account toClientAccount(SignUpRequestDto requestDto, String encodedPassword) {
		return Account.builder()
			.firstName(requestDto.getFirstName())
			.lastName(requestDto.getLastName())
			.userId(requestDto.getEmail())
			.email(requestDto.getEmail())
			.password(encodedPassword)
			.phoneNumber(requestDto.getPhoneNumber())
			.region(requestDto.getRegion())
			.birthDate(requestDto.getBirthDate())
			.gender(requestDto.getGender())
			.role(AccountRole.USER)
			.active(true)
			.build();
	}

	public static Consent toConsent(SignUpRequestDto requestDto, Account account) {
		return Consent.builder()
			.accountId(account.getId())
			.privacyConsent(requestDto.isPrivacyConsent())
			.marketingConsent(requestDto.isMarketingConsent())
			.build();
	}
}
