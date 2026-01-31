<script setup>
import { ref, onMounted } from 'vue'
import { get, post, put, del } from '@/utils/request'

const categories = ref([])
const isLoading = ref(false)
const errorMessage = ref('')
const showAddModal = ref(false)
const showEditModal = ref(false)
const currentCategory = ref(null)

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

const openAddModal = () => {
  currentCategory.value = {
    name: '',
    description: '',
    enabled: true
  }
  showAddModal.value = true
}

const openEditModal = (category) => {
  currentCategory.value = { ...category }
  showEditModal.value = true
}

const saveCategory = async (isEdit = false) => {
  try {
    if (isEdit) {
      await put(`/api/category/${currentCategory.value.id}`, currentCategory.value)
    } else {
      await post('/api/category', currentCategory.value)
    }
    
    await fetchCategories()
    showAddModal.value = false
    showEditModal.value = false
    alert(isEdit ? '分类编辑成功！' : '分类添加成功！')
  } catch (error) {
    console.error('保存分类失败:', error)
    alert('保存分类失败，请稍后重试')
  }
}

const deleteCategory = async (categoryId) => {
  if (!confirm('确定要删除这个分类吗？')) return
  
  try {
    await del(`/api/category/${categoryId}`)
    await fetchCategories()
    alert('分类删除成功！')
  } catch (error) {
    console.error('删除分类失败:', error)
    alert('删除分类失败，请稍后重试')
  }
}

onMounted(() => {
  fetchCategories()
})
</script>

<template>
  <div class="seller-category-list">
    <div class="header-actions">
      <h2>分类管理</h2>
      <button @click="openAddModal" class="btn btn-add">添加分类</button>
    </div>
    
    <div v-if="isLoading" class="loading">加载中...</div>
    <div v-else-if="errorMessage" class="error">{{ errorMessage }}</div>
    <div v-else-if="categories.length === 0" class="empty">暂无分类</div>
    <div v-else class="category-list">
      <div v-for="category in categories" :key="category.id" class="category-item">
        <h3>{{ category.name }}</h3>
        <p>{{ category.description || '无描述' }}</p>
        <div class="category-actions">
          <button @click="openEditModal(category)" class="btn btn-edit">编辑</button>
          <button @click="deleteCategory(category.id)" class="btn btn-delete">删除</button>
        </div>
      </div>
    </div>
    
    <!-- 添加/编辑分类模态框 -->
    <div v-if="showAddModal || showEditModal" class="modal-overlay">
      <div class="modal">
        <h3>{{ showEditModal ? '编辑分类' : '添加分类' }}</h3>
        <form @submit.prevent="saveCategory(showEditModal)">
          <div class="form-group">
            <label>分类名称</label>
            <input v-model="currentCategory.name" type="text" required>
          </div>
          <div class="form-group">
            <label>分类描述</label>
            <textarea v-model="currentCategory.description"></textarea>
          </div>
          <div class="form-group">
            <label>是否启用</label>
            <input v-model="currentCategory.enabled" type="checkbox">
          </div>
          <div class="modal-actions">
            <button type="button" @click="showAddModal = showEditModal = false" class="btn btn-cancel">取消</button>
            <button type="submit" class="btn btn-save">保存</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<style scoped>
.seller-category-list {
  padding: 20px;
}

.header-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

h2 {
  color: #42b883;
  margin: 0;
}

.btn-add {
  background-color: #52c41a;
  color: white;
}

.btn-add:hover {
  background-color: #73d13d;
}

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

.modal {
  background-color: white;
  padding: 20px;
  border-radius: 8px;
  width: 90%;
  max-width: 500px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.modal h3 {
  margin-top: 0;
  margin-bottom: 20px;
  color: #333;
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
  padding: 8px;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  font-size: 14px;
}

.form-group textarea {
  resize: vertical;
  min-height: 100px;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
}

.btn-cancel {
  background-color: #f5f5f5;
  color: #333;
}

.btn-cancel:hover {
  background-color: #e8e8e8;
}

.btn-save {
  background-color: #1890ff;
  color: white;
}

.btn-save:hover {
  background-color: #40a9ff;
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

.category-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.category-item {
  background-color: #fff;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  padding: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.category-item h3 {
  margin-top: 0;
  margin-bottom: 8px;
  color: #333;
}

.category-item p {
  margin: 8px 0;
  color: #666;
}

.category-actions {
  display: flex;
  gap: 10px;
  margin-top: 16px;
}

.btn {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.3s;
}

.btn-edit {
  background-color: #1890ff;
  color: white;
}

.btn-edit:hover {
  background-color: #40a9ff;
}

.btn-delete {
  background-color: #ff4d4f;
  color: white;
}

.btn-delete:hover {
  background-color: #ff7875;
}
</style>