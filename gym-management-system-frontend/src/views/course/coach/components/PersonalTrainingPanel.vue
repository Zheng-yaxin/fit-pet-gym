<template>
  <div class="pt-panel space-y-12 min-h-[600px]">

    <div class="relative">
      <div class="relative flex items-center justify-center h-12 mb-8">
        <div class="text-center">
          <h3 class="text-2xl font-bold text-slate-600 tracking-tight">预约申请</h3>
          <p class="text-xs font-medium text-slate-400 mt-0.5">管理新的和即将到来的课程</p>
        </div>
        <div class="absolute right-0 top-1/2 -translate-y-1/2">
          <button
              @click="loadBookings"
              class="w-10 h-10 rounded-full bg-white hover:bg-slate-50 border border-slate-100 flex items-center justify-center text-slate-500 transition-all hover:scale-105 active:scale-95"
          >
            <el-icon><Refresh /></el-icon>
          </button>
        </div>
      </div>

      <el-empty v-if="bookingList.length === 0" description="暂无待处理请求" :image-size="100" class="bg-white rounded-[32px] border border-slate-100 py-12" />

      <div v-else class="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-6">
        <div
            v-for="booking in bookingList"
            :key="booking.id"
            class="relative flex flex-col group bg-white rounded-[24px] p-6 shadow-sm border border-slate-100 transition-all duration-300 hover:shadow-md hover:border-slate-200"
        >
          <div class="relative flex justify-center items-center mb-6">
            <h4 class="font-bold text-slate-700 text-lg">{{ booking.memberName }}</h4>
            <div class="absolute right-0 top-1/2 -translate-y-1/2">
               <span
                   class="px-2.5 py-1 rounded-full text-[10px] font-bold uppercase tracking-wider"
                   :class="{
                  'bg-orange-50 text-orange-400': booking.status === 0,
                  'bg-blue-50 text-blue-500': booking.status === 1,
                  'bg-emerald-50 text-emerald-500': booking.status === 2,
                  'bg-slate-100 text-slate-400': booking.status === 3
                }"
               >
                {{ getStatusText(booking.status) }}
              </span>
            </div>
          </div>

          <div class="space-y-3 flex-1">
            <div class="flex items-center gap-3 text-sm text-slate-500 bg-slate-50 p-3 rounded-xl">
              <div class="w-8 h-8 rounded-lg bg-white flex items-center justify-center text-slate-400 shrink-0 shadow-sm">
                <el-icon><Calendar /></el-icon>
              </div>
              <div class="flex flex-col">
                <span class="text-[10px] text-slate-400 font-bold uppercase tracking-wider">日期</span>
                <span class="font-semibold text-slate-600">{{ booking.date }}</span>
              </div>
            </div>

            <div class="flex items-center gap-3 text-sm text-slate-500 bg-slate-50 p-3 rounded-xl">
              <div class="w-8 h-8 rounded-lg bg-white flex items-center justify-center text-slate-400 shrink-0 shadow-sm">
                <el-icon><Clock /></el-icon>
              </div>
              <div class="flex flex-col">
                <span class="text-[10px] text-slate-400 font-bold uppercase tracking-wider">时间</span>
                <span class="font-semibold text-slate-600">{{ booking.startTime }} - {{ booking.endTime }}</span>
              </div>
            </div>

            <div class="flex justify-center mt-4">
              <span class="text-xs text-slate-400 font-medium uppercase tracking-wide mr-2 self-end mb-1">费用</span>
              <span class="font-bold text-slate-700 text-xl">¥{{ booking.amount }}</span>
            </div>
          </div>

          <div class="flex gap-3 mt-6 pt-4 border-t border-slate-50">
            <template v-if="booking.status === 0">
              <button
                  class="flex-1 h-10 rounded-full bg-slate-800 text-white text-sm font-semibold hover:bg-slate-700 transition-colors shadow-sm"
                  @click="handleAction(booking, 'confirm')"
              >
                接受
              </button>
              <button
                  class="flex-1 h-10 rounded-full bg-white border border-slate-200 text-slate-600 text-sm font-semibold hover:bg-slate-50 transition-colors"
                  @click="handleAction(booking, 'cancel')"
              >
                拒绝
              </button>
            </template>
            <template v-if="booking.status === 1">
              <button
                  class="flex-1 h-10 rounded-full bg-blue-500 text-white text-sm font-semibold hover:bg-blue-600 transition-colors shadow-sm"
                  @click="handleAction(booking, 'complete')"
              >
                完成课程
              </button>
            </template>
            <span v-if="booking.status === 2" class="w-full text-center text-xs font-bold text-emerald-500 bg-emerald-50 py-2.5 rounded-lg">已完成</span>
            <span v-if="booking.status === 3" class="w-full text-center text-xs font-bold text-slate-400 bg-slate-100 py-2.5 rounded-lg">已取消</span>
          </div>
        </div>
      </div>
    </div>

    <div class="bg-white rounded-[32px] p-8 shadow-sm border border-slate-100">
      <div class="relative flex items-center justify-center mb-8 h-10">
        <div class="text-center">
          <h3 class="text-xl font-bold text-slate-600 tracking-tight">空闲时间管理</h3>
          <p class="text-xs text-slate-400 mt-0.5 font-medium">设置不可预约的时间段</p>
        </div>
        <div class="absolute right-0 top-1/2 -translate-y-1/2">
          <el-button
              type="primary"
              icon="Plus"
              class="!rounded-full !px-5 !h-9 !bg-slate-800 !border-none !font-semibold shadow-sm hover:!bg-slate-700"
              @click="openScheduleDialog"
          >
            设置停课
          </el-button>
        </div>
      </div>

      <div class="overflow-hidden rounded-[20px] border border-slate-100">
        <el-table
            :data="slotList"
            style="width: 100%"
            class="slate-table"
            :header-cell-style="{ background: 'var(--ff-surface-raised)', color: '#64748b', fontWeight: '600', fontSize: '12px', textTransform: 'uppercase', height: '48px', borderBottom: '1px solid #e2e8f0' }"
        >
          <el-table-column prop="date" label="日期" min-width="120" sortable>
            <template #default="{ row }">
              <span class="font-semibold text-slate-600">{{ row.date }}</span>
            </template>
          </el-table-column>
          <el-table-column label="时间段" min-width="150">
            <template #default="{ row }">
              <div class="bg-slate-50 inline-block px-3 py-1 rounded-md border border-slate-100">
                <span class="font-mono font-medium text-slate-500 text-xs">{{ row.startTime?.substring(0,5) }} - {{ row.endTime?.substring(0,5) }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="140">
            <template #default="{ row }">
              <span class="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full bg-rose-50 text-rose-500 border border-rose-100">
                <span class="w-1.5 h-1.5 rounded-full bg-rose-400"></span>
                <span class="text-[10px] font-bold uppercase">不可预约</span>
              </span>
            </template>
          </el-table-column>
          <el-table-column label="" width="80" align="center">
            <template #default="{ row }">
              <button
                  class="w-8 h-8 flex items-center justify-center rounded-full text-slate-300 hover:text-rose-500 hover:bg-rose-50 transition-colors"
                  @click="handleDeleteSlot(row)"
              >
                <el-icon><Delete /></el-icon>
              </button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>

    <el-dialog
        v-model="slotDialogVisible"
        :title="null"
        width="440px"
        class="clean-dialog"
        destroy-on-close
        align-center
    >
      <div class="pt-2 px-2 pb-2">
        <h3 class="text-xl font-bold text-slate-700 mb-2 text-center">设置停课时间</h3>
        <p class="text-sm text-slate-400 mb-8 text-center px-8">选定的时间段将在会员预约选项中隐藏。</p>

        <div class="space-y-6">
          <div class="bg-slate-50 rounded-[16px] p-4 border border-slate-100">
            <div class="flex flex-col gap-2">
              <label class="text-xs font-bold text-slate-400 uppercase tracking-wider ml-1">日期</label>
              <el-date-picker
                  v-model="slotForm.date"
                  type="date"
                  value-format="YYYY-MM-DD"
                  placeholder="选择日期"
                  class="!w-full clean-date-picker"
                  :disabled-date="disablePastDate"
              />
            </div>
          </div>

          <div class="grid grid-cols-2 gap-4">
            <div class="bg-slate-50 rounded-[16px] p-4 border border-slate-100">
              <div class="flex flex-col gap-2">
                <label class="text-xs font-bold text-slate-400 uppercase tracking-wider ml-1">开始时间</label>
                <el-time-select
                    v-model="slotForm.startTime"
                    start="06:00"
                    step="01:00"
                    end="22:00"
                    placeholder="09:00"
                    class="!w-full clean-date-picker"
                />
              </div>
            </div>
            <div class="bg-slate-50 rounded-[16px] p-4 border border-slate-100">
              <div class="flex flex-col gap-2">
                <label class="text-xs font-bold text-slate-400 uppercase tracking-wider ml-1">结束时间</label>
                <el-time-select
                    v-model="slotForm.endTime"
                    start="06:00"
                    step="01:00"
                    end="22:00"
                    :min-time="slotForm.startTime"
                    placeholder="10:00"
                    class="!w-full clean-date-picker"
                />
              </div>
            </div>
          </div>

          <div class="relative py-2">
            <div class="absolute inset-0 flex items-center">
              <div class="w-full border-t border-slate-100"></div>
            </div>
            <div class="relative flex justify-center text-xs uppercase">
              <span class="bg-white px-3 text-slate-300 font-bold">或者</span>
            </div>
          </div>

          <button
              class="w-full py-3 rounded-xl bg-rose-50 text-rose-500 font-bold text-sm hover:bg-rose-100 transition-colors border border-rose-100"
              @click="setFullDayUnavailable"
              :disabled="!slotForm.date"
              :class="{ 'opacity-50 cursor-not-allowed': !slotForm.date }"
          >
            全天停课
          </button>
        </div>

        <div class="flex gap-3 mt-8">
          <button @click="slotDialogVisible = false" class="flex-1 py-3 rounded-full bg-slate-100 text-slate-500 font-semibold hover:bg-slate-200 transition-colors">取消</button>
          <button @click="submitSlot" class="flex-1 py-3 rounded-full bg-slate-800 text-white font-semibold hover:bg-black transition-colors shadow-lg shadow-slate-200">确认</button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { Calendar, Clock, Refresh, Plus, Delete } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMySlots, addSlot, deleteSlot, getMyBookings, confirmBooking, completeBooking, cancelBooking } from '@/api/coach'

