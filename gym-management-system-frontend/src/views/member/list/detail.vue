<template>
  <div class="member-container flex flex-col h-full bg-slate-50 font-sans selection:bg-slate-200">

    <header class="h-24 px-10 relative flex items-center justify-center shrink-0 z-20 transition-all">
      <div class="absolute left-1/2 -translate-x-1/2 text-center">
        <h1 class="text-2xl font-bold text-slate-600 tracking-tight">Details List</h1>
        <p class="text-slate-400 mt-1 text-xs font-medium tracking-wide">Manage gym members and their information</p>
      </div>

      <button
          @click="handleAdd"
          class="absolute right-10 flex items-center gap-2 px-5 py-2.5 bg-slate-800 text-white rounded-lg font-semibold text-sm shadow-md shadow-slate-200 hover:bg-slate-700 hover:scale-105 active:scale-95 transition-all"
      >
        <el-icon><Plus /></el-icon> Add Member
      </button>
    </header>

    <main class="flex-1 overflow-y-auto px-10 pb-8 custom-scrollbar flex flex-col">
      <div class="mb-6 flex gap-3 shrink-0">
        <div class="relative flex-1 max-w-lg group">
          <el-icon class="absolute left-4 top-1/2 -translate-y-1/2 text-slate-400 group-focus-within:text-slate-600 transition-colors"><Search /></el-icon>
          <input
              v-model="queryParams.keyword"
              class="w-full pl-11 pr-4 py-3 rounded-xl bg-white border border-slate-200 outline-none focus:border-slate-300 focus:ring-4 focus:ring-slate-50 transition-all text-[14px] font-medium text-slate-600 placeholder-slate-300 shadow-sm"
              placeholder="Search by username or phone..."
              @keyup.enter="handleQuery"
          />
        </div>
        <button
            @click="handleQuery"
            class="px-6 py-3 bg-white text-slate-600 border border-slate-200 rounded-xl font-bold text-sm shadow-sm hover:bg-slate-50 transition-all"
        >
          Search
        </button>
        <button
            @click="resetQuery"
            class="px-5 py-3 bg-transparent text-slate-400 rounded-xl font-bold text-sm hover:text-slate-600 hover:bg-slate-100 transition-all"
        >
          Reset
        </button>
      </div>

      <div class="flex-1 bg-white rounded-[24px] p-2 shadow-sm shadow-slate-200/50 border border-slate-100 flex flex-col">
        <el-table
            :data="memberList"
            style="width: 100%"
            v-loading="loading"
            class="slate-table flex-1"
            :header-cell-style="{ background: 'transparent', color: '#94a3b8', fontWeight: '600', fontSize: '11px', textTransform: 'uppercase', letterSpacing: '0.05em', borderBottom: '1px solid #f1f5f9', padding: '16px 24px' }"
            :cell-style="{ padding: '16px 24px', borderBottom: '1px solid var(--ff-surface-raised)' }"
        >
          <el-table-column prop="id" label="ID" width="80" align="center">
            <template #default="scope">
              <span class="text-slate-400 text-xs font-mono">#{{ scope.row.id }}</span>
            </template>
          </el-table-column>

          <el-table-column label="USER INFO" width="240">
            <template #default="scope">
              <div class="flex items-center gap-4">
                <div class="w-10 h-10 rounded-full bg-slate-50 text-slate-600 flex items-center justify-center font-bold text-sm border border-slate-100">
                  {{ scope.row.nickname ? scope.row.nickname.charAt(0).toUpperCase() : 'U' }}
                </div>
                <div>
                  <div class="font-bold text-slate-600 text-[14px]">{{ scope.row.nickname || 'Unknown' }}</div>
                  <div class="text-[11px] text-slate-400 font-mono">@{{ scope.row.username }}</div>
                </div>
              </div>
            </template>
          </el-table-column>

          <el-table-column prop="phone" label="PHONE" align="center">
            <template #default="scope">
              <span class="text-slate-500">{{ scope.row.phone }}</span>
            </template>
          </el-table-column>

          <el-table-column label="GENDER" width="100" align="center">
            <template #default="scope">
              <span v-if="scope.row.gender === 1" class="px-2.5 py-1 bg-blue-50 text-blue-500 rounded-md text-xs font-semibold">Male</span>
              <span v-else class="px-2.5 py-1 bg-pink-50 text-pink-500 rounded-md text-xs font-semibold">Female</span>
            </template>
          </el-table-column>

          <el-table-column label="STATUS" width="120" align="center">
            <template #default="scope">
              <div class="flex items-center gap-2 justify-center">
                <div class="w-2 h-2 rounded-full" :class="scope.row.status === '0' ? 'bg-emerald-300' : 'bg-rose-300'"></div>
                <span class="text-xs font-bold" :class="scope.row.status === '0' ? 'text-emerald-600' : 'text-rose-500'">{{ scope.row.status === '0' ? 'Active' : 'Disabled' }}</span>
              </div>
            </template>
          </el-table-column>

          <el-table-column prop="createTime" label="JOINED DATE" width="180" align="center">
            <template #default="scope">
              <span class="text-slate-400 text-xs font-medium">{{ scope.row.createTime }}</span>
            </template>
          </el-table-column>

          <el-table-column label="ACTIONS" width="160" align="center">
            <template #default="scope">
              <div class="flex items-center justify-center gap-2">
                <button
                    @click="handleEdit(scope.row)"
                    class="w-8 h-8 rounded-lg hover:bg-slate-100 text-slate-400 hover:text-slate-600 transition-all flex items-center justify-center"
                    title="Edit"
                >
                  <el-icon><Edit /></el-icon>
                </button>
                <button
                    @click="handleDelete(scope.row)"
                    class="w-8 h-8 rounded-lg hover:bg-rose-50 text-slate-400 hover:text-rose-500 transition-all flex items-center justify-center"
                    title="Delete"
                >
                  <el-icon><Delete /></el-icon>
                </button>
              </div>
            </template>
          </el-table-column>
        </el-table>

        <div class="mt-4 flex justify-center pb-2">
          <el-pagination
              background
              layout="prev, pager, next, sizes, total"
              :total="total"
              v-model:current-page="queryParams.pageNum"
              v-model:page-size="queryParams.pageSize"
              :page-sizes="[5, 10, 20, 50]"
              @current-change="handleQuery"
              @size-change="handleSizeChange"
              class="slate-pagination"
          />
        </div>
      </div>

      <MemberModal
          v-model="modalVisible"
          :data="modalData"
          @success="handleQuery"
      />
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { getMemberList, deleteMember, type Member } from '@/api/member'
import { Search, Plus, Edit, Delete } from '@element-plus/icons-vue'
import { ElMessageBox, ElMessage } from 'element-plus'
import MemberModal from './components/MemberModal.vue'

