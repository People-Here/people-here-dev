package com.peoplehere.shared.common.enums;

import static java.util.stream.Collectors.*;

import java.util.List;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.peoplehere.shared.common.data.response.RegionResponseDto;

import lombok.Getter;

@Getter
public enum Region {
	// 북미
	CA("Canada", "캐나다", 1),
	US("United States of America", "미국", 1),

	// 유럽
	AT("Republic of Austria", "오스트리아", 43),
	BE("Kingdom of Belgium", "벨기에", 32),
	CZ("Czech Republic", "체코", 420),
	DK("Kingdom of Denmark", "덴마크", 45),
	FI("Republic of Finland", "핀란드", 358),
	FR("French Republic", "프랑스", 33),
	DE("Federal Republic of Germany", "독일", 49),
	IE("Republic of Ireland", "아일랜드", 353),
	IT("Italian Republic", "이탈리아", 39),
	NL("Kingdom of the Netherlands", "네덜란드", 31),
	NO("Kingdom of Norway", "노르웨이", 47),
	PL("Republic of Poland", "폴란드", 48),
	PT("Portuguese Republic", "포르투갈", 351),
	SK("Slovak Republic", "슬로바키아", 421),
	ES("Kingdom of Spain", "스페인", 34),
	SE("Kingdom of Sweden", "스웨덴", 46),
	CH("Swiss Confederation", "스위스", 41),
	GB("United Kingdom of Great Britain and Northern Ireland", "영국", 44),

	// South America
	AR("Argentine Republic", "아르헨티나", 54),
	BR("Federative Republic of Brazil", "브라질", 55),
	CL("Republic of Chile", "칠레", 56),
	CO("Republic of Colombia", "콜롬비아", 57),
	PE("Republic of Peru", "페루", 51),
	VE("Bolivarian Republic of Venezuela", "베네수엘라", 58),

	// 아시아 태평양
	AU("Commonwealth of Australia", "호주", 61),
	JP("Japan", "일본", 81),
	KR("Republic of Korea", "대한민국", 82),
	RU("Russian Federation", "러시아", 7),
	TW("Taiwan", "대만", 886),
	CN("People's Republic of China", "중국", 86);

	private final String englishName;
	private final String koreanName;
	private final int dialCode;

	public static final Region[] VALUES = values();
	public static final List<RegionResponseDto> REGION_INFO_LIST = Stream.of(VALUES)
		.map(region -> RegionResponseDto.builder()
			.countryCode(region.name())
			.englishName(region.getEnglishName())
			.koreanName(region.getKoreanName())
			.dialCode(region.getDialCode())
			.build())
		.collect(toList());

	Region(String englishName, String koreanName, int dialCode) {
		this.englishName = englishName;
		this.koreanName = koreanName;
		this.dialCode = dialCode;
	}

	@JsonCreator
	public static Region findByCode(String code) {
		for (Region region : VALUES) {
			if (region.name().equalsIgnoreCase(code)) {
				return region;
			}
		}
		throw new IllegalArgumentException("Invalid country code: " + code);
	}

	@JsonValue
	public String getCountryCode() {
		return name();
	}

	public String getMapLanguageCode() {
		if (this == KR) {
			return "ko-KR";
		}
		return "en-US";
	}

}
