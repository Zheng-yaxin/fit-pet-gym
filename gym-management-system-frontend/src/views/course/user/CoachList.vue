<template>
  <div class="coach-container">
    <div class="header-glass">
      <h2 class="page-title">私教预约</h2>
      <span class="subtitle">Personal Training</span>
    </div>

    <div v-if="coachList.length === 0" class="empty-state">
      <div class="empty-icon">🧘🏻‍♂️</div>
      <p>暂无教练信息</p>
    </div>

    <div v-else class="coach-grid">
      <div v-for="coach in coachList" :key="coach.id" class="coach-card slate-panel group">

        <div class="coach-header">
          <div class="avatar-wrapper">
            <img :src="coach.avatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'" class="avatar-img" />
          </div>
          <div class="coach-info">
            <h3 class="coach-name">{{ coach.name }}</h3>
            <span class="coach-badge">{{ coach.specialties || '全能教练' }}</span>
          </div>
        </div>

        <div class="coach-stats">
          <div class="stat-item">
            <span class="stat-label">经验</span>
            <span class="stat-value">{{ coach.experienceYears }}<small>年</small></span>
          </div>
          <div class="stat-divider"></div>
          <div class="stat-item">
            <span class="stat-label">费用</span>
            <span class="stat-value text-slate-700">¥{{ coach.hourlyRate }}<small>/h</small></span>
          </div>
        </div>

        <div class="coach-bio">
          <p>{{ coach.bio || '暂无简介' }}</p>
        </div>

        <div class="action-row mt-auto">
          <button class="btn-slate secondary" @click="handleConsult(coach)">在线咨询</button>
          <button class="btn-slate primary" @click="handleOpenBooking(coach)">预约课程</button>
        </div>
      </div>
    </div>

    <Teleport to="body">
      <Transition name="sheet-fade">
        <div v-if="bookingVisible" class="sheet-overlay">
          <div class="sheet-backdrop" @click="bookingVisible = false"></div>

          <div class="sheet-modal animate-slide-up">
            <div class="sheet-header">
              <div class="sheet-handle"></div>
              <div class="header-content relative flex justify-center items-center h-8">
                <span class="modal-title absolute left-0 right-0 text-center pointer-events-none">预约课程</span>
                <button class="close-btn absolute right-0" @click="bookingVisible = false">✕</button>
              </div>
              <div class="coach-summary text-center mt-2" v-if="currentCoach">
                <span class="summary-name">与 {{ currentCoach.name }} 教练</span>
              </div>
            </div>

            <div class="sheet-body custom-scrollbar">
              <div class="section-label">选择日期</div>
              <div class="date-picker-wrapper">
                <el-date-picker
                    v-model="selectedDate"
                    type="date"
                    placeholder="请选择日期"
                    format="YYYY-MM-DD"
                    value-format="YYYY-MM-DD"
                    :disabled-date="disabledDate"
                    @change="fetchSlots"
                    :clearable="false"
                    class="slate-date-picker"
                    popper-class="slate-popper"
                />
              </div>

              <div class="section-label mt-6">选择时间段</div>
              <div v-loading="slotsLoading" class="slots-container min-h-[150px]">

                <div class="legend">
                  <span class="legend-item"><i class="dot available"></i>可预约</span>
                  <span class="legend-item"><i class="dot booked"></i>已占用</span>
                  <span class="legend-item"><i class="dot rest"></i>休息</span>
                </div>

                <div v-if="availableSlots.length > 0" class="slots-grid">
                  <div
                      v-for="slot in availableSlots"
                      :key="slot.startTime"
                      class="time-capsule"
                      :class="[getSlotClass(slot.status), getStatusTagClass(slot.status)]"
                      @click="slot.status === 0 && handleBook(slot)"
                  >
                    <span class="time">{{ slot.startTime }}</span>
                    <span class="status-text">{{ slot.statusText }}</span>
                  </div>
                </div>

                <div v-else class="empty-slots">
                  <span>📅</span>
                  <p>该日期暂无可用时段</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </Transition>
    </Teleport>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getPublicCoachList, getCoachAvailableSlots, bookCoachSlot } from '@/api/coach'

const router = useRouter()
const coachList = ref<any[]>([])
const bookingVisible = ref(false)
const currentCoach = ref<any>(null)
const selectedDate = ref('')
const availableSlots = ref<any[]>([])
const slotsLoading = ref(false)

