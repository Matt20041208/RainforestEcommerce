<script setup>
import { ref, onMounted, computed, watch } from 'vue'
import { useRoute } from 'vue-router'
import { get, post } from '@/utils/request'

// 获取路由参数
const route = useRoute()
const productId = computed(() => route.params.id)

// 用户信息
const currentUser = ref(null)

// 商品详情数据
const product = ref(null)
const isLoading = ref(false)
const errorMessage = ref('')

// 购买数量
const quantity = ref(1)

// 购物车操作反馈
const cartMessage = ref('')

// 评论相关数据
const comments = ref([])
const isLoadingComments = ref(false)
const commentContent = ref('')
const commentRating = ref(5)
const commentMessage = ref('')
const averageRating = ref(0)

// 当前登录用户ID
const currentUserId = ref(null)



// 获取商品详情
const fetchProductDetails = async () => {
  isLoading.value = true
  errorMessage.value = ''
  try {
    product.value = await get(`/api/product/${productId.value}`)
  } catch (error) {
    console.error('获取商品详情失败:', error)
    errorMessage.value = '获取商品详情失败'
  } finally {
    isLoading.value = false
  }
}

// 获取商品评论
const fetchComments = async () => {
  isLoadingComments.value = true
  try {
    comments.value = await get(`/api/comment/product/${productId.value}`)
    calculateAverageRating()
  } catch (error) {
    console.error('获取评论失败:', error)
  } finally {
    isLoadingComments.value = false
  }
}

// 计算平均评分
const calculateAverageRating = () => {
  if (comments.value.length === 0) {
    averageRating.value = 0
    return
  }
  const total = comments.value.reduce((sum, comment) => sum + comment.rating, 0)
  averageRating.value = (total / comments.value.length).toFixed(1)
}

// 提交评论
const submitComment = async () => {
  if (!commentContent.value.trim() || !currentUserId.value) {
    commentMessage.value = '请填写评论内容'
    return
  }
  
  try {
    await post('/api/comment/add', {
      userId: currentUserId.value,
      productId: product.value.id,
      rating: commentRating.value,
      content: commentContent.value
    })
    
    commentMessage.value = '评论成功'
    commentContent.value = ''
    commentRating.value = 5
    // 重新获取评论列表
    fetchComments()
    // 3秒后清除消息
    setTimeout(() => {
      commentMessage.value = ''
    }, 3000)
  } catch (error) {
    console.error('提交评论失败:', error)
    commentMessage.value = '评论失败'
    setTimeout(() => {
      commentMessage.value = ''
    }, 3000)
  }
}

// 加入购物车
const addToCart = async () => {
  if (!product.value) return
  
  // 检查用户是否登录
  if (!currentUser.value) {
    // 未登录，显示登录提示
    showLoginPrompt()
    return
  }
  
  try {
    await post('/api/cart/add', {
      productId: product.value.id,
      quantity: quantity.value
    })
    
    cartMessage.value = `已将"${product.value.name}"加入购物车`
    // 3秒后清除消息
    setTimeout(() => {
      cartMessage.value = ''
    }, 3000)
  } catch (error) {
    console.error('加入购物车失败:', error)
    cartMessage.value = '加入购物车失败'
    setTimeout(() => {
      cartMessage.value = ''
    }, 3000)
  }
}

// 收藏相关
const isFavorite = ref(false)
const favoriteMessage = ref('')

// 检查商品是否已收藏
const checkFavoriteStatus = () => {
  if (!currentUser.value || !product.value) {
    isFavorite.value = false
    return
  }
  
  try {
    const favorites = localStorage.getItem('favorites')
    if (favorites) {
      const favoriteList = JSON.parse(favorites)
      isFavorite.value = favoriteList.some(item => item.productId === product.value.id)
    } else {
      isFavorite.value = false
    }
  } catch (error) {
    console.error('检查收藏状态失败:', error)
    isFavorite.value = false
  }
}

