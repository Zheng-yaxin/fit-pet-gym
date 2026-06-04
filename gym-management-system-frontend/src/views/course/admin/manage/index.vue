<template>
  <div class="flex flex-col h-full bg-slate-50 relative font-sans overflow-hidden">

    <header class="h-24 flex-shrink-0 flex items-center justify-between px-10 z-20">
      <div>
        <h1 class="text-3xl font-bold text-slate-600 tracking-tight">课程管理</h1>
        <p class="text-sm font-medium text-slate-400 mt-1 ml-0.5">管理团课和私教课程项目</p>
      </div>
      <button
          @click="handleAdd"
          class="h-11 px-6 bg-slate-700 text-white rounded-full font-bold text-sm shadow-md shadow-slate-200 hover:bg-slate-800 hover:scale-105 active:scale-95 transition-all flex items-center gap-2"
      >
        <Plus :size="18" /> 新增课程
      </button>
    </header>

    <div class="flex-1 overflow-y-auto px-10 py-4 custom-scrollbar z-10 relative">

      <div class="sticky top-0 z-30 mb-8 mx-auto max-w-2xl">
        <div class="bg-white rounded-full p-2 shadow-sm border border-slate-100 flex items-center gap-2 transition-all duration-300 focus-within:shadow-md">
          <div class="w-10 h-10 flex items-center justify-center text-slate-400">
            <Search :size="20" />
          </div>
          <input
              v-model="queryParams.keyword"
              class="flex-1 h-full bg-transparent border-none outline-none text-slate-600 placeholder-slate-400 font-medium text-[15px]"
              placeholder="搜索课程名称..."
              @keyup.enter="handleQuery"
          />
          <div class="flex gap-2 mr-1">
            <button @click="handleQuery" class="px-5 py-2 bg-slate-100 text-slate-600 rounded-full text-xs font-bold hover:bg-slate-200 transition-colors">搜索</button>
            <button @click="resetQuery" v-if="queryParams.keyword" class="w-8 h-8 flex items-center justify-center rounded-full bg-slate-100 text-slate-400 hover:text-red-400 transition-colors">✕</button>
          </div>
        </div>
      </div>

      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-8 pb-10">
        <div
            v-for="course in courseList"
            :key="course.id"
            class="group relative bg-white rounded-[32px] p-4 shadow-sm border border-slate-50 hover:shadow-xl hover:shadow-slate-200/50 hover:-translate-y-2 transition-all duration-500 flex flex-col"
        >
          <div class="h-56 relative overflow-hidden rounded-[24px] bg-slate-50 mb-5">
            <img
                v-if="course.imageUrl"
                :src="course.imageUrl"
                class="w-full h-full object-cover transition-transform duration-700 group-hover:scale-110"
            />
            <div v-else class="w-full h-full flex items-center justify-center text-slate-300 bg-slate-50">
              <ImageIcon :size="48" class="opacity-50" />
            </div>

            <div class="absolute top-4 right-4">
              <span
                  class="px-3 py-1.5 rounded-full text-[10px] font-bold backdrop-blur-md border border-white/20 shadow-sm tracking-wide uppercase"
                  :class="course.status === '0' ? 'bg-white/90 text-emerald-600' : 'bg-slate-800/80 text-white'"
              >
                {{ course.status === '0' ? '上架' : '下架' }}
              </span>
            </div>
          </div>

          <div class="px-2 pb-2 flex-1 flex flex-col">
            <h3 class="text-xl font-bold text-slate-600 mb-1.5 line-clamp-1 tracking-tight text-center">{{ course.name }}</h3>
            <p class="text-xs font-medium text-slate-400 line-clamp-2 mb-6 h-8 leading-relaxed text-center">{{ course.description || '暂无课程描述' }}</p>

            <div class="flex gap-3 mb-6">
              <div class="flex-1 bg-slate-50 rounded-2xl p-3 flex flex-col items-center justify-center gap-1 border border-slate-50">
                <Clock :size="16" class="text-blue-400" />
                <span class="text-[11px] font-bold text-slate-500">{{ course.duration }}分钟</span>
              </div>
              <div class="flex-1 bg-slate-50 rounded-2xl p-3 flex flex-col items-center justify-center gap-1 border border-slate-50">
                <Users :size="16" class="text-indigo-400" />
                <span class="text-[11px] font-bold text-slate-500">限 {{ course.maxParticipants }} 人</span>
              </div>
            </div>

            <div class="mt-auto flex items-center justify-between pt-5 border-t border-slate-50">
              <span class="text-xl font-bold text-slate-600">¥{{ course.price }}</span>
              <div class="flex gap-2">
                <button @click="handleEdit(course)" class="w-10 h-10 flex items-center justify-center rounded-full text-slate-300 hover:text-white hover:bg-blue-400 transition-all duration-300">
                  <Edit :size="18" />
                </button>
                <button @click="handleDelete(course)" class="w-10 h-10 flex items-center justify-center rounded-full text-slate-300 hover:text-white hover:bg-red-400 transition-all duration-300">
                  <Trash2 :size="18" />
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="flex justify-center pb-8">
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

    <el-dialog
        v-model="dialog.visible"
        width="480px"
        destroy-on-close
        class="clean-sheet-dialog"
        :show-close="false"
        align-center
    >
      <div class="p-4 relative">
        <div class="flex justify-center items-center mb-8 relative">
          <h2 class="text-2xl font-bold text-slate-600 tracking-tight absolute left-0 right-0 text-center pointer-events-none">{{ dialog.title }}</h2>
          <button @click="dialog.visible = false" class="absolute right-0 w-8 h-8 rounded-full bg-slate-100 hover:bg-slate-200 flex items-center justify-center text-slate-500 transition-colors z-10">✕</button>
        </div>
        <div class="h-6"></div>

        <el-form ref="courseFormRef" :model="formData" :rules="rules" label-position="top" class="space-y-6">
          <el-form-item label="课程名称" prop="name" class="slate-form-item">
            <el-input v-model="formData.name" placeholder="例如：HIIT燃脂训练" class="slate-input"/>
          </el-form-item>

          <div class="grid grid-cols-2 gap-5">
            <el-form-item label="时长 (分钟)" prop="duration" class="slate-form-item">
              <el-input-number v-model="formData.duration" :min="1" controls-position="right" class="!w-full slate-number"/>
            </el-form-item>
            <el-form-item label="价格 (¥)" prop="price" class="slate-form-item">
              <el-input-number v-model="formData.price" :min="0" :precision="2" controls-position="right" class="!w-full slate-number"/>
            </el-form-item>
          </div>

          <el-form-item label="最大人数" prop="maxParticipants" class="slate-form-item">
            <el-input-number v-model="formData.maxParticipants" :min="1" controls-position="right" class="!w-full slate-number"/>
          </el-form-item>

          <el-form-item label="封面图片" prop="imageUrl" class="slate-form-item">
            <el-upload
                class="w-full"
                :action="uploadUrl"
                :headers="uploadHeaders"
                :show-file-list="false"
                :on-success="handleAvatarSuccess"
                :before-upload="beforeAvatarUpload"
            >
              <div v-if="formData.imageUrl" class="w-full h-48 rounded-[20px] overflow-hidden relative group cursor-pointer border border-slate-200 shadow-sm">
                <img :src="formData.imageUrl" class="w-full h-full object-cover" />
                <div class="absolute inset-0 bg-slate-900/40 backdrop-blur-sm flex items-center justify-center opacity-0 group-hover:opacity-100 transition-all duration-300 text-white font-bold text-sm">更换图片</div>
              </div>
              <div v-else class="w-full h-48 border border-dashed border-slate-300 rounded-[20px] flex flex-col items-center justify-center text-slate-400 hover:border-blue-400 hover:text-blue-400 hover:bg-blue-50/50 transition-all cursor-pointer bg-slate-50">
                <div class="w-12 h-12 rounded-full bg-white shadow-sm flex items-center justify-center mb-3">
                  <Plus :size="20" />
                </div>
                <span class="text-xs font-bold uppercase tracking-wide">上传封面</span>
              </div>
            </el-upload>
          </el-form-item>

          <el-form-item label="课程描述" class="slate-form-item">
            <el-input v-model="formData.description" type="textarea" :rows="3" placeholder="简要描述课程内容..." class="slate-textarea"/>
          </el-form-item>

          <el-form-item label="状态" prop="status" class="slate-form-item">
            <div class="flex bg-slate-100 p-1.5 rounded-2xl w-full">
              <button type="button" @click="formData.status = '0'" class="flex-1 py-2.5 text-xs font-bold rounded-xl transition-all duration-300" :class="formData.status === '0' ? 'bg-white text-emerald-600 shadow-sm' : 'text-slate-400 hover:text-slate-600'">上架</button>
              <button type="button" @click="formData.status = '1'" class="flex-1 py-2.5 text-xs font-bold rounded-xl transition-all duration-300" :class="formData.status === '1' ? 'bg-white text-slate-600 shadow-sm' : 'text-slate-400 hover:text-slate-600'">下架</button>
            </div>
          </el-form-item>
        </el-form>

        <div class="flex gap-4 mt-10">
          <button @click="dialog.visible = false" class="flex-1 h-12 rounded-full font-bold text-slate-500 bg-slate-100 hover:bg-slate-200 transition-colors">取消</button>
          <button @click="submitForm" class="flex-1 h-12 rounded-full font-bold text-white bg-slate-800 hover:scale-[1.02] active:scale-95 transition-all shadow-lg shadow-slate-200">保存</button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { UploadProps } from 'element-plus'
