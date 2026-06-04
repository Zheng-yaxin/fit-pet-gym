<template>
  <div class="min-h-screen bg-slate-50 pb-safe-bottom relative overflow-hidden">

    <header class="sticky top-0 z-40 px-6 pt-safe-top pb-4 transition-all duration-300 bg-slate-50/90 backdrop-blur-xl border-b border-slate-200/50">
      <div class="relative flex items-center justify-center max-w-2xl mx-auto h-10">
        <button
            @click="router.back()"
            class="absolute left-0 w-10 h-10 rounded-full bg-white hover:bg-slate-100 flex items-center justify-center shadow-sm text-slate-600 active:scale-90 transition-all duration-300 border border-slate-100"
        >
          <ArrowLeft :size="20" class="opacity-70" />
        </button>
        <h1 class="text-xl font-bold tracking-tight text-slate-600">我的报修</h1>
      </div>
    </header>

    <main class="max-w-2xl mx-auto px-5 pt-6 space-y-5 animate-slide-up relative z-10">

      <div v-if="loading" class="flex justify-center py-20">
        <div class="glass-loader"></div>
      </div>

      <div v-else-if="repairList.length === 0" class="flex flex-col items-center justify-center py-32 text-center">
        <div class="w-24 h-24 rounded-[32px] bg-white flex items-center justify-center mb-6 shadow-sm">
          <ClipboardList :size="40" class="text-slate-300" />
        </div>
        <h3 class="text-lg font-medium text-slate-600 mb-2">暂无记录</h3>
        <p class="text-slate-400 text-sm mb-8">您还没有提交过任何设备报修申请</p>
        <button
            @click="router.push('/equipment/query')"
            class="px-8 py-3 bg-slate-600 text-white rounded-full text-sm font-semibold shadow-lg shadow-slate-200 active:scale-95 transition-transform"
        >
          去报修
        </button>
      </div>

      <div
          v-for="item in repairList"
          :key="item.id"
          class="group relative overflow-hidden bg-white rounded-[28px] p-5 shadow-sm hover:shadow-lg hover:shadow-slate-200/50 transition-all duration-300 active:scale-[0.99]"
      >
        <div class="absolute top-0 bottom-0 left-0 w-1.5 transition-colors duration-300" :class="getStatusColor(item.status)"></div>

        <div class="flex flex-col gap-3 pl-2">
          <div class="relative flex justify-center items-center h-7 mb-1">
            <h3 class="font-semibold text-[17px] text-slate-600 tracking-tight">{{ item.equipmentName || '未知设备' }}</h3>
            <span
                class="absolute right-0 px-3 py-1 rounded-full text-[11px] font-bold uppercase tracking-wider"
                :class="getStatusBadge(item.status)"
            >
              {{ getStatusText(item.status) }}
            </span>
          </div>

          <p class="text-[15px] leading-relaxed text-slate-600 line-clamp-2 text-center">{{ item.faultDesc }}</p>

          <div class="flex items-center justify-center gap-4 mt-1 pt-3 border-t border-slate-50">
            <span class="flex items-center gap-1.5 text-xs font-medium text-slate-400">
              <Clock :size="13" /> {{ formatDate(item.createTime) }}
            </span>
            <span v-if="item.status === 2" class="flex items-center gap-1.5 text-xs font-bold text-slate-500 bg-slate-100 px-2 py-0.5 rounded-lg">
              <CheckCircle :size="13" /> 已处理
            </span>
          </div>

          <div v-if="item.repairRemark && item.status !== 0" class="mt-2 p-4 bg-slate-50 rounded-2xl">
            <div class="text-[11px] font-bold text-slate-400 uppercase tracking-wide mb-1 text-center">维修反馈</div>
            <div class="text-[13px] text-slate-600 leading-normal text-center">{{ item.repairRemark }}</div>
          </div>
        </div>
      </div>

    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowLeft, ClipboardList, Clock, CheckCircle } from 'lucide-vue-next'
import { getMyRepairPage } from '@/api/equipment'
import { useUserStore } from '@/store/modules/user'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const repairList = ref<any[]>([])

onMounted(async () => {
  loading.value = true
  try {
    const res: any = await getMyRepairPage({
      pageNum: 1,
      pageSize: 100,
      reporterId: userStore.userId
    })
    repairList.value = res.records || []
  } catch (e) {
    console.error(e)
  }
  finally { loading.value = false }
})

const formatDate = (d: string) => d ? d.split(' ')[0] : ''
// 状态：0-待处理 1-维修中 2-已完成 3-已驳回
const getStatusText = (s: number) => ({ 0: '待处理', 1: '维修中', 2: '已完成', 3: '已驳回' }[s] || '未知')

// Pastel Colors for Decoration Bar
const getStatusColor = (s: number) => ({
  0: 'bg-orange-300',
  1: 'bg-blue-300',
  2: 'bg-emerald-300',
  3: 'bg-slate-300'
}[s] || 'bg-slate-200')

// Pastel Backgrounds with Slate Text for Badges
const getStatusBadge = (s: number) => ({
  0: 'bg-orange-100 text-slate-600',
  1: 'bg-blue-100 text-slate-600',
  2: 'bg-emerald-100 text-slate-600',
  3: 'bg-slate-100 text-slate-500'
}[s])
</script>

<style scoped>
.pb-safe-bottom { padding-bottom: max(env(safe-area-inset-bottom), 24px); }
.pt-safe-top { padding-top: max(env(safe-area-inset-top), 20px); }

.glass-loader {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  border: 2.5px solid rgba(0, 0, 0, 0.05);
  border-top-color: #475569;
  animation: spin 0.8s ease-in-out infinite;
}

@keyframes spin { to { transform: rotate(360deg); } }

.animate-slide-up { animation: slideUp 0.8s cubic-bezier(0.16, 1, 0.3, 1) forwards; opacity: 0; }

@keyframes slideUp {
  from { opacity: 0; transform: translateY(20px) scale(0.98); }
  to { opacity: 1; transform: translateY(0) scale(1); }
}
</style>