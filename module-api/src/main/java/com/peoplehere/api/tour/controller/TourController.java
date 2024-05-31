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
import com.peoplehere.api.common.config.authorize.CreateMessageAuthorize;
import com.peoplehere.api.common.config.authorize.UpdateTourAuthorize;
import com.peoplehere.api.common.service.MessageService;
import com.peoplehere.api.tour.service.TourService;
import com.peoplehere.shared.common.enums.LangCode;
import com.peoplehere.shared.common.enums.Region;
import com.peoplehere.shared.tour.data.request.TourCreateRequestDto;
import com.peoplehere.shared.tour.data.request.TourIdRequestDto;
import com.peoplehere.shared.tour.data.request.TourListRequestDto;
import com.peoplehere.shared.tour.data.request.TourMessageCreateRequestDto;
import com.peoplehere.shared.tour.data.request.TourMessageStatusRequestDto;
import com.peoplehere.shared.tour.data.request.TourMessageTranslateRequestDto;
import com.peoplehere.shared.tour.data.request.TourUpdateRequestDto;
import com.peoplehere.shared.tour.data.response.TourListResponseDto;
import com.peoplehere.shared.tour.data.response.TourMessageListResponseDto;
import com.peoplehere.shared.tour.data.response.TourMessageTranslateResponseDto;
import com.peoplehere.shared.tour.data.response.TourResponseDto;
import com.peoplehere.shared.tour.data.response.TourRoomListResponseDto;

import jakarta.annotation.Nullable;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tours")
public class TourController {

	private final TourService tourService;
	private final MessageService messageService;

	/**
	 * 장소 목록 조회
	 * @param region 지역
	 * @param langCode 언어 코드
	 * @return 장소 목록
	 */
	@CheckAbusing
	@GetMapping("/{region}/{langCode}")
	public ResponseEntity<TourListResponseDto> getTourList(@PathVariable Region region, @PathVariable LangCode langCode,
		@Nullable Principal principal) {
		try {
			String userId = principal == null ? null : principal.getName();
			return ResponseEntity.ok(tourService.findTourList(userId, region, langCode));
		} catch (Exception e) {
			log.error("장소 목록 조회 중 오류 발생", e);
			return ResponseEntity.internalServerError().build();
		}
	}

