<template>
    <div class="quiz-component">
      <!-- 头部信息栏 -->
      <div class="quiz-header">
        <div class="quiz-info">
          <div class="quiz-title">{{ currentQuiz.title }}</div>
          <div class="quiz-meta">
            <span class="question-count">{{ currentQuestionIndex + 1 }}/{{ questions.length }}</span>
            <span class="category">{{ currentQuiz.category }}</span>
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
      <div class="quiz-content" v-if="!isQuizComplete">
        <div class="question-card">
          <div class="question-header">
            <h3 class="question-text">{{ currentQuestion.question }}</h3>
            <div class="question-timer">
              <el-progress 
                type="circle" 
                :percentage="(timeLeft / questionTimeLimit) * 100" 
                :width="60"
                :stroke-width="6"
                :color="getTimerColor()"
              />
            </div>
          </div>
  
          <!-- 题目图片（如果有） -->
          <div class="question-image" v-if="currentQuestion.image">
            <img :src="currentQuestion.image" :alt="currentQuestion.question" />
          </div>
  
          <!-- 选项列表 -->
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
  
          <!-- 答案解释 -->
          <div class="explanation" v-if="showAnswer && currentQuestion.explanation">
            <div class="explanation-header">
              <el-icon><InfoFilled /></el-icon>
              <span>解释</span>
            </div>
            <p>{{ currentQuestion.explanation }}</p>
          </div>
  
          <!-- 控制按钮 -->
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
  
      <!-- 结果页面 -->
      <div class="quiz-results" v-else>
        <div class="results-card">
          <div class="results-header">
            <div class="results-icon">
              <el-icon v-if="getGrade() === 'A'" class="grade-excellent"><Trophy /></el-icon>
              <el-icon v-else-if="getGrade() === 'B'" class="grade-good"><Medal /></el-icon>
              <el-icon v-else class="grade-fair"><Star /></el-icon>
            </div>
            <h2>测试完成！</h2>
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
            <el-button @click="$emit('backToHome')">返回首页</el-button>
            <el-button type="success" @click="shareResults">分享结果</el-button>
          </div>
        </div>
      </div>
    </div>
  </template>
  
  <script setup lang="ts">
  import { ref, reactive, computed, onMounted, onUnmounted } from 'vue'

  import { ElMessage } from 'element-plus'
  import { 
    Clock, Trophy, Check, Close, InfoFilled, 
    Medal, Star
  } from '@element-plus/icons-vue'
  
  // 定义组件事件
  const emit = defineEmits(['backToHome', 'quizComplete'])
  
  // 定义组件属性
  interface Props {
    quizData?: {
      title?: string
      category?: string
      difficulty?: string
      questions?: Array<any>
    }
  }
  
  const props = withDefaults(defineProps<Props>(), {
    quizData: () => ({
      title: 'JavaScript 基础知识测试',
      category: '前端开发',
      difficulty: 'medium',
      questions: []
    })
  })
  
  // 响应式数据
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
  
  // 模拟题目数据
  const currentQuiz = reactive({
    title: props.quizData.title || 'JavaScript 基础知识测试',
    category: props.quizData.category || '前端开发',
    difficulty: props.quizData.difficulty || 'medium'
  })
  
  const questions = ref(props.quizData.questions?.length ? props.quizData.questions : [
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
  ])
  
  // 计算属性
  const currentQuestion = computed(() => questions.value[currentQuestionIndex.value])
  
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
  
  const submitAnswer = () => {
    if (selectedAnswer.value === null) {
      selectedAnswer.value = -1 // 标记为未选择
    }
    
    stopTimer()
    showAnswer.value = true
    
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
    
    // 发送完成事件到父组件
    emit('quizComplete', {
      score: score.value,
      correctAnswers: correctAnswers.value,
      totalQuestions: questions.value.length,
      grade: getGrade()
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
    currentQuestionIndex.value = 0
    selectedAnswer.value = null
    showAnswer.value = false
    score.value = 0
    correctAnswers.value = 0
    isQuizComplete.value = false
    startTimer()
  }
  
  const shareResults = () => {
    const text = `我刚完成了"${currentQuiz.title}"测试，得分${score.value}分，正确率${Math.round((correctAnswers.value / questions.value.length) * 100)}%！`
    
    if (navigator.share) {
      navigator.share({
        title: 'PopQuiz 测试结果',
        text: text,
        url: window.location.href
      })
    } else {
      // 复制到剪贴板
      navigator.clipboard.writeText(text).then(() => {
        ElMessage.success('结果已复制到剪贴板！')
      })
    }
  }
  
  // 生命周期
  onMounted(() => {
    totalTime.value = questions.value.length * questionTimeLimit
    startTimer()
  })
  
  onUnmounted(() => {
    stopTimer()
  })
  </script>
  
  <style scoped>
  .quiz-component {
    background: #f5f7fa;
    border-radius: 8px;
    padding: 20px;
    height: calc(100vh - 200px);
    overflow-y: auto;
  }
  
  .quiz-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    background: white;
    padding: 20px;
    border-radius: 10px;
    margin-bottom: 20px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  }
  
  .quiz-title {
    font-size: 20px;
    font-weight: 600;
    color: #333;
    margin-bottom: 8px;
  }
  
  .quiz-meta {
    display: flex;
    gap: 15px;
    font-size: 14px;
    color: #666;
  }
  
  .difficulty {
    padding: 2px 8px;
    border-radius: 12px;
    font-size: 12px;
    font-weight: 500;
  }
  
  .difficulty.easy { background: #f0f9ff; color: #0ea5e9; }
  .difficulty.medium { background: #fef3c7; color: #f59e0b; }
  .difficulty.hard { background: #fee2e2; color: #ef4444; }
  
  .quiz-controls {
    display: flex;
    gap: 20px;
    align-items: center;
  }
  
  .timer, .score {
    display: flex;
    align-items: center;
    gap: 8px;
    font-weight: 600;
  }
  
  .timer {
    color: #10b981;
  }
  
  .timer.time-warning {
    color: #ef4444;
    animation: pulse 1s infinite;
  }
  
  @keyframes pulse {
    0%, 100% { opacity: 1; }
    50% { opacity: 0.5; }
  }
  
  .score {
    color: #8b5cf6;
  }
  
  .progress-bar {
    width: 100%;
    height: 8px;
    background: #e5e7eb;
    border-radius: 4px;
    overflow: hidden;
    margin-bottom: 30px;
  }
  
  .progress-fill {
    height: 100%;
    background: linear-gradient(90deg, #10b981, #3b82f6);
    transition: width 0.3s ease;
  }
  
  .question-card {
    background: white;
    border-radius: 12px;
    padding: 30px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  }
  
  .question-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 25px;
  }
  
  .question-text {
    font-size: 18px;
    font-weight: 600;
    color: #1f2937;
    margin: 0;
    flex: 1;
    margin-right: 20px;
  }
  
  .question-image {
    margin-bottom: 25px;
    text-align: center;
  }
  
  .question-image img {
    max-width: 100%;
    height: auto;
    border-radius: 8px;
  }
  
  .options-container {
    display: flex;
    flex-direction: column;
    gap: 12px;
    margin-bottom: 25px;
  }
  
  .option-item {
    display: flex;
    align-items: center;
    padding: 15px;
    border: 2px solid #e5e7eb;
    border-radius: 8px;
    cursor: pointer;
    transition: all 0.2s ease;
    position: relative;
  }
  
  .option-item:hover:not(.disabled) {
    border-color: #3b82f6;
    background: #f8fafc;
  }
  
  .option-item.selected {
    border-color: #3b82f6;
    background: #eff6ff;
  }
  
  .option-item.correct {
    border-color: #10b981;
    background: #f0fdf4;
  }
  
  .option-item.incorrect {
    border-color: #ef4444;
    background: #fef2f2;
  }
  
  .option-item.disabled {
    cursor: not-allowed;
  }
  
  .option-label {
    width: 32px;
    height: 32px;
    background: #f3f4f6;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-weight: 600;
    margin-right: 15px;
    font-size: 14px;
  }
  
  .option-item.selected .option-label {
    background: #3b82f6;
    color: white;
  }
  
  .option-item.correct .option-label {
    background: #10b981;
    color: white;
  }
  
  .option-item.incorrect .option-label {
    background: #ef4444;
    color: white;
  }
  
  .option-text {
    flex: 1;
    font-size: 16px;
    color: #374151;
  }
  
  .option-indicator {
    position: absolute;
    right: 15px;
    top: 50%;
    transform: translateY(-50%);
  }
  
  .correct-icon {
    color: #10b981;
    font-size: 20px;
  }
  
  .incorrect-icon {
    color: #ef4444;
    font-size: 20px;
  }
  
  .explanation {
    background: #f8fafc;
    border-left: 4px solid #3b82f6;
    padding: 15px;
    margin-bottom: 25px;
    border-radius: 0 8px 8px 0;
  }
  
  .explanation-header {
    display: flex;
    align-items: center;
    gap: 8px;
    font-weight: 600;
    color: #3b82f6;
    margin-bottom: 8px;
  }
  
  .explanation p {
    margin: 0;
    color: #4b5563;
    line-height: 1.6;
  }
  
  .quiz-actions {
    display: flex;
    justify-content: center;
  }
  
  .quiz-actions .el-button {
    padding: 12px 30px;
    font-size: 16px;
  }
  
  .quiz-results {
    display: flex;
    justify-content: center;
    align-items: center;
    min-height: 70vh;
  }
  
  .results-card {
    background: white;
    border-radius: 16px;
    padding: 40px;
    text-align: center;
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
    max-width: 500px;
    width: 100%;
  }
  
  .results-icon {
    font-size: 48px;
    margin-bottom: 20px;
  }
  
  .grade-excellent { color: #f59e0b; }
  .grade-good { color: #10b981; }
  .grade-fair { color: #6b7280; }
  
  .results-header h2 {
    color: #1f2937;
    margin-bottom: 20px;
  }
  
  .final-score {
    font-size: 48px;
    font-weight: 700;
    margin-bottom: 15px;
  }
  
  .score-number {
    color: #3b82f6;
  }
  
  .score-total {
    color: #9ca3af;
    font-size: 24px;
  }
  
  .grade-badge {
    display: inline-block;
    padding: 8px 16px;
    border-radius: 20px;
    font-weight: 600;
    margin-bottom: 30px;
  }
  
  .grade-badge.a { background: #fef3c7; color: #f59e0b; }
  .grade-badge.b { background: #f0fdf4; color: #10b981; }
  .grade-badge.c { background: #f3f4f6; color: #6b7280; }
  .grade-badge.d { background: #fef2f2; color: #ef4444; }
  .grade-badge.f { background: #fef2f2; color: #ef4444; }
  
  .results-stats {
    display: flex;
    justify-content: space-around;
    margin-bottom: 30px;
    padding: 20px;
    background: #f8fafc;
    border-radius: 8px;
  }
  
  .stat-item {
    text-align: center;
  }
  
  .stat-label {
    font-size: 14px;
    color: #6b7280;
    margin-bottom: 5px;
  }
  
  .stat-value {
    font-size: 20px;
    font-weight: 600;
    color: #1f2937;
  }
  
  .results-actions {
    display: flex;
    gap: 10px;
    justify-content: center;
    flex-wrap: wrap;
  }
  
  @media (max-width: 768px) {
    .quiz-component {
      padding: 15px;
    }
    
    .quiz-header {
      flex-direction: column;
      gap: 15px;
      text-align: center;
    }
    
    .question-header {
      flex-direction: column;
      gap: 15px;
    }
    
    .question-text {
      margin-right: 0;
    }
    
    .results-actions {
      flex-direction: column;
    }
    
    .results-stats {
      flex-direction: column;
      gap: 15px;
    }
  }
  </style>
  