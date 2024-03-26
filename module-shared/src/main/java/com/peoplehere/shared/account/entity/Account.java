package com.peoplehere.shared.account.entity;

import java.io.Serial;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.peoplehere.shared.account.enums.AccountAuthority;
import com.peoplehere.shared.account.enums.AccountRole;
import com.peoplehere.shared.account.enums.Gender;

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
	private long id;

	@Column
	private String firstName;

	@Column
	private String lastName;

	@NotNull
	@Column(nullable = false, unique = true)
	private String userId;

	@Column
	private String password;

	@Column
	private String email;

	@Column
	private String phoneNum;

	@Column
	private String country;

	@Column
	private String birthDate;

	@Column
	@Enumerated(EnumType.STRING)
	private Gender gender;

	@Column
	@Enumerated(EnumType.STRING)
	AccountRole role;

	@Column
	private boolean active;

	@Column
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
		return firstName + lastName;
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
}
