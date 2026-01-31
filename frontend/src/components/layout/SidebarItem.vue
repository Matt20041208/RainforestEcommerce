<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'

const props = defineProps({
  item: {
    type: Object,
    required: true
  },
  isActive: {
    type: Boolean,
    default: false
  },
  isCollapsed: {
    type: Boolean,
    default: false
  }
})

const router = useRouter()

const handleClick = () => {
  router.push(props.item.path)
}
</script>

<template>
  <div 
    class="sidebar-item" 
    :class="{ 'active': isActive }"
    @click="handleClick"
  >
    <div class="item-icon">{{ item.icon }}</div>
    <div v-if="!isCollapsed" class="item-title">{{ item.title }}</div>
  </div>
</template>

<style scoped>
.sidebar-item {
  display: flex;
  align-items: center;
  padding: 12px 20px;
  cursor: pointer;
  transition: all 0.3s ease;
  border-left: 3px solid transparent;
}

.sidebar-item:hover {
  background-color: #f5f5f5;
}

.sidebar-item.active {
  background-color: #e8f5e8;
  border-left-color: #42b883;
  color: #42b883;
}

.item-icon {
  font-size: 18px;
  margin-right: 12px;
  min-width: 20px;
}

.item-title {
  font-size: 14px;
  font-weight: 500;
}
</style>