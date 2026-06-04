<template>
  <main class="forge-home" aria-label="会员首页">
    <section class="command-hero" aria-labelledby="home-title">
      <img class="hero-media" :src="heroImage" alt="健身房动感单车训练区" />
      <div class="hero-overlay"></div>
      <div class="hero-grid"></div>

      <header class="topbar">
        <div>
          <p class="date-chip">{{ currentDate }}</p>
          <h1 id="home-title">
            <span>今日训练指挥台</span>
            <strong>{{ displayName }}</strong>
          </h1>
        </div>

        <div class="profile-cluster">
          <button class="icon-command" type="button" aria-label="进入个人中心" @click="handleProfile">
            <User :size="18" />
            <span>{{ avatarInitial }}</span>
          </button>
          <button class="ghost-command" type="button" @click="handleLogout">
            <LogOut :size="16" />
            <span>退出</span>
          </button>
        </div>
      </header>

      <div class="hero-content">
        <div class="mission-panel">
          <div class="mission-status">
            <span class="pulse-dot"></span>
            <span>ForgeFit OS 在线</span>
          </div>
          <p class="mission-kicker">TRAINING WINDOW</p>
          <h2>力量日 · 下肢与核心</h2>
          <p class="mission-copy">
            先完成热身，再进入主训练。今日建议控制强度，保持动作质量。
          </p>

          <div class="mission-actions">
            <button class="power-button" type="button" @click="router.push('/training/plan')">
              <Zap :size="17" />
              <span>开始训练</span>
            </button>
            <button class="line-button" type="button" @click="router.push('/chat')">
              <MessageSquare :size="17" />
              <span>联系教练</span>
            </button>
          </div>
        </div>

        <div class="readout-strip" aria-label="今日关键指标">
          <article v-for="metric in heroMetrics" :key="metric.label" class="readout-cell">
            <p>{{ metric.label }}</p>
            <strong>{{ metric.value }}</strong>
            <span>{{ metric.hint }}</span>
          </article>
        </div>
      </div>
    </section>

    <section class="control-grid" aria-label="训练与健康概览">
      <article class="data-panel nutrition-panel">
        <div class="panel-header">
          <div>
            <p class="eyebrow">FUEL STATUS</p>
            <h2>今日摄入</h2>
          </div>
          <Utensils :size="22" />
        </div>

        <div class="fuel-readout">
          <strong>{{ caloriesConsumed }}</strong>
          <span>/ {{ calorieGoal }} kcal</span>
        </div>

        <div class="energy-track" aria-label="饮食目标进度">
          <span :style="{ width: caloriePercentage + '%' }"></span>
        </div>

        <div class="macro-grid">
          <div>
            <span>蛋白质</span>
            <strong>{{ Math.round(dietSummary.totalProtein || 0) }}g</strong>
          </div>
          <div>
            <span>碳水</span>
            <strong>{{ Math.round(dietSummary.totalCarb || 0) }}g</strong>
          </div>
          <div>
            <span>脂肪</span>
            <strong>{{ Math.round(dietSummary.totalFat || 0) }}g</strong>
          </div>
        </div>
      </article>

      <article class="data-panel body-panel">
        <div class="panel-header">
          <div>
            <p class="eyebrow">BODY METRICS</p>
            <h2>身体状态</h2>
          </div>
          <HeartPulse :size="22" />
        </div>

        <div class="body-readouts">
          <div>
            <span>当前体重</span>
            <strong>{{ weightValue }}</strong>
            <em>kg</em>
          </div>
          <div>
            <span>BMI</span>
            <strong>{{ displayBMI }}</strong>
            <em>{{ bmiLabel }}</em>
          </div>
        </div>

        <button class="panel-link" type="button" @click="router.push('/health')">
          <span>进入健康中心</span>
          <ChevronRight :size="17" />
        </button>
      </article>

      <article class="data-panel heat-panel">
        <div class="panel-header">
          <div>
            <p class="eyebrow">LIVE FLOOR</p>
            <h2>客流热力</h2>
          </div>
          <Radio :size="22" />
        </div>

        <div class="zone-map" aria-hidden="true">
          <span class="zone zone-power"></span>
          <span class="zone zone-cardio"></span>
          <span class="zone zone-studio"></span>
          <span class="zone zone-free"></span>
        </div>

        <div class="heat-footer">
          <span>当前建议：避开有氧高峰</span>
          <button type="button" @click="router.push('/gym/traffic')">查看热力</button>
        </div>
      </article>
    </section>

    <section class="action-section" aria-labelledby="actions-title">
      <div class="section-heading">
        <div>
          <p class="eyebrow">QUICK COMMANDS</p>
          <h2 id="actions-title">常用入口</h2>
        </div>
        <span>单击直达</span>
      </div>

      <div class="action-grid">
        <button
          v-for="action in quickActions"
          :key="action.label"
          class="action-tile"
          type="button"
          :aria-label="action.label"
          @click="router.push(action.path)"
        >
          <component :is="action.icon" :size="22" />
          <span>{{ action.label }}</span>
          <em>{{ action.desc }}</em>
        </button>
      </div>
    </section>
  </main>
