<template>
    <div class="speech-quiz-system">
      <!-- 头部导航 -->
      <div class="header">
        <el-breadcrumb separator="/">
          <el-breadcrumb-item @click="goHome" style="cursor:pointer;">首页</el-breadcrumb-item>
          <el-breadcrumb-item>演讲答题系统</el-breadcrumb-item>
        </el-breadcrumb>
      </div>
  
      <!-- 主要内容区 -->
      <div class="main-content">
        <!-- 左侧演讲选择面板 -->
        <div class="left-panel">
          <div class="panel-card">
            <div class="panel-header">
              <h3>演讲管理</h3>
            </div>
            
            <div class="speech-selector">
              <el-select v-model="selectedSpeech" placeholder="选择演讲" @change="onSpeechChange">
                <el-option
                  v-for="speech in speeches"
                  :key="speech.id"
                  :label="speech.title"
                  :value="speech.id">
                </el-option>
              </el-select>
            </div>
  
            <!-- 当前演讲信息 -->
            <div class="speech-info" v-if="currentSpeech">
              <div class="info-item">
                <i class="el-icon-time"></i>
                <span>{{ currentSpeech.date }}</span>
              </div>
              <div class="info-item">
                <i class="el-icon-user"></i>
                <span>{{ currentSpeech.participants }}人参与</span>
              </div>
              <div class="info-item">
                <i class="el-icon-document"></i>
                <span>{{ currentSpeech.quizCount }}个测试</span>
              </div>
            </div>
  
            <!-- 实时统计概览 -->
            <div class="stats-overview">
              <div class="stat-item">
                <div class="stat-number">{{ totalParticipants }}</div>
                <div class="stat-label">总参与人数</div>
              </div>
              <div class="stat-item">
                <div class="stat-number">{{ averageAccuracy }}%</div>
                <div class="stat-label">平均正确率</div>
              </div>
              <div class="stat-item">
                <div class="stat-number">{{ completedQuizzes }}</div>
                <div class="stat-label">已完成测试</div>
              </div>
            </div>
          </div>
        </div>
  
        <!-- 右侧内容区 -->
        <div class="right-content">
          <!-- 角色切换标签 -->
          <el-tabs v-model="activeTab" type="card">
            <!-- 教师视图 -->
            <el-tab-pane label="教师视图" name="teacher">
              <div class="teacher-view">
                <!-- 测试列表 -->
                <div class="quiz-list">
                  <div 
                    v-for="quiz in quizzes" 
                    :key="quiz.id"
                    class="quiz-card"
                    @click="selectQuiz(quiz.id)"
                    :class="{ active: selectedQuiz === quiz.id }">
                    <div class="quiz-header">
                      <h4>{{ quiz.title }}</h4>
                      <el-tag :type="getQuizStatusType(quiz.status)">{{ quiz.status }}</el-tag>
                    </div>
                    <div class="quiz-stats">
                      <div class="stat-row">
                        <span>参与人数: {{ quiz.participants }}/{{ totalParticipants }}</span>
                        <span>正确率: {{ quiz.accuracy }}%</span>
                      </div>
                      <el-progress 
                        :percentage="quiz.participationRate" 
                        :stroke-width="6"
                        :text-inside="true">
                      </el-progress>
                    </div>
                  </div>
                </div>

                <!-- 详细统计 -->
                <div class="detailed-stats" v-if="selectedQuizData">
                  <el-card class="stats-card">
                    <template v-slot:header>
<div >
                      <span>{{ selectedQuizData.title }} - 详细统计</span>
                    </div>
