<template>
    <div class="teaching-record-container">
      <!-- 页面头部 -->
      <div class="page-header">
        <div class="header-left">
          <h2 class="page-title">教学记录</h2>
          <div class="breadcrumb">
            <span>首页</span>
            <i class="el-icon-arrow-right"></i>
            <span>教学记录</span>
          </div>
        </div>
        <div class="header-right">
          <el-button type="primary" icon="el-icon-plus" @click="createNewRecord">
            新建记录
          </el-button>
        </div>
      </div>
  
      <!-- 统计卡片 -->
      <div class="stats-cards">
        <div class="stat-card">
          <div class="stat-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">
            <i class="el-icon-document"></i>
          </div>
          <div class="stat-content">
            <h3>{{ totalRecords }}</h3>
            <p>总教学记录</p>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);">
            <i class="el-icon-edit-outline"></i>
          </div>
          <div class="stat-content">
            <h3>{{ totalPopquizzes }}</h3>
            <p>总 Popquiz 数</p>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);">
            <i class="el-icon-user"></i>
          </div>
          <div class="stat-content">
            <h3>{{ averageParticipation }}%</h3>
            <p>平均参与率</p>
          </div>
        </div>
      </div>
  
      <!-- 筛选区域 -->
      <div class="filter-section">
        <el-row :gutter="20">
          <el-col :span="6">
            <el-select v-model="selectedCourse" placeholder="选择课程" clearable>
              <el-option
                v-for="course in courses"
                :key="course.id"
                :label="course.name"
                :value="course.id">
              </el-option>
            </el-select>
          </el-col>
          <el-col :span="6">
            <el-date-picker
              v-model="dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              format="yyyy-MM-dd">
            </el-date-picker>
          </el-col>
          <el-col :span="6">
            <el-input
              v-model="searchKeyword"
              placeholder="搜索教学记录..."
              prefix-icon="el-icon-search"
              clearable>
            </el-input>
          </el-col>
        </el-row>
      </div>
  
      <!-- 教学记录列表 -->
      <div class="record-list">
        <div 
          v-for="record in filteredRecords" 
          :key="record.id"
          class="record-card"
          @click="viewRecordDetail(record)">
          <div class="record-header">
            <div class="record-title">
              <h3>{{ record.title }}</h3>
              <el-tag 
                :type="getStatusType(record.status)" 
                size="small">
                {{ record.status }}
              </el-tag>
            </div>
            <div class="record-meta">
              <span class="date">{{ formatDate(record.date) }}</span>
              <span class="duration">{{ record.duration }}分钟</span>
            </div>
          </div>
          
          <div class="record-content">
            <div class="record-info">
              <div class="info-item">
                <i class="el-icon-user"></i>
                <span>{{ record.participantCount }}人参与</span>
              </div>
              <div class="info-item">
                <i class="el-icon-edit-outline"></i>
                <span>{{ record.popquizCount }}个 Popquiz</span>
              </div>
              <div class="info-item">
                <i class="el-icon-data-analysis"></i>
                <span>平均分: {{ record.averageScore }}分</span>
              </div>
            </div>
            
            <div class="record-actions">
              <el-button size="mini" type="text" @click.stop="editRecord(record)">
                <i class="el-icon-edit"></i>
              </el-button>
              <el-button size="mini" type="text" @click.stop="deleteRecord(record)">
                <i class="el-icon-delete"></i>
              </el-button>
            </div>
          </div>
        </div>
      </div>
  
      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="currentPage"
          :page-sizes="[10, 20, 50, 100]"
          :page-size="pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="totalItems">
        </el-pagination>
      </div>
  
      <!-- 教学记录详情抽屉 -->
      <el-drawer
        title="教学记录详情"
        v-model:visible="drawerVisible"
        direction="rtl"
        size="60%"
        :before-close="closeDrawer">
        <div class="drawer-content" v-if="selectedRecord">
          <!-- 记录基本信息 -->
          <div class="detail-section">
            <h3>基本信息</h3>
            <div class="detail-grid">
              <div class="detail-item">
                <label>课程标题:</label>
                <span>{{ selectedRecord.title }}</span>
              </div>
              <div class="detail-item">
                <label>教学日期:</label>
                <span>{{ formatDate(selectedRecord.date) }}</span>
              </div>
              <div class="detail-item">
                <label>持续时间:</label>
                <span>{{ selectedRecord.duration }}分钟</span>
              </div>
              <div class="detail-item">
                <label>参与人数:</label>
                <span>{{ selectedRecord.participantCount }}人</span>
              </div>
            </div>
          </div>
  
          <!-- Popquiz 列表 -->
          <div class="detail-section">
            <h3>Popquiz 记录 ({{ selectedRecord.popquizzes.length }})</h3>
            <div class="popquiz-list">
              <div 
                v-for="(quiz, index) in selectedRecord.popquizzes" 
                :key="quiz.id"
                class="popquiz-item"
                @click="viewQuizDetail(quiz)">
                <div class="quiz-header">
                  <span class="quiz-number">Quiz {{ index + 1 }}</span>
                  <span class="quiz-time">{{ formatTime(quiz.startTime) }}</span>
                </div>
                <div class="quiz-content">
                  <p class="quiz-question">{{ quiz.question }}</p>
                  <div class="quiz-stats">
                    <span class="stat">
                      <i class="el-icon-check"></i>
                      正确: {{ quiz.correctCount }}
                    </span>
                    <span class="stat">
                      <i class="el-icon-close"></i>
                      错误: {{ quiz.incorrectCount }}
                    </span>
                    <span class="stat">
                      <i class="el-icon-time"></i>
                      {{ quiz.duration }}s
                    </span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </el-drawer>
  
      <!-- Popquiz 详情对话框 -->
      <el-dialog
        title="Popquiz 详细数据"
        v-model:visible="quizDialogVisible"
        width="70%"
        :before-close="closeQuizDialog">
        <div class="quiz-detail-content" v-if="selectedQuiz">
          <div class="quiz-overview">
            <h4>{{ selectedQuiz.question }}</h4>
            <div class="quiz-summary">
              <el-row :gutter="20">
                <el-col :span="8">
                  <div class="summary-item correct">
                    <i class="el-icon-circle-check"></i>
                    <div>
                      <span class="number">{{ selectedQuiz.correctCount }}</span>
                      <span class="label">正确答案</span>
                    </div>
                  </div>
                </el-col>
                <el-col :span="8">
                  <div class="summary-item incorrect">
                    <i class="el-icon-circle-close"></i>
                    <div>
                      <span class="number">{{ selectedQuiz.incorrectCount }}</span>
                      <span class="label">错误答案</span>
                    </div>
                  </div>
                </el-col>
                <el-col :span="8">
                  <div class="summary-item">
                    <i class="el-icon-time"></i>
                    <div>
                      <span class="number">{{ selectedQuiz.duration }}s</span>
                      <span class="label">答题时间</span>
                    </div>
                  </div>
                </el-col>
              </el-row>
            </div>
          </div>
  
          <!-- 答题选项分析 -->
          <div class="options-analysis">
            <h4>选项分析</h4>
            <div class="options-chart">
              <div 
                v-for="option in selectedQuiz.options" 
                :key="option.id"
                class="option-bar">
                <div class="option-label">
                  <span class="option-text">{{ option.text }}</span>
                  <span class="option-count">{{ option.count }}</span>
                </div>
                <div class="option-progress">
                  <div 
                    class="progress-bar" 
                    :class="{ 'correct': option.isCorrect }"
                    :style="{ width: getOptionPercentage(option.count) + '%' }">
                  </div>
                </div>
              </div>
            </div>
          </div>
  
          <!-- 学生答题详情 -->
          <div class="student-answers">
            <h4>学生答题详情</h4>
            <el-table :data="selectedQuiz.studentAnswers" style="width: 100%">
              <el-table-column prop="studentName" label="学生姓名" width="120"></el-table-column>
              <el-table-column prop="answer" label="答案" width="100"></el-table-column>
              <el-table-column prop="isCorrect" label="正确性" width="100">
                <template v-slot="scope">
                  <el-tag :type="scope.row.isCorrect ? 'success' : 'danger'" size="mini">
                    {{ scope.row.isCorrect ? '正确' : '错误' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="responseTime" label="响应时间" width="100"></el-table-column>
              <el-table-column prop="submitTime" label="提交时间"></el-table-column>
            </el-table>
          </div>
        </div>
      </el-dialog>
    </div>
  </template>
  
  <script>
  export default {
    name: 'TeachingRecord',
    data() {
      return {
        // 基础数据
        currentPage: 1,
        pageSize: 10,
        totalItems: 0,
        drawerVisible: false,
        quizDialogVisible: false,
        selectedRecord: null,
        selectedQuiz: null,
        
        // 筛选条件
        selectedCourse: '',
        dateRange: '',
        searchKeyword: '',
        
        // 课程选项
        courses: [
          { id: 1, name: '计算机科学与技术2021级1班' },
          { id: 2, name: '软件工程2021级1班' },
          { id: 3, name: '数据科学2021级1班' }
        ],
        
        // 教学记录数据
        teachingRecords: [
          {
            id: 1,
            title: '第一章：计算机基础概念',
            date: '2024-01-15',
            duration: 90,
            participantCount: 32,
            popquizCount: 3,
            averageScore: 85.5,
            status: '已完成',
            courseId: 1,
            popquizzes: [
              {
                id: 1,
                question: '什么是计算机？',
                startTime: '09:15:00',
                duration: 60,
                correctCount: 28,
                incorrectCount: 4,
                options: [
                  { id: 1, text: '电子计算设备', count: 28, isCorrect: true },
                  { id: 2, text: '机械设备', count: 2, isCorrect: false },
                  { id: 3, text: '通信设备', count: 1, isCorrect: false },
                  { id: 4, text: '存储设备', count: 1, isCorrect: false }
                ],
                studentAnswers: [
                  { studentName: '张三', answer: 'A', isCorrect: true, responseTime: '15s', submitTime: '09:15:30' },
                  { studentName: '李四', answer: 'A', isCorrect: true, responseTime: '22s', submitTime: '09:15:37' },
                  { studentName: '王五', answer: 'B', isCorrect: false, responseTime: '18s', submitTime: '09:15:33' }
                ]
              },
              {
                id: 2,
                question: '计算机的主要组成部分包括哪些？',
                startTime: '09:45:00',
                duration: 90,
                correctCount: 25,
                incorrectCount: 7,
                options: [
                  { id: 1, text: 'CPU、内存、存储、I/O设备', count: 25, isCorrect: true },
                  { id: 2, text: '显示器、键盘、鼠标', count: 3, isCorrect: false },
                  { id: 3, text: '操作系统、应用软件', count: 2, isCorrect: false },
                  { id: 4, text: '网络设备', count: 2, isCorrect: false }
                ],
                studentAnswers: [
                  { studentName: '张三', answer: 'A', isCorrect: true, responseTime: '25s', submitTime: '09:45:40' },
                  { studentName: '李四', answer: 'B', isCorrect: false, responseTime: '30s', submitTime: '09:45:45' }
                ]
              }
            ]
          },
          {
            id: 2,
            title: '第二章：数据结构与算法',
            date: '2024-01-22',
            duration: 120,
            participantCount: 30,
            popquizCount: 4,
            averageScore: 78.2,
            status: '已完成',
            courseId: 1,
            popquizzes: [
              {
                id: 3,
                question: '数组的时间复杂度是多少？',
                startTime: '10:30:00',
                duration: 45,
                correctCount: 22,
                incorrectCount: 8,
                options: [
                  { id: 1, text: 'O(1)', count: 22, isCorrect: true },
                  { id: 2, text: 'O(n)', count: 5, isCorrect: false },
                  { id: 3, text: 'O(log n)', count: 2, isCorrect: false },
                  { id: 4, text: 'O(n²)', count: 1, isCorrect: false }
                ],
                studentAnswers: [
                  { studentName: '张三', answer: 'A', isCorrect: true, responseTime: '12s', submitTime: '10:30:25' },
                  { studentName: '李四', answer: 'B', isCorrect: false, responseTime: '20s', submitTime: '10:30:35' }
                ]
              }
            ]
          }
        ]
      }
    },
    
    computed: {
      // 统计数据
      totalRecords() {
        return this.teachingRecords.length;
      },
      
      totalPopquizzes() {
        return this.teachingRecords.reduce((sum, record) => sum + record.popquizCount, 0);
      },
      
      averageParticipation() {
        if (this.teachingRecords.length === 0) return 0;
        const total = this.teachingRecords.reduce((sum, record) => sum + record.participantCount, 0);
        return Math.round(total / this.teachingRecords.length);
      },
      
      // 筛选后的记录
      filteredRecords() {
        let filtered = this.teachingRecords;
        
        // 课程筛选
        if (this.selectedCourse) {
          filtered = filtered.filter(record => record.courseId === this.selectedCourse);
        }
        
        // 关键词搜索
        if (this.searchKeyword) {
          filtered = filtered.filter(record => 
            record.title.toLowerCase().includes(this.searchKeyword.toLowerCase())
          );
        }
        
        // 日期范围筛选
        if (this.dateRange && this.dateRange.length === 2) {
          filtered = filtered.filter(record => {
            const recordDate = new Date(record.date);
            return recordDate >= this.dateRange[0] && recordDate <= this.dateRange[1];
          });
        }
        
        return filtered;
      }
    },
    
    methods: {
      // 格式化日期
      formatDate(date) {
        return new Date(date).toLocaleDateString('zh-CN');
      },
      
      // 格式化时间
      formatTime(time) {
        return time;
      },
      
      // 获取状态类型
      getStatusType(status) {
        const typeMap = {
          '已完成': 'success',
          '进行中': 'warning',
          '已取消': 'danger'
        };
        return typeMap[status] || 'info';
      },
      
      // 查看记录详情
      viewRecordDetail(record) {
        this.selectedRecord = record;
        this.drawerVisible = true;
      },
      
      // 关闭抽屉
      closeDrawer() {
        this.drawerVisible = false;
        this.selectedRecord = null;
      },
      
      // 查看 Quiz 详情
      viewQuizDetail(quiz) {
        this.selectedQuiz = quiz;
        this.quizDialogVisible = true;
      },
      
      // 关闭 Quiz 对话框
      closeQuizDialog() {
        this.quizDialogVisible = false;
        this.selectedQuiz = null;
      },
      
      // 获取选项百分比
      getOptionPercentage(count) {
        if (!this.selectedQuiz) return 0;
        const total = this.selectedQuiz.correctCount + this.selectedQuiz.incorrectCount;
        return total > 0 ? (count / total) * 100 : 0;
      },
      
      // 分页处理
      handleSizeChange(val) {
        this.pageSize = val;
      },
      
      handleCurrentChange(val) {
        this.currentPage = val;
      },
      
      // 操作方法
      createNewRecord() {
        this.$message.info('新建记录功能');
      },
      
      editRecord(record) {
        this.$message.info(`编辑记录: ${record.title}`);
      },
      
      deleteRecord(record) {
        this.$confirm('此操作将永久删除该记录, 是否继续?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.$message.success('删除成功!');
        }).catch(() => {
          this.$message.info('已取消删除');
        });
      }
    }
  }
  </script>
  
  <style scoped>
  .teaching-record-container {
    padding: 20px;
    background-color: #f5f7fa;
    min-height: 100vh;
  }
  
  /* 页面头部 */
  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
  }
  
  .page-title {
    font-size: 24px;
    font-weight: 600;
    color: #303133;
    margin: 0 0 5px 0;
  }
  
  .breadcrumb {
    color: #909399;
    font-size: 14px;
  }
  
  .breadcrumb i {
    margin: 0 8px;
  }
  
  /* 统计卡片 */
  .stats-cards {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
    gap: 20px;
    margin-bottom: 20px;
  }
  
  .stat-card {
    background: white;
    border-radius: 12px;
    padding: 24px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
    display: flex;
    align-items: center;
    transition: transform 0.3s ease;
  }
  
  .stat-card:hover {
    transform: translateY(-2px);
  }
  
  .stat-icon {
    width: 60px;
    height: 60px;
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-right: 20px;
  }
  
  .stat-icon i {
    font-size: 24px;
    color: white;
  }
  
  .stat-content h3 {
    font-size: 28px;
    font-weight: 600;
    color: #303133;
    margin: 0 0 8px 0;
  }
  
  .stat-content p {
    color: #909399;
    font-size: 14px;
    margin: 0;
  }
  
  /* 筛选区域 */
  .filter-section {
    background: white;
    padding: 20px;
    border-radius: 12px;
    margin-bottom: 20px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  }
  
  /* 记录列表 */
  .record-list {
    display: grid;
    gap: 16px;
  }
  
  .record-card {
    background: white;
    border-radius: 12px;
    padding: 24px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
    cursor: pointer;
    transition: all 0.3s ease;
  }
  
  .record-card:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  }
  
  .record-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 16px;
  }
  
  .record-title {
    display: flex;
    align-items: center;
    gap: 12px;
  }
  
  .record-title h3 {
    font-size: 18px;
    font-weight: 600;
    color: #303133;
    margin: 0;
  }
  
  .record-meta {
    display: flex;
    gap: 16px;
    color: #909399;
    font-size: 14px;
  }
  
  .record-content {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  
  .record-info {
    display: flex;
    gap: 24px;
  }
  
  .info-item {
    display: flex;
    align-items: center;
    gap: 8px;
    color: #606266;
    font-size: 14px;
  }
  
  .info-item i {
    color: #909399;
  }
  
  .record-actions {
    display: flex;
    gap: 8px;
  }
  
  /* 分页 */
  .pagination-wrapper {
    display: flex;
    justify-content: center;
    margin-top: 20px;
  }
  
  /* 抽屉内容 */
  .drawer-content {
    padding: 20px;
  }
  
  .detail-section {
    margin-bottom: 32px;
  }
  
  .detail-section h3 {
    font-size: 18px;
    font-weight: 600;
    color: #303133;
    margin-bottom: 16px;
    border-bottom: 2px solid #e4e7ed;
    padding-bottom: 8px;
  }
  
  .detail-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
    gap: 16px;
  }
  
  .detail-item {
    display: flex;
    align-items: center;
  }
  
  .detail-item label {
    font-weight: 500;
    color: #606266;
    margin-right: 8px;
    min-width: 80px;
  }
  
  .detail-item span {
    color: #303133;
  }
  
  /* Popquiz 列表 */
  .popquiz-list {
    display: grid;
    gap: 16px;
  }
  
  .popquiz-item {
    background: #f8f9fa;
    border-radius: 8px;
    padding: 16px;
    cursor: pointer;
    transition: all 0.3s ease;
  }
  
  .popquiz-item:hover {
    background: #e9ecef;
  }
  
  .quiz-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 12px;
  }
  
  .quiz-number {
    font-weight: 600;
    color: #409eff;
  }
  
  .quiz-time {
    color: #909399;
    font-size: 14px;
  }
  
  .quiz-question {
    font-size: 16px;
    color: #303133;
    margin: 0 0 12px 0;
  }
  
  .quiz-stats {
    display: flex;
    gap: 20px;
  }
  
  .quiz-stats .stat {
    display: flex;
    align-items: center;
    gap: 4px;
    color: #606266;
    font-size: 14px;
  }
  
  /* Quiz 详情对话框 */
  .quiz-detail-content {
    padding: 20px;
  }
  
  .quiz-overview h4 {
    font-size: 20px;
    font-weight: 600;
    color: #303133;
    margin-bottom: 20px;
  }
  
  .quiz-summary {
    margin-bottom: 32px;
  }
  
  .summary-item {
    display: flex;
    align-items: center;
    gap: 16px;
    padding: 20px;
    background: #f8f9fa;
    border-radius: 12px;
    text-align: center;
  }
  
  .summary-item.correct {
    background: linear-gradient(135deg, #67c23a, #85ce61);
    color: white;
  }
  
  .summary-item.incorrect {
    background: linear-gradient(135deg, #f56c6c, #f78989);
    color: white;
  }
  
  .summary-item i {
    font-size: 24px;
  }
  
  .summary-item .number {
    display: block;
    font-size: 24px;
    font-weight: 600;
  }
  
  .summary-item .label {
    font-size: 14px;
    opacity: 0.8;
  }
  
  /* 选项分析 */
  .options-analysis h4 {
    font-size: 16px;
    font-weight: 600;
    color: #303133;
    margin-bottom: 16px;
  }
  
  .options-chart {
    display: grid;
    gap: 12px;
  }
  
  .option-bar {
    display: flex;
    align-items: center;
    gap: 12px;
  }
  
  .option-label {
    min-width: 200px;
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  
  .option-text {
    color: #303133;
  }
  
  .option-count {
    color: #909399;
    font-size: 14px;
  }
  
  .option-progress {
    flex: 1;
    height: 20px;
    background: #f0f2f5;
    border-radius: 10px;
    overflow: hidden;
  }
  
  .progress-bar {
    height: 100%;
    background: #409eff;
    transition: width 0.3s ease;
  }
  
  .progress-bar.correct {
    background: #67c23a;
  }
  
  /* 学生答题详情 */
  .student-answers h4 {
    font-size: 16px;
    font-weight: 600;
    color: #303133;
    margin-bottom: 16px;
  }
  
  /* 响应式设计 */
  @media (max-width: 768px) {
    .stats-cards {
      grid-template-columns: 1fr;
    }
    
    .record-header {
      flex-direction: column;
      gap: 12px;
    }
    
    .record-content {
      flex-direction: column;
      align-items: flex-start;
      gap: 16px;
    }
    
    .record-info {
      flex-wrap: wrap;
    }
  }
  </style>
  