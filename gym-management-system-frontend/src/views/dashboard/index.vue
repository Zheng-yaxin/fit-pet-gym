<template>
  <div class="dashboard-container">
    <header class="clean-header">
      <div class="header-content">
        <div class="header-left">
          <h1 class="page-title">概览</h1>
          <span class="page-subtitle">Dashboard</span>
        </div>

        <div class="header-right">
          <div class="date-display">{{ currentDate }}</div>
          <div class="divider-vertical"></div>
          <button class="icon-btn hover-effect">
            <div class="notification-dot" v-if="hasNotification"></div>
            <Bell :size="20" />
          </button>
          <div class="avatar-circle hover-effect">
            <span>A</span>
          </div>
        </div>
      </div>
    </header>

    <main class="main-content custom-scrollbar">
      <div class="content-wrapper">

        <section class="welcome-section animate-fade-in">
          <h2 class="welcome-text">
            Good Morning,<br>
            <span class="admin-name">Administrator</span>
          </h2>
          <p class="summary-text">
            <span class="status-indicator online"></span>
            系统连接正常，今日运营数据实时同步中
          </p>
        </section>

        <section class="metrics-grid animate-slide-up">

          <div class="slate-card interactive" @click="navigateTo('/member/list')">
            <div class="card-header centered-header">
              <div class="header-action left">
                <div class="icon-box bg-blue-pastel">
                  <Users :size="20" class="text-slate-600" />
                </div>
              </div>
              <h3 class="card-title">会员总数</h3>
              <div class="header-action right">
                <div class="trend-badge positive">
                  <ArrowUpRight :size="14" />
                  <span>实时</span>
                </div>
              </div>
            </div>
            <div class="card-body">
              <div class="metric-value-wrapper">
                <span class="metric-value">{{ animatedStats.members }}</span>
                <span class="metric-unit">人</span>
              </div>
            </div>
            <div class="card-accent accent-blue"></div>
            <div class="card-hover-overlay"></div>
          </div>

          <div class="slate-card interactive" @click="navigateTo('/course/manage')">
            <div class="card-header centered-header">
              <div class="header-action left">
                <div class="icon-box bg-emerald-pastel">
                  <UserCheck :size="20" class="text-slate-600" />
                </div>
              </div>
              <h3 class="card-title">教练团队</h3>
              <div class="header-action right">
                <div class="status-dot active"></div>
              </div>
            </div>
            <div class="card-body">
              <div class="metric-value-wrapper">
                <span class="metric-value">{{ animatedStats.coaches }}</span>
                <span class="metric-unit">位在岗</span>
              </div>
            </div>
            <div class="card-accent accent-emerald"></div>
            <div class="card-hover-overlay"></div>
          </div>

          <div class="slate-card interactive" @click="navigateTo('/equipment/manage')">
            <div class="card-header centered-header">
              <div class="header-action left">
                <div class="icon-box bg-purple-pastel">
                  <Dumbbell :size="20" class="text-slate-600" />
                </div>
              </div>
              <h3 class="card-title">器材总数</h3>
            </div>
            <div class="card-body">
              <div class="metric-value-wrapper">
                <span class="metric-value">{{ animatedStats.equipment }}</span>
                <span class="metric-unit">台设备</span>
              </div>
            </div>
            <div class="card-accent accent-purple"></div>
            <div class="card-hover-overlay"></div>
          </div>

          <div class="slate-card interactive" @click="navigateTo('/course/schedule')">
            <div class="card-header centered-header">
              <div class="header-action left">
                <div class="icon-box bg-orange-pastel">
                  <CalendarRange :size="20" class="text-slate-600" />
                </div>
              </div>
              <h3 class="card-title">本周排课</h3>
              <div class="header-action right">
                <button class="mini-link">排课</button>
              </div>
            </div>
            <div class="card-body">
              <div class="metric-value-wrapper">
                <span class="metric-value">{{ animatedStats.courses }}</span>
                <span class="metric-unit">节</span>
              </div>
            </div>
            <div class="card-accent accent-orange"></div>
            <div class="card-hover-overlay"></div>
          </div>

        </section>

        <section class="quick-access-section animate-slide-up-delayed">
          <div class="slate-card large-panel">
            <div class="panel-header centered-header">
              <div class="header-action left">
                <div class="icon-box-small">
                  <LayoutGrid :size="18" class="text-slate-500" />
                </div>
              </div>
              <h3 class="card-title">管理员快捷入口</h3>
            </div>

            <div class="actions-grid-container">
              <div
                  v-for="action in quickActions"
                  :key="action.label"
                  class="action-item hover-effect"
                  @click="navigateTo(action.path)"
              >
                <div class="action-icon-wrapper" :class="action.colorClass">
                  <component :is="action.icon" :size="24" />
                </div>
                <span class="action-label">{{ action.label }}</span>
                <span class="action-desc">{{ action.desc }}</span>
              </div>
            </div>
          </div>
        </section>

      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, reactive, markRaw } from 'vue'
