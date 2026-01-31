<script setup>
import { ref, reactive, computed } from 'vue'
import { post } from '@/utils/request'

const loginForm = reactive({
  username: '',
  password: ''
})

const errors = reactive({
  username: '',
  password: ''
})

const errorMessage = ref('')
const isLoading = ref(false)

// 表单验证
const validateUsername = () => {
  if (!loginForm.username.trim()) {
    errors.username = '用户名不能为空'
    return false
  }
  errors.username = ''
  return true
}

const validatePassword = () => {
  if (!loginForm.password) {
    errors.password = '密码不能为空'
    return false
  }
  errors.password = ''
  return true
}

// 检查表单是否可以提交
const isFormValid = computed(() => {
  return !Object.values(errors).some(error => error) && 
         Object.values(loginForm).every(value => value.trim())
})

const handleLogin = async () => {
  // 表单验证
  if (!validateUsername() || !validatePassword()) {
    return
  }
  
  errorMessage.value = ''
  isLoading.value = true
  
  try {
    const data = await post('/api/user/login', {
      ...loginForm,
      role: 'seller'
    })
    
    // 保存用户信息和令牌到localStorage
    localStorage.setItem('user', JSON.stringify(data.user))
    localStorage.setItem('token', data.token)
    // 跳转到首页或个人中心
    window.location.href = '/'  
  } catch (error) {
    errorMessage.value = error.message || '登录失败，请稍后重试'
  } finally {
    isLoading.value = false
  }
}
</script>

<template>
  <div class="login-container">
    <h2>商家登录</h2>
    <form @submit.prevent="handleLogin" class="login-form">
      <div class="form-group">
        <label for="username">用户名</label>
        <input 
          type="text" 
          id="username" 
          v-model="loginForm.username"
          @input="validateUsername"
        />
        <div v-if="errors.username" class="error-text">{{ errors.username }}</div>
      </div>
      
      <div class="form-group">
        <label for="password">密码</label>
        <input 
          type="password" 
          id="password" 
          v-model="loginForm.password"
          @input="validatePassword"
        />
        <div v-if="errors.password" class="error-text">{{ errors.password }}</div>
      </div>
      
      <div v-if="errorMessage" class="error-message">
        {{ errorMessage }}
      </div>
      
      <button type="submit" class="login-btn" :disabled="isLoading || !isFormValid">
        {{ isLoading ? '登录中...' : '商家登录' }}
      </button>
    </form>
  </div>
</template>

<style scoped>
.login-container {
  max-width: 400px;
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

.login-form {
  display: flex;
  flex-direction: column;
}

.form-group {
  margin-bottom: 15px;
}

label {
  display: block;
  margin-bottom: 5px;
  font-weight: 500;
}

input {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

input:focus {
  outline: none;
  border-color: #42b883;
}

.error-text {
  color: #ff4d4f;
  font-size: 12px;
  margin-top: 5px;
}

.error-message {
  color: #ff4d4f;
  margin-bottom: 15px;
  font-size: 14px;
  text-align: center;
}

.login-btn {
  background-color: #42b883;
  color: white;
  border: none;
  padding: 12px;
  border-radius: 4px;
  font-size: 16px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.login-btn:hover {
  background-color: #359d68;
}

.login-btn:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}
</style>