<template>
  <div class="container">
    <!-- 通知消息 -->
    <div v-if="notification" class="notification">
      <span>{{ notification.message }}</span>
      <button @click="notification = null" class="close-btn">×</button>
    </div>

    <div class="main-content">
      <!-- 页面标题 -->
      <div class="header">
        <div class="title-section">
          <div class="icon">👨‍🎓</div>
          <div class="title-text">
            <h1>讲座学生管理</h1>
            <p>按讲座分类管理学生参与信息</p>
          </div>
        </div>
      </div>

      <!-- 统计卡片 -->
      <div class="stats-grid">
        <div class="stat-card">
          <div class="stat-content">
            <div class="stat-text">
              <p class="stat-label">总学生数</p>
              <p class="stat-value">{{ totalStudents }}</p>
            </div>
            <div class="stat-icon blue">👨‍🎓</div>
          </div>
        </div>
        
        <div class="stat-card">
          <div class="stat-content">
            <div class="stat-text">
              <p class="stat-label">讲座总数</p>
              <p class="stat-value">{{ totalLectures }}</p>
            </div>
            <div class="stat-icon green">📚</div>
          </div>
        </div>
        
        <div class="stat-card">
          <div class="stat-content">
            <div class="stat-text">
              <p class="stat-label">平均参与度</p>
              <p class="stat-value">{{ averageParticipation }}</p>
            </div>
            <div class="stat-icon purple">📊</div>
          </div>
        </div>
        
        <div class="stat-card">
          <div class="stat-content">
            <div class="stat-text">
              <p class="stat-label">最热门讲座</p>
              <p class="stat-value">{{ mostPopularLecture }}</p>
            </div>
            <div class="stat-icon orange">🔥</div>
          </div>
        </div>
      </div>

      <!-- 操作栏 -->
      <div class="toolbar">
        <div class="search-filters">
          <div class="search-box">
            <span class="search-icon">🔍</span>
            <input
              type="text"
              placeholder="搜索学生姓名..."
              v-model="searchTerm"
              class="search-input"
            />
          </div>
          
          <select v-model="lectureFilter" class="filter-select">
            <option value="all">所有讲座</option>
            <option 
              v-for="lecture in lectureList" 
              :key="lecture.id" 
              :value="lecture.id"
            >
              {{ lecture.title }}
            </option>
          </select>
        </div>
        
        <div class="action-buttons">
          <button @click="openModal('add')" class="btn-primary">
            <span>➕</span>
            添加学生
          </button>
          
          <button
            v-if="selectedRows.length > 0"
            @click="handleBatchDelete"
            class="btn-danger"
          >
            <span>🗑️</span>
            批量删除 ({{ selectedRows.length }})
          </button>
          
          <button @click="loadData" :disabled="loading" class="btn-secondary">
            <span :class="{ 'spinning': loading }">🔄</span>
            刷新
          </button>
        </div>
      </div>

      <!-- 按讲座分类展示 -->
      <div class="lectures-container">
        <div v-if="loading" class="loading-container">
          <span class="spinning">🔄</span>
          <span>加载中...</span>
        </div>
        
        <div v-else-if="groupedData.length === 0" class="empty-container">
          <div class="empty-icon">📭</div>
          <h3>暂无学生数据</h3>
          <p>还没有学生参与任何讲座</p>
        </div>
        
        <div v-else>
          <div 
            v-for="lectureGroup in groupedData" 
            :key="lectureGroup.lectureId" 
            class="lecture-group"
          >
            <!-- 讲座标题 -->
            <div class="lecture-header">
              <div class="lecture-info">
                <h3 class="lecture-title">
                  📚 {{ getLectureName(lectureGroup.lectureId) }}
                  <span class="lecture-id">ID: {{ lectureGroup.lectureId }}</span>
                </h3>
                <p class="student-count">{{ lectureGroup.students.length }} 名学生</p>
              </div>
              <div class="lecture-actions">
                <button 
                  @click="toggleLectureExpanded(lectureGroup.lectureId)"
                  class="expand-btn"
                >
                  <span v-if="expandedLectures.includes(lectureGroup.lectureId)">📁</span>
                  <span v-else>📂</span>
                  {{ expandedLectures.includes(lectureGroup.lectureId) ? '收起' : '展开' }}
                </button>
              </div>
            </div>

            <!-- 学生列表 -->
            <div v-if="expandedLectures.includes(lectureGroup.lectureId)" class="students-container">
              <div class="students-grid">
                <div 
                  v-for="student in lectureGroup.students" 
                  :key="student.id"
                  class="student-card"
                  :class="{ 'selected': selectedRows.includes(student.id) }"
                >
                  <div class="student-card-header">
                    <input
                      type="checkbox"
                      :checked="selectedRows.includes(student.id)"
                      @change="handleSelectRow(student.id, $event.target.checked)"
                      class="student-checkbox"
                    />
                    <div class="student-avatar">
                      {{ getStudentInitials(student.userName) }}
                    </div>
                  </div>
                  
                  <div class="student-info">
                    <h4 class="student-name">{{ student.userName || '未知学生' }}</h4>
                    <p class="student-id">ID: {{ student.id }}</p>
                    <p class="user-id">用户ID: {{ student.userId }}</p>
                  </div>
                  
                  <div class="student-actions">
                    <button @click="openModal('view', student)" class="action-btn view" title="查看详情">👁️</button>
                    <button @click="openModal('edit', student)" class="action-btn edit" title="编辑">✏️</button>
                    <button @click="handleDelete(student.id)" class="action-btn delete" title="删除">🗑️</button>
                  </div>
                </div>
              </div>
              
              <!-- 如果该讲座没有学生 -->
              <div v-if="lectureGroup.students.length === 0" class="no-students">
                <div class="no-students-icon">👤</div>
                <p>该讲座暂无学生参与</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 分页 -->
      <div v-if="totalPages > 1" class="pagination">
        <div class="pagination-info">
          显示第 {{ (currentPage - 1) * pageSize + 1 }} 到 {{ Math.min(currentPage * pageSize, totalRecords) }} 条，共 {{ totalRecords }} 条记录
        </div>
        <div class="pagination-controls">
          <button
            @click="currentPage = Math.max(1, currentPage - 1)"
            :disabled="currentPage === 1"
            class="page-btn"
          >
            上一页
          </button>
          
          <div class="page-numbers">
            <button
              v-for="page in getPageNumbers()"
              :key="page"
              @click="currentPage = page"
              :class="currentPage === page ? 'page-btn active' : 'page-btn'"
            >
              {{ page }}
            </button>
          </div>
          
          <button
            @click="currentPage = Math.min(totalPages, currentPage + 1)"
            :disabled="currentPage === totalPages"
            class="page-btn"
          >
            下一页
          </button>
        </div>
      </div>
    </div>

    <!-- 模态框 -->
    <div v-if="showModal" class="modal-overlay">
      <div class="modal">
        <div class="modal-header">
          <h3>{{ modalMode === 'add' ? '添加学生' : modalMode === 'edit' ? '编辑学生信息' : '学生详情' }}</h3>
          <button @click="showModal = false" class="close-btn">✕</button>
        </div>
        
        <div class="modal-body">
          <div class="form-group">
            <label>讲座</label>
            <select
              v-if="modalMode !== 'view'"
              v-model="formData.lectureId"
              class="form-input"
            >
              <option value="">请选择讲座</option>
              <option 
                v-for="lecture in lectureList" 
                :key="lecture.id" 
                :value="lecture.id"
              >
                {{ lecture.title }}
              </option>
            </select>
            <div v-else class="view-field">
              {{ getLectureName(formData.lectureId) }}
            </div>
          </div>
          
          <div class="form-group">
            <label>选择学生</label>
            <select
              v-if="modalMode !== 'view'"
              v-model="formData.userId"
              class="form-input"
            >
              <option value="">请选择学生</option>
              <option 
                v-for="user in studentUserList" 
                :key="user.id" 
                :value="user.id"
              >
                {{ user.realName || user.name }}
              </option>
            </select>
            <div v-else class="view-field">
              {{ getUserNameById(formData.userId) }}
            </div>
          </div>
          
          <div v-if="modalMode !== 'view'" class="modal-actions">
            <button @click="showModal = false" class="btn-secondary">取消</button>
            <button @click="handleSubmit" :disabled="loading" class="btn-primary">
              <span v-if="loading" class="spinning">🔄</span>
              {{ modalMode === 'add' ? '添加' : '保存' }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'LectureStudentsManagement',
  data() {
    return {
      data: [],
      loading: false,
      selectedRows: [],
      currentPage: 1,
      pageSize: 20,
      totalPages: 0,
      totalRecords: 0,
      searchTerm: '',
      lectureFilter: 'all',
      showModal: false,
      modalMode: 'add',
      currentRecord: null,
      notification: null,
      userList: [],
      lectureList: [],
      expandedLectures: [], // 展开的讲座列表
      formData: {
        lectureId: '',
        userId: '',
        role: 'student' // 固定为学生
      }
    }
  },
  computed: {
    // 按讲座ID分组的数据
    groupedData() {
      const filteredData = this.getFilteredData()
      const groups = {}
      
      filteredData.forEach(student => {
        const lectureId = student.lectureId
        if (!groups[lectureId]) {
          groups[lectureId] = {
            lectureId: lectureId,
            students: []
          }
        }
        groups[lectureId].students.push(student)
      })
      
      // 转换为数组并按讲座ID排序
      return Object.values(groups).sort((a, b) => a.lectureId - b.lectureId)
    },

    // 只返回学生用户
    studentUserList() {
      return this.userList.filter(user => !user.userType || user.userType === 'student')
    },

    // 统计信息
    totalStudents() {
      return this.data.length
    },

    totalLectures() {
      return new Set(this.data.map(item => item.lectureId)).size
    },

    averageParticipation() {
      if (this.totalLectures === 0) return '0'
      return Math.round(this.totalStudents / this.totalLectures)
    },

    mostPopularLecture() {
      if (this.groupedData.length === 0) return 'N/A'
      const mostPopular = this.groupedData.reduce((max, current) => 
        current.students.length > max.students.length ? current : max
      )
      return mostPopular.students.length
    }
  },
  watch: {
    currentPage() {
      this.loadData()
    },
    pageSize() {
      this.loadData()
    },
    searchTerm() {
      this.currentPage = 1
      // 搜索在前端进行，不需要重新加载数据
    },
    lectureFilter() {
      this.currentPage = 1
      // 过滤在前端进行，不需要重新加载数据
    }
  },
  mounted() {
    this.loadData()
    this.loadUserList()
    this.loadLectureList()
  },
  methods: {
    // API基础URL
    getApiUrl() {
      return 'http://localhost:5555/lecture-participants'
    },

    getUserApiUrl() {
      return 'http://localhost:5555/user'
    },

    getLectureApiUrl() {
      return 'http://localhost:5555/lecture'
    },

    // 获取过滤后的数据
    getFilteredData() {
      let filtered = this.data
      
      // 搜索过滤
      if (this.searchTerm) {
        const term = this.searchTerm.toLowerCase()
        filtered = filtered.filter(item => 
          (item.userName || '').toLowerCase().includes(term)
        )
      }
      
      // 讲座过滤
      if (this.lectureFilter !== 'all') {
        filtered = filtered.filter(item => item.lectureId == this.lectureFilter)
      }
      
      return filtered
    },

    // 获取学生姓名首字母
    getStudentInitials(name) {
      if (!name) return '?'
      const names = name.split('')
      return names[0].toUpperCase()
    },

    // 切换讲座展开状态
    toggleLectureExpanded(lectureId) {
      const index = this.expandedLectures.indexOf(lectureId)
      if (index > -1) {
        this.expandedLectures.splice(index, 1)
      } else {
        this.expandedLectures.push(lectureId)
      }
    },

    // 根据讲座ID获取讲座名称
    getLectureName(lectureId) {
      const lecture = this.lectureList.find(l => l.id == lectureId)
      return lecture ? lecture.title : `讲座 ${lectureId}`
    },

    // 根据用户ID获取用户姓名
    getUserNameById(userId) {
      const user = this.userList.find(u => u.id == userId)
      return user ? (user.realName || user.name) : '未知用户'
    },

    // 获取用户信息
    async getUserById(userId) {
      try {
        const response = await fetch(`${this.getUserApiUrl()}/get/${userId}`)
        if (response.ok) {
          const data = await response.json()
          return data
        }
        return null
      } catch (error) {
        console.error(`获取用户${userId}信息失败:`, error)
        return null
      }
    },

    // 获取所有用户列表
    async loadUserList() {
      try {
        const response = await fetch(`${this.getUserApiUrl()}/list`)
        if (response.ok) {
          this.userList = await response.json()
          console.log('用户列表:', this.userList)
        }
      } catch (error) {
        console.error('获取用户列表失败:', error)
        this.userList = []
      }
    },

    // 获取讲座列表
    async loadLectureList() {
      try {
        const response = await fetch(`${this.getLectureApiUrl()}/list`)
        if (response.ok) {
          this.lectureList = await response.json()
          console.log('讲座列表:', this.lectureList)
          
          // 默认展开前3个讲座
          this.expandedLectures = this.lectureList.slice(0, 3).map(l => l.id)
        }
      } catch (error) {
        console.error('获取讲座列表失败:', error)
        this.lectureList = []
      }
    },

    // 构建查询条件
    buildConditions() {
      const conditions = {
        role: 'student' // 只查询学生
      }
      
      // 后端搜索暂时移除，在前端进行
      // if (this.searchTerm) {
      //   conditions.searchTerm = this.searchTerm
      // }
      
      return conditions
    },

    // 显示通知
    showNotification(message, type = 'success') {
      this.notification = { message, type }
      setTimeout(() => {
        this.notification = null
      }, 3000)
    },

    // 加载数据
    async loadData() {
      this.loading = true
      try {
        const conditions = this.buildConditions()
        const response = await fetch(`${this.getApiUrl()}/page?pageNo=${this.currentPage}&pageSize=${this.pageSize}`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(conditions)
        })
        const result = await response.json()
        
        console.log('原始数据:', result.records)
        
        if (result && result.records) {
          // 过滤掉教师，只保留学生
          const studentRecords = result.records.filter(item => item.role === 'student')
          
          // 提取所有唯一的用户ID
          const uniqueUserIds = [...new Set(studentRecords.map(item => item.userId))]
          
          // 批量获取用户信息
          const userPromises = uniqueUserIds.map(userId => this.getUserById(userId))
          const users = await Promise.all(userPromises)
          
          // 创建用户ID到用户信息的映射
          const userMap = {}
          users.forEach((user, index) => {
            const userId = uniqueUserIds[index]
            userMap[userId] = user || { id: userId, name: `用户${userId}` }
          })
          
          // 将用户姓名添加到数据中
          const dataWithUserNames = studentRecords.map(item => ({
            ...item,
            userName: userMap[item.userId].realName || userMap[item.userId].name
          }))
          
          console.log('处理后的学生数据:', dataWithUserNames)
          
          this.data = dataWithUserNames
          this.totalPages = Math.ceil(dataWithUserNames.length / this.pageSize)
          this.totalRecords = dataWithUserNames.length
        } else {
          this.data = []
          this.totalPages = 0
          this.totalRecords = 0
        }
      } catch (error) {
        console.error('加载数据失败:', error)
        this.showNotification('加载数据失败', 'error')
        this.data = []
      } finally {
        this.loading = false
      }
    },

    // 获取页码数组
    getPageNumbers() {
      const pages = []
      const maxPages = Math.min(5, this.totalPages)
      for (let i = 1; i <= maxPages; i++) {
        pages.push(i)
      }
      return pages
    },

    // 处理表单提交
    async handleSubmit() {
      if (!this.formData.lectureId || !this.formData.userId) {
        this.showNotification('请填写完整信息并选择学生', 'error')
        return
      }
      
      this.loading = true
      
      try {
        const submitData = {
          ...this.formData,
          role: 'student' // 固定为学生
        }
        
        console.log('提交数据:', submitData)
        
        let response
        if (this.modalMode === 'add') {
          response = await fetch(`${this.getApiUrl()}/add`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(submitData)
          })
          this.showNotification('添加成功')
        } else if (this.modalMode === 'edit') {
          response = await fetch(`${this.getApiUrl()}/update`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ ...submitData, id: this.currentRecord.id })
          })
          this.showNotification('更新成功')
        }
        
        this.showModal = false
        this.loadData()
        this.resetForm()
      } catch (error) {
        console.error('操作失败:', error)
        this.showNotification('操作失败', 'error')
      } finally {
        this.loading = false
      }
    },

    // 重置表单
    resetForm() {
      this.formData = {
        lectureId: '',
        userId: '',
        role: 'student'
      }
      this.currentRecord = null
    },

    // 处理删除
    async handleDelete(id) {
      if (!confirm('确定要删除这个学生记录吗？')) return
      
      this.loading = true
      try {
        await fetch(`${this.getApiUrl()}/delete/${id}`, {
          method: 'DELETE'
        })
        this.showNotification('删除成功')
        this.loadData()
        this.selectedRows = []
      } catch (error) {
        console.error('删除失败:', error)
        this.showNotification('删除失败', 'error')
      } finally {
        this.loading = false
      }
    },

    // 处理批量删除
    async handleBatchDelete() {
      if (this.selectedRows.length === 0) {
        this.showNotification('请选择要删除的记录', 'error')
        return
      }
      
      if (!confirm(`确定要删除选中的 ${this.selectedRows.length} 条学生记录吗？`)) return
      
      this.loading = true
      try {
        await fetch(`${this.getApiUrl()}/delete/batch`, {
          method: 'DELETE',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(this.selectedRows)
        })
        this.showNotification('批量删除成功')
        this.loadData()
        this.selectedRows = []
      } catch (error) {
        console.error('批量删除失败:', error)
        this.showNotification('批量删除失败', 'error')
      } finally {
        this.loading = false
      }
    },

    // 打开模态框
    openModal(mode, record = null) {
      this.modalMode = mode
      this.currentRecord = record
      
      if (mode === 'edit' && record) {
        this.formData = {
          lectureId: record.lectureId,
          userId: record.userId,
          role: 'student'
        }
      } else if (mode === 'view' && record) {
        this.formData = {
          lectureId: record.lectureId,
          userId: record.userId,
          role: 'student'
        }
      } else if (mode === 'add') {
        this.resetForm()
      }
      
      this.showModal = true
    },

    // 处理单选
    handleSelectRow(id, checked) {
      if (checked) {
        this.selectedRows.push(id)
      } else {
        this.selectedRows = this.selectedRows.filter(rowId => rowId !== id)
      }
    }
  }
}
</script>

