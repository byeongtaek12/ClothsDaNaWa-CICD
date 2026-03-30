package com.example.clothsdanawa.init;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.clothsdanawa.auth.dto.AuthSignUpRequestDto;
import com.example.clothsdanawa.product.entity.Product;
import com.example.clothsdanawa.product.repository.ProductRepository;
import com.example.clothsdanawa.store.dto.request.StoreCreateRequestDto;
import com.example.clothsdanawa.store.entity.Store;
import com.example.clothsdanawa.store.repository.StoreRepository;
import com.example.clothsdanawa.user.entity.User;
import com.example.clothsdanawa.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

	private final UserRepository userRepository;
	private final StoreRepository storeRepository;
	private final ProductRepository productRepository;
	private final PasswordEncoder passwordEncoder;

	private static final int USER_COUNT = 30;
	private static final int STORE_COUNT = 30;
	private static final int PRODUCT_COUNT = 10_000;

	private final Random random = new Random();

	@Override
	@Transactional
	public void run(String... args) {
		if (productRepository.count() > 0 || storeRepository.count() > 0 || userRepository.count() > 0) {
			System.out.println("더미 데이터가 이미 존재해서 생성을 건너뜁니다.");
			return;
		}

		System.out.println("검색 테스트용 더미 데이터 생성을 시작합니다.");

		List<User> users = createUsers();
		userRepository.saveAll(users);

		List<Store> stores = createStores(users);
		storeRepository.saveAll(stores);

		saveProductsInBatch(stores);

		System.out.println("검색 테스트용 더미 데이터 생성 완료");
		System.out.println("유저: " + USER_COUNT);
		System.out.println("가게: " + STORE_COUNT);
		System.out.println("상품: " + PRODUCT_COUNT);
	}

	private List<User> createUsers() {
		List<User> users = new ArrayList<>();

		for (int i = 1; i <= USER_COUNT; i++) {
			AuthSignUpRequestDto req = new AuthSignUpRequestDto(
				"name" + i,
				"email" + i  + "@"+"gmail.com",
				"1234",
				"서울",
				"owner"
				);
			User user = User.of(req, "encodedPassword");
			users.add(user);
		}

		return users;
	}

	private List<Store> createStores(List<User> users) {
		List<Store> stores = new ArrayList<>();

		String[] concepts = {
			"빈티지", "캐주얼", "스트릿", "미니멀", "데일리",
			"남성", "여성", "오버핏", "클래식", "모던"
		};

		String[] suffixes = {
			"하우스", "샵", "스토어", "마켓", "웨어",
			"셀렉트", "클로젯", "부티크", "존", "랩"
		};

		for (int i = 0; i < STORE_COUNT; i++) {
			User owner = users.get(i % users.size());

			String concept = concepts[i % concepts.length];
			String suffix = suffixes[i % suffixes.length];
			String storeName = concept + suffix + (i + 1);
			String storeNumber =  "010" + random.nextInt(10) + random.nextInt(10)
				+ random.nextInt(10) + random.nextInt(10) + random.nextInt(10)
				+ random.nextInt(10) + random.nextInt(10) + random.nextInt(10);

			StoreCreateRequestDto req = new StoreCreateRequestDto(
				storeName,
				storeNumber,
				"서울"
			);

			Store store = Store.of(req);
			store.approveStore();
			store.setUser(owner);

			stores.add(store);
		}

		return stores;
	}

	private void saveProductsInBatch(List<Store> stores) {
		List<Product> batch = new ArrayList<>();

		String[] concepts = {
			"빈티지", "캐주얼", "스트릿", "미니멀", "데일리",
			"남성", "여성", "오버핏", "클래식", "모던"
		};

		String[] colors = {
			"블랙", "화이트", "네이비", "그레이", "베이지",
			"카키", "브라운", "아이보리"
		};

		String[] fits = {
			"오버핏", "슬림핏", "루즈핏", "기본핏", "와이드핏"
		};

		String[] categories = {
			"반팔티", "셔츠", "후드집업", "맨투맨", "청바지",
			"슬랙스", "니트", "자켓", "코트", "가디건"
		};

		String[] seasons = {
			"봄", "여름", "가을", "겨울"
		};

		for (int i = 1; i <= PRODUCT_COUNT; i++) {
			Store store = stores.get(random.nextInt(stores.size()));

			String concept = concepts[random.nextInt(concepts.length)];
			String color = colors[random.nextInt(colors.length)];
			String fit = fits[random.nextInt(fits.length)];
			String category = categories[random.nextInt(categories.length)];
			String season = seasons[random.nextInt(seasons.length)];

			String productName = season + " " + concept + " " + color + " " + fit + " " + category + " " + i;

			int price = 10000 + random.nextInt(90000);

			Product product = new Product(store, productName, price, 9999);

			batch.add(product);

			if (batch.size() == 1000) {
				productRepository.saveAll(batch);
				batch.clear();
				System.out.println(i + "개 상품 저장 완료");
			}
		}

		if (!batch.isEmpty()) {
			productRepository.saveAll(batch);
		}
	}
}
