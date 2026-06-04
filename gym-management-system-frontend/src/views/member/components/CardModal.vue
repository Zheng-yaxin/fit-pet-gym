<template>
  <Teleport to="body">
    <Transition name="zoom-fade">
      <div v-if="visible" class="fixed inset-0 z-[100] flex items-center justify-center p-6">
        <div class="absolute inset-0 bg-slate-200/40 backdrop-blur-sm transition-opacity duration-500" @click="handleClose"></div>

        <div class="relative w-full max-w-[420px] bg-white rounded-[32px] shadow-2xl border border-slate-100 overflow-hidden flex flex-col max-h-[90vh] transition-all duration-500">

          <div class="relative flex items-center justify-center px-6 py-6 border-b border-slate-50 z-10">
            <div class="text-center">
              <h3 class="text-xl font-bold text-slate-600 tracking-tight">Wallet</h3>
              <p class="text-xs font-medium text-slate-400 mt-0.5">Manage membership cards</p>
            </div>
            <button @click="handleClose" class="absolute right-6 w-8 h-8 rounded-full bg-slate-50 hover:bg-slate-100 flex items-center justify-center text-slate-400 transition-all">
              <X :size="16" stroke-width="2.5" />
            </button>
          </div>

          <div class="flex-1 overflow-y-auto custom-scrollbar px-6 py-6 space-y-6">

            <div class="flex items-center gap-4 p-4 bg-slate-50 rounded-2xl border border-slate-100/50">
              <div class="w-12 h-12 rounded-full bg-blue-100 text-blue-500 flex items-center justify-center font-bold text-lg shadow-sm">
                {{ member?.name?.[0] }}
              </div>
              <div>
                <div class="font-bold text-slate-600 text-[17px]">{{ member?.name }}</div>
                <div class="text-xs font-medium text-slate-400">Balance: <span class="text-slate-600 font-semibold">¥{{ member?.balance }}</span></div>
              </div>
            </div>

            <div v-if="currentCard" class="relative overflow-hidden rounded-[24px] bg-slate-700 text-white p-6 shadow-lg shadow-slate-300/50 transition-transform hover:scale-[1.02] duration-500">
              <div class="absolute top-0 right-0 w-48 h-48 bg-white/5 rounded-full blur-[60px] -mr-16 -mt-16 pointer-events-none"></div>
              <div class="absolute bottom-0 left-0 w-32 h-32 bg-blue-500/10 rounded-full blur-[40px] -ml-10 -mb-10 pointer-events-none"></div>

              <div class="relative z-10 flex flex-col h-32 justify-between">
                <div class="flex justify-between items-start">
                  <span class="font-bold text-xl tracking-wide text-slate-50">{{ currentCard.cardType }}</span>
                  <div class="px-2.5 py-1 bg-white/10 backdrop-blur-md rounded-lg text-[10px] font-mono tracking-wider border border-white/5 text-slate-200">
                    {{ currentCard.cardNo }}
                  </div>
                </div>
                <div class="space-y-1">
                  <div class="text-[11px] text-slate-400 uppercase tracking-widest font-bold">Valid Thru</div>
                  <div class="text-sm font-medium font-mono tracking-wide text-slate-100">{{ formatDate(currentCard.expireDate) }}</div>
                  <div v-if="currentCard.remainingTimes !== null" class="text-[11px] text-slate-300 mt-1 flex items-center gap-1">
                    <div class="w-1.5 h-1.5 bg-emerald-400 rounded-full animate-pulse"></div>
                    {{ currentCard.remainingTimes }} Credits left
                  </div>
                </div>
              </div>
            </div>

            <div v-else class="py-8 rounded-[24px] border-2 border-dashed border-slate-200 flex flex-col items-center justify-center text-slate-400 gap-2 bg-slate-50/50">
              <div class="w-12 h-12 rounded-full bg-slate-100 flex items-center justify-center">
                <div class="w-6 h-4 border-2 border-slate-300 rounded-sm"></div>
              </div>
              <span class="text-sm font-medium">No Active Card</span>
            </div>

            <div>
              <div class="flex bg-slate-100 p-1 rounded-full mb-6 relative">
                <div class="absolute inset-y-1 rounded-full bg-white shadow-sm transition-all duration-300 ease-spring"
                     :class="mode === 'buy' ? 'left-1 w-[calc(50%-4px)]' : 'left-[calc(50%+2px)] w-[calc(50%-4px)]'"></div>
                <button @click="mode = 'buy'" class="flex-1 py-2 text-[13px] font-semibold rounded-full relative z-10 transition-colors duration-200" :class="mode === 'buy' ? 'text-slate-600' : 'text-slate-400'">
                  {{ currentCard ? '办理新卡' : '发卡' }}
                </button>
                <button v-if="currentCard" @click="mode = 'renew'" class="flex-1 py-2 text-[13px] font-semibold rounded-full relative z-10 transition-colors duration-200" :class="mode === 'renew' ? 'text-slate-600' : 'text-slate-400'">
                  续费
                </button>
              </div>

              <div class="space-y-5 animate-slide-up">
                <div v-if="mode === 'buy'">
                  <label class="text-[11px] font-bold text-slate-400 uppercase tracking-widest ml-1 mb-2 block">Card Type</label>
                  <div class="flex gap-3">
                    <button v-for="t in ['年卡','月卡','次卡']" :key="t"
                            @click="form.cardType=t"
                            class="flex-1 py-3 rounded-2xl text-[13px] font-bold border transition-all duration-300"
                            :class="form.cardType===t
                              ? 'bg-slate-700 text-white border-transparent shadow-lg shadow-slate-200 scale-105'
                              : 'bg-slate-50 text-slate-600 border-transparent hover:bg-slate-100'">
                      {{t}}
                    </button>
                  </div>
                </div>

                <div class="grid grid-cols-2 gap-4">
                  <div v-if="isDurationCard">
                    <label class="text-[11px] font-bold text-slate-400 uppercase tracking-widest ml-1 mb-1.5 block">Duration</label>
                    <input v-model.number="form.duration" type="number" class="w-full h-12 bg-slate-50 hover:bg-white focus:bg-white rounded-2xl border-none outline-none text-center font-bold text-slate-600 text-lg shadow-sm transition-all ring-1 ring-transparent focus:ring-blue-100 placeholder:text-slate-300" />
                  </div>
                  <div v-if="form.cardType === '次卡' || (mode==='renew' && currentCard?.cardType==='次卡')">
                    <label class="text-[11px] font-bold text-slate-400 uppercase tracking-widest ml-1 mb-1.5 block">Times</label>
                    <input v-model.number="form.times" type="number" class="w-full h-12 bg-slate-50 hover:bg-white focus:bg-white rounded-2xl border-none outline-none text-center font-bold text-slate-600 text-lg shadow-sm transition-all ring-1 ring-transparent focus:ring-blue-100" />
                  </div>
                  <div class="col-span-2">
                    <label class="text-[11px] font-bold text-slate-400 uppercase tracking-widest ml-1 mb-1.5 block">Total Amount</label>
                    <div class="relative">
                      <span class="absolute left-4 top-1/2 -translate-y-1/2 text-slate-400 font-bold">¥</span>
                      <input v-model.number="form.amount" type="number" class="w-full h-14 pl-8 pr-4 bg-slate-50 hover:bg-white focus:bg-white rounded-2xl border-none outline-none font-bold text-slate-600 text-xl shadow-sm transition-all ring-1 ring-transparent focus:ring-blue-100" placeholder="0.00" />
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div class="p-6 pt-0 mt-auto">
            <button @click="handleSubmit" :disabled="loading" class="w-full h-14 bg-slate-800 hover:bg-slate-700 active:scale-[0.98] text-white rounded-full font-bold text-[16px] shadow-lg shadow-slate-300 transition-all flex items-center justify-center gap-2">
              <span v-if="loading" class="w-5 h-5 border-2 border-white/30 border-t-white rounded-full animate-spin"></span>
              <span v-else>{{ mode === 'buy' ? 'Confirm & Pay' : 'Renew Membership' }}</span>
            </button>
          </div>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup lang="ts">