</template>
                    
                    <el-row :gutter="20">
                      <el-col :span="8">
                        <div class="stat-block">
                          <div class="stat-icon answered">
                            <i class="el-icon-check"></i>
                          </div>
                          <div class="stat-content">
                            <div class="stat-value">{{ selectedQuizData.answered }}</div>
                            <div class="stat-desc">已回答</div>
                          </div>
                        </div>
                      </el-col>
                      <el-col :span="8">
                        <div class="stat-block">
                          <div class="stat-icon correct">
                            <i class="el-icon-success"></i>
                          </div>
                          <div class="stat-content">
                            <div class="stat-value">{{ selectedQuizData.correct }}</div>
                            <div class="stat-desc">答对</div>
                          </div>
                        </div>
                      </el-col>
                      <el-col :span="8">
                        <div class="stat-block">
                          <div class="stat-icon unanswered">
                            <i class="el-icon-warning"></i>
                          </div>
                          <div class="stat-content">
                            <div class="stat-value">{{ selectedQuizData.unanswered }}</div>
                            <div class="stat-desc">未回答</div>
                          </div>
                        </div>
                      </el-col>
                    </el-row>

                    <!-- 答案分布图表 -->
                    <div class="answer-distribution">
                      <h4>答案分布</h4>
                      <div class="options-stats">
                        <div 
                          v-for="option in selectedQuizData.options" 
                          :key="option.key"
                          class="option-stat">
                          <div class="option-label">{{ option.key }}. {{ option.text }}</div>
                          <div class="option-progress">
                            <el-progress 
                              :percentage="option.percentage" 
                              :stroke-width="20"
                              :text-inside="true"
                              :color="option.isCorrect ? '#67C23A' : '#E6A23C'">
                            </el-progress>
                          </div>
                        </div>
                      </div>
                    </div>
                  </el-card>
                </div>
              </div>
            </el-tab-pane>

            <!-- 学生视图 -->
          </el-tabs>
        </div>
      </div>
    </div>
  </template>
  
  <script lang="ts">
  import { useRouter } from 'vue-router'
  import { inject } from 'vue';
  import axios from 'axios';

  interface LectureParticipant {
    id: number;
    lectureId: number;
    userType?: string;
    // 其他字段可根据实际DTO补充
  }

  interface Speech {
    id: number;
    title: string;
    date: string;
    participants: number;
    quizCount?: number;
  }

  interface Quiz {
    id: number;
    title: string;
    status: string;
    participants: number;
    accuracy: number;
    participationRate: number;
  }

  export default {
    name: 'SpeechQuizSystem',
    data() {
      return {
        activeTab: 'teacher',
        selectedSpeech: 0,
        selectedQuiz: null as number | null,
        speeches: [] as Speech[],
        currentSpeech: null as Speech | null,
        totalParticipants: 0,
        averageAccuracy: 87,
        completedQuizzes: 3,
        quizzes: [] as Quiz[],
        selectedQuizData: null as null | {
          id: number;
          title: string;
          answered: number;
          correct: number;
          unanswered: number;
          options: { key: string; text: string; percentage: number; isCorrect: boolean; }[];
        },
        studentInfo: {
          name: '张三',
          studentId: '202100001',
          avatar: '',
          accuracy: 85,
          rank: 12,
          completed: 2,
          id: 1
        },
        studentAnswers: [] as { quizTitle: string; myAnswer: string; correctAnswer: string; isCorrect: boolean; answerTime: string; }[],
        rankings: [] as { id: number; name: string; accuracy: number; }[]
      }
    },
    
    setup() {
      const router = useRouter();
      const goHome = () => {
        router.push('/student-homepage');
      };
      return { goHome };
    },
    
    methods: {
      async fetchParticipantsAndSpeeches() {
        const baseurl = inject('baseurl') as string;
        // 获取所有演讲（假设/lecture/list）和所有参与者
        const [speechesRes, participantsRes] = await Promise.all([
          axios.get(baseurl + '/lecture/list'),
          axios.get(baseurl + '/lecture-participants/list')
        ]);
        const speeches = speechesRes.data || [];
        const participantsArr: LectureParticipant[] = Array.isArray(participantsRes.data) ? participantsRes.data : [];
        // 过滤掉老师
        const filteredParticipants = participantsArr.filter((p: LectureParticipant) => p.userType !== 'teacher');
        // 统计每个演讲的参与人数
        const speechMap: Record<number, number> = {};
        filteredParticipants.forEach((p: LectureParticipant) => {
          if (p.lectureId) {
            speechMap[p.lectureId] = (speechMap[p.lectureId] || 0) + 1;
          }
        });
        // 填充speeches的participants字段
        speeches.forEach((speech: any) => {
          speech.participants = speechMap[speech.id] || 0;
        });
        this.speeches = speeches;
        this.totalParticipants = filteredParticipants.length;
        // 默认选择第一个演讲
        if (this.speeches.length > 0) {
          this.selectedSpeech = this.speeches[0].id;
          this.onSpeechChange(this.selectedSpeech);
        }
      },
      onSpeechChange(speechId: number) {
        this.currentSpeech = this.speeches.find((s: Speech) => s.id === speechId) || null;
        // TODO: 可根据speechId加载对应quiz数据
        this.loadSpeechData(speechId);
      },
      selectQuiz(quizId: number) {
        this.selectedQuiz = quizId;
        // 保持原有模拟数据
        this.selectedQuizData = {
          id: quizId,
          title: '第一题：什么是机器学习？',
          answered: 42,
          correct: 37,
          unanswered: 3,
          options: [
            { key: 'A', text: '一种让计算机从数据中学习的方法', percentage: 88, isCorrect: true },
            { key: 'B', text: '一种编程语言', percentage: 5, isCorrect: false },
            { key: 'C', text: '一种硬件设备', percentage: 2, isCorrect: false },
            { key: 'D', text: '一种网络协议', percentage: 5, isCorrect: false }
          ]
        };
      },
      getQuizStatusType(status: string) {
        const statusMap: Record<string, string> = {
          '已结束': 'success',
          '进行中': 'warning',
          '未开始': 'info'
        };
        return statusMap[status] || 'info';
      },
      loadSpeechData(speechId: number) {
        // 保持原有模拟数据
        // 可扩展为后端获取quiz数据
      }
    },
    
    mounted() {
      this.fetchParticipantsAndSpeeches();
    }
  }
  </script>
  
  <style scoped>
  .speech-quiz-system {
    min-height: 100vh;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    padding: 20px;
  }
  
  .header {
    margin-bottom: 20px;
  }
  
  .header .el-breadcrumb {
    color: white;
  }
  
  .main-content {
    display: flex;
    gap: 20px;
    height: calc(100vh - 80px);
  }
  
  .left-panel {
    width: 350px;
    flex-shrink: 0;
  }
  
  .panel-card {
    background: white;
    border-radius: 16px;
    padding: 24px;
    box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
    height: 100%;
  }
  
  .panel-header h3 {
    color: #2c3e50;
    margin-bottom: 20px;
    font-size: 18px;
  }
  
  .speech-selector {
    margin-bottom: 20px;
  }
  
  .speech-selector .el-select {
    width: 100%;
  }
  
  .speech-info {
    margin-bottom: 20px;
    padding: 15px;
    background: #f8f9fa;
    border-radius: 8px;
  }
  
  .info-item {
    display: flex;
    align-items: center;
    margin-bottom: 8px;
    color: #666;
  }
  
  .info-item i {
    margin-right: 8px;
    color: #667eea;
  }
  
  .stats-overview {
    display: flex;
    justify-content: space-between;
    margin-top: 20px;
  }
  
  .stat-item {
    text-align: center;
    flex: 1;
  }
  
  .stat-number {
    font-size: 24px;
    font-weight: bold;
    color: #667eea;
    margin-bottom: 5px;
  }
  
  .stat-label {
    font-size: 12px;
    color: #666;
  }
  
  .right-content {
    flex: 1;
    background: white;
    border-radius: 16px;
    padding: 24px;
    box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
    overflow-y: auto;
  }
  
  /* 教师视图样式 */
  .teacher-view {
    height: 100%;
  }
  
  .quiz-list {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
    gap: 16px;
    margin-bottom: 20px;
  }
  
  .quiz-card {
    border: 2px solid #e1e8ed;
    border-radius: 12px;
    padding: 16px;
    cursor: pointer;
    transition: all 0.3s;
  }
  
  .quiz-card:hover {
    border-color: #667eea;
    box-shadow: 0 4px 12px rgba(102, 126, 234, 0.15);
  }
  
  .quiz-card.active {
    border-color: #667eea;
    background: #f8f9ff;
  }
  
  .quiz-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 12px;
  }
  
  .quiz-header h4 {
    margin: 0;
    font-size: 14px;
    color: #2c3e50;
  }
  
  .quiz-stats {
    font-size: 12px;
    color: #666;
  }
  
  .stat-row {
    display: flex;
    justify-content: space-between;
    margin-bottom: 8px;
  }
  
  .detailed-stats {
    margin-top: 20px;
  }
  
  .stat-block {
    display: flex;
    align-items: center;
    padding: 20px;
    background: #f8f9fa;
    border-radius: 8px;
    margin-bottom: 16px;
  }
  
  .stat-icon {
    width: 50px;
    height: 50px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-right: 16px;
    font-size: 20px;
    color: white;
  }
  
  .stat-icon.answered {
    background: #409eff;
  }
  
  .stat-icon.correct {
    background: #67c23a;
  }
  
  .stat-icon.unanswered {
    background: #e6a23c;
  }
  
  .stat-content {
    flex: 1;
  }
  
  .stat-value {
    font-size: 24px;
    font-weight: bold;
    color: #2c3e50;
    margin-bottom: 4px;
  }
  
  .stat-desc {
    color: #666;
    font-size: 14px;
  }
  
  .answer-distribution {
    margin-top: 20px;
  }
  
  .answer-distribution h4 {
    margin-bottom: 16px;
    color: #2c3e50;
  }
  
  .option-stat {
    margin-bottom: 12px;
  }
  
  .option-label {
    margin-bottom: 4px;
    font-size: 14px;
    color: #2c3e50;
  }
  
  /* 学生视图样式 */
  .student-view {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 20px;
    height: 100%;
  }
  
  .student-card {
    margin-bottom: 20px;
  }
  
  .student-header {
    display: flex;
    align-items: center;
    margin-bottom: 20px;
  }
  
  .student-info {
    margin-left: 16px;
  }
  
  .student-info h3 {
    margin: 0 0 4px 0;
    color: #2c3e50;
  }
  
  .student-info p {
    margin: 0;
    color: #666;
  }
  
  .student-stats {
    display: flex;
    justify-content: space-around;
    text-align: center;
  }
  
  .answer-record {
    margin-bottom: 20px;
  }
  
  .ranking-card {
    height: fit-content;
  }
  
  .ranking-list {
    max-height: 300px;
    overflow-y: auto;
  }
  
  .ranking-item {
    display: flex;
    align-items: center;
    padding: 12px;
    margin-bottom: 8px;
    background: #f8f9fa;
    border-radius: 8px;
    transition: background 0.3s;
  }
  
  .ranking-item.my-rank {
    background: #e6f7ff;
    border: 1px solid #40a9ff;
  }
  
  .rank-number {
    width: 30px;
    height: 30px;
    background: #667eea;
    color: white;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-right: 12px;
    font-weight: bold;
  }
  
  .rank-info {
    flex: 1;
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  
  .rank-name {
    font-weight: 500;
    color: #2c3e50;
  }
  
  .rank-score {
    color: #667eea;
    font-weight: bold;
  }
  
  /* 响应式设计 */
  @media (max-width: 768px) {
    .main-content {
      flex-direction: column;
      height: auto;
    }
    
    .left-panel {
      width: 100%;
      margin-bottom: 20px;
    }
    
    .student-view {
      grid-template-columns: 1fr;
    }
    
    .quiz-list {
      grid-template-columns: 1fr;
    }
  }

  .scrollable-ranking-list {
    max-height: 320px;
    overflow-y: auto;
    padding-right: 4px;
  }

  .ranking-item {
    display: flex;
    align-items: center;
    padding: 10px 16px;
    border-bottom: 1px solid #f0f0f0;
    background: #fff;
    transition: background 0.2s;
  }
  .ranking-item:last-child {
    border-bottom: none;
  }
  .ranking-item.my-rank {
    background: #f0f7ff;
    font-weight: bold;
  }
  .rank-number {
    width: 36px;
    text-align: center;
    font-size: 16px;
    color: #667eea;
    font-weight: 600;
  }
  .rank-info {
    flex: 1;
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  .rank-name {
    font-size: 15px;
    color: #333;
  }
  .rank-score {
    font-size: 15px;
    color: #52c41a;
    font-weight: 500;
  }
  </style>
  