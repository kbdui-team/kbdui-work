<template>
  <div>
    <div class="decor-left"></div>
    <div class="decor-right"></div>
    <div class="wrong-review">
      <!-- 新增统计信息展示 -->
      <div v-if="statInfo" class="stat-info-bar">
        <span>已答题数：<b>{{ statInfo.answeredCount }}</b></span>
        <span>正确数：<b style="color:#27ae60">{{ statInfo.correctCount }}</b></span>
        <span>总题数：<b>{{ statInfo.totalCount }}</b></span>
      </div>
      <div class="header-row">
        <h2>错题回顾</h2>
        <div class="filter-bar">
          <button
            :class="{ active: filterType === 'today' }"
            @click="filterType = 'today'"
          >
            <span class="filter-title">今日错题</span>
            <span class="filter-count">{{ todayCount }}</span>
          </button>
          <button
            :class="{ active: filterType === 'all' }"
            @click="filterType = 'all'"
          >
            <span class="filter-title">错题总数</span>
            <span class="filter-count">{{ allCount }}</span>
          </button>
        </div>
      </div>
      <div v-if="filteredList.length === 0" class="empty">
        <img src="https://img.icons8.com/ios/100/cccccc/open-book--v2.png" width="60" style="margin-bottom:12px;" />
        <div>暂无错题，继续加油！</div>
      </div>
      <transition-group name="fade" tag="div">
        <div v-for="item in filteredList" :key="item.id" class="wrong-card">
          <div class="card-header">
            <span class="subject">题目ID: <b>{{ item.id }}</b></span>
            <span class="date">答题时间: {{ item.time }}</span>
          </div>
          <div class="question">
            <span class="question-label">题干：</span>
            <span class="question-content" v-if="item.question">{{ item.question }}</span>
            <span class="question-content empty" v-else>暂无题干</span>
          </div>
          <div class="options-list" v-if="item.options && item.options.length">
            <div v-for="(opt, idx) in item.options" :key="opt.id" class="option-item">
              <span class="option-label">{{ String.fromCharCode(65 + idx) }}.</span>
              <span class="option-text" :style="(opt.isCorrect === 'true' || opt.isCorrect === true) ? 'color:#27ae60;font-weight:bold;' : ''">
                {{ opt.optionText }}
                <span v-if="opt.isCorrect === 'true' || opt.isCorrect === true" style="margin-left:8px;color:#27ae60;font-size:13px;">（正确答案）</span>
              </span>
            </div>
          </div>
          <div class="options">
            <div>
              <span class="my-answer-label">我的答案：</span>
              <span :class="['my-answer', item.isCorrect === 1 ? 'right' : 'wrong']">{{ item.myAnswer }}</span>
              <span v-if="item.isCorrect === 1" class="tag right-tag">正确</span>
              <span v-else class="tag wrong-tag">错误</span>
            </div>

            <!-- 调试使用信息，能看到原始数据 -->
            <!-- <div class="is-correct">isCorrect: <b :class="item.isCorrect === 1 ? 'right' : 'wrong'">{{ item.isCorrect }}</b></div>
            <details class="raw-details">
              <summary>原始数据</summary>
              <pre>{{ JSON.stringify(item.raw, null, 2) }}</pre>
            </details> -->

          </div>
        </div>
      </transition-group>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import type { AxiosResponse } from 'axios'
import request from '@/utils/request'

// 修改 props，允许 lectureId 为 string 或 number
const props = defineProps<{ studentId: string | number, lectureId: string | number }>()

const filterType = ref<'today' | 'all'>('today')
const wrongList = ref<Array<{
  id: number
  question: string
  options?: Array<{ id: number; questionId: number; optionText: string; optionOrder: string; isCorrect?: string | boolean }>
  myAnswer: string
  isCorrect: number
  time: string
  raw: AnswerHistoryRecord
}>>([])

// 新增：答题统计信息
const statInfo = ref<{ answeredCount: number; correctCount: number; totalCount: number } | null>(null)

// 新增：所有题目列表
const allQuestions = ref<Array<{ id: number; questionText: string }>>([])

// 新增：所有题目选项列表
const allOptions = ref<Array<{ id: number; questionId: number; optionText: string; optionOrder: string }>>([])

// 定义 AnswerHistoryRecord 类型
interface AnswerHistoryRecord {
  id: number
  userAnswer: string
  isCorrect: number
  answerTime?: number | string
  questionDTO?: {
    id: number
    questionText: string
  }
  questionId: number // Added for mapping
  [key: string]: any
}

// 定义 QuestionDTO 类型
interface QuestionDTO {
  id: number
  questionText: string
}

function getTodayStr() {
  const d = new Date()
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
}

const todayStr = getTodayStr()

const filteredList = computed(() => {
  if (filterType.value === 'today') {
    return wrongList.value.filter(item => item.time === todayStr)
  } else {
    return wrongList.value
  }
})
const todayCount = computed(() => wrongList.value.filter(item => item.time === todayStr).length)
const allCount = computed(() => wrongList.value.length)

