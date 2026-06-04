import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'
import { useUserStore } from '@/store/modules/user'
// 引入布局组件
import AdminLayout from '@/layout/AdminLayout.vue'

const routes: RouteRecordRaw[] = [
    {
        path: '/',
        redirect: '/login' // 修改：访问根路径默认跳转至登录页
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
        path: '/profile',
        name: 'Profile',
        component: () => import('@/views/profile/index.vue'),
        meta: { title: '个人中心', requiresAuth: true, roles: ['MEMBER'] }
    },
    // 1. 选课中心 (团课)
    {
        path: '/course/list',
        name: 'CourseList',
        component: () => import('@/views/course/user/index.vue'),
        meta: { title: '选课中心', requiresAuth: true, roles: ['MEMBER'] }
    },
    // 2. 私教预约 (私教)
    {
        path: '/coach/list',
        name: 'CoachList',
        component: () => import('@/views/course/user/CoachList.vue'),
        meta: { title: '私教预约', requiresAuth: true, roles: ['MEMBER'] }
    },
    // 3. 在线咨询 (聊天)
    {
        path: '/chat',
        name: 'Chat',
        component: () => import('@/views/chat/index.vue'),
        meta: { title: '在线咨询', requiresAuth: true, roles: ['MEMBER'] }
    },
    // 器材相关
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
    {
        path: '/exercise/library',
        name: 'ExerciseLibrary',
        component: () => import('@/views/exercise/library/index.vue'),
        meta: { title: '动作教学库', requiresAuth: true, roles: ['MEMBER'] }
    },
    {
        path: '/training/plan',
        name: 'TrainingPlan',
        component: () => import('@/views/training/plan/index.vue'),
        meta: { title: '智能训练计划', requiresAuth: true, roles: ['MEMBER'] }
    },
    {
        path: '/training/log',
        name: 'TrainingLog',
        component: () => import('@/views/training/log/index.vue'),
        meta: { title: '训练打卡与日志', requiresAuth: true, roles: ['MEMBER'] }
    },
    {
        path: '/course/feedback',
        name: 'CourseFeedback',
        component: () => import('@/views/course/feedback/index.vue'),
        meta: { title: '课后反馈', requiresAuth: true, roles: ['MEMBER'] }
    },
    {
        path: '/gym/traffic',
        name: 'GymTraffic',
        component: () => import('@/views/gym/traffic/index.vue'),
        meta: { title: '健身房人流热力图', requiresAuth: true, roles: ['MEMBER'] }
    },

    // ================== 管理员路由 (SYS_USER) ==================
    {
        path: '/admin',
        component: AdminLayout,
        redirect: '/dashboard',
        meta: { requiresAuth: true, roles: ['SYS_USER'] },
        children: [
            {
                path: '/dashboard',
                name: 'Dashboard',
                component: () => import('@/views/dashboard/index.vue'),
                meta: { title: '管理控制台' }
            },
            {
                path: '/member/list',
                name: 'MemberList',
                component: () => import('@/views/member/index.vue'),
                meta: { title: '会员管理' }
            },
            {
                path: '/member/cards',
                name: 'MemberCards',
                component: () => import('@/views/member/cards.vue'),
                meta: { title: '会员卡管理' }
            },
            // 器材模块
            {
                path: '/equipment/manage',
                name: 'EquipmentManage',
                component: () => import('@/views/equipment/admin/EquipmentManage.vue'),
                meta: { title: '器材设施管理' }
            },
            {
                path: '/equipment/repair-admin',
                name: 'AdminRepairManagement',
                component: () => import('@/views/equipment/report/AdminRepairManagement.vue'),
                meta: { title: '报修处理' }
            },
            // 课程模块
            {
                path: '/course/manage',
                name: 'CourseManage',
                component: () => import('@/views/course/admin/manage/index.vue'),
                meta: { title: '课程管理' }
            },
            {
                path: '/course/schedule',
                name: 'ScheduleManage',
                component: () => import('@/views/course/admin/schedule/index.vue'),
                meta: { title: '排课管理' }
            },
            {
                path: '/course/enrollment',
                name: 'EnrollmentManage',
                component: () => import('@/views/course/admin/enrollment/index.vue'),
                meta: { title: '报名管理' }
            },
            // 训练闭环
            {
                path: '/exercise/admin',
                name: 'ExerciseAdmin',
                component: () => import('@/views/exercise/admin/index.vue'),
                meta: { title: '动作库管理' }
            },
            {
                path: '/training/admin/logs',
                name: 'AdminTrainingLogs',
                component: () => import('@/views/training/admin/logs.vue'),
                meta: { title: '训练日志统计' }
            },
            {
                path: '/feedback/admin',
                name: 'AdminFeedback',
                component: () => import('@/views/feedback/admin/index.vue'),
                meta: { title: '课后反馈分析' }
            },
            // 运营模块
            {
                path: '/gym/admin/traffic',
                name: 'AdminGymTraffic',
                component: () => import('@/views/gym/admin/traffic.vue'),
                meta: { title: '人流热力管理' }
            }
        ]
    },
    // 教练端 (示例)
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

// 路由守卫
router.beforeEach((to, _from, next) => {
    const userStore = useUserStore()
    const token = userStore.token
    document.title = (to.meta.title as string) || 'Gym System'

    if (to.path === '/login') {
        if (token) {
            // 如果已登录，根据角色跳转到对应首页
            next(userStore.userType === 'SYS_USER' ? '/dashboard' : '/home')
        } else {
            next()
        }
    } else {
        if (to.meta.requiresAuth && !token) {
            next('/login')
        } else {
            if (token && !userStore.userType) {
                userStore.logout()
                next('/login')
                return
            }

            const roles = to.meta.roles as string[] | undefined
            if (roles && !roles.includes(userStore.userType || '')) {
                const redirectPath = userStore.userType === 'SYS_USER' ? '/dashboard' : '/home'

                if (to.path === redirectPath) {
                    console.warn('Role mismatch loop detected. Redirecting to login.')
                    userStore.logout()
                    next('/login')
                } else {
                    next(redirectPath)
                }
            } else {
                next()
            }
        }
    }
})

export default router
