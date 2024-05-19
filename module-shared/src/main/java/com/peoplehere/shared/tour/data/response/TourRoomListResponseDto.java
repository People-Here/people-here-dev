package com.peoplehere.shared.tour.data.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.peoplehere.shared.profile.data.ProfileInfoDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TourRoomListResponseDto {

	private List<TourRoomResponseDto> tourRoomList;
	private long accountId;

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class TourRoomResponseDto {
		private long id;
		private long tourId;
		@JsonIgnore
		private long messageId;
		private String title;
		private String lastMessage;
		private ProfileInfoDto ownerInfo;
		private ProfileInfoDto guestInfo;
	}
}
