package com.example.demo.repository;

import com.example.demo.model.Product;
import com.example.demo.model.Category;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
	// Tìm kiếm sản phẩm theo tên chứa keyword (không phân biệt hoa thường)
	List<Product> findByNameContainingIgnoreCase(String keyword);

	// Phân trang và tìm kiếm
	Page<Product> findByNameContainingIgnoreCase(String keyword, Pageable pageable);
	Page<Product> findAll(Pageable pageable);
	Page<Product> findByCategoryAndNameContainingIgnoreCase(Category category, String keyword, Pageable pageable);
	Page<Product> findByCategory(Category category, Pageable pageable);
}
