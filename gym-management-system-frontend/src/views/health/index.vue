<template>
  <div class="health-page">

    <header class="navbar sticky top-0 z-40">
      <div class="nav-content">
        <div class="flex items-center gap-3">
          <button @click="router.push('/home')" class="back-btn">
            <ArrowLeft :size="20" />
          </button>
          <h1 class="nav-title">健康数据</h1>
        </div>

        <div class="status-badge" :class="{ 'updating': isUpdating }">
          <div class="dot"></div>
          <span>{{ isUpdating ? '更新中...' : '已同步' }}</span>
        </div>
      </div>
    </header>

    <main class="content-grid animate-fade-in-up">

      <div class="main-column">

        <div class="metrics-grid">
          <AppleCard clickable @click="openHealthModal" class="metric-card weight-theme">
            <div class="card-header-center">
              <div class="icon-circle pastel-blue"><Scale :size="20" /></div>
              <span class="header-text">身体指标</span>
            </div>

            <div class="card-body-center">
              <div class="value-group">
                <span class="big-num">{{ latestHealth.weight !== undefined && latestHealth.weight !== null ? latestHealth.weight : '--' }}</span>
                <span class="unit">kg</span>
              </div>

              <div class="stats-row">
                <div class="bmi-pill">
                  <span class="label">BMI {{ displayBMI }}</span>
                  <span class="status" :class="bmiStatus.colorClass">{{ bmiStatus.text }}</span>
                </div>
                <div class="height-pill" v-if="latestHealth.height">
                  <span class="label">身高 {{ latestHealth.height }}cm</span>
                </div>
              </div>

              <div v-if="latestHealth.measureTime" class="last-update">
                上次记录: {{ formatLastUpdate(latestHealth.measureTime) }}
              </div>
            </div>
          </AppleCard>

          <AppleCard clickable @click="openDietModal" class="metric-card rings-theme">
            <div class="absolute top-4 right-4 text-gray-300"><ChevronRight :size="18" /></div>
            <div class="card-header-center mb-2">
              <div class="icon-circle pastel-orange"><Activity :size="20" /></div>
              <span class="header-text">今日摄入</span>
            </div>
            <DietRings :summary="dietSummary" />
          </AppleCard>
        </div>

        <div class="actions-row">
          <button class="action-item" @click="openDietModal">
            <div class="circle pastel-orange-bg"><Plus :size="22" /></div>
            <span class="label">记饮食</span>
          </button>

          <button class="action-item" @click="triggerAiScan">
            <div class="circle pastel-indigo-bg"><Sparkles :size="22" /></div>
            <span class="label">AI识别</span>
            <input type="file" ref="aiFileInput" class="hidden" accept="image/*" @change="handleAiFileChange" />
          </button>

          <button class="action-item" @click="openHealthModal">
            <div class="circle pastel-blue-bg"><Activity :size="22" /></div>
            <span class="label">身体打卡</span>
          </button>

          <button class="action-item" @click="triggerUpload">
            <div class="circle pastel-purple-bg"><Camera :size="22" /></div>
            <span class="label">拍体态</span>
            <input type="file" ref="fileInput" class="hidden" accept="image/*" @change="handleFileUpload" />
          </button>

          <button class="action-item" @click="showBodyGallery = true">
            <div class="circle pastel-pink-bg"><Camera :size="22" /></div>
            <span class="label">体态相册</span>
          </button>
        </div>

        <AppleCard class="chart-card">
          <div class="relative flex justify-center items-center border-b border-gray-50 pb-4 mb-4">
            <h3 class="font-semibold text-slate-600">体重趋势</h3>
            <span class="absolute right-0 text-xs font-medium text-slate-400 bg-slate-50 px-3 py-1 rounded-full">30天</span>
          </div>
          <div class="h-[250px]">
            <HealthChart :data="historyData" />
          </div>
        </AppleCard>
      </div>

      <div class="side-column flex flex-col h-full">

        <AppleCard class="flex flex-col flex-1 min-h-[380px]">
          <div class="relative flex justify-center items-center mb-6">
            <h4 class="font-bold text-lg text-slate-600">今日记录</h4>
            <span class="absolute right-0 text-xs font-bold text-slate-400 bg-slate-50 px-2 py-1 rounded-md">
              {{ Math.round(dietSummary.totalCalories) }} KCAL
            </span>
          </div>

          <div class="flex-1 overflow-y-auto custom-scrollbar space-y-3">
            <div v-for="item in dietSummary.details" :key="item.id" class="list-row group">
              <div class="row-icon-box">{{ getMealIcon(item.mealType) }}</div>
              <div class="row-content">
                <p class="row-title">{{ item.foodName }}</p>
                <p class="row-sub">{{ item.amount }}g · {{ Math.round(item.calories) }} kcal</p>
              </div>
              <button @click="handleDeleteDiet(item.id)" class="delete-btn">
                <Trash2 :size="16" />
              </button>
            </div>

            <div v-if="!dietSummary.details?.length" class="empty-state">
              <span class="text-4xl mb-3 opacity-50">🍽️</span>
              <p>暂无记录</p>
            </div>
          </div>
        </AppleCard>

        <AppleCard class="mt-6 shrink-0">
          <div class="relative flex justify-center items-center mb-3">
            <h4 class="font-bold text-slate-600 flex items-center gap-2 text-xs uppercase tracking-wide">
              <Sparkles :size="14" class="text-emerald-500" />
              营养建议
            </h4>
          </div>

          <ul class="text-sm text-slate-600 space-y-2.5">
            <li v-for="(tip, idx) in dietSummary.suggestions" :key="idx" class="flex gap-2.5 items-start">
              <span class="block min-w-[6px] h-1.5 rounded-full bg-emerald-300 mt-1.5"></span>
              <span class="leading-relaxed">{{ tip }}</span>
            </li>
            <li v-if="!dietSummary.suggestions?.length" class="italic opacity-60 text-xs">保持均衡饮食，继续加油！</li>
          </ul>
        </AppleCard>
      </div>
    </main>

    <DietRecordSheet v-model="showDietModal" @success="fetchData" />

    <BodyGallery v-model="showBodyGallery" :images="bodyImages" @upload="handleGalleryUpload" @delete="handleGalleryDelete" />

    <Transition name="fade">
      <div v-if="showHealthModal" class="fixed inset-0 z-50 flex items-center justify-center p-6">
        <div class="absolute inset-0 bg-slate-900/20 backdrop-blur-sm" @click="showHealthModal = false"></div>
        <div class="bg-white rounded-[32px] w-full max-w-sm p-8 relative z-10 shadow-xl animate-bounce-in">
          <h3 class="text-xl font-bold mb-8 text-center text-slate-800">更新身体数据</h3>
          <div class="space-y-6">
            <div>
              <label class="text-xs font-bold text-slate-400 uppercase tracking-wide block mb-2">体重 (kg)</label>
              <input v-model.number="formHealth.weight" type="number" class="w-full text-4xl font-bold text-slate-700 border-b-2 border-slate-100 py-2 outline-none focus:border-blue-300 transition-colors text-center" placeholder="0.0" />
            </div>
            <div class="grid grid-cols-2 gap-6">
              <div>
                <label class="text-xs font-bold text-slate-400 uppercase tracking-wide block mb-2">身高 (cm)</label>
                <input v-model.number="formHealth.height" type="number" class="w-full text-xl font-bold text-slate-600 border-b-2 border-slate-100 py-2 outline-none focus:border-blue-300 text-center" />
              </div>
              <div>
                <label class="text-xs font-bold text-slate-400 uppercase tracking-wide block mb-2">体脂 (%)</label>
                <input v-model.number="formHealth.bodyFatRate" type="number" class="w-full text-xl font-bold text-slate-600 border-b-2 border-slate-100 py-2 outline-none focus:border-blue-300 text-center" />
              </div>
            </div>
            <button @click="submitHealth" class="w-full py-4 bg-slate-800 text-white rounded-2xl font-bold mt-6 shadow-lg shadow-slate-200 active:scale-95 transition-transform">保存数据</button>
          </div>
        </div>
      </div>
    </Transition>

    <Transition name="fade">
      <div v-if="showAiModal" class="fixed inset-0 z-[60] flex items-center justify-center p-6">
        <div class="absolute inset-0 bg-slate-900/30 backdrop-blur-md" @click="showAiModal = false"></div>
        <div class="bg-white rounded-[32px] w-full max-w-md overflow-hidden z-10 animate-bounce-in shadow-2xl">
          <div class="h-56 relative bg-slate-100">
            <img :src="aiPreviewUrl" class="w-full h-full object-cover" />
            <div class="absolute bottom-0 left-0 p-6 bg-gradient-to-t from-black/50 to-transparent w-full">
              <h3 class="text-white font-bold text-2xl">{{ aiResult.name }}</h3>
            </div>
          </div>
          <div class="p-8">
            <div class="mb-6">
              <div class="flex justify-between items-center mb-2">
                <span class="text-slate-500 font-medium">预估重量</span>
                <span class="font-bold text-3xl text-slate-800">{{ aiResult.estimatedWeight }}g</span>
              </div>
              <input type="range" v-model.number="aiResult.estimatedWeight" min="10" max="1000" step="10" class="w-full accent-blue-400 h-2 bg-slate-100 rounded-lg appearance-none cursor-pointer" />
            </div>

            <div class="bg-slate-50 rounded-2xl p-4 mb-6">
              <h4 class="text-sm font-bold text-slate-600 mb-3">营养成分 (每100g)</h4>
              <div class="grid grid-cols-2 gap-3">
                <div class="bg-white rounded-xl p-3">
                  <div class="text-xs text-slate-400 mb-1">热量</div>
                  <div class="text-lg font-bold text-orange-500">{{ aiResult.caloriesPer100g }} kcal</div>
                </div>
                <div class="bg-white rounded-xl p-3">
                  <div class="text-xs text-slate-400 mb-1">蛋白质</div>
                  <div class="text-lg font-bold text-blue-500">{{ aiResult.proteinPer100g || 0 }}g</div>
                </div>
                <div class="bg-white rounded-xl p-3">
                  <div class="text-xs text-slate-400 mb-1">脂肪</div>
                  <div class="text-lg font-bold text-yellow-500">{{ aiResult.fatPer100g || 0 }}g</div>
                </div>
                <div class="bg-white rounded-xl p-3">
                  <div class="text-xs text-slate-400 mb-1">碳水</div>
                  <div class="text-lg font-bold text-green-500">{{ aiResult.carbPer100g || 0 }}g</div>
                </div>
              </div>
            </div>

            <div class="flex gap-4">
              <button @click="showAiModal = false" class="flex-1 py-3.5 bg-slate-100 text-slate-600 rounded-2xl font-semibold hover:bg-slate-200 transition-colors">取消</button>
              <button @click="submitAiResult" class="flex-1 py-3.5 bg-blue-400 text-white rounded-2xl font-semibold shadow-lg shadow-blue-100 hover:bg-blue-500 transition-colors">添加到饮食</button>
            </div>
          </div>
        </div>
      </div>
    </Transition>

  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import dayjs from 'dayjs'
