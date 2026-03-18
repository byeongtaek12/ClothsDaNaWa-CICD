package com.example.clothsdanawa.user.entity;

import com.example.clothsdanawa.common.exception.BaseException;
import com.example.clothsdanawa.common.exception.ErrorCode;

public enum UserRole {
	USER, OWNER, ADMIN;

	public static UserRole from(String userRole) {
		for (UserRole value : UserRole.values()) {
			if ("ADMIN".equalsIgnoreCase(userRole)) {
				throw new BaseException(ErrorCode.BAD_REQUEST_ADMIN);
			}

			if (String.valueOf(value).equals(userRole.toUpperCase())) {
				return UserRole.valueOf(userRole.toUpperCase());
			}
		}
		throw new BaseException(ErrorCode.BAD_REQUEST_USER_ROLE);
	}
}
