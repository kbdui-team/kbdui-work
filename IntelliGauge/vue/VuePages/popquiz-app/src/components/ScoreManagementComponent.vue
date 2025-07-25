<template>
  <div class="answer-management">
    <!-- 顶部操作栏 -->
    <div class="management-header">
      <div class="header-left">
        <h2>答题历史管理</h2>
        <div class="stats-summary">
          <el-tag type="primary">总答题次数: {{ totalAnswers }}</el-tag>
          <el-tag type="success">平均正确率: {{ overallAccuracy }}%</el-tag>
          <el-tag type="warning">参与学生: {{ totalStudents }}</el-tag>
        </div>
      </div>
    </div>

    <!-- 筛选和搜索区域 -->
    <div class="filter-section">
      <div class="filter-controls">
        <el-select 
          v-model="selectedLecture" 
          placeholder="选择讲座" 
          clearable 
          @change="handleLectureChange"
          style="width: 200px"
        >
          <el-option label="全部讲座" value="" />
          <el-option
            v-for="lecture in lectureList"
            :key="lecture.id"
            :label="lecture.name"
            :value="lecture.id"
          />
        </el-select>
        
        <el-select 
          v-model="selectedStudent" 
          placeholder="选择学生" 
          clearable 
          filterable
          @change="handleStudentChange"
          style="width: 220px"
        >
          <el-option label="全部学生" value="" />
          <el-option
            v-for="student in availableStudents"
            :key="student.id"
            :label="`${student.realName || student.name || '未知'} (${student.userName || student.username || '未知'})`"
            :value="student.id"
          />
        </el-select>
        
        <el-input
          v-model="searchKeyword"
          placeholder="搜索学生姓名或学号"
          clearable
          @input="handleSearch"
          style="width: 250px"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
      </div>
    </div>

    <!-- 数据概览卡片 -->
    <div class="overview-cards">
      <div class="overview-card">
        <div class="card-icon excellent">
          <el-icon><Trophy /></el-icon>
        </div>
        <div class="card-content">
          <h3>正确率 90%+</h3>
          <p class="card-number">{{ excellentRate }}%</p>
          <p class="card-desc">优秀答题 ({{ excellentCount }}名学生)</p>
        </div>
      </div>
      
      <div class="overview-card">
        <div class="card-icon good">
          <el-icon><Medal /></el-icon>
        </div>
        <div class="card-content">
          <h3>正确率 80-89%</h3>
          <p class="card-number">{{ goodRate }}%</p>
          <p class="card-desc">良好答题 ({{ goodCount }}名学生)</p>
        </div>
      </div>
      
      <div class="overview-card">
        <div class="card-icon pass">
          <el-icon><CircleCheck /></el-icon>
        </div>
        <div class="card-content">
          <h3>正确率 60-79%</h3>
          <p class="card-number">{{ passRate }}%</p>
          <p class="card-desc">及格答题 ({{ passCount }}名学生)</p>
        </div>
      </div>
      
      <div class="overview-card">
        <div class="card-icon fail">
          <el-icon><Warning /></el-icon>
        </div>
        <div class="card-content">
          <h3>正确率 60%以下</h3>
          <p class="card-number">{{ failRate }}%</p>
          <p class="card-desc">需要改进 ({{ failCount }}名学生)</p>
        </div>
      </div>
    </div>

    <!-- 答题记录详情表格 -->
    <div class="answer-table">
      <div class="table-header">
        <h3>答题记录详情</h3>
        <div class="table-actions">
          <el-button 
            size="small" 
            @click="batchOperation"
            :disabled="selectedAnswers.length === 0"
          >
            批量操作 ({{ selectedAnswers.length }})
          </el-button>
          <el-button size="small" type="primary" @click="addAnswer">
            <el-icon><Plus /></el-icon>
            录入记录
          </el-button>
        </div>
      </div>
      
      <el-table
        :data="paginatedDisplayList"
        style="width: 100%"
        @selection-change="handleSelectionChange"
        @sort-change="handleSortChange"
        height="500"
        v-loading="loading"
        empty-text="暂无数据"
        stripe
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="studentName" label="学生姓名" width="120" sortable="custom" />
        <el-table-column prop="studentUserName" label="学号" width="150" sortable="custom" />
        <el-table-column prop="questionId" label="题目ID" width="100" sortable="custom" />
        <el-table-column prop="userAnswer" label="学生答案" min-width="150" show-overflow-tooltip />
        <el-table-column label="是否正确" width="100" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.isCorrect ? 'success' : 'danger'" size="small">
              {{ scope.row.isCorrect ? '正确' : '错误' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="answerTime" label="答题时间" width="160" sortable="custom">
          <template #default="scope">
            {{ formatDateTime(scope.row.answerTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button size="small" type="primary" @click="editAnswer(scope.row)">
              编辑
            </el-button>
            <el-button size="small" type="info" @click="viewDetails(scope.row)">
              详情
            </el-button>
            <el-dropdown @command="handleRowAction" trigger="click">
              <el-button size="small" type="text">
                更多<el-icon class="el-icon--right"><ArrowDown /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item :command="`history-${scope.row.id}`">
                    <el-icon><Clock /></el-icon>历史记录
                  </el-dropdown-item>
                  <el-dropdown-item :command="`analysis-${scope.row.id}`">
                    <el-icon><DataAnalysis /></el-icon>答题分析
                  </el-dropdown-item>
                  <el-dropdown-item :command="`delete-${scope.row.id}`" divided>
                    <el-icon><Delete /></el-icon>删除
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="filteredTotal"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>

    <!-- 答题记录录入/编辑对话框 -->
    <el-dialog
      v-model="answerDialogVisible"
      :title="isEdit ? '编辑答题记录' : '录入答题记录'"
      width="600px"
      destroy-on-close
      @close="resetForm"
    >
      <el-form 
        :model="answerForm" 
        :rules="answerRules" 
        ref="answerFormRef" 
        label-width="100px"
        @submit.prevent="submitAnswer"
      >
        <el-form-item label="学生" prop="userId">
          <el-select 
            v-model="answerForm.userId" 
            placeholder="选择学生" 
            style="width: 100%" 
            @change="handleStudentSelect"
            filterable
          >
            <el-option
              v-for="student in availableStudents"
              :key="student.id"
              :label="`${student.realName || student.name || '未知'} (${student.userName || student.username || '未知'})`"
              :value="student.id"
            />
          </el-select>
        </el-form-item>

        <!-- 显示学生详细信息 -->
        <div v-if="selectedStudentInfo" class="student-info-card">
          <h4>学生详细信息</h4>
          <div class="info-grid">
            <div class="info-item">
              <label>姓名:</label>
              <span>{{ selectedStudentInfo.realName || selectedStudentInfo.name || '未设置' }}</span>
            </div>
            <div class="info-item">
              <label>用户名:</label>
              <span>{{ selectedStudentInfo.userName || selectedStudentInfo.username || '未设置' }}</span>
            </div>
            <div class="info-item">
              <label>邮箱:</label>
              <span>{{ selectedStudentInfo.email || '未设置' }}</span>
            </div>
            <div class="info-item">
              <label>手机:</label>
              <span>{{ selectedStudentInfo.phone || '未设置' }}</span>
            </div>
          </div>
        </div>
        
        <el-form-item label="题目ID" prop="questionId">
          <el-input-number
            v-model="answerForm.questionId"
            placeholder="请输入题目ID"
            style="width: 100%"
            :min="1"
          />
        </el-form-item>
        
        <el-form-item label="学生答案" prop="userAnswer">
          <el-input
            v-model="answerForm.userAnswer"
            placeholder="请输入学生答案"
            type="textarea"
            :rows="3"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
        
        <el-form-item label="是否正确" prop="isCorrect">
          <el-switch
            v-model="answerForm.isCorrect"
            active-text="正确"
            inactive-text="错误"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="answerDialogVisible = false" :disabled="submitting">
            取消
          </el-button>
          <el-button type="primary" @click="submitAnswer" :loading="submitting">
            {{ isEdit ? '更新' : '录入' }}
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 详情查看对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="答题记录详情"
      width="500px"
    >
      <div v-if="currentRecord" class="detail-content">
        <div class="detail-item">
          <label>学生姓名:</label>
          <span>{{ currentRecord.studentName }}</span>
        </div>
        <div class="detail-item">
          <label>学号:</label>
          <span>{{ currentRecord.studentUserName }}</span>
        </div>
        <div class="detail-item">
          <label>题目ID:</label>
          <span>{{ currentRecord.questionId }}</span>
        </div>
        <div class="detail-item">
          <label>学生答案:</label>
          <span>{{ currentRecord.userAnswer }}</span>
        </div>
        <div class="detail-item">
          <label>是否正确:</label>
          <el-tag :type="currentRecord.isCorrect ? 'success' : 'danger'">
            {{ currentRecord.isCorrect ? '正确' : '错误' }}
          </el-tag>
        </div>
        <div class="detail-item">
          <label>答题时间:</label>
          <span>{{ formatDateTime(currentRecord.answerTime) }}</span>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, watch, nextTick } from 'vue'
import { ElMessage, ElMessageBox, ElNotification } from 'element-plus'
import { 
  Refresh, Download, Search, Trophy, Medal, CircleCheck, Warning, 
  Plus, ArrowDown, Clock, DataAnalysis, Delete 
} from '@element-plus/icons-vue'
import axios from 'axios'

// 类型定义
interface Student {
  id: number
  realName?: string
  name?: string
  userName?: string
  username?: string
  email?: string
  phone?: string
}

interface Lecture {
  id: number
  name: string
}

interface AnswerRecord {
  id: number
  userId: number
  questionId: number
  userAnswer: string
  isCorrect: boolean
  answerTime?: string
  studentName?: string
  studentUserName?: string
  studentEmail?: string
  studentPhone?: string
}

interface ApiResponse<T> {
  records: T[]
  total: number
}

// 响应式数据
const selectedLecture = ref<string>('')
const selectedStudent = ref<string>('')
const searchKeyword = ref<string>('')
const dateRange = ref<[Date, Date] | null>(null)
const currentPage = ref<number>(1)
const pageSize = ref<number>(20)
const selectedAnswers = ref<AnswerRecord[]>([])
const loading = ref<boolean>(false)
const submitting = ref<boolean>(false)
const exporting = ref<boolean>(false)
const selectedStudentInfo = ref<Student | null>(null)

const answerDialogVisible = ref<boolean>(false)
const detailDialogVisible = ref<boolean>(false)
const isEdit = ref<boolean>(false)
const currentRecord = ref<AnswerRecord | null>(null)

// API基础URL
const API_BASE_URL = 'http://localhost:5555/answerHistory'
const USER_API_URL = 'http://localhost:5555/user'

// 表单引用
const answerFormRef = ref()

// 表单数据
const answerForm = reactive({
  id: null as number | null,
  userId: '' as string | number,
  questionId: null as number | null,
  userAnswer: '',
  isCorrect: false,
  answerTime: ''
})

// 验证规则
const answerRules = {
  userId: [{ required: true, message: '请选择学生', trigger: 'change' }],
  questionId: [
    { required: true, message: '请输入题目ID', trigger: 'blur' },
    { type: 'number', min: 1, message: '题目ID必须大于0', trigger: 'blur' }
  ],
  userAnswer: [
    { required: true, message: '请输入学生答案', trigger: 'blur' },
    { max: 500, message: '答案长度不能超过500字符', trigger: 'blur' }
  ],
  answerTime: [{ required: true, message: '请选择答题时间', trigger: 'change' }]
}

// 数据列表
const lectureList = ref<Lecture[]>([])
const rawAnswersList = ref<AnswerRecord[]>([]) // 原始答题记录数据
const studentsCache = ref<Map<number, Student>>(new Map()) // 学生信息缓存
const totalRecords = ref<number>(0)

// 排序相关
const sortField = ref<string>('')
const sortOrder = ref<'ascending' | 'descending' | null>(null)

// 计算属性 - 可用的学生列表
const availableStudents = computed(() => {
  const studentArray = Array.from(studentsCache.value.values())
  return studentArray.filter(student => student && student.id)
})

// 计算属性 - 筛选后的数据
const filteredAnswersList = computed(() => {
  let filtered = rawAnswersList.value.map(answer => {
    const userId = answer.userId || (answer as any).user_id
    const studentInfo = studentsCache.value.get(userId)
    
    return {
      ...answer,
      id: answer.id,
      userId: userId,
      questionId: answer.questionId || (answer as any).question_id,
      userAnswer: answer.userAnswer || (answer as any).user_answer || '',
      isCorrect: answer.isCorrect === 1 || answer.isCorrect === true || (answer as any).is_correct === 1,
      answerTime: answer.answerTime || (answer as any).answer_time,
      studentName: studentInfo?.realName || studentInfo?.name || '未知学生',
      studentUserName: studentInfo?.userName || studentInfo?.username || '未知学号',
      studentEmail: studentInfo?.email || '',
      studentPhone: studentInfo?.phone || ''
    } as AnswerRecord
  })

  // 应用搜索过滤
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    filtered = filtered.filter(answer => 
      answer.studentName.toLowerCase().includes(keyword) || 
      answer.studentUserName.toLowerCase().includes(keyword)
    )
  }

  // 应用学生过滤
  if (selectedStudent.value) {
    filtered = filtered.filter(answer => answer.userId.toString() === selectedStudent.value)
  }

  // 应用日期范围过滤
  if (dateRange.value && dateRange.value.length === 2) {
    const [startDate, endDate] = dateRange.value
    filtered = filtered.filter(answer => {
      if (!answer.answerTime) return false
      const answerDate = new Date(answer.answerTime)
      return answerDate >= startDate && answerDate <= endDate
    })
  }

  // 应用排序
  if (sortField.value && sortOrder.value) {
    const isAscending = sortOrder.value === 'ascending'
    filtered.sort((a, b) => {
      const aVal = (a as any)[sortField.value]
      const bVal = (b as any)[sortField.value]
      
      if (typeof aVal === 'string' && typeof bVal === 'string') {
        return isAscending ? aVal.localeCompare(bVal) : bVal.localeCompare(aVal)
      }
      
      if (aVal < bVal) return isAscending ? -1 : 1
      if (aVal > bVal) return isAscending ? 1 : -1
      return 0
    })
  }

  return filtered
})

// 计算属性 - 显示的答题记录列表（分页后）
const paginatedDisplayList = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredAnswersList.value.slice(start, end)
})

// 计算属性 - 筛选后的总数
const filteredTotal = computed(() => filteredAnswersList.value.length)

// 统计计算属性
const totalAnswers = computed(() => filteredAnswersList.value.length)
const totalStudents = computed(() => new Set(filteredAnswersList.value.map(a => a.userId)).size)
const overallAccuracy = computed(() => {
  if (totalAnswers.value === 0) return 0
  const correct = filteredAnswersList.value.filter(a => a.isCorrect).length
  return Math.round((correct / totalAnswers.value) * 100)
})

// 计算学生正确率分布
const studentAccuracyStats = computed(() => {
  const studentAccuracy = new Map<number, { correct: number; total: number }>()
  
  filteredAnswersList.value.forEach(answer => {
    if (!studentAccuracy.has(answer.userId)) {
      studentAccuracy.set(answer.userId, { correct: 0, total: 0 })
    }
    const stats = studentAccuracy.get(answer.userId)!
    stats.total++
    if (answer.isCorrect) stats.correct++
  })
  
  return studentAccuracy
})

const excellentCount = computed(() => {
  return Array.from(studentAccuracyStats.value.values()).filter(stats => 
    (stats.correct / stats.total) >= 0.9
  ).length
})

const excellentRate = computed(() => {
  const total = studentAccuracyStats.value.size
  return total > 0 ? Math.round((excellentCount.value / total) * 100) : 0
})

const goodCount = computed(() => {
  return Array.from(studentAccuracyStats.value.values()).filter(stats => {
    const rate = stats.correct / stats.total
    return rate >= 0.8 && rate < 0.9
  }).length
})

const goodRate = computed(() => {
  const total = studentAccuracyStats.value.size
  return total > 0 ? Math.round((goodCount.value / total) * 100) : 0
})

const passCount = computed(() => {
  return Array.from(studentAccuracyStats.value.values()).filter(stats => {
    const rate = stats.correct / stats.total
    return rate >= 0.6 && rate < 0.8
  }).length
})

const passRate = computed(() => {
  const total = studentAccuracyStats.value.size
  return total > 0 ? Math.round((passCount.value / total) * 100) : 0
})

const failCount = computed(() => {
  return Array.from(studentAccuracyStats.value.values()).filter(stats => 
    (stats.correct / stats.total) < 0.6
  ).length
})

const failRate = computed(() => {
  const total = studentAccuracyStats.value.size
  return total > 0 ? Math.round((failCount.value / total) * 100) : 0
})

// API方法
const loadAnswerHistory = async () => {
  try {
    loading.value = true
    const conditions = buildConditions()
    
    const response = await axios.post(`${API_BASE_URL}/page`, conditions, {
      params: {
        pageNo: 1, // 获取所有数据，前端分页
        pageSize: 1000 // 使用较大的页面大小
      }
    })
    
    console.log('答题记录API响应:', response.data)
    
    if (response.data && response.data.records) {
      rawAnswersList.value = response.data.records
      totalRecords.value = response.data.total || 0
      
      // 批量获取学生信息
      await batchLoadStudentInfo(rawAnswersList.value)
    } else {
      rawAnswersList.value = []
      totalRecords.value = 0
    }
  } catch (error) {
    console.error('加载答题记录失败:', error)
    ElMessage.error('加载答题记录失败')
    rawAnswersList.value = []
    totalRecords.value = 0
  } finally {
    loading.value = false
  }
}

const batchLoadStudentInfo = async (answerRecords: AnswerRecord[]) => {
  // 提取所有唯一的用户ID
  const userIds = [...new Set(answerRecords.map(record => 
    record.userId || (record as any).user_id
  ))].filter(id => id && !studentsCache.value.has(id))
  
  console.log('需要获取学生信息的用户ID:', userIds)
  
  // 批量获取学生信息
  const promises = userIds.map(async (userId) => {
    try {
      const studentInfo = await getUserById(userId)
      if (studentInfo) {
        studentsCache.value.set(userId, studentInfo)
      }
    } catch (error) {
      console.error(`获取用户${userId}信息失败:`, error)
      // 设置一个默认值避免重复请求
      studentsCache.value.set(userId, {
        id: userId,
        realName: '未知学生',
        userName: '未知学号',
        name: '未知学生',
        username: '未知学号'
      })
    }
  })
  
  await Promise.allSettled(promises) // 使用 allSettled 避免单个失败导致整体失败
  console.log('学生信息缓存:', studentsCache.value)
}

const getUserById = async (userId: number): Promise<Student | null> => {
  try {
    const response = await axios.get(`${USER_API_URL}/get/${userId}`)
    return response.data
  } catch (error) {
    console.error(`获取用户${userId}信息失败:`, error)
    return null
  }
}

const loadLectureList = async () => {
  try {
    const response = await axios.get('http://localhost:5555/lecture/list')
    if (response.data) {
      lectureList.value = response.data
    }
  } catch (error) {
    console.error('加载讲座列表失败:', error)
    lectureList.value = []
    ElMessage.warning('加载讲座列表失败')
  }
}

const buildConditions = () => {
  const conditions: any = {}
  
  if (selectedLecture.value) {
    conditions.lectureId = selectedLecture.value
  }
  
  return conditions
}

// 工具方法
const formatDateTime = (dateTime: string | null | undefined): string => {
  if (!dateTime) return '未知时间'
  try {
    const date = new Date(dateTime)
    return date.toLocaleString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit'
    })
  } catch {
    return '时间格式错误'
  }
}

