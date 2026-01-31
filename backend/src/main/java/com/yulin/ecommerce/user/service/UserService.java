package com.yulin.ecommerce.user.service;

import com.yulin.ecommerce.user.entity.User;
import java.util.Optional;

public interface UserService {
    
    User register(User user);
    
    User login(String username, String password, String role);
    
    Optional<User> getByUsername(String username);
    
    User updateUserInfo(Long userId, User user);
    
    User changePassword(Long userId, String oldPassword, String newPassword);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    
    User updateUserRole(Long userId, String role);
}