	/**
	 * 장소 목록 키워드를 통한 검색
	 * @param requestDto 검색 키워드
	 * @param principal 사용자 정보
	 * @param bindingResult 바인딩 결과
	 * @return
	 * @throws BindException
	 */
	@CheckAbusing
	@PostMapping("/search")
	public ResponseEntity<TourListResponseDto> getTourList(@RequestBody @Validated TourListRequestDto requestDto,
		@Nullable Principal principal,
		BindingResult bindingResult) throws BindException {
		if (bindingResult.hasErrors()) {
			throw new BindException(bindingResult);
		}
		try {
			String userId = principal == null ? null : principal.getName();
			return ResponseEntity.ok(tourService.findTourListByKeyword(userId, requestDto));
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

	/**
	 * 장소 상세 조회
	 * @param tourId 장소 id
	 * @param region 지역
	 * @param langCode 언어 코드
	 * @return
	 */
	@CheckAbusing
	@GetMapping("/{tourId}/{region}/{langCode}")
	public ResponseEntity<TourResponseDto> getTourDetail(@PathVariable long tourId, @PathVariable Region region,
		@PathVariable LangCode langCode, @Nullable Principal principal) {
		try {
			String userId = principal == null ? null : principal.getName();
			return ResponseEntity.ok(tourService.findTourDetail(tourId, userId, region, langCode));
		} catch (Exception e) {
			log.error("장소: id: {}, region: {} langCode: {} 상세 조회 중 오류 발생", tourId, region, langCode, e);
			return ResponseEntity.internalServerError().build();
		}
	}

	/**
	 * 유저가 좋아요 누른 장소 목록 조회
	 * @param region 지역
	 * @param langCode 언어 코드
	 * @return
	 */
	@CheckAbusing
	@UpdateTourAuthorize
	@GetMapping("/like/{region}/{langCode}")
	public ResponseEntity<TourListResponseDto> getLikeTourList(@PathVariable Region region,
		@PathVariable LangCode langCode, Principal principal) {
		try {
			String userId = principal.getName();
			return ResponseEntity.ok(tourService.findLikeTourList(userId, region, langCode));
		} catch (Exception e) {
			log.error("유저: userId: {}, region: {} langCode: {} 좋아요 누른 장소 목록 조회 중 오류 발생", principal.getName(), region,
				langCode, e);
			return ResponseEntity.internalServerError().build();
		}
	}

	/**
	 * 해당 유저가 만든 장소 목록 조회
	 * @param region 지역
	 * @param langCode 언어 코드
	 * @return
	 */
	@CheckAbusing
	@GetMapping("/{region}/{langCode}/account/{accountId}")
	public ResponseEntity<TourListResponseDto> getTourListByAccount(@PathVariable Region region,
		@PathVariable LangCode langCode, @PathVariable long accountId, @Nullable Principal principal) {
		try {
			String requesterName = principal == null ? null : principal.getName();
			return ResponseEntity.ok(tourService.findTourListByAccount(requesterName, accountId, region, langCode));
		} catch (EntityNotFoundException entityNotFoundException) {
			log.error("해당 유저: accountId: {}, region: {} langCode: {} 를 찾을 수 없습니다.", accountId, region, langCode);
			return ResponseEntity.notFound().build();
		} catch (Exception e) {
			log.error("유저: accountId: {}, region: {} langCode: {} 좋아요 누른 장소 목록 조회 중 오류 발생", accountId, region,
				langCode, e);
			return ResponseEntity.internalServerError().build();
		}
	}

	/**
	 * 투어에 좋아요 누르기
	 * @return
	 */
	@CheckAbusing
	@UpdateTourAuthorize
	@PostMapping("/like")
	public ResponseEntity<Void> likeTour(@RequestBody @Validated TourIdRequestDto requestDto, Principal principal,
		BindingResult bindingResult) throws BindException {
		if (bindingResult.hasErrors()) {
			throw new BindException(bindingResult);
		}

		try {
			tourService.likeTour(requestDto.id(), principal.getName());
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			log.error("투어 좋아요 누르기 id: {} userId: {} 중 오류 발생", requestDto.id(), principal.getName(), e);
			return ResponseEntity.internalServerError().build();
		}
	}

	@CreateMessageAuthorize
	@GetMapping("/messages/{langCode}")
	public ResponseEntity<TourRoomListResponseDto> getTourRoomList(Principal principal,
		@PathVariable LangCode langCode) {
		try {
			return ResponseEntity.ok(tourService.findTourRoomList(principal.getName(), langCode));
		} catch (Exception exception) {
			log.error("[@{}], 언어정보: {} 투어 메시지 목록 조회 중 오류 발생", principal.getName(), langCode, exception);
			return ResponseEntity.internalServerError().build();
		}

	}

	@CreateMessageAuthorize
	@PutMapping("/messages/{tourRoomId}/{langCode}")
	public ResponseEntity<TourMessageListResponseDto> readTourMessageList(Principal principal,
		@PathVariable long tourRoomId,
		@PathVariable LangCode langCode) {
		try {
			return ResponseEntity.ok(tourService.readTourMessageList(principal.getName(), tourRoomId, langCode));
		} catch (Exception exception) {
			log.error("[@{}], 언어정보: {} 투어 메시지 상세 조회 중 오류 발생", principal.getName(), langCode, exception);
			return ResponseEntity.internalServerError().build();
		}

	}

	/**
	 * 해당 투어에 대한 쪽지 보내기
	 * @return
	 */
	@CheckAbusing
	@CreateMessageAuthorize
	@PostMapping("/messages")
	public ResponseEntity<Object> createTourMessage(@Validated @RequestBody TourMessageCreateRequestDto requestDto,
		Principal principal, BindingResult bindingResult) throws BindException {
		if (bindingResult.hasErrors()) {
			throw new BindException(bindingResult);
		}
		try {
			tourService.createTourMessage(principal.getName(), requestDto);
			return ResponseEntity.ok().build();
		} catch (IllegalArgumentException illegalArgumentException) {
			return ResponseEntity.badRequest().body("쪽지 보내기 실패 - 잘못된 요청 정보");
		} catch (EntityNotFoundException entityNotFoundException) {
			return ResponseEntity.notFound().build();
		} catch (Exception exception) {
			return ResponseEntity.internalServerError().build();
		}
	}

	/**
	 * 투어 메시지에 대한 번역 요청
	 * @param requestDto
	 * @param bindingResult
	 * @return
	 * @throws BindException
	 */
	@CheckAbusing
	@CreateMessageAuthorize
	@PostMapping("/messages/translate")
	public ResponseEntity<TourMessageTranslateResponseDto> translateTourMessage(
		@Validated @RequestBody TourMessageTranslateRequestDto requestDto, BindingResult bindingResult)
		throws BindException {
		if (bindingResult.hasErrors()) {
			throw new BindException(bindingResult);
		}
		try {
			return ResponseEntity.ok(messageService.translateTourMessage(requestDto));
		} catch (Exception exception) {
			return ResponseEntity.internalServerError().build();
		}
	}

	@CreateMessageAuthorize
	@PostMapping("/messages/status")
	public ResponseEntity<Void> modifyTourMessageStatus(@RequestBody TourMessageStatusRequestDto requestDto,
		Principal principal) {
		try {
			tourService.modifyTourMessageStatus(requestDto, principal.getName());
			return ResponseEntity.ok().build();
		} catch (EntityNotFoundException entityNotFoundException) {
			log.error("투어 쪽지 허용 상태 변경 request: {} userId: {} 중 오류 발생", requestDto, principal.getName(),
				entityNotFoundException);
			return ResponseEntity.notFound().build();
		} catch (Exception e) {
			log.error("투어 쪽지 허용 상태 변경 request: {} userId: {} 중 오류 발생", requestDto, principal.getName(), e);
			return ResponseEntity.internalServerError().build();
		}
	}

}
