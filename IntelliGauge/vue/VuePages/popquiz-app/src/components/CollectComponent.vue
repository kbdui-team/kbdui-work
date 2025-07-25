<template>
    <div class="upload-component">
      <el-loading :loading="uploading" lock text="正在上传和生成题目，请稍候..." background="rgba(255,255,255,0.7)">
        <div class="content-header">
          <h1>多模态输入收集</h1>
          <p>支持从文本、PowerPoint、PDF、音频、等多种格式文件中生成问题</p>
        </div>
  
        <!-- 上传前参数选择对话框（所有上传共用） -->
        <el-dialog v-model="uploadDialogVisible" title="上传设置" width="400px" :close-on-click-modal="false" @open="fetchLectureList">
          <el-form label-width="90px">
            <el-form-item label="选择讲座">
              <el-select
                v-model="selectedLectureId"
                placeholder="请选择讲座"
                filterable
                allow-create
                default-first-option
                @change="onLectureChange"
                @input="onLectureInput"
                :loading="lectureListLoading"
              >
                <el-option v-for="lecture in lectureList" :key="lecture.id" :label="lecture.title" :value="lecture.id" />
                <template #empty>
                  <div style="color:#aaa;padding:8px 0;text-align:center;">暂无讲座，请新建</div>
                </template>
              </el-select>
              <el-input
                v-if="selectedLectureId==='__new__'"
                v-model="newLectureTitle"
                placeholder="输入新讲座名称"
                style="margin-top:8px;"
                @input="onLectureInput"
              />
              <el-button
                v-if="selectedLectureId==='__new__' && newLectureTitle.trim()"
                type="primary"
                size="small"
                style="margin-top:8px;"
                @click="createLecture"
              >创建讲座</el-button>
            </el-form-item>
            <el-form-item label="生成题目数">
              <el-input-number v-model="questionCount" :min="1" :max="20" />
            </el-form-item>
          </el-form>
          <template #footer>
            <el-button @click="uploadDialogVisible = false">取消</el-button>
            <el-button type="primary" @click="confirmUpload">确定</el-button>
          </template>
        </el-dialog>
  
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
              :http-request="beforeShowUploadDialog('text')"
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
              :http-request="beforeShowUploadDialog('ppt')"
              :before-upload="beforePPTUpload"
              accept=".ppt,.pptx"
              :show-file-list="false"
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
              :http-request="beforeShowUploadDialog('pdf')"
              :before-upload="beforePDFUpload"
              accept=".pdf"
              :show-file-list="false"
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
              :http-request="beforeShowUploadDialog('audio')"
              :before-upload="beforeAudioUpload"
              accept=".mp3,.wav,.m4a,.aac"
              :show-file-list="false"
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
            <!-- <el-table-column prop="extractedText" label="提取文字预览" min-width="200">
              <template #default="scope">
                <div class="text-preview">{{ scope.row.extractedText }}</div>
              </template>
            </el-table-column> -->
            <!-- <el-table-column prop="uploadTime" label="上传时间" width="180"></el-table-column> -->
            <el-table-column label="操作" width="120">
              <template #default="scope">
                <el-button size="small" @click="viewDetails(scope.row)">详情</el-button>
                <el-button size="small" type="danger" @click="deleteRecord(scope.row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-loading>
    </div>
  </template>
  
  <script setup lang="ts">
  import { ref, inject, onMounted } from 'vue'
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

  
  // 获取后端 baseurl
  const baseurl = inject('baseurl', 'http://localhost:5555') as string
  
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

  // 新增：讲座列表、选择、题目数
  const lectureList = ref<any[]>([])
  const lectureListLoading = ref(false)
  const selectedLectureId = ref<number|string|null>(null)
  const newLectureTitle = ref('')
  const questionCount = ref(5)
  const uploadDialogVisible = ref(false)
  let pendingUploadOption: any = null
  // 新增：记录上传类型
  const uploadType = ref<'text'|'ppt'|'pdf'|'audio'|null>(null)
  const uploading = ref(false)

  // 页面加载时获取所有内容输入记录
  onMounted(async () => {
    try {
      // const baseurl = inject('baseurl', 'http://localhost:5555') as string
      const res = await fetch(`${baseurl}/content-input/list`)
      const data = await res.json()
      if (Array.isArray(data)) {
        // 类型映射表
        const typeMap: Record<string, string> = {
          pdf: 'PDF文件',
          ppt: 'PowerPoint',
          pptx: 'PowerPoint',
          doc: '文本文件',
          docx: '文本文件',
          mp3: '音频文件',
          wav: '音频文件',
          m4a: '音频文件',
          aac: '音频文件',
          txt: '文本文件'
        }
        const mapped = data.map((item: any) => {
          // 取扩展名
          let ext = ''
          if (item.fileName && item.fileName.includes('.')) {
            ext = item.fileName.split('.').pop().toLowerCase()
          }
          return {
            id: item.id,
            filename: item.fileName || '',
            type: typeMap[item.contentType?.toLowerCase?.()] || typeMap[ext] || item.contentType || '未知',
            size: (typeof item.fileSize === 'number' ? (item.fileSize / 1024).toFixed(2) : '0.00') + 'MB',
            status: '已完成',
            extractedText: '',
            uploadTime: ''
          }
        })
        processingHistory.value = mapped
      }
    } catch (e) {
      // 可选：ElMessage.error('获取内容输入记录失败')
    }
    fetchLectureList()
  })
  
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
  
  // 工具函数：插入content_input并返回真实id，需传lectureId
  async function insertContentInput(file: File, lectureId: number) {
    const res = await fetch(`${baseurl}/content-input/add`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        fileName: file.name,
        fileSize: file.size,
        contentType: file.type || 'text',
        uploadTime: new Date().toISOString(),
        lectureId: lectureId,
        filepath: '暂无'
      })
    })
    const data = await res.json()
    // console.log("insertContentInput", data)
    return data
  }
  
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

  // 修改上传前弹窗方法，支持多类型
  const beforeShowUploadDialog = (type: 'text'|'ppt'|'pdf'|'audio') => (option: any) => {
    uploadType.value = type
    pendingUploadOption = option
    uploadDialogVisible.value = true
    return false // 阻止自动上传
  }

  // 确认上传时分发到不同上传方法
  const confirmUpload = () => {
    if (!selectedLectureId.value || selectedLectureId.value === '__new__') {
      ElMessage.error('请选择讲座')
      return
    }
    uploadDialogVisible.value = false
    const lectureIdNum = typeof selectedLectureId.value === 'string' ? Number(selectedLectureId.value) : selectedLectureId.value
    if (pendingUploadOption && uploadType.value) {
      if (uploadType.value === 'text') {
        customTextUploadRequestWithParams(pendingUploadOption, lectureIdNum, questionCount.value)
      } else if (uploadType.value === 'ppt') {
        customPPTUploadRequestWithParams(pendingUploadOption, lectureIdNum, questionCount.value)
      } else if (uploadType.value === 'pdf') {
        customPDFUploadRequestWithParams(pendingUploadOption, lectureIdNum, questionCount.value)
      } else if (uploadType.value === 'audio') {
        customAudioUploadRequestWithParams(pendingUploadOption, lectureIdNum, questionCount.value)
      }
      pendingUploadOption = null
      uploadType.value = null
    }
  }

  // 包装各自上传方法
  const customTextUploadRequestWithParams = (option: any, lectureId: number, count: number) => {
    customTextUploadRequest(option, lectureId, count)
  }
  const customPPTUploadRequestWithParams = (option: any, lectureId: number, count: number) => {
    customPPTUploadRequest(option, lectureId, count)
  }
  const customPDFUploadRequestWithParams = (option: any, lectureId: number, count: number) => {
    customPDFUploadRequest(option, lectureId, count)
  }
  const customAudioUploadRequestWithParams = (option: any, lectureId: number, count: number) => {
    customAudioUploadRequest(option, lectureId, count)
  }

  // 自定义上传方法，使用 fetch（无超时限制）
  const customTextUploadRequest = async (option: UploadRequestOptions, lectureId?: number, count?: number) => {
    uploading.value = true
    try {
      const file = option.file as File
      const realLectureId = lectureId || 1
      // 1. 先插入 content_input 记录，拿到真实id
      const contentInputId = await insertContentInput(file, realLectureId)
      if (!contentInputId) {
        ElMessage.error('content_input表插入失败')
        if (option.onError) option.onError({
          message: 'content_input插入失败', name: '', status: 0, method: '', url: ''
        })
        return
      }
      // 2. 检查/插入 lecture（如无则新建，示例用lectureId）
      let realLectureIdToUse: number | null = realLectureId // 使用传入的lectureId
      if (!realLectureIdToUse) {
        // fallback: 文件名查找
        try {
          const lectureListRes = await fetch(`${baseurl}/lecture/list`)
          const lectureList = await lectureListRes.json()
          console.log("lectureList", lectureList)
          const existLecture = Array.isArray(lectureList) ? lectureList.find((l: any) => l.title === file.name) : null
          if (existLecture) {
            realLectureIdToUse = existLecture.id
          } else {
            // 新建lecture
            const lectureRes = await fetch(`${baseurl}/lecture/add`, {
              method: 'POST',
              headers: { 'Content-Type': 'application/json' },
              body: JSON.stringify({ title: file.name, description: '由文件上传自动创建', status: 'active' })
            })
            const lectureSaved = await lectureRes.json()
            realLectureIdToUse = lectureSaved.id
          }
        } catch {
          ElMessage.error('lecture表插入失败')
          if (option.onError) option.onError({ message: 'lecture插入失败', name: '', status: 0, method: '', url: '' })
          return
        }
      }
      // 3. 上传文件并生成题目
      try {
        const formData = new FormData()
        formData.append('file', file)
        formData.append('count', String(count || 5))
        formData.append('model', 'v3')
        let response: Response
        const fetchWithTimeout = (url: string, opts: RequestInit, timeout = 600000) => {
          return Promise.race([
            fetch(url, opts),
            new Promise((_, reject) => setTimeout(() => reject(new Error('请求超时，请稍后重试')), timeout))
          ])
        }
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
          // 4. 循环存储题目和选项
          if (Array.isArray(result.data)) {
            for (const q of result.data) {
              if(realLectureIdToUse === null) realLectureIdToUse = 1;
              // 1. 存储题目
              const questionDTO: QuestionDTO = {
                questionText: q.question,
                lectureId: realLectureIdToUse,
                contentInputId: typeof contentInputId === 'number' ? contentInputId : undefined,
                questionType: '单选题'
              }
              const questionRes = await fetch(`${baseurl}/question/add`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(questionDTO)
              })
              const questionSaved = await questionRes.json()
              const questionId = questionSaved.id
              // 2. 存储选项（每个选项单独POST，四个选项同属一个questionId）
              if (Array.isArray(q.options)) {
                for (let i = 0; i < q.options.length; i++) {
                  const optionDTO: QuestionOptionsDTO = {
                    questionId: questionId,
                    optionText: q.options[i],
                    optionOrder: String.fromCharCode(65 + i),
                    isCorrect: (q.answer && q.answer.toUpperCase() === String.fromCharCode(65 + i))
                  }
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
    } finally {
      uploading.value = false
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
    const isLt100M = file.size / 1024 / 1024 < 100

    if (!isValidType) {
      ElMessage.error('只能上传 PDF 格式文件!')
      return false
    }
    if (!isLt100M) {
      ElMessage.error('文件大小不能超过 100MB!')
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

  // 上传成功处理
  const handlePPTSuccess = (response: unknown, file: File) => {
    ElMessage.success('PowerPoint文件上传成功!')
    addProcessingRecord(file, 'PowerPoint', (response as {extractedText: string}).extractedText, '已完成')
    emit('uploadSuccess', { type: 'ppt', file, response })
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

  // PDF自定义上传方法，生成题目并存储到数据库
  const customPDFUploadRequest = async (option: UploadRequestOptions, lectureId?: number, count?: number) => {
    uploading.value = true
    try {
      const file = option.file as File
      const realLectureId = lectureId || 1
      // 1. 先插入 content_input 记录，拿到真实id
      const contentInputId = await insertContentInput(file, realLectureId)
      console.log("contentInputId", contentInputId)
      if (!contentInputId) {
        ElMessage.error('content_input表插入失败')
        if (option.onError) option.onError({ message: 'content_input插入失败', name: '', status: 0, method: '', url: '' })
        return
      }
      try {
        const formData = new FormData()
        formData.append('file', file)
        formData.append('count', String(count || 5))
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
          addProcessingRecord(file, 'PDF文件', '请求超时', '失败')
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
          addProcessingRecord(file, 'PDF文件', preview, '已完成')
          // 循环存储题目和选项
          if (Array.isArray(result.data)) {
            for (const q of result.data) {
              const questionDTO: QuestionDTO = {
                questionText: q.question,
                lectureId: realLectureId,
                contentInputId: typeof contentInputId === 'number' ? contentInputId : undefined,
                questionType: "单选题"
              }
              const questionRes = await fetch(`${baseurl}/question/add`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(questionDTO)
              })
              const questionSaved = await questionRes.json()
              const questionId = questionSaved.id
              if (Array.isArray(q.options)) {
                for (let i = 0; i < q.options.length; i++) {
                  const optionDTO: QuestionOptionsDTO = {
                    questionId: questionId,
                    optionText: q.options[i],
                    optionOrder: String.fromCharCode(65 + i),
                    isCorrect: (q.answer && q.answer.toUpperCase() === String.fromCharCode(65 + i))
                  }
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
          addProcessingRecord(file, 'PDF文件', result.message || '未知错误', '失败')
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
        addProcessingRecord(file, 'PDF文件', '上传失败', '失败')
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
    } finally {
      uploading.value = false
    }
  }

  // 自定义PPT上传方法，生成题目并存储到数据库
const customPPTUploadRequest = async (option: UploadRequestOptions, lectureId?: number, count?: number) => {
  uploading.value = true
  try {
    const file = option.file as File
    const realLectureId = lectureId || 1
    const contentInputId = await insertContentInput(file, realLectureId)
    if (!contentInputId) {
      ElMessage.error('content_input表插入失败')
      if (option.onError) option.onError({ message: 'content_input插入失败', name: '', status: 0, method: '', url: '' })
      return
    }
    
    try {
      const formData = new FormData()
      formData.append('file', file)
      formData.append('count', String(count || 5))
      formData.append('model', 'v3') // 选择模型版本或其他参数

      // 超时控制：10分钟
      const fetchWithTimeout = (url: string, opts: RequestInit, timeout = 600000) => {
        return Promise.race([
          fetch(url, opts),
          new Promise((_, reject) => setTimeout(() => reject(new Error('请求超时，请稍后重试')), timeout))
        ])
      }

      let response: Response
      try {
        // 使用超时控制进行请求
        response = await fetchWithTimeout(`${baseurl}/deepseek/generate-questions`, {
          method: 'POST',
          body: formData
        }) as Response
      } catch {
        ElMessage.error('题目生成请求超时，已判定为失败')
        addProcessingRecord(file, 'PowerPoint', '请求超时', '失败')
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
        addProcessingRecord(file, 'PowerPoint', preview, '已完成')

        // 循环存储题目和选项
        if (Array.isArray(result.data)) {
          for (const q of result.data) {
            const questionDTO: QuestionDTO = {
              questionText: q.question,
              lectureId: realLectureId,
              contentInputId: typeof contentInputId === 'number' ? contentInputId : undefined,
              questionType: "单选题"
            }
            const questionRes = await fetch(`${baseurl}/question/add`, {
              method: 'POST',
              headers: { 'Content-Type': 'application/json' },
              body: JSON.stringify(questionDTO)
            })
            const questionSaved = await questionRes.json()
            const questionId = questionSaved.id

            if (Array.isArray(q.options)) {
              for (let i = 0; i < q.options.length; i++) {
                const optionDTO: QuestionOptionsDTO = {
                  questionId: questionId,
                  optionText: q.options[i],
                  optionOrder: String.fromCharCode(65 + i),
                  isCorrect: (q.answer && q.answer.toUpperCase() === String.fromCharCode(65 + i))
                }
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
        addProcessingRecord(file, 'PowerPoint', result.message || '未知错误', '失败')
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
      addProcessingRecord(file, 'PowerPoint', '上传失败', '失败')
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
  } finally {
    uploading.value = false
  }
}


  // 音频自定义上传方法，生成题目并存储到数据库
  const customAudioUploadRequest = async (option: UploadRequestOptions, lectureId?: number, count?: number) => {
    uploading.value = true
    try {
      const file = option.file as File
      const realLectureId = lectureId || 1
      const contentInputId = await insertContentInput(file, realLectureId)
      if (!contentInputId) {
        ElMessage.error('content_input表插入失败')
        if (option.onError) option.onError({ message: 'content_input插入失败', name: '', status: 0, method: '', url: '' })
        return
      }
      try {
        const formData = new FormData()
        formData.append('audioFile', file)
        formData.append('count', String(count || 5))
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
          response = await fetchWithTimeout(`${baseurl}/deepseek/generate-questions-from-audio`, {
            method: 'POST',
            body: formData
          }) as Response
        } catch {
          ElMessage.error('题目生成请求超时，已判定为失败')
          addProcessingRecord(file, '音频文件', '请求超时', '失败')
          if (option.onError) {
            option.onError({
              name: 'UploadAjaxError',
              status: 408,
              method: 'POST',
              url: `${baseurl}/deepseek/generate-questions-from-audio`,
              message: '请求超时'
            })
          }
          return
        }
        const result = await response.json()
        if ((result.code === 0 || result.code === 200) && result.data) {
          ElMessage.success('题目生成成功！')
          const preview = JSON.stringify(result.data).slice(0, 100) + (JSON.stringify(result.data).length > 100 ? '...' : '')
          addProcessingRecord(file, '音频文件', preview, '已完成')
          // 循环存储题目和选项
          if (Array.isArray(result.data)) {
            for (const q of result.data) {
              const questionDTO: QuestionDTO = {
                questionText: q.question,
                lectureId: realLectureId,
                contentInputId: typeof contentInputId === 'number' ? contentInputId : undefined,
                questionType: "单选题"
              }
              const questionRes = await fetch(`${baseurl}/question/add`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(questionDTO)
              })
              const questionSaved = await questionRes.json()
              const questionId = questionSaved.id
              if (Array.isArray(q.options)) {
                for (let i = 0; i < q.options.length; i++) {
                  const optionDTO: QuestionOptionsDTO = {
                    questionId: questionId,
                    optionText: q.options[i],
                    optionOrder: String.fromCharCode(65 + i),
                    isCorrect: (q.answer && q.answer.toUpperCase() === String.fromCharCode(65 + i))
                  }
                  await fetch(`${baseurl}/question-options/add`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(optionDTO)
                  })
                }
              }
            }
          }
        } else {
          ElMessage.error('题目生成失败: ' + (result.message || '未知错误'))
          addProcessingRecord(file, '音频文件', result.message || '未知错误', '失败')
          if (option.onError) {
            option.onError({
              name: 'UploadAjaxError',
              status: 500,
              method: 'POST',
              url: `${baseurl}/deepseek/generate-questions-from-audio`,
              message: result.message || '题目生成失败'
            })
          }
        }
      } catch {
        ElMessage.error('题目生成请求失败')
        addProcessingRecord(file, '音频文件', '上传失败', '失败')
        if (option.onError) {
          option.onError({
            name: 'UploadAjaxError',
            status: 500,
            method: 'POST',
            url: `${baseurl}/deepseek/generate-questions-from-audio`,
            message: '题目生成请求失败'
          })
        }
      }
    } finally {
      uploading.value = false
    }
  }

  // 获取讲座列表
  const fetchLectureList = async () => {
    lectureListLoading.value = true
    try {
      // const baseurl = inject('baseurl', 'http://localhost:5555') as string
      const res = await fetch(`${baseurl}/lecture/list`)
      const data = await res.json()
      console.log("lectureList", lectureList)
      if (Array.isArray(data) && data.length > 0) {
        lectureList.value = data
      } else {
        lectureList.value = []
      }
    } catch {
      lectureList.value = []
    } finally {
      lectureListLoading.value = false
    }
  }

  const onLectureInput = (val: string) => {
    if (val && !lectureList.value.some(l => l.title === val)) {
      selectedLectureId.value = '__new__'
      newLectureTitle.value = val
    }
  }

  const onLectureChange = (val: any) => {
    if (val === '__new__') {
      // 保持newLectureTitle
    } else {
      newLectureTitle.value = ''
    }
  }

  const createLecture = async () => {
    if (!newLectureTitle.value) return
    // const baseurl = inject('baseurl', 'http://localhost:5555') as string
    const res = await fetch(`${baseurl}/lecture/add`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ title: newLectureTitle.value, description: '用户自助新建', status: 'active' })
    })
    const data = await res.json()
    await fetchLectureList()
    // 自动选中新建的lecture
    if (data && data.id) {
      selectedLectureId.value = data.id
    }
    newLectureTitle.value = ''
  }
  </script>
  
  <style scoped>
  .upload-component {
    padding: 20px;
    background: #f8f9fa;
    border-radius: 8px;
    position: relative;
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
  