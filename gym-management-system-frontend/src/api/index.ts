import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'
import { useUserStore } from '@/store/modules/user'

const routes: RouteRecordRaw[] = [
    {
        path: '/',
        redirect: (to) => {
            const token = localStorage.getItem('token')
            const userType = localStorage.getItem('userType')
            if (!token) return '/login'
            return userType === 'SYS_USER' ? '/dashboard' : '/home'
        }
    },
    {
        path: '/login',
        name: 'Login',
        component: () => import('@/views/login/index.vue'),
        meta: { title: '登录 - 健身管理系统' }
    },
    // ================== 会员端路由 (MEMBER) ==================
    {
        path: '/home',
        name: 'Home',
        component: () => import('@/views/home/index.vue'),
        meta: { title: '首页', requiresAuth: true, roles: ['MEMBER'] }
    },
    {
        path: '/health',
        name: 'Health',
        component: () => import('@/views/health/index.vue'),
        meta: { title: '健康中心', requiresAuth: true, roles: ['MEMBER'] }
    },
    {
        path: '/course/list',
        name: 'CourseList',
        component: () => import('@/views/course/user/index.vue'),
        meta: { title: '选课中心', requiresAuth: true, roles: ['MEMBER'] }
    },
    {
        path: '/equipment/query',
        name: 'EquipmentQuery',
        component: () => import('@/views/equipment/user/EquipmentQuery.vue'),
        meta: { title: '器材查询', requiresAuth: true, roles: ['MEMBER'] }
    },
    {
        path: '/equipment/my-repair',
        name: 'MyRepairLogs',
        component: () => import('@/views/equipment/user/MyRepairLogs.vue'),
        meta: { title: '我的报修', requiresAuth: true, roles: ['MEMBER'] }
    },

    // ================== 管理员路由 (SYS_USER) ==================
    {
        path: '/dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '管理控制台', requiresAuth: true, roles: ['SYS_USER'] }
    },
    {
        path: '/member/list',
        name: 'MemberList',
        component: () => import('@/views/member/index.vue'),
        meta: { title: '会员管理', requiresAuth: true, roles: ['SYS_USER'] }
    },
    // 新增：会员卡管理路由
    {
        path: '/member/cards',
        name: 'MemberCards',
        component: () => import('@/views/member/cards.vue'),
        meta: { title: '会员卡管理', requiresAuth: true, roles: ['SYS_USER'] }
    },

    // 管理员器材模块
    {
        path: '/equipment/manage',
        name: 'EquipmentManage',
        component: () => import('@/views/equipment/admin/EquipmentManage.vue'),
        meta: { title: '器材设施管理', requiresAuth: true, roles: ['SYS_USER'] }
    },
    {
        path: '/equipment/repair-admin',
        name: 'AdminRepairManagement',
        component: () => import('@/views/equipment/report/AdminRepairManagement.vue'),
        meta: { title: '报修处理', requiresAuth: true, roles: ['SYS_USER'] }
    },
    // 管理员课程模块
    {
        path: '/course/manage',
        name: 'CourseManage',
        component: () => import('@/views/course/admin/manage/index.vue'),
        meta: { title: '课程管理', requiresAuth: true, roles: ['SYS_USER'] }
    },
    {
        path: '/course/schedule',
        name: 'ScheduleManage',
        component: () => import('@/views/course/admin/schedule/index.vue'),
        meta: { title: '排课管理', requiresAuth: true, roles: ['SYS_USER'] }
    },
    {
        path: '/course/enrollment',
        name: 'EnrollmentManage',
        component: () => import('@/views/course/admin/enrollment/index.vue'),
        meta: { title: '报名管理', requiresAuth: true, roles: ['SYS_USER'] }
    },
    // 教练端路由
    {
        path: '/course/coach',
        name: 'CoachCourse',
        component: () => import('@/views/course/coach/index.vue'),
        meta: { title: '我的排课', requiresAuth: true, roles: ['SYS_USER', 'COACH'] }
    },

    {
        path: '/:pathMatch(.*)*',
        redirect: '/'
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

router.beforeEach((to, from, next) => {
    const userStore = useUserStore()
    const token = userStore.token
    document.title = (to.meta.title as string) || 'Gym System'

    if (to.path === '/login') {
        if (token) {
            next(userStore.userType === 'SYS_USER' ? '/dashboard' : '/home')
        } else {
            next()
        }
    } else {
        if (to.meta.requiresAuth && !token) {
            next('/login')
        } else {
            if (to.meta.roles && !to.meta.roles.includes(userStore.userType || '')) {
                next(userStore.userType === 'SYS_USER' ? '/dashboard' : '/home')
            } else {
                next()
            }
        }
    }
})

export default router