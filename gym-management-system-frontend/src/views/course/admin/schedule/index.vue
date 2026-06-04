<template>
  <div class="flex flex-col h-full bg-slate-50 relative font-sans overflow-hidden">

    <header class="h-24 flex-shrink-0 flex items-center justify-between px-10 z-20">
      <div>
        <h1 class="text-3xl font-bold text-slate-600 tracking-tight">排课管理</h1>
        <p class="text-sm font-medium text-slate-400 mt-1 ml-0.5">管理课程的排期与时间安排</p>
      </div>
      <button @click="handleAdd" class="h-11 px-6 bg-slate-700 text-white rounded-full font-bold text-sm shadow-md shadow-slate-200 hover:bg-slate-800 hover:scale-105 active:scale-95 transition-all flex items-center gap-2">
        <Plus :size="18" /> 新增排课
      </button>
    </header>

    <div class="flex-1 overflow-y-auto px-10 py-6 custom-scrollbar z-10 relative">

      <div class="mb-8 flex justify-center">
        <div class="bg-white rounded-full p-2 pl-6 shadow-sm border border-slate-100 flex items-center gap-6">
          <div class="flex items-center gap-3">
            <span class="text-[11px] font-bold text-slate-400 uppercase tracking-wider">星期</span>
            <el-select v-model="queryParams.dayOfWeek" placeholder="全部" clearable class="w-32 slate-pill-select">
              <el-option label="周一" :value="1"/><el-option label="周二" :value="2"/><el-option label="周三" :value="3"/>
              <el-option label="周四" :value="4"/><el-option label="周五" :value="5"/><el-option label="周六" :value="6"/><el-option label="周日" :value="7"/>
            </el-select>
          </div>
          <div class="w-px h-6 bg-slate-100"></div>
          <div class="flex items-center gap-3">
            <span class="text-[11px] font-bold text-slate-400 uppercase tracking-wider">课程</span>
            <el-select v-model="queryParams.courseId" placeholder="所有课程" clearable class="w-40 slate-pill-select">
              <el-option v-for="item in courseOptions" :key="item.id" :label="item.name" :value="item.id"/>
            </el-select>
          </div>
          <div class="w-px h-6 bg-slate-100"></div>
          <div class="flex items-center gap-3">
            <span class="text-[11px] font-bold text-slate-400 uppercase tracking-wider">教练</span>
            <el-select v-model="queryParams.coachId" placeholder="所有教练" clearable class="w-32 slate-pill-select">
              <el-option v-for="item in coachOptions" :key="item.id" :label="item.name" :value="item.id"/>
            </el-select>
          </div>

          <div class="pl-4">
            <button @click="handleQuery" class="h-10 px-6 rounded-full bg-slate-800 text-white text-xs font-bold hover:bg-black transition-colors">筛选</button>
            <button @click="resetQuery" class="h-10 w-10 ml-2 rounded-full text-slate-400 hover:bg-slate-50 transition-colors flex items-center justify-center">↺</button>
          </div>
        </div>
      </div>

      <div class="bg-white rounded-[32px] shadow-sm border border-slate-100 overflow-hidden min-h-[400px]">
        <el-table :data="scheduleList" style="width: 100%" class="slate-table" row-class-name="slate-row" v-loading="loading">

          <el-table-column label="课程信息" min-width="180">
            <template #default="{ row }">
              <div class="flex flex-col py-1">
                <span class="font-bold text-slate-600 text-[15px] truncate" :title="row.courseName">{{ row.courseName }}</span>
                <div class="flex items-center gap-2 mt-1">
                  <div class="w-4 h-4 rounded-full bg-indigo-100 flex items-center justify-center text-[9px] font-bold text-indigo-500 flex-shrink-0">教</div>
                  <span class="text-xs font-medium text-slate-500 truncate">{{ row.coachName }}</span>
                </div>
              </div>
            </template>
          </el-table-column>

          <el-table-column label="上课时间" min-width="200">
            <template #default="{ row }">
              <div class="flex flex-col gap-1.5">
                 <span
                     class="w-fit px-2.5 py-0.5 rounded-md text-[10px] font-bold uppercase tracking-wider border"
                     :class="getWeekdayClass(row.dayOfWeek)"
                 >
                  {{ ['','周一','周二','周三','周四','周五','周六','周日'][row.dayOfWeek] }}
                </span>
                <span class="font-mono text-sm font-semibold text-slate-600">{{ row.startTime }} - {{ row.endTime }}</span>
              </div>
            </template>
          </el-table-column>

          <el-table-column label="有效期" min-width="240">
            <template #default="{ row }">
              <div class="flex items-center gap-2 text-slate-500">
                <span class="text-xs font-medium bg-slate-50 px-2 py-1 rounded-lg border border-slate-100 whitespace-nowrap">{{ row.startDate }}</span>
                <span class="text-slate-300">→</span>
                <span class="text-xs font-medium bg-slate-50 px-2 py-1 rounded-lg border border-slate-100 whitespace-nowrap">{{ row.endDate }}</span>
              </div>
            </template>
          </el-table-column>

          <el-table-column label="报名情况" min-width="180">
            <template #default="{ row }">
              <div class="flex flex-col gap-1.5 w-full pr-8">
                <div class="flex justify-between text-[11px] font-bold text-slate-400 uppercase tracking-wider">
                  <span>已报名</span>
                  <span :class="row.enrolledCount >= row.capacity ? 'text-red-400' : 'text-slate-500'">{{ row.enrolledCount || 0 }}/{{ row.capacity }}</span>
                </div>
                <div class="h-1.5 w-full bg-slate-100 rounded-full overflow-hidden">
                  <div class="h-full rounded-full transition-all duration-1000 ease-out shadow-sm"
                       :style="{width: Math.min(((row.enrolledCount || 0) / row.capacity) * 100, 100) + '%'}"
                       :class="(row.enrolledCount || 0) >= row.capacity ? 'bg-red-300' : 'bg-blue-400'">
                  </div>
                </div>
              </div>
            </template>
          </el-table-column>

          <el-table-column label="操作" width="140" align="center" fixed="right">
            <template #default="{ row }">
              <div class="flex items-center justify-center gap-3">
                <button @click="handleEdit(row)" class="w-9 h-9 rounded-full flex items-center justify-center text-blue-500 bg-blue-50 hover:bg-blue-100 transition-colors" title="修改">
                  <span class="text-xs font-bold">改</span>
                </button>
                <button @click="handleDelete(row)" class="w-9 h-9 rounded-full flex items-center justify-center text-red-500 bg-red-50 hover:bg-red-100 transition-colors" title="删除">
                  <span class="text-xl leading-none">×</span>
                </button>
              </div>
            </template>
          </el-table-column>
        </el-table>

        <div class="flex justify-center p-6 border-t border-slate-50">
          <el-pagination
              v-if="total > 0"
              background
              layout="prev, pager, next"
              :total="total"
              v-model:current-page="queryParams.pageNum"
              v-model:page-size="queryParams.pageSize"
              @current-change="getList"
              class="slate-pagination"
          />
        </div>
      </div>
    </div>

    <el-dialog
        v-model="dialog.visible"
        width="500px"
        class="clean-modal"
        destroy-on-close
        align-center
        :show-close="false"
    >
      <div class="p-8 relative">
        <div class="absolute top-6 left-0 right-0 flex justify-center pointer-events-none">
          <h3 class="text-xl font-bold text-slate-600 pointer-events-auto">{{ dialog.title }}</h3>
        </div>
        <div class="h-8"></div>

        <el-form ref="formRef" :model="formData" :rules="rules" label-position="top" class="space-y-5 mt-4">

          <div class="grid grid-cols-2 gap-5">
            <el-form-item label="课程" prop="courseId" class="slate-form-item">
              <el-select v-model="formData.courseId" class="w-full slate-input" placeholder="选择课程">
                <el-option v-for="i in courseOptions" :key="i.id" :label="i.name" :value="i.id"/>
              </el-select>
            </el-form-item>

            <el-form-item label="教练" prop="coachId" class="slate-form-item">
              <el-select v-model="formData.coachId" class="w-full slate-input" placeholder="选择教练">
                <el-option v-for="i in coachOptions" :key="i.id" :label="i.name" :value="i.id"/>
              </el-select>
            </el-form-item>
          </div>

          <el-form-item label="上课星期" prop="dayOfWeek" class="slate-form-item">
            <div class="flex bg-slate-100 p-1 rounded-xl w-full">
              <div class="grid grid-cols-7 w-full gap-1">
                <div
                    v-for="d in 7" :key="d"
                    @click="formData.dayOfWeek = d"
                    class="h-9 flex items-center justify-center rounded-lg text-[10px] font-bold cursor-pointer transition-all"
                    :class="formData.dayOfWeek === d ? 'bg-white text-slate-700 shadow-sm' : 'text-slate-400 hover:text-slate-600'"
                >
                  {{ ['一','二','三','四','五','六','日'][d-1] }}
                </div>
              </div>
            </div>
          </el-form-item>

          <el-form-item label="上课时间段" prop="timeRange" class="slate-form-item">
            <el-time-picker
                v-model="formData.timeRange"
                is-range
                format="HH:mm"
                value-format="HH:mm"
                range-separator="-"
                start-placeholder="开始时间"
                end-placeholder="结束时间"
                class="!w-full slate-date-picker"
                :clearable="false"
            />
          </el-form-item>

          <el-form-item label="有效期范围" prop="dateRange" class="slate-form-item">
            <el-date-picker
                v-model="formData.dateRange"
                type="daterange"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                class="!w-full slate-date-picker"
            />
          </el-form-item>

          <el-form-item label="人数限制" prop="capacity" class="slate-form-item">
            <el-input-number v-model="formData.capacity" :min="1" controls-position="right" class="!w-full slate-number"/>
          </el-form-item>

          <div class="flex gap-3 pt-6">
            <button type="button" @click="dialog.visible=false" class="flex-1 py-3.5 rounded-2xl font-bold text-slate-500 bg-slate-50 hover:bg-slate-100 transition-colors">取消</button>
            <button type="button" @click="submitForm" class="flex-1 py-3.5 rounded-2xl font-bold text-white bg-slate-800 shadow-lg shadow-slate-200 hover:scale-[1.02] active:scale-95 transition-all">保存排课</button>
          </div>
        </el-form>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { Plus } from 'lucide-vue-next'
