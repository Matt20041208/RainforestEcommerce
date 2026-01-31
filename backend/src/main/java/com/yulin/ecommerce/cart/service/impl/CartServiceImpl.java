package com.yulin.ecommerce.cart.service.impl;

import com.yulin.ecommerce.cart.entity.Cart;
import com.yulin.ecommerce.cart.entity.CartItem;
import com.yulin.ecommerce.cart.service.CartService;
import com.yulin.ecommerce.product.entity.Product;
import com.yulin.ecommerce.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Objects;

@Service
public class CartServiceImpl implements CartService {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Autowired
    private ProductService productService;
    
    private static final String CART_PREFIX = "cart:";
    private static final Duration CART_EXPIRE_TIME = Duration.ofDays(7);
    
    @Override
    public Cart getCart(Long userId) {
        String cartKey = CART_PREFIX + userId;
        Object cartObject = redisTemplate.opsForValue().get(cartKey);
        Cart cart = (cartObject instanceof Cart) ? (Cart) cartObject : null;
        if (cart == null) {
            cart = new Cart(userId);
            saveCart(cart);
        }
        return cart;
    }
    
    @Override
    public Cart addToCart(Long userId, Long productId, Integer quantity) {
        // 获取商品信息
        Product product = productService.getProductById(productId);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }
        
        // 创建购物车项
        CartItem item = new CartItem(product, quantity);
        
        // 获取购物车
        Cart cart = getCart(userId);
        
        // 添加商品到购物车
        cart.addItem(item);
        
        // 保存购物车到Redis
        saveCart(cart);
        
        return cart;
    }
    
    @Override
    public Cart updateCartItem(Long userId, Long productId, Integer quantity) {
        // 获取购物车
        Cart cart = getCart(userId);
        
        // 更新商品数量
        cart.updateItem(productId, quantity);
        
        // 保存购物车到Redis
        saveCart(cart);
        
        return cart;
    }
    
    @Override
    public Cart removeCartItem(Long userId, Long productId) {
        // 获取购物车
        Cart cart = getCart(userId);
        
        // 删除商品
        cart.removeItem(productId);
        
        // 保存购物车到Redis
        saveCart(cart);
        
        return cart;
    }
    
    @Override
    public Cart clearCart(Long userId) {
        // 创建空购物车
        Cart cart = new Cart(userId);
        
        // 保存空购物车到Redis
        saveCart(cart);
        
        return cart;
    }
    
    /**
     * 保存购物车到Redis
     * @param cart 购物车
     */
    private void saveCart(Cart cart) {
        String cartKey = CART_PREFIX + cart.getUserId();
        redisTemplate.opsForValue().set(cartKey, cart, CART_EXPIRE_TIME);
    }
}
