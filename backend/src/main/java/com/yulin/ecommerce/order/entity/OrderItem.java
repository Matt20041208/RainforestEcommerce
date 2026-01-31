package com.yulin.ecommerce.order.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class OrderItem implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private Long orderId;
    private Long productId;
    private String productName;
    private String productImage;
    private BigDecimal productPrice;
    private Integer quantity;
    private BigDecimal totalPrice;
    
    public OrderItem(Long productId, String productName, String productImage, BigDecimal productPrice, Integer quantity) {
        this.productId = productId;
        this.productName = productName;
        this.productImage = productImage;
        this.productPrice = productPrice;
        this.quantity = quantity;
        this.totalPrice = productPrice.multiply(new BigDecimal(quantity));
    }
}