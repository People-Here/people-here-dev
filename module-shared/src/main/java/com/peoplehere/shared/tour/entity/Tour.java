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

	@NotNull
	@Comment("제목")
	@Column(nullable = false)
	private String title;

	@Column(name = "is_default_image")
	@Comment("기본 이미지 사용 여부")
	private Boolean isDefaultImage;
}
