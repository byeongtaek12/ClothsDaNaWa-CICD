// package com.example.clothsdanawa.store.service;
//
// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.Mockito.*;
//
// import org.junit.jupiter.api.Test;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.http.HttpStatus;
//
// import com.example.clothsdanawa.common.exception.BaseException;
// import com.example.clothsdanawa.store.entity.Store;
// import com.example.clothsdanawa.store.entity.StoreStatus;
// import com.example.clothsdanawa.store.repository.StoreRepository;
// import com.example.clothsdanawa.user.entity.User;
// import com.example.clothsdanawa.user.repository.UserRepository;
//
// @SpringBootTest
// class StoreServiceTest {
//
// 	@Mock
// 	private StoreRepository storeRepository;
//
// 	@Mock
// 	private UserRepository userRepository;
//
// 	@InjectMocks
// 	private StoreService storeService;
//
// 	@Test
// 	void closeStore_성공() {
// 		User user = mock(User.class);
// 		Store store = new Store(1L, "상호", StoreStatus.OPEN, "01033701620", "주소", user);
//
// 		when(userRepository.findByEmailAndDeletedAtIsNullOrElseThrow(any())).thenReturn(user);
//
// 		when(storeRepository.findByStoreIdOrElseThrow(any())).thenReturn(store);
//
// 		storeService.closeStore(1L, "email");
// 		assertEquals(StoreStatus.CLOSED, store.getStoreStatus());
// 	}
//
// 	@Test
// 	void closeStore_실패_스토어_주인이_아님() {
// 		User user1 = mock(User.class);
// 		User user2 = mock(User.class);
// 		Store store = new Store(1L, "상호", StoreStatus.OPEN, "01033701620", "주소", user1);
//
// 		when(userRepository.findByEmailAndDeletedAtIsNullOrElseThrow(any())).thenReturn(user2);
//
// 		when(storeRepository.findByStoreIdOrElseThrow(any())).thenReturn(store);
//
// 		BaseException exception = assertThrows(BaseException.class, () -> {
// 			storeService.closeStore(1L, "email");
// 		});
//
// 		assertEquals(HttpStatus.FORBIDDEN, exception.getErrorCode().getHttpStatus());
// 	}
// }