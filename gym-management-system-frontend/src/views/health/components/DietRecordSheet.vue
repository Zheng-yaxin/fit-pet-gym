<template>
  <Teleport to="body">
    <Transition name="liquid-dim">
      <div v-if="visible" class="fixed inset-0 z-[999] flex flex-col justify-end sm:justify-center sm:items-center">
        <div class="absolute inset-0 bg-slate-900/20 backdrop-blur-[4px] transition-opacity" @click="handleClose"></div>

        <Transition name="liquid-sheet" appear>
          <div v-if="visible" class="liquid-sheet-container">

            <div class="w-full flex justify-center pt-3 pb-4 cursor-grab active:cursor-grabbing z-50" @click="handleClose">
              <div class="w-10 h-1 bg-slate-300 rounded-full"></div>
            </div>

            <div class="px-6 pb-2 flex items-center justify-between h-10 relative z-20 shrink-0">
              <div class="flex-1 flex justify-start">
                <button
                    v-if="currentView === 'detail' || currentView === 'custom'"
                    @click="goBack"
                    class="flex items-center text-slate-500 hover:text-slate-800 transition-colors -ml-2 group"
                >
                  <ChevronLeft class="w-6 h-6 group-active:scale-90 transition-transform" stroke-width="2.5" />
                  <span class="text-[16px] font-semibold leading-none mb-[1px]">返回</span>
                </button>
              </div>

              <div class="absolute inset-0 flex items-center justify-center pointer-events-none">
                <span class="font-bold text-[17px] text-slate-800 tracking-tight transition-all duration-500"
                      :class="currentView !== 'list' ? 'opacity-100 translate-y-0 scale-100' : 'opacity-0 translate-y-4 scale-95'">
                  {{ currentView === 'custom' ? '添加自定义食物' : '添加食物' }}
                </span>
              </div>

              <div class="flex-1 flex justify-end">
                <button
                    @click="handleClose"
                    class="w-8 h-8 rounded-full bg-slate-100 text-slate-500 flex items-center justify-center hover:bg-slate-200 active:scale-90 transition-all"
                >
                  <X class="w-4 h-4 stroke-[3]" />
                </button>
              </div>
            </div>

            <div class="relative w-full flex-1 overflow-hidden bg-transparent">

              <Transition :name="transitionName">
                <div v-if="currentView === 'list'" class="absolute inset-0 flex flex-col w-full h-full">

                  <div class="px-6 pb-4 bg-transparent sticky top-0 z-10">
                    <div class="relative group">
                      <div class="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none">
                        <Search class="h-4 w-4 text-slate-400" />
                      </div>
                      <input
                          ref="searchInput"
                          v-model="keyword"
                          @input="handleSearch"
                          class="block w-full pl-10 pr-4 py-3 rounded-2xl bg-slate-100/80 border-none text-[16px] text-slate-800 placeholder-slate-400 focus:bg-white focus:ring-2 focus:ring-slate-100 transition-all duration-300 ease-out caret-slate-800"
                          placeholder="搜索食物 (如: 燕麦)"
                          autofocus
                      />
                    </div>
                  </div>

                  <div class="flex-1 overflow-y-auto custom-scrollbar px-6">
                    <div v-if="!keyword" class="mt-2">
                      <h3 class="text-[15px] font-bold text-slate-800 mb-4">常见食物</h3>
                      <div class="grid grid-cols-3 gap-4 pb-8">
                        <div
                            v-for="food in commonFoods"
                            :key="food.id"
                            @click="selectFood(food)"
                            class="aspect-[4/5] bg-white rounded-[20px] p-3 flex flex-col items-center justify-center shadow-sm border border-slate-100 hover:border-slate-300 active:scale-[0.97] transition-all cursor-pointer"
                        >
                          <span class="text-4xl mb-3">{{ food.emoji || '🥗' }}</span>
                          <span class="font-bold text-slate-700 text-[13px] truncate w-full text-center">{{ food.name }}</span>
                          <span class="text-[11px] text-slate-400 font-medium mt-1">{{ food.calories }} kcal</span>
                        </div>
                      </div>
                    </div>

                    <div v-if="keyword" class="pb-8">
                      <div class="flex flex-col gap-3">
                        <div
                            v-for="(item, index) in searchList"
                            :key="item.id"
                            @click="selectFood(item)"
                            class="group flex items-center justify-between p-4 bg-white rounded-2xl active:scale-[0.99] transition-all cursor-pointer shadow-sm border border-slate-50 hover:border-slate-200"
                        >
                          <div class="flex items-center gap-4">
                            <div class="w-10 h-10 rounded-full bg-slate-50 text-xl flex items-center justify-center">
                              {{ item.emoji || '🥘' }}
                            </div>
                            <div>
                              <div class="font-bold text-[16px] text-slate-800">{{ item.name }}</div>
                              <div class="text-[12px] text-slate-400 font-medium">{{ item.calories }} 千卡 / 100g</div>
                            </div>
                          </div>
                          <PlusCircle class="w-6 h-6 text-slate-300 group-hover:text-slate-600 transition-colors" />
                        </div>

                        <div v-if="searchList.length === 0 && !isLoading" class="text-center py-8">
                          <p class="text-slate-400 mb-4">未找到该食物</p>
                          <button @click="openCustomFoodModal" class="px-6 py-3 bg-slate-800 text-white rounded-2xl font-semibold hover:bg-black transition-colors">
                            添加自定义食物
                          </button>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </Transition>

              <Transition :name="transitionName">
                <div v-if="currentView === 'detail' && selectedFood" class="absolute inset-0 flex flex-col w-full h-full">

                  <div class="flex-1 overflow-y-auto px-6 pt-4 custom-scrollbar">

                    <div class="bg-white rounded-[28px] p-8 text-center relative overflow-hidden mb-6 shadow-sm border border-slate-100">
                      <div class="text-7xl mb-6 animate-float">{{ selectedFood.emoji || '🥘' }}</div>
                      <h2 class="text-[24px] font-bold text-slate-800 tracking-tight mb-2">{{ selectedFood.name }}</h2>
                      <div class="flex justify-center gap-3 mt-6">
                        <div class="px-5 py-2 bg-slate-50 rounded-xl text-[13px] font-semibold text-slate-500">
                          <span class="text-slate-900 text-[15px]">{{ Math.round(selectedFood.calories * amount / 100) }}</span> Kcal
                        </div>
                        <div class="px-5 py-2 bg-slate-50 rounded-xl text-[13px] font-semibold text-slate-500">
                          <span class="text-slate-900 text-[15px]">{{ Math.round(selectedFood.protein * amount / 100) }}g</span> 蛋白
                        </div>
                      </div>
                    </div>

                    <div class="bg-white rounded-[28px] p-6 mb-6 shadow-sm border border-slate-100">
                      <div class="flex justify-between items-end mb-8 px-1">
                        <label class="text-[15px] font-bold text-slate-700">摄入份量</label>
                        <div class="text-right">
                          <span class="text-4xl font-bold text-slate-800 font-mono tracking-tighter">{{ amount }}</span>
                          <span class="text-slate-400 ml-1 text-[15px] font-medium">g</span>
                        </div>
                      </div>

                      <div class="relative w-full h-10 flex items-center mb-6">
                        <input
                            type="range"
                            v-model.number="amount"
                            min="10"
                            max="500"
                            step="10"
                            class="liquid-slider w-full"
                            @input="hapticTick"
                        />
                      </div>

                      <div class="flex justify-between items-center gap-4">
                        <button @click="adjustAmount(-10)" class="circle-btn">
                          <Minus class="w-5 h-5" />
                        </button>
                        <div class="flex gap-2">
                          <button v-for="val in [100, 150, 200, 300]" :key="val" @click="setAmount(val)"
                                  class="quick-amount-btn">
                            {{ val }}
                          </button>
                        </div>
                        <button @click="adjustAmount(10)" class="circle-btn">
                          <Plus class="w-5 h-5" />
                        </button>
                      </div>
                    </div>

                    <div class="bg-slate-100 rounded-2xl p-1 flex mb-8 relative">
                      <div class="absolute top-1 bottom-1 bg-white rounded-xl shadow-sm transition-all duration-300"
                           :style="{
                             left: `${(mealType - 1) * 25}%`,
                             width: '25%'
                           }"></div>

                      <button
                          v-for="(label, val) in {1:'早餐', 2:'午餐', 3:'晚餐', 4:'加餐'}"
                          :key="val"
                          @click="mealType = Number(val); hapticLight()"
                          class="flex-1 py-2.5 rounded-xl text-[13px] font-bold transition-colors relative z-10"
                          :class="mealType === Number(val) ? 'text-slate-800' : 'text-slate-400 hover:text-slate-600'"
                      >
                        {{ label }}
                      </button>
                    </div>

                  </div>

                  <div class="p-6 pt-2 bg-transparent safe-area-bottom">
                    <button
                        @click="handleSubmit"
                        :disabled="submitting"
                        class="w-full h-[54px] bg-slate-800 hover:bg-black active:scale-[0.98] rounded-2xl flex items-center justify-center gap-2 shadow-lg shadow-slate-200 transition-all duration-300 relative overflow-hidden"
                        :class="{'bg-emerald-500 hover:bg-emerald-500 shadow-emerald-200': isSuccess}"
                    >
                      <template v-if="!submitting && !isSuccess">
                        <span class="text-white font-bold text-[16px]">添加记录</span>
                      </template>

                      <div v-if="submitting && !isSuccess" class="absolute inset-0 flex items-center justify-center">
                        <div class="w-5 h-5 border-[2px] border-white/30 border-t-white rounded-full animate-spin"></div>
                      </div>

                      <div v-if="isSuccess" class="flex items-center text-white animate-scale-in">
                        <Check class="w-5 h-5 mr-2 stroke-[3]" />
                        <span class="font-bold text-[16px]">已保存</span>
                      </div>
                    </button>
                  </div>

                </div>
              </Transition>

              <Transition :name="transitionName">
                <div v-if="currentView === 'custom'" class="absolute inset-0 flex flex-col w-full h-full">
                  <div class="flex-1 overflow-y-auto px-6 pt-4 custom-scrollbar">
                    <div class="bg-white rounded-[28px] p-6 mb-6 shadow-sm border border-slate-100">
                      <h3 class="text-[17px] font-bold text-slate-800 mb-6">添加自定义食物</h3>

                      <div class="space-y-5">
                        <div>
                          <label class="text-[13px] font-bold text-slate-600 mb-2 block">食物名称</label>
                          <input v-model="customFood.name" type="text" placeholder="例如: 自制沙拉"
                            class="w-full px-4 py-3 rounded-xl bg-slate-50 border-none text-[15px] focus:ring-2 focus:ring-slate-200 transition-all" />
                        </div>

                        <div class="grid grid-cols-2 gap-4">
                          <div>
                            <label class="text-[13px] font-bold text-slate-600 mb-2 block">热量 (kcal/100g)</label>
                            <input v-model.number="customFood.calories" type="number" placeholder="0"
                              class="w-full px-4 py-3 rounded-xl bg-slate-50 border-none text-[15px] focus:ring-2 focus:ring-slate-200 transition-all" />
                          </div>
                          <div>
                            <label class="text-[13px] font-bold text-slate-600 mb-2 block">蛋白质 (g/100g)</label>
                            <input v-model.number="customFood.protein" type="number" placeholder="0"
                              class="w-full px-4 py-3 rounded-xl bg-slate-50 border-none text-[15px] focus:ring-2 focus:ring-slate-200 transition-all" />
                          </div>
                        </div>

                        <div class="grid grid-cols-2 gap-4">
                          <div>
                            <label class="text-[13px] font-bold text-slate-600 mb-2 block">脂肪 (g/100g)</label>
                            <input v-model.number="customFood.fat" type="number" placeholder="0"
                              class="w-full px-4 py-3 rounded-xl bg-slate-50 border-none text-[15px] focus:ring-2 focus:ring-slate-200 transition-all" />
                          </div>
                          <div>
                            <label class="text-[13px] font-bold text-slate-600 mb-2 block">碳水 (g/100g)</label>
                            <input v-model.number="customFood.carbohydrate" type="number" placeholder="0"
                              class="w-full px-4 py-3 rounded-xl bg-slate-50 border-none text-[15px] focus:ring-2 focus:ring-slate-200 transition-all" />
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>

                  <div class="p-6 pt-2 bg-transparent safe-area-bottom">
                    <button @click="handleAddCustomFood" :disabled="!customFood.name || submitting"
                      class="w-full h-[54px] bg-slate-800 hover:bg-black active:scale-[0.98] rounded-2xl text-white font-bold text-[16px] shadow-lg shadow-slate-200 transition-all duration-300 disabled:opacity-50">
                      保存并添加
                    </button>
                  </div>
                </div>
              </Transition>

            </div>
          </div>
        </Transition>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup lang="ts">
