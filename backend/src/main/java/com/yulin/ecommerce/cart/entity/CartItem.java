package com.yulin.ecommerce.cart.entity;

import com.yulin.ecommerce.product.entity.Product;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class CartItem implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long productId;
    private String productName;
    private String productImage;
    private BigDecimal productPrice;
    private Integer quantity;
    private BigDecimal totalPrice;
    
    public CartItem(Product product, Integer quantity) {
        this.productId = product.getId();
        this.productName = product.getName();
        this.productImage = product.getImage();
        this.productPrice = product.getPrice();
        this.quantity = quantity;
        this.totalPrice = product.getPrice().multiply(new BigDecimal(quantity));
    }
    
    public void updateQuantity(Integer quantity) {
        this.quantity = quantity;
        this.totalPrice = productPrice.multiply(new BigDecimal(quantity));
    }
}
