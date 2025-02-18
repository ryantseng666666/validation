<template>
  <div class="qc-detail">
    <el-container>
      <!-- 顶部导航栏 -->
      <el-header class="header">
        <div class="header-content">
          <div class="header-left">
            <div class="logo">
              <img src="../assets/cmhk.png" alt="CMHK Logo" class="nav-logo-image">
              <span class="logo-text">装维质检系统</span>
            </div>
          </div>
          <div class="nav-menu">
            <el-menu
              mode="horizontal"
              :router="true"
              class="main-menu"
              :default-active="$route.path">
              <el-menu-item index="/">
                <el-icon><HomeFilled /></el-icon>
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
              <span class="user-info">
                admin
                <el-icon><CaretBottom /></el-icon>
              </span>
            </template>
          </div>
        </div>
      </el-header>

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
                      <el-switch v-model="speedForm.qcPassed" />
                    </el-form-item>
                    <el-form-item label="上传速度">
                      <div class="input-with-unit">
                        <el-input-number v-model="speedForm.uploadSpeed" :min="0" :precision="2" :step="0.1" />
                        <span class="unit">MB</span>
                      </div>
                    </el-form-item>
                    <el-form-item label="下载速度">
                      <div class="input-with-unit">
                        <el-input-number v-model="speedForm.downloadSpeed" :min="0" :precision="2" :step="0.1" />
                        <span class="unit">MB</span>
                      </div>
                    </el-form-item>
                    <el-form-item label="IP是否重复">
                      <el-switch v-model="speedForm.ipDuplicate" />
                    </el-form-item>
                    <el-form-item label="参考编号是否重复">
                      <el-switch v-model="speedForm.refNoDuplicate" />
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
                      <el-switch v-model="powerForm.qcPassed" />
                    </el-form-item>
                    <el-form-item label="FM光功率">
                      <div class="input-with-unit">
                        <el-input-number v-model="powerForm.fmPower" :precision="2" :step="0.01" />
                        <span class="unit">dBm</span>
                      </div>
                    </el-form-item>
                    <el-form-item label="插座光功率">
                      <div class="input-with-unit">
                        <el-input-number v-model="powerForm.outletPower" :precision="2" :step="0.01" />
                        <span class="unit">dBm</span>
                      </div>
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
                      <el-switch v-model="contractForm.qcPassed" />
                    </el-form-item>
                    <el-form-item label="合同号">
                      <el-input v-model="contractForm.contractNo" class="custom-input" />
                    </el-form-item>
                    <el-form-item label="旧SN编码">
                      <el-input v-model="contractForm.oldSNCode" class="custom-input" />
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
                <h3>整体操作</h3>
              </div>
            </template>
            <div class="form-container">
              <div class="form-actions overall-actions">
                <el-button type="primary" size="large" @click="handleOverallSubmit" :loading="submitting">
                  <el-icon><Check /></el-icon>整体提交
                </el-button>
                <el-button type="warning" size="large" @click="handleOverallReset">
                  <el-icon><RefreshLeft /></el-icon>全部重置
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
  RefreshLeft
} from '@element-plus/icons-vue'
import request from '../utils/request'

const route = useRoute()
const router = useRouter()
const jobNo = route.params.jobNo
const submitting = ref(false)
const orderData = ref({})

// 表单数据
const speedForm = ref({
  qcPassed: false,
  uploadSpeed: null,
  downloadSpeed: null,
  ipDuplicate: false,
  refNoDuplicate: false
})

const powerForm = ref({
  qcPassed: false,
  fmPower: null,
  outletPower: null
})