import { computed, ref, reactive, watch } from 'vue'
import { X } from 'lucide-vue-next'
import { getValidCard, buyMemberCard, renewMemberCard, type Member, type MemberCard } from '@/api/member'
import { ElMessage } from 'element-plus'

const props = defineProps<{ modelValue: boolean; member?: Member | null }>()
const emit = defineEmits(['update:modelValue', 'success'])
const visible = ref(false)
const loading = ref(false)
const currentCard = ref<MemberCard|null>(null)
const mode = ref<'buy'|'renew'>('buy')
const form = reactive({ cardType: '年卡', duration: 1, times: 10, amount: 0 })
const activeCardType = computed(() => form.cardType || currentCard.value?.cardType || '')
const isDurationCard = computed(() => ['年卡','月卡'].includes(activeCardType.value))

watch(() => props.modelValue, (val) => {
  visible.value = val
  if(val && props.member) {
    loadCard()
    mode.value = 'buy'
    form.amount = 0
  }
})

const loadCard = async () => {
  try {
    const res = await getValidCard(props.member!.id!)
    currentCard.value = res as MemberCard | null
    if(currentCard.value) mode.value = 'renew'
  } catch { currentCard.value = null; mode.value = 'buy' }
}

const formatDate = (d?: string) => d ? d.split(' ')[0] : ''
const handleClose = () => { visible.value = false; emit('update:modelValue', false) }

const handleSubmit = async () => {
  if(!form.amount) return ElMessage.warning('请输入金额')
  loading.value = true
  try {
    const payload = { ...form, memberId: props.member!.id!, remark: '后台操作' }
    if(mode.value === 'renew' && currentCard.value) {
      payload.cardType = currentCard.value.cardType || form.cardType
      await renewMemberCard(payload)
    } else {
      await buyMemberCard(payload)
    }
    ElMessage.success('操作成功')
    emit('success'); handleClose()
  } catch(e: any) { ElMessage.error(e.response?.data?.msg || '操作失败') }
  finally { loading.value = false }
}
</script>

<style scoped>
.custom-scrollbar::-webkit-scrollbar { width: 0; }
.zoom-fade-enter-active, .zoom-fade-leave-active { transition: all 0.4s cubic-bezier(0.19, 1, 0.22, 1); }
.zoom-fade-enter-from, .zoom-fade-leave-to { opacity: 0; transform: scale(0.95); }
.ease-spring { transition-timing-function: cubic-bezier(0.175, 0.885, 0.32, 1.275); }
.animate-slide-up { animation: slideUp 0.4s cubic-bezier(0.16, 1, 0.3, 1); }
@keyframes slideUp { from { opacity: 0; transform: translateY(10px); } to { opacity: 1; transform: translateY(0); } }
</style>
