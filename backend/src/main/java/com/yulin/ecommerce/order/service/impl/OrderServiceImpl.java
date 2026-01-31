package com.yulin.ecommerce.order.service.impl;

import com.yulin.ecommerce.order.entity.Order;
import com.yulin.ecommerce.order.entity.OrderItem;
import com.yulin.ecommerce.order.service.OrderService;
import com.yulin.ecommerce.cart.entity.Cart;
import com.yulin.ecommerce.cart.entity.CartItem;
import com.yulin.ecommerce.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.math.BigDecimal;

@Service
public class OrderServiceImpl implements OrderService {
    
    @Autowired
    private ProductService productService;
    
    // 模拟数据库存储订单
    private static final Map<Long, Order> orders = new HashMap<>();
    private static final AtomicLong orderIdGenerator = new AtomicLong(1);
    
    // 静态初始化块，添加一些测试订单
    static {
        // 添加测试订单1
        Order order1 = new Order(1L, BigDecimal.valueOf(199.99), "user");
        order1.setId(orderIdGenerator.getAndIncrement());
        order1.setCreateTime(new Date(System.currentTimeMillis() - 3600000)); // 1小时前
        order1.setStatus("已付款");
        
        // 添加订单项
        List<OrderItem> items1 = new ArrayList<>();
        OrderItem item1 = new OrderItem(1L, "测试商品1", "", BigDecimal.valueOf(99.99), 1);
        item1.setId(System.nanoTime());
        item1.setOrderId(order1.getId());
        items1.add(item1);
        
        OrderItem item2 = new OrderItem(2L, "测试商品2", "", BigDecimal.valueOf(100.00), 1);
        item2.setId(System.nanoTime() + 1);
        item2.setOrderId(order1.getId());
        items1.add(item2);
        
        order1.setItems(items1);
        orders.put(order1.getId(), order1);
        
        // 添加测试订单2
        Order order2 = new Order(1L, BigDecimal.valueOf(299.99), "user");
        order2.setId(orderIdGenerator.getAndIncrement());
        order2.setCreateTime(new Date(System.currentTimeMillis() - 7200000)); // 2小时前
        order2.setStatus("待付款");
        
        // 添加订单项
        List<OrderItem> items2 = new ArrayList<>();
        OrderItem item3 = new OrderItem(3L, "测试商品3", "", BigDecimal.valueOf(299.99), 1);
        item3.setId(System.nanoTime() + 2);
        item3.setOrderId(order2.getId());
        items2.add(item3);
        
        order2.setItems(items2);
        orders.put(order2.getId(), order2);
    }
    
    @Override
    public Order createOrderFromCart(Long userId, Cart cart, String orderType) {
        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("购物车为空，无法创建订单");
        }
        
        Order order = new Order(userId, cart.getTotalPrice(), orderType);
        order.setId(orderIdGenerator.getAndIncrement());
        
        // 将购物车项转换为订单项
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cart.getItems()) {
            OrderItem orderItem = new OrderItem(
                    cartItem.getProductId(),
                    cartItem.getProductName(),
                    cartItem.getProductImage(),
                    cartItem.getProductPrice(),
                    cartItem.getQuantity()
            );
            orderItem.setId(System.nanoTime()); // 使用纳秒时间作为临时ID
            orderItem.setOrderId(order.getId());
            orderItems.add(orderItem);
            
            // 更新商品库存和销量
            productService.updateStock(cartItem.getProductId(), -cartItem.getQuantity());
            productService.updateSales(cartItem.getProductId(), cartItem.getQuantity());
        }
        
        order.setItems(orderItems);
        orders.put(order.getId(), order);
        
        return order;
    }
    
    @Override
    public List<Order> getOrdersByUserId(Long userId) {
        List<Order> userOrders = new ArrayList<>();
        for (Order order : orders.values()) {
            if (order.getUserId().equals(userId) && ("user".equals(order.getOrderType()) || order.getOrderType() == null)) {
                userOrders.add(order);
            }
        }
        return userOrders;
    }
    
    @Override
    public org.springframework.data.domain.Page<Order> getOrdersByUserId(Long userId, Integer page, Integer pageSize) {
        // 获取用户的所有订单
        List<Order> userOrders = getOrdersByUserId(userId);
        
        // 处理分页参数
        if (page == null || page < 1) {
            page = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }
        
        // 计算总页数
        int totalItems = userOrders.size();
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);
        
        // 计算起始和结束索引
        int startIndex = (page - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, totalItems);
        
        // 获取当前页的订单
        List<Order> paginatedOrders = userOrders.subList(startIndex, endIndex);
        
        // 创建Page对象
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page - 1, pageSize);
        return new org.springframework.data.domain.PageImpl<>(paginatedOrders, pageable, totalItems);
    }
    
    @Override
    public Order getOrderById(Long id) {
        Order order = orders.get(id);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        return order;
    }
    
    @Override
    public Order payOrder(Long id) {
        Order order = getOrderById(id);
        if ("已付款".equals(order.getStatus())) {
            throw new RuntimeException("订单已付款，无需重复付款");
        }
        
        // 模拟付款过程
        order.setStatus("已付款");
        orders.put(id, order);
        
        return order;
    }
    
    @Override
    public void deleteOrder(Long id) {
        Order order = getOrderById(id);
        if (order != null) {
            orders.remove(id);
        }
    }

    @Override
    public List<Order> getAllOrders() {
        return new ArrayList<>(orders.values());
    }

    @Override
    public double getTotalSales() {
        BigDecimal totalSales = BigDecimal.ZERO;
        for (Order order : orders.values()) {
            // 只计算已付款的订单
            if ("已付款".equals(order.getStatus())) {
                totalSales = totalSales.add(order.getTotalPrice());
            }
        }
        return totalSales.doubleValue();
    }

    @Override
    public int getTotalOrders() {
        return orders.size();
    }

    @Override
    public int getTotalProducts() {
        int totalProducts = 0;
        for (Order order : orders.values()) {
            if ("已付款".equals(order.getStatus())) {
                for (OrderItem item : order.getItems()) {
                    totalProducts += item.getQuantity();
                }
            }
        }
        return totalProducts;
    }

    @Override
    public List<Object[]> getTopSellingProducts(int limit) {
        // 统计每个商品的销量
        Map<Long, Integer> productSales = new HashMap<>();
        Map<Long, String> productNames = new HashMap<>();
        
        for (Order order : orders.values()) {
            if ("已付款".equals(order.getStatus())) {
                for (OrderItem item : order.getItems()) {
                    Long productId = item.getProductId();
                    int quantity = item.getQuantity();
                    
                    // 累加销量
                    productSales.put(productId, productSales.getOrDefault(productId, 0) + quantity);
                    // 保存商品名称
                    productNames.put(productId, item.getProductName());
                }
            }
        }
        
        // 将商品销量转换为List并按销量排序
        List<Object[]> topProducts = new ArrayList<>();
        productSales.entrySet().stream()
            .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
            .limit(limit)
            .forEach(entry -> {
                Object[] productInfo = new Object[2];
                productInfo[0] = productNames.get(entry.getKey());
                productInfo[1] = entry.getValue();
                topProducts.add(productInfo);
            });
        
        return topProducts;
    }
}