<template>
  <div class="flex flex-col h-full bg-slate-50 relative font-sans overflow-hidden selection:bg-slate-200">
    <header class="h-24 flex-shrink-0 flex items-center justify-between px-10 z-20">
      <div>
        <h1 class="text-3xl font-bold text-slate-600 tracking-tight">Enrollments</h1>
        <p class="text-sm font-medium text-slate-400 mt-1 ml-0.5">Track booking status & history</p>
      </div>
    </header>

    <div class="flex-1 overflow-y-auto px-10 pb-10 custom-scrollbar z-10 relative space-y-8">

      <div class="grid grid-cols-2 md:grid-cols-5 gap-6">
        <div class="slate-card group">
          <div class="text-3xl font-bold text-slate-600 group-hover:scale-110 transition-transform duration-500">{{ stats.totalEnrollments || 0 }}</div>
          <div class="stat-label">Total</div>
        </div>
        <div class="slate-card group">
          <div class="text-3xl font-bold text-blue-400 group-hover:scale-110 transition-transform duration-500">{{ stats.pendingCount || 0 }}</div>
          <div class="stat-label">Pending</div>
        </div>
        <div class="slate-card group">
          <div class="text-3xl font-bold text-emerald-400 group-hover:scale-110 transition-transform duration-500">{{ stats.completedCount || 0 }}</div>
          <div class="stat-label">Completed</div>
        </div>
        <div class="slate-card group">
          <div class="text-3xl font-bold text-slate-400 group-hover:scale-110 transition-transform duration-500">{{ stats.cancelledCount || 0 }}</div>
          <div class="stat-label">Cancelled</div>
        </div>
        <div class="slate-card group">
          <div class="text-3xl font-bold text-indigo-400 group-hover:scale-110 transition-transform duration-500">{{ stats.todayEnrollments || 0 }}</div>
          <div class="stat-label">Today</div>
        </div>
      </div>

      <div class="bg-white rounded-[24px] p-5 shadow-sm border border-slate-100">
        <el-form :model="queryParams" inline class="flex flex-wrap items-center gap-4">
          <el-form-item label="Course" class="!mb-0 slate-form-item">
            <el-select v-model="queryParams.courseId" placeholder="All Courses" clearable class="!w-48 slate-select">
              <el-option v-for="item in courseList" :key="item.id" :label="item.name" :value="item.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="Coach" class="!mb-0 slate-form-item">
            <el-select v-model="queryParams.coachId" placeholder="All Coaches" clearable class="!w-40 slate-select">
              <el-option v-for="item in coachList" :key="item.id" :label="item.name" :value="item.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="Status" class="!mb-0 slate-form-item">
            <el-select v-model="queryParams.status" placeholder="All Status" clearable class="!w-36 slate-select">
              <el-option label="Enrolled" value="0" />
              <el-option label="Cancelled" value="1" />
              <el-option label="Completed" value="2" />
            </el-select>
          </el-form-item>
          <el-form-item label="Date" class="!mb-0 slate-form-item">
            <el-date-picker
                v-model="dateRange"
                type="daterange"
                range-separator="to"
                start-placeholder="Start"
                end-placeholder="End"
                value-format="YYYY-MM-DD"
                class="!w-64 slate-date"
            />
          </el-form-item>
          <div class="flex-1"></div>
          <div class="flex gap-3">
            <button @click="handleReset" class="px-5 py-2 rounded-full text-sm font-semibold text-slate-500 hover:bg-slate-100 transition-colors">Reset</button>
            <button @click="handleSearch" class="px-6 py-2 rounded-full text-sm font-semibold bg-slate-700 text-white shadow-lg shadow-slate-200 hover:scale-105 transition-transform hover:bg-slate-800">Search</button>
          </div>
        </el-form>
      </div>

      <div class="bg-white rounded-[32px] shadow-sm border border-slate-100 overflow-hidden">
        <el-table :data="tableData" v-loading="loading" style="width: 100%" class="slate-table" row-class-name="slate-row">
          <el-table-column prop="memberName" label="Member" width="160">
            <template #default="{ row }">
              <span class="font-bold text-slate-600">{{ row.memberName }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="memberPhone" label="Phone" width="150" />
          <el-table-column prop="courseName" label="Course" min-width="180">
            <template #default="{ row }">
              <span class="font-medium text-slate-600">{{ row.courseName }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="coachName" label="Coach" width="140">
            <template #default="{ row }">
              <div class="flex items-center gap-2">
                <div class="w-6 h-6 rounded-full bg-slate-100 flex items-center justify-center text-[10px] font-bold text-slate-500">{{ row.coachName?.[0] }}</div>
                <span class="text-slate-500">{{ row.coachName }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="classTime" label="Schedule" width="200">
            <template #default="{ row }">
              <span class="font-mono text-xs text-slate-500 bg-slate-50 px-2 py-1 rounded-md">{{ formatDateTime(row.classTime) }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="price" label="Fee" width="100">
            <template #default="{ row }">
              <span class="font-bold text-slate-600">¥{{ row.price }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="Status" width="120">
            <template #default="{ row }">
              <div
                  class="inline-flex items-center px-2.5 py-1 rounded-full text-[11px] font-bold uppercase tracking-wide"
                  :class="{
                  'bg-blue-50 text-blue-500': row.status === '0',
                  'bg-slate-100 text-slate-400': row.status === '1',
                  'bg-emerald-50 text-emerald-500': row.status === '2'
                }"
              >
                {{ getStatusText(row.status) }}
              </div>
            </template>
          </el-table-column>
          <el-table-column label="Actions" width="180" fixed="right" align="right">
            <template #default="{ row }">
              <div class="flex justify-end gap-2 opacity-0 group-hover:opacity-100 transition-opacity duration-200">
                <button
                    v-if="row.status === '0'"
                    class="w-8 h-8 flex items-center justify-center rounded-full bg-emerald-50 text-emerald-500 hover:bg-emerald-100 transition-colors"
                    title="Complete"
                    @click="handleComplete(row.id)"
                >
                  <span class="text-xs font-bold">✓</span>
                </button>
                <button
                    v-if="row.status === '0'"
                    class="w-8 h-8 flex items-center justify-center rounded-full bg-red-50 text-red-400 hover:bg-red-100 transition-colors"
                    title="Cancel"
                    @click="handleCancelEnrollment(row.id)"
                >
                  <span class="text-xs font-bold">✕</span>
                </button>
                <button
                    class="w-8 h-8 flex items-center justify-center rounded-full bg-rose-50 text-rose-500 hover:bg-rose-100 transition-colors"
                    title="Delete"
                    @click="handleDelete(row.id)"
                >
                  <span class="text-lg font-bold">×</span>
                </button>
              </div>
            </template>
          </el-table-column>
        </el-table>

        <div class="px-6 py-4 border-t border-slate-100 flex justify-center bg-white">
          <el-pagination
              v-model:current-page="queryParams.pageNum"
              v-model:page-size="queryParams.pageSize"
              :total="total"
              layout="total, prev, pager, next"
              background
              @current-change="loadData"
              class="slate-pagination"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getAdminEnrollmentList,
  updateAdminEnrollmentStatus,
  getAdminEnrollmentStats,
  getAllAdminCourses,
  getAllAdminCoaches,
  deleteAdminEnrollment
} from '@/api/course'

const loading = ref(false)
const tableData = ref<any[]>([])
const total = ref(0)
const stats = ref<any>({})
const courseList = ref<any[]>([])
const coachList = ref<any[]>([])
const dateRange = ref<string[]>([])

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  courseId: undefined as number | undefined,
  coachId: undefined as number | undefined,
  status: undefined as string | undefined,
  startDate: undefined as string | undefined,
  endDate: undefined as string | undefined
})

onMounted(() => {
  loadData()
  loadStats()
  loadCourses()
  loadCoaches()
})

const loadData = async () => {
  loading.value = true
  try {
    if (dateRange.value && dateRange.value.length === 2) {
      queryParams.startDate = dateRange.value[0]
      queryParams.endDate = dateRange.value[1]
    } else {
      queryParams.startDate = undefined
      queryParams.endDate = undefined
    }
    const res: any = await getAdminEnrollmentList(queryParams)
    tableData.value = res.rows || []
    total.value = res.total || 0
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const loadStats = async () => {
  try {
    stats.value = await getAdminEnrollmentStats() || {}
  } catch (e) { console.error(e) }
}

const loadCourses = async () => {
  try {
    courseList.value = await getAllAdminCourses() || []
  } catch (e) { console.error(e) }
}

const loadCoaches = async () => {
  try {
    coachList.value = await getAllAdminCoaches() || []
  } catch (e) { console.error(e) }
}

const handleSearch = () => {
  queryParams.pageNum = 1
  loadData()
}

const handleReset = () => {
  queryParams.courseId = undefined
  queryParams.coachId = undefined
  queryParams.status = undefined
  dateRange.value = []
  queryParams.pageNum = 1
  loadData()
}

const handleComplete = async (id: number) => {
  try {
    await ElMessageBox.confirm('确定将该预约标记为已完成吗？', '提示')
    await updateAdminEnrollmentStatus(id, '2')
    ElMessage.success('操作成功')
    loadData()
    loadStats()
  } catch (e: any) {}
}

const handleCancelEnrollment = async (id: number) => {
  try {
    await ElMessageBox.confirm('确定要取消该预约吗？', '提示')
    await updateAdminEnrollmentStatus(id, '1')
    ElMessage.success('操作成功')
    loadData()
    loadStats()
  } catch (e: any) {}
}

const handleDelete = async (id: number) => {
  try {
    await ElMessageBox.confirm('确定要删除该报名记录吗？此操作将直接从数据库中删除记录，无法恢复！', '警告', {
      type: 'warning',
      confirmButtonText: '确定删除',
      cancelButtonText: '取消'
    })
    await deleteAdminEnrollment(id)
    ElMessage.success('删除成功')
    loadData()
    loadStats()
  } catch (e: any) {}
}

const formatDateTime = (dateStr: string) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN', {
    year: 'numeric', month: '2-digit', day: '2-digit',
    hour: '2-digit', minute: '2-digit'
  })
}

