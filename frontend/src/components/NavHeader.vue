<template>
  <el-header class="header">
    <div class="header-content">
      <!-- Logo区域 -->
      <div class="logo-container">
        <router-link to="/" class="logo">
          <img src="../assets/cmhk.png" alt="CMHK Logo" class="nav-logo-image">
          <span class="logo-text">装维质检系统</span>
        </router-link>
      </div>

      <!-- 导航菜单区域 -->
      <div class="nav-menu-container">
        <el-menu
          mode="horizontal"
          :router="true"
          class="main-menu"
          :ellipsis="false"
          :default-active="currentRoute">
          <el-menu-item v-for="item in menuItems" 
            :key="item.path" 
            :index="item.path"
            class="menu-item">
            <el-icon><component :is="item.icon" /></el-icon>
            <span>{{ item.title }}</span>
          </el-menu-item>
        </el-menu>
      </div>

      <!-- 用户信息区域 -->
      <div class="user-container">
        <template v-if="isLoggedIn">
          <el-dropdown>
            <span class="user-dropdown">
              {{ username }}
              <el-icon><CaretBottom /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="handleLogout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
        <template v-else>
          <el-button type="primary" @click="handleLogin">登录</el-button>
        </template>
      </div>
    </div>
  </el-header>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  HomeFilled,
  Document,
  Monitor,
  Lightning,
  Cpu,
  CaretBottom
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()

// 当前路由
const currentRoute = computed(() => route.path)

// 用户名
const username = computed(() => {
  const user = localStorage.getItem('user')
  return user ? JSON.parse(user).username : 'admin'
})

// 登录状态
const isLoggedIn = computed(() => {
  return localStorage.getItem('token') !== null
})

// 导航菜单配置
const menuItems = [
  { path: '/', title: '首页', icon: 'HomeFilled' },
  { path: '/work-order', title: '工单详情', icon: 'Document' },
  { path: '/speed-test', title: '速度测试识别', icon: 'Monitor' },
  { path: '/optical-power', title: '光功率识别', icon: 'Lightning' },
  { path: '/sn-code', title: 'SN识别', icon: 'Cpu' }
]

// 处理登录
const handleLogin = () => {
  router.push('/login')
}

// 处理登出
const handleLogout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('user')
  ElMessage.success('已退出登录')
  router.push('/login')
}
</script>

<style scoped>
.header {
  background-color: #fff;
  border-bottom: 1px solid #dcdfe6;
  padding: 0;
  position: fixed;
  width: 100%;
  z-index: 1000;
  height: 60px;
}

.header-content {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  max-width: 1400px;
  margin: 0 auto;
  gap: 20px;
}

/* Logo样式 */
.logo-container {
  flex-shrink: 0;
  min-width: 180px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 8px;
  text-decoration: none;
}

.nav-logo-image {
  height: 32px;
  width: auto;
}

.logo-text {
  font-size: 16px;
  font-weight: 500;
  color: #303133;
  white-space: nowrap;
}

/* 导航菜单样式 */
.nav-menu-container {
  flex: 1;
  display: flex;
  justify-content: center;
  overflow: visible;
}

.main-menu {
  border-bottom: none;
  background: transparent;
  display: flex;
  justify-content: center;
  flex-wrap: nowrap;
  width: auto;
}

.menu-item {
  height: 60px;
  line-height: 60px;
  padding: 0 16px;
  font-size: 14px;
  white-space: nowrap;
}

:deep(.el-menu--horizontal) {
  border-bottom: none;
  display: flex;
  justify-content: center;
  flex-wrap: nowrap;
}

:deep(.el-menu-item) {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 0 16px;
  white-space: nowrap;
  overflow: visible;
}

:deep(.el-menu-item .el-icon) {
  margin-right: 4px;
  font-size: 18px;
  flex-shrink: 0;
}

:deep(.el-menu-item span) {
  white-space: nowrap;
  overflow: visible;
}

/* 用户信息样式 */
.user-container {
  flex-shrink: 0;
  min-width: 120px;
  display: flex;
  justify-content: flex-end;
}

.user-dropdown {
  display: flex;
  align-items: center;
  gap: 4px;
  cursor: pointer;
  font-size: 14px;
  color: #606266;
  padding: 0 8px;
}

.user-dropdown .el-icon {
  font-size: 12px;
}

/* 响应式布局 */
@media screen and (max-width: 1200px) {
  .header-content {
    padding: 0 16px;
  }

  .menu-item {
    padding: 0 12px; /* 在较小屏幕上减小padding */
  }

  .main-menu {
    min-width: 500px; /* 较小屏幕上适当减小最小宽度 */
  }
}

@media screen and (max-width: 992px) {
  .logo-text {
    display: none;
  }

  .logo-container {
    min-width: auto;
  }

  .menu-item {
    min-width: 90px; /* 更小屏幕上减小菜单项最小宽度 */
  }

  .main-menu {
    min-width: 450px;
  }
}
</style> 