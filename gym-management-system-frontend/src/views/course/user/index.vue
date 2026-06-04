<template>
  <div class="page-container">
    <header class="sticky-header">
      <div class="header-content relative flex items-center justify-center h-10">
        <div class="absolute left-0">
          <button @click="router.push('/home')" class="icon-btn">
            <ArrowLeft :size="20" />
          </button>
        </div>
        <h1 class="page-title text-center">选课中心</h1>
      </div>

      <div class="segment-wrapper">
        <div class="segment-control">
          <div class="segment-bg" :style="segmentStyle"></div>
          <button
              v-for="tab in tabs"
              :key="tab.id"
              @click="activeTab = tab.id; handleTabChange(tab.id)"
              class="segment-item"
              :class="{ active: activeTab === tab.id }"
          >
            <component :is="tab.icon" :size="15" class="tab-icon" />
            <span>{{ tab.label }}</span>
          </button>
        </div>
      </div>
    </header>

    <main class="content-area custom-scrollbar">

      <Transition name="fade-slide" mode="out-in">
        <div v-if="activeTab === 'group'" key="group" class="tab-pane">
          <div class="filter-scroll no-scrollbar">
            <button
                class="filter-chip"
                :class="{ active: !filterCourseId }"
                @click="filterCourseId = undefined; loadGroupSchedules()"
            >
              全部课程
            </button>
            <button
                v-for="item in courseTypeList"
                :key="item.id"
                class="filter-chip"
                :class="{ active: filterCourseId === item.id }"
                @click="filterCourseId = item.id; loadGroupSchedules()"
            >
              {{ item.name }}
            </button>
          </div>

          <div v-if="loading" class="loading-state"><div class="spinner"></div></div>

          <div v-else-if="groupScheduleList.length === 0" class="empty-state">
            <div class="empty-icon">🤸</div>
            <p>暂无可预约团课</p>
          </div>

          <div v-else class="course-grid">
            <div
                v-for="item in groupScheduleList"
                :key="item.id"
                class="course-card slate-panel flex flex-col h-full"
            >
              <div class="image-section">
                <img :src="item.courseImage || 'https://images.unsplash.com/photo-1534438327276-14e5300c3a48?q=80&w=1000&auto=format&fit=crop'" class="course-img" />
                <div class="overlay-gradient"></div>
                <div class="status-capsule" :class="item.remainingSlots > 0 ? 'available' : 'full'">
                  {{ item.remainingSlots > 0 ? `剩 ${item.remainingSlots} 名额` : '已满员' }}
                </div>
                <div class="price-capsule">¥{{ item.price }}</div>
              </div>

              <div class="info-section flex-1 flex flex-col">
                <div class="title-row mb-2 text-center">
                  <h3 class="course-title mx-auto">{{ item.courseName }}</h3>
                </div>

                <div class="flex justify-center mb-4">
                  <div class="coach-pill">
                    <User :size="10" /> {{ item.coachName }}
                  </div>
                </div>

                <div class="meta-row justify-center">
                  <div class="meta-item">
                    <div class="icon-box blue"><Calendar :size="12" /></div>
                    <span>{{ formatScheduleDisplay(item) }}</span>
                  </div>
                  <div class="meta-item">
                    <div class="icon-box purple"><Clock :size="12" /></div>
                    <span>{{ item.duration }}分钟</span>
                  </div>
                </div>

                <div class="action-section mt-auto pt-4 border-t border-slate-50">
                  <button
                      v-if="item.enrolled"
                      class="action-btn cancel"
                      @click="handleCancelGroup(item.enrollmentId)"
                  >
                    取消预约
                  </button>
                  <button
                      v-else
                      class="action-btn primary"
                      :disabled="item.remainingSlots <= 0"
                      @click="handleEnrollGroup(item.id)"
                  >
                    {{ item.remainingSlots > 0 ? '立即预约' : '已满员' }}
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div v-else-if="activeTab === 'private'" key="private" class="tab-pane">
          <div class="coach-grid-layout">
            <div
                v-for="coach in coachList"
                :key="coach.id"
                class="coach-card-simple slate-panel"
                @click="openPrivateBooking(coach)"
            >
              <div class="simple-avatar">
                <img :src="coach.avatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'" />
              </div>
              <h3 class="simple-name">{{ coach.name }}</h3>
              <p class="simple-role">{{ coach.specialties || '私人教练' }}</p>

              <div class="simple-actions">
                <button @click.stop="openPrivateBooking(coach)" class="icon-action-btn primary">
                  <Calendar :size="16" />
                </button>
                <button @click.stop="handleChat(coach)" class="icon-action-btn secondary">
                  <MessageCircle :size="16" />
                </button>
              </div>
            </div>

            <div v-if="coachList.length === 0 && !loading" class="empty-state">
              <div class="empty-icon">🧘‍♀️</div>
              <p>暂无私教信息</p>
            </div>
          </div>
        </div>

        <div v-else-if="activeTab === 'my'" key="my" class="tab-pane">
          <div class="sub-nav-wrapper">
            <div class="sub-nav">
              <button
                  v-for="type in ['group', 'private']"
                  :key="type"
                  @click="setMyCourseType(type as CourseRecordType)"
                  class="sub-nav-item"
                  :class="{ active: myCourseType === type }"
              >
                {{ type === 'group' ? '团课记录' : '私教记录' }}
              </button>
            </div>
          </div>

          <div v-if="loading" class="loading-state"><div class="spinner"></div></div>

          <div v-else class="list-container">
            <template v-if="myCourseType === 'group'">
              <div v-for="row in myGroupEnrollments" :key="row.id" class="list-item slate-panel">
                <div class="item-left">
                  <h4 class="item-title">{{ row.courseName || row.course?.name || '未知课程' }}</h4>
                  <div class="item-meta">
                    <Calendar :size="12" /> {{ calculateRealDate(row) }}
                  </div>
                </div>
                <div class="item-right">
                  <span class="status-badge" :class="getStatusColorText(row.status)">
                    {{ getStatusText(row.status) }}
                  </span>
                  <button v-if="row.status === '0' || row.status === 0" @click="handleCancelGroup(row.id)" class="cancel-link">
                    取消
                  </button>
                </div>
              </div>
              <div v-if="myGroupEnrollments.length === 0" class="empty-list-text">暂无团课记录</div>
            </template>

            <template v-if="myCourseType === 'private'">
              <div v-for="row in myPrivateBookings" :key="row.id" class="list-item slate-panel">
                <div class="item-left">
                  <h4 class="item-title">私教: {{ row.coachName }}</h4>
                  <div class="item-meta">
                    <Clock :size="12" /> {{ formatDateWithWeek(row.date) }} {{ row.startTime }}
                  </div>
                </div>
                <div class="item-right vertical">
                  <span class="status-text" :class="getPrivateStatusColor(row.status)">
                    {{ getPrivateStatusText(row.status) }}
                  </span>
                  <div class="btn-group flex gap-2">
                    <button v-if="row.status <= 1" @click="handleCancelPrivate(row.id)" class="text-btn danger">取消</button>
                    <button @click="handleChat({id: row.coachId, name: row.coachName})" class="text-btn primary">咨询</button>
                  </div>
                </div>
              </div>
              <div v-if="myPrivateBookings.length === 0" class="empty-list-text">暂无私教预约</div>
            </template>
          </div>
        </div>
      </Transition>

    </main>

    <Teleport to="body">
      <Transition name="sheet-slide">
        <div v-if="bookingDialogVisible" class="sheet-container">
          <div class="sheet-backdrop" @click="bookingDialogVisible = false"></div>
          <div class="sheet-content">
            <div class="handle-bar"></div>

            <div class="sheet-header-simple relative flex justify-center items-center h-8 mb-6">
              <div class="absolute inset-0 flex flex-col items-center justify-center pointer-events-none">
                <h3 class="sheet-title">预约私教</h3>
                <p class="sheet-subtitle mt-1">与 {{ currentBookingCoach?.name }} 教练</p>
              </div>
              <button class="absolute right-0 w-8 h-8 rounded-full bg-slate-50 text-slate-400 hover:bg-slate-100 flex items-center justify-center pointer-events-auto" @click="bookingDialogVisible = false">✕</button>
            </div>

            <div class="sheet-form mt-8">
              <div class="form-label">选择日期</div>
              <div class="input-wrapper">
                <input
                    v-model="bookingDate"
                    type="date"
                    class="slate-input"
                    @change="loadCoachSlots"
                />
              </div>

              <div class="form-label mt-6">选择时段</div>
              <div v-if="slotsLoading" class="loading-state h-20"><div class="spinner"></div></div>
              <div v-else class="time-grid custom-scrollbar">
                <button
                    v-for="slot in availableSlots"
                    :key="slot.id || slot.startTime"
                    :disabled="slot.status !== 0 && slot.status !== '0' && slot.available === false"
                    class="time-btn"
                    :class="(slot.status === 0 || slot.status === '0' || slot.available !== false) ? 'available' : 'unavailable'"
                    @click="confirmPrivateBooking(slot)"
                >
                  {{ slot.startTime.slice(0, 5) }}
                </button>
                <div v-if="availableSlots.length === 0 && bookingDate" class="no-slots">
                  暂无可用时段
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
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft, Calendar, User, Clock, MessageCircle, Layers } from 'lucide-vue-next'
import dayjs from 'dayjs'
import AppleButton from '@/components/ui/AppleButton.vue' // Keeping logical import, template uses standard HTML
import {
  getMemberCourseList, getMemberAvailableSchedules, getMemberEnrollments, memberEnrollCourse, memberCancelEnrollment
} from '@/api/course'
import {
  getPublicCoachList, getCoachAvailableSlots, bookCoachSlot,
  getMyPrivateBookings, cancelPrivateBooking
} from '@/api/coach'

