<template>
  <div class="admin-page">
    <header>
      <div>
        <p>Traffic Admin</p>
        <h1>人流热力管理</h1>
      </div>
      <div class="actions">
        <el-button @click="createArea">新增示例区域</el-button>
        <el-button type="primary" @click="createSnapshot">新增快照</el-button>
      </div>
    </header>

    <section class="recommend-grid">
      <article v-for="item in recommendations" :key="item.areaId" class="recommend-card">
        <span>Priority {{ item.priority || 2 }} · {{ item.statusLabel || 'Live' }}</span>
        <h3>{{ item.areaName || 'Training area' }}</h3>
        <p>{{ item.reason || 'Traffic data is ready for routing.' }}</p>
        <div class="recommend-meta">
          <b>{{ item.score || 0 }} score</b>
          <b>{{ item.occupancyPercent || 0 }}% full</b>
          <b>{{ item.currentCount || 0 }}/{{ item.capacity || 0 }}</b>
        </div>
        <em>{{ item.action || 'Keep this area available for the next member wave.' }}</em>
      </article>
    </section>

    <el-table :data="areas" class="table" border>
      <el-table-column prop="name" label="区域" />
      <el-table-column prop="capacity" label="容量" />
      <el-table-column prop="location" label="位置" />
      <el-table-column prop="status" label="状态" />
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { addGymArea, addTrafficSnapshot, getGymAreas, getTrafficRecommendations } from '@/api/traffic'

const areas = ref<any[]>([])
const recommendations = ref<any[]>([])

const loadAreas = async () => {
  const [nextAreas, nextRecommendations] = await Promise.all([
    getGymAreas(),
    getTrafficRecommendations('general', 4)
  ])
  areas.value = nextAreas as any[]
  recommendations.value = nextRecommendations as any[]
}

const createArea = async () => {
  await addGymArea({ name: '自由力量区', capacity: 30, location: '一层', status: '0' })
  ElMessage.success('区域已新增')
  await loadAreas()
}

const createSnapshot = async () => {
  const area = areas.value[0]
  await addTrafficSnapshot({ areaId: area?.id, currentCount: 12, capacity: area?.capacity || 30, snapshotTime: new Date().toISOString() })
  ElMessage.success('快照已新增')
}

onMounted(loadAreas)
</script>

<style scoped>
.admin-page { padding: 32px; min-height: 100%; background: var(--ff-surface-raised); }
header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
header p { margin: 0 0 6px; color: #64748b; font-size: 12px; font-weight: 700; text-transform: uppercase; }
h1 { margin: 0; color: #1f2937; font-size: 24px; }
.actions { display: flex; gap: 12px; }
.table { border-radius: 8px; overflow: hidden; }
.recommend-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(220px, 1fr)); gap: 14px; margin-bottom: 20px; }
.recommend-card { border: 1px solid #e5e7eb; border-radius: 8px; background: #fff; padding: 16px; box-shadow: 0 12px 28px rgba(15, 23, 42, 0.06); }
.recommend-card span { color: #64748b; font-size: 12px; font-weight: 800; text-transform: uppercase; }
.recommend-card h3 { margin: 8px 0 6px; color: #111827; }
.recommend-card p { min-height: 42px; margin: 0 0 10px; color: #475569; line-height: 1.5; }
.recommend-card em { display: block; margin-top: 10px; color: #0f766e; font-size: 12px; font-style: normal; font-weight: 700; }
.recommend-meta { display: flex; gap: 8px; flex-wrap: wrap; }
.recommend-meta b { border-radius: 999px; background: #f1f5f9; color: #334155; padding: 5px 10px; font-size: 12px; }
</style>
