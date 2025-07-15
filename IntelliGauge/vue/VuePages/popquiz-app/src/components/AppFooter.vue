<template>
  <el-footer class="app-footer">
    <div class="main-footer-content">
      <div class="footer-left">
        <p>&copy; 2024 PopQuiz 咕咕嘎嘎在线学习平台. 保留所有权利.</p>
        <p>当前版本: v2.1.0 | 最后更新: {{ lastUpdateTime }}</p>
      </div>
      <div class="footer-right">
        <div class="footer-links">
          <el-link href="#" :underline="false" class="footer-link">使用帮助</el-link>
          <el-divider direction="vertical" />
          <el-link href="#" :underline="false" class="footer-link">联系我们</el-link>
          <el-divider direction="vertical" />
          <el-link href="#" :underline="false" class="footer-link">隐私政策</el-link>
        </div>
      </div>
    </div>
  </el-footer>
</template>

<script setup lang="ts">
import { defineProps } from 'vue'
const props = defineProps<{ lastUpdateTime?: string }>()
</script>

<style scoped>
/* 复制你原有的页脚样式 */
.app-footer {
  background: #fafbfc;
  border-top: 1px solid #e8eaec;
  padding: 20px 30px;
  border-radius: 0 0 12px 12px;
  margin-top: auto;
}
.main-footer-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.footer-left p {
  margin: 0;
  font-size: 12px;
  color: #666;
  line-height: 1.4;
}
.footer-right {
  display: flex;
  align-items: center;
}
.footer-links {
  display: flex;
  align-items: center;
  gap: 8px;
}
.footer-link {
  font-size: 12px;
  color: #666 !important;
  transition: color 0.3s ease;
}
.footer-link:hover {
  color: #667eea !important;
}
.footer-links .el-divider {
  margin: 0 8px;
  border-color: #e8eaec;
}
@media (max-width: 1024px) {
  .main-footer-content {
    flex-direction: column;
    gap: 12px;
    text-align: center;
  }
}
</style>
