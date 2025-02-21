<template>
  <div class="qc-detail">
    <el-container>
      <NavHeader />
      <!-- 主要内容区域 -->
      <el-main>
        <div class="qc-detail-container">
          <!-- 工单概况 -->
          <el-card class="box-card">
            <template #header>
              <div class="card-header">
                <h3>工单概况</h3>
              </div>
            </template>
            <div class="overview-content">
              <div class="overview-item">
                <span class="label">整体质检是否通过：</span>
                <span class="value">
                  <el-tag :type="orderData.qualityStatus === 'Y' ? 'success' : 'warning'" class="status-tag">
                    {{ orderData.qualityStatus === 'Y' ? '已通过' : '未通过' }}
                  </el-tag>
                </span>
              </div>
              <div class="overview-item">
                <span class="label">工单号：</span>
                <span class="value">{{ orderData.contractId || '-' }}</span>
              </div>
              <div class="overview-item">
                <span class="label">客户号：</span>
                <span class="value">{{ orderData.customerOrderId || '-' }}</span>
              </div>
              <div class="overview-item">
                <span class="label">Job Number：</span>
                <span class="value">{{ orderData.jobNo || '-' }}</span>
              </div>
              <div class="overview-item">
                <span class="label">工单类型：</span>
                <span class="value">{{ orderData.orderType || '-' }}</span>
              </div>
              <div class="overview-item">
                <span class="label">工单创建时间：</span>
                <span class="value">{{ formatDate(orderData.createDate) }}</span>
              </div>
            </div>
          </el-card>

          <!-- 宽带速度质检 -->
          <el-card class="box-card">
            <template #header>
              <div class="card-header">
                <h3>宽带速度质检</h3>
              </div>
            </template>
            <div class="form-container">
              <el-form :model="speedForm" label-position="left" label-width="200px" class="qc-form">
                <div class="form-main-content">
                  <div class="form-fields">
                    <el-form-item label="速度测试质检是否通过">
                      <span class="value">
                        <el-tag :type="speedForm.qcPassed ? 'success' : 'warning'" class="status-tag">
                          {{ speedForm.qcPassed ? '已通过' : '未通过' }}
                        </el-tag>
                      </span>
                    </el-form-item>
                    <el-form-item label="AI识别上传速度">
                      <span class="value">
                        {{ orderData.uploadSpeed || '暂无数据' }}
                        {{ orderData.uploadSpeed ? 'MB' : '' }}
                      </span>
                    </el-form-item>
                    <el-form-item label="分包商手动编辑上传速度">
                      <span class="value">
                        {{ orderData.uploadSpeedManual || '暂无数据' }}
                        {{ orderData.uploadSpeedManual ? 'MB' : '' }}
                      </span>
                    </el-form-item>
                    <el-form-item label="管理员输入——上传速度">
                      <div class="input-with-unit">
                        <el-input-number v-model="speedForm.adminUploadSpeed" :min="0" :precision="2" :step="0.1" />
                        <span class="unit">MB</span>
                      </div>
                    </el-form-item>
                    <el-form-item label="上传速度是否达标">
                      <span class="value">
                        <el-tag :type="orderData.uploadSpeedSuccess === 'Y' ? 'success' : 'warning'" class="status-tag">
                          {{ orderData.uploadSpeedSuccess === 'Y' ? '已达标' : '未达标' }}
                        </el-tag>
                      </span>
                    </el-form-item>
                    <el-form-item label="AI识别下载速度">
                      <span class="value">
                        {{ orderData.downloadSpeed || '暂无数据' }}
                        {{ orderData.downloadSpeed ? 'MB' : '' }}
                      </span>
                    </el-form-item>
                    <el-form-item label="分包商手动编辑下载速度">
                      <span class="value">
                        {{ orderData.downloadSpeedManual || '暂无数据' }}
                        {{ orderData.downloadSpeedManual ? 'MB' : '' }}
                      </span>
                    </el-form-item>
                    <el-form-item label="下载速度是否达标">
                      <span class="value">
                        <el-tag :type="orderData.downloadSpeedSuccess === 'Y' ? 'success' : 'warning'" class="status-tag">
                          {{ orderData.downloadSpeedSuccess === 'Y' ? '已达标' : '未达标' }}
                        </el-tag>
                      </span>
                    </el-form-item>
                    <el-form-item label="管理员输入——下载速度">
                      <div class="input-with-unit">
                        <el-input-number v-model="speedForm.adminDownloadSpeed" :min="0" :precision="2" :step="0.1" />
                        <span class="unit">MB</span>
                      </div>
                    </el-form-item>
                    <el-form-item label="IP是否重复">
                      <span class="value">
                        <el-tag :type="!speedForm.ipDuplicate ? 'success' : 'warning'" class="status-tag">
                          {{ !speedForm.ipDuplicate ? '未重复' : '已重复' }}
                        </el-tag>
                      </span>
                    </el-form-item>
                    <el-form-item label="参考编号是否重复">
                      <span class="value">
                        <el-tag :type="!speedForm.refNoDuplicate ? 'success' : 'warning'" class="status-tag">
                          {{ !speedForm.refNoDuplicate ? '未重复' : '已重复' }}
                        </el-tag>
                      </span>
                    </el-form-item>
                  </div>
                  <div class="image-container">
                    <h4>速度测试结果图片</h4>
                    <div class="image-preview">
                      <img v-if="orderData.speedTestResult" :src="orderData.speedTestResult" alt="Speed Test Result" />
                      <div v-else class="no-image">
                        <el-icon><Picture /></el-icon>
                        <span>暂无图片</span>
                      </div>
                    </div>
                  </div>
                </div>
                <div class="form-actions">
                  <el-button type="primary" @click="handleSpeedSubmit" :loading="submitting">提交</el-button>
                  <el-button @click="resetSpeedForm">重置</el-button>
                </div>
              </el-form>
            </div>
          </el-card>

          <!-- 光功率质检 -->
          <el-card class="box-card">
            <template #header>
              <div class="card-header">
                <h3>光功率质检</h3>
              </div>
            </template>
            <div class="form-container">
              <el-form :model="powerForm" label-position="left" label-width="200px" class="qc-form">
                <div class="form-main-content">
                  <div class="form-fields">
                    <el-form-item label="光功率质检是否通过">
                      <span class="value">
                        <el-tag :type="powerForm.qcPassed ? 'success' : 'warning'" class="status-tag">
                          {{ powerForm.qcPassed ? '已通过' : '未通过' }}
                        </el-tag>
                      </span>
                    </el-form-item>
                    <el-form-item label="AI识别FM光功率">
                      <span class="value">
                        {{ orderData.fmOutputPower || '暂无数据' }}
                        {{ orderData.fmOutputPower ? 'dBm' : '' }}
                      </span>
                    </el-form-item>
                    <el-form-item label="分包商手动编辑FM光功率">
                      <span class="value">
                        {{ orderData.fmOutputPowerManual || '暂无数据' }}
                        {{ orderData.fmOutputPowerManual ? 'dBm' : '' }}
                      </span>
                    </el-form-item>
                    <el-form-item label="管理员输入——FM光功率">
                      <div class="input-with-unit">
                        <el-input-number v-model="powerForm.adminFmOpticalPower" :precision="2" :step="0.01" />
                        <span class="unit">dBm</span>
                      </div>
                    </el-form-item>
                    <el-form-item label="AI识别插座光功率">
                      <span class="value">
                        {{ orderData.odbPowerMeter || '暂无数据' }}
                        {{ orderData.odbPowerMeter ? 'dBm' : '' }}
                      </span>
                    </el-form-item>
                    <el-form-item label="分包商手动编辑插座光功率">
                      <span class="value">
                        {{ orderData.odbPowerMeterManual || '暂无数据' }}
                        {{ orderData.odbPowerMeterManual ? 'dBm' : '' }}
                      </span>
                    </el-form-item>
                    <el-form-item label="管理员输入——插座光功率">
                      <div class="input-with-unit">
                        <el-input-number v-model="powerForm.adminSocketOpticalPower" :precision="2" :step="0.01" />
                        <span class="unit">dBm</span>
                      </div>
                    </el-form-item>
                    <el-form-item label="光功率衰减值是否达标">
                      <span class="value">
                        <el-tag :type="orderData.opticalDiffSuccess === 'Y' ? 'success' : 'warning'" class="status-tag">
                          {{ orderData.opticalDiffSuccess === 'Y' ? '已达标' : '未达标' }}
                        </el-tag>
                      </span>
                    </el-form-item>
                  </div>
                  <div class="image-container">
                    <div class="power-images">
                      <div class="power-image-item">
                        <h4>FM光功率图片</h4>
                        <div class="image-preview">
                          <img v-if="orderData.fmOutputPowerSnapshot" :src="orderData.fmOutputPowerSnapshot" alt="FM Output Power" />
                          <div v-else class="no-image">
                            <el-icon><Picture /></el-icon>
                            <span>暂无图片</span>
                          </div>
                        </div>
                      </div>
                      <div class="power-image-item">
                        <h4>插座光功率图片</h4>
                        <div class="image-preview">
                          <img v-if="orderData.odbPowerMeterSnapshot" :src="orderData.odbPowerMeterSnapshot" alt="ODB Power Meter" />
                          <div v-else class="no-image">
                            <el-icon><Picture /></el-icon>
                            <span>暂无图片</span>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
                <div class="form-actions">
                  <el-button type="primary" @click="handlePowerSubmit" :loading="submitting">提交</el-button>
                  <el-button @click="resetPowerForm">重置</el-button>
                </div>
              </el-form>
            </div>
          </el-card>

          <!-- 合同号质检 -->
          <el-card class="box-card">
            <template #header>
              <div class="card-header">
                <h3>合同号质检</h3>
              </div>
            </template>
            <div class="form-container">
              <el-form :model="contractForm" label-position="left" label-width="200px" class="qc-form">
                <div class="form-main-content">
                  <div class="form-fields">
                    <el-form-item label="合同号质检是否通过">
                      <span class="value">
                        <el-tag :type="contractForm.qcPassed ? 'success' : 'warning'" class="status-tag">
                          {{ contractForm.qcPassed ? '已通过' : '未通过' }}
                        </el-tag>
                      </span>
                    </el-form-item>
                    <el-form-item label="AI识别合同号">
                      <span class="value">
                        {{ orderData.ocrContractId || '暂无数据' }}
                      </span>
                    </el-form-item>
                    <el-form-item label="工单数据合同号">
                      <span class="value">
                        {{ orderData.contractId || '暂无数据' }}
                      </span>
                    </el-form-item>
                    <el-form-item label="管理员输入——合同号">
                      <el-input v-model="contractForm.adminContractId" class="custom-input" />
                    </el-form-item>
                    <el-form-item label="AI识别SN码">
                      <span class="value">
                        {{ orderData.snCode || '暂无数据' }}
                      </span>
                    </el-form-item>
                    <el-form-item label="分包商手动输入SN码">
                      <span class="value">
                        {{ orderData.snCodeManual || '暂无数据' }}
                      </span>
                    </el-form-item>
                    <el-form-item label="管理员输入——SN编码">
                      <el-input v-model="contractForm.adminSn" class="custom-input" />
                    </el-form-item>
                  </div>
                  <div class="image-container">
                    <div class="contract-images">
                      <div class="contract-image-item">
                        <h4>合同号图片</h4>
                        <div class="image-preview">
                          <img v-if="orderData.ontSnapshot" :src="orderData.ontSnapshot" alt="Contract" />
                          <div v-else class="no-image">
                            <el-icon><Picture /></el-icon>
                            <span>暂无图片</span>
                          </div>
                        </div>
                      </div>
                      <div class="contract-image-item">
                        <h4>SN编码图片</h4>
                        <div class="image-preview">
                          <img v-if="orderData.snSnapshot" :src="orderData.snSnapshot" alt="SN Code" />
                          <div v-else class="no-image">
                            <el-icon><Picture /></el-icon>
                            <span>暂无图片</span>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
                <div class="form-actions">
                  <el-button type="primary" @click="handleContractSubmit" :loading="submitting">提交</el-button>
                  <el-button @click="resetContractForm">重置</el-button>
                </div>
              </el-form>
            </div>
          </el-card>

          <!-- 整体操作 -->
          <el-card class="box-card">
            <template #header>
              <div class="card-header">
                <h3>管理员操作</h3>
              </div>
            </template>
            <div class="form-container">
              <div class="form-actions overall-actions">
                <el-button type="success" size="large" @click="handleQualityStatusUpdate('Y')" :loading="submitting">
                  <el-icon><Check /></el-icon>质检人工通过
                </el-button>
                <el-button type="danger" size="large" @click="handleQualityStatusUpdate('N')" :loading="submitting">
                  <el-icon><Close /></el-icon>质检人工未通过
                </el-button>
                <el-button type="primary" size="large" @click="handleOverallSubmit" :loading="submitting">
                  <el-icon><Check /></el-icon>整体更新
                </el-button>
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
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from 'axios'
import {
  HomeFilled,
  Document,
  Monitor,
  Lightning,
  Cpu,
  CaretBottom,
  Picture,
  Check,
  Close,
  RefreshLeft
} from '@element-plus/icons-vue'
import request from '../utils/request'
import NavHeader from '../components/NavHeader.vue'

