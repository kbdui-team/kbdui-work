<template>
    <div class="register-container">
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
          <el-form-item prop="userName">
            <el-input
              v-model="registerForm.userName"
              placeholder="请输入用户名"
              clearable
            >
              <template #prefix>
                <el-icon><User /></el-icon>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item prop="realName">
            <el-input
              v-model="registerForm.realName"
              placeholder="请输入真实姓名"
              clearable
            >
              <template #prefix>
                <el-icon><User /></el-icon>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item prop="userType">
            <el-select v-model="registerForm.userType" placeholder="请选择用户类型" clearable>
              <el-option label="普通用户" value="user" />
              <el-option label="管理员" value="admin" />
            </el-select>
          </el-form-item>
          <el-form-item prop="status">
            <el-input
              v-model="registerForm.status"
              placeholder="请输入状态（可选，默认 active）"
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
  import { ref, reactive, inject } from 'vue'
  import { ElMessage } from 'element-plus'
  import { User, Lock, Message, Phone } from '@element-plus/icons-vue'
  import { useRouter } from 'vue-router'
  import axios from 'axios'
  
  const router = useRouter()
  const registerFormRef = ref()
  const loading = ref(false)
  const baseurl = inject('baseurl')
  
  const registerForm = reactive({
    userName: '',
    realName: '',
    email: '',
    phone: '',
    password: '',
    confirmPassword: '',
    userType: '',
    status: 'active',
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
    userName: [
      { required: true, message: '请输入用户名', trigger: 'blur' },
      { min: 3, max: 20, message: '用户名长度应为3-20个字符', trigger: 'blur' },
      { pattern: /^[a-zA-Z0-9_]+$/, message: '用户名只能包含字母、数字和下划线', trigger: 'blur' }
    ],
    realName: [
      { required: true, message: '请输入真实姓名', trigger: 'blur' },
      { min: 2, max: 20, message: '真实姓名长度应为2-20个字符', trigger: 'blur' }
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
    userType: [
      { required: true, message: '请选择用户类型', trigger: 'change' }
    ],
    status: [
      { required: false }
    ],
    agreement: [
      { validator: validateAgreement, trigger: 'change' }
    ]
  }
  
  const handleRegister = async () => {
    if (!registerFormRef.value) return

    await registerFormRef.value.validate(async (valid) => {
      if (valid) {
        loading.value = true
        try {
          const payload = {
            userName: registerForm.userName,
            realName: registerForm.realName,
            email: registerForm.email,
            phone: registerForm.phone,
            password: registerForm.password,
            userType: registerForm.userType,
            status: registerForm.status || 'active'
          }
          // 使用baseurl拼接完整后端地址
          const response = await axios.post(`${baseurl}/user/add`, payload)
          loading.value = false
          // 后端返回true为注册成功
          if (response.data === true) {
            ElMessage.success('注册成功！请登录您的账户')
            router.push('/')
          } else {
            ElMessage.error('注册失败，请重试')
          }
        } catch (error) {
          loading.value = false
          ElMessage.error(error.response?.data?.message || '注册失败，请检查网络或稍后重试')
        }
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
  }
  
  .register-form-wrapper {
    width: 450px;
    background: white;
    border-radius: 10px;
    box-shadow: 0 15px 35px rgba(0, 0, 0, 0.1);
    padding: 40px;
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
  
  @media (max-width: 480px) {
    .register-form-wrapper {
      width: 90%;
      padding: 30px 20px;
    }
  }
  </style>
  