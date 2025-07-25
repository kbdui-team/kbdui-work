<template>
  <div class="lecture-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <h2>教学记录</h2>
      <div class="header-actions">
        <el-button type="primary" @click="openForm()">
          <el-icon><Plus /></el-icon>
          新建讲座
        </el-button>
        <el-button @click="fetchData" :loading="loading">
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
      </div>
    </div>

    <!-- 搜索筛选 -->
    <div class="filter-bar">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索标题、讲师或地点..."
        style="width: 300px"
        clearable
        @input="handleSearch">
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
      <el-select v-model="statusFilter" placeholder="状态筛选" clearable style="width: 150px" @change="handleSearch">
        <el-option label="已安排" value="SCHEDULED"></el-option>
        <el-option label="进行中" value="ONGOING"></el-option>
        <el-option label="已完成" value="COMPLETED"></el-option>
        <el-option label="已取消" value="CANCELLED"></el-option>
      </el-select>
    </div>

    <!-- 讲座列表 -->
    <div class="lecture-list" v-loading="loading">
      <el-empty v-if="!loading && filteredLectures.length === 0" description="暂无数据" />
      
      <div v-for="item in paginatedData" :key="item.id" class="lecture-card">
        <div class="card-header">
          <div class="title-section">
            <h3>{{ item.title }}</h3>
            <el-tag :type="getStatusType(item.status)" size="small">
              {{ getStatusText(item.status) }}
            </el-tag>
          </div>
          <div class="actions">
            <el-button size="small" text @click="openForm(item)">
              <el-icon><Edit /></el-icon>
            </el-button>
            <el-button size="small" text type="danger" @click="deleteItem(item)">
              <el-icon><Delete /></el-icon>
            </el-button>
          </div>
        </div>
       
      </div>
    </div>

    <!-- 分页 -->
    <el-pagination
      v-if="filteredLectures.length > 0"
      v-model:current-page="currentPage"
      v-model:page-size="pageSize"
      :page-sizes="[10, 20, 50]"
      layout="total, sizes, prev, pager, next"
      :total="filteredLectures.length"
      class="pagination"
    />

    <!-- 表单对话框 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑讲座' : '新建讲座'" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入讲座标题" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="讲座描述(可选)" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" style="width: 100%">
            <el-option label="可用" value="active" />
            <el-option label="暂停" value="inactive" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm" :loading="submitting">
          {{ isEdit ? '更新' : '创建' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Refresh, Search, User, Location, Calendar, Edit, Delete } from '@element-plus/icons-vue'

// API配置
const API_BASE = import.meta.env.VITE_API_BASE_URL || 'http://localhost:5555'

// 响应式数据
const loading = ref(false)
const submitting = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const searchKeyword = ref('')
const statusFilter = ref('')
const lectures = ref([])
const teacherList = ref([])

// 表单数据
const form = reactive({
  id: null,
  title: '',
  description: '',
  status: 'active',
  teacherId: null
})

const formRef = ref()

// 验证规则
const rules = {
  title: [{ required: true, message: '请输入讲座标题', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

// 计算属性
const filteredLectures = computed(() => {
  let result = lectures.value
  
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    result = result.filter(item => 
      item.title?.toLowerCase().includes(keyword) ||
      item.speaker?.toLowerCase().includes(keyword) ||
      item.location?.toLowerCase().includes(keyword)
    )
  }
  
  if (statusFilter.value) {
    result = result.filter(item => item.status === statusFilter.value)
  }
  
  return result
})

const paginatedData = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  return filteredLectures.value.slice(start, start + pageSize.value)
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

  getAllLectures() {
    return this.request('/lecture/list')
  },

  addLecture(data) {
    return this.request('/lecture/add', {
      method: 'POST',
      body: JSON.stringify(data)
    })
  },

  updateLecture(data) {
    return this.request('/lecture/update', {
      method: 'PUT',
      body: JSON.stringify(data)
    })
  },

  deleteLecture(id) {
    return this.request(`/lecture/delete/${id}`, {
      method: 'DELETE'
    })
  }
}

// 方法
const fetchData = async () => {
  try {
    loading.value = true
    const data = await api.getAllLectures()
    lectures.value = data || []
  } catch (error) {
    ElMessage.error('获取数据失败: ' + error.message)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
}

// 自动赋值teacherId为当前登录用户id
function setTeacherIdFromLogin() {
  try {
    const userStr = localStorage.getItem('currentUser')
    if (userStr) {
      const user = JSON.parse(userStr)
      // console.log("user", user)
      if (user && user.id) form.teacherId = user.id
    }
  } catch {}
}

const openForm = (item = null) => {
  isEdit.value = !!item
  if (item) {
    Object.assign(form, item)
  } else {
    resetForm()
    setTeacherIdFromLogin()
  }
  dialogVisible.value = true
}

const resetForm = () => {
  Object.assign(form, {
    id: null,
    title: '',
    description: '',
    status: 'active',
    teacherId: null
  })
  formRef.value?.clearValidate()
}

const submitForm = async () => {
  try {
    await formRef.value.validate()
    submitting.value = true
    
    if (isEdit.value) {
      await api.updateLecture(form)
      ElMessage.success('更新成功')
    } else {
      console.log("form", form)
      await api.addLecture(form)
      ElMessage.success('创建成功')
    }
    
    dialogVisible.value = false
    fetchData()
  } catch (error) {
    if (error.message && !error.message.includes('validation')) {
      ElMessage.error('操作失败: ' + error.message)
    }
  } finally {
    submitting.value = false
  }
}

const deleteItem = async (item) => {
  try {
    await ElMessageBox.confirm(`确定删除讲座 "${item.title}" 吗？`, '确认删除', {
      type: 'warning'
    })
    
    await api.deleteLecture(item.id)
    ElMessage.success('删除成功')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败: ' + error.message)
    }
  }
}

const formatDate = (date) => {
  return date ? new Date(date).toLocaleDateString('zh-CN') : '-'
}

const getStatusType = (status) => {
  const map = {
    'SCHEDULED': '',
    'ONGOING': 'warning',
    'COMPLETED': 'success',
    'CANCELLED': 'danger'
  }
  return map[status] || ''
}

const getStatusText = (status) => {
  const map = {
    'SCHEDULED': '已安排',
    'ONGOING': '进行中',
    'COMPLETED': '已完成',
    'CANCELLED': '已取消'
  }
  return map[status] || status
}

// 获取教师列表
const fetchTeacherList = async () => {
  try {
    const res = await fetch(`${API_BASE}/user/list`)
    const data = await res.json()
    teacherList.value = Array.isArray(data) ? data.filter(u => u.userType === 'teacher') : []
  } catch {}
}

// 初始化
onMounted(() => {
  fetchData()
  fetchTeacherList()
})
</script>

<style scoped>
.lecture-container {
  padding: 20px;
  background: #f5f7fa;
  min-height: 100vh;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
  color: #303133;
}

.header-actions {
  display: flex;
  gap: 12px;
}

.filter-bar {
  background: white;
  padding: 16px;
  border-radius: 8px;
  margin-bottom: 20px;
  display: flex;
  gap: 16px;
  align-items: center;
}

.lecture-list {
  display: grid;
  gap: 16px;
  margin-bottom: 20px;
}

.lecture-card {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  transition: transform 0.2s;
}

.lecture-card:hover {
  transform: translateY(-2px);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.title-section {
  display: flex;
  align-items: center;
  gap: 12px;
}

.title-section h3 {
  margin: 0;
  color: #303133;
  font-size: 16px;
}

.actions {
  display: flex;
  gap: 8px;
}

.card-content .info-row {
  display: flex;
  gap: 20px;
  color: #606266;
  font-size: 14px;
  margin-bottom: 8px;
}

.info-row span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.capacity-info {
  font-size: 14px;
  color: #909399;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

@media (max-width: 768px) {
  .filter-bar {
    flex-direction: column;
    align-items: stretch;
  }
  
  .card-header {
    flex-direction: column;
    gap: 12px;
  }
  
  .info-row {
    flex-direction: column !important;
    gap: 8px !important;
  }
}
</style>