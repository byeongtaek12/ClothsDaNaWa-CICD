package com.example.clothsdanawa.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthSignUpRequestDto {
	@NotBlank(message = "이름은 필수입니다")
	private String name;
	@Email
	@NotBlank(message = "이메일은 필수입니다")
	private String email;
	@NotBlank(message = "패스워드는 필수입니다")
	private String password;
	@NotBlank(message = "주소는 필수입니다")
	private String address;
	@NotBlank(message = "역할은 필수입니다")
	private String userRole;
}
