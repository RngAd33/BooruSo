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
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.container {
  text-align: center;
  max-width: 600px;
  width: 100%;
  padding: 0 20px;
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
</style>