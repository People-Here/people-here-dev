package com.peoplehere.shared.tour.entity;

import org.hibernate.annotations.Comment;

import com.peoplehere.shared.common.entity.BaseTimeEntity;
import com.peoplehere.shared.common.enums.LangCode;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "location_info")
@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class LocationInfo extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@NotNull
	@Column(name = "place_id", nullable = false)
	private String placeId;

	@Column(name = "language")
	@Enumerated(EnumType.STRING)
	@Comment("언어")
	private LangCode langCode;

	@NotNull
	@Comment("장소명")
	@Column(nullable = false)
	private String name;

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
