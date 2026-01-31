package com.yulin.ecommerce.order.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
public class Order implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private Long userId;
    private String orderType; // 订单类型：user（用户订单）或 seller（商家订单）
    private Date createTime;
    private String status;
    private BigDecimal totalPrice;
    private List<OrderItem> items = new ArrayList<>();
    
    public Order(Long userId, BigDecimal totalPrice, String orderType) {
        this.userId = userId;
        this.orderType = orderType;
        this.createTime = new Date();
        this.status = "待付款";
        this.totalPrice = totalPrice;
    }
}