const getStatusType = (status: string) => {
  const map: Record<string, string> = { '0': 'primary', '1': 'info', '2': 'success' }
  return map[status] || 'info'
}

const getStatusText = (status: string) => {
  const map: Record<string, string> = { '0': '已报名', '1': '已取消', '2': '已完成' }
  return map[status] || '未知'
}
</script>

<style scoped lang="scss">
.custom-scrollbar::-webkit-scrollbar { width: 0px; }

.slate-card {
  @apply bg-white rounded-[24px] p-6 shadow-sm border border-slate-100 flex flex-col items-center justify-center text-center cursor-default hover:shadow-md transition-all duration-300;
}

.stat-label {
  @apply text-[11px] font-bold text-slate-400 uppercase tracking-wider mt-2;
}

:deep(.slate-form-item) {
  .el-form-item__label {
    color: #94a3b8;
    font-weight: 600;
    font-size: 12px;
    text-transform: uppercase;
    letter-spacing: 0.05em;
  }
}

:deep(.slate-select .el-input__wrapper),
:deep(.slate-date .el-input__wrapper) {
  background-color: var(--ff-surface-raised) !important;
  box-shadow: none !important;
  border-radius: 12px;
  padding: 4px 12px;
  transition: all 0.2s;

  &:hover, &.is-focus {
    background-color: var(--ff-surface) !important;
    box-shadow: 0 0 0 1px var(--ff-border-strong) !important;
  }
  .el-input__inner {
    color: #475569;
  }
}

:deep(.slate-table) {
  background: transparent !important;

  tr, th, td {
    background-color: transparent !important;
  }

  th {
    border-bottom: 1px solid #f1f5f9 !important;
    text-transform: uppercase;
    font-size: 11px;
    color: #94a3b8;
    letter-spacing: 0.05em;
    padding: 20px 0;
    font-weight: 600;
  }

  td {
    border-bottom: 1px solid var(--ff-surface-raised) !important;
    padding: 16px 0;
    color: #475569;
  }

  .el-table__inner-wrapper::before { display: none; }

  .slate-row:hover > td {
    background-color: var(--ff-surface-raised) !important;
  }
}

:deep(.slate-pagination) {
  .el-pagination__total { color: #94a3b8; }
  .btn-prev, .btn-next { background: transparent !important; color: #94a3b8; }
  .el-pager li {
    background: transparent !important;
    border-radius: 8px;
    color: #94a3b8;
    &.is-active {
      background: #475569 !important;
      color: var(--ff-text);
    }
    &:hover:not(.is-active) { color: #475569; }
  }
}
</style>
