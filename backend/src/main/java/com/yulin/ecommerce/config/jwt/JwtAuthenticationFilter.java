package com.yulin.ecommerce.config.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            // 从请求头中提取令牌
            String token = extractTokenFromHeader(request);
            
            // 验证令牌
            if (token != null && jwtUtils.validateToken(token)) {
                // 从令牌中获取用户名和角色
                String username = jwtUtils.getUsernameFromToken(token);
                String role = jwtUtils.getRoleFromToken(token);
                
                // 创建UserDetails对象
                UserDetails userDetails = User.builder()
                        .username(username)
                        .password("") // 密码不需要，因为令牌已经验证过
                        .roles(role)
                        .build();
                
                // 创建认证对象
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                
                // 设置认证详情
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                // 将认证对象设置到SecurityContext中
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            // 令牌验证失败，不设置认证信息
            SecurityContextHolder.clearContext();
        }
        
        // 继续执行过滤器链
        filterChain.doFilter(request, response);
    }
    
    // 从请求头中提取令牌
    private String extractTokenFromHeader(HttpServletRequest request) {
        String header = request.getHeader(jwtUtils.getHeader());
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7); // 移除"Bearer "前缀
        }
        return null;
    }
}
