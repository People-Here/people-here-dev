package com.peoplehere.shared.tour.entity;

import org.hibernate.annotations.Comment;

import com.peoplehere.shared.common.entity.BaseTimeEntity;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@Table(name = "tour_message")
@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class TourMessage extends BaseTimeEntity {

	@Id
	@Tsid
	private Long id;

	@NotNull
	@Column(name = "tour_room_id", nullable = false)
	private long tourRoomId;

	@NotNull
	@Comment("송신자 ID")
	@Column(name = "sender_id", nullable = false)
	private long senderId;

	@NotNull
	@Comment("수신자 ID")
	@Column(name = "receiver_id", nullable = false)
	private long receiverId;

	@NotNull
	@Comment("메시지")
	@Column(nullable = false)
	private String message;

	@NotNull
	@Column(name = "sender_read_flag", nullable = false)
	@Builder.Default
	private boolean senderReadFlag = false;

	@NotNull
	@Column(name = "receiver_read_flag", nullable = false)
	@Builder.Default
	private boolean receiverReadFlag = false;

	public void setReadFlag(long accountId) {
		if (accountId == this.senderId) {
			this.senderReadFlag = true;
		} else if (accountId == this.receiverId) {
			this.receiverReadFlag = true;
		}
	}
}
