package com.peoplehere.api.tour.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.peoplehere.api.common.annotation.CheckAbusing;
import com.peoplehere.api.tour.service.MapService;
import com.peoplehere.shared.common.enums.Region;
import com.peoplehere.shared.tour.data.response.PlaceInfoListResponseDto;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/places")
@Validated
public class PlaceController {

	private final MapService mapService;

	/**
	 * 장소명으로 관련 장소 목록 조회
	 * @param name 장소명
	 * @return 장소 목록
	 */
	@CheckAbusing
	@GetMapping("")
	public ResponseEntity<PlaceInfoListResponseDto> getPlaceInfoList(@RequestParam(name = "name") @NotBlank String name,
		@RequestParam(name = "region", defaultValue = "KR") Region region) {
		try {
			long start = System.currentTimeMillis();
			PlaceInfoListResponseDto responseDto = mapService.getPlaceInfoList(name, region);
			log.info("장소 요청 : [{}, {}]으로 관련 장소 목록 조회 처리 시간: {}ms", name, region, System.currentTimeMillis() - start);
			return ResponseEntity.ok(responseDto);
		} catch (Exception e) {
			log.error("장소 요청 : [{}, {}]으로 관련 장소 목록 조회 중 오류 발생", name, region, e);
			return ResponseEntity.internalServerError().build();
		}
	}

}
