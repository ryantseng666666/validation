<template>
  <div class="login-container">
    <el-card class="login-card">
      <template #header>
        <h2>登录</h2>
      </template>
      
      <el-form :model="loginForm" :rules="rules" ref="loginFormRef">
        <el-form-item prop="username">
          <el-input v-model="loginForm.username" placeholder="用户名">
            <template #prefix>
              <el-icon><User /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        
        <el-form-item prop="password">
          <el-input v-model="loginForm.password" type="password" placeholder="密码">
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        
        <el-form-item prop="verificationCode" v-if="!isSuperUser">
          <el-input v-model="loginForm.verificationCode" placeholder="验证码">
            <template #append>
              <el-button @click="sendVerificationCode">获取验证码</el-button>
            </template>
          </el-input>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="handleLogin" style="width: 100%">登录</el-button>
        </el-form-item>
        
        <div class="login-links">
          <router-link to="/reset-password" class="forgot-password">忘记密码？</router-link>
          <router-link to="/register">没有账号？立即注册</router-link>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { User, Lock } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import request from '../utils/request'

const router = useRouter()
const loginFormRef = ref(null)

const loginForm = reactive({
  username: '',
  password: '',
  verificationCode: ''
})

// 万能账号
const superUser = {
  username: 'admin',
  password: 'admin123'
}

// 判断是否是万能账号
const isSuperUser = computed(() => {
  return loginForm.username === superUser.username && loginForm.password === superUser.password
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ],
  verificationCode: [
    { required: true, message: '请输入验证码', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  await loginFormRef.value.validate((valid, fields) => {
    if (valid) {
      // 检查是否使用万能账号
      if (isSuperUser.value) {
        ElMessage.success('登录成功')
        localStorage.setItem('token', 'super-admin-token')
        localStorage.setItem('user', JSON.stringify({
          username: 'admin',
          role: 'ADMIN',
          email: 'admin@example.com'
        }))
        router.push('/')
        return
      }
      
      // 正常登录逻辑
      request.post('/api/auth/login', loginForm)
        .then(response => {
          ElMessage.success('登录成功')
          localStorage.setItem('token', response.token)
          localStorage.setItem('user', JSON.stringify(response))
          router.push('/')
        })
    }
  })
}

const sendVerificationCode = () => {
  if (!loginForm.username) {
    ElMessage.warning('请先输入用户名')
    return
  }
  
  request.post('/api/auth/verification-code/login', null, {
    params: { email: loginForm.username }
  })
    .then(() => {
      ElMessage.success('验证码已发送')
    })
    .catch(error => {
      ElMessage.error('验证码发送失败：' + (error.response?.data || error.message))
    })
}
</script>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f5f7fa;
}

.login-card {
  width: 400px;
}

.login-card :deep(.el-card__header) {
  text-align: center;
  font-size: 20px;
  font-weight: bold;
  padding: 20px;
}

.login-links {
  text-align: right;
  margin-top: 15px;
  display: flex;
  justify-content: space-between;
}

.login-links a {
  color: #409EFF;
  text-decoration: none;
}

.login-links a:hover {
  color: #66b1ff;
}

.forgot-password {
  color: #909399;
}

.forgot-password:hover {
  color: #606266;
}
</style> 