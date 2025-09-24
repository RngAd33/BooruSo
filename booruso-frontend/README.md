# 看图项目前端

一个基于Vue.js的看图应用前端，支持图片搜索、缩略图展示和原图查看下载功能。

## 功能特性

- 🔍 **图片搜索**: 在首页输入关键词搜索相关图片
- 🖼️ **缩略图展示**: 以网格形式展示搜索结果的缩略图
- 🔍 **原图查看**: 点击缩略图查看高清原图
- 📥 **图片下载**: 支持下载原图到本地
- 📱 **响应式设计**: 适配桌面端和移动端

## 技术栈

- Vue 3 - 前端框架
- Vue Router - 路由管理
- Element Plus - UI组件库
- Axios - HTTP请求库
- Vite - 构建工具

## 项目结构

```
src/
├── api/           # API接口
│   └── search.js  # 搜索相关接口
├── views/         # 页面组件
│   ├── Home.vue           # 首页
│   ├── SearchResults.vue  # 搜索结果页
│   └── ImageDetail.vue    # 图片详情页
├── App.vue        # 根组件
└── main.js        # 入口文件
```

## 安装依赖

```bash
npm install
```

## 开发运行

```bash
npm run dev
```

项目将在 http://localhost:3000 启动

## 构建部署

```bash
npm run build
```

## 后端接口

项目需要配合后端API使用，后端接口包括：

1. `GET /easy?searchText={keyword}` - 获取缩略图地址表
2. `GET /final?easyPageUrl={url}` - 获取原图地址

## 页面说明

### 1. 首页 (/)
- 展示项目logo和搜索框
- 用户输入搜索关键词后跳转到搜索结果页

### 2. 搜索结果页 (/search)
- 展示搜索到的缩略图网格
- 点击缩略图获取原图地址并跳转到详情页
- 支持重新搜索

### 3. 图片详情页 (/image)
- 展示高清原图
- 显示图片信息（尺寸、地址等）
- 提供图片下载功能

## 配置说明

项目通过 `vite.config.js` 配置了代理，将 `/api` 路径的请求代理到后端服务器 `http://localhost:8081`。

如需修改后端地址，请编辑 `vite.config.js` 文件中的 `proxy.target` 配置。