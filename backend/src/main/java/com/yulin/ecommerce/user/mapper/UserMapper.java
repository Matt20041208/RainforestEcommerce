package com.yulin.ecommerce.user.mapper;

import com.yulin.ecommerce.user.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    User findByUsername(String username);
    User findByEmail(String email);
    User findById(Long id);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    int insert(User user);
    int updateById(User user);
    int deleteById(Long id);
}
