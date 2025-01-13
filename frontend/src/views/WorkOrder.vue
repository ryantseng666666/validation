<template>
  <div class="work-order">
    <el-container>
      <!-- 顶部导航栏 -->
      <el-header class="header">
        <div class="header-content">
          <div class="header-left">
            <div class="logo">
              <img src="../assets/cmhk.png" alt="CMHK Logo" class="nav-logo-image">
              <h2>装维质检系统</h2>
            </div>
            <el-menu
              mode="horizontal"
              :router="true"
              class="main-menu"
              :default-active="$route.path">
              <el-menu-item index="/">
                <el-icon><House /></el-icon>
                <span>首页</span>
              </el-menu-item>
              <el-menu-item index="/work-order">
                <el-icon><Document /></el-icon>
                <span>工单详情</span>
              </el-menu-item>
              <el-menu-item index="/speed-test">
                <el-icon><Monitor /></el-icon>
                <span>速度测试识别</span>
              </el-menu-item>
              <el-menu-item index="/optical-power">
                <el-icon><Lightning /></el-icon>
                <span>光功率识别</span>
              </el-menu-item>
              <el-menu-item index="/sn-code">
                <el-icon><Cpu /></el-icon>
                <span>SN识别</span>
              </el-menu-item>
            </el-menu>
          </div>
          <div class="header-right">
            <template v-if="isLoggedIn">
              <el-dropdown>
                <span class="user-dropdown">
                  {{ userInfo.username }}
                  <el-icon><CaretBottom /></el-icon>
                </span>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item @click="handleLogout">退出登录</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </template>
          </div>
        </div>
      </el-header>

      <!-- 主要内容区域 -->
      <el-main>
        <div class="work-order-content">
          <el-card class="work-order-card">
            <template #header>
              <div class="card-header">
                <h3>工单详情</h3>
              </div>
            </template>
            
            <!-- 工单内容将在这里实现 -->
            <div class="work-order-placeholder">
              <el-empty description="工单功能开发中..." />
            </div>
          </el-card>
        </div>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  House,
  Document,
  Monitor,
  Lightning,
  Cpu,
  CaretBottom
} from '@element-plus/icons-vue'

const router = useRouter()
const userInfo = ref({
  username: '',
  role: '',
  email: ''
})

const isLoggedIn = computed(() => {
  return localStorage.getItem('token') !== null
})

onMounted(() => {
  const userStr = localStorage.getItem('user')
  if (userStr) {
    userInfo.value = JSON.parse(userStr)
  }
})

const handleLogout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('user')
  ElMessage.success('已退出登录')
  router.push('/login')
}
</script>

<style scoped>
.work-order {
  min-height: 100vh;
  background-color: #f0f2f5;
}

.header {
  background: #fff;
  box-shadow: 0 1px 4px rgba(0,21,41,0.08);
  position: fixed;
  width: 100%;
  z-index: 100;
  padding: 0;
}

.header-content {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  min-width: 1200px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 24px;
  flex: 1;
}

.logo {
  display: flex;
  align-items: center;
  min-width: 200px;
}

.logo h2 {
  margin: 0;
  font-size: 18px;
  white-space: nowrap;
}

.nav-logo-image {
  height: 32px;
  width: auto;
  margin-right: 8px;
}

.main-menu {
  border-bottom: none;
  flex: 1;
  white-space: nowrap;
}

:deep(.el-menu--horizontal) {
  border-bottom: none;
}

:deep(.el-menu--horizontal > .el-menu-item) {
  display: flex;
  align-items: center;
  gap: 4px;
  height: 64px;
  line-height: 64px;
  border-bottom: none;
  padding: 0 16px;
}

:deep(.el-menu-item.is-active) {
  color: #1890ff;
  background: transparent;
  border-bottom: 2px solid #1890ff;
}

.user-dropdown {
  cursor: pointer;
  color: #1d2129;
  display: flex;
  align-items: center;
  gap: 4px;
}

.el-main {
  padding-top: 84px;
  min-height: 100vh;
}

.work-order-content {
  padding: 24px;
  max-width: 1200px;
  margin: 0 auto;
}

.work-order-card {
  margin-bottom: 24px;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.card-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 500;
  color: #1d2129;
}

.work-order-placeholder {
  padding: 40px;
  text-align: center;
}

.header-right {
  display: flex;
  gap: 16px;
  align-items: center;
}
</style> 