package com.yulin.ecommerce.cart.controller;

import com.yulin.ecommerce.cart.entity.Cart;
import com.yulin.ecommerce.cart.service.CartService;
import com.yulin.ecommerce.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    
    @Autowired
    private CartService cartService;
    
    @Autowired
    private OrderService orderService;
    
    /**
     * 获取购物车
     * @return 购物车信息
     */
    @GetMapping
    public ResponseEntity<Cart> getCart() {
        // 这里使用固定用户ID 1，实际项目中应该从用户认证中获取
        Long userId = 1L;
        Cart cart = cartService.getCart(userId);
        return ResponseEntity.ok(cart);
    }
    
    /**
     * 添加商品到购物车
     * @param requestBody 包含productId和quantity的请求体
     * @return 更新后的购物车
     */
    @PostMapping("/add")
    public ResponseEntity<Cart> addToCart(@RequestBody Map<String, Object> requestBody) {
        // 这里使用固定用户ID 1，实际项目中应该从用户认证中获取
        Long userId = 1L;
        
        // 解析请求参数
        Long productId = Long.valueOf(requestBody.get("productId").toString());
        Integer quantity = Integer.valueOf(requestBody.get("quantity").toString());
        
        // 添加商品到购物车
        Cart cart = cartService.addToCart(userId, productId, quantity);
        return new ResponseEntity<>(cart, HttpStatus.CREATED);
    }
    
    /**
     * 更新购物车商品数量
     * @param productId 商品ID
     * @param requestBody 包含quantity的请求体
     * @return 更新后的购物车
     */
    @PutMapping("/{productId}")
    public ResponseEntity<Cart> updateCartItem(
            @PathVariable Long productId, 
            @RequestBody Map<String, Object> requestBody) {
        // 这里使用固定用户ID 1，实际项目中应该从用户认证中获取
        Long userId = 1L;
        
        // 解析请求参数
        Integer quantity = Integer.valueOf(requestBody.get("quantity").toString());
        
        // 更新购物车商品数量
        Cart cart = cartService.updateCartItem(userId, productId, quantity);
        return ResponseEntity.ok(cart);
    }
    
    /**
     * 删除购物车商品
     * @param productId 商品ID
     * @return 更新后的购物车
     */
    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<Cart> removeCartItem(@PathVariable Long productId) {
        // 这里使用固定用户ID 1，实际项目中应该从用户认证中获取
        Long userId = 1L;
        
        // 删除购物车商品
        Cart cart = cartService.removeCartItem(userId, productId);
        return ResponseEntity.ok(cart);
    }
    
    /**
     * 清空购物车
     * @return 空购物车
     */
    @DeleteMapping("/clear")
    public ResponseEntity<Cart> clearCart() {
        // 这里使用固定用户ID 1，实际项目中应该从用户认证中获取
        Long userId = 1L;
        
        // 清空购物车
        Cart cart = cartService.clearCart(userId);
        return ResponseEntity.ok(cart);
    }
    
    /**
     * 购物车付款
     * @return 创建的订单
     */
    @PostMapping("/pay")
    public ResponseEntity<?> payCart(@RequestParam(required = false, defaultValue = "user") String orderType) {
        // 这里使用固定用户ID 1，实际项目中应该从用户认证中获取
        Long userId = 1L;
        
        try {
            // 获取购物车
            Cart cart = cartService.getCart(userId);
            
            // 从购物车创建订单
            com.yulin.ecommerce.order.entity.Order order = orderService.createOrderFromCart(userId, cart, orderType);
            
            // 清空购物车
            cartService.clearCart(userId);
            
            return ResponseEntity.ok(order);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
