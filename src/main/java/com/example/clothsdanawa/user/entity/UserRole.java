package com.example.clothsdanawa.user.entity;

import com.example.clothsdanawa.common.exception.BaseException;
import com.example.clothsdanawa.common.exception.ErrorCode;

public enum UserRole {
	USER, OWNER, ADMIN;

	public static UserRole from(String userRole) {
		if (userRole == null || userRole.isBlank()) {
			throw new BaseException(ErrorCode.BAD_REQUEST_USER_ROLE);
		}

		String normalized = userRole.trim();

		if ("ADMIN".equalsIgnoreCase(normalized)) {
			throw new BaseException(ErrorCode.BAD_REQUEST_ADMIN);
		}

		for (UserRole value : UserRole.values()) {
			if (value.name().equalsIgnoreCase(normalized)) {
				return value;
			}
		}
		throw new BaseException(ErrorCode.BAD_REQUEST_USER_ROLE);
	}
}
