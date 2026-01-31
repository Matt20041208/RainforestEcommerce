<script setup>
import { ref, onMounted } from 'vue'
import { get, post, del } from '@/utils/request'

// 订单列表数据
const orders = ref([])
// 当前选中的订单
const selectedOrder = ref(null)
// 加载状态
const isLoading = ref(false)
// 错误信息
const errorMessage = ref('')
// 当前用户ID（从登录信息获取）
const currentUser = ref(JSON.parse(localStorage.getItem('user') || 'null'))
const userId = ref(currentUser.value?.id || 1)

// 支付相关
const paymentModalVisible = ref(false)
const selectedPaymentMethod = ref('')
const paymentMethods = [
  { id: 'alipay', name: '支付宝', icon: '💳' },
  { id: 'wechat', name: '微信支付', icon: '💚' },
  { id: 'creditcard', name: '信用卡', icon: '💳' },
  { id: 'banktransfer', name: '银行转账', icon: '🏦' }
]

// 订单状态跟踪
const orderStatuses = {
  '待付款': { name: '待付款', description: '订单已创建，等待支付', icon: '⏳' },
  '已付款': { name: '已付款', description: '支付成功，等待发货', icon: '✅' },
  '待发货': { name: '待发货', description: '订单已支付，等待商家发货', icon: '📦' },
  '已发货': { name: '已发货', description: '订单已发货，正在配送中', icon: '🚚' },
  '待收货': { name: '待收货', description: '商品正在配送中，预计1-2天送达', icon: '📬' },
  '已完成': { name: '已完成', description: '订单已完成，感谢您的购买', icon: '🎉' },
  '已取消': { name: '已取消', description: '订单已取消', icon: '❌' }
}

// 物流信息模拟
const logisticsInfo = ref({
  id: '',
  orderId: '',
  status: '',
  trackingNumber: '',
  company: '',
  updates: []
})

// 分页相关
const pagination = ref({
  currentPage: 1,
  pageSize: 10,
  totalPages: 1,
  totalItems: 0
})

// 获取订单列表
const fetchOrders = async () => {
  isLoading.value = true
  errorMessage.value = ''
  try {
    const url = `/api/order/user/${userId.value}?page=${pagination.value.currentPage}&pageSize=${pagination.value.pageSize}`
    const data = await get(url)
    // 检查是否是分页响应格式
    if (data.content && Array.isArray(data.content)) {
      // 分页响应格式
      orders.value = data.content
      pagination.value.totalItems = data.totalElements
      pagination.value.totalPages = data.totalPages
      pagination.value.currentPage = data.number + 1 // 后端通常从0开始，前端从1开始
    } else {
      // 非分页响应格式（兼容旧接口）
      orders.value = data
      pagination.value.totalItems = data.length
      pagination.value.totalPages = 1
      pagination.value.currentPage = 1
    }
  } catch (error) {
    console.error('获取订单列表失败:', error)
    errorMessage.value = '获取订单列表失败'
  } finally {
    isLoading.value = false
  }
}

// 查看订单详情
const viewOrderDetails = async (orderId) => {
  try {
    selectedOrder.value = await get(`/api/order/${orderId}`)
  } catch (error) {
    console.error('获取订单详情失败:', error)
    alert('获取订单详情失败')
  }
}

// 关闭订单详情
const closeOrderDetails = () => {
  selectedOrder.value = null
}

// 打开支付弹窗
const openPaymentModal = (order) => {
  selectedOrder.value = order
  selectedPaymentMethod.value = ''
  paymentModalVisible.value = true
}

// 关闭支付弹窗
const closePaymentModal = () => {
  paymentModalVisible.value = false
}

