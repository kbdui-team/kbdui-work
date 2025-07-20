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
  
      <!-- 匿名答题按钮 -->
      <div class="anonymous-card">
        <el-icon class="anon-icon">
          <Hide v-if="isAnonymous" />
          <UserFilled v-else />
        </el-icon>
        <div class="anon-desc">
          <div class="anon-title">匿名答题</div>
          <div class="anon-tip">{{ isAnonymous ? '当前为匿名答题，成绩不会记录到个人名下' : '当前为实名答题，成绩将记录到个人名下' }}</div>
        </div>
        <el-switch v-model="isAnonymous" active-text="匿名" inactive-text="实名" class="anon-switch" />
      </div>
  
      <!-- 进度条 -->
      <div class="progress-bar">
        <div 
          class="progress-fill" 
          :style="{ width: ((currentQuestionIndex + 1) / questions.length) * 100 + '%' }"
        ></div>
      </div>
  
      <!-- 答题区域，空值保护 -->
      <div class="quiz-content" v-if="!isQuizComplete">
        <div v-if="questions.length > 0 && currentQuestion && currentQuestion.question && Array.isArray(currentQuestion.options) && currentQuestion.options.length > 0">
          <div style="color:#aaa;font-size:13px;margin-bottom:8px;">
            <span>【调试】当前题目索引: {{ currentQuestionIndex }}，题目内容: {{ currentQuestion && currentQuestion.question ? currentQuestion.question : '空' }}，选项数: {{ Array.isArray(currentQuestion && currentQuestion.options) ? currentQuestion.options.length : 0 }}</span>
          </div>
          <div class="question-card">
            <div class="question-header">
              <h3 class="question-text">{{ currentQuestion && currentQuestion.question ? currentQuestion.question : '无题目内容' }}</h3>
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
            <div class="question-image" v-if="currentQuestion && currentQuestion.image">
              <img :src="currentQuestion.image" :alt="currentQuestion.question" />
            </div>
            <div v-if="Array.isArray(currentQuestion.options) && currentQuestion.options.length > 0" class="options-container">
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
            <div v-else style="color:#e57373;text-align:center;padding:16px 0;">
              【调试】当前题目无有效选项，请检查题库数据。
            </div>
            <div class="explanation" v-if="showAnswer && currentQuestion && currentQuestion.explanation">
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
        <div v-else style="text-align:center;color:#888;padding:40px 0;">
          【调试】题目或选项数据为空，questions.length={{ questions.length }}，currentQuestion={{ currentQuestion ? JSON.stringify(currentQuestion) : 'null' }}
          <br>暂无可用题目或选项，请联系管理员添加题库。
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
  import { ref, reactive, computed, onMounted, onUnmounted, inject } from 'vue'

  import { ElMessage } from 'element-plus'
  import { 
    Clock, Trophy, Check, Close, InfoFilled, 
    Medal, Star, UserFilled, Hide
  } from '@element-plus/icons-vue'
  
  import axios from 'axios'
  
  // 定义组件事件
  const emit = defineEmits(['backToHome', 'quizComplete'])
  
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

  // 获取题目和选项
  const fetchQuestions = async () => {
    loadingQuestions.value = true
    try {
      const ts = Date.now();
      // 兼容baseurl不存在时的绝对路径
      const qUrl = baseurl ? `${baseurl}/question/list?t=${ts}` : `/question/list?t=${ts}`;
      const oUrl = baseurl ? `${baseurl}/question-options/list?t=${ts}` : `/question-options/list?t=${ts}`;
      const qRes = await axios.get(qUrl)
      const oRes = await axios.get(oUrl)
      console.log('题目接口请求:', qUrl, '返回:', qRes.data)
      console.log('选项接口请求:', oUrl, '返回:', oRes.data)
      // 严格按后端字段名判断
      const qList = Array.isArray(qRes.data) ? qRes.data : [];
      const oList = Array.isArray(oRes.data) ? oRes.data : [];
      if (!qList.length || !oList.length) {
        ElMessage.error('题目或选项数据格式错误');
        questions.value = [];
        return;
      }
      // 合并题目和选项
      const optionMap = new Map();
      oList.forEach((opt: any) => {
        if (!optionMap.has(opt.questionId)) optionMap.set(opt.questionId, []);
        optionMap.get(opt.questionId).push(opt);
      });
      questions.value = qList.map((q: any) => {
        const opts = optionMap.get(q.id) || [];
        // 选项按optionOrder排序（A/B/C/D...）
        opts.sort((a: any, b: any) => (a.optionOrder > b.optionOrder ? 1 : -1));
        return {
          ...q,
          question: q.questionText || '',
          options: opts.map((opt: any) => opt.optionText),
          correct: opts.findIndex((opt: any) => String(opt.isCorrect).toLowerCase() === 'true'),
          explanation: q.explanation || '',
          image: q.image || null
        };
      });
      if (questions.value.length === 0) {
        ElMessage.warning('暂无题目数据')
      }
    } catch (e) {
      console.error('获取题目或选项失败', e)
      ElMessage.error('获取题目或选项失败: ' + (e && e.message ? e.message : ''))
      questions.value = []
    } finally {
      loadingQuestions.value = false
    }
  }
  
  // 计算属性
  const currentQuestion = computed(() => questions.value[currentQuestionIndex.value] || { question: '', options: [], correct: -1, explanation: '', image: null })
  
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
    fetchQuestions()
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
  
  .anonymous-card {
    display: flex;
    align-items: center;
    background: #fff;
    border-radius: 16px;
    box-shadow: 0 2px 12px rgba(102,126,234,0.10);
    padding: 12px 24px 12px 16px;
    position: absolute;
    top: 18px;
    right: 32px;
    z-index: 10;
    min-width: 260px;
    transition: box-shadow 0.2s;
  }
  .anonymous-card:hover {
    box-shadow: 0 4px 24px rgba(102,126,234,0.18);
  }
  .anon-icon {
    font-size: 28px;
    margin-right: 14px;
    color: #764ba2;
  }
  .anon-desc {
    flex: 1;
  }
  .anon-title {
    font-weight: 600;
    font-size: 16px;
    color: #333;
  }
  .anon-tip {
    font-size: 13px;
    color: #888;
    margin-top: 2px;
  }
  .anon-switch {
    margin-left: 18px;
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
  