import { ref, watch, nextTick, onMounted } from 'vue'
import { Search, X, ChevronLeft, Plus, Minus, PlusCircle, Check } from 'lucide-vue-next'
import { getFoodList, recordDiet, addCustomFood, type Food } from '@/api/health'
import dayjs from 'dayjs'
import _ from 'lodash'
import { ElMessage } from 'element-plus'

const props = defineProps<{ modelValue: boolean }>()
const emit = defineEmits(['update:modelValue', 'success'])

// State
const visible = ref(false)
const currentView = ref<'list' | 'detail' | 'custom'>('list')
const transitionName = ref('slide-next')
const keyword = ref('')
const searchList = ref<Food[]>([])
const isLoading = ref(false)
const submitting = ref(false)
const isSuccess = ref(false)
const searchInput = ref<HTMLInputElement | null>(null)

// Data Selection
const selectedFood = ref<any>(null)
const amount = ref(100)
const mealType = ref(2)

// Custom Food
const customFood = ref({
  name: '',
  calories: 0,
  protein: 0,
  fat: 0,
  carbohydrate: 0
})

// Common Foods from database
const commonFoods = ref<Food[]>([])

const hapticLight = () => { if (navigator.vibrate) navigator.vibrate(5) }
const hapticTick = () => { if (navigator.vibrate) navigator.vibrate(2) }
const hapticSuccess = () => { if (navigator.vibrate) navigator.vibrate([10, 30, 10]) }

