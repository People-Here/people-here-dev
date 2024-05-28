package com.peoplehere.shared.tour.entity;

import org.hibernate.annotations.Comment;

import com.peoplehere.shared.common.entity.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
@Table(name = "location")
@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Location extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@NotNull
	@Comment("장소 ID")
	@Column(name = "place_id", nullable = false, unique = true)
	private String placeId;

	@Column(name = "default_thumbnail_url")
	@Comment("기본 이미지")
	private String defaultThumbnailUrl;

	@Comment("위도")
	private Double latitude;

	@Comment("경도")
	private Double longitude;

}
