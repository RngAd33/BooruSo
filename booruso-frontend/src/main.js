import { createApp } from 'vue'
import { createRouter, createWebHistory } from 'vue-router'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import App from './App.vue'
import Home from './views/Home.vue'
import SearchResults from './views/SearchResults.vue'
import ImageDetail from './views/ImageDetail.vue'

const routes = [
  { path: '/', component: Home },
  { path: '/search', component: SearchResults },
  { path: '/image', component: ImageDetail }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

const app = createApp(App)
app.use(router)
app.use(ElementPlus)
app.mount('#app')