import 'dayjs/locale/zh-cn'
import {
  Activity, Scale, Plus, Sparkles, Camera, ArrowLeft, ChevronRight,
  Trash2
} from 'lucide-vue-next'
import AppleCard from '@/components/ui/AppleCard.vue'
import DietRings from './components/DietRings.vue'
import HealthChart from './components/HealthChart.vue'
import DietRecordSheet from './components/DietRecordSheet.vue'
import BodyGallery from './components/BodyGallery.vue'
import {
  getLatestHealthData,
  getHealthDataHistory,
  getDietSummary,
  getBodyImageHistory,
  deleteDietLog,
  saveHealthData,
  analyzeFoodImage,
  recordDiet,
  uploadBodyImageFile,
  saveBodyImageRecord,
  deleteBodyImage,
  addCustomFood,
  getFoodList
} from '@/api/health'
import type { HealthData, DietSummaryVO, BodyImage, FoodAnalysisVO } from '@/api/health'
import { ElMessage } from 'element-plus'

const router = useRouter()
dayjs.locale('zh-cn')

// --- 状态变量 ---
const isUpdating = ref(false)
const currentDate = ref(dayjs().format('YYYY-MM-DD'))
const latestHealth = ref<HealthData>({})
const historyData = ref<HealthData[]>([])
const dietSummary = ref<DietSummaryVO>({
  date: currentDate.value,
  totalCalories: 0,
  totalProtein: 0,
  totalFat: 0,
  totalCarb: 0,
  recommendCalories: 2000,
  recommendProtein: 150,
  recommendFat: 60,
  recommendCarb: 250,
  details: [],
  suggestions: []
})
const bodyImages = ref<BodyImage[]>([])
const showDietModal = ref(false)
const showHealthModal = ref(false)
const showBodyGallery = ref(false)
const showAiModal = ref(false)
const aiPreviewUrl = ref('')
const aiResult = reactive<FoodAnalysisVO>({
  name: '',
  estimatedWeight: 200,
  caloriesPer100g: 0,
  proteinPer100g: 0,
  fatPer100g: 0,
  carbPer100g: 0,
  analysis: ''
})
const formHealth = reactive<HealthData>({ weight: undefined, height: undefined, bodyFatRate: undefined })
const fileInput = ref<HTMLInputElement|null>(null)
const aiFileInput = ref<HTMLInputElement|null>(null)

