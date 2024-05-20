package com.peoplehere.shared.tour.data.response;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TourMessageListResponseDto {

	private long tourId;
	private long tourRoomId;
	private String title;
	private ProfileInfoDto ownerInfo;
	private ProfileInfoDto guestInfo;
	private List<MessageInfo> messageList;

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class MessageInfo {
		private long messageId;
		private long senderId;
		private long receiverId;
		private String message;
		@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
		private LocalDateTime createdAt;
	}
}
