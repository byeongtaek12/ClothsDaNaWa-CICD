package com.example.clothsdanawa.product.service;

import com.example.clothsdanawa.common.exception.BaseException;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.clothsdanawa.common.exception.ErrorCode;
import com.example.clothsdanawa.product.dto.request.ProductStockRequest;
import com.example.clothsdanawa.product.dto.response.ProductResponse;
import com.example.clothsdanawa.product.dto.response.ProductSliceResponse;
import com.example.clothsdanawa.product.entity.Product;
import com.example.clothsdanawa.product.enums.StockOperationType;
import com.example.clothsdanawa.product.repository.ProductRepository;
import com.example.clothsdanawa.store.entity.Store;
import com.example.clothsdanawa.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 상품 관련 비즈니스 로직을 담당하는 서비스 클래스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

	private final ProductRepository productRepository;
	private final StoreRepository storeRepository;

	/**
	 * 상품 등록
	 */
	@Transactional
	public Long createProduct(Long storeId, String productName, int price, int stock) {
		Store store = storeRepository.findById(storeId)
			.orElseThrow(() -> new BaseException(ErrorCode.STORE_NOT_FOUND));
		Product product = new Product(store, productName, price, stock);
		return productRepository.save(product).getId();
	}

	/**
	 * 상품 단건 조회 (내부용)
	 */
	private Product getProductEntityById(Long productId) {
		return productRepository.findById(productId)
			.orElseThrow(() -> new BaseException(ErrorCode.PRODUCT_NOT_FOUND));
	}

	/**
	 * 상품 수정
	 */
	@Transactional
	public void updateProduct(Long productId, String productName, int price, int stock) {
		Product product = getProductEntityById(productId);
		product.update(productName, price, stock);
	}

	/**
	 * 상품 삭제 (soft delete)
	 */
	@Transactional
	public void deleteProduct(Long productId) {
		Product product = getProductEntityById(productId);
		product.markAsDeleted();
	}

	/**
	 * 전체 상품 조회
	 */
	@Transactional(readOnly = true)
	public List<Product> findAllProducts() {
		return productRepository.findAll(); // soft delete 제외됨
	}

	/**
	 * 상품 재고 변경
	 * 재고 변경 타입(INCREASE 또는 DECREASE)에 따라 수량을 증감시킵니다.
	 * 낙관적 락(@Version) 기반
	 *
	 * @param productId 재고를 변경할 상품의 ID
	 * @param request 재고 변경 요청 DTO (quantity, type)
	 * @return 변경된 상품 엔티티
	 * @throws BaseException PRODUCT_NOT_FOUND: 해당 상품이 존재하지 않을 경우
	 * @throws BaseException OUT_OF_STOCK: 재고 수량이 부족한 경우 (DECREASE 시)
	 * @throws BaseException INVALID_STOCK_OPERATION: 수량이 0 이하이거나 잘못된 타입인 경우
	 */
	@Transactional
	public Product updateStock(Long productId, ProductStockRequest request) {
		Product product = getProductEntityById(productId);
		int quantity = request.getQuantity();

		if (request.getType() == StockOperationType.INCREASE) {
			product.increaseStock(quantity);
		} else if (request.getType() == StockOperationType.DECREASE) {
			product.decreaseStock(quantity);
		} else {
			throw new BaseException(ErrorCode.INVALID_STOCK_OPERATION);
		}

		return product;
	}

	/**
	 * 상품 단건 조회 (DTO 변환)
	 */
	public ProductResponse findProductById(Long productId) {
		Product product = productRepository.findById(productId)
			.orElseThrow(() -> new BaseException(ErrorCode.PRODUCT_NOT_FOUND));
		return new ProductResponse(product);
	}

	public ProductSliceResponse<ProductResponse> getProductByKeyword(String keyword, int page, int size) {

		Pageable pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));

		Slice<Product> products = productRepository.findByProductNameContaining(keyword, pageRequest);

		List<ProductResponse> responseList = products.getContent().stream()
			.map(ProductResponse::new)
			.toList();

		return new ProductSliceResponse<>(responseList, products.getNumber(), products.getSize(), products.hasNext());
	}
}
