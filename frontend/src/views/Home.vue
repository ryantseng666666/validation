<template>
  <div class="home">
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
            <template v-else>
              <el-button type="primary" @click="$router.push('/login')">登录</el-button>
              <el-button @click="$router.push('/register')">注册</el-button>
            </template>
          </div>
        </div>
      </el-header>

      <!-- 主要内容区域 -->
      <el-main>
        <template v-if="isLoggedIn">
          <div class="dashboard">
            <h2 class="section-title">功能模块</h2>
            <el-row :gutter="24">
              <el-col :span="8">
                <el-card class="feature-card" shadow="hover" @click="$router.push('/speed-test')">
                  <div class="feature-icon">
                    <el-icon><Monitor /></el-icon>
                  </div>
                  <h3>速度测试识别</h3>
                  <p>自动识别网速测试截图，快速获取上传速度、下载速度等信息</p>
                </el-card>
              </el-col>
              <el-col :span="8">
                <el-card class="feature-card" shadow="hover" @click="$router.push('/optical-power')">
                  <div class="feature-icon">
                    <el-icon><Lightning /></el-icon>
                  </div>
                  <h3>光功率识别</h3>
                  <p>精确识别光功率读数，提供准确的数值分析</p>
                </el-card>
              </el-col>
              <el-col :span="8">
                <el-card class="feature-card" shadow="hover" @click="$router.push('/sn-code')">
                  <div class="feature-icon">
                    <el-icon><Cpu /></el-icon>
                  </div>
                  <h3>SN码识别</h3>
                  <p>快速识别设备SN码，提高工作效率</p>
                </el-card>
              </el-col>
            </el-row>

            <h2 class="section-title">数据概览</h2>
            <el-row :gutter="24">
              <el-col :span="6">
                <el-card class="stat-card" shadow="hover">
                  <div class="stat-value">128</div>
                  <div class="stat-label">今日识别总数</div>
                </el-card>
              </el-col>
              <el-col :span="6">
                <el-card class="stat-card" shadow="hover">
                  <div class="stat-value">98%</div>
                  <div class="stat-label">识别准确率</div>
                </el-card>
              </el-col>
              <el-col :span="6">
                <el-card class="stat-card" shadow="hover">
                  <div class="stat-value">1.2s</div>
                  <div class="stat-label">平均响应时间</div>
                </el-card>
              </el-col>
              <el-col :span="6">
                <el-card class="stat-card" shadow="hover">
                  <div class="stat-value">2,456</div>
                  <div class="stat-label">累计处理工单</div>
                </el-card>
              </el-col>
            </el-row>
          </div>
        </template>
        
        <template v-else>
          <div class="welcome">
            <h1>欢迎使用装维质检系统</h1>
            <p>高效、准确的质检解决方案</p>
            <div class="action-buttons">
              <el-button type="primary" size="large" @click="$router.push('/login')">立即开始</el-button>
              <el-button size="large" @click="$router.push('/register')">注册账号</el-button>
            </div>
          </div>
        </template>
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
.home {
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
  color: #1d2129;
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

.dashboard {
  padding: 24px;
}

.section-title {
  font-size: 24px;
  font-weight: 500;
  color: #1d2129;
  margin: 32px 0 24px;
}

.feature-card {
  height: 240px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
}

.feature-card:hover {
  transform: translateY(-4px);
}

.feature-icon {
  font-size: 48px;
  color: #1890ff;
  margin-bottom: 16px;
}

.feature-card h3 {
  font-size: 18px;
  color: #1d2129;
  margin: 16px 0 8px;
}

.feature-card p {
  font-size: 14px;
  color: #4e5969;
  line-height: 1.5;
  padding: 0 24px;
}

.stat-card {
  text-align: center;
  padding: 24px;
}

.stat-value {
  font-size: 36px;
  font-weight: 500;
  color: #1890ff;
  line-height: 1;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  color: #4e5969;
}

.welcome {
  text-align: center;
  padding: 120px 0;
}

.welcome h1 {
  font-size: 48px;
  font-weight: 600;
  color: #1d2129;
  margin-bottom: 16px;
}

.welcome p {
  font-size: 20px;
  color: #4e5969;
  margin-bottom: 32px;
}

.action-buttons {
  display: flex;
  gap: 16px;
  justify-content: center;
}

.header-right {
  display: flex;
  gap: 16px;
  align-items: center;
}
</style> 