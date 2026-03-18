package com.example.clothsdanawa.auth.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.clothsdanawa.auth.dto.AuthLoginRequestDto;
import com.example.clothsdanawa.auth.dto.AuthResponseDto;
import com.example.clothsdanawa.auth.dto.AuthSignUpRequestDto;
import com.example.clothsdanawa.common.exception.BaseException;
import com.example.clothsdanawa.common.exception.ErrorCode;
import com.example.clothsdanawa.common.jwt.JwtUtil;
import com.example.clothsdanawa.user.entity.User;
import com.example.clothsdanawa.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;

	public AuthResponseDto signup(AuthSignUpRequestDto authSignUpRequestDto) {

		if (userRepository.existsByEmail(authSignUpRequestDto.getEmail())) {
			throw new BaseException(ErrorCode.CONFLICT_EMAIL);
		}

		String encodedPassword = passwordEncoder.encode(authSignUpRequestDto.getPassword());

		User user = User.of(authSignUpRequestDto, encodedPassword);

		User savedUser = userRepository.save(user);

		return AuthResponseDto.from(savedUser);
	}

	public AuthResponseDto login(AuthLoginRequestDto authLoginRequestDto) {

		User findedUser = userRepository.findByEmailAndDeletedAtIsNullOrElseThrow(authLoginRequestDto.getEmail());
		if (!passwordEncoder.matches(authLoginRequestDto.getPassword(), findedUser.getPassword())) {
			throw new BaseException(ErrorCode.BAD_REQUEST_PASSWORD);
		}

		String jwtToken = jwtUtil.createToken(findedUser.getUserId(), findedUser.getName(), findedUser.getEmail(),
			findedUser.getUserRole());

		return AuthResponseDto.of(findedUser, jwtToken);
	}
}
