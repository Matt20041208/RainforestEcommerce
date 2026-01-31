package com.yulin.ecommerce.order.controller;

import com.yulin.ecommerce.order.entity.Order;
import com.yulin.ecommerce.order.service.OrderService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/order")
public class OrderController {
    
    @Autowired
    private OrderService orderService;
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getOrdersByUserId(@PathVariable Long userId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize) {
        // 如果没有提供分页参数，返回列表
        if (page == null && pageSize == null) {
            List<Order> orders = orderService.getOrdersByUserId(userId);
            return ResponseEntity.ok(orders);
        } else {
            // 否则返回分页结果
            org.springframework.data.domain.Page<Order> orderPage = orderService.getOrdersByUserId(userId, page, pageSize);
            return ResponseEntity.ok(orderPage);
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        Order order = orderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }
    
    @PostMapping("/{id}/pay")
    public ResponseEntity<Order> payOrder(@PathVariable Long id) {
        Order order = orderService.payOrder(id);
        return ResponseEntity.ok(order);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }
    
    // 统计相关接口
    @GetMapping("/statistics/sales")
    public ResponseEntity<Double> getTotalSales() {
        double totalSales = orderService.getTotalSales();
        return ResponseEntity.ok(totalSales);
    }
    
    @GetMapping("/statistics/orders")
    public ResponseEntity<Integer> getTotalOrders() {
        int totalOrders = orderService.getTotalOrders();
        return ResponseEntity.ok(totalOrders);
    }
    
    @GetMapping("/statistics/top-products")
    public ResponseEntity<List<Object[]>> getTopSellingProducts(@RequestParam(defaultValue = "5") int limit) {
        List<Object[]> topProducts = orderService.getTopSellingProducts(limit);
        return ResponseEntity.ok(topProducts);
    }
    
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalSales", orderService.getTotalSales());
        statistics.put("totalOrders", orderService.getTotalOrders());
        statistics.put("totalProducts", orderService.getTotalProducts());
        statistics.put("topSellingProducts", orderService.getTopSellingProducts(5));
        return ResponseEntity.ok(statistics);
    }
}