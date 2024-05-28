package com.peoplehere.shared.common.service;

import com.peoplehere.shared.common.enums.LangCode;
import com.peoplehere.shared.tour.data.request.PlaceDetailInfoRequestDto;
import com.peoplehere.shared.tour.data.response.PlaceDetailResponseDto;
import com.peoplehere.shared.tour.data.response.PlaceInfoListResponseDto;

/**
 * 장소 관련 서비스
 */
public interface MapComponent {

	/**
	 * 장소명으로 관련 장소 정보 목록 조회 api 호출
	 * @param name 장소명
	 * @param langCode 언어 코드
	 * @return
	 */
	PlaceInfoListResponseDto fetchPlaceInfoList(String name, LangCode langCode);

	/**
	 * 장소 상세 정보 api 호출을 통해 가져오기
	 * @param requestDto 장소id, 언어코드
	 * @return
	 */
	PlaceDetailResponseDto fetchPlaceDetailInfo(PlaceDetailInfoRequestDto requestDto);

}
