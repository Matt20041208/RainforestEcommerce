<script setup>
import { ref, onMounted } from 'vue'
import { get, post } from '@/utils/request'

// 用户信息
const currentUser = ref(null)

// 商品列表
const products = ref([])
const categories = ref([])
const isLoading = ref(false)
const errorMessage = ref('')
const searchKeyword = ref('')
const selectedCategory = ref('')

// 增强筛选条件
const priceRange = ref({
  min: '',
  max: ''
})
const sortBy = ref('') // price-asc, price-desc, sales-asc, sales-desc, rating-desc
const searchHistory = ref([])

// 分页相关
const pagination = ref({
  currentPage: 1,
  pageSize: 12,
  totalPages: 1,
  totalItems: 0
})

// 购物车操作反馈
const cartMessage = ref('')

// 购物车数量选择弹窗
const cartModalVisible = ref(false)
const selectedProduct = ref(null)
const selectedQuantity = ref(1)

// 保存搜索历史
const saveSearchHistory = (keyword) => {
  if (keyword && !searchHistory.value.includes(keyword)) {
    searchHistory.value.unshift(keyword)
    // 限制历史记录数量
    if (searchHistory.value.length > 10) {
      searchHistory.value.pop()
    }
    // 可以保存到localStorage
    localStorage.setItem('searchHistory', JSON.stringify(searchHistory.value))
  }
}

// 清除搜索历史
const clearSearchHistory = () => {
  searchHistory.value = []
  localStorage.removeItem('searchHistory')
}

// 加载搜索历史
const loadSearchHistory = () => {
  const history = localStorage.getItem('searchHistory')
  if (history) {
    searchHistory.value = JSON.parse(history)
  }
}

// 获取所有分类
const fetchCategories = async () => {
  try {
    categories.value = await get('/api/category')
  } catch (error) {
    console.error('获取分类失败:', error)
  }
}

// 获取在售商品列表
const fetchProducts = async () => {
  isLoading.value = true
  errorMessage.value = ''
  try {
    const url = '/api/product/filter'
    const params = new URLSearchParams()
    
    if (searchKeyword.value) {
      params.append('keyword', searchKeyword.value)
      // 保存搜索历史
      saveSearchHistory(searchKeyword.value)
    }
    
    // 分类筛选
    if (selectedCategory.value) {
      params.append('categoryId', selectedCategory.value)
    }
    
    // 价格区间
    if (priceRange.value.min !== '') {
      params.append('minPrice', priceRange.value.min)
    }
    if (priceRange.value.max !== '') {
      params.append('maxPrice', priceRange.value.max)
    }
    
    // 排序
    if (sortBy.value) {
      params.append('sortBy', sortBy.value)
    }
    
    // 分页参数
    params.append('page', pagination.value.currentPage)
    params.append('pageSize', pagination.value.pageSize)
    
    const fullUrl = `${url}?${params.toString()}`
    const data = await get(fullUrl)
    
    // 检查是否是分页响应格式
    if (data.content && Array.isArray(data.content)) {
      // 分页响应格式
      products.value = data.content
      pagination.value.totalItems = data.totalElements
      pagination.value.totalPages = data.totalPages
      pagination.value.currentPage = data.number + 1 // 后端通常从0开始，前端从1开始
    } else {
      // 非分页响应格式（兼容旧接口）
      products.value = data
      pagination.value.totalItems = data.length
      pagination.value.totalPages = 1
      pagination.value.currentPage = 1
    }
  } catch (error) {
    console.error('获取商品列表失败:', error)
    errorMessage.value = '获取商品列表失败'
  } finally {
    isLoading.value = false
  }
}

// 打开加入购物车弹窗
const openCartModal = (product) => {
  selectedProduct.value = product
  selectedQuantity.value = 1
  cartModalVisible.value = true
}

