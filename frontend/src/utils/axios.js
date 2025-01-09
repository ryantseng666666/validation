import axios from 'axios'
import router from '@/router'

// 创建axios实例
const instance = axios.create({
  baseURL: process.env.VUE_APP_API_URL || 'http://localhost:8081',
  timeout: 15000
})

// 请求拦截器
instance.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
instance.interceptors.response.use(
  response => {
    return response
  },
  error => {
    if (error.response) {
      switch (error.response.status) {
        case 401:
          // 清除token并跳转登录页
          localStorage.removeItem('token')
          localStorage.removeItem('user')
          router.replace({
            path: '/login',
            query: { redirect: router.currentRoute.fullPath }
          })
          break
        case 403:
          router.push('/403')
          break
        case 404:
          router.push('/404')
          break
        case 500:
          router.push('/500')
          break
      }
    }
    return Promise.reject(error)
  }
)

export default instance 