// Load common foods from database
const loadCommonFoods = async () => {
  try {
    const res = await getFoodList('')
    commonFoods.value = res?.slice(0, 12).map(f => ({
      ...f,
      emoji: getEmojiForFood(f.name)
    })) || []
  } catch (e) {
    console.error('Failed to load common foods:', e)
  }
}

const getEmojiForFood = (name: string): string => {
  const emojiMap: Record<string, string> = {
    '蛋': '🥚', '鸡': '🍗', '米饭': '🍚', '牛奶': '🥛', '香蕉': '🍌',
    '燕麦': '🥣', '面包': '🍞', '苹果': '🍎', '牛肉': '🥩', '鱼': '🐟',
    '虾': '🦐', '蔬菜': '🥗', '沙拉': '🥗', '汤': '🍲', '粥': '🥣'
  }
  for (const key in emojiMap) {
    if (name.includes(key)) return emojiMap[key]
  }
  return '🥘'
}

watch(() => props.modelValue, (val) => {
  visible.value = val
  if (val) {
    resetState()
    loadCommonFoods()
    hapticLight()
    nextTick(() => { setTimeout(() => searchInput.value?.focus(), 400) })
  }
})

const handleClose = () => {
  visible.value = false
  setTimeout(() => emit('update:modelValue', false), 350)
}

