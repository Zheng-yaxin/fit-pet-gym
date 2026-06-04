<template>
  <Teleport to="body">
    <Transition name="modal-fade">
      <div v-if="visible" class="fixed inset-0 z-[60] flex items-center justify-center p-4 sm:p-6">
        <div class="absolute inset-0 bg-slate-200/50 backdrop-blur-sm transition-opacity duration-500" @click="handleClose"></div>

        <div class="relative w-full max-w-[420px] bg-white rounded-[40px] shadow-[0_40px_100px_-20px_rgba(71,85,105,0.15)] overflow-hidden transform transition-all flex flex-col max-h-[90vh]">

          <div class="px-8 py-6 flex items-center justify-center relative z-20 sticky top-0 bg-white/90 backdrop-blur-xl">
            <h3 class="text-[18px] font-bold text-slate-600 tracking-tight">
              {{ viewMode === 'view' ? '会员卡' : (viewMode === 'buy' ? '办理新卡' : '卡片续费') }}
            </h3>

            <button
                @click="handleClose"
                class="absolute right-8 w-8 h-8 rounded-full bg-slate-50 flex items-center justify-center text-slate-400 hover:bg-slate-100 hover:text-slate-600 transition-all active:scale-90"
            >
              <el-icon :size="16"><Close /></el-icon>
            </button>
          </div>

          <div class="flex-1 overflow-y-auto px-8 pb-8 custom-scrollbar">

            <div v-if="currentCard && viewMode === 'view'" class="space-y-8 animate-fade-in">

              <div class="relative w-full aspect-[1.586/1] rounded-[24px] overflow-hidden shadow-[0_20px_40px_-10px_rgba(0,0,0,0.2)] transform transition-transform hover:scale-[1.01] duration-500 group">
                <div class="absolute inset-0 bg-slate-800"></div>
                <div class="absolute top-0 right-0 w-[120%] h-[120%] bg-gradient-to-br from-blue-500/20 via-purple-500/20 to-transparent blur-[60px] opacity-60"></div>

                <div class="relative z-10 p-7 h-full flex flex-col justify-between text-white">
                  <div class="flex justify-between items-start">
                    <div class="flex items-center gap-3">
                      <div class="w-10 h-10 rounded-xl bg-white/10 backdrop-blur-md flex items-center justify-center shadow-inner">
                        <el-icon class="text-xl"><CreditCard /></el-icon>
                      </div>
                      <div>
                        <div class="font-bold text-sm tracking-widest opacity-90">GYM PASS</div>
                        <div class="text-[10px] font-medium opacity-50 tracking-wide uppercase">Universal Access</div>
                      </div>
                    </div>
                    <span class="font-mono text-sm opacity-60 tracking-wider">
                      {{ currentCard.cardNo }}
                    </span>
                  </div>

                  <div class="space-y-2">
                    <div class="text-[32px] font-bold tracking-tight text-white drop-shadow-sm">
                      {{ currentCard.cardType }}
                    </div>
                    <div class="flex items-center gap-2">
                       <span class="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full bg-white/10 backdrop-blur-md text-[11px] font-bold uppercase tracking-wider">
                        <span class="w-1.5 h-1.5 rounded-full" :class="isExpired(currentCard.expireDate) ? 'bg-red-300' : 'bg-emerald-300'"></span>
                        {{ isExpired(currentCard.expireDate) ? '已过期' : '有效期内' }}
                      </span>
                    </div>
                  </div>

                  <div class="flex justify-between items-end">
                    <div>
                      <div class="text-[10px] text-white/40 uppercase tracking-widest font-bold mb-1">VALID THRU</div>
                      <div class="font-mono text-base font-medium tracking-wide">{{ formatDate(currentCard.expireDate) }}</div>
                    </div>
                    <div v-if="currentCard.cardType === '次卡'" class="text-right">
                      <div class="text-[10px] text-white/40 uppercase tracking-widest font-bold mb-1">BALANCE</div>
                      <div class="font-mono text-base font-medium tracking-wide">{{ currentCard.remainingTimes }} <span class="text-xs">PTS</span></div>
                    </div>
                  </div>
                </div>
              </div>

              <div class="grid grid-cols-2 gap-4">
                <button
                    @click="initRenew"
                    class="flex items-center justify-center gap-2 py-4 bg-slate-800 text-white rounded-[20px] text-[15px] font-bold hover:scale-[1.02] active:scale-[0.98] transition-all shadow-lg shadow-slate-200"
                >
                  <el-icon><Refresh /></el-icon> 续费
                </button>
                <button
                    v-if="currentCard.status === '0'"
                    @click="handleReportLoss"
                    class="flex items-center justify-center gap-2 py-4 bg-white text-rose-500 rounded-[20px] text-[15px] font-bold shadow-sm ring-1 ring-slate-100 hover:shadow-md hover:bg-rose-50 transition-all active:scale-[0.98]"
                >
                  <el-icon><Warning /></el-icon> 挂失
                </button>
              </div>

              <div class="text-center pt-2">
                <button @click="initBuy" class="text-xs font-medium text-slate-400 hover:text-blue-500 transition-colors">
                  办理其他卡种
                </button>
              </div>
            </div>

            <div v-else class="space-y-8 animate-slide-up">

              <div class="bg-slate-50 rounded-[18px] p-1 flex relative">
                <div class="absolute inset-y-1 w-[50%] bg-white rounded-[14px] shadow-sm transition-all duration-300" :class="viewMode === 'view' ? 'left-1' : 'left-[calc(50%-4px)] translate-x-1'"></div>

                <button
                    v-if="viewMode === 'view'"
                    class="relative z-10 flex-1 py-2 text-sm font-bold text-slate-600"
                >
                  详情
                </button>
                <div v-else class="relative z-10 flex-1 py-2 text-center text-sm font-bold text-slate-600">
                  {{ viewMode === 'buy' ? '新购方案' : '续费方案' }}
                </div>
                <button
                    v-if="currentCard"
                    @click="viewMode = 'view'"
                    class="relative z-10 flex-1 py-2 text-xs font-semibold text-slate-400 hover:text-slate-600 transition-colors"
                >
                  取消
                </button>
              </div>

              <div class="space-y-6">

                <div v-if="viewMode === 'buy'" class="space-y-3">
                  <label class="block text-xs font-bold text-slate-400 uppercase tracking-widest ml-1">选择类型</label>
                  <div class="grid grid-cols-3 gap-3">
                    <button
                        v-for="type in ['年卡', '月卡', '次卡']"
                        :key="type"
                        @click="form.cardType = type"
                        class="relative py-3.5 rounded-2xl text-[15px] font-bold transition-all overflow-hidden group"
                        :class="form.cardType === type ? 'bg-slate-800 text-white shadow-lg shadow-slate-200' : 'bg-slate-50 text-slate-500 hover:bg-slate-100'"
                    >
                      <span class="relative z-10">{{ type }}</span>
                    </button>
                  </div>
                  <div class="px-4 py-3 bg-blue-50 rounded-2xl flex items-start gap-3">
                    <el-icon class="text-blue-500 mt-0.5"><InfoFilled /></el-icon>
                    <span class="text-xs font-medium text-blue-700/80 leading-relaxed">
                      <template v-if="form.cardType === '年卡'">尊享 365 天无限次通行权益</template>
                      <template v-else-if="form.cardType === '月卡'">30 天短期无限次体验</template>
                      <template v-else>有效期 1 年，灵活扣次使用</template>
                    </span>
                  </div>
                </div>

                <div class="bg-white rounded-[24px] p-6 shadow-sm ring-1 ring-slate-100 space-y-5">
                  <div v-if="form.cardType === '年卡' || form.cardType === '月卡'">
                    <div class="flex justify-between mb-4">
                      <label class="text-[15px] font-bold text-slate-600">购买时长</label>
                      <span class="text-sm font-medium text-slate-400">{{ form.cardType === '年卡' ? '年' : '月' }}</span>
                    </div>
                    <div class="flex items-center justify-center gap-5">
                      <button @click="form.duration = Math.max(1, (form.duration || 1) - 1)" class="w-10 h-10 rounded-full bg-slate-50 flex items-center justify-center text-slate-600 hover:bg-slate-100 transition-colors text-xl pb-1">-</button>
                      <input v-model.number="form.duration" type="number" class="w-24 text-center font-bold text-2xl outline-none bg-transparent text-slate-700" />
                      <button @click="form.duration = (form.duration || 1) + 1" class="w-10 h-10 rounded-full bg-slate-50 flex items-center justify-center text-slate-600 hover:bg-slate-100 transition-colors text-xl pb-1">+</button>
                    </div>
                  </div>

                  <div v-if="form.cardType === '次卡'">
                    <div class="flex justify-between mb-4">
                      <label class="text-[15px] font-bold text-slate-600">充值次数</label>
                      <span class="text-sm font-medium text-slate-400">次</span>
                    </div>
                    <div class="flex items-center gap-5">
                      <button @click="form.times = Math.max(10, (form.times || 10) - 10)" class="w-10 h-10 rounded-full bg-slate-50 flex items-center justify-center text-slate-600 hover:bg-slate-100 transition-colors text-xl pb-1">-</button>
                      <input v-model.number="form.times" type="number" class="flex-1 text-center font-bold text-2xl outline-none bg-transparent text-slate-700" step="10" />
                      <button @click="form.times = (form.times || 10) + 10" class="w-10 h-10 rounded-full bg-slate-50 flex items-center justify-center text-slate-600 hover:bg-slate-100 transition-colors text-xl pb-1">+</button>
                    </div>
                  </div>
                </div>

                <div>
                  <input
                      v-model="form.remark"
                      class="w-[92%] mx-auto block p-4 bg-slate-50 rounded-[20px] outline-none focus:bg-white focus:ring-2 focus:ring-blue-100 transition-all text-[15px] text-slate-600 placeholder-slate-400"
                      placeholder="添加备注信息 (选填)"
                  />
                </div>

                <div class="pt-4">
                  <div class="flex justify-between items-center mb-5 px-2">
                    <span class="text-sm font-medium text-slate-500">支付总额</span>
                    <div class="flex items-baseline gap-1">
                      <span class="text-sm font-bold text-slate-600">¥</span>
                      <span class="text-3xl font-bold text-slate-700 tracking-tight">{{ calculatedAmount }}</span>
                    </div>
                  </div>

                  <button
                      @click="viewMode === 'buy' ? handleBuyCard() : handleRenew()"
                      :disabled="loading || calculatedAmount <= 0"
                      class="group w-full py-4 bg-slate-800 text-white rounded-[24px] font-bold text-[17px] shadow-xl shadow-slate-200 hover:scale-[1.02] active:scale-[0.98] transition-all disabled:opacity-50 disabled:scale-100 flex items-center justify-center gap-3"
                  >
                    <span v-if="loading" class="w-5 h-5 border-2 border-white/30 border-t-white rounded-full animate-spin"></span>
                    <span>支付</span>
                    <div class="bg-white/20 rounded-full p-1 group-hover:translate-x-1 transition-transform">
                      <el-icon><ArrowRight /></el-icon>
                    </div>
                  </button>
                </div>

              </div>
            </div>

          </div>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup lang="ts">
