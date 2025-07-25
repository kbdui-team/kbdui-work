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
            <p class="user-role">{{ userInfo.userType === 'admin' ? '管理员' : '学生' }}</p>
          </div>
          
          <div class="info-list">
            <!-- <div class="info-item">
              <el-icon class="info-icon"><School /></el-icon>
              <div class="info-content">
                <span class="info-label">班级</span>
                <span class="info-value">{{ userInfo.class }}</span>
              </div>
            </div> -->
            <div class="info-item">
              <el-icon class="info-icon"><Postcard /></el-icon>
              <div class="info-content">
                <span class="info-label">学号</span>
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
            
            <!-- <div class="info-item">
              <el-icon class="info-icon"><Calendar /></el-icon>
              <div class="info-content">
                <span class="info-label">入学时间</span>
                <span class="info-value">{{ userInfo.enrollmentDate }}</span>
              </div>
            </div> -->
          </div>
          
          <div class="user-stats">
            <div class="stat-item">
              <div class="stat-number">{{ stat.answeredCount }}</div>
              <div class="stat-label">已完成题数</div>
            </div>
            <div class="stat-item">
              <div class="stat-number">{{ stat.accuracy }}%</div>
              <div class="stat-label">正确率</div>
            </div>
            <div class="stat-item">
              <div class="stat-number">{{ stat.totalCount }}</div>
              <div class="stat-label">总题数</div>
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
                :class="{ active: activeItem === 'quiz' }"
                @click="() => { showquiz = true; activeItem = 'quiz'; }"
              >
                <el-icon><EditPen /></el-icon>
                <span>在线答题</span>
                <el-badge :value="quizCount" class="item-badge" />
              </div>

        

              <div 
                class="menu-item"
                :class="{ active: activeItem === 'review' }"
                @click="() => { activeItem = 'review'; }"
              >
                <el-icon><View /></el-icon>
                <span>错题回顾</span>
              </div>

              <!-- <div 
                class="menu-item"
                :class="{ active: activeItem === 'orator_result' }"
                @click="navigateTo('orator_result')"
              >
                <el-icon><View /></el-icon>
                <span>练习结果</span>
              </div> -->
            </div>
          </div>
          
          <!-- 成绩模块 -->
          <div class="menu-section">
            <div class="section-header">
              <el-icon><TrendCharts /></el-icon>
              <span>成绩模块</span>
            </div>
            <div class="menu-items">
              <!-- <div 
                class="menu-item"
                :class="{ active: activeItem === 'scores' }"
                @click="navigateTo('scores')"
              >
                <el-icon><DataAnalysis /></el-icon>
                <span>成绩查询</span>
              </div> -->
              <div 
                class="menu-item"
                :class="{ active: activeItem === 'ranking' }"
                @click="() => { activeItem = 'ranking'; }"
              >
                <el-icon><Trophy /></el-icon>
                <span>排行榜</span>
              </div>
              <div 
                class="menu-item"
                :class="{ active: activeItem === 'progress' }"
                @click="() => { activeItem = 'progress'; }"
              >
                <el-icon><Promotion /></el-icon>
                <span>学习进度</span>
              </div>
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
              <!-- <div 
                class="menu-item"
                :class="{ active: activeItem === 'notifications' }"
                @click="navigateTo('notifications')"
              >
                <el-icon><Bell /></el-icon>
                <span>通知设置</span>
              </div> -->
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
            <el-breadcrumb-item>{{ currentPageTitle }}</el-breadcrumb-item>
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
        <!-- 显示答题组件或错题回顾 -->
        <component 
          :is="QuizComponent.default" 
          v-if="activeItem === 'quiz'"
          :quiz-data="currentQuizData"
          @back-to-home="handleBackToHome"
          @quiz-complete="handleQuizComplete"
        />
        <component :is="WrongReview.default" v-else-if="activeItem === 'review'" :student-id="userInfo.studentId" :lecture-id="0" />
        <component :is="Ranking.default" v-else-if="activeItem === 'ranking'" />
        <component :is="StudyProgress.default" v-else-if="activeItem === 'progress'" />
        <router-view v-else />
      </div>
    
      </div>
    </div>
    <AppFooter :lastUpdateTime="lastUpdateTime" />
  </template>
  
  <script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import AppFooter from '@/components/AppFooter.vue'
