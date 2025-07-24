<template>
  <div class="ranking-bg">
    <div class="ranking-container">
      <h2 class="ranking-title">讲座排行榜</h2>
      <div class="lecture-tabs">
        <button
          v-for="lecture in lectures"
          :key="lecture.id"
          :class="['lecture-tab', {active: lecture.id === currentLectureId}]"
          @click="selectLecture(lecture.id)"
        >
          {{ lecture.title || ('讲座' + lecture.id) }}
        </button>
      </div>
      <div v-if="loading" class="loading">加载中...</div>
      <div v-else-if="rankingList.length === 0" class="empty">暂无数据</div>
      <div v-else class="ranking-list">
        <div
          v-for="(item, idx) in sortedRanking"
          :key="item.userId"
          class="ranking-item"
          :class="badgeClass(idx)"
        >
          <div class="rank-badge">
            <span v-if="idx === 0">🥇</span>
            <span v-else-if="idx === 1">🥈</span>
            <span v-else-if="idx === 2">🥉</span>
            <span v-else>#{{ idx + 1 }}</span>
          </div>
          <img :src="item.avatar" class="avatar" />
          <div class="info">
            <div class="name">{{ item.realName || item.userName || '学生' + item.userId }}</div>
            <div class="meta">
              <span class="meta-answered">答题：{{ item.answeredCount }}</span>
              <span class="meta-correct">答对：{{ item.correctCount }}</span>
              <span class="meta-accuracy">正确率：{{ item.accuracy }}%</span>
            </div>
          </div>
          <div class="medal" v-if="idx < 3">TOP{{ idx + 1 }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import request from '@/utils/request'

const lectures = ref<any[]>([])
const currentLectureId = ref<number | null>(null)
const rankingList = ref<any[]>([])
const loading = ref(false)

// 用户信息缓存，避免重复请求
const userMap = ref<Record<number, any>>({})

// 获取所有讲座
async function fetchLectures() {
  const res = await request.get('/lecture/list')
  lectures.value = Array.isArray(res.data) ? res.data : []
  if (lectures.value.length > 0) {
    currentLectureId.value = lectures.value[0].id
    fetchRanking()
  }
}

// 切换讲座
function selectLecture(id: number) {
  if (currentLectureId.value !== id) {
    currentLectureId.value = id
    fetchRanking()
  }
}

// 获取所有用户信息
async function fetchAllUsers() {
  const res = await request.get('/user/list')
  if (Array.isArray(res.data)) {
    res.data.forEach((u: any) => {
      userMap.value[u.id] = u
    })
  }
}

// 获取当前讲座下所有学生的排行榜
async function fetchRanking() {
  if (!currentLectureId.value) return
  loading.value = true
  rankingList.value = []
  // 1. 获取所有参与者
  const partRes = await request.get('/lecture-participants/list')
  const participants = Array.isArray(partRes.data) ? partRes.data : []
  // 2. 过滤出当前讲座的学生
  const lectureStudents = participants.filter((p: any) => p.lectureId === currentLectureId.value && (!p.role || p.role === 'student'))
  // 3. 获取所有用户信息（只请求一次）
  if (Object.keys(userMap.value).length === 0) {
    await fetchAllUsers()
  }
  // 4. 并发请求每个学生的答题统计
  const statPromises = lectureStudents.map(async (stu: any) => {
    const statRes = await request.get(`/answerHistory/student/${stu.userId}/lecture/${currentLectureId.value}/stat`)
    const stat = statRes.data || {}
    const user = userMap.value[stu.userId] || {}
    return {
      userId: stu.userId,
      userName: user.userName,
      realName: user.realName,
      avatar: user.avatar || `https://api.dicebear.com/7.x/identicon/svg?seed=${user.userName || user.realName || stu.userId}`,
      answeredCount: stat.answeredCount || 0,
      correctCount: stat.correctCount || 0,
      accuracy: stat.answeredCount ? Math.round((stat.correctCount / stat.answeredCount) * 100) : 0
    }
  })
  rankingList.value = await Promise.all(statPromises)
  loading.value = false
}

const sortedRanking = computed(() => {
  return rankingList.value.slice().sort((a, b) => {
    if (b.correctCount !== a.correctCount) return b.correctCount - a.correctCount
    if (b.answeredCount !== a.answeredCount) return b.answeredCount - a.answeredCount
    return b.accuracy - a.accuracy
  })
})

function badgeClass(idx: number) {
  if (idx === 0) return 'first'
  if (idx === 1) return 'second'
  if (idx === 2) return 'third'
  return ''
}

onMounted(() => {
  fetchLectures()
})
</script>

<style scoped>
.ranking-bg {
  min-height: 100vh;
  background: linear-gradient(135deg, #a084ee 0%, #6c63ff 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0;
}
.ranking-container {
  background: #fff;
  border-radius: 24px;
  box-shadow: 0 8px 32px #6c63ff22;
  padding: 48px 36px 36px 36px;
  width: 100%;
  max-width: 600px;
  min-width: 320px;
  min-height: 80vh;
  margin: 32px 0;
  position: relative;
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
}
.ranking-title {
  text-align: center;
  font-size: 2.2em;
  font-weight: bold;
  color: #6c63ff;
  margin-bottom: 8px;
  letter-spacing: 2px;
}
.lecture-tabs {
  display: flex;
  gap: 12px;
  justify-content: center;
  margin-bottom: 24px;
}
.lecture-tab {
  background: #f6f8fa;
  border: none;
  border-radius: 8px 8px 0 0;
  padding: 8px 22px;
  font-size: 1.1em;
  color: #6c63ff;
  cursor: pointer;
  font-weight: 500;
  transition: background 0.2s, color 0.2s;
}
.lecture-tab.active {
  background: linear-gradient(90deg, #6c63ff 0%, #a084ee 100%);
  color: #fff;
  font-weight: bold;
}
.loading {
  text-align: center;
  color: #888;
  font-size: 1.2em;
  margin: 32px 0;
}
.empty {
  text-align: center;
  color: #aaa;
  font-size: 1.1em;
  margin: 32px 0;
}
.ranking-list {
  display: flex;
  flex-direction: column;
  gap: 18px;
  max-height: 420px;
  overflow-y: auto;
  padding-right: 4px;
}
.ranking-item {
  display: flex;
  align-items: center;
  background: #f6f8fa;
  border-radius: 14px;
  box-shadow: 0 2px 8px #a084ee22;
  padding: 16px 18px;
  position: relative;
  /* 去除hover放大动画 */
  /* transition: transform 0.2s; */
}
/* .ranking-item:hover {
  transform: scale(1.025);
  box-shadow: 0 6px 24px #a084ee33;
} */
.rank-badge {
  font-size: 1.5em;
  width: 48px;
  text-align: center;
  font-weight: bold;
  color: #bbb;
}
.avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  margin: 0 18px;
  border: 3px solid #fff;
  box-shadow: 0 2px 8px #a084ee22;
}
.info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.name {
  font-size: 1.1em;
  font-weight: 600;
  color: #333;
}
.meta {
  display: flex;
  gap: 12px;
  margin-top: 2px;
  font-size: 0.98em;
}
.meta-answered { color: #1976d2; }
.meta-correct { color: #8e24aa; }
.meta-accuracy { color: #388e3c; }
.score {
  color: #6c63ff;
  font-weight: bold;
  font-size: 1em;
}
.medal {
  background: linear-gradient(90deg, #ffe082 0%, #ffd54f 100%);
  color: #b8860b;
  font-size: 0.95em;
  font-weight: bold;
  border-radius: 8px;
  padding: 2px 10px;
  margin-left: 12px;
  box-shadow: 0 2px 8px #ffd54f44;
}
.ranking-item.first {
  background: linear-gradient(90deg, #fffbe6 60%, #ffe082 100%);
  border: 2px solid #ffd54f;
}
.ranking-item.second {
  background: linear-gradient(90deg, #f0f4ff 60%, #b0c4de 100%);
  border: 2px solid #b0c4de;
}
.ranking-item.third {
  background: linear-gradient(90deg, #fbeee6 60%, #ffb74d 100%);
  border: 2px solid #ffb74d;
}
</style>

export default {}