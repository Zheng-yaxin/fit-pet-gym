<template>
  <div class="flex flex-col h-full bg-slate-50 selection:bg-blue-200/30 font-sans">

    <header class="h-20 flex-shrink-0 relative flex items-center justify-center px-8 bg-slate-50/85 backdrop-blur-xl z-30 sticky top-0 transition-all">
      <div class="absolute left-8 flex items-center">
        <span class="px-3 py-1 rounded-full bg-slate-200/50 text-slate-400 text-[11px] font-bold uppercase tracking-wider">Assets Manager</span>
      </div>

      <div class="absolute left-1/2 top-1/2 -translate-x-1/2 -translate-y-1/2">
        <h1 class="text-xl font-bold tracking-tight text-slate-600">器材资产管理</h1>
      </div>

      <button @click="handleAdd" class="absolute right-8 px-5 py-2.5 bg-slate-800 hover:bg-slate-900 text-white rounded-full font-bold text-[13px] shadow-lg shadow-slate-300/50 active:scale-95 transition-all flex items-center gap-2">
        <Plus :size="16" stroke-width="2.5"/>
        <span>新增器材</span>
      </button>
    </header>

    <div class="flex-1 overflow-y-auto px-8 py-8 custom-scrollbar scroll-smooth" v-loading="loading">

      <div class="flex justify-center mb-10">
        <div class="flex items-center gap-4 bg-white p-1.5 rounded-full border border-slate-100 shadow-sm hover:shadow-md transition-all duration-300">
          <div class="relative group">
            <input
                v-model="searchForm.keyword"
                class="w-72 h-10 pl-10 pr-4 rounded-full bg-slate-50 hover:bg-slate-100 focus:bg-white border-0 outline-none focus:ring-2 ring-blue-100 transition-all text-[14px] placeholder-slate-400 text-slate-600 font-medium"
                placeholder="搜索名称或编号"
                @keyup.enter="handleSearch"
            />
            <Search class="absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-400 w-4 h-4 pointer-events-none" />
          </div>

          <div class="h-5 w-px bg-slate-200/60"></div>

          <el-select v-model="searchForm.status" placeholder="所有状态" clearable class="apple-select w-32">
            <el-option label="正常" :value="0"/>
            <el-option label="维护中" :value="1"/>
            <el-option label="损坏" :value="2"/>
            <el-option label="报废" :value="3"/>
          </el-select>

          <button @click="handleSearch" class="px-6 h-10 bg-slate-100 hover:bg-slate-200 rounded-full text-sm font-bold text-slate-600 transition-all active:scale-95">查询</button>
        </div>
      </div>

      <div v-if="tableData.length > 0" class="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 2xl:grid-cols-4 gap-6 pb-20">
        <div
            v-for="item in tableData"
            :key="item.id"
            class="group relative bg-white rounded-[28px] p-6 shadow-sm hover:shadow-xl hover:shadow-slate-200/50 hover:-translate-y-1 transition-all duration-500 flex flex-col items-center"
        >
          <div class="absolute top-6 right-6 flex items-center gap-1.5 z-10">
            <div class="w-2 h-2 rounded-full animate-pulse" :class="getStatusColor(item.status).dot"></div>
            <span class="text-[10px] font-bold tracking-wide uppercase" :class="getStatusColor(item.status).text">{{ getStatusText(item.status) }}</span>
          </div>

          <div class="mb-5 mt-2 w-full flex justify-center">
            <div class="w-24 h-24 rounded-2xl bg-slate-50 flex items-center justify-center text-slate-400 border border-slate-100/50 group-hover:scale-105 transition-all duration-500 overflow-hidden relative">
              <img v-if="item.imageUrl" :src="item.imageUrl" class="w-full h-full object-cover" />
              <Dumbbell v-else :size="32" stroke-width="1.5" class="opacity-50" />
            </div>
          </div>

          <div class="text-center mb-6 w-full px-2">
            <h3 class="font-bold text-slate-600 text-[17px] leading-tight mb-2 truncate">{{ item.name }}</h3>
            <div class="inline-flex items-center px-2.5 py-0.5 rounded-md bg-slate-50 text-slate-400 text-[11px] font-mono font-medium">
              {{ item.code }}
            </div>
          </div>

          <div class="w-full space-y-3 mb-8 px-2 flex-1">
            <div class="flex justify-between items-center text-[13px]">
              <span class="text-slate-400 font-medium flex items-center gap-1.5"><MapPin :size="13"/> 位置</span>
              <span class="font-semibold text-slate-600 truncate max-w-[60%]">{{ item.location || '未知区域' }}</span>
            </div>
            <div class="flex justify-between items-center text-[13px]">
              <span class="text-slate-400 font-medium flex items-center gap-1.5"><Tag :size="13"/> 分类</span>
              <span class="font-semibold text-slate-600 truncate max-w-[60%]">{{ item.categoryName || '未分类' }}</span>
            </div>
          </div>

          <div class="w-full flex gap-3 mt-auto pt-5 border-t border-slate-50">
            <button @click="handleEdit(item)" class="flex-1 py-2.5 bg-slate-50 hover:bg-slate-100 rounded-xl text-xs font-bold text-slate-500 hover:text-blue-600 transition-all">
              编辑详情
            </button>
            <button @click="handleDelete(item)" class="px-4 py-2.5 bg-slate-50 hover:bg-red-50 rounded-xl text-slate-400 hover:text-red-400 transition-all">
              <Trash2 :size="16"/>
            </button>
          </div>
        </div>
      </div>

      <div v-else-if="!loading" class="flex flex-col items-center justify-center py-32 text-slate-400">
        <div class="w-20 h-20 rounded-full bg-slate-100/50 flex items-center justify-center mb-6">
          <Dumbbell :size="32" class="opacity-20 text-slate-400" />
        </div>
        <p class="font-medium text-slate-400">暂无器材数据</p>
      </div>

      <div class="mt-8 flex justify-center pb-12">
        <el-pagination
            background
            layout="prev, pager, next"
            :total="pagination.total"
            :page-size="pagination.pageSize"
            v-model:current-page="pagination.pageNum"
            @current-change="getList"
            class="ios-pagination-simple"
        />
      </div>
    </div>

    <EquipmentForm
        v-model="formVisible"
        ref="equipmentFormRef"
        :category-list="flatCategoryList"
        @success="handleFormSuccess"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, nextTick, computed } from 'vue'