import {
  User, School, Postcard, Phone, Message, Calendar,
  Reading, EditPen, Notebook, View, TrendCharts, DataAnalysis,
  Trophy, Promotion, Setting, Bell, SwitchButton,
  Refresh, FullScreen
} from '@element-plus/icons-vue'

import * as QuizComponent from '../components/QuizComponent.vue' // 引入答题组件
import * as WrongReview from './WrongReview.vue'
import * as Ranking from './Ranking.vue'
import * as StudyProgress from './StudyProgress.vue'
import axios from 'axios'
import request from '@/utils/request'

const router = useRouter()
const activeItem = ref('quiz')
const showquiz = ref(true)
  
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
  enrollmentDate: '',
  class: '',
  studentId: '',
  stats: {
    completedQuizzes: 0,
    averageScore: 0,
    rank: 0
  }
})

// 新增：可答题目数量
const quizCount = ref(0)

// 获取可答题目数量
async function fetchQuizCount() {
  try {
    const userStr = localStorage.getItem('currentUser')
    let userId = null
    if (userStr) {
      try {
        const user = JSON.parse(userStr)
        userId = user.id
      } catch {}
    }
    // 获取所有题目和所有答题记录
    const [qRes, aRes] = await Promise.all([
      axios.get('/question/list'),
      axios.get('/answerHistory/list')
    ])
    const qList = Array.isArray(qRes.data) ? qRes.data : []
    const aList = Array.isArray(aRes.data) ? aRes.data : []
    let answeredIds = []
    if (userId !== null) {
      answeredIds = aList.filter((item: any) => item.userId === userId).map((item: any) => item.questionId)
    }
    quizCount.value = qList.filter((q: any) => !answeredIds.includes(q.id)).length
  } catch (e) {
    quizCount.value = 0
  }
}

// 新增：动态统计学生数据
async function fetchStudentStats() {
  try {
    const userStr = localStorage.getItem('currentUser')
    let userId = null
    let userClass = ''
    if (userStr) {
      try {
        const user = JSON.parse(userStr)
        userId = user.id
        userClass = user.class || ''
      } catch {}
    }
    if (!userId) return
    // 获取所有答题记录和所有用户
    const [aRes, uRes] = await Promise.all([
      axios.get('/answerHistory/list'),
      axios.get('/user/list')
    ])
    const aList = Array.isArray(aRes.data) ? aRes.data : []
    const uList = Array.isArray(uRes.data) ? uRes.data : []
    // 1. 本人答题记录
    const myAnswers = aList.filter((item: any) => item.userId === userId)
    // 2. 已完成测试数
    userInfo.stats.completedQuizzes = myAnswers.length
    // 3. 平均得分（假设 isCorrect 字段 1为对 0为错，每题100分）
    if (myAnswers.length > 0) {
      const correctCount = myAnswers.filter((item: any) => item.isCorrect === 1).length
      userInfo.stats.averageScore = Math.round((correctCount / myAnswers.length) * 100)
    } else {
      userInfo.stats.averageScore = 0
    }
    // 4. 班级排名（按平均得分降序）
    const classmates = uList.filter((u: any) => u.class === userClass && u.userType === 'student')
    const classStats = classmates.map((stu: any) => {
      const stuAnswers = aList.filter((item: any) => item.userId === stu.id)
      const correct = stuAnswers.filter((item: any) => item.isCorrect === 1).length
      const avg = stuAnswers.length > 0 ? correct / stuAnswers.length : 0
      return { id: stu.id, avg }
    })
    classStats.sort((a, b) => b.avg - a.avg)
    const myRank = classStats.findIndex(s => s.id === userId)
    userInfo.stats.rank = myRank >= 0 ? myRank + 1 : 0
  } catch {}
}