const contractForm = ref({
  qcPassed: false,
  contractNo: '',
  oldSNCode: '',
  newSNCode: ''
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
      qcPassed: response.data.quality_status === 'Y',
      uploadSpeed: Number(response.data.uploadSpeedManual) || Number(response.data.uploadSpeed) || null,
      downloadSpeed: Number(response.data.downloadSpeedManual) || Number(response.data.downloadSpeed) || null,
      ipDuplicate: response.data.speedTestIP === 'N',
      refNoDuplicate: response.data.speedTestRefNo === 'N'
    }
    speedForm.value = speedData
    
    // 更新功率表单
    const powerData = {
      qcPassed: response.data.optical_power_status === 'Y',
      fmPower: Number(response.data.fmOutputPowerManual) || Number(response.data.fmOutputPower) || null,
      outletPower: Number(response.data.odbPowerMeterManual) || Number(response.data.odbPowerMeter) || null
    }
    powerForm.value = powerData
    
    // 更新合同表单
    const contractData = {
      qcPassed: response.data.contract_status === 'Y',
      contractNo: response.data.ocrContractId || '',
      oldSNCode: response.data.ontOldSn || '',
      newSNCode: response.data.ontNewSn || ''
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
      quality_status: speedForm.value.qcPassed ? 'Y' : 'N',
      uploadSpeedManual: speedForm.value.uploadSpeed,
      downloadSpeedManual: speedForm.value.downloadSpeed
    }
    await axios.put(`http://localhost:8081/api/orders/${jobNo}`, updatedData)
    ElMessage.success('速度质检数据提交成功')
    fetchOrderDetails()
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
      optical_power_status: powerForm.value.qcPassed ? 'Y' : 'N',
      fmOutputPowerManual: powerForm.value.fmPower,
      odbPowerMeterManual: powerForm.value.outletPower
    }
    await axios.put(`http://localhost:8081/api/orders/${jobNo}`, updatedData)
    ElMessage.success('光功率质检数据提交成功')
    fetchOrderDetails()
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
      contract_status: contractForm.value.qcPassed ? 'Y' : 'N'
    }
    await axios.put(`http://localhost:8081/api/orders/${jobNo}`, updatedData)
    ElMessage.success('合同质检数据提交成功')
    fetchOrderDetails()
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
    uploadSpeed: null,
    downloadSpeed: null,
    ipDuplicate: false,
    refNoDuplicate: false
  }
}

const resetPowerForm = () => {
  powerForm.value = {
    qcPassed: false,
    fmPower: null,
    outletPower: null
  }
}

const resetContractForm = () => {
  contractForm.value = {
    qcPassed: false,
    contractNo: orderData.value.ocrContractId || '',
    oldSNCode: orderData.value.ontOldSn || '',
    newSNCode: orderData.value.ontNewSn || ''
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
      quality_status: speedForm.value.qcPassed ? 'Y' : 'N',
      uploadSpeedManual: speedForm.value.uploadSpeed,
      downloadSpeedManual: speedForm.value.downloadSpeed,
      // 光功率数据
      optical_power_status: powerForm.value.qcPassed ? 'Y' : 'N',
      fmOutputPowerManual: powerForm.value.fmPower,
      odbPowerMeterManual: powerForm.value.outletPower,
      // 合同数据
      contract_status: contractForm.value.qcPassed ? 'Y' : 'N'
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

onMounted(() => {
  fetchOrderDetails()
})
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
  padding: 0 20px;
  max-width: 1400px;
  margin: 0 auto;
}

.header-left {
  display: flex;
  align-items: center;
  margin-right: 40px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 8px;
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

.nav-menu {
  flex: 1;
}

.main-menu {
  border-bottom: none;
  background: transparent;
}

.main-menu :deep(.el-menu-item) {
  height: 60px;
  line-height: 60px;
  padding: 0 16px;
}

.main-menu :deep(.el-menu-item .el-icon) {
  margin-right: 4px;
  font-size: 18px;
}

.main-menu :deep(.el-menu-item span) {
  font-size: 14px;
}

.header-right {
  margin-left: auto;
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 4px;
  cursor: pointer;
  font-size: 14px;
  color: #606266;
}

.user-info .el-icon {
  font-size: 12px;
}

.qc-detail-container {
  padding-top: 80px;
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
  background-color: #f5f7fa;
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
  color: #303133;
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
  color: #909399;
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
}

.overall-actions .el-button {
  min-width: 120px;
  margin: 0 20px;
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
</style> 