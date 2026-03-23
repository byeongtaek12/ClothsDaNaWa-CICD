package com.example.clothsdanawa.auth.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import com.example.clothsdanawa.auth.dto.AuthSignUpRequestDto;
import com.example.clothsdanawa.auth.dto.AuthSignUpResponseDto;
import com.example.clothsdanawa.user.entity.User;
import com.example.clothsdanawa.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@InjectMocks
	private AuthService authService;

	@Test
	@DisplayName("정상적으로 토큰없이 반환되는지 확인")
	void signup() {

		//given
		AuthSignUpRequestDto req = new AuthSignUpRequestDto(
			"오병택",
			"byeongtaek12@gmail.com",
			"1234",
			"test주소",
			"user"
		);

		String encoded = "encodedPassword";

		User user = User.of(req, encoded);
		ReflectionTestUtils.setField(user, "userId", 1L);

		given(userRepository.existsByEmail(req.getEmail())).willReturn(false);
		given(passwordEncoder.encode(req.getPassword())).willReturn(encoded);
		given(userRepository.save(any(User.class))).willReturn(user);

		//when
		AuthSignUpResponseDto response = authService.signup(req);

		//then
		assertThat(response.getId()).isEqualTo(1L);

		verify(userRepository).existsByEmail(req.getEmail());
		verify(passwordEncoder).encode(req.getPassword());
		verify(userRepository).save(any(User.class));
	}
}