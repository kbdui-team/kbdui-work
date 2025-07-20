<template>
    <div class="profile-settings">
      <!-- 页面标题 -->
      <div class="page-title">
        <h2>个人设置</h2>
        <p>管理您的个人信息和账户设置</p>
      </div>
  
      <!-- 设置内容区域 -->
      <div class="settings-content">
        <!-- 头像修改卡片 -->
        <div class="settings-card">
          <div class="card-header">
            <h3>头像设置</h3>
            <p>上传您的个人头像</p>
          </div>
          <div class="card-body">
            <div class="avatar-upload">
              <el-avatar :size="100" :src="userInfo.avatar" class="avatar-preview">
                <el-icon><User /></el-icon>
              </el-avatar>
              <div class="upload-actions">
                <el-upload
                  :show-file-list="false"
                  :before-upload="beforeAvatarUpload"
                  :on-success="handleAvatarSuccess"
                  action="#"
                  :auto-upload="false"
                >
                  <el-button type="primary" :icon="Camera">更换头像</el-button>
                </el-upload>
                <el-button type="danger" plain @click="removeAvatar">
                  <el-icon><Delete /></el-icon>
                  删除头像
                </el-button>
              </div>
            </div>
          </div>
        </div>
  
        <!-- 基本信息修改卡片 -->
        <div class="settings-card">
          <div class="card-header">
            <h3>基本信息</h3>
            <p>修改您的个人基本信息</p>
          </div>
          <div class="card-body">
            <el-form
              ref="basicFormRef"
              :model="basicForm"
              :rules="basicRules"
              label-width="100px"
              class="settings-form"
            >
              <el-row :gutter="20">
                <el-col :span="12">
                  <el-form-item label="姓名" prop="name">
                    <el-input
                      v-model="basicForm.name"
                      placeholder="请输入姓名"
                      :prefix-icon="User"
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="性别" prop="gender">
                    <el-select v-model="basicForm.gender" placeholder="请选择性别">
                      <el-option label="男" value="male" />
                      <el-option label="女" value="female" />
                    </el-select>
                  </el-form-item>
                </el-col>
              </el-row>
  
              <el-row :gutter="20">
                <el-col :span="12">
                  <el-form-item label="学号" prop="studentId">
                    <el-input
                      v-model="basicForm.studentId"
                      placeholder="请输入学号"
                      :prefix-icon="Postcard"
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="班级" prop="class">
                    <el-input
                      v-model="basicForm.class"
                      placeholder="请输入班级"
                      :prefix-icon="School"
                    />
                  </el-form-item>
                </el-col>
              </el-row>
  
              <el-row :gutter="20">
                <el-col :span="12">
                  <el-form-item label="手机号" prop="phone">
                    <el-input
                      v-model="basicForm.phone"
                      placeholder="请输入手机号"
                      :prefix-icon="Phone"
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="邮箱" prop="email">
                    <el-input
                      v-model="basicForm.email"
                      placeholder="请输入邮箱"
                      :prefix-icon="Message"
                    />
                  </el-form-item>
                </el-col>
              </el-row>
  
              <el-form-item label="入学时间" prop="enrollmentDate">
                <el-date-picker
                  v-model="basicForm.enrollmentDate"
                  type="date"
                  placeholder="请选择入学时间"
                  format="YYYY年MM月DD日"
                  value-format="YYYY-MM-DD"
                  :prefix-icon="Calendar"
                />
              </el-form-item>
  
              <el-form-item label="个人简介" prop="bio">
                <el-input
                  v-model="basicForm.bio"
                  type="textarea"
                  :rows="4"
                  placeholder="请输入个人简介"
                  maxlength="200"
                  show-word-limit
                />
              </el-form-item>
  
              <el-form-item>
                <el-button type="primary" @click="saveBasicInfo" :loading="saving">
                  <el-icon><Check /></el-icon>
                  保存修改
                </el-button>
                <el-button @click="resetBasicForm">
                  <el-icon><RefreshLeft /></el-icon>
                  重置
                </el-button>
              </el-form-item>
            </el-form>
          </div>
        </div>
  
        <!-- 密码修改卡片 -->
        <div class="settings-card">
          <div class="card-header">
            <h3>密码修改</h3>
            <p>定期修改密码以保护账户安全</p>
          </div>
          <div class="card-body">
            <el-form
              ref="passwordFormRef"
              :model="passwordForm"
              :rules="passwordRules"
              label-width="100px"
              class="settings-form"
            >
              <el-form-item label="当前密码" prop="currentPassword">
                <el-input
                  v-model="passwordForm.currentPassword"
                  type="password"
                  placeholder="请输入当前密码"
                  :prefix-icon="Lock"
                  show-password
                />
              </el-form-item>
  
              <el-form-item label="新密码" prop="newPassword">
                <el-input
                  v-model="passwordForm.newPassword"
                  type="password"
                  placeholder="请输入新密码"
                  :prefix-icon="Lock"
                  show-password
                />
              </el-form-item>
  
              <el-form-item label="确认密码" prop="confirmPassword">
                <el-input
                  v-model="passwordForm.confirmPassword"
                  type="password"
                  placeholder="请再次输入新密码"
                  :prefix-icon="Lock"
                  show-password
                />
              </el-form-item>
  
              <el-form-item>
                <el-button type="primary" @click="changePassword" :loading="changingPassword">
                  <el-icon><Lock /></el-icon>
                  修改密码
                </el-button>
                <el-button @click="resetPasswordForm">
                  <el-icon><RefreshLeft /></el-icon>
                  重置
                </el-button>
              </el-form-item>
            </el-form>
          </div>
        </div>

      </div>
    </div>
  </template>
  
  <script setup lang="ts">
  import { ref, reactive, onMounted, inject, watch } from 'vue'
  import axios from 'axios'
  import { ElMessage, ElMessageBox } from 'element-plus'
  import {
    User, Camera, Delete, Postcard, School, Phone, Message, Calendar,
    Lock, Check, RefreshLeft
  } from '@element-plus/icons-vue'
  
  // 响应式数据
  const basicFormRef = ref()
  const passwordFormRef = ref()
  const saving = ref(false)
  const changingPassword = ref(false)
  
  // 用户信息
  const userInfo = reactive({
    avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'
  })
  
  // 基本信息表单，字段与后端UserDTO一致，并补充页面字段
  const basicForm = reactive({
    id: '',
    userName: '',
    realName: '',
    email: '',
    phone: '',
    status: '',
    password: '',
    userType: '',
    name: '',
    gender: '',
    studentId: '',
    class: '',
    enrollmentDate: '',
    bio: ''
  })
  
  // 密码修改表单
  const passwordForm = reactive({
    currentPassword: '',
    newPassword: '',
    confirmPassword: ''
  })
  
  // 表单验证规则
  const basicRules = {
    name: [
      { required: true, message: '请输入姓名', trigger: 'blur' },
      { min: 2, max: 10, message: '姓名长度应为2-10个字符', trigger: 'blur' }
    ],
    studentId: [
      { required: true, message: '请输入学号', trigger: 'blur' },
      { pattern: /^\d{10}$/, message: '学号应为10位数字', trigger: 'blur' }
    ],
    class: [
      { required: true, message: '请输入班级', trigger: 'blur' }
    ],
    phone: [
      { required: true, message: '请输入手机号', trigger: 'blur' },
      { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
    ],
    email: [
      { required: true, message: '请输入邮箱', trigger: 'blur' },
      { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
    ],
    enrollmentDate: [
      { required: true, message: '请选择入学时间', trigger: 'change' }
    ]
  }
  
  const passwordRules = {
    currentPassword: [
      { required: true, message: '请输入当前密码', trigger: 'blur' }
    ],
    newPassword: [
      { required: true, message: '请输入新密码', trigger: 'blur' },
      { min: 6, max: 20, message: '密码长度应为6-20个字符', trigger: 'blur' }
    ],
    confirmPassword: [
      { required: true, message: '请再次输入新密码', trigger: 'blur' },
      {
        validator: (rule: unknown, value: string, callback: (err?: Error) => void) => {
          if (value !== passwordForm.newPassword) {
            callback(new Error('两次输入的密码不一致'))
          } else {
            callback()
          }
        },
        trigger: 'blur'
      }
    ]
  }
  
  // 头像上传前检查
  const beforeAvatarUpload = (file: unknown) => {
    const f = file as File & { type: string; size: number }
    const isJPG = f.type === 'image/jpeg' || f.type === 'image/png'
    const isLt2M = f.size / 1024 / 1024 < 2
  
    if (!isJPG) {
      ElMessage.error('上传头像只能是 JPG/PNG 格式!')
    }
    if (!isLt2M) {
      ElMessage.error('上传头像大小不能超过 2MB!')
    }
    return isJPG && isLt2M
  }
  
  // 头像上传成功
  const handleAvatarSuccess = (response: unknown, file: unknown) => {
    const f = file as { raw: File }
    userInfo.avatar = URL.createObjectURL(f.raw)
    ElMessage.success('头像上传成功')
  }
  
  // 删除头像
  const removeAvatar = () => {
    ElMessageBox.confirm('确定要删除头像吗？', '删除确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      userInfo.avatar = ''
      ElMessage.success('头像已删除')
    })
  }
  
  // 保存基本信息
  const baseurl = inject('baseurl')
  const saveBasicInfo = async () => {
    if (!basicFormRef.value) return;
    // 同步页面字段到后端字段
    basicForm.realName = basicForm.name;
    basicForm.userName = basicForm.studentId;
    basicForm.status = basicForm.class;
    // 如有需要可同步basicForm.bio、basicForm.enrollmentDate到后端自定义字段
    await basicFormRef.value.validate(async (valid: unknown) => {
      if (valid) {
        saving.value = true;
        try {
          const payload = {
            id: basicForm.id,
            userName: basicForm.userName,
            realName: basicForm.realName,
            email: basicForm.email,
            phone: basicForm.phone,
            status: basicForm.status,
            password: basicForm.password,
            userType: basicForm.userType
          };
          const res = await axios.put(`${baseurl}/user/update`, payload);
          saving.value = false;
          if (res.data === true) {
            ElMessage.success('基本信息保存成功');
            localStorage.setItem('currentUser', JSON.stringify({ ...basicForm, ...payload }));
          } else {
            ElMessage.error('保存失败，请重试');
          }
        } catch {
          saving.value = false;
          ElMessage.error('保存失败，请检查网络或稍后重试');
        }
      } else {
        ElMessage.error('请检查填写的信息');
      }
    });
  }
  
  // 重置基本信息表单
  const resetBasicForm = () => {
    basicFormRef.value?.resetFields()
    ElMessage.info('表单已重置')
  }
  
  // 修改密码
  const changePassword = async () => {
    if (!passwordFormRef.value) return;
    await passwordFormRef.value.validate(async (valid: unknown) => {
      if (valid) {
        // 校验旧密码
        const userStr = localStorage.getItem('currentUser');
        let oldPassword = '';
        if (userStr) {
          try {
            const user = JSON.parse(userStr);
            oldPassword = user.password;
          } catch {}
        }
        if (passwordForm.currentPassword !== oldPassword) {
          ElMessage.error('当前密码错误');
          return;
        }
        if (passwordForm.newPassword !== passwordForm.confirmPassword) {
          ElMessage.error('两次输入的新密码不一致');
          return;
        }
        changingPassword.value = true;
        try {
          // 只更新密码字段
          const payload = {
            id: basicForm.id,
            password: passwordForm.newPassword
          };
          const res = await axios.put(`${baseurl}/user/update`, payload);
          changingPassword.value = false;
          if (res.data === true) {
            ElMessage.success('密码修改成功');
            // 更新localStorage
            const user = userStr ? JSON.parse(userStr) : {};
            user.password = passwordForm.newPassword;
            localStorage.setItem('currentUser', JSON.stringify(user));
            passwordForm.currentPassword = '';
            passwordForm.newPassword = '';
            passwordForm.confirmPassword = '';
          } else {
            ElMessage.error('密码修改失败，请重试');
          }
        } catch {
          changingPassword.value = false;
          ElMessage.error('密码修改失败，请检查网络或稍后重试');
        }
      } else {
        ElMessage.error('请检查密码信息');
      }
    });
  }
  
  // 重置密码表单
  const resetPasswordForm = () => {
    passwordFormRef.value?.resetFields()
    ElMessage.info('密码表单已重置')
  }
  
  // 页面加载时填充表单
  onMounted(() => {
    const userStr = localStorage.getItem('currentUser')
    if (userStr) {
      try {
        const user = JSON.parse(userStr)
        Object.keys(basicForm).forEach(key => {
          if (user[key as keyof typeof basicForm] !== undefined) basicForm[key as keyof typeof basicForm] = user[key as keyof typeof basicForm]
        })
      } catch {}
    }
  })

  // 实时同步basicForm到localStorage
  watch(basicForm, (val) => {
    localStorage.setItem('currentUser', JSON.stringify(val))
  }, { deep: true })
  </script>
  
  <style scoped>
  .profile-settings {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 20px;
  }
  
  .page-title {
    margin-bottom: 30px;
    text-align: center;
  }
  
  .page-title h2 {
    color: #1a1a1a;
    font-size: 28px;
    font-weight: 600;
    margin: 0 0 10px 0;
  }
  
  .page-title p {
    color: #666;
    font-size: 16px;
    margin: 0;
  }
  
  .settings-content {
    display: flex;
    flex-direction: column;
    gap: 30px;
  }
  
  .settings-card {
    background: #fff;
    border-radius: 16px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
    overflow: hidden;
    transition: box-shadow 0.3s ease;
  }
  
  .settings-card:hover {
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  }
  
  .settings-card.danger-zone {
    border: 2px solid #ff4d4f;
  }
  
  .settings-card.danger-zone .card-header {
    background: #fff2f0;
    border-bottom-color: #ffccc7;
  }
  
  .card-header {
    background: #fafbfc;
    border-bottom: 1px solid #e8eaec;
    padding: 25px 30px;
  }
  
  .card-header h3 {
    color: #1a1a1a;
    font-size: 18px;
    font-weight: 600;
    margin: 0 0 5px 0;
  }
  
  .card-header p {
    color: #666;
    font-size: 14px;
    margin: 0;
  }
  
  .card-body {
    padding: 30px;
  }
  
  .avatar-upload {
    display: flex;
    align-items: center;
    gap: 30px;
  }
  
  .avatar-preview {
    border: 4px solid #f0f0f0;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  }
  
  .upload-actions {
    display: flex;
    flex-direction: column;
    gap: 12px;
  }
  
  .settings-form {
    max-width: 800px;
  }
  
  .settings-form .el-form-item {
    margin-bottom: 25px;
  }
  
  .notification-settings {
    display: flex;
    flex-direction: column;
    gap: 25px;
  }
  
  .setting-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 20px;
    background: #f8faff;
    border-radius: 12px;
    border: 1px solid #e8eaec;
  }
  
  .setting-info h4 {
    color: #1a1a1a;
    font-size: 16px;
    font-weight: 600;
    margin: 0 0 5px 0;
  }
  
  .setting-info p {
    color: #666;
    font-size: 14px;
    margin: 0;
  }
  
  .notification-actions {
    margin-top: 30px;
    padding-top: 25px;
    border-top: 1px solid #e8eaec;
  }
  
  .danger-actions {
    display: flex;
    flex-direction: column;
    gap: 25px;
  }
  
  .danger-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 20px;
    background: #fff2f0;
    border-radius: 12px;
    border: 1px solid #ffccc7;
  }
  
  .danger-info h4 {
    color: #ff4d4f;
    font-size: 16px;
    font-weight: 600;
    margin: 0 0 5px 0;
  }
  
  .danger-info p {
    color: #666;
    font-size: 14px;
    margin: 0;
  }
  
  /* 表单样式优化 */
  .settings-form :deep(.el-input) {
    height: 45px;
  }
  
  .settings-form :deep(.el-input__wrapper) {
    border-radius: 8px;
  }
  
  .settings-form :deep(.el-select) {
    width: 100%;
  }
  
  .settings-form :deep(.el-date-editor) {
    width: 100%;
  }
  
  .settings-form :deep(.el-textarea__inner) {
    border-radius: 8px;
  }
  
  /* 按钮样式 */
  .el-button {
    border-radius: 8px;
    padding: 12px 24px;
    font-weight: 500;
  }
  
  .el-button--primary {
    background: linear-gradient(135deg, #667eea, #764ba2);
    border: none;
  }
  
  .el-button--primary:hover {
    background: linear-gradient(135deg, #5a6fd8, #6a4190);
  }
  
  /* 开关样式 */
  .el-switch {
    --el-switch-on-color: #667eea;
  }
  
  /* 响应式设计 */
  @media (max-width: 768px) {
    .profile-settings {
      padding: 0 15px;
    }
    
    .card-body {
      padding: 20px;
    }
    
    .avatar-upload {
      flex-direction: column;
      text-align: center;
    }
    
    .setting-item,
    .danger-item {
      flex-direction: column;
      gap: 15px;
      text-align: center;
    }
    
    .settings-form .el-col {
      width: 100%;
    }
  }
  
  /* 动画效果 */
  .settings-card {
    animation: slideInUp 0.5s ease-out;
  }
  
  @keyframes slideInUp {
    from {
      opacity: 0;
      transform: translateY(20px);
    }
    to {
      opacity: 1;
      transform: translateY(0);
    }
  }
  </style>
  