const router = useRouter()
type CourseTab = 'group' | 'private' | 'my'
type CourseRecordType = 'group' | 'private'

const activeTab = ref<CourseTab>('group')
const loading = ref(false)

// Tabs Config
const tabs = [
  { id: 'group', label: '团课', icon: Calendar },
  { id: 'private', label: '私教', icon: User },
  { id: 'my', label: '我的', icon: Layers }
] as const

const segmentStyle = computed(() => {
  const idx = tabs.findIndex(t => t.id === activeTab.value)
  return { transform: `translateX(${idx * 100}%)` }
})

// State Variables
const courseTypeList = ref<any[]>([])
const groupScheduleList = ref<any[]>([])
const coachList = ref<any[]>([])
const myGroupEnrollments = ref<any[]>([])
const myPrivateBookings = ref<any[]>([])
const filterCourseId = ref<number | undefined>()
const myCourseType = ref<CourseRecordType>('group')
const bookingDialogVisible = ref(false)
const currentBookingCoach = ref<any>(null)
const bookingDate = ref('')
const availableSlots = ref<any[]>([])
const slotsLoading = ref(false)

// Tab Change Handler
const handleTabChange = async (tabId: CourseTab) => {
  if (tabId === 'group') await loadGroupSchedules()
  else if (tabId === 'private') await loadCoaches()
  else if (tabId === 'my') await loadMyCourses()
}

