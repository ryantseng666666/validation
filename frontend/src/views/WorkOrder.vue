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
            
            <el-table :data="workOrders" style="width: 100%" v-loading="loading">
              <el-table-column prop="orderNo" label="工单号" width="180" />
              <el-table-column prop="type" label="工单类型" width="120" />
              <el-table-column prop="status" label="状态" width="100">
                <template #default="scope">
                  <el-tag :type="getStatusType(scope.row.status)">
                    {{ getStatusText(scope.row.status) }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="createTime" label="创建时间" width="180" />
              <el-table-column prop="address" label="地址" />
              <el-table-column label="操作" width="200" fixed="right">
                <template #default="scope">
                  <el-button link type="primary" @click="handleView(scope.row)">查看</el-button>
                  <el-button link type="primary" @click="handleEdit(scope.row)">编辑</el-button>
                  <el-button link type="danger" @click="handleDelete(scope.row)">删除</el-button>
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
  CaretBottom
} from '@element-plus/icons-vue'
import request from '../utils/request'

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
const workOrders = ref([
  {
    orderNo: 'WO20240101001',
    type: '安装',
    status: 'pending',
    createTime: '2024-01-01 10:00:00',
    address: '广东省深圳市南山区科技园',
    remark: '新装宽带'
  },
  {
    orderNo: 'WO20240101002',
    type: '维修',
    status: 'processing',
    createTime: '2024-01-01 11:00:00',
    address: '广东省深圳市福田区CBD',
    remark: '网络故障维修'
  },
  {
    orderNo: 'WO20240101003',
    type: '升级',
    status: 'completed',
    createTime: '2024-01-01 14:00:00',
    address: '广东省深圳市龙岗区横岗街道',
    remark: '百兆升级千兆'
  }
])
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
    // 模拟API延迟
    await new Promise(resolve => setTimeout(resolve, 500))
    
    // 根据搜索条件过滤数据
    const filteredOrders = workOrders.value.filter(order => {
      const matchOrderNo = !searchForm.value.orderNo || 
        order.orderNo.toLowerCase().includes(searchForm.value.orderNo.toLowerCase())
      
      const matchStatus = !searchForm.value.status || 
        order.status === searchForm.value.status
      
      let matchDate = true
      if (searchForm.value.dateRange?.length === 2) {
        const orderDate = new Date(order.createTime.split(' ')[0])
        const startDate = new Date(searchForm.value.dateRange[0])
        const endDate = new Date(searchForm.value.dateRange[1])
        matchDate = orderDate >= startDate && orderDate <= endDate
      }
      
      return matchOrderNo && matchStatus && matchDate
    })
    
    // 更新总数
    total.value = filteredOrders.length
    
    // 模拟分页
    const start = (currentPage.value - 1) * pageSize.value
    const end = start + pageSize.value
    workOrders.value = filteredOrders.slice(start, end)
    
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
  handleSearch()
}

// 状态标签类型
const getStatusType = (status) => {
  const types = {
    pending: 'warning',
    processing: 'primary',
    completed: 'success'
  }
  return types[status] || 'info'
}

// 状态文本
const getStatusText = (status) => {
  const texts = {
    pending: '待处理',
    processing: '处理中',
    completed: '已完成'
  }
  return texts[status] || status
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
const handleView = (row) => {
  dialogType.value = 'view'
  orderForm.value = { ...row }
  dialogVisible.value = true
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

onMounted(() => {
  fetchWorkOrders()
})
</script>

<style scoped>
.work-order {
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

.work-order-content {
  padding: 24px;
  max-width: 1200px;
  margin: 0 auto;
}

.work-order-card {
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

.work-order-placeholder {
  padding: 40px;
  text-align: center;
}

.header-right {
  display: flex;
  gap: 16px;
  align-items: center;
}

.search-card {
  margin-bottom: 24px;
}

.search-form {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
}

.pagination-container {
  margin-top: 24px;
  display: flex;
  justify-content: flex-end;
}

:deep(.el-tag) {
  text-align: center;
  min-width: 80px;
}
</style> 