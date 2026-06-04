<template>
  <div class="liquid-rings-card">

    <div class="status-header">
      <div class="target-info">
        <span class="label">目标</span>
        <span class="num">{{ Math.round(summary.recommendCalories) }}</span>
        <span class="unit">KCAL</span>
      </div>

      <div class="status-badge" :class="isOver ? 'over' : 'good'">
        <span v-if="isOver">⚠️ 已超标 {{ Math.round(summary.totalCalories - summary.recommendCalories) }}</span>
        <span v-else>🔥 剩余 {{ Math.round(summary.recommendCalories - summary.totalCalories) }}</span>
      </div>
    </div>

    <div class="rings-wrapper">
      <svg class="rings-svg bg-tracks" viewBox="0 0 100 100">
        <circle cx="50" cy="50" r="40" class="track" />
        <circle cx="50" cy="50" r="28" class="track" />
        <circle cx="50" cy="50" r="16" class="track" />
      </svg>

      <svg class="rings-svg progress-rings" viewBox="0 0 100 100">
        <circle
            cx="50" cy="50" r="40"
            class="ring ring-pastel-red"
            :stroke-dasharray="`${caloriesPercent * 2.51}, 251`"
        />
        <circle
            cx="50" cy="50" r="28"
            class="ring ring-pastel-green"
            :stroke-dasharray="`${proteinPercent * 1.76}, 176`"
        />
        <circle
            cx="50" cy="50" r="16"
            class="ring ring-pastel-blue"
            :stroke-dasharray="`${carbPercent * 1.00}, 100`"
        />
      </svg>

      <div class="center-data">
        <span class="value" :class="{'text-red': isOver}">{{ Math.round(summary.totalCalories) }}</span>
        <span class="unit">已摄入</span>
      </div>
    </div>

    <div class="legend-grid">
      <div class="glass-pill pill-red">
        <div class="dot"></div>
        <div class="info">
          <span class="val">{{ Math.round(summary.totalCalories) }}</span>
          <span class="label">热量</span>
        </div>
      </div>
      <div class="glass-pill pill-green">
        <div class="dot"></div>
        <div class="info">
          <span class="val">{{ Math.round(summary.totalProtein) }}g</span>
          <span class="label">蛋白质</span>
        </div>
      </div>
      <div class="glass-pill pill-blue">
        <div class="dot"></div>
        <div class="info">
          <span class="val">{{ Math.round(summary.totalCarb) }}g</span>
          <span class="label">碳水</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { DietSummaryVO } from '@/api/health'

const props = defineProps<{ summary: DietSummaryVO }>()

const calc = (cur: number, target: number) => {
  if (!target || target === 0) return 0
  const p = (cur / target) * 100
  return p > 100 ? 100 : p
}

const caloriesPercent = computed(() => calc(props.summary.totalCalories, props.summary.recommendCalories))
const proteinPercent = computed(() => calc(props.summary.totalProtein, props.summary.recommendProtein))
const carbPercent = computed(() => calc(props.summary.totalCarb, props.summary.recommendCarb))

// 计算是否超标
const isOver = computed(() => props.summary.totalCalories > props.summary.recommendCalories)
</script>

<style scoped lang="scss">
.liquid-rings-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 16px 0 24px;
  background: transparent;
  width: 100%;
}

/* --- 新增部分样式 --- */
.status-header {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  margin-bottom: 24px;
}

.target-info {
  font-size: 13px;
  color: #94A3B8;
  font-weight: 600;
  display: flex;
  align-items: baseline;
  gap: 4px;

  .num {
    font-family: "SF Mono", monospace;
    font-weight: 700;
    color: #64748B;
    font-size: 15px;
  }
  .unit { font-size: 10px; font-weight: 700; opacity: 0.8; }
}

.status-badge {
  font-size: 12px;
  font-weight: 700;
  padding: 4px 12px;
  border-radius: 99px;
  transition: all 0.3s;

  &.good {
    background: #ECFDF5; /* Emerald-50 */
    color: #10B981;      /* Emerald-500 */
    border: 1px solid #D1FAE5;
  }

  &.over {
    background: #FEF2F2; /* Red-50 */
    color: #EF4444;      /* Red-500 */
    border: 1px solid #FEE2E2;
    animation: pulse-red 2s infinite;
  }
}

@keyframes pulse-red {
  0% { box-shadow: 0 0 0 0 rgba(239, 68, 68, 0.2); }
  70% { box-shadow: 0 0 0 6px rgba(239, 68, 68, 0); }
  100% { box-shadow: 0 0 0 0 rgba(239, 68, 68, 0); }
}

/* --- 原有样式微调 --- */
.rings-wrapper {
  position: relative;
  width: 180px;
  height: 180px;
}

.rings-svg {
  position: absolute;
  top: 0; left: 0;
  width: 100%; height: 100%;
  transform: rotate(-90deg);

  circle {
    fill: none;
    stroke-width: 8;
    stroke-linecap: round;
  }
}

.bg-tracks .track {
  stroke: #F1F5F9; /* Slate-100 */
}

/* 莫兰迪/粉蜡笔色系 */
.ring {
  transition: stroke-dasharray 1.2s cubic-bezier(0.23, 1, 0.32, 1);
}

.ring-pastel-red { stroke: #FDA4AF; }
.ring-pastel-green { stroke: #6EE7B7; }
.ring-pastel-blue { stroke: #7DD3FC; }

.center-data {
  position: absolute;
  inset: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  z-index: 10;
  pointer-events: none;

  .value {
    font-family: system-ui, sans-serif;
    font-size: 36px;
    font-weight: 800;
    color: #475569; /* Slate-600 */
    transition: color 0.3s;

    &.text-red { color: #F87171; } /* Red-400 */
  }
  .unit {
    font-size: 11px;
    font-weight: 700;
    color: #94A3B8;
    margin-top: 2px;
  }
}

.legend-grid {
  display: flex;
  justify-content: center;
  gap: 16px;
  margin-top: 24px;
  width: 100%;
}

.glass-pill {
  flex: 0 0 auto;
  min-width: 60px;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 10px 12px;
  border-radius: 16px;
  background: var(--ff-surface-raised);
  border: 1px solid #F1F5F9;

  .dot {
    width: 8px;
    height: 8px;
    border-radius: 50%;
    margin-bottom: 6px;
  }

  &.pill-red .dot { background: #FDA4AF; }
  &.pill-green .dot { background: #6EE7B7; }
  &.pill-blue .dot { background: #7DD3FC; }

  .val {
    font-weight: 700;
    font-size: 14px;
    color: #64748B;
  }

  .label {
    font-size: 10px;
    color: #94A3B8;
    margin-top: 2px;
  }
}
</style>