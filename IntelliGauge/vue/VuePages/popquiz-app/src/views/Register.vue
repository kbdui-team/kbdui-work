<template>
  <div class="register-container">
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
    
    <div class="register-form-wrapper">
      <div class="register-header">
        <h2>用户注册</h2>
        <p>创建您的新账户，开始使用我们的服务</p>
      </div>
      
      <el-form
        ref="registerFormRef"
        :model="registerForm"
        :rules="registerRules"
        class="register-form"
        size="large"
      >
        <el-form-item prop="username">
          <el-input
            v-model="registerForm.username"
            placeholder="请输入用户名"
            clearable
          >
            <template #prefix>
              <el-icon><User /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        
        <el-form-item prop="email">
          <el-input
            v-model="registerForm.email"
            placeholder="请输入邮箱地址"
            clearable
          >
            <template #prefix>
              <el-icon><Message /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        
        <el-form-item prop="phone">
          <el-input
            v-model="registerForm.phone"
            placeholder="请输入手机号码"
            clearable
          >
            <template #prefix>
              <el-icon><Phone /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        
        <el-form-item prop="password">
          <el-input
            v-model="registerForm.password"
            type="password"
            placeholder="请输入密码"
            show-password
          >
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        
        <el-form-item prop="confirmPassword">
          <el-input
            v-model="registerForm.confirmPassword"
            type="password"
            placeholder="请确认密码"
            show-password
            @keyup.enter="handleRegister"
          >
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        
        <el-form-item prop="agreement">
          <el-checkbox v-model="registerForm.agreement">
            我已阅读并同意
            <el-link type="primary" :underline="false">《用户协议》</el-link>
            和
            <el-link type="primary" :underline="false">《隐私政策》</el-link>
          </el-checkbox>
        </el-form-item>
        
        <el-form-item>
          <el-button
            type="primary"
            class="register-button"
            :loading="loading"
            @click="handleRegister"
          >
            {{ loading ? '注册中...' : '立即注册' }}
          </el-button>
        </el-form-item>
      </el-form>
      
      <div class="register-footer">
        <p>已有账户？<el-link type="primary" :underline="false" @click="goToLogin">立即登录</el-link></p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { User, Lock, Message, Phone } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const registerFormRef = ref()
const loading = ref(false)

const registerForm = reactive({
  username: '',
  email: '',
  phone: '',
  password: '',
  confirmPassword: '',
  agreement: false
})

// 自定义验证规则
const validateConfirmPassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== registerForm.password) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

const validatePhone = (rule, value, callback) => {
  const phoneRegex = /^1[3-9]\d{9}$/
  if (value === '') {
    callback(new Error('请输入手机号码'))
  } else if (!phoneRegex.test(value)) {
    callback(new Error('请输入正确的手机号码'))
  } else {
    callback()
  }
}

const validateEmail = (rule, value, callback) => {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  if (value === '') {
    callback(new Error('请输入邮箱地址'))
  } else if (!emailRegex.test(value)) {
    callback(new Error('请输入正确的邮箱地址'))
  } else {
    callback()
  }
}

const validateAgreement = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请阅读并同意用户协议和隐私政策'))
  } else {
    callback()
  }
}

const registerRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度应为3-20个字符', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_]+$/, message: '用户名只能包含字母、数字和下划线', trigger: 'blur' }
  ],
  email: [
    { validator: validateEmail, trigger: 'blur' }
  ],
  phone: [
    { validator: validatePhone, trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度应为6-20个字符', trigger: 'blur' },
    { pattern: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)/, message: '密码必须包含大小写字母和数字', trigger: 'blur' }
  ],
  confirmPassword: [
    { validator: validateConfirmPassword, trigger: 'blur' }
  ],
  agreement: [
    { validator: validateAgreement, trigger: 'change' }
  ]
}

const handleRegister = async () => {
  if (!registerFormRef.value) return
  
  await registerFormRef.value.validate((valid) => {
    if (valid) {
      loading.value = true
      
      // 模拟注册请求
      setTimeout(() => {
        loading.value = false
        console.log('注册数据:', {
          username: registerForm.username,
          email: registerForm.email,
          phone: registerForm.phone,
          password: registerForm.password
        })
        ElMessage.success('注册成功！请登录您的账户')
        
        // 这里可以添加路由跳转到登录页面
        // router.push('/login')
      }, 2000)
    } else {
      ElMessage.error('请完善注册信息')
    }
  })
}

