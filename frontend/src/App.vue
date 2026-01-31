<script setup>
import { ref, onMounted, computed } from 'vue'
import Sidebar from './components/layout/Sidebar.vue'

const currentUser = ref(null)

onMounted(() => {
  // 从localStorage获取用户信息
  const user = localStorage.getItem('user')
  if (user) {
    currentUser.value = JSON.parse(user)
  }
})

const handleLogout = () => {
  localStorage.removeItem('user')
  currentUser.value = null
  // 跳转到首页
  window.location.href = '/'
}

const showSidebar = computed(() => !!currentUser.value)
</script>

<template>
  <div class="app-container">
    <Sidebar v-if="showSidebar" />
    <div class="main-content" :class="{ 'with-sidebar': showSidebar }">
      <header class="header">
        <div class="header-left">
          <router-link to="/" class="logo-link">
            <h1 class="logo">雨林电商</h1>
          </router-link>
          <span class="user-info" v-if="currentUser">
            欢迎，{{ currentUser.username }}
          </span>
        </div>
        <nav class="nav">
          <router-link to="/" class="nav-link">首页</router-link>
          <router-link to="/store" class="nav-link">商品商城</router-link>
          <div v-if="!currentUser" class="nav-dropdown">
            <span class="nav-link dropdown-toggle">用户</span>
            <div class="dropdown-menu">
              <router-link to="/register" class="dropdown-item">用户注册</router-link>
              <router-link to="/login" class="dropdown-item">用户登录</router-link>
            </div>
          </div>
          <div v-if="!currentUser" class="nav-dropdown">
            <span class="nav-link dropdown-toggle">商家</span>
            <div class="dropdown-menu">
              <router-link to="/seller/register" class="dropdown-item">商家注册</router-link>
              <router-link to="/seller/login" class="dropdown-item">商家登录</router-link>
            </div>
          </div>
          <router-link to="/cart" class="nav-link">购物车</router-link>
          <router-link to="/orders" class="nav-link">我的订单</router-link>
          <div v-if="currentUser" class="user-actions">
            <router-link to="/profile" class="nav-link">个人信息</router-link>
            <router-link v-if="currentUser.role === 'seller'" to="/products" class="nav-link">商品管理</router-link>
            <router-link v-if="currentUser.role === 'seller'" to="/categories" class="nav-link">分类管理</router-link>
            <button class="logout-btn" @click="handleLogout">退出</button>
          </div>
        </nav>
      </header>
      
      <main class="main">
        <router-view></router-view>
      </main>
      
      <footer class="footer">
        <p>&copy; 2026 雨林电商 版权所有</p>
      </footer>
    </div>
  </div>
</template>

<style scoped>
/* App组件的样式 */
.app-container {
  display: flex;
  min-height: 100vh;
}

.main-content {
  flex: 1;
  transition: margin-left 0.3s ease;
  display: flex;
  flex-direction: column;
}

.main-content.with-sidebar {
  margin-left: 240px;
}

/* 折叠状态 */
:deep(.sidebar.collapsed) + .main-content.with-sidebar {
  margin-left: 64px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 2rem;
  background-color: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 2rem;
}

.logo-link {
  text-decoration: none;
  display: inline-block;
  transition: transform 0.3s ease;
}

.logo-link:hover {
  transform: scale(1.05);
}

.logo {
  color: #42b883;
  margin: 0;
  font-size: 24px;
  cursor: pointer;
}

.user-info {
  color: #333;
  font-weight: 500;
  font-size: 14px;
  background-color: #f0f8ff;
  padding: 0.5rem 1rem;
  border-radius: 20px;
  border: 1px solid #e6f7ff;
  transition: all 0.3s ease;
}

.user-info:hover {
  background-color: #e6f7ff;
  box-shadow: 0 2px 4px rgba(24, 144, 255, 0.1);
}

.nav {
  display: flex;
  gap: 1.5rem;
  align-items: center;
}

.user-actions {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.nav-dropdown {
  position: relative;
}

.dropdown-toggle {
  cursor: pointer;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.dropdown-toggle:hover {
  background-color: #f0f8ff;
  color: #42b883;
}

.dropdown-menu {
  position: absolute;
  top: 100%;
  left: 0;
  background-color: white;
  border: 1px solid #ddd;
  border-radius: 4px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  z-index: 1000;
  min-width: 120px;
  display: none;
}

.nav-dropdown:hover .dropdown-menu {
  display: block;
}

.dropdown-item {
  display: block;
  padding: 0.5rem 1rem;
  text-decoration: none;
  color: #333;
  width: 100%;
  border: none;
  background: none;
  text-align: left;
  cursor: pointer;
}

.dropdown-item:hover {
  background-color: #f0f8ff;
  color: #42b883;
}

.dropdown-menu .dropdown-item:first-child {
  border-top-left-radius: 4px;
  border-top-right-radius: 4px;
}

.dropdown-menu .dropdown-item:last-child {
  border-bottom-left-radius: 4px;
  border-bottom-right-radius: 4px;
}

.nav-link {
  text-decoration: none;
  color: #333;
  font-weight: 500;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.nav-link:hover {
  background-color: #f0f8ff;
  color: #42b883;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 1rem;
  color: #333;
}

.logout-btn {
  background-color: #ff4d4f;
  color: white;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.3s;
}

.logout-btn:hover {
  background-color: #ff7875;
}

.main {
  flex: 1;
  padding: 2rem;
  background-color: #f5f5f5;
}

.footer {
  text-align: center;
  padding: 1rem;
  background-color: #fff;
  border-top: 1px solid #eee;
  color: #666;
}
</style>