import { Plus, Trash2, Dumbbell, Search, MapPin, Tag } from 'lucide-vue-next'
import { getEquipmentList, deleteEquipment, getCategoryTree, type Equipment, type Category } from '@/api/equipment'
import EquipmentForm from './components/EquipmentForm.vue'
import { ElMessageBox, ElMessage } from 'element-plus'

const loading = ref(false)
const tableData = ref<Equipment[]>([])
const categoryTree = ref<Category[]>([])
const formVisible = ref(false)
const equipmentFormRef = ref()
const searchForm = reactive({ keyword: '', status: undefined })
const pagination = reactive({ pageNum: 1, pageSize: 12, total: 0 })

// 将树形分类扁平化，以便在 Select 中展示
const flatCategoryList = computed(() => {
  const result: Array<Category & { displayName: string }> = []
  const flatten = (list: Category[], prefix = '') => {
    list.forEach(item => {
      result.push({ ...item, displayName: prefix + item.name })
      if (item.children && item.children.length) {
        flatten(item.children, prefix + '-- ')
      }
    })
  }
  flatten(categoryTree.value)
  return result
})

const getList = async () => {
  loading.value = true
  try {
    const { total, ...params } = pagination
    const res = await getEquipmentList({ ...searchForm, ...params })
    tableData.value = res.rows || res.records || []
    pagination.total = res.total || 0
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => { pagination.pageNum = 1; getList() }

const handleAdd = () => {
  formVisible.value = true;
  nextTick(() => equipmentFormRef.value?.setData(null))
}

const handleEdit = (row: Equipment) => {
  formVisible.value = true;
  nextTick(() => equipmentFormRef.value?.setData(row))
}

const handleDelete = (row: Equipment) => {
  if (!row.id) return
  const id = row.id
  ElMessageBox.confirm(`确定要删除器材 "${row.name}" 吗?`, '警告', {
    confirmButtonText: '删除',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await deleteEquipment(id)
    ElMessage.success('已删除')
    getList()
  })
}

const handleFormSuccess = () => { formVisible.value = false; getList() }

const getStatusText = (s?: number) => ['正常','维护中','损坏','报废'][Number(s)] || '未知'
const getStatusColor = (s?: number) => {
  const map: Record<number, { dot: string; text: string }> = {
    0: { dot: 'bg-emerald-300', text: 'text-slate-400' },
    1: { dot: 'bg-orange-300', text: 'text-slate-400' },
    2: { dot: 'bg-red-300', text: 'text-slate-400' },
    3: { dot: 'bg-slate-300', text: 'text-slate-400' }
  }
  return map[Number(s)] || { dot: 'bg-gray-200', text: 'text-gray-400' }
}

onMounted(async () => {
  try {
    const res = await getCategoryTree()
    categoryTree.value = res || []
  } catch (e) {}
  getList()
})
</script>

<style scoped>
.custom-scrollbar::-webkit-scrollbar { width: 0px; }
.custom-scrollbar { -ms-overflow-style: none; scrollbar-width: none; }

/* Custom Element UI Select Style - Slate Theme */
.apple-select :deep(.el-input__wrapper) {
  box-shadow: none !important;
  background-color: transparent;
  padding: 0 8px;
}
.apple-select :deep(.el-input__inner) {
  color: #475569; /* slate-600 */
  font-weight: 500;
  font-size: 14px;
}
.apple-select :deep(.el-input__suffix-inner) {
  opacity: 0.5;
}

/* Pagination Overrides - Slate Theme */
:deep(.ios-pagination-simple .el-pager li) {
  background-color: transparent !important;
  border-radius: 8px;
  margin: 0 4px;
  border: none;
  font-weight: 600;
  color: #94a3b8; /* slate-400 */
}
:deep(.ios-pagination-simple .el-pager li.is-active) {
  background-color: #f1f5f9 !important; /* slate-100 */
  color: #475569; /* slate-600 */
}
:deep(.ios-pagination-simple button) {
  background-color: transparent !important;
  color: #94a3b8;
}
</style>
