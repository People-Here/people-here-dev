package com.peoplehere.shared.tour.entity;

import org.hibernate.annotations.Comment;

import com.peoplehere.shared.common.entity.BaseTimeEntity;
import com.peoplehere.shared.common.enums.LangCode;
import com.peoplehere.shared.tour.data.request.TourUpdateRequestDto;

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
@Table(name = "tour_info")
@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class TourInfo extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@NotNull
	@Column(name = "tour_id", nullable = false)
	private long tourId;

	@Column(name = "language")
	@Enumerated(EnumType.STRING)
	@Comment("언어")
	private LangCode langCode;

	@NotNull
	@Comment("제목")
	@Column(nullable = false)
	private String title;

	@Comment("설명")
	@Column
	private String description;

	public void updateInfo(TourUpdateRequestDto requestDto) {
		this.title = requestDto.title();
		this.description = requestDto.description();
	}
}
