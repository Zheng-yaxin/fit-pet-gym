<template>
  <el-dialog
      v-model="visible"
      width="480px"
      :show-close="false"
      class="glass-dialog"
      destroy-on-close
      align-center
  >
    <template #header>
      <div class="relative flex items-center justify-center pt-2 pb-2 h-10">
        <h3 class="text-[17px] font-bold text-slate-600 tracking-tight">器材详情</h3>

        <div v-if="detail.status !== undefined" class="absolute right-0 top-1/2 -translate-y-1/2">
          <div class="flex items-center gap-1.5 px-3 py-1 rounded-full bg-slate-50 border border-slate-100">
            <div class="w-1.5 h-1.5 rounded-full" :class="getStatusDot(detail.status)"></div>
            <span class="text-[11px] font-bold text-slate-500">{{ statusMap[detail.status]?.label }}</span>
          </div>
        </div>
      </div>
    </template>

    <div class="py-4 px-2" v-loading="loading">
      <div class="flex flex-col items-center mb-8">
        <div class="w-20 h-20 bg-slate-50 rounded-2xl flex items-center justify-center text-3xl mb-4 border border-slate-100 shadow-sm">
          <span class="opacity-80">🏋️</span>
        </div>
        <h2 class="text-xl font-bold text-slate-600 mb-1.5">{{ detail.name }}</h2>
        <span class="font-mono text-xs text-slate-400 bg-slate-50 px-2 py-0.5 rounded border border-slate-100">{{ detail.code }}</span>
      </div>

      <div class="space-y-4">
        <div class="bg-slate-50/50 rounded-[20px] p-5 space-y-3.5 border border-slate-50">
          <div class="flex justify-between items-center" v-for="(val, label) in { '分类': detail.categoryName, '品牌': detail.brand, '型号': detail.model }" :key="label">
            <span class="text-xs font-bold text-slate-400 uppercase tracking-wide">{{ label }}</span>
            <span class="text-[14px] font-medium text-slate-600">{{ val || '-' }}</span>
          </div>
        </div>

        <div class="bg-slate-50/50 rounded-[20px] p-5 space-y-3.5 border border-slate-50">
          <div class="flex justify-between items-center" v-for="(val, label) in { '位置': detail.location, '负责人': detail.managerName, '购买日期': detail.buyDate }" :key="label">
            <span class="text-xs font-bold text-slate-400 uppercase tracking-wide">{{ label }}</span>
            <span class="text-[14px] font-medium text-slate-600">{{ val || '-' }}</span>
          </div>
        </div>

        <div class="bg-slate-50/50 rounded-[20px] p-5 space-y-3.5 border border-slate-50">
          <div class="flex justify-between items-center pb-3 border-b border-slate-200/30">
            <span class="text-xs font-bold text-slate-400 uppercase tracking-wide">价格</span>
            <span class="text-[15px] font-bold text-slate-700">¥ {{ detail.price || '0.00' }}</span>
          </div>
          <div class="pt-1">
            <span class="text-xs font-bold text-slate-400 uppercase tracking-wide block mb-2">备注</span>
            <p class="text-[13px] text-slate-500 leading-relaxed">{{ detail.remark || '无备注信息' }}</p>
          </div>
        </div>
      </div>
    </div>

    <template #footer>
      <div class="pt-2">
        <button
            @click="visible = false"
            class="w-full py-3.5 bg-slate-800 text-white rounded-2xl font-bold text-[14px] shadow-lg shadow-slate-200 hover:bg-slate-900 active:scale-[0.98] transition-all"
        >
          完成
        </button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { getEquipmentDetail } from '@/api/equipment'

const props = defineProps({ modelValue: Boolean })
const emit = defineEmits(['update:modelValue'])

const visible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

const loading = ref(false)
const detail = reactive<any>({})
const statusMap: any = {
  0: { label: '正常' },
  1: { label: '维护中' },
  2: { label: '损坏' },
  3: { label: '报废' }
}

// Pastel Dots
const getStatusDot = (s: number) => {
  return {
    0: 'bg-emerald-300',
    1: 'bg-orange-300',
    2: 'bg-red-300',
    3: 'bg-slate-300'
  }[s] || 'bg-slate-200'
}

const loadData = async (id: number) => {
  loading.value = true
  try {
    const res = await getEquipmentDetail(id)
    Object.assign(detail, res)
  } finally {
    loading.value = false
  }
}

defineExpose({ loadData })
</script>

<style scoped>
:deep(.glass-dialog) {
  background: white;
  border-radius: 32px;
  box-shadow: 0 20px 40px rgba(0,0,0,0.05);
  padding: 24px;
  border: 1px solid rgba(241, 245, 249, 0.5); /* slate-100 */
}
:deep(.el-dialog__header) {
  margin: 0;
  padding: 0 0 10px 0;
}
:deep(.el-dialog__body) {
  padding: 0 !important;
}
:deep(.el-dialog__footer) {
  padding: 10px 0 0 0;
}
</style>