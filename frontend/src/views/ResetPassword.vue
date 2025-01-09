<template>
  <div class="reset-container">
    <el-card class="reset-card">
      <template #header>
        <h2>重置密码</h2>
      </template>
      
      <el-form :model="resetForm" :rules="rules" ref="resetFormRef">
        <el-form-item prop="email">
          <el-input v-model="resetForm.email" placeholder="邮箱">
            <template #prefix>
              <el-icon><Message /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        
        <el-form-item prop="verificationCode">
          <el-input v-model="resetForm.verificationCode" placeholder="验证码">
            <template #append>
              <el-button @click="sendVerificationCode">获取验证码</el-button>
            </template>
          </el-input>
        </el-form-item>
        
        <el-form-item prop="newPassword">
          <el-input v-model="resetForm.newPassword" type="password" placeholder="新密码">
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        
        <el-form-item prop="confirmPassword">
          <el-input v-model="resetForm.confirmPassword" type="password" placeholder="确认新密码">
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="handleReset" style="width: 100%">重置密码</el-button>
        </el-form-item>
        
        <div class="reset-links">
          <router-link to="/login">返回登录</router-link>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { Lock, Message } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import request from '../utils/request'

const router = useRouter()
const resetFormRef = ref(null)

const resetForm = reactive({
  email: '',
  verificationCode: '',
  newPassword: '',
  confirmPassword: ''
})

const validatePass = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请输入新密码'))
  } else {
    if (resetForm.confirmPassword !== '') {
      resetFormRef.value.validateField('confirmPassword')
    }
    callback()
  }
}

const validatePass2 = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入新密码'))
  } else if (value !== resetForm.newPassword) {
    callback(new Error('两次输入密码不一致!'))
  } else {
    callback()
  }
}

const rules = {
  email: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  verificationCode: [
    { required: true, message: '请输入验证码', trigger: 'blur' }
  ],
  newPassword: [
    { validator: validatePass, trigger: 'blur' },
    { min: 6, message: '密码长度不能小于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { validator: validatePass2, trigger: 'blur' }
  ]
}

const handleReset = async () => {
  if (!resetFormRef.value) return
  
  await resetFormRef.value.validate((valid, fields) => {
    if (valid) {
      request.post('/api/auth/reset-password', {
        email: resetForm.email,
        verificationCode: resetForm.verificationCode,
        newPassword: resetForm.newPassword
      })
        .then(() => {
          ElMessage.success('密码重置成功')
          router.push('/login')
        })
    }
  })
}

const sendVerificationCode = () => {
  if (!resetForm.email) {
    ElMessage.warning('请先输入邮箱地址')
    return
  }
  
  request.post('/api/auth/verification-code/reset-password', null, {
    params: { email: resetForm.email }
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
.reset-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f5f7fa;
}

.reset-card {
  width: 400px;
}

.reset-card :deep(.el-card__header) {
  text-align: center;
  font-size: 20px;
  font-weight: bold;
  padding: 20px;
}

.reset-links {
  text-align: right;
  margin-top: 15px;
}

.reset-links a {
  color: #409EFF;
  text-decoration: none;
}

.reset-links a:hover {
  color: #66b1ff;
}
</style> 