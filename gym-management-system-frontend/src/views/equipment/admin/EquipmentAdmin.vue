<template>
  <div class="min-h-screen bg-slate-50 pb-safe relative overflow-hidden font-sans selection:bg-blue-100">

    <div class="fixed top-0 left-0 w-full h-96 bg-gradient-to-b from-blue-50/30 to-transparent pointer-events-none"></div>

    <header class="sticky top-0 z-40 px-8 py-5 h-20 flex items-center justify-center transition-all duration-300 bg-slate-50/90 backdrop-blur-xl border-b border-slate-200/30">

      <div class="flex flex-col items-center">
        <h2 class="text-xl font-bold tracking-tight text-slate-600">器材管理</h2>
        <p class="text-[10px] uppercase tracking-widest text-slate-400 font-bold mt-0.5">Administration</p>
      </div>

      <div class="absolute right-8">
        <button
            @click="openForm()"
            class="px-5 py-2.5 rounded-full bg-slate-800 text-white font-bold text-[13px] shadow-lg shadow-slate-300/50 hover:bg-slate-900 active:scale-95 transition-all flex items-center gap-2"
        >
          <span class="flex items-center gap-1.5">
            <i class="el-icon-plus"></i> 新增器材
          </span>
        </button>
      </div>
    </header>

    <main class="px-8 py-8 animate-fade-in">
      <div class="bg-white rounded-[32px] shadow-sm border border-slate-100 overflow-hidden relative z-10 p-2">

        <el-table
            v-loading="loading"
            :data="equipmentList"
            style="width: 100%"
            class="ios-table"
            :header-cell-style="{ background: 'transparent', color: '#94a3b8', fontSize: '11px', fontWeight: '700', textTransform: 'uppercase', letterSpacing: '0.05em', borderBottom: '1px solid #f1f5f9', padding: '20px 24px' }"
            :cell-style="{ background: 'transparent', borderBottom: '1px solid var(--ff-surface-raised)', padding: '20px 24px', color: '#475569', fontSize: '14px', fontWeight: '500' }"
        >
          <el-table-column prop="name" label="器材名称" min-width="140">
            <template #default="{ row }">
              <span class="font-bold text-slate-600">{{ row.name }}</span>
            </template>
          </el-table-column>

          <el-table-column prop="code" label="器材编号" min-width="120">
            <template #default="{ row }">
              <span class="font-mono text-xs bg-slate-50 px-2 py-1 rounded-md text-slate-400 border border-slate-100">{{ row.code }}</span>
            </template>
          </el-table-column>

          <el-table-column prop="categoryName" label="所属分类" min-width="120">
            <template #default="{ row }">
              <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-slate-50 text-slate-500">
                {{ row.categoryName }}
              </span>
            </template>
          </el-table-column>

          <el-table-column prop="statusDesc" label="状态" width="120">
            <template #default="{ row }">
              <div class="flex items-center gap-2">
                <div class="w-2 h-2 rounded-full"
                     :class="{'bg-emerald-300': row.status === 0, 'bg-orange-300': row.status === 1, 'bg-red-300': row.status === 2, 'bg-slate-300': row.status === 3}"></div>
                <span class="text-xs text-slate-500">{{ row.statusDesc }}</span>
              </div>
            </template>
          </el-table-column>

          <el-table-column label="操作" width="220" fixed="right" align="right">
            <template #default="scope">
              <div class="flex items-center justify-end gap-2">
                <button @click="openDetail(scope.row.id)" class="px-3 py-1.5 rounded-lg text-xs font-bold text-slate-500 hover:bg-slate-50 transition-colors">查看</button>
                <button @click="openForm(scope.row)" class="px-3 py-1.5 rounded-lg text-xs font-bold text-blue-500 hover:bg-blue-50 transition-colors">编辑</button>
                <button @click="handleDelete(scope.row.id)" class="px-2 py-1.5 rounded-lg text-slate-400 hover:text-red-400 hover:bg-red-50 transition-colors">
                  <i class="el-icon-delete"></i>
                </button>
              </div>
            </template>
          </el-table-column>
        </el-table>

        <div class="px-6 py-4 flex justify-center bg-white border-t border-slate-50">
          <el-pagination
              v-model:current-page="pagination.pageNum"
              v-model:page-size="pagination.pageSize"
              :page-sizes="[10, 20, 50]"
              :total="pagination.total"
              layout="prev, pager, next"
              @size-change="handleSizeChange"
              @current-change="handleCurrentChange"
              class="ios-pagination"
          />
        </div>
      </div>
    </main>

    <EquipmentDetail
        v-if="detailVisible"
        v-model="detailVisible"
        ref="detailRef"
    />

    <EquipmentForm
        v-if="formVisible"
        v-model="formVisible"
        :categoryList="categoryList"
        @success="handleFormSuccess"
        ref="formRef"
    />

    <el-dialog
        v-model="statusVisible"
        width="380px"
        :show-close="false"
        class="clean-dialog rounded-[24px]"
        align-center
    >
      <template #header>
        <div class="text-center pt-2">
          <h3 class="text-lg font-bold text-slate-600">状态设置</h3>
        </div>
      </template>

      <div class="px-2 pt-2 pb-6">
        <el-form :model="statusForm" label-position="top">
          <div class="bg-slate-50 rounded-2xl p-4 mb-4 border border-slate-100">
            <el-form-item label="当前状态" class="mb-0">
              <span class="text-slate-500 font-bold text-sm">{{ statusOptions.find(item => item.value === currentStatus)?.label || '-' }}</span>
            </el-form-item>
          </div>

          <el-form-item label="目标状态">
            <el-select v-model="statusForm.status" placeholder="请选择状态" class="w-full ios-select">
              <el-option
                  v-for="item in statusOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
              />
            </el-select>
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <div class="grid grid-cols-2 gap-3 px-2 pb-2">
          <button @click="statusVisible = false" class="py-3 rounded-xl bg-slate-100 text-slate-500 font-bold text-sm hover:bg-slate-200 transition-colors">取消</button>
          <button @click="confirmStatusChange" :disabled="statusLoading" class="py-3 rounded-xl bg-slate-800 text-white font-bold text-sm shadow-md hover:bg-slate-900 transition-all">
            {{ statusLoading ? '提交中...' : '确定' }}
          </button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import EquipmentDetail from './components/EquipmentDetail.vue'