// 事件处理方法
const handleStudentSelect = async (userId: string | number) => {
  if (userId) {
    const numericUserId = typeof userId === 'string' ? parseInt(userId) : userId
    selectedStudentInfo.value = studentsCache.value.get(numericUserId) || null
    
    if (!selectedStudentInfo.value) {
      // 如果缓存中没有，则获取
      selectedStudentInfo.value = await getUserById(numericUserId)
      if (selectedStudentInfo.value) {
        studentsCache.value.set(numericUserId, selectedStudentInfo.value)
      }
    }
  } else {
    selectedStudentInfo.value = null
  }
}

const handleLectureChange = () => {
  currentPage.value = 1
  loadAnswerHistory()
}

const handleStudentChange = () => {
  currentPage.value = 1
  // 不需要重新加载数据，由计算属性处理筛选
}

const handleSearch = () => {
  currentPage.value = 1
  // 搜索通过计算属性实现，不需要重新加载数据
}

const handleDateRangeChange = () => {
  currentPage.value = 1
  // 日期筛选通过计算属性实现
}

const handleSelectionChange = (selection: AnswerRecord[]) => {
  selectedAnswers.value = selection
}

const handleSortChange = (sortInfo: { prop: string; order: 'ascending' | 'descending' | null }) => {
  sortField.value = sortInfo.prop
  sortOrder.value = sortInfo.order
}

