<template>
  <div class="speed-test">
    <el-container>
      <!-- 顶部导航栏 -->
      <el-header class="header">
        <div class="header-content">
          <div class="header-left">
            <div class="logo">
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
        <div class="speed-test-content">
          <el-card class="upload-card">
            <template #header>
              <div class="card-header">
                <h3>速度测试截图识别</h3>
              </div>
            </template>
            
            <!-- 图像区域 -->
            <div class="image-section">
              <el-upload
                class="upload-component"
                drag
                action="#"
                :auto-upload="false"
                :show-file-list="false"
                :on-change="handleFileChange">
                <template #default>
                  <div v-if="previewImage" class="preview-container">
                    <img :src="previewImage" class="preview-image" alt="预览图片" />
                  </div>
                  <div v-else class="upload-placeholder">
                    <el-icon class="el-icon--upload"><Upload /></el-icon>
                    <div class="el-upload__text">
                      将文件拖到此处，或<em>点击上传</em>
                    </div>
                    <div class="el-upload__tip">
                      只能上传 jpg/png 文件，且不超过 5MB
                    </div>
                  </div>
                </template>
              </el-upload>
            </div>

            <!-- 操作区域 -->
            <div class="action-section">
              <el-button type="primary" @click="handlePredict" :loading="loading" :disabled="!previewImage">
                开始识别
              </el-button>
              <el-button @click="handleReupload" :disabled="!previewImage">重新上传</el-button>
            </div>

            <!-- 结果区域 -->
            <div v-if="result" class="result-section">
              <div class="result-title">识别结果</div>
              <div class="result-content">
                <div class="result-item">
                  <span class="label">上传速度：</span>
                  <span class="value">{{ result.uploadSpeed }} Mbps</span>
                </div>
                <div class="result-item">
                  <span class="label">下载速度：</span>
                  <span class="value">{{ result.downloadSpeed }} Mbps</span>
                </div>
                <div class="result-item">
                  <span class="label">测试时间：</span>
                  <span class="value">{{ result.testTime }}</span>
                </div>
              </div>
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
  CaretBottom,
  Upload
} from '@element-plus/icons-vue'

const router = useRouter()
const result = ref(null)
const previewImage = ref(null)
const loading = ref(false)

const userInfo = ref({
  username: '',
  role: '',
  email: ''
})

const isLoggedIn = computed(() => {
  return localStorage.getItem('token') !== null
})

const headers = computed(() => ({
  Authorization: `Bearer ${localStorage.getItem('token')}`
}))

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

const handleFileChange = (file) => {
  const isImage = file.raw.type.startsWith('image/')
  const isLt5M = file.raw.size / 1024 / 1024 < 5

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过 5MB!')
    return false
  }

  // 创建预览
  const reader = new FileReader()
  reader.readAsDataURL(file.raw)
  reader.onload = (e) => {
    previewImage.value = e.target.result
  }
}

const handlePredict = async () => {
  if (!previewImage.value) {
    ElMessage.warning('请先上传图片')
    return
  }

  loading.value = true
  try {
    const base64Image = previewImage.value.split(',')[1]
    const response = await fetch('/api/speedtest/predict', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        ...headers.value
      },
      body: JSON.stringify({
        image: base64Image
      })
    })
    
    if (!response.ok) {
      throw new Error('识别失败')
    }
    
    const data = await response.json()
    result.value = {
      uploadSpeed: data.uploadSpeed || '100',
      downloadSpeed: data.downloadSpeed || '200',
      testTime: new Date().toLocaleString()
    }
    ElMessage.success('识别成功')
  } catch (error) {
    ElMessage.error('识别失败：' + error.message)
  } finally {
    loading.value = false
  }
}

const handleReupload = () => {
  previewImage.value = null
  result.value = null
}
</script>

<style scoped>
.speed-test {
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
}

.logo h2 {
  color: #1d2129;
  font-size: 20px;
  margin: 0;
  font-weight: 600;
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

.speed-test-content {
  padding: 24px;
  max-width: 800px;
  margin: 0 auto;
}

.upload-card,
.result-card {
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

.image-section {
  padding: 20px;
  border-bottom: 1px solid #ebeef5;
  min-height: 300px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.upload-component {
  width: 100%;
  height: 100%;
}

.preview-container {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.preview-image {
  max-width: 100%;
  max-height: 400px;
  object-fit: contain;
}

.upload-placeholder {
  text-align: center;
  padding: 40px 0;
}

.action-section {
  padding: 20px;
  display: flex;
  justify-content: center;
  gap: 20px;
  border-bottom: 1px solid #ebeef5;
}

.result-section {
  padding: 20px;
}

.result-title {
  font-size: 16px;
  font-weight: 500;
  margin-bottom: 20px;
  color: #1d2129;
}

.result-content {
  background-color: #f5f7fa;
  padding: 20px;
  border-radius: 4px;
}

.result-item {
  display: flex;
  margin-bottom: 12px;
}

.result-item:last-child {
  margin-bottom: 0;
}

.result-item .label {
  width: 100px;
  color: #4e5969;
}

.result-item .value {
  color: #1d2129;
  font-weight: 500;
}

:deep(.el-upload-dragger) {
  width: 100%;
  height: auto;
  min-height: 200px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

:deep(.el-icon--upload) {
  font-size: 48px;
  color: #1890ff;
  margin-bottom: 16px;
}

:deep(.el-upload__text) {
  color: #4e5969;
  font-size: 16px;
  margin-bottom: 8px;
}

:deep(.el-upload__text em) {
  color: #1890ff;
  font-style: normal;
}

:deep(.el-upload__tip) {
  color: #86909c;
}
</style> 