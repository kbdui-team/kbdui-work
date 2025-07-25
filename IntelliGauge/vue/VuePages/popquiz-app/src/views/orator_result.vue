<template>
  <div class="quiz-system">
    <!-- 精美头部 -->
    <div class="header">
      <div class="header-content">
        <div class="title-section">
          <div class="icon-wrapper">
            <el-icon class="header-icon"><Document /></el-icon>
          </div>
          <div class="title-text">
            <h1>演讲答题系统</h1>
            <p>智能题目管理平台</p>
          </div>
        </div>
        <div class="header-actions">
          <!-- <el-button type="primary" size="large" @click="openQuestionDialog()" :disabled="!selectedContent" class="primary-btn">
            <el-icon><Plus /></el-icon>
            新建题目
          </el-button> -->
          <el-button size="large" @click="loadData" :loading="loading" class="refresh-btn">
            <el-icon><Refresh /></el-icon>
            刷新数据
          </el-button>
        </div>
      </div>
    </div>

    <!-- 导航面包屑 -->
    <div class="breadcrumb-section" v-if="selectedLecture || selectedContent || selectedQuestion">
      <el-breadcrumb separator="/" class="custom-breadcrumb">
        <el-breadcrumb-item>
          <el-icon><Collection /></el-icon>
          全部讲座
        </el-breadcrumb-item>
        <el-breadcrumb-item v-if="selectedLecture">
          <el-icon><Document /></el-icon>
          讲座 {{ selectedLecture.id }}
        </el-breadcrumb-item>
        <el-breadcrumb-item v-if="selectedContent">
          <el-icon><List /></el-icon>
          内容 {{ selectedContent.id }}
        </el-breadcrumb-item>
        <el-breadcrumb-item v-if="selectedQuestion">
          <el-icon><Edit /></el-icon>
          题目 {{ selectedQuestion.id }}
        </el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-section">
      <div class="stat-card">
        <div class="stat-icon lectures-icon">
          <el-icon><Collection /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ lectureGroups.length }}</div>
          <div class="stat-label">讲座数量</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon contents-icon">
          <el-icon><Document /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ contentGroups.length }}</div>
          <div class="stat-label">内容数量</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon questions-icon">
          <el-icon><List /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ questions.length }}</div>
          <div class="stat-label">题目总数</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon options-icon">
          <el-icon><CircleCheck /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ allOptions.length }}</div>
          <div class="stat-label">选项总数</div>
        </div>
      </div>
    </div>

    <!-- 三层递进内容 -->
    <div class="main-content">
      <!-- 第一层：讲座列表 -->
      <div class="lecture-section">
        <div class="section-header">
          <div class="section-title">
            <el-icon class="section-icon"><Collection /></el-icon>
            <h3>讲座列表</h3>
            <span class="count-badge">{{ lectureGroups.length }}</span>
          </div>
        </div>
        
        <div v-loading="loading" class="lecture-list">
          <div v-if="!loading && lectureGroups.length === 0" class="empty-state">
            <div class="empty-icon">
              <el-icon><Collection /></el-icon>
            </div>
            <h4>暂无讲座数据</h4>
            <p>等待数据加载...</p>
          </div>

          <div v-for="lecture in lectureGroups" 
               :key="lecture.lectureId" 
               class="lecture-card"
               :class="{ active: selectedLecture?.lectureId === lecture.lectureId }"
               @click="selectLecture(lecture)">
            <div class="card-header">
              <div class="lecture-info">
                <div class="lecture-number">{{ lecture.lectureId }}</div>
                <div class="lecture-meta">
                  <span class="lecture-title">讲座 {{ lecture.lectureId }}</span>
                  <span class="lecture-count">{{ lecture.questionCount }} 个题目</span>
                </div>
              </div>
              <div class="lecture-stats">
                <div class="stat-item">
                  <span class="stat-label">内容</span>
                  <span class="stat-value">{{ lecture.contentCount }}</span>
                </div>
                <div class="stat-item">
                  <span class="stat-label">题目</span>
                  <span class="stat-value">{{ lecture.questionCount }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 第二层：内容列表 -->
      <div class="content-section">
        <div v-if="!selectedLecture" class="no-selection">
          <div class="no-selection-icon">
            <el-icon><Document /></el-icon>
          </div>
          <h4>请选择讲座</h4>
          <p>点击左侧讲座查看内容列表</p>
        </div>

        <div v-else class="content-content">
          <div class="section-header">
            <div class="section-title">
              <el-icon class="section-icon"><Document /></el-icon>
              <h3>内容列表</h3>
              <span class="count-badge">{{ currentContents.length }}</span>
            </div>
          </div>

          <div class="selected-lecture-info">
            <h4>讲座 {{ selectedLecture.lectureId }}</h4>
            <p>包含 {{ currentContents.length }} 个内容模块</p>
          </div>

          <div class="content-list">
            <div v-if="currentContents.length === 0" class="empty-state">
              <div class="empty-icon">
                <el-icon><Document /></el-icon>
              </div>
              <h4>暂无内容</h4>
              <p>该讲座下暂无内容模块</p>
            </div>

            <div v-for="content in currentContents" 
                 :key="content.contentInputId" 
                 class="content-card"
                 :class="{ active: selectedContent?.contentInputId === content.contentInputId }"
                 @click="selectContent(content)">
              <div class="card-header">
                <div class="content-info">
                  <div class="content-number">{{ content.contentInputId }}</div>
                  <div class="content-meta">
                    <span class="content-title">内容 {{ content.contentInputId }}</span>
                    <span class="content-count">{{ content.questionCount }} 个题目</span>
                  </div>
                </div>
                <el-icon class="arrow-icon"><ArrowRight /></el-icon>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 第三层：题目列表 -->
      <div class="question-section">
        <div v-if="!selectedContent" class="no-selection">
          <div class="no-selection-icon">
            <el-icon><List /></el-icon>
          </div>
          <h4>请选择内容</h4>
          <p>点击中间内容模块查看题目列表</p>
        </div>

        <div v-else class="question-content">
          <div class="section-header">
            <div class="section-title">
              <el-icon class="section-icon"><List /></el-icon>
              <h3>题目列表</h3>
              <span class="count-badge">{{ currentQuestions.length }}</span>
            </div>
          </div>

          <div class="selected-content-info">
            <h4>讲座 {{ selectedLecture.lectureId }} - 内容 {{ selectedContent.contentInputId }}</h4>
            <p>包含 {{ currentQuestions.length }} 个题目</p>
          </div>

          <div class="question-list">
            <div v-if="currentQuestions.length === 0" class="empty-state">
              <div class="empty-icon">
                <el-icon><List /></el-icon>
              </div>
              <h4>暂无题目</h4>
              <p>点击"新建题目"按钮创建题目</p>
            </div>

            <div v-for="question in currentQuestions" 
                 :key="question.id" 
                 class="question-card"
                 :class="{ active: selectedQuestion?.id === question.id }"
                 @click="selectQuestion(question)">
              <div class="card-header">
                <div class="question-info">
                  <div class="question-number">{{ question.id }}</div>
                  <div class="question-meta">
                    <span class="question-title">题目 {{ question.id }}</span>
                    <el-tag :type="getQuestionTypeColor(question.questionType)" size="small" effect="light">
                      {{ question.questionType }}
                    </el-tag>
                  </div>
                </div>
                <div class="card-actions" @click.stop>
                  <el-tooltip content="查看选项" placement="top">
                    <el-button size="small" circle @click="viewQuestionOptions(question)" class="action-btn view-btn">
                      <el-icon><View /></el-icon>
                    </el-button>
                  </el-tooltip>
                  <el-tooltip content="编辑题目" placement="top">
                    <el-button size="small" circle @click="editQuestion(question)" class="action-btn edit-btn">
                    <!-- <el-button size="small" circle @click="" class="action-btn edit-btn"> -->
                      <el-icon><Edit /></el-icon>
                    </el-button>
                  </el-tooltip>
                  <el-tooltip content="删除题目" placement="top">
                    <el-button size="small" circle @click="deleteQuestion(question.id)" class="action-btn delete-btn">
                    <!-- <el-button size="small" circle @click="" class="action-btn delete-btn"> -->
                      <el-icon><Delete /></el-icon>
                    </el-button>
                  </el-tooltip>
                </div>
              </div>
              <div class="card-content">
                <p class="question-text">{{ question.questionText }}</p>
                <div class="question-details">
                  <div class="detail-item">
                    <el-icon class="detail-icon"><Menu /></el-icon>
                    <span>{{ getOptionCount(question.id) }} 个选项</span>
                  </div>
                  <div class="detail-item">
                    <el-icon class="detail-icon"><CircleCheck /></el-icon>
                    <span>正确答案: {{ getCorrectAnswer(question.id) }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 选项查看对话框 -->
    <el-dialog v-model="optionsDialogVisible" title="题目选项" width="700px" class="custom-dialog">
      <div v-if="selectedQuestion" class="options-dialog-content">
        <div class="question-preview">
          <h4>{{ selectedQuestion.questionText }}</h4>
          <div class="question-tags">
            <el-tag :type="getQuestionTypeColor(selectedQuestion.questionType)" effect="light">
              {{ selectedQuestion.questionType }}
            </el-tag>
            <el-tag type="info" effect="plain">讲座 {{ selectedQuestion.lectureId }}</el-tag>
            <el-tag type="info" effect="plain">内容 {{ selectedQuestion.contentInputId }}</el-tag>
          </div>
        </div>

        <div class="options-section">
          <div class="section-header">
            <h4>选项列表</h4>
            <el-button size="small" @click="addOption">
              <el-icon><Plus /></el-icon>
              添加选项
            </el-button>
          </div>

          <div v-loading="optionsLoading" class="options-list">
            <div v-if="currentOptions.length === 0" class="empty-state">
              <el-empty description="暂无选项" />
            </div>

            <div v-for="(option, index) in currentOptions" :key="option.id" 
                 class="option-card" 
                 :class="{ 'correct-option': option.isCorrect === 'true' || option.isCorrect === true }">
              <div class="option-header">
                <div class="option-label-wrapper">
                  <div class="option-label" :class="{ correct: option.isCorrect === 'true' || option.isCorrect === true }">
                    {{ option.optionOrder }}
                  </div>
                  <div class="option-number">选项 {{ index + 1 }}</div>
                </div>
                <div class="option-status">
                  <el-tag v-if="option.isCorrect === 'true' || option.isCorrect === true" 
                          type="success" size="small" effect="light" class="correct-tag">
                    <el-icon><CircleCheck /></el-icon>
                    正确答案
                  </el-tag>
                  <el-tag v-else type="info" size="small" effect="plain">
                    错误选项
                  </el-tag>
                </div>
                <div class="option-actions">
                  <el-button size="small" circle @click="editOption(option)" class="action-btn edit-btn">
                    <el-icon><Edit /></el-icon>
                  </el-button>
                  <el-button size="small" circle @click="deleteOption(option.id)" class="action-btn delete-btn">
                    <el-icon><Delete /></el-icon>
                  </el-button>
                </div>
              </div>
              <div class="option-content">
                <p class="option-text">{{ option.optionText }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </el-dialog>

    <!-- 题目表单对话框 -->
    <el-dialog v-model="questionDialogVisible" 
               :title="isEditing ? '编辑题目' : '新建题目'" 
               width="600px"
               class="custom-dialog">
      <div class="dialog-content">
        <el-form :model="questionForm" label-width="100px" class="question-form">
          <el-form-item label="所属讲座" class="form-item">
            <el-input-number v-model="questionForm.lectureId" :min="1" class="full-width" :disabled="isEditing" />
          </el-form-item>
          <el-form-item label="所属内容" class="form-item">
            <el-input-number v-model="questionForm.contentInputId" :min="1" class="full-width" :disabled="isEditing" />
          </el-form-item>
          <el-form-item label="题目内容" class="form-item">
            <el-input v-model="questionForm.questionText" 
                      type="textarea" 
                      :rows="4" 
                      placeholder="请输入题目内容..."
                      maxlength="500"
                      show-word-limit />
          </el-form-item>
          <el-form-item label="题目类型" class="form-item">
            <el-select v-model="questionForm.questionType" class="full-width" placeholder="选择题目类型">
              <el-option label="单选题" value="单选题" />
              <el-option label="多选题" value="多选题" />
              <el-option label="判断题" value="判断题" />
            </el-select>
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="questionDialogVisible = false" class="cancel-btn">取消</el-button>
          <el-button type="primary" @click="saveQuestion" :loading="saving" class="save-btn">
            <el-icon v-if="!saving"><Check /></el-icon>
            {{ isEditing ? '更新题目' : '创建题目' }}
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 选项表单对话框 -->
    <el-dialog v-model="optionDialogVisible" 
               :title="isEditingOption ? '编辑选项' : '新建选项'" 
               width="500px"
               class="custom-dialog">
      <div class="dialog-content">
        <el-form :model="optionForm" label-width="80px" class="option-form">
          <el-form-item label="选项内容" class="form-item">
            <el-input v-model="optionForm.optionText" 
                      type="textarea" 
                      :rows="3" 
                      placeholder="请输入选项内容..."
                      maxlength="200"
                      show-word-limit />
          </el-form-item>
          <el-form-item label="选项序号" class="form-item">
            <el-select v-model="optionForm.optionOrder" class="full-width" placeholder="选择选项序号">
              <el-option v-for="letter in ['A', 'B', 'C', 'D', 'E', 'F']" 
                         :key="letter" 
                         :label="letter" 
                         :value="letter" />
            </el-select>
          </el-form-item>
          <el-form-item label="正确答案" class="form-item">
            <el-switch v-model="optionForm.isCorrect" 
                       active-text="正确答案"
                       inactive-text="错误选项"
                       active-color="#67c23a"
                       inactive-color="#dcdfe6" />
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="optionDialogVisible = false" class="cancel-btn">取消</el-button>
          <el-button type="primary" @click="saveOption" :loading="saving" class="save-btn">
            <el-icon v-if="!saving"><Check /></el-icon>
            {{ isEditingOption ? '更新选项' : '创建选项' }}
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Plus, Refresh, Document, List, Check, Edit, Delete, View, 
  Collection, Search, DocumentRemove, Connection, Menu, 
  CircleCheck, Select, Remove, ArrowRight
} from '@element-plus/icons-vue'

// API配置
const API_BASE = import.meta.env.VITE_API_BASE_URL || 'http://localhost:5555'

// 响应式数据
const loading = ref(false)
const optionsLoading = ref(false)
const saving = ref(false)
const questions = ref([])
const allOptions = ref([])

// 选择状态
const selectedLecture = ref(null)
const selectedContent = ref(null)
const selectedQuestion = ref(null)

// 对话框状态
const questionDialogVisible = ref(false)
const optionDialogVisible = ref(false)
const optionsDialogVisible = ref(false)
const isEditing = ref(false)
const isEditingOption = ref(false)

// 表单数据
const questionForm = reactive({
  id: null,
  lectureId: 1,
  contentInputId: 1,
  questionType: '单选题',
  questionText: ''
})

const optionForm = reactive({
  id: null,
  questionId: null,
  optionText: '',
  optionOrder: 'A',
  isCorrect: false
})

// 计算属性 - 按层级分组
const lectureGroups = computed(() => {
  const groups = new Map()
  questions.value.forEach(q => {
    if (!groups.has(q.lectureId)) {
      groups.set(q.lectureId, {
        lectureId: q.lectureId,
        questionCount: 0,
        contentCount: 0,
        contentIds: new Set()
      })
    }
    const group = groups.get(q.lectureId)
    group.questionCount++
    group.contentIds.add(q.contentInputId)
    group.contentCount = group.contentIds.size
  })
  return Array.from(groups.values()).sort((a, b) => a.lectureId - b.lectureId)
})

const contentGroups = computed(() => {
  if (!selectedLecture.value) return []
  const groups = new Map()
  questions.value
    .filter(q => q.lectureId === selectedLecture.value.lectureId)
    .forEach(q => {
      if (!groups.has(q.contentInputId)) {
        groups.set(q.contentInputId, {
          contentInputId: q.contentInputId,
          lectureId: q.lectureId,
          questionCount: 0
        })
      }
      groups.get(q.contentInputId).questionCount++
    })
  return Array.from(groups.values()).sort((a, b) => a.contentInputId - b.contentInputId)
})

const currentContents = computed(() => contentGroups.value)

const currentQuestions = computed(() => {
  if (!selectedContent.value) return []
  return questions.value.filter(q => 
    q.lectureId === selectedContent.value.lectureId && 
    q.contentInputId === selectedContent.value.contentInputId
  ).sort((a, b) => a.id - b.id)
})

const currentOptions = computed(() => {
  if (!selectedQuestion.value) return []
  return allOptions.value
    .filter(opt => opt.questionId === selectedQuestion.value.id)
    .sort((a, b) => a.optionOrder.localeCompare(b.optionOrder))
})

// API方法
const api = {
  async request(url, options = {}) {
    const token = localStorage.getItem('token')
    const response = await fetch(`${API_BASE}${url}`, {
      headers: {
        'Content-Type': 'application/json',
        'Authorization': token ? `Bearer ${token}` : '',
        ...options.headers
      },
      ...options
    })
    
    if (!response.ok) {
      throw new Error(`请求失败: ${response.status}`)
    }
    
    return response.json()
  },

  // 题目API
  getQuestions: () => api.request('/question/list'),
  addQuestion: (data) => api.request('/question/add', { method: 'POST', body: JSON.stringify(data) }),
  updateQuestion: (data) => api.request('/question/update', { method: 'PUT', body: JSON.stringify(data) }),
  deleteQuestion: (id) => api.request(`/question/delete/${id}`, { method: 'DELETE' }),

  // 选项API
  getOptions: () => api.request('/question-options/list'),
  addOption: (data) => api.request('/question-options/add', { method: 'POST', body: JSON.stringify(data) }),
  updateOption: (data) => api.request('/question-options/update', { method: 'PUT', body: JSON.stringify(data) }),
  deleteOption: (id) => api.request(`/question-options/delete/${id}`, { method: 'DELETE' })
}

// 工具方法
const getQuestionTypeColor = (type) => {
  const colorMap = {
    '单选题': 'primary',
    '多选题': 'success',
    '判断题': 'warning'
  }
  return colorMap[type] || 'info'
}

const getOptionCount = (questionId) => {
  return allOptions.value.filter(option => option.questionId === questionId).length
}

const getCorrectAnswer = (questionId) => {
  const correctOption = allOptions.value.find(
    option => option.questionId === questionId && (option.isCorrect === 'true' || option.isCorrect === true)
  )
  return correctOption ? correctOption.optionOrder : '-'
}

// 选择方法
const selectLecture = (lecture) => {
  selectedLecture.value = lecture
  selectedContent.value = null
  selectedQuestion.value = null
}

const selectContent = (content) => {
  selectedContent.value = content
  selectedQuestion.value = null
}

const selectQuestion = (question) => {
  selectedQuestion.value = question
}

const viewQuestionOptions = (question) => {
  selectedQuestion.value = question
  optionsDialogVisible.value = true
}

// 数据加载
const loadData = async () => {
  try {
    loading.value = true
    const [questionsData, optionsData] = await Promise.all([
      api.getQuestions(),
      api.getOptions()
    ])
    questions.value = questionsData || []
    allOptions.value = optionsData || []
    console.log('加载数据成功:', { questions: questionsData, options: optionsData })
  } catch (error) {
    console.error('加载数据失败:', error)
    ElMessage.error('加载数据失败: ' + error.message)
  } finally {
    loading.value = false
  }
}

// 题目操作
const openQuestionDialog = () => {
  if (!selectedContent.value) {
    ElMessage.warning('请先选择内容模块')
    return
  }
  
  isEditing.value = false
  Object.assign(questionForm, {
    id: null,
    lectureId: selectedContent.value.lectureId,
    contentInputId: selectedContent.value.contentInputId,
    questionType: '单选题',
    questionText: ''
  })
  questionDialogVisible.value = true
}

const editQuestion = (question) => {
  isEditing.value = true
  Object.assign(questionForm, question)
  questionDialogVisible.value = true
}

const saveQuestion = async () => {
  try {
    saving.value = true
    if (isEditing.value) {
      await api.updateQuestion(questionForm)
      ElMessage.success('题目更新成功')
    } else {
      await api.addQuestion(questionForm)
      ElMessage.success('题目创建成功')
    }
    questionDialogVisible.value = false
    await loadData()
  } catch (error) {
    ElMessage.error('操作失败: ' + error.message)
  } finally {
    saving.value = false
  }
}

const deleteQuestion = async (id) => {
  try {
    await ElMessageBox.confirm('确定删除这个题目吗？', '确认删除', { 
      type: 'warning',
      confirmButtonText: '确定删除',
      cancelButtonText: '取消'
    })
    await api.deleteQuestion(id)
    ElMessage.success('删除成功')
    if (selectedQuestion.value && selectedQuestion.value.id === id) {
      selectedQuestion.value = null
    }
    await loadData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败: ' + error.message)
    }
  }
}

// 选项操作
const addOption = () => {
  if (!selectedQuestion.value) return
  isEditingOption.value = false
  
  // 自动分配下一个可用的选项序号
  const usedOrders = currentOptions.value.map(opt => opt.optionOrder)
  const availableOrders = ['A', 'B', 'C', 'D', 'E', 'F']
  const nextOrder = availableOrders.find(order => !usedOrders.includes(order)) || 'A'
  
  Object.assign(optionForm, {
    id: null,
    questionId: selectedQuestion.value.id,
    optionText: '',
    optionOrder: nextOrder,
    isCorrect: false
  })
  optionDialogVisible.value = true
}

const editOption = (option) => {
  isEditingOption.value = true
  Object.assign(optionForm, option)
  optionDialogVisible.value = true
}

const saveOption = async () => {
  try {
    saving.value = true
    if (isEditingOption.value) {
      await api.updateOption(optionForm)
      ElMessage.success('选项更新成功')
    } else {
      await api.addOption(optionForm)
      ElMessage.success('选项创建成功')
    }
    optionDialogVisible.value = false
    await loadData()
  } catch (error) {
    ElMessage.error('操作失败: ' + error.message)
  } finally {
    saving.value = false
  }
}

const deleteOption = async (id) => {
  try {
    await ElMessageBox.confirm('确定删除这个选项吗？', '确认删除', { 
      type: 'warning',
      confirmButtonText: '确定删除',
      cancelButtonText: '取消'
    })
    await api.deleteOption(id)
    ElMessage.success('删除成功')
    await loadData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败: ' + error.message)
    }
  }
}

