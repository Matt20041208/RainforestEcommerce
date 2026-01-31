<script setup>
import { ref, onMounted } from 'vue'
import { get } from '@/utils/request'

const statistics = ref({
  totalSales: 0,
  totalOrders: 0,
  totalProducts: 0,
  topSellingProducts: []
})
const isLoading = ref(false)
const errorMessage = ref('')

const fetchStatistics = async () => {
  isLoading.value = true
  errorMessage.value = ''
  try {
    const data = await get('/api/order/statistics')
    // 处理热销商品数据格式
    const topSellingProducts = data.topSellingProducts.map(item => ({
      name: item[0],
      sales: item[1]
    }))
    
    statistics.value = {
      totalSales: data.totalSales,
      totalOrders: data.totalOrders,
      totalProducts: data.totalProducts,
      topSellingProducts
    }
    isLoading.value = false
  } catch (error) {
    console.error('获取统计数据失败:', error)
    errorMessage.value = '获取统计数据失败'
    isLoading.value = false
  }
}

onMounted(() => {
  fetchStatistics()
})
</script>

<template>
  <div class="seller-statistics">
    <h2>销售统计</h2>
    
    <div v-if="isLoading" class="loading">加载中...</div>
    <div v-else-if="errorMessage" class="error">{{ errorMessage }}</div>
    <div v-else class="statistics-container">
      <div class="stats-card">
        <div class="stat-item">
          <h3>总销售额</h3>
          <p class="stat-value">¥{{ statistics.totalSales }}</p>
        </div>
        <div class="stat-item">
          <h3>总订单数</h3>
          <p class="stat-value">{{ statistics.totalOrders }}</p>
        </div>
        <div class="stat-item">
          <h3>商品总数</h3>
          <p class="stat-value">{{ statistics.totalProducts }}</p>
        </div>
      </div>
      
      <div class="top-products">
        <h3>热销商品</h3>
        <div class="product-list">
          <div v-for="(product, index) in statistics.topSellingProducts" :key="index" class="product-item">
            <span class="rank">{{ index + 1 }}</span>
            <span class="product-name">{{ product.name }}</span>
            <span class="product-sales">{{ product.sales }} 件</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.seller-statistics {
  padding: 20px;
}

h2 {
  color: #42b883;
  margin-bottom: 20px;
}

.loading,
.error {
  text-align: center;
  padding: 40px;
  background-color: #f5f5f5;
  border-radius: 8px;
  margin-bottom: 20px;
}

.error {
  color: #ff4d4f;
}

.statistics-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.stats-card {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
}

.stat-item {
  background-color: #fff;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  padding: 20px;
  text-align: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.stat-item h3 {
  margin: 0 0 12px 0;
  color: #666;
  font-size: 14px;
  font-weight: 500;
}

.stat-value {
  margin: 0;
  color: #42b883;
  font-size: 24px;
  font-weight: 600;
}

.top-products {
  background-color: #fff;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.top-products h3 {
  margin: 0 0 16px 0;
  color: #333;
}

.product-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.product-item {
  display: flex;
  align-items: center;
  padding: 12px;
  border-radius: 4px;
  background-color: #f9f9f9;
}

.rank {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background-color: #42b883;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 600;
  margin-right: 16px;
}

.product-name {
  flex: 1;
  color: #333;
}

.product-sales {
  color: #666;
  font-size: 14px;
}
</style>