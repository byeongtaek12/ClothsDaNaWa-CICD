package com.example.clothsdanawa.search;

import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.clothsdanawa.product.entity.Product;
import com.example.clothsdanawa.product.repository.ProductRepository;
import com.example.clothsdanawa.search.dto.SearchResponseDto;
import com.example.clothsdanawa.store.entity.Store;
import com.example.clothsdanawa.store.repository.StoreRepository;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SearchService {

	private final StoreRepository storeRepository;
	private final ProductRepository productRepository;
	private final EntityManagerFactory entityManagerFactory;

	public List<SearchResponseDto> searchAll(String keyword, Pageable pageRequest) {

		SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
		Statistics statistics = sessionFactory.getStatistics();

		statistics.clear();

		List<Store> stores = storeRepository.searchTop10StoreByKeywordOrderByIdDesc(keyword, pageRequest);
		List<Product> products = productRepository.searchTop10ProductByKeywordOrderByIdDesc(keyword, pageRequest);

		List<SearchResponseDto> result = stores.stream().map(SearchResponseDto::from).collect(Collectors.toList());
		List<SearchResponseDto> result2 = products.stream().map(SearchResponseDto::from).collect(Collectors.toList());

		result.addAll(result2);

		System.out.println("실행된 SQL 수 = " + statistics.getPrepareStatementCount());

		return result;
	}
}