// Load Group Schedules
const loadGroupSchedules = async () => {
  loading.value = true
  try {
    const res: any = await getMemberAvailableSchedules({ courseId: filterCourseId.value })
    groupScheduleList.value = res || []
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

// Load Coaches
const loadCoaches = async () => {
  loading.value = true
  try {
    const res: any = await getPublicCoachList()
    // 兼容后端直接返回数组或返回 R 对象
    coachList.value = res?.data || (Array.isArray(res) ? res : [])
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const setMyCourseType = async (type: CourseRecordType) => {
  myCourseType.value = type
  await loadMyCourses()
}

// Load My Courses
const loadMyCourses = async () => {
  loading.value = true
  try {
    if (myCourseType.value === 'group') {
      const res: any = await getMemberEnrollments({ pageNum: 1, pageSize: 50 })
      // 后端返回的是分页对象 {total, rows, code, msg}
      myGroupEnrollments.value = res?.rows || res || []
      console.log('My group enrollments:', myGroupEnrollments.value)
    } else {
      const res: any = await getMyPrivateBookings()
      myPrivateBookings.value = res?.data || (Array.isArray(res) ? res : [])
      console.log('My private bookings:', myPrivateBookings.value)
    }
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

// Enroll Group Course
const handleEnrollGroup = async (scheduleId: number) => {
  try {
    await memberEnrollCourse(scheduleId)
    ElMessage.success('预约成功！')
    await loadGroupSchedules()
  } catch (e) {
    console.error(e)
  }
}

// Cancel Group Enrollment
const handleCancelGroup = async (enrollmentId: number) => {
  ElMessageBox.confirm('确定取消预约吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await memberCancelEnrollment(enrollmentId)
      ElMessage.success('已取消预约')
      if (activeTab.value === 'group') {
        await loadGroupSchedules()
      } else {
        await loadMyCourses()
      }
    } catch (e) {
      console.error(e)
    }
  }).catch(() => {})
}

// Open Private Booking Dialog
const openPrivateBooking = (coach: any) => {
  currentBookingCoach.value = coach
  // 修复：使用 dayjs 获取本地日期，避免 toISOString 转 UTC 导致的日期偏差
  bookingDate.value = dayjs().format('YYYY-MM-DD')
  bookingDialogVisible.value = true
  loadCoachSlots()
}

// Load Coach Available Slots
const loadCoachSlots = async () => {
  if (!currentBookingCoach.value || !bookingDate.value) return
  slotsLoading.value = true
  try {
    const res: any = await getCoachAvailableSlots(currentBookingCoach.value.id, bookingDate.value)
    availableSlots.value = res?.data || res || []
    console.log('Available slots:', availableSlots.value)
  } catch (e) {
    console.error(e)
    availableSlots.value = []
  } finally {
    slotsLoading.value = false
  }
}

// Confirm Private Booking
const confirmPrivateBooking = async (slot: any) => {
  ElMessageBox.confirm(
      `确认预约 ${currentBookingCoach.value?.name} 教练 ${bookingDate.value} ${slot.startTime}-${slot.endTime} 的课程吗？\n费用将从余额扣除。`,
      '预约确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'info'
      }
  ).then(async () => {
    try {
      await bookCoachSlot({
        coachId: currentBookingCoach.value.id,
        slotId: slot.id || 0,
        date: bookingDate.value,
        startTime: slot.startTime,
        endTime: slot.endTime
      })
      ElMessage.success('预约成功！')
      bookingDialogVisible.value = false

      // 询问是否咨询教练
      ElMessageBox.confirm('预约成功，是否现在咨询教练？', '提示', {
        confirmButtonText: '去咨询',
        cancelButtonText: '稍后',
        type: 'success'
      }).then(() => {
        handleChat(currentBookingCoach.value)
      }).catch(() => {})
    } catch (e) {
      console.error(e)
    }
  }).catch(() => {})
}

// Cancel Private Booking
const handleCancelPrivate = async (bookingId: number) => {
  ElMessageBox.confirm('确定取消预约吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await cancelPrivateBooking(bookingId)
      ElMessage.success('已取消预约')
      await loadMyCourses()
    } catch (e) {
      console.error(e)
    }
  }).catch(() => {})
}

// Navigate to Chat
const handleChat = (coach: any) => {
  router.push({
    name: 'Chat',
    query: {
      targetId: coach.id,
      targetName: coach.name,
      role: 'COACH',
      avatar: coach.avatar
    }
  })
}

// Helper Functions
const formatScheduleDisplay = (item: any) => {
  const weekMap = ['', '周一', '周二', '周三', '周四', '周五', '周六', '周日']
  if (item.dayOfWeek) return `${weekMap[item.dayOfWeek]} ${item.startTime}-${item.endTime}`
  return item.classTime?.slice(5, 16) || '待定'
}

const calculateRealDate = (row: any) => {
  // 尝试多个可能的日期字段
  if (row.classTime) return row.classTime.slice(0, 16)
  if (row.scheduleTime) return row.scheduleTime.slice(0, 16)
  if (row.createTime) return row.createTime.slice(0, 16)
  if (row.schedule?.classTime) return row.schedule.classTime.slice(0, 16)
  return '待定'
}

const getStatusColorText = (s: string | number) => {
  const status = String(s)
  return { '0': 'text-blue-500', '1': 'text-slate-400', '2': 'text-emerald-500' }[status] || 'text-slate-400'
}

const getStatusText = (s: string | number) => {
  const status = String(s)
  return { '0': '已报名', '1': '已取消', '2': '已完成' }[status] || `状态${status}`
}

const getPrivateStatusColor = (status: number | string) => {
  const s = Number(status)
  return { 0: 'text-blue-500', 1: 'text-emerald-500', 2: 'text-slate-400' }[s] || 'text-slate-400'
}

const getPrivateStatusText = (status: number | string) => {
  const s = Number(status)
  return { 0: '待确认', 1: '已确认', 2: '已完成', 3: '已取消' }[s] || `状态${status}`
}

const formatDateWithWeek = (date: string) => {
  if (!date) return ''
  const d = new Date(date)
  const weekMap = ['日', '一', '二', '三', '四', '五', '六']
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  const week = weekMap[d.getDay()]
  return `${month}-${day} (周${week})`
}

// Initialize
onMounted(async () => {
  // Load course types
  try {
    const res: any = await getMemberCourseList()
    courseTypeList.value = res || []
  } catch (e) {
    console.error(e)
  }

  // Load initial data
  await loadGroupSchedules()
})
</script>

<style scoped lang="scss">
:root {
  --bg-slate: var(--ff-surface-raised); /* slate-50 */
  --text-primary: #475569; /* slate-600 */
  --text-secondary: #94A3B8; /* slate-400 */
}

.page-container {
  min-height: 100vh;
  background-color: var(--bg-slate);
  font-family: var(--ff-font-ui);
  padding-bottom: 80px;
}

/* --- Sticky Header & Segment --- */
.sticky-header {
  position: sticky; top: 0; z-index: 50;
  background: rgba(255,255,255,0.9);
  backdrop-filter: saturate(180%) blur(20px);
  border-bottom: 1px solid #F1F5F9;
  padding: 12px 16px;
}

.icon-btn {
  width: 32px; height: 32px; border-radius: 50%;
  background: #F1F5F9; border: none;
  display: flex; align-items: center; justify-content: center;
  color: var(--text-primary); cursor: pointer;
  &:hover { background: #E2E8F0; }
}
.page-title { font-size: 18px; font-weight: 700; color: var(--text-primary); margin: 0; }

.segment-wrapper { display: flex; justify-content: center; margin-top: 12px; }
.segment-control {
  position: relative; display: flex;
  background: #F1F5F9; /* slate-100 */
  border-radius: 12px; padding: 4px;
  height: 44px; width: 100%; max-width: 400px;
}
.segment-bg {
  position: absolute; top: 4px; bottom: 4px; left: 4px;
  width: calc((100% - 8px) / 3);
  background: var(--ff-surface); border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
  transition: transform 0.3s cubic-bezier(0.2, 0.8, 0.2, 1);
}
.segment-item {
  flex: 1; position: relative; z-index: 1;
  font-size: 13px; font-weight: 600; color: var(--text-secondary);
  display: flex; align-items: center; justify-content: center; gap: 6px;
  border: none; background: none; cursor: pointer;
  transition: color 0.2s;
  &.active { color: var(--text-primary); }
}

/* --- Content Area --- */
.content-area { padding: 24px 16px; max-width: 1024px; margin: 0 auto; }

/* Filter Chips */
.filter-scroll {
  display: flex; gap: 8px; overflow-x: auto; margin-bottom: 24px;
  padding-bottom: 4px;
}
.filter-chip {
  padding: 8px 16px; border-radius: 100px;
  background: var(--ff-surface); color: var(--text-secondary);
  font-size: 13px; font-weight: 600; border: none;
  box-shadow: 0 1px 3px rgba(0,0,0,0.05);
  white-space: nowrap; transition: all 0.2s;
  &.active { background: #475569; color: var(--ff-text); transform: scale(1.02); }
}

/* Course Grid */
.course-grid {
  display: grid; grid-template-columns: repeat(auto-fill, minmax(300px, 1fr)); gap: 20px;
}
.slate-panel {
  background: var(--ff-surface); border-radius: 24px; overflow: hidden;
  box-shadow: 0 4px 6px -1px rgba(0,0,0,0.02);
  border: 1px solid transparent;
  transition: all 0.2s;
  &:hover { transform: translateY(-4px); box-shadow: 0 10px 15px -3px rgba(0,0,0,0.05); }
}

/* Course Card Details */
.image-section { position: relative; height: 160px; }
.course-img { width: 100%; height: 100%; object-fit: cover; }
.overlay-gradient {
  position: absolute; inset: 0;
  background: linear-gradient(to top, rgba(0,0,0,0.4), transparent);
}
.status-capsule {
  position: absolute; top: 12px; right: 12px;
  font-size: 10px; font-weight: 700; color: var(--ff-text);
  padding: 4px 10px; border-radius: 100px;
  backdrop-filter: blur(8px);
  &.available { background: rgba(52, 211, 153, 0.9); /* emerald-400 */ }
  &.full { background: rgba(15, 23, 42, 0.6); }
}
.price-capsule {
  position: absolute; bottom: 12px; left: 12px;
  font-size: 16px; font-weight: 700; color: var(--ff-text); text-shadow: 0 2px 4px rgba(0,0,0,0.2);
}

.info-section { padding: 20px; }
.course-title { font-size: 17px; font-weight: 700; color: var(--text-primary); }
.coach-pill {
  font-size: 11px; color: var(--text-secondary); background: var(--ff-surface-raised);
  padding: 4px 10px; border-radius: 100px; display: flex; align-items: center; gap: 4px;
}

.meta-row { display: flex; gap: 12px; margin-bottom: 20px; }
.meta-item { display: flex; align-items: center; gap: 6px; font-size: 12px; color: var(--text-secondary); }
.icon-box {
  width: 24px; height: 24px; border-radius: 8px; display: flex; align-items: center; justify-content: center;
  &.blue { background: #EFF6FF; color: #60A5FA; }
  &.purple { background: #F5F3FF; color: #A78BFA; }
}

.action-btn {
  width: 100%; padding: 12px; border-radius: 16px;
  font-size: 14px; font-weight: 600; border: none; cursor: pointer;
  transition: all 0.2s;
  &.primary { background: #334155; color: var(--ff-text); &:hover { background: #1E293B; } }
  &.cancel { background: #FEF2F2; color: #F87171; &:hover { background: #FEE2E2; } }
  &:disabled { opacity: 0.5; background: var(--ff-border-strong); cursor: not-allowed; }
}

/* Coach Grid */
.coach-grid-layout { display: grid; grid-template-columns: repeat(2, 1fr); gap: 16px; }
.coach-card-simple {
  padding: 24px; display: flex; flex-direction: column; align-items: center; text-align: center;
  cursor: pointer;
}
.simple-avatar {
  width: 72px; height: 72px; border-radius: 50%; overflow: hidden; margin-bottom: 12px;
  border: 4px solid var(--ff-surface-raised);
  img { width: 100%; height: 100%; object-fit: cover; }
}
.simple-name { font-size: 16px; font-weight: 700; color: var(--text-primary); margin-bottom: 4px; }
.simple-role { font-size: 11px; color: var(--text-secondary); margin-bottom: 16px; text-transform: uppercase; letter-spacing: 0.05em; }
.simple-actions { display: flex; gap: 8px; width: 100%; }
.icon-action-btn {
  flex: 1; height: 36px; border-radius: 12px; border: none; display: flex; align-items: center; justify-content: center;
  transition: all 0.2s;
  &.primary { background: #EFF6FF; color: #60A5FA; &:hover { background: #DBEAFE; } }
  &.secondary { background: var(--ff-surface-raised); color: #94A3B8; &:hover { background: #F1F5F9; } }
}

/* My Courses List */
.sub-nav-wrapper { display: flex; justify-content: center; margin-bottom: 24px; }
.sub-nav {
  background: #F1F5F9; padding: 4px; border-radius: 12px; display: inline-flex;
}
.sub-nav-item {
  padding: 8px 20px; font-size: 13px; font-weight: 600; color: var(--text-secondary);
  border-radius: 8px; border: none; background: transparent; cursor: pointer;
  &.active { background: var(--ff-surface); color: var(--text-primary); box-shadow: 0 2px 4px rgba(0,0,0,0.05); }
}

.list-item {
  display: flex; justify-content: space-between; align-items: center;
  padding: 20px; margin-bottom: 16px;
}
.item-title { font-size: 15px; font-weight: 600; color: var(--text-primary); margin-bottom: 4px; }
.item-meta { display: flex; align-items: center; gap: 6px; font-size: 12px; color: var(--text-secondary); }
.item-right { display: flex; align-items: center; gap: 12px; }
.item-right.vertical { flex-direction: column; align-items: flex-end; gap: 8px; }

.status-badge { font-size: 12px; font-weight: 600; }
.status-text { font-size: 11px; font-weight: 600; letter-spacing: 0.02em; }
.cancel-link { font-size: 12px; color: #F87171; background: #FEF2F2; padding: 6px 12px; border-radius: 8px; border: none; cursor: pointer; }
.text-btn { font-size: 11px; font-weight: 600; background: none; border: none; cursor: pointer; }
.text-btn.danger { color: #F87171; }
.text-btn.primary { color: #60A5FA; }

/* Sheet Modal (Private) */
.sheet-container { position: fixed; inset: 0; z-index: 100; display: flex; flex-direction: column; justify-content: flex-end; }
.sheet-backdrop { position: absolute; inset: 0; background: rgba(0,0,0,0.2); backdrop-filter: blur(4px); }
.sheet-content {
  position: relative; z-index: 10; background: var(--ff-surface);
  border-top-left-radius: 32px; border-top-right-radius: 32px;
  padding: 32px 24px; padding-bottom: max(32px, env(safe-area-inset-bottom));
  box-shadow: 0 -10px 40px rgba(0,0,0,0.05);
  animation: slideUpSheet 0.4s cubic-bezier(0.19, 1, 0.22, 1);
}
.handle-bar { width: 40px; height: 4px; background: #E2E8F0; border-radius: 10px; margin: -10px auto 20px; }
.sheet-title { font-size: 20px; font-weight: 700; color: var(--text-primary); }
.sheet-subtitle { font-size: 13px; color: var(--text-secondary); }

.slate-input {
  width: 100%; padding: 16px; border-radius: 16px;
  background: var(--ff-surface-raised); border: 1px solid transparent; font-size: 16px;
  outline: none; color: var(--text-primary);
  transition: all 0.2s;
  &:focus { background: var(--ff-surface); border-color: #E2E8F0; box-shadow: 0 0 0 4px #F1F5F9; }
}

.time-grid {
  display: grid; grid-template-columns: repeat(4, 1fr); gap: 12px;
  max-height: 240px; overflow-y: auto; padding-right: 4px;
}
.time-btn {
  padding: 12px; border-radius: 12px; font-size: 14px; font-weight: 600;
  border: none; cursor: pointer; transition: all 0.2s;
  &.available { background: #EFF6FF; color: #3B82F6; &:hover { background: #DBEAFE; transform: scale(1.05); } }
  &.unavailable { background: var(--ff-surface-raised); color: var(--ff-border-strong); cursor: not-allowed; }
}

@keyframes slideUpSheet { from { transform: translateY(100%); } to { transform: translateY(0); } }
.loading-state { display: flex; justify-content: center; padding: 20px; }
.spinner { width: 24px; height: 24px; border: 3px solid #E2E8F0; border-top-color: #334155; border-radius: 50%; animation: spin 1s infinite linear; }
@keyframes spin { to { transform: rotate(360deg); } }

.empty-list-text { text-align: center; padding: 30px; font-size: 13px; color: var(--text-secondary); }
.empty-state { text-align: center; padding: 60px 0; color: var(--text-secondary); font-size: 14px; .empty-icon { font-size: 40px; margin-bottom: 10px; opacity: 0.5; } }
.custom-scrollbar::-webkit-scrollbar { width: 0; }
</style>