</template>

<script setup lang="ts">
import { computed, markRaw, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/modules/user'
import { ElMessageBox } from 'element-plus'
import dayjs from 'dayjs'
import 'dayjs/locale/zh-cn'
import heroImage from '@/assets/images/forgefit-home-bike.jpg'
import {
  BookOpenCheck,
  CalendarDays,
  ChevronRight,
  ClipboardCheck,
  Dumbbell,
  Flame,
  Gauge,
  HeartPulse,
  ListChecks,
  LogOut,
  MapPinned,
  MessageSquare,
  Radio,
  Trophy,
  User,
  Utensils,
  Wrench,
  Zap
} from 'lucide-vue-next'
import { getDietSummary, getHealthDataHistory, getLatestHealthData } from '@/api/health'
import type { DietSummaryVO, HealthData } from '@/api/health'

const router = useRouter()
const userStore = useUserStore()

dayjs.locale('zh-cn')

const todayStr = dayjs().format('YYYY-MM-DD')
const currentDate = computed(() => dayjs().format('M月D日 dddd'))

const healthData = ref<HealthData>({})
const dietSummary = ref<DietSummaryVO>({
  date: todayStr,
  totalCalories: 0,
  totalProtein: 0,
  totalFat: 0,
  totalCarb: 0,
  recommendCalories: 2000,
  recommendProtein: 0,
  recommendFat: 0,
  recommendCarb: 0,
  suggestions: [],
  details: []
})

const displayName = computed(() => userStore.userInfo?.nickname || '运动伙伴')
const avatarInitial = computed(() => displayName.value.slice(0, 1).toUpperCase())

const displayBMI = computed(() => {
  const { bmi, weight, height } = healthData.value
  if (bmi) return bmi
  if (weight && height) {
    const heightInMeters = height / 100
    return (weight / (heightInMeters * heightInMeters)).toFixed(1)
  }
  return '--'
})

const bmiLabel = computed(() => {
  const value = Number(displayBMI.value)
  if (Number.isNaN(value)) return '待记录'
  if (value < 18.5) return '偏低'
  if (value < 24) return '标准'
  if (value < 28) return '偏高'
  return '需关注'
})

const weightValue = computed(() => {
  const weight = healthData.value.weight
  return weight !== undefined && weight !== null ? weight : '--'
})

const caloriesConsumed = computed(() => Math.round(dietSummary.value.totalCalories || 0))
const calorieGoal = computed(() => Math.round(dietSummary.value.recommendCalories || 2000))
const caloriePercentage = computed(() => {
  return Math.min((caloriesConsumed.value / calorieGoal.value) * 100, 100)
})

const heroMetrics = computed(() => [
  {
    label: '摄入进度',
    value: `${Math.round(caloriePercentage.value)}%`,
    hint: `${Math.max(calorieGoal.value - caloriesConsumed.value, 0)} kcal 余量`
  },
  {
    label: '身体指数',
    value: String(displayBMI.value),
    hint: bmiLabel.value
  },
  {
    label: '训练窗口',
    value: '18:30',
    hint: '建议开始'
  }
])

const quickActions = [
  { label: '课程表', desc: '预约团课', path: '/course/list', icon: markRaw(CalendarDays) },
  { label: '动作库', desc: '肌群教学', path: '/exercise/library', icon: markRaw(BookOpenCheck) },
  { label: '训练计划', desc: '今日动作', path: '/training/plan', icon: markRaw(ListChecks) },
  { label: '训练打卡', desc: '记录表现', path: '/training/log', icon: markRaw(ClipboardCheck) },
  { label: '客流热力', desc: '避开高峰', path: '/gym/traffic', icon: markRaw(MapPinned) },
  { label: '器械查询', desc: '设施状态', path: '/equipment/query', icon: markRaw(Dumbbell) },
  { label: '我的报修', desc: '进度追踪', path: '/equipment/my-repair', icon: markRaw(Wrench) },
  { label: '课后反馈', desc: '体验复盘', path: '/course/feedback', icon: markRaw(Trophy) }
]

const loadData = async () => {
  try {
    const [healthRes, dietRes] = await Promise.all([
      getLatestHealthData(),
      getDietSummary(todayStr)
    ])

    if (healthRes) {
      healthData.value = healthRes
    } else {
      const historyRes = await getHealthDataHistory()
      if (historyRes?.length) healthData.value = historyRes[0]
    }

    if (dietRes) dietSummary.value = dietRes
  } catch (error) {
    console.error('Failed to load home data:', error)
  }
}

const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗?', '退出确认', {
    confirmButtonText: '退出',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => userStore.logout()).catch(() => {})
}

