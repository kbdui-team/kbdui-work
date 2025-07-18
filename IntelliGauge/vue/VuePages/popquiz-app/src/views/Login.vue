<template>
  <div class="login-container">
    <!-- 添加斜向遍布的背景艺术字 -->
    <div class="background-text-container">
      <div class="background-text">PopQuiz</div>
      <div class="background-text">PopQuiz</div>
      <div class="background-text">PopQuiz</div>
      <div class="background-text">PopQuiz</div>
      <div class="background-text">PopQuiz</div>
      <div class="background-text">PopQuiz</div>
      <div class="background-text">PopQuiz</div>
      <div class="background-text">PopQuiz</div>
      <div class="background-text">PopQuiz</div>
      <div class="background-text">PopQuiz</div>
      <div class="background-text">PopQuiz</div>
      <div class="background-text">PopQuiz</div>
      <div class="background-text">PopQuiz</div>
      <div class="background-text">PopQuiz</div>
      <div class="background-text">PopQuiz</div>
      <div class="background-text">PopQuiz</div>
      <div class="background-text">PopQuiz</div>
      <div class="background-text">PopQuiz</div>
      <div class="background-text">PopQuiz</div>
      <div class="background-text">PopQuiz</div>
      <div class="background-text">PopQuiz</div>
      <div class="background-text">PopQuiz</div>
      <div class="background-text">PopQuiz</div>
      <div class="background-text">PopQuiz</div>
      <div class="background-text">PopQuiz</div>
      <div class="background-text">PopQuiz</div>
      <div class="background-text">PopQuiz</div>
      <div class="background-text">PopQuiz</div>
      <div class="background-text">PopQuiz</div>
      <div class="background-text">PopQuiz</div>
    </div>

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
  position: relative;
  overflow: hidden;
}

/* 背景艺术字容器 */
.background-text-container {
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  transform: rotate(-15deg);
  overflow: hidden;
  z-index: 0;
  pointer-events: none;
}

