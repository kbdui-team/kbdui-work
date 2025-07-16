<template>
    <div class="student-management">
      <!-- 顶部操作栏 -->
      <div class="management-header">
        <div class="header-left">
          <h2>学生管理</h2>
          <div class="stats-summary">
            <el-tag type="primary">总学生数: {{ totalStudents }}</el-tag>
            <el-tag type="success">在线学生: {{ onlineStudents }}</el-tag>
            <el-tag type="warning">今日活跃: {{ activeToday }}</el-tag>
          </div>
        </div>
        <div class="header-actions">
          <el-button type="primary" @click="showAddStudentDialog" :icon="Plus">
            添加学生
          </el-button>
          <el-button type="success" @click="showBatchImportDialog" :icon="Upload">
            批量导入
          </el-button>
          <el-button type="info" @click="exportStudents" :icon="Download">
            导出数据
          </el-button>
        </div>
      </div>
  
      <!-- 搜索和筛选区域 -->
      <div class="search-filters">
        <div class="search-section">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索学生姓名、学号或邮箱"
            :prefix-icon="Search"
            style="width: 300px"
            clearable
            @input="handleSearch"
          />
        </div>
        <div class="filter-section">
          <el-select v-model="selectedClass" placeholder="选择班级" clearable @change="handleClassFilter">
            <el-option label="全部班级" value="" />
            <el-option
              v-for="cls in classList"
              :key="cls.id"
              :label="cls.name"
              :value="cls.id"
            />
          </el-select>
          <el-select v-model="selectedStatus" placeholder="学习状态" clearable @change="handleStatusFilter">
            <el-option label="全部状态" value="" />
            <el-option label="活跃" value="active" />
            <el-option label="不活跃" value="inactive" />
            <el-option label="新学生" value="new" />
          </el-select>
        </div>
      </div>
  
      <!-- 班级管理卡片 -->
      <div class="class-management">
        <h3>班级管理</h3>
        <div class="class-cards">
          <div
            v-for="cls in classList"
            :key="cls.id"
            class="class-card"
            :class="{ active: selectedClass === cls.id }"
            @click="selectClass(cls.id)"
          >
            <div class="class-header">
              <div class="class-info">
                <h4>{{ cls.name }}</h4>
                <p>{{ cls.description }}</p>
              </div>
              <div class="class-actions">
                <el-dropdown @command="handleClassAction">
                  <el-button type="text" :icon="More" />
                  <template #dropdown>
                    <el-dropdown-menu>
                      <el-dropdown-item :command="`edit-${cls.id}`">编辑班级</el-dropdown-item>
                      <el-dropdown-item :command="`delete-${cls.id}`" divided>删除班级</el-dropdown-item>
                    </el-dropdown-menu>
                  </template>
                </el-dropdown>
              </div>
            </div>
            <div class="class-stats">
              <div class="stat-item">
                <span class="stat-number">{{ cls.studentCount }}</span>
                <span class="stat-label">学生数</span>
              </div>
              <div class="stat-item">
                <span class="stat-number">{{ cls.averageScore }}%</span>
                <span class="stat-label">平均分</span>
              </div>
              <div class="stat-item">
                <span class="stat-number">{{ cls.activeRate }}%</span>
                <span class="stat-label">活跃率</span>
              </div>
            </div>
          </div>
          <div class="class-card add-class-card" @click="showAddClassDialog">
            <div class="add-class-content">
              <el-icon class="add-icon"><Plus /></el-icon>
              <span>添加新班级</span>
            </div>
          </div>
        </div>
      </div>
  
      <!-- 学生列表 -->
      <div class="student-list">
        <el-table
          :data="filteredStudents"
          style="width: 100%"
          @selection-change="handleSelectionChange"
          row-key="id"
        >
          <el-table-column type="selection" width="55" />
          <el-table-column label="头像" width="80">
            <template #default="scope">
              <el-avatar :size="40" :src="scope.row.avatar">
                <el-icon><User /></el-icon>
              </el-avatar>
            </template>
          </el-table-column>
          <el-table-column prop="name" label="姓名" width="120" sortable />
          <el-table-column prop="studentId" label="学号" width="120" sortable />
          <el-table-column prop="className" label="班级" width="150" />
          <el-table-column prop="email" label="邮箱" width="200" show-overflow-tooltip />
          <el-table-column prop="phone" label="电话" width="130" />
          <el-table-column label="学习统计" width="200">
            <template #default="scope">
              <div class="study-stats">
                <div class="stat-row">
                  <span>完成: {{ scope.row.completedQuizzes }}</span>
                  <span>平均分: {{ scope.row.averageScore }}%</span>
                </div>
                <div class="stat-row">
                  <span>排名: {{ scope.row.rank }}</span>
                  <span>活跃度: {{ scope.row.activityLevel }}</span>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="状态" width="100">
            <template #default="scope">
              <el-tag
                :type="getStatusType(scope.row.status)"
                size="small"
              >
                {{ getStatusText(scope.row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="lastLoginTime" label="最后登录" width="150" sortable />
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="scope">
              <el-button size="small" type="primary" @click="viewStudentDetail(scope.row)">
                详情
              </el-button>
              <el-button size="small" type="info" @click="viewQuizHistory(scope.row)">
                答题记录
              </el-button>
              <el-dropdown @command="handleStudentAction">
                <el-button size="small" type="text" :icon="More" />
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item :command="`edit-${scope.row.id}`">编辑</el-dropdown-item>
                    <el-dropdown-item :command="`reset-password-${scope.row.id}`">重置密码</el-dropdown-item>
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
            :total="filteredStudents.length"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </div>
  
      <!-- 学生详情对话框 -->
      <el-dialog
        v-model="studentDetailVisible"
        title="学生详情"
        width="800px"
        :before-close="handleDetailClose"
      >
        <div v-if="selectedStudent" class="student-detail">
          <div class="detail-header">
            <el-avatar :size="80" :src="selectedStudent.avatar">
              <el-icon><User /></el-icon>
            </el-avatar>
            <div class="student-info">
              <h3>{{ selectedStudent.name }}</h3>
              <p>学号: {{ selectedStudent.studentId }}</p>
              <p>班级: {{ selectedStudent.className }}</p>
              <el-tag :type="getStatusType(selectedStudent.status)" size="small">
                {{ getStatusText(selectedStudent.status) }}
              </el-tag>
            </div>
          </div>
          
          <el-tabs v-model="activeDetailTab">
            <el-tab-pane label="基本信息" name="basic">
              <div class="basic-info">
                <el-descriptions :column="2" border>
                  <el-descriptions-item label="姓名">{{ selectedStudent.name }}</el-descriptions-item>
                  <el-descriptions-item label="学号">{{ selectedStudent.studentId }}</el-descriptions-item>
                  <el-descriptions-item label="邮箱">{{ selectedStudent.email }}</el-descriptions-item>
                  <el-descriptions-item label="电话">{{ selectedStudent.phone }}</el-descriptions-item>
                  <el-descriptions-item label="班级">{{ selectedStudent.className }}</el-descriptions-item>
                  <el-descriptions-item label="入学时间">{{ selectedStudent.enrollmentDate }}</el-descriptions-item>
                  <el-descriptions-item label="最后登录">{{ selectedStudent.lastLoginTime }}</el-descriptions-item>
                  <el-descriptions-item label="注册时间">{{ selectedStudent.registerTime }}</el-descriptions-item>
                </el-descriptions>
              </div>
            </el-tab-pane>
            
            <el-tab-pane label="学习统计" name="stats">
              <div class="learning-stats">
                <div class="stats-cards">
                  <div class="stats-card">
                    <div class="stats-icon">
                      <el-icon><EditPen /></el-icon>
                    </div>
                    <div class="stats-content">
                      <div class="stats-number">{{ selectedStudent.completedQuizzes }}</div>
                      <div class="stats-label">完成测试</div>
                    </div>
                  </div>
                  <div class="stats-card">
                    <div class="stats-icon">
                      <el-icon><Trophy /></el-icon>
                    </div>
                    <div class="stats-content">
                      <div class="stats-number">{{ selectedStudent.averageScore }}%</div>
                      <div class="stats-label">平均得分</div>
                    </div>
                  </div>
                  <div class="stats-card">
                    <div class="stats-icon">
                      <el-icon><TrendCharts /></el-icon>
                    </div>
                    <div class="stats-content">
                      <div class="stats-number">{{ selectedStudent.rank }}</div>
                      <div class="stats-label">班级排名</div>
                    </div>
                  </div>
                  <div class="stats-card">
                    <div class="stats-icon">
                      <el-icon><Clock /></el-icon>
                    </div>
                    <div class="stats-content">
                      <div class="stats-number">{{ selectedStudent.totalStudyTime }}h</div>
                      <div class="stats-label">学习时长</div>
                    </div>
                  </div>
                </div>
              </div>
            </el-tab-pane>
          </el-tabs>
        </div>
      </el-dialog>
  
      <!-- 答题记录对话框 -->
      <el-dialog
        v-model="quizHistoryVisible"
        title="答题记录"
        width="1000px"
        :before-close="handleHistoryClose"
      >
        <div v-if="selectedStudent" class="quiz-history">
          <div class="history-header">
            <h4>{{ selectedStudent.name }} 的答题记录</h4>
            <div class="history-stats">
              <el-statistic title="总测试次数" :value="quizHistory.length" />
              <el-statistic title="平均得分" :value="selectedStudent.averageScore" suffix="%" />
              <el-statistic title="最高得分" :value="getMaxScore()" suffix="%" />
            </div>
          </div>
          
          <el-table :data="quizHistory" style="width: 100%">
            <el-table-column prop="quizTitle" label="测试标题" width="200" />
            <el-table-column prop="category" label="分类" width="120" />
            <el-table-column prop="difficulty" label="难度" width="100">
              <template #default="scope">
                <el-tag :type="getDifficultyType(scope.row.difficulty)" size="small">
                  {{ scope.row.difficulty }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="score" label="得分" width="100" sortable />
            <el-table-column prop="totalQuestions" label="题目数" width="100" />
            <el-table-column prop="correctAnswers" label="正确数" width="100" />
            <el-table-column prop="timeSpent" label="用时" width="100" />
            <el-table-column prop="completedTime" label="完成时间" width="150" sortable />
            <el-table-column label="操作" width="120">
              <template #default="scope">
                <el-button size="small" type="primary" @click="viewQuizDetail(scope.row)">
                  查看详情
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-dialog>
  
      <!-- 添加学生对话框 -->
      <el-dialog
        v-model="addStudentVisible"
        title="添加学生"
        width="600px"
        :before-close="handleAddStudentClose"
      >
        <el-form :model="newStudentForm" :rules="studentRules" ref="newStudentFormRef" label-width="80px">
          <el-form-item label="姓名" prop="name">
            <el-input v-model="newStudentForm.name" placeholder="请输入学生姓名" />
          </el-form-item>
          <el-form-item label="学号" prop="studentId">
            <el-input v-model="newStudentForm.studentId" placeholder="请输入学号" />
          </el-form-item>
          <el-form-item label="邮箱" prop="email">
            <el-input v-model="newStudentForm.email" placeholder="请输入邮箱地址" />
          </el-form-item>
          <el-form-item label="电话" prop="phone">
            <el-input v-model="newStudentForm.phone" placeholder="请输入电话号码" />
          </el-form-item>
          <el-form-item label="班级" prop="classId">
            <el-select v-model="newStudentForm.classId" placeholder="选择班级" style="width: 100%">
              <el-option
                v-for="cls in classList"
                :key="cls.id"
                :label="cls.name"
                :value="cls.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="初始密码" prop="password">
            <el-input v-model="newStudentForm.password" type="password" placeholder="设置初始密码" />
          </el-form-item>
        </el-form>
        
        <template #footer>
          <span class="dialog-footer">
            <el-button @click="addStudentVisible = false">取消</el-button>
            <el-button type="primary" @click="submitNewStudent">确定</el-button>
          </span>
        </template>
      </el-dialog>
  
      <!-- 添加班级对话框 -->
      <el-dialog
        v-model="addClassVisible"
        title="添加班级"
        width="500px"
      >
        <el-form :model="newClassForm" :rules="classRules" ref="newClassFormRef" label-width="80px">
          <el-form-item label="班级名称" prop="name">
            <el-input v-model="newClassForm.name" placeholder="请输入班级名称" />
          </el-form-item>
          <el-form-item label="班级描述" prop="description">
            <el-input v-model="newClassForm.description" type="textarea" placeholder="请输入班级描述" />
          </el-form-item>
        </el-form>
        
        <template #footer>
          <span class="dialog-footer">
            <el-button @click="addClassVisible = false">取消</el-button>
            <el-button type="primary" @click="submitNewClass">确定</el-button>
          </span>
        </template>
      </el-dialog>
    </div>
  </template>
  
  <script setup lang="ts">
  import { ref, reactive, computed, onMounted } from 'vue'
  import { ElMessage, ElMessageBox } from 'element-plus'
  import {
    Plus, Upload, Download, Search, More, User, EditPen, 
    Trophy, TrendCharts, Clock
  } from '@element-plus/icons-vue'
  
  // 响应式数据
  const searchKeyword = ref('')
  const selectedClass = ref('')
  const selectedStatus = ref('')
  const currentPage = ref(1)
  const pageSize = ref(20)
  const selectedStudents = ref([])
  
  const studentDetailVisible = ref(false)
  const quizHistoryVisible = ref(false)
  const addStudentVisible = ref(false)
  const addClassVisible = ref(false)
  const activeDetailTab = ref('basic')
  
  const selectedStudent = ref(null)
  
  // 表单数据
  const newStudentForm = reactive({
    name: '',
    studentId: '',
    email: '',
    phone: '',
    classId: '',
    password: ''
  })
  
  const newClassForm = reactive({
    name: '',
    description: ''
  })
  
  // 验证规则
  const studentRules = {
    name: [{ required: true, message: '请输入学生姓名', trigger: 'blur' }],
    studentId: [{ required: true, message: '请输入学号', trigger: 'blur' }],
    email: [
      { required: true, message: '请输入邮箱地址', trigger: 'blur' },
      { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
    ],
    classId: [{ required: true, message: '请选择班级', trigger: 'change' }],
    password: [{ required: true, message: '请设置初始密码', trigger: 'blur' }]
  }
  
  const classRules = {
    name: [{ required: true, message: '请输入班级名称', trigger: 'blur' }]
  }
  
  // 模拟数据
  const classList = ref([
    {
      id: 1,
      name: '计算机科学与技术2021级1班',
      description: '计算机科学与技术专业一班',
      studentCount: 45,
      averageScore: 87,
      activeRate: 92
    },
    {
      id: 2,
      name: '计算机科学与技术2021级2班',
      description: '计算机科学与技术专业二班',
      studentCount: 42,
      averageScore: 85,
      activeRate: 88
    },
    {
      id: 3,
      name: '软件工程2021级1班',
      description: '软件工程专业一班',
      studentCount: 38,
      averageScore: 89,
      activeRate: 95
    }
  ])
  
  const studentsList = ref([
    {
      id: 1,
      name: '张三',
      studentId: '2021001001',
      email: 'zhangsan@email.com',
      phone: '138****8888',
      classId: 1,
      className: '计算机科学与技术2021级1班',
      avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
      completedQuizzes: 15,
      averageScore: 87,
      rank: 5,
      activityLevel: '高',
      status: 'active',
      lastLoginTime: '2024-07-15 09:30',
      enrollmentDate: '2021-09-01',
      registerTime: '2021-08-25 14:30',
      totalStudyTime: 48
    },
    {
      id: 2,
      name: '李四',
      studentId: '2021001002',
      email: 'lisi@email.com',
      phone: '139****9999',
      classId: 1,
      className: '计算机科学与技术2021级1班',
      avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
      completedQuizzes: 12,
      averageScore: 92,
      rank: 2,
      activityLevel: '高',
      status: 'active',
      lastLoginTime: '2024-07-15 08:45',
      enrollmentDate: '2021-09-01',
      registerTime: '2021-08-25 15:20',
      totalStudyTime: 52
    },
    {
      id: 3,
      name: '王五',
      studentId: '2021001003',
      email: 'wangwu@email.com',
      phone: '137****7777',
      classId: 2,
      className: '计算机科学与技术2021级2班',
      avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
      completedQuizzes: 8,
      averageScore: 78,
      rank: 15,
      activityLevel: '中',
      status: 'inactive',
      lastLoginTime: '2024-07-13 16:20',
      enrollmentDate: '2021-09-01',
      registerTime: '2021-08-26 10:15',
      totalStudyTime: 32
    }
  ])
  
  const quizHistory = ref([
    {
      id: 1,
      quizTitle: 'JavaScript基础测试',
      category: '前端开发',
      difficulty: '简单',
      score: 95,
      totalQuestions: 20,
      correctAnswers: 19,
      timeSpent: '15分钟',
      completedTime: '2024-07-15 09:30'
    },
    {
      id: 2,
      quizTitle: 'HTML/CSS进阶',
      category: '前端开发',
      difficulty: '中等',
      score: 88,
      totalQuestions: 25,
      correctAnswers: 22,
      timeSpent: '23分钟',
      completedTime: '2024-07-14 14:20'
    }
  ])
  
  // 计算属性
  const totalStudents = computed(() => studentsList.value.length)
  const onlineStudents = computed(() => 
    studentsList.value.filter(s => s.status === 'active').length
  )
  const activeToday = computed(() => {
    const today = new Date().toDateString()
    return studentsList.value.filter(s => 
      new Date(s.lastLoginTime).toDateString() === today
    ).length
  })
  
  const filteredStudents = computed(() => {
    let filtered = studentsList.value
  
    // 搜索过滤
    if (searchKeyword.value) {
      const keyword = searchKeyword.value.toLowerCase()
      filtered = filtered.filter(student =>
        student.name.toLowerCase().includes(keyword) ||
        student.studentId.includes(keyword) ||
        student.email.toLowerCase().includes(keyword)
      )
    }
  
    // 班级过滤
    if (selectedClass.value) {
      filtered = filtered.filter(student => student.classId === selectedClass.value)
    }
  
    // 状态过滤
    if (selectedStatus.value) {
      filtered = filtered.filter(student => student.status === selectedStatus.value)
    }
  
    return filtered
  })
  
  // 方法
  const handleSearch = () => {
    currentPage.value = 1
  }
  
  const handleClassFilter = () => {
    currentPage.value = 1
  }
  
  const handleStatusFilter = () => {
    currentPage.value = 1
  }
  
  const selectClass = (classId: number) => {
    selectedClass.value = classId
  }
  
  const handleSelectionChange = (selection: any[]) => {
    selectedStudents.value = selection
  }
  
  const viewStudentDetail = (student: any) => {
    selectedStudent.value = student
    studentDetailVisible.value = true
  }
  
  const viewQuizHistory = (student: any) => {
    selectedStudent.value = student
    quizHistoryVisible.value = true
  }
  
  const getStatusType = (status: string) => {
    const types: Record<string, string> = {
      'active': 'success',
      'inactive': 'warning',
      'new': 'info'
    }
    return types[status] || 'info'
  }
  
  const getStatusText = (status: string) => {
    const texts: Record<string, string> = {
      'active': '活跃',
      'inactive': '不活跃',
      'new': '新学生'
    }
    return texts[status] || status
  }
  
  const getDifficultyType = (difficulty: string) => {
    const types: Record<string, string> = {
      '简单': 'success',
      '中等': 'warning',
      '困难': 'danger'
    }
    return types[difficulty] || 'info'
  }
  
  const getMaxScore = () => {
    return Math.max(...quizHistory.value.map(h => h.score))
  }
  
  const showAddStudentDialog = () => {
    addStudentVisible.value = true
  }
  
  const showAddClassDialog = () => {
    addClassVisible.value = true
  }
  
  const showBatchImportDialog = () => {
    ElMessage.info('批量导入功能开发中...')
  }
  
  const exportStudents = () => {
    ElMessage.success('数据导出成功!')
  }
  
  const handleClassAction = (command: string) => {
    const [action, id] = command.split('-')
    if (action === 'edit') {
      ElMessage.info('编辑班级功能开发中...')
    } else if (action === 'delete') {
      ElMessageBox.confirm('确定要删除这个班级吗？', '确认删除', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        ElMessage.success('删除成功!')
      })
    }
  }
  
  const handleStudentAction = (command: string) => {
    const [action, id] = command.split('-')
    const studentId = parseInt(id)
    
    if (action === 'edit') {
      ElMessage.info('编辑学生功能开发中...')
    } else if (action === 'reset-password') {
      ElMessageBox.confirm('确定要重置该学生的密码吗？', '确认重置', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        ElMessage.success('密码重置成功!')
      })
    } else if (action === 'delete') {
      ElMessageBox.confirm('确定要删除这个学生吗？', '确认删除', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        const index = studentsList.value.findIndex(s => s.id === studentId)
        if (index > -1) {
          studentsList.value.splice(index, 1)
          ElMessage.success('删除成功!')
        }
      })
    }
  }
  
  const submitNewStudent = () => {
    // 这里应该调用API添加学生
    const className = classList.value.find(c => c.id === newStudentForm.classId)?.name || ''
    
    const newStudent = {
      id: Date.now(),
      name: newStudentForm.name,
      studentId: newStudentForm.studentId,
      email: newStudentForm.email,
      phone: newStudentForm.phone,
      classId: newStudentForm.classId,
      className: className,
      avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
      completedQuizzes: 0,
      averageScore: 0,
      rank: 0,
      activityLevel: '新',
      status: 'new',
      lastLoginTime: '从未登录',
      enrollmentDate: new Date().toISOString().split('T')[0],
      registerTime: new Date().toLocaleString(),
      totalStudyTime: 0
    }
    
    studentsList.value.unshift(newStudent)
    addStudentVisible.value = false
    
    // 重置表单
    Object.keys(newStudentForm).forEach(key => {
      newStudentForm[key] = ''
    })
    
    ElMessage.success('学生添加成功!')
  }
  

const submitNewClass = () => {
  const newClass = {
    id: Date.now(),
    name: newClassForm.name,
    description: newClassForm.description,
    studentCount: 0,
    averageScore: 0,
    activeRate: 0
  }
  
  classList.value.push(newClass)
  addClassVisible.value = false
  
  // 重置表单
  newClassForm.name = ''
  newClassForm.description = ''
  
  ElMessage.success('班级添加成功!')
}

const handleDetailClose = () => {
  studentDetailVisible.value = false
  selectedStudent.value = null
}

const handleHistoryClose = () => {
  quizHistoryVisible.value = false
  selectedStudent.value = null
}

const handleAddStudentClose = () => {
  addStudentVisible.value = false
  // 重置表单
  Object.keys(newStudentForm).forEach(key => {
    newStudentForm[key] = ''
  })
}

const viewQuizDetail = (quiz: any) => {
  ElMessage.info('查看测试详情功能开发中...')
}

const handleSizeChange = (size: number) => {
  pageSize.value = size
  currentPage.value = 1
}

const handleCurrentChange = (page: number) => {
  currentPage.value = page
}

// 组件挂载时的初始化
onMounted(() => {
  console.log('学生管理组件已加载')
})
</script>

<style scoped>
.student-management {
  padding: 20px;
  background: #f8f9fa;
  min-height: 100%;
}

.management-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20px;
  padding: 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.header-left h2 {
  margin: 0 0 10px 0;
  color: #333;
  font-size: 24px;
}

.stats-summary {
  display: flex;
  gap: 10px;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.search-filters {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 15px 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.filter-section {
  display: flex;
  gap: 15px;
}

.class-management {
  margin-bottom: 30px;
}

.class-management h3 {
  margin: 0 0 15px 0;
  color: #333;
  font-size: 18px;
}

.class-cards {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.class-card {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: all 0.3s ease;
  border: 2px solid transparent;
}

.class-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.class-card.active {
  border-color: #409eff;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}

.class-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 15px;
}

.class-info h4 {
  margin: 0 0 5px 0;
  color: #333;
  font-size: 16px;
}

.class-info p {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.class-stats {
  display: flex;
  justify-content: space-between;
}

.stat-item {
  text-align: center;
}

.stat-number {
  display: block;
  font-size: 20px;
  font-weight: 600;
  color: #409eff;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 12px;
  color: #666;
}

.add-class-card {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 120px;
  border: 2px dashed #ddd;
  background: #fafafa;
}

.add-class-card:hover {
  border-color: #409eff;
  background: #f0f9ff;
}

.add-class-content {
  text-align: center;
  color: #666;
}

.add-icon {
  font-size: 24px;
  margin-bottom: 8px;
  color: #409eff;
}

.student-list {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.study-stats {
  font-size: 12px;
}

.stat-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 4px;
}

.stat-row:last-child {
  margin-bottom: 0;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.student-detail {
  max-height: 600px;
}

.detail-header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid #eee;
}

.student-info {
  margin-left: 20px;
}

.student-info h3 {
  margin: 0 0 8px 0;
  color: #333;
  font-size: 20px;
}

.student-info p {
  margin: 0 0 5px 0;
  color: #666;
  font-size: 14px;
}

.basic-info {
  padding: 20px 0;
}

.learning-stats {
  padding: 20px 0;
}

.stats-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
}

.stats-card {
  display: flex;
  align-items: center;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
  border-left: 4px solid #409eff;
}

.stats-icon {
  width: 48px;
  height: 48px;
  background: #409eff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
  color: white;
  font-size: 20px;
}

.stats-content {
  flex: 1;
}

.stats-number {
  font-size: 24px;
  font-weight: 600;
  color: #333;
  margin-bottom: 4px;
}

.stats-label {
  font-size: 14px;
  color: #666;
}

.quiz-history {
  max-height: 600px;
}

.history-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #eee;
}

.history-header h4 {
  margin: 0;
  color: #333;
  font-size: 18px;
}

.history-stats {
  display: flex;
  gap: 30px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .student-management {
    padding: 10px;
  }
  
  .management-header {
    flex-direction: column;
    gap: 15px;
  }
  
  .search-filters {
    flex-direction: column;
    gap: 15px;
  }
  
  .class-cards {
    grid-template-columns: 1fr;
  }
  
  .stats-cards {
    grid-template-columns: 1fr;
  }
  
  .history-header {
    flex-direction: column;
    gap: 15px;
  }
  
  .history-stats {
    justify-content: space-around;
    width: 100%;
  }
}

@media (max-width: 480px) {
  .header-actions {
    flex-direction: column;
    width: 100%;
  }
  
  .filter-section {
    flex-direction: column;
    width: 100%;
  }
  
  .stat-row {
    flex-direction: column;
    gap: 2px;
  }
}
</style>
