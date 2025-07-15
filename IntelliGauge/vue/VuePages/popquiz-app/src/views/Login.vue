<template>
  <div class="login-container">
    <div class="login-form-wrapper">
      <div class="login-header">
        <h2>用户登录</h2>
        <p>欢迎回来，请选择角色并输入您的账户信息</p>
      </div>
      
      <!-- 角色选择 -->
      <div class="role-selector">
        <div class="role-tabs">
          <div 
            class="role-tab"
            :class="{ active: selectedRole === 'teacher' }"
            @click="selectRole('teacher')"
          >
            <el-icon><User /></el-icon>
            <span>教师登录</span>
          </div>
          <div 
            class="role-tab"
            :class="{ active: selectedRole === 'student' }"
            @click="selectRole('student')"
          >
            <el-icon><Avatar /></el-icon>
            <span>学生登录</span>
          </div>
        </div>
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
            :placeholder="selectedRole === 'teacher' ? '请输入教师工号' : '请输入学号'"
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
            {{ loading ? '登录中...' : `${roleNames[selectedRole]}登录` }}
          </el-button>
        </el-form-item>
      </el-form>
      
      <div class="login-footer">
        <p>还没有账户？<el-link type="primary" :underline="false" @click="goToRegister">立即注册</el-link></p>
      </div>
      
      <!-- 提示信息 -->
      <div class="login-hint">
        <el-alert
          :title="`${roleNames[selectedRole]}测试账号`"
          type="info"
          :closable="false"
          show-icon
        >
          <template #default>
            <div v-if="selectedRole === 'teacher'">
              <p>工号：teacher001</p>
              <p>密码：teacher123</p>
              <p class="role-desc">教师可管理演讲和查看统计</p>
            </div>
            <div v-else>
              <p>学号：202100001</p>
              <p>密码：student123</p>
              <p class="role-desc">学生可参与答题和查看成绩</p>
            </div>
          </template>
        </el-alert>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { User, Lock, Avatar } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'

defineOptions({ name: 'LoginPage' })

const router = useRouter()
const loginFormRef = ref()
const loading = ref(false)
const selectedRole = ref('teacher') // 默认选择教师

// 角色名称映射
const roleNames: Record<string, string> = {
  teacher: '教师',
  student: '学生'
}

// 预设的账号密码（按角色分类）
const VALID_CREDENTIALS: Record<string, { username: string; password: string }> = {
  teacher: {
    username: 'teacher001',
    password: 'teacher123'
  },
  student: {
    username: '202100001',
    password: 'student123'
  }
}

// 路由映射 - 修正为与路由表一致的路径
const ROLE_ROUTES: Record<string, string> = {
  teacher: '/HomePage',  // 对应路由表中的教师首页路径
  student: '/student-homepage'   // 对应路由表中的学生首页路径
}

const loginForm = reactive({
  username: '',
  password: '',
  remember: false
})

// 动态验证规则
const loginRules = computed(() => ({
  username: [
    { required: true, message: selectedRole.value === 'teacher' ? '请输入教师工号' : '请输入学号', trigger: 'blur' },
    { 
      min: 3, 
      max: 20, 
      message: selectedRole.value === 'teacher' ? '工号长度应为3-20个字符' : '学号长度应为3-20个字符', 
      trigger: 'blur' 
    }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 3, max: 20, message: '密码长度应为3-20个字符', trigger: 'blur' }
  ]
}))

// 选择角色
const selectRole = (role: string) => {
  selectedRole.value = role
  // 清空表单
  loginForm.username = ''
  loginForm.password = ''
  
  // 清除验证错误
  if (loginFormRef.value) {
    loginFormRef.value.clearValidate()
  }
  
  // 检查是否有记住的账号
  checkRememberedCredentials()
}

// 验证账号密码
const validateCredentials = (username: string, password: string, role: string) => {
  const validCred = VALID_CREDENTIALS[role]
  return username === validCred.username && password === validCred.password
}

