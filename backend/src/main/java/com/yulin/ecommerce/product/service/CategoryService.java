package com.yulin.ecommerce.product.service;

import com.yulin.ecommerce.product.entity.Category;

import java.util.List;

public interface CategoryService {
    
    Category createCategory(Category category);
    
    Category updateCategory(Long id, Category category);
    
    void deleteCategory(Long id);
    
    Category getCategoryById(Long id);
    
    List<Category> getAllCategories();
    
    List<Category> getEnabledCategories();
    
    boolean existsByName(String name);
}