// 切换收藏状态
const toggleFavorite = () => {
  if (!currentUser.value) {
    showLoginPrompt()
    return
  }
  
  if (!product.value) return
  
  try {
    const favorites = localStorage.getItem('favorites')
    let favoriteList = favorites ? JSON.parse(favorites) : []
    
    const productIndex = favoriteList.findIndex(item => item.productId === product.value.id)
    
    if (productIndex > -1) {
      // 取消收藏
      favoriteList.splice(productIndex, 1)
      isFavorite.value = false
      favoriteMessage.value = '已取消收藏'
    } else {
      // 添加收藏
      const favoriteItem = {
        id: Date.now(),
        productId: product.value.id,
        productName: product.value.name,
        productPrice: product.value.price,
        productImage: product.value.image,
        favoritedAt: new Date().toLocaleString('zh-CN')
      }
      favoriteList.push(favoriteItem)
      isFavorite.value = true
      favoriteMessage.value = '收藏成功'
    }
    
    // 保存到本地存储
    localStorage.setItem('favorites', JSON.stringify(favoriteList))
    
    // 3秒后清除消息
    setTimeout(() => {
      favoriteMessage.value = ''
    }, 3000)
  } catch (error) {
    console.error('操作收藏失败:', error)
    favoriteMessage.value = '操作收藏失败'
    setTimeout(() => {
      favoriteMessage.value = ''
    }, 3000)
  }
}

// 增加购买数量
const increaseQuantity = () => {
  if (product.value && quantity.value < product.value.stock) {
    quantity.value++
  }
}

// 减少购买数量
const decreaseQuantity = () => {
  if (quantity.value > 1) {
    quantity.value--
  }
}

// 未登录提示函数
const showLoginPromptModal = ref(false)

const showLoginPrompt = () => {
  showLoginPromptModal.value = true
}

const handleLoginChoice = (choice) => {
  showLoginPromptModal.value = false
  if (choice === '1') {
    // 商家登录
    window.location.href = '/seller/login'
  } else if (choice === '2') {
    // 用户登录
    window.location.href = '/login'
  }
  // 其他情况取消操作
}

// 将商品添加到浏览历史
const addToViewHistory = (product) => {
  if (!currentUser.value) return
  
  try {
    // 获取现有浏览历史
    const savedHistory = localStorage.getItem('viewHistory')
    let history = savedHistory ? JSON.parse(savedHistory) : []
    
    // 移除重复的商品（如果有）
    history = history.filter(item => item.productId !== product.id)
    
    // 创建浏览历史记录
    const historyItem = {
      id: Date.now(), // 使用时间戳作为唯一ID
      productId: product.id,
      productName: product.name,
      productPrice: product.price,
      productImage: product.image,
      viewedAt: new Date().toLocaleString('zh-CN')
    }
    
    // 添加到历史记录开头
    history.unshift(historyItem)
    
    // 限制历史记录数量（最多20条）
    if (history.length > 20) {
      history = history.slice(0, 20)
    }
    
    // 保存到本地存储
    localStorage.setItem('viewHistory', JSON.stringify(history))
  } catch (error) {
    console.error('添加浏览历史失败:', error)
  }
}

// 组件挂载时获取商品详情和评论
onMounted(() => {
  // 获取用户信息
  const user = localStorage.getItem('user')
  if (user) {
    currentUser.value = JSON.parse(user)
    currentUserId.value = currentUser.value.id
  }
  
  fetchProductDetails()
  fetchComments()
})

// 监听商品数据变化，当商品加载完成后添加到浏览历史并检查收藏状态
watch(() => product.value, (newProduct) => {
  if (newProduct) {
    addToViewHistory(newProduct)
    checkFavoriteStatus()
  }
}, { immediate: false })
</script>

