package com.peoplehere.api.tour.service;

import static com.peoplehere.shared.tour.data.request.TourCreateRequestDto.*;
import static java.util.stream.Collectors.*;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.peoplehere.shared.common.entity.Account;
import com.peoplehere.shared.common.enums.LangCode;
import com.peoplehere.shared.common.repository.AccountRepository;
import com.peoplehere.shared.common.service.FileService;
import com.peoplehere.shared.common.webhook.AlertWebhook;
import com.peoplehere.shared.tour.data.request.TourCreateRequestDto;
import com.peoplehere.shared.tour.data.request.TourListRequestDto;
import com.peoplehere.shared.tour.data.request.TourUpdateRequestDto;
import com.peoplehere.shared.tour.data.response.TourListResponseDto;
import com.peoplehere.shared.tour.data.response.TourResponseDto;
import com.peoplehere.shared.tour.entity.Tour;
import com.peoplehere.shared.tour.entity.TourImage;
import com.peoplehere.shared.tour.repository.CustomTourRepository;
import com.peoplehere.shared.tour.repository.TourImageRepository;
import com.peoplehere.shared.tour.repository.TourInfoRepository;
import com.peoplehere.shared.tour.repository.TourRepository;

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
	private final CustomTourRepository customTourRepository;
	private final AccountRepository accountRepository;
	private final FileService fileService;
	private final AlertWebhook alertWebhook;

	@Transactional(readOnly = true)
	public TourListResponseDto findTourList(LangCode langCode) {
		List<TourResponseDto> dtoList = customTourRepository.findTourListByLangCode(langCode);
		return TourListResponseDto.builder().tourList(dtoList).build();
	}

	@Transactional(readOnly = true)
	public TourListResponseDto findTourListByKeyword(TourListRequestDto requestDto) {
		List<TourResponseDto> dtoList = customTourRepository.findTourListByKeyword(requestDto);
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
			Account account = accountRepository.findByUserId(userId)
				.orElseThrow(() -> new EntityNotFoundException("해당 유저[%s]를 찾을 수 없습니다.".formatted(userId)));

			boolean isDefaultImage = CollectionUtils.isEmpty(requestDto.images());
			Tour tour = tourRepository.save(toTourEntity(requestDto, account.getId(), isDefaultImage));

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
			tourInfoRepository.save(toTourInfoEntity(requestDto, tour.getId()));

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

			// 2. 이미지 업로드 및 투어 이미지 새로 저장 - 기본 이미지 사용 x && 이미지가 존재하는 경우
			if (!requestDto.isDefaultImage() && !CollectionUtils.isEmpty(requestDto.images())) {
				tourImageRepository.deleteAllByTourId(tour.getId());

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

			// 3. 투어 정보 수정
			tourInfoRepository.findByTourIdAndLangCode(tour.getId(), LangCode.ORIGIN)
				.ifPresent(tourInfo -> {
					tourInfo.updateInfo(requestDto);
				});

		} catch (Exception exception) {
			log.error("투어: [{}] 수정 중 오류 발생", requestDto, exception);
			alertWebhook.alertError("투어 수정 중 오류 발생",
				"request: [%s], error: [%s]".formatted(requestDto, exception.getMessage()));
			throw new RuntimeException("투어 수정 중 오류 발생: %s".formatted(requestDto), exception);
		}
	}

	@Transactional
	public void deleteTour(long tourId) {
		try {
			tourRepository.deleteById(tourId);
			tourInfoRepository.deleteAllByTourId(tourId);
			tourImageRepository.deleteAllByTourId(tourId);
		} catch (Exception exception) {
			log.error("투어 ID: [{}] 삭제 중 오류 발생", tourId, exception);
			alertWebhook.alertError("투어 삭제 중 오류 발생",
				"tourId: [%s], error: [%s]".formatted(tourId, exception.getMessage()));
			throw new RuntimeException("투어 삭제 중 오류 발생: %s".formatted(tourId), exception);
		}
	}
}
