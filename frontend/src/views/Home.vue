<template>
  <div class="home">
    <el-container>
      <el-header>
        <div class="header-content">
          <div class="header-left">
            <h1>装维质检系统</h1>
            <el-menu mode="horizontal" :router="true" v-if="isLoggedIn">
              <el-menu-item index="/">首页</el-menu-item>
              <el-menu-item index="/speed-test">速度测试</el-menu-item>
              <el-menu-item index="/optical-power">光功率读数</el-menu-item>
              <el-menu-item index="/sn-code">SN码识别</el-menu-item>
            </el-menu>
          </div>
          <div class="header-right" v-if="isLoggedIn">
            <span class="username">{{ userInfo.username }}</span>
            <el-button type="text" @click="handleLogout">退出登录</el-button>
          </div>
          <div class="header-right" v-else>
            <el-button type="text" @click="$router.push('/login')">登录</el-button>
            <el-button type="text" @click="$router.push('/register')">注册</el-button>
          </div>
        </div>
      </el-header>
      
      <el-main>
        <el-row :gutter="20" justify="center">
          <el-col :span="16">
            <!-- 已登录显示功能模块 -->
            <template v-if="isLoggedIn">
              <h2 class="section-title">功能模块</h2>
              <el-row :gutter="20">
                <el-col :span="8">
                  <el-card class="feature-card" @click="$router.push('/speed-test')">
                    <h3>速度测试场景</h3>
                    <p>自动识别网速测试截图，快速获取上传速度、下载速度等信息</p>
                    <el-button type="primary">开始使用</el-button>
                  </el-card>
                </el-col>
                <el-col :span="8">
                  <el-card class="feature-card" @click="$router.push('/optical-power')">
                    <h3>光功率读数场景</h3>
                    <p>精确识别光功率读数，提供准确的数值分析</p>
                    <el-button type="primary">开始使用</el-button>
                  </el-card>
                </el-col>
                <el-col :span="8">
                  <el-card class="feature-card" @click="$router.push('/sn-code')">
                    <h3>SN码识别场景</h3>
                    <p>快速识别设备SN码，提高工作效率</p>
                    <el-button type="primary">开始使用</el-button>
                  </el-card>
                </el-col>
              </el-row>
              
              <h2 class="section-title">使用流程</h2>
              <el-row :gutter="20">
                <el-col :span="8">
                  <div class="step-card">
                    <div class="step-number">1</div>
                    <h3>上传图片</h3>
                    <p>选择需要识别的图片文件</p>
                  </div>
                </el-col>
                <el-col :span="8">
                  <div class="step-card">
                    <div class="step-number">2</div>
                    <h3>AI识别</h3>
                    <p>系统自动分析图片内容</p>
                  </div>
                </el-col>
                <el-col :span="8">
                  <div class="step-card">
                    <div class="step-number">3</div>
                    <h3>获取结果</h3>
                    <p>快速展示识别结果</p>
                  </div>
                </el-col>
              </el-row>
            </template>
            
            <!-- 未登录显示欢迎信息 -->
            <el-card class="welcome-card" v-else>
              <h2>欢迎使用装维质检系统</h2>
              <p>这是一个用于验证和管理的系统平台。</p>
              <div class="action-buttons">
                <el-button type="primary" @click="$router.push('/login')">立即登录</el-button>
                <el-button @click="$router.push('/register')">注册账号</el-button>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

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
  background: linear-gradient(180deg, #f8f9fa 0%, #fff 100%);
  position: relative;
  overflow: hidden;
}

.home::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 600px;
  background: linear-gradient(135deg, rgba(22,93,255,0.05) 0%, rgba(64,128,255,0.05) 100%);
  transform: skewY(-6deg);
  transform-origin: top left;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
  height: 64px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 48px;
}

.header-left h1 {
  font-size: 24px;
  font-weight: 600;
  color: #1d2129;
  margin: 0;
  background: linear-gradient(90deg, #165dff, #4080ff);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 24px;
}

.username {
  font-size: 14px;
  color: #4e5969;
  font-weight: 500;
}

.el-header {
  background-color: rgba(255,255,255,0.9);
  backdrop-filter: blur(8px);
  box-shadow: 0 1px 2px rgba(0,21,41,0.08);
  position: fixed;
  width: 100%;
  z-index: 100;
}

.el-main {
  padding-top: 84px;
  position: relative;
}

.section-title {
  margin: 48px 0 32px;
  color: #1d2129;
  font-size: 32px;
  font-weight: 600;
  text-align: center;
  position: relative;
}

.section-title::after {
  content: '';
  position: absolute;
  bottom: -12px;
  left: 50%;
  transform: translateX(-50%);
  width: 48px;
  height: 4px;
  background: linear-gradient(90deg, #165dff, #4080ff);
  border-radius: 2px;
}

.feature-card {
  height: 100%;
  text-align: center;
  padding: 40px 24px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: none;
  background: #fff;
  border-radius: 16px;
  position: relative;
  overflow: hidden;
}

.feature-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #165dff, #4080ff);
  opacity: 0;
  transition: opacity 0.3s;
}

.feature-card:hover::before {
  opacity: 1;
}

.feature-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 32px rgba(0,0,0,0.1);
}