// 确认加入购物车
const confirmAddToCart = async () => {
  if (!selectedProduct.value) return
  
  // 检查用户是否登录
  if (!currentUser.value) {
    // 未登录，显示登录提示
    showLoginPrompt()
    cartModalVisible.value = false
    return
  }
  
  try {
    await post('/api/cart/add', {
      productId: selectedProduct.value.id,
      quantity: selectedQuantity.value
    })
    
    cartMessage.value = `已将${selectedQuantity.value}件"${selectedProduct.value.name}"加入购物车`
    cartModalVisible.value = false
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

// 关闭加入购物车弹窗
const closeCartModal = () => {
  cartModalVisible.value = false
  selectedProduct.value = null
  selectedQuantity.value = 1
}

// 过滤商品
const filterProducts = () => {
  fetchProducts()
}

// 重置筛选条件
const resetFilters = () => {
  searchKeyword.value = ''
  selectedCategory.value = ''
  priceRange.value = {
    min: '',
    max: ''
  }
  sortBy.value = ''
  fetchProducts()
}

// 页面加载时获取数据
onMounted(() => {
  // 获取用户信息
  const user = localStorage.getItem('user')
  if (user) {
    currentUser.value = JSON.parse(user)
  }
  
  fetchCategories()
  loadSearchHistory()
  fetchProducts()
})

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
</script>

<template>
  <div class="product-store-container">
    <h2>商品商城</h2>
    
    <!-- 筛选和搜索 -->
    <div class="filter-section">
      <div class="search-box">
        <input 
          type="text" 
          v-model="searchKeyword" 
          placeholder="搜索商品..."
        />
        <select v-model="selectedCategory">
          <option value="">全部分类</option>
          <option v-for="category in categories" :key="category.id" :value="category.id">
            {{ category.name }}
          </option>
        </select>
        <button @click="filterProducts" class="filter-btn">筛选</button>
        <button @click="resetFilters" class="reset-btn">重置</button>
      </div>
      
      <!-- 增强筛选条件 -->
      <div class="enhanced-filters">
        <!-- 价格区间筛选 -->
        <div class="price-filter">
          <h4>价格区间</h4>
          <div class="price-range">
            <input 
              type="number" 
              v-model.number="priceRange.min" 
              placeholder="最低价"
              min="0"
            />
            <span>-</span>
            <input 
              type="number" 
              v-model.number="priceRange.max" 
              placeholder="最高价"
              min="1"
            />
          </div>
        </div>
        
        <!-- 排序选项 -->
        <div class="sort-options">
          <h4>排序方式</h4>
          <select v-model="sortBy">
            <option value="">默认排序</option>
            <option value="price-asc">价格从低到高</option>
            <option value="price-desc">价格从高到低</option>
            <option value="sales-desc">销量从高到低</option>
          </select>
        </div>
      </div>
      
      <!-- 搜索历史 -->
      <div v-if="searchHistory.length > 0" class="search-history">
        <div class="history-header">
          <h4>搜索历史</h4>
          <button class="clear-history-btn" @click="clearSearchHistory">清除历史</button>
        </div>
        <div class="history-tags">
          <span 
            v-for="(keyword, index) in searchHistory" 
            :key="index" 
            class="history-tag"
            @click="searchKeyword = keyword; fetchProducts()"
          >
            {{ keyword }}
          </span>
        </div>
      </div>
    </div>
    
    <!-- 购物车操作消息 -->
    <div v-if="cartMessage" class="cart-message">{{ cartMessage }}</div>
    
    <!-- 错误提示 -->
    <div v-if="errorMessage" class="error-message">{{ errorMessage }}</div>
    
    <!-- 商品列表 -->
    <div v-if="isLoading" class="loading">加载中...</div>
    <div v-else-if="products.length === 0" class="no-products">暂无商品</div>
    <div v-else class="product-grid">
      <div v-for="product in products" :key="product.id" class="product-card" @click="$router.push(`/product/${product.id}`)">
        <div class="product-image">
          <img 
            :src="product.image ? (product.image.startsWith('http') ? product.image : `http://localhost:8080${product.image}`) : 'https://via.placeholder.com/200?text=No+Image'" 
            :alt="product.name"
          />
        </div>
        <div class="product-info">
          <h3 class="product-name">{{ product.name }}</h3>
          <p class="product-description">{{ product.description }}</p>
          <p class="product-price">¥{{ product.price.toFixed(2) }}</p>
          <p class="product-stock" :class="{ 'low-stock': product.stock <= 10 }">
            {{ product.stock > 0 ? `库存: ${product.stock}` : '已售罄' }}
          </p>
          <div class="product-actions">
            <button 
              @click.stop="openCartModal(product)" 
              class="add-cart-btn" 
              :disabled="product.stock <= 0"
            >
              {{ product.stock > 0 ? '加入购物车' : '已售罄' }}
            </button>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 分页控件 -->
    <div v-if="products.length > 0" class="pagination">
      <button 
        @click="() => { pagination.currentPage = 1; fetchProducts(); }" 
        :disabled="pagination.currentPage === 1"
        class="page-btn"
      >
        首页
      </button>
      <button 
        @click="() => { pagination.currentPage--; fetchProducts(); }" 
        :disabled="pagination.currentPage === 1"
        class="page-btn"
      >
        上一页
      </button>
      
      <!-- 简单页码逻辑：当总页数小于等于5时，显示所有页码 -->
      <template v-if="pagination.totalPages <= 5">
        <span v-for="page in pagination.totalPages" :key="page" class="page-number" :class="{ active: page === pagination.currentPage }" @click="() => { pagination.currentPage = page; fetchProducts(); }">
          {{ page }}
        </span>
      </template>
      
      <!-- 复杂页码逻辑：当总页数大于5时，显示部分页码 -->
      <template v-else>
        <span v-for="page in 3" :key="page" class="page-number" :class="{ active: page === pagination.currentPage }" @click="() => { pagination.currentPage = page; fetchProducts(); }">
          {{ page }}
        </span>
        
        <span v-if="pagination.currentPage > 4" class="page-ellipsis">...</span>
        
        <span v-if="pagination.currentPage > 3 && pagination.currentPage < pagination.totalPages - 2" class="page-number" :class="{ active: true }" @click="() => { fetchProducts(); }">
          {{ pagination.currentPage }}
        </span>
        
        <span v-if="pagination.currentPage < pagination.totalPages - 3" class="page-ellipsis">...</span>
        
        <span v-for="page in [pagination.totalPages - 2, pagination.totalPages - 1, pagination.totalPages]" :key="page" class="page-number" :class="{ active: page === pagination.currentPage }" @click="() => { pagination.currentPage = page; fetchProducts(); }">
          {{ page }}
        </span>
      </template>
      
      <button 
        @click="() => { pagination.currentPage++; fetchProducts(); }" 
        :disabled="pagination.currentPage === pagination.totalPages"
        class="page-btn"
      >
        下一页
      </button>
      <button 
        @click="() => { pagination.currentPage = pagination.totalPages; fetchProducts(); }" 
        :disabled="pagination.currentPage === pagination.totalPages"
        class="page-btn"
      >
        末页
      </button>
      
      <span class="page-info">
        第 {{ pagination.currentPage }}/{{ pagination.totalPages }} 页，共 {{ pagination.totalItems }} 件商品
      </span>
    </div>
  </div>

  <!-- 加入购物车弹窗 -->
  <div v-if="cartModalVisible" class="cart-modal-overlay" @click="closeCartModal">
    <div class="cart-modal" @click.stop>
      <div class="cart-modal-header">
        <h3>加入购物车</h3>
        <button class="close-btn" @click="closeCartModal">&times;</button>
      </div>
      <div class="cart-modal-content">
        <div v-if="selectedProduct" class="product-info">
          <h4>{{ selectedProduct.name }}</h4>
          <p class="product-price">¥{{ selectedProduct.price.toFixed(2) }}</p>
          <p class="product-stock">库存: {{ selectedProduct.stock }}</p>
          
          <div class="quantity-control-modal">
            <label for="quantity">选择数量:</label>
            <div class="quantity-controls">
              <button 
                @click="selectedQuantity = Math.max(1, selectedQuantity - 1)" 
                class="quantity-btn"
              >
                -
              </button>
              <input 
                type="number" 
                id="quantity"
                v-model.number="selectedQuantity" 
                min="1" 
                :max="selectedProduct.stock"
                class="quantity-input"
              />
              <button 
                @click="selectedQuantity = Math.min(selectedProduct.stock, selectedQuantity + 1)" 
                class="quantity-btn"
              >
                +
              </button>
            </div>
          </div>
        </div>
      </div>
      <div class="cart-modal-footer">
        <button class="cancel-btn" @click="closeCartModal">取消</button>
        <button class="confirm-btn" @click="confirmAddToCart">确认加入购物车</button>
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
.product-store-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

h2 {
  color: #42b883;
  margin-bottom: 20px;
  text-align: center;
}

.filter-section {
  margin-bottom: 20px;
  padding: 15px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.search-box {
  display: flex;
  gap: 10px;
  align-items: center;
  margin-bottom: 20px;
  flex-wrap: wrap;
  justify-content: center;
}

/* 增强筛选条件样式 */
.enhanced-filters {
  display: flex;
  gap: 30px;
  margin-bottom: 20px;
  padding-top: 15px;
  border-top: 1px solid #eee;
  flex-wrap: wrap;
}

.price-filter, .sort-options {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.price-filter h4, .sort-options h4 {
  margin: 0;
  font-size: 16px;
  color: #333;
  font-weight: 500;
}

.price-range {
  display: flex;
  align-items: center;
  gap: 10px;
}

.price-range input {
  width: 100px;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.sort-options select {
  width: 180px;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

/* 搜索历史样式 */
.search-history {
  padding-top: 15px;
  border-top: 1px solid #eee;
}

.search-history .history-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.search-history h4 {
  margin: 0;
  font-size: 16px;
  color: #333;
  font-weight: 500;
}

.clear-history-btn {
  background: none;
  border: none;
  color: #999;
  font-size: 14px;
  cursor: pointer;
  padding: 5px 10px;
  border-radius: 4px;
  transition: all 0.3s;
}

.clear-history-btn:hover {
  background-color: #f5f5f5;
  color: #666;
}

.history-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.history-tag {
  padding: 6px 12px;
  background-color: #f5f5f5;
  border: 1px solid #ddd;
  border-radius: 15px;
  font-size: 14px;
  color: #666;
  cursor: pointer;
  transition: background-color 0.3s;
}

.history-tag:hover {
  background-color: #e0e0e0;
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
  background-color: #42b883;
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

.search-box input,
.search-box select {
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.search-box input {
  width: 250px;
}

.filter-btn,
.reset-btn {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.filter-btn {
  background-color: #42b883;
  color: white;
}

.filter-btn:hover {
  background-color: #35495e;
}

.reset-btn {
  background-color: #f0f0f0;
  color: #333;
}

.reset-btn:hover {
  background-color: #e0e0e0;
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

.error-message {
  color: #ff4d4f;
  margin-bottom: 15px;
  padding: 10px;
  background-color: #fff2f0;
  border-radius: 4px;
  text-align: center;
}

.loading {
  text-align: center;
  padding: 40px 0;
  color: #666;
  font-size: 16px;
}

.no-products {
  text-align: center;
  padding: 40px 0;
  color: #666;
  font-size: 16px;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 25px;
}

.product-card {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  transition: transform 0.3s, box-shadow 0.3s;
}

.product-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
}

.product-image {
  height: 200px;
  overflow: hidden;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}

.product-card:hover .product-image img {
  transform: scale(1.05);
}

.product-info {
  padding: 15px;
}

.product-name {
  font-size: 18px;
  margin-bottom: 8px;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.product-description {
  font-size: 14px;
  color: #666;
  margin-bottom: 10px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  line-height: 1.4;
}

.product-price {
  font-size: 22px;
  font-weight: bold;
  color: #e74c3c;
  margin-bottom: 8px;
}

.product-stock {
  font-size: 14px;
  color: #666;
  margin-bottom: 15px;
}

.product-stock.low-stock {
  color: #ff9800;
  font-weight: 500;
}

.product-actions {
  display: flex;
  justify-content: center;
}

.add-cart-btn {
  width: 100%;
  padding: 10px;
  background-color: #42b883;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.3s;
}

.add-cart-btn:hover:not(:disabled) {
  background-color: #35495e;
}

.add-cart-btn:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
  opacity: 0.7;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(-10px); }
  to { opacity: 1; transform: translateY(0); }
}

/* 加入购物车弹窗样式 */
.cart-modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.cart-modal {
  background-color: white;
  border-radius: 8px;
  width: 90%;
  max-width: 400px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  animation: fadeIn 0.3s ease;
}

.cart-modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  border-bottom: 1px solid #eee;
}

.cart-modal-header h3 {
  margin: 0;
  color: #42b883;
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #999;
  padding: 0;
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.close-btn:hover {
  background-color: #f5f5f5;
}

.cart-modal-content {
  padding: 20px;
}

.product-info h4 {
  margin-top: 0;
  margin-bottom: 10px;
  color: #333;
}

.product-price {
  font-size: 18px;
  font-weight: 500;
  color: #e74c3c;
  margin: 10px 0;
}

.product-stock {
  color: #666;
  margin: 10px 0;
}

.quantity-control-modal {
  margin-top: 20px;
}

.quantity-control-modal label {
  display: block;
  margin-bottom: 10px;
  color: #333;
  font-weight: 500;
}

.quantity-controls {
  display: flex;
  align-items: center;
  gap: 10px;
}

.quantity-controls .quantity-btn {
  width: 40px;
  height: 40px;
  border: 1px solid #42b883;
  background-color: white;
  color: #42b883;
  border-radius: 4px;
  font-size: 20px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s;
}

.quantity-controls .quantity-btn:hover {
  background-color: #42b883;
  color: white;
}

.quantity-controls .quantity-input {
  width: 70px;
  height: 40px;
  text-align: center;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 16px;
}

.cart-modal-footer {
  display: flex;
  gap: 10px;
  padding: 15px 20px;
  border-top: 1px solid #eee;
  justify-content: flex-end;
}

.cancel-btn {
  padding: 10px 20px;
  background-color: white;
  color: #666;
  border: 1px solid #ddd;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
}

.cancel-btn:hover {
  background-color: #f5f5f5;
}

.confirm-btn {
  padding: 10px 20px;
  background-color: #42b883;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.confirm-btn:hover {
  background-color: #35495e;
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