const handleProfile = () => router.push('/profile')

onMounted(loadData)
</script>

<style scoped lang="scss">
.forge-home {
  --ff-canvas: #0b0c0a;
  --ff-surface: #151713;
  --ff-surface-raised: #20231d;
  --ff-surface-muted: #292d25;
  --ff-border: #343a30;
  --ff-border-strong: #4a5244;
  --ff-text: #f3f1e8;
  --ff-text-secondary: #a7a99e;
  --ff-text-muted: #71766c;
  --ff-accent-power: #b8ff2c;
  --ff-accent-heat: #ff6a1a;
  --ff-accent-cool: #31d8c8;
  --ff-warning: #f6c945;
  min-height: 100vh;
  padding: 20px;
  color: var(--ff-text);
  background:
    radial-gradient(circle at 12% 0%, rgba(184, 255, 44, 0.14), transparent 30%),
    radial-gradient(circle at 88% 12%, rgba(255, 106, 26, 0.12), transparent 28%),
    linear-gradient(135deg, #090a08 0%, #11140f 52%, #070806 100%);
  font-family: "Alibaba PuHuiTi", "HarmonyOS Sans SC", "Source Han Sans SC", "Microsoft YaHei UI", sans-serif;
}

.forge-home *,
.forge-home *::before,
.forge-home *::after {
  box-sizing: border-box;
}

button {
  font: inherit;
}

.command-hero {
  position: relative;
  min-height: 520px;
  max-width: 1180px;
  margin: 0 auto;
  overflow: hidden;
  border: 1px solid rgba(184, 255, 44, 0.18);
  border-radius: 8px;
  background: var(--ff-surface);
  isolation: isolate;
  box-shadow: 0 24px 80px rgba(0, 0, 0, 0.4);
}

.hero-media {
  position: absolute;
  inset: 0;
  z-index: -3;
  width: 100%;
  height: 100%;
  object-fit: cover;
  filter: saturate(0.88) contrast(1.08) brightness(0.55);
}

.hero-overlay {
  position: absolute;
  inset: 0;
  z-index: -2;
  background:
    linear-gradient(90deg, rgba(11, 12, 10, 0.96) 0%, rgba(11, 12, 10, 0.76) 48%, rgba(11, 12, 10, 0.38) 100%),
    linear-gradient(0deg, rgba(11, 12, 10, 0.92) 0%, transparent 45%);
}

.hero-grid {
  position: absolute;
  inset: 0;
  z-index: -1;
  opacity: 0.22;
  background-image:
    linear-gradient(rgba(184, 255, 44, 0.2) 1px, transparent 1px),
    linear-gradient(90deg, rgba(184, 255, 44, 0.16) 1px, transparent 1px);
  background-size: 64px 64px;
  mask-image: linear-gradient(90deg, black 0%, transparent 82%);
}

.topbar,
.hero-content,
.control-grid,
.action-section {
  max-width: 1180px;
}

.topbar {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 24px;
  padding: 26px;
}

.date-chip,
.eyebrow {
  margin: 0;
  color: var(--ff-accent-power);
  font-family: "Bahnschrift", "DIN Alternate", "Rajdhani", sans-serif;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0;
  text-transform: uppercase;
}

.topbar h1 {
  margin: 8px 0 0;
  display: flex;
  flex-direction: column;
  gap: 4px;
  line-height: 1;
}

.topbar h1 span {
  color: var(--ff-text-secondary);
  font-size: 18px;
  font-weight: 500;
}

.topbar h1 strong {
  color: var(--ff-text);
  font-family: "Bahnschrift", "DIN Alternate", "Rajdhani", sans-serif;
  font-size: clamp(38px, 8vw, 74px);
  font-weight: 800;
  letter-spacing: 0;
  text-transform: uppercase;
}

.profile-cluster,
.mission-actions,
.panel-header,
.heat-footer,
.section-heading {
  display: flex;
  align-items: center;
}

.profile-cluster {
  gap: 10px;
}

.icon-command,
.ghost-command,
.power-button,
.line-button,
.panel-link,
.heat-footer button,
.action-tile {
  min-height: 44px;
  border: 1px solid var(--ff-border);
  border-radius: 8px;
  color: var(--ff-text);
  cursor: pointer;
  transition:
    transform 120ms cubic-bezier(0.2, 0.8, 0.2, 1),
    border-color 180ms cubic-bezier(0.16, 1, 0.3, 1),
    background 180ms cubic-bezier(0.16, 1, 0.3, 1),
    color 180ms cubic-bezier(0.16, 1, 0.3, 1);
}

.icon-command:focus-visible,
.ghost-command:focus-visible,
.power-button:focus-visible,
.line-button:focus-visible,
.panel-link:focus-visible,
.heat-footer button:focus-visible,
.action-tile:focus-visible {
  outline: 2px solid var(--ff-accent-power);
  outline-offset: 3px;
}

.icon-command:active,
.ghost-command:active,
.power-button:active,
.line-button:active,
.panel-link:active,
.heat-footer button:active,
.action-tile:active {
  transform: scale(0.97);
}

.icon-command {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 0 13px;
  background: rgba(32, 35, 29, 0.76);
}

.icon-command span {
  color: var(--ff-accent-power);
  font-weight: 800;
}

.ghost-command,
.line-button {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 0 14px;
  background: rgba(21, 23, 19, 0.64);
}

.ghost-command:hover,
.line-button:hover,
.panel-link:hover {
  border-color: var(--ff-border-strong);
  color: var(--ff-accent-power);
}

.hero-content {
  display: grid;
  grid-template-columns: minmax(0, 1.08fr) minmax(280px, 0.92fr);
  gap: 18px;
  padding: 40px 26px 26px;
}

.mission-panel,
.data-panel,
.action-tile {
  border: 1px solid var(--ff-border);
  border-radius: 8px;
  background:
    linear-gradient(145deg, rgba(32, 35, 29, 0.88), rgba(15, 17, 13, 0.94)),
    repeating-linear-gradient(135deg, rgba(255, 255, 255, 0.025) 0 1px, transparent 1px 8px);
}

.mission-panel {
  padding: 24px;
  animation: forgeEnter 420ms cubic-bezier(0.16, 1, 0.3, 1) both;
}

.mission-status {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  min-height: 30px;
  padding: 0 10px;
  border: 1px solid rgba(49, 216, 200, 0.36);
  border-radius: 999px;
  color: var(--ff-accent-cool);
  background: rgba(49, 216, 200, 0.08);
  font-size: 12px;
  font-weight: 700;
}

.pulse-dot {
  width: 8px;
  height: 8px;
  border-radius: 999px;
  background: var(--ff-accent-cool);
  box-shadow: 0 0 18px var(--ff-accent-cool);
}

.mission-kicker {
  margin: 30px 0 8px;
  color: var(--ff-accent-heat);
  font-family: "Bahnschrift", "DIN Alternate", "Rajdhani", sans-serif;
  font-size: 12px;
  font-weight: 800;
}

.mission-panel h2 {
  max-width: 620px;
  margin: 0;
  color: var(--ff-text);
  font-size: clamp(34px, 7vw, 82px);
  line-height: 0.94;
  font-weight: 900;
  letter-spacing: 0;
}

.mission-copy {
  max-width: 520px;
  margin: 18px 0 0;
  color: var(--ff-text-secondary);
  font-size: 16px;
  line-height: 1.7;
}

.mission-actions {
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 28px;
}

.power-button {
  display: inline-flex;
  align-items: center;
  gap: 9px;
  padding: 0 20px;
  border-color: rgba(184, 255, 44, 0.72);
  color: #10120d;
  background: var(--ff-accent-power);
  font-weight: 900;
  box-shadow: 0 0 32px rgba(184, 255, 44, 0.18);
}

.power-button:hover {
  transform: translateY(-2px);
}

.line-button {
  color: var(--ff-text);
}

.readout-strip {
  display: grid;
  grid-template-columns: 1fr;
  gap: 12px;
  animation: forgeEnter 420ms cubic-bezier(0.16, 1, 0.3, 1) 80ms both;
}

.readout-cell {
  min-height: 124px;
  padding: 18px;
  border: 1px solid rgba(184, 255, 44, 0.16);
  border-radius: 8px;
  background: rgba(11, 12, 10, 0.78);
}

.readout-cell p,
.readout-cell span {
  margin: 0;
  color: var(--ff-text-muted);
  font-size: 13px;
}

.readout-cell strong {
  display: block;
  margin: 8px 0;
  color: var(--ff-text);
  font-family: "Bahnschrift", "DIN Alternate", "Rajdhani", sans-serif;
  font-size: 38px;
  line-height: 1;
  font-variant-numeric: tabular-nums;
}

.control-grid,
.action-section {
  margin: 18px auto 0;
}

.control-grid {
  display: grid;
  grid-template-columns: 1.15fr 0.95fr 0.9fr;
  gap: 18px;
}

.data-panel {
  min-height: 260px;
  padding: 20px;
  animation: forgeEnter 420ms cubic-bezier(0.16, 1, 0.3, 1) both;
}

.nutrition-panel {
  animation-delay: 70ms;
}

.body-panel {
  animation-delay: 110ms;
}

.heat-panel {
  animation-delay: 150ms;
}

.panel-header,
.section-heading {
  justify-content: space-between;
  gap: 16px;
}

.panel-header h2,
.section-heading h2 {
  margin: 5px 0 0;
  color: var(--ff-text);
  font-size: 22px;
  line-height: 1.1;
}

.panel-header svg {
  color: var(--ff-accent-power);
}

.fuel-readout {
  display: flex;
  align-items: baseline;
  gap: 10px;
  margin-top: 34px;
}

.fuel-readout strong,
.body-readouts strong {
  color: var(--ff-text);
  font-family: "Bahnschrift", "DIN Alternate", "Rajdhani", sans-serif;
  font-size: 58px;
  line-height: 0.9;
  font-variant-numeric: tabular-nums;
}

.fuel-readout span,
.body-readouts span,
.body-readouts em,
.heat-footer span,
.section-heading > span {
  color: var(--ff-text-secondary);
  font-size: 13px;
  font-style: normal;
}

.energy-track {
  height: 10px;
  margin-top: 22px;
  overflow: hidden;
  border-radius: 999px;
  background: rgba(243, 241, 232, 0.08);
}

.energy-track span {
  display: block;
  height: 100%;
  border-radius: inherit;
  background: linear-gradient(90deg, var(--ff-accent-power), var(--ff-accent-heat));
  transition: width 420ms cubic-bezier(0.16, 1, 0.3, 1);
}

.macro-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 10px;
  margin-top: 18px;
}

