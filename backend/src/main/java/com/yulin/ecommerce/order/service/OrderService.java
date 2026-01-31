package com.yulin.ecommerce.order.service;

import com.yulin.ecommerce.order.entity.Order;
import com.yulin.ecommerce.cart.entity.Cart;

import java.util.List;

public interface OrderService {
    Order createOrderFromCart(Long userId, Cart cart, String orderType);
    List<Order> getOrdersByUserId(Long userId);
    org.springframework.data.domain.Page<Order> getOrdersByUserId(Long userId, Integer page, Integer pageSize);
    Order getOrderById(Long id);
    Order payOrder(Long id);
    void deleteOrder(Long id);
    
    // 获取所有订单
    List<Order> getAllOrders();
    
    // 统计相关方法
    double getTotalSales();
    int getTotalOrders();
    int getTotalProducts();
    List<Object[]> getTopSellingProducts(int limit);
}