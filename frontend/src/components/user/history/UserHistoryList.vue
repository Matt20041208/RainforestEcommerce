<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const currentUser = ref(JSON.parse(localStorage.getItem('user') || 'null'))
const history = ref([])
const isLoading = ref(false)
const errorMessage = ref('')

const viewProductDetail = (productId) => {
  router.push(`/product/${productId}`)
}

const fetchHistory = async () => {
  if (!currentUser.value) return
  
  isLoading.value = true
  errorMessage.value = ''
  try {
    // 从本地存储获取浏览历史
    const savedHistory = localStorage.getItem('viewHistory')
    if (savedHistory) {
      history.value = JSON.parse(savedHistory)
    } else {
      history.value = []
    }
    isLoading.value = false
  } catch (error) {
    console.error('获取浏览历史失败:', error)
    errorMessage.value = '获取浏览历史失败'
    isLoading.value = false
  }
}

const clearHistory = () => {
  if (confirm('确定要清空浏览历史吗？')) {
    history.value = []
    // 清空本地存储中的浏览历史
    localStorage.removeItem('viewHistory')
  }
}

onMounted(() => {
  fetchHistory()
})
</script>

<template>
  <div class="user-history-list">
    <div class="header">
      <h2>浏览历史</h2>
      <button v-if="currentUser && history.length > 0" class="btn btn-danger" @click="clearHistory">清空历史</button>
    </div>
    
    <div v-if="!currentUser" class="not-logged-in">
      <p>请先登录</p>
      <router-link to="/login" class="btn btn-primary">去登录</router-link>
    </div>
    <div v-else-if="isLoading" class="loading">加载中...</div>
    <div v-else-if="errorMessage" class="error">{{ errorMessage }}</div>
    <div v-else-if="history.length === 0" class="empty">暂无浏览历史</div>
    <div v-else class="history-list">
      <div v-for="item in history" :key="item.id" class="history-item">
        <div class="history-image">
          <img :src="item.productImage ? (item.productImage.startsWith('http') ? item.productImage : `http://localhost:8080${item.productImage}`) : 'https://via.placeholder.com/80x80?text=No+Image'" :alt="item.productName">
        </div>
        <div class="history-info">
          <h3>{{ item.productName }}</h3>
          <p class="price">¥{{ item.productPrice }}</p>
          <p class="time">浏览时间: {{ item.viewedAt }}</p>
        </div>
        <div class="history-actions">
          <button class="btn btn-view" @click="viewProductDetail(item.productId)">查看详情</button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.user-history-list {
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.header h2 {
  color: #42b883;
  margin: 0;
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

.history-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.history-item {
  background-color: #fff;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  padding: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
}

.history-image {
  width: 80px;
  height: 80px;
  margin-right: 16px;
  flex-shrink: 0;
}

.history-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 4px;
}

.history-info {
  flex: 1;
}

.history-info h3 {
  margin: 0 0 8px 0;
  color: #333;
  font-size: 16px;
}

.price {
  margin: 0 0 8px 0;
  color: #ff4d4f;
  font-weight: 600;
}

.time {
  margin: 0;
  color: #999;
  font-size: 12px;
}

.history-actions {
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

.btn-danger {
  background-color: #ff4d4f;
  color: white;
}

.btn-danger:hover {
  background-color: #ff7875;
}

.btn-primary {
  background-color: #42b883;
  color: white;
}

.btn-primary:hover {
  background-color: #359d68;
}
</style>