import EquipmentForm from './components/EquipmentForm.vue'
import { getEquipmentList, getCategoryList, deleteEquipment, updateEquipmentStatus } from '@/api/equipment'

// 状态管理
const loading = ref(false)
const detailVisible = ref(false)
const formVisible = ref(false)
const statusVisible = ref(false)

const statusLoading = ref(false)  // 状态设置按钮 loading

const currentEquipmentId = ref(null)
const currentStatus = ref(null)

// 引用
const detailRef = ref(null)
const formRef = ref(null)

// 数据存储
const equipmentList = ref([])
const categoryList = ref([])

// 分页参数
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

// 状态选项配置
const statusOptions = [
  { value: 0, label: '正常', type: 'success' },
  { value: 1, label: '维修中', type: 'warning' },
  { value: 2, label: '已报废', type: 'danger' },
  { value: 3, label: '闲置', type: 'info' }
]

const statusMap = {
  0: { type: 'success' },
  1: { type: 'warning' },
  2: { type: 'danger' },
  3: { type: 'info' }
}

// 状态表单
const statusForm = reactive({
  status: null
})

// 重置状态表单
const resetStatusForm = () => {
  statusForm.status = null
  currentEquipmentId.value = null
  currentStatus.value = null
  statusLoading.value = false
}

