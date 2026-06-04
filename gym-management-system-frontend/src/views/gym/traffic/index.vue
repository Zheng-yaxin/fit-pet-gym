<template>
  <div class="feature-page">
    <section class="feature-hero">
      <div>
        <p class="eyebrow">Traffic Heatmap</p>
        <h1>健身房人流热力图</h1>
        <p class="summary">查看当前区域拥挤度，帮助会员避开高峰训练。</p>
      </div>
      <el-button @click="loadData">刷新</el-button>
    </section>

    <div class="traffic-grid">
      <el-card v-for="item in traffic" :key="item.id" shadow="never" class="traffic-card">
        <div class="heat" :style="{ width: `${Math.min(item.heatLevel || 0, 100)}%` }"></div>
        <h3>区域 {{ item.areaId || '-' }}</h3>
        <p>{{ item.currentCount || 0 }} / {{ item.capacity || 0 }} 人</p>
        <strong>{{ item.heatLevel || 0 }}%</strong>
      </el-card>
    </div>
    <el-empty v-if="traffic.length === 0" description="暂无人流快照" />
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { getCurrentTraffic } from '@/api/traffic'

const traffic = ref<any[]>([])

const loadData = async () => {
  traffic.value = (await getCurrentTraffic()) as any[]
}

onMounted(loadData)
</script>

<style scoped>
.feature-page { min-height: 100%; padding: 32px; background: #f7f8fb; color: #334155; }
.feature-hero { display: flex; justify-content: space-between; gap: 24px; align-items: center; margin-bottom: 24px; }
.eyebrow { margin: 0 0 8px; color: #64748b; font-size: 12px; font-weight: 700; letter-spacing: .08em; text-transform: uppercase; }
h1 { margin: 0; font-size: 28px; color: #1f2937; }
.summary { margin: 10px 0 0; color: #64748b; }
.traffic-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(220px, 1fr)); gap: 16px; }
.traffic-card { position: relative; overflow: hidden; border-radius: 8px; border: 1px solid #e5e7eb; }
.heat { position: absolute; inset: 0 auto 0 0; background: linear-gradient(90deg, rgba(16,185,129,.18), rgba(245,158,11,.2), rgba(239,68,68,.18)); }
.traffic-card h3, .traffic-card p, .traffic-card strong { position: relative; }
.traffic-card h3 { margin: 0 0 8px; }
.traffic-card p { margin: 0 0 10px; color: #64748b; }
</style>