.macro-grid div {
  padding: 12px;
  border: 1px solid rgba(243, 241, 232, 0.08);
  border-radius: 8px;
  background: rgba(0, 0, 0, 0.18);
}

.macro-grid span,
.macro-grid strong {
  display: block;
}

.macro-grid span {
  color: var(--ff-text-muted);
  font-size: 12px;
}

.macro-grid strong {
  margin-top: 5px;
  color: var(--ff-accent-cool);
  font-family: "Bahnschrift", "DIN Alternate", "Rajdhani", sans-serif;
  font-size: 22px;
  font-variant-numeric: tabular-nums;
}

.body-readouts {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
  margin-top: 28px;
}

.body-readouts div {
  min-height: 126px;
  padding: 16px;
  border: 1px solid rgba(243, 241, 232, 0.08);
  border-radius: 8px;
  background: rgba(0, 0, 0, 0.2);
}

.body-readouts span,
.body-readouts em {
  display: block;
}

.body-readouts em {
  margin-top: 8px;
}

.panel-link {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  margin-top: 18px;
  padding: 0 14px;
  background: rgba(184, 255, 44, 0.06);
}

.zone-map {
  position: relative;
  display: grid;
  grid-template-columns: 1.2fr 0.8fr;
  grid-template-rows: 78px 66px;
  gap: 8px;
  margin-top: 28px;
  padding: 10px;
  border: 1px solid rgba(243, 241, 232, 0.08);
  border-radius: 8px;
  background:
    linear-gradient(90deg, rgba(243, 241, 232, 0.04) 1px, transparent 1px),
    linear-gradient(rgba(243, 241, 232, 0.04) 1px, transparent 1px),
    rgba(0, 0, 0, 0.2);
  background-size: 22px 22px;
}

