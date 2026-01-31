<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'

// 获取路由参数
const route = useRoute()
const productId = computed(() => route.params.id)

// 评论相关数据
const comments = ref([])
const isLoading = ref(false)
const errorMessage = ref('')
const replyContent = ref('')
const replyCommentId = ref(null)
const actionMessage = ref('')

// 模态框状态
const showReplyModal = ref(false)

// 获取商品评论
const fetchComments = async () => {
  isLoading.value = true
  errorMessage.value = ''
  try {
    const response = await fetch(`/api/comment/product/${productId.value}`)
    if (response.ok) {
      comments.value = await response.json()
    } else {
      errorMessage.value = '获取评论失败'
    }
  } catch (error) {
    console.error('获取评论失败:', error)
    errorMessage.value = '获取评论失败'
  } finally {
    isLoading.value = false
  }
}

// 打开回复模态框
const openReplyModal = (commentId) => {
  replyCommentId.value = commentId
  replyContent.value = ''
  showReplyModal.value = true
}

// 关闭回复模态框
const closeReplyModal = () => {
  showReplyModal.value = false
  replyCommentId.value = null
  replyContent.value = ''
}

// 提交回复
const submitReply = async () => {
  if (!replyContent.value.trim() || !replyCommentId.value) {
    actionMessage.value = '请输入回复内容'
    return
  }
  
  try {
    const response = await fetch(`/api/comment/${replyCommentId.value}/reply`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        reply: replyContent.value
      })
    })
    
    if (response.ok) {
      const updatedComment = await response.json()
      // 更新本地评论列表中的回复
      const commentIndex = comments.value.findIndex(c => c.id === replyCommentId.value)
      if (commentIndex !== -1) {
        comments.value[commentIndex].reply = updatedComment.reply
      }
      
      actionMessage.value = '回复成功'
      closeReplyModal()
      
      // 3秒后清除消息
      setTimeout(() => {
        actionMessage.value = ''
      }, 3000)
    } else {
      actionMessage.value = '回复失败'
    }
  } catch (error) {
    console.error('提交回复失败:', error)
    actionMessage.value = '回复失败'
  }
}

// 组件挂载时获取评论
onMounted(() => {
  fetchComments()
})
</script>

<template>
  <div class="comment-management-container">
    <h2>评论管理</h2>
    
    <!-- 错误提示 -->
    <div v-if="errorMessage" class="error-message">{{ errorMessage }}</div>
    
    <!-- 操作消息 -->
    <div v-if="actionMessage" class="action-message">{{ actionMessage }}</div>
    
    <!-- 加载状态 -->
    <div v-if="isLoading" class="loading">加载中...</div>
    
    <!-- 评论列表 -->
    <div v-else-if="comments.length === 0" class="no-comments">暂无评论</div>
    <div v-else class="comments-list">
      <div v-for="comment in comments" :key="comment.id" class="comment-item">
        <div class="comment-header">
          <div class="comment-user-info">
            <span class="comment-user">{{ comment.user.username }}</span>
            <div class="comment-rating">
              <span v-for="i in 5" :key="i" :class="{ 'filled': i <= comment.rating }">★</span>
            </div>
          </div>
          <span class="comment-time">{{ new Date(comment.createTime).toLocaleString('zh-CN') }}</span>
        </div>
        
        <div class="comment-content">{{ comment.content }}</div>
        
        <!-- 商家回复 -->
        <div v-if="comment.reply" class="merchant-reply">
          <div class="reply-header">
            <strong>商家回复:</strong>
          </div>
          <p>{{ comment.reply }}</p>
        </div>
        
        <!-- 回复按钮 -->
        <div class="comment-actions">
          <button @click="openReplyModal(comment.id)" :disabled="comment.reply" class="reply-btn">
            {{ comment.reply ? '已回复' : '回复' }}
          </button>
        </div>
      </div>
    </div>
    
    <!-- 回复模态框 -->
    <div v-if="showReplyModal" class="modal-overlay" @click="closeReplyModal">
      <div class="modal" @click.stop>
        <div class="modal-header">
          <h3>回复评论</h3>
          <button class="close-btn" @click="closeReplyModal">×</button>
        </div>
        <div class="modal-body">
          <textarea
            v-model="replyContent"
            placeholder="请输入回复内容..."
            rows="6"
          ></textarea>
        </div>
        <div class="modal-footer">
          <button class="cancel-btn" @click="closeReplyModal">取消</button>
          <button class="submit-btn" @click="submitReply">提交回复</button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.comment-management-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.comment-management-container h2 {
  color: #42b883;
  margin-bottom: 20px;
  text-align: center;
}

.error-message {
  color: #ff4d4f;
  margin-bottom: 15px;
  padding: 10px;
  background-color: #fff2f0;
  border-radius: 4px;
  text-align: center;
}

.action-message {
  background-color: #e8f5e9;
  color: #4caf50;
  padding: 10px;
  border-radius: 4px;
  margin-bottom: 15px;
  text-align: center;
  animation: fadeIn 0.3s ease;
}

.loading {
  text-align: center;
  font-size: 18px;
  color: #42b883;
  margin: 40px 0;
}

.no-comments {
  text-align: center;
  color: #8c8c8c;
  padding: 40px;
  background-color: #f9f9f9;
  border-radius: 8px;
}

.comments-list {
  margin-top: 20px;
}

.comment-item {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 20px;
  margin-bottom: 20px;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 15px;
}

.comment-user-info {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.comment-user {
  font-weight: bold;
  color: #333;
  font-size: 16px;
}

.comment-rating {
  color: #e8e8e8;
  font-size: 16px;
}

.comment-rating .filled {
  color: #ff4d4f;
}

.comment-time {
  color: #8c8c8c;
  font-size: 14px;
}

.comment-content {
  color: #666;
  line-height: 1.6;
  margin-bottom: 20px;
  white-space: pre-line;
}

.merchant-reply {
  background-color: #f5f5f5;
  padding: 15px;
  border-radius: 4px;
  margin-bottom: 20px;
}

.reply-header {
  margin-bottom: 10px;
}

.comment-actions {
  display: flex;
  justify-content: flex-end;
}

.reply-btn {
  padding: 8px 16px;
  background-color: #42b883;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.reply-btn:hover:not(:disabled) {
  background-color: #35495e;
}

.reply-btn:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
  opacity: 0.7;
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
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal {
  background-color: white;
  border-radius: 8px;
  width: 90%;
  max-width: 500px;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.3);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  border-bottom: 1px solid #eee;
}

.modal-header h3 {
  margin: 0;
  color: #333;
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  color: #8c8c8c;
  cursor: pointer;
  padding: 0;
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.modal-body {
  padding: 20px;
}

.modal-body textarea {
  width: 100%;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  resize: vertical;
  font-family: inherit;
  font-size: 16px;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding: 15px 20px;
  border-top: 1px solid #eee;
}

.cancel-btn {
  padding: 8px 16px;
  background-color: #f5f5f5;
  color: #666;
  border: 1px solid #ddd;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.cancel-btn:hover {
  background-color: #e8e8e8;
}

.submit-btn {
  padding: 8px 16px;
  background-color: #42b883;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.submit-btn:hover {
  background-color: #35495e;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(-10px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>