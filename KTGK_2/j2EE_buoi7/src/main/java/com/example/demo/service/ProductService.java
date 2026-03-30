package com.example.demo.service;


import com.example.demo.model.Category;
import com.example.demo.model.Product;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // Phân trang và tìm kiếm sản phẩm
    public Page<Product> getProductsByPage(int page, String keyword) {
        Pageable pageable = PageRequest.of(page, 5);
        if (keyword == null || keyword.trim().isEmpty()) {
            return productRepository.findAll(pageable);
        }
        return productRepository.findByNameContainingIgnoreCase(keyword, pageable);
    }

    // Phân trang, lọc theo category, tìm kiếm và sắp xếp theo giá
    public Page<Product> getProductsByFilter(int page, String keyword, Integer categoryId, String sort) {
        Sort sortObj = Sort.unsorted();
        if ("asc".equalsIgnoreCase(sort)) {
            sortObj = Sort.by(Sort.Direction.ASC, "price");
        } else if ("desc".equalsIgnoreCase(sort)) {
            sortObj = Sort.by(Sort.Direction.DESC, "price");
        }
        Pageable pageable = PageRequest.of(page, 5, sortObj);
        if ((categoryId == null || categoryId == 0) && (keyword == null || keyword.trim().isEmpty())) {
            return productRepository.findAll(pageable);
        } else if (categoryId == null || categoryId == 0) {
            return productRepository.findByNameContainingIgnoreCase(keyword, pageable);
        } else if (keyword == null || keyword.trim().isEmpty()) {
            Category category = categoryRepository.findById(categoryId).orElse(null);
            if (category == null) return productRepository.findAll(pageable);
            return productRepository.findByCategory(category, pageable);
        } else {
            Category category = categoryRepository.findById(categoryId).orElse(null);
            if (category == null) return productRepository.findAll(pageable);
            return productRepository.findByCategoryAndNameContainingIgnoreCase(category, keyword, pageable);
        }
    }

    public void saveProduct(Product product) {
        productRepository.save(product);
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
}
