package com.yulin.ecommerce.user.service.impl;

import com.yulin.ecommerce.user.entity.User;
import com.yulin.ecommerce.user.mapper.UserMapper;
import com.yulin.ecommerce.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    
    @Override
    public User register(User user) {
        if (existsByUsername(user.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        if (existsByEmail(user.getEmail())) {
            throw new RuntimeException("邮箱已存在");
        }
        
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        System.out.println("Encoded password: " + encodedPassword);
        user.setPassword(encodedPassword);
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        if (user.getRole() == null) {
            user.setRole("user");
        }
        userMapper.insert(user);
        return user;
    }
    
    @Override
    public User login(String username, String password, String role) {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }
        
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }
        
        // 验证角色
        if (role != null && !role.equals(user.getRole())) {
            throw new RuntimeException("角色不匹配，请使用正确的登录入口");
        }
        
        return user;
    }
    
    @Override
    public Optional<User> getByUsername(String username) {
        User user = userMapper.findByUsername(username);
        return Optional.ofNullable(user);
    }
    
    @Override
    public User updateUserInfo(Long userId, User user) {
        // 这里应该通过 userId 查询用户，而不是通过 username
        // 假设 UserMapper 有 findById 方法
        User existingUser = userMapper.findById(userId);
        if (existingUser == null) {
            throw new RuntimeException("用户不存在");
        }
        
        existingUser.setEmail(user.getEmail());
        existingUser.setPhone(user.getPhone());
        existingUser.setAddress(user.getAddress());
        existingUser.setAvatar(user.getAvatar());
        existingUser.setGender(user.getGender());
        existingUser.setBirthday(user.getBirthday());
        existingUser.setUpdateTime(new Date());
        
        userMapper.updateById(existingUser);
        return existingUser;
    }
    
    @Override
    public User changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("原密码错误");
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdateTime(new Date());
        userMapper.updateById(user);
        return user;
    }
    
    @Override
    public boolean existsByUsername(String username) {
        return userMapper.findByUsername(username) != null;
    }
    
    @Override
    public boolean existsByEmail(String email) {
        return userMapper.findByEmail(email) != null;
    }
    
    @Override
    public User updateUserRole(Long userId, String role) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 验证角色有效性
        if (!"user".equals(role) && !"seller".equals(role)) {
            throw new RuntimeException("无效的角色");
        }
        
        user.setRole(role);
        user.setUpdateTime(new Date());
        userMapper.updateById(user);
        return user;
    }
}