// 处理支付
const processPayment = async () => {
  if (!selectedPaymentMethod.value) {
    alert('请选择支付方式')
    return
  }
  
  if (!selectedOrder.value) return
  
  try {
    // 处理支付
    isLoading.value = true
    
    // 调用后端支付API
    const updatedOrder = await post(`/api/order/${selectedOrder.value.id}/pay`)
    
    // 更新订单状态为已付款
    selectedOrder.value.status = updatedOrder.status
    
    // 更新原始订单数组中的订单状态
    const orderIndex = orders.value.findIndex(order => order.id === selectedOrder.value.id)
    if (orderIndex !== -1) {
      orders.value[orderIndex].status = updatedOrder.status
    }
    
    // 关闭支付弹窗
    closePaymentModal()
    
    // 模拟生成物流信息
    if (selectedOrder.value) {
      generateLogisticsInfo(selectedOrder.value.id)
    }
    
    alert(`使用${paymentMethods.find(m => m.id === selectedPaymentMethod.value).name}支付成功！`)
  } catch (error) {
    console.error('支付失败:', error)
    alert('支付失败，请稍后重试')
  } finally {
    isLoading.value = false
  }
}

// 删除订单
const deleteOrder = async (orderId) => {
  if (confirm('确定要删除该订单吗？')) {
    try {
      await del(`/api/order/${orderId}`)
      
      // 从订单列表中移除该订单
      orders.value = orders.value.filter(order => order.id !== orderId)
      // 更新分页信息
      pagination.value.totalItems--
      alert('订单删除成功')
    } catch (error) {
      console.error('删除订单失败:', error)
      alert('删除订单失败，请稍后重试')
    }
  }
}

// 模拟生成物流信息
const generateLogisticsInfo = (orderId) => {
  logisticsInfo.value = {
    id: `log-${Date.now()}`,
    orderId: orderId,
    status: '运输中',
    trackingNumber: `SF${Math.floor(100000000000 + Math.random() * 900000000000)}`,
    company: '顺丰速运',
    updates: [
      {
        time: new Date().toISOString(),
        message: '订单已支付，商家准备发货'
      },
      {
        time: new Date(Date.now() + 3600000).toISOString(),
        message: '商家已发货，商品正在运输中'
      },
      {
        time: new Date(Date.now() + 7200000).toISOString(),
        message: '快递已到达本地仓库'
      },
      {
        time: new Date(Date.now() + 86400000).toISOString(),
        message: '快递员正在派送中'
      },
      {
        time: new Date(Date.now() + 90000000).toISOString(),
        message: '商品已签收'
      }
    ]
  }
}

