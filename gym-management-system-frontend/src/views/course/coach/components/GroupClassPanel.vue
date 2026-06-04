<template>
  <div class="group-class-panel min-h-[600px]">
    <div class="mb-8 flex justify-center">
      <el-tabs v-model="activeTab" class="clean-segmented-tabs">
        <el-tab-pane name="upcoming" label="待上课程" />
        <el-tab-pane name="history" label="历史记录" />
      </el-tabs>
    </div>

    <div v-if="activeTab === 'upcoming'">
      <el-empty v-if="scheduleList.length === 0" description="暂无待上课程" :image-size="120" class="opacity-60" />

      <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
        <div
            v-for="item in scheduleList"
            :key="item.id"
            class="group relative flex flex-col bg-white rounded-[24px] p-6 shadow-sm border border-slate-100 hover:shadow-md transition-all duration-300"
        >
          <div class="relative h-10 flex items-center justify-center mb-6 w-full">
            <h3 class="text-xl font-bold text-slate-600 tracking-tight text-center truncate max-w-[200px]">{{ item.courseName }}</h3>
            <div class="absolute right-0 top-1/2 -translate-y-1/2">
              <div class="bg-slate-50 text-slate-500 px-2 py-1 rounded-md text-[11px] font-bold border border-slate-100">
                {{ formatTimeRange(item) }}
              </div>
            </div>
          </div>

          <div class="flex justify-center mb-6">
            <p class="text-xs font-semibold text-slate-400 flex items-center gap-1.5 uppercase tracking-wider">
              <el-icon class="text-indigo-400"><Location /></el-icon>
              {{ item.location || '一号操房' }}
            </p>
          </div>

          <div class="flex-1 space-y-4 mb-8 bg-slate-50/50 rounded-[16px] p-4 border border-slate-100">
            <div class="flex items-center justify-between">
              <span class="text-xs font-medium text-slate-400">日期</span>
              <span class="text-sm font-semibold text-slate-600">{{ formatScheduleDisplay(item) }}</span>
            </div>
            <div class="flex items-center justify-between">
              <span class="text-xs font-medium text-slate-400">时长</span>
              <span class="text-sm font-semibold text-slate-600">{{ item.duration || 60 }} 分钟</span>
            </div>
            <div class="flex flex-col gap-2 mt-2">
              <div class="flex justify-between text-xs">
                <span class="font-medium text-slate-400">人数</span>
                <span class="font-bold text-slate-600">{{ item.enrolledCount }}/{{ item.capacity }}</span>
              </div>
              <div class="h-1.5 w-full bg-slate-200 rounded-full overflow-hidden">
                <div
                    class="h-full bg-blue-400 rounded-full"
                    :style="{ width: `${Math.min((item.enrolledCount / item.capacity)*100, 100)}%` }"
                ></div>
              </div>
            </div>
          </div>

          <div class="mt-auto">
            <el-button
                type="primary"
                class="!w-full !rounded-full !h-11 !text-[14px] !font-semibold !border-none !bg-slate-800 hover:!bg-slate-900 shadow-lg shadow-slate-200 transition-all"
                @click="viewStudents(item)"
            >
              管理与签到
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <div v-else>
      <div class="mt-6 bg-white rounded-[24px] border border-slate-100 p-1 overflow-hidden shadow-sm">
        <el-table
            :data="historyList"
            style="width: 100%"
            class="clean-table"
            :header-cell-style="{ background: 'var(--ff-surface-raised)', color: '#64748b', fontWeight: '600', fontSize: '12px', textTransform: 'uppercase' }"
        >
          <el-table-column prop="courseName" label="课程名称" min-width="180">
            <template #default="{ row }">
              <span class="font-semibold text-slate-600">{{ row.courseName }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="classTime" label="日期" width="240">
            <template #default="{ row }">
              <span class="text-slate-500 font-medium">{{ calculateRealDate(row) }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="enrolledCount" label="报名人数" width="120" align="center">
            <template #default="{ row }">
              <span class="bg-slate-100 text-slate-500 px-3 py-1 rounded-full text-xs font-bold">{{ row.enrolledCount }}</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="100" align="center">
            <template #default="{ row }">
              <el-button link type="primary" class="!font-semibold !text-blue-500" @click="viewStudents(row)">查看</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>

    <el-dialog
        v-model="dialogVisible"
        :title="null"
        width="650px"
        destroy-on-close
        class="clean-dialog"
        :show-close="false"
        align-center
    >
      <div class="px-2 pt-2 pb-6">
        <div class="relative flex items-center justify-center mb-8 border-b border-slate-100 pb-6 h-12">
          <div class="text-center">
            <h3 class="text-xl font-bold text-slate-700 tracking-tight">{{ currentCourse?.courseName || '课程' }}</h3>
            <p class="text-xs font-medium text-slate-400 mt-1">学员名单 · {{ studentList.length }} 已报名</p>
          </div>
          <div class="absolute right-0 top-1/2 -translate-y-1/2 flex gap-2">
            <button @click="viewStudents(currentCourse)" class="w-8 h-8 rounded-full bg-slate-50 flex items-center justify-center hover:bg-slate-100 transition-colors text-slate-500">
              <el-icon><Refresh /></el-icon>
            </button>
            <button @click="dialogVisible = false" class="w-8 h-8 rounded-full bg-slate-50 flex items-center justify-center hover:bg-slate-100 transition-colors text-slate-500">
              <span class="text-lg leading-none mb-1">×</span>
            </button>
          </div>
        </div>

        <div class="max-h-[450px] overflow-y-auto custom-scrollbar px-1">
          <div v-if="loadingStudents" class="py-10 text-center text-slate-400">加载中...</div>
          <div v-else class="space-y-3">
            <div v-for="student in studentList" :key="student.id" class="flex items-center justify-between p-4 rounded-xl bg-slate-50 hover:bg-white transition-colors duration-200 border border-transparent hover:border-slate-100 hover:shadow-sm">
              <div class="flex items-center gap-4">
                <div class="w-10 h-10 rounded-full bg-slate-200 flex items-center justify-center text-slate-500 font-bold text-sm">
                  {{ student.memberName?.[0] }}
                </div>
                <div>
                  <div class="font-bold text-slate-700">{{ student.memberName }}</div>
                  <div class="text-xs text-slate-400 font-mono mt-0.5">{{ student.memberPhone }}</div>
                </div>
              </div>

              <div class="flex items-center gap-4">
                <div v-if="student.status === '2'" class="flex items-center gap-1.5 text-emerald-600 bg-emerald-50 px-3 py-1.5 rounded-full">
                  <div class="w-1.5 h-1.5 rounded-full bg-emerald-500"></div>
                  <span class="text-xs font-bold">已签到</span>
                </div>
                <div v-else-if="student.status === '0'" class="flex items-center gap-1.5 text-blue-600 bg-blue-50 px-3 py-1.5 rounded-full">
                  <div class="w-1.5 h-1.5 rounded-full bg-blue-500"></div>
                  <span class="text-xs font-bold">待签到</span>
                </div>
                <div v-else class="text-xs text-slate-400 font-medium bg-slate-100 px-3 py-1 rounded-full">已取消</div>

                <el-button
                    v-if="student.status === '0'"
                    type="success"
                    size="small"
                    class="!rounded-full !px-4 !h-8 !font-bold !bg-emerald-500 !border-emerald-500 hover:!bg-emerald-600"
                    @click="handleCheckIn(student)"
                >
                  签到
                </el-button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { Location, Refresh } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { getCoachGroupSchedules, getCoachGroupHistory, getGroupClassStudents, checkInStudent } from '@/api/coach'

const activeTab = ref('upcoming')
const scheduleList = ref<any[]>([])
const historyList = ref<any[]>([])
const dialogVisible = ref(false)
const loadingStudents = ref(false)
const studentList = ref<any[]>([])
const currentCourse = ref<any>(null)

const loadData = async () => {
  try {
    const res: any = await getCoachGroupSchedules()
    scheduleList.value = res || []
    const histRes: any = await getCoachGroupHistory({ pageNum: 1, pageSize: 20 })
    historyList.value = histRes?.rows || []
  } catch (e) {
    console.error(e)
  }
}

const viewStudents = async (course: any) => {
  if(!course) return
  currentCourse.value = course
  dialogVisible.value = true
  loadingStudents.value = true
  try {
    const res: any = await getGroupClassStudents(course.id)
    studentList.value = res || []
  } finally {
    loadingStudents.value = false
  }
}

const handleCheckIn = async (student: any) => {
  try {
    // 假设传递的是 enrollmentId
    await checkInStudent(student.id)
    ElMessage.success('签到成功')
    viewStudents(currentCourse.value)
  } catch (e) {
    console.error(e)
  }
}

// === 新增/修改的格式化工具 ===

// 显示：周X (用于待上课程列表)
const formatScheduleDisplay = (item: any) => {
  if (item.dayOfWeekName) {
    return item.dayOfWeekName; // 仅显示 "周一"
  }
  // 降级：显示完整日期
  return formatDate(item.classTime);
}

// 显示：HH:mm - HH:mm (用于右上角标签)
const formatTimeRange = (item: any) => {
  if (item.startTime && item.endTime) {
    return `${item.startTime} - ${item.endTime}`;
  }
  return formatTime(item.classTime);
}

// 计算具体日期 (用于历史记录)
const calculateRealDate = (row: any) => {
  // 历史记录本身应该就是具体的 classTime，直接格式化即可
  // 如果是那种周期性的记录被归档到历史里，这里也可以尝试显示具体日期
  // 因为历史记录通常对应数据库中已经发生的记录，classTime 应该是准确的过去时间
  return formatDateTime(row.classTime);
}

const formatDateTime = (val: string) => {
  if (!val) return ''
  const date = new Date(val)
  return date.toLocaleString('zh-CN', { month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' })
}
const formatDate = (val: string) => {
  if (!val) return ''
  return new Date(val).toLocaleDateString('zh-CN', { month: '2-digit', day: '2-digit', weekday: 'short' })
}
const formatTime = (val: string) => {
  if (!val) return ''
  return new Date(val).toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
}

onMounted(() => {
  loadData()
})
</script>

<style lang="scss" scoped>
:deep(.clean-segmented-tabs) {
  .el-tabs__header {
    margin-bottom: 0;
    .el-tabs__nav-wrap::after { display: none; }
    .el-tabs__active-bar { display: none; }

    .el-tabs__nav {
      background: #f1f5f9;
      border-radius: 99px;
      padding: 4px;
    }

    .el-tabs__item {
      padding: 0 32px !important;
      height: 36px;
      line-height: 36px;
      border-radius: 99px;
      color: #64748b;
      font-weight: 600;
      border: none !important;

      &.is-active {
        background: var(--ff-surface);
        color: #334155;
        box-shadow: 0 2px 4px rgba(0,0,0,0.05);
      }
    }
  }
}

:deep(.clean-dialog) {
  border-radius: 24px !important;
  background: var(--ff-surface) !important;
  box-shadow: 0 24px 64px rgba(0,0,0,0.1) !important;

  .el-dialog__header { display: none; }
  .el-dialog__body { padding: 32px; }
}

.custom-scrollbar::-webkit-scrollbar {
  width: 4px;
}
.custom-scrollbar::-webkit-scrollbar-track {
  background: transparent;
}
.custom-scrollbar::-webkit-scrollbar-thumb {
  background: var(--ff-border-strong);
  border-radius: 4px;
}
</style>