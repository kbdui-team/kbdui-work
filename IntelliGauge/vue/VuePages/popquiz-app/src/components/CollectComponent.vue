<template>
    <div class="upload-component">
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
            :http-request="customTextUploadRequest"
            :before-upload="beforeTextUpload"
            accept=".txt,.doc,.docx"
            :show-file-list="false"
          >
            <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
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
            <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
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
            <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
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
            <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
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
            <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
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
            <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
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
  </template>
  
  <script setup lang="ts">
  import { ref, inject } from 'vue'
  import { ElMessage, ElMessageBox } from 'element-plus'
  import {
    Document,
    Files,
    Reading,
    Microphone,
    VideoCamera,
    UploadFilled,
    View
  } from '@element-plus/icons-vue'
  import type { UploadRequestOptions } from 'element-plus'
  
  // 定义组件事件
  const emit = defineEmits(['uploadSuccess', 'uploadError'])
  
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
  
  // 处理记录类型
  interface ProcessingRecord {
    id: number
    filename: string
    type: string
    size: string
    status: string
    extractedText: string
    uploadTime: string
  }

  const processingHistory = ref<ProcessingRecord[]>([])
  
  // 新增: 题目和选项 DTO 类型
  interface QuestionDTO {
    id?:number
    lectureId?: number
    contentInputId?: number
    questionType?: string
    questionText: string
  }
  interface QuestionOptionsDTO {
    questionId: number
    optionText: string
    optionOrder: string
    isCorrect: boolean
  }
  
  // 获取后端 baseurl
  const baseurl = inject('baseurl', 'http://localhost:5555') as string
  
  // 文件上传前验证
  const beforeTextUpload = (file: File) => {
    const isValidType = ['txt', 'doc', 'docx'].includes(file.name.split('.').pop()?.toLowerCase() || '')
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

  // 自定义上传方法，使用 fetch（无超时限制）
  const customTextUploadRequest = async (option: UploadRequestOptions) => {
    const file = option.file as File
    // TODO: 这里可根据实际业务传入或选择 lectureId 和 contentInputId
    // 目前写死为 1，可根据实际需求传参或弹窗选择
    const lectureId: number = 1 // 必须为 number，不能为 undefined/null
    // 生成 10000~99999 的 5 位随机数作为 contentInputId
    const contentInputId: number = Math.floor(10000 + Math.random() * 90000)
    try {
      const formData = new FormData()
      formData.append('file', file)
      formData.append('count', '5')
      formData.append('model', 'v3')
      // 超时控制：10分钟
      const fetchWithTimeout = (url: string, opts: RequestInit, timeout = 600000) => {
        return Promise.race([
          fetch(url, opts),
          new Promise((_, reject) => setTimeout(() => reject(new Error('请求超时，请稍后重试')), timeout))
        ])
      }
      let response: Response
      try {
        response = await fetchWithTimeout(`${baseurl}/deepseek/generate-questions`, {
          method: 'POST',
          body: formData
        }) as Response
      } catch {
        ElMessage.error('题目生成请求超时，已判定为失败')
        addProcessingRecord(file, '文本文件', '请求超时', '失败')
        if (option.onError) {
          option.onError({
            name: 'UploadAjaxError',
            status: 408,
            method: 'POST',
            url: `${baseurl}/deepseek/generate-questions`,
            message: '请求超时'
          })
        }
        return
      }
      const result = await response.json()
      if ((result.code === 0 || result.code === 200) && result.data) {
        ElMessage.success('题目生成成功！')
        const preview = JSON.stringify(result.data).slice(0, 100) + (JSON.stringify(result.data).length > 100 ? '...' : '')
        addProcessingRecord(file, '文本文件', preview, '已完成')
        // 新增: 循环存储题目和选项
        if (Array.isArray(result.data)) {
          for (const q of result.data) {
            // 1. 存储题目
            const questionDTO: QuestionDTO = {
              questionText: q.question,
              lectureId,
              contentInputId,
              questionType:"单选题"
              
            }
            const questionRes = await fetch(`${baseurl}/question/add`, {
              method: 'POST',
              headers: { 'Content-Type': 'application/json' },
              body: JSON.stringify(questionDTO)
            })
            
            console.log("dkasjhdk",questionRes)
            const questionSaved = await questionRes.json()
            console.log(questionSaved)
            const questionId = questionSaved.id
            // 2. 存储选项（每个选项单独POST，四个选项同属一个questionId）
            if (Array.isArray(q.options)) {
              for (let i = 0; i < q.options.length; i++) {
                const optionDTO: QuestionOptionsDTO = {
                  questionId: questionId, // 确保等于刚刚保存问题的id
                  optionText: q.options[i],
                  optionOrder: String.fromCharCode(65 + i), // A/B/C/D
                  isCorrect: (q.answer && q.answer.toUpperCase() === String.fromCharCode(65 + i))
                }
                console.log("dsaiwe",optionDTO)
                // 每个选项单独POST，获得自己的id
                await fetch(`${baseurl}/question-options/add`, {
                  method: 'POST',
                  headers: { 'Content-Type': 'application/json' },
                  body: JSON.stringify(optionDTO)
                })
              }
            }
          }
        }
        if (option.onSuccess) { option.onSuccess(result) }
      } else {
        ElMessage.error('题目生成失败: ' + (result.message || '未知错误'))
        addProcessingRecord(file, '文本文件', result.message || '未知错误', '失败')
        if (option.onError) {
          option.onError({
            name: 'UploadAjaxError',
            status: 500,
            method: 'POST',
            url: `${baseurl}/deepseek/generate-questions`,
            message: result.message || '题目生成失败'
          })
        }
      }
    } catch {
      ElMessage.error('题目生成请求失败')
      addProcessingRecord(file, '文本文件', '上传失败', '失败')
      if (option.onError) {
        option.onError({
          name: 'UploadAjaxError',
          status: 500,
          method: 'POST',
          url: `${baseurl}/deepseek/generate-questions`,
          message: '题目生成请求失败'
        })
      }
    }
  }

  // 上传失败处理
  const handleUploadError = (error: unknown, file: File) => {
    ElMessage.error(`文件上传失败: ${file.name}`)
    addProcessingRecord(file, '文本文件', '上传失败', '失败')
  }
  
  const beforePPTUpload = (file: File) => {
    const isValidType = ['ppt', 'pptx'].includes(file.name.split('.').pop()?.toLowerCase() || '')
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
  
  const beforePDFUpload = (file: File) => {
    const isValidType = file.name.split('.').pop()?.toLowerCase() === 'pdf'
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
  
  const beforeAudioUpload = (file: File) => {
    const isValidType = ['mp3', 'wav', 'm4a', 'aac'].includes(file.name.split('.').pop()?.toLowerCase() || '')
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
  
  const beforeVideoUpload = (file: File) => {
    const isValidType = ['mp4', 'avi', 'mov', 'wmv'].includes(file.name.split('.').pop()?.toLowerCase() || '')
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
  const handlePPTSuccess = (response: unknown, file: File) => {
    ElMessage.success('PowerPoint文件上传成功!')
    addProcessingRecord(file, 'PowerPoint', (response as {extractedText: string}).extractedText, '已完成')
    emit('uploadSuccess', { type: 'ppt', file, response })
  }
  
  const handlePDFSuccess = (response: unknown, file: File) => {
    ElMessage.success('PDF文件上传成功!')
    addProcessingRecord(file, 'PDF文件', (response as {extractedText: string}).extractedText, '已完成')
    emit('uploadSuccess', { type: 'pdf', file, response })
  }
  
  const handleAudioSuccess = (response: unknown, file: File) => {
    ElMessage.success('音频文件上传成功!')
    addProcessingRecord(file, '音频文件', (response as {extractedText: string}).extractedText, '已完成')
    emit('uploadSuccess', { type: 'audio', file, response })
  }
  
  const handleVideoSuccess = (response: unknown, file: File) => {
    ElMessage.success('视频文件上传成功!')
    addProcessingRecord(file, '视频文件', (response as {extractedText: string}).extractedText, '已完成')
    emit('uploadSuccess', { type: 'video', file, response })
  }
  
  const handleVideoOCRSuccess = (response: unknown, file: File) => {
    ElMessage.success('视频文字识别完成!')
    addProcessingRecord(file, '视频OCR', (response as {extractedText: string}).extractedText, '已完成')
    emit('uploadSuccess', { type: 'video-ocr', file, response })
  }
  
  // 添加处理记录
  const addProcessingRecord = (file: File, type: string, extractedText: string, status: string) => {
    const newRecord: ProcessingRecord = {
      id: Date.now(),
      filename: file.name,
      type,
      size: (file.size / 1024 / 1024).toFixed(2) + 'MB',
      status,
      extractedText,
      uploadTime: new Date().toLocaleString()
    }
    processingHistory.value.unshift(newRecord)
  }
  
  // 获取类型颜色
  const getTypeColor = (type: string) => {
    const colors: Record<string, string> = {
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
  const getStatusColor = (status: string) => {
    const colors: Record<string, string> = {
      '已完成': 'success',
      '处理中': 'warning',
      '失败': 'danger'
    }
    return colors[status] || 'info'
  }
  
  // 查看详情
  const viewDetails = (record: ProcessingRecord) => {
    ElMessageBox.alert(record.extractedText, `文件详情 - ${record.filename}`, {
      confirmButtonText: '确定',
      type: 'info'
    })
  }
  
  // 删除记录
  const deleteRecord = (record: ProcessingRecord) => {
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
  </script>
  
  <style scoped>
  .upload-component {
    padding: 20px;
    background: #f8f9fa;
    border-radius: 8px;
  }
  
  .content-header {
    text-align: center;
    margin-bottom: 30px;
    padding: 20px;
    background: white;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  }
  
  .content-header h1 {
    color: #333;
    font-size: 28px;
    margin-bottom: 10px;
  }
  
  .content-header p {
    color: #666;
    font-size: 16px;
    margin: 0;
  }
  
  .upload-sections {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
    gap: 20px;
    margin-bottom: 30px;
  }
  
  .upload-card {
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  }
  
  .card-header {
    display: flex;
    align-items: center;
    gap: 10px;
    font-weight: 600;
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
  
  .upload-status {
    margin-top: 15px;
  }
  
  .processing-history {
    background: white;
    padding: 20px;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  }
  
  .processing-history h3 {
    margin-bottom: 20px;
    color: #333;
  }
  
  .text-preview {
    max-width: 200px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    cursor: pointer;
  }
  
  .text-preview:hover {
    color: #409eff;
  }
  
  @media (max-width: 768px) {
    .upload-sections {
      grid-template-columns: 1fr;
    }
    
    .upload-component {
      padding: 15px;
    }
  }
  </style>
  