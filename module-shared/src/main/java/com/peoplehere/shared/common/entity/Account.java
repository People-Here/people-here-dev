package com.peoplehere.shared.common.entity;

import java.io.Serial;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.hibernate.annotations.Comment;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.peoplehere.shared.common.enums.AccountAuthority;
import com.peoplehere.shared.common.enums.AccountRole;
import com.peoplehere.shared.common.enums.Gender;
import com.peoplehere.shared.common.enums.Region;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@Table(name = "account")
@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Account extends BaseTimeEntity implements UserDetails {

	@Serial
	@Transient
	private static final long serialVersionUID = 1L;

	@Id
	@Tsid
	private Long id;

	@Column(name = "first_name")
	@Comment("성")
	private String firstName;

	@Column(name = "last_name")
	@Comment("이름")
	private String lastName;

	@NotNull
	@Comment("유저 아이디")
	@Column(name = "user_id", nullable = false, unique = true)
	private String userId;

	@Column
	@Comment("비밀번호")
	private String password;

	@NotNull
	@Column(nullable = false, unique = true)
	@Comment("이메일")
	private String email;

	@Column(name = "phone_number")
	@Comment("전화번호")
	private String phoneNumber;

	@Column
	@Comment("국가정보")
	@Enumerated(EnumType.STRING)
	private Region region;

	@Column(name = "birth_date")
	@Comment("생년월일")
	private LocalDate birthDate;

	@Column
	@Enumerated(EnumType.STRING)
	@Comment("성별")
	private Gender gender;

	@Column
	@Enumerated(EnumType.STRING)
	@Comment("유저 권한")
	AccountRole role;

	@Column
	@Comment("유저 활성화 여부")
	private boolean active;

	@Column(name = "deleted_at")
	LocalDateTime deletedAt;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		if (this.role == null) {
			return Collections.emptyList();
		}

		List<GrantedAuthority> authorityList = new ArrayList<>();

		authorityList.add(new SimpleGrantedAuthority(this.role.getValue()));

		Arrays.stream(AccountAuthority.values())
			.filter(accountAuthority -> this.role.hasAuthority(accountAuthority))
			.map(accountAuthority -> new SimpleGrantedAuthority(accountAuthority.getValue()))
			.forEach(authorityList::add);

		return authorityList;
	}

	@Override
	public String getUsername() {
		return userId;
	}

	/**
	 * 계정이 만료되지 않았는지 리턴
	 * @return
	 */
	@Override
	public boolean isAccountNonExpired() {
		return this.active;
	}

	/**
	 * 계정이 잠겨있지 않은지 리턴
	 * @return
	 */
	@Override
	public boolean isAccountNonLocked() {
		return this.active;
	}

	/**
	 * 비밀번호가 만료되지 않았는지 리턴
	 * @return
	 */
	@Override
	public boolean isCredentialsNonExpired() {
		return this.active;
	}

	/**
	 * 계정이 활성화(사용가능)인지 리턴
	 * @return
	 */
	@Override
	public boolean isEnabled() {
		return this.active;
	}

	public void updatePassword(String encodedPassword) {
		this.password = encodedPassword;
	}
}
