package com.yulin.ecommerce.product.service;

import com.yulin.ecommerce.product.entity.Product;

import java.math.BigDecimal;
import java.util.List;

public interface ProductService {
    
    Product createProduct(Product product);
    
    Product updateProduct(Long id, Product product);
    
    void deleteProduct(Long id);
    
    Product getProductById(Long id);
    
    List<Product> getAllProducts();
    
    List<Product> getEnabledProducts();
    
    List<Product> getProductsByCategory(Long categoryId);
    
    List<Product> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice);
    
    List<Product> searchProducts(String keyword);
    
    List<Product> getTopSellingProducts(Integer limit);
    
    // 支持分页和综合筛选的方法
    org.springframework.data.domain.Page<Product> findProductsByFilters(
            String keyword,
            Long categoryId,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Boolean enabled,
            String sortBy,
            Integer page,
            Integer pageSize);
    
    Product updateStock(Long id, Integer stockChange);
    
    Product updateSales(Long id, Integer salesChange);
    
    int getTotalProducts();
}