// --- 计算属性: BMI 逻辑 ---
const displayBMI = computed(() => {
  const { bmi, weight, height } = latestHealth.value
  if (bmi) return bmi
  if (weight && height) {
    const h = height / 100
    return (weight / (h * h)).toFixed(1)
  }
  return '--'
})

const bmiStatus = computed(() => {
  const bmiVal = parseFloat(displayBMI.value as string)
  if (isNaN(bmiVal)) return { text: '未设置', colorClass: 'text-slate-400 bg-slate-100' }

  if (bmiVal < 18.5) return { text: '偏瘦', colorClass: 'text-blue-500 bg-blue-50' }
  if (bmiVal < 24) return { text: '标准', colorClass: 'text-emerald-500 bg-emerald-50' }
  if (bmiVal < 28) return { text: '超重', colorClass: 'text-orange-500 bg-orange-50' }
  return { text: '肥胖', colorClass: 'text-red-500 bg-red-50' }
})

// --- 辅助函数 ---
const getMealIcon = (type: number) => ({1:'☕️',2:'🍱',3:'🥗',4:'🍎'}[type] || '🍽️')

const formatLastUpdate = (time: string) => {
  const now = dayjs()
  const measureTime = dayjs(time)
  const diffDays = now.diff(measureTime, 'day')

  if (diffDays === 0) {
    return '今天 ' + measureTime.format('HH:mm')
  } else if (diffDays === 1) {
    return '昨天 ' + measureTime.format('HH:mm')
  } else if (diffDays < 7) {
    return diffDays + '天前'
  } else {
    return measureTime.format('MM-DD')
  }
}

