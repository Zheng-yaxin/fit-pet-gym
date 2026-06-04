<template>
  <Teleport to="body">
    <div v-if="visible" class="fixed inset-0 z-50 flex items-center justify-center p-6">
      <div class="absolute inset-0 bg-slate-200/40 backdrop-blur-sm transition-opacity duration-500" @click="handleClose"></div>

      <div class="relative w-full max-w-[340px] bg-white rounded-[36px] p-8 shadow-[0_20px_60px_-15px_rgba(71,85,105,0.1)] animate-pop-in text-center overflow-hidden">

        <div class="absolute top-0 left-1/2 -translate-x-1/2 w-48 h-48 bg-blue-300/10 rounded-full blur-[60px] pointer-events-none"></div>

        <div class="relative flex items-center justify-center mb-1">
          <h3 class="font-bold text-2xl text-slate-600 tracking-tight">充值余额</h3>
        </div>

        <p class="relative text-sm font-medium text-slate-400 mb-8">当前可用: ¥{{ member?.balance || 0 }}</p>

        <div class="relative mb-8 group">
          <div class="absolute inset-0 bg-slate-50 rounded-[24px] transform transition-transform group-hover:scale-[1.02]"></div>
          <div class="relative flex items-center justify-center h-24">
            <span class="text-3xl font-semibold text-slate-600 mr-1 mt-1">¥</span>
            <input
                v-model.number="amount" type="number"
                class="w-40 bg-transparent text-center text-5xl font-bold outline-none text-slate-600 placeholder-slate-200 font-sans tracking-tight"
                placeholder="0"
            />
          </div>
        </div>

        <div class="grid grid-cols-3 gap-3 mb-8">
          <button
              v-for="v in [100, 500, 1000]" :key="v"
              @click="amount=v"
              class="py-3 bg-white rounded-2xl text-sm font-bold text-slate-500 shadow-sm hover:shadow-md hover:text-blue-600 hover:-translate-y-0.5 transition-all active:scale-95"
              :class="amount === v ? 'ring-2 ring-blue-200 text-blue-600 bg-blue-50/50' : 'bg-slate-50'"
          >
            ¥{{ v }}
          </button>
        </div>

        <button
            @click="handleSubmit"
            :disabled="loading || !amount"
            class="w-full h-[52px] bg-slate-800 text-white rounded-full font-semibold text-[15px] hover:scale-[1.02] active:scale-[0.98] transition-all disabled:opacity-30 disabled:hover:scale-100 flex items-center justify-center gap-2 shadow-lg shadow-slate-200"
        >
          <span v-if="loading" class="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin"></span>
          <span v-else class="flex items-center gap-1.5">
            支付
          </span>
        </button>
      </div>
    </div>
  </Teleport>
</template>
<script setup lang="ts">
import { ref, watch } from 'vue'
import { rechargeMember, type Member } from '@/api/member'
import { ElMessage } from 'element-plus'

const props = defineProps<{ modelValue: boolean; member?: Member | null }>()
const emit = defineEmits(['update:modelValue', 'success'])
const visible = ref(false)
const amount = ref<number>(100)
const loading = ref(false)

watch(() => props.modelValue, (val) => visible.value = val)
const handleClose = () => { visible.value = false; emit('update:modelValue', false) }
const handleSubmit = async () => {
  loading.value = true;
  try {
    await rechargeMember({ memberId: props.member!.id!, amount: amount.value, remark: 'iOS Re' })
    ElMessage.success('充值成功'); emit('success'); handleClose();
  } catch(e) { ElMessage.error('充值失败') } finally { loading.value = false }
}
</script>
<style scoped>
.animate-pop-in { animation: popIn 0.5s cubic-bezier(0.34, 1.56, 0.64, 1); }
@keyframes popIn { from { transform: scale(0.9) translateY(10px); opacity: 0; } to { transform: scale(1) translateY(0); opacity: 1; } }
</style>