import { Plus, Search, Image as ImageIcon, Clock, User as Users, Edit, Trash2 } from 'lucide-vue-next'
import { getAdminCourseList, addAdminCourse, updateAdminCourse, deleteAdminCourse } from '@/api/course'
// 【关键修复1】引入 userStore 以获取正确的 token
import { useUserStore } from '@/store/modules/user'

const userStore = useUserStore()
const courseList = ref<any[]>([])
const total = ref(0)
const loading = ref(false)

// 【关键修复2】上传接口地址，对应 CommonController
const uploadUrl = '/api/common/upload'

// 【关键修复3】使用 store 中的 token，保持与全局 request.ts 一致，解决 403 问题
// 如果 userStore.token 中已经包含了 Bearer，这里直接使用即可，避免重复
const uploadHeaders = computed(() => ({ Authorization: userStore.token }))

const queryParams = reactive({ pageNum: 1, pageSize: 8, keyword: '' })
const dialog = reactive({ visible: false, title: '' })
const formData = reactive<any>({ status: '0' })
const rules = { name: [{ required: true, message: '必填项', trigger: 'blur' }] }
const courseFormRef = ref()

const getList = () => {
  loading.value = true
  getAdminCourseList(queryParams).then((res:any) => {
    courseList.value = res.rows || []
    total.value = res.total || 0
  }).finally(()=>loading.value=false)
}
const handleQuery = () => { queryParams.pageNum=1; getList() }
const resetQuery = () => { queryParams.keyword=''; handleQuery() }

