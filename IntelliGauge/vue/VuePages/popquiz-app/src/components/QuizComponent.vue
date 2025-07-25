<template>
    <div class="quiz-component">
      <!-- 课程选择页面 -->
      <div class="lecture-selection" v-if="!selectedLecture && !isQuizComplete">
        <div class="selection-card">
          <div class="selection-header">
            <h2>选择课程</h2>
            <p>请选择您要练习的课程</p>
          </div>
          
          <div class="lecture-grid" v-loading="loadingLectures">
            <div
              v-for="lecture in lectureList"
              :key="lecture.id"
              class="lecture-card"
              :class="{ 'inactive': lecture.status !== 'active' }"
              @click="selectLecture(lecture)"
            >
              <div class="lecture-header">
                <h3>{{ lecture.title }}</h3>
                <el-tag :type="lecture.status === 'active' ? 'success' : 'info'" size="small">
                  {{ lecture.status === 'active' ? '可用' : '暂停' }}
                </el-tag>
              </div>
              <p class="lecture-description">{{ lecture.description }}</p>
              <div class="lecture-stats">
                <span class="question-count">
                  <el-icon><Document /></el-icon>
                  {{ getLectureQuestionCount(lecture.id) }} 题
                </span>
                <span class="teacher-info">
                  <el-icon><User /></el-icon>
                  讲师ID: {{ lecture.teacherId }}
                </span>
              </div>
            </div>
          </div>
          
          <div class="selection-actions">
            <el-button @click="$emit('backToHome')">返回首页</el-button>
          </div>
        </div>
      </div>

      <!-- 答题界面 -->
      <div v-else-if="selectedLecture && !isQuizComplete">
        <!-- 头部信息栏 -->
        <div class="quiz-header">
          <div class="quiz-info">
            <div class="quiz-title-row">
              <div class="quiz-title">{{ selectedLecture.title }}</div>
              <el-button size="small" @click="backToLectureSelection">
                <el-icon><Back /></el-icon>
                重新选择课程
              </el-button>
            </div>
            <div class="quiz-meta">
              <span class="question-count">{{ currentQuestionIndex + 1 }}/{{ questions.length }}</span>
              <span class="category">{{ selectedLecture.description }}</span>
              <span class="difficulty" :class="currentQuiz.difficulty">{{ getDifficultyText(currentQuiz.difficulty) }}</span>
            </div>
          </div>
          <div class="quiz-controls">
            <div class="timer" :class="{ 'time-warning': timeLeft <= 10 }">
              <el-icon><Clock /></el-icon>
              <span>{{ formatTime(timeLeft) }}</span>
            </div>
            <div class="score">
              <el-icon><Trophy /></el-icon>
              <span>{{ score }}</span>
            </div>
          </div>
        </div>
        
        <!-- 进度条 -->
        <div class="progress-bar">
          <div 
            class="progress-fill" 
            :style="{ width: ((currentQuestionIndex + 1) / questions.length) * 100 + '%' }"
          ></div>
        </div>

        <!-- 答题区域 -->
        <div class="quiz-content">
          <div v-if="questions.length > 0 && currentQuestion && currentQuestion.question && Array.isArray(currentQuestion.options) && currentQuestion.options.length > 0">
            <div class="question-card">
              <div class="question-header">
                <h3 class="question-text">{{ currentQuestion.question }}</h3>
                <div class="question-timer">
                  <el-progress 
                    type="circle" 
                    :percentage="Number(((timeLeft / questionTimeLimit) * 100).toFixed(2))" 
                    :width="60"
                    :stroke-width="6"
                    :color="getTimerColor()"
                  />
                </div>
              </div>
              <div class="question-image" v-if="currentQuestion.image">
                <img :src="currentQuestion.image" :alt="currentQuestion.question" />
              </div>
              <div class="options-container">
                <div
                  v-for="(option, index) in currentQuestion.options"
                  :key="index"
                  class="option-item"
                  :class="{
                    'selected': selectedAnswer === index,
                    'correct': showAnswer && index === currentQuestion.correct,
                    'incorrect': showAnswer && selectedAnswer === index && index !== currentQuestion.correct,
                    'disabled': showAnswer
                  }"
                  @click="selectAnswer(index)"
                >
                  <div class="option-label">{{ String.fromCharCode(65 + index) }}</div>
                  <div class="option-text">{{ option }}</div>
                  <div class="option-indicator" v-if="showAnswer">
                    <el-icon v-if="index === currentQuestion.correct" class="correct-icon">
                      <Check />
                    </el-icon>
                    <el-icon v-else-if="selectedAnswer === index" class="incorrect-icon">
                      <Close />
                    </el-icon>
                  </div>
                </div>
              </div>
              <div class="explanation" v-if="showAnswer && currentQuestion.explanation">
                <div class="explanation-header">
                  <el-icon><InfoFilled /></el-icon>
                  <span>解释</span>
                </div>
                <p>{{ currentQuestion.explanation }}</p>
              </div>
              <div class="quiz-actions">
                <el-button 
                  v-if="!showAnswer" 
                  type="primary" 
                  :disabled="selectedAnswer === null"
                  @click="submitAnswer"
                >
                  提交答案
                </el-button>
                <el-button 
                  v-else 
                  type="success" 
                  @click="nextQuestion"
                >
                  {{ currentQuestionIndex < questions.length - 1 ? '下一题' : '完成测试' }}
                </el-button>
              </div>
            </div>
          </div>
          <div v-else-if="loadingQuestions" class="loading-container">
            <el-icon class="loading-icon"><Loading /></el-icon>
            <p>正在加载题目...</p>
          </div>
          <div v-else class="no-questions">
            <el-icon class="empty-icon"><DocumentDelete /></el-icon>
            <h3>暂无题目</h3>
            <p>该课程暂未添加题目，请联系管理员或选择其他课程</p>
            <el-button type="primary" @click="backToLectureSelection">重新选择课程</el-button>
          </div>
        </div>
      </div>

      <!-- 结果页面 -->
      <div class="quiz-results" v-else-if="isQuizComplete">
        <div class="results-card">
          <div class="results-header">
            <div class="results-icon">
              <el-icon v-if="getGrade() === 'A'" class="grade-excellent"><Trophy /></el-icon>
              <el-icon v-else-if="getGrade() === 'B'" class="grade-good"><Medal /></el-icon>
              <el-icon v-else class="grade-fair"><Star /></el-icon>
            </div>
            <h2>《{{ selectedLecture.title }}》测试完成！</h2>
            <div class="final-score">
              <span class="score-number">{{ score }}</span>
              <span class="score-total">/ {{ questions.length * 100 }}</span>
            </div>
            <div class="grade-badge" :class="getGrade().toLowerCase()">
              {{ getGradeText() }}
            </div>
          </div>

          <div class="results-stats">
            <div class="stat-item">
              <div class="stat-label">正确率</div>
              <div class="stat-value">{{ Math.round((correctAnswers / questions.length) * 100) }}%</div>
            </div>
            <div class="stat-item">
              <div class="stat-label">用时</div>
              <div class="stat-value">{{ formatTime(totalTime - timeLeft) }}</div>
            </div>
            <div class="stat-item">
              <div class="stat-label">正确题数</div>
              <div class="stat-value">{{ correctAnswers }}/{{ questions.length }}</div>
            </div>
          </div>

          <div class="results-actions">
            <el-button type="primary" @click="restartQuiz">重新测试</el-button>
            <el-button @click="backToLectureSelection">选择其他课程</el-button>
            <el-button @click="$emit('backToHome')">返回首页</el-button>
            <el-button type="success" @click="shareResults">分享结果</el-button>
          </div>
        </div>
      </div>
    </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, onUnmounted, inject } from 'vue'
