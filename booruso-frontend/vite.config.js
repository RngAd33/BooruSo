import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://localhost:8081/api',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, '')
      },
      // 堆糖CDN代理，绕过Referer防盗链
      '/duitang-cdn': {
        target: 'https://c-ssl.duitang.com',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/duitang-cdn/, ''),
        configure: (proxy) => {
          proxy.on('proxyReq', (proxyReq) => {
            proxyReq.setHeader('Referer', 'https://www.duitang.com/')
          })
        }
      }
    }
  }
})