async function fetchWrongList(studentId: string | number, lectureId: string | number) {
  if (!studentId || !lectureId) return
  const userIdNum = Number(studentId)
  const lectureIdNum = Number(lectureId)
  if (isNaN(userIdNum) || userIdNum <= 0 || isNaN(lectureIdNum) || lectureIdNum <= 0) {
    console.error('studentId 或 lectureId 非法:', studentId, lectureId)
    wrongList.value = []
    return
  }
  // 始终传递 userId 和 lectureId
  const postData = { userId: userIdNum, lectureId: lectureIdNum }
  try {
    const res: AxiosResponse = await request.post(
      `/answerHistory/student/${userIdNum}/answers/query`,
      postData
    )
    // 适配新接口结构，筛选错题
    if (res.data && Array.isArray(res.data.answeredQuestionDTOS)) {
      wrongList.value = res.data.answeredQuestionDTOS
        .filter((item: any) => item.isCorrect === 0)
        .map((item: any) => {
          const q = item.questionDTO || { id: 0, questionText: '' }
          return {
            id: q.id,
            question: q.questionText,
            myAnswer: item.userAnswer,
            isCorrect: item.isCorrect,
            time: item.answerTime ? new Date(item.answerTime).toISOString().slice(0, 10) : '',
            raw: item
          }
        })
    } else {
      wrongList.value = []
    }
  } catch (e: any) {
    if (e.response) {
      console.error('后端返回:', e.response.data)
    }
    console.error('获取答题数据失败', e)
    wrongList.value = []
  }
}

// 获取所有题目
async function fetchAllQuestions() {
  try {
    const res: AxiosResponse = await request.get('/question/list')
    if (Array.isArray(res.data)) {
      allQuestions.value = res.data
    } else {
      allQuestions.value = []
    }
  } catch (e) {
    allQuestions.value = []
    console.error('获取题库失败', e)
  }
}

// 获取所有题目选项
async function fetchAllOptions() {
  try {
    const res: AxiosResponse = await request.get('/question-options/list')
    if (Array.isArray(res.data)) {
      allOptions.value = res.data
    } else {
      allOptions.value = []
    }
  } catch (e) {
    allOptions.value = []
    console.error('获取题目选项失败', e)
  }
}

// 修改：获取所有讲座下错题时，补全题干和选项
async function fetchAllLectureWrongList(studentId: string | number) {
  if (!studentId) return
  const userIdNum = Number(studentId)
  if (isNaN(userIdNum) || userIdNum <= 0) {
    wrongList.value = []
    return
  }
  try {
    // 1. 获取所有讲座
    const lectureRes: AxiosResponse = await request.get('/lecture/list')
    const lectures: Array<{ id: number }> = Array.isArray(lectureRes.data) ? lectureRes.data : []
    if (!lectures.length) {
      wrongList.value = []
      return
    }
    // 2. 并发获取所有讲座下错题，body 带 userId/pageNo/pageSize/lectureId
    const allWrongLists = await Promise.all(
      lectures.map(async (lecture) => {
        try {
          const body = {
            pageNo: 1,
            pageSize: 100,
            lectureId: lecture.id
          }
          const res: AxiosResponse = await request.post(
            `/answerHistory/student/${userIdNum}/answers/query`,
            body
          )
          if (res.data && Array.isArray(res.data.answeredQuestionDTOS)) {
            return res.data.answeredQuestionDTOS
              .filter((item: any) => item.isCorrect === 0)
              .map((item: any) => {
                // 优先用题库的题干
                const qid = item.questionDTO?.id || item.questionId || 0
                let questionText = ''
                const found = allQuestions.value.find(q => q.id === qid)
                if (found) {
                  questionText = found.questionText
                } else if (item.questionDTO && item.questionDTO.questionText) {
                  questionText = item.questionDTO.questionText
                }
                // 取出该题的所有选项，按 optionOrder 排序
                const options = allOptions.value
                  .filter(opt => opt.questionId === qid)
                  .sort((a, b) => a.optionOrder.localeCompare(b.optionOrder))
                  .slice(0, 4)
                return {
                  id: qid,
                  question: questionText,
                  options: options,
                  myAnswer: item.userAnswer,
                  isCorrect: item.isCorrect,
                  time: item.answerTime ? new Date(item.answerTime).toISOString().slice(0, 10) : '',
                  raw: item
                }
              })
          }
        } catch {}
        return []
      })
    )
    // 3. 合并所有错题
    wrongList.value = allWrongLists.flat()
  } catch (e) {
    wrongList.value = []
    console.error('获取所有讲座错题失败', e)
  }
}


onMounted(async () => {
  await fetchAllQuestions()
  await fetchAllOptions()
  await fetchAllLectureWrongList(props.studentId)
})

watch(() => props.studentId, async (newId, oldId) => {
  if (newId && newId !== oldId) {
    await fetchAllQuestions()
    await fetchAllOptions()
    await fetchAllLectureWrongList(newId)
  }
})
</script>

