package com.peoplehere.api.tour.service;

import static com.peoplehere.shared.tour.data.request.TourCreateRequestDto.*;
import static com.peoplehere.shared.tour.data.request.TourInfoTranslateRequestDto.*;
import static com.peoplehere.shared.tour.data.request.TourMessageCreateRequestDto.*;
import static java.util.stream.Collectors.*;

import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.peoplehere.shared.common.entity.Account;
import com.peoplehere.shared.common.enums.LangCode;
import com.peoplehere.shared.common.enums.Region;
import com.peoplehere.shared.common.event.TourInfoTranslatedEvent;
import com.peoplehere.shared.common.repository.AccountRepository;
import com.peoplehere.shared.common.service.FileService;
import com.peoplehere.shared.common.webhook.AlertWebhook;
import com.peoplehere.shared.tour.data.request.TourCreateRequestDto;
import com.peoplehere.shared.tour.data.request.TourListRequestDto;
import com.peoplehere.shared.tour.data.request.TourMessageCreateRequestDto;
import com.peoplehere.shared.tour.data.request.TourUpdateRequestDto;
import com.peoplehere.shared.tour.data.response.TourListResponseDto;
import com.peoplehere.shared.tour.data.response.TourMessageListResponseDto;
import com.peoplehere.shared.tour.data.response.TourResponseDto;
import com.peoplehere.shared.tour.data.response.TourRoomListResponseDto;
import com.peoplehere.shared.tour.entity.Tour;
import com.peoplehere.shared.tour.entity.TourImage;
import com.peoplehere.shared.tour.entity.TourInfo;
import com.peoplehere.shared.tour.entity.TourLike;
import com.peoplehere.shared.tour.entity.TourRoom;
import com.peoplehere.shared.tour.repository.CustomTourRepository;
import com.peoplehere.shared.tour.repository.CustomTourRoomRepository;
import com.peoplehere.shared.tour.repository.TourImageRepository;
import com.peoplehere.shared.tour.repository.TourInfoRepository;
import com.peoplehere.shared.tour.repository.TourLikeRepository;
import com.peoplehere.shared.tour.repository.TourMessageRepository;
import com.peoplehere.shared.tour.repository.TourRepository;
import com.peoplehere.shared.tour.repository.TourRoomRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class TourService {

	private final TourRepository tourRepository;
	private final TourImageRepository tourImageRepository;
	private final TourInfoRepository tourInfoRepository;
	private final TourLikeRepository tourLikeRepository;
	private final TourMessageRepository tourMessageRepository;
	private final TourRoomRepository tourRoomRepository;
	private final CustomTourRepository customTourRepository;
	private final CustomTourRoomRepository customTourRoomRepository;
	private final AccountRepository accountRepository;
	private final FileService fileService;
	private final AlertWebhook alertWebhook;
	private final ApplicationEventPublisher eventPublisher;

	@Transactional(readOnly = true)
	public TourListResponseDto findTourList(String userId, Region region, LangCode langCode) {
		Long accountId = accountRepository.findByUserId(userId).map(Account::getId).orElse(null);
		List<TourResponseDto> dtoList = customTourRepository.findTourList(accountId, region, langCode);
		return TourListResponseDto.builder().tourList(dtoList).build();
	}

	@Transactional(readOnly = true)
	public TourListResponseDto findTourListByKeyword(String userId, TourListRequestDto requestDto) {
		Long accountId = accountRepository.findByUserId(userId).map(Account::getId).orElse(null);
		List<TourResponseDto> dtoList = customTourRepository.findTourListByKeyword(accountId, requestDto);
		return TourListResponseDto.builder().tourList(dtoList).build();
	}

	/**
	 * 투어 등록
	 * 원문으로 투어 정보 등록 후 번역 진행
	 * @param userId 유저 id
	 * @param requestDto
	 */
	@Transactional
	public void createTour(String userId, TourCreateRequestDto requestDto) {
		try {
			// 1. 유저 조회 후 투어 등록
			long accountId = accountRepository.findByUserId(userId)
				.map(Account::getId)
				.orElseThrow(() -> new EntityNotFoundException("해당 유저[%s]를 찾을 수 없습니다.".formatted(userId)));

			boolean isDefaultImage = CollectionUtils.isEmpty(requestDto.images());
			Tour tour = tourRepository.save(toTourEntity(requestDto, accountId, isDefaultImage));

			// 2. 이미지 업로드 및 투어 이미지 저장
			if (!isDefaultImage) {
				List<String> tourImageUrlList = fileService.uploadFileListAndGetFileInfoList(tour.getId(),
					requestDto.images());

				List<TourImage> tourImageList = tourImageUrlList
					.stream()
					.map(url -> TourImage.builder()
						.tourId(tour.getId())
						.thumbnailUrl(url)
						.build())
					.collect(toList());

				tourImageRepository.saveAll(tourImageList);
			}

			// 3. 투어 정보 저장
			TourInfo tourInfo = tourInfoRepository.save(toTourInfoEntity(requestDto, tour.getId()));

			// 4. 이벤트 발행
			eventPublisher.publishEvent(new TourInfoTranslatedEvent(toTranslateRequestDto(tour, tourInfo)));
		} catch (Exception exception) {
			log.error("투어: [{}] 등록 중 오류 발생", requestDto, exception);
			alertWebhook.alertError("투어 등록 중 오류 발생",
				"request: [%s], error: [%s]".formatted(requestDto, exception.getMessage()));
			throw new RuntimeException("투어 등록 중 오류 발생: %s".formatted(requestDto), exception);
		}
	}

	/**
	 * 투어 수정
	 * 원문으로 투어 정보 수정 후 번역 진행
	 * @param userId 유저 id
	 * @param requestDto
	 */
	@Transactional
	public void updateTour(String userId, TourUpdateRequestDto requestDto) {
		try {
			// 1. 유저의 투어 조회
			Account account = accountRepository.findByUserId(userId)
				.orElseThrow(() -> new EntityNotFoundException("해당 유저[%s]를 찾을 수 없습니다.".formatted(userId)));
			Tour tour = tourRepository.findByIdAndAccountId(requestDto.id(), account.getId())
				.orElseThrow(() -> new EntityNotFoundException("해당 투어[%s]를 찾을 수 없습니다.".formatted(requestDto.id())));

			tour.updateInfo(requestDto);
			updateTourImage(requestDto);

			// 3. 투어 정보 수정
			TourInfo tourInfo = tourInfoRepository.findByTourIdAndLangCode(tour.getId(), LangCode.ORIGIN)
				.orElseThrow(() -> new EntityNotFoundException("해당 투어[%s]의 정보를 찾을 수 없습니다.".formatted(tour.getId())));
			tourInfo.updateInfo(requestDto);

			// 4. 이벤트 발행
			eventPublisher.publishEvent(new TourInfoTranslatedEvent(toTranslateRequestDto(tour, tourInfo)));
		} catch (Exception exception) {
			log.error("투어: [{}] 수정 중 오류 발생", requestDto, exception);
			alertWebhook.alertError("투어 수정 중 오류 발생",
				"request: [%s], error: [%s]".formatted(requestDto, exception.getMessage()));
			throw new RuntimeException("투어 수정 중 오류 발생: %s".formatted(requestDto), exception);
		}
	}

	/**
	 * 이미지 업로드 및 투어 이미지 새로 저장 - 기본 이미지 사용 x && 이미지가 존재하는 경우
	 * @param requestDto 투어 수정 요청 DTO
	 */
	private void updateTourImage(TourUpdateRequestDto requestDto) {
		long tourId = requestDto.id();
		if (!requestDto.isDefaultImage() && !CollectionUtils.isEmpty(requestDto.images())) {
			tourImageRepository.deleteAllByTourId(tourId);

			List<String> tourImageUrlList = fileService.uploadFileListAndGetFileInfoList(tourId,
				requestDto.images());

			List<TourImage> tourImageList = tourImageUrlList
				.stream()
				.map(url -> TourImage.builder()
					.tourId(tourId)
					.thumbnailUrl(url)
					.build())
				.collect(toList());

			tourImageRepository.saveAll(tourImageList);
		}
	}

	@Transactional
	public void deleteTour(long tourId) {
		try {
			tourRepository.deleteById(tourId);
			tourInfoRepository.deleteAllByTourId(tourId);
			tourImageRepository.deleteAllByTourId(tourId);
			tourLikeRepository.deleteAllByTourId(tourId);
		} catch (Exception exception) {
			log.error("투어 ID: [{}] 삭제 중 오류 발생", tourId, exception);
			alertWebhook.alertError("투어 삭제 중 오류 발생",
				"tourId: [%s], error: [%s]".formatted(tourId, exception.getMessage()));
			throw new RuntimeException("투어 삭제 중 오류 발생: %s".formatted(tourId), exception);
		}
	}

	@Transactional(readOnly = true)
	public TourResponseDto findTourDetail(long tourId, String userId, Region region, LangCode langCode) {
		Long accountId = accountRepository.findByUserId(userId).map(Account::getId).orElse(null);
		return customTourRepository.findTourDetail(tourId, accountId, region, langCode).orElse(null);
	}

	/**
	 * 유저가 좋아요 누른 투어 목록 조회
	 * @param userId 유저 id
	 * @param langCode 언어 코드
	 * @return 투어 목록
	 */
	@Transactional(readOnly = true)
	public TourListResponseDto findLikeTourList(String userId, Region region, LangCode langCode) {
		long accountId = accountRepository.findByUserId(userId)
			.map(Account::getId)
			.orElseThrow(() -> new EntityNotFoundException("해당 유저[%s]를 찾을 수 없습니다.".formatted(userId)));
		List<TourResponseDto> dtoList = customTourRepository.findLikeTourList(accountId, region, langCode);
		return TourListResponseDto.builder().tourList(dtoList).build();
	}

	/**
	 * 유저가 만든 투어 목록 조회
	 * @param requesterName 요청자 이름
	 * @param targetAccountId 대상 유저 id
	 * @param region 지역
	 * @param langCode 언어 코드
	 * @return 투어 목록
	 */
	@Transactional(readOnly = true)
	public TourListResponseDto findTourListByAccount(String requesterName, long targetAccountId, Region region,
		LangCode langCode) throws
		EntityNotFoundException {
		Long requesterId = accountRepository.findByUserId(requesterName).map(Account::getId).orElse(null);

		if (!accountRepository.existsById(targetAccountId)) {
			throw new EntityNotFoundException("해당 유저[%s]를 찾을 수 없습니다.".formatted(targetAccountId));
		}
		List<TourResponseDto> dtoList = customTourRepository.findTourListByAccount(requesterId, targetAccountId, region,
			langCode);
		return TourListResponseDto.builder().tourList(dtoList).build();
	}

	/**
	 * 투어 좋아요 토글
	 * @param tourId 투어 id
	 * @param userId 유저 id
	 */
	@Transactional
	public void likeTour(long tourId, String userId) {
		long accountId = accountRepository.findByUserId(userId)
			.map(Account::getId)
			.orElseThrow(() -> new EntityNotFoundException("해당 유저[%s]를 찾을 수 없습니다.".formatted(userId)));

		TourLike tourLike = tourLikeRepository.findByTourIdAndAccountId(tourId, accountId)
			.orElse(TourLike.builder()
				.tourId(tourId)
				.accountId(accountId)
				.build());

		tourLike.toggleLike();
		tourLikeRepository.save(tourLike);
	}

	/**
	 * 유저의 투어 룸 목록 조회
	 * @param userId
	 * @return
	 */
	@Transactional(readOnly = true)
	public TourRoomListResponseDto findTourRoomList(String userId, LangCode langCode) {
		long accountId = accountRepository.findByUserId(userId)
			.map(Account::getId)
			.orElseThrow(() -> new EntityNotFoundException("해당 유저[%s]를 찾을 수 없습니다.".formatted(userId)));

		return customTourRoomRepository.findTourRoomList(accountId, langCode).orElse(null);
	}

	/**
	 * 유저의 투어 메시지 상세 조회
	 * @param userId
	 * @return
	 */
	@Transactional(readOnly = true)
	public TourMessageListResponseDto findTourMessageList(String userId, long tourRoomId, LangCode langCode) {
		long accountId = accountRepository.findByUserId(userId)
			.map(Account::getId)
			.orElseThrow(() -> new EntityNotFoundException("해당 유저[%s]를 찾을 수 없습니다.".formatted(userId)));

		return customTourRoomRepository.findTourMessageList(accountId, tourRoomId, langCode).orElse(null);
	}

	/**
	 * 투어 메시지 보내기
	 *
	 * @param senderName 보내는 사람 아이디
	 * @param requestDto 메시지 정보
	 * @throws EntityNotFoundException 요청 정보에 해당하는 entity를 찾을 수 없는 경우
	 * @throws IllegalArgumentException 요청 정보가 잘못된 경우
	 */
	@Transactional
	public void createTourMessage(String senderName, TourMessageCreateRequestDto requestDto) throws
		EntityNotFoundException, IllegalArgumentException {
		try {
			// 1. sender와 receiver가 존재하는지 확인
			long senderId = accountRepository.findByUserId(senderName)
				.map(Account::getId)
				.orElseThrow(() -> new EntityNotFoundException("sender - 유저[%s]를 찾을 수 없습니다.".formatted(senderName)));

			long receiverId = accountRepository.findById(requestDto.receiverId())
				.map(Account::getId)
				.orElseThrow(
					() -> new EntityNotFoundException(
						"receiver - 유저[%s]를 찾을 수 없습니다.".formatted(requestDto.receiverId())));

			if (senderId == receiverId) {
				throw new IllegalArgumentException("자기 자신에게 쪽지를 보낼 수 없습니다.");
			}

			// 2. 투어 메시지 보낼 수 있는지 확인
			Tour tour = tourRepository.findByIdAndDirectMessageStatus(requestDto.tourId(), true)
				.orElseThrow(
					() -> new IllegalArgumentException("쪽지를 보낼수없는 투어 정보: [%s]입니다".formatted(requestDto.tourId())));

			TourRoom tourRoom = getTourRoom(senderId, receiverId, tour);

			tourMessageRepository.save(toTourMessageEntity(requestDto, tourRoom.getId(), senderId));

		} catch (IllegalArgumentException illegalArgumentException) {
			log.error("쪽지 보내기 실패 - 잘못된 요청 정보", illegalArgumentException);
			alertWebhook.alertError("쪽지 보내기 실패 - 잘못된 요청 정보",
				"request: [%s], error: [%s]".formatted(requestDto, illegalArgumentException.getMessage()));
			throw illegalArgumentException;
		} catch (EntityNotFoundException entityNotFoundException) {
			log.error("쪽지 보내기 실패 - 해당 투어[%s]를 찾을 수 없습니다.".formatted(requestDto.tourId()), entityNotFoundException);
			throw entityNotFoundException;
		} catch (Exception exception) {
			log.error("쪽지 보내기 중 오류 발생", exception);
			alertWebhook.alertError("쪽지 보내기 중 오류 발생",
				"request: [%s], error: [%s]".formatted(requestDto, exception.getMessage()));
			throw new RuntimeException("쪽지 보내기 중 오류 발생: %s".formatted(requestDto), exception);
		}
	}

	/**
	 * 투어 관련 대화방을 조회
	 * @param senderId 보내는 사람 id
	 * @param receiverId 받는 사람 id
	 * @param tour 투어 정보
	 * @return
	 * @throws IllegalArgumentException 투어 주최자 또는 유효하지 않은 사용자가 대화를 시도할 때 예외를 발생시킵니다
	 */
	private TourRoom getTourRoom(long senderId, long receiverId, Tour tour) throws IllegalArgumentException {
		long ownerId = tour.getAccountId();
		long guestId;
		long tourId = tour.getId();

		TourRoom tourRoom;

		if (senderId == ownerId) {
			guestId = receiverId;
			tourRoom = tourRoomRepository.findByTourIdAndOwnerIdAndGuestId(tourId, ownerId, guestId)
				.orElseThrow(() -> new IllegalArgumentException("투어 주인은 투어룸을 생성할 수 없습니다"));
		} else if (receiverId == ownerId) {
			guestId = senderId;
			tourRoom = tourRoomRepository.findByTourIdAndOwnerIdAndGuestId(tourId, ownerId, guestId)
				.orElseGet(() -> tourRoomRepository.save(TourRoom.builder()
					.tourId(tourId)
					.ownerId(ownerId)
					.guestId(guestId)
					.build()));
		} else {
			throw new IllegalArgumentException("투어 주인과 게스트가 아닌 사람은 쪽지를 보낼 수 없습니다");
		}
		return tourRoom;
	}
}
