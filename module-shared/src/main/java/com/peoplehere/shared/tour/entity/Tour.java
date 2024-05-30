package com.peoplehere.shared.tour.entity;

import org.hibernate.annotations.Comment;

import com.peoplehere.shared.common.entity.BaseTimeEntity;
import com.peoplehere.shared.tour.data.request.TourUpdateRequestDto;

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
@Table(name = "tour")
@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Tour extends BaseTimeEntity {

	@Id
	@Tsid
	private Long id;

	@NotNull
	@Column(name = "account_id", nullable = false)
	private long accountId;

	@NotNull
	@Column(name = "place_id", nullable = false)
	private String placeId;

	@Column(name = "is_default_image")
	@Comment("기본 이미지 사용 여부")
	private Boolean isDefaultImage;

	@Column(name = "theme")
	@Comment("테마")
	private String theme;

	@Column(name = "direct_message_status")
	@Comment("쪽지 활성화 여부")
	@Builder.Default
	private boolean directMessageStatus = true;

	public void updateInfo(TourUpdateRequestDto requestDto) {
		this.placeId = requestDto.placeId();
		this.theme = requestDto.theme();
		this.isDefaultImage = requestDto.isDefaultImage();
	}

	public void updateDirectMessageStatus(boolean directMessageStatus) {
		this.directMessageStatus = directMessageStatus;
	}
}
