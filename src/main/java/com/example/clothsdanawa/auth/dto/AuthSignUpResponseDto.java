package com.example.clothsdanawa.auth.dto;

import com.example.clothsdanawa.user.entity.User;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthSignUpResponseDto {
	private Long id;

	public static AuthSignUpResponseDto from(User user) {
		return AuthSignUpResponseDto.builder()
			.id(user.getUserId())
			.build();
	}

	public static AuthSignUpResponseDto of(Long id) {
		return AuthSignUpResponseDto.builder()
			.id(id)
			.build();
	}
}
