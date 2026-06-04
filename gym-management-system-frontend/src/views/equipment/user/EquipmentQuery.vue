<template>
  <div class="min-h-screen bg-slate-50 pb-safe-bottom relative selection:bg-blue-200">

    <header class="sticky top-0 z-40 bg-slate-50/85 backdrop-blur-2xl backdrop-saturate-150 border-b border-slate-200/50 pt-safe-top transition-all duration-300">
      <div class="px-5 pb-2">
        <div class="relative flex items-center justify-center mb-6 mt-2 h-10">
          <button
              @click="router.back()"
              class="absolute left-0 w-10 h-10 rounded-full bg-white hover:bg-slate-100 flex items-center justify-center shadow-sm text-slate-600 active:scale-90 transition-all duration-300"
          >
            <ArrowLeft :size="20" stroke-width="2" class="opacity-80" />
          </button>
          <h1 class="text-xl font-bold tracking-tight text-slate-600">器材设施</h1>
        </div>

        <div class="relative group mb-6">
          <div class="absolute inset-0 bg-blue-200/20 rounded-[20px] blur-lg group-hover:bg-blue-200/30 transition-colors"></div>
          <div class="relative flex items-center bg-white rounded-[20px] shadow-sm transition-all duration-300 overflow-hidden">
            <Search class="ml-4 text-slate-400 w-5 h-5" stroke-width="2.5" />
            <input
                v-model="queryParams.keyword"
                @input="handleSearch"
                placeholder="搜索器材 (如: 跑步机)"
                class="w-full h-12 bg-transparent pl-3 pr-4 text-[16px] text-slate-600 placeholder-slate-400 outline-none font-medium text-center"
            />
          </div>
        </div>
      </div>

      <div class="flex justify-center gap-3 overflow-x-auto px-5 pb-4 no-scrollbar">
        <button
            @click="selectType(undefined)"
            class="px-5 py-2 rounded-full text-[12px] font-bold tracking-wide transition-all duration-300 active:scale-95 flex-shrink-0"
            :class="!queryParams.categoryId
              ? 'bg-slate-600 text-white shadow-lg shadow-slate-200'
              : 'bg-white text-slate-400 hover:bg-slate-100'"
        >
          全部
        </button>
        <button
            v-for="type in typeList"
            :key="type.id"
            @click="selectType(type.id)"
            class="px-5 py-2 rounded-full text-[12px] font-bold tracking-wide transition-all duration-300 active:scale-95 flex-shrink-0"
            :class="queryParams.categoryId === type.id
              ? 'bg-slate-600 text-white shadow-lg shadow-slate-200'
              : 'bg-white text-slate-400 hover:bg-slate-100'"
        >
          {{ type.name }}
        </button>
      </div>
    </header>

    <main class="px-4 pt-4 animate-fade-in-up pb-24">
      <div v-if="loading" class="flex justify-center py-32">
        <div class="glass-loader"></div>
      </div>

      <div v-else-if="equipmentList.length === 0" class="flex flex-col items-center justify-center py-32 text-slate-400">
        <div class="w-20 h-20 rounded-full bg-slate-100 flex items-center justify-center mb-4">
          <Dumbbell :size="32" class="opacity-30" />
        </div>
        <p class="font-medium text-slate-400">暂无器材信息</p>
      </div>

      <div v-else class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-3 sm:gap-5">
        <div
            v-for="item in equipmentList"
            :key="item.id"
            @click="openDetail(item)"
            class="group relative bg-white rounded-[22px] p-2 shadow-sm cursor-pointer active:scale-[0.97] transition-all duration-300 hover:shadow-lg hover:shadow-slate-200 hover:-translate-y-1 flex flex-col h-full"
        >
          <div class="aspect-[4/3] w-full rounded-[16px] bg-slate-50 overflow-hidden relative">
            <img
                :src="item.imageUrl || 'https://placehold.co/400x300/f1f5f9/94a3b8?text=Equipment'"
                class="w-full h-full object-cover transition-transform duration-700 group-hover:scale-105"
            />
            <div class="absolute inset-0 bg-gradient-to-t from-slate-900/10 to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-300"></div>

            <div
                class="absolute top-2 right-2 px-2 py-0.5 rounded-full text-[9px] font-bold uppercase backdrop-blur-md shadow-sm tracking-wide"
                :class="getStatusBadgeClass(item.status)"
            >
              {{ getStatusText(item.status) }}
            </div>
          </div>

          <div class="px-1 pt-3 pb-1 flex-1 flex flex-col justify-between">
            <div>
              <div class="flex items-center justify-between mb-1 gap-1">
                <h3 class="font-bold text-slate-600 text-[14px] truncate flex-1">{{ item.name }}</h3>
                <button
                    @click.stop="handleReport(item)"
                    class="shrink-0 w-7 h-7 rounded-full bg-slate-50 hover:bg-slate-100 text-slate-400 hover:text-slate-600 flex items-center justify-center transition-colors active:scale-90"
                >
                  <Wrench :size="14" />
                </button>
              </div>
              <p class="text-[11px] text-slate-400 flex items-center gap-1">
                <MapPin :size="10" class="opacity-70" /> {{ item.location || '综合训练区' }}
              </p>
            </div>
          </div>
        </div>
      </div>
    </main>

    <Teleport to="body">
      <Transition name="sheet">
        <div v-if="detailVisible && currentItem" class="fixed inset-0 z-50 flex items-end sm:items-center justify-center">
          <div class="absolute inset-0 bg-slate-200/30 backdrop-blur-[8px] transition-opacity" @click="detailVisible = false"></div>

          <div class="relative w-full max-w-md bg-white rounded-t-[36px] sm:rounded-[36px] overflow-hidden shadow-2xl h-[90vh] sm:h-auto sm:max-h-[85vh] flex flex-col ring-1 ring-slate-100">

            <button @click="detailVisible = false" class="absolute top-4 right-4 z-10 w-8 h-8 rounded-full bg-white/80 backdrop-blur-md flex items-center justify-center text-slate-600 active:scale-90 transition-transform shadow-sm">
              <X :size="16" stroke-width="3" />
            </button>

            <div class="h-72 relative flex-shrink-0">
              <img :src="currentItem.imageUrl || 'https://placehold.co/400x300/f1f5f9/94a3b8?text=Equipment'" class="w-full h-full object-cover" />
            </div>

            <div class="p-8 flex-1 overflow-y-auto no-scrollbar">
              <div class="flex justify-center items-center mb-2">
                <h2 class="text-2xl font-bold text-slate-600 tracking-tight text-center">{{ currentItem.name }}</h2>
              </div>

              <div class="flex justify-center items-center gap-3 mb-8">
                 <span class="px-3 py-1 rounded-full text-xs font-bold" :class="getStatusBadgeClass(currentItem.status)">
                  {{ getStatusText(currentItem.status) }}
                </span>
                <span class="text-slate-400 text-sm font-medium flex items-center gap-1.5">
                  <MapPin :size="14" class="text-slate-400" /> {{ currentItem.location || '未知位置' }}
                </span>
              </div>

              <div class="space-y-6">
                <div class="p-6 bg-slate-50 rounded-3xl">
                  <h4 class="text-xs font-bold text-slate-400 uppercase tracking-widest mb-3 text-center">设备详情</h4>
                  <p class="text-[15px] text-slate-600 leading-7 font-medium text-center">{{ currentItem.description || '暂无详细描述信息。' }}</p>
                </div>
              </div>
            </div>

            <div class="p-6 pt-4 border-t border-slate-50 bg-white">
              <button
                  @click="handleReport(currentItem)"
                  class="w-full py-4 rounded-[20px] bg-slate-600 text-white font-bold text-[15px] shadow-lg shadow-slate-200 active:scale-[0.98] transition-all flex items-center justify-center gap-2 hover:bg-slate-700"
              >
                <Wrench :size="18" /> 提交故障报修
              </button>
            </div>
          </div>
        </div>
      </Transition>
    </Teleport>

    <Teleport to="body">
      <Transition name="sheet">
        <div v-if="reportVisible" class="fixed inset-0 z-[60] flex items-end sm:items-center justify-center">
          <div class="absolute inset-0 bg-slate-200/30 backdrop-blur-[8px]" @click="reportVisible = false"></div>

          <div class="relative w-full max-w-md bg-slate-50 rounded-t-[36px] sm:rounded-[36px] shadow-2xl overflow-hidden p-6 pb-safe border-t border-white">
            <div class="w-12 h-1.5 bg-slate-200 rounded-full mx-auto mb-6"></div>

            <div class="relative flex items-center justify-center mb-6 h-8">
              <h3 class="text-xl font-bold text-slate-600">故障报修</h3>
              <button @click="reportVisible = false" class="absolute right-0 w-8 h-8 rounded-full bg-slate-200/50 flex items-center justify-center active:scale-90 transition-transform">
                <X :size="18" class="text-slate-500"/>
              </button>
            </div>

            <div class="bg-white rounded-[24px] p-4 mb-5 flex items-center gap-4 shadow-sm border border-slate-100">
              <img :src="reportForm.equipmentImage || 'https://placehold.co/100x100/f1f5f9/94a3b8?text=EQ'" class="w-14 h-14 rounded-2xl object-cover bg-slate-100" />
              <div>
                <div class="font-bold text-slate-600 text-[15px]">{{ reportForm.equipmentName }}</div>
                <div class="text-xs text-slate-400 font-medium mt-0.5">ID: #{{ reportForm.equipmentId }}</div>
              </div>
            </div>

            <div class="bg-white rounded-[24px] overflow-hidden mb-8 shadow-sm">
               <textarea
                   v-model="reportForm.description"
                   rows="5"
                   class="w-full p-5 text-[16px] outline-none resize-none placeholder-slate-300 text-slate-600 bg-transparent leading-relaxed"
                   placeholder="请描述故障情况..."
               ></textarea>
            </div>

            <button
                @click="submitReport"
                :disabled="submitLoading || !reportForm.description"
                class="w-full py-4 bg-slate-600 text-white rounded-[22px] font-bold text-[15px] shadow-lg shadow-slate-200 disabled:opacity-50 active:scale-[0.98] transition-all hover:bg-slate-700"
            >
              <span v-if="submitLoading" class="flex items-center justify-center gap-2">
                <div class="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin"></div> 提交中...
              </span>
              <span v-else>发送报修申请</span>
            </button>
          </div>
        </div>
      </Transition>
    </Teleport>

  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowLeft, Search, Dumbbell, Wrench, MapPin, X } from 'lucide-vue-next'
