package com.peoplehere.shared.common.entity;

import org.hibernate.annotations.Comment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Table(name = "consent")
@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Consent extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@NotNull
	@Column(name = "account_id", nullable = false)
	private long accountId;

	@Comment("개인정보 이용 동의")
	@Column(name = "privacy_consent")
	private Boolean privacyConsent;

	@Comment("마케팅 정보 수신 동의")
	@Column(name = "marketing_consent")
	private Boolean marketingConsent;

	@Comment("알람 수신 동의")
	@Column(name = "alarm_consent")
	private Boolean alarmConsent;

}
