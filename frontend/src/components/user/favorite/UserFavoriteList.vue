<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const currentUser = ref(JSON.parse(localStorage.getItem('user') || 'null'))
const favorites = ref([])
const isLoading = ref(false)
const errorMessage = ref('')

const fetchFavorites = async () => {
  if (!currentUser.value) return
  
  isLoading.value = true
  errorMessage.value = ''
  try {
    // 从本地存储获取收藏数据
    const savedFavorites = localStorage.getItem('favorites')
    if (savedFavorites) {
      favorites.value = JSON.parse(savedFavorites)
    } else {
      favorites.value = []
    }
    isLoading.value = false
  } catch (error) {
    console.error('获取收藏列表失败:', error)
    errorMessage.value = '获取收藏列表失败'
    isLoading.value = false
  }
}

const removeFavorite = (favoriteId) => {
  // 从本地存储中删除收藏
  try {
    const savedFavorites = localStorage.getItem('favorites')
    if (savedFavorites) {
      let favoriteList = JSON.parse(savedFavorites)
      favoriteList = favoriteList.filter(fav => fav.id !== favoriteId)
      localStorage.setItem('favorites', JSON.stringify(favoriteList))
      // 更新本地状态
      favorites.value = favoriteList
    }
  } catch (error) {
    console.error('删除收藏失败:', error)
  }
}

const viewProductDetail = (productId) => {
  router.push(`/product/${productId}`)
}

onMounted(() => {
  fetchFavorites()
})
</script>

<template>
  <div class="user-favorite-list">
    <h2>我的收藏</h2>
    
    <div v-if="!currentUser" class="not-logged-in">
      <p>请先登录</p>
      <router-link to="/login" class="btn btn-primary">去登录</router-link>
    </div>
    <div v-else-if="isLoading" class="loading">加载中...</div>
    <div v-else-if="errorMessage" class="error">{{ errorMessage }}</div>
    <div v-else-if="favorites.length === 0" class="empty">暂无收藏</div>
    <div v-else class="favorite-list">
      <div v-for="favorite in favorites" :key="favorite.id" class="favorite-item">
        <div class="favorite-image">
          <img :src="favorite.productImage ? (favorite.productImage.startsWith('http') ? favorite.productImage : `http://localhost:8080${favorite.productImage}`) : 'https://via.placeholder.com/100x100?text=No+Image'" :alt="favorite.productName">
        </div>
        <div class="favorite-info">
          <h3>{{ favorite.productName }}</h3>
          <p class="price">¥{{ favorite.productPrice }}</p>
          <p class="time">收藏时间: {{ favorite.favoritedAt || favorite.createdAt }}</p>
        </div>
        <div class="favorite-actions">
          <button class="btn btn-view" @click="viewProductDetail(favorite.productId)">查看详情</button>
          <button class="btn btn-remove" @click="removeFavorite(favorite.id)">取消收藏</button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.user-favorite-list {
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

.favorite-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.favorite-item {
  background-color: #fff;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  padding: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
}

.favorite-image {
  width: 100px;
  height: 100px;
  margin-right: 16px;
  flex-shrink: 0;
}

.favorite-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 4px;
}

.favorite-info {
  flex: 1;
}

.favorite-info h3 {
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

.favorite-actions {
  display: flex;
  flex-direction: column;
  gap: 8px;
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

.btn-remove {
  background-color: #ff4d4f;
  color: white;
}

.btn-remove:hover {
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