.feature-card h3 {
  margin: 24px 0 16px;
  color: #1d2129;
  font-size: 22px;
  font-weight: 600;
}

.feature-card p {
  color: #4e5969;
  margin-bottom: 28px;
  min-height: 48px;
  line-height: 1.8;
  font-size: 15px;
}

.feature-card .el-button {
  padding: 12px 28px;
  font-size: 16px;
  border-radius: 8px;
  background: linear-gradient(90deg, #165dff, #4080ff);
  border: none;
  transition: all 0.3s;
}

.feature-card .el-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 16px rgba(22,93,255,0.2);
}

.step-card {
  text-align: center;
  padding: 40px 24px;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.06);
  position: relative;
  overflow: hidden;
}

.step-card::after {
  content: '';
  position: absolute;
  top: 0;
  right: 0;
  width: 120px;
  height: 120px;
  background: linear-gradient(135deg, rgba(22,93,255,0.05) 0%, rgba(64,128,255,0.05) 100%);
  border-radius: 0 0 0 120px;
}

.step-number {
  width: 56px;
  height: 56px;
  line-height: 56px;
  border-radius: 28px;
  background: linear-gradient(135deg, #165dff 0%, #4080ff 100%);
  color: white;
  font-size: 24px;
  margin: 0 auto 28px;
  font-weight: 600;
  position: relative;
  z-index: 1;
  box-shadow: 0 8px 16px rgba(22,93,255,0.2);
}

.step-card h3 {
  color: #1d2129;
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 16px;
  position: relative;
  z-index: 1;
}

.step-card p {
  color: #4e5969;
  line-height: 1.8;
  font-size: 15px;
  position: relative;
  z-index: 1;
}

.welcome-card {
  margin-top: 48px;
  text-align: center;
  padding: 64px;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.06);
  position: relative;
  overflow: hidden;
}

.welcome-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 200px;
  background: linear-gradient(135deg, rgba(22,93,255,0.05) 0%, rgba(64,128,255,0.05) 100%);
  transform: skewY(-6deg);
  transform-origin: top left;
}

.welcome-card h2 {
  margin-bottom: 28px;
  color: #1d2129;
  font-size: 36px;
  font-weight: 600;
  position: relative;
}

.welcome-card p {
  margin-bottom: 16px;
  color: #4e5969;
  line-height: 1.8;
  font-size: 16px;
  position: relative;
}

.action-buttons {
  margin-top: 40px;
  position: relative;
}

.action-buttons .el-button {
  margin: 0 16px;
  padding: 14px 36px;
  font-size: 16px;
  border-radius: 8px;
  transition: all 0.3s;
}

.action-buttons .el-button--primary {
  background: linear-gradient(90deg, #165dff, #4080ff);
  border: none;
}

.action-buttons .el-button--primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 16px rgba(22,93,255,0.2);
}

.action-buttons .el-button--default {
  border: 2px solid #e5e6eb;
  color: #4e5969;
}

.action-buttons .el-button--default:hover {
  border-color: #165dff;
  color: #165dff;
}

:deep(.el-menu--horizontal) {
  border-bottom: none;
  background: transparent;
}

:deep(.el-menu-item) {
  font-size: 15px;
  color: #4e5969;
  height: 64px;
  line-height: 64px;
  padding: 0 24px;
  transition: all 0.3s;
}

:deep(.el-menu-item.is-active) {
  color: #165dff;
  font-weight: 500;
  background: linear-gradient(90deg, rgba(22,93,255,0.1), rgba(64,128,255,0.1));
  border-radius: 8px;
}

:deep(.el-menu-item:hover) {
  color: #165dff;
  background: rgba(22,93,255,0.05);
  border-radius: 8px;
}

@media (max-width: 768px) {
  .header-left {
    gap: 24px;
  }
  
  .header-left h1 {
    font-size: 20px;
  }
  
  .section-title {
    font-size: 28px;
  }
  
  .feature-card {
    padding: 32px 20px;
  }
  
  .welcome-card {
    padding: 48px 24px;
  }
  
  .welcome-card h2 {
    font-size: 28px;
  }
  
  .action-buttons .el-button {
    padding: 12px 24px;
    margin: 0 8px;
  }
}
</style> 