const handleSizeChange = (size: number) => {
  pageSize.value = size
  currentPage.value = 1
}

const handleCurrentChange = (page: number) => {
  currentPage.value = page
}

const refreshData = async () => {
  await loadAnswerHistory()
  ElMessage.success('数据刷新成功')
}

const exportData = async () => {
  try {
    exporting.value = true
    
    // 准备导出数据
    const exportData = filteredAnswersList.value.map(record => ({
      '学生姓名': record.studentName,
      '学号': record.studentUserName,
      '题目ID': record.questionId,
      '学生答案': record.userAnswer,
      '是否正确': record.isCorrect ? '正确' : '错误',
      '答题时间': formatDateTime(record.answerTime)
    }))
    
    // 创建CSV内容
    const headers = Object.keys(exportData[0] || {})
    const csvContent = [
      headers.join(','),
      ...exportData.map(row => headers.map(header => `"${row[header as keyof typeof row] || ''}"`).join(','))
    ].join('\n')
    
    // 下载文件
    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' })
    const link = document.createElement('a')
    const url = URL.createObjectURL(blob)
    link.setAttribute('href', url)
    link.setAttribute('download', `答题记录_${new Date().toISOString().split('T')[0]}.csv`)
    link.style.visibility = 'hidden'
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    
    ElMessage.success('数据导出成功')
  } catch (error) {
    console.error('导出数据失败:', error)
    ElMessage.error('导出数据失败')
  } finally {
    exporting.value = false
  }
}

