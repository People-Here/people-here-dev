package com.peoplehere.shared.common.entity;

import org.hibernate.annotations.Comment;

import com.peoplehere.shared.common.enums.LangCode;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@Table(name = "translate_text")
@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class TranslateText extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(updatable = false, insertable = false)
	long id;

	String src;

	String dest;

	@Column(name = "target_lang_code")
	@Enumerated(EnumType.STRING)
	@Comment("대상 언어")
	private LangCode targetLangCode;
}