import { ElMessage } from 'element-plus'
import { 
  Clock, Trophy, Check, Close, InfoFilled, Medal, Star, 
  Document, User, Back, Loading, DocumentDelete
} from '@element-plus/icons-vue'
import axios from 'axios'

// 定义组件事件
const emit = defineEmits(['backToHome', 'quizComplete'])

// 课程相关数据
const selectedLecture = ref(null)
const lectureList = ref([])
const loadingLectures = ref(false)
const questionCountMap = ref(new Map()) // 存储每个课程的题目数量

// 答题状态数据
const currentQuestionIndex = ref(0)
const selectedAnswer = ref<number | null>(null)
const showAnswer = ref(false)
const score = ref(0)
const correctAnswers = ref(0)
const timeLeft = ref(30)
const totalTime = ref(0)
const isQuizComplete = ref(false)
const timer = ref<NodeJS.Timeout | null>(null)
const questionTimeLimit = 30

const currentQuiz = reactive({
  title: '在线答题',
  category: '',
  difficulty: 'medium'
})

// 题目和选项数据
const questions = ref<any[]>([])
const loadingQuestions = ref(false)

const baseurl = inject('baseurl') as string

// 获取课程列表
const fetchLectures = async () => {
  loadingLectures.value = true
  try {
    const response = await axios.get(`${baseurl}/lecture/list`)
    if (Array.isArray(response.data)) {
      lectureList.value = response.data
      // 获取每个课程的题目数量
      await fetchQuestionCounts()
    } else {
      ElMessage.error('获取课程列表失败')
      lectureList.value = []
    }
  } catch (error) {
    console.error('获取课程列表失败:', error)
    ElMessage.error('获取课程列表失败')
    lectureList.value = []
  } finally {
    loadingLectures.value = false
  }
}

