<template>
    <div class="notification-container">
      <!-- 页面头部 -->
      <div class="page-header">
        <h1 class="page-title">通知设置</h1>
        <div class="header-actions">
          <el-button type="primary" @click="saveSettings">保存设置</el-button>
          <el-button @click="resetSettings">重置</el-button>
        </div>
      </div>
  
      <!-- 通知设置内容 -->
      <div class="settings-content">
        <el-row :gutter="20">
          <!-- 左侧设置面板 -->
          <el-col :span="16">
            <!-- 系统通知设置 -->
            <el-card class="settings-card" shadow="hover">
              <template #header>
                <div class="card-header">
                  <el-icon><Bell /></el-icon>
                  <span>系统通知</span>
                </div>
              </template>
              
              <div class="notification-group">
                <div class="notification-item">
                  <div class="notification-info">
                    <h4>登录通知</h4>
                    <p>当有新的登录活动时通知我</p>
                  </div>
                  <el-switch v-model="settings.system.loginNotification" />
                </div>
                
                <div class="notification-item">
                  <div class="notification-info">
                    <h4>安全警告</h4>
                    <p>检测到异常活动时发送安全警告</p>
                  </div>
                  <el-switch v-model="settings.system.securityAlert" />
                </div>
                
                <div class="notification-item">
                  <div class="notification-info">
                    <h4>系统维护</h4>
                    <p>系统维护和更新通知</p>
                  </div>
                  <el-switch v-model="settings.system.maintenanceNotice" />
                </div>
              </div>
            </el-card>
  
            <!-- 业务通知设置 -->
            <el-card class="settings-card" shadow="hover">
              <template #header>
                <div class="card-header">
                  <el-icon><Message /></el-icon>
                  <span>业务通知</span>
                </div>
              </template>
              
              <div class="notification-group">
                <div class="notification-item">
                  <div class="notification-info">
                    <h4>任务提醒</h4>
                    <p>任务截止日期和状态变更提醒</p>
                  </div>
                  <el-switch v-model="settings.business.taskReminder" />
                </div>
                
                <div class="notification-item">
                  <div class="notification-info">
                    <h4>审批通知</h4>
                    <p>待审批事项和审批结果通知</p>
                  </div>
                  <el-switch v-model="settings.business.approvalNotification" />
                </div>
                
                <div class="notification-item">
                  <div class="notification-info">
                    <h4>数据更新</h4>
                    <p>重要数据更新和同步通知</p>
                  </div>
                  <el-switch v-model="settings.business.dataUpdate" />
                </div>
                
                <div class="notification-item">
                  <div class="notification-info">
                    <h4>报告生成</h4>
                    <p>定期报告生成完成通知</p>
                  </div>
                  <el-switch v-model="settings.business.reportGeneration" />
                </div>
              </div>
            </el-card>
  
            <!-- 通知方式设置 -->
            <el-card class="settings-card" shadow="hover">
              <template #header>
                <div class="card-header">
                  <el-icon><ChatDotRound /></el-icon>
                  <span>通知方式</span>
                </div>
              </template>
              
              <div class="notification-methods">
                <el-row :gutter="20">
                  <el-col :span="8">
                    <div class="method-item">
                      <div class="method-header">
                        <el-icon><Message /></el-icon>
                        <span>站内信</span>
                        <el-switch v-model="settings.methods.inApp" />
                      </div>
                      <div class="method-settings" v-if="settings.methods.inApp">
                        <el-checkbox v-model="settings.methods.inAppSound">声音提醒</el-checkbox>
                        <el-checkbox v-model="settings.methods.inAppPopup">弹窗提醒</el-checkbox>
                      </div>
                    </div>
                  </el-col>
                  
                  <el-col :span="8">
                    <div class="method-item">
                      <div class="method-header">
                        <el-icon><Message /></el-icon>
                        <span>邮件通知</span>
                        <el-switch v-model="settings.methods.email" />
                      </div>
                      <div class="method-settings" v-if="settings.methods.email">
                        <el-input v-model="settings.methods.emailAddress" placeholder="邮箱地址" size="small" />
                        <el-select v-model="settings.methods.emailFrequency" placeholder="发送频率" size="small">
                          <el-option label="实时" value="realtime" />
                          <el-option label="每小时汇总" value="hourly" />
                          <el-option label="每日汇总" value="daily" />
                        </el-select>
                      </div>
                    </div>
                  </el-col>
                  
                  <el-col :span="8">
                    <div class="method-item">
                      <div class="method-header">
                        <el-icon><ChatDotRound /></el-icon>
                        <span>短信通知</span>
                        <el-switch v-model="settings.methods.sms" />
                      </div>
                      <div class="method-settings" v-if="settings.methods.sms">
                        <el-input v-model="settings.methods.phoneNumber" placeholder="手机号码" size="small" />
                        <el-checkbox v-model="settings.methods.smsUrgentOnly">仅紧急通知</el-checkbox>
                      </div>
                    </div>
                  </el-col>
                </el-row>
              </div>
            </el-card>
  
            <!-- 通知时间设置 -->
            <el-card class="settings-card" shadow="hover">
              <template #header>
                <div class="card-header">
                  <el-icon><Clock /></el-icon>
                  <span>通知时间</span>
                </div>
              </template>
              
              <div class="time-settings">
                <div class="time-item">
                  <label>免打扰时间</label>
                  <el-time-picker
                    v-model="settings.time.doNotDisturbStart"
                    placeholder="开始时间"
                    format="HH:mm"
                    value-format="HH:mm"
                  />
                  <span class="time-separator">至</span>
                  <el-time-picker
                    v-model="settings.time.doNotDisturbEnd"
                    placeholder="结束时间"
                    format="HH:mm"
                    value-format="HH:mm"
                  />
                </div>
                
                <div class="time-item">
                  <label>工作日通知</label>
                  <el-checkbox-group v-model="settings.time.workdays">
                    <el-checkbox label="monday">周一</el-checkbox>
                    <el-checkbox label="tuesday">周二</el-checkbox>
                    <el-checkbox label="wednesday">周三</el-checkbox>
                    <el-checkbox label="thursday">周四</el-checkbox>
                    <el-checkbox label="friday">周五</el-checkbox>
                    <el-checkbox label="saturday">周六</el-checkbox>
                    <el-checkbox label="sunday">周日</el-checkbox>
                  </el-checkbox-group>
                </div>
              </div>
            </el-card>
          </el-col>
  
          <!-- 右侧预览面板 -->
          <el-col :span="8">
            <el-card class="preview-card" shadow="hover">
              <template #header>
                <div class="card-header">
                  <el-icon><View /></el-icon>
                  <span>通知预览</span>
                </div>
              </template>
              
              <div class="notification-preview">
                <div class="preview-item" v-for="notification in previewNotifications" :key="notification.id">
                  <div class="preview-header">
                    <el-icon :class="notification.type"><component :is="notification.icon" /></el-icon>
                    <span class="preview-title">{{ notification.title }}</span>
                    <span class="preview-time">{{ notification.time }}</span>
                  </div>
                  <div class="preview-content">{{ notification.content }}</div>
                  <div class="preview-actions">
                    <el-button size="small" type="text">查看详情</el-button>
                    <el-button size="small" type="text">标记已读</el-button>
                  </div>
                </div>
              </div>
            </el-card>
  
            <!-- 通知统计 -->
            <el-card class="stats-card" shadow="hover">
              <template #header>
                <div class="card-header">
                  <el-icon><DataAnalysis /></el-icon>
                  <span>通知统计</span>
                </div>
              </template>
              
              <div class="notification-stats">
                <div class="stat-item">
                  <div class="stat-number">24</div>
                  <div class="stat-label">今日通知</div>
                </div>
                <div class="stat-item">
                  <div class="stat-number">156</div>
                  <div class="stat-label">本周通知</div>
                </div>
                <div class="stat-item">
                  <div class="stat-number">3</div>
                  <div class="stat-label">未读通知</div>
                </div>
                <div class="stat-item">
                  <div class="stat-number">98%</div>
                  <div class="stat-label">送达率</div>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>
    </div>
  </template>
  
  <script setup>
  import { ref, reactive } from 'vue'
  import { ElMessage } from 'element-plus'
  import { 
    Bell, 
    Message, 
    ChatDotRound, 
    Clock, 
    View, 
    DataAnalysis,
    Warning,
    InfoFilled,
    SuccessFilled
  } from '@element-plus/icons-vue'
  
  // 通知设置数据
  const settings = reactive({
    system: {
      loginNotification: true,
      securityAlert: true,
      maintenanceNotice: false
    },
    business: {
      taskReminder: true,
      approvalNotification: true,
      dataUpdate: false,
      reportGeneration: true
    },
    methods: {
      inApp: true,
      inAppSound: true,
      inAppPopup: false,
      email: true,
      emailAddress: 'user@example.com',
      emailFrequency: 'daily',
      sms: false,
      phoneNumber: '',
      smsUrgentOnly: true
    },
    time: {
      doNotDisturbStart: '22:00',
      doNotDisturbEnd: '08:00',
      workdays: ['monday', 'tuesday', 'wednesday', 'thursday', 'friday']
    }
  })
  
  // 预览通知数据
  const previewNotifications = ref([
    {
      id: 1,
      type: 'info',
      icon: 'InfoFilled',
      title: '系统维护通知',
      content: '系统将于今晚22:00-24:00进行维护升级',
      time: '2小时前'
    },
    {
      id: 2,
      type: 'warning',
      icon: 'Warning',
      title: '任务即将到期',
      content: '您有3个任务将在明天到期，请及时处理',
      time: '5小时前'
    },
    {
      id: 3,
      type: 'success',
      icon: 'SuccessFilled',
      title: '审批已通过',
      content: '您提交的申请已通过审批',
      time: '1天前'
    }
  ])
  
  // 保存设置
  const saveSettings = () => {
    // 这里应该调用API保存设置
    console.log('保存设置:', settings)
    ElMessage.success('通知设置已保存')
  }
  
  // 重置设置
  const resetSettings = () => {
    // 重置为默认设置
    ElMessage.info('设置已重置为默认值')
  }
  </script>
  
  <style scoped>
  .notification-container {
    padding: 20px;
    background-color: #f5f7fa;
    min-height: 100vh;
  }
  
  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
  }
  
  .page-title {
    font-size: 24px;
    font-weight: 500;
    margin: 0;
    color: #303133;
  }
  
  .header-actions {
    display: flex;
    gap: 10px;
  }
  
  .settings-content {
    margin-bottom: 20px;
  }
  
  .settings-card {
    margin-bottom: 20px;
    border-radius: 8px;
  }
  
  .card-header {
    display: flex;
    align-items: center;
    gap: 8px;
    font-weight: 500;
  }
  
  .notification-group {
    display: flex;
    flex-direction: column;
    gap: 20px;
  }
  
  .notification-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 16px;
    background-color: #f8f9fa;
    border-radius: 8px;
    border: 1px solid #e9ecef;
  }
  
  .notification-info h4 {
    margin: 0 0 4px 0;
    font-size: 16px;
    color: #303133;
  }
  
  .notification-info p {
    margin: 0;
    font-size: 14px;
    color: #606266;
  }
  
  .notification-methods {
    padding: 10px 0;
  }
  
  .method-item {
    padding: 16px;
    background-color: #f8f9fa;
    border-radius: 8px;
    border: 1px solid #e9ecef;
  }
  
  .method-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 12px;
  }
  
  .method-header span {
    flex: 1;
    margin-left: 8px;
    font-weight: 500;
  }
  
  .method-settings {
    display: flex;
    flex-direction: column;
    gap: 8px;
  }
  
  .method-settings .el-input,
  .method-settings .el-select {
    margin-top: 8px;
  }
  
  .time-settings {
    display: flex;
    flex-direction: column;
    gap: 20px;
  }
  
  .time-item {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 16px;
    background-color: #f8f9fa;
    border-radius: 8px;
    border: 1px solid #e9ecef;
  }
  
  .time-item label {
    min-width: 100px;
    font-weight: 500;
    color: #303133;
  }
  
  .time-separator {
    color: #606266;
  }
  
  .preview-card,
  .stats-card {
    margin-bottom: 20px;
    border-radius: 8px;
  }
  
  .notification-preview {
    max-height: 400px;
    overflow-y: auto;
  }
  
  .preview-item {
    padding: 12px;
    border-bottom: 1px solid #f0f0f0;
  }
  
  .preview-item:last-child {
    border-bottom: none;
  }
  
  .preview-header {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-bottom: 8px;
  }
  
  .preview-title {
    flex: 1;
    font-weight: 500;
    font-size: 14px;
  }
  
  .preview-time {
    font-size: 12px;
    color: #909399;
  }
  
  .preview-content {
    font-size: 13px;
    color: #606266;
    margin-bottom: 8px;
    line-height: 1.4;
  }
  
  .preview-actions {
    display: flex;
    gap: 8px;
  }
  
  .notification-stats {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 16px;
  }
  
  .stat-item {
    text-align: center;
    padding: 16px;
    background-color: #f8f9fa;
    border-radius: 8px;
    border: 1px solid #e9ecef;
  }
  
  .stat-number {
    font-size: 24px;
    font-weight: bold;
    color: #409eff;
    margin-bottom: 4px;
  }
  
  .stat-label {
    font-size: 12px;
    color: #606266;
  }
  
  .info {
    color: #409eff;
  }
  
  .warning {
    color: #e6a23c;
  }
  
  .success {
    color: #67c23a;
  }
  </style>