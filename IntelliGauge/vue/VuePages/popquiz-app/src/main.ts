// main.js
import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import App from './App.vue'

const app = createApp(App)

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

import router from './router'
app.use(router)


import Login from './views/Login.vue'
import Register from './views/Register.vue'
import Answer from './views/answer.vue'

const routes = [
  { path: '/', component: Login },
  { path: '/register', component: Register },
  { path: '/answer', component: Answer },
]
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}
router.addRoute(routes)
app.use(ElementPlus)
app.mount('#app')