import { ElMessage } from 'element-plus'
import { getEquipmentList, getCategoryList, addRepair } from '@/api/equipment'
import { useUserStore } from '@/store/modules/user'
import _ from 'lodash'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const equipmentList = ref<any[]>([])
const typeList = ref<any[]>([])

const queryParams = reactive({
  pageNum: 1,
  pageSize: 50,
  keyword: '',
  categoryId: undefined as number | undefined,
  status: undefined
})

const detailVisible = ref(false)
const reportVisible = ref(false)
const currentItem = ref<any>(null)
const reportForm = reactive({ equipmentId: 0, equipmentName: '', equipmentImage: '', description: '' })
const submitLoading = ref(false)

onMounted(async () => {
  await loadTypes()
  loadData()
})

const loadTypes = async () => {
  try {
    typeList.value = await getCategoryList()
  } catch (e) {
    console.error('Failed to load types', e)
  }
}

const loadData = async () => {
  loading.value = true
  try {
    const res: any = await getEquipmentList(queryParams)
    equipmentList.value = res.records || res.rows || []
  } catch (e) { console.error(e) }
  finally { loading.value = false }
}

const handleSearch = _.debounce(() => {
  queryParams.pageNum = 1
  loadData()
}, 300)

const selectType = (id?: number) => {
  queryParams.categoryId = id;
  queryParams.pageNum = 1;
  loadData()
}

