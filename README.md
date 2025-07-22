# IntelliGauge

> AI-driven content-to-quiz system for educational engagement and feedback collection.

## 📌 项目简介

**IntelliGauge** 是一个多模态内容处理与交互测评系统，支持将 PPT、PDF、音视频等内容自动转化为测试题，并通过 Web 页面与用户交互完成测评与反馈闭环。该系统适用于演讲、课堂、培训等场景，帮助组织者量化受众理解效果，优化内容表达。

---

## ✨ 核心功能

### 1. 输入内容采集（多模态转文字）
- 📄 支持输入格式：
  - 文本文件（.txt）
  - PowerPoint 文件（.ppt/.pptx）
  - PDF 文件
  - 音频文件（自动语音识别）
  - 视频文件（提取音轨与 OCR 图文信息）
- ✅ 所有提取内容均存入数据库供后续分析

### 2. 智能生成测试题
- 🤖 使用大语言模型（LLM）将内容转化为选择题（四选一）
- ⏱ 每道题答题时长控制在 10 秒内
- 🎯 输出题目经过语义质量检测，避免“太浅/文不对题”
- 🔄 实现 AI 闭环：自动检测题目质量 → 调整 prompt → 再生成

### 3. 用户端交互体验
- 📱 用户在网页/微信小程序上完成答题
- 📊 实时反馈正确率、答题进度与个人排名
- 💬 每道题目结束后开启讨论区，展示题干与用户评论

### 4. 讲者 & 组织者视图
- 👨‍🏫 讲者可查看学生每题答题人数与正确率
- 🏢 组织者可查看整场演讲的汇总反馈、各演讲场次数据对比
- 🔁 收集听众反馈：内容节奏/趣味性/题目质量等维度

---

## 🧩 技术架构

| 层级 | 技术栈 |
|------|--------|
| 前端 Web | Vue 3 + Vite + Element Plus |
| 小程序 | uni-app / 微信小程序 |
| 后端 | Spring Boot + MyBatis-Plus + Spring Security + JWT |
| AI 服务 | Spring AI + OpenAI API / DeepSeek API |
| 数据存储 | MySQL + Redis + MinIO |
| 多模态解析 | FFmpeg + Whisper + Tesseract OCR（Python 服务） |
| 实时通信 | WebSocket |
| 构建与部署 | Maven + Docker + GitHub Actions |

---

## 📁 项目目录结构

```bash
IntelliGauge/
├── frontend/               # Vue 前端项目
├── backend/                # Spring Boot 后端项目
│   ├── src/main/java/…     # 后端逻辑模块
│   └── src/main/resources/ # 配置文件
├── miniapp/                # 小程序端
├── model-worker/           # Python 多模态/LLM 服务
├── docs/                   # 项目文档
└── README.md

```

## ✨ 部署方式 <i class="fa fa-folder"></i>

1. **拉取仓库**

2. **安装并运行前端**
   
   在 `kbdui-work/IntelliGauge/vue/VuePages/popquiz-app` 路径下，运行以下命令安装依赖：

   ```bash
   npm install
   
   npm run dev
4. 导入数据库
   `kbdui-work/sql` 中的 `intelligauge_data2.sql` 导入到本地的 MySQL 数据库表。

5. 修改数据库配置
修改 `kbdui-work/IntelliGauge/IntelliGauge_back/src/main/resources/application-local.yml` 中的 MySQL 数据库配置。

6. 配置 API 密钥
在 `kbdui-work/IntelliGauge/IntelliGauge_back/src/main/resources` 路径下新建 `application-key.ym` 文件，并添加以下内容（将xxx替换为自己的密钥，或者联系项目负责人获取)：

```yaml
# deepseek的api开放平台密钥
deepseek:
  api:
    key: xxx

# 使用的为讯飞开放平台星火的录音文件转写Ifasr接口,模型调用路径为https://raasr.xfyun.cn/v2/api
iflytek:
  api:
    apiId: xxx
    secret_key: xxx
```
6.添加maven后运行即可启动后端