const route = useRoute()
const router = useRouter()
const jobNo = route.params.jobNo
const submitting = ref(false)
const orderData = ref({})

// 表单数据
const speedForm = ref({
  qcPassed: false,
  adminUploadSpeed: null,
  adminDownloadSpeed: null,
  ipDuplicate: false,
  refNoDuplicate: false
})

const powerForm = ref({
  qcPassed: false,
  adminFmOpticalPower: null,
  adminSocketOpticalPower: null
})

const contractForm = ref({
  qcPassed: false,
  adminContractId: '',
  adminSn: ''
})

const userInfo = ref({
  username: '',
  role: '',
  email: ''
})

const isLoggedIn = computed(() => {
  return localStorage.getItem('token') !== null
})

// 保存初始数据用于重置
const initialData = ref({
  speed: null,
  power: null,
  contract: null
})

// 获取工单详情
const fetchOrderDetails = async () => {
  try {
    const response = await axios.get(`http://localhost:8081/api/orders/${jobNo}`)
    orderData.value = response.data

    // 更新速度表单
    const speedData = {
      qcPassed: response.data.qualityStatus === 'Y',
      adminUploadSpeed: null,
      adminDownloadSpeed: null,
      ipDuplicate: response.data.speedTestIP === 'N',
      refNoDuplicate: response.data.speedTestRefNo === 'N'
    }
    speedForm.value = speedData
    
    // 更新功率表单
    const powerData = {
      qcPassed: response.data.opticalPowerStatus === 'Y',
      adminFmOpticalPower: null,
      adminSocketOpticalPower: null
    }
    powerForm.value = powerData
    
    // 更新合同表单
    const contractData = {
      qcPassed: response.data.contractStatus === 'Y',
      adminContractId: response.data.ocrContractId || '',
      adminSn: response.data.ontOldSn || ''
    }
    contractForm.value = contractData

    // 保存初始数据
    initialData.value = {
      speed: { ...speedData },
      power: { ...powerData },
      contract: { ...contractData }
    }
  } catch (error) {
    ElMessage.error('获取工单详情失败')
    console.error('Error fetching order details:', error)
  }
}

