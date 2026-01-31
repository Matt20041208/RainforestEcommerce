<script setup>
import { ref, reactive, computed } from 'vue'

const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  email: '',
  phone: ''
})

const errors = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  email: '',
  phone: ''
})

const successMessage = ref('')
const isLoading = ref(false)

// 表单验证规则
const validateUsername = () => {
  if (!registerForm.username.trim()) {
    errors.username = '用户名不能为空'
    return false
  } else if (registerForm.username.length < 3 || registerForm.username.length > 20) {
    errors.username = '用户名长度应在3-20个字符之间'
    return false
  }
  errors.username = ''
  return true
}

const validatePassword = () => {
  if (!registerForm.password) {
    errors.password = '密码不能为空'
    return false
  } else if (registerForm.password.length < 6) {
    errors.password = '密码长度不能少于6个字符'
    return false
  } else if (!/[a-zA-Z]/.test(registerForm.password) || !/\d/.test(registerForm.password)) {
    errors.password = '密码必须包含字母和数字'
    return false
  }
  errors.password = ''
  return true
}

const validateConfirmPassword = () => {
  if (!registerForm.confirmPassword) {
    errors.confirmPassword = '请确认密码'
    return false
  } else if (registerForm.confirmPassword !== registerForm.password) {
    errors.confirmPassword = '两次输入的密码不一致'
    return false
  }
  errors.confirmPassword = ''
  return true
}

const validateEmail = () => {
  if (!registerForm.email) {
    errors.email = '邮箱不能为空'
    return false
  } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(registerForm.email)) {
    errors.email = '请输入有效的邮箱地址'
    return false
  }
  errors.email = ''
  return true
}

const validatePhone = () => {
  if (registerForm.phone && !/^1[3-9]\d{9}$/.test(registerForm.phone)) {
    errors.phone = '请输入有效的手机号'
    return false
  }
  errors.phone = ''
  return true
}

// 检查表单是否可以提交
const isFormValid = computed(() => {
  return !Object.values(errors).some(error => error) && 
         Object.values(registerForm).filter((value, index, array) => index !== array.length - 1).every(value => value.trim())
})

const handleRegister = async () => {
  // 表单验证
  if (!validateUsername() || !validatePassword() || !validateConfirmPassword() || !validateEmail() || !validatePhone()) {
    return
  }
  
  successMessage.value = ''
  isLoading.value = true
  
  try {
    const response = await fetch('/api/user/register', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        username: registerForm.username,
        password: registerForm.password,
        email: registerForm.email,
        phone: registerForm.phone
      })
    })
    
    if (response.ok) {
      const data = await response.json()
      successMessage.value = '注册成功！'
      // 重置表单
      Object.keys(registerForm).forEach(key => {
        registerForm[key] = ''
      })
    } else {
      const error = await response.text()
      successMessage.value = ''
      alert(error)
    }
  } catch (error) {
    successMessage.value = ''
    alert('注册失败，请稍后重试')
  } finally {
    isLoading.value = false
  }
}

const checkUsername = async () => {
  if (!validateUsername()) return
  
  try {
    const response = await fetch(`/api/user/check/username/${registerForm.username}`)
    const data = await response.json()
    if (data.exists) {
      errors.username = '用户名已存在'
    }
  } catch (error) {
    console.error('检查用户名失败', error)
  }
}

const checkEmail = async () => {
  if (!validateEmail()) return
  
  try {
    const response = await fetch(`/api/user/check/email/${registerForm.email}`)
    const data = await response.json()
    if (data.exists) {
      errors.email = '邮箱已存在'
    }
  } catch (error) {
    console.error('检查邮箱失败', error)
  }
}
</script>

<template>
  <div class="register-container">
    <h2>用户注册</h2>
    <form @submit.prevent="handleRegister" class="register-form">
      <div class="form-group">
        <label for="username">用户名</label>
        <input 
          type="text" 
          id="username" 
          v-model="registerForm.username"
          @blur="checkUsername"
          @input="validateUsername"
        />
        <div v-if="errors.username" class="error-text">{{ errors.username }}</div>
      </div>
      
      <div class="form-group">
        <label for="email">邮箱</label>
        <input 
          type="email" 
          id="email" 
          v-model="registerForm.email"
          @blur="checkEmail"
          @input="validateEmail"
        />
        <div v-if="errors.email" class="error-text">{{ errors.email }}</div>
      </div>
      
      <div class="form-group">
        <label for="password">密码</label>
        <input 
          type="password" 
          id="password" 
          v-model="registerForm.password"
          @input="validatePassword"
        />
        <div v-if="errors.password" class="error-text">{{ errors.password }}</div>
      </div>
      
      <div class="form-group">
        <label for="confirmPassword">确认密码</label>
        <input 
          type="password" 
          id="confirmPassword" 
          v-model="registerForm.confirmPassword"
          @input="validateConfirmPassword"
        />
        <div v-if="errors.confirmPassword" class="error-text">{{ errors.confirmPassword }}</div>
      </div>
      
      <div class="form-group">
        <label for="phone">手机号</label>
        <input 
          type="tel" 
          id="phone" 
          v-model="registerForm.phone"
          @input="validatePhone"
        />
        <div v-if="errors.phone" class="error-text">{{ errors.phone }}</div>
      </div>
      
      <div v-if="successMessage" class="success-message">
        {{ successMessage }}
      </div>
      
      <button type="submit" class="register-btn" :disabled="isLoading || !isFormValid">
        {{ isLoading ? '注册中...' : '注册' }}
      </button>
    </form>
  </div>
</template>

<style scoped>
.register-container {
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

.register-form {
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
  text-align: center;
}

.success-message {
  color: #52c41a;
  margin-bottom: 15px;
  text-align: center;
}

.register-btn {
  background-color: #42b883;
  color: white;
  border: none;
  padding: 12px;
  border-radius: 4px;
  font-size: 16px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.register-btn:hover {
  background-color: #35495e;
}

.register-btn:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}
</style>