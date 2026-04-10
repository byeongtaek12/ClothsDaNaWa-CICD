package com.example.clothsdanawa.store.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import com.example.clothsdanawa.common.exception.BaseException;
import com.example.clothsdanawa.store.dto.response.StoreResponseDto;
import com.example.clothsdanawa.store.dto.response.StoreSliceResponse;
import com.example.clothsdanawa.store.entity.Store;
import com.example.clothsdanawa.store.entity.StoreStatus;
import com.example.clothsdanawa.store.repository.StoreRepository;
import com.example.clothsdanawa.user.entity.User;
import com.example.clothsdanawa.user.entity.UserRole;
import com.example.clothsdanawa.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class StoreServiceTest {

	@Mock
	private StoreRepository storeRepository;

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private StoreService storeService;

	@Test
	void closeStore_성공() {
		User user = mock(User.class);
		Store store = new Store(1L, "상호", StoreStatus.OPEN, "01033701620", "주소", user);

		when(userRepository.findByEmailAndDeletedAtIsNullOrElseThrow(any())).thenReturn(user);

		when(storeRepository.findByStoreIdOrElseThrow(any())).thenReturn(store);

		storeService.closeStore(1L, "email");
		assertEquals(StoreStatus.CLOSED, store.getStoreStatus());
	}

	@Test
	void closeStore_실패_스토어_주인이_아님() {
		User user1 = mock(User.class);
		User user2 = mock(User.class);
		Store store = new Store(1L, "상호", StoreStatus.OPEN, "01033701620", "주소", user1);

		when(userRepository.findByEmailAndDeletedAtIsNullOrElseThrow(any())).thenReturn(user2);

		when(storeRepository.findByStoreIdOrElseThrow(any())).thenReturn(store);

		BaseException exception = assertThrows(BaseException.class, () -> {
			storeService.closeStore(1L, "email");
		});

		assertEquals(HttpStatus.FORBIDDEN, exception.getErrorCode().getHttpStatus());
	}

	@Test
	void getStoreByKeyWord_SUCCESS() {
		//given
		String keyword = "빈티지";
		int page = 0;
		int size = 10;
		StoreStatus storeStatus = StoreStatus.OPEN;
		Pageable pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "storeId"));

		List<Store> stores = new ArrayList<>();

		User user = User.builder().name("문무겸비")
			.email("byeongtaek12@gmail.com")
			.password("1234")
			.address("서울 어딘가")
			.userRole(UserRole.USER)
			.build();

		ReflectionTestUtils.setField(user, "userId", 1L);

		for (int i = 1; i <= 10; i++) {
			Store store = Store.builder()
				.storeId((long)i)
				.company("빈티지" + i)
				.storeStatus(StoreStatus.OPEN)
				.storeNumber("010-1234-1234")
				.address("서울 어딘가")
				.user(user)
				.build();
			stores.add(store);
		}

		Slice<Store> storeSlice = new SliceImpl<>(stores, pageRequest, false);

		given(storeRepository.findByCompanyContainingAndStoreStatus(keyword, pageRequest, storeStatus)).willReturn(storeSlice);

		//when
		StoreSliceResponse<StoreResponseDto> response = storeService.getStoreByKeyword(keyword, page, size);

		//then
		assertThat(response.getContent())
			.extracting(StoreResponseDto::getCompany)
			.containsExactlyElementsOf(
				stores.stream()
					.map(Store::getCompany)
					.toList()
			);
		assertThat(response.getPage()).isEqualTo(0);
		assertThat(response.getSize()).isEqualTo(10);
		assertThat(response.isHasNext()).isEqualTo(false);
	}
}