/* 背景艺术字样式 */
.background-text {
  position: absolute;
  font-size: 4rem;
  font-weight: 900;
  font-family: 'Arial Black', 'Helvetica Neue', Arial, sans-serif;
  background: linear-gradient(45deg, 
    #ff6b6b 0%,
    #4ecdc4 25%, 
    #45b7d1 50%, 
    #96ceb4 75%, 
    #ffeaa7 100%);
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
  background-size: 200% 200%;
  animation: gradientShift 4s ease-in-out infinite;
  opacity: 0.12;
  white-space: nowrap;
  user-select: none;
  letter-spacing: 0.1em;
}

/* 渐变色彩动画 */
@keyframes gradientShift {
  0%, 100% {
    background-position: 0% 50%;
  }
  50% {
    background-position: 100% 50%;
  }
}

/* 遍布整个页面的艺术字定位 */
.background-text:nth-child(1) { top: 5%; left: 10%; animation-delay: 0s; }
.background-text:nth-child(2) { top: 5%; left: 60%; animation-delay: 0.2s; opacity: 0.08; }
.background-text:nth-child(3) { top: 15%; left: 20%; animation-delay: 0.4s; opacity: 0.15; }
.background-text:nth-child(4) { top: 15%; left: 70%; animation-delay: 0.6s; opacity: 0.10; }
.background-text:nth-child(5) { top: 25%; left: 5%; animation-delay: 0.8s; opacity: 0.13; }
.background-text:nth-child(6) { top: 25%; left: 45%; animation-delay: 1.0s; opacity: 0.09; }
.background-text:nth-child(7) { top: 25%; left: 85%; animation-delay: 1.2s; opacity: 0.14; }
.background-text:nth-child(8) { top: 35%; left: 15%; animation-delay: 1.4s; opacity: 0.11; }
.background-text:nth-child(9) { top: 35%; left: 55%; animation-delay: 1.6s; opacity: 0.16; }
.background-text:nth-child(10) { top: 45%; left: 0%; animation-delay: 1.8s; opacity: 0.12; }
.background-text:nth-child(11) { top: 45%; left: 35%; animation-delay: 2.0s; opacity: 0.08; }
.background-text:nth-child(12) { top: 45%; left: 75%; animation-delay: 2.2s; opacity: 0.15; }
.background-text:nth-child(13) { top: 55%; left: 20%; animation-delay: 2.4s; opacity: 0.10; }
.background-text:nth-child(14) { top: 55%; left: 60%; animation-delay: 2.6s; opacity: 0.13; }
.background-text:nth-child(15) { top: 65%; left: 10%; animation-delay: 2.8s; opacity: 0.09; }
.background-text:nth-child(16) { top: 65%; left: 45%; animation-delay: 3.0s; opacity: 0.14; }
.background-text:nth-child(17) { top: 65%; left: 80%; animation-delay: 3.2s; opacity: 0.11; }
.background-text:nth-child(18) { top: 75%; left: 5%; animation-delay: 3.4s; opacity: 0.16; }
.background-text:nth-child(19) { top: 75%; left: 30%; animation-delay: 3.6s; opacity: 0.12; }
.background-text:nth-child(20) { top: 75%; left: 65%; animation-delay: 3.8s; opacity: 0.08; }
.background-text:nth-child(21) { top: 85%; left: 15%; animation-delay: 4.0s; opacity: 0.15; }
.background-text:nth-child(22) { top: 85%; left: 50%; animation-delay: 4.2s; opacity: 0.10; }
.background-text:nth-child(23) { top: 95%; left: 25%; animation-delay: 4.4s; opacity: 0.13; }
.background-text:nth-child(24) { top: 95%; left: 70%; animation-delay: 4.6s; opacity: 0.09; }

/* 补充更多位置覆盖空白区域 */
.background-text:nth-child(25) { top: 0%; left: 35%; animation-delay: 0.3s; opacity: 0.14; }
.background-text:nth-child(26) { top: 10%; left: 90%; animation-delay: 0.7s; opacity: 0.11; }
.background-text:nth-child(27) { top: 30%; left: 25%; animation-delay: 1.1s; opacity: 0.16; }
.background-text:nth-child(28) { top: 40%; left: 65%; animation-delay: 1.5s; opacity: 0.12; }
.background-text:nth-child(29) { top: 60%; left: 30%; animation-delay: 2.5s; opacity: 0.08; }
.background-text:nth-child(30) { top: 80%; left: 85%; animation-delay: 3.5s; opacity: 0.15; }

.login-form-wrapper {
  width: 420px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(15px);
  border-radius: 15px;
  box-shadow: 
    0 25px 50px rgba(0, 0, 0, 0.15),
    0 0 0 1px rgba(255, 255, 255, 0.3);
  padding: 40px;
  position: relative;
  z-index: 10;
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
  background: rgba(248, 249, 250, 0.9);
  color: #666;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  font-size: 14px;
}

.role-tab:hover {
  background: rgba(230, 247, 255, 0.9);
  color: #1890ff;
}

.role-tab.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
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
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
}

.login-button:hover {
  background: linear-gradient(135deg, #5a6fd8 0%, #6a4190 100%);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
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
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(5px);
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

/* 响应式设计 */
@media (max-width: 768px) {
  .background-text {
    font-size: 2rem;
  }
  
  .login-form-wrapper {
    width: 90%;
    padding: 30px 20px;
  }
}

@media (max-width: 480px) {
  .background-text {
    font-size: 1.5rem;
    letter-spacing: 0.05em;
  }
  
  .login-form-wrapper {
    width: 95%;
    padding: 25px 15px;
  }
  
  .role-tab {
    padding: 10px 12px;
    font-size: 13px;
  }
  
  .role-tab .el-icon {
    font-size: 14px;
  }
}

/* 额外的装饰效果 */
.login-container::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: 
    radial-gradient(circle at 15% 25%, rgba(255, 107, 107, 0.1) 0%, transparent 40%),
    radial-gradient(circle at 85% 75%, rgba(78, 205, 196, 0.1) 0%, transparent 40%),
    radial-gradient(circle at 50% 50%, rgba(69, 183, 209, 0.05) 0%, transparent 50%);
  pointer-events: none;
  z-index: 1;
}
</style>