const bookingList = ref<any[]>([])
const slotList = ref<any[]>([])
const slotDialogVisible = ref(false)

const slotForm = reactive({
  date: '',
  startTime: '',
  endTime: ''
})

const loadBookings = async () => {
  try {
    const res: any = await getMyBookings()
    bookingList.value = res || []
  } catch (e) { console.error(e) }
}

const loadSlots = async () => {
  try {
    const res: any = await getMySlots({})
    slotList.value = res || []
  } catch (e) { console.error(e) }
}

const handleAction = async (booking: any, action: string) => {
  try {
    if (action === 'confirm') await confirmBooking(booking.id)
    else if (action === 'complete') {
      await ElMessageBox.confirm('确认课程已结束并完成？', '提示')
      await completeBooking(booking.id)
    }
    else if (action === 'cancel') {
      await ElMessageBox.confirm('确定拒绝/取消该预约吗？', '警告', { type: 'warning' })
      await cancelBooking(booking.id)
    }
    ElMessage.success('操作成功')
    loadBookings()
  } catch (e) {
    if(e !== 'cancel') ElMessage.error('操作失败')
  }
}

const handleDeleteSlot = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定删除该不可预约时间段吗？', '提示', { type: 'warning' })
    await deleteSlot(row.id)
    ElMessage.success('已删除')
    loadSlots()
  } catch (e) {}
}