// 提交速度质检
const handleSpeedSubmit = async () => {
  if (submitting.value) return
  submitting.value = true
  
  try {
    const updatedData = {
      ...orderData.value,
      qualityStatus: speedForm.value.qcPassed ? 'Y' : 'N',
      uploadSpeedManual: speedForm.value.adminUploadSpeed,
      downloadSpeedManual: speedForm.value.adminDownloadSpeed
    }
    await axios.put(`http://localhost:8081/api/orders/${jobNo}`, updatedData)
    orderData.value = {
      ...orderData.value,
      uploadSpeedManual: speedForm.value.adminUploadSpeed,
      downloadSpeedManual: speedForm.value.adminDownloadSpeed
    }
    ElMessage.success('速度质检数据提交成功')
  } catch (error) {
    ElMessage.error('提交失败')
    console.error('Error submitting speed data:', error)
  } finally {
    submitting.value = false
  }
}

// 提交功率质检
const handlePowerSubmit = async () => {
  if (submitting.value) return
  submitting.value = true
  
  try {
    const updatedData = {
      ...orderData.value,
      opticalPowerStatus: powerForm.value.qcPassed ? 'Y' : 'N',
      fmOutputPowerManual: powerForm.value.adminFmOpticalPower,
      odbPowerMeterManual: powerForm.value.adminSocketOpticalPower
    }
    await axios.put(`http://localhost:8081/api/orders/${jobNo}`, updatedData)
    orderData.value = {
      ...orderData.value,
      fmOutputPowerManual: powerForm.value.adminFmOpticalPower,
      odbPowerMeterManual: powerForm.value.adminSocketOpticalPower
    }
    ElMessage.success('光功率质检数据提交成功')
  } catch (error) {
    ElMessage.error('提交失败')
    console.error('Error submitting power data:', error)
  } finally {
    submitting.value = false
  }
}