const stat = ref({
  answeredCount: 0,
  correctCount: 0,
  totalCount: 0,
  accuracy: 0
})

function getCurrentUserId() {
  try {
    const userStr = localStorage.getItem('currentUser')
    if (userStr) {
      const user = JSON.parse(userStr)
      return user.id
    }
  } catch {}
  return null
}

async function fetchStat() {
  const userId = getCurrentUserId()
  if (!userId) return
  const res = await request.get(`/answerHistory/student/${userId}/lecture/0/stat`)
  const data = res.data
  stat.value.answeredCount = data.answeredCount || 0
  stat.value.correctCount = data.correctCount || 0
  stat.value.totalCount = data.totalCount || 0
  stat.value.accuracy = data.answeredCount ? Math.round((data.correctCount / data.answeredCount) * 100) : 0
}

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
      userInfo.studentId = String(user.id || '')
      userInfo.enrollmentDate = user.enrollmentDate || ''
      // 可选：头像
      userInfo.avatar = user.avatar || userInfo.avatar
      // 可选：统计数据
      if (user.stats) {
        userInfo.stats.completedQuizzes = user.stats.completedQuizzes || 0
        userInfo.stats.averageScore = user.stats.averageScore || 0
        userInfo.stats.rank = user.stats.rank || 0
      }
    } catch {}
  }
  fetchQuizCount()
  fetchStudentStats()
  fetchStat()
})
  
  // 当前答题数据
const currentQuizData = ref({
  title: 'JavaScript 基础知识测试',
  category: '前端开发',
  difficulty: 'medium',
  questions: [
    {
      question: '以下哪个方法可以向数组末尾添加元素？',
      options: ['push()', 'pop()', 'shift()', 'unshift()'],
      correct: 0,
      explanation: 'push() 方法将一个或多个元素添加到数组的末尾，并返回该数组的新长度。',
      image: null
    },
    {
      question: 'JavaScript 中 == 和 === 的区别是什么？',
      options: [
        '没有区别',
        '== 比较值，=== 比较值和类型',
        '== 比较类型，=== 比较值',
        '=== 性能更差'
      ],
      correct: 1,
      explanation: '== 会进行类型转换后比较，而 === 会同时比较值和类型，不进行类型转换。'
    },
    {
      question: '以下哪个不是 JavaScript 的基本数据类型？',
      options: ['string', 'number', 'array', 'boolean'],
      correct: 2,
      explanation: 'array 是引用数据类型，不是基本数据类型。JavaScript 的基本数据类型有：string、number、boolean、null、undefined、symbol、bigint。'
    }
  ]
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
  const navigateTo = (item: string) => {
  activeItem.value = item
  showquiz.value = false
  
  // 根据不同的选项进行路由跳转或操作
  const routeMap: Record<string, string> = {
    'Collection': '/Collection',
    'orator_result': '/orator_result',
    'changeInfo': '/changeInfo'
  }
  
  if (routeMap[item]) {
    router.push(routeMap[item])
  } else {
    ElMessage.info(`切换到${currentPageTitle.value}`)
  }
}
const handleBackToHome = () => {
  showquiz.value = false
  activeItem.value = 'home'
  ElMessage.info('返回首页')
}

const handleQuizComplete = (results: any) => {
  console.log('答题完成，结果：', results)
  
  // 更新用户统计数据
  userInfo.stats.completedQuizzes++
  userInfo.stats.averageScore = Math.round(
    (userInfo.stats.averageScore * (userInfo.stats.completedQuizzes - 1) + 
     (results.score / (results.totalQuestions * 100)) * 100) / 
    userInfo.stats.completedQuizzes
  )
  
  ElMessage.success(`测试完成！得分：${results.score}，等级：${results.grade}`)
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
  