// 获取每个课程的题目数量
const fetchQuestionCounts = async () => {
  try {
    const response = await axios.get(`${baseurl}/question/list`)
    if (Array.isArray(response.data)) {
      const questionCounts = new Map()
      response.data.forEach(question => {
        const lectureId = question.lectureId || question.lecture_id
        if (lectureId) {
          questionCounts.set(lectureId, (questionCounts.get(lectureId) || 0) + 1)
        }
      })
      questionCountMap.value = questionCounts
    }
  } catch (error) {
    console.error('获取题目数量失败:', error)
  }
}

// 获取课程题目数量
const getLectureQuestionCount = (lectureId) => {
  return questionCountMap.value.get(lectureId) || 0
}

// 选择课程
const selectLecture = (lecture) => {
  if (lecture.status !== 'active') {
    ElMessage.warning('该课程暂时不可用')
    return
  }
  
  selectedLecture.value = lecture
  currentQuiz.title = lecture.title
  currentQuiz.category = lecture.description
  
  // 获取该课程的题目
  fetchQuestionsByLecture(lecture.id)
}

// 返回课程选择页面
const backToLectureSelection = () => {
  selectedLecture.value = null
  isQuizComplete.value = false
  resetQuizState()
  stopTimer()
}

// 重置答题状态
const resetQuizState = () => {
  currentQuestionIndex.value = 0
  selectedAnswer.value = null
  showAnswer.value = false
  score.value = 0
  correctAnswers.value = 0
  questions.value = []
}

// 根据课程获取题目
const fetchQuestionsByLecture = async (lectureId) => {
  loadingQuestions.value = true
  try {
    // 1. 获取已答题记录
    let answeredIds: number[] = []
    const userId = getCurrentUserId()
    if (userId !== null) {
      try {
        const answerRes = await axios.get(`${baseurl}/answerHistory/list`)
        if (Array.isArray(answerRes.data)) {
          answeredIds = answerRes.data
            .filter((item: any) => item.userId === userId)
            .map((item: any) => item.questionId)
        }
      } catch (e) {
        console.error('获取答题记录失败', e)
      }
    }

    // 2. 获取指定课程的题目和选项
    const ts = Date.now()
    const qUrl = `${baseurl}/question/list?t=${ts}`
    const oUrl = `${baseurl}/question-options/list?t=${ts}`
    
    const [qRes, oRes] = await Promise.all([
      axios.get(qUrl),
      axios.get(oUrl)
    ])
    
    const qList = Array.isArray(qRes.data) ? qRes.data : []
    const oList = Array.isArray(oRes.data) ? oRes.data : []
    
    if (!qList.length || !oList.length) {
      ElMessage.error('题目或选项数据格式错误')
      questions.value = []
      return
    }

    // 3. 过滤出指定课程的题目
    const lectureQuestions = qList.filter((q: any) => {
      const qLectureId = q.lectureId || q.lecture_id
      return qLectureId && qLectureId == lectureId
    })

    if (lectureQuestions.length === 0) {
      ElMessage.warning('该课程暂无题目')
      questions.value = []
      return
    }

    // 4. 过滤掉已答过的题目
    const filteredQuestions = userId !== null ? 
      lectureQuestions.filter((q: any) => !answeredIds.includes(q.id)) : 
      lectureQuestions

    if (filteredQuestions.length === 0) {
      ElMessage.info('该课程的题目您都已经答过了')
      questions.value = []
      return
    }

    // 5. 合并题目和选项
    const optionMap = new Map()
    oList.forEach((opt: any) => {
      if (!optionMap.has(opt.questionId)) {
        optionMap.set(opt.questionId, [])
      }
      optionMap.get(opt.questionId).push(opt)
    })

    questions.value = filteredQuestions.map((q: any) => {
      const opts = optionMap.get(q.id) || []
      opts.sort((a: any, b: any) => (a.optionOrder > b.optionOrder ? 1 : -1))
      
      return {
        ...q,
        question: q.questionText || '',
        options: opts.map((opt: any) => opt.optionText),
        correct: opts.findIndex((opt: any) => String(opt.isCorrect).toLowerCase() === 'true'),
        explanation: q.explanation || '',
        image: q.image || null
      }
    })

    if (questions.value.length > 0) {
      ElMessage.success(`加载了 ${questions.value.length} 道题目，开始答题！`)
      startTimer()
    }

  } catch (error) {
    console.error('获取题目失败:', error)
    ElMessage.error('获取题目失败: ' + (error?.message || ''))
    questions.value = []
  } finally {
    loadingQuestions.value = false
  }
}