// 初始化加载教练列表
onMounted(async () => {
  try {
    const res = await getPublicCoachList()
    if (res) {
      coachList.value = res.data || (Array.isArray(res) ? res : [])
    }
  } catch (error) {
    console.error(error)
  }
})

// 禁用过去的时间
const disabledDate = (time: Date) => {
  return time.getTime() < Date.now() - 8.64e7
}

// 打开预约弹窗
const handleOpenBooking = (coach: any) => {
  currentCoach.value = coach
  selectedDate.value = new Date().toISOString().split('T')[0]
  bookingVisible.value = true
  fetchSlots()
}

// 获取并生成时间段（核心修改部分）
const fetchSlots = async () => {
  if (!currentCoach.value || !selectedDate.value) return
  slotsLoading.value = true
  try {
    // 1. 获取后端返回的“特殊状态”时间段 (包含已预约、休息中等)
    const res = await getCoachAvailableSlots(currentCoach.value.id, selectedDate.value)
    const backendData = res.data || []

    // 2. 在前端生成默认的每日时间网格 (例如 09:00 - 22:00)
    // 这样即使后端返回空数据，也会显示默认可预约的时间段
    const startHour = 9
    const endHour = 22
    const generatedSlots = []

    for (let i = startHour; i < endHour; i++) {
      const currentStartStr = `${i.toString().padStart(2, '0')}:00`
      const currentEndStr = `${(i + 1).toString().padStart(2, '0')}:00`

      // 默认初始化为：可预约 (status: 0)
      let slot = {
        startTime: currentStartStr,
        endTime: currentEndStr,
        status: 0,
        statusText: '可预约',
        id: 0 // 默认为0，表示这是一个自动生成的空闲时段
      }

      // 3. 将后端数据合并到生成的网格中
      // 检查当前生成的时段是否被后端返回的数据覆盖（例如该时段教练在休息或已被约）
      const matchedRecord = backendData.find((item: any) => {
        // 简单的时间匹配逻辑：提取小时数进行比较
        // 后端可能返回 "HH:mm:ss" 或 "HH:mm"
        const itemStartHour = parseInt(item.startTime.split(':')[0])
        const itemEndHour = parseInt(item.endTime.split(':')[0])
        const currentHour = i

        // 如果当前小时落在后端记录的区间内 [start, end)
        return currentHour >= itemStartHour && currentHour < itemEndHour
      })

      if (matchedRecord) {
        slot.status = Number(matchedRecord.status)
        slot.id = matchedRecord.id || 0
        // 根据状态设置文本
        if (slot.status === 1) slot.statusText = '已被约'
        else if (slot.status === 2) slot.statusText = '休息中'
        else if (slot.status === 3) slot.statusText = '已取消'
      }

      generatedSlots.push(slot)
    }

    availableSlots.value = generatedSlots

  } catch (error) {
    console.error(error)
    ElMessage.error('获取预约信息失败')
  } finally {
    slotsLoading.value = false
  }
}

// 样式逻辑
const getSlotClass = (status: number) => {
  switch (status) {
    case 0: // 可预约
      return 'available'
    case 1: // 已被约
      return 'booked'
    case 2: // 休息中
      return 'rest'
    default:
      return ''
  }
}

const getStatusTagClass = (status: number) => {
  // Keeping for compatibility, but logic moved to CSS classes above
  return ''
}

// 确认预约
const handleBook = (slot: any) => {
  ElMessageBox.confirm(
      `确认预约 ${currentCoach.value.name} 教练 ${selectedDate.value} ${slot.startTime}-${slot.endTime} 的课程吗？\n费用将从余额扣除。`,
      '预约确认',
      { confirmButtonText: '确定', cancelButtonText: '取消', type: 'primary' }
  ).then(async () => {
    try {
      await bookCoachSlot({
        coachId: currentCoach.value.id,
        slotId: slot.id || 0, // 动态生成的slot没有ID，传0即可
        date: selectedDate.value,
        startTime: slot.startTime,
        endTime: slot.endTime
      })
      ElMessage.success('预约成功！')

      // 预约成功后刷新列表，更新状态
      fetchSlots()

      ElMessageBox.confirm('预约成功，是否现在咨询教练？', '提示', {
        confirmButtonText: '去咨询',
        cancelButtonText: '稍后',
      }).then(() => handleConsult(currentCoach.value)).catch(() => {})
    } catch (error) {
      // 错误由 request 拦截器统一处理
    }
  })
}

