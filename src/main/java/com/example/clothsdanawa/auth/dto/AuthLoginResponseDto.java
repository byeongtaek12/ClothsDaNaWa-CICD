package com.example.clothsdanawa.auth.dto;

import com.example.clothsdanawa.user.entity.User;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthLoginResponseDto {
	private Long id;
	private String jwtToken;

	public static AuthLoginResponseDto of(User user, String jwtToken) {
		return AuthLoginResponseDto.builder()
			.id(user.getUserId())
			.jwtToken(jwtToken)
			.build();
	}
}
