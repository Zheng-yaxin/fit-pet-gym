<template>
  <div class="flex flex-col h-full bg-slate-50 font-sans selection:bg-blue-100 relative overflow-hidden">

    <div class="fixed top-[-20%] right-[-10%] w-[500px] h-[500px] bg-blue-100/30 rounded-full blur-[100px] pointer-events-none"></div>

    <header class="h-20 flex-shrink-0 relative flex items-center justify-center px-8 bg-slate-50/90 backdrop-blur-xl border-b border-slate-200/30 z-10 sticky top-0 transition-all">
      <div class="flex flex-col items-center">
        <h1 class="text-xl font-bold tracking-tight text-slate-600">报修工单处理</h1>
        <div class="text-[10px] font-bold uppercase tracking-wider text-slate-400 mt-0.5">Ticket Management</div>
      </div>
    </header>

    <div class="flex-1 overflow-y-auto px-8 py-8 custom-scrollbar relative z-0" v-loading="loading">

      <div class="bg-white rounded-[32px] border border-slate-100 shadow-sm overflow-hidden min-h-[400px]">
        <el-table
            :data="repairList"
            style="width: 100%"
            class="ios-table"
            :header-cell-style="{ background: 'transparent', color: '#94a3b8', fontSize: '11px', fontWeight: '700', textTransform: 'uppercase', letterSpacing: '0.05em', borderBottom: '1px solid #f1f5f9', padding: '20px 24px' }"
            :cell-style="{ background: 'transparent', borderBottom: '1px solid var(--ff-surface-raised)', padding: '20px 24px', color: '#475569', fontSize: '14px', fontWeight: '500' }"
        >
          <el-table-column prop="equipmentName" label="报修器材" min-width="180">
            <template #default="{ row }">
              <div class="flex items-center gap-4">
                <div class="w-10 h-10 rounded-xl bg-slate-50 border border-slate-100 flex items-center justify-center text-lg text-slate-400">
                  <span class="opacity-80">🛠️</span>
                </div>
                <div>
                  <div class="font-bold text-slate-600 leading-tight">{{ row.equipmentName || '未知设备' }}</div>
                  <div class="text-[11px] font-mono text-slate-400 mt-0.5 bg-slate-50 inline-block px-1 rounded border border-slate-100/50">#{{ row.equipmentId }}</div>
                </div>
              </div>
            </template>
          </el-table-column>

          <el-table-column prop="faultDesc" label="故障描述" min-width="260" show-overflow-tooltip>
            <template #default="{ row }">
              <div class="text-slate-500 font-medium leading-relaxed truncate">{{ row.faultDesc }}</div>
            </template>
          </el-table-column>

          <el-table-column label="报修人" width="160">
            <template #default="{ row }">
              <div class="flex flex-col">
                <span class="text-sm font-bold text-slate-600">{{ row.repairByName || row.reporterName || '匿名' }}</span>
                <span class="text-[11px] text-slate-400 font-medium">{{ row.createTime?.split(' ')[0] }}</span>
              </div>
            </template>
          </el-table-column>

          <el-table-column label="当前状态" width="140">
            <template #default="{ row }">
              <div class="flex items-center gap-2">
                <div class="w-2 h-2 rounded-full" :class="getStatusDot(row.status)"></div>
                <span class="text-xs font-bold text-slate-500">{{ getStatusText(row.status) }}</span>
              </div>
            </template>
          </el-table-column>

          <el-table-column label="操作" width="120" fixed="right" align="right">
            <template #default="{ row }">
              <button
                  v-if="row.status < 2"
                  @click="handleProcess(row)"
                  class="px-4 py-1.5 bg-slate-800 text-white rounded-full text-xs font-bold hover:bg-slate-900 hover:scale-105 active:scale-95 transition-all shadow-md shadow-slate-300/50"
              >
                处理
              </button>
              <span v-else class="text-slate-300 text-xs font-bold px-2 uppercase tracking-wide">已归档</span>
            </template>
          </el-table-column>
        </el-table>

        <div class="px-6 py-4 flex justify-end border-t border-slate-50 bg-white">
          <el-pagination
              background
              layout="prev, pager, next"
              :total="total"
              v-model:current-page="queryParams.pageNum"
              :page-size="queryParams.pageSize"
              @current-change="loadData"
              class="ios-pagination"
          />
        </div>
      </div>
    </div>

    <el-dialog
        v-model="processVisible"
        width="420px"
        class="clean-dialog"
        destroy-on-close
        align-center
        :show-close="false"
    >
      <template #header>
        <div class="relative flex items-center justify-center pt-2 pb-2">
          <h3 class="text-[17px] font-bold text-slate-600">工单处理</h3>
        </div>
      </template>

      <div class="px-1 pt-2 pb-2">
        <div class="mb-5 p-4 bg-slate-50 rounded-[20px] border border-slate-100 flex items-start gap-3">
          <div class="text-2xl pt-1 opacity-60">📋</div>
          <div>
            <div class="text-[10px] font-bold text-slate-400 uppercase tracking-wide mb-1">正在处理</div>
            <div class="text-sm font-bold text-slate-600 leading-snug">{{ currentProcessRow?.equipmentName }}</div>
            <div class="text-xs text-slate-400 mt-1 line-clamp-2">{{ currentProcessRow?.faultDesc }}</div>
          </div>
        </div>

        <div class="space-y-4">
          <div>
            <label class="block text-[11px] font-bold text-slate-400 uppercase tracking-wider mb-2 ml-2">更新状态</label>
            <el-select v-model="processForm.status" class="w-full ios-select-filled">
              <el-option label="维修中 (跟进)" :value="1"/>
              <el-option label="已完成 (修复)" :value="2"/>
              <el-option label="驳回 / 取消" :value="3"/>
            </el-select>
          </div>

          <div>
            <label class="block text-[11px] font-bold text-slate-400 uppercase tracking-wider mb-2 ml-2">处理结果 / 备注</label>
            <div class="relative group">
              <textarea
                  v-model="processForm.remark"
                  class="w-full p-4 bg-slate-50 rounded-[20px] border-0 text-[14px] outline-none resize-none focus:bg-white focus:ring-2 ring-slate-200 transition-all text-slate-600 placeholder-slate-400 font-medium"
                  rows="4"
                  placeholder="请填写详细的维修结果或驳回理由..."
              ></textarea>
            </div>
          </div>
        </div>
      </div>

      <template #footer>
        <div class="grid grid-cols-2 gap-3 pt-2">
          <button @click="processVisible=false" class="py-3.5 rounded-xl bg-slate-100 text-slate-500 font-bold text-sm hover:bg-slate-200 transition-colors active:scale-[0.98]">取消</button>
          <button @click="submitProcess" :disabled="submitLoading" class="py-3.5 rounded-xl bg-slate-800 text-white font-bold text-sm shadow-lg shadow-slate-300/50 hover:bg-slate-900 active:scale-[0.98] transition-all disabled:opacity-50">
            {{ submitLoading ? '提交中...' : '确认更新' }}
          </button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { getRepairPage, handleRepair } from '@/api/equipment'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const submitLoading = ref(false)