<template>
  <div class="product-detail-container">
    <h2>商品详情</h2>
    
    <!-- 错误提示 -->
    <div v-if="errorMessage" class="error-message">{{ errorMessage }}</div>
    
    <!-- 购物车操作消息 -->
    <div v-if="cartMessage" class="cart-message">{{ cartMessage }}</div>
    
    <!-- 加载状态 -->
    <div v-if="isLoading" class="loading">加载中...</div>
    
    <!-- 商品详情内容 -->
    <div v-else-if="product" class="product-detail">
      <!-- 商品图片区 -->
      <div class="product-images">
        <div class="main-image">
          <img 
            :src="product.image ? (product.image.startsWith('http') ? product.image : `http://localhost:8080${product.image}`) : 'https://via.placeholder.com/600x400?text=No+Image'" 
            :alt="product.name"
          />
        </div>
        <!-- 可以添加缩略图轮播功能 -->
      </div>
      
      <!-- 商品信息区 -->
      <div class="product-info">
        <h3 class="product-name">{{ product.name }}</h3>
        <p class="product-price">¥{{ product.price.toFixed(2) }}</p>
        
        <div class="product-stock" :class="{ 'low-stock': product.stock <= 10 }">
          {{ product.stock > 0 ? `库存: ${product.stock}` : '已售罄' }}
        </div>
        
        <div class="quantity-selector">
          <label for="quantity">数量:</label>
          <div class="quantity-controls">
            <button @click="decreaseQuantity" :disabled="quantity <= 1">-</button>
            <input 
              type="number" 
              id="quantity" 
              v-model.number="quantity" 
              :min="1" 
              :max="product.stock"
            />
            <button @click="increaseQuantity" :disabled="quantity >= product.stock">+</button>
          </div>
        </div>
        
        <div class="action-buttons">
          <button 
            @click="addToCart" 
            class="add-cart-btn" 
            :disabled="product.stock <= 0"
          >
            {{ product.stock > 0 ? '加入购物车' : '已售罄' }}
          </button>
          
          <!-- 收藏按钮 -->
          <button 
            @click="toggleFavorite" 
            class="favorite-btn" 
            :class="{ 'active': isFavorite }"
          >
            <span class="favorite-icon">❤</span>
            {{ isFavorite ? '已收藏' : '收藏' }}
          </button>
        </div>
        
        <!-- 收藏操作消息 -->
        <div v-if="favoriteMessage" class="favorite-message">{{ favoriteMessage }}</div>
        

        
        <div class="product-description">
          <h4>商品描述</h4>
          <p>{{ product.description }}</p>
        </div>
        
        <div class="product-meta">
          <p>上架时间: {{ new Date(product.createTime).toLocaleDateString('zh-CN') }}</p>
          <p>销量: {{ product.sales }}</p>
        </div>
      </div>
    </div>
    
    <!-- 商品不存在 -->
    <div v-else class="no-product">商品不存在</div>
    
    <!-- 评论与评分部分 -->
    <div class="comments-section">
      <h3>商品评价</h3>
      
      <!-- 平均评分 -->
      <div class="average-rating">
        <div class="rating-display">
          <span class="rating-score">{{ averageRating }}</span>
          <div class="stars">
            <span v-for="i in 5" :key="i" :class="{ 'filled': i <= Math.round(averageRating) }">★</span>
          </div>
        </div>
        <div class="rating-count">{{ comments.length }}条评价</div>
      </div>
      
      <!-- 提交评论 -->
      <div class="submit-comment">
        <h4>发表评价</h4>
        <div v-if="commentMessage" class="comment-message">{{ commentMessage }}</div>
        
        <div class="rating-selector">
          <label>评分:</label>
          <div class="stars">
            <span v-for="i in 5" :key="i" :class="{ 'filled': i <= commentRating }" @click="commentRating = i">★</span>
          </div>
        </div>
        
        <textarea
          v-model="commentContent"
          placeholder="请输入您的评价..."
          rows="4"
        ></textarea>
        
        <button @click="submitComment" class="submit-btn">提交评价</button>
      </div>
      
      <!-- 评论列表 -->
      <div class="comments-list">
        <h4>用户评价</h4>
        <div v-if="isLoadingComments" class="loading">加载评论中...</div>
        <div v-else-if="comments.length === 0" class="no-comments">暂无评论</div>
        <div v-else>
          <div v-for="comment in comments" :key="comment.id" class="comment-item">
            <div class="comment-header">
              <span class="comment-user">{{ comment.user.username }}</span>
              <div class="comment-rating">
                <span v-for="i in 5" :key="i" :class="{ 'filled': i <= comment.rating }">★</span>
              </div>
              <span class="comment-time">{{ new Date(comment.createTime).toLocaleDateString('zh-CN') }}</span>
            </div>
            <div class="comment-content">{{ comment.content }}</div>
            
            <!-- 商家回复 -->
            <div v-if="comment.reply" class="merchant-reply">
              <strong>商家回复:</strong>
              <p>{{ comment.reply }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
  
  <!-- 登录提示模态框 -->
  <div v-if="showLoginPromptModal" class="login-prompt-overlay">
    <div class="login-prompt-modal">
      <h3>登录提示</h3>
      <p>您还未登录，请选择登录角色：</p>
      <div class="login-options">
        <button @click="handleLoginChoice('1')" class="login-option-btn seller-btn">
          商家登录
        </button>
        <button @click="handleLoginChoice('2')" class="login-option-btn user-btn">
          用户登录
        </button>
      </div>
      <button @click="showLoginPromptModal = false" class="cancel-btn">
        取消
      </button>
    </div>
  </div>
</template>

<style scoped>
.product-detail-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.product-detail-container h2 {
  color: #42b883;
  margin-bottom: 20px;
  text-align: center;
}

.error-message {
  color: #ff4d4f;
  margin-bottom: 15px;
  padding: 10px;
  background-color: #fff2f0;
  border-radius: 4px;
  text-align: center;
}

.cart-message {
  background-color: #e8f5e9;
  color: #4caf50;
  padding: 10px;
  border-radius: 4px;
  margin-bottom: 15px;
  text-align: center;
  font-weight: 500;
  animation: fadeIn 0.3s ease;
}

.loading {
  text-align: center;
  padding: 40px 0;
  color: #666;
  font-size: 16px;
}

.product-detail {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 30px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 20px;
}

.product-images {
  display: flex;
  flex-direction: column;
}

.main-image {
  height: 400px;
  overflow: hidden;
  border-radius: 8px;
  margin-bottom: 15px;
}

.main-image img {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.product-info {
  display: flex;
  flex-direction: column;
}

.product-name {
  font-size: 24px;
  margin-bottom: 15px;
  color: #333;
}

.product-price {
  font-size: 32px;
  font-weight: bold;
  color: #e74c3c;
  margin-bottom: 15px;
}

.product-stock {
  font-size: 16px;
  color: #666;
  margin-bottom: 20px;
}

.product-stock.low-stock {
  color: #ff9800;
  font-weight: 500;
}

.quantity-selector {
  margin-bottom: 20px;
}

.quantity-selector label {
  display: block;
  margin-bottom: 8px;
  color: #333;
  font-weight: 500;
}

.quantity-controls {
  display: flex;
  align-items: center;
  width: 150px;
}

.quantity-controls button {
  width: 40px;
  height: 40px;
  border: 1px solid #ddd;
  background-color: #f5f5f5;
  font-size: 18px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.quantity-controls button:hover:not(:disabled) {
  background-color: #e0e0e0;
}

.quantity-controls button:disabled {
  background-color: #f0f0f0;
  color: #ccc;
  cursor: not-allowed;
}

.quantity-controls input {
  width: 60px;
  height: 40px;
  border: 1px solid #ddd;
  text-align: center;
  font-size: 16px;
}

.add-cart-btn {
  padding: 12px 24px;
  background-color: #42b883;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 18px;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.3s;
  margin-bottom: 30px;
}

.add-cart-btn:hover:not(:disabled) {
  background-color: #35495e;
}

.add-cart-btn:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
  opacity: 0.7;
}

/* 操作按钮容器 */
.action-buttons {
  display: flex;
  gap: 12px;
  margin-bottom: 30px;
}

/* 收藏按钮样式 */
.favorite-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  background-color: white;
  color: #666;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 16px;
  cursor: pointer;
  transition: all 0.3s;
  flex: 1;
}

.favorite-btn:hover {
  background-color: #f9f9f9;
  border-color: #ff4d4f;
  color: #ff4d4f;
}

.favorite-btn.active {
  background-color: #fff0f0;
  border-color: #ff4d4f;
  color: #ff4d4f;
}

.favorite-icon {
  font-size: 20px;
}

.favorite-message {
  background-color: #e8f5e9;
  color: #4caf50;
  padding: 8px;
  border-radius: 4px;
  margin-bottom: 15px;
  text-align: center;
  animation: fadeIn 0.3s ease;
}

.product-description {
  margin-bottom: 20px;
}

.product-description h4 {
  margin-bottom: 10px;
  color: #333;
}

.product-description p {
  color: #666;
  line-height: 1.6;
  white-space: pre-line;
}

.product-meta {
  border-top: 1px solid #eee;
  padding-top: 15px;
  color: #999;
  font-size: 14px;
}

.no-product {
  text-align: center;
  padding: 40px 0;
  color: #666;
  font-size: 16px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 40px;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(-10px); }
  to { opacity: 1; transform: translateY(0); }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .product-detail {
    grid-template-columns: 1fr;
  }
  
  .main-image {
    height: 300px;
  }
}

