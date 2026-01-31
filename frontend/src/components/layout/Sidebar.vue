<script setup>
import { ref, computed, watch } from 'vue'
import { useRouter } from 'vue-router'
import SidebarItem from './SidebarItem.vue'

const router = useRouter()
const isCollapsed = ref(false)
const currentUser = ref(JSON.parse(localStorage.getItem('user') || 'null'))

// 根据用户角色生成菜单
const menuItems = computed(() => {
  if (!currentUser.value) {
    return []
  }
  
  if (currentUser.value.role === 'seller') {
    return [
      {
        title: '商品管理',
        icon: '📦',
        path: '/seller/products'
      },
      {
        title: '分类管理',
        icon: '🏷️',
        path: '/seller/categories'
      },
      {
        title: '订单管理',
        icon: '📋',
        path: '/seller/orders'
      },
      {
        title: '销售统计',
        icon: '📊',
        path: '/seller/statistics'
      }
    ]
  } else {
    return [
      {
        title: '个人中心',
        icon: '👤',
        path: '/user/profile'
      },
      {
        title: '我的订单',
        icon: '📦',
        path: '/user/orders'
      },
      {
        title: '收货地址',
        icon: '📍',
        path: '/user/addresses'
      },
      {
        title: '我的收藏',
        icon: '❤️',
        path: '/user/favorites'
      },
      {
        title: '浏览历史',
        icon: '⏰',
        path: '/user/history'
      }
    ]
  }
})

// 切换折叠状态
const toggleCollapse = () => {
  isCollapsed.value = !isCollapsed.value
}

// 监听路由变化，高亮当前菜单项
const currentPath = computed(() => router.currentRoute.value.path)
</script>

<template>
  <div class="sidebar" :class="{ 'collapsed': isCollapsed }">
    <div class="sidebar-header">
      <h3 v-if="!isCollapsed">{{ currentUser?.role === 'seller' ? '商家中心' : '用户中心' }}</h3>
      <button class="collapse-btn" @click="toggleCollapse">
        {{ isCollapsed ? '展开' : '收起' }}
      </button>
    </div>
    <div class="sidebar-menu">
      <SidebarItem
        v-for="item in menuItems"
        :key="item.path"
        :item="item"
        :is-active="currentPath.includes(item.path)"
        :is-collapsed="isCollapsed"
      />
    </div>
  </div>
</template>

<style scoped>
.sidebar {
  width: 240px;
  height: 100vh;
  background-color: #fff;
  border-right: 1px solid #e0e0e0;
  transition: width 0.3s ease;
  position: fixed;
  left: 0;
  top: 0;
  z-index: 1000;
  overflow-y: auto;
}

.sidebar.collapsed {
  width: 64px;
}

.sidebar-header {
  padding: 20px;
  border-bottom: 1px solid #f0f0f0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.sidebar-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #42b883;
}

.collapse-btn {
  background: none;
  border: none;
  cursor: pointer;
  font-size: 12px;
  color: #666;
}

.sidebar-menu {
  padding: 20px 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .sidebar {
    transform: translateX(-100%);
  }
  
  .sidebar.open {
    transform: translateX(0);
  }
}
</style>