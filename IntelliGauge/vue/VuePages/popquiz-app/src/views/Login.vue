<template>
    <div class="login-container">
      <div class="login-form-wrapper">
        <div class="login-header">
          <h2>用户登录</h2>
          <p>欢迎回来，请输入您的账户信息</p>
        </div>
        
        <el-form
          ref="loginFormRef"
          :model="loginForm"
          :rules="loginRules"
          class="login-form"
          size="large"
        >
          <el-form-item prop="username">
            <el-input
              v-model="loginForm.username"
              placeholder="请输入用户名"
              clearable
            >
              <template #prefix>
                <el-icon><User /></el-icon>
              </template>
            </el-input>
          </el-form-item>
          
          <el-form-item prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="请输入密码"
              show-password
              @keyup.enter="handleLogin"
            >
              <template #prefix>
                <el-icon><Lock /></el-icon>
              </template>
            </el-input>
          </el-form-item>
          
          <el-form-item>
            <div class="login-options">
              <el-checkbox v-model="loginForm.remember">记住密码</el-checkbox>
              <el-link type="primary" :underline="false">忘记密码？</el-link>
            </div>
          </el-form-item>
          
          <el-form-item>
            <el-button
              type="primary"
              class="login-button"
              :loading="loading"
              @click="handleLogin"
            >
              {{ loading ? '登录中...' : '登录' }}
            </el-button>
          </el-form-item>
        </el-form>
        
        <div class="login-footer">
          <p>还没有账户？<el-link type="primary" :underline="false">立即注册</el-link></p>
        </div>
      </div>
    </div>
  </template>
  
  <script setup>
  import { ref, reactive } from 'vue'
  import { ElMessage } from 'element-plus'
  import { User, Lock } from '@element-plus/icons-vue'
  
  const loginFormRef = ref()
  const loading = ref(false)
  
  const loginForm = reactive({
    username: '',
    password: '',
    remember: false
  })
  
  const loginRules = {
    username: [
      { required: true, message: '请输入用户名', trigger: 'blur' },
      { min: 3, max: 20, message: '用户名长度应为3-20个字符', trigger: 'blur' }
    ],
    password: [
      { required: true, message: '请输入密码', trigger: 'blur' },
      { min: 6, max: 20, message: '密码长度应为6-20个字符', trigger: 'blur' }
    ]
  }
  
  const handleLogin = async () => {
    if (!loginFormRef.value) return
    
    await loginFormRef.value.validate((valid) => {
      if (valid) {
        loading.value = true
        
        setTimeout(() => {
          loading.value = false
          console.log('登录数据:', loginForm)
          ElMessage.success('登录成功！')
        }, 1500)
      } else {
        ElMessage.error('请填写完整的登录信息')
      }
    })
  }
  </script>
  
  <style scoped>
  .login-container {
    min-height: 100vh;
    display: flex;
    align-items: center;
    justify-content: center;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    padding: 20px;
  }
  
  .login-form-wrapper {
    width: 400px;
    background: white;
    border-radius: 10px;
    box-shadow: 0 15px 35px rgba(0, 0, 0, 0.1);
    padding: 40px;
  }
  
  .login-header {
    text-align: center;
    margin-bottom: 30px;
  }
  
  .login-header h2 {
    color: #333;
    margin-bottom: 10px;
    font-size: 24px;
    font-weight: 600;
  }
  
  .login-header p {
    color: #666;
    font-size: 14px;
    margin: 0;
  }
  
  .login-form .el-form-item {
    margin-bottom: 20px;
  }
  
  .login-options {
    display: flex;
    justify-content: space-between;
    align-items: center;
    width: 100%;
  }
  
  .login-button {
    width: 100%;
    height: 45px;
    font-size: 16px;
    border-radius: 6px;
  }
  
  .login-footer {
    text-align: center;
    margin-top: 20px;
    color: #666;
    font-size: 14px;
  }
  
  .login-footer p {
    margin: 0;
  }
  
  @media (max-width: 480px) {
    .login-form-wrapper {
      width: 90%;
      padding: 30px 20px;
    }
  }
  </style>
  