/* 评论与评分样式 */
.comments-section {
  margin-top: 60px;
  padding-top: 40px;
  border-top: 2px solid #f0f0f0;
}

.comments-section h3 {
  font-size: 24px;
  color: #333;
  margin-bottom: 20px;
}

.average-rating {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 30px;
}

.rating-display {
  display: flex;
  align-items: center;
  gap: 10px;
}

.rating-score {
  font-size: 36px;
  font-weight: bold;
  color: #ff4d4f;
}

.stars {
  color: #e8e8e8;
  font-size: 20px;
}

.stars .filled {
  color: #ff4d4f;
}

.rating-count {
  color: #8c8c8c;
  font-size: 14px;
}

.submit-comment {
  background-color: #f9f9f9;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 40px;
}

.submit-comment h4 {
  font-size: 18px;
  color: #333;
  margin-bottom: 15px;
}

.comment-message {
  background-color: #e8f5e9;
  color: #4caf50;
  padding: 8px;
  border-radius: 4px;
  margin-bottom: 15px;
  text-align: center;
}

.rating-selector {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 15px;
}

.rating-selector label {
  color: #333;
  font-weight: bold;
}

.rating-selector .stars {
  cursor: pointer;
}

.submit-comment textarea {
  width: 100%;
  padding: 12px;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  resize: vertical;
  min-height: 100px;
  margin-bottom: 15px;
  font-family: inherit;
}

