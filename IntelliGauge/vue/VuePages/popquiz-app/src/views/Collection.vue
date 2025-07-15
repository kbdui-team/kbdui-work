<template>
    <div class="input-collection-page">
      <div class="sidebar">
        <div class="feature-menu">
          <h3>功能菜单</h3>
          <ul class="menu-list">
            <li class="menu-item">
              <el-icon><Document /></el-icon>
              学习模块
            </li>
            <li class="menu-item active">
              <el-icon><Upload /></el-icon>
              输入收集
              <span class="badge">6</span>
            </li>
            <li class="menu-item">
              <el-icon><Edit /></el-icon>
              练习模式
            </li>
            <li class="menu-item">
              <el-icon><View /></el-icon>
              错题回顾
            </li>
          </ul>
  
          <div class="completion-section">
            <h3>成绩模块</h3>
            <ul class="menu-list">
              <li class="menu-item">
                <el-icon><DataAnalysis /></el-icon>
                成绩查询
              </li>
              <li class="menu-item">
                <el-icon><TrendCharts /></el-icon>
                排行榜
              </li>
              <li class="menu-item">
                <el-icon><Medal /></el-icon>
                学习进度
              </li>
            </ul>
          </div>
  
          <div class="settings-section">
            <h3>设置模块</h3>
            <ul class="menu-list">
              <li class="menu-item">
                <el-icon><User /></el-icon>
                个人设置
              </li>
              <li class="menu-item">
                <el-icon><Setting /></el-icon>
                通用设置
              </li>
              <li class="menu-item">
                <el-icon><SwitchButton /></el-icon>
                退出登录
              </li>
            </ul>
          </div>
        </div>
  
        <div class="footer-info">
          <p>© 2024 PopQuiz 在线学习平台</p>
          <p>当前版本: v2.1.0</p>
          <p>最后更新: 2024-07-14 14:30</p>
        </div>
      </div>
  
      <div class="main-content">
        <div class="content-header">
          <h1>多模态输入收集</h1>
          <p>支持文本、PowerPoint、PDF、音频、视频等多种格式文件的内容提取和保存</p>
        </div>
  
        <div class="upload-sections">
          <!-- 文本文件上传 -->
          <el-card class="upload-card">
            <template #header>
              <div class="card-header">
                <el-icon class="file-icon text-icon"><Document /></el-icon>
                <span>文本文件上传</span>
                <el-tag size="small" type="info">支持 .txt, .doc, .docx</el-tag>
              </div>
            </template>
            <el-upload
              class="upload-dragger"
              drag
              action="/api/upload/text"
              :before-upload="beforeTextUpload"
              :on-success="handleTextSuccess"
              :on-error="handleUploadError"
              accept=".txt,.doc,.docx"
            >
              <el-icon class="el-icon--upload"><upload-filled /></el-icon>
              <div class="el-upload__text">
                将文本文件拖到此处，或<em>点击上传</em>
              </div>
            </el-upload>
            <div class="upload-status" v-if="textStatus">
              <el-progress :percentage="textProgress" :status="textStatus"></el-progress>
            </div>
          </el-card>
  
          <!-- PowerPoint文件上传 -->
          <el-card class="upload-card">
            <template #header>
              <div class="card-header">
                <el-icon class="file-icon ppt-icon"><Files /></el-icon>
                <span>PowerPoint文件上传</span>
                <el-tag size="small" type="warning">支持 .ppt, .pptx</el-tag>
              </div>
            </template>
            <el-upload
              class="upload-dragger"
              drag
              action="/api/upload/powerpoint"
              :before-upload="beforePPTUpload"
              :on-success="handlePPTSuccess"
              :on-error="handleUploadError"
              accept=".ppt,.pptx"
            >
              <el-icon class="el-icon--upload"><upload-filled /></el-icon>
              <div class="el-upload__text">
                将PowerPoint文件拖到此处，或<em>点击上传</em>
              </div>
            </el-upload>
            <div class="upload-status" v-if="pptStatus">
              <el-progress :percentage="pptProgress" :status="pptStatus"></el-progress>
            </div>
          </el-card>
  
          <!-- PDF文件上传 -->
          <el-card class="upload-card">
            <template #header>
              <div class="card-header">
                <el-icon class="file-icon pdf-icon"><Reading /></el-icon>
                <span>PDF文件上传</span>
                <el-tag size="small" type="danger">支持 .pdf</el-tag>
              </div>
            </template>
            <el-upload
              class="upload-dragger"
              drag
              action="/api/upload/pdf"
              :before-upload="beforePDFUpload"
              :on-success="handlePDFSuccess"
              :on-error="handleUploadError"
              accept=".pdf"
            >
              <el-icon class="el-icon--upload"><upload-filled /></el-icon>
              <div class="el-upload__text">
                将PDF文件拖到此处，或<em>点击上传</em>
              </div>
            </el-upload>
            <div class="upload-status" v-if="pdfStatus">
              <el-progress :percentage="pdfProgress" :status="pdfStatus"></el-progress>
            </div>
          </el-card>
  
          <!-- 音频文件上传 -->
          <el-card class="upload-card">
            <template #header>
              <div class="card-header">
                <el-icon class="file-icon audio-icon"><Microphone /></el-icon>
                <span>音频文件上传</span>
                <el-tag size="small" type="success">支持 .mp3, .wav, .m4a</el-tag>
              </div>
            </template>
            <el-upload
              class="upload-dragger"
              drag
              action="/api/upload/audio"
              :before-upload="beforeAudioUpload"
              :on-success="handleAudioSuccess"
              :on-error="handleUploadError"
              accept=".mp3,.wav,.m4a,.aac"
            >
              <el-icon class="el-icon--upload"><upload-filled /></el-icon>
              <div class="el-upload__text">
                将音频文件拖到此处，或<em>点击上传</em>
              </div>
              <div class="el-upload__tip">
                音频将自动转换为文字并保存到数据库
              </div>
            </el-upload>
            <div class="upload-status" v-if="audioStatus">
              <el-progress :percentage="audioProgress" :status="audioStatus"></el-progress>
            </div>
          </el-card>
  
          <!-- 视频文件上传 -->
          <el-card class="upload-card">
            <template #header>
              <div class="card-header">
                <el-icon class="file-icon video-icon"><VideoCamera /></el-icon>
                <span>视频文件上传</span>
                <el-tag size="small" type="primary">支持 .mp4, .avi, .mov</el-tag>
              </div>
            </template>
            <el-upload
              class="upload-dragger"
              drag
              action="/api/upload/video"
              :before-upload="beforeVideoUpload"
              :on-success="handleVideoSuccess"
              :on-error="handleUploadError"
              accept=".mp4,.avi,.mov,.wmv"
            >
              <el-icon class="el-icon--upload"><upload-filled /></el-icon>
              <div class="el-upload__text">
                将视频文件拖到此处，或<em>点击上传</em>
              </div>
              <div class="el-upload__tip">
                视频音频将转换为文字，画面中的文字信息也会被识别
              </div>
            </el-upload>
            <div class="upload-status" v-if="videoStatus">
              <el-progress :percentage="videoProgress" :status="videoStatus"></el-progress>
            </div>
          </el-card>
  
          <!-- 视频文字识别上传 -->
          <el-card class="upload-card">
            <template #header>
              <div class="card-header">
                <el-icon class="file-icon ocr-icon"><View /></el-icon>
                <span>视频文字识别</span>
                <el-tag size="small" type="info">OCR + 语音识别</el-tag>
              </div>
            </template>
            <el-upload
              class="upload-dragger"
              drag
              action="/api/upload/video-ocr"
              :before-upload="beforeVideoOCRUpload"
              :on-success="handleVideoOCRSuccess"
              :on-error="handleUploadError"
              accept=".mp4,.avi,.mov,.wmv"
            >
              <el-icon class="el-icon--upload"><upload-filled /></el-icon>
              <div class="el-upload__text">
                将视频文件拖到此处，或<em>点击上传</em>
              </div>
              <div class="el-upload__tip">
                专门针对PowerPoint演示视频，提取画面文字和语音内容
              </div>
            </el-upload>
            <div class="upload-status" v-if="videoOCRStatus">
              <el-progress :percentage="videoOCRProgress" :status="videoOCRStatus"></el-progress>
            </div>
          </el-card>
        </div>
  
        <!-- 处理记录 -->
        <div class="processing-history">
          <h3>处理记录</h3>
          <el-table :data="processingHistory" style="width: 100%">
            <el-table-column prop="filename" label="文件名" width="200"></el-table-column>
            <el-table-column prop="type" label="文件类型" width="120">
              <template #default="scope">
                <el-tag :type="getTypeColor(scope.row.type)">{{ scope.row.type }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="size" label="文件大小" width="120"></el-table-column>
            <el-table-column prop="status" label="处理状态" width="120">
              <template #default="scope">
                <el-tag :type="getStatusColor(scope.row.status)">{{ scope.row.status }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="extractedText" label="提取文字预览" min-width="200">
              <template #default="scope">
                <div class="text-preview">{{ scope.row.extractedText }}</div>
              </template>
            </el-table-column>
            <el-table-column prop="uploadTime" label="上传时间" width="180"></el-table-column>
            <el-table-column label="操作" width="120">
              <template #default="scope">
                <el-button size="small" @click="viewDetails(scope.row)">详情</el-button>
                <el-button size="small" type="danger" @click="deleteRecord(scope.row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
    </div>
  </template>
  
  <script>
  import { ref, reactive, onMounted } from 'vue'
  import { ElMessage, ElMessageBox } from 'element-plus'
  import {
    Document,
    Upload,
    Edit,
    View,
    DataAnalysis,
    TrendCharts,
    Medal,
    User,
    Setting,
    SwitchButton,
    Files,
    Reading,
    Microphone,
    VideoCamera,
    UploadFilled
  } from '@element-plus/icons-vue'
  
  export default {
    name: 'InputCollection',
    components: {
      Document,
      Upload,
      Edit,
      View,
      DataAnalysis,
      TrendCharts,
      Medal,
      User,
      Setting,
      SwitchButton,
      Files,
      Reading,
      Microphone,
      VideoCamera,
      UploadFilled
    },
    setup() {
      // 上传进度状态
      const textProgress = ref(0)
      const textStatus = ref('')
      const pptProgress = ref(0)
      const pptStatus = ref('')
      const pdfProgress = ref(0)
      const pdfStatus = ref('')
      const audioProgress = ref(0)
      const audioStatus = ref('')
      const videoProgress = ref(0)
      const videoStatus = ref('')
      const videoOCRProgress = ref(0)
      const videoOCRStatus = ref('')
  
      // 处理记录
      const processingHistory = ref([
        {
          id: 1,
          filename: '学习资料.docx',
          type: '文本文件',
          size: '2.5MB',
          status: '已完成',
          extractedText: '这是一份关于机器学习的学习资料，包含了基础概念和实例...',
          uploadTime: '2024-07-14 14:30'
        },
        {
          id: 2,
          filename: '课程演示.pptx',
          type: 'PowerPoint',
          size: '8.2MB',
          status: '处理中',
          extractedText: '正在提取PowerPoint文件中的文本内容...',
          uploadTime: '2024-07-14 14:25'
        },
        {
          id: 3,
          filename: '讲座录音.mp3',
          type: '音频文件',
          size: '45.6MB',
          status: '已完成',
          extractedText: '今天我们来讲解人工智能的基本原理和应用场景...',
          uploadTime: '2024-07-14 14:20'
        }
      ])
  
      // 文件上传前验证
      const beforeTextUpload = (file) => {
        const isValidType = ['txt', 'doc', 'docx'].includes(file.name.split('.').pop().toLowerCase())
        const isLt50M = file.size / 1024 / 1024 < 50
        
        if (!isValidType) {
          ElMessage.error('只能上传 txt、doc、docx 格式文件!')
          return false
        }
        if (!isLt50M) {
          ElMessage.error('文件大小不能超过 50MB!')
          return false
        }
        return true
      }
  
      const beforePPTUpload = (file) => {
        const isValidType = ['ppt', 'pptx'].includes(file.name.split('.').pop().toLowerCase())
        const isLt100M = file.size / 1024 / 1024 < 100
        
        if (!isValidType) {
          ElMessage.error('只能上传 ppt、pptx 格式文件!')
          return false
        }
        if (!isLt100M) {
          ElMessage.error('文件大小不能超过 100MB!')
          return false
        }
        return true
      }
  
      const beforePDFUpload = (file) => {
        const isValidType = file.name.split('.').pop().toLowerCase() === 'pdf'
        const isLt50M = file.size / 1024 / 1024 < 50
        
        if (!isValidType) {
          ElMessage.error('只能上传 PDF 格式文件!')
          return false
        }
        if (!isLt50M) {
          ElMessage.error('文件大小不能超过 50MB!')
          return false
        }
        return true
      }
  
      const beforeAudioUpload = (file) => {
        const isValidType = ['mp3', 'wav', 'm4a', 'aac'].includes(file.name.split('.').pop().toLowerCase())
        const isLt200M = file.size / 1024 / 1024 < 200
        
        if (!isValidType) {
          ElMessage.error('只能上传 mp3、wav、m4a、aac 格式音频文件!')
          return false
        }
        if (!isLt200M) {
          ElMessage.error('文件大小不能超过 200MB!')
          return false
        }
        return true
      }
  
      const beforeVideoUpload = (file) => {
        const isValidType = ['mp4', 'avi', 'mov', 'wmv'].includes(file.name.split('.').pop().toLowerCase())
        const isLt500M = file.size / 1024 / 1024 < 500
        
        if (!isValidType) {
          ElMessage.error('只能上传 mp4、avi、mov、wmv 格式视频文件!')
          return false
        }
        if (!isLt500M) {
          ElMessage.error('文件大小不能超过 500MB!')
          return false
        }
        return true
      }
  
      const beforeVideoOCRUpload = beforeVideoUpload
  
      // 上传成功处理
      const handleTextSuccess = (response, file) => {
        ElMessage.success('文本文件上传成功!')
        addProcessingRecord(file, '文本文件', response.extractedText)
      }
  
      const handlePPTSuccess = (response, file) => {
        ElMessage.success('PowerPoint文件上传成功!')
        addProcessingRecord(file, 'PowerPoint', response.extractedText)
      }
  
      const handlePDFSuccess = (response, file) => {
        ElMessage.success('PDF文件上传成功!')
        addProcessingRecord(file, 'PDF文件', response.extractedText)
      }
  
      const handleAudioSuccess = (response, file) => {
        ElMessage.success('音频文件上传成功!')
        addProcessingRecord(file, '音频文件', response.extractedText)
      }
  
      const handleVideoSuccess = (response, file) => {
        ElMessage.success('视频文件上传成功!')
        addProcessingRecord(file, '视频文件', response.extractedText)
      }
  
      const handleVideoOCRSuccess = (response, file) => {
        ElMessage.success('视频文字识别完成!')
        addProcessingRecord(file, '视频OCR', response.extractedText)
      }
  
      const handleUploadError = (error, file) => {
        ElMessage.error(`文件上传失败: ${file.name}`)
        console.error('Upload error:', error)
      }
  
      // 添加处理记录
      const addProcessingRecord = (file, type, extractedText) => {
        const newRecord = {
          id: Date.now(),
          filename: file.name,
          type: type,
          size: (file.size / 1024 / 1024).toFixed(2) + 'MB',
          status: '已完成',
          extractedText: extractedText || '文本提取中...',
          uploadTime: new Date().toLocaleString()
        }
        processingHistory.value.unshift(newRecord)
      }
  
      // 获取类型颜色
      const getTypeColor = (type) => {
        const colors = {
          '文本文件': 'info',
          'PowerPoint': 'warning',
          'PDF文件': 'danger',
          '音频文件': 'success',
          '视频文件': 'primary',
          '视频OCR': 'info'
        }
        return colors[type] || 'info'
      }
  
      // 获取状态颜色
      const getStatusColor = (status) => {
        const colors = {
          '已完成': 'success',
          '处理中': 'warning',
          '失败': 'danger'
        }
        return colors[status] || 'info'
      }
  
      // 查看详情
      const viewDetails = (record) => {
        ElMessageBox.alert(record.extractedText, `文件详情 - ${record.filename}`, {
          confirmButtonText: '确定',
          type: 'info'
        })
      }
  
      // 删除记录
      const deleteRecord = (record) => {
        ElMessageBox.confirm(`确定要删除文件 "${record.filename}" 的处理记录吗?`, '确认删除', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          const index = processingHistory.value.findIndex(item => item.id === record.id)
          if (index > -1) {
            processingHistory.value.splice(index, 1)
            ElMessage.success('删除成功!')
          }
        }).catch(() => {
          ElMessage.info('已取消删除')
        })
      }
  
      return {
        textProgress,
        textStatus,
        pptProgress,
        pptStatus,
        pdfProgress,
        pdfStatus,
        audioProgress,
        audioStatus,
        videoProgress,
        videoStatus,
        videoOCRProgress,
        videoOCRStatus,
        processingHistory,
        beforeTextUpload,
        beforePPTUpload,
        beforePDFUpload,
        beforeAudioUpload,
        beforeVideoUpload,
        beforeVideoOCRUpload,
        handleTextSuccess,
        handlePPTSuccess,
        handlePDFSuccess,
        handleAudioSuccess,
        handleVideoSuccess,
        handleVideoOCRSuccess,
        handleUploadError,
        getTypeColor,
        getStatusColor,
        viewDetails,
        deleteRecord
      }
    }
  }
  </script>
  
  <style scoped>
  .input-collection-page {
    display: flex;
    min-height: 100vh;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  }
  
  .sidebar {
    width: 280px;
    background: rgba(255, 255, 255, 0.95);
    backdrop-filter: blur(10px);
    border-radius: 20px;
    margin: 20px;
    padding: 30px 20px 20px 20px;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
    display: flex;
    flex-direction: column;
  }
  
  .feature-menu h3 {
    color: #333;
    margin-bottom: 20px;
    font-size: 16px;
    font-weight: 600;
  }
  
  .menu-list {
    list-style: none;
    padding: 0;
    margin: 0 0 30px 0;
  }
  
  .menu-item {
    display: flex;
    align-items: center;
    padding: 12px 16px;
    margin-bottom: 8px;
    border-radius: 12px;
    cursor: pointer;
    transition: all 0.3s ease;
    color: #666;
    position: relative;
  }
  
  .menu-item:hover {
    background: rgba(102, 126, 234, 0.1);
    color: #667eea;
  }
  
  .menu-item.active {
    background: linear-gradient(135deg, #667eea, #764ba2);
    color: white;
  }
  
  .menu-item .el-icon {
    margin-right: 10px;
    font-size: 18px;
  }
  
  .badge {
    background: #ff4757;
    color: white;
    border-radius: 10px;
    padding: 2px 8px;
    font-size: 12px;
    margin-left: auto;
  }
  
  .main-content {
    flex: 1;
    padding: 20px;
    overflow-y: auto;
  }
  
  .content-header {
    background: rgba(255, 255, 255, 0.95);
    backdrop-filter: blur(10px);
    border-radius: 20px;
    padding: 30px;
    margin-bottom: 30px;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  }
  
  .content-header h1 {
    color: #333;
    margin: 0 0 10px 0;
    font-size: 28px;
    font-weight: 700;
  }
  
  .content-header p {
    color: #666;
    margin: 0;
    font-size: 16px;
  }
  
  .upload-sections {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
    gap: 20px;
    margin-bottom: 30px;
  }
  
  .upload-card {
    border-radius: 16px;
    overflow: hidden;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
    transition: transform 0.3s ease;
  }
  
  .upload-card:hover {
    transform: translateY(-5px);
  }
  
  .card-header {
    display: flex;
    align-items: center;
    gap: 10px;
  }
  
  .file-icon {
    font-size: 20px;
  }
  
  .text-icon { color: #409eff; }
  .ppt-icon { color: #e6a23c; }
  .pdf-icon { color: #f56c6c; }
  .audio-icon { color: #67c23a; }
  .video-icon { color: #909399; }
  .ocr-icon { color: #409eff; }
  
  .upload-dragger {
    width: 100%;
  }
  
  .upload-dragger .el-upload-dragger {
    border: 2px dashed #d9d9d9;
    border-radius: 12px;
    padding: 40px;
    text-align: center;
    background: #fafafa;
    transition: all 0.3s ease;
  }
  
  .upload-dragger .el-upload-dragger:hover {
    border-color: #667eea;
    background: rgba(102, 126, 234, 0.05);
  }
  
  .upload-status {
    margin-top: 15px;
  }
  
  .processing-history {
    background: rgba(255, 255, 255, 0.95);
    backdrop-filter: blur(10px);
    border-radius: 20px;
    padding: 30px;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  }
  
  .processing-history h3 {
    color: #333;
    margin: 0 0 20px 0;
    font-size: 20px;
    font-weight: 600;
  }
  
  .text-preview {
    max-width: 200px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    color: #666;
  }
  
  .footer-info {
    margin-top: auto;
    padding-top: 20px;
    border-top: 1px solid #eee;
    text-align: center;
  }
  
  .footer-info p {
    color: #999;
    font-size: 12px;
    margin: 5px 0;
  }
  
  :deep(.el-card__header) {
    padding: 20px;
    background: #f8f9fa;
  }
  
  :deep(.el-card__body) {
    padding: 20px;
  }
  
  :deep(.el-upload__tip) {
    color: #999;
    font-size: 12px;
    margin-top: 10px;
  }
  
  :deep(.el-table) {
    border-radius: 12px;
    overflow: hidden;
  }
  
  :deep(.el-table th) {
    background: #f8f9fa;
    color: #333;
    font-weight: 600;
  }
  </style>
  