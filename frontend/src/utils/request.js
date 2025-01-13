// 导入axios HTTP客户端库
import axios from 'axios'

// 创建axios实例并配置默认值
const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,  // 设置API基础URL
  timeout: 10000,  // 设置请求超时时间为10秒
  withCredentials: true,  // 允许跨域请求携带凭证
  headers: {
    'Content-Type': 'application/json',  // 设置请求内容类型为JSON
    'Accept': 'application/json'  // 设置接受的响应类型为JSON
  }
})

// 添加请求拦截器,在发送请求前执行
request.interceptors.request.use(
  config => {
    // 从localStorage获取token
    const token = localStorage.getItem('token')
    if (token) {
      // 如果token存在,添加到请求头中
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  error => {
    // 发生错误时返回Promise错误
    return Promise.reject(error)
  }
)

// 添加响应拦截器,在接收响应后执行
request.interceptors.response.use(
  response => {
    // 直接返回响应数据
    return response.data
  },
  error => {
    // 处理错误响应
    if (error.response) {
      switch (error.response.status) {
        case 401:  // 未授权
          // 清除token和用户信息并跳转到登录页
          localStorage.removeItem('token')
          localStorage.removeItem('user')
          window.location.href = '/login'
          break
        case 403:  // 禁止访问
          window.location.href = '/403'
          break
        case 404:  // 页面未找到
          window.location.href = '/404'
          break
        case 500:  // 服务器错误
          window.location.href = '/500'
          break
      }
    }
    // 返回Promise错误
    return Promise.reject(error)
  }
)

// 导出配置好的axios实例
export default request 