import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

export const useAuth = () => {
  const router = useRouter()
  const userInfo = ref({
    username: '',
    role: '',
    email: ''
  })

  const isLoggedIn = computed(() => {
    return localStorage.getItem('token') !== null
  })

  const loadUserInfo = () => {
    const userStr = localStorage.getItem('user')
    if (userStr) {
      userInfo.value = JSON.parse(userStr)
    }
  }

  const handleLogout = () => {
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    ElMessage.success('已退出登录')
    router.push('/login')
  }

  return {
    userInfo,
    isLoggedIn,
    loadUserInfo,
    handleLogout
  }
} 