// 提交合同质检
const handleContractSubmit = async () => {
  if (submitting.value) return
  submitting.value = true
  
  try {
    const updatedData = {
      ...orderData.value,
      contractStatus: contractForm.value.qcPassed ? 'Y' : 'N',
      ocrContractId: contractForm.value.adminContractId,
      ontOldSn: contractForm.value.adminSn
    }
    await axios.put(`http://localhost:8081/api/orders/${jobNo}`, updatedData)
    orderData.value = {
      ...orderData.value,
      ocrContractId: contractForm.value.adminContractId,
      ontOldSn: contractForm.value.adminSn
    }
    ElMessage.success('合同质检数据提交成功')
  } catch (error) {
    ElMessage.error('提交失败')
    console.error('Error submitting contract data:', error)
  } finally {
    submitting.value = false
  }
}

// 重置表单
const resetSpeedForm = () => {
  speedForm.value = {
    qcPassed: false,
    adminUploadSpeed: null,
    adminDownloadSpeed: null,
    ipDuplicate: false,
    refNoDuplicate: false
  }
}

const resetPowerForm = () => {
  powerForm.value = {
    qcPassed: false,
    adminFmOpticalPower: null,
    adminSocketOpticalPower: null
  }
}

const resetContractForm = () => {
  contractForm.value = {
    qcPassed: false,
    adminContractId: '',
    adminSn: ''
  }
}

