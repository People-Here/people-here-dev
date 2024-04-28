package com.peoplehere.shared.common.enums;

import static java.util.stream.Collectors.*;

import java.util.List;
import java.util.stream.Stream;

import com.peoplehere.shared.common.data.response.LanguageResponseDto;

import lombok.Getter;

@Getter
public enum LangCode {
	ORIGIN("Origin", "원문", "origin"),
	ARABIC("Arabic", "아랍어", "ar"),
	BENGALI("Bengali", "벵골어", "bn"),
	BULGARIAN("Bulgarian", "불가리아어", "bg"),
	CHINESE("Chinese", "중국어", "zh"),
	DUTCH("Dutch", "네덜란드어", "nl"),
	ENGLISH("English", "영어", "en"),
	FRENCH("French", "프랑스어", "fr"),
	GERMAN("German", "독일어", "de"),
	GREEK("Greek", "그리스어", "el"),
	GUJARATI("Gujarati", "구자라트어", "gu"),
	HINDI("Hindi", "힌디어", "hi"),
	ITALIAN("Italian", "이탈리아어", "it"),
	JAPANESE("Japanese", "일본어", "ja"),
	JAVANESE("Javanese", "자바어", "jv"),
	KANNADA("Kannada", "칸나다어", "kn"),
	KOREAN("Korean", "한국어", "ko"),
	MALAYALAM("Malayalam", "말라얄람어", "ml"),
	MARATHI("Marathi", "마라티어", "mr"),
	NEPALI("Nepali", "네팔어", "ne"),
	PERSIAN("Persian", "페르시아어", "fa"),
	POLISH("Polish", "폴란드어", "pl"),
	PORTUGUESE("Portuguese", "포르투갈어", "pt"),
	PUNJABI("Punjabi", "펀자브어", "pa"),
	ROMANIAN("Romanian", "루마니아어", "ro"),
	RUSSIAN("Russian", "러시아어", "ru"),
	SINHALA("Sinhala", "신할라어", "si"),
	SOMALI("Somali", "소말리어", "so"),
	SPANISH("Spanish", "스페인어", "es"),
	SWAHILI("Swahili", "스와힐리어", "sw"),
	TAGALOG("Tagalog", "타갈로그어", "tl"),
	TAMIL("Tamil", "타밀어", "ta"),
	TELUGU("Telugu", "텔루구어", "te"),
	THAI("Thai", "태국어", "th"),
	TURKISH("Turkish", "터키어", "tr"),
	UKRAINIAN("Ukrainian", "우크라이나어", "uk"),
	URDU("Urdu", "우르두어", "ur"),
	VIETNAMESE("Vietnamese", "베트남어", "vi");

	private final String englishName;
	private final String koreanName;
	private final String code;

	public static final LangCode[] VALUES = values();
	public static final List<LanguageResponseDto> LANGUAGE_INFO_LIST = Stream.of(VALUES)
		.map(langCode -> LanguageResponseDto.builder()
			.langCode(langCode.name())
			.englishName(langCode.getEnglishName())
			.koreanName(langCode.getKoreanName())
			.code(langCode.getCode())
			.build())
		.collect(toList());

	LangCode(String englishName, String koreanName, String code) {
		this.englishName = englishName;
		this.koreanName = koreanName;
		this.code = code;
	}

}