// 初始化
onMounted(() => {
  loadData()
})
</script>

<style scoped>
/* 基础样式 */
.quiz-system {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
}

/* 头部样式 */
.header {
  margin-bottom: 20px;
}

.header-content {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 20px;
  padding: 30px 40px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.title-section {
  display: flex;
  align-items: center;
  gap: 20px;
}

.icon-wrapper {
  width: 60px;
  height: 60px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 10px 20px rgba(102, 126, 234, 0.3);
}

.header-icon {
  font-size: 24px;
  color: white;
}

.title-text h1 {
  margin: 0;
  font-size: 28px;
  font-weight: 700;
  background: linear-gradient(135deg, #667eea, #764ba2);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.title-text p {
  margin: 5px 0 0 0;
  color: #666;
  font-size: 14px;
}

.header-actions {
  display: flex;
  gap: 12px;
}

.primary-btn {
  background: linear-gradient(135deg, #667eea, #764ba2);
  border: none;
  border-radius: 12px;
  padding: 12px 24px;
  font-weight: 600;
  box-shadow: 0 8px 16px rgba(102, 126, 234, 0.3);
  transition: all 0.3s ease;
}

.primary-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 12px 24px rgba(102, 126, 234, 0.4);
}

.primary-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.refresh-btn {
  border-radius: 12px;
  padding: 12px 24px;
  border: 2px solid #e1e8ed;
  background: white;
  color: #333;
  font-weight: 600;
  transition: all 0.3s ease;
}

.refresh-btn:hover {
  border-color: #667eea;
  color: #667eea;
  transform: translateY(-1px);
}

/* 面包屑导航 */
.breadcrumb-section {
  margin-bottom: 20px;
}

.custom-breadcrumb {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  padding: 16px 24px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.custom-breadcrumb :deep(.el-breadcrumb__item) {
  color: #666;
  font-weight: 500;
}

.custom-breadcrumb :deep(.el-breadcrumb__item:last-child .el-breadcrumb__inner) {
  color: #667eea;
  font-weight: 600;
}

/* 统计卡片 */
.stats-section {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.stat-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.12);
}

.stat-icon {
  width: 50px;
  height: 50px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  color: white;
}

.lectures-icon {
  background: linear-gradient(135deg, #667eea, #764ba2);
}

.contents-icon {
  background: linear-gradient(135deg, #4facfe, #00f2fe);
}

.questions-icon {
  background: linear-gradient(135deg, #43e97b, #38f9d7);
}

.options-icon {
  background: linear-gradient(135deg, #f093fb, #f5576c);
}

.stat-content {
  flex: 1;
}

.stat-number {
  font-size: 24px;
  font-weight: 700;
  color: #333;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: #666;
}

/* 主要内容 */
.main-content {
  display: grid;
  grid-template-columns: 1fr 1fr 1fr;
  gap: 20px;
}

.lecture-section,
.content-section,
.question-section {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 20px;
  padding: 24px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  height: fit-content;
  max-height: 700px;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 2px solid #f0f2f5;
  flex-shrink: 0;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 12px;
}

.section-icon {
  font-size: 20px;
  color: #667eea;
}

.section-title h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.count-badge {
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
}

/* 列表容器 */
.lecture-list,
.content-list,
.question-list {
  flex: 1;
  overflow-y: auto;
  padding-right: 8px;
  display: flex;
  flex-direction: column;
  gap: 12px;
  max-height: 420px;
}

.lecture-list::-webkit-scrollbar,
.content-list::-webkit-scrollbar,
.question-list::-webkit-scrollbar {
  width: 8px;
  background: #f1f1f1;
  border-radius: 4px;
}
.lecture-list::-webkit-scrollbar-thumb,
.content-list::-webkit-scrollbar-thumb,
.question-list::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 4px;
}
.lecture-list::-webkit-scrollbar-thumb:hover,
.content-list::-webkit-scrollbar-thumb:hover,
.question-list::-webkit-scrollbar-thumb:hover {
  background: #b0b0b0;
}

/* 通用卡片样式 */
.lecture-card,
.content-card,
.question-card {
  border: 2px solid #e1e8ed;
  border-radius: 12px;
  padding: 16px;
  cursor: pointer;
  transition: all 0.3s ease;
  background: white;
}

.lecture-card:hover,
.content-card:hover,
.question-card:hover {
  border-color: #667eea;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.15);
  transform: translateY(-2px);
}

.lecture-card.active,
.content-card.active,
.question-card.active {
  border-color: #667eea;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.05), rgba(118, 75, 162, 0.05));
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.2);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.lecture-info,
.content-info,
.question-info {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
}

.lecture-number,
.content-number,
.question-number {
  width: 32px;
  height: 32px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 14px;
  flex-shrink: 0;
}

.lecture-meta,
.content-meta,
.question-meta {
  display: flex;
  flex-direction: column;
  gap: 2px;
  min-width: 0;
}

.lecture-title,
.content-title,
.question-title {
  font-weight: 600;
  color: #333;
  font-size: 14px;
}

.lecture-count,
.content-count {
  font-size: 12px;
  color: #666;
}

.lecture-stats {
  display: flex;
  gap: 12px;
}

.stat-item {
  text-align: center;
}

.stat-item .stat-label {
  font-size: 11px;
  color: #999;
  display: block;
}

.stat-item .stat-value {
  font-size: 16px;
  font-weight: 600;
  color: #667eea;
}

.arrow-icon {
  color: #999;
  font-size: 16px;
}

/* 题目卡片特殊样式 */
.card-actions {
  display: flex;
  gap: 6px;
}

.action-btn {
  width: 28px;
  height: 28px;
  border: none;
  border-radius: 6px;
  transition: all 0.3s ease;
}

.view-btn {
  background: #e3f2fd;
  color: #1976d2;
}

.view-btn:hover {
  background: #1976d2;
  color: white;
  transform: scale(1.1);
}

.edit-btn {
  background: #fff3e0;
  color: #f57c00;
}

.edit-btn:hover {
  background: #f57c00;
  color: white;
  transform: scale(1.1);
}

.delete-btn {
  background: #ffebee;
  color: #d32f2f;
}

.delete-btn:hover {
  background: #d32f2f;
  color: white;
  transform: scale(1.1);
}

.card-content {
  margin-top: 8px;
}

.question-text {
  margin: 0 0 8px 0;
  font-size: 13px;
  line-height: 1.4;
  color: #333;
}

.question-details {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #666;
}

.detail-icon {
  font-size: 12px;
  color: #999;
}

/* 空状态和选择提示 */
.no-selection {
  text-align: center;
  padding: 40px 20px;
  color: #999;
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

.no-selection-icon {
  font-size: 48px;
  color: #ddd;
  margin-bottom: 16px;
}

.no-selection h4 {
  margin: 0 0 8px 0;
  font-size: 16px;
  color: #666;
}

.no-selection p {
  margin: 0;
  font-size: 14px;
}

.empty-state {
  text-align: center;
  padding: 40px 20px;
  color: #999;
}

.empty-icon {
  font-size: 48px;
  color: #ddd;
  margin-bottom: 16px;
}

.empty-state h4 {
  margin: 0 0 8px 0;
  font-size: 16px;
  color: #666;
}

.empty-state p {
  margin: 0;
  font-size: 14px;
}

/* 选中信息区域 */
.selected-lecture-info,
.selected-content-info {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.08), rgba(118, 75, 162, 0.08));
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 16px;
  flex-shrink: 0;
}

.selected-lecture-info h4,
.selected-content-info h4 {
  margin: 0 0 4px 0;
  font-size: 16px;
  color: #333;
}

.selected-lecture-info p,
.selected-content-info p {
  margin: 0;
  font-size: 14px;
  color: #666;
}

/* 选项对话框 */
.options-dialog-content {
  max-height: 600px;
  overflow-y: auto;
}

.question-preview {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.08), rgba(118, 75, 162, 0.08));
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 24px;
}

.question-preview h4 {
  margin: 0 0 12px 0;
  font-size: 16px;
  color: #333;
  line-height: 1.5;
}

.question-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.options-section .section-header {
  border-bottom: 1px solid #f0f2f5;
  padding-bottom: 12px;
  margin-bottom: 16px;
}

.options-section .section-header h4 {
  margin: 0;
  font-size: 16px;
  color: #333;
}

.options-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.option-card {
  border: 2px solid #e1e8ed;
  border-radius: 12px;
  padding: 16px;
  background: white;
}

.option-card.correct-option {
  border-color: #67c23a;
  background: linear-gradient(135deg, rgba(103, 194, 58, 0.05), rgba(103, 194, 58, 0.02));
}

.option-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.option-label-wrapper {
  display: flex;
  align-items: center;
  gap: 12px;
}

.option-label {
  width: 28px;
  height: 28px;
  background: #667eea;
  color: white;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 14px;
}

.option-label.correct {
  background: #67c23a;
}

.option-number {
  font-size: 13px;
  color: #666;
  font-weight: 500;
}

.option-status {
  flex: 1;
  display: flex;
  justify-content: center;
}

.correct-tag {
  border: none;
}

.option-actions {
  display: flex;
  gap: 6px;
}

.option-content {
  margin-left: 40px;
}

.option-text {
  margin: 0;
  font-size: 14px;
  line-height: 1.5;
  color: #333;
}

/* 对话框样式 */
.custom-dialog :deep(.el-dialog) {
  border-radius: 20px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
}

.custom-dialog :deep(.el-dialog__header) {
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
  padding: 20px 30px;
  border-bottom: none;
}

.custom-dialog :deep(.el-dialog__title) {
  font-size: 18px;
  font-weight: 600;
}

.custom-dialog :deep(.el-dialog__close) {
  color: white;
  font-size: 16px;
}

.dialog-content {
  padding: 30px;
}

.question-form,
.option-form {
  margin: 0;
}

.form-item {
  margin-bottom: 24px;
}

.form-item :deep(.el-form-item__label) {
  font-weight: 600;
  color: #333;
}

.full-width {
  width: 100%;
}

.dialog-footer {
  padding: 20px 30px;
  border-top: 1px solid #f0f2f5;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.cancel-btn {
  border-radius: 8px;
  padding: 10px 20px;
  border: 2px solid #e1e8ed;
  background: white;
  color: #666;
  font-weight: 600;
}

.save-btn {
  background: linear-gradient(135deg, #667eea, #764ba2);
  border: none;
  border-radius: 8px;
  padding: 10px 20px;
  font-weight: 600;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

/* 响应式设计 */
@media (max-width: 1400px) {
  .main-content {
    grid-template-columns: 1fr 1fr;
  }
  
  .question-section {
    grid-column: 1 / -1;
  }
}

@media (max-width: 900px) {
  .main-content {
    grid-template-columns: 1fr;
    gap: 16px;
  }
  
  .stats-section {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .quiz-system {
    padding: 15px;
  }

  .header-content {
    flex-direction: column;
    gap: 20px;
    padding: 20px;
  }

  .title-section {
    text-align: center;
  }

  .header-actions {
    width: 100%;
    justify-content: center;
  }

  .stats-section {
    grid-template-columns: 1fr;
    gap: 12px;
  }

  .custom-dialog {
    margin: 20px;
    width: calc(100% - 40px);
  }
}
</style>