import { ref, reactive, watch, computed } from 'vue'
import { Close, CreditCard, Refresh, Warning, InfoFilled, ArrowRight } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getValidCard, buyMemberCard, renewMemberCard, reportLossCard,
  type Member, type MemberCard, type CardBuyDto
} from '@/api/member'

const props = defineProps<{ modelValue: boolean; member?: Member | null }>()
const emit = defineEmits(['update:modelValue', 'success'])

const visible = ref(false)
const loading = ref(false)
const currentCard = ref<MemberCard | null>(null)
const viewMode = ref<'view' | 'buy' | 'renew'>('view')

const form = reactive<CardBuyDto>({
  memberId: 0,
  cardType: '年卡',
  duration: 1,
  times: 10,
  amount: 0,
  remark: ''
})

// 计算金额
const calculatedAmount = computed(() => {
  let price = 0
  const duration = form.duration || 0
  const times = form.times || 0
  if (form.cardType === '次卡') price = times * 10
  else if (form.cardType === '月卡') price = duration * 100
  else if (form.cardType === '年卡') price = duration * 1000
  return price
})

// 辅助函数
const formatDate = (dateString?: string) => dateString ? new Date(dateString).toLocaleDateString('zh-CN') : ''
const isExpired = (dateString?: string) => !dateString || new Date(dateString) < new Date()

