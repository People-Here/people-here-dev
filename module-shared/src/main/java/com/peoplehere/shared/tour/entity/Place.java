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
@Table(name = "place")
@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Place extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@NotNull
	@Comment("장소 ID")
	@Column(name = "place_id", nullable = false, unique = true)
	private String placeId;

	@NotNull
	@Comment("장소명")
	@Column(nullable = false)
	private String name;

	@Column(name = "default_thumbnail_url")
	@Comment("기본 이미지")
	private String defaultThumbnailUrl;

	@Comment("위도")
	private Double latitude;

	@Comment("경도")
	private Double longitude;

	@Comment("전체 주소")
	private String address;

	@Comment("나라")
	private String country;

	@Comment("도시")
	private String city;

	@Comment("구")
	private String district;

	@Column(name = "street_address")
	@Comment("상세 주소")
	private String streetAddress;

}