// 跳转到聊天咨询
const handleConsult = (coach: any) => {
  router.push({
    name: 'Chat',
    query: { targetId: coach.id, targetName: coach.name, role: 'COACH', avatar: coach.avatar }
  })
}
</script>

<style scoped lang="scss">
/* --- Base Variables & Mixins --- */
:root {
  --bg-slate: var(--ff-surface-raised); /* slate-50 */
  --text-primary: #475569; /* slate-600 */
  --text-secondary: #94A3B8; /* slate-400 */
}

.coach-container {
  padding: 24px;
  min-height: 100vh;
  background-color: var(--bg-slate);
  font-family: var(--ff-font-ui);
}

/* --- Header --- */
.header-glass {
  margin-bottom: 32px;
  padding-bottom: 16px;
  border-bottom: 1px solid #E2E8F0; /* slate-200 */
  text-align: center;
}
.page-title {
  font-size: 28px; font-weight: 700; color: var(--text-primary); letter-spacing: -0.5px;
}
.subtitle {
  font-size: 13px; font-weight: 600; color: var(--text-secondary); text-transform: uppercase; letter-spacing: 0.5px;
}

/* --- Grid & Cards --- */
.coach-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 24px;
}

.slate-panel {
  background: var(--ff-surface);
  border-radius: 24px;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05); /* very soft shadow */
  padding: 24px;
  position: relative;
  overflow: hidden;
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
  display: flex; flex-direction: column;
  border: 1px solid transparent;

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.05);
  }
}

.coach-header {
  display: flex; flex-direction: column; align-items: center; text-align: center;
  margin-bottom: 20px;
}

.avatar-wrapper {
  width: 100px; height: 100px; margin-bottom: 16px;
  border-radius: 50%; padding: 4px;
  background: var(--ff-surface-raised); /* slate-50 */
  box-shadow: none;
}
.avatar-img {
  width: 100%; height: 100%; object-fit: cover; border-radius: 50%;
}

