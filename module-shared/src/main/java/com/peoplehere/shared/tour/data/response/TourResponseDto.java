package com.peoplehere.shared.tour.data.response;

import java.util.Collections;
import java.util.List;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.peoplehere.shared.profile.data.ProfileInfoDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TourResponseDto {

	private long id;
	private String title;
	private String description;
	@Builder.Default
	private boolean like = false;
	private String theme;
	private PlaceInfo placeInfo;
	private ProfileInfoDto userInfo;

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class PlaceInfo {
		@JsonProperty("id")
		private String placeId;
		private String name;
		private Boolean isDefaultImage;
		private List<PlaceImageInfo> imageUrlList;
		private String district;
		private String address;

		public List<PlaceImageInfo> getImageUrlList() {
			if (Boolean.TRUE.equals(isDefaultImage)) {
				return Collections.emptyList();
			}
			return imageUrlList;
		}
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class PlaceImageInfo {
		@JsonProperty("imageUrl")
		private String placeImageUrl;
		@JsonIgnore
		private String optimizedImageUrl;

		public String getPlaceImageUrl() {
			if (StringUtils.hasText(optimizedImageUrl)) {
				return optimizedImageUrl;
			}
			return placeImageUrl;
		}
	}
}
