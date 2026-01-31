<script setup>
defineProps({
  msg: String
})
</script>

<template>
  <h2>{{ msg }}</h2>
  <div class="container">
    <div class="features">
      <div class="feature">
        <h3>用户管理</h3>
        <p>注册、登录、个人信息管理</p>
      </div>
      <div class="feature">
        <h3>商品管理</h3>
        <p>商品浏览、分类、搜索</p>
      </div>
      <div class="feature">
        <h3>购物车</h3>
        <p>添加商品、修改数量</p>
      </div>
    </div>
  </div>
</template>

<style scoped>
.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.features {
  display: flex;
  justify-content: space-around;
  margin-top: 40px;
}

.feature {
  background-color: #f0f8ff;
  padding: 20px;
  border-radius: 8px;
  text-align: center;
  width: 30%;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.feature h3 {
  color: #42b883;
  margin-top: 0;
}
</style>