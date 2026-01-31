package com.yulin.ecommerce.product.service.impl;

import com.yulin.ecommerce.product.entity.Category;
import com.yulin.ecommerce.product.mapper.CategoryMapper;
import com.yulin.ecommerce.product.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {
    
    @Autowired
    private CategoryMapper categoryMapper;
    
    @Override
    public Category createCategory(Category category) {
        if (existsByName(category.getName())) {
            throw new RuntimeException("分类名称已存在");
        }
        category.setCreateTime(new Date());
        category.setUpdateTime(new Date());
        if (category.getEnabled() == null) {
            category.setEnabled(true);
        }
        categoryMapper.insert(category);
        return category;
    }
    
    @Override
    public Category updateCategory(Long id, Category category) {
        Category existingCategory = getCategoryById(id);
        if (!existingCategory.getName().equals(category.getName()) && existsByName(category.getName())) {
            throw new RuntimeException("分类名称已存在");
        }
        existingCategory.setName(category.getName());
        existingCategory.setDescription(category.getDescription());
        existingCategory.setSortOrder(category.getSortOrder());
        existingCategory.setEnabled(category.getEnabled());
        existingCategory.setUpdateTime(new Date());
        categoryMapper.updateById(existingCategory);
        return existingCategory;
    }
    
    @Override
    public void deleteCategory(Long id) {
        Category category = categoryMapper.findById(id);
        if (category == null) {
            throw new RuntimeException("分类不存在");
        }
        categoryMapper.deleteById(id);
    }
    
    @Override
    public Category getCategoryById(Long id) {
        Category category = categoryMapper.findById(id);
        if (category == null) {
            throw new RuntimeException("分类不存在");
        }
        return category;
    }
    
    @Override
    public List<Category> getAllCategories() {
        return categoryMapper.findByEnabled(null);
    }
    
    @Override
    public List<Category> getEnabledCategories() {
        return categoryMapper.findByEnabled(true);
    }
    
    @Override
    public boolean existsByName(String name) {
        return categoryMapper.existsByName(name);
    }
}