// 计算属性
const currentQuestion = computed(() => {
  return questions.value[currentQuestionIndex.value] || { 
    question: '', 
    options: [], 
    correct: -1, 
    explanation: '', 
    image: null 
  }
})

// 方法
const startTimer = () => {
  timeLeft.value = questionTimeLimit
  timer.value = setInterval(() => {
    timeLeft.value--
    if (timeLeft.value <= 0) {
      timeUp()
    }
  }, 1000)
}

const stopTimer = () => {
  if (timer.value) {
    clearInterval(timer.value)
    timer.value = null
  }
}

const timeUp = () => {
  stopTimer()
  if (!showAnswer.value) {
    ElMessage.warning('时间到！')
    submitAnswer()
  }
}

const selectAnswer = (index: number) => {
  if (showAnswer.value) return
  selectedAnswer.value = index
}

// 获取当前用户信息
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

const submitAnswer = async () => {
  if (selectedAnswer.value === null) {
    selectedAnswer.value = -1
  }

  stopTimer()
  showAnswer.value = true

  // 保存答题记录到数据库
  const userId = getCurrentUserId()
  if (userId !== null) {
    const answerHistoryDTO = {
      userId: userId,
      questionId: currentQuestion.value.id,
      userAnswer: selectedAnswer.value !== -1 ? String.fromCharCode(65 + selectedAnswer.value) : '',
      isCorrect: selectedAnswer.value === currentQuestion.value.correct ? 1 : 0,
      answerTime: Date.now(),
    }
    try {
      await axios.post(`${baseurl}/answerHistory/add`, answerHistoryDTO)
    } catch (e) {
      console.error('答题记录保存失败', e)
    }
  }

  if (selectedAnswer.value === currentQuestion.value.correct) {
    correctAnswers.value++
    const timeBonus = Math.max(0, timeLeft.value * 2)
    score.value += 100 + timeBonus
    ElMessage.success('回答正确！')
  } else {
    ElMessage.error('回答错误！')
  }
}

const nextQuestion = () => {
  if (currentQuestionIndex.value < questions.value.length - 1) {
    currentQuestionIndex.value++
    selectedAnswer.value = null
    showAnswer.value = false
    startTimer()
  } else {
    completeQuiz()
  }
}

const completeQuiz = () => {
  isQuizComplete.value = true
  stopTimer()
  ElMessage.success('测试完成！')
  
  emit('quizComplete', {
    score: score.value,
    correctAnswers: correctAnswers.value,
    totalQuestions: questions.value.length,
    grade: getGrade(),
    lectureName: selectedLecture.value?.title
  })
}

const getGrade = () => {
  const percentage = (correctAnswers.value / questions.value.length) * 100
  if (percentage >= 90) return 'A'
  if (percentage >= 80) return 'B'
  if (percentage >= 70) return 'C'
  if (percentage >= 60) return 'D'
  return 'F'
}

const getGradeText = () => {
  const grade = getGrade()
  const texts: Record<string, string> = {
    'A': '优秀',
    'B': '良好',
    'C': '一般',
    'D': '及格',
    'F': '不及格'
  }
  return texts[grade]
}

const getDifficultyText = (difficulty: string) => {
  const texts: Record<string, string> = {
    'easy': '简单',
    'medium': '中等',
    'hard': '困难'
  }
  return texts[difficulty]
}

const formatTime = (seconds: number) => {
  const mins = Math.floor(seconds / 60)
  const secs = seconds % 60
  return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
}

const getTimerColor = () => {
  const percentage = (timeLeft.value / questionTimeLimit) * 100
  if (percentage > 60) return '#67C23A'
  if (percentage > 30) return '#E6A23C'
  return '#F56C6C'
}

const restartQuiz = () => {
  if (selectedLecture.value) {
    resetQuizState()
    fetchQuestionsByLecture(selectedLecture.value.id)
  }
}

const shareResults = () => {
  const text = `我刚完成了"${selectedLecture.value?.title}"课程测试，得分${score.value}分，正确率${Math.round((correctAnswers.value / questions.value.length) * 100)}%！`
  
  if (navigator.share) {
    navigator.share({
      title: 'PopQuiz 测试结果',
      text: text,
      url: window.location.href
    })
  } else {
    navigator.clipboard.writeText(text).then(() => {
      ElMessage.success('结果已复制到剪贴板！')
    })
  }
}