import { getAdminScheduleList, addAdminSchedule, updateAdminSchedule, deleteAdminSchedule, getAllAdminCourses, getAllAdminCoaches } from '@/api/course'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const scheduleList = ref([])
const total = ref(0)
const courseOptions = ref<any[]>([])
const coachOptions = ref<any[]>([])
const queryParams = reactive({ pageNum: 1, pageSize: 10, dayOfWeek: undefined, courseId: undefined, coachId: undefined })
const dialog = reactive({ visible: false, title: '' })
const formRef = ref()

// 表单数据，包含前端临时的 range 字段
const formData = reactive<any>({
  id: undefined,
  courseId: undefined,
  coachId: undefined,
  dayOfWeek: 1,
  capacity: 20,
  timeRange: ['09:00', '10:00'],
  dateRange: []
})

const rules = {
  courseId: [{ required: true, message: '请选择课程', trigger: 'change' }],
  coachId: [{ required: true, message: '请选择教练', trigger: 'change' }],
  dayOfWeek: [{ required: true, message: '请选择星期', trigger: 'change' }],
  timeRange: [{ required: true, message: '请选择上课时间', trigger: 'change' }],
  dateRange: [{ required: true, message: '请选择有效期', trigger: 'change' }],
  capacity: [{ required: true, message: '请输入人数限制', trigger: 'blur' }]
}