// Helpers - 重构颜色为 Slate / Pastel 风格
const getStatusText = (s: number | string) => ({ '0': '正常', '1': '维护中', '2': '损坏', '3': '报废' }[s] || '未知')

// 使用 pastel 背景，文字统一 slate-600
const getStatusBadgeClass = (s: number | string) => ({
  '0': 'bg-emerald-200 text-slate-600',
  '1': 'bg-orange-200 text-slate-600',
  '2': 'bg-red-200 text-slate-600',
  '3': 'bg-slate-200 text-slate-500'
}[s])

// Interactions
const openDetail = (item: any) => { currentItem.value = item; detailVisible.value = true }

const handleReport = (item: any) => {
  reportForm.equipmentId = item.id
  reportForm.equipmentName = item.name
  reportForm.equipmentImage = item.imageUrl // 修改引用
  reportForm.description = ''
  detailVisible.value = false
  reportVisible.value = true
}

const submitReport = async () => {
  // 核心修改：兼容获取 ID 字段 (userId 或 id)，避免 store 计算属性为 undefined
  const uid = userStore.userId || userStore.userInfo?.userId || userStore.userInfo?.id

  if (!uid) {
    ElMessage.error('无法获取用户信息，请重新登录')
    console.error('User info missing in store:', userStore.userInfo)
    return
  }

  submitLoading.value = true
  try {
    await addRepair({
      equipmentId: reportForm.equipmentId,
      faultDesc: reportForm.description,
      reporterId: uid // 使用获取到的兼容 ID
    })
    ElMessage.success('报修成功，我们将尽快处理')
    reportVisible.value = false
  } catch (e) {
    ElMessage.error('提交失败，请稍后重试')
  }
  finally { submitLoading.value = false }
}
</script>

<style scoped>
.pt-safe-top { padding-top: max(env(safe-area-inset-top), 20px); }
.pb-safe { padding-bottom: max(env(safe-area-inset-bottom), 20px); }
.pb-safe-bottom { padding-bottom: max(env(safe-area-inset-bottom), 30px); }
.no-scrollbar::-webkit-scrollbar { display: none; }

.animate-fade-in-up { animation: fadeInUp 0.8s cubic-bezier(0.16, 1, 0.3, 1); }
@keyframes fadeInUp {
  from { opacity: 0; transform: translateY(30px) scale(0.98); }
  to { opacity: 1; transform: translateY(0) scale(1); }
}

.glass-loader {
  width: 32px;
  height: 32px;
  border: 3px solid rgba(0,0,0,0.05);
  border-radius: 50%;
  border-top-color: #475569;
  animation: spin 1s ease-in-out infinite;
}
@keyframes spin { to { transform: rotate(360deg); } }

.sheet-enter-active, .sheet-leave-active {
  transition: all 0.5s cubic-bezier(0.32, 0.72, 0, 1);
}
.sheet-enter-from, .sheet-leave-to {
  opacity: 0;
  transform: translateY(100%) scale(0.95);
}
.sheet-enter-to, .sheet-leave-from {
  opacity: 1;
  transform: translateY(0) scale(1);
}
</style>