<template>
  <div>
    <div class="decor-left"></div>
    <div class="decor-right"></div>
    <div class="wrong-review">
      <div class="header-row">
        <h2>错题回顾</h2>
        <div class="filter-bar">
          <button
            :class="{ active: filterType === 'today' }"
            @click="filterType = 'today'"
          >
            <span class="filter-title">今日错题</span>
            <span class="filter-count">{{ todayCount }}</span>
          </button>
          <button
            :class="{ active: filterType === 'all' }"
            @click="filterType = 'all'"
          >
            <span class="filter-title">错题总数</span>
            <span class="filter-count">{{ allCount }}</span>
          </button>
        </div>
      </div>
      <div v-if="filteredList.length === 0" class="empty">
        <img src="https://img.icons8.com/ios/100/cccccc/open-book--v2.png" width="60" style="margin-bottom:12px;" />
        <div>暂无错题，继续加油！</div>
      </div>
      <transition-group name="fade" tag="div">
        <div v-for="item in filteredList" :key="item.id" class="wrong-card">
          <div class="card-header">
            <span class="subject">{{ item.subject }}</span>
            <span class="date">{{ item.time }}</span>
          </div>
          <div class="question">{{ item.question }}</div>
          <div class="options">
            <div
              v-for="(opt, idx) in item.options"
              :key="idx"
              :class="[
                'option',
                item.myAnswer === opt.key ? (item.myAnswer === item.correctAnswer ? 'right' : 'wrong') : '',
                item.correctAnswer === opt.key && item.myAnswer !== opt.key ? 'right' : ''
              ]"
            >
              <span class="opt-key">{{ opt.key }}.</span>
              <span>{{ opt.text }}</span>
              <span v-if="item.myAnswer === opt.key" class="tag">我的选择</span>
              <span v-if="item.correctAnswer === opt.key" class="tag right-tag">正确答案</span>
            </div>
          </div>
          <div class="explain" v-if="item.explanation">
            <span>解析：</span>{{ item.explanation }}
          </div>
        </div>
      </transition-group>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'

function getTodayStr() {
  const d = new Date()
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
}

const filterType = ref<'today' | 'all'>('today')

const wrongList = ref([
  {
    id: 1,
    question: '下列哪种数据结构最适合实现队列？',
    options: [
      { key: 'A', text: '链表' },
      { key: 'B', text: '栈' },
      { key: 'C', text: '哈希表' },
      { key: 'D', text: '树' }
    ],
    myAnswer: 'B',
    correctAnswer: 'A',
    explanation: '链表可以高效地实现队列的入队和出队操作。',
    subject: '数据结构',
    time: getTodayStr()
  },
  {
    id: 2,
    question: '操作系统中，用于实现进程间通信的机制是？',
    options: [
      { key: 'A', text: '中断' },
      { key: 'B', text: '信号量' },
      { key: 'C', text: '虚拟内存' },
      { key: 'D', text: '死锁' }
    ],
    myAnswer: 'A',
    correctAnswer: 'B',
    explanation: '信号量是一种常用的进程间通信与同步机制。',
    subject: '操作系统',
    time: '2024-06-02'
  },
  {
    id: 3,
    question: '冯·诺依曼计算机体系结构中，不包括以下哪一项？',
    options: [
      { key: 'A', text: '运算器' },
      { key: 'B', text: '控制器' },
      { key: 'C', text: '输入设备' },
      { key: 'D', text: '图形处理器' }
    ],
    myAnswer: 'D',
    correctAnswer: 'D',
    explanation: '冯·诺依曼体系结构包括运算器、控制器、存储器、输入设备和输出设备，不包括GPU。',
    subject: '计算机组成原理',
    time: '2024-06-01'
  },
  {
    id: 4,
    question: '下列哪种协议工作在OSI模型的传输层？',
    options: [
      { key: 'A', text: 'IP' },
      { key: 'B', text: 'TCP' },
      { key: 'C', text: 'ARP' },
      { key: 'D', text: 'ICMP' }
    ],
    myAnswer: 'A',
    correctAnswer: 'B',
    explanation: 'TCP协议属于传输层，IP属于网络层。',
    subject: '计算机网络',
    time: '2024-06-03'
  }
])

