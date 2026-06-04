<template>
  <Teleport to="body">
    <div v-if="visible" class="fixed inset-0 z-[100] flex items-center justify-center p-6">
      <div class="absolute inset-0 bg-slate-200/40 backdrop-blur-sm transition-opacity" @click="handleClose"></div>

      <div class="relative w-full max-w-[360px] bg-white rounded-[36px] shadow-2xl border border-slate-100 overflow-hidden animate-pop-in">

        <div class="relative flex items-center justify-center pt-6 pb-2">
          <h3 class="font-bold text-lg text-slate-600 tracking-tight">Account Top-up</h3>
        </div>

        <div class="px-8 pb-4 text-center">
          <p class="text-slate-400 text-[11px] font-medium tracking-wider uppercase">{{ member?.name }} • ID {{ member?.id }}</p>
        </div>

        <div class="mx-6 p-6 rounded-3xl bg-emerald-50 border border-emerald-100 text-center mb-2">
          <p class="text-[9px] text-emerald-600/60 uppercase tracking-[0.2em] mb-1">Current Balance</p>
          <span class="text-3xl font-bold font-mono tracking-tight text-emerald-600">¥ {{ member?.balance || 0 }}</span>
        </div>

        <div class="p-8 space-y-6 pt-4">
          <div class="text-center">
            <label class="text-[11px] font-bold text-slate-400 uppercase tracking-widest mb-3 block">Top-up Amount</label>
            <div class="relative inline-block w-full">
              <span class="absolute left-6 top-1/2 -translate-y-1/2 text-slate-400 text-2xl font-bold">¥</span>
              <input
                  v-model.number="amount"
                  type="number"
                  class="w-full h-16 pl-12 pr-6 bg-slate-50 hover:bg-white focus:bg-white rounded-2xl outline-none font-bold text-3xl text-slate-600 text-center shadow-sm border border-transparent focus:border-emerald-200 focus:ring-4 focus:ring-emerald-50 transition-all placeholder:text-slate-200"
                  placeholder="0"
              />
            </div>
          </div>

          <div>
            <label class="text-[11px] font-bold text-slate-400 uppercase tracking-widest mb-2 block pl-1">Remark</label>
            <input v-model="remark" class="w-full h-12 px-4 bg-slate-50 rounded-2xl text-[13px] font-medium outline-none border border-transparent focus:bg-white focus:shadow-sm focus:ring-2 focus:ring-slate-100 transition-all text-center placeholder:text-slate-300 text-slate-600" placeholder="Optional note" />
          </div>

          <button @click="handleSubmit" :disabled="loading || !amount" class="w-full h-14 bg-emerald-500 hover:bg-emerald-600 active:scale-[0.98] text-white rounded-full font-bold text-[17px] shadow-lg shadow-emerald-200 transition-all disabled:opacity-50 disabled:shadow-none flex items-center justify-center gap-2">
            <span v-if="loading" class="w-5 h-5 border-2 border-white/30 border-t-white rounded-full animate-spin"></span>
            <span v-else>Confirm Pay</span>
          </button>
        </div>
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
const loading = ref(false)
const amount = ref<number>(100)
const remark = ref('')

watch(() => props.modelValue, (val) => {
  visible.value = val
  if(!val) { amount.value = 100; remark.value = '' }
})

const handleClose = () => { visible.value = false; emit('update:modelValue', false) }
const handleSubmit = async () => {
  if(!props.member?.id || !amount.value) return
  loading.value = true
  try {
    await rechargeMember({ memberId: props.member.id, amount: amount.value, remark: remark.value || '后台充值' })
    ElMessage.success('充值成功')
    emit('success'); handleClose()
  } catch(e) { ElMessage.error('充值失败') }
  finally { loading.value = false }
}
</script>

<style scoped>
.animate-pop-in { animation: popIn 0.5s cubic-bezier(0.175, 0.885, 0.32, 1.275); }
@keyframes popIn { from { transform: scale(0.9); opacity: 0; } to { transform: scale(1); opacity: 1; } }
</style>