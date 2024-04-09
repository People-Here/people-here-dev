package com.peoplehere.api.tour.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.peoplehere.shared.tour.data.response.TourListResponseDto;
import com.peoplehere.shared.tour.data.response.TourResponseDto;
import com.peoplehere.shared.tour.repository.CustomTourRepository;
import com.peoplehere.shared.tour.repository.TourRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class TourService {

	private final TourRepository tourRepository;
	private final CustomTourRepository customTourRepository;

	@Transactional(readOnly = true)
	public TourListResponseDto findTourList() {
		List<TourResponseDto> dtoList = customTourRepository.findTourList();
		return TourListResponseDto.builder().tourList(dtoList).build();
	}

	@Transactional(readOnly = true)
	public TourListResponseDto findTourListByKeyword(String keyword) {
		List<TourResponseDto> dtoList = customTourRepository.findTourListByKeyword(keyword);
		return TourListResponseDto.builder().tourList(dtoList).build();
	}
}
