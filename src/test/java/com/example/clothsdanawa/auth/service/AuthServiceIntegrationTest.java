package com.example.clothsdanawa.auth.service;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.example.clothsdanawa.MySQLContainerBaseTest;
import com.example.clothsdanawa.auth.dto.AuthSignUpRequestDto;
import com.example.clothsdanawa.user.repository.UserRepository;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class AuthServiceIntegrationTest extends MySQLContainerBaseTest {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private AuthService authService;

	@Test
	@DisplayName("같은 이메일 동시 접근 시 예외가 발생")
	void signup_fail_email_conflict() throws Exception {
		int threadCount = 5;
		ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
		CountDownLatch readyLatch = new CountDownLatch(threadCount);
		CountDownLatch startLatch = new CountDownLatch(1);
		CountDownLatch doneLatch = new CountDownLatch(threadCount);

		AtomicInteger successCount = new AtomicInteger();
		List<Throwable> exceptions = Collections.synchronizedList(new ArrayList<>());

		String sameEmail = "test1234@gmail.com";

		for (int i = 0; i < threadCount; i++) {
			int index = i;
			executorService.submit(() -> {
				readyLatch.countDown();
				try {
					startLatch.await();

					AuthSignUpRequestDto req =  new AuthSignUpRequestDto(
						"obt"+ index,
						sameEmail,
						"1234",
						"test주소",
						"user"
					);

					authService.signup(req);
					successCount.incrementAndGet();
				} catch (Throwable e) {
					exceptions.add(e);
				} finally {
					doneLatch.countDown();
				}
			});
		}

		readyLatch.await();
		startLatch.countDown();
		doneLatch.await();

		long userCount = userRepository.count();

		assertThat(successCount.get()).isEqualTo(1);
		assertThat(userCount).isEqualTo(1L);
		assertThat(exceptions).hasSize(threadCount - 1);

		executorService.shutdown();
	}


}
