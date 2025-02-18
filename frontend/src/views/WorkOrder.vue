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
          <!-- 搜索和筛选区域 -->
          <el-card class="search-card">
            <el-form :inline="true" :model="searchForm" class="search-form">
              <el-form-item label="工单号">
                <el-input v-model="searchForm.orderNo" placeholder="请输入工单号" clearable />
              </el-form-item>
              <el-form-item label="状态">
                <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
                  <el-option label="待处理" value="pending" />
                  <el-option label="处理中" value="processing" />
                  <el-option label="已完成" value="completed" />
                </el-select>
              </el-form-item>
              <el-form-item label="日期范围">
                <el-date-picker
                  v-model="searchForm.dateRange"
                  type="daterange"
                  range-separator="至"
                  start-placeholder="开始日期"
                  end-placeholder="结束日期"
                  value-format="YYYY-MM-DD"
                />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="handleSearch">搜索</el-button>
                <el-button @click="resetSearch">重置</el-button>
              </el-form-item>
            </el-form>
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
              :cell-style="{ 'white-space': 'nowrap' }"
              :header-cell-style="{ 'white-space': 'nowrap' }"
            >
              <el-table-column prop="contractId" label="工单号" min-width="200" />
              <el-table-column prop="customerOrderId" label="客户号" min-width="180" />
              <el-table-column prop="jobNo" label="Job Number" min-width="200" />
              <el-table-column prop="uploadSpeed" label="上传速度" min-width="150">
                <template #default="scope">
                  {{ scope.row.uploadSpeed || scope.row.uploadSpeedManual || '-' }} Mbps
                </template>
              </el-table-column>
              <el-table-column prop="downloadSpeed" label="下载速度" min-width="150">
                <template #default="scope">
                  {{ scope.row.downloadSpeed || scope.row.downloadSpeedManual || '-' }} Mbps
                </template>
              </el-table-column>
              <el-table-column prop="speedTestRefNo" label="参考编号" min-width="200" />
              <el-table-column prop="speedTestIP" label="IP地址" min-width="160" />
              <el-table-column prop="fmOutputPower" label="楼层光功率" min-width="150">
                <template #default="scope">
                  {{ scope.row.fmOutputPower || scope.row.fmOutputPowerManual  || '-' }} dBm
                </template>
              </el-table-column>
              <el-table-column prop="createDate" label="工单时间" min-width="180">
                <template #default="scope">
                  {{ formatDate(scope.row.createDate) }}
                </template>
              </el-table-column>
              <el-table-column prop="qualityStatus" label="质检状态" min-width="120">
                <template #default="scope">
                  <el-tag :type="scope.row.qualityStatus === 'Y' ? 'success' : 'warning'">
                    {{ scope.row.qualityStatus === 'Y' ? '已通过' : '未通过' }}
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

// 搜索表单
const searchForm = ref({
  orderNo: '',
  status: '',
  dateRange: []
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
    let response;
    if (searchForm.value.orderNo || searchForm.value.status || searchForm.value.dateRange?.length) {
      // If search criteria exist, use the paginated search endpoint
      response = await request.get('http://localhost:8081/api/orders/last-month/page', {
        params: {
          page: currentPage.value - 1,
          size: pageSize.value,
          search: searchForm.value.orderNo || searchForm.value.status || searchForm.value.dateRange?.join(',')
        }
      })
      workOrders.value = response.data.content
      total.value = response.data.totalElements
    } else {
      // If no search criteria, fetch last year's orders
      response = await axios.get('http://localhost:8081/api/orders/last-year')
      if (Array.isArray(response.data)) {
        workOrders.value = response.data
        total.value = response.data.length
      } else {
        ElMessage.error('获取工单列表数据格式错误')
        console.error('Invalid response format:', response.data)
      }
    }
  } catch (error) {
    ElMessage.error('获取工单列表失败')
    console.error('Error fetching work orders:', error)
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
    orderNo: '',
    status: '',
    dateRange: []
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

onMounted(() => {
  fetchWorkOrders()
})
</script>

<style scoped>
.work-order {
  min-height: 100vh;
  background-color: #f8f9fa;
}

.header {
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
  position: fixed;
  width: 100%;
  z-index: 100;
  padding: 0;
  height: 60px;
}

.header-content {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  max-width: 1400px;
  margin: 0 auto;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 32px;
  flex: 1;
}

.logo {
  display: flex;
  align-items: center;
  min-width: 200px;
}

.logo h2 {
  margin: 0;
  font-size: 16px;
  font-weight: 500;
  color: #1d2129;
  white-space: nowrap;
}

.nav-logo-image {
  height: 28px;
  width: auto;
  margin-right: 12px;
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
  gap: 6px;
  height: 60px;
  line-height: 60px;
  border-bottom: none;
  padding: 0 20px;
  font-size: 14px;
  color: #4e5969;
}

:deep(.el-menu-item.is-active) {
  color: #165dff;
  background: transparent;
  border-bottom: 2px solid #165dff;
  font-weight: 500;
}

:deep(.el-menu-item:hover) {
  color: #165dff;
  background: rgba(22, 93, 255, 0.05);
}

.user-dropdown {
  cursor: pointer;
  color: #1d2129;
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
}

.el-main {
  padding-top: 80px;
  min-height: calc(100vh - 60px);
}

.work-order-content {
  max-width: 1800px;
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
  display: flex;
  flex-wrap: wrap;
  gap: 24px;
  align-items: flex-start;
}

:deep(.el-form-item) {
  margin-bottom: 0;
}

:deep(.el-form-item__label) {
  color: #4e5969;
  font-weight: normal;
  font-size: 14px;
}

:deep(.el-input__inner) {
  border-radius: 6px;
  border-color: #e5e6eb;
}

:deep(.el-input__inner:hover) {
  border-color: #c9cdd4;
}

:deep(.el-input__inner:focus) {
  border-color: #165dff;
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
}

:deep(.el-table__body-wrapper) {
  overflow-x: auto;
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

.header-right {
  display: flex;
  gap: 24px;
  align-items: center;
}
</style> 