// 状态管理
const loading = ref(false)
const memberList = ref<Member[]>([])
const total = ref(0)

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  keyword: ''
})

// 弹窗状态
const modalVisible = ref(false)
const modalData = ref<Member | null>(null)

// 页面加载时查询数据
onMounted(() => {
  handleQuery()
})

// 查询会员列表
const handleQuery = async () => {
  loading.value = true
  try {
    const res: any = await getMemberList(queryParams)
    memberList.value = res.records || []
    total.value = res.total || 0
  } catch (error) {
    console.error('Failed to fetch member list:', error)
    ElMessage.error('Failed to load member data')
  } finally {
    loading.value = false
  }
}

// 处理分页大小变化
const handleSizeChange = (size: number) => {
  queryParams.pageSize = size
  handleQuery()
}

// 重置查询条件
const resetQuery = () => {
  queryParams.keyword = ''
  queryParams.pageNum = 1
  handleQuery()
}

// 打开新增弹窗
const handleAdd = () => {
  modalData.value = null
  modalVisible.value = true
}

// 打开编辑弹窗
const handleEdit = (row: Member) => {
  modalData.value = { ...row }
  modalVisible.value = true
}

// 删除会员
const handleDelete = async (row: Member) => {
  try {
    await ElMessageBox.confirm(
        `Are you sure to delete member "${row.nickname}"?`,
        'Confirm Delete',

        {
          confirmButtonText: 'Delete',
          cancelButtonText: 'Cancel',
          type: 'warning'
        }
    )

    await deleteMember(row.id!)
    ElMessage.success('Member deleted successfully')
    handleQuery()
  } catch (error) {
    // 取消删除时不显示错误信息
    if (error !== 'cancel') {
      console.error('Delete failed:', error)
      ElMessage.error('Failed to delete member')
    }
  }
}
</script>

<style scoped>
.member-container {
  min-height: 100vh;
}

.custom-scrollbar::-webkit-scrollbar {
  width: 0;
}

/* Slate Table Styles */
:deep(.slate-table) {
  --el-table-border-color: transparent;
  --el-table-header-bg-color: transparent;
  --el-table-tr-bg-color: transparent;
  background: transparent !important;
  --el-fill-color-lighter: var(--ff-surface-raised);
}
:deep(.el-table__inner-wrapper::before) { display: none; }
:deep(.el-table--enable-row-hover .el-table__body tr:hover > td.el-table__cell) {
  background-color: var(--ff-surface-raised) !important;
}

/* Pagination */
:deep(.slate-pagination .el-pager li) { background: transparent; border-radius: 8px; color: #94a3b8; }
:deep(.slate-pagination .el-pager li.is-active) { background: #e2e8f0; color: #475569; }
:deep(.slate-pagination .btn-prev), :deep(.slate-pagination .btn-next) { background: transparent; }
</style>