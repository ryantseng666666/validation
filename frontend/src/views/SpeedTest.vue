<template>
  <div class="speed-test">
    <el-card class="upload-card">
      <template #header>
        <div class="card-header">
          <h2>速度测试识别</h2>
          <p class="subtitle">上传网速测试截图，自动识别上传和下载速度</p>
        </div>
      </template>
      
      <el-upload
        class="upload-area"
        drag
        action="#"
        :auto-upload="false"
        :show-file-list="false"
        :on-change="handleFileChange"
      >
        <el-icon class="el-icon--upload"><upload-filled /></el-icon>
        <div class="el-upload__text">
          将文件拖到此处，或<em>点击上传</em>
        </div>
        <template #tip>
          <div class="el-upload__tip">
            支持jpg/png格式的图片
          </div>
        </template>
      </el-upload>

      <div v-if="previewUrl" class="preview-area">
        <h3>预览图片</h3>
        <img :src="previewUrl" class="preview-image" />
        <el-button type="primary" @click="handleAnalyze">开始识别</el-button>
      </div>

      <div v-if="result" class="result-area">
        <h3>识别结果</h3>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="下载速度">
            {{ result.downloadSpeed }} Mbps
          </el-descriptions-item>
          <el-descriptions-item label="上传速度">
            {{ result.uploadSpeed }} Mbps
          </el-descriptions-item>
          <el-descriptions-item label="参考编号">
            {{ result.referenceNumber }}
          </el-descriptions-item>
          <el-descriptions-item label="IP地址">
            {{ result.ipAddress }}
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { UploadFilled } from '@element-plus/icons-vue'
import request from '../utils/request'

const previewUrl = ref('')
const selectedFile = ref(null)
const result = ref(null)

const handleFileChange = (file) => {
  if (!file) return
  
  const isImage = file.raw.type === 'image/jpeg' || file.raw.type === 'image/png'
  if (!isImage) {
    ElMessage.error('只能上传jpg/png格式的图片！')
    return
  }
  
  selectedFile.value = file.raw
  previewUrl.value = URL.createObjectURL(file.raw)
}

const handleAnalyze = async () => {
  if (!selectedFile.value) {
    ElMessage.warning('请先选择图片')
    return
  }

  try {
    // 将图片转换为base64
    const reader = new FileReader()
    reader.readAsDataURL(selectedFile.value)
    reader.onload = async () => {
      const base64Image = reader.result.split(',')[1]
      
      // 调用后端API
      const response = await request.post('/api/speedtest/predict', {
        image: base64Image
      })
      
      result.value = response
      ElMessage.success('识别成功')
    }
  } catch (error) {
    ElMessage.error('识别失败：' + error.message)
  }
}
</script>

<style scoped>
.speed-test {
  padding: 40px 20px;
  max-width: 800px;
  margin: 0 auto;
  min-height: calc(100vh - 64px);
  background: linear-gradient(180deg, #f8f9fa 0%, #fff 100%);
  position: relative;
  overflow: hidden;
}

.speed-test::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 400px;
  background: linear-gradient(135deg, rgba(22,93,255,0.05) 0%, rgba(64,128,255,0.05) 100%);
  transform: skewY(-6deg);
  transform-origin: top left;
}

.upload-card {
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.06);
  border: none;
  position: relative;
  background: rgba(255,255,255,0.9);
  backdrop-filter: blur(8px);
}

.card-header {
  text-align: center;
  padding: 32px 0;
  border-bottom: 1px solid #f2f3f5;
  position: relative;
}

