import { useRouter } from 'vue-router'

export const setupAuthGuard = (router) => {
  router.beforeEach((to, from, next) => {
    const token = localStorage.getItem('token')
    
    if (to.matched.some(record => record.meta.requiresAuth)) {
      if (!token) {
        next({
          path: '/login',
          query: { redirect: to.fullPath }
        })
      } else {
        next()
      }
    } else if (to.matched.some(record => record.meta.guest)) {
      if (token) {
        next('/')
      } else {
        next()
      }
    } else {
      next()
    }
  })
} 