const addAnswer = () => {
  isEdit.value = false
  answerDialogVisible.value = true
  resetAnswerForm()
  selectedStudentInfo.value = null
}

const editAnswer = async (answer: AnswerRecord) => {
  isEdit.value = true
  answerDialogVisible.value = true
  
  // 填充表单数据
  Object.assign(answerForm, {
    id: answer.id,
    userId: answer.userId,
    questionId: answer.questionId,
    userAnswer: answer.userAnswer,
    isCorrect: answer.isCorrect,
    answerTime: answer.answerTime || new Date().toISOString().slice(0, 19).replace('T', ' ')
  })
  
  // 从缓存中获取学生信息
  selectedStudentInfo.value = studentsCache.value.get(answer.userId) || null
}

const viewDetails = (answer: AnswerRecord) => {
  currentRecord.value = answer
  detailDialogVisible.value = true
}

const batchOperation = () => {
  if (selectedAnswers.value.length === 0) {
    ElMessage.warning('请先选择要操作的答题记录')
    return
  }
  
  ElMessageBox.confirm(
    `确定要批量删除选中的 ${selectedAnswers.value.length} 条记录吗？此操作不可撤销！`, 
    '批量删除确认', 
    {
      confirmButtonText: '确定删除',
      cancelButtonText: '取消',
      type: 'warning',
      dangerouslyUseHTMLString: true
    }
  ).then(async () => {
    try {
      loading.value = true
      const ids = selectedAnswers.value.map(item => item.id)
      await axios.post(`${API_BASE_URL}/deleteBatch`, ids)
      
      ElNotification({
        title: '批量删除成功',
        message: `成功删除 ${ids.length} 条记录`,
        type: 'success'
      })
      
      await loadAnswerHistory()
      selectedAnswers.value = []
    } catch (error) {
      console.error('批量删除失败:', error)
      ElMessage.error('批量删除失败')
    } finally {
      loading.value = false
    }
  })
}

