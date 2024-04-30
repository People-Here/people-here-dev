package com.peoplehere.shared.tour.entity;

import org.hibernate.annotations.Comment;

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
@Table(name = "tour_like")
@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class TourLike {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@NotNull
	@Column(name = "tour_id", nullable = false)
	private long tourId;

	@NotNull
	@Comment("좋아요 누른 사용자 ID")
	@Column(name = "account_id", nullable = false)
	private long accountId;

	@NotNull
	@Column(name = "is_like", nullable = false)
	private boolean isLike;

	public void toggleLike() {
		this.isLike = !this.isLike;
	}
}
