package com.peoplehere.api.tour.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.peoplehere.api.common.annotation.CheckAbusing;
import com.peoplehere.api.tour.service.TourService;
import com.peoplehere.shared.tour.data.request.TourListRequestDto;
import com.peoplehere.shared.tour.data.response.TourListResponseDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tours")
public class TourController {

	private final TourService tourService;

	/**
	 * 장소 목록 조회
	 * @return 장소 목록
	 */
	@CheckAbusing
	@GetMapping("")
	public ResponseEntity<TourListResponseDto> getTourList() {
		try {
			return ResponseEntity.ok(tourService.findTourList());
		} catch (Exception e) {
			log.error("장소 목록 조회 중 오류 발생", e);
			return ResponseEntity.internalServerError().build();
		}
	}

	/**
	 * 장소 목록 키워드를 통한 검색
	 * @param requestDto 검색 키워드
	 * @param bindingResult 바인딩 결과
	 * @return 장소 목록
	 * @throws BindException
	 */
	@CheckAbusing
	@PostMapping("/search")
	public ResponseEntity<TourListResponseDto> getTourList(@Validated @RequestBody TourListRequestDto requestDto,
		BindingResult bindingResult) throws BindException {
		if (bindingResult.hasErrors()) {
			throw new BindException(bindingResult);
		}
		try {
			return ResponseEntity.ok(tourService.findTourListByKeyword(requestDto.keyword()));
		} catch (Exception e) {
			log.error("키워드: {} 를 통한 장소 목록 검색 중 오류 발생", requestDto, e);
			return ResponseEntity.internalServerError().build();
		}
	}

}
