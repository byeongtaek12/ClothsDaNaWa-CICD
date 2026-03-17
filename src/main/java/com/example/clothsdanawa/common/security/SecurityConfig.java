package com.example.clothsdanawa.common.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.clothsdanawa.common.jwt.JwtFilter;
import com.example.clothsdanawa.common.jwt.JwtUtil;
import com.example.clothsdanawa.common.oauth.CustomFailureHandler;
import com.example.clothsdanawa.common.oauth.CustomOAuthUserService;
import com.example.clothsdanawa.common.oauth.CustomSuccessHandler;

import jakarta.servlet.Filter;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtUtil jwtUtil;
	private final CustomUserDetailsService userDetailsService;
	private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
	private final CustomAccessDeniedHandler customAccessDeniedHandler;
	private final CustomOAuthUserService customOAuthUserService;
	private final CustomSuccessHandler customSuccessHandler;
	private final CustomFailureHandler customFailureHandler;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf(AbstractHttpConfigurer::disable) // csrf 기능 비활성화
			.sessionManagement(sm ->
				sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 비활성화
			.authorizeHttpRequests(auth -> auth
				.requestMatchers("/auth/**", "/oauth2/authorization/**",
					"/login/oauth2/code/**", "/css/**", "/js/**", "/publicPage/**").permitAll()  // WHITE_LIST 느낌
				.requestMatchers(HttpMethod.GET, "/stores/**").permitAll()
				.requestMatchers(HttpMethod.GET, "/users/**").permitAll()
				.requestMatchers("/admin/**").hasRole("ADMIN")
				.requestMatchers("/stores/**").hasRole("OWNER")
				.anyRequest().authenticated()) // 나머지는 인증 필요
			.anonymous(AbstractHttpConfigurer::disable) // 익명 객체 생성 비활성화
			.oauth2Login(oauth -> oauth.userInfoEndpoint(userInfo ->
					userInfo.userService(customOAuthUserService))
				.successHandler(customSuccessHandler)
				.failureHandler(customFailureHandler))
			.addFilterBefore(jwtFilter(), UsernamePasswordAuthenticationFilter.class)
			.exceptionHandling((ex -> ex
				.authenticationEntryPoint(customAuthenticationEntryPoint)
				.accessDeniedHandler(customAccessDeniedHandler)));
		return http.build();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
		return configuration.getAuthenticationManager();
	}

	@Bean
	public Filter jwtFilter() {
		return new JwtFilter(jwtUtil, userDetailsService);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}

