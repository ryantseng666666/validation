import Home from '../views/Home.vue'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import ResetPassword from '../views/ResetPassword.vue'
import SpeedTest from '../views/SpeedTest.vue'
import OpticalPower from '../views/OpticalPower.vue'
import SNCode from '../views/SNCode.vue'
import WorkOrder from '../views/WorkOrder.vue'
import QCDetail from '../views/QCDetail.vue'

export default [
  {
    path: '/',
    name: 'Home',
    component: Home,
    meta: { requiresAuth: false }
  },
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: { guest: true }
  },
  {
    path: '/register',
    name: 'Register',
    component: Register,
    meta: { guest: true }
  },
  {
    path: '/reset-password',
    name: 'ResetPassword',
    component: ResetPassword,
    meta: { guest: true }
  },
  {
    path: '/speed-test',
    name: 'SpeedTest',
    component: SpeedTest,
    meta: { requiresAuth: true }
  },
  {
    path: '/optical-power',
    name: 'OpticalPower',
    component: OpticalPower,
    meta: { requiresAuth: true }
  },
  {
    path: '/sn-code',
    name: 'SNCode',
    component: SNCode,
    meta: { requiresAuth: true }
  },
  {
    path: '/work-order',
    name: 'WorkOrder',
    component: WorkOrder,
    meta: { requiresAuth: true }
  },
  {
    path: '/qc-detail/:jobNo',
    name: 'QCDetail',
    component: QCDetail,
    meta: { requiresAuth: true }
  }
] 