package com.peoplehere.api.tour.controller;

import java.security.Principal;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.peoplehere.api.common.annotation.CheckAbusing;
import com.peoplehere.api.common.config.authorize.UpdateTourAuthorize;
import com.peoplehere.api.tour.service.TourService;
import com.peoplehere.shared.common.enums.LangCode;
import com.peoplehere.shared.tour.data.request.TourCreateRequestDto;
import com.peoplehere.shared.tour.data.request.TourIdRequestDto;
import com.peoplehere.shared.tour.data.request.TourListRequestDto;
import com.peoplehere.shared.tour.data.request.TourUpdateRequestDto;
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
	 * @param langCode 언어 코드
	 * @return 장소 목록
	 */
	@CheckAbusing
	@GetMapping("/{langCode}")
	public ResponseEntity<TourListResponseDto> getTourList(@PathVariable LangCode langCode) {
		try {
			return ResponseEntity.ok(tourService.findTourList(langCode));
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
	public ResponseEntity<TourListResponseDto> getTourList(@RequestBody @Validated TourListRequestDto requestDto,
		BindingResult bindingResult) throws BindException {
		if (bindingResult.hasErrors()) {
			throw new BindException(bindingResult);
		}
		try {
			return ResponseEntity.ok(tourService.findTourListByKeyword(requestDto));
		} catch (Exception e) {
			log.error("키워드: {} 를 통한 장소 목록 검색 중 오류 발생", requestDto, e);
			return ResponseEntity.internalServerError().build();
		}
	}

	@CheckAbusing
	@UpdateTourAuthorize
	@PostMapping(value = "", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
	public ResponseEntity<Void> addTour(@ModelAttribute @Validated TourCreateRequestDto requestDto, Principal principal,
		BindingResult bindingResult) throws BindException {
		if (bindingResult.hasErrors()) {
			throw new BindException(bindingResult);
		}

		try {
			log.info("[@{}] 장소 추가 요청", principal.getName());
			long start = System.currentTimeMillis();
			tourService.createTour(principal.getName(), requestDto);
			log.info("[@{}] 장소 추가 완료 {}ms", principal.getName(), System.currentTimeMillis() - start);

			return ResponseEntity.ok().build();
		} catch (Exception e) {
			log.error("장소 추가 중 오류 발생", e);
			return ResponseEntity.internalServerError().build();
		}
	}

	/**
	 * 장소 수정
	 * @param requestDto
	 * @param principal
	 * @param bindingResult
	 * @return
	 * @throws BindException
	 */
	@CheckAbusing
	@UpdateTourAuthorize
	@PutMapping(value = "", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
	public ResponseEntity<Void> modifyTour(@ModelAttribute @Validated TourUpdateRequestDto requestDto,
		Principal principal,
		BindingResult bindingResult) throws BindException {
		if (bindingResult.hasErrors()) {
			throw new BindException(bindingResult);
		}

		try {
			log.info("[@{}] 장소 정보 수정 요청", principal.getName());
			long start = System.currentTimeMillis();
			tourService.updateTour(principal.getName(), requestDto);
			log.info("[@{}] 장소 정보 수정 완료 {}ms", principal.getName(), System.currentTimeMillis() - start);

			return ResponseEntity.ok().build();
		} catch (Exception e) {
			log.error("장소 정보 수정 중 오류 발생", e);
			return ResponseEntity.internalServerError().build();
		}
	}

	/**
	 * 장소 삭제
	 * @param requestDto
	 * @return
	 */
	@UpdateTourAuthorize
	@DeleteMapping("")
	public ResponseEntity<Void> deleteTour(@RequestBody @Validated TourIdRequestDto requestDto,
		BindingResult bindingResult) throws BindException {

		if (bindingResult.hasErrors()) {
			throw new BindException(bindingResult);
		}

		try {
			tourService.deleteTour(requestDto.id());
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			return ResponseEntity.internalServerError().build();
		}
	}

}
