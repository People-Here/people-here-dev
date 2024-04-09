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
public class TourResponseDto {

	private long id;
	private String title;
	private List<CategoryInfo> categoryList;
	@Builder.Default
	private boolean like = false;
	private PlaceInfo placeInfo;
	private UserInfo userInfo;

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class PlaceInfo {
		@JsonProperty("id")
		private String placeId;
		private String name;
		private List<PlaceImageInfo> imageUrlList;
		private String district;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class UserInfo {
		@JsonProperty("id")
		private long accountId;
		private String firstName;
		private String lastName;
		private String profileImageUrl;
		private boolean directMessageStatus;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class CategoryInfo {
		@JsonProperty("name")
		private String categoryName;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class PlaceImageInfo {
		@JsonProperty("imageUrl")
		private String placeImageUrl;
	}
}
