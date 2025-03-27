<template>
  <div class="work-order">
    <el-container>
      <NavHeader />
      <el-main>
        <div class="work-order-content">
          <!-- 搜索和筛选区域 -->
          <el-card class="search-card">
            <el-form :inline="true" :model="searchForm" class="search-form">
              <div class="search-form-items">
                <el-form-item label="工单号" class="form-item-fixed">
                  <el-input v-model="searchForm.jobNo" placeholder="请输入工单号" clearable />
                </el-form-item>
                <el-form-item label="客户号" class="form-item-fixed">
                  <el-input v-model="searchForm.customerOrderId" placeholder="请输入客户号" clearable />
                </el-form-item>
                <el-form-item label="AI处理状态" class="form-item-fixed">
                  <el-select v-model="searchForm.autoSuccess" placeholder="请选择状态" clearable>
                    <el-option label="成功" :value="1" />
                    <el-option label="失败" :value="0" />
                    <el-option label="异常" :value="-1" />
                  </el-select>
                </el-form-item>
                <el-form-item label="日期范围" class="form-item-fixed">
                  <el-date-picker
                    v-model="searchForm.dateRange"
                    type="daterange"
                    range-separator="至"
                    start-placeholder="开始日期"
                    end-placeholder="结束日期"
                    value-format="YYYY-MM-DD HH:mm:ss"
                  />
                </el-form-item>
              </div>
              <div class="search-buttons">
                <el-form-item class="form-item-fixed">
                  <el-button type="primary" @click="handleSearch">搜索</el-button>
                  <el-button @click="resetSearch">重置</el-button>
                </el-form-item>
              </div>
            </el-form>
            
            <!-- AI处理按钮区域 -->
            <div class="ai-process-container">
              <el-form-item label="处理月份" class="month-picker">
                <el-date-picker
                  v-model="currentMonth"
                  type="month"
                  format="YYYY-MM"
                  value-format="YYYY-MM"
                  placeholder="选择月份"
                />
              </el-form-item>
              <el-button 
                type="primary" 
                @click="handleAIProcess"
                :loading="aiProcessing"
                class="ai-process-button">
                AI质检数据处理
              </el-button>
            </div>
          </el-card>

          <!-- 工单列表 -->
          <el-card class="work-order-card">
            <template #header>
              <div class="card-header">
                <h3>工单列表</h3>
                <el-button type="primary" @click="handleCreateOrder">新建工单</el-button>
              </div>
            </template>
            
            <el-table 
              :data="workOrders" 
              style="width: 100%" 
              v-loading="loading"
              :cell-style="{ 
                'white-space': 'nowrap',
                'text-align': 'center',
                'vertical-align': 'middle'
              }"
              :header-cell-style="{ 
                'white-space': 'nowrap',
                'text-align': 'center',
                'background-color': '#F5F7FA',
                'color': '#303133',
                'font-weight': 'bold',
                'vertical-align': 'middle'
              }"
            >
              <el-table-column prop="jobNo" label="工单号" min-width="150" />
              <el-table-column prop="contractId" label="合同号" min-width="150" />
              <el-table-column prop="customerOrderId" label="客户号" min-width="180" />
              <el-table-column prop="orderType" label="工单类型" min-width="120" />
              <el-table-column prop="uploadSpeed" label="上传速度" min-width="150">
                <template #default="scope">
                  {{ scope.row.uploadSpeed || scope.row.uploadSpeedManual || '-' }}
                  {{ scope.row.uploadSpeed || scope.row.uploadSpeedManual ? 'Mbps' : '' }}
                </template>
              </el-table-column>
              <el-table-column prop="downloadSpeed" label="下载速度" min-width="150">
                <template #default="scope">
                  {{ scope.row.downloadSpeed || scope.row.downloadSpeedManual || '-' }}
                  {{ scope.row.downloadSpeed || scope.row.downloadSpeedManual ? 'Mbps' : '' }}
                </template>
              </el-table-column>
              <el-table-column prop="fmOutputPower" label="楼层光功率" min-width="150">
                <template #default="scope">
                  {{ (scope.row.fmOutputPower != null ? scope.row.fmOutputPower : null) || 
                     (scope.row.fmOutputPowerManual != null ? scope.row.fmOutputPowerManual : null) || 
                     '-' }}
                  {{ scope.row.fmOutputPower || scope.row.fmOutputPowerManual ? 'dBm' : '' }}
                </template>
              </el-table-column>
              <el-table-column prop="createDate" label="工单时间" min-width="180">
                <template #default="scope">
                  {{ formatDate(scope.row.createDate) }}
                </template>
              </el-table-column>
              <el-table-column prop="autoSuccess" label="AI处理状态" min-width="120">
                <template #default="scope">
                  <el-tag :type="getAutoSuccessTagType(scope.row.autoSuccess)">
                    {{ getAutoSuccessText(scope.row.autoSuccess) }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="qualityStatus" label="质检状态" min-width="120">
                <template #default="scope">
                  <el-tag :type="scope.row.qualityStatus === 'autoSuccess' ? 'success' : 'warning'">
                    {{ scope.row.qualityStatus === 'autoSuccess' ? '已通过' : '未通过' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column fixed="right" label="操作" min-width="100">
                <template #default="scope">
                  <el-button
                    link
                    type="primary"
                    @click="handleViewDetail(scope.row.jobNo)"
                  >
                    详情
                  </el-button>
                </template>
              </el-table-column>
            </el-table>

            <div class="pagination-container">
              <el-pagination
                v-model:current-page="currentPage"
                v-model:page-size="pageSize"
                :page-sizes="[10, 20, 50, 100]"
                :total="total"
                layout="total, sizes, prev, pager, next, jumper"
                @size-change="handleSizeChange"
                @current-change="handleCurrentChange"
              />
            </div>
          </el-card>
        </div>
      </el-main>
    </el-container>

    <!-- 工单详情对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="60%"
      destroy-on-close
    >
      <el-form
        ref="orderForm"
        :model="orderForm"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="工单号" prop="orderNo">
          <el-input v-model="orderForm.orderNo" :disabled="dialogType === 'view'" />
        </el-form-item>
        <el-form-item label="工单类型" prop="type">
          <el-select v-model="orderForm.type" placeholder="请选择工单类型" :disabled="dialogType === 'view'">
            <el-option label="安装" value="installation" />
            <el-option label="维修" value="repair" />
            <el-option label="升级" value="upgrade" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="orderForm.status" placeholder="请选择状态" :disabled="dialogType === 'view'">
            <el-option label="待处理" value="pending" />
            <el-option label="处理中" value="processing" />
            <el-option label="已完成" value="completed" />
          </el-select>
        </el-form-item>
        <el-form-item label="地址" prop="address">
          <el-input v-model="orderForm.address" :disabled="dialogType === 'view'" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="orderForm.remark"
            type="textarea"
            :rows="3"
            :disabled="dialogType === 'view'"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit" v-if="dialogType !== 'view'">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  House,
  Document,
  Monitor,
  Lightning,
  Cpu,
  CaretBottom,
  Search
} from '@element-plus/icons-vue'
import request from '../utils/request'
import axios from 'axios'
import NavHeader from '../components/NavHeader.vue'

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

// 搜索表单数据
const searchForm = ref({
  jobNo: '',
  customerOrderId: '',
  dateRange: [],
  autoSuccess: ''
})

// 分页相关
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 表格数据
const workOrders = ref([])
const loading = ref(false)

// 对话框相关
const dialogVisible = ref(false)
const dialogType = ref('') // 'create', 'edit', 'view'
const dialogTitle = computed(() => {
  const titles = {
    create: '新建工单',
    edit: '编辑工单',
    view: '工单详情'
  }
  return titles[dialogType.value] || ''
})

// 表单数据
const orderForm = ref({
  orderNo: '',
  type: '',
  status: '',
  address: '',
  remark: ''
})

// 表单校验规则
const rules = {
  orderNo: [{ required: true, message: '请输入工单号', trigger: 'blur' }],
  type: [{ required: true, message: '请选择工单类型', trigger: 'change' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }],
  address: [{ required: true, message: '请输入地址', trigger: 'blur' }]
}

// 获取工单列表
const fetchWorkOrders = async () => {
  loading.value = true
  try {
    const params = {
      jobNo: searchForm.value.jobNo || '',
      customerOrderId: searchForm.value.customerOrderId || '',
      startDate: searchForm.value.dateRange?.[0] || '',
      endDate: searchForm.value.dateRange?.[1] || '',
      autoSuccess: searchForm.value.autoSuccess || '',
      page: currentPage.value - 1,
      size: pageSize.value
    }
    
    console.log('Fetching orders with params:', params) // 添加调试日志
    const response = await request.get('http://localhost:8081/api/orders/search', { params })
    

    // 检查响应数据结构
    if (response) {
      workOrders.value = response.content || []
      total.value = response.totalElements || 0
      console.log('Fetched orders:', workOrders.value) // 添加调试日志
      console.log('Total elements:', total.value) // 添加调试日志
    } else {
      workOrders.value = []
      total.value = 0
      ElMessage.warning('未获取到数据')
    }
  } catch (error) {
    console.error('获取数据失败：', error)
    workOrders.value = []
    total.value = 0
    ElMessage.error(error.response?.data?.message || '获取数据失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  fetchWorkOrders()
}

// 重置搜索
const resetSearch = () => {
  searchForm.value = {
    jobNo: '',
    customerOrderId: '',
    dateRange: [],
    autoSuccess: ''
  }
  currentPage.value = 1
  fetchWorkOrders()
}

// 新建工单
const handleCreateOrder = () => {
  dialogType.value = 'create'
  orderForm.value = {
    orderNo: '',
    type: '',
    status: 'pending',
    address: '',
    remark: ''
  }
  dialogVisible.value = true
}

// 查看工单
const handleViewDetail = (jobNo) => {
  router.push(`/qc-detail/${jobNo}`)
}

// 编辑工单
const handleEdit = (row) => {
  dialogType.value = 'edit'
  orderForm.value = { ...row }
  dialogVisible.value = true
}

// 删除工单
const handleDelete = (row) => {
  ElMessageBox.confirm('确认删除该工单吗？', '提示', {
    type: 'warning'
  }).then(() => {
    const index = workOrders.value.findIndex(item => item.orderNo === row.orderNo)
    if (index !== -1) {
      workOrders.value.splice(index, 1)
      total.value--
      ElMessage.success('删除成功')
      fetchWorkOrders()
    }
  })
}

// 提交表单
const handleSubmit = async () => {
  try {
    if (dialogType.value === 'create') {
      // 模拟创建工单
      const newOrder = {
        ...orderForm.value,
        createTime: new Date().toLocaleString()
      }
      workOrders.value.unshift(newOrder)
      total.value++
      ElMessage.success('创建成功')
    } else if (dialogType.value === 'edit') {
      // 模拟编辑工单
      const index = workOrders.value.findIndex(item => item.orderNo === orderForm.value.orderNo)
      if (index !== -1) {
        workOrders.value[index] = { ...orderForm.value }
      }
      ElMessage.success('更新成功')
    }
    dialogVisible.value = false
    fetchWorkOrders()
  } catch (error) {
    ElMessage.error(dialogType.value === 'create' ? '创建失败' : '更新失败')
  }
}

// 分页大小改变
const handleSizeChange = (val) => {
  pageSize.value = val
  fetchWorkOrders()
}

// 页码改变
const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchWorkOrders()
}

const formatDate = (date) => {
  if (!date) return '-'
  return new Date(date).toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// AI处理
const aiProcessing = ref(false)
const currentMonth = ref(new Date().toISOString().slice(0, 7)) // 默认当前月份

const handleAIProcess = async () => {
  if (!currentMonth.value) {
    ElMessage.warning('请选择要处理的月份')
    return
  }
  
  aiProcessing.value = true
  try {
    const response = await axios.post('http://localhost:8081/api/orders/process-monthly-ai', null, {
      params: { monthDate: currentMonth.value }
    })
    if (response.data === 'success') {
      ElMessage.success('处理成功')
      fetchWorkOrders() // 刷新数据
    } else {
      ElMessage.warning('处理失败：' + response.data)
    }
  } catch (error) {
    ElMessage.error('处理失败：' + error.message)
  } finally {
    aiProcessing.value = false
  }
}

const getAutoSuccessTagType = (status) => {
  switch (status) {
    case 1:
      return 'success'
    case 0:
      return 'warning'
    case -1:
      return 'danger'
    default:
      return 'info'
  }
}

const getAutoSuccessText = (status) => {
  switch (status) {
    case 1:
      return '成功'
    case 0:
      return '失败'
    case -1:
      return '异常'
    default:
      return '未处理'
  }
}

onMounted(() => {
  fetchWorkOrders()
})
</script>

<style scoped>
.work-order {
  min-height: 100vh;
  background-color: #f5f7fa;
}

.el-main {
  padding-top: 80px;
  max-width: 1800px;
  margin: 0 auto;
}

/* Remove header related styles */
.header,
.header-content,
.header-left,
.logo,
.nav-logo-image,
.logo-text,
.nav-menu,
.main-menu,
.header-right,
.user-info {
  /* These styles are now handled by NavHeader component */
}

/* Keep other existing styles */
.work-order-content {
  max-width: 100%;
  margin: 0 auto;
  padding: 20px;
}

.search-card {
  margin-bottom: 24px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  border: none;
}

:deep(.el-card__body) {
  padding: 24px;
}

.search-form {
  width: 100%;
}

.search-form-items {
  display: flex;
  flex-wrap: nowrap;
  align-items: center;
  gap: 16px;
  margin-bottom: 16px;
}

.search-buttons {
  display: flex;
  justify-content: center;
  width: 100%;
  padding-top: 16px;
  border-top: 1px solid #ebeef5;
}

.form-item-fixed {
  flex-shrink: 0;
  margin-bottom: 0;
  margin-right: 0;
}

:deep(.form-item-fixed .el-input) {
  width: 200px;
}

:deep(.form-item-fixed .el-select) {
  width: 200px;
}

:deep(.form-item-fixed .el-date-editor) {
  width: 320px;
}

:deep(.search-buttons .el-form-item__content) {
  margin-left: 0 !important;
  display: flex;
  gap: 8px;
  justify-content: center;
}

:deep(.el-input__wrapper),
:deep(.el-select__wrapper) {
  box-shadow: none !important;
}

:deep(.el-input__wrapper:hover),
:deep(.el-select__wrapper:hover) {
  box-shadow: 0 0 0 1px #c9cdd4 inset !important;
}

:deep(.el-input__wrapper.is-focus),
:deep(.el-select__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #165dff inset !important;
}

.work-order-card {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  border: none;
  margin-top: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  border-bottom: 1px solid #f0f0f0;
}

.card-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 500;
  color: #1d2129;
}

/* 表格样式 */
:deep(.el-table) {
  width: 100% !important;
  overflow-x: auto;
}

:deep(.el-table__body),
:deep(.el-table__header) {
  min-width: 100%;
}

:deep(.el-table .cell) {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  padding: 12px 0;
}

:deep(.el-table__header-wrapper) {
  th {
    background-color: #F5F7FA !important;
    font-weight: bold;
  }
}

:deep(.el-table__body-wrapper) {
  overflow-x: auto;
}

:deep(.el-table__cell) {
  padding: 8px 0;
}

:deep(.el-table .el-tag) {
  display: inline-flex;
  justify-content: center;
  align-items: center;
  margin: 0 auto;
}

:deep(.el-button--text) {
  color: #165dff;
  padding: 0;
}

:deep(.el-button--text:hover) {
  color: #4080ff;
}

.pagination-container {
  margin-top: 24px;
  padding: 0 24px 24px;
  display: flex;
  justify-content: flex-end;
}

:deep(.el-pagination) {
  font-weight: normal;
}

:deep(.el-pagination .el-select .el-input) {
  width: 120px;
}

:deep(.el-pagination__total) {
  color: #86909c;
}

:deep(.el-pagination .btn-prev),
:deep(.el-pagination .btn-next) {
  background: #fff;
  border: 1px solid #e5e6eb;
  border-radius: 6px;
}

:deep(.el-pagination .el-pager li) {
  background: #fff;
  border: 1px solid #e5e6eb;
  border-radius: 6px;
  font-weight: normal;
}

:deep(.el-pagination .el-pager li.active) {
  background: #165dff;
  color: #fff;
  border-color: #165dff;
  font-weight: 500;
}

:deep(.el-tag) {
  border-radius: 4px;
  padding: 0 8px;
  height: 24px;
  line-height: 22px;
  font-size: 12px;
  border: none;
}

:deep(.el-tag--success) {
  background: rgba(0, 180, 42, 0.1);
  color: #00b42a;
}

:deep(.el-tag--warning) {
  background: rgba(255, 125, 0, 0.1);
  color: #ff7d00;
}

.ai-process-container {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
}

.month-picker {
  margin-right: 20px;
  margin-bottom: 0;
}

.ai-process-button {
  min-width: 140px;
}

:deep(.el-form-item.month-picker .el-form-item__label) {
  font-size: 14px;
  color: #606266;
}

:deep(.el-date-editor.el-input) {
  width: 200px;
}
</style> 