package com.peoplehere.shared.tour.data.response;

import java.time.LocalDateTime;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.peoplehere.shared.profile.data.ProfileInfoDto;
import com.peoplehere.shared.tour.entity.TourMessage;

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
	private List<MessageInfo> messageInfoList;
	@JsonIgnore
	private List<TourMessage> tourMessageList;

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
		private boolean readFlag;
		@JsonIgnore
		private boolean senderReadFlag;
		@JsonIgnore
		private boolean receiverReadFlag;

		/**
		 * accountId와 senderId, receiverId 비교하여 readFlag를 설정한다.
		 * @return
		 */
		public boolean isReadFlag(long accountId) {
			if (accountId == senderId) {
				return senderReadFlag;
			} else {
				return receiverReadFlag;
			}

		}
	}

	public List<MessageInfo> getMessageInfoList() {
		return this.tourMessageList.stream()
			.map(MessageInfoMapper.MAPPER::toDto)
			.toList();
	}

	@Mapper(componentModel = "spring")
	interface MessageInfoMapper {
		MessageInfoMapper MAPPER = Mappers.getMapper(MessageInfoMapper.class);

		@Mapping(source = "id", target = "messageId")
		MessageInfo toDto(TourMessage tourMessage);
	}
}
