<template>
    <div class="score-management">
      <!-- 顶部操作栏 -->
      <div class="management-header">
        <div class="header-left">
          <h2>成绩管理</h2>
          <div class="stats-summary">
            <el-tag type="primary">总考试次数: {{ totalExams }}</el-tag>
            <el-tag type="success">平均分: {{ overallAverage }}分</el-tag>
            <el-tag type="warning">参与学生: {{ totalStudents }}</el-tag>
          </div>
        </div>
        <div class="header-actions">
          <el-button type="primary" @click="showImportDialog" :icon="Upload">
            导入成绩
          </el-button>
          <el-button type="success" @click="exportScores" :icon="Download">
            导出报告
          </el-button>
          <el-button type="info" @click="refreshData" :icon="Refresh">
            刷新数据
          </el-button>
        </div>
      </div>
  
      <!-- 筛选和搜索区域 -->
      <div class="filter-section">
        <div class="filter-controls">
          <el-select v-model="selectedClass" placeholder="选择班级" clearable @change="handleClassChange">
            <el-option label="全部班级" value="" />
            <el-option
              v-for="cls in classList"
              :key="cls.id"
              :label="cls.name"
              :value="cls.id"
            />
          </el-select>
          
          <el-select v-model="selectedExam" placeholder="选择考试" clearable @change="handleExamChange">
            <el-option label="全部考试" value="" />
            <el-option
              v-for="exam in examList"
              :key="exam.id"
              :label="exam.name"
              :value="exam.id"
            />
          </el-select>
          
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            @change="handleDateChange"
          />
          
          <el-input
            v-model="searchKeyword"
            placeholder="搜索学生姓名或学号"
            :prefix-icon="Search"
            clearable
            @input="handleSearch"
          />
        </div>
      </div>
  
      <!-- 数据概览卡片 -->
      <div class="overview-cards">
        <div class="overview-card">
          <div class="card-icon excellent">
            <el-icon><TrophyBase /></el-icon>
          </div>
          <div class="card-content">
            <h3>优秀率</h3>
            <p class="card-number">{{ excellentRate }}%</p>
            <p class="card-desc">90分以上</p>
          </div>
        </div>
        
        <div class="overview-card">
          <div class="card-icon good">
            <el-icon><Medal /></el-icon>
          </div>
          <div class="card-content">
            <h3>良好率</h3>
            <p class="card-number">{{ goodRate }}%</p>
            <p class="card-desc">80-89分</p>
          </div>
        </div>
        
        <div class="overview-card">
          <div class="card-icon pass">
            <el-icon><Check /></el-icon>
          </div>
          <div class="card-content">
            <h3>及格率</h3>
            <p class="card-number">{{ passRate }}%</p>
            <p class="card-desc">60分以上</p>
          </div>
        </div>
        
        <div class="overview-card">
          <div class="card-icon fail">
            <el-icon><Close /></el-icon>
          </div>
          <div class="card-content">
            <h3>不及格率</h3>
            <p class="card-number">{{ failRate }}%</p>
            <p class="card-desc">60分以下</p>
          </div>
        </div>
      </div>
  
      <!-- 图表展示区域 -->
      <div class="charts-container">
        <div class="chart-row">
          <!-- 成绩分布柱状图 -->
          <div class="chart-card">
            <div class="chart-header">
              <h3>成绩分布统计</h3>
              <div class="chart-controls">
                <el-radio-group v-model="chartType" @change="updateCharts">
                  <el-radio-button label="bar">柱状图</el-radio-button>
                  <el-radio-button label="pie">饼图</el-radio-button>
                  <el-radio-button label="line">折线图</el-radio-button>
                </el-radio-group>
              </div>
            </div>
            <div class="chart-content">
              <div id="scoreDistribution" class="chart"></div>
            </div>
          </div>
          
          <!-- 平均分趋势图 -->
          <div class="chart-card">
            <div class="chart-header">
              <h3>平均分趋势</h3>
              <div class="chart-controls">
                <el-select v-model="trendPeriod" @change="updateTrendChart">
                  <el-option label="最近7天" value="7days" />
                  <el-option label="最近30天" value="30days" />
                  <el-option label="最近3个月" value="3months" />
                </el-select>
              </div>
            </div>
            <div class="chart-content">
              <div id="averageTrend" class="chart"></div>
            </div>
          </div>
        </div>
        
        <div class="chart-row">
          <!-- 正态分布图 -->
          <div class="chart-card full-width">
            <div class="chart-header">
              <h3>成绩正态分布</h3>
              <div class="chart-controls">
                <el-switch
                  v-model="showNormalCurve"
                  active-text="显示正态曲线"
                  @change="updateNormalDistribution"
                />
              </div>
            </div>
            <div class="chart-content">
              <div id="normalDistribution" class="chart large-chart"></div>
            </div>
          </div>
        </div>
        
        <div class="chart-row">
          <!-- 班级对比雷达图 -->
          <div class="chart-card">
            <div class="chart-header">
              <h3>班级对比分析</h3>
              <div class="chart-controls">
                <el-button size="small" @click="selectCompareClasses">选择对比班级</el-button>
              </div>
            </div>
            <div class="chart-content">
              <div id="classComparison" class="chart"></div>
            </div>
          </div>
          
          <!-- 学科成绩对比 -->
          <div class="chart-card">
            <div class="chart-header">
              <h3>学科成绩对比</h3>
              <div class="chart-controls">
                <el-checkbox-group v-model="selectedSubjects" @change="updateSubjectChart">
                  <el-checkbox v-for="subject in subjectList" :key="subject" :label="subject" />
                </el-checkbox-group>
              </div>
            </div>
            <div class="chart-content">
              <div id="subjectComparison" class="chart"></div>
            </div>
          </div>
        </div>
      </div>
  
      <!-- 成绩详情表格 -->
      <div class="score-table">
        <div class="table-header">
          <h3>成绩详情</h3>
          <div class="table-actions">
            <el-button size="small" @click="batchOperation">批量操作</el-button>
            <el-button size="small" type="primary" @click="addScore">录入成绩</el-button>
          </div>
        </div>
        
        <el-table
          :data="filteredScores"
          style="width: 100%"
          @selection-change="handleSelectionChange"
          @sort-change="handleSortChange"
          height="400"
        >
          <el-table-column type="selection" width="55" />
          <el-table-column prop="studentName" label="学生姓名" width="120" sortable />
          <el-table-column prop="studentId" label="学号" width="120" sortable />
          <el-table-column prop="className" label="班级" width="150" />
          <el-table-column prop="examName" label="考试名称" width="150" />
          <el-table-column prop="subject" label="科目" width="100" />
          <el-table-column prop="score" label="成绩" width="80" sortable>
            <template #default="scope">
              <span :class="getScoreClass(scope.row.score)">{{ scope.row.score }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="fullScore" label="满分" width="80" />
          <el-table-column label="等级" width="80">
            <template #default="scope">
              <el-tag :type="getGradeType(scope.row.score)" size="small">
                {{ getGradeText(scope.row.score) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="classRank" label="班级排名" width="100" sortable />
          <el-table-column prop="gradeRank" label="年级排名" width="100" sortable />
          <el-table-column prop="examDate" label="考试日期" width="120" sortable />
          <el-table-column label="操作" width="150" fixed="right">
            <template #default="scope">
              <el-button size="small" type="primary" @click="editScore(scope.row)">编辑</el-button>
              <el-button size="small" type="info" @click="viewDetails(scope.row)">详情</el-button>
              <el-dropdown @command="handleRowAction">
                <el-button size="small" type="text" :icon="More" />
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item :command="`history-${scope.row.id}`">历史成绩</el-dropdown-item>
                    <el-dropdown-item :command="`analysis-${scope.row.id}`">成绩分析</el-dropdown-item>
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
            :total="filteredScores.length"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </div>
  
      <!-- 成绩录入对话框 -->
      <el-dialog
        v-model="scoreDialogVisible"
        :title="isEdit ? '编辑成绩' : '录入成绩'"
        width="600px"
      >
        <el-form :model="scoreForm" :rules="scoreRules" ref="scoreFormRef" label-width="100px">
          <el-form-item label="学生" prop="studentId">
            <el-select v-model="scoreForm.studentId" placeholder="选择学生" style="width: 100%">
              <el-option
                v-for="student in studentList"
                :key="student.id"
                :label="`${student.name} (${student.studentId})`"
                :value="student.id"
              />
            </el-select>
          </el-form-item>
          
          <el-form-item label="考试" prop="examId">
            <el-select v-model="scoreForm.examId" placeholder="选择考试" style="width: 100%">
              <el-option
                v-for="exam in examList"
                :key="exam.id"
                :label="exam.name"
                :value="exam.id"
              />
            </el-select>
          </el-form-item>
          
          <el-form-item label="科目" prop="subject">
            <el-select v-model="scoreForm.subject" placeholder="选择科目" style="width: 100%">
              <el-option
                v-for="subject in subjectList"
                :key="subject"
                :label="subject"
                :value="subject"
              />
            </el-select>
          </el-form-item>
          
          <el-form-item label="成绩" prop="score">
            <el-input-number
              v-model="scoreForm.score"
              :min="0"
              :max="scoreForm.fullScore"
              placeholder="请输入成绩"
              style="width: 100%"
            />
          </el-form-item>
          
          <el-form-item label="满分" prop="fullScore">
            <el-input-number
              v-model="scoreForm.fullScore"
              :min="1"
              placeholder="请输入满分"
              style="width: 100%"
            />
          </el-form-item>
          
          <el-form-item label="考试日期" prop="examDate">
            <el-date-picker
              v-model="scoreForm.examDate"
              type="date"
              placeholder="选择考试日期"
              style="width: 100%"
            />
          </el-form-item>
          
          <el-form-item label="备注" prop="remark">
            <el-input
              v-model="scoreForm.remark"
              type="textarea"
              placeholder="请输入备注"
            />
          </el-form-item>
        </el-form>
        
        <template #footer>
          <span class="dialog-footer">
            <el-button @click="scoreDialogVisible = false">取消</el-button>
            <el-button type="primary" @click="submitScore">确定</el-button>
          </span>
        </template>
      </el-dialog>
  
      <!-- 导入成绩对话框 -->
      <el-dialog
        v-model="importDialogVisible"
        title="导入成绩"
        width="600px"
      >
        <div class="import-section">
          <div class="import-tips">
            <el-alert
              title="导入说明"
              type="info"
              :closable="false"
              show-icon
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
              action="/api/scores/import"
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
  
      <!-- 班级对比选择对话框 -->
      <el-dialog
        v-model="compareDialogVisible"
        title="选择对比班级"
        width="500px"
      >
        <el-checkbox-group v-model="selectedCompareClasses">
          <div class="class-options">
            <el-checkbox
              v-for="cls in classList"
              :key="cls.id"
              :label="cls.id"
              class="class-option"
            >
              {{ cls.name }}
            </el-checkbox>
          </div>
        </el-checkbox-group>
        
        <template #footer>
          <span class="dialog-footer">
            <el-button @click="compareDialogVisible = false">取消</el-button>
            <el-button type="primary" @click="confirmCompareClasses">确定</el-button>
          </span>
        </template>
      </el-dialog>
    </div>
  </template>
  
  <script setup lang="ts">
  import { ref, reactive, computed, onMounted, nextTick } from 'vue'
  import { ElMessage, ElMessageBox } from 'element-plus'
  import * as echarts from 'echarts'
  import {
    Upload, Download, Refresh, Search, More,
    TrophyBase, Medal, Check, Close
  } from '@element-plus/icons-vue'
  
  // 响应式数据
  const selectedClass = ref('')
  const selectedExam = ref('')
  const dateRange = ref([])
  const searchKeyword = ref('')
  const chartType = ref('bar')
  const trendPeriod = ref('30days')
  const showNormalCurve = ref(true)
  const selectedSubjects = ref(['数学', '语文', '英语'])
  const currentPage = ref(1)
  const pageSize = ref(20)
  const selectedScores = ref([])
  
  const scoreDialogVisible = ref(false)
  const importDialogVisible = ref(false)
  const compareDialogVisible = ref(false)
  const isEdit = ref(false)
  const selectedCompareClasses = ref([])
  
  // 表单数据
  const scoreForm = reactive({
    studentId: '',
    examId: '',
    subject: '',
    score: 0,
    fullScore: 100,
    examDate: '',
    remark: ''
  })
  
  // 验证规则
  const scoreRules = {
    studentId: [{ required: true, message: '请选择学生', trigger: 'change' }],
    examId: [{ required: true, message: '请选择考试', trigger: 'change' }],
    subject: [{ required: true, message: '请选择科目', trigger: 'change' }],
    score: [{ required: true, message: '请输入成绩', trigger: 'blur' }],
    fullScore: [{ required: true, message: '请输入满分', trigger: 'blur' }],
    examDate: [{ required: true, message: '请选择考试日期', trigger: 'change' }]
  }
  
  // 模拟数据
  const classList = ref([
    { id: 1, name: '计算机科学与技术2021级1班' },
    { id: 2, name: '计算机科学与技术2021级2班' },
    { id: 3, name: '软件工程2021级1班' }
  ])
  
  const examList = ref([
    { id: 1, name: '期中考试', date: '2024-04-15' },
    { id: 2, name: '期末考试', date: '2024-06-20' },
    { id: 3, name: '月考一', date: '2024-03-15' }
  ])
  
  const subjectList = ref(['数学', '语文', '英语', '物理', '化学', '生物', '历史', '地理'])
  
  const studentList = ref([
    { id: 1, name: '张三', studentId: '2021001001' },
    { id: 2, name: '李四', studentId: '2021001002' },
    { id: 3, name: '王五', studentId: '2021001003' }
  ])
  
  const scoresList = ref([
    {
      id: 1,
      studentName: '张三',
      studentId: '2021001001',
      className: '计算机科学与技术2021级1班',
      examName: '期中考试',
      subject: '数学',
      score: 92,
      fullScore: 100,
      classRank: 3,
      gradeRank: 15,
      examDate: '2024-04-15'
    },
    {
      id: 2,
      studentName: '李四',
      studentId: '2021001002',
      className: '计算机科学与技术2021级1班',
      examName: '期中考试',
      subject: '数学',
      score: 87,
      fullScore: 100,
      classRank: 8,
      gradeRank: 32,
      examDate: '2024-04-15'
    },
    {
      id: 3,
      studentName: '王五',
      studentId: '2021001003',
      className: '计算机科学与技术2021级2班',
      examName: '期中考试',
      subject: '数学',
      score: 78,
      fullScore: 100,
      classRank: 15,
      gradeRank: 68,
      examDate: '2024-04-15'
    }
  ])
  
  // 计算属性
  const totalExams = computed(() => examList.value.length)
  const totalStudents = computed(() => studentList.value.length)
  const overallAverage = computed(() => {
    const total = scoresList.value.reduce((sum, score) => sum + score.score, 0)
    return Math.round(total / scoresList.value.length)
  })
  
  const excellentRate = computed(() => {
    const excellent = scoresList.value.filter(s => s.score >= 90).length
    return Math.round((excellent / scoresList.value.length) * 100)
  })
  
  const goodRate = computed(() => {
    const good = scoresList.value.filter(s => s.score >= 80 && s.score < 90).length
    return Math.round((good / scoresList.value.length) * 100)
  })
  
  const passRate = computed(() => {
    const pass = scoresList.value.filter(s => s.score >= 60).length
    return Math.round((pass / scoresList.value.length) * 100)
  })
  
  const failRate = computed(() => {
    const fail = scoresList.value.filter(s => s.score < 60).length
    return Math.round((fail / scoresList.value.length) * 100)
  })
  
  const filteredScores = computed(() => {
    let filtered = scoresList.value
  
    // 搜索过滤
    if (searchKeyword.value) {
      const keyword = searchKeyword.value.toLowerCase()
      filtered = filtered.filter(score =>
        score.studentName.toLowerCase().includes(keyword) ||
        score.studentId.includes(keyword)
      )
    }
  
    // 班级过滤
    if (selectedClass.value) {
      filtered = filtered.filter(score => score.classId === selectedClass.value)
    }
  
    // 考试过滤
    if (selectedExam.value) {
      filtered = filtered.filter(score => score.examId === selectedExam.value)
    }
  
    return filtered
  })
  
  // ECharts 实例
  let scoreDistributionChart: any = null
  let averageTrendChart: any = null
  let normalDistributionChart: any = null
  let classComparisonChart: any = null
  let subjectComparisonChart: any = null
  
  // 方法
  const handleClassChange = () => {
    updateCharts()
  }
  
  const handleExamChange = () => {
    updateCharts()
  }
  
  const handleDateChange = () => {
    updateCharts()
  }
  
  const handleSearch = () => {
    currentPage.value = 1
  }
  
  const handleSelectionChange = (selection: any[]) => {
    selectedScores.value = selection
  }
  
  const handleSortChange = (sortInfo: any) => {
    console.log('排序变化:', sortInfo)
  }
  
  const getScoreClass = (score: number) => {
    if (score >= 90) return 'score-excellent'
    if (score >= 80) return 'score-good'
    if (score >= 60) return 'score-pass'
    return 'score-fail'
  }
  
  const getGradeType = (score: number) => {
    if (score >= 90) return 'success'
    if (score >= 80) return 'warning'
    if (score >= 60) return 'info'
    return 'danger'
  }
  
  const getGradeText = (score: number) => {
    if (score >= 90) return '优秀'
    if (score >= 80) return '良好'
    if (score >= 60) return '及格'
    return '不及格'
  }
  
  const showImportDialog = () => {
    importDialogVisible.value = true
  }
  
  const exportScores = () => {
    ElMessage.success('成绩报告导出成功!')
  }
  
  const refreshData = () => {
    updateCharts()
    ElMessage.success('数据已刷新!')
  }
  
  const addScore = () => {
    isEdit.value = false
    scoreDialogVisible.value = true
    resetScoreForm()
  }
  
  const editScore = (score: any) => {
    isEdit.value = true
    scoreDialogVisible.value = true
    // 填充表单数据
    Object.assign(scoreForm, score)
  }
  
  const viewDetails = (score: any) => {
    ElMessage.info('查看成绩详情功能开发中...')
  }
  
  const batchOperation = () => {
    if (selectedScores.value.length === 0) {
      ElMessage.warning('请先选择要操作的成绩记录')
      return
    }
    ElMessage.info('批量操作功能开发中...')
  }
  
  const handleRowAction = (command: string) => {
    const [action, id] = command.split('-')
    console.log('行操作:', action, id)
    
    if (action === 'delete') {
      ElMessageBox.confirm('确定要删除这条成绩记录吗？', '确认删除', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        const index = scoresList.value.findIndex(s => s.id === parseInt(id))
        if (index > -1) {
          scoresList.value.splice(index, 1)
          ElMessage.success('删除成功!')
          updateCharts()
        }
      })
    }
  }
  
  const submitScore = () => {
    // 这里应该调用API保存成绩
    if (isEdit.value) {
      ElMessage.success('成绩修改成功!')
    } else {
      ElMessage.success('成绩录入成功!')
    }
    scoreDialogVisible.value = false
    updateCharts()
  }
  
  const resetScoreForm = () => {
    Object.keys(scoreForm).forEach(key => {
      if (key === 'fullScore') {
        scoreForm[key] = 100
      } else if (key === 'score') {
        scoreForm[key] = 0
      } else {
        scoreForm[key] = ''
      }
    })
  }
  
  const downloadTemplate = () => {
    ElMessage.success('模板下载成功!')
  }
  
  const beforeImport = (file: File) => {
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
    ElMessage.success('成绩导入成功!')
    importDialogVisible.value = false
    updateCharts()
  }
  
  const handleImportError = () => {
    ElMessage.error('成绩导入失败!')
  }
  
  const selectCompareClasses = () => {
    compareDialogVisible.value = true
  }
  
  const confirmCompareClasses = () => {
    compareDialogVisible.value = false
    updateClassComparisonChart()
  }
  
  const handleSizeChange = (size: number) => {
    pageSize.value = size
    currentPage.value = 1
  }
  
  const handleCurrentChange = (page: number) => {
    currentPage.value = page
  }
  
  // 图表更新方法
  const updateCharts = () => {
    nextTick(() => {
      updateScoreDistributionChart()
      updateAverageTrendChart()
      updateNormalDistribution()
      updateClassComparisonChart()
      updateSubjectChart()
    })
  }
  
  const updateScoreDistributionChart = () => {
    if (!scoreDistributionChart) return
    
    const data = [
      { name: '90-100分', value: scoresList.value.filter(s => s.score >= 90).length },
      { name: '80-89分', value: scoresList.value.filter(s => s.score >= 80 && s.score < 90).length },
      { name: '70-79分', value: scoresList.value.filter(s => s.score >= 70 && s.score < 80).length },
      { name: '60-69分', value: scoresList.value.filter(s => s.score >= 60 && s.score < 70).length },
      { name: '60分以下', value: scoresList.value.filter(s => s.score < 60).length }
    ]
    
    const option = {
      title: { text: '成绩分布', left: 'center' },
      tooltip: { trigger: 'item' },
      series: [{
        type: chartType.value,
        data: chartType.value === 'pie' ? data : data.map(d => d.value),
        radius: chartType.value === 'pie' ? '70%' : undefined
      }],
      xAxis: chartType.value !== 'pie' ? {
        type: 'category',
        data: data.map(d => d.name)
      } : undefined,
      yAxis: chartType.value !== 'pie' ? { type: 'value' } : undefined
    }
    
    scoreDistributionChart.setOption(option)
  }
  
const updateAverageTrendChart = () => {
  if (!averageTrendChart) return
  
  // 模拟趋势数据
  const dates = []
  const scores = []
  const days = trendPeriod.value === '7days' ? 7 : trendPeriod.value === '30days' ? 30 : 90
  
  for (let i = days; i >= 0; i--) {
    const date = new Date()
    date.setDate(date.getDate() - i)
    dates.push(date.toISOString().split('T')[0])
    scores.push(Math.floor(Math.random() * 20 + 75)) // 75-95分之间
  }
  
  const option = {
    title: { text: '平均分趋势', left: 'center' },
    tooltip: { trigger: 'axis' },
    xAxis: {
      type: 'category',
      data: dates
    },
    yAxis: {
      type: 'value',
      min: 60,
      max: 100
    },
    series: [{
      type: 'line',
      data: scores,
      smooth: true,
      lineStyle: { color: '#409EFF' },
      areaStyle: { opacity: 0.3 }
    }]
  }
  
  averageTrendChart.setOption(option)
}

const updateNormalDistribution = () => {
  if (!normalDistributionChart) return
  
  const scores = scoresList.value.map(s => s.score)
  const mean = scores.reduce((sum, score) => sum + score, 0) / scores.length
  const variance = scores.reduce((sum, score) => sum + Math.pow(score - mean, 2), 0) / scores.length
  const stdDev = Math.sqrt(variance)
  
  // 创建分组数据
  const bins = []
  const binSize = 5
  for (let i = 0; i <= 100; i += binSize) {
    bins.push({
      range: `${i}-${i + binSize - 1}`,
      count: scores.filter(score => score >= i && score < i + binSize).length,
      x: i + binSize / 2
    })
  }
  
  // 正态分布曲线数据
  const normalCurve = []
  if (showNormalCurve.value) {
    for (let x = 0; x <= 100; x += 1) {
      const y = (1 / (stdDev * Math.sqrt(2 * Math.PI))) * 
                Math.exp(-0.5 * Math.pow((x - mean) / stdDev, 2))
      normalCurve.push([x, y * scores.length * binSize])
    }
  }
  
  const option = {
    title: { text: '成绩正态分布', left: 'center' },
    tooltip: { trigger: 'axis' },
    legend: { data: ['实际分布', '正态曲线'] },
    xAxis: {
      type: 'category',
      data: bins.map(b => b.range)
    },
    yAxis: { type: 'value' },
    series: [
      {
        name: '实际分布',
        type: 'bar',
        data: bins.map(b => b.count),
        itemStyle: { color: '#409EFF' }
      },
      ...(showNormalCurve.value ? [{
        name: '正态曲线',
        type: 'line',
        data: normalCurve.map(point => point[1]),
        smooth: true,
        lineStyle: { color: '#F56C6C' }
      }] : [])
    ]
  }
  
  normalDistributionChart.setOption(option)
}

const updateClassComparisonChart = () => {
  if (!classComparisonChart) return
  
  const compareClasses = selectedCompareClasses.value.length > 0 
    ? selectedCompareClasses.value 
    : classList.value.slice(0, 3).map(c => c.id)
  
  const indicators = [
    { name: '平均分', max: 100 },
    { name: '优秀率', max: 100 },
    { name: '及格率', max: 100 },
    { name: '参与度', max: 100 },
    { name: '进步率', max: 100 }
  ]
  
  const seriesData = compareClasses.map(classId => {
    const className = classList.value.find(c => c.id === classId)?.name || ''
    const classScores = scoresList.value.filter(s => s.classId === classId)
    
    return {
      name: className,
      value: [
        classScores.reduce((sum, s) => sum + s.score, 0) / classScores.length || 0,
        (classScores.filter(s => s.score >= 90).length / classScores.length) * 100 || 0,
        (classScores.filter(s => s.score >= 60).length / classScores.length) * 100 || 0,
        Math.random() * 30 + 70, // 模拟参与度
        Math.random() * 20 + 80  // 模拟进步率
      ]
    }
  })
  
  const option = {
    title: { text: '班级对比分析', left: 'center' },
    tooltip: {},
    radar: {
      indicator: indicators,
      radius: '70%'
    },
    series: [{
      type: 'radar',
      data: seriesData
    }]
  }
  
  classComparisonChart.setOption(option)
}

const updateSubjectChart = () => {
  if (!subjectComparisonChart) return
  
  const subjectData = selectedSubjects.value.map(subject => {
    const subjectScores = scoresList.value.filter(s => s.subject === subject)
    return {
      name: subject,
      average: subjectScores.reduce((sum, s) => sum + s.score, 0) / subjectScores.length || 0,
      highest: Math.max(...subjectScores.map(s => s.score)) || 0,
      lowest: Math.min(...subjectScores.map(s => s.score)) || 0
    }
  })
  
  const option = {
    title: { text: '学科成绩对比', left: 'center' },
    tooltip: { trigger: 'axis' },
    legend: { data: ['平均分', '最高分', '最低分'] },
    xAxis: {
      type: 'category',
      data: subjectData.map(d => d.name)
    },
    yAxis: { type: 'value' },
    series: [
      {
        name: '平均分',
        type: 'bar',
        data: subjectData.map(d => d.average),
        itemStyle: { color: '#409EFF' }
      },
      {
        name: '最高分',
        type: 'bar',
        data: subjectData.map(d => d.highest),
        itemStyle: { color: '#67C23A' }
      },
      {
        name: '最低分',
        type: 'bar',
        data: subjectData.map(d => d.lowest),
        itemStyle: { color: '#F56C6C' }
      }
    ]
  }
  
  subjectComparisonChart.setOption(option)
}

const updateTrendChart = () => {
  updateAverageTrendChart()
}

// 初始化图表
const initCharts = () => {
  nextTick(() => {
    scoreDistributionChart = echarts.init(document.getElementById('scoreDistribution'))
    averageTrendChart = echarts.init(document.getElementById('averageTrend'))
    normalDistributionChart = echarts.init(document.getElementById('normalDistribution'))
    classComparisonChart = echarts.init(document.getElementById('classComparison'))
    subjectComparisonChart = echarts.init(document.getElementById('subjectComparison'))
    
    updateCharts()
    
    // 窗口大小变化时重新渲染图表
    window.addEventListener('resize', () => {
      scoreDistributionChart?.resize()
      averageTrendChart?.resize()
      normalDistributionChart?.resize()
      classComparisonChart?.resize()
      subjectComparisonChart?.resize()
    })
  })
}

// 组件挂载时初始化
onMounted(() => {
  initCharts()
})
</script>

<style scoped>
.score-management {
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

.charts-container {
  margin-bottom: 30px;
}

.chart-row {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
}

.chart-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  flex: 1;
}

.chart-card.full-width {
  width: 100%;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 1px solid #e2e8f0;
}

.chart-header h3 {
  margin: 0;
  color: #1a202c;
  font-size: 18px;
  font-weight: 600;
}

.chart-controls {
  display: flex;
  gap: 12px;
  align-items: center;
}

.chart-content {
  position: relative;
}

.chart {
  width: 100%;
  height: 300px;
}

.chart.large-chart {
  height: 400px;
}

.score-table {
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

.score-excellent {
  color: #16a085;
  font-weight: 600;
}

.score-good {
  color: #f39c12;
  font-weight: 600;
}

.score-pass {
  color: #3498db;
  font-weight: 600;
}

.score-fail {
  color: #e74c3c;
  font-weight: 600;
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

.class-options {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 12px;
  max-height: 300px;
  overflow-y: auto;
}

.class-option {
  padding: 8px 12px;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  transition: all 0.3s;
}

.class-option:hover {
  background: #f7fafc;
  border-color: #cbd5e0;
}

.dialog-footer {
  display: flex;
  gap: 12px;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .chart-row {
    flex-direction: column;
  }
  
  .overview-cards {
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  }
}

@media (max-width: 768px) {
  .score-management {
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
}

@media (max-width: 480px) {
  .overview-card {
    flex-direction: column;
    text-align: center;
  }
  
  .card-icon {
    margin-right: 0;
    margin-bottom: 12px;
  }
  
  .stats-summary {
    flex-direction: column;
  }
  
  .header-actions {
    flex-direction: column;
  }
}

/* 表格样式优化 */
.el-table {
  font-size: 14px;
}

.el-table th {
  background: #f8f9fa;
  color: #495057;
  font-weight: 600;
}

.el-table td {
  padding: 12px 0;
}

/* 对话框样式优化 */
.el-dialog {
  border-radius: 12px;
}

.el-dialog__header {
  padding: 24px 24px 12px;
  border-bottom: 1px solid #e2e8f0;
}

.el-dialog__body {
  padding: 20px 24px;
}

.el-dialog__footer {
  padding: 12px 24px 24px;
  border-top: 1px solid #e2e8f0;
}

/* 表单样式优化 */
.el-form-item {
  margin-bottom: 20px;
}

.el-form-item__label {
  font-weight: 600;
  color: #4a5568;
}

/* 按钮样式优化 */
.el-button {
  border-radius: 8px;
  font-weight: 500;
}

.el-button--primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
}

.el-button--success {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  border: none;
}

.el-button--info {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
  border: none;
}

/* 标签样式优化 */
.el-tag {
  border-radius: 6px;
  font-weight: 500;
}

/* 选择器样式优化 */
.el-select {
  border-radius: 8px;
}

.el-input {
  border-radius: 8px;
}

/* 分页样式优化 */
.el-pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.el-pagination .el-pager li {
  border-radius: 6px;
}

/* 卡片阴影效果 */
.overview-card,
.chart-card,
.score-table {
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
}

/* 动画效果 */
.overview-card,
.chart-card {
  transition: all 0.3s ease;
}

.overview-card:hover,
.chart-card:hover {
  box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05);
}

/* 加载状态 */
.chart-loading {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 300px;
  color: #718096;
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 300px;
  color: #718096;
}

.empty-state .el-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

/* 滚动条样式 */
::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}

::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 4px;
}

::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 4px;
}

::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}
</style>
