package com.peoplehere.shared.common.enums;

import static com.peoplehere.shared.common.enums.LangCode.*;
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
	CA("Canada", "캐나다", "+1"),
	US("United States", "미국", "+1"),
	AG("Antigua & Barbuda", "앤티가 바부다", "+1"),
	AI("Anguilla", "앵귈라", "+1"),
	AW("Aruba", "아루바", "+297"),
	BS("Bahamas", "바하마", "+1"),
	BB("Barbados", "바베이도스", "+1"),
	BM("Bermuda", "버뮤다", "+1"),
	VG("British Virgin Islands", "영국령 버진아일랜드", "+1"),
	KY("Cayman Islands", "케이맨 제도", "+1"),
	DM("Dominica", "도미니카 연방", "+1"),
	DO("Dominican Republic", "도미니카 공화국", "+1"),
	GD("Grenada", "그레나다", "+1"),
	GP("Guadeloupe", "과들루프", "+590"),
	GU("Guam", "괌", "+1"),
	JM("Jamaica", "자메이카", "+1"),
	MQ("Martinique", "마르티니크", "+596"),
	MS("Montserrat", "몬트세랫", "+1"),
	PR("Puerto Rico", "푸에르토리코", "+1"),
	KN("St. Kitts & Nevis", "세인트키츠 네비스", "+1"),
	LC("St. Lucia", "세인트루시아", "+1"),
	VC("St. Vincent & Grenadines", "세인트빈센트 그레나딘", "+1"),
	TT("Trinidad & Tobago", "트리니다드 토바고", "+1"),
	VI("U.S. Virgin Islands", "미국령 버진아일랜드", "+1"),

	// 유럽
	AT("Austria", "오스트리아", "+43"),
	BE("Belgium", "벨기에", "+32"),
	CZ("Czechia", "체코", "+420"),
	DK("Denmark", "덴마크", "+45"),
	FI("Finland", "핀란드", "+358"),
	FR("France", "프랑스", "+33"),
	DE("Germany", "독일", "+49"),
	IE("Ireland", "아일랜드", "+353"),
	IT("Italy", "이탈리아", "+39"),
	NL("Netherlands", "네덜란드", "+31"),
	NO("Norway", "노르웨이", "+47"),
	PL("Poland", "폴란드", "+48"),
	PT("Portugal", "포르투갈", "+351"),
	SK("Slovakia", "슬로바키아", "+421"),
	ES("Spain", "스페인", "+34"),
	SE("Sweden", "스웨덴", "+46"),
	CH("Switzerland", "스위스", "+41"),
	GB("United Kingdom", "영국", "+44"),
	AX("Åland Islands", "올란드 제도", "+358"),
	AL("Albania", "알바니아", "+355"),
	AD("Andorra", "안도라", "+376"),
	BY("Belarus", "벨라루스", "+375"),
	BA("Bosnia & Herzegovina", "보스니아 헤르체고비나", "+387"),
	BG("Bulgaria", "불가리아", "+359"),
	HR("Croatia", "크로아티아", "+385"),
	CY("Cyprus", "키프로스", "+357"),
	EE("Estonia", "에스토니아", "+372"),
	FO("Faroe Islands", "페로 제도", "+298"),
	GE("Georgia", "조지아", "+995"),
	GI("Gibraltar", "지브롤터", "+350"),
	GR("Greece", "그리스", "+30"),
	GL("Greenland", "그린란드", "+299"),
	GG("Guernsey", "건지", "+44"),
	HU("Hungary", "헝가리", "+36"),
	IS("Iceland", "아이슬란드", "+354"),
	IM("Isle of Man", "맨섬", "+44"),
	JE("Jersey", "저지섬", "+44"),
	LV("Latvia", "라트비아", "+371"),
	LI("Liechtenstein", "리히텐슈타인", "+423"),
	LT("Lithuania", "리투아니아", "+370"),
	LU("Luxembourg", "룩셈부르크", "+352"),
	MT("Malta", "몰타", "+356"),
	MD("Moldova", "몰도바", "+373"),
	MC("Monaco", "모나코", "+377"),
	ME("Montenegro", "몬테네그로", "+382"),
	MK("North Macedonia", "북마케도니아", "+389"),
	RO("Romania", "루마니아", "+40"),
	RS("Serbia", "세르비아", "+381"),
	SI("Slovenia", "슬로베니아", "+386"),
	SJ("Svalbard & Jan Mayen", "스발바르 얀마옌 제도", "+47"),
	TR("Turkey", "튀르키예", "+90"),
	UA("Ukraine", "우크라이나", "+380"),
	VA("Vatican City", "바티칸 시국", "+379"),

	// South America
	AR("Argentina", "아르헨티나", "+54"),
	BR("Brazil", "브라질", "+55"),
	CL("Chile", "칠레", "+56"),
	CO("Colombia", "콜롬비아", "+57"),
	PE("Peru", "페루", "+51"),
	VE("Venezuela", "베네수엘라", "+58"),
	EC("Ecuador", "에콰도르", "+593"),
	GF("French Guiana", "프랑스령 기아나", "+594"),
	GY("Guyana", "가이아나", "+592"),
	PY("Paraguay", "파라과이", "+595"),
	SR("Suriname", "수리남", "+597"),
	UY("Uruguay", "우루과이", "+598"),

	// 아시아 태평양
	AU("Australia", "호주", "+61"),
	JP("Japan", "일본", "+81"),
	KR("South Korea", "대한민국", "+82"),
	RU("Russia", "러시아", "+7"),
	TW("Taiwan", "대만", "+886"),
	CN("China", "중국", "+86"),
	AF("Afghanistan", "아프가니스탄", "+93"),
	BD("Bangladesh", "방글라데시", "+880"),
	BT("Bhutan", "부탄", "+975"),
	BN("Brunei", "브루나이", "+673"),
	KH("Cambodia", "캄보디아", "+855"),
	HK("Hong Kong", "홍콩", "+852"),
	IN("India", "인도", "+91"),
	ID("Indonesia", "인도네시아", "+62"),
	KZ("Kazakhstan", "카자흐스탄", "+7"),
	KG("Kyrgyzstan", "키르기스스탄", "+996"),
	LA("Laos", "라오스", "+856"),
	MO("Macau", "마카오", "+853"),
	MY("Malaysia", "말레이시아", "+60"),
	MV("Maldives", "몰디브", "+960"),
	MN("Mongolia", "몽골", "+976"),
	MM("Myanmar", "미얀마", "+95"),
	NP("Nepal", "네팔", "+977"),
	NZ("New Zealand", "뉴질랜드", "+64"),
	PK("Pakistan", "파키스탄", "+92"),
	PG("Papua New Guinea", "파푸아뉴기니", "+675"),
	PH("Philippines", "필리핀", "+63"),
	SG("Singapore", "싱가포르", "+65"),
	LK("Sri Lanka", "스리랑카", "+94"),
	TH("Thailand", "태국", "+66"),
	TL("Timor-Leste", "동티모르", "+670"),
	VN("Vietnam", "베트남", "+84"),

	// Africa
	DZ("Algeria", "알제리", "+213"),
	AO("Angola", "앙골라", "+244"),
	BJ("Benin", "베냉", "+229"),
	BW("Botswana", "보츠와나", "+267"),
	BF("Burkina Faso", "부르키나파소", "+226"),
	BI("Burundi", "부룬디", "+257"),
	CV("Cape Verde", "카보베르데", "+238"),
	CM("Cameroon", "카메룬", "+237"),
	CF("Central African Republic", "중앙아프리카공화국", "+236"),
	TD("Chad", "차드", "+235"),
	KM("Comoros", "코모로", "+269"),
	CG("Congo", "콩고", "+242"),
	CD("Democratic Republic of the Congo", "콩고민주공화국", "+243"),
	DJ("Djibouti", "지부티", "+253"),
	EG("Egypt", "이집트", "+20"),
	GQ("Equatorial Guinea", "적도기니", "+240"),
	ER("Eritrea", "에리트레아", "+291"),
	SZ("Eswatini", "에스와티니", "+268"),
	ET("Ethiopia", "에티오피아", "+251"),
	GA("Gabon", "가봉", "+241"),
	GM("Gambia", "감비아", "+220"),
	GH("Ghana", "가나", "+233"),
	GN("Guinea", "기니", "+224"),
	GW("Guinea-Bissau", "기니비사우", "+245"),
	CI("Ivory Coast", "코트디부아르", "+225"),
	KE("Kenya", "케냐", "+254"),
	LS("Lesotho", "레소토", "+266"),
	LR("Liberia", "라이베리아", "+231"),
	LY("Libya", "리비아", "+218"),
	MG("Madagascar", "마다가스카르", "+261"),
	MW("Malawi", "말라위", "+265"),
	ML("Mali", "말리", "+223"),
	MR("Mauritania", "모리타니아", "+222"),
	MU("Mauritius", "모리셔스", "+230"),
	YT("Mayotte", "마요트", "+262"),
	MA("Morocco", "모로코", "+212"),
	MZ("Mozambique", "모잠비크", "+258"),
	NA("Namibia", "나미비아", "+264"),
	NE("Niger", "니제르", "+227"),
	NG("Nigeria", "나이지리아", "+234"),
	RE("Réunion", "레위니옹", "+262"),
	RW("Rwanda", "르완다", "+250"),
	ST("São Tomé & Príncipe", "상투메 프린시페", "+239"),
	SN("Senegal", "세네갈", "+221"),
	SC("Seychelles", "세이셸", "+248"),
	SL("Sierra Leone", "시에라리온", "+232"),
	SO("Somalia", "소말리아", "+252"),
	ZA("South Africa", "남아프리카공화국", "+27"),
	SS("South Sudan", "남수단", "+211"),
	SD("Sudan", "수단", "+249"),
	TZ("Tanzania", "탄자니아", "+255"),
	TG("Togo", "토고", "+228"),
	TN("Tunisia", "튀니지", "+216"),
	UG("Uganda", "우간다", "+256"),
	EH("Western Sahara", "서사하라", "+212"),
	ZM("Zambia", "잠비아", "+260"),
	ZW("Zimbabwe", "짐바브웨", "+263");


	private final String englishName;
	private final String koreanName;
	private final String dialCode;

	public static final Region[] VALUES = values();
	public static final List<RegionResponseDto> REGION_INFO_LIST = Stream.of(VALUES)
		.map(region -> RegionResponseDto.builder()
			.countryCode(region.name())
			.englishName(region.getEnglishName())
			.koreanName(region.getKoreanName())
			.dialCode(region.getDialCode())
			.build())
		.collect(toList());

	Region(String englishName, String koreanName, String dialCode) {
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

	/**
	 * 지도 언어 코드 반환
	 * 나라가 한국인 경우 한국어 반환
	 * 그외의 나라의 경우 영어 반환
	 * @return
	 */
	public LangCode getMapLangCode() {
		if (KR.equals(this)) {
			return KOREAN;
		}
		return ENGLISH;
	}

	/**
	 * 나라에 해당하는 언어 코드 반환
	 * 나라가 한국인 경우 원문 반환
	 * 그외의 나라의 경우 영어 반환
	 * @return
	 */
	public LangCode getLangCode() {
		if (KR.equals(this)) {
			return ORIGIN;
		}
		return ENGLISH;
	}

	/**
	 * 국가 dialCode 번호를 포함한 전화번호 반환
	 * @param phoneNumber 전화번호
	 * @return
	 */
	public String getRegionPhoneNumber(String phoneNumber) {
		return this.dialCode + getNormalizedPhoneNumber(phoneNumber);
	}

	/**
	 * 첫 글자가 0이면 0을 제거한 번호를 반환한다. - 지역번호 제거를 위해
	 * @return
	 */
	public static String getNormalizedPhoneNumber(String phoneNumber) {
		return phoneNumber.startsWith("0") ? phoneNumber.substring(1) : phoneNumber;
	}

}