<script lang="ts">
export default {
  filters: {
    json(value: any) {
      return JSON.stringify(value, null, 2)
    }
  }
}
</script>

<style scoped>
.wrong-review {
  padding: 32px 0 0 0;
  max-width: 1000px;
  margin: 0 auto;
}
.header-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 18px;
}
h2 {
  margin: 0 0 0 18px;
  font-size: 2em;
  color: #5a4fcf;
  letter-spacing: 2px;
}
.filter-bar {
  display: flex;
  gap: 18px;
}
.filter-bar button {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 22px;
  border: none;
  border-radius: 24px;
  background: #f3f3fa;
  color: #5a4fcf;
  font-size: 15px;
  font-weight: 500;
  cursor: pointer;
  box-shadow: 0 2px 8px #e0e0e0;
  transition: background 0.2s, color 0.2s, box-shadow 0.2s;
  position: relative;
}
.filter-bar button.active {
  background: linear-gradient(90deg, #6c63ff 0%, #a084ee 100%);
  color: #fff;
  box-shadow: 0 4px 16px #d1cfff;
}
.filter-title {
  font-size: 15px;
}
.filter-count {
  background: #fff;
  color: #6c63ff;
  border-radius: 12px;
  padding: 2px 10px;
  font-size: 13px;
  font-weight: bold;
  margin-left: 2px;
}
.filter-bar button.active .filter-count {
  background: #fff;
  color: #6c63ff;
}
.empty {
  color: #aaa;
  text-align: center;
  margin-top: 60px;
  font-size: 1.1em;
  letter-spacing: 1px;
}
.fade-enter-active, .fade-leave-active {
  transition: all 0.3s;
}
.fade-enter-from, .fade-leave-to {
  opacity: 0;
  transform: translateY(20px);
}
.wrong-card {
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 4px 18px #e0e0e0;
  padding: 28px 48px 22px 48px;
  margin-bottom: 32px;
  border-left: 8px solid #6c63ff22;
  transition: box-shadow 0.2s, transform 0.2s;
  position: relative;
  max-width: 900px;
  margin-left: auto;
  margin-right: auto;
}
.wrong-card:hover {
  box-shadow: 0 8px 32px #bdbdfc55;
  transform: translateY(-2px) scale(1.01);
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}
.subject {
  color: #6c63ff;
  font-weight: bold;
  font-size: 16px;
}
.date {
  color: #bbb;
  font-size: 13px;
}
.question {
  font-weight: bold;
  margin-bottom: 12px;
  font-size: 1.1em;
  color: #333;
  display: flex;
  align-items: center;
}
.question-label {
  color: #a084ee;
  margin-right: 8px;
}
.question-content.empty {
  color: #bbb;
  font-style: italic;
}
.options {
  margin-bottom: 10px;
  font-size: 15px;
}
.my-answer-label {
  color: #888;
}
.my-answer {
  font-weight: bold;
  margin-left: 4px;
  margin-right: 8px;
}
.my-answer.right {
  color: #27ae60;
}
.my-answer.wrong {
  color: #e74c3c;
}
.tag {
  font-size: 12px;
  border-radius: 4px;
  padding: 2px 8px;
  margin-left: 8px;
}
.right-tag {
  background: #eafaf1;
  color: #27ae60;
  border: 1px solid #b7ebc6;
}
.wrong-tag {
  background: #ffeaea;
  color: #e74c3c;
  border: 1px solid #f5c6cb;
}
.is-correct {
  margin-top: 4px;
  color: #888;
}
.is-correct .right {
  color: #27ae60;
}
.is-correct .wrong {
  color: #e74c3c;
}
.raw-details {
  margin-top: 8px;
  font-size: 13px;
}
.raw-details summary {
  cursor: pointer;
  color: #6c63ff;
  font-weight: 500;
}
.raw-details pre {
  background: #f6f8fa;
  border-radius: 6px;
  padding: 8px 12px;
  margin: 0;
  color: #555;
  font-size: 13px;
  overflow-x: auto;
}
.stat-info-bar {
  display: flex;
  gap: 32px;
  background: #f3f3fa;
  border-radius: 10px;
  padding: 12px 24px;
  margin-bottom: 18px;
  font-size: 16px;
  color: #5a4fcf;
  align-items: center;
  box-shadow: 0 2px 8px #e0e0e0;
}
.decor-left {
  position: fixed;
  top: 80px;
  left: 0;
  width: 180px;
  height: 320px;
  background: radial-gradient(circle at 60% 40%, #a084ee33 60%, transparent 100%);
  z-index: 0;
  pointer-events: none;
  border-radius: 50% 40% 60% 70%;
  filter: blur(2px);
}

.decor-right {
  position: fixed;
  bottom: 40px;
  right: 0;
  width: 160px;
  height: 260px;
  background: radial-gradient(circle at 40% 60%, #6c63ff22 60%, transparent 100%);
  z-index: 0;
  pointer-events: none;
  border-radius: 60% 80% 50% 70%;
  filter: blur(2px);
}
</style>

export default {}