const resetState = () => {
  currentView.value = 'list'
  keyword.value = ''
  searchList.value = []
  amount.value = 100
  const hour = new Date().getHours()
  if (hour < 10) mealType.value = 1
  else if (hour < 14) mealType.value = 2
  else if (hour < 20) mealType.value = 3
  else mealType.value = 4
  isSuccess.value = false
  customFood.value = { name: '', calories: 0, protein: 0, fat: 0, carbohydrate: 0 }
}

const handleSearch = _.debounce(async () => {
  if (!keyword.value) {
    searchList.value = []
    return
  }
  isLoading.value = true
  try {
    const res = await getFoodList(keyword.value)
    searchList.value = res?.map(f => ({
      ...f,
      emoji: getEmojiForFood(f.name)
    })) || []
  } finally {
    isLoading.value = false
  }
}, 300)

const selectFood = (food: Food) => {
  selectedFood.value = food
  transitionName.value = 'slide-next'
  currentView.value = 'detail'
  hapticLight()
}

const goBack = () => {
  transitionName.value = 'slide-prev'
  currentView.value = 'list'
  hapticLight()
}

const openCustomFoodModal = () => {
  customFood.value.name = keyword.value
  transitionName.value = 'slide-next'
  currentView.value = 'custom'
  hapticLight()
}

const handleAddCustomFood = async () => {
  if (!customFood.value.name || submitting.value) return
  submitting.value = true

  try {
    await addCustomFood(customFood.value)
    ElMessage.success('食物已添加')

    // Reload food list and select the new food
    const res = await getFoodList(customFood.value.name)
    const newFood = res?.find(f => f.name === customFood.value.name)
    if (newFood) {
      selectFood(newFood)
    }
  } catch (e) {
    ElMessage.error('添加失败')
  } finally {
    submitting.value = false
  }
}

