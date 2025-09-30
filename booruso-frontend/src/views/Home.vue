<template>
  <div class="home">
    <div class="container">
      <div class="header">
        <div class="logo">
          <el-icon size="60" color="#409EFF">
            <Picture />
          </el-icon>
          <h1>BooruSo</h1>
        </div>
      </div>
      
      <div class="search-section">
        <div class="search-box">
          <el-input
            v-model="searchText"
            placeholder="请输入搜索关键词"
            size="large"
            class="search-input"
            @keyup.enter="handleSearch"
          >
            <template #append>
              <el-button 
                type="primary" 
                @click="handleSearch"
                :loading="loading"
              >
                搜索
              </el-button>
            </template>
          </el-input>
        </div>
        
        <div class="search-tips">
          <p>输入关键词搜索相关图片</p>
        </div>
      </div>
    </div>
    
    <!-- 页面底部信息区域 -->
    <div class="footer-section">
      <div class="container">
        <div class="footer-content">
          <p class="author-info">
            作者：RngAd33 | 
            <a href="https://github.com/RngAd33/BooruSo" target="_blank" class="github-link">
              开源地址
            </a>
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { Picture } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

export default {
  name: 'Home',
  components: {
    Picture
  },
  setup() {
    const router = useRouter()
    const searchText = ref('')
    const loading = ref(false)

    const handleSearch = () => {
      if (!searchText.value.trim()) {
        ElMessage.warning('请输入搜索关键词')
        return
      }
      
      loading.value = true
      
      // 跳转到搜索结果页
      router.push({
        path: '/search',
        query: { q: searchText.value.trim() }
      })
    }

    return {
      searchText,
      loading,
      handleSearch
    }
  }
}
</script>

<style scoped>
.home {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  position: relative;
}

.home > .container {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 100%;
  max-width: 600px;
  padding: 0 20px;
  box-sizing: border-box;
}

.container {
  text-align: center;
  width: 100%;
  padding: 0 20px;
  box-sizing: border-box;
}

.header {
  margin-bottom: 60px;
}

.logo {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
}

.logo h1 {
  color: white;
  font-size: 2.5rem;
  font-weight: 300;
  margin: 0;
}

.search-section {
  background: white;
  padding: 40px;
  border-radius: 12px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
}

.search-box {
  margin-bottom: 20px;
}

.search-input {
  width: 100%;
}

.search-input :deep(.el-input__wrapper) {
  border-radius: 8px;
}

.search-tips {
  color: #666;
  font-size: 14px;
}

.search-tips p {
  margin: 0;
}

.footer {
  margin-top: 40px;
}

.author-info {
  color: rgba(255, 255, 255, 0.8);
  font-size: 14px;
  margin: 0;
}

.github-link {
  color: rgba(255, 255, 255, 0.9);
  text-decoration: none;
  transition: color 0.2s;
}

.github-link:hover {
  color: white;
  text-decoration: underline;
}

/* 底部信息区域样式 */
.footer-section {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  padding: 20px 0;
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
}

.footer-content {
  text-align: center;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.footer-content .author-info {
  color: #333;
  font-size: 14px;
  margin: 0;
}

.footer-content .github-link {
  color: #409eff;
  text-decoration: none;
  transition: color 0.2s;
}

.footer-content .github-link:hover {
  color: #66b1ff;
  text-decoration: underline;
}
</style>