.card-header::after {
  content: '';
  position: absolute;
  bottom: -1px;
  left: 50%;
  transform: translateX(-50%);
  width: 48px;
  height: 4px;
  background: linear-gradient(90deg, #165dff, #4080ff);
  border-radius: 2px;
}

.card-header h2 {
  margin: 0;
  color: #1d2129;
  font-size: 28px;
  font-weight: 600;
  background: linear-gradient(90deg, #165dff, #4080ff);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.subtitle {
  margin: 12px 0 0;
  color: #4e5969;
  font-size: 15px;
  line-height: 1.8;
}

.upload-area {
  margin: 40px 0;
  position: relative;
}

:deep(.el-upload-dragger) {
  width: 100%;
  height: 280px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  border: 2px dashed #e5e6eb;
  border-radius: 12px;
  background: #fafafa;
  transition: all 0.3s;
  position: relative;
  overflow: hidden;
}

:deep(.el-upload-dragger::before) {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(22,93,255,0.02) 0%, rgba(64,128,255,0.02) 100%);
  opacity: 0;
  transition: opacity 0.3s;
}

:deep(.el-upload-dragger:hover) {
  border-color: #165dff;
  transform: translateY(-2px);
  box-shadow: 0 8px 16px rgba(22,93,255,0.1);
}

:deep(.el-upload-dragger:hover::before) {
  opacity: 1;
}

:deep(.el-icon--upload) {
  font-size: 56px;
  color: #165dff;
  margin-bottom: 20px;
  filter: drop-shadow(0 4px 8px rgba(22,93,255,0.2));
}

:deep(.el-upload__text) {
  color: #4e5969;
  font-size: 16px;
  margin-bottom: 12px;
  line-height: 1.8;
}

:deep(.el-upload__text em) {
  color: #165dff;
  font-style: normal;
  margin: 0 4px;
  font-weight: 500;
}

:deep(.el-upload__tip) {
  color: #86909c;
  font-size: 14px;
  line-height: 1.6;
}

.preview-area {
  margin-top: 48px;
  text-align: center;
  position: relative;
}

.preview-area h3 {
  margin-bottom: 28px;
  color: #1d2129;
  font-size: 20px;
  font-weight: 600;
  position: relative;
  display: inline-block;
}

.preview-area h3::after {
  content: '';
  position: absolute;
  bottom: -8px;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(90deg, #165dff, #4080ff);
  border-radius: 1.5px;
  transform: scaleX(0.6);
}

.preview-image {
  max-width: 100%;
  max-height: 400px;
  margin-bottom: 28px;
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0,0,0,0.08);
  transition: all 0.3s;
}

.preview-image:hover {
  transform: scale(1.02);
  box-shadow: 0 12px 32px rgba(0,0,0,0.12);
}

.preview-area .el-button {
  padding: 14px 32px;
  font-size: 16px;
  border-radius: 8px;
  background: linear-gradient(90deg, #165dff, #4080ff);
  border: none;
  transition: all 0.3s;
}

.preview-area .el-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 16px rgba(22,93,255,0.2);
}

.result-area {
  margin-top: 48px;
  padding-top: 40px;
  border-top: 1px solid #f2f3f5;
  position: relative;
}

.result-area::before {
  content: '';
  position: absolute;
  top: -1px;
  left: 50%;
  transform: translateX(-50%);
  width: 48px;
  height: 4px;
  background: linear-gradient(90deg, #165dff, #4080ff);
  border-radius: 2px;
}

.result-area h3 {
  margin-bottom: 28px;
  color: #1d2129;
  font-size: 20px;
  font-weight: 600;
  text-align: center;
  position: relative;
  display: inline-block;
}

.result-area h3::after {
  content: '';
  position: absolute;
  bottom: -8px;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(90deg, #165dff, #4080ff);
  border-radius: 1.5px;
  transform: scaleX(0.6);
}

:deep(.el-descriptions) {
  margin-top: 28px;
  background: #fafafa;
  border-radius: 12px;
  padding: 24px;
  box-shadow: inset 0 0 0 1px #f2f3f5;
}

:deep(.el-descriptions__title) {
  font-size: 18px;
  font-weight: 600;
  color: #1d2129;
  margin-bottom: 20px;
}

:deep(.el-descriptions__label) {
  color: #4e5969;
  font-weight: normal;
  padding: 16px 24px;
}

:deep(.el-descriptions__content) {
  color: #1d2129;
  font-weight: 500;
  padding: 16px 24px;
}

:deep(.el-descriptions__cell) {
  background: #fff;
}

@media (max-width: 768px) {
  .speed-test {
    padding: 32px 16px;
  }
  
  .card-header {
    padding: 24px 0;
  }
  
  .card-header h2 {
    font-size: 24px;
  }
  
  :deep(.el-upload-dragger) {
    height: 240px;
  }
  
  :deep(.el-icon--upload) {
    font-size: 48px;
  }
  
  .preview-area .el-button {
    padding: 12px 24px;
  }
  
  :deep(.el-descriptions__label),
  :deep(.el-descriptions__content) {
    padding: 12px 16px;
  }
}
</style> 