// 生命周期
onMounted(() => {
  fetchLectures()
})

onUnmounted(() => {
  stopTimer()
})
</script>

<style scoped>
.quiz-component {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 20px;
  padding: 24px;
  height: calc(100vh - 200px);
  overflow-y: auto;
  position: relative;
}

.quiz-component::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><defs><pattern id="grain" width="100" height="100" patternUnits="userSpaceOnUse"><circle cx="25" cy="25" r="1" fill="white" fill-opacity="0.03"/><circle cx="75" cy="75" r="1" fill="white" fill-opacity="0.03"/><circle cx="50" cy="10" r="0.5" fill="white" fill-opacity="0.05"/><circle cx="20" cy="60" r="0.5" fill="white" fill-opacity="0.05"/><circle cx="80" cy="40" r="0.5" fill="white" fill-opacity="0.05"/></pattern></defs><rect width="100%" height="100%" fill="url(%23grain)"/></svg>');
  border-radius: 20px;
  pointer-events: none;
}

/* 课程选择页面样式 */
.lecture-selection {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 70vh;
  position: relative;
}

.selection-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 24px;
  padding: 48px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1), 0 0 0 1px rgba(255, 255, 255, 0.1);
  max-width: 1200px;
  width: 100%;
  animation: slideUp 0.6s cubic-bezier(0.16, 1, 0.3, 1);
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.selection-header {
  text-align: center;
  margin-bottom: 48px;
}

.selection-header h2 {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  margin-bottom: 16px;
  font-size: 36px;
  font-weight: 700;
  letter-spacing: -0.02em;
}

.selection-header p {
  color: #64748b;
  font-size: 18px;
  font-weight: 400;
  opacity: 0.8;
}

.lecture-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
  gap: 24px;
  margin-bottom: 40px;
}

.lecture-card {
  background: linear-gradient(145deg, #ffffff 0%, #f8fafc 100%);
  border: 1px solid rgba(255, 255, 255, 0.6);
  border-radius: 20px;
  padding: 32px;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.16, 1, 0.3, 1);
  position: relative;
  overflow: hidden;
}

.lecture-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #667eea, #764ba2, #f093fb, #f5576c);
  border-radius: 20px 20px 0 0;
  transform: scaleX(0);
  transition: transform 0.4s ease;
}

.lecture-card:hover:not(.inactive)::before {
  transform: scaleX(1);
}

.lecture-card:hover:not(.inactive) {
  transform: translateY(-8px) scale(1.02);
  box-shadow: 0 25px 50px rgba(102, 126, 234, 0.15), 0 0 0 1px rgba(102, 126, 234, 0.1);
  border-color: rgba(102, 126, 234, 0.3);
}

.lecture-card.inactive {
  opacity: 0.5;
  cursor: not-allowed;
  filter: grayscale(0.3);
}

.lecture-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}

.lecture-header h3 {
  color: #1e293b;
  font-size: 22px;
  font-weight: 600;
  margin: 0;
  flex: 1;
  line-height: 1.3;
}

.lecture-description {
  color: #64748b;
  font-size: 15px;
  line-height: 1.6;
  margin-bottom: 24px;
  min-height: 48px;
}

.lecture-stats {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 13px;
  color: #64748b;
  background: rgba(248, 250, 252, 0.8);
  padding: 12px 16px;
  border-radius: 12px;
  margin: -8px -8px 0 -8px;
}

.question-count,
.teacher-info {
  display: flex;
  align-items: center;
  gap: 6px;
  font-weight: 500;
}

.selection-actions {
  text-align: center;
}