const todayStr = getTodayStr()
const filteredList = computed(() => {
  if (filterType.value === 'today') {
    return wrongList.value.filter(item => item.time === todayStr)
  } else {
    return wrongList.value
  }
})
const todayCount = computed(() => wrongList.value.filter(item => item.time === todayStr).length)
const allCount = computed(() => wrongList.value.length)
</script>

<style scoped>
.wrong-review {
  padding: 32px 0 0 0;
  max-width: 700px;
  margin: 0 auto;
}
.header-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 18px;
}
h2 {
  margin: 0 0 0 18px;
  font-size: 2em;
  color: #5a4fcf;
  letter-spacing: 2px;
}
.filter-bar {
  display: flex;
  gap: 18px;
}
.filter-bar button {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 22px;
  border: none;
  border-radius: 24px;
  background: #f3f3fa;
  color: #5a4fcf;
  font-size: 15px;
  font-weight: 500;
  cursor: pointer;
  box-shadow: 0 2px 8px #e0e0e0;
  transition: background 0.2s, color 0.2s, box-shadow 0.2s;
  position: relative;
}
.filter-bar button.active {
  background: linear-gradient(90deg, #6c63ff 0%, #a084ee 100%);
  color: #fff;
  box-shadow: 0 4px 16px #d1cfff;
}
.filter-title {
  font-size: 15px;
}
.filter-count {
  background: #fff;
  color: #6c63ff;
  border-radius: 12px;
  padding: 2px 10px;
  font-size: 13px;
  font-weight: bold;
  margin-left: 2px;
}
.filter-bar button.active .filter-count {
  background: #fff;
  color: #6c63ff;
}
.empty {
  color: #aaa;
  text-align: center;
  margin-top: 60px;
  font-size: 1.1em;
  letter-spacing: 1px;
}
.fade-enter-active, .fade-leave-active {
  transition: all 0.3s;
}
.fade-enter-from, .fade-leave-to {
  opacity: 0;
  transform: translateY(20px);
}
.wrong-card {
  background: #fff;
  border-radius: 14px;
  box-shadow: 0 4px 18px #e0e0e0;
  padding: 24px 28px 18px 28px;
  margin-bottom: 28px;
  transition: box-shadow 0.2s;
  border-left: 6px solid #6c63ff22;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}
.subject {
  color: #6c63ff;
  font-weight: bold;
  font-size: 15px;
}
.date {
  color: #bbb;
  font-size: 13px;
}
.question {
  font-weight: bold;
  margin-bottom: 12px;
  font-size: 1.1em;
  color: #333;
}
.options {
  margin-bottom: 10px;
}
.option {
  padding: 7px 14px;
  border-radius: 7px;
  margin-bottom: 5px;
  display: flex;
  align-items: center;
  background: #f6f8fa;
  position: relative;
  font-size: 15px;
  transition: background 0.2s, color 0.2s;
}
.option.wrong {
  background: #ffeaea;
  color: #e74c3c;
  font-weight: bold;
}
.option.right {
  background: #eafaf1;
  color: #27ae60;
  font-weight: bold;
}
.opt-key {
  width: 24px;
  display: inline-block;
}
.tag {
  font-size: 12px;
  background: #f5c6cb;
  color: #a94442;
  border-radius: 4px;
  padding: 2px 6px;
  margin-left: 10px;
}
.right-tag {
  background: #b7ebc6;
  color: #237804;
}
.explain {
  background: #f6f8fa;
  padding: 8px 12px;
  border-radius: 6px;
  margin-bottom: 8px;
  color: #555;
  font-size: 14px;
  margin-top: 8px;
  border-left: 3px solid #6c63ff44;
}
.meta {
  font-size: 12px;
  color: #888;
  display: flex;
  justify-content: space-between;
}
.decor-left {
  position: fixed;
  top: 80px;
  left: 0;
  width: 180px;
  height: 320px;
  background: radial-gradient(circle at 60% 40%, #a084ee33 60%, transparent 100%);
  z-index: 0;
  pointer-events: none;
  border-radius: 50% 40% 60% 70%;
  filter: blur(2px);
}

.decor-right {
  position: fixed;
  bottom: 40px;
  right: 0;
  width: 160px;
  height: 260px;
  background: radial-gradient(circle at 40% 60%, #6c63ff22 60%, transparent 100%);
  z-index: 0;
  pointer-events: none;
  border-radius: 60% 80% 50% 70%;
  filter: blur(2px);
}
</style>