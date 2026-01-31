<script setup>
import { ref, onMounted, reactive } from 'vue'
import { put } from '@/utils/request'

const currentUser = ref(JSON.parse(localStorage.getItem('user') || 'null'))
const isLoading = ref(false)
const errorMessage = ref('')
const successMessage = ref('')

const profileForm = reactive({
  username: currentUser.value?.username || '',
  email: currentUser.value?.email || '',
  phone: currentUser.value?.phone || '',
  address: currentUser.value?.address || ''
})

const updateProfile = async () => {
  if (!currentUser.value) return
  
  isLoading.value = true
  errorMessage.value = ''
  successMessage.value = ''
  
  try {
    const updatedUser = await put(`/api/user/${currentUser.value.id}`, profileForm)
    currentUser.value = updatedUser
    localStorage.setItem('user', JSON.stringify(updatedUser))
    successMessage.value = '个人信息更新成功'
  } catch (error) {
    console.error('更新失败:', error)
    errorMessage.value = '更新失败'
  } finally {
    isLoading.value = false
  }
}

onMounted(() => {
  if (currentUser.value) {
    profileForm.username = currentUser.value.username
    profileForm.email = currentUser.value.email
    profileForm.phone = currentUser.value.phone
    profileForm.address = currentUser.value.address
  }
})
</script>

<template>
  <div class="user-profile">
    <h2>个人中心</h2>
    
    <div v-if="!currentUser" class="not-logged-in">
      <p>请先登录</p>
      <router-link to="/login" class="btn btn-primary">去登录</router-link>
    </div>
    <div v-else>
      <div v-if="successMessage" class="success">{{ successMessage }}</div>
      <div v-if="errorMessage" class="error">{{ errorMessage }}</div>
      
      <form @submit.prevent="updateProfile" class="profile-form">
        <div class="form-group">
          <label for="username">用户名</label>
          <input 
            type="text" 
            id="username" 
            v-model="profileForm.username" 
            readonly
          />
        </div>
        
        <div class="form-group">
          <label for="email">邮箱</label>
          <input 
            type="email" 
            id="email" 
            v-model="profileForm.email" 
          />
        </div>
        
        <div class="form-group">
          <label for="phone">手机号</label>
          <input 
            type="tel" 
            id="phone" 
            v-model="profileForm.phone" 
          />
        </div>
        
        <div class="form-group">
          <label for="address">地址</label>
          <textarea 
            id="address" 
            v-model="profileForm.address" 
            rows="3"
          ></textarea>
        </div>
        
        <button 
          type="submit" 
          class="btn btn-primary" 
          :disabled="isLoading"
        >
          {{ isLoading ? '更新中...' : '更新信息' }}
        </button>
      </form>
    </div>
  </div>
</template>

<style scoped>
.user-profile {
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

.profile-form {
  background-color: #fff;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
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

.form-group input:read-only {
  background-color: #f5f5f5;
  cursor: not-allowed;
}

.form-group textarea {
  resize: vertical;
}

.btn {
  padding: 10px 20px;
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

.btn-primary:disabled {
  background-color: #d9d9d9;
  cursor: not-allowed;
}
</style>