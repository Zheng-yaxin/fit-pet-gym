<template>
  <div class="flex flex-col h-full bg-slate-50 font-sans selection:bg-slate-200">
    <header class="h-24 relative flex items-center justify-center px-10 sticky top-0 z-20 shrink-0">
      <div class="absolute left-1/2 -translate-x-1/2 text-center">
        <h1 class="text-2xl font-bold text-slate-600 tracking-tight leading-tight">会员管理</h1>
        <p class="text-xs font-medium text-slate-400 tracking-wide mt-1">Total Members: {{ total }}</p>
      </div>

      <div class="absolute right-10 flex items-center gap-4">
        <button
            @click="handleQuery"
            class="w-10 h-10 rounded-xl bg-white shadow-sm border border-slate-200 flex items-center justify-center text-slate-500 hover:text-slate-700 hover:bg-slate-50 transition-all duration-300 group"
            title="刷新"
        >
          <RefreshCcw :size="18" class="group-hover:rotate-180 transition-transform duration-700 ease-spring" />
        </button>
        <button
            @click="handleAdd"
            class="h-10 px-6 bg-slate-800 text-white rounded-lg font-semibold text-sm shadow-md shadow-slate-200 hover:bg-slate-700 hover:scale-105 active:scale-95 transition-all duration-300 flex items-center gap-2"
        >
          <Plus :size="18" stroke-width="2.5" />
          <span>新增会员</span>
        </button>
      </div>
    </header>

    <div class="flex-1 overflow-hidden flex flex-col px-10 pb-8">
      <div class="flex gap-4 mb-6 shrink-0">
        <div class="relative flex-1 max-w-lg group">
          <div class="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none">
            <Search class="text-slate-400 group-focus-within:text-slate-600 transition-colors duration-300" :size="18" />
          </div>
          <input
              v-model="queryParams.keyword"
              class="w-full h-12 pl-11 pr-4 rounded-xl bg-white border border-slate-200 outline-none focus:border-slate-300 focus:ring-4 focus:ring-slate-100 transition-all duration-300 text-[15px] font-medium text-slate-600 placeholder-slate-300 shadow-sm"
              placeholder="搜索姓名或手机号..."
              @keyup.enter="handleQuery"
          />
        </div>
      </div>

      <div class="flex-1 bg-white rounded-[24px] shadow-sm shadow-slate-200/60 border border-slate-100 overflow-hidden flex flex-col relative isolate">
        <div class="flex-1 overflow-hidden p-2">
          <el-table
              :data="memberList"
              style="width: 100%; height: 100%;"
              v-loading="loading"
              class="slate-table"
              :header-cell-style="{ background: 'transparent', color: '#94a3b8', fontWeight: '600', fontSize: '11px', textTransform: 'uppercase', letterSpacing: '0.05em', borderBottom: '1px solid #f1f5f9', padding: '16px 24px' }"
              :cell-style="{ padding: '16px 24px', borderBottom: '1px solid var(--ff-surface-raised)' }"
          >
            <el-table-column label="MEMBER" min-width="220">
              <template #default="scope">
                <div class="flex items-center gap-3">
                  <div class="w-9 h-9 rounded-full bg-slate-100 text-slate-600 flex items-center justify-center font-bold text-sm border border-slate-50 shrink-0">
                    {{ (scope.row.nickname || scope.row.name || 'U').charAt(0).toUpperCase() }}
                  </div>
                  <div class="flex flex-col min-w-0">
                    <span class="font-bold text-slate-600 text-[13px] truncate">{{ scope.row.nickname || scope.row.name || '未命名' }}</span>
                    <span class="text-xs text-slate-400 font-medium tracking-wide mt-0.5 truncate">{{ scope.row.phone }}</span>
                  </div>
                </div>
              </template>
            </el-table-column>

            <el-table-column label="GENDER" min-width="120" align="center">
              <template #default="scope">
                <div class="inline-flex items-center justify-center w-8 h-8 rounded-lg"
                     :class="scope.row.gender === 1 ? 'bg-blue-50 text-blue-500' : 'bg-rose-50 text-rose-500'">
                  <User :size="16" v-if="scope.row.gender === 1" />
                  <User :size="16" v-else />
                </div>
              </template>
            </el-table-column>

            <el-table-column prop="cardType" label="MEMBERSHIP" min-width="160">
              <template #default="scope">
                <span v-if="scope.row.cardType" class="inline-flex items-center px-2.5 py-1 rounded-lg text-xs font-semibold bg-slate-50 text-slate-600 border border-slate-100 whitespace-nowrap">
                  {{ scope.row.cardType }}
                </span>
                <span v-else class="text-xs text-slate-400 italic">无会员卡</span>
              </template>
            </el-table-column>

            <el-table-column label="BALANCE" min-width="160">
              <template #default="scope">
                <span class="text-emerald-600 font-bold font-mono tracking-tight text-[13px]">¥ {{ scope.row.balance || '0.00' }}</span>
              </template>
            </el-table-column>

            <el-table-column label="STATUS" min-width="140" align="center">
              <template #default="scope">
                <div class="flex items-center justify-center gap-2">
                  <span class="w-2 h-2 rounded-full"
                        :class="scope.row.status === '0' ? 'bg-emerald-300' : 'bg-rose-300'"></span>
                  <span class="text-xs font-bold" :class="scope.row.status === '0' ? 'text-emerald-600' : 'text-rose-500'">
                    {{ scope.row.status === '0' ? 'Active' : 'Disabled' }}
                  </span>
                </div>
              </template>
            </el-table-column>

            <el-table-column label="ACTIONS" align="right" width="280" fixed="right">
              <template #default="scope">
                <div class="flex items-center justify-end gap-2">
                  <button @click="handleEdit(scope.row)" class="w-8 h-8 rounded-lg hover:bg-slate-100 flex items-center justify-center text-slate-400 hover:text-slate-600 transition-all duration-200" title="编辑">
                    <Edit :size="16" />
                  </button>
                  <button @click="handleRecharge(scope.row)" class="w-8 h-8 rounded-lg hover:bg-emerald-50 flex items-center justify-center text-slate-400 hover:text-emerald-600 transition-all duration-200" title="充值">
                    <Wallet :size="16" />
                  </button>
                  <button @click="handleCard(scope.row)" class="w-8 h-8 rounded-lg hover:bg-indigo-50 flex items-center justify-center text-slate-400 hover:text-indigo-600 transition-all duration-200" title="办卡">
                    <CreditCard :size="16" />
                  </button>
                  <div class="w-px h-3 bg-slate-200 mx-1"></div>
                  <button @click="handleDelete(scope.row)" class="w-8 h-8 rounded-lg hover:bg-rose-50 flex items-center justify-center text-slate-400 hover:text-rose-500 transition-all duration-200" title="删除">
                    <Trash2 :size="16" />
                  </button>
                </div>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <div class="h-16 flex items-center justify-end px-6 border-t border-slate-50 bg-white">
          <el-pagination
              background
              layout="prev, pager, next"
              :total="total"
              v-model:current-page="queryParams.pageNum"
              v-model:page-size="queryParams.pageSize"
              @current-change="handleQuery"
              class="slate-pagination"
          />
        </div>
      </div>
    </div>

    <MemberModal v-model="modalVisible" :data="modalData" @success="handleQuery" />
    <RechargeModal v-model="rechargeVisible" :member="currentMember" @success="handleQuery" />
    <CardModal v-model="cardVisible" :member="currentMember" @success="handleQuery" />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { getMemberList, deleteMember, type Member } from '@/api/member'