// --- 数据加载 ---
const fetchData = async () => {
  isUpdating.value = true
  try {
    const [latest, history, diet, images] = await Promise.all([
      getLatestHealthData(),
      getHealthDataHistory(),
      getDietSummary(currentDate.value),
      getBodyImageHistory()
    ])
    console.log('Latest health data:', latest)
    console.log('History data:', history)

    // 如果latest为null但history有数据，使用history的第一条
    if (!latest && history && history.length > 0) {
      latestHealth.value = history[0]
    } else {
      latestHealth.value = latest || {}
    }

    historyData.value = history || []
    dietSummary.value = diet || dietSummary.value
    bodyImages.value = images || []
  } catch(e) {
    console.error('Fetch data error:', e)
  } finally {
    setTimeout(() => isUpdating.value = false, 800)
  }
}

// --- 交互 Handler ---
const openHealthModal = () => {
  Object.assign(formHealth, latestHealth.value);
  showHealthModal.value = true
}

const openDietModal = () => showDietModal.value = true
const triggerUpload = () => fileInput.value?.click()
const triggerAiScan = () => aiFileInput.value?.click()

// 1. 保存身体数据 (包含BMI自动计算入库)
const submitHealth = async () => {
  try {
    let calculatedBmi = formHealth.bmi
    if (formHealth.weight && formHealth.height) {
      const h = formHealth.height / 100
      calculatedBmi = parseFloat((formHealth.weight / (h * h)).toFixed(1))
    }

    await saveHealthData({
      ...formHealth,
      bmi: calculatedBmi,
      measureTime: dayjs().format('YYYY-MM-DD HH:mm:ss')
    })
    showHealthModal.value = false
    ElMessage.success('保存成功')
    fetchData()
  } catch(e) {
    ElMessage.error('保存失败')
  }
}

