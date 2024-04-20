package com.peoplehere.shared.common.enums;

import static java.util.stream.Collectors.*;

import java.util.List;
import java.util.stream.Stream;

import com.peoplehere.shared.common.data.response.LanguageResponseDto;

import lombok.Getter;

@Getter
public enum LangCode {
	ARABIC("Arabic", "아랍어"),
	BENGALI("Bengali", "벵골어"),
	BULGARIAN("Bulgarian", "불가리아어"),
	CHINESE("Chinese", "중국어"),
	DUTCH("Dutch", "네덜란드어"),
	ENGLISH("English", "영어"),
	FRENCH("French", "프랑스어"),
	GERMAN("German", "독일어"),
	GREEK("Greek", "그리스어"),
	GUJARATI("Gujarati", "구자라트어"),
	HINDI("Hindi", "힌디어"),
	ITALIAN("Italian", "이탈리아어"),
	JAPANESE("Japanese", "일본어"),
	JAVANESE("Javanese", "자바어"),
	KANNADA("Kannada", "칸나다어"),
	KOREAN("Korean", "한국어"),
	MALAYALAM("Malayalam", "말라얄람어"),
	MARATHI("Marathi", "마라티어"),
	NEPALI("Nepali", "네팔어"),
	PERSIAN("Persian", "페르시아어"),
	POLISH("Polish", "폴란드어"),
	PORTUGUESE("Portuguese", "포르투갈어"),
	PUNJABI("Punjabi", "펀자브어"),
	ROMANIAN("Romanian", "루마니아어"),
	RUSSIAN("Russian", "러시아어"),
	SINHALA("Sinhala", "신할라어"),
	SOMALI("Somali", "소말리어"),
	SPANISH("Spanish", "스페인어"),
	SWAHILI("Swahili", "스와힐리어"),
	TAGALOG("Tagalog", "타갈로그어"),
	TAMIL("Tamil", "타밀어"),
	TELUGU("Telugu", "텔루구어"),
	THAI("Thai", "태국어"),
	TURKISH("Turkish", "터키어"),
	UKRAINIAN("Ukrainian", "우크라이나어"),
	URDU("Urdu", "우르두어"),
	VIETNAMESE("Vietnamese", "베트남어");

	private final String englishName;
	private final String koreanName;

	public static final LangCode[] VALUES = values();
	public static final List<LanguageResponseDto> LANGUAGE_INFO_LIST = Stream.of(VALUES)
		.map(langCode -> LanguageResponseDto.builder()
			.langCode(langCode.name())
			.englishName(langCode.getEnglishName())
			.koreanName(langCode.getKoreanName())
			.build())
		.collect(toList());

	LangCode(String englishName, String koreanName) {
		this.englishName = englishName;
		this.koreanName = koreanName;
	}

	public static LangCode fromKoreanName(String koreanName) {
		for (LangCode lc : VALUES) {
			if (lc.getKoreanName().equalsIgnoreCase(koreanName)) {
				return lc;
			}
		}
		return null;
	}

}