import { Search, Plus, Edit, Trash2, RefreshCcw, Wallet, CreditCard, User } from 'lucide-vue-next'
import { ElMessageBox, ElMessage } from 'element-plus'
import MemberModal from './components/MemberModal.vue'
import RechargeModal from './components/RechargeModal.vue'
import CardModal from './components/CardModal.vue'

const loading = ref(false)
const memberList = ref<Member[]>([])
const total = ref(0)
const queryParams = reactive({ pageNum: 1, pageSize: 10, keyword: '' })

// Modal States
const modalVisible = ref(false)
const modalData = ref<Member | null>(null)
const rechargeVisible = ref(false)
const cardVisible = ref(false)
const currentMember = ref<Member | null>(null)

const handleQuery = async () => {
  loading.value = true
  try {
    const res: any = await getMemberList(queryParams)
    memberList.value = res.rows || res.records || []
    total.value = res.total || 0
  } catch (e) { console.error(e) }
  finally { loading.value = false }
}

const handleAdd = () => { modalData.value = null; modalVisible.value = true }
const handleEdit = (row: Member) => { modalData.value = { ...row }; modalVisible.value = true }
const handleRecharge = (row: Member) => { currentMember.value = { ...row }; rechargeVisible.value = true }
const handleCard = (row: Member) => { currentMember.value = { ...row }; cardVisible.value = true }

const handleDelete = (row: Member) => {
  ElMessageBox.confirm(`确定要删除会员 ${row.nickname || row.phone} 吗?`, '删除确认', {
    confirmButtonText: '删除', cancelButtonText: '取消', type: 'warning'
  }).then(async () => {
    await deleteMember(row.id!)
    ElMessage.success('已删除')
    handleQuery()
  }).catch(() => {})
}

onMounted(handleQuery)
</script>

<style scoped>
/* Slate Table Overrides */
:deep(.slate-table) {
  --el-table-border-color: transparent;
  --el-table-header-bg-color: transparent;
  --el-table-tr-bg-color: transparent;
  background: transparent !important;
  --el-fill-color-lighter: var(--ff-surface-raised); /* Slate-50 */
}

:deep(.el-table__inner-wrapper::before),
:deep(.el-table__border-left-patch) {
  display: none !important;
}

:deep(.el-table--enable-row-hover .el-table__body tr:hover > td.el-table__cell) {
  background-color: var(--ff-surface-raised) !important; /* Slate-50 */
}

/* Pagination Styling */
:deep(.slate-pagination .el-pager li),
:deep(.slate-pagination .btn-prev),
:deep(.slate-pagination .btn-next) {
  background: transparent;
  border-radius: 8px;
  font-weight: 500;
  color: #94a3b8;
}

:deep(.slate-pagination .el-pager li.is-active) {
  background: #e2e8f0;
  color: #475569;
  box-shadow: none;
}

:deep(.slate-pagination .el-pager li:hover:not(.is-active)) {
  color: #475569;
  background: #f1f5f9;
}

.ease-spring {
  transition-timing-function: cubic-bezier(0.175, 0.885, 0.32, 1.275);
}
</style>