const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  await loginFormRef.value.validate((valid: boolean) => {
    if (valid) {
      loading.value = true
      
      // 模拟登录请求延迟
      setTimeout(() => {
        loading.value = false
        
        // 验证账号密码
        if (validateCredentials(loginForm.username, loginForm.password, selectedRole.value)) {
          console.log('登录成功，用户信息:', {
            username: loginForm.username,
            role: selectedRole.value,
            roleName: roleNames[selectedRole.value],
            remember: loginForm.remember,
            loginTime: new Date().toLocaleString()
          })
          
          // 如果勾选了记住密码，按角色存储到localStorage
          const storageKey = `remembered_${selectedRole.value}`
          if (loginForm.remember) {
            localStorage.setItem(storageKey, JSON.stringify({
              username: loginForm.username,
              role: selectedRole.value
            }))
          } else {
            localStorage.removeItem(storageKey)
          }
          
          // 存储登录状态和用户信息
          localStorage.setItem('isLoggedIn', 'true')
          localStorage.setItem('currentUser', JSON.stringify({
            username: loginForm.username,
            role: selectedRole.value,
            roleName: roleNames[selectedRole.value]
          }))
          
          ElMessage.success(`${roleNames[selectedRole.value]}登录成功！欢迎回来`)
          
          // 根据角色跳转到不同的页面
          setTimeout(() => {
            const targetRoute = ROLE_ROUTES[selectedRole.value]
            console.log('准备跳转到:', targetRoute) // 添加调试信息
            router.push(targetRoute)
          }, 1000)
          
        } else {
          // 账号或密码错误
          ElMessage.error(`${roleNames[selectedRole.value]}账号或密码错误，请重新输入`)
          
          // 清空密码输入框
          loginForm.password = ''
          
          // 聚焦到用户名输入框
          setTimeout(() => {
            const usernameInput = document.querySelector('input[placeholder*="请输入"]')
            if (usernameInput) {
              (usernameInput as HTMLElement).focus()
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
  // 带上角色参数跳转到注册页面
  router.push(`/register?role=${selectedRole.value}`)
}

// 检查记住的凭证
const checkRememberedCredentials = () => {
  const storageKey = `remembered_${selectedRole.value}`
  const remembered = localStorage.getItem(storageKey)
  
  if (remembered) {
    try {
      const { username } = JSON.parse(remembered)
      loginForm.username = username
      loginForm.remember = true
    } catch {
      localStorage.removeItem(storageKey)
    }
  }
}

// 组件挂载时检查是否有记住的用户名
import { onMounted } from 'vue'

onMounted(() => {
  // 检查URL参数中是否指定了角色
  const urlParams = new URLSearchParams(window.location.search)
  const roleParam = urlParams.get('role')
  if (roleParam && (roleParam === 'teacher' || roleParam === 'student')) {
    selectedRole.value = roleParam
  }
  
  // 检查记住的凭证
  checkRememberedCredentials()
  
  // 检查是否已经登录
  const isLoggedIn = localStorage.getItem('isLoggedIn')
  const currentUser = localStorage.getItem('currentUser')
  
  if (isLoggedIn === 'true' && currentUser) {
    try {
      const user = JSON.parse(currentUser)
      ElMessage.info(`您已登录为${user.roleName}：${user.username}`)
      // 仅提示已登录，不自动跳转
    } catch {
      // 清除无效的登录状态
      localStorage.removeItem('isLoggedIn')
      localStorage.removeItem('currentUser')
    }
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
  width: 420px;
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

/* 角色选择样式 */
.role-selector {
  margin-bottom: 25px;
}

.role-tabs {
  display: flex;
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid #e4e7ed;
}

.role-tab {
  flex: 1;
  padding: 12px 16px;
  text-align: center;
  cursor: pointer;
  background: #f8f9fa;
  color: #666;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  font-size: 14px;
}

.role-tab:hover {
  background: #e6f7ff;
  color: #1890ff;
}

.role-tab.active {
  background: #667eea;
  color: white;
}

.role-tab .el-icon {
  font-size: 16px;
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

.role-desc {
  color: #909399;
  font-style: italic;
  margin-top: 8px !important;
}

@media (max-width: 480px) {
  .login-form-wrapper {
    width: 90%;
    padding: 30px 20px;
  }
  
  .role-tab {
    padding: 10px 12px;
    font-size: 13px;
  }
  
  .role-tab .el-icon {
    font-size: 14px;
  }
}
</style>
