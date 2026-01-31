<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { get, post } from '@/utils/request'

const router = useRouter()
const currentUser = ref(JSON.parse(localStorage.getItem('user') || 'null'))
const orders = ref([])
const isLoading = ref(false)
const errorMessage = ref('')
const selectedOrder = ref(null)
const showOrderDetail = ref(false)

const fetchOrders = async () => {
  if (!currentUser.value) return
  
  isLoading.value = true
  errorMessage.value = ''
  try {
    const data = await get(`/api/order/user/${currentUser.value.id}`)
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

const viewOrderDetail = async (orderId) => {
  try {
    selectedOrder.value = await get(`/api/order/${orderId}`)
    // 处理订单数据
    selectedOrder.value.createdAt = selectedOrder.value.createTime ? new Date(selectedOrder.value.createTime).toLocaleString() : new Date().toLocaleString()
    showOrderDetail.value = true
  } catch (error) {
    console.error('获取订单详情失败:', error)
    errorMessage.value = '获取订单详情失败'
  }
}

const payOrder = async (orderId) => {
  try {
    await post(`/api/order/${orderId}/pay`)
    // 付款成功，刷新订单列表
    await fetchOrders()
    alert('付款成功！')
  } catch (error) {
    console.error('付款失败:', error)
    errorMessage.value = '付款失败'
  }
}

const closeOrderDetail = () => {
  showOrderDetail.value = false
  selectedOrder.value = null
}

onMounted(() => {
  fetchOrders()
})
</script>

<template>
  <div class="user-order-list">
    <h2>我的订单</h2>
    
    <div v-if="!currentUser" class="not-logged-in">
      <p>请先登录</p>
      <router-link to="/login" class="btn btn-primary">去登录</router-link>
    </div>
    <div v-else-if="isLoading" class="loading">加载中...</div>
    <div v-else-if="errorMessage" class="error">{{ errorMessage }}</div>
    <div v-else-if="orders.length === 0" class="empty">暂无订单</div>
    <div v-else class="order-list">
      <div v-for="order in orders" :key="order.id" class="order-item">
        <div class="order-header">
          <h3>订单号: {{ order.id }}</h3>
          <span class="order-status">{{ order.status }}</span>
        </div>
        <div class="order-info">
          <p>总价: ¥{{ order.totalPrice }}</p>
          <p>下单时间: {{ order.createdAt }}</p>
        </div>
        <div class="order-actions">
          <button class="btn btn-view" @click="viewOrderDetail(order.id)">查看详情</button>
          <button v-if="order.status === '待付款'" class="btn btn-pay" @click="payOrder(order.id)">去付款</button>
          <button v-if="order.status === '待收货'" class="btn btn-confirm">确认收货</button>
        </div>
      </div>
    </div>
    
    <!-- 订单详情弹窗 -->
    <div v-if="showOrderDetail && selectedOrder" class="order-detail-modal">
      <div class="modal-content">
        <div class="modal-header">
          <h3>订单详情</h3>
          <button class="btn-close" @click="closeOrderDetail">&times;</button>
        </div>
        <div class="modal-body">
          <div class="order-info">
            <p><strong>订单号:</strong> {{ selectedOrder.id }}</p>
            <p><strong>用户ID:</strong> {{ selectedOrder.userId }}</p>
            <p><strong>总价:</strong> ¥{{ selectedOrder.totalPrice }}</p>
            <p><strong>状态:</strong> {{ selectedOrder.status }}</p>
            <p><strong>下单时间:</strong> {{ selectedOrder.createdAt }}</p>
          </div>
          <div class="order-items">
            <h4>商品列表:</h4>
            <div class="item-list">
              <div v-for="item in selectedOrder.items" :key="item.id" class="item">
                <span class="item-name">{{ item.productName }}</span>
                <span class="item-quantity">x{{ item.quantity }}</span>
                <span class="item-price">¥{{ item.productPrice }}</span>
              </div>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn btn-primary" @click="closeOrderDetail">关闭</button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.user-order-list {
  padding: 20px;
}

h2 {
  color: #42b883;
  margin-bottom: 20px;
}

.not-logged-in {
  text-align: center;
  padding: 40px;
  background-color: #f5f5f5;
  border-radius: 8px;
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
  background-color: #e6f7ff;
  color: #1890ff;
}

.order-info {
  margin-bottom: 16px;
}

.order-info p {
  margin: 6px 0;
  color: #666;
  font-size: 14px;
}

.order-actions {
  display: flex;
  gap: 10px;
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

.btn-pay {
  background-color: #ff4d4f;
  color: white;
}

.btn-pay:hover {
  background-color: #ff7875;
}

.btn-confirm {
  background-color: #52c41a;
  color: white;
}

.btn-confirm:hover {
  background-color: #73d13d;
}

/* 订单详情弹窗样式 */
.order-detail-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background-color: white;
  border-radius: 8px;
  width: 90%;
  max-width: 600px;
  max-height: 80vh;
  overflow-y: auto;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid #e8e8e8;
}

.modal-header h3 {
  margin: 0;
  color: #333;
}

.btn-close {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #999;
  padding: 0;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 4px;
  transition: all 0.3s;
}

.btn-close:hover {
  background-color: #f0f0f0;
  color: #333;
}

.modal-body {
  padding: 16px;
}

.modal-body .order-info {
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.modal-body .order-info p {
  margin: 8px 0;
  color: #666;
}

.modal-body .order-items {
  margin-bottom: 16px;
}

.modal-body .order-items h4 {
  margin: 0 0 12px 0;
  color: #333;
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

.modal-footer {
  padding: 16px;
  border-top: 1px solid #e8e8e8;
  display: flex;
  justify-content: flex-end;
}

.modal-footer .btn-primary {
  background-color: #1890ff;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.3s;
}

.modal-footer .btn-primary:hover {
  background-color: #40a9ff;
}
</style>