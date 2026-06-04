import * as echarts from 'echarts'

export const FORGEFIT_ECHARTS_THEME = 'forgefit'

export const forgefitChartTokens = {
  canvas: '#0b0c0a',
  surface: '#151713',
  raised: '#20231d',
  border: '#343a30',
  text: '#f3f1e8',
  secondary: '#a7a99e',
  muted: '#71766c',
  power: '#b8ff2c',
  heat: '#ff6a1a',
  cool: '#31d8c8',
  danger: '#ff3b30'
}

echarts.registerTheme(FORGEFIT_ECHARTS_THEME, {
  color: [
    forgefitChartTokens.power,
    forgefitChartTokens.heat,
    forgefitChartTokens.cool,
    forgefitChartTokens.danger,
    '#6eeb83',
    '#f6c945'
  ],
  backgroundColor: 'transparent',
  textStyle: {
    color: forgefitChartTokens.secondary,
    fontFamily: 'Bahnschrift, DIN Alternate, Rajdhani, Microsoft YaHei UI, sans-serif'
  },
  title: {
    textStyle: { color: forgefitChartTokens.text },
    subtextStyle: { color: forgefitChartTokens.muted }
  },
  legend: {
    textStyle: { color: forgefitChartTokens.secondary }
  },
  tooltip: {
    backgroundColor: 'rgba(21, 23, 19, 0.96)',
    borderColor: forgefitChartTokens.border,
    textStyle: { color: forgefitChartTokens.text },
    axisPointer: {
      lineStyle: { color: forgefitChartTokens.power },
      crossStyle: { color: forgefitChartTokens.power }
    }
  },
  grid: {
    borderColor: forgefitChartTokens.border
  },
  categoryAxis: {
    axisLine: { lineStyle: { color: forgefitChartTokens.border } },
    axisTick: { lineStyle: { color: forgefitChartTokens.border } },
    axisLabel: { color: forgefitChartTokens.muted },
    splitLine: { lineStyle: { color: 'rgba(243, 241, 232, 0.07)' } }
  },
  valueAxis: {
    axisLine: { lineStyle: { color: forgefitChartTokens.border } },
    axisTick: { lineStyle: { color: forgefitChartTokens.border } },
    axisLabel: { color: forgefitChartTokens.muted },
    splitLine: { lineStyle: { color: 'rgba(243, 241, 232, 0.07)', type: 'dashed' } }
  }
})

