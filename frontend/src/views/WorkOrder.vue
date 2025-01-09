<template>
  <div class="work-order">
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
        <div class="work-order-content">
          <!-- 搜索和过滤区域 -->
          <div class="search-section">
            <el-form :inline="true" :model="searchForm" class="search-form">
              <el-form-item label="工单号">
                <el-input v-model="searchForm.orderNo" placeholder="请输入工单号" />
              </el-form-item>
              <el-form-item label="状态">
                <el-select v-model="searchForm.status" placeholder="请选择状态">
                  <el-option label="全部" value="" />
                  <el-option label="待处理" value="pending" />
                  <el-option label="处理中" value="processing" />
                  <el-option label="已完成" value="completed" />
                </el-select>
              </el-form-item>
              <el-form-item label="日期">
                <el-date-picker
                  v-model="searchForm.dateRange"
                  type="daterange"
                  range-separator="至"
                  start-placeholder="开始日期"
                  end-placeholder="结束日期"
                />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="handleSearch">搜索</el-button>
                <el-button @click="resetSearch">重置</el-button>
              </el-form-item>
            </el-form>
          </div>

          <!-- 工单列表 -->
          <div class="table-section">
            <div class="table-header">
              <h2>工单列表</h2>
              <el-button type="primary" @click="createOrder">新建工单</el-button>
            </div>
            <el-table :data="orderList" style="width: 100%" v-loading="loading">
              <el-table-column prop="orderNo" label="工单号" width="180" />
              <el-table-column prop="type" label="类型" width="120">
                <template #default="scope">
                  <el-tag :type="getTypeTag(scope.row.type)">{{ scope.row.type }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="status" label="状态" width="120">
                <template #default="scope">
                  <el-tag :type="getStatusTag(scope.row.status)">{{ scope.row.status }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="createTime" label="创建时间" width="180" />
              <el-table-column prop="address" label="地址" />
              <el-table-column label="操作" width="200" fixed="right">
                <template #default="scope">
                  <el-button link type="primary" @click="viewDetail(scope.row)">查看</el-button>
                  <el-button link type="primary" @click="editOrder(scope.row)">编辑</el-button>
                  <el-button link type="danger" @click="deleteOrder(scope.row)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>

            <!-- 分页 -->
            <div class="pagination">
              <el-pagination
                v-model:current-page="currentPage"
                v-model:page-size="pageSize"
                :page-sizes="[10, 20, 50, 100]"
                layout="total, sizes, prev, pager, next, jumper"
                :total="total"
                @size-change="handleSizeChange"
                @current-change="handleCurrentChange"
              />
            </div>
          </div>
        </div>
      </el-main>
    </el-container>
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

const router = useRouter()
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const userInfo = ref({
  username: '',
  role: '',
  email: ''
})

const isLoggedIn = computed(() => {
  return localStorage.getItem('token') !== null
})

const searchForm = ref({
  orderNo: '',
  status: '',
  dateRange: []
})

const orderList = ref([
  {
    orderNo: 'WO2024010001',
    type: '速度测试',
    status: '待处理',
    createTime: '2024-01-09 10:00:00',
    address: '广东省深圳市南山区科技园'
  },
  {
    orderNo: 'WO2024010002',
    type: '光功率',
    status: '处理中',
    createTime: '2024-01-09 11:30:00',
    address: '广东省深圳市福田区CBD'
  },
  {
    orderNo: 'WO2024010003',
    type: 'SN识别',
    status: '已完成',
    createTime: '2024-01-09 14:20:00',
    address: '广东省深圳市宝安区新安街道'
  }
])

onMounted(() => {
  const userStr = localStorage.getItem('user')
  if (userStr) {
    userInfo.value = JSON.parse(userStr)
  }
  fetchOrderList()
})

const handleLogout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('user')
  ElMessage.success('已退出登录')
  router.push('/login')
}

const getTypeTag = (type) => {
  const typeMap = {
    '速度测试': 'primary',
    '光功率': 'success',
    'SN识别': 'warning'
  }
  return typeMap[type] || 'info'
}

const getStatusTag = (status) => {
  const statusMap = {
    '待处理': 'info',
    '处理中': 'warning',
    '已完成': 'success'
  }
  return statusMap[status] || 'info'
}

const handleSearch = () => {
  currentPage.value = 1
  fetchOrderList()
}

const resetSearch = () => {
  searchForm.value = {
    orderNo: '',
    status: '',
    dateRange: []
  }
  handleSearch()
}

const fetchOrderList = () => {
  loading.value = true
  // 模拟API调用
  setTimeout(() => {
    loading.value = false
  }, 500)
}

const handleSizeChange = (val) => {
  pageSize.value = val
  fetchOrderList()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchOrderList()
}

const createOrder = () => {
  ElMessage.success('新建工单功能开发中')
}

const viewDetail = (row) => {
  ElMessage.success('查看详情功能开发中')
}

const editOrder = (row) => {
  ElMessage.success('编辑工单功能开发中')
}

const deleteOrder = (row) => {
  ElMessageBox.confirm(
    '确定要删除该工单吗？',
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    ElMessage.success('删除成功')
  }).catch(() => {})
}
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

.work-order-content {
  padding: 24px;
}

.search-section {
  background: #fff;
  padding: 24px;
  border-radius: 2px;
  margin-bottom: 24px;
}

.table-section {
  background: #fff;
  padding: 24px;
  border-radius: 2px;
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.table-header h2 {
  font-size: 20px;
  font-weight: 500;
  color: #1d2129;
  margin: 0;
}

.pagination {
  margin-top: 24px;
  display: flex;
  justify-content: flex-end;
}

.header-right {
  display: flex;
  gap: 16px;
  align-items: center;
}
</style> 