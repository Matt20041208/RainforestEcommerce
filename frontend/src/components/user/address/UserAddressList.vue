<script setup>
import { ref, onMounted, reactive } from 'vue'
import { get, post } from '@/utils/request'

const currentUser = ref(JSON.parse(localStorage.getItem('user') || 'null'))
const addresses = ref([])
const isLoading = ref(false)
const errorMessage = ref('')
const successMessage = ref('')
const showAddModal = ref(false)

const addressForm = reactive({
  receiver: '',
  phone: '',
  province: '',
  city: '',
  district: '',
  detailAddress: '',
  isDefault: false
})

const fetchAddresses = async () => {
  if (!currentUser.value) return
  
  isLoading.value = true
  errorMessage.value = ''
  try {
    addresses.value = await get(`/api/address/user/${currentUser.value.id}`)
  } catch (error) {
    console.error('获取地址列表失败:', error)
    errorMessage.value = '获取地址列表失败'
  } finally {
    isLoading.value = false
  }
}

const addAddress = async () => {
  if (!currentUser.value) return
  
  isLoading.value = true
  errorMessage.value = ''
  successMessage.value = ''
  
  try {
    await post('/api/address', {
      ...addressForm,
      userId: currentUser.value.id
    })
    await fetchAddresses()
    showAddModal.value = false
    // 重置表单
    Object.keys(addressForm).forEach(key => {
      addressForm[key] = ''
    })
    addressForm.isDefault = false
    successMessage.value = '地址添加成功'
  } catch (error) {
    console.error('添加失败:', error)
    errorMessage.value = '添加失败'
  } finally {
    isLoading.value = false
  }
}

onMounted(() => {
  fetchAddresses()
})
</script>

<template>
  <div class="user-address-list">
    <div class="header">
      <h2>收货地址</h2>
      <button v-if="currentUser" class="btn btn-primary" @click="showAddModal = true">添加地址</button>
    </div>
    
    <div v-if="!currentUser" class="not-logged-in">
      <p>请先登录</p>
      <router-link to="/login" class="btn btn-primary">去登录</router-link>
    </div>
    <div v-else>
      <div v-if="successMessage" class="success">{{ successMessage }}</div>
      <div v-if="errorMessage" class="error">{{ errorMessage }}</div>
      
      <div v-if="isLoading" class="loading">加载中...</div>
      <div v-else-if="addresses.length === 0" class="empty">暂无收货地址</div>
      <div v-else class="address-list">
        <div v-for="address in addresses" :key="address.id" class="address-item">
          <div class="address-info">
            <div class="address-header">
              <h3>{{ address.name }} {{ address.phone }}</h3>
              <span v-if="address.isDefault" class="default-tag">默认</span>
            </div>
            <p>{{ address.province }}{{ address.city }}{{ address.district }}{{ address.detail }}</p>
          </div>
          <div class="address-actions">
            <button class="btn btn-edit">编辑</button>
            <button class="btn btn-delete">删除</button>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 添加地址模态框 -->
    <div v-if="showAddModal" class="modal-overlay">
      <div class="modal">
        <div class="modal-header">
          <h3>添加收货地址</h3>
          <button class="close-btn" @click="showAddModal = false">&times;</button>
        </div>
        <div class="modal-body">
          <form @submit.prevent="addAddress">
            <div class="form-group">
              <label for="receiver">收货人</label>
              <input type="text" id="receiver" v-model="addressForm.receiver" required>
            </div>
            <div class="form-group">
              <label for="phone">手机号</label>
              <input type="tel" id="phone" v-model="addressForm.phone" required>
            </div>
            <div class="form-group">
              <label for="province">省份</label>
              <input type="text" id="province" v-model="addressForm.province" required>
            </div>
            <div class="form-group">
              <label for="city">城市</label>
              <input type="text" id="city" v-model="addressForm.city" required>
            </div>
            <div class="form-group">
              <label for="district">区县</label>
              <input type="text" id="district" v-model="addressForm.district" required>
            </div>
            <div class="form-group">
              <label for="detailAddress">详细地址</label>
              <textarea id="detailAddress" v-model="addressForm.detailAddress" rows="3" required></textarea>
            </div>
            <div class="form-group checkbox-group">
              <input type="checkbox" id="isDefault" v-model="addressForm.isDefault">
              <label for="isDefault">设为默认地址</label>
            </div>
            <div class="modal-footer">
              <button type="button" class="btn btn-cancel" @click="showAddModal = false">取消</button>
              <button type="submit" class="btn btn-primary" :disabled="isLoading">
                {{ isLoading ? '添加中...' : '添加' }}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.user-address-list {
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

.success {
  background-color: #f6ffed;
  border: 1px solid #b7eb8f;
  color: #52c41a;
  padding: 12px;
  border-radius: 4px;
  margin-bottom: 20px;
}

.error {
  background-color: #fff2f0;
  border: 1px solid #ffccc7;
  color: #ff4d4f;
  padding: 12px;
  border-radius: 4px;
  margin-bottom: 20px;
}

.loading,
.empty {
  text-align: center;
  padding: 40px;
  background-color: #f5f5f5;
  border-radius: 8px;
  margin-bottom: 20px;
}

.address-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.address-item {
  background-color: #fff;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  padding: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.address-info {
  flex: 1;
}

.address-header {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}

.address-header h3 {
  margin: 0;
  color: #333;
  font-size: 16px;
  margin-right: 12px;
}

.default-tag {
  padding: 2px 8px;
  background-color: #ff4d4f;
  color: white;
  border-radius: 10px;
  font-size: 12px;
}

.address-info p {
  margin: 8px 0;
  color: #666;
  line-height: 1.4;
}

.address-actions {
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

.btn-primary {
  background-color: #42b883;
  color: white;
}

.btn-primary:hover {
  background-color: #359d68;
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

.btn-cancel {
  background-color: #d9d9d9;
  color: #333;
}

.btn-cancel:hover {
  background-color: #bfbfbf;
}

/* 模态框样式 */
.modal-overlay {
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

.modal {
  background-color: white;
  border-radius: 8px;
  width: 90%;
  max-width: 500px;
  max-height: 90vh;
  overflow-y: auto;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid #e0e0e0;
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
}

.modal-body {
  padding: 16px;
}

.form-group {
  margin-bottom: 16px;
}

.form-group label {
  display: block;
  margin-bottom: 6px;
  color: #333;
  font-weight: 500;
}

.form-group input,
.form-group textarea {
  width: 100%;
  padding: 10px;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  font-size: 14px;
}

.checkbox-group {
  display: flex;
  align-items: center;
}

.checkbox-group input {
  width: auto;
  margin-right: 8px;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding: 16px;
  border-top: 1px solid #e0e0e0;
}
</style>