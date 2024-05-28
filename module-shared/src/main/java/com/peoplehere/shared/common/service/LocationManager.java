package com.peoplehere.shared.common.service;

import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.peoplehere.shared.common.enums.LangCode;
import com.peoplehere.shared.tour.data.response.PlaceDetailResponseDto;
import com.peoplehere.shared.tour.data.response.PlaceInfoResponseDto;
import com.peoplehere.shared.tour.entity.Location;
import com.peoplehere.shared.tour.entity.LocationInfo;
import com.peoplehere.shared.tour.repository.LocationInfoRepository;
import com.peoplehere.shared.tour.repository.LocationRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocationManager {

	private final LocationRepository locationRepository;
	private final LocationInfoRepository locationInfoRepository;

	/**
	 * 요청을 통해 조회한 장소 세부정보를 통해 PlaceInfoResponseDto로 변환
	 * @param detailResponseDto
	 * @param langCode
	 * @return
	 */
	@Transactional
	public PlaceInfoResponseDto convertPlaceInfoResponseDto(PlaceDetailResponseDto detailResponseDto,
		LangCode langCode) {
		Location location = saveLocation(Objects.requireNonNull(detailResponseDto));
		LocationInfo locationInfo = saveLocationInfo(Objects.requireNonNull(detailResponseDto), langCode);

		return PlaceInfoResponseDto.builder()
			.placeId(location.getPlaceId())
			.name(locationInfo.getName())
			.address(locationInfo.getAddress())
			.build();
	}

	/**
	 * 요청을 통해 조회한 장소 세부정보를 통해 Location 저장
	 * @param detailResponseDto
	 * @return
	 */
	private Location saveLocation(PlaceDetailResponseDto detailResponseDto) {
		PlaceDetailResponseDto.PlaceDetails placeDetails = detailResponseDto.getPlaceDetails();
		String placeId = placeDetails.getPlaceId();

		return locationRepository.findByPlaceId(placeId).orElseGet(() -> {
			Location newLocation = Location.builder()
				.placeId(placeId)
				.latitude(placeDetails.getGeometry().getLocation().getLat())
				.longitude(placeDetails.getGeometry().getLocation().getLng())
				.build();
			return locationRepository.save(newLocation);
		});
	}

	/**
	 * 요청을 통해 조회한 장소 세부정보를 통해 LocationInfo 저장
	 * @param detailResponseDto
	 * @param langCode
	 * @return
	 */
	private LocationInfo saveLocationInfo(PlaceDetailResponseDto detailResponseDto, LangCode langCode) {
		PlaceDetailResponseDto.PlaceDetails details = detailResponseDto.getPlaceDetails();
		String placeId = details.getPlaceId();

		return locationInfoRepository.findByPlaceIdAndLangCode(placeId, langCode).orElseGet(() -> {
			LocationInfo newLocationInfo = LocationInfo.builder()
				.placeId(placeId)
				.langCode(langCode)
				.name(details.getName())
				.address(details.getFormattedAddress())
				.country(details.getAddressComponents().stream()
					.filter(c -> c.getTypes().contains("country") && c.getTypes().contains("political"))
					.findFirst()
					.map(PlaceDetailResponseDto.AddressComponent::getLongName)
					.orElse(null))
				.city(details.getAddressComponents().stream()
					.filter(c -> c.getTypes().contains("administrative_area_level_1"))
					.findFirst()
					.map(PlaceDetailResponseDto.AddressComponent::getLongName)
					.orElse(null))
				.district(details.getAddressComponents().stream()
					.filter(c -> c.getTypes().contains("sublocality_level_1") && c.getTypes().contains("sublocality"))
					.findFirst()
					.map(PlaceDetailResponseDto.AddressComponent::getLongName)
					.orElse(null))
				.streetAddress(details.getAddressComponents().stream()
					.filter(c -> c.getTypes().contains("route"))
					.findFirst()
					.map(PlaceDetailResponseDto.AddressComponent::getLongName)
					.orElse(null))
				.build();
			return locationInfoRepository.save(newLocationInfo);
		});
	}
}