import { useRouter } from 'vue-router'
// 引入更多图标用于快捷入口
import {
  Bell, Users, UserCheck, Dumbbell, CalendarRange, ArrowUpRight,
  LayoutGrid, Wrench, ClipboardList, BookOpenCheck, ClipboardCheck,
  MessageSquare, MapPinned, WalletCards
} from 'lucide-vue-next'
import dayjs from 'dayjs'
import 'dayjs/locale/zh-cn'

// API 导入
import { getMemberList } from '@/api/member'
import { getEquipmentPage } from '@/api/equipment'
import { getAdminCoachList, getAdminScheduleList } from '@/api/course'

dayjs.locale('zh-cn')
const router = useRouter()

// --- 状态定义 ---
const currentDate = computed(() => dayjs().format('YYYY年M月D日 dddd'))
const hasNotification = ref(true)

// 用于动画显示的数字
const animatedStats = reactive({
  members: 0,
  coaches: 0,
  equipment: 0,
  courses: 0
})

// --- 快捷入口配置 ---
const quickActions = [
  {
    label: '会员管理',
    desc: '查看会员列表及详情',
    path: '/member/list',
    icon: markRaw(Users),
    colorClass: 'bg-blue-100 text-blue-600'
  },
  {
    label: '会员卡管理',
    desc: '查看会员卡与续卡信息',
    path: '/member/cards',
    icon: markRaw(WalletCards),
    colorClass: 'bg-sky-100 text-sky-600'
  },
  {
    label: '课程排期',
    desc: '管理每周课程安排',
    path: '/course/schedule',
    icon: markRaw(CalendarRange),
    colorClass: 'bg-orange-100 text-orange-600'
  },
  {
    label: '器材管理',
    desc: '器材录入与维护',
    path: '/equipment/manage',
    icon: markRaw(Dumbbell),
    colorClass: 'bg-purple-100 text-purple-600'
  },
  {
    label: '报修处理',
    desc: '查看器材报修记录',
    path: '/equipment/repair-admin',
    icon: markRaw(Wrench),
    colorClass: 'bg-red-100 text-red-600'
  },
  {
    label: '报名记录',
    desc: '课程报名审核查询',
    path: '/course/enrollment',
    icon: markRaw(ClipboardList),
    colorClass: 'bg-emerald-100 text-emerald-600'
  },
  {
    label: '动作库管理',
    desc: '维护训练动作和教学信息',
    path: '/exercise/admin',
    icon: markRaw(BookOpenCheck),
    colorClass: 'bg-indigo-100 text-indigo-600'
  },
  {
    label: '训练日志',
    desc: '查看会员训练打卡记录',
    path: '/training/admin/logs',
    icon: markRaw(ClipboardCheck),
    colorClass: 'bg-lime-100 text-lime-600'
  },
  {
    label: '课后反馈',
    desc: '查看评分、强度和反馈内容',
    path: '/feedback/admin',
    icon: markRaw(MessageSquare),
    colorClass: 'bg-amber-100 text-amber-600'
  },
  {
    label: '人流热力',
    desc: '维护场地区域和人流快照',
    path: '/gym/admin/traffic',
    icon: markRaw(MapPinned),
    colorClass: 'bg-teal-100 text-teal-600'
  }
]

