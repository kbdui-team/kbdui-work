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
        <p>还没有账户？<el-link type="primary" :underline="false" @click="goToRegister">立即注册</el-link></p>
      </div>
      
      <!-- 提示信息 -->
      <div class="login-hint">
        <el-alert
          title="测试账号"
          type="info"
          :closable="false"
          show-icon
        >
          <template #default>
            <p>用户名：admin</p>
            <p>密码：admin</p>
          </template>
        </el-alert>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const loginFormRef = ref()
const loading = ref(false)

// 预设的账号密码
const VALID_CREDENTIALS = {
  username: 'admin',
  password: 'admin'
}

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
    { min: 3, max: 20, message: '密码长度应为3-20个字符', trigger: 'blur' }
  ]
}

// 验证账号密码
const validateCredentials = (username, password) => {
  return username === VALID_CREDENTIALS.username && password === VALID_CREDENTIALS.password
}

const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  await loginFormRef.value.validate((valid) => {
    if (valid) {
      loading.value = true
      
      // 模拟登录请求延迟
      setTimeout(() => {
        loading.value = false
        
        // 验证账号密码
        if (validateCredentials(loginForm.username, loginForm.password)) {
          console.log('登录成功，用户信息:', {
            username: loginForm.username,
            remember: loginForm.remember,
            loginTime: new Date().toLocaleString()
          })
          
          // 如果勾选了记住密码，可以存储到localStorage
          if (loginForm.remember) {
            localStorage.setItem('rememberedUser', loginForm.username)
          } else {
            localStorage.removeItem('rememberedUser')
          }
          
          // 存储登录状态
          localStorage.setItem('isLoggedIn', 'true')
          localStorage.setItem('currentUser', loginForm.username)
          
          ElMessage.success('登录成功！欢迎回来')
          
          // 跳转到答题页面或其他页面
          setTimeout(() => {
            router.push('/answer')
          }, 1000)
          
        } else {
          // 账号或密码错误
          ElMessage.error('用户名或密码错误，请重新输入')
          
          // 清空密码输入框
          loginForm.password = ''
          
          // 聚焦到用户名输入框
          setTimeout(() => {
            const usernameInput = document.querySelector('input[placeholder="请输入用户名"]')
            if (usernameInput) {
              usernameInput.focus()
            }
          }, 100)
        }
      }, 1500)
    } else {
      ElMessage.error('请填写完整的登录信息')
    }
  })
}

const goToRegister = () => {
  router.push('/register')
}

// 组件挂载时检查是否有记住的用户名
import { onMounted } from 'vue'

onMounted(() => {
  const rememberedUser = localStorage.getItem('rememberedUser')
  if (rememberedUser) {
    loginForm.username = rememberedUser
    loginForm.remember = true
  }
})
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

.login-footer .el-link {
  cursor: pointer;
}

.login-hint {
  margin-top: 20px;
}

.login-hint .el-alert {
  border-radius: 6px;
}

.login-hint .el-alert p {
  margin: 2px 0;
  font-size: 13px;
}

@media (max-width: 480px) {
  .login-form-wrapper {
    width: 90%;
    padding: 30px 20px;
  }
}
</style>