<style scoped>
/* 基础容器样式保持不变 */
.container {
  min-height: 100vh;
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
  padding: 24px;
}

.main-content {
  max-width: 1400px;
  margin: 0 auto;
}

.notification {
  position: fixed;
  top: 16px;
  right: 16px;
  z-index: 1000;
  background: #10b981;
  color: white;
  padding: 12px 16px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  display: flex;
  align-items: center;
  gap: 8px;
}

.close-btn {
  background: none;
  border: none;
  color: inherit;
  cursor: pointer;
  font-size: 18px;
  padding: 0;
  margin-left: 8px;
}

/* 页面标题 */
.header {
  margin-bottom: 32px;
}

.title-section {
  display: flex;
  align-items: center;
  gap: 12px;
}

.icon {
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, #3b82f6, #8b5cf6);
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}

.title-text h1 {
  font-size: 32px;
  font-weight: bold;
  color: #1f2937;
  margin: 0 0 4px 0;
}

.title-text p {
  color: #6b7280;
  margin: 0;
}

/* 统计卡片样式保持不变 */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 24px;
  margin-bottom: 32px;
}

.stat-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  border: 1px solid #e5e7eb;
}

.stat-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.stat-label {
  font-size: 14px;
  font-weight: 500;
  color: #6b7280;
  margin: 0 0 4px 0;
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #1f2937;
  margin: 0;
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}

