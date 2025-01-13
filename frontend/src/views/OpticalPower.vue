<template>
  <div class="optical-power">
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
        <div class="optical-power-content">
          <el-card class="upload-card">
            <template #header>
              <div class="card-header">
                <h3>光功率识别</h3>
              </div>
            </template>
            
            <!-- 上传预览区域 -->
            <div class="preview-area" @click="triggerUpload" v-if="!previewImage">
              <el-icon class="upload-icon"><Upload /></el-icon>
              <div class="upload-text">点击或拖拽图片到此处上传</div>
              <div class="upload-tip">支持 jpg/png 格式，大小不超过 5MB</div>
            </div>
            <div class="preview-area" v-else>
              <img :src="previewImage" class="preview-image" alt="预览图片" />
            </div>

            <!-- 操作按钮区域 -->
            <div class="action-area">
              <el-button type="primary" @click="triggerUpload" :disabled="uploading">
                {{ previewImage ? '重新上传' : '上传图片' }}
              </el-button>
              <el-button type="success" @click="handlePredict" :disabled="!previewImage || uploading">
                开始识别
              </el-button>
            </div>

            <!-- 结果展示区域 -->
            <div class="result-area" v-if="result">
              <div class="result-item">
                <span class="label">功率值：</span>
                <span class="value">{{ result.opticalPower }} dBm</span>
              </div>
              <div class="result-item">
                <span class="label">状态：</span>
                <span class="value" :class="{ 'status-error': !result.valid }">
                  {{ result.valid ? '有效' : '无效' }}
                </span>
              </div>
              <div class="result-item" v-if="result.rawText">
                <span class="label">原始文本：</span>
                <span class="value">{{ result.rawText }}</span>
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
import request from '../utils/request'
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

const uploading = ref(false)
const fileInput = ref(null)

const triggerUpload = () => {
  const input = document.createElement('input')
  input.type = 'file'
  input.accept = 'image/*'
  input.onchange = (e) => {
    const file = e.target.files[0]
    if (file) {
      handleFileUpload(file)
    }
  }
  input.click()
}

const handleFileUpload = (file) => {
  if (!file) return
  
  const isImage = file.type.startsWith('image/')
  const isLt5M = file.size / 1024 / 1024 < 5

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return
  }
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过 5MB!')
    return
  }

  const reader = new FileReader()
  reader.readAsDataURL(file)
  reader.onload = (e) => {
    previewImage.value = e.target.result
  }
}

const handlePredict = async () => {
  if (!previewImage.value) return
  
  uploading.value = true
  try {
    const base64Image = previewImage.value.split(',')[1]
    const data = await request.post('/optical-power/predict', {
      base64Image: base64Image
    })
    
    console.log('识别结果:', data)
    result.value = data
    ElMessage.success('识别成功')
  } catch (error) {
    console.error('识别失败:', error)
    ElMessage.error('识别失败：' + error.message)
  } finally {
    uploading.value = false
  }
}
</script>

<style scoped>
.optical-power {
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

.optical-power-content {
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

.upload-area {
  display: flex;
  justify-content: center;
  padding: 24px 0;
}

.upload-component {
  width: 100%;
}

.result-content {
  padding: 16px 0;
}

.result-item {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
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

.header-right {
  display: flex;
  gap: 16px;
  align-items: center;
}

.preview-image {
  width: 100%;
  max-height: 300px;
  object-fit: contain;
}

.uploaded-image-container {
  margin-bottom: 20px;
  text-align: center;
  background-color: #f5f7fa;
  padding: 16px;
  border-radius: 4px;
}

.uploaded-image {
  max-width: 100%;
  max-height: 400px;
  object-fit: contain;
  border-radius: 4px;
}

.result-details {
  margin-top: 20px;
  padding: 16px;
  background-color: #f5f7fa;
  border-radius: 4px;
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

:deep(.el-upload-dragger:hover) {
  border-color: #1890ff;
}

:deep(.el-upload-dragger.is-dragover) {
  background-color: rgba(24, 144, 255, 0.1);
  border-color: #1890ff;
}

.preview-area {
  width: 100%;
  height: 300px;
  border: 2px dashed #d9d9d9;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: border-color 0.3s;
  margin-bottom: 20px;
  background-color: #fafafa;
}

.preview-area:hover {
  border-color: #1890ff;
}

.upload-icon {
  font-size: 48px;
  color: #8c8c8c;
  margin-bottom: 16px;
}

.upload-text {
  font-size: 16px;
  color: #262626;
  margin-bottom: 8px;
}

.upload-tip {
  font-size: 14px;
  color: #8c8c8c;
}

.preview-image {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
}

.action-area {
  display: flex;
  justify-content: center;
  gap: 16px;
  margin-bottom: 24px;
}

.result-area {
  background-color: #f5f7fa;
  padding: 20px;
  border-radius: 8px;
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

.status-warning {
  color: #ff4d4f;
}
</style> 