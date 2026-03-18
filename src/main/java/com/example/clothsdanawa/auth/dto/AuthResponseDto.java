package com.example.clothsdanawa.auth.dto;

import com.example.clothsdanawa.user.entity.User;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthResponseDto {
	private Long id;
	private String jwtToken;

	public static AuthResponseDto of(User user, String jwtToken) {
		return AuthResponseDto.builder()
			.id(user.getUserId())
			.jwtToken(jwtToken)
			.build();
	}

	public static AuthResponseDto from(User user) {
		return AuthResponseDto.builder()
			.id(user.getUserId())
			.build();
	}
}
