<script setup>
import { ref, onMounted } from 'vue'
import { get } from '@/utils/request'

const orders = ref([])
const isLoading = ref(false)
const errorMessage = ref('')

const fetchOrders = async () => {
  isLoading.value = true
  errorMessage.value = ''
  try {
    const data = await get('/api/order')
    // 处理订单数据，确保订单状态和时间格式正确
    orders.value = data.map(order => ({
      ...order,
      status: order.status || '待处理',
      createdAt: order.createTime ? new Date(order.createTime).toLocaleString() : new Date().toLocaleString()
    }))
  } catch (error) {
    console.error('获取订单列表失败:', error)
    errorMessage.value = '获取订单列表失败'
  } finally {
    isLoading.value = false
  }
}

onMounted(() => {
  fetchOrders()
})
</script>

<template>
  <div class="seller-order-list">
    <h2>订单管理</h2>
    
    <div v-if="isLoading" class="loading">加载中...</div>
    <div v-else-if="errorMessage" class="error">{{ errorMessage }}</div>
    <div v-else-if="orders.length === 0" class="empty">暂无订单</div>
    <div v-else class="order-list">
      <div v-for="order in orders" :key="order.id" class="order-item">
        <div class="order-header">
          <h3>订单号: {{ order.id }}</h3>
          <span class="order-status" :class="{
            'status-pending': order.status === '待付款',
            'status-paid': order.status === '已付款',
            'status-processed': order.status === '已处理'
          }">{{ order.status }}</span>
        </div>
        <div class="order-info">
          <p>用户ID: {{ order.userId }}</p>
          <p>总价: ¥{{ order.totalPrice }}</p>
          <p>下单时间: {{ order.createdAt }}</p>
        </div>
        <div class="order-items">
          <h4>商品列表:</h4>
          <div class="item-list">
            <div v-for="item in order.items" :key="item.id" class="item">
              <span class="item-name">{{ item.productName }}</span>
              <span class="item-quantity">x{{ item.quantity }}</span>
              <span class="item-price">¥{{ item.productPrice }}</span>
            </div>
          </div>
        </div>
        <div class="order-actions">
          <button class="btn btn-view">查看详情</button>
          <button class="btn btn-process" v-if="order.status === '已付款'">处理订单</button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.seller-order-list {
  padding: 20px;
}

h2 {
  color: #42b883;
  margin-bottom: 20px;
}

.loading,
.error,
.empty {
  text-align: center;
  padding: 40px;
  background-color: #f5f5f5;
  border-radius: 8px;
  margin-bottom: 20px;
}

.error {
  color: #ff4d4f;
}

.order-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.order-item {
  background-color: #fff;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  padding: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.order-header h3 {
  margin: 0;
  color: #333;
  font-size: 16px;
}

.order-status {
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.status-pending {
  background-color: #fff7e6;
  color: #fa8c16;
}

.status-paid {
  background-color: #e6f7ff;
  color: #1890ff;
}

.status-processed {
  background-color: #f6ffed;
  color: #52c41a;
}

.order-info {
  margin-bottom: 16px;
}

.order-info p {
  margin: 6px 0;
  color: #666;
  font-size: 14px;
}

.order-items {
  margin-bottom: 16px;
  border-top: 1px solid #f0f0f0;
  padding-top: 12px;
}

.order-items h4 {
  margin: 0 0 10px 0;
  color: #333;
  font-size: 14px;
}

.item-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8px;
  background-color: #f9f9f9;
  border-radius: 4px;
}

.item-name {
  flex: 1;
  color: #333;
  font-size: 14px;
}

.item-quantity {
  margin: 0 16px;
  color: #666;
  font-size: 14px;
}

.item-price {
  color: #ff4d4f;
  font-size: 14px;
  font-weight: 500;
}

.order-actions {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
}

.btn {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.3s;
}

.btn-view {
  background-color: #1890ff;
  color: white;
}

.btn-view:hover {
  background-color: #40a9ff;
}

.btn-process {
  background-color: #52c41a;
  color: white;
}

.btn-process:hover {
  background-color: #73d13d;
}
</style>