// --- 方法：数字滚动动画 ---
const animateValue = (key: keyof typeof animatedStats, start: number, end: number, duration: number) => {
  let startTimestamp: number | null = null;
  const step = (timestamp: number) => {
    if (!startTimestamp) startTimestamp = timestamp;
    const progress = Math.min((timestamp - startTimestamp) / duration, 1);
    const value = Math.floor(progress * (end - start) + start);

    animatedStats[key] = value;

    if (progress < 1) {
      window.requestAnimationFrame(step);
    } else {
      animatedStats[key] = end;
    }
  };
  window.requestAnimationFrame(step);
}

// --- 方法：获取 Dashboard 核心数据 ---
const fetchDashboardData = async () => {
  try {
    const membersPromise = getMemberList({ pageNum: 1, pageSize: 1 }).then(res => res.total || 0)
    const coachesPromise = getAdminCoachList({ pageNum: 1, pageSize: 1 }).then(res => res.total || 0)
    const equipmentPromise = getEquipmentPage({ pageNum: 1, pageSize: 1 }).then(res => res.total || 0)

    const startOfWeek = dayjs().startOf('week').format('YYYY-MM-DD HH:mm:ss')
    const endOfWeek = dayjs().endOf('week').format('YYYY-MM-DD HH:mm:ss')
    const coursesPromise = getAdminScheduleList({
      startDate: startOfWeek,
      endDate: endOfWeek,
      pageNum: 1,
      pageSize: 1
    }).then(res => res.total || 0).catch(() => 0)

    const [members, coaches, equipment, courses] = await Promise.all([
      membersPromise,
      coachesPromise,
      equipmentPromise,
      coursesPromise
    ])

    animateValue('members', 0, members, 1500)
    animateValue('coaches', 0, coaches, 1000)
    animateValue('equipment', 0, equipment, 1200)
    animateValue('courses', 0, courses, 1000)

  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
}

// 页面跳转
const navigateTo = (path: string) => {
  router.push(path)
}

onMounted(() => {
  fetchDashboardData()
})
</script>

<style scoped lang="scss">
/* --- ForgeFit OS page tokens --- */
:root {
  --bg-page: var(--ff-canvas);
  --bg-card: var(--ff-surface);
  --text-primary: var(--ff-text);
  --text-secondary: var(--ff-text-secondary);
  --accent-blue: var(--ff-accent-cool);
  --accent-emerald: var(--ff-accent-power);
  --accent-purple: var(--ff-accent-cool);
  --accent-orange: var(--ff-accent-heat);
  --shadow-soft: var(--ff-shadow-panel);
  --shadow-hover: 0 0 0 1px rgba(184, 255, 44, 0.36), var(--ff-shadow-panel);
}

/* --- Layout & Reset --- */
.dashboard-container {
  height: 100vh;
  width: 100%;
  background-color: var(--bg-page);
  display: flex;
  flex-direction: column;
  font-family: var(--ff-font-ui);
  color: var(--text-primary);
}

/* --- Header --- */
.clean-header {
  height: 72px;
  background: var(--bg-card);
  border-bottom: 1px solid #f1f5f9;
  display: flex;
  justify-content: center;
  flex-shrink: 0;
  z-index: 10;
}

.header-content {
  width: 100%; max-width: 1400px; padding: 0 32px;
  display: flex; justify-content: space-between; align-items: center;
}

.page-title { font-size: 20px; font-weight: 800; color: var(--text-primary); letter-spacing: 0; }
.page-subtitle { font-size: 12px; font-weight: 500; color: var(--text-secondary); text-transform: uppercase; margin-top: 2px; }

.header-right { display: flex; align-items: center; gap: 16px; }

.date-display { font-size: 14px; font-weight: 500; color: var(--text-secondary); }
.divider-vertical { width: 1px; height: 24px; background: #e2e8f0; }

.icon-btn {
  width: 36px; height: 36px; border-radius: 50%;
  border: 1px solid transparent; background: transparent;
  display: flex; align-items: center; justify-content: center;
  color: var(--text-secondary); cursor: pointer; position: relative;
  transition: all 0.2s;
}
.icon-btn.hover-effect:hover { background: var(--ff-surface); border-color: #f1f5f9; color: var(--text-primary); transform: translateY(-1px); box-shadow: 0 2px 5px rgba(0,0,0,0.05); }

.notification-dot {
  position: absolute; top: 8px; right: 8px; width: 6px; height: 6px;
  background: #f87171; border: 1px solid var(--ff-border); border-radius: 50%;
}

.avatar-circle {
  width: 36px; height: 36px; border-radius: 50%;
  background: #f1f5f9; border: 1px solid #e2e8f0;
  display: flex; align-items: center; justify-content: center;
  font-weight: 600; font-size: 14px; color: var(--text-primary);
  cursor: pointer; transition: all 0.2s;
}
.avatar-circle.hover-effect:hover { border-color: var(--ff-border-strong); transform: scale(1.05); }

/* --- Main Content --- */
.main-content {
  flex: 1; overflow-y: auto; padding: 40px 32px;
}

.content-wrapper {
  max-width: 1200px; margin: 0 auto;
  display: flex; flex-direction: column; gap: 40px;
}

.welcome-section { margin-bottom: 8px; }

.welcome-text {
  font-size: 32px; font-weight: 700; color: var(--text-primary);
  line-height: 1.2; margin-bottom: 8px;
  .admin-name { color: #64748b; font-weight: 400; }
}

.summary-text {
  font-size: 15px; color: var(--text-secondary);
  display: flex; align-items: center; gap: 8px;
}
.status-indicator {
  width: 8px; height: 8px; border-radius: 50%;
  &.online { background-color: #22c55e; box-shadow: 0 0 0 2px #dcfce7; }
}

/* --- Cards & Grid --- */
.metrics-grid {
  display: grid; grid-template-columns: repeat(1, 1fr); gap: 24px;
  @media (min-width: 1024px) { grid-template-columns: repeat(4, 1fr); }
  @media (min-width: 768px) and (max-width: 1023px) { grid-template-columns: repeat(2, 1fr); }
}

.slate-card {
  background: var(--bg-card);
  border-radius: 20px;
  padding: 24px;
  box-shadow: var(--shadow-soft);
  position: relative; overflow: hidden;
  transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  display: flex; flex-direction: column;
  height: 100%;
  border: 1px solid transparent;

  &.interactive {
    cursor: pointer;
  }

  &.interactive:hover {
    transform: translateY(-4px);
    box-shadow: var(--shadow-hover);
    border-color: #f1f5f9;
  }
}

.card-hover-overlay {
  position: absolute; inset: 0; background: linear-gradient(180deg, rgba(255,255,255,0) 0%, rgba(255,255,255,0.4) 100%);
  opacity: 0; transition: opacity 0.3s; pointer-events: none;
}
.slate-card:hover .card-hover-overlay { opacity: 1; }

.centered-header {
  position: relative;
  display: flex; align-items: center; justify-content: center;
  margin-bottom: 24px;
  height: 36px;
}

.card-title {
  font-size: 15px; font-weight: 600; color: var(--text-primary);
  z-index: 2;
}

.header-action {
  position: absolute;
  top: 50%; transform: translateY(-50%);
  display: flex; align-items: center;
  z-index: 2;
  &.left { left: 0; }
  &.right { right: 0; }
}

.icon-box {
  width: 40px; height: 40px; border-radius: 12px;
  display: flex; align-items: center; justify-content: center;
  transition: transform 0.3s;
}
.slate-card:hover .icon-box { transform: scale(1.1) rotate(-5deg); }

.icon-box-small {
  width: 32px; height: 32px; border-radius: 8px; background: #f1f5f9;
  display: flex; align-items: center; justify-content: center;
}

.bg-blue-pastel { background: #e0f2fe; }
.bg-emerald-pastel { background: #d1fae5; }
.bg-purple-pastel { background: #ede9fe; }
.bg-orange-pastel { background: #ffedd5; }

.trend-badge {
  display: flex; align-items: center; gap: 4px;
  font-size: 12px; font-weight: 700; padding: 4px 10px;
  border-radius: 100px;
  &.positive { background: #f0fdf4; color: #15803d; }
}

.status-dot {
  width: 8px; height: 8px; border-radius: 50%; background: var(--ff-border-strong);
  &.active { background: #22c55e; box-shadow: 0 0 0 2px #dcfce7; }
}

.mini-link {
  font-size: 12px; color: var(--text-secondary); background: rgba(0,0,0,0.03);
  padding: 4px 10px; border-radius: 100px; border: none; cursor: pointer;
  transition: all 0.2s;
  &:hover { background: #334155; color: var(--ff-text); }
}

.card-body {
  flex: 1;
  display: flex; flex-direction: column; align-items: center; justify-content: center;
  padding: 8px 0 20px 0;
  z-index: 2;
}

.metric-value-wrapper { display: flex; align-items: baseline; gap: 4px; }
.metric-value {
  font-size: 38px; font-weight: 800; color: var(--text-primary);
  letter-spacing: 0; line-height: 1;
  font-feature-settings: "tnum";
  font-variant-numeric: tabular-nums;
}
.metric-unit { font-size: 13px; color: var(--text-secondary); font-weight: 500; }

.card-accent {
  position: absolute; bottom: 0; left: 0; right: 0; height: 4px;
  opacity: 0.8;
}
.accent-blue { background: var(--accent-blue); }
.accent-emerald { background: var(--accent-emerald); }
.accent-purple { background: var(--accent-purple); }
.accent-orange { background: var(--accent-orange); }

/* --- Quick Access Section --- */
.large-panel {
  min-height: auto;
  padding-bottom: 32px;
}

.actions-grid-container {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
  gap: 20px;
  padding: 0 16px;
}

.action-item {
  display: flex; flex-direction: column; align-items: center; text-align: center;
  padding: 24px 16px;
  border-radius: 16px;
  border: 1px solid #f1f5f9;
  cursor: pointer;
  transition: all 0.3s ease;
  background: var(--ff-surface);
}

.action-item:hover {
  border-color: #e2e8f0;
  box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.05);
  transform: translateY(-2px);
}

.action-icon-wrapper {
  width: 56px; height: 56px;
  border-radius: 16px;
  display: flex; align-items: center; justify-content: center;
  margin-bottom: 12px;
  transition: transform 0.3s ease;
}

.action-item:hover .action-icon-wrapper {
  transform: scale(1.1) rotate(-3deg);
}

.action-label {
  font-size: 15px; font-weight: 600; color: #334155;
  margin-bottom: 4px;
}

.action-desc {
  font-size: 12px; color: #94a3b8;
}

/* Tailwind-like utilities for icons */
.bg-blue-100 { background-color: #dbeafe; }
.text-blue-600 { color: #2563eb; }
.bg-sky-100 { background-color: #e0f2fe; }
.text-sky-600 { color: #0284c7; }
.bg-orange-100 { background-color: #ffedd5; }
.text-orange-600 { color: #ea580c; }
.bg-purple-100 { background-color: #f3e8ff; }
.text-purple-600 { color: #9333ea; }
.bg-red-100 { background-color: #fee2e2; }
.text-red-600 { color: #dc2626; }
.bg-emerald-100 { background-color: #d1fae5; }
.text-emerald-600 { color: #059669; }
.bg-indigo-100 { background-color: #e0e7ff; }
.text-indigo-600 { color: #4f46e5; }
.bg-lime-100 { background-color: #ecfccb; }
.text-lime-600 { color: #65a30d; }
.bg-amber-100 { background-color: #fef3c7; }
.text-amber-600 { color: #d97706; }
.bg-teal-100 { background-color: #ccfbf1; }
.text-teal-600 { color: #0d9488; }

/* --- Animations --- */
.animate-fade-in { animation: fadeIn 0.8s ease-out; }
.animate-slide-up { animation: slideUp 0.8s cubic-bezier(0.2, 0.8, 0.2, 1); }
.animate-slide-up-delayed { animation: slideUp 0.8s cubic-bezier(0.2, 0.8, 0.2, 1) 0.1s backwards; }

@keyframes fadeIn { from { opacity: 0; } to { opacity: 1; } }
@keyframes slideUp { from { opacity: 0; transform: translateY(30px); } to { opacity: 1; transform: translateY(0); } }

.custom-scrollbar::-webkit-scrollbar { width: 6px; }
.custom-scrollbar::-webkit-scrollbar-thumb { background: #e2e8f0; border-radius: 3px; }
.custom-scrollbar::-webkit-scrollbar-track { background: transparent; }
</style>