const repairList = ref([])
const total = ref(0)

const processVisible = ref(false)
const currentProcessRow = ref<any>(null)
const queryParams = reactive({ pageNum: 1, pageSize: 10 })
const processForm = reactive({ id: 0, status: 1, remark: '' })

const loadData = async () => {
  loading.value = true
  try {
    const res: any = await getRepairPage(queryParams)
    repairList.value = res.rows || res.records || []
    total.value = res.total || 0
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const handleProcess = (row: any) => {
  currentProcessRow.value = row
  // 初始化表单，如果当前是待处理(0)，默认选维修中(1)；如果是维修中(1)，默认选已完成(2)
  const nextStatus = row.status === 0 ? 1 : 2

  Object.assign(processForm, {
    id: row.id,
    status: nextStatus,
    remark: row.remark || ''
  })
  processVisible.value = true
}

const submitProcess = async () => {
  if (!processForm.remark && processForm.status === 3) {
    ElMessage.warning('驳回或取消时请填写备注原因')
    return
  }

  submitLoading.value = true
  try {
    await handleRepair(processForm)
    ElMessage.success('状态已更新')
    processVisible.value = false
    loadData()
  } catch (e) {
    console.error(e)
  } finally {
    submitLoading.value = false
  }
}

const getStatusText = (s: number) => ['待处理','维修中','已完成','已取消'][s] || '未知'
// UPDATED: Pastel Dots for ForgeFit OS
const getStatusDot = (s: number) => {
  return [
    'bg-red-300',   // 0
    'bg-blue-300', // 1
    'bg-emerald-300', // 2
    'bg-slate-300' // 3
  ][s] || 'bg-slate-200'
}

onMounted(loadData)
</script>

<style scoped>
.custom-scrollbar::-webkit-scrollbar { width: 0px; }

/* Deep Table Overrides - ForgeFit OS */
:deep(.ios-table) {
  background-color: transparent !important;
  --el-table-border-color: transparent;
  --el-table-bg-color: transparent;
  --el-table-tr-bg-color: transparent;
  --el-table-row-hover-bg-color: var(--ff-surface-raised) !important; /* slate-50 */
}
:deep(.el-table__inner-wrapper::before) { display: none; }

/* Pagination Styling */
:deep(.ios-pagination button),
:deep(.ios-pagination li) {
  background-color: transparent !important;
  border-radius: 8px;
  color: #94a3b8; /* slate-400 */
  font-weight: 600;
}
:deep(.ios-pagination li.is-active) {
  background-color: #f1f5f9 !important; /* slate-100 */
  color: #475569; /* slate-600 */
}

/* Clean Dialog */
:deep(.clean-dialog) {
  background: white;
  border-radius: 32px;
  box-shadow: 0 40px 80px rgba(0,0,0,0.1);
  padding: 24px;
}
:deep(.el-dialog__header) { margin: 0; padding: 0 0 10px; }
:deep(.el-dialog__body) { padding: 0 !important; }
:deep(.el-dialog__footer) { padding: 20px 0 0; }

/* Custom Select - Slate Theme */
:deep(.ios-select-filled .el-input__wrapper) {
  background-color: var(--ff-surface-raised); /* slate-50 */
  border-radius: 12px;
  box-shadow: none !important;
  padding: 8px 12px;
  height: 44px;
}
:deep(.ios-select-filled .el-input__inner) {
  font-weight: 500;
  color: #475569; /* slate-600 */
}
</style>