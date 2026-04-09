package com.example.clothsdanawa.product.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;
import org.springframework.test.util.ReflectionTestUtils;

import com.example.clothsdanawa.product.dto.response.ProductResponse;
import com.example.clothsdanawa.product.dto.response.ProductSliceResponse;
import com.example.clothsdanawa.product.entity.Product;
import com.example.clothsdanawa.product.repository.ProductRepository;
import com.example.clothsdanawa.store.entity.Store;
import com.example.clothsdanawa.store.entity.StoreStatus;
import com.example.clothsdanawa.user.entity.User;
import com.example.clothsdanawa.user.entity.UserRole;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

	@Mock
	private ProductRepository productRepository;

	@InjectMocks
	private ProductService productService;

	@Test
	@DisplayName("데이터가 잘 조회 되는지 확인")
	void getProductByKeyword() {
		//given
		String keyword = "바지";
		int page = 0;
		int size = 10;

		PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
		User user = User.builder().name("문무겸비")
			.email("byeongtaek12@gmail.com")
			.password("1234")
			.address("서울 어딘가")
			.userRole(UserRole.USER)
			.build();

		ReflectionTestUtils.setField(user, "userId", 1L);

		Store store = Store.builder().storeId(1L)
			.company("완벽그자체인가게")
			.storeStatus(StoreStatus.OPEN)
			.storeNumber("010-1234-1234")
			.address("서울 어딘가")
			.user(user)
			.build();
		List<Product> productList = new ArrayList<>();

		for (int i = 1; i <= 10; i++) {
			Product product = new Product(store, "바지" + i, i * 1000, i * 10);
			ReflectionTestUtils.setField(product, "id", (long)i);
			productList.add(product);
		}

		Slice<Product> productSlice = new SliceImpl<>(productList, pageRequest, false);

		given(productRepository.findByProductNameContaining(keyword, pageRequest)).willReturn(productSlice);

		//when
		ProductSliceResponse<ProductResponse> response = productService.getProductByKeyword(keyword, page, size);

		//then
		assertThat(response.getContent())
			.extracting(ProductResponse::getProductName)
			.containsExactlyElementsOf(
				productList.stream()
					.map(Product::getProductName)
					.toList()
			);
		assertThat(response.getPage()).isEqualTo(0);
		assertThat(response.getSize()).isEqualTo(10);
		assertThat(response.isHasNext()).isEqualTo(false);
	}
}