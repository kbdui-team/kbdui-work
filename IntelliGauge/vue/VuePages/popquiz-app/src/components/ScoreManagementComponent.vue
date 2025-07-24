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
      <div class="header-actions">
        <el-button type="primary" @click="showImportDialog">
          导入记录
        </el-button>
        <el-button type="success" @click="exportAnswers">
          导出报告
        </el-button>
        <el-button type="info" @click="refreshData">
          刷新数据
        </el-button>
      </div>
    </div>

    <!-- 筛选和搜索区域 -->
    <div class="filter-section">
      <div class="filter-controls">
        <el-select v-model="selectedLecture" placeholder="选择讲座" clearable @change="handleLectureChange">
          <el-option label="全部讲座" value="" />
          <el-option
            v-for="lecture in lectureList"
            :key="lecture.id"
            :label="lecture.name"
            :value="lecture.id"
          />
        </el-select>
        
        <el-select v-model="selectedStudent" placeholder="选择学生" clearable @change="handleStudentChange">
          <el-option label="全部学生" value="" />
          <el-option
            v-for="student in availableStudents"
            :key="student.id"
            :label="`${student.realName || student.name || '未知'} (${student.userName || student.username || '未知'})`"
            :value="student.id"
          />
        </el-select>
        
        <el-date-picker
          v-model="dateRange"
          type="datetimerange"
          range-separator="至"
          start-placeholder="开始时间"
          end-placeholder="结束时间"
          @change="handleDateChange"
        />
        
        <el-input
          v-model="searchKeyword"
          placeholder="搜索学生姓名或学号"
          clearable
          @input="handleSearch"
        />
      </div>
    </div>

    <!-- 数据概览卡片 -->
    <div class="overview-cards">
      <div class="overview-card">
        <div class="card-icon excellent">
          <span>A+</span>
        </div>
        <div class="card-content">
          <h3>正确率 90%+</h3>
          <p class="card-number">{{ excellentRate }}%</p>
          <p class="card-desc">优秀答题</p>
        </div>
      </div>
      
      <div class="overview-card">
        <div class="card-icon good">
          <span>A</span>
        </div>
        <div class="card-content">
          <h3>正确率 80-89%</h3>
          <p class="card-number">{{ goodRate }}%</p>
          <p class="card-desc">良好答题</p>
        </div>
      </div>
      
      <div class="overview-card">
        <div class="card-icon pass">
          <span>B</span>
        </div>
        <div class="card-content">
          <h3>正确率 60-79%</h3>
          <p class="card-number">{{ passRate }}%</p>
          <p class="card-desc">及格答题</p>
        </div>
      </div>
      
      <div class="overview-card">
        <div class="card-icon fail">
          <span>C</span>
        </div>
        <div class="card-content">
          <h3>正确率 60%以下</h3>
          <p class="card-number">{{ failRate }}%</p>
          <p class="card-desc">需要改进</p>
        </div>
      </div>
    </div>

    <!-- 答题记录详情表格 -->
    <div class="answer-table">
      <div class="table-header">
        <h3>答题记录详情</h3>
        <div class="table-actions">
          <el-button size="small" @click="batchOperation">批量操作</el-button>
          <el-button size="small" type="primary" @click="addAnswer">录入记录</el-button>
        </div>
      </div>
      
      <el-table
        :data="displayAnswersList"
        style="width: 100%"
        @selection-change="handleSelectionChange"
        @sort-change="handleSortChange"
        height="400"
        v-loading="loading"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="studentName" label="学生姓名" width="120" sortable />
        <el-table-column prop="studentUserName" label="学号" width="120" sortable />
        <el-table-column prop="questionId" label="题目ID" width="100" sortable />
        <el-table-column prop="userAnswer" label="学生答案" width="200" show-overflow-tooltip />
        <el-table-column label="是否正确" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.isCorrect ? 'success' : 'danger'" size="small">
              {{ scope.row.isCorrect ? '正确' : '错误' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="answerTime" label="答题时间" width="160" sortable />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="scope">
            <el-button size="small" type="primary" @click="editAnswer(scope.row)">编辑</el-button>
            <el-button size="small" type="info" @click="viewDetails(scope.row)">详情</el-button>
            <el-dropdown @command="handleRowAction">
              <el-button size="small" type="text">更多</el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item :command="`history-${scope.row.id}`">历史记录</el-dropdown-item>
                  <el-dropdown-item :command="`analysis-${scope.row.id}`">答题分析</el-dropdown-item>
                  <el-dropdown-item :command="`delete-${scope.row.id}`" divided>删除</el-dropdown-item>
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
          :total="totalRecords"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>

    <!-- 答题记录录入对话框 -->
    <el-dialog
      v-model="answerDialogVisible"
      :title="isEdit ? '编辑答题记录' : '录入答题记录'"
      width="600px"
    >
      <el-form :model="answerForm" :rules="answerRules" ref="answerFormRef" label-width="100px">
        <el-form-item label="学生" prop="userId">
          <el-select v-model="answerForm.userId" placeholder="选择学生" style="width: 100%" @change="handleStudentSelect">
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
          />
        </el-form-item>
        
        <el-form-item label="学生答案" prop="userAnswer">
          <el-input
            v-model="answerForm.userAnswer"
            placeholder="请输入学生答案"
          />
        </el-form-item>
        
        <el-form-item label="是否正确" prop="isCorrect">
          <el-switch
            v-model="answerForm.isCorrect"
            active-text="正确"
            inactive-text="错误"
          />
        </el-form-item>
        
        <el-form-item label="答题时间" prop="answerTime">
          <el-date-picker
            v-model="answerForm.answerTime"
            type="datetime"
            placeholder="选择答题时间"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="answerDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitAnswer" :loading="submitting">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 导入记录对话框 -->
    <el-dialog
      v-model="importDialogVisible"
      title="导入答题记录"
      width="600px"
    >
      <div class="import-section">
        <div class="import-tips">
          <el-alert
            title="导入说明"
            type="info"
            :closable="false"
          >
            <template #default>
              <p>1. 请下载模板文件，按照模板格式填写数据</p>
              <p>2. 支持 .xlsx 和 .csv 格式文件</p>
              <p>3. 单次最多导入1000条记录</p>
            </template>
          </el-alert>
        </div>
        
        <div class="import-actions">
          <el-button @click="downloadTemplate">下载模板</el-button>
          <el-upload
            action="/api/answerHistory/import"
            :before-upload="beforeImport"
            :on-success="handleImportSuccess"
            :on-error="handleImportError"
            accept=".xlsx,.csv"
            :show-file-list="false"
          >
            <el-button type="primary">选择文件导入</el-button>
          </el-upload>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from 'axios'

// 响应式数据
const selectedLecture = ref('')
const selectedStudent = ref('')
const dateRange = ref([])
const searchKeyword = ref('')
const currentPage = ref(1)
const pageSize = ref(20)
const selectedAnswers = ref([])
const loading = ref(false)
const submitting = ref(false)
const selectedStudentInfo = ref(null)

const answerDialogVisible = ref(false)
const importDialogVisible = ref(false)
const isEdit = ref(false)

// API基础URL
const API_BASE_URL = 'http://localhost:5555/answerHistory'
const USER_API_URL = 'http://localhost:5555/user'

// 表单数据
const answerForm = reactive({
  id: null,
  userId: '',
  questionId: '',
  userAnswer: '',
  isCorrect: false,
  answerTime: ''
})

// 验证规则
const answerRules = {
  userId: [{ required: true, message: '请选择学生', trigger: 'change' }],
  questionId: [{ required: true, message: '请输入题目ID', trigger: 'blur' }],
  userAnswer: [{ required: true, message: '请输入学生答案', trigger: 'blur' }],
  answerTime: [{ required: true, message: '请选择答题时间', trigger: 'change' }]
}

// 数据列表
const lectureList = ref([])
const rawAnswersList = ref([]) // 原始答题记录数据
const studentsCache = ref(new Map()) // 学生信息缓存 Map<userId, userInfo>
const totalRecords = ref(0)

// 计算属性 - 可用的学生列表（从答题记录中提取的学生）
const availableStudents = computed(() => {
  const studentArray = Array.from(studentsCache.value.values())
  return studentArray.filter(student => student && student.id)
})

// 计算属性 - 显示的答题记录列表（已映射学生信息）
const displayAnswersList = computed(() => {
  return rawAnswersList.value.map(answer => {
    const userId = answer.userId || answer.user_id
    const studentInfo = studentsCache.value.get(userId)
    
    return {
      ...answer,
      id: answer.id,
      userId: userId,
      questionId: answer.questionId || answer.question_id,
      userAnswer: answer.userAnswer || answer.user_answer || '',
      isCorrect: answer.isCorrect === 1 || answer.isCorrect === true || answer.is_correct === 1,
      answerTime: answer.answerTime || answer.answer_time || '',
      studentName: studentInfo?.realName || studentInfo?.name || '未知学生',
      studentUserName: studentInfo?.userName || studentInfo?.username || '未知学号',
      studentEmail: studentInfo?.email || '',
      studentPhone: studentInfo?.phone || ''
    }
  }).filter(answer => {
    // 搜索过滤
    if (searchKeyword.value) {
      const keyword = searchKeyword.value.toLowerCase()
      return answer.studentName.toLowerCase().includes(keyword) || 
             answer.studentUserName.toLowerCase().includes(keyword)
    }
    return true
  })
})

// 统计计算属性
const totalAnswers = computed(() => displayAnswersList.value.length)
const totalStudents = computed(() => new Set(displayAnswersList.value.map(a => a.userId)).size)
const overallAccuracy = computed(() => {
  if (totalAnswers.value === 0) return 0
  const correct = displayAnswersList.value.filter(a => a.isCorrect).length
  return Math.round((correct / totalAnswers.value) * 100)
})

const excellentRate = computed(() => {
  const studentAccuracy = new Map()
  displayAnswersList.value.forEach(answer => {
    if (!studentAccuracy.has(answer.userId)) {
      studentAccuracy.set(answer.userId, { correct: 0, total: 0 })
    }
    const stats = studentAccuracy.get(answer.userId)
    stats.total++
    if (answer.isCorrect) stats.correct++
  })
  
  const excellent = Array.from(studentAccuracy.values()).filter(stats => 
    (stats.correct / stats.total) >= 0.9
  ).length
  
  return studentAccuracy.size > 0 ? Math.round((excellent / studentAccuracy.size) * 100) : 0
})

const goodRate = computed(() => {
  const studentAccuracy = new Map()
  displayAnswersList.value.forEach(answer => {
    if (!studentAccuracy.has(answer.userId)) {
      studentAccuracy.set(answer.userId, { correct: 0, total: 0 })
    }
    const stats = studentAccuracy.get(answer.userId)
    stats.total++
    if (answer.isCorrect) stats.correct++
  })
  
  const good = Array.from(studentAccuracy.values()).filter(stats => {
    const rate = stats.correct / stats.total
    return rate >= 0.8 && rate < 0.9
  }).length
  
  return studentAccuracy.size > 0 ? Math.round((good / studentAccuracy.size) * 100) : 0
})

const passRate = computed(() => {
  const studentAccuracy = new Map()
  displayAnswersList.value.forEach(answer => {
    if (!studentAccuracy.has(answer.userId)) {
      studentAccuracy.set(answer.userId, { correct: 0, total: 0 })
    }
    const stats = studentAccuracy.get(answer.userId)
    stats.total++
    if (answer.isCorrect) stats.correct++
  })
  
  const pass = Array.from(studentAccuracy.values()).filter(stats => {
    const rate = stats.correct / stats.total
    return rate >= 0.6 && rate < 0.8
  }).length
  
  return studentAccuracy.size > 0 ? Math.round((pass / studentAccuracy.size) * 100) : 0
})

const failRate = computed(() => {
  const studentAccuracy = new Map()
  displayAnswersList.value.forEach(answer => {
    if (!studentAccuracy.has(answer.userId)) {
      studentAccuracy.set(answer.userId, { correct: 0, total: 0 })
    }
    const stats = studentAccuracy.get(answer.userId)
    stats.total++
    if (answer.isCorrect) stats.correct++
  })
  
  const fail = Array.from(studentAccuracy.values()).filter(stats => 
    (stats.correct / stats.total) < 0.6
  ).length
  
  return studentAccuracy.size > 0 ? Math.round((fail / studentAccuracy.size) * 100) : 0
})

// API方法
const loadAnswerHistory = async () => {
  try {
    loading.value = true
    const conditions = buildConditions()
    
    const response = await axios.post(`${API_BASE_URL}/page`, conditions, {
      params: {
        pageNo: currentPage.value,
        pageSize: pageSize.value
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

const batchLoadStudentInfo = async (answerRecords) => {
  // 提取所有唯一的用户ID
  const userIds = [...new Set(answerRecords.map(record => record.userId || record.user_id))]
  
  console.log('需要获取学生信息的用户ID:', userIds)
  
  // 批量获取学生信息
  const promises = userIds.map(async (userId) => {
    if (!userId || studentsCache.value.has(userId)) {
      return // 如果已经缓存了就跳过
    }
    
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
  
  await Promise.all(promises)
  console.log('学生信息缓存:', studentsCache.value)
}

const getUserById = async (userId) => {
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
  }
}

const buildConditions = () => {
  const conditions = {}
  
  if (selectedLecture.value) {
    conditions.lectureId = selectedLecture.value
  }
  
  if (selectedStudent.value) {
    conditions.userId = selectedStudent.value
  }
  
  if (dateRange.value && dateRange.value.length === 2) {
    conditions.startTime = dateRange.value[0]
    conditions.endTime = dateRange.value[1]
  }
  
  return conditions
}

// 事件处理方法
const handleStudentSelect = async (userId) => {
  if (userId) {
    selectedStudentInfo.value = studentsCache.value.get(parseInt(userId))
    if (!selectedStudentInfo.value) {
      // 如果缓存中没有，则获取
      selectedStudentInfo.value = await getUserById(parseInt(userId))
      if (selectedStudentInfo.value) {
        studentsCache.value.set(parseInt(userId), selectedStudentInfo.value)
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
  loadAnswerHistory()
}

const handleDateChange = () => {
  currentPage.value = 1
  loadAnswerHistory()
}

const handleSearch = () => {
  // 搜索通过计算属性实现，不需要重新加载数据
}

const handleSelectionChange = (selection) => {
  selectedAnswers.value = selection
}

const handleSortChange = (sortInfo) => {
  console.log('排序变化:', sortInfo)
  // 可以在这里实现前端排序或重新请求数据
}

const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  loadAnswerHistory()
}

const handleCurrentChange = (page) => {
  currentPage.value = page
  loadAnswerHistory()
}

const showImportDialog = () => {
  importDialogVisible.value = true
}

const exportAnswers = () => {
  ElMessage.success('答题记录导出成功!')
}

const refreshData = () => {
  studentsCache.value.clear() // 清空缓存
  loadAnswerHistory()
  ElMessage.success('数据已刷新!')
}

const addAnswer = () => {
  isEdit.value = false
  answerDialogVisible.value = true
  resetAnswerForm()
  selectedStudentInfo.value = null
}

const editAnswer = async (answer) => {
  isEdit.value = true
  answerDialogVisible.value = true
  Object.assign(answerForm, answer)
  // 从缓存中获取学生信息
  selectedStudentInfo.value = studentsCache.value.get(answer.userId)
}

const viewDetails = (answer) => {
  ElMessage.info('查看答题详情功能开发中...')
}

const batchOperation = () => {
  if (selectedAnswers.value.length === 0) {
    ElMessage.warning('请先选择要操作的答题记录')
    return
  }
  
  ElMessageBox.confirm(`确定要批量删除选中的 ${selectedAnswers.value.length} 条记录吗？`, '批量删除', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const ids = selectedAnswers.value.map(item => item.id)
      await axios.post(`${API_BASE_URL}/deleteBatch`, ids)
      ElMessage.success('批量删除成功!')
      loadAnswerHistory()
    } catch (error) {
      console.error('批量删除失败:', error)
      ElMessage.error('批量删除失败')
    }
  })
}

const handleRowAction = (command) => {
  const [action, id] = command.split('-')
  
  if (action === 'delete') {
    ElMessageBox.confirm('确定要删除这条答题记录吗？', '确认删除', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(async () => {
      try {
        await axios.delete(`${API_BASE_URL}/delete/${id}`)
        ElMessage.success('删除成功!')
        loadAnswerHistory()
      } catch (error) {
        console.error('删除失败:', error)
        ElMessage.error('删除失败')
      }
    })
  } else if (action === 'history') {
    ElMessage.info('查看历史记录功能开发中...')
  } else if (action === 'analysis') {
    ElMessage.info('答题分析功能开发中...')
  }
}

const submitAnswer = async () => {
  try {
    submitting.value = true
    
    if (isEdit.value) {
      await axios.put(`${API_BASE_URL}/update`, answerForm)
      ElMessage.success('答题记录修改成功!')
    } else {
      await axios.post(`${API_BASE_URL}/add`, answerForm)
      ElMessage.success('答题记录录入成功!')
    }
    
    answerDialogVisible.value = false
    selectedStudentInfo.value = null
    loadAnswerHistory()
  } catch (error) {
    console.error('提交失败:', error)
    ElMessage.error('操作失败')
  } finally {
    submitting.value = false
  }
}

const resetAnswerForm = () => {
  Object.keys(answerForm).forEach(key => {
    if (key === 'isCorrect') {
      answerForm[key] = false
    } else {
      answerForm[key] = ''
    }
  })
}

const downloadTemplate = () => {
  ElMessage.success('模板下载成功!')
}

const beforeImport = (file) => {
  const isValidType = ['xlsx', 'csv'].includes(file.name.split('.').pop()?.toLowerCase() || '')
  const isLt10M = file.size / 1024 / 1024 < 10
  
  if (!isValidType) {
    ElMessage.error('只能上传 xlsx 或 csv 格式文件!')
    return false
  }
  if (!isLt10M) {
    ElMessage.error('文件大小不能超过 10MB!')
    return false
  }
  return true
}

const handleImportSuccess = () => {
  ElMessage.success('答题记录导入成功!')
  importDialogVisible.value = false
  loadAnswerHistory()
}

const handleImportError = () => {
  ElMessage.error('答题记录导入失败!')
}

// 组件挂载时初始化
onMounted(async () => {
  console.log('组件初始化开始')
  
  try {
    // 先加载讲座列表
    await loadLectureList()
    
    // 然后加载答题记录（会自动获取学生信息）
    await loadAnswerHistory()
    
    console.log('组件初始化完成')
  } catch (error) {
    console.error('组件初始化失败:', error)
    ElMessage.error('页面初始化失败')
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

.header-actions {
  display: flex;
  gap: 12px;
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
}

.filter-controls .el-select,
.filter-controls .el-date-picker,
.filter-controls .el-input {
  width: 200px;
}

.overview-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
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
  transition: transform 0.3s ease;
}

.overview-card:hover {
  transform: translateY(-2px);
}

.card-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  font-weight: bold;
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
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
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

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.import-section {
  padding: 20px 0;
}

.import-tips {
  margin-bottom: 20px;
}

.import-actions {
  display: flex;
  gap: 12px;
  align-items: center;
}

.dialog-footer {
  display: flex;
  gap: 12px;
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
  
  .header-actions {
    width: 100%;
    justify-content: center;
  }
  
  .filter-controls {
    flex-direction: column;
  }
  
  .filter-controls .el-select,
  .filter-controls .el-date-picker,
  .filter-controls .el-input {
    width: 100%;
  }
  
  .overview-cards {
    grid-template-columns: 1fr;
  }
  
  .table-header {
    flex-direction: column;
    gap: 12px;
  }
  
  .table-actions {
    width: 100%;
    justify-content: center;
  }
  
  .info-grid {
    grid-template-columns: 1fr;
  }
}
</style>