// 加载器材列表
const loadData = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize
    }
    const res = await getEquipmentList(params)
    equipmentList.value = res.records || []
    pagination.total = res.total || 0
  } catch (err) {
    console.error('加载器材列表失败：', err)
    equipmentList.value = []
    ElMessage.error('加载器材列表失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 加载分类
const loadCategories = async () => {
  try {
    const res = await getCategoryList()
    categoryList.value = res || []
  } catch (err) {
    console.error('加载分类列表失败：', err)
    categoryList.value = []
    ElMessage.error('加载分类列表失败，请稍后重试')
  }
}

// 分页处理
const handleSizeChange = (size) => {
  pagination.pageSize = size
  loadData()
}

const handleCurrentChange = (page) => {
  pagination.pageNum = page
  loadData()
}

// 打开详情
const openDetail = (id) => {
  detailVisible.value = true
  nextTick(() => {
    detailRef.value?.loadData(id).catch(() => {
      ElMessage.error('加载详情失败')
      detailVisible.value = false
    })
  })
}

// 打开表单（新增/编辑）
const openForm = (row) => {
  formVisible.value = true
  nextTick(() => {
    formRef.value?.setData(row || null)
  })
}

// 打开状态设置弹窗
const openStatusSetting = (row) => {
  currentEquipmentId.value = row.id
  currentStatus.value = row.status
  statusForm.status = row.status
  statusVisible.value = true
}

// 确认变更状态
const confirmStatusChange = async () => {
  if (statusForm.status === currentStatus.value) {
    ElMessage.info('状态未变更，无需提交')
    statusVisible.value = false
    resetStatusForm()
    return
  }

  statusLoading.value = true
  try {
    await updateEquipmentStatus(currentEquipmentId.value, statusForm.status)
    ElMessage.success('器材状态更新成功')
    statusVisible.value = false
    resetStatusForm()
    loadData()
  } catch (err) {
    console.error('更新状态失败：', err)
    ElMessage.error('更新状态失败，请稍后重试')
  } finally {
    statusLoading.value = false
  }
}

// 删除器材（使用 ElMessageBox 确认框，更美观一致）
const handleDelete = (id) => {
  ElMessageBox.confirm('确定要删除该器材吗？此操作不可恢复！', '删除确认', {
    confirmButtonText: '确定删除',
    cancelButtonText: '取消',
    type: 'warning',
    distinguishCancelAndClose: true
  })
      .then(async () => {
        try {
          await deleteEquipment(id)
          ElMessage.success('器材删除成功')
          loadData()
        } catch (err) {
          console.error('删除失败：', err)
          ElMessage.error('删除失败，请稍后重试')
        }
      })
      .catch((action) => {
        if (action === 'cancel') {
          ElMessage.info('已取消删除')
        }
      })
}

// 表单提交成功回调
const handleFormSuccess = () => {
  formVisible.value = false
  loadData()
}

// 初始化
onMounted(() => {
  loadData()
  loadCategories()
})
</script>

<style scoped>
.pb-safe { padding-bottom: max(env(safe-area-inset-bottom), 20px); }

.animate-fade-in {
  animation: fadeIn 0.8s cubic-bezier(0.16, 1, 0.3, 1);
}
@keyframes fadeIn { from { opacity: 0; transform: translateY(20px); } to { opacity: 1; transform: translateY(0); } }

/* Deep Element Plus Overrides for ForgeFit OS Look */
:deep(.ios-table) {
  background-color: transparent !important;
  --el-table-border-color: transparent;
  --el-table-bg-color: transparent;
  --el-table-tr-bg-color: transparent;
  --el-table-row-hover-bg-color: var(--ff-surface-raised) !important; /* slate-50 */
}
:deep(.el-table__inner-wrapper::before) { display: none; }
:deep(.el-table__border-left-patch) { display: none; }

/* Dialog Clean Effect */
:deep(.clean-dialog) {
  background: white;
  box-shadow: 0 24px 48px rgba(0,0,0,0.1);
  border: 1px solid #f1f5f9;
}
:deep(.el-dialog__header) { margin: 0; padding: 10px; }
:deep(.el-dialog__body) { padding: 10px 24px; }
:deep(.el-dialog__footer) { padding: 10px 24px 24px; border-top: none; }

/* Custom Inputs in Dialog */
:deep(.ios-select .el-input__wrapper) {
  background-color: var(--ff-surface-raised); /* slate-50 */
  border-radius: 12px;
  box-shadow: none !important;
  padding: 8px 12px;
  transition: all 0.2s;
}
:deep(.ios-select .el-input__wrapper:hover) { background-color: #f1f5f9; }
:deep(.ios-select .el-input.is-focus .el-input__wrapper) {
  background-color: var(--ff-surface);
  box-shadow: 0 0 0 2px var(--ff-border-strong) !important; /* slate-300 */
}

/* Pagination */
:deep(.ios-pagination .el-pager li) {
  background: transparent !important;
  font-weight: 600;
  color: #94a3b8;
}
:deep(.ios-pagination .el-pager li.is-active) {
  color: #475569;
}
:deep(.ios-pagination button) {
  background: transparent !important;
}
</style>