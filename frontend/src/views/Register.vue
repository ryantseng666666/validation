<template>
  <div class="register-container">
    <el-card class="register-card">
      <template #header>
        <div class="logo-container">
          <img src="../assets/cmhk.png" alt="CMHK Logo" class="logo-image">
          <h2>注册</h2>
        </div>
      </template>
      
      <el-form :model="registerForm" :rules="rules" ref="registerFormRef">
        <el-form-item prop="username">
          <el-input v-model="registerForm.username" placeholder="用户名">
            <template #prefix>
              <el-icon><User /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        
        <el-form-item prop="password">
          <el-input v-model="registerForm.password" type="password" placeholder="密码">
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        
        <el-form-item prop="confirmPassword">
          <el-input v-model="registerForm.confirmPassword" type="password" placeholder="确认密码">
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        
        <el-form-item prop="email">
          <el-input v-model="registerForm.email" placeholder="邮箱">
            <template #prefix>
              <el-icon><Message /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        
        <el-form-item prop="verificationCode">
          <el-input v-model="registerForm.verificationCode" placeholder="验证码">
            <template #append>
              <el-button @click="sendVerificationCode">获取验证码</el-button>
            </template>
          </el-input>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="handleRegister" style="width: 100%">注册</el-button>
        </el-form-item>
        
        <div class="register-links">
          <router-link to="/login">已有账号？立即登录</router-link>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { User, Lock, Message } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import request from '../utils/request'

const router = useRouter()
const registerFormRef = ref(null)

const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  email: '',
  verificationCode: ''
})

const validatePass = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请输入密码'))
  } else {
    if (registerForm.confirmPassword !== '') {
      registerFormRef.value.validateField('confirmPassword')
    }
    callback()
  }
}

const validatePass2 = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== registerForm.password) {
    callback(new Error('两次输入密码不一致!'))
  } else {
    callback()
  }
}

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { validator: validatePass, trigger: 'blur' },
    { min: 6, message: '密码长度不能小于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { validator: validatePass2, trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  verificationCode: [
    { required: true, message: '请输入验证码', trigger: 'blur' }
  ]
}

const handleRegister = async () => {
  if (!registerFormRef.value) return
  
  await registerFormRef.value.validate((valid, fields) => {
    if (valid) {
      request.post('/auth/register', registerForm)
        .then(response => {
          ElMessage.success('注册成功')
          router.push('/login')
        })
        .catch(error => {
          ElMessage.error('注册失败：' + (error.response?.data || error.message))
        })
    }
  })
}

const sendVerificationCode = () => {
  if (!registerForm.email) {
    ElMessage.warning('请先输入邮箱地址')
    return
  }
  
  request.post('/auth/verification-code/register', null, {
    params: { email: registerForm.email }
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
.register-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f5f7fa;
}

.register-card {
  width: 400px;
}

.register-card :deep(.el-card__header) {
  text-align: center;
  font-size: 20px;
  font-weight: bold;
  padding: 20px;
}

.register-links {
  text-align: right;
  margin-top: 15px;
}

.register-links a {
  color: #409EFF;
  text-decoration: none;
}

.register-links a:hover {
  color: #66b1ff;
}

.logo-container {
  text-align: center;
  padding: 20px;
}

.logo-image {
  width: 120px;
  height: auto;
  margin-bottom: 15px;
}
</style> 