// 获取卡片信息
const fetchValidCard = async (memberId: number) => {
  try {
    const res: any = await getValidCard(memberId)
    currentCard.value = res.data
    // 如果有卡，默认查看；无卡，默认购买
    viewMode.value = res.data ? 'view' : 'buy'
  } catch (e) {
    currentCard.value = null
    viewMode.value = 'buy'
  }
}

// 监听弹窗打开
watch(() => props.modelValue, (val) => {
  visible.value = val
  if (val && props.member?.id) {
    form.memberId = props.member.id
    fetchValidCard(props.member.id)
  }
})

// 初始化操作
const initBuy = () => { viewMode.value = 'buy'; form.cardType = '年卡'; form.duration = 1; form.times = 10 }
const initRenew = () => {
  if (currentCard.value) {
    viewMode.value = 'renew'; form.cardType = currentCard.value.cardType;
    form.duration = 1; form.times = 10
  }
}
const handleClose = () => { visible.value = false; emit('update:modelValue', false) }

// 业务逻辑
const handleBuyCard = async () => {
  if (!form.memberId) return ElMessage.error('会员信息缺失')
  form.amount = calculatedAmount.value
  loading.value = true
  try {
    await buyMemberCard(form)
    ElMessage.success('办理成功')
    await fetchValidCard(form.memberId)
    emit('success')
  } catch (e: any) {
    ElMessage.error(e.response?.data?.msg || '余额不足或办理失败')
  } finally { loading.value = false }
}