const getList = () => {
  loading.value = true
  getAdminScheduleList(queryParams).then((res:any) => {
    scheduleList.value = res.rows || []
    total.value = res.total || 0
  }).finally(() => loading.value = false)
}

const handleQuery = () => { queryParams.pageNum=1; getList() }
const resetQuery = () => { Object.assign(queryParams, { dayOfWeek: undefined, courseId: undefined, coachId: undefined }); handleQuery() }

const handleAdd = () => {
  dialog.title='新增排课'
  dialog.visible=true
  // 重置表单
  Object.assign(formData, {
    id: undefined,
    courseId: undefined,
    coachId: undefined,
    dayOfWeek: 1,
    capacity: 20,
    timeRange: ['09:00', '10:00'],
    dateRange: []
  })
}

const handleEdit = (row:any) => {
  dialog.title='修改排课'
  dialog.visible=true
  Object.assign(formData, {
    ...row,
    timeRange: [row.startTime, row.endTime],
    dateRange: [row.startDate, row.endDate]
  })
}

const handleDelete = (row:any) => {
  ElMessageBox.confirm(
      '确定要删除这条排课记录吗？',
      '提示',
      { confirmButtonText: '删除', cancelButtonText: '取消', type: 'warning' }
  ).then(() => {
    deleteAdminSchedule(row.id).then(() => {
      ElMessage.success('删除成功')
      getList()
    })
  })
}