.zone {
  border: 1px solid rgba(243, 241, 232, 0.1);
  border-radius: 6px;
}

.zone-power {
  background: rgba(184, 255, 44, 0.42);
  box-shadow: 0 0 28px rgba(184, 255, 44, 0.22);
}

.zone-cardio {
  background: rgba(255, 106, 26, 0.58);
  box-shadow: 0 0 28px rgba(255, 106, 26, 0.28);
}

.zone-studio {
  background: rgba(246, 201, 69, 0.48);
}

.zone-free {
  background: rgba(49, 216, 200, 0.34);
}

.heat-footer {
  justify-content: space-between;
  gap: 12px;
  margin-top: 18px;
}

.heat-footer button {
  padding: 0 12px;
  color: #10120d;
  border-color: var(--ff-warning);
  background: var(--ff-warning);
  font-weight: 900;
}

.action-section {
  padding: 22px 0 18px;
}

.action-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 14px;
  margin-top: 16px;
}

.action-tile {
  position: relative;
  display: flex;
  min-height: 122px;
  flex-direction: column;
  align-items: flex-start;
  justify-content: flex-end;
  gap: 6px;
  padding: 16px;
  overflow: hidden;
  text-align: left;
}

.action-tile::before {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  width: 4px;
  height: 100%;
  background: var(--ff-accent-power);
  opacity: 0;
  transform: scaleY(0.2);
  transform-origin: bottom;
  transition: opacity 180ms cubic-bezier(0.16, 1, 0.3, 1), transform 180ms cubic-bezier(0.16, 1, 0.3, 1);
}

