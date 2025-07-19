// main.js
import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import App from './App.vue'

const app = createApp(App)
app.provide("baseurl", "http://localhost:5555")

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

import router from './router'
app.use(router)


import Login from './views/Login.vue' // 登录页面
import Register from './views/Register.vue' //登录页面
import Answer from './views/answer.vue' // 答题页面
import StudentHomePage from './views/student-homepage.vue' // 学生首页
import TeacherHomePage from './views/teacher-homepage.vue' // 教师首页
import changeInfo from './views/changeInfo.vue' // 修改个人信息页面
import Collection from './views/Collection.vue' // 模块输入页面
import OratorResult from './views/orator_result.vue' // 练习结果页面
const routes = [
  { path: '/', component: Login },
  { path: '/register', component: Register },
  { path: '/answer', component: Answer },   
  { path: '/student-homepage', component: StudentHomePage },
  { path: '/teacher-homepage', component: TeacherHomePage },
  { path: '/changeInfo', component: changeInfo },
  { path: '/Collection', component: Collection },
  { path: '/orator_result', component: OratorResult },
]
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}
router.addRoute(routes)
app.use(ElementPlus)
app.mount('#app')