.stat-icon.blue { background: #dbeafe; color: #2563eb; }
.stat-icon.green { background: #dcfce7; color: #16a34a; }
.stat-icon.purple { background: #f3e8ff; color: #9333ea; }
.stat-icon.orange { background: #fed7aa; color: #ea580c; }

/* 工具栏样式 */
.toolbar {
  background: white;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  border: 1px solid #e5e7eb;
  display: flex;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 16px;
}

.search-filters {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
  flex: 1;
}

.search-box {
  position: relative;
}

.search-icon {
  position: absolute;
  left: 12px;
  top: 50%;
  transform: translateY(-50%);
  color: #9ca3af;
}

.search-input {
  padding: 8px 12px 8px 36px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  font-size: 14px;
  width: 200px;
}

.search-input:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.filter-select {
  padding: 8px 12px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  font-size: 14px;
  background: white;
}

.action-buttons {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.btn-primary {
  background: linear-gradient(135deg, #3b82f6, #2563eb);
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 8px;
  font-weight: 500;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: all 0.2s;
}

.btn-primary:hover {
  background: linear-gradient(135deg, #2563eb, #1d4ed8);
}

.btn-danger {
  background: linear-gradient(135deg, #ef4444, #dc2626);
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 8px;
  font-weight: 500;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: all 0.2s;
}

.btn-secondary {
  background: #f3f4f6;
  color: #374151;
  border: none;
  padding: 8px 16px;
  border-radius: 8px;
  font-weight: 500;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: all 0.2s;
}

/* 讲座容器 */
.lectures-container {
  margin-bottom: 32px;
}

.loading-container, .empty-container {
  background: white;
  border-radius: 12px;
  padding: 48px;
  text-align: center;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  border: 1px solid #e5e7eb;
}

.loading-container {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  color: #6b7280;
}

.empty-container {
  color: #6b7280;
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.empty-container h3 {
  margin: 0 0 8px 0;
  color: #374151;
}

/* 讲座分组 */
.lecture-group {
  background: white;
  border-radius: 12px;
  margin-bottom: 24px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  border: 1px solid #e5e7eb;
  overflow: hidden;
}

.lecture-header {
  background: linear-gradient(135deg, #f8fafc, #f1f5f9);
  padding: 20px 24px;
  border-bottom: 1px solid #e5e7eb;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.lecture-title {
  font-size: 18px;
  font-weight: 600;
  color: #1f2937;
  margin: 0 0 4px 0;
  display: flex;
  align-items: center;
  gap: 8px;
}

.lecture-id {
  font-size: 12px;
  font-weight: normal;
  color: #6b7280;
  background: #f3f4f6;
  padding: 2px 8px;
  border-radius: 12px;
}

.student-count {
  font-size: 14px;
  color: #6b7280;
  margin: 0;
}

.expand-btn {
  background: #f3f4f6;
  border: none;
  padding: 8px 12px;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 4px;
  transition: all 0.2s;
}

.expand-btn:hover {
  background: #e5e7eb;
}

/* 学生容器 */
.students-container {
  padding: 24px;
}

.students-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
}

.student-card {
  background: #f8fafc;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  padding: 16px;
  transition: all 0.2s;
  cursor: pointer;
}

.student-card:hover {
  border-color: #3b82f6;
  box-shadow: 0 2px 8px rgba(59, 130, 246, 0.1);
}

.student-card.selected {
  border-color: #3b82f6;
  background: #eff6ff;
}

.student-card-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.student-checkbox {
  width: 16px;
  height: 16px;
}

.student-avatar {
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, #3b82f6, #8b5cf6);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: 600;
  font-size: 16px;
}

.student-info {
  margin-bottom: 12px;
}

.student-name {
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
  margin: 0 0 4px 0;
}

.student-id, .user-id {
  font-size: 12px;
  color: #6b7280;
  margin: 0;
}

.student-actions {
  display: flex;
  gap: 8px;
  justify-content: flex-end;
}

.action-btn {
  background: none;
  border: none;
  cursor: pointer;
  padding: 4px;
  border-radius: 4px;
  transition: all 0.2s;
  font-size: 14px;
}

.action-btn.view:hover { background: #dbeafe; }
.action-btn.edit:hover { background: #dcfce7; }
.action-btn.delete:hover { background: #fee2e2; }

.no-students {
  text-align: center;
  padding: 32px;
  color: #6b7280;
}

.no-students-icon {
  font-size: 32px;
  margin-bottom: 8px;
}

/* 分页样式保持不变 */
.pagination {
  background: white;
  border-radius: 12px;
  padding: 16px 24px;
  margin-top: 24px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  border: 1px solid #e5e7eb;
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 16px;
}

.pagination-info {
  font-size: 14px;
  color: #374151;
}

.pagination-controls {
  display: flex;
  align-items: center;
  gap: 8px;
}

.page-numbers {
  display: flex;
  gap: 4px;
}

.page-btn {
  padding: 8px 12px;
  border: 1px solid #d1d5db;
  background: white;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  color: #374151;
  transition: all 0.2s;
}

.page-btn:hover {
  background: #f9fafb;
}

.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-btn.active {
  background: #3b82f6;
  color: white;
  border-color: #3b82f6;
}

/* 模态框样式保持不变 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 16px;
}

.modal {
  background: white;
  border-radius: 12px;
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1);
  max-width: 500px;
  width: 100%;
  max-height: 90vh;
  overflow-y: auto;
}

.modal-header {
  padding: 20px 24px;
  border-bottom: 1px solid #e5e7eb;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-header h3 {
  font-size: 18px;
  font-weight: 600;
  color: #1f2937;
  margin: 0;
}

.modal-body {
  padding: 24px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  font-size: 14px;
  font-weight: 500;
  color: #374151;
  margin-bottom: 8px;
}

.form-input {
  width: 100%;
  padding: 8px 12px;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 14px;
  box-sizing: border-box;
}

.form-input:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.view-field {
  padding: 8px 12px;
  background: #f9fafb;
  border: 1px solid #e5e7eb;
  border-radius: 6px;
  color: #374151;
  font-size: 14px;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 24px;
  padding-top: 20px;
  border-top: 1px solid #e5e7eb;
}

/* 动画 */
.spinning {
  display: inline-block;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

/* 响应式 */
@media (max-width: 768px) {
  .container {
    padding: 16px;
  }
  
  .toolbar {
    flex-direction: column;
  }
  
  .search-filters {
    width: 100%;
  }
  
  .search-input {
    width: 100%;
  }
  
  .action-buttons {
    width: 100%;
    justify-content: center;
  }
  
  .lecture-header {
    flex-direction: column;
    gap: 12px;
    text-align: center;
  }
  
  .students-grid {
    grid-template-columns: 1fr;
  }
  
  .stats-grid {
    grid-template-columns: 1fr;
  }
}
</style>