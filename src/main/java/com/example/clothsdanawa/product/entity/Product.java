package com.example.clothsdanawa.product.entity;

import com.example.clothsdanawa.common.exception.BaseException;
import com.example.clothsdanawa.common.exception.ErrorCode;
import com.example.clothsdanawa.store.entity.Store;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

/**
 * Product 엔티티 클래스
 * 상품 정보를 저장하고 재고를 변경할 때 낙관적 락(@Version)을 활용하여 동시성 문제를 방지
 * soft delete를 위해 deletedAt을 사용함
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE product SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
@Table(indexes = @Index(name = "idx_product_name", columnList = "product_name"))
public class Product {

	/**
	 * 상품 ID (PK)
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * 연관된 쇼핑몰 정보 (Store)
	 */
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "store_id")
	private Store store;

	/**
	 * 상품 이름
	 */
	@Column(nullable = false)
	private String productName;

	/**
	 * 상품 가격
	 */
	@Column(nullable = false)
	private int price;

	/**
	 * 상품 재고
	 */
	@Column(nullable = false)
	private int stock;

	/**
	 * 버전 필드 - 낙관적 락을 위한 필드
	 */
	@Version
	private Long version;

	/**
	 * 생성 시각
	 */
	private LocalDateTime createdAt;

	/**
	 * 수정 시각
	 */
	private LocalDateTime updatedAt;

	/**
	 * 삭제 시각
	 */
	private LocalDateTime deletedAt;

	/**
	 * 상품 생성자
	 * @param store 연관된 쇼핑몰
	 * @param productName 상품 이름
	 * @param price 상품 가격
	 * @param stock 초기 재고
	 */
	public Product(Store store, String productName, int price, int stock) {
		this.store = store;
		this.productName = productName;
		this.price = price;
		this.stock = stock;
		this.createdAt = LocalDateTime.now();
		this.updatedAt = LocalDateTime.now();
	}

	/**
	 * 상품 정보 수정
	 * @param productName 새로운 이름
	 * @param price 새로운 가격
	 * @param stock 새로운 재고
	 */
	public void update(String productName, int price, int stock) {
		this.productName = productName;
		this.price = price;
		this.stock = stock;
		this.updatedAt = LocalDateTime.now();
	}

	/**
	 * 재고 차감
	 * @param quantity 차감 수량
	 */
	public void decreaseStock(int quantity) {
		if (this.deletedAt != null) {
			throw new BaseException(ErrorCode.ALREADY_DELETED_PRODUCT);
		}
		if (quantity <= 0) {
			throw new BaseException(ErrorCode.INVALID_STOCK_OPERATION);
		}
		if (this.stock < quantity) {
			throw new BaseException(ErrorCode.OUT_OF_STOCK);
		}
		this.stock -= quantity;
		this.updatedAt = LocalDateTime.now();
	}

	/**
	 * 재고 증가
	 * @param quantity 추가할 수량
	 */
	public void increaseStock(int quantity) {
		if (this.deletedAt != null) {
			throw new BaseException(ErrorCode.ALREADY_DELETED_PRODUCT);
		}
		if (quantity <= 0) {
			throw new BaseException(ErrorCode.INVALID_STOCK_OPERATION);
		}
		this.stock += quantity;
		this.updatedAt = LocalDateTime.now();
	}

	/**
	 * soft delete 처리
	 */
	public void markAsDeleted() {
		this.deletedAt = LocalDateTime.now();
	}
}
