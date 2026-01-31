package com.yulin.ecommerce.product.mapper;

import com.yulin.ecommerce.product.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface CategoryMapper {
    Category findById(Long id);
    List<Category> findByEnabled(Boolean enabled);
    boolean existsByName(String name);
    int insert(Category category);
    int updateById(Category category);
    int deleteById(Long id);
}
