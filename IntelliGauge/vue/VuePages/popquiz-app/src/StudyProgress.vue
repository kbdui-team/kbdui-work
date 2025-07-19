<template>
  <div>
    <svg class="decor-progress-left" width="120" height="340" viewBox="0 0 120 340" fill="none" xmlns="http://www.w3.org/2000/svg">
      <polyline points="10,20 60,60 20,120 80,200 40,300" stroke="#6cbbff55" stroke-width="4" fill="none"/>
      <polyline points="30,60 90,100 50,180 100,260 60,320" stroke="#6c63ff33" stroke-width="2" fill="none"/>
      <circle cx="60" cy="60" r="8" fill="#6cbbff33"/>
      <circle cx="80" cy="200" r="6" fill="#6c63ff22"/>
    </svg>
    <div class="decor-progress-right">
      <div v-for="i in 18" :key="i" :style="dotStyle(i)" class="dot"></div>
    </div>
    <div class="study-progress">
      <h2>学习进度</h2>
      <div class="stats">
        <div class="stat-card">
          <div class="stat-value">{{ stats.finished }}</div>
          <div class="stat-label">已完成题数</div>
        </div>
        <div class="stat-card">
          <div class="stat-value">{{ stats.accuracy }}%</div>
          <div class="stat-label">正确率</div>
        </div>
        <div class="stat-card">
          <div class="stat-value">{{ stats.days }}</div>
          <div class="stat-label">连续学习天数</div>
        </div>
      </div>
      <div class="chart-area">
        <h3>最近一周答题趋势</h3>
        <div class="chart-bar">
          <div v-for="(item, idx) in weekData" :key="idx" class="bar-item">
            <div class="bar-container">
              <div class="bar-label-top">{{ item.count }}</div>
              <div
                class="bar"
                :style="{height: barHeight(item.count) + 'px'}"
              ></div>
            </div>
            <div
              class="bar-week"
              :class="{ today: idx === todayIndex }"
            >
              {{ item.day }}
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'

const stats = ref({
  finished: 120,
  accuracy: 88,
  days: 7
})

const jsDayToZh = ['日', '一', '二', '三', '四', '五', '六']
const today = new Date()
const todayIndex = today.getDay() // 0-6

const weekData = ref([
  { day: '一', count: 10 },
  { day: '二', count: 15 },
  { day: '三', count: 20 },
  { day: '四', count: 18 },
  { day: '五', count: 22 },
  { day: '六', count: 25 },
  { day: '日', count: 10 }
])

const maxBarHeight = 120
const maxCount = computed(() => Math.max(...weekData.value.map(i => i.count), 1))
const barHeight = (count: number) => {
  return Math.round((count / maxCount.value) * maxBarHeight)
}

// 右侧点云装饰的样式生成
function dotStyle(i: number) {
  // 让点分布在右下角，随机但有规律
  const top = 60 + (i % 6) * 28 + Math.floor(i / 6) * 10
  const right = 18 + (i % 3) * 22 + Math.floor(i / 6) * 8
  const size = 8 + (i % 3) * 4
  const opacity = 0.18 + (i % 4) * 0.07
  return {
    top: `${top}px`,
    right: `${right}px`,
    width: `${size}px`,
    height: `${size}px`,
    opacity,
    background: 'radial-gradient(circle, #a084ee 60%, #fff0 100%)',
  }
}
</script>

<style scoped>
.study-progress {
  padding: 32px;
  max-width: 700px;
  margin: 0 auto;
  position: relative;
  z-index: 1;
}
h2 {
  margin-bottom: 24px;
  color: #5a4fcf;
  letter-spacing: 2px;
}
.stats {
  display: flex;
  gap: 24px;
  margin-bottom: 32px;
}
.stat-card {
  background: #fff;
  border-radius: 10px;
  box-shadow: 0 2px 8px #e0e0e0;
  padding: 24px 32px;
  text-align: center;
  flex: 1;
}
.stat-value {
  font-size: 2.2em;
  font-weight: bold;
  color: #6c63ff;
}
.stat-label {
  color: #888;
  margin-top: 8px;
}
.chart-area {
  margin-top: 32px;
}
.chart-bar {
  display: flex;
  align-items: flex-end;
  height: 160px;
  gap: 18px;
  margin-top: 16px;
  padding-bottom: 8px;
}
.bar-item {
  text-align: center;
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
}
.bar-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  height: 120px;
  justify-content: flex-end;
}
.bar-label-top {
  font-size: 15px;
  color: #6c63ff;
  font-weight: bold;
  margin-bottom: 4px;
  height: 20px;
}
.bar {
  width: 28px;
  background: linear-gradient(180deg, #6c63ff 0%, #a084ee 100%);
  border-radius: 6px 6px 0 0;
  transition: height 0.3s;
  margin-bottom: 0;
}
.bar-week {
  font-size: 15px;
  color: #888;
  margin-top: 8px;
  font-weight: 500;
  letter-spacing: 1px;
  padding: 2px 8px;
  border-radius: 8px;
  transition: background 0.2s, color 0.2s;
}
.bar-week.today {
  background: linear-gradient(90deg, #6c63ff 0%, #a084ee 100%);
  color: #fff;
  font-weight: bold;
}

/* 左侧科技线条装饰 */
.decor-progress-left {
  position: fixed;
  top: 90px;
  left: 0;
  z-index: 0;
  pointer-events: none;
}
/* 右侧点云装饰 */
.decor-progress-right {
  position: fixed;
  bottom: 40px;
  right: 0;
  width: 120px;
  height: 320px;
  z-index: 0;
  pointer-events: none;
}
.decor-progress-right .dot {
  position: absolute;
  border-radius: 50%;
  background: radial-gradient(circle, #a084ee 60%, #fff0 100%);
}
</style>