// 2. 删除饮食记录
const handleDeleteDiet = async (id: number) => {
  try {
    await deleteDietLog(id)
    ElMessage.success('删除成功')
    fetchData()
  } catch(e) {
    ElMessage.error('删除失败')
  }
}

// 3. 上传体态照片
const handleFileUpload = async (e: Event) => {
  const files = (e.target as HTMLInputElement).files
  if (!files || files.length === 0) return

  try {
    const file = files[0]
    const imageUrl = await uploadBodyImageFile(file)
    await saveBodyImageRecord({
      imageUrl,
      recordTime: dayjs().format('YYYY-MM-DD HH:mm:ss')
    })
    ElMessage.success('上传成功')
    fetchData()
    if (fileInput.value) fileInput.value.value = ''
  } catch(e) {
    ElMessage.error('上传失败')
  }
}

// 4. AI 识别
const handleAiFileChange = async (e: Event) => {
  const files = (e.target as HTMLInputElement).files
  if (!files || files.length === 0) return

  try {
    const file = files[0]
    aiPreviewUrl.value = URL.createObjectURL(file)

    ElMessage.info('正在识别中...')
    const result = await analyzeFoodImage(file)

    Object.assign(aiResult, result)
    showAiModal.value = true

    if (aiFileInput.value) aiFileInput.value.value = ''
  } catch(e) {
    ElMessage.error('识别失败，请重试')
    if (aiFileInput.value) aiFileInput.value.value = ''
  }
}

// 5. 提交 AI 识别结果
const submitAiResult = async () => {
  try {
    // 自动创建自定义食物
    const foodData = {
      name: aiResult.name,
      calories: aiResult.caloriesPer100g,
      protein: aiResult.proteinPer100g || 0,
      fat: aiResult.fatPer100g || 0,
      carbohydrate: aiResult.carbPer100g || 0
    }

    await addCustomFood(foodData)

    // 获取食物 ID 并记录
    const foods = await getFoodList(aiResult.name)
    const food = foods.find(f => f.name === aiResult.name)

    if (!food) {
      ElMessage.error('食物创建失败')
      return
    }

    // 获取当前时间段
    const hour = dayjs().hour()
    let mealType = 1
    if (hour >= 10 && hour < 14) mealType = 2
    else if (hour >= 14 && hour < 18) mealType = 3 // 这里的判断仅供参考，可复用getCurrentMealType
    else if (hour >= 18) mealType = 3
    else mealType = 1

    await recordDiet({
      foodId: food.id!,
      amount: aiResult.estimatedWeight,
      mealType: mealType,
      eatDate: dayjs().format('YYYY-MM-DD')
    })

    showAiModal.value = false
    ElMessage.success('记录成功')
    fetchData()
  } catch(e) {
    console.error(e)
    ElMessage.error('保存失败')
  }
}