const handleRowAction = (command: string) => {
  const [action, id] = command.split('-')
  const recordId = parseInt(id)
  
  if (action === 'delete') {
    ElMessageBox.confirm(
      '确定要删除这条答题记录吗？此操作不可撤销！', 
      '确认删除', 
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning'
      }
    ).then(async () => {
      try {
        loading.value = true
        await axios.delete(`${API_BASE_URL}/delete/${recordId}`)
        ElMessage.success('删除成功!')
        await loadAnswerHistory()
      } catch (error) {
        console.error('删除失败:', error)
        ElMessage.error('删除失败')
      } finally {
        loading.value = false
      }
    })
  } else if (action === 'history') {
    ElMessage.info('查看历史记录功能开发中...')
  } else if (action === 'analysis') {
    ElMessage.info('答题分析功能开发中...')
  }
}

const submitAnswer = async () => {
  if (!answerFormRef.value) return
  
  try {
    // 验证表单
    await answerFormRef.value.validate()
    
    submitting.value = true
    
    // 准备提交数据
    const submitData = {
      ...answerForm,
      answerTime: answerForm.answerTime || new Date().toISOString().slice(0, 19).replace('T', ' ')
    }
    
    if (isEdit.value) {
      await axios.put(`${API_BASE_URL}/update`, submitData)
      ElMessage.success('答题记录修改成功!')
    } else {
      await axios.post(`${API_BASE_URL}/add`, submitData)
      ElMessage.success('答题记录录入成功!')
    }
    
    answerDialogVisible.value = false
    selectedStudentInfo.value = null
    await loadAnswerHistory()
  } catch (error: any) {
    if (error.message) {
      // 表单验证错误
      return
    }
    console.error('提交失败:', error)
    ElMessage.error('操作失败，请重试')
  } finally {
    submitting.value = false
  }
}

