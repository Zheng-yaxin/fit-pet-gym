<template>
  <div class="admin-page">
    <header>
      <div>
        <p>Training Logs</p>
        <h1>训练日志统计</h1>
      </div>
      <el-button @click="loadAll">刷新</el-button>
    </header>

    <section class="active-grid">
      <article v-for="item in activeCheckins" :key="item.id" class="active-card">
        <span>进行中训练</span>
        <strong>会员 {{ item.memberId }} · {{ formatElapsed(item.startTime) }}</strong>
        <p>计划 {{ item.planId || '未绑定' }} · 开始 {{ formatTime(item.startTime) }}</p>
      </article>
      <el-empty v-if="!activeCheckins.length" description="暂无进行中的训练打卡" />
    </section>

    <section class="review-grid">
      <article v-for="item in reviews" :key="item.memberId" class="review-card">
        <span>会员 {{ item.memberId }}</span>
        <strong>Lv.{{ item.level || 1 }} · {{ item.badgeTitle || '热身起步徽章' }}</strong>
        <p>{{ item.review || '暂无复盘' }}</p>
        <div class="review-meta">
          <b>本周 {{ item.weeklyMinutes || 0 }} min</b>
          <b>连续 {{ item.streakDays || 0 }} 天</b>
        </div>
      </article>
      <el-empty v-if="!reviews.length" description="暂无训练复盘数据" />
    </section>

    <el-table :data="list" class="table" border>
      <el-table-column prop="memberId" label="会员ID" width="100" />
      <el-table-column prop="trainingDate" label="日期" min-width="170" />
      <el-table-column prop="durationMinutes" label="时长" width="100" />
      <el-table-column prop="intensity" label="强度" width="100" />
      <el-table-column prop="caloriesBurned" label="热量" width="100" />
      <el-table-column prop="feeling" label="感受" min-width="140" />
      <el-table-column prop="remark" label="备注" min-width="180" show-overflow-tooltip />
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { getAdminActiveTrainingCheckins, getAdminTrainingLogs, getAdminTrainingReviews } from '@/api/training'

const list = ref<any[]>([])
const reviews = ref<any[]>([])
const activeCheckins = ref<any[]>([])

const loadAll = async () => {
  const [nextLogs, nextReviews, nextActiveCheckins] = await Promise.all([
    getAdminTrainingLogs(),
    getAdminTrainingReviews(),
    getAdminActiveTrainingCheckins()
  ])
  list.value = nextLogs as any[]
  reviews.value = nextReviews as any[]
  activeCheckins.value = nextActiveCheckins as any[]
}

const formatTime = (value?: string) => {
  if (!value) return '--'
  return value.slice(0, 16).replace('T', ' ')
}

const formatElapsed = (value?: string) => {
  if (!value) return '00:00'
  const start = new Date(value).getTime()
  if (Number.isNaN(start)) return '00:00'
  const minutes = Math.max(0, Math.floor((Date.now() - start) / 60000))
  const hours = Math.floor(minutes / 60)
  const rest = minutes % 60
  return `${String(hours).padStart(2, '0')}:${String(rest).padStart(2, '0')}`
}

onMounted(loadAll)
</script>

<style scoped>
.admin-page { padding: 32px; min-height: 100%; background: var(--ff-surface-raised); }
header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
header p { margin: 0 0 6px; color: #64748b; font-size: 12px; font-weight: 700; text-transform: uppercase; }
h1 { margin: 0; color: #1f2937; font-size: 24px; }
.active-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(240px, 1fr)); gap: 14px; margin-bottom: 20px; }
.active-card { border: 1px solid #fed7aa; border-radius: 8px; background: #fff7ed; padding: 16px; box-shadow: 0 12px 28px rgba(251, 146, 60, 0.12); }
.active-card span { color: #c2410c; font-size: 12px; font-weight: 700; }
.active-card strong { display: block; margin-top: 8px; color: #111827; font-size: 16px; }
.active-card p { margin: 8px 0 0; color: #7c2d12; line-height: 1.5; }
.review-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(240px, 1fr)); gap: 14px; margin-bottom: 20px; }
.review-card { border: 1px solid #e5e7eb; border-radius: 8px; background: #fff; padding: 16px; box-shadow: 0 12px 28px rgba(15, 23, 42, 0.06); }
.review-card span { color: #64748b; font-size: 12px; font-weight: 700; }
.review-card strong { display: block; margin-top: 8px; color: #111827; font-size: 16px; }
.review-card p { min-height: 42px; margin: 8px 0 12px; color: #475569; line-height: 1.5; }
.review-meta { display: flex; gap: 8px; flex-wrap: wrap; }
.review-meta b { border-radius: 999px; background: #f1f5f9; color: #334155; padding: 5px 10px; font-size: 12px; }
.table { border-radius: 8px; overflow: hidden; }
</style>
