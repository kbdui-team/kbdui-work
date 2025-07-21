<template>
    <div class="profile-container">
      <!-- 左侧个人信息区域 -->
      <div class="profile-sidebar">
        <!-- 个人信息卡片 -->
        <div class="user-info-card">
          <div class="avatar-section">
            <el-avatar :size="80" :src="userInfo.avatar">
              <el-icon><User /></el-icon>
            </el-avatar>
            <div class="online-status"></div>
          </div>
          
          <div class="user-details">
            <h3 class="user-name">{{ userInfo.realName || userInfo.userName }}</h3>
            <p class="user-role">{{ userInfo.userType === 'admin' ? '管理员' : '教师' }}</p>
          </div>
          
          <div class="info-list">
            <div class="info-item">
              <el-icon class="info-icon"><School /></el-icon>
              <div class="info-content">
                <span class="info-label">办公室</span>
                <span class="info-value">{{ userInfo.class }}</span>
              </div>
            </div>
            <div class="info-item">
              <el-icon class="info-icon"><Postcard /></el-icon>
              <div class="info-content">
                <span class="info-label">工号</span>
                <span class="info-value">{{ userInfo.userName }}</span>
              </div>
            </div>
            
            <div class="info-item">
              <el-icon class="info-icon"><Phone /></el-icon>
              <div class="info-content">
                <span class="info-label">电话</span>
                <span class="info-value">{{ userInfo.phone }}</span>
              </div>
            </div>
            
            <div class="info-item">
              <el-icon class="info-icon"><Message /></el-icon>
              <div class="info-content">
                <span class="info-label">邮箱</span>
                <span class="info-value">{{ userInfo.email }}</span>
              </div>
            </div>
            
           
          </div>
          
          
        </div>
        
        <!-- 功能选择区域 -->
        <div class="function-menu">
          <h4 class="menu-title">功能菜单</h4>
          
          <!-- 学习模块 -->
          <div class="menu-section">
            <div class="section-header">
              <el-icon><Reading /></el-icon>
              <span>学习模块</span>
            </div>
            <div class="menu-items">
              <div 
                class="menu-item"
                :class="{ active: activeItem === 'Collection' }"
                @click="navigateTo('Collection')"
              >
                <el-icon><Notebook /></el-icon>
                <span>模式选择</span>
              </div>

              <div 
                class="menu-item"
                :class="{ active: activeItem === 'review' }"
                @click="navigateTo('review')"
              >
                <el-icon><View /></el-icon>
                <span>教学记录</span>
              </div>

              <!-- <div 
                class="menu-item"
                :class="{ active: activeItem === 'orator_result' }"
                @click="navigateTo('orator_result')"
              >
                <el-icon><View /></el-icon>
                <span>练习记录</span>
              </div> -->
            </div>
          </div>
          
          <!-- 成绩模块 -->
          <div class="menu-section">
            <div class="section-header">
              <el-icon><TrendCharts /></el-icon>
              <span>管理模块</span>
            </div>
            <div class="menu-items">
              <div 
                class="menu-item"
                :class="{ active: activeItem === 'scores' }"
                @click="navigateTo('scores')"
              >
                <el-icon><DataAnalysis /></el-icon>
                <span>学生管理</span>
              </div>
              <div 
                class="menu-item"
                :class="{ active: activeItem === 'ranking' }"
                @click="navigateTo('ranking')"
              >
                <el-icon><Trophy /></el-icon>
                <span>成绩管理</span>
              </div>
              <!-- <div 
                class="menu-item"
                :class="{ active: activeItem === 'progress' }"
                @click="navigateTo('progress')"
              >
                <el-icon><Promotion /></el-icon>
                <span>学习进度1</span>
              </div> -->
            </div>
          </div>
          
          <!-- 设置模块 -->
          <div class="menu-section">
            <div class="section-header">
              <el-icon><Setting /></el-icon>
              <span>设置模块</span>
            </div>
            <div class="menu-items">
              <div 
                class="menu-item"
                :class="{ active: activeItem === 'changeInfo' }"
                @click="navigateTo('changeInfo')"
              >
                <el-icon><User /></el-icon>
                <span>个人设置</span>
              </div>
              <div 
                class="menu-item"
                :class="{ active: activeItem === 'notifications' }"
                @click="navigateTo('notifications')"
              >
                <el-icon><Bell /></el-icon>
                <span>通知设置</span>
              </div>
              <div 
                class="menu-item"
                @click="handleLogout"
              >
                <el-icon><SwitchButton /></el-icon>
                <span>退出登录</span>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 右侧内容区域 -->
      <div class="main-content">
        <div class="content-header">
          <div class="breadcrumb">
            <el-breadcrumb separator="/">
              <el-breadcrumb-item>首页</el-breadcrumb-item>
              <el-breadcrumb-item>多模态输入收集</el-breadcrumb-item>
            </el-breadcrumb>
          </div>
          <div class="header-actions">
            <el-button type="text" @click="refreshPage">
              <el-icon><Refresh /></el-icon>
            </el-button>
            <el-button type="text" @click="toggleFullscreen">
              <el-icon><FullScreen /></el-icon>
            </el-button>
          </div>
        </div>
        
        <div class="content-body">
          <StudentManagementComponent v-if="activeItem === 'scores'" />
          <ScoreManagementComponent v-else-if="activeItem === 'ranking'" />
          <TeachingRecordComponent v-else-if="activeItem === 'review'" />
          <CollectComponent 
            v-else
            @uploadSuccess="handleUploadSuccess"
            @uploadError="handleUploadError"
          />
        </div>
        
        <AppFooter :lastUpdateTime="lastUpdateTime" />
        
      </div>
    </div>
  </template>
  
  <script setup lang="ts">
  import { ref, reactive, computed, onMounted } from 'vue'
  import { useRouter } from 'vue-router'
  import { ElMessage, ElMessageBox } from 'element-plus'
  import AppFooter from '@/components/AppFooter.vue'
  import CollectComponent from '@/components/CollectComponent.vue'
  import StudentManagementComponent from '@/components/StudentManagementComponent.vue'
  import ScoreManagementComponent from '../components/ScoreManagementComponent.vue'
  import TeachingRecordComponent from '../components/TeachingRecordComponent.vue'
  import {
    User, School, Postcard, Phone, Message,
    Reading, Notebook, View, TrendCharts, DataAnalysis,
    Trophy, Promotion, Setting, Bell, SwitchButton,
    Refresh, FullScreen
  } from '@element-plus/icons-vue'
  
  const router = useRouter()
  const activeItem = ref('quiz')
  
  // 用户信息数据，初始为空
  const userInfo = reactive({
    userName: '',
    realName: '',
    email: '',
    phone: '',
    status: '',
    password: '',
    userType: '',
    // 页面展示用
    avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
    class: '', // 办公室
    studentId: '', // 工号
  })

  // 页面挂载时从localStorage获取用户信息
  onMounted(() => {
    const userStr = localStorage.getItem('currentUser')
    if (userStr) {
      try {
        const user = JSON.parse(userStr)
        userInfo.userName = user.userName || ''
        userInfo.realName = user.realName || ''
        userInfo.email = user.email || ''
        userInfo.phone = user.phone || ''
        userInfo.status = user.status || ''
        userInfo.password = user.password || ''
        userInfo.userType = user.userType || ''
        // 下面字段如有后端返回可直接赋值，否则留空
        userInfo.class = user.class || ''
        userInfo.studentId = user.userName || '' // 工号用userName
        // 可选：头像
        userInfo.avatar = user.avatar || userInfo.avatar
      } catch {}
    }
  })
  
  // 当前页面标题
  const currentPageTitle = computed(() => {
    const titleMap: Record<string, string> = {
      'quiz': '在线答题',
      'Collection': '模式选择',
      'review': '错题回顾',
      'orator_result': '练习结果',
      'scores': '成绩查询',
      'ranking': '排行榜',
      'progress': '学习进度',
      'changeInfo': '个人设置',
      'notifications': '通知设置'
    }
    return titleMap[activeItem.value] || '个人中心'
  })
  
  // 最后更新时间
  const lastUpdateTime = ref('2024-07-14 14:30')
  
  // 导航方法
  const navigateTo = (route: string) => {
    if (route === 'changeInfo') {
      router.push('/changeInfo')
      return
    }
    activeItem.value = route
    // router.push(`/${route}`) // 移除路由跳转
    ElMessage.success(`正在跳转到${currentPageTitle.value}`)
  }
  
  // 刷新页面
  const refreshPage = () => {
    ElMessage.success('页面已刷新')
    // 这里可以添加刷新逻辑
  }
  
  // 全屏切换
  const toggleFullscreen = () => {
    if (!document.fullscreenElement) {
      document.documentElement.requestFullscreen()
      ElMessage.success('已进入全屏模式')
    } else {
      document.exitFullscreen()
      ElMessage.success('已退出全屏模式')
    }
  }

  // 退出登录
  const handleLogout = () => {
    ElMessageBox.confirm(
      '确定要退出登录吗？',
      '退出确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
    ).then(() => {
      localStorage.removeItem('isLoggedIn')
      localStorage.removeItem('currentUser')
      ElMessage.success('已安全退出')
      router.push('/login')
    }).catch(() => {
      ElMessage.info('已取消退出')
    })
  }

  // 处理文件上传成功
  const handleUploadSuccess = (data: { type: string; file: File; response: any }) => {
    console.log('文件上传成功:', data)
    ElMessage.success(`文件 ${data.file.name} 上传成功！`)
  }

  // 处理文件上传错误
  const handleUploadError = (data: { error: any; file: File }) => {
    console.error('文件上传失败:', data)
    ElMessage.error(`文件 ${data.file.name} 上传失败！`)
  }
  </script>
  
  <style scoped>
  .profile-container {
    display: flex;
    min-height: 100vh;
    background: #f0f2f5;
    font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
  }
  
  /* 左侧边栏 */
  .profile-sidebar {
    width: 320px;
    background: linear-gradient(180deg, #667eea 0%, #764ba2 100%);
    padding: 20px;
    overflow-y: auto;
    box-shadow: 2px 0 8px rgba(0, 0, 0, 0.1);
    display: flex;
    flex-direction: column;
  }
  
  /* 用户信息卡片 */
  .user-info-card {
    background: rgba(255, 255, 255, 0.95);
    border-radius: 16px;
    padding: 25px;
    margin-bottom: 20px;
    backdrop-filter: blur(10px);
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  }
  
  .avatar-section {
    text-align: center;
    margin-bottom: 20px;
    position: relative;
  }
  
  .avatar-section .el-avatar {
    border: 4px solid #fff;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  }
  
  .online-status {
    position: absolute;
    bottom: 5px;
    right: calc(50% - 35px);
    width: 12px;
    height: 12px;
    background: #52c41a;
    border: 2px solid #fff;
    border-radius: 50%;
  }
  
  .user-details {
    text-align: center;
    margin-bottom: 25px;
  }
  
  .user-name {
    font-size: 20px;
    font-weight: 600;
    color: #1a1a1a;
    margin: 0 0 5px 0;
  }
  
  .user-role {
    color: #666;
    margin: 0;
    font-size: 14px;
  }
  
  .info-list {
    margin-bottom: 25px;
  }
  
  .info-item {
    display: flex;
    align-items: center;
    padding: 12px 0;
    border-bottom: 1px solid #f0f0f0;
  }
  
  .info-item:last-child {
    border-bottom: none;
  }
  
  .info-icon {
    color: #667eea;
    margin-right: 12px;
    font-size: 16px;
  }
  
  .info-content {
    flex: 1;
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  
  .info-label {
    color: #666;
    font-size: 14px;
  }
  
  .info-value {
    color: #1a1a1a;
    font-weight: 500;
    font-size: 14px;
  }
  
  .user-stats {
    display: flex;
    justify-content: space-around;
    padding-top: 20px;
    border-top: 1px solid #f0f0f0;
  }
  
  .stat-item {
    text-align: center;
  }
  
  .stat-number {
    font-size: 18px;
    font-weight: 700;
    color: #667eea;
    margin-bottom: 4px;
  }
  
  .stat-label {
    font-size: 12px;
    color: #666;
  }
  
  /* 功能菜单 */
  .function-menu {
    background: rgba(255, 255, 255, 0.95);
    border-radius: 16px;
    padding: 20px;
    backdrop-filter: blur(10px);
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
    flex: 1;
    margin-bottom: 20px;
  }
  
  .menu-title {
    color: #1a1a1a;
    font-size: 16px;
    font-weight: 600;
    margin: 0 0 20px 0;
    padding-bottom: 10px;
    border-bottom: 2px solid #667eea;
  }
  
  .menu-section {
    margin-bottom: 25px;
  }
  
  .menu-section:last-child {
    margin-bottom: 0;
  }
  
  .section-header {
    display: flex;
    align-items: center;
    color: #667eea;
    font-weight: 600;
    margin-bottom: 12px;
    font-size: 14px;
  }
  
  .section-header .el-icon {
    margin-right: 8px;
  }
  
  .menu-items {
    margin-left: 8px;
  }
  
  .menu-item {
    display: flex;
    align-items: center;
    padding: 12px 16px;
    margin-bottom: 6px;
    border-radius: 8px;
    cursor: pointer;
    transition: all 0.3s ease;
    position: relative;
  }
  
  .menu-item:hover {
    background: #f8faff;
    transform: translateX(4px);
  }
  
  .menu-item.active {
    background: linear-gradient(135deg, #667eea, #764ba2);
    color: white;
    box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
  }
  
  .menu-item .el-icon {
    margin-right: 12px;
    font-size: 16px;
  }
  
  .menu-item span {
    flex: 1;
    font-size: 14px;
  }
  
  .item-badge {
    margin-left: auto;
  }
  
  /* 左侧页脚 */
  .sidebar-footer {
    background: rgba(255, 255, 255, 0.95);
    border-radius: 16px;
    padding: 16px;
    backdrop-filter: blur(10px);
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
    margin-top: auto;
  }
  
  .sidebar-footer-content {
    text-align: center;
  }
  
  .sidebar-footer-content p {
    margin: 4px 0;
    font-size: 11px;
    color: #666;
    line-height: 1.3;
  }
  
  /* 主内容区域 */
  .main-content {
    flex: 1;
    display: flex;
    flex-direction: column;
    background: #fff;
    margin: 20px 20px 20px 0;
    border-radius: 12px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    overflow: hidden;
  }
  
  .content-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 20px 30px;
    background: #fafbfc;
    border-bottom: 1px solid #e8eaec;
  }
  
  .breadcrumb .el-breadcrumb {
    font-size: 14px;
  }
  
  .header-actions {
    display: flex;
    gap: 8px;
  }
  
  .content-body {
    flex: 1;
    padding: 30px;
    overflow-y: auto;
    min-height: 0; /* 重要：确保内容区域能够正确计算高度 */
  }
  
  /* 右侧页脚 - 与内容区域风格一致 */
  .main-footer {
    background: #fafbfc;
    border-top: 1px solid #e8eaec;
    padding: 20px 30px;
    border-radius: 0 0 12px 12px;
    margin-top: auto;
  }
  
  .main-footer-content {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  
  .footer-left p {
    margin: 0;
    font-size: 12px;
    color: #666;
    line-height: 1.4;
  }
  
  .footer-right {
    display: flex;
    align-items: center;
  }
  
  .footer-links {
    display: flex;
    align-items: center;
    gap: 8px;
  }
  
  .footer-link {
    font-size: 12px;
    color: #666 !important;
    transition: color 0.3s ease;
  }
  
  .footer-link:hover {
    color: #667eea !important;
  }
  
  .footer-links .el-divider {
    margin: 0 8px;
    border-color: #e8eaec;
  }
  
  /* 响应式设计 */
  @media (max-width: 1024px) {
    .profile-sidebar {
      width: 280px;
    }
    
    .main-footer-content {
      flex-direction: column;
      gap: 12px;
      text-align: center;
    }
  }
  
  @media (max-width: 768px) {
    .profile-container {
      flex-direction: column;
    }
    
    .profile-sidebar {
      width: 100%;
      position: relative;
    }
    
    .main-content {
      margin: 0;
      border-radius: 0;
    }
    
    .main-footer {
      border-radius: 0;
    }
    
    .main-footer-content {
      flex-direction: column;
      gap: 10px;
      text-align: center;
    }
  }
  
  /* 滚动条样式 */
  .profile-sidebar::-webkit-scrollbar {
    width: 6px;
  }
  
  .profile-sidebar::-webkit-scrollbar-track {
    background: rgba(255, 255, 255, 0.1);
    border-radius: 3px;
  }
  
  .profile-sidebar::-webkit-scrollbar-thumb {
    background: rgba(255, 255, 255, 0.3);
    border-radius: 3px;
  }
  
  .profile-sidebar::-webkit-scrollbar-thumb:hover {
    background: rgba(255, 255, 255, 0.5);
  }
  </style>
  