const formatDate = (date) => {
  if (!date) return '-'
  return new Date(date).toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

const handleLogout = () => {
  localStorage.removeItem('token')
  router.push('/login')
}

// 整体提交函数
const handleOverallSubmit = async () => {
  if (submitting.value) return
  
  try {
    await ElMessageBox.confirm(
      '确定要提交所有修改吗？',
      '提交确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )
    
    submitting.value = true
    const updatedData = {
      ...orderData.value,
      // 速度测试数据
      qualityStatus: speedForm.value.qcPassed ? 'Y' : 'N',
      uploadSpeedManual: speedForm.value.adminUploadSpeed,
      downloadSpeedManual: speedForm.value.adminDownloadSpeed,
      // 光功率数据
      opticalPowerStatus: powerForm.value.qcPassed ? 'Y' : 'N',
      fmOutputPowerManual: powerForm.value.adminFmOpticalPower,
      odbPowerMeterManual: powerForm.value.adminSocketOpticalPower,
      // 合同数据
      contractStatus: contractForm.value.qcPassed ? 'Y' : 'N'
    }

    await axios.put(`http://localhost:8081/api/orders/${jobNo}`, updatedData)
    ElMessage.success('所有数据提交成功')
    await fetchOrderDetails()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('提交失败')
      console.error('Error submitting all data:', error)
    }
  } finally {
    submitting.value = false
  }
}

// 整体重置函数
const handleOverallReset = async () => {
  try {
    await ElMessageBox.confirm(
      '确定要重置所有修改吗？这将恢复到页面加载时的初始状态。',
      '重置确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )
    
    // 恢复所有表单到初始状态
    speedForm.value = { ...initialData.value.speed }
    powerForm.value = { ...initialData.value.power }
    contractForm.value = { ...initialData.value.contract }
    
    ElMessage.success('已重置所有数据到初始状态')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('重置失败')
      console.error('Error resetting forms:', error)
    }
  }
}

// 处理质检状态更新
const handleQualityStatusUpdate = async (status) => {
  if (submitting.value) return
  
  // 检查状态是否有变化
  if (orderData.value.qualityStatus === status) {
    ElMessage.warning('质检状态无变化，无需更新')
    return
  }
  
  try {
    submitting.value = true
    const updatedData = {
      ...orderData.value,
      qualityStatus: status
    }

    await axios.put(`http://localhost:8081/api/orders/${jobNo}`, updatedData)
    
    // 更新本地数据
    orderData.value = {
      ...orderData.value,
      qualityStatus: status
    }
    
    ElMessage.success(`质检状态已更新为${status === 'Y' ? '人工通过' : '人工未通过'}`)
  } catch (error) {
    ElMessage.error('质检状态更新失败')
    console.error('Error updating quality status:', error)
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  fetchOrderDetails()
})
</script>

<style scoped>
.qc-detail {
  min-height: 100vh;
  background-color: #f5f7fa;
}

.el-main {
  padding-top: 80px;
  max-width: 1200px;
  margin: 0 auto;
}

.qc-detail-container {
  padding: 20px;
}

.box-card {
  margin-bottom: 20px;
  border-radius: 4px;
}

.card-header {
  padding: 15px 0;
}

.card-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #1f2f3d;
}