const adjustAmount = (delta: number) => {
  amount.value = Math.max(10, amount.value + delta)
  hapticTick()
}
const setAmount = (val: number) => {
  amount.value = val
  hapticLight()
}

const handleSubmit = async () => {
  if (!selectedFood.value || submitting.value) return
  submitting.value = true
  hapticLight()

  try {
    await recordDiet({
      foodId: selectedFood.value.id!,
      amount: amount.value,
      mealType: mealType.value,
      eatDate: dayjs().format('YYYY-MM-DD')
    })
    isSuccess.value = true
    hapticSuccess()
    setTimeout(() => { emit('success'); handleClose() }, 800)
  } catch (e) { }
  finally { if (!isSuccess.value) setTimeout(() => submitting.value = false, 500) }
}

onMounted(() => {
  loadCommonFoods()
})
</script>

<style scoped>
.liquid-sheet-container {
  width: 100%;
  max-width: 600px;
  height: 94vh;
  /* 莫兰迪风格背景：极浅的冷灰色 */
  background: var(--ff-surface-raised);
  border-top-left-radius: 32px;
  border-top-right-radius: 32px;
  box-shadow: 0 -10px 40px rgba(0,0,0,0.08);
  display: flex;
  flex-direction: column;
  overflow: hidden;
  transform: translate3d(0,0,0);
}

.liquid-dim-enter-active, .liquid-dim-leave-active { transition: opacity 0.4s ease; }
.liquid-dim-enter-from, .liquid-dim-leave-to { opacity: 0; }

.liquid-sheet-enter-active { transition: transform 0.5s cubic-bezier(0.19, 1, 0.22, 1); }
.liquid-sheet-leave-active { transition: transform 0.3s cubic-bezier(0.19, 1, 0.22, 1); }
.liquid-sheet-enter-from, .liquid-sheet-leave-to { transform: translateY(100%); }

.slide-next-enter-active, .slide-next-leave-active,
.slide-prev-enter-active, .slide-prev-leave-active {
  transition: all 0.4s cubic-bezier(0.19, 1, 0.22, 1);
  position: absolute; width: 100%; height: 100%;
}
.slide-next-enter-from { transform: translateX(100%); opacity: 1; z-index: 10; }
.slide-next-leave-to { transform: translateX(-20%); opacity: 0.8; z-index: 0; }
.slide-prev-enter-from { transform: translateX(-20%); opacity: 0.8; z-index: 0; }
.slide-prev-leave-to { transform: translateX(100%); opacity: 1; z-index: 10; }

.custom-scrollbar::-webkit-scrollbar { width: 0px; display: none; }

/* 扁平化滑块 */
.liquid-slider {
  -webkit-appearance: none;
  background: transparent;
  height: 40px;
}
.liquid-slider::-webkit-slider-runnable-track {
  width: 100%; height: 6px;
  background: #E2E8F0; /* Slate-200 */
  border-radius: 3px;
}
.liquid-slider::-webkit-slider-thumb {
  -webkit-appearance: none;
  height: 28px; width: 28px;
  border-radius: 50%;
  background: var(--ff-surface);
  border: 4px solid #334155; /* Slate-700 Ring */
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  margin-top: -11px;
  transition: transform 0.1s;
}
.liquid-slider:active::-webkit-slider-thumb { transform: scale(1.1); }

.circle-btn {
  width: 44px; height: 44px;
  border-radius: 50%;
  background: var(--ff-surface);
  color: #64748B;
  display: flex; align-items: center; justify-content: center;
  border: 1px solid #E2E8F0;
  box-shadow: 0 2px 4px rgba(0,0,0,0.02);
}
.circle-btn:active { transform: scale(0.92); background: var(--ff-surface-raised); }

.quick-amount-btn {
  padding: 8px 16px;
  border-radius: 12px;
  background: #F1F5F9;
  color: #64748B;
  font-size: 13px;
  font-weight: 600;
  transition: all 0.2s;
}
.quick-amount-btn:active { background: #334155; color: white; }

.animate-float { animation: float 6s ease-in-out infinite; }
@keyframes float {
  0% { transform: translateY(0px); }
  50% { transform: translateY(-6px); }
  100% { transform: translateY(0px); }
}

.safe-area-bottom { padding-bottom: max(env(safe-area-inset-bottom), 24px); }
</style>