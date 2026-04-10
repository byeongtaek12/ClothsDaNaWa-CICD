package com.example.clothsdanawa.product.controller;

import com.example.clothsdanawa.product.dto.request.ProductCreateRequest;
import com.example.clothsdanawa.product.dto.request.ProductStockRequest;
import com.example.clothsdanawa.product.dto.request.ProductUpdateRequest;
import com.example.clothsdanawa.product.dto.response.ProductCreateResponse;
import com.example.clothsdanawa.product.dto.response.ProductResponse;
import com.example.clothsdanawa.product.dto.response.ProductSliceResponse;
import com.example.clothsdanawa.product.dto.response.ProductStockResponse;
import com.example.clothsdanawa.product.dto.response.ProductUpdateResponse;
import com.example.clothsdanawa.product.entity.Product;
import com.example.clothsdanawa.product.service.ProductService;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 상품 API controller 클래스
 * 상품 등록, 수정, 삭제, 조회, 재고 증감 관련 HTTP 요청을 처리한다.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/product")
@Validated
public class ProductController {

	private final ProductService productService;

	/**
	 * 상품 등록 API
	 *
	 * @param request 상품 등록 요청 DTO
	 * @return 생성된 상품 ID를 포함한 응답 DTO
	 */
	@PostMapping
	public ProductCreateResponse createProduct(@RequestBody ProductCreateRequest request) {
		Long productId = productService.createProduct(
			request.getStoreId(),
			request.getProductName(),
			request.getPrice(),
			request.getStock()
		);
		return new ProductCreateResponse(productId);
	}

	/**
	 * 상품 수정 API
	 *
	 * @param productId 수정할 상품 ID
	 * @param request 수정 요청 DTO
	 * @return 수정 완료 메시지를 포함한 응답 DTO
	 */
	@PutMapping("/{productId}")
	public ProductUpdateResponse updateProduct(@PathVariable Long productId,
		@RequestBody ProductUpdateRequest request) {
		productService.updateProduct(
			productId,
			request.getProductName(),
			request.getPrice(),
			request.getStock()
		);
		return new ProductUpdateResponse("상품이 수정되었습니다.");
	}

	/**
	 * 상품 삭제 API (Soft Delete)
	 *
	 * @param productId 삭제할 상품 ID
	 * @return 삭제 완료 메시지를 포함한 응답 DTO
	 */
	@DeleteMapping("/{productId}")
	public ProductUpdateResponse deleteProduct(@PathVariable Long productId) {
		productService.deleteProduct(productId);
		return new ProductUpdateResponse("상품이 삭제되었습니다.");
	}

	/**
	 * 전체 상품 조회 API
	 *
	 * @return 전체 상품 목록 (Soft Delete 제외)
	 */
	@GetMapping
	public List<ProductResponse> getAllProducts() {
		return productService.findAllProducts().stream()
			.map(ProductResponse::new)
			.collect(Collectors.toList());
	}

	/**
	 * 상품 단건 조회 API
	 *
	 * @param productId 조회할 상품 ID
	 * @return 해당 상품 정보
	 */
	@GetMapping("/{productId}")
	public ProductResponse getProduct(@PathVariable Long productId) {
		return productService.findProductById(productId);
	}

	/**
	 * 상품 재고 변경 API (증가 / 감소)
	 *
	 * @param productId 상품 ID
	 * @param request 재고 변경 요청 DTO (quantity, type)
	 * @return 변경 후 상품 상태
	 */
	@PatchMapping("/{productId}/stock")
	public ProductStockResponse updateStock(@PathVariable Long productId,
		@RequestBody ProductStockRequest request) {
		Product updatedProduct = productService.updateStock(productId, request);
		return new ProductStockResponse(updatedProduct);
	}

	@GetMapping("/search")
	public ProductSliceResponse<ProductResponse> getProductByKeyword(
		@RequestParam @NotBlank String keyword,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size
	) {
		return productService.getProductByKeyword(keyword, page, size);
	}
}

