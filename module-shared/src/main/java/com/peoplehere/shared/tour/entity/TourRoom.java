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
@Table(name = "tour_room")
@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class TourRoom extends BaseTimeEntity {

	@Id
	@Tsid
	private Long id;

	@NotNull
	@Column(name = "tour_id", nullable = false)
	private long tourId;

	@NotNull
	@Comment("Owner ID")
	@Column(name = "owner_id", nullable = false)
	private long ownerId;

	@NotNull
	@Comment("Guest ID")
	@Column(name = "guest_id", nullable = false)
	private long guestId;
}