const goToLogin = () => {
  router.push('/')
}
</script>

<style scoped>
.register-container {
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
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  transform: rotate(-15deg);
  overflow: hidden;
  z-index: 0;
  pointer-events: none;
}

/* 背景艺术字样式 */
.background-text {
  position: absolute;
  font-size: 3rem;
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
  opacity: 0.15;
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

/* 分布多个艺术字 */
.background-text:nth-child(1) {
  top: -10%;
  left: -20%;
  animation-delay: 0s;
}

.background-text:nth-child(2) {
  top: 10%;
  left: 20%;
  animation-delay: 0.8s;
  opacity: 0.12;
}

.background-text:nth-child(3) {
  top: 30%;
  left: -10%;
  animation-delay: 1.6s;
  opacity: 0.18;
}

.background-text:nth-child(4) {
  top: 50%;
  left: 30%;
  animation-delay: 2.4s;
  opacity: 0.1;
}

.background-text:nth-child(5) {
  top: 70%;
  left: 0%;
  animation-delay: 3.2s;
  opacity: 0.16;
}

.background-text:nth-child(6) {
  top: 90%;
  left: 40%;
  animation-delay: 4s;
  opacity: 0.14;
}

.register-form-wrapper {
  width: 450px;
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

.register-header {
  text-align: center;
  margin-bottom: 30px;
}

.register-header h2 {
  color: #333;
  margin-bottom: 10px;
  font-size: 24px;
  font-weight: 600;
}

.register-header p {
  color: #666;
  font-size: 14px;
  margin: 0;
}

.register-form .el-form-item {
  margin-bottom: 20px;
}

.register-form .el-form-item:nth-last-child(2) {
  margin-bottom: 15px;
}

.register-button {
  width: 100%;
  height: 45px;
  font-size: 16px;
  border-radius: 6px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
}

.register-button:hover {
  background: linear-gradient(135deg, #5a6fd8 0%, #6a4190 100%);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.register-footer {
  text-align: center;
  margin-top: 20px;
  color: #666;
  font-size: 14px;
}

.register-footer p {
  margin: 0;
}

/* 协议复选框样式调整 */
.register-form .el-checkbox {
  line-height: 1.5;
}

.register-form .el-checkbox__label {
  font-size: 14px;
  color: #666;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .background-text {
    font-size: 6rem;
  }
  
  .register-form-wrapper {
    width: 90%;
    padding: 30px 20px;
  }
}

@media (max-width: 480px) {
  .background-text {
    font-size: 4rem;
    letter-spacing: 0.05em;
  }
  
  .register-form-wrapper {
    width: 95%;
    padding: 25px 15px;
  }
}

/* 额外的装饰效果 */
.register-container::after {
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

<style scoped>
.register-container {
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

.register-form-wrapper {
  width: 450px;
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

/* 其他样式保持不变... */
.register-header {
  text-align: center;
  margin-bottom: 30px;
}

.register-header h2 {
  color: #333;
  margin-bottom: 10px;
  font-size: 24px;
  font-weight: 600;
}

.register-header p {
  color: #666;
  font-size: 14px;
  margin: 0;
}

.register-form .el-form-item {
  margin-bottom: 20px;
}

.register-form .el-form-item:nth-last-child(2) {
  margin-bottom: 15px;
}

.register-button {
  width: 100%;
  height: 45px;
  font-size: 16px;
  border-radius: 6px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
}

.register-button:hover {
  background: linear-gradient(135deg, #5a6fd8 0%, #6a4190 100%);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.register-footer {
  text-align: center;
  margin-top: 20px;
  color: #666;
  font-size: 14px;
}

.register-footer p {
  margin: 0;
}

.register-form .el-checkbox {
  line-height: 1.5;
}

.register-form .el-checkbox__label {
  font-size: 14px;
  color: #666;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .background-text {
    font-size: 2rem;
  }
  
  .register-form-wrapper {
    width: 90%;
    padding: 30px 20px;
  }
}

@media (max-width: 480px) {
  .background-text {
    font-size: 1.5rem;
    letter-spacing: 0.05em;
  }
  
  .register-form-wrapper {
    width: 95%;
    padding: 25px 15px;
  }
}

/* 额外的装饰效果 */
.register-container::after {
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