.overview-content {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
  padding: 20px;
}

.overview-content .overview-item:first-child {
  grid-column: 1 / -1;
}

.overview-item {
  display: flex;
  align-items: center;
}

.overview-item .label {
  min-width: 120px;
  color: #606266;
  font-weight: 500;
}

.overview-item .value {
  color: #606266;
  font-size: 14px;
  display: flex;
  align-items: center;
}

.overview-item .status-tag {
  margin-left: 0;
}

.form-container {
  padding: 20px;
}

.form-main-content {
  display: flex;
  flex-direction: column;
  gap: 30px;
}

.form-fields {
  flex: 1;
}

.form-fields :deep(.el-form-item) {
  margin-bottom: 20px;
}

.form-fields :deep(.el-form-item__label) {
  font-weight: 500;
  color: #606266;
}

.input-with-unit {
  display: flex;
  align-items: center;
  gap: 8px;
}

.unit {
  margin-left: 8px;
  color: #909399;
}

.value {
  color: #606266;
  font-size: 14px;
  display: flex;
  align-items: center;
}

.image-container {
  width: 100%;
}

.power-images,
.contract-images {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
}

.power-image-item,
.contract-image-item {
  text-align: center;
}

.power-image-item h4,
.contract-image-item h4 {
  margin: 0 0 10px;
  font-size: 14px;
  color: #606266;
}

.image-preview {
  width: 100%;
  height: 200px;
  border: 1px dashed #d9d9d9;
  border-radius: 4px;
  display: flex;
  justify-content: center;
  align-items: center;
  overflow: hidden;
  background: #fafafa;
}

.image-preview img {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
}

.no-image {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  color: #909399;
}

.no-image :deep(.el-icon) {
  font-size: 24px;
}

.form-actions {
  display: flex;
  justify-content: center;
  gap: 16px;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
}

:deep(.el-input-number) {
  width: 160px;
}

.custom-input {
  width: 300px;
}

.overall-actions {
  padding: 20px 0;
  display: flex;
  justify-content: center;
  gap: 16px;
}

.overall-actions .el-button {
  min-width: 120px;
}

.overall-actions .el-icon {
  margin-right: 8px;
}

@media (max-width: 768px) {
  .overview-content {
    grid-template-columns: 1fr;
  }

  .power-images,
  .contract-images {
    grid-template-columns: 1fr;
  }

  .custom-input {
    width: 100%;
  }
}

:deep(.el-tag) {
  border-radius: 4px;
  padding: 0 8px;
  height: 24px;
  line-height: 22px;
  font-size: 12px;
  border: none;
}

.status-tag {
  margin: 0;
}
</style> 