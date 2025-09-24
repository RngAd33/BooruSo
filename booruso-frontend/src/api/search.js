import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 请求拦截器
api.interceptors.request.use(
  config => {
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
api.interceptors.response.use(
  response => {
    const { data } = response
    if (data.code === 0) {
      return data.data
    } else {
      throw new Error(data.message || '请求失败')
    }
  },
  error => {
    console.error('API请求错误:', error)
    throw error
  }
)

export const searchAPI = {
  // 获取缩略图地址表
  doEasySearch(searchText) {
    return api.get('/easy', {
      params: { searchText }
    })
  },

  // 获取原图地址
  getOriginalImageUrl(easyPageUrl) {
    return api.get('/final', {
      params: { easyPageUrl }
    })
  }
}