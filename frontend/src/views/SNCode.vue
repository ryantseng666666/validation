<template>
  <div class="sn-code">
    <el-container>
      <NavHeader />
      <el-main>
        <div class="sn-code-content">
          <el-card class="upload-card">
            <template #header>
              <div class="card-header">
                <h3>SN码识别</h3>
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
                <span class="label">SN码：</span>
                <span class="value">{{ result.snCode }}</span>
              </div>
              <div class="result-item">
                <span class="label">识别时间：</span>
                <span class="value">{{ new Date().toLocaleString() }}</span>
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
import NavHeader from '../components/NavHeader.vue'
import { useAuth } from '../composables/useAuth'
import { useFileUpload } from '../composables/useFileUpload'

const router = useRouter()
const result = ref(null)
const { userInfo, isLoggedIn, loadUserInfo } = useAuth()
const { uploading, previewImage, triggerUpload } = useFileUpload({
  maxSize: 5,
  acceptTypes: ['image/jpeg', 'image/png'],
  onSuccess: (base64Image) => {
    // Handle successful upload
  }
})

onMounted(() => {
  loadUserInfo()
})

const handleLogout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('user')
  ElMessage.success('已退出登录')
  router.push('/login')
}

const handlePredict = async () => {
  if (!previewImage.value) return
  
  uploading.value = true
  try {
    const base64Image = previewImage.value.split(',')[1]
    console.log("发送的base64Image长度: ", base64Image.length)
    
    const data = await request.post('/sn-code/predict', {
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
.sn-code {
  min-height: 100vh;
  background-color: #f5f7fa;
}

.el-main {
  padding-top: 80px;
  max-width: 1200px;
  margin: 0 auto;
}

.sn-code-content {
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

.nav-logo-image {
  height: 32px;
  width: auto;
  margin-right: 8px;
}
</style> 