/* 头部样式优化 */
.quiz-header {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  padding: 24px 32px;
  border-radius: 20px;
  margin-bottom: 24px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.quiz-title {
  font-size: 24px;
  font-weight: 700;
  background: linear-gradient(135deg, #1e293b 0%, #334155 100%);
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  margin-bottom: 8px;
}

.quiz-title-row {
  display: flex;
  align-items: center;
  gap: 20px;
  flex-wrap: wrap;
}

.quiz-meta {
  display: flex;
  gap: 20px;
  font-size: 14px;
  color: #64748b;
  font-weight: 500;
}

.quiz-meta .question-count {
  background: linear-gradient(135deg, #3b82f6, #1d4ed8);
  color: white;
  padding: 4px 12px;
  border-radius: 20px;
  font-weight: 600;
}

.difficulty {
  padding: 4px 12px;
  border-radius: 16px;
  font-size: 12px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.difficulty.easy { 
  background: linear-gradient(135deg, #10b981, #059669);
  color: white;
}
.difficulty.medium { 
  background: linear-gradient(135deg, #f59e0b, #d97706);
  color: white;
}
.difficulty.hard { 
  background: linear-gradient(135deg, #ef4444, #dc2626);
  color: white;
}

.quiz-controls {
  display: flex;
  gap: 24px;
  align-items: center;
}

.timer, .score {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  padding: 8px 16px;
  border-radius: 16px;
  font-size: 15px;
}

.timer {
  background: linear-gradient(135deg, #10b981, #059669);
  color: white;
}

.timer.time-warning {
  background: linear-gradient(135deg, #ef4444, #dc2626);
  animation: pulse 1s infinite;
}

@keyframes pulse {
  0%, 100% { 
    opacity: 1;
    transform: scale(1);
  }
  50% { 
    opacity: 0.9;
    transform: scale(1.05);
  }
}

.score {
  background: linear-gradient(135deg, #8b5cf6, #7c3aed);
  color: white;
}

/* 进度条优化 */
.progress-bar {
  width: 100%;
  height: 8px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 10px;
  overflow: hidden;
  margin-bottom: 32px;
  box-shadow: inset 0 2px 4px rgba(0, 0, 0, 0.1);
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #10b981, #3b82f6, #8b5cf6);
  border-radius: 10px;
  transition: width 0.6s cubic-bezier(0.16, 1, 0.3, 1);
  position: relative;
}

.progress-fill::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
  animation: shimmer 2s infinite;
}

@keyframes shimmer {
  0% { transform: translateX(-100%); }
  100% { transform: translateX(100%); }
}

/* 题目卡片优化 */
.question-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 24px;
  padding: 40px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  animation: slideUp 0.6s cubic-bezier(0.16, 1, 0.3, 1);
}

.question-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 32px;
}

.question-text {
  font-size: 22px;
  font-weight: 600;
  color: #1e293b;
  margin: 0;
  flex: 1;
  margin-right: 24px;
  line-height: 1.4;
}

.question-image {
  margin-bottom: 32px;
  text-align: center;
}

.question-image img {
  max-width: 100%;
  height: auto;
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

/* 选项样式大幅优化 */
.options-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-bottom: 32px;
}

.option-item {
  display: flex;
  align-items: center;
  padding: 20px 24px;
  border: 2px solid #e2e8f0;
  border-radius: 16px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.16, 1, 0.3, 1);
  position: relative;
  background: linear-gradient(145deg, #ffffff 0%, #f8fafc 100%);
}

.option-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, #3b82f6, #1d4ed8);
  border-radius: 14px;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.option-item:hover:not(.disabled)::before {
  opacity: 0.05;
}

.option-item:hover:not(.disabled) {
  border-color: #3b82f6;
  transform: translateX(4px);
  box-shadow: 0 8px 25px rgba(59, 130, 246, 0.15);
}

.option-item.selected {
  border-color: #3b82f6;
  background: linear-gradient(145deg, #eff6ff 0%, #dbeafe 100%);
  transform: translateX(8px);
  box-shadow: 0 12px 30px rgba(59, 130, 246, 0.2);
}

.option-item.correct {
  border-color: #10b981;
  background: linear-gradient(145deg, #f0fdf4 0%, #dcfce7 100%);
  animation: correctPulse 0.6s ease;
}

.option-item.incorrect {
  border-color: #ef4444;
  background: linear-gradient(145deg, #fef2f2 0%, #fee2e2 100%);
  animation: incorrectShake 0.6s ease;
}

@keyframes correctPulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.02); }
}

@keyframes incorrectShake {
  0%, 100% { transform: translateX(0); }
  25% { transform: translateX(-4px); }
  75% { transform: translateX(4px); }
}

.option-item.disabled {
  cursor: not-allowed;
}

.option-label {
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, #f1f5f9, #e2e8f0);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  margin-right: 20px;
  font-size: 16px;
  color: #64748b;
  transition: all 0.3s ease;
  position: relative;
  z-index: 1;
}

.option-item.selected .option-label {
  background: linear-gradient(135deg, #3b82f6, #1d4ed8);
  color: white;
  transform: scale(1.1);
}

.option-item.correct .option-label {
  background: linear-gradient(135deg, #10b981, #059669);
  color: white;
  transform: scale(1.1);
}

.option-item.incorrect .option-label {
  background: linear-gradient(135deg, #ef4444, #dc2626);
  color: white;
  transform: scale(1.1);
}

.option-text {
  flex: 1;
  font-size: 16px;
  color: #374151;
  font-weight: 500;
  line-height: 1.5;
  position: relative;
  z-index: 1;
}

.option-indicator {
  position: absolute;
  right: 20px;
  top: 50%;
  transform: translateY(-50%);
  z-index: 1;
}

.correct-icon {
  color: #10b981;
  font-size: 24px;
  animation: bounceIn 0.6s ease;
}

.incorrect-icon {
  color: #ef4444;
  font-size: 24px;
  animation: bounceIn 0.6s ease;
}

@keyframes bounceIn {
  0% { 
    opacity: 0;
    transform: translateY(-50%) scale(0.3);
  }
  50% { 
    transform: translateY(-50%) scale(1.1);
  }
  100% { 
    opacity: 1;
    transform: translateY(-50%) scale(1);
  }
}

/* 解释区域优化 */
.explanation {
  background: linear-gradient(145deg, #f8fafc 0%, #f1f5f9 100%);
  border-left: 4px solid #3b82f6;
  padding: 24px;
  margin-bottom: 32px;
  border-radius: 0 16px 16px 0;
  animation: slideDown 0.5s ease;
}

@keyframes slideDown {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.explanation-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  color: #3b82f6;
  margin-bottom: 12px;
  font-size: 16px;
}

.explanation p {
  margin: 0;
  color: #4b5563;
  line-height: 1.6;
  font-size: 15px;
}

/* 按钮优化 */
.quiz-actions {
  display: flex;
  justify-content: center;
  gap: 16px;
}

.quiz-actions .el-button {
  padding: 14px 32px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 16px;
  transition: all 0.3s ease;
}

.quiz-actions .el-button--primary {
  background: linear-gradient(135deg, #3b82f6, #1d4ed8);
  border: none;
  box-shadow: 0 4px 14px rgba(59, 130, 246, 0.4);
}

.quiz-actions .el-button--primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(59, 130, 246, 0.5);
}

.quiz-actions .el-button--success {
  background: linear-gradient(135deg, #10b981, #059669);
  border: none;
  box-shadow: 0 4px 14px rgba(16, 185, 129, 0.4);
}

.quiz-actions .el-button--success:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(16, 185, 129, 0.5);
}

/* 加载和空状态优化 */
.loading-container,
.no-questions {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 400px;
  color: #64748b;
  text-align: center;
}

.loading-icon,
.empty-icon {
  font-size: 64px;
  margin-bottom: 24px;
  color: rgba(255, 255, 255, 0.6);
}

.loading-icon {
  animation: spin 1s linear infinite;
}

.no-questions h3 {
  color: rgba(255, 255, 255, 0.9);
  margin-bottom: 12px;
  font-size: 24px;
  font-weight: 600;
}

.no-questions p {
  margin-bottom: 24px;
  text-align: center;
  max-width: 400px;
  color: rgba(255, 255, 255, 0.7);
  font-size: 16px;
  line-height: 1.5;
}

/* 结果页面超级优化 */
.quiz-results {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 70vh;
}

.results-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 32px;
  padding: 48px;
  text-align: center;
  box-shadow: 0 25px 50px rgba(0, 0, 0, 0.15);
  max-width: 600px;
  width: 100%;
  animation: celebrateIn 0.8s cubic-bezier(0.16, 1, 0.3, 1);
}

@keyframes celebrateIn {
  from {
    opacity: 0;
    transform: translateY(40px) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

.results-icon {
  font-size: 72px;
  margin-bottom: 24px;
  animation: bounce 2s infinite;
}

@keyframes bounce {
  0%, 20%, 50%, 80%, 100% { transform: translateY(0); }
  40% { transform: translateY(-10px); }
  60% { transform: translateY(-5px); }
}

.grade-excellent { 
  background: linear-gradient(135deg, #f59e0b, #d97706);
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}
.grade-good { 
  background: linear-gradient(135deg, #10b981, #059669);
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}
.grade-fair { 
  background: linear-gradient(135deg, #6b7280, #4b5563);
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.results-header h2 {
  background: linear-gradient(135deg, #1e293b, #334155);
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  margin-bottom: 24px;
  font-size: 28px;
  font-weight: 700;
}

.final-score {
  font-size: 64px;
  font-weight: 800;
  margin-bottom: 20px;
  line-height: 1;
}

.score-number {
  background: linear-gradient(135deg, #3b82f6, #1d4ed8);
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.score-total {
  color: #9ca3af;
  font-size: 32px;
}

.grade-badge {
  display: inline-block;
  padding: 12px 24px;
  border-radius: 24px;
  font-weight: 700;
  margin-bottom: 32px;
  font-size: 18px;
  text-transform: uppercase;
  letter-spacing: 1px;
}

.grade-badge.a { 
  background: linear-gradient(135deg, #fef3c7, #fde68a);
  color: #92400e;
  box-shadow: 0 4px 14px rgba(245, 158, 11, 0.3);
}
.grade-badge.b { 
  background: linear-gradient(135deg, #f0fdf4, #dcfce7);
  color: #166534;
  box-shadow: 0 4px 14px rgba(16, 185, 129, 0.3);
}
.grade-badge.c { 
  background: linear-gradient(135deg, #f3f4f6, #e5e7eb);
  color: #374151;
  box-shadow: 0 4px 14px rgba(107, 114, 128, 0.3);
}
.grade-badge.d,
.grade-badge.f { 
  background: linear-gradient(135deg, #fef2f2, #fee2e2);
  color: #991b1b;
  box-shadow: 0 4px 14px rgba(239, 68, 68, 0.3);
}

.results-stats {
  display: flex;
  justify-content: space-around;
  margin-bottom: 40px;
  padding: 24px;
  background: linear-gradient(145deg, #f8fafc, #f1f5f9);
  border-radius: 20px;
  box-shadow: inset 0 2px 4px rgba(0, 0, 0, 0.05);
}

.stat-item {
  text-align: center;
}

.stat-label {
  font-size: 14px;
  color: #64748b;
  margin-bottom: 8px;
  font-weight: 500;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  background: linear-gradient(135deg, #1e293b, #334155);
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.results-actions {
  display: flex;
  gap: 12px;
  justify-content: center;
  flex-wrap: wrap;
}

.results-actions .el-button {
  padding: 12px 24px;
  border-radius: 16px;
  font-weight: 600;
  transition: all 0.3s ease;
}

.results-actions .el-button:hover {
  transform: translateY(-2px);
}

/* 响应式优化 */
@media (max-width: 768px) {
  .quiz-component {
    padding: 16px;
  }
  
  .selection-card {
    padding: 32px 24px;
  }
  
  .lecture-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }
  
  .lecture-card {
    padding: 24px;
  }
  
  .quiz-header {
    flex-direction: column;
    gap: 20px;
    text-align: center;
    padding: 20px;
  }
  
  .quiz-title-row {
    flex-direction: column;
    gap: 12px;
  }
  
  .quiz-controls {
    gap: 16px;
  }
  
  .question-header {
    flex-direction: column;
    gap: 20px;
  }
  
  .question-text {
    margin-right: 0;
    font-size: 20px;
  }
  
  .question-card {
    padding: 24px;
  }
  
  .options-container {
    gap: 12px;
  }
  
  .option-item {
    padding: 16px 20px;
  }
  
  .option-label {
    width: 36px;
    height: 36px;
    margin-right: 16px;
  }
  
  .quiz-actions {
    flex-direction: column;
  }
  
  .results-card {
    padding: 32px 24px;
  }
  
  .results-actions {
    flex-direction: column;
  }
  
  .results-stats {
    flex-direction: column;
    gap: 20px;
  }
  
  .lecture-stats {
    flex-direction: column;
    gap: 8px;
    align-items: flex-start;
  }
  
  .final-score {
    font-size: 48px;
  }
  
  .selection-header h2 {
    font-size: 28px;
  }
}

@media (max-width: 480px) {
  .quiz-component {
    padding: 12px;
  }
  
  .selection-card {
    padding: 24px 16px;
  }
  
  .lecture-card {
    padding: 20px;
  }
  
  .question-card {
    padding: 20px;
  }
  
  .quiz-title {
    font-size: 20px;
  }
  
  .question-text {
    font-size: 18px;
  }
  
  .option-item {
    padding: 14px 16px;
  }
  
  .option-text {
    font-size: 14px;
  }
  
  .final-score {
    font-size: 40px;
  }
  
  .selection-header h2 {
    font-size: 24px;
  }
}

/* 特殊动画效果 */
@keyframes floating {
  0%, 100% { transform: translateY(0px); }
  50% { transform: translateY(-10px); }
}

.quiz-component:hover::before {
  animation: floating 3s ease-in-out infinite;
}

/* 滚动条美化 */
.quiz-component::-webkit-scrollbar {
  width: 8px;
}

.quiz-component::-webkit-scrollbar-track {
  background: rgba(255, 255, 255, 0.1);
  border-radius: 4px;
}

.quiz-component::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.3);
  border-radius: 4px;
}

.quiz-component::-webkit-scrollbar-thumb:hover {
  background: rgba(255, 255, 255, 0.5);
}
</style>