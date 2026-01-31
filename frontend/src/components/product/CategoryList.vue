<script setup>
import { ref, reactive, onMounted } from 'vue'
import { get, post, put, del } from '@/utils/request'

const categories = ref([])
const isLoading = ref(false)
const errorMessage = ref('')

// 分类表单数据
const categoryForm = reactive({
  id: null,
  name: '',
  description: '',
  sortOrder: 0,
  enabled: true
})

// 模态框状态
const showModal = ref(false)
const modalTitle = ref('添加分类')

// 获取分类列表
const fetchCategories = async () => {
  isLoading.value = true
  errorMessage.value = ''
  try {
    categories.value = await get('/api/category')
  } catch (error) {
    console.error('获取分类列表失败:', error)
    errorMessage.value = '获取分类列表失败'
  } finally {
    isLoading.value = false
  }
}

// 打开添加分类模态框
const openAddModal = () => {
  modalTitle.value = '添加分类'
  resetForm()
  showModal.value = true
}

// 打开编辑分类模态框
const openEditModal = (category) => {
  modalTitle.value = '编辑分类'
  // 填充表单数据
  categoryForm.id = category.id
  categoryForm.name = category.name
  categoryForm.description = category.description
  categoryForm.sortOrder = category.sortOrder || 0
  categoryForm.enabled = category.enabled
  showModal.value = true
}

// 重置表单
const resetForm = () => {
  categoryForm.id = null
  categoryForm.name = ''
  categoryForm.description = ''
  categoryForm.sortOrder = 0
  categoryForm.enabled = true
}

// 保存分类
const saveCategory = async () => {
  // 表单验证
  if (!categoryForm.name) {
    alert('请填写分类名称')
    return
  }
  
  try {
    if (categoryForm.id) {
      // 更新分类
      await put(`/api/category/${categoryForm.id}`, categoryForm)
    } else {
      // 添加分类
      await post('/api/category', categoryForm)
    }
    
    showModal.value = false
    fetchCategories()
    alert(categoryForm.id ? '分类更新成功' : '分类添加成功')
  } catch (error) {
    console.error('保存分类失败:', error)
    alert('保存分类失败')
  }
}

// 删除分类
const deleteCategory = async (categoryId, categoryName) => {
  if (confirm(`确定要删除分类 "${categoryName}" 吗？`)) {
    try {
      await del(`/api/category/${categoryId}`)
      fetchCategories()
      alert('分类删除成功')
    } catch (error) {
      console.error('删除分类失败:', error)
      alert('删除分类失败')
    }
  }
}

// 页面加载时获取数据
onMounted(() => {
  fetchCategories()
})
</script>

<template>
  <div class="category-list-container">
    <h2>分类管理</h2>
    
    <!-- 错误提示 -->
    <div v-if="errorMessage" class="error-message">{{ errorMessage }}</div>
    
    <!-- 操作按钮 -->
    <div class="action-section">
      <button @click="openAddModal" class="add-btn">添加分类</button>
    </div>
    
    <!-- 分类列表 -->
    <div v-if="isLoading" class="loading">加载中...</div>
    <div v-else class="category-table-container">
      <table class="category-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>名称</th>
            <th>描述</th>
            <th>排序</th>
            <th>状态</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="category in categories" :key="category.id">
            <td>{{ category.id }}</td>
            <td>{{ category.name }}</td>
            <td>{{ category.description || '-' }}</td>
            <td>{{ category.sortOrder || 0 }}</td>
            <td>
              <span class="status" :class="{ 'enabled': category.enabled, 'disabled': !category.enabled }">
                {{ category.enabled ? '启用' : '禁用' }}
              </span>
            </td>
            <td>
              <div class="actions">
                <button @click="openEditModal(category)" class="edit-btn">编辑</button>
                <button @click="deleteCategory(category.id, category.name)" class="delete-btn">删除</button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
      
      <!-- 空状态 -->
      <div v-if="categories.length === 0" class="empty-state">
        暂无分类数据
      </div>
    </div>
    
    <!-- 分类表单模态框 -->
    <div v-if="showModal" class="modal-overlay">
      <div class="modal">
        <div class="modal-header">
          <h3>{{ modalTitle }}</h3>
          <button @click="showModal = false" class="close-btn">×</button>
        </div>
        <div class="modal-body">
          <form @submit.prevent="saveCategory">
            <div class="form-group">
              <label>分类名称 *</label>
              <input type="text" v-model="categoryForm.name" required />
            </div>
            
            <div class="form-group">
              <label>分类描述</label>
              <textarea v-model="categoryForm.description" rows="3"></textarea>
            </div>
            
            <div class="form-group">
              <label>排序</label>
              <input type="number" v-model="categoryForm.sortOrder" step="1" min="0" />
            </div>
            
            <div class="form-group">
              <label>是否启用</label>
              <input type="checkbox" v-model="categoryForm.enabled" />
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
.category-list-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

h2 {
  text-align: center;
  color: #42b883;
  margin-bottom: 20px;
}

.error-message {
  color: #ff4d4f;
  margin-bottom: 15px;
  padding: 10px;
  background-color: #fff2f0;
  border-radius: 4px;
}

.action-section {
  margin-bottom: 20px;
  text-align: right;
}

.add-btn {
  padding: 10px 20px;
  background-color: #42b883;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.add-btn:hover {
  background-color: #35495e;
}

.loading {
  text-align: center;
  padding: 20px;
  color: #666;
}

.category-table-container {
  overflow-x: auto;
}

.category-table {
  width: 100%;
  border-collapse: collapse;
}

.category-table th,
.category-table td {
  padding: 12px;
  text-align: left;
  border-bottom: 1px solid #eee;
}

.category-table th {
  background-color: #f5f5f5;
  font-weight: 500;
  color: #333;
}

.category-table tr:hover {
  background-color: #f9f9f9;
}

.status {
  display: inline-block;
  padding: 4px 8px;
  border-radius: 10px;
  font-size: 12px;
}

.status.enabled {
  background-color: #e8f5e9;
  color: #4caf50;
}

.status.disabled {
  background-color: #ffebee;
  color: #f44336;
}

.actions {
  display: flex;
  gap: 8px;
}

.edit-btn,
.delete-btn {
  padding: 6px 12px;
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

.empty-state {
  text-align: center;
  padding: 40px;
  color: #999;
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
  max-width: 400px;
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
.form-group textarea {
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

.form-group input[type="checkbox"] {
  width: auto;
  margin-right: 5px;
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