.submit-btn {
  background-color: #42b883;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
  transition: background-color 0.3s;
}

.submit-btn:hover {
  background-color: #35495e;
}

.comments-list h4 {
  font-size: 18px;
  color: #333;
  margin-bottom: 20px;
}

.no-comments {
  text-align: center;
  color: #8c8c8c;
  padding: 40px;
  background-color: #f9f9f9;
  border-radius: 8px;
}

.comment-item {
  border-bottom: 1px solid #f0f0f0;
  padding: 20px 0;
}

.comment-header {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 10px;
}

.comment-user {
  font-weight: bold;
  color: #333;
}

.comment-time {
  color: #8c8c8c;
  font-size: 14px;
}

.comment-content {
  color: #666;
  line-height: 1.6;
  margin-bottom: 15px;
}

.merchant-reply {
  background-color: #f5f5f5;
  padding: 10px;
  border-radius: 4px;
  margin-top: 10px;
}

.merchant-reply strong {
  color: #42b883;
}

/* 登录提示模态框样式 */
.login-prompt-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 2000;
}

.login-prompt-modal {
  background-color: white;
  padding: 30px;
  border-radius: 12px;
  width: 90%;
  max-width: 400px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  text-align: center;
}

.login-prompt-modal h3 {
  margin-top: 0;
  margin-bottom: 20px;
  color: #42b883;
  font-size: 18px;
  font-weight: 600;
}

.login-prompt-modal p {
  margin-bottom: 30px;
  color: #666;
  font-size: 14px;
}

.login-options {
  display: flex;
  flex-direction: column;
  gap: 15px;
  margin-bottom: 25px;
}

.login-option-btn {
  padding: 12px 20px;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.seller-btn {
  background-color: #1890ff;
  color: white;
}

.seller-btn:hover {
  background-color: #40a9ff;
  transform: translateY(-2px);
  box-shadow: 0 2px 8px rgba(24, 144, 255, 0.3);
}

.user-btn {
  background-color: #52c41a;
  color: white;
}

.user-btn:hover {
  background-color: #73d13d;
  transform: translateY(-2px);
  box-shadow: 0 2px 8px rgba(82, 196, 26, 0.3);
}

.login-prompt-modal .cancel-btn {
  width: 100%;
  padding: 10px;
  background-color: #f0f0f0;
  color: #666;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.login-prompt-modal .cancel-btn:hover {
  background-color: #e0e0e0;
}
</style>