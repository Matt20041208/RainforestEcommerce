<script setup>
import { ref, onMounted } from 'vue'
import { get, post, del } from '@/utils/request'

// 购物车数据
const cart = ref({
  userId: 1,
  items: [],
  totalPrice: 0
})

// 加载购物车数据
const loadCart = async () => {
  try {
    const data = await get('/api/cart')
    cart.value = data
  } catch (error) {
    console.error('获取购物车失败')
  }
}

// 删除购物车项
const removeItem = async (productId) => {
  try {
    await del(`/api/cart/remove/${productId}`)
    await loadCart()
  } catch (error) {
    console.error('删除商品失败')
  }
}

// 清空购物车
const clearCart = async () => {
  try {
    await del('/api/cart/clear')
    await loadCart()
  } catch (error) {
    console.error('清空购物车失败')
  }
}

// 创建订单功能
const createOrder = async () => {
  try {
    const order = await post('/api/cart/pay')
    alert(`订单创建成功，等待付款！订单号：${order.id}`)
    await loadCart() // 创建订单后重新加载购物车（此时应该为空）
  } catch (error) {
    console.error('创建订单失败:', error)
    alert(`创建订单失败：${error}`)
  }
}

// 组件挂载时加载购物车数据
onMounted(() => {
  loadCart()
})
</script>

<template>
  <div class="cart-container">
    <h2>我的购物车</h2>
    
    <div v-if="cart.items.length === 0" class="empty-cart">
      <p>购物车为空</p>
    </div>
    
    <div v-else class="cart-content">
      <table class="cart-table">
        <thead>
          <tr>
            <th>商品名称</th>
            <th>单价</th>
            <th>数量</th>
            <th>小计</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in cart.items" :key="item.productId">
            <td>{{ item.productName }}</td>
            <td>¥{{ item.productPrice.toFixed(2) }}</td>
            <td>{{ item.quantity }}</td>
            <td>¥{{ item.totalPrice.toFixed(2) }}</td>
            <td>
              <button @click="removeItem(item.productId)" class="remove-btn">删除</button>
            </td>
          </tr>
        </tbody>
      </table>
      
      <div class="cart-summary">
        <div class="total-price">
          总价: <span class="price">¥{{ cart.totalPrice }}</span>
        </div>
        <div class="cart-actions">
          <button @click="clearCart" class="clear-btn">清空购物车</button>
          <button @click="createOrder" class="pay-btn" :disabled="cart.items.length === 0">创建订单</button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.cart-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem 0;
}

.cart-container h2 {
  margin-bottom: 2rem;
  color: #333;
}

.empty-cart {
  text-align: center;
  padding: 4rem 0;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.empty-cart p {
  font-size: 1.2rem;
  color: #666;
}

.cart-content {
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.cart-table {
  width: 100%;
  border-collapse: collapse;
}

.cart-table th,
.cart-table td {
  padding: 1rem;
  text-align: left;
  border-bottom: 1px solid #eee;
}

.cart-table th {
  background-color: #f5f5f5;
  font-weight: 600;
  color: #333;
}

.cart-table tr:last-child td {
  border-bottom: none;
}

.remove-btn {
  background-color: #ff4d4f;
  color: white;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.3s;
}

.remove-btn:hover {
  background-color: #ff7875;
}

.cart-summary {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1.5rem;
  border-top: 2px solid #eee;
}

.cart-actions {
  display: flex;
  gap: 1rem;
}

.pay-btn {
  background-color: #52c41a;
  color: white;
  border: none;
  padding: 0.5rem 1.5rem;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: background-color 0.3s;
}

.pay-btn:hover {
  background-color: #73d13d;
}

.pay-btn:disabled {
  background-color: #d9d9d9;
  color: #bfbfbf;
  cursor: not-allowed;
}

.total-price {
  font-size: 1.5rem;
  font-weight: 600;
  color: #333;
}

.price {
  color: #ff4d4f;
}

.clear-btn {
  background-color: #f5f5f5;
  color: #666;
  border: 1px solid #ddd;
  padding: 0.5rem 1.5rem;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s;
}

.clear-btn:hover {
  background-color: #e8e8e8;
  border-color: #ccc;
}
</style>