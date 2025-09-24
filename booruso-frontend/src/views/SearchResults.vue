<template>
  <div class="search-results">
    <div class="header">
      <div class="container">
        <div class="search-bar">
          <el-input
            v-model="searchText"
            placeholder="请输入搜索关键词"
            size="large"
            class="search-input"
            @keyup.enter="handleSearch"
          >
            <template #prepend>
              <el-button @click="goHome" icon="House">首页</el-button>
            </template>
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
      </div>
    </div>

    <div class="content">
      <div class="container">
        <div v-if="loading" class="loading">
          <el-loading-spinner />
          <p>正在搜索图片...</p>
        </div>

        <div v-else-if="error" class="error">
          <el-icon size="60" color="#F56C6C">
            <Warning />
          </el-icon>
          <p>{{ error }}</p>
          <el-button type="primary" @click="handleSearch">重试</el-button>
        </div>

        <div v-else-if="thumbnails.length === 0" class="empty">
          <el-icon size="60" color="#909399">
            <Picture />
          </el-icon>
          <p>未找到相关图片</p>
        </div>

        <div v-else class="results">
          <div class="results-info">
            <p>找到 {{ thumbnails.length }} 张图片</p>
          </div>
          
          <div class="image-grid">
            <div 
              v-for="(thumbnail, index) in thumbnails" 
              :key="index"
              class="image-item"
              @click="handleImageClick(thumbnail)"
            >
              <div class="image-wrapper">
                <img 
                  :src="thumbnail" 
                  :alt="`图片 ${index + 1}`"
                  @load="onImageLoad"
                  @error="onImageError"
                />
                <div class="image-overlay">
                  <el-icon size="24" color="white">
                    <ZoomIn />
                  </el-icon>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { House, Warning, Picture, ZoomIn } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { searchAPI } from '../api/search'

export default {
  name: 'SearchResults',
  components: {
    House,
    Warning,
    Picture,
    ZoomIn
  },
  setup() {
    const router = useRouter()
    const route = useRoute()
    
    const searchText = ref('')
    const loading = ref(false)
    const error = ref('')
    const thumbnails = ref([])

    const goHome = () => {
      router.push('/')
    }

    const handleSearch = async () => {
      if (!searchText.value.trim()) {
        ElMessage.warning('请输入搜索关键词')
        return
      }

      loading.value = true
      error.value = ''
      
      try {
        const result = await searchAPI.doEasySearch(searchText.value.trim())
        thumbnails.value = result || []
        
        // 更新URL参数
        router.replace({
          path: '/search',
          query: { q: searchText.value.trim() }
        })
      } catch (err) {
        error.value = err.message || '搜索失败，请重试'
        thumbnails.value = []
      } finally {
        loading.value = false
      }
    }

    const handleImageClick = async (thumbnailUrl) => {
      try {
        loading.value = true
        const originalUrl = await searchAPI.getOriginalImageUrl(thumbnailUrl)
        
        router.push({
          path: '/image',
          query: { 
            thumbnail: thumbnailUrl,
            original: originalUrl
          }
        })
      } catch (err) {
        ElMessage.error('获取原图失败：' + (err.message || '未知错误'))
      } finally {
        loading.value = false
      }
    }

    const onImageLoad = (event) => {
      event.target.style.opacity = '1'
    }

    const onImageError = (event) => {
      event.target.src = 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMjAwIiBoZWlnaHQ9IjIwMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48cmVjdCB3aWR0aD0iMTAwJSIgaGVpZ2h0PSIxMDAlIiBmaWxsPSIjZjVmNWY1Ii8+PHRleHQgeD0iNTAlIiB5PSI1MCUiIGZvbnQtZmFtaWx5PSJBcmlhbCwgc2Fucy1zZXJpZiIgZm9udC1zaXplPSIxNCIgZmlsbD0iIzk5OSIgdGV4dC1hbmNob3I9Im1pZGRsZSIgZHk9Ii4zZW0iPuWbvueJh+WKoOi9veWksei0pTwvdGV4dD48L3N2Zz4='
    }

    // 监听路由参数变化
    watch(() => route.query.q, (newQuery) => {
      if (newQuery) {
        searchText.value = newQuery
        handleSearch()
      }
    }, { immediate: true })

    onMounted(() => {
      const query = route.query.q
      if (query) {
        searchText.value = query
        handleSearch()
      }
    })

    return {
      searchText,
      loading,
      error,
      thumbnails,
      goHome,
      handleSearch,
      handleImageClick,
      onImageLoad,
      onImageError
    }
  }
}
</script>

<style scoped>
.search-results {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.header {
  background: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 20px 0;
  position: sticky;
  top: 0;
  z-index: 100;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.search-bar {
  max-width: 600px;
  margin: 0 auto;
}

.content {
  padding: 40px 0;
}

.loading, .error, .empty {
  text-align: center;
  padding: 60px 20px;
}

.loading p, .error p, .empty p {
  margin: 20px 0;
  color: #666;
  font-size: 16px;
}

.results-info {
  margin-bottom: 30px;
  color: #666;
}

.image-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 20px;
}

.image-item {
  cursor: pointer;
  transition: transform 0.2s;
}

.image-item:hover {
  transform: translateY(-5px);
}

.image-wrapper {
  position: relative;
  background: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  aspect-ratio: 1;
}

.image-wrapper img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  opacity: 0;
  transition: opacity 0.3s;
}

.image-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.2s;
}

.image-item:hover .image-overlay {
  opacity: 1;
}
</style>