const handleRenew = async () => {
  if (!currentCard.value) return
  const renewData = {
    memberId: form.memberId,
    cardType: currentCard.value.cardType,
    duration: form.duration,
    times: form.times,
    amount: calculatedAmount.value,
    remark: `自助续费: ¥${calculatedAmount.value}`
  }
  loading.value = true
  try {
    await renewMemberCard(renewData)
    ElMessage.success('续费成功')
    await fetchValidCard(form.memberId)
    emit('success')
    viewMode.value = 'view'
  } catch (e: any) {
    ElMessage.error(e.response?.data?.msg || '余额不足或续费失败')
  } finally { loading.value = false }
}

const handleReportLoss = async () => {
  if (!currentCard.value) return
  ElMessageBox.confirm('挂失后卡片将立即失效，确定继续吗？', '挂失确认', {
    confirmButtonText: '确定挂失', cancelButtonText: '取消', type: 'warning'
  }).then(async () => {
    loading.value = true
    try {
      await reportLossCard(currentCard.value!.id!)
      ElMessage.success('挂失成功')
      await fetchValidCard(form.memberId)
      emit('success')
    } catch (e: any) { ElMessage.error('操作失败') }
    finally { loading.value = false }
  }).catch(() => {})
}
</script>

<style scoped>
.modal-fade-enter-active, .modal-fade-leave-active { transition: opacity 0.4s ease; }
.modal-fade-enter-from, .modal-fade-leave-to { opacity: 0; }
.animate-fade-in { animation: fadeIn 0.6s ease-out forwards; }
.animate-slide-up { animation: slideUp 0.6s cubic-bezier(0.16, 1, 0.3, 1) forwards; }
@keyframes fadeIn { from { opacity: 0; } to { opacity: 1; } }
@keyframes slideUp { from { opacity: 0; transform: translateY(20px); } to { opacity: 1; transform: translateY(0); } }

.custom-scrollbar::-webkit-scrollbar { width: 0px; }
</style>