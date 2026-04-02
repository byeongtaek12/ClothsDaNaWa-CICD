package com.example.clothsdanawa.product.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.clothsdanawa.product.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

	@Query("SELECT p FROM Product p Join fetch p.store WHERE p.productName LIKE %:keyword% AND p.deletedAt IS NULL")
	List<Product> searchProductByKeyword(String keyword);
}