const submitForm = () => {
  formRef.value.validate((valid: boolean) => {
    if (valid) {
      // 转换前端Range数据为后端字段
      const submitData = {
        ...formData,
        startTime: formData.timeRange[0],
        endTime: formData.timeRange[1],
        startDate: formData.dateRange[0],
        endDate: formData.dateRange[1]
      }

      const api = formData.id ? updateAdminSchedule : addAdminSchedule
      api(submitData).then(() => {
        ElMessage.success(formData.id ? '修改成功' : '创建成功')
        dialog.visible = false
        getList()
      })
    }
  })
}

const getWeekdayClass = (d:number) => (['','bg-slate-100 text-slate-500 border-slate-200','bg-slate-100 text-slate-500 border-slate-200','bg-slate-100 text-slate-500 border-slate-200','bg-slate-100 text-slate-500 border-slate-200','bg-slate-100 text-slate-500 border-slate-200','bg-blue-50 text-blue-500 border-blue-100','bg-red-50 text-red-500 border-red-100'][d])

onMounted(() => {
  getList()
  getAllAdminCourses().then((res:any) => courseOptions.value = res)
  getAllAdminCoaches().then((res:any) => coachOptions.value = res)
})
</script>

<style scoped lang="scss">
.custom-scrollbar::-webkit-scrollbar { width: 0; }

:deep(.slate-pill-select .el-input__wrapper) {
  box-shadow: none !important;
  background-color: transparent;
  padding: 0;
  .el-input__inner { font-weight: 600; color: #475569; text-align: right; }
}

:deep(.slate-table) {
  background: transparent !important;
  tr, th, td { background: transparent !important; }
  th {
    border-bottom: 1px solid #f1f5f9 !important;
    text-transform: uppercase; font-size: 11px; color: #94a3b8; letter-spacing: 0.05em; padding: 18px 0;
  }
  td { border-bottom: 1px solid var(--ff-surface-raised) !important; padding: 16px 0; }
  .slate-row:hover > td { background-color: var(--ff-surface-raised) !important; }
  .el-table__inner-wrapper::before { display: none; }
}

:deep(.clean-modal) {
  border-radius: 32px !important;
  background: var(--ff-surface) !important;
  box-shadow: 0 40px 100px -10px rgba(0,0,0,0.1) !important;
  border: 1px solid #f1f5f9;
  .el-dialog__header { display: none; }
  .el-dialog__body { padding: 0; }
}

:deep(.slate-form-item .el-form-item__label) {
  color: #94a3b8; font-weight: 700; font-size: 11px; text-transform: uppercase; letter-spacing: 0.05em;
}

:deep(.slate-input .el-input__wrapper),
:deep(.slate-date-picker .el-input__wrapper){
  background-color: var(--ff-surface-raised) !important;
  box-shadow: none !important;
  border-radius: 12px;
  padding: 8px 12px;
  &.is-focus, &:focus-within { background-color: var(--ff-surface) !important; box-shadow: 0 0 0 2px #e2e8f0 !important; }
}

:deep(.slate-number .el-input__wrapper) {
  background-color: var(--ff-surface-raised) !important; box-shadow: none !important; border-radius: 12px;
}

:deep(.slate-pagination) {
  .el-pager li { background: transparent !important; color: #94a3b8; &.is-active { background: #1e293b !important; color: white; border-radius: 8px; } }
  .btn-prev, .btn-next { background: transparent !important; }
}
</style>