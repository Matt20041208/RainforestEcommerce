package com.yulin.ecommerce.cart.service;

import com.yulin.ecommerce.cart.entity.Cart;

public interface CartService {
    
    /**
     * 获取用户购物车
     * @param userId 用户ID
     * @return 购物车信息
     */
    Cart getCart(Long userId);
    
    /**
     * 添加商品到购物车
     * @param userId 用户ID
     * @param productId 商品ID
     * @param quantity 商品数量
     * @return 更新后的购物车
     */
    Cart addToCart(Long userId, Long productId, Integer quantity);
    
    /**
     * 更新购物车商品数量
     * @param userId 用户ID
     * @param productId 商品ID
     * @param quantity 商品数量
     * @return 更新后的购物车
     */
    Cart updateCartItem(Long userId, Long productId, Integer quantity);
    
    /**
     * 删除购物车商品
     * @param userId 用户ID
     * @param productId 商品ID
     * @return 更新后的购物车
     */
    Cart removeCartItem(Long userId, Long productId);
    
    /**
     * 清空购物车
     * @param userId 用户ID
     * @return 空购物车
     */
    Cart clearCart(Long userId);
}