const resetAnswerForm = () => {
  Object.assign(answerForm, {
    id: null,
    userId: '',
    questionId: null,
    userAnswer: '',
    isCorrect: false,
    answerTime: ''
  })
  selectedStudentInfo.value = null
}

const resetForm = () => {
  resetAnswerForm()
  if (answerFormRef.value) {
    answerFormRef.value.resetFields()
  }
}

// 监听器
watch([selectedLecture, selectedStudent, searchKeyword, dateRange], () => {
  currentPage.value = 1
}, { deep: true })

// 组件挂载时初始化
onMounted(async () => {
  console.log('组件初始化开始')
  
  try {
    // 并行加载数据以提高性能
    await Promise.all([
      loadLectureList(),
      loadAnswerHistory()
    ])
    
    console.log('组件初始化完成')
  } catch (error) {
    console.error('组件初始化失败:', error)
    ElMessage.error('页面初始化失败，请刷新重试')
  }
})
</script>

<style scoped>
.answer-management {
  padding: 20px;
  background: #f8f9fa;
  min-height: 100vh;
}

.management-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20px;
  padding: 24px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.header-left h2 {
  margin: 0 0 12px 0;
  color: #1a202c;
  font-size: 28px;
  font-weight: 600;
}

.stats-summary {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.header-right {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.filter-section {
  margin-bottom: 20px;
  padding: 20px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.filter-controls {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
  align-items: center;
}

.overview-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.overview-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  transition: all 0.3s ease;
  border: 1px solid transparent;
}

.overview-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
}

.card-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: white;
  margin-right: 16px;
}