// 汉化标题
const handleAdd = () => {
  dialog.title='新增课程';
  dialog.visible=true;
  Object.assign(formData, {id:undefined, status:'0', name:'', duration: 60, price: 0, maxParticipants: 20, description: '', imageUrl: ''})
}

const handleEdit = (row:any) => {
  dialog.title='编辑课程';
  dialog.visible=true;
  Object.assign(formData, row)
}

const handleDelete = (row:any) => {
  ElMessageBox.confirm('确定要删除该课程吗？','提示',{type:'warning', confirmButtonText: '确定', cancelButtonText: '取消'}).then(()=>{
    deleteAdminCourse(row.id).then(()=>{ ElMessage.success('已删除'); getList() })
  })
}

const submitForm = () => {
  courseFormRef.value.validate((valid:boolean)=>{
    if(valid) {
      (formData.id ? updateAdminCourse(formData) : addAdminCourse(formData)).then(()=>{
        ElMessage.success('保存成功'); dialog.visible=false; getList()
      })
    }
  })
}

const beforeAvatarUpload: UploadProps['beforeUpload'] = (file) => true

// 【关键修复4】处理上传回调，data 即为 URL 字符串
const handleAvatarSuccess = (res:any) => {
  formData.imageUrl = res.data
}

onMounted(getList)
</script>

<style scoped lang="scss">
.custom-scrollbar::-webkit-scrollbar { width: 0; }

:deep(.clean-sheet-dialog) {
  border-radius: 40px !important;
  background: var(--ff-surface) !important;
  box-shadow: 0 40px 80px -10px rgba(0,0,0,0.1) !important;
  border: 1px solid #f1f5f9;

  .el-dialog__header { display: none; }
  .el-dialog__body { padding: 32px; }
}

:deep(.slate-form-item .el-form-item__label) {
  color: #94a3b8; /* slate-400 */
  font-weight: 700;
  font-size: 11px;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  margin-bottom: 8px;
}

:deep(.slate-input .el-input__wrapper),
:deep(.slate-textarea .el-textarea__inner) {
  background-color: var(--ff-surface-raised) !important; /* slate-50 */
  box-shadow: none !important;
  border-radius: 16px;
  padding: 12px 16px;
  font-size: 16px;
  transition: all 0.3s;
  color: #475569;

  &:hover { background-color: #f1f5f9 !important; }
  &.is-focus, &:focus {
    background-color: var(--ff-surface) !important;
    box-shadow: 0 0 0 2px var(--ff-border-strong) !important; /* slate-300 */
  }
}

:deep(.slate-number .el-input__wrapper) {
  background-color: var(--ff-surface-raised) !important;
  box-shadow: none !important;
  border-radius: 16px;
}

:deep(.slate-pagination) {
  .el-pager li {
    background: transparent !important;
    border-radius: 8px;
    font-weight: 600;
    color: #94a3b8;
    &.is-active { background: #475569 !important; color: white; }
    &:hover:not(.is-active) { color: #64748b; }
  }
  .btn-prev, .btn-next { background: transparent !important; color: #94a3b8; }
}
</style>