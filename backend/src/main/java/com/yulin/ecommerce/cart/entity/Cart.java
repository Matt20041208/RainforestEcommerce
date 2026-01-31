package com.yulin.ecommerce.cart.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Data
@NoArgsConstructor
public class Cart implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long userId;
    private List<CartItem> items = new ArrayList<>();
    private BigDecimal totalPrice = BigDecimal.ZERO;
    
    public Cart(Long userId) {
        this.userId = userId;
    }
    
    public void addItem(CartItem item) {
        Optional<CartItem> existingItem = items.stream()
                .filter(i -> i.getProductId().equals(item.getProductId()))
                .findFirst();
        
        if (existingItem.isPresent()) {
            CartItem cartItem = existingItem.get();
            cartItem.updateQuantity(cartItem.getQuantity() + item.getQuantity());
        } else {
            items.add(item);
        }
        
        updateTotalPrice();
    }
    
    public void updateItem(Long productId, Integer quantity) {
        Optional<CartItem> existingItem = items.stream()
                .filter(i -> i.getProductId().equals(productId))
                .findFirst();
        
        if (existingItem.isPresent()) {
            if (quantity <= 0) {
                removeItem(productId);
            } else {
                CartItem cartItem = existingItem.get();
                cartItem.updateQuantity(quantity);
                updateTotalPrice();
            }
        }
    }
    
    public void removeItem(Long productId) {
        items.removeIf(item -> item.getProductId().equals(productId));
        updateTotalPrice();
    }
    
    public void clear() {
        items.clear();
        totalPrice = BigDecimal.ZERO;
    }
    
    private void updateTotalPrice() {
        totalPrice = items.stream()
                .map(CartItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
