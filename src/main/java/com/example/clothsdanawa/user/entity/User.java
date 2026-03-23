package com.example.clothsdanawa.user.entity;

import java.time.LocalDateTime;

import org.springframework.security.oauth2.core.user.OAuth2User;

import com.example.clothsdanawa.auth.dto.AuthSignUpRequestDto;
import com.example.clothsdanawa.common.BaseEntity;
import com.example.clothsdanawa.user.dto.UserUpdateRequestDto;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
	name = "users",
	uniqueConstraints = @UniqueConstraint(name = "uk_users_email", columnNames = "email")
)
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long userId;

	private String name;

	private String email;

	private String password;

	private String address;

	private String provider;

	private String providerId;

	@Enumerated(EnumType.STRING)
	private UserRole userRole;

	private LocalDateTime deletedAt = null;

	@Builder
	private User(String name, String email, String password, String address, String provider, String providerId,
		UserRole userRole) {
		this.name = name;
		this.email = email;
		this.password = password;
		this.address = address;
		this.provider = provider;
		this.providerId = providerId;
		this.userRole = userRole;
	}

	public static User of(AuthSignUpRequestDto authSignUpRequestDto, String encodedPassword) {
		return User.builder()
			.name(authSignUpRequestDto.getName())
			.email(authSignUpRequestDto.getEmail())
			.password(encodedPassword)
			.address(authSignUpRequestDto.getAddress())
			.userRole(UserRole.from(authSignUpRequestDto.getUserRole()))
			.build();
	}

	public static User createOauthUser(OAuth2User oAuth2User, String email, String provider, String providerId,
		String userRole) {
		return User.builder()
			.name(oAuth2User.getName())
			.email(email)
			.provider(provider)
			.providerId(providerId)
			.userRole(UserRole.from(userRole))
			.build();
	}

	public void updateUser(UserUpdateRequestDto userUpdateRequestDto, String encodedPassword) {
		if (userUpdateRequestDto.getName() != null) {
			this.name = userUpdateRequestDto.getName();
		}

		if (userUpdateRequestDto.getEmail() != null) {
			this.email = userUpdateRequestDto.getEmail();
		}

		if (encodedPassword != null) {
			this.password = encodedPassword;
		}

		if (userUpdateRequestDto.getAddress() != null) {
			this.address = userUpdateRequestDto.getAddress();
		}
	}

	public void softDelete() {
		this.deletedAt = LocalDateTime.now();
	}
}
