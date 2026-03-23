package com.example.clothsdanawa.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.clothsdanawa.auth.dto.AuthLoginRequestDto;
import com.example.clothsdanawa.auth.dto.AuthLoginResponseDto;
import com.example.clothsdanawa.auth.dto.AuthSignUpRequestDto;
import com.example.clothsdanawa.auth.dto.AuthSignUpResponseDto;
import com.example.clothsdanawa.auth.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

	private final AuthService authService;

	@PostMapping("/signup")
	public ResponseEntity<AuthSignUpResponseDto> signup(@Valid @RequestBody AuthSignUpRequestDto authSignUpRequestDto) {

		return ResponseEntity.status(201).body(authService.signup(authSignUpRequestDto));

	}

	@PostMapping("/login")
	public ResponseEntity<AuthLoginResponseDto> login(@RequestBody AuthLoginRequestDto authLoginRequestDto) {

		return ResponseEntity.status(200).body(authService.login(authLoginRequestDto));

	}

}
