import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'login',
      component: () => import('../views/Login.vue'),
    },
    {
      path: '/answer',
      name: 'answer',
      component: () => import('../views/answer.vue'),
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('../views/Register.vue'),
    },
    {
      path: '/HomePage',
      name: 'HomePage',
      component: () => import('../views/teacher-homepage.vue'),
    },
    {
      path: '/changeInfo',
      name: 'changeInfo',
      component: () => import('../views/changeInfo.vue'),
    },
    {
      path: '/Collection',
      name: 'Collection',
      component: () => import('../views/Collection.vue'),
    },
    {
      path: '/orator_result',
      name: 'orator_result',
      component: () => import('../views/orator_result.vue'),
    },
    {
      path: '/student-homepage',
      name: 'student-homepage',
      component: () => import('../views/student-homepage.vue'),
    },
  ],
})

export default router
