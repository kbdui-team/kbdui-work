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
          <div class="icon">👥</div>
          <div class="title-text">
            <h1>讲座参与者管理</h1>
            <p>管理讲座的参与者信息，包括教师和学生</p>
          </div>
        </div>
      </div>

      <!-- 统计卡片 -->
      <div class="stats-grid">
        <div class="stat-card">
          <div class="stat-content">
            <div class="stat-text">
              <p class="stat-label">总参与者</p>
              <p class="stat-value">{{ totalRecords }}</p>
            </div>
            <div class="stat-icon blue">👥</div>
          </div>
        </div>
        
        <div class="stat-card">
          <div class="stat-content">
            <div class="stat-text">
              <p class="stat-label">教师数量</p>
              <p class="stat-value">{{ stats.teachers }}</p>
            </div>
            <div class="stat-icon green">👨‍🏫</div>
          </div>
        </div>
        
        <div class="stat-card">
          <div class="stat-content">
            <div class="stat-text">
              <p class="stat-label">学生数量</p>
              <p class="stat-value">{{ stats.students }}</p>
            </div>
            <div class="stat-icon purple">👨‍🎓</div>
          </div>
        </div>
        
        <div class="stat-card">
          <div class="stat-content">
            <div class="stat-text">
              <p class="stat-label">匿名参与</p>
              <p class="stat-value">{{ stats.anonymous }}</p>
            </div>
            <div class="stat-icon orange">👤</div>
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
              placeholder="搜索用户姓名..."
              v-model="searchTerm"
              class="search-input"
            />
          </div>
          
          <select v-model="roleFilter" class="filter-select">
            <option value="all">所有角色</option>
            <option value="teacher">教师</option>
            <option value="student">学生</option>
          </select>
          
          <select v-model="anonymousFilter" class="filter-select">
            <option value="all">所有状态</option>
            <option value="false">实名</option>
            <option value="true">匿名</option>
          </select>
        </div>
        
        <div class="action-buttons">
          <button @click="openModal('add')" class="btn-primary">
            <span>➕</span>
            添加参与者
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

      <!-- 数据表格 -->
      <div class="table-container">
        <table class="data-table">
          <thead>
            <tr>
              <th>
                <input
                  type="checkbox"
                  :checked="selectedRows.length === data.length && data.length > 0"
                  @change="handleSelectAll($event.target.checked)"
                />
              </th>
              <th>ID</th>
              <th>讲座ID</th>
              <th>用户姓名</th>
              <th>角色</th>
              <th>匿名状态</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="loading">
              <td colspan="7" class="loading-cell">
                <span class="spinning">🔄</span>
                <span>加载中...</span>
              </td>
            </tr>
            <tr v-else-if="data.length === 0">
              <td colspan="7" class="empty-cell">暂无数据</td>
            </tr>
            <tr v-else v-for="item in data" :key="item.id" class="data-row">
              <td>
                <input
                  type="checkbox"
                  :checked="selectedRows.includes(item.id)"
                  @change="handleSelectRow(item.id, $event.target.checked)"
                />
              </td>
              <td class="font-bold">{{ item.id }}</td>
              <td>{{ item.lectureId }}</td>
              <td>{{ item.userName || '未知用户' }}</td>
              <td>
                <span :class="item.role === 'teacher' ? 'badge-green' : 'badge-blue'" class="badge">
                  {{ item.role === 'teacher' ? '教师' : '学生' }}
                </span>
              </td>
              <td>
                <span :class="item.anonymous ? 'badge-orange' : 'badge-gray'" class="badge">
                  {{ item.anonymous ? '匿名' : '实名' }}
                </span>
              </td>
              <td>
                <div class="action-icons">
                  <button @click="openModal('view', item)" class="icon-btn blue" title="查看详情">👁️</button>
                  <button @click="openModal('edit', item)" class="icon-btn green" title="编辑">✏️</button>
                  <button @click="handleDelete(item.id)" class="icon-btn red" title="删除">🗑️</button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
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
          <h3>{{ modalMode === 'add' ? '添加参与者' : modalMode === 'edit' ? '编辑参与者' : '查看详情' }}</h3>
          <button @click="showModal = false" class="close-btn">✕</button>
        </div>
        
        <div class="modal-body">
          <div class="form-group">
            <label>讲座ID</label>
            <input
              type="number"
              v-model="formData.lectureId"
              :disabled="modalMode === 'view'"
              class="form-input"
            />
          </div>
          
          <div class="form-group">
            <label>选择用户</label>
            <select
              v-if="modalMode !== 'view'"
              v-model="formData.userId"
              class="form-input"
            >
              <option value="">请选择用户</option>
              <option 
                v-for="user in userList" 
                :key="user.id" 
                :value="user.id"
              >
                {{ user.realName || user.name }}
              </option>
            </select>
            <!-- 查看模式显示用户姓名 -->
            <div v-else class="view-field">
              {{ getUserNameById(formData.userId) }}
            </div>
          </div>
          
          <div class="form-group">
            <label>角色</label>
            <select
              v-model="formData.role"
              :disabled="modalMode === 'view'"
              class="form-input"
            >
              <option value="student">学生</option>
              <option value="teacher">教师</option>
            </select>
          </div>
          
          <div class="checkbox-group">
            <label class="checkbox-label">
              <input
                type="checkbox"
                v-model="formData.anonymous"
                :disabled="modalMode === 'view'"
              />
              <span>匿名参与</span>
            </label>
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
  name: 'LectureParticipantsManagement',
  data() {
    return {
      data: [],
      loading: false,
      selectedRows: [],
      currentPage: 1,
      pageSize: 10,
      totalPages: 0,
      totalRecords: 0,
      searchTerm: '',
      roleFilter: 'all',
      anonymousFilter: 'all',
      lectureFilter: 'all',
      showModal: false,
      modalMode: 'add',
      currentRecord: null,
      notification: null,
      userList: [], // 用户列表
      formData: {
        lectureId: '',
        userId: '',
        role: 'student',
        anonymous: false  // 修改为布尔值
      }
    }
  },
  computed: {
    stats() {
      const teachers = this.data.filter(item => item.role === 'teacher').length
      const students = this.data.filter(item => item.role === 'student').length
      // 修复：直接使用布尔值判断
      const anonymous = this.data.filter(item => item.anonymous === true).length
      
      return { teachers, students, anonymous }
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
      this.loadData()
    },
    roleFilter() {
      this.currentPage = 1
      this.loadData()
    },
    anonymousFilter() {
      this.currentPage = 1
      this.loadData()
    },
    lectureFilter() {
      this.currentPage = 1
      this.loadData()
    }
  },
  mounted() {
    this.loadData()
    this.loadUserList()
  },
  methods: {
    // API基础URL
    getApiUrl() {
      return 'http://localhost:5555/lecture-participants'
    },

    // 用户API基础URL
    getUserApiUrl() {
      return 'http://localhost:5555/user'
    },

    // 统一布尔值转换工具方法
    normalizeBoolean(value) {
      if (typeof value === 'boolean') {
        return value
      }
      if (typeof value === 'string') {
        return value.toLowerCase() === 'true'
      }
      return Boolean(value)
    },

    // 根据用户ID获取用户姓名
    getUserNameById(userId) {
      const user = this.userList.find(u => u.id == userId)
      return user ? (user.realName || user.name) : '未知用户'
    },

    // 获取用户信息
    async getUserById(userId) {
      try {
        const response = await fetch(`${this.getUserApiUrl()}/get/${userId}`);
        
        if (response.ok) {
          const data = await response.json();
          console.log('用户数据:', data);
          return data;
        }
        
        return null;
      } catch (error) {
        console.error(`获取用户${userId}信息失败:`, error);
        return null;
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

    // 构建查询条件
    buildConditions() {
      const conditions = {}
      
      if (this.searchTerm) {
        conditions.searchTerm = this.searchTerm
      }
      
      if (this.roleFilter !== 'all') {
        conditions.role = this.roleFilter
      }
      
      if (this.anonymousFilter !== 'all') {
        conditions.anonymous = this.anonymousFilter
      }
      
      if (this.lectureFilter !== 'all') {
        conditions.lectureId = parseInt(this.lectureFilter)
      }
      
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
          // 提取所有唯一的用户ID，避免重复查询
          const uniqueUserIds = [...new Set(result.records.map(item => item.userId))]
          
          // 批量获取用户信息（并行请求）
          const userPromises = uniqueUserIds.map(userId => this.getUserById(userId))
          const users = await Promise.all(userPromises)
          
          // 创建用户ID到用户信息的映射
          const userMap = {}
          users.forEach((user, index) => {
            const userId = uniqueUserIds[index]
            userMap[userId] = user || { id: userId, name: `用户${userId}` }
          })
          
          // 将用户姓名添加到数据中，并统一anonymous字段类型
          const dataWithUserNames = result.records.map(item => ({
            ...item,
            userName: userMap[item.userId].realName || userMap[item.userId].name,
            // 关键修复：统一转换为布尔值
            anonymous: this.normalizeBoolean(item.anonymous)
          }))
          
          console.log('处理后的数据:', dataWithUserNames)
          
          this.data = dataWithUserNames
          this.totalPages = result.pages || 0
          this.totalRecords = result.total || 0
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
        this.showNotification('请填写完整信息并选择用户', 'error')
        return
      }
      
      this.loading = true
      
      try {
        // 确保发送给后端的数据类型正确
        const submitData = {
          ...this.formData,
          anonymous: this.normalizeBoolean(this.formData.anonymous)
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
        role: 'student',
        anonymous: false  // 改为布尔值
      }
      this.currentRecord = null
    },

    // 处理删除
    async handleDelete(id) {
      if (!confirm('确定要删除这条记录吗？')) return
      
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
      
      if (!confirm(`确定要删除选中的 ${this.selectedRows.length} 条记录吗？`)) return
      
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
          role: record.role,
          anonymous: this.normalizeBoolean(record.anonymous)  // 统一转换
        }
      } else if (mode === 'view' && record) {
        this.formData = {
          lectureId: record.lectureId,
          userId: record.userId,
          role: record.role,
          anonymous: this.normalizeBoolean(record.anonymous)  // 统一转换
        }
      } else if (mode === 'add') {
        this.resetForm()
      }
      
      this.showModal = true
    },

    // 处理全选
    handleSelectAll(checked) {
      if (checked) {
        this.selectedRows = this.data.map(item => item.id)
      } else {
        this.selectedRows = []
      }
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
/* 基础容器 */
.container {
  min-height: 100vh;
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
  padding: 24px;
}

.main-content {
  max-width: 1200px;
  margin: 0 auto;
}

/* 通知消息 */
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

/* 统计卡片 */
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

/* 工具栏 */
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

.filter-select:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
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

.btn-danger:hover {
  background: linear-gradient(135deg, #dc2626, #b91c1c);
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

.btn-secondary:hover {
  background: #e5e7eb;
}

.btn-secondary:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* 表格 */
.table-container {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  border: 1px solid #e5e7eb;
}

.data-table {
  width: 100%;
  border-collapse: collapse;
}

.data-table th {
  background: #f8fafc;
  padding: 16px;
  text-align: left;
  font-weight: 600;
  color: #374151;
  font-size: 12px;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  border-bottom: 1px solid #e5e7eb;
}

.data-table td {
  padding: 16px;
  border-bottom: 1px solid #f3f4f6;
  font-size: 14px;
  color: #1f2937;
}

.data-row:hover {
  background: #f8fafc;
}

.font-bold {
  font-weight: 600;
}

.loading-cell, .empty-cell {
  text-align: center;
  padding: 48px 16px;
  color: #6b7280;
}

.loading-cell {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

/* 徽章 */
.badge {
  display: inline-flex;
  align-items: center;
  padding: 4px 12px;
  border-radius: 9999px;
  font-size: 12px;
  font-weight: 500;
}

.badge-green { background: #dcfce7; color: #16a34a; }
.badge-blue { background: #dbeafe; color: #2563eb; }
.badge-orange { background: #fed7aa; color: #ea580c; }
.badge-gray { background: #f3f4f6; color: #374151; }

/* 操作图标 */
.action-icons {
  display: flex;
  gap: 8px;
}

.icon-btn {
  background: none;
  border: none;
  cursor: pointer;
  padding: 4px;
  border-radius: 4px;
  transition: all 0.2s;
}

.icon-btn.blue:hover { background: #dbeafe; }
.icon-btn.green:hover { background: #dcfce7; }
.icon-btn.red:hover { background: #fee2e2; }

/* 分页 */
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

/* 模态框 */
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

.form-input:disabled {
  background: #f9fafb;
  color: #6b7280;
}

.checkbox-group {
  margin-bottom: 20px;
}

.checkbox-label {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  font-size: 14px;
  color: #374151;
}

.checkbox-label input[type="checkbox"] {
  width: auto;
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
  
  .pagination {
    flex-direction: column;
  }
  
  .stats-grid {
    grid-template-columns: 1fr;
  }
}
</style>