.action-tile:hover {
  border-color: rgba(184, 255, 44, 0.46);
  transform: translateY(-3px);
}

.action-tile:hover::before,
.action-tile:focus-visible::before {
  opacity: 1;
  transform: scaleY(1);
}

.action-tile svg {
  color: var(--ff-accent-power);
}

.action-tile span {
  color: var(--ff-text);
  font-size: 16px;
  font-weight: 800;
}

.action-tile em {
  color: var(--ff-text-muted);
  font-size: 13px;
  font-style: normal;
}

@keyframes forgeEnter {
  from {
    opacity: 0;
    transform: translateY(16px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@media (max-width: 980px) {
  .hero-content,
  .control-grid {
    grid-template-columns: 1fr;
  }

  .readout-strip {
    grid-template-columns: repeat(3, 1fr);
  }

  .action-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 640px) {
  .forge-home {
    padding: 12px;
  }

  .command-hero {
    min-height: auto;
  }

  .topbar {
    flex-direction: column;
    padding: 18px;
  }

  .profile-cluster {
    width: 100%;
  }

  .icon-command,
  .ghost-command {
    flex: 1;
    justify-content: center;
  }

  .hero-content {
    padding: 18px;
  }

  .mission-panel,
  .data-panel {
    padding: 16px;
  }

  .mission-panel h2 {
    font-size: 42px;
  }

  .mission-actions,
  .readout-strip,
  .macro-grid,
  .body-readouts,
  .action-grid {
    grid-template-columns: 1fr;
  }

  .mission-actions {
    display: grid;
  }

  .power-button,
  .line-button {
    justify-content: center;
    width: 100%;
  }

  .readout-cell {
    min-height: 96px;
  }

  .fuel-readout {
    flex-wrap: wrap;
  }

  .section-heading {
    align-items: flex-start;
    flex-direction: column;
    gap: 6px;
  }
}

@media (prefers-reduced-motion: reduce) {
  .forge-home *,
  .forge-home *::before,
  .forge-home *::after {
    animation-duration: 1ms !important;
    transition-duration: 1ms !important;
    scroll-behavior: auto !important;
  }
}
</style>
