<template>
  <div class="chart-container">
    <div ref="chartRef" class="chart-canvas"></div>
    <div v-if="!data.length" class="empty-state">
      暂无历史数据
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, watch, nextTick } from 'vue'
import * as echarts from 'echarts'
import { FORGEFIT_ECHARTS_THEME, forgefitChartTokens } from '@/utils/forgefitEcharts'
import type { HealthData } from '@/api/health'
import dayjs from 'dayjs'

const props = defineProps<{ data: HealthData[] }>()
const chartRef = ref<HTMLElement>()
let chart: echarts.ECharts | null = null
let resizeHandler: (() => void) | null = null

const initChart = () => {
  if (!chartRef.value) return
  chart = echarts.init(chartRef.value, FORGEFIT_ECHARTS_THEME)
  setOptions()
}

const setOptions = () => {
  if (!chart || !props.data.length) return

  const sortedData = [...props.data].sort((a, b) =>
    new Date(a.measureTime || '').getTime() - new Date(b.measureTime || '').getTime()
  )
  const dates = sortedData.map(item => dayjs(item.measureTime).format('MM-DD'))
  const weights = sortedData.map(item => item.weight)

  chart.setOption({
    backgroundColor: 'transparent',
    grid: { left: 0, right: 0, top: 10, bottom: 0, containLabel: true },
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(21, 23, 19, 0.96)',
      borderColor: forgefitChartTokens.border,
      textStyle: { color: forgefitChartTokens.text, fontSize: 13 },
      extraCssText: 'box-shadow: 0 24px 80px rgba(0,0,0,0.42); border-radius: 8px;',
      formatter: (params: any) => {
        const item = params[0]
        return `<div style="font-weight:700;margin-bottom:4px;">${item.name}</div>
          <div style="color:${forgefitChartTokens.secondary};">体重: <span style="font-weight:800;color:${forgefitChartTokens.power};">${item.value}</span> kg</div>`
      }
    },
    xAxis: {
      type: 'category',
      data: dates,
      axisLine: { show: false },
      axisTick: { show: false },
      axisLabel: { color: forgefitChartTokens.muted, fontSize: 11, margin: 12 }
    },
    yAxis: {
      type: 'value',
      scale: true,
      splitLine: { show: true, lineStyle: { type: 'dashed', color: 'rgba(243, 241, 232, 0.08)', width: 1 } },
      axisLabel: { show: false }
    },
    series: [
      {
        data: weights,
        type: 'line',
        smooth: true,
        showSymbol: false,
        symbolSize: 8,
        lineStyle: { width: 4, color: forgefitChartTokens.power, cap: 'round' },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(184, 255, 44, 0.18)' },
            { offset: 1, color: 'rgba(184, 255, 44, 0)' }
          ])
        },
        itemStyle: {
          color: forgefitChartTokens.power,
          borderWidth: 2,
          borderColor: forgefitChartTokens.surface
        }
      }
    ]
  })
}

watch(() => props.data, setOptions, { deep: true })

onMounted(() => {
  nextTick(() => {
    initChart()
    resizeHandler = () => chart?.resize()
    window.addEventListener('resize', resizeHandler)
  })
})

onUnmounted(() => {
  if (resizeHandler) window.removeEventListener('resize', resizeHandler)
  chart?.dispose()
})
</script>

<style scoped>
.chart-container {
  width: 100%;
  height: 100%;
  min-height: 220px;
  position: relative;
}

.chart-canvas {
  width: 100%;
  height: 100%;
  min-height: 220px;
}

.empty-state {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--ff-text-muted, #71766c);
  font-size: 13px;
  font-weight: 600;
}
</style>
