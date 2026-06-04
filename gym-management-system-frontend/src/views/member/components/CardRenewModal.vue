<template>
  <Transition name="modal-bounce">
    <div v-if="visible" class="fixed inset-0 z-50 flex items-center justify-center p-6">
      <div class="absolute inset-0 bg-slate-200/40 backdrop-blur-sm transition-opacity" @click="handleClose"></div>

      <div class="relative w-full max-w-md bg-white rounded-[32px] shadow-2xl border border-slate-100 overflow-hidden transform transition-all">

        <div class="relative flex items-center justify-center px-8 py-6 border-b border-slate-50">
          <h3 class="text-lg font-bold text-slate-600 tracking-tight">Renew Card</h3>
          <button @click="handleClose" class="absolute right-8 w-8 h-8 rounded-full bg-slate-50 hover:bg-slate-100 transition-colors flex items-center justify-center text-slate-500">
            <Close class="w-4 h-4" />
          </button>
        </div>

        <div class="p-8 space-y-6">
          <div class="bg-slate-50 p-5 rounded-[20px] border border-slate-100 flex flex-col gap-3">
            <div class="flex justify-between items-center">
              <span class="px-3 py-1 bg-white shadow-sm text-blue-500 text-[11px] font-bold rounded-full uppercase tracking-wide border border-blue-50">{{ card?.cardType }}</span>
              <span class="text-[13px] text-slate-400 font-mono tracking-wider">{{ card?.cardNo }}</span>
            </div>
            <div>
              <div class="font-bold text-slate-600 text-lg">{{ card?.memberName }}</div>
              <div class="text-xs text-slate-400 mt-0.5 font-medium">
                Expires on <span class="text-slate-600">{{ card?.expireDate?.split(' ')[0] }}</span>
              </div>
            </div>
          </div>

          <div class="space-y-4">
            <div v-if="card?.cardType === '年卡' || card?.cardType === '月卡'">
              <label class="block text-[11px] font-bold text-slate-400 uppercase tracking-widest mb-2 pl-1">
                Extension Duration ({{ card?.cardType === '年卡' ? 'Years' : 'Months' }})
              </label>
              <input
                  v-model.number="form.duration"
                  type="number"
                  min="1"
                  class="w-full h-12 px-4 bg-white border border-slate-200 rounded-2xl outline-none focus:border-blue-400 focus:ring-4 focus:ring-blue-50 transition-all text-[15px] font-semibold text-slate-600"
              />
            </div>

            <div v-if="card?.cardType === '次卡'">
              <label class="block text-[11px] font-bold text-slate-400 uppercase tracking-widest mb-2 pl-1">Add Times</label>
              <input
                  v-model.number="form.times"
                  type="number"
                  min="10"
                  step="10"
                  class="w-full h-12 px-4 bg-white border border-slate-200 rounded-2xl outline-none focus:border-blue-400 focus:ring-4 focus:ring-blue-50 transition-all text-[15px] font-semibold text-slate-600"
              />
            </div>

            <div>
              <label class="block text-[11px] font-bold text-slate-400 uppercase tracking-widest mb-2 pl-1">Renewal Cost (¥)</label>
              <input
                  v-model.number="form.amount"
                  type="number"
                  min="0"
                  class="w-full h-12 px-4 bg-white border border-slate-200 rounded-2xl outline-none focus:border-blue-400 focus:ring-4 focus:ring-blue-50 transition-all text-[15px] font-semibold text-slate-600"
                  placeholder="0.00"
              />
            </div>

            <div>
              <label class="block text-[11px] font-bold text-slate-400 uppercase tracking-widest mb-2 pl-1">Notes</label>
              <textarea
                  v-model="form.remark"
                  rows="2"
                  class="w-full p-4 bg-white border border-slate-200 rounded-2xl outline-none focus:border-blue-400 focus:ring-4 focus:ring-blue-50 transition-all text-[15px] resize-none text-slate-600"
                  placeholder="Add a remark..."
              ></textarea>
            </div>
          </div>
        </div>

        <div class="p-8 pt-2 flex gap-4">
          <button @click="handleClose" class="flex-1 h-12 rounded-full bg-slate-100 text-slate-500 font-bold text-[15px] hover:bg-slate-200 transition-colors">
            Cancel
          </button>
          <button
              @click="handleSubmit"
              :disabled="loading || !form.amount"
              class="flex-1 h-12 rounded-full bg-slate-800 text-white font-bold text-[15px] shadow-lg shadow-slate-300 hover:scale-[1.02] active:scale-[0.98] transition-all flex items-center justify-center"
          >
            <span v-if="loading" class="w-4 h-4 border-2 border-white/30 border-t-white rounded-full mr-2 animate-spin"></span>
            Confirm
          </button>
        </div>
      </div>
    </div>
  </Transition>
</template>

<script setup lang="ts">
import { ref, reactive, watch } from 'vue'
import { Close } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { renewMemberCard, type MemberCard, type CardBuyDto } from '@/api/member'

const props = defineProps<{
  modelValue: boolean
  card: MemberCard | null
}>()

const emit = defineEmits(['update:modelValue', 'success'])

const visible = ref(false)
const loading = ref(false)

const form = reactive<CardBuyDto>({
  memberId: 0,
  cardType: '',
  duration: 1,
  times: 10,
  amount: 0,
  remark: ''
})

watch(() => props.modelValue, (val) => {
  visible.value = val
  if (val && props.card) {
    // 初始化表单
    form.memberId = props.card.memberId
    form.cardType = props.card.cardType
    form.duration = 1
    form.times = 10
    form.amount = 0
    form.remark = ''
  }
})

const handleClose = () => {
  visible.value = false
  emit('update:modelValue', false)
}

const handleSubmit = async () => {
  if (!form.amount || form.amount < 0) return ElMessage.warning('Please enter a valid amount')

  loading.value = true
  try {
    await renewMemberCard(form)
    ElMessage.success('Card renewed successfully')
    emit('success')
    handleClose()
  } catch (e: any) {
    ElMessage.error(e.response?.data?.msg || 'Failed to renew card')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.modal-bounce-enter-active, .modal-bounce-leave-active { transition: all 0.4s cubic-bezier(0.34, 1.56, 0.64, 1); }
.modal-bounce-enter-from, .modal-bounce-leave-to { opacity: 0; transform: scale(0.9) translateY(20px); }
</style>