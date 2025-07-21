<template>
  <div class="ranking-bg">
    <div class="ranking-container">
      <h2 class="ranking-title">🏆 班级排行榜</h2>
      <div class="ranking-desc">综合答对题数、答题数量和正确率，看看谁是本周学习之星！</div>
      <div class="ranking-list">
        <div
          v-for="(item, idx) in sortedRanking"
          :key="item.id"
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
            <div class="name">{{ item.name }}</div>
            <div class="meta">
              <span class="meta-answered">答题：{{ item.totalAnswered }}</span>
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
const rankingList = [
  {
    id: 1,
    name: '张三',
    avatar: 'https://randomuser.me/api/portraits/men/32.jpg',
    totalAnswered: 120,
    correctCount: 100,
    accuracy: 83.3
  },
  {
    id: 2,
    name: '李四',
    avatar: 'https://randomuser.me/api/portraits/women/44.jpg',
    totalAnswered: 110,
    correctCount: 95,
    accuracy: 86.4
  },
  {
    id: 3,
    name: '王五',
    avatar: 'https://randomuser.me/api/portraits/men/45.jpg',
    totalAnswered: 130,
    correctCount: 93,
    accuracy: 71.5
  },
  {
    id: 4,
    name: '赵六',
    avatar: 'https://randomuser.me/api/portraits/men/46.jpg',
    totalAnswered: 90,
    correctCount: 90,
    accuracy: 100
  },
  {
    id: 5,
    name: '小明',
    avatar: 'https://randomuser.me/api/portraits/women/47.jpg',
    totalAnswered: 88,
    correctCount: 80,
    accuracy: 90.9
  }
  // ...可继续添加更多
]

// 排序规则：答对题数 > 答题数量 > 正确率
const sortedRanking = rankingList.slice().sort((a, b) => {
  if (b.correctCount !== a.correctCount) {
    return b.correctCount - a.correctCount
  }
  if (b.totalAnswered !== a.totalAnswered) {
    return b.totalAnswered - a.totalAnswered
  }
  return b.accuracy - a.accuracy
})

function badgeClass(idx: number) {
  if (idx === 0) return 'first'
  if (idx === 1) return 'second'
  if (idx === 2) return 'third'
  return ''
}
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
  max-width: 440px;
  width: 100%;
  margin: 48px 0;
  position: relative;
}
.ranking-title {
  text-align: center;
  font-size: 2.2em;
  font-weight: bold;
  color: #6c63ff;
  margin-bottom: 8px;
  letter-spacing: 2px;
}
.ranking-desc {
  text-align: center;
  color: #888;
  margin-bottom: 32px;
  font-size: 1.1em;
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
  transition: transform 0.2s;
}
.ranking-item:hover {
  transform: scale(1.025);
  box-shadow: 0 6px 24px #a084ee33;
}
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