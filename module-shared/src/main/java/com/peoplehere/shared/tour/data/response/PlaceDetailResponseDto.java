package com.peoplehere.shared.tour.data.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaceDetailResponseDto {

	@JsonProperty("result")
	private PlaceDetails placeDetails;

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class PlaceDetails {
		private String name;
		@JsonProperty("place_id")
		private String placeId;
		private Geometry geometry;
		@JsonProperty("formatted_address")
		private String formattedAddress;
		@JsonProperty("address_components")
		private List<AddressComponent> addressComponents;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Geometry {
		private Location location;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Location {
		private Double lat;
		private Double lng;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class AddressComponent {
		@JsonProperty("long_name")
		private String longName;
		private List<String> types;
	}
}
