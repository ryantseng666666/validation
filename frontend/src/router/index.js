import { createRouter, createWebHistory } from 'vue-router'
import { setupAuthGuard } from '../services/authGuard'
import routes from './routes'

const router = createRouter({
  history: createWebHistory(),
  routes
})

setupAuthGuard(router)

export default router 