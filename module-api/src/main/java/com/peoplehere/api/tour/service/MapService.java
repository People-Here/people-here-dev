package com.peoplehere.api.tour.service;

import com.peoplehere.shared.common.enums.Region;
import com.peoplehere.shared.tour.data.request.PlaceInfoRequestDto;
import com.peoplehere.shared.tour.data.response.PlaceInfoListResponseDto;
import com.peoplehere.shared.tour.data.response.PlaceInfoResponseDto;

/**
 * 장소 관련 서비스
 */
public interface MapService {

	/**
	 * 장소명으로 관련 장소 정보 목록 조회
	 * @param name 장소명
	 * @param region 국가 코드
	 * @return
	 */
	PlaceInfoListResponseDto getPlaceInfoList(String name, Region region);

	/**
	 * 장소 상세 정보 가져오기
	 * @param userId 사용자id
	 * @param requestDto 장소id, 언어코드
	 */
	PlaceInfoResponseDto getPlaceDetailInfo(String userId, PlaceInfoRequestDto requestDto);

}
