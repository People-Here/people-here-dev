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
		private long accountId;
		@JsonIgnore
		private long messageId;
		private String title;
		private String lastMessage;
		private boolean readFlag;
		@JsonIgnore
		private long senderId;
		@JsonIgnore
		private long receiverId;
		@JsonIgnore
		private boolean senderReadFlag;
		@JsonIgnore
		private boolean receiverReadFlag;
		private ProfileInfoDto ownerInfo;
		private ProfileInfoDto guestInfo;

		/**
		 * accountId와 senderId, receiverId 비교하여 readFlag를 설정한다.
		 * @return
		 */
		public boolean isReadFlag() {
			if (accountId == senderId) {
				return senderReadFlag;
			} else {
				return receiverReadFlag;
			}
		}
	}
}
