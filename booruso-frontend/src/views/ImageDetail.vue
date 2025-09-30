<template>
  <div class="image-detail">
    <div class="header">
      <div class="container">
        <div class="nav-bar">
          <el-button @click="goBack" icon="ArrowLeft">上一页</el-button>
          <el-button @click="goHome" icon="House">回到首页</el-button>
          <div class="actions">
            <!-- 下载功能已移除 -->
          </div>
        </div>
      </div>
    </div>

    <div class="content">
      <div class="container">
        <div v-if="loading" class="loading">
          <el-loading-spinner />
          <p>少女祈祷中...</p>
        </div>

        <div v-else-if="error" class="error">
          <el-icon size="60" color="#F56C6C">
            <Warning />
          </el-icon>
          <p>{{ error }}</p>
          <el-button type="primary" @click="loadImage">重试</el-button>
        </div>

        <div v-else class="image-container">
          <div class="image-wrapper">
            <img 
              ref="imageRef"
              :src="originalUrl" 
              :alt="'原图'"
              @load="onImageLoad"
              @error="onImageError"
            />
          </div>
          
          <div class="image-info">
            <div v-if="imageSize" class="info-item">
              <span class="label">图片尺寸：</span>
              <span class="value">{{ imageSize }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ArrowLeft, House, Warning } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

export default {
  name: 'ImageDetail',
  components: {
    ArrowLeft,
    House,
    Warning
  },
  setup() {
    const router = useRouter()
    const route = useRoute()
    
    const imageRef = ref(null)
    const loading = ref(true)
    const error = ref('')
    const thumbnailUrl = ref('')
    const originalUrl = ref('')
    const imageSize = ref('')

    const goBack = () => {
      router.go(-1)
    }

    const goHome = () => {
      router.push('/')
    }

    const loadImage = () => {
      const thumbnail = route.query.thumbnail
      const original = route.query.original

      if (!thumbnail || !original) {
        error.value = '缺少图片参数'
        loading.value = false
        return
      }

      thumbnailUrl.value = thumbnail
      originalUrl.value = original
      loading.value = false
    }

    const onImageLoad = () => {
      if (imageRef.value) {
        const img = imageRef.value
        imageSize.value = `${img.naturalWidth} × ${img.naturalHeight}`
      }
    }

    const onImageError = () => {
      error.value = '图片加载失败'
    }



    onMounted(() => {
      loadImage()
    })

    return {
      imageRef,
      loading,
      error,
      thumbnailUrl,
      originalUrl,
      imageSize,
      goBack,
      goHome,
      loadImage,
      onImageLoad,
      onImageError
    }
  }
}
</script>

<style scoped>
.image-detail {
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

.nav-bar {
  display: flex;
  align-items: center;
  gap: 10px;
}

.actions {
  margin-left: auto;
}

.content {
  padding: 40px 0;
}

.loading, .error {
  text-align: center;
  padding: 60px 20px;
}

.loading p, .error p {
  margin: 20px 0;
  color: #666;
  font-size: 16px;
}

.image-container {
  background: white;
  border-radius: 12px;
  padding: 30px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.image-wrapper {
  text-align: center;
  margin-bottom: 30px;
}

.image-wrapper img {
  max-width: 100%;
  max-height: 70vh;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.image-info {
  border-top: 1px solid #eee;
  padding-top: 20px;
}

.info-item {
  display: flex;
  margin-bottom: 10px;
  word-break: break-all;
}

.label {
  font-weight: 500;
  color: #333;
  min-width: 100px;
  flex-shrink: 0;
}

.value {
  color: #666;
  flex: 1;
}

@media (max-width: 768px) {
  .nav-bar {
    flex-wrap: wrap;
  }
  
  .actions {
    margin-left: 0;
    width: 100%;
    margin-top: 10px;
  }
  
  .info-item {
    flex-direction: column;
  }
  
  .label {
    margin-bottom: 5px;
  }
}
</style>