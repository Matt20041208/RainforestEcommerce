<script setup>
import { ref, reactive, onMounted } from 'vue'

const products = ref([])
const categories = ref([])
const isLoading = ref(false)
const errorMessage = ref('')
const searchKeyword = ref('')
const selectedCategory = ref('')

// 商品表单数据
const productForm = reactive({
  id: null,
  name: '',
  description: '',
  image: '',
  price: '',
  stock: '',
  enabled: true,
  categoryId: ''
})

// 模态框状态
const showModal = ref(false)
const modalTitle = ref('添加商品')

// 上传图片相关
const imagePreview = ref('')
const imageFile = ref(null)

// 获取所有分类
const fetchCategories = async () => {
  try {
    const response = await fetch('/api/category')
    if (response.ok) {
      categories.value = await response.json()
    } else {
      console.error('获取分类失败')
    }
  } catch (error) {
    console.error('获取分类失败:', error)
  }
}

// 获取商品列表
const fetchProducts = async () => {
  isLoading.value = true
  errorMessage.value = ''
  try {
    let url = '/api/product/filter'
    const params = new URLSearchParams()
    
    if (searchKeyword.value) {
      params.append('keyword', searchKeyword.value)
    }
    if (selectedCategory.value) {
      params.append('categoryId', selectedCategory.value)
    }
    // 添加默认分页参数
    params.append('page', '1')
    params.append('pageSize', '100')
    
    if (params.toString()) {
      url += '?' + params.toString()
    }
    
    const response = await fetch(url)
    if (response.ok) {
      const data = await response.json()
      products.value = data.content || []
    } else {
      errorMessage.value = '获取商品列表失败'
    }
  } catch (error) {
    console.error('获取商品列表失败:', error)
    errorMessage.value = '获取商品列表失败'
  } finally {
    isLoading.value = false
  }
}

// 打开添加商品模态框
const openAddModal = () => {
  modalTitle.value = '添加商品'
  resetForm()
  showModal.value = true
}

// 打开编辑商品模态框
const openEditModal = (product) => {
  modalTitle.value = '编辑商品'
  // 填充表单数据
  productForm.id = product.id
  productForm.name = product.name
  productForm.description = product.description
  productForm.image = product.image
  productForm.price = product.price
  productForm.stock = product.stock
  productForm.enabled = product.enabled
  productForm.categoryId = product.category?.id || ''
  
  // 设置图片预览
  if (product.image) {
    imagePreview.value = product.image.startsWith('http') ? product.image : `http://localhost:8080${product.image}`
  } else {
    imagePreview.value = ''
  }
  
  showModal.value = true
}

// 重置表单
const resetForm = () => {
  productForm.id = null
  productForm.name = ''
  productForm.description = ''
  productForm.image = ''
  productForm.price = ''
  productForm.stock = ''
  productForm.enabled = true
  productForm.categoryId = ''
  imagePreview.value = ''
  imageFile.value = null
}

// 处理图片上传
const handleImageUpload = async (event) => {
  const file = event.target.files[0]
  if (!file) return
  
  // 检查文件类型
  if (!file.type.startsWith('image/')) {
    alert('只能上传图片文件')
    return
  }
  
  // 显示预览
  const reader = new FileReader()
  reader.onload = (e) => {
    imagePreview.value = e.target.result
  }
  reader.readAsDataURL(file)
  
  imageFile.value = file
  
  // 上传图片到服务器
  try {
    const formData = new FormData()
    formData.append('file', file)
    
    const response = await fetch('/api/product/upload-image', {
      method: 'POST',
      body: formData
    })
    
    if (response.ok) {
      const data = await response.json()
      productForm.image = data.url
    } else {
      const error = await response.text()
      alert('图片上传失败: ' + error)
    }
  } catch (error) {
    console.error('图片上传失败:', error)
    alert('图片上传失败')
  }
}

