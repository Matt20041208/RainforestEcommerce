package com.yulin.ecommerce.product.mapper;

import com.yulin.ecommerce.product.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface ProductMapper {
    Product findById(Long id);
    List<Product> findByEnabled(Boolean enabled);
    List<Product> findByCategoryId(Long categoryId);
    List<Product> findByPriceBetween(Double minPrice, Double maxPrice);
    List<Product> findByNameContaining(String name);
    List<Product> findTop10ByOrderBySalesDesc();
    int insert(Product product);
    int updateById(Product product);
    int deleteById(Long id);
}