.coach-name { font-size: 20px; font-weight: 700; color: var(--text-primary); margin-bottom: 4px; }
.coach-badge {
  display: inline-block; padding: 4px 12px;
  background: #F1F5F9; /* slate-100 */
  border-radius: 100px;
  font-size: 11px; font-weight: 600; color: #64748B; /* slate-500 */
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.coach-stats {
  display: flex; justify-content: center; align-items: center;
  margin-bottom: 20px;
  padding: 12px; background: var(--ff-surface-raised); /* slate-50 */
  border-radius: 16px;
  border: 1px solid #F1F5F9;
}
.stat-item { text-align: center; flex: 1; }
.stat-label { font-size: 10px; color: var(--text-secondary); display: block; margin-bottom: 2px; uppercase: true; letter-spacing: 0.05em; }
.stat-value {
  font-size: 16px; font-weight: 700; color: var(--text-primary); font-feature-settings: "tnum";
  small { font-size: 11px; font-weight: 500; color: var(--text-secondary); margin-left: 1px; }
}
.stat-divider { width: 1px; height: 20px; background: #E2E8F0; /* slate-200 */ }

.coach-bio {
  flex: 1; font-size: 13px; color: #64748B; /* slate-500 */
  line-height: 1.6;
  margin-bottom: 24px; text-align: center;
  display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden;
}

.action-row {
  display: flex; gap: 12px;
}
.btn-slate {
  flex: 1; padding: 12px; border-radius: 12px;
  font-size: 13px; font-weight: 600; border: none; cursor: pointer;
  transition: all 0.2s;

  &.primary {
    background: #334155; /* slate-700 */
    color: var(--ff-text);
    &:hover { background: #1E293B; transform: scale(1.02); }
  }
  &.secondary {
    background: #F1F5F9; /* slate-100 */
    color: #475569;
    &:hover { background: #E2E8F0; }
  }
}

.empty-state {
  text-align: center; padding: 60px; color: var(--text-secondary);
  .empty-icon { font-size: 48px; margin-bottom: 16px; opacity: 0.5; }
}

/* --- Sheet Modal --- */
.sheet-overlay {
  position: fixed; inset: 0; z-index: 100;
  display: flex; align-items: center; justify-content: center;
}
.sheet-backdrop {
  position: absolute; inset: 0;
  background: rgba(0,0,0,0.2); backdrop-filter: blur(4px);
}
.sheet-modal {
  position: relative; z-index: 10;
  width: 100%; max-width: 440px;
  background: var(--ff-surface);
  border-radius: 32px;
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 8px 10px -6px rgba(0, 0, 0, 0.1);
  display: flex; flex-direction: column;
  max-height: 85vh;
  margin: 16px;
  border: 1px solid #F1F5F9;
}

.sheet-header {
  padding: 24px 24px 16px; position: relative;
  border-bottom: 1px solid #F1F5F9;
}
.sheet-handle {
  width: 36px; height: 4px; background: #E2E8F0; border-radius: 10px;
  margin: -10px auto 16px;
}
.modal-title { font-size: 18px; font-weight: 700; color: var(--text-primary); }
.close-btn {
  width: 32px; height: 32px; border-radius: 50%;
  background: var(--ff-surface-raised); border: none; font-size: 14px;
  color: #94A3B8; cursor: pointer;
  display: flex; align-items: center; justify-content: center;
  &:hover { background: #F1F5F9; color: #64748B; }
}
.coach-summary { margin-top: 4px; font-size: 13px; color: var(--text-secondary); }

.sheet-body { padding: 24px; overflow-y: auto; }
.section-label {
  font-size: 12px; font-weight: 700; color: var(--text-secondary); text-transform: uppercase;
  margin-bottom: 12px; letter-spacing: 0.05em;
}

/* Date Picker Overrides */
.date-picker-wrapper :deep(.el-input__wrapper) {
  background: var(--ff-surface-raised) !important;
  box-shadow: none !important;
  border-radius: 12px;
  height: 44px;
  border: 1px solid transparent;
  &:hover { background: #F1F5F9 !important; }
  &.is-focus { background: var(--ff-surface) !important; box-shadow: 0 0 0 1px var(--ff-border-strong) !important; }
}

/* Time Slots */
.slots-grid {
  display: grid; grid-template-columns: repeat(4, 1fr); gap: 10px;
}
.time-capsule {
  padding: 12px 6px; border-radius: 12px;
  display: flex; flex-direction: column; align-items: center; justify-content: center;
  cursor: pointer; transition: all 0.2s; border: 1px solid transparent;

  .time { font-size: 14px; font-weight: 600; font-feature-settings: "tnum"; }
  .status-text { font-size: 10px; margin-top: 2px; }

  /* States */
  &.available {
    background: #EFF6FF; /* blue-50 */
    color: #3B82F6; /* blue-500 */
    &:hover { background: #DBEAFE; transform: scale(1.02); }
  }
  &.booked {
    background: #F1F5F9; color: #94A3B8; cursor: not-allowed;
    .time { text-decoration: line-through; }
  }
  &.rest {
    background: var(--ff-surface-raised); color: var(--ff-border-strong); cursor: not-allowed;
  }
}

.legend {
  display: flex; justify-content: flex-end; gap: 12px; margin-bottom: 12px;
}
.legend-item { font-size: 11px; color: var(--text-secondary); display: flex; align-items: center; gap: 4px; }
.dot { width: 6px; height: 6px; border-radius: 50%; display: inline-block; }
.dot.available { background: #3B82F6; }
.dot.booked { background: #94A3B8; }
.dot.rest { background: #E2E8F0; }

.empty-slots {
  text-align: center; padding: 40px; color: var(--text-secondary); font-size: 13px;
  span { font-size: 32px; display: block; margin-bottom: 8px; }
}

/* Animations */
.animate-slide-up { animation: slideUp 0.4s cubic-bezier(0.19, 1, 0.22, 1); }
@keyframes slideUp {
  from { opacity: 0; transform: translateY(40px) scale(0.95); }
  to { opacity: 1; transform: translateY(0) scale(1); }
}
.sheet-fade-enter-active, .sheet-fade-leave-active { transition: opacity 0.3s; }
.sheet-fade-enter-from, .sheet-fade-leave-to { opacity: 0; }

.custom-scrollbar::-webkit-scrollbar { width: 0; }
</style>