// 6. 相册组件回调
const handleGalleryUpload = async (file: File) => {
  try {
    const imageUrl = await uploadBodyImageFile(file)
    await saveBodyImageRecord({
      imageUrl,
      recordTime: dayjs().format('YYYY-MM-DD HH:mm:ss')
    })
    ElMessage.success('上传成功')
    fetchData()
  } catch(e) {
    ElMessage.error('上传失败')
  }
}

const handleGalleryDelete = async (id: number) => {
  try {
    await deleteBodyImage(id)
    ElMessage.success('删除成功')
    fetchData()
  } catch(e) {
    ElMessage.error('删除失败')
  }
}

onMounted(() => fetchData())
</script>

<style scoped lang="scss">
.health-page {
  min-height: 100vh;
  background-color: var(--ff-surface-raised); /* Slate-50: 极浅的灰冷色 */
  padding-bottom: 60px;
  font-family: var(--ff-font-ui);
}

/* 导航栏：极简白风格 */
.navbar {
  padding: 16px 24px;
  display: flex; justify-content: center;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid rgba(0,0,0,0.03);
}
.nav-content {
  width: 100%; max-width: 1200px;
  display: flex; justify-content: space-between; align-items: center;
}
.back-btn {
  width: 40px; height: 40px; border-radius: 50%;
  background: #F1F5F9; border: none;
  display: flex; align-items: center; justify-content: center;
  color: #475569; cursor: pointer;
  transition: background 0.2s;
  &:hover { background: #E2E8F0; }
}
.nav-title { font-size: 18px; font-weight: 700; color: #334155; margin: 0; }

.status-badge {
  display: flex; align-items: center; gap: 8px;
  font-size: 12px; font-weight: 600; color: #94A3B8;
  padding: 6px 12px; background: var(--ff-surface-raised); border-radius: 20px;

  .dot { width: 6px; height: 6px; border-radius: 50%; background: var(--ff-border-strong); }
  &.updating .dot { background: #60A5FA; animation: pulse 1.5s infinite; }
}

/* 主布局 */
.content-grid {
  max-width: 1200px; margin: 32px auto; padding: 0 24px;
  display: grid; grid-template-columns: 1fr; gap: 32px;
  @media (min-width: 960px) { grid-template-columns: 2fr 1fr; }
}

.main-column { display: flex; flex-direction: column; gap: 32px; }

/* 1. 指标卡片区域 */
.metrics-grid {
  display: grid; grid-template-columns: 1fr; gap: 24px;
  @media (min-width: 640px) { grid-template-columns: 1fr 1fr; }
}

.metric-card {
  min-height: 260px; /* 增加高度 */
  padding: 32px; /* 增加内边距 */
  display: flex; flex-direction: column; align-items: center; /* 核心：整体居中 */
  background: white; border-radius: 24px;
  box-shadow: 0 4px 20px -4px rgba(0,0,0,0.03);
  border: 1px solid rgba(255,255,255,0.5);
}

/* 统一卡片头部居中样式 */
.card-header-center {
  display: flex; flex-direction: column; align-items: center; gap: 12px;
  margin-bottom: 24px;

  .icon-circle {
    width: 48px; height: 48px; border-radius: 50%;
    display: flex; align-items: center; justify-content: center;
    transition: transform 0.3s ease;
  }
  .header-text { font-size: 14px; font-weight: 600; color: #64748B; letter-spacing: 0.5px; }
}

/* 柔和色系图标背景 */
.pastel-blue { background: #E0F2FE; color: #38BDF8; } /* Sky-100 / Sky-400 */
.pastel-orange { background: #FFEDD5; color: #FB923C; } /* Orange-100 / Orange-400 */

.card-body-center {
  display: flex; flex-direction: column; align-items: center; justify-content: center; width: 100%; flex: 1;
}

.weight-theme {
  .big-num { font-size: 56px; font-weight: 800; color: #334155; letter-spacing: -2px; line-height: 1; }
  .unit { font-size: 16px; font-weight: 600; color: #94A3B8; margin-top: 4px; }

  .stats-row {
    display: flex; gap: 12px; margin-top: 20px;
  }

  .bmi-pill, .height-pill {
    background: var(--ff-surface-raised); padding: 6px 16px; border-radius: 99px;
    display: flex; gap: 8px; font-size: 13px; font-weight: 600; align-items: center;
    .label { color: #94A3B8; }
    .status { font-weight: 700; }
  }

  .last-update {
    margin-top: 12px;
    font-size: 12px;
    color: #94A3B8;
    font-weight: 500;
  }
}

/* 2. 操作按钮区域 */
.actions-row {
  display: grid; grid-template-columns: repeat(5, 1fr); gap: 16px; /* 改为5列 */
  margin: 12px 0;
  @media (max-width: 768px) { grid-template-columns: repeat(3, 1fr); } /* 小屏幕3列 */
  @media (max-width: 480px) { grid-template-columns: repeat(2, 1fr); } /* 更小屏幕2列 */
}
.action-item {
  background: white; border: none; cursor: pointer;
  padding: 16px 8px; border-radius: 20px; /* 改为卡片式按钮 */
  display: flex; flex-direction: column; align-items: center; gap: 12px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.02);
  transition: transform 0.2s, box-shadow 0.2s;

  &:hover { transform: translateY(-2px); box-shadow: 0 8px 20px rgba(0,0,0,0.04); }

  .circle {
    width: 52px; height: 52px; border-radius: 20px; /* 改为圆角矩形，视觉更柔和 */
    display: flex; align-items: center; justify-content: center;
    transition: transform 0.2s;
  }

  /* 莫兰迪色系背景 */
  .pastel-orange-bg { background: #FFEDD5; color: #F97316; }
  .pastel-indigo-bg { background: #E0E7FF; color: #6366F1; }
  .pastel-blue-bg   { background: #E0F2FE; color: #0EA5E9; }
  .pastel-purple-bg { background: #F3E8FF; color: #A855F7; }
  .pastel-pink-bg   { background: #FCE7F3; color: #EC4899; }

  .label { font-size: 13px; color: #64748B; font-weight: 600; }
}

/* 3. 列表区域 */
.list-row {
  display: flex; align-items: center; gap: 16px;
  padding: 16px; border-radius: 16px;
  transition: background 0.2s;
  &:hover { background: var(--ff-surface-raised); }

  .row-icon-box {
    width: 40px; height: 40px; background: #F1F5F9; border-radius: 12px;
    display: flex; align-items: center; justify-content: center; font-size: 20px;
  }
  .row-content { flex: 1; min-width: 0; }
  .row-title { font-size: 15px; font-weight: 600; color: #334155; margin-bottom: 2px; }
  .row-sub { font-size: 12px; color: #94A3B8; }

  .delete-btn {
    width: 32px; height: 32px; display: flex; align-items: center; justify-content: center;
    color: var(--ff-border-strong); background: none; border: none; cursor: pointer;
    border-radius: 8px;
    &:hover { background: #FEE2E2; color: #EF4444; }
  }
}

.empty-state {
  display: flex; flex-direction: column; align-items: center; justify-content: center;
  height: 200px; color: var(--ff-border-strong); font-size: 14px;
}

@keyframes pulse { 50% { opacity: 0.5; } }
.animate-bounce-in { animation: bounceIn 0.5s cubic-bezier(0.34, 1.56, 0.64, 1); }
@keyframes bounceIn { from { opacity: 0; transform: scale(0.9); } to { opacity: 1; transform: scale(1); } }
.animate-fade-in-up { animation: fadeInUp 0.6s cubic-bezier(0.2, 0.8, 0.2, 1) forwards; }
@keyframes fadeInUp { from { opacity: 0; transform: translateY(20px); } to { opacity: 1; transform: translateY(0); } }
</style>