.card-icon.excellent {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.card-icon.good {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.card-icon.pass {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.card-icon.fail {
  background: linear-gradient(135deg, #ff9a9e 0%, #fecfef 100%);
}

.card-content h3 {
  margin: 0 0 8px 0;
  color: #4a5568;
  font-size: 14px;
  font-weight: 500;
}

.card-number {
  font-size: 32px;
  font-weight: 700;
  color: #1a202c;
  margin-bottom: 4px;
}

.card-desc {
  font-size: 12px;
  color: #718096;
  margin: 0;
}

.answer-table {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 1px solid #e2e8f0;
}

.table-header h3 {
  margin: 0;
  color: #1a202c;
  font-size: 18px;
  font-weight: 600;
}

.table-actions {
  display: flex;
  gap: 12px;
}

.student-info-card {
  margin: 20px 0;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #e9ecef;
}

.student-info-card h4 {
  margin: 0 0 12px 0;
  color: #495057;
  font-size: 16px;
  font-weight: 600;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 12px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.info-item label {
  font-weight: 600;
  color: #6c757d;
  min-width: 60px;
}

.info-item span {
  color: #495057;
}

.detail-content {
  padding: 10px 0;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
  padding: 8px 0;
  border-bottom: 1px solid #f0f0f0;
}

.detail-item:last-child {
  border-bottom: none;
  margin-bottom: 0;
}

.detail-item label {
  font-weight: 600;
  color: #6c757d;
  min-width: 80px;
}

.detail-item span {
  color: #495057;
  flex: 1;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 20px;
  padding-top: 16px;
  border-top: 1px solid #e2e8f0;
}

.dialog-footer {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .answer-management {
    padding: 12px;
  }
  
  .management-header {
    flex-direction: column;
    gap: 16px;
  }
  
  .header-right {
    width: 100%;
    justify-content: flex-start;
  }
  
  .filter-controls {
    flex-direction: column;
  }
  
  .filter-controls > * {
    width: 100% !important;
  }
  
  .overview-cards {
    grid-template-columns: 1fr;
  }
  
  .table-header {
    flex-direction: column;
    gap: 12px;
    align-items: flex-start;
  }
  
  .table-actions {
    width: 100%;
    justify-content: flex-start;
  }
  
  .info-grid {
    grid-template-columns: 1fr;
  }

  .el-table {
    font-size: 12px;
  }
  
  .detail-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 4px;
  }
  
  .detail-item label {
    min-width: auto;
  }
}

@media (max-width: 480px) {
  .card-content h3 {
    font-size: 12px;
  }
  
  .card-number {
    font-size: 24px;
  }
  
  .card-desc {
    font-size: 10px;
  }
}

/* 表格优化 */
.el-table .el-table__cell {
  padding: 8px 0;
}

.el-table .cell {
  padding: 0 8px;
}

/* 加载状态优化 */
.el-loading-mask {
  border-radius: 8px;
}

/* 按钮样式优化 */
.el-button + .el-button {
  margin-left: 8px;
}

/* 标签样式优化 */
.el-tag {
  border-radius: 6px;
}

/* 对话框样式优化 */
.el-dialog {
  border-radius: 12px;
}

.el-dialog__header {
  padding: 20px 20px 10px;
}

.el-dialog__body {
  padding: 10px 20px 20px;
}

.el-dialog__footer {
  padding: 10px 20px 20px;
}
</style>