const openScheduleDialog = () => {
  slotForm.date = ''
  slotForm.startTime = ''
  slotForm.endTime = ''
  slotDialogVisible.value = true
}

const disablePastDate = (time: Date) => time.getTime() < Date.now() - 8.64e7

const setFullDayUnavailable = async () => {
  if (!slotForm.date) {
    return ElMessage.warning('请先选择日期')
  }

  try {
    await ElMessageBox.confirm(
        `确定将 ${slotForm.date} 设置为全天停课吗？这将设置从 06:00 到 22:00 的所有时段为不可预约。`,
        '全天停课确认',
        { type: 'warning' }
    )

    const payload = {
      date: slotForm.date,
      startTime: '06:00:00',
      endTime: '22:00:00',
    }

    await addSlot(payload)
    ElMessage.success('全天停课设置成功')
    slotDialogVisible.value = false
    loadSlots()
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error('设置失败，请检查是否有时间冲突')
    }
  }
}

const submitSlot = async () => {
  if (!slotForm.date) {
    return ElMessage.warning('请选择日期')
  }

  if (!slotForm.startTime || !slotForm.endTime) {
    return ElMessage.warning('请填写完整时间段')
  }

  try {
    const payload = {
      date: slotForm.date,
      startTime: slotForm.startTime.length === 5 ? slotForm.startTime + ':00' : slotForm.startTime,
      endTime: slotForm.endTime.length === 5 ? slotForm.endTime + ':00' : slotForm.endTime,
    }
    await addSlot(payload)
    ElMessage.success('设置成功')
    slotDialogVisible.value = false
    loadSlots()
  } catch (e) {
    ElMessage.error('设置失败，请检查时间冲突')
  }
}

const getStatusText = (status: number) => {
  // 0:待确认, 1:已确认, 2:已完成, 3:已取消
  const map: any = { 0: '待确认', 1: '已确认', 2: '已完成', 3: '已取消' }
  return map[status] || '未知'
}
const getStatusType = (status: number) => {
  const map: any = { 0: 'warning', 1: 'primary', 2: 'success', 3: 'info' }
  return map[status]
}

onMounted(() => {
  loadBookings()
  loadSlots()
})
</script>

<style lang="scss" scoped>
:deep(.slate-table) {
  --el-table-border-color: transparent;
  --el-table-header-bg-color: var(--ff-surface-raised);
  --el-table-row-hover-bg-color: #f1f5f9;

  th.el-table__cell {
    background-color: var(--ff-surface-raised) !important;
  }
}

:deep(.clean-dialog) {
  border-radius: 24px !important;
  box-shadow: 0 20px 40px -10px rgba(0,0,0,0.1) !important;

  .el-dialog__header { display: none; }
  .el-dialog__body { padding: 32px; }
}

:deep(.clean-date-picker) {
  .el-input__wrapper {
    background-color: transparent !important;
    box-shadow: none !important;
    padding: 0;
  }
  .el-input__inner {
    font-weight: 600;
    font-size: 16px;
    color: #334155;
  }
  .el-input__prefix { display: none; }
}
</style>