// 查看物流信息
const viewLogistics = (order) => {
  selectedOrder.value = order
  // 生成模拟物流信息
  generateLogisticsInfo(order.id)
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

// 组件挂载时加载订单列表
onMounted(() => {
  fetchOrders()
})
</script>

<template>
  <div class="order-container">
    <h2>我的订单</h2>
    
    <!-- 错误提示 -->
    <div v-if="errorMessage" class="error-message">{{ errorMessage }}</div>
    
    <!-- 加载状态 -->
    <div v-if="isLoading" class="loading">加载中...</div>
    
    <!-- 订单列表为空时的提示 -->
    <div v-else-if="orders.length === 0" class="empty-orders">
      <p>暂无订单记录</p>
    </div>
    
    <!-- 订单列表 -->
    <div v-else class="orders-list">
      <div v-for="order in orders" :key="order.id" class="order-card">
        <div class="order-header">
          <div class="order-info">
            <span class="order-id">订单号: {{ order.id }}</span>
            <span class="order-status" :class="`status-${order.status}`">{{ order.status }}</span>
          </div>
          <div class="order-date">{{ formatDate(order.createTime) }}</div>
        </div>
        
        <div class="order-items-preview">
          <div v-for="item in order.items" :key="item.id" class="item-preview">
            <div class="item-info">
              <span class="item-name">{{ item.productName }}</span>
              <span class="item-quantity">x{{ item.quantity }}</span>
            </div>
            <div class="item-price">¥{{ item.productPrice }}</div>
          </div>
        </div>
        
        <div class="order-footer">
          <div class="order-total">
            共{{ order.items.length }}件商品 总价: <span class="total-price">¥{{ order.totalPrice }}</span>
          </div>
          <div class="order-actions">
            <button 
              v-if="order.status === '待付款'" 
              @click="openPaymentModal(order)" 
              class="pay-btn"
            >
              立即支付
            </button>
            <button 
              v-if="order.status !== '待付款' && order.status !== '已取消'" 
              @click="viewLogistics(order)" 
              class="logistics-btn"
            >
              查看物流
            </button>
            <button @click="viewOrderDetails(order.id)" class="detail-btn">查看详情</button>
            <button @click="deleteOrder(order.id)" class="delete-btn">删除订单</button>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 分页控件 -->
    <div v-if="pagination.totalPages > 1" class="pagination">
      <button 
        @click="pagination.currentPage = 1" 
        :disabled="pagination.currentPage === 1"
        class="page-btn"
      >
        首页
      </button>
      <button 
        @click="pagination.currentPage--" 
        :disabled="pagination.currentPage === 1"
        class="page-btn"
      >
        上一页
      </button>
      
      <span v-for="page in Math.min(pagination.totalPages, 5)" :key="page" class="page-number" :class="{ active: page === pagination.currentPage }" @click="pagination.currentPage = page">
        {{ page }}
      </span>
      
      <span v-if="pagination.totalPages > 5 && pagination.currentPage < pagination.totalPages - 2" class="page-ellipsis">...</span>
      
      <span v-if="pagination.totalPages > 5 && pagination.currentPage >= pagination.totalPages - 2" class="page-number" :class="{ active: pagination.totalPages - 2 === pagination.currentPage }" @click="pagination.currentPage = pagination.totalPages - 2">
        {{ pagination.totalPages - 2 }}
      </span>
      <span v-if="pagination.totalPages > 5 && pagination.currentPage >= pagination.totalPages - 1" class="page-number" :class="{ active: pagination.totalPages - 1 === pagination.currentPage }" @click="pagination.currentPage = pagination.totalPages - 1">
        {{ pagination.totalPages - 1 }}
      </span>
      <span v-if="pagination.totalPages > 1" class="page-number" :class="{ active: pagination.totalPages === pagination.currentPage }" @click="pagination.currentPage = pagination.totalPages">
        {{ pagination.totalPages }}
      </span>
      
      <button 
        @click="pagination.currentPage++" 
        :disabled="pagination.currentPage === pagination.totalPages"
        class="page-btn"
      >
        下一页
      </button>
      <button 
        @click="pagination.currentPage = pagination.totalPages" 
        :disabled="pagination.currentPage === pagination.totalPages"
        class="page-btn"
      >
        末页
      </button>
      
      <span class="page-info">
        第 {{ pagination.currentPage }}/{{ pagination.totalPages }} 页，共 {{ pagination.totalItems }} 条订单
      </span>
    </div>
    
    <!-- 订单详情弹窗 -->
    <div v-if="selectedOrder" class="modal-overlay" @click="closeOrderDetails">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>订单详情</h3>
          <button class="close-btn" @click="closeOrderDetails">×</button>
        </div>
        
        <div class="order-details">
          <div class="order-basic-info">
            <p><strong>订单号:</strong> {{ selectedOrder.id }}</p>
            <p><strong>创建时间:</strong> {{ formatDate(selectedOrder.createTime) }}</p>
            <p><strong>订单状态:</strong> <span class="order-status" :class="`status-${selectedOrder.status}`">{{ selectedOrder.status }}</span></p>
          </div>
          
          <div class="order-items">
            <h4>商品列表</h4>
            <table class="items-table">
              <thead>
                <tr>
                  <th>商品名称</th>
                  <th>单价</th>
                  <th>数量</th>
                  <th>小计</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="item in selectedOrder.items" :key="item.id">
                  <td>{{ item.productName }}</td>
                  <td>¥{{ item.productPrice }}</td>
                  <td>{{ item.quantity }}</td>
                  <td>¥{{ item.totalPrice }}</td>
                </tr>
              </tbody>
            </table>
          </div>
          
          <div class="order-summary">
            <div class="summary-item">
              <span>商品总价:</span>
              <span>¥{{ selectedOrder.totalPrice }}</span>
            </div>
            <div class="summary-item total">
              <span>实付款:</span>
              <span class="final-price">¥{{ selectedOrder.totalPrice }}</span>
            </div>
          </div>
          
          <!-- 订单状态跟踪 -->
          <div class="order-status-tracking">
            <h4>订单状态跟踪</h4>
            <div class="status-timeline">
              <div 
                v-for="(status, index) in Object.values(orderStatuses)" 
                :key="index"
                class="status-item"
                :class="{ 'active': selectedOrder.status === status.name }"
              >
                <div class="status-icon">{{ status.icon }}</div>
                <div class="status-info">
                  <div class="status-name">{{ status.name }}</div>
                  <div class="status-description">{{ status.description }}</div>
                </div>
              </div>
            </div>
          </div>
          
          <!-- 物流信息 -->
          <div v-if="logisticsInfo.orderId === selectedOrder.id" class="logistics-info">
            <h4>物流信息</h4>
            <div class="logistics-header">
              <div class="logistics-company">
                <span>快递公司:</span> {{ logisticsInfo.company }}
              </div>
              <div class="tracking-number">
                <span>运单号:</span> {{ logisticsInfo.trackingNumber }}
              </div>
              <div class="logistics-status">
                <span>物流状态:</span> <span class="status-text">{{ logisticsInfo.status }}</span>
              </div>
            </div>
            <div class="logistics-updates">
              <div 
                v-for="(update, index) in logisticsInfo.updates" 
                :key="index"
                class="logistics-update"
              >
                <div class="update-time">{{ formatDate(update.time) }}</div>
                <div class="update-message">{{ update.message }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 支付弹窗 -->
    <div v-if="paymentModalVisible" class="modal-overlay" @click="closePaymentModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>选择支付方式</h3>
          <button class="close-btn" @click="closePaymentModal">&times;</button>
        </div>
        <div class="modal-body">
          <div v-if="selectedOrder" class="payment-info">
            <p class="payment-amount">订单金额: <span class="amount">¥{{ selectedOrder.totalPrice }}</span></p>
            <p class="payment-order-id">订单号: {{ selectedOrder.id }}</p>
          </div>
          
          <div class="payment-methods">
            <h4>选择支付方式</h4>
            <div 
              v-for="method in paymentMethods" 
              :key="method.id"
              class="payment-method"
              :class="{ 'selected': selectedPaymentMethod === method.id }"
              @click="selectedPaymentMethod = method.id"
            >
              <div class="method-icon">{{ method.icon }}</div>
              <div class="method-name">{{ method.name }}</div>
              <div class="method-select">
                <input 
                  type="radio" 
                  :id="method.id" 
                  :name="'payment-method'"
                  :checked="selectedPaymentMethod === method.id"
                  @change="selectedPaymentMethod = method.id"
                />
                <label :for="method.id"></label>
              </div>
            </div>
          </div>
          
          <div class="modal-actions">
            <button @click="closePaymentModal" class="cancel-btn">取消</button>
            <button 
              @click="processPayment" 
              class="confirm-pay-btn"
              :disabled="!selectedPaymentMethod || isLoading"
            >
              {{ isLoading ? '支付处理中...' : '确认支付' }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.order-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem 0;
}

.order-container h2 {
  margin-bottom: 2rem;
  color: #333;
}

.error-message {
  background-color: #fff2f0;
  border: 1px solid #ffccc7;
  color: #cf1322;
  padding: 1rem;
  border-radius: 4px;
  margin-bottom: 1rem;
}

.loading {
  text-align: center;
  padding: 2rem;
  font-size: 1.2rem;
  color: #666;
}

.empty-orders {
  text-align: center;
  padding: 4rem 0;
  background-color: #f5f5f5;
  border-radius: 8px;
  color: #666;
  font-size: 1.2rem;
}

.orders-list {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.order-card {
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 1.5rem;
  transition: box-shadow 0.3s;
}

.order-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
  padding-bottom: 1rem;
  border-bottom: 1px solid #eee;
}

.order-info {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.order-id {
  font-weight: 600;
  color: #333;
}

.order-status {
  padding: 0.25rem 0.75rem;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
}

.status-待付款 {
  background-color: #fffbe6;
  color: #faad14;
  border: 1px solid #ffe7ba;
}

.status-已付款 {
  background-color: #f6ffed;
  color: #52c41a;
  border: 1px solid #b7eb8f;
}

.order-date {
  color: #999;
  font-size: 14px;
}

.order-items-preview {
  margin-bottom: 1rem;
}

.item-preview {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.5rem 0;
  border-bottom: 1px solid #f0f0f0;
}

.item-preview:last-child {
  border-bottom: none;
}

.item-info {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.item-name {
  color: #333;
}

.item-quantity {
  color: #999;
  font-size: 14px;
}

.item-price {
  font-weight: 500;
  color: #333;
}

.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 1rem;
  border-top: 1px solid #eee;
}

.order-total {
  color: #666;
}

.total-price {
  font-weight: 600;
  color: #ff4d4f;
  font-size: 1.1rem;
}

.detail-btn {
  background-color: #1890ff;
  color: white;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.3s;
}

.detail-btn:hover {
  background-color: #40a9ff;
}

/* 弹窗样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background-color: white;
  border-radius: 8px;
  width: 90%;
  max-width: 800px;
  max-height: 80vh;
  overflow-y: auto;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1.5rem;
  border-bottom: 1px solid #eee;
}

.modal-header h3 {
  margin: 0;
  color: #333;
}

.close-btn {
  background: none;
  border: none;
  font-size: 1.5rem;
  color: #999;
  cursor: pointer;
  padding: 0;
  line-height: 1;
}

.close-btn:hover {
  color: #333;
}

.order-details {
  padding: 1.5rem;
}

.order-basic-info {
  margin-bottom: 2rem;
  padding: 1rem;
  background-color: #fafafa;
  border-radius: 4px;
}

.order-basic-info p {
  margin: 0.5rem 0;
}

.order-items h4 {
  margin-top: 0;
  margin-bottom: 1rem;
  color: #333;
}

.items-table {
  width: 100%;
  border-collapse: collapse;
  margin-bottom: 2rem;
}

.items-table th,
.items-table td {
  padding: 0.75rem;
  text-align: left;
  border-bottom: 1px solid #eee;
}

.items-table th {
  background-color: #fafafa;
  font-weight: 600;
  color: #333;
}

.order-summary {
  border-top: 2px solid #eee;
  padding-top: 1rem;
}

.summary-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 0.5rem;
  color: #666;
}

.summary-item.total {
  margin-top: 1rem;
  padding-top: 1rem;
  border-top: 1px solid #eee;
  font-weight: 600;
  color: #333;
  font-size: 1.1rem;
}

.final-price {
  color: #ff4d4f;
  font-size: 1.2rem;
}

/* 订单操作按钮样式 */
.order-actions {
  display: flex;
  gap: 10px;
}

.order-actions button {
  padding: 6px 12px;
  border: 1px solid #ddd;
  background-color: white;
  border-radius: 4px;
  cursor: pointer;
  font-size: 13px;
  color: #333; /* 确保文字颜色可见 */
  transition: all 0.3s;
  text-align: center;
  min-width: 80px;
  display: inline-block;
}

.order-actions button:hover {
  background-color: #f5f5f5;
}

.pay-btn {
  background-color: #ff4d4f;
  color: white;
  border-color: #ff4d4f;
}

.pay-btn:hover {
  background-color: #ff7875;
}

.logistics-btn {
  background-color: #1890ff;
  color: white;
  border-color: #1890ff;
}

.logistics-btn:hover {
  background-color: #40a9ff;
}

.delete-btn {
  background-color: #ff4d4f;
  color: white;
  border-color: #ff4d4f;
}

.delete-btn:hover {
  background-color: #ff7875;
}

/* 订单状态跟踪样式 */
.order-status-tracking {
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.order-status-tracking h4 {
  margin-bottom: 20px;
  color: #333;
}

.status-timeline {
  position: relative;
  padding-left: 30px;
}

.status-timeline::before {
  content: '';
  position: absolute;
  left: 10px;
  top: 0;
  bottom: 0;
  width: 2px;
  background-color: #e8e8e8;
}

.status-item {
  position: relative;
  margin-bottom: 20px;
  padding-left: 20px;
}

.status-item.active .status-icon {
  background-color: #52c41a;
  color: white;
}

.status-item.active .status-name {
  color: #52c41a;
  font-weight: 500;
}

.status-icon {
  position: absolute;
  left: -25px;
  top: 0;
  width: 20px;
  height: 20px;
  border-radius: 50%;
  background-color: #e8e8e8;
  color: #999;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  z-index: 1;
}

.status-info {
  background-color: #fafafa;
  padding: 10px 15px;
  border-radius: 4px;
}

.status-name {
  font-weight: 500;
  margin-bottom: 5px;
}

.status-description {
  font-size: 12px;
  color: #999;
}

/* 物流信息样式 */
.logistics-info {
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.logistics-info h4 {
  margin-bottom: 20px;
  color: #333;
}

.logistics-header {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #eee;
}

.logistics-company,
.tracking-number,
.logistics-status {
  color: #666;
}

.logistics-company span,
.tracking-number span,
.logistics-status span {
  color: #333;
  font-weight: 500;
}

.status-text {
  color: #1890ff;
}

.logistics-updates {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.logistics-update {
  display: flex;
  gap: 15px;
}

.update-time {
  width: 120px;
  color: #999;
  font-size: 13px;
  flex-shrink: 0;
}

.update-message {
  color: #666;
  flex: 1;
}

/* 支付弹窗样式 */
.payment-info {
  background-color: #f9f9f9;
  padding: 15px;
  border-radius: 4px;
  margin-bottom: 20px;
}

.payment-amount {
  font-size: 18px;
  margin-bottom: 10px;
}

.amount {
  font-weight: 600;
  color: #ff4d4f;
}

.payment-order-id {
  color: #666;
  font-size: 14px;
}

.payment-methods {
  margin-bottom: 20px;
}

.payment-methods h4 {
  margin-bottom: 15px;
}

.payment-method {
  display: flex;
  align-items: center;
  padding: 15px;
  border: 1px solid #eee;
  border-radius: 4px;
  margin-bottom: 10px;
  cursor: pointer;
  transition: all 0.3s;
}

.payment-method:hover {
  border-color: #42b883;
}

.payment-method.selected {
  border-color: #42b883;
  background-color: #f0f9f4;
}

.method-icon {
  font-size: 24px;
  margin-right: 15px;
}

.method-name {
  flex: 1;
  font-size: 16px;
}

.method-select {
  position: relative;
}

.method-select input[type="radio"] {
  opacity: 0;
  position: absolute;
}

.method-select label {
  width: 20px;
  height: 20px;
  border: 1px solid #ddd;
  border-radius: 50%;
  display: block;
  cursor: pointer;
}

.payment-method.selected .method-select label::after {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background-color: #42b883;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
}

.modal-actions button {
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.cancel-btn {
  background-color: white;
  color: #666;
  border: 1px solid #ddd;
}

.cancel-btn:hover {
  background-color: #f5f5f5;
}

.confirm-pay-btn {
  background-color: #42b883;
  color: white;
  border: none;
}

.confirm-pay-btn:hover:not(:disabled) {
  background-color: #35495e;
}

.confirm-pay-btn:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

/* 分页控件样式 */
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 8px;
  margin-top: 30px;
  flex-wrap: wrap;
}

.page-btn {
  padding: 8px 12px;
  border: 1px solid #ddd;
  background-color: #fff;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.page-btn:hover:not(:disabled) {
  background-color: #f5f5f5;
}

.page-btn:disabled {
  color: #ccc;
  cursor: not-allowed;
}

.page-number {
  padding: 8px 12px;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.page-number:hover {
  background-color: #f5f5f5;
}

.page-number.active {
  background-color: #1890ff;
  color: white;
  font-weight: 500;
}

.page-ellipsis {
  padding: 0 8px;
  color: #666;
}

.page-info {
  margin-left: 15px;
  color: #666;
  font-size: 14px;
}
</style>