// 保存商品
const saveProduct = async () => {
  // 表单验证
  if (!productForm.name || !productForm.price || !productForm.stock || !productForm.categoryId) {
    alert('请填写必填字段')
    return
  }
  
  try {
    const productData = {
      ...productForm,
      category: { id: productForm.categoryId }
    }
    
    let response
    if (productForm.id) {
      // 更新商品
      response = await fetch(`/api/product/${productForm.id}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(productData)
      })
    } else {
      // 添加商品
      response = await fetch('/api/product', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(productData)
      })
    }
    
    if (response.ok) {
      showModal.value = false
      fetchProducts()
      alert(productForm.id ? '商品更新成功' : '商品添加成功')
    } else {
      const error = await response.text()
      alert('保存失败: ' + error)
    }
  } catch (error) {
    console.error('保存商品失败:', error)
    alert('保存商品失败')
  }
}

// 删除商品
const deleteProduct = async (productId, productName) => {
  if (confirm(`确定要删除商品 "${productName}" 吗？`)) {
    try {
      const response = await fetch(`/api/product/${productId}`, {
        method: 'DELETE'
      })
      
      if (response.ok) {
        fetchProducts()
        alert('商品删除成功')
      } else {
        const error = await response.text()
        alert('删除失败: ' + error)
      }
    } catch (error) {
      console.error('删除商品失败:', error)
      alert('删除商品失败')
    }
  }
}

// 过滤商品
const filterProducts = () => {
  fetchProducts()
}

// 重置筛选条件
const resetFilters = () => {
  searchKeyword.value = ''
  selectedCategory.value = ''
  fetchProducts()
}

// 页面加载时获取数据
onMounted(() => {
  fetchCategories()
  fetchProducts()
})
</script>

<template>
  <div class="product-list-container">
    <h2>商品管理</h2>
    
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
      <button @click="openAddModal" class="add-btn">添加商品</button>
    </div>
    
    <!-- 错误提示 -->
    <div v-if="errorMessage" class="error-message">{{ errorMessage }}</div>
    
    <!-- 商品列表 -->
    <div v-if="isLoading" class="loading">加载中...</div>
    <div v-else class="product-grid">
      <div v-for="product in products" :key="product.id" class="product-card">
        <div class="product-image">
          <img 
            :src="product.image ? (product.image.startsWith('http') ? product.image : `http://localhost:8080${product.image}`) : 'https://via.placeholder.com/200?text=No+Image'" 
            :alt="product.name"
          />
        </div>
        <div class="product-info">
          <h3 class="product-name">{{ product.name }}</h3>
          <p class="product-price">¥{{ product.price }}</p>
          <p class="product-stock">库存: {{ product.stock }}</p>
          <p class="product-status" :class="{ 'enabled': product.enabled, 'disabled': !product.enabled }">
            {{ product.enabled ? '在售' : '下架' }}
          </p>
          <div class="product-actions">
            <button @click="openEditModal(product)" class="edit-btn">编辑</button>
            <button @click="deleteProduct(product.id, product.name)" class="delete-btn">删除</button>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 商品表单模态框 -->
    <div v-if="showModal" class="modal-overlay">
      <div class="modal">
        <div class="modal-header">
          <h3>{{ modalTitle }}</h3>
          <button @click="showModal = false" class="close-btn">×</button>
        </div>
        <div class="modal-body">
          <form @submit.prevent="saveProduct">
            <div class="form-group">
              <label>商品名称 *</label>
              <input type="text" v-model="productForm.name" required />
            </div>
            
            <div class="form-group">
              <label>商品描述</label>
              <textarea v-model="productForm.description" rows="3"></textarea>
            </div>
            
            <div class="form-group">
              <label>商品图片</label>
              <div class="image-upload-container">
                <input 
                  ref="fileInput"
                  type="file" 
                  accept="image/*" 
                  @change="handleImageUpload"
                  class="image-upload-input"
                />
                <button type="button" class="image-upload-btn" @click="$refs.fileInput.click()">选择图片</button>
              </div>
              <div v-if="imagePreview" class="image-preview">
                <img :src="imagePreview" alt="预览" />
              </div>
            </div>
            
            <div class="form-group">
              <label>商品价格 *</label>
              <input type="number" v-model="productForm.price" step="0.01" min="0" required />
            </div>
            
            <div class="form-group">
              <label>库存数量 *</label>
              <input type="number" v-model="productForm.stock" min="0" required />
            </div>
            
            <div class="form-group">
              <label>分类 *</label>
              <select v-model="productForm.categoryId" required>
                <option value="">请选择分类</option>
                <option v-for="category in categories" :key="category.id" :value="category.id">
                  {{ category.name }}
                </option>
              </select>
            </div>
            
            <div class="form-group">
              <label>是否启用</label>
              <input type="checkbox" v-model="productForm.enabled" />
            </div>
            
            <div class="modal-actions">
              <button type="button" @click="showModal = false" class="cancel-btn">取消</button>
              <button type="submit" class="save-btn">保存</button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.product-list-container {
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
  display: flex;
  justify-content: space-between;
  align-items: center;
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
}

.search-box input,
.search-box select {
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.search-box input {
  width: 200px;
}

.filter-btn,
.reset-btn,
.add-btn {
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

.add-btn {
  background-color: #42b883;
  color: white;
  font-size: 16px;
  padding: 10px 20px;
}

.add-btn:hover {
  background-color: #35495e;
}

.error-message {
  color: #ff4d4f;
  margin-bottom: 15px;
  padding: 10px;
  background-color: #fff2f0;
  border-radius: 4px;
}

.loading {
  text-align: center;
  padding: 20px;
  color: #666;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
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
}

.product-info {
  padding: 15px;
}

.product-name {
  font-size: 18px;
  margin-bottom: 8px;
  color: #333;
}

.product-price {
  font-size: 20px;
  font-weight: bold;
  color: #e74c3c;
  margin-bottom: 5px;
}

.product-stock {
  font-size: 14px;
  color: #666;
  margin-bottom: 5px;
}

.product-status {
  font-size: 12px;
  padding: 2px 6px;
  border-radius: 10px;
  display: inline-block;
  margin-bottom: 10px;
}

.product-status.enabled {
  background-color: #e8f5e9;
  color: #4caf50;
}

.product-status.disabled {
  background-color: #ffebee;
  color: #f44336;
}

.product-actions {
  display: flex;
  gap: 10px;
}

.edit-btn,
.delete-btn {
  flex: 1;
  padding: 8px;
  border: none;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.edit-btn {
  background-color: #42b883;
  color: white;
}

.edit-btn:hover {
  background-color: #35495e;
}

.delete-btn {
  background-color: #ff4d4f;
  color: white;
}

.delete-btn:hover {
  background-color: #ff7875;
}

/* 模态框样式 */
.modal-overlay {
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

.modal {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.3);
  width: 90%;
  max-width: 500px;
  max-height: 90vh;
  overflow-y: auto;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  border-bottom: 1px solid #eee;
}

.modal-header h3 {
  margin: 0;
  color: #333;
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #666;
  padding: 0;
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 4px;
}

.close-btn:hover {
  background-color: #f0f0f0;
}

.modal-body {
  padding: 15px;
}

.form-group {
  margin-bottom: 15px;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
  font-weight: 500;
  color: #333;
}

.form-group input,
.form-group textarea,
.form-group select {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.form-group textarea {
  resize: vertical;
  min-height: 80px;
}

.image-upload-container {
  display: flex;
  gap: 10px;
  align-items: center;
}

.image-upload-input {
  display: none;
}

.image-upload-btn {
  padding: 8px 16px;
  background-color: #42b883;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.image-upload-btn:hover {
  background-color: #35495e;
}

.image-preview {
  margin-top: 10px;
  max-width: 200px;
  max-height: 200px;
  overflow: hidden;
}

.image-preview img {
  width: 100%;
  height: auto;
  border-radius: 4px;
}

.modal-actions {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
  margin-top: 20px;
}

.cancel-btn,
.save-btn {
  padding: 10px 20px;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.cancel-btn {
  background-color: #f0f0f0;
  color: #333;
}

.cancel-btn:hover {
  background-color: #e0e0e0;
}

.save-btn {
  background-color: #42b883;
  color: white;
}

.save-btn:hover {
  background-color: #35495e;
}
</style>