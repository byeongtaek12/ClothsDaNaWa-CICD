package com.example.clothsdanawa.search;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.clothsdanawa.product.entity.Product;
import com.example.clothsdanawa.product.repository.ProductRepository;
import com.example.clothsdanawa.search.dto.SearchResponseDto;
import com.example.clothsdanawa.store.entity.Store;
import com.example.clothsdanawa.store.repository.StoreRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SearchService {

	private final StoreRepository storeRepository;
	private final ProductRepository productRepository;

	public List<SearchResponseDto> searchAll(String keyword, Pageable pageRequest) {

		List<Store> stores = storeRepository.searchTop10StoreByKeywordOrderByIdDesc(keyword, pageRequest);
		List<Product> products = productRepository.searchTop10ProductByKeywordOrderByIdDesc(keyword, pageRequest);

		List<SearchResponseDto> result = stores.stream().map(SearchResponseDto::from).collect(Collectors.toList());
		List<SearchResponseDto> result2 = products.stream().map(SearchResponseDto::from).collect(Collectors.toList());

		result.addAll(result2);

		return result;
	}
}
