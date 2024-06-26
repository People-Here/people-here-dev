package com.peoplehere.api.tour.controller;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.peoplehere.api.common.annotation.CheckAbusing;
import com.peoplehere.api.tour.service.LocationService;
import com.peoplehere.shared.common.enums.PageType;
import com.peoplehere.shared.common.enums.Region;
import com.peoplehere.shared.tour.data.request.PlaceInfoRequestDto;
import com.peoplehere.shared.tour.data.response.PlaceInfoHistoryResponseDto;
import com.peoplehere.shared.tour.data.response.PlaceInfoListResponseDto;
import com.peoplehere.shared.tour.data.response.PlaceInfoResponseDto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/places")
@Validated
public class PlaceController {

	private final LocationService locationService;

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
			PlaceInfoListResponseDto responseDto = locationService.getPlaceInfoList(name, region);
			log.info("장소 요청 : [{}, {}]으로 관련 장소 목록 조회 처리 시간: {}ms", name, region, System.currentTimeMillis() - start);
			return ResponseEntity.ok(responseDto);
		} catch (Exception e) {
			log.error("장소 요청 : [{}, {}]으로 관련 장소 목록 조회 중 오류 발생", name, region, e);
			return ResponseEntity.internalServerError().build();
		}
	}

	/**
	 * 장소 상세 정보 추가
	 * @param requestDto 장소 상세 정보
	 * @param bindingResult 바인딩 결과
	 * @return
	 */
	@CheckAbusing
	@PostMapping("")
	public ResponseEntity<PlaceInfoResponseDto> addPlaceInfo(
		@RequestBody @Validated PlaceInfoRequestDto requestDto, @Nullable Principal principal,
		BindingResult bindingResult) throws BindException {
		if (bindingResult.hasErrors()) {
			throw new BindException(bindingResult);
		}
		try {
			String userId = principal == null ? null : principal.getName();
			return ResponseEntity.ok(locationService.addPlaceInfo(userId, requestDto));
		} catch (Exception e) {
			log.error("장소 id: {}, region: {} 상세 정보 추가 중 오류 발생", requestDto.placeId(), requestDto.region(), e);
			return ResponseEntity.internalServerError().build();
		}
	}

	/**
	 * 유저의 장소 검색 기록 조회
	 * @param principal
	 * @return 장소 정보 목록
	 */
	@CheckAbusing
	@GetMapping("/search-history/{type}")
	public ResponseEntity<PlaceInfoHistoryResponseDto> getSearchPlaceHistory(@PathVariable PageType type,
		Principal principal) {
		try {
			return ResponseEntity.ok(locationService.getSearchHistory(type, principal.getName()));
		} catch (Exception e) {
			log.error("장소 검색 기록 조회 중 오류 발생", e);
			return ResponseEntity.internalServerError().build();
		}
	}
}
