<template>
  <div class="flex h-screen w-full bg-slate-50 font-sans overflow-hidden selection:bg-slate-200">

    <aside class="w-[280px] flex-shrink-0 bg-white border-r border-slate-100 flex flex-col z-30 relative">
      <div class="h-24 flex items-center px-8">
        <div class="w-10 h-10 rounded-xl bg-slate-800 flex items-center justify-center text-white mr-4 shadow-lg shadow-slate-200">
          <el-icon :size="20"><Monitor /></el-icon>
        </div>
        <span class="text-xl font-bold tracking-tight text-slate-600">Gym Admin</span>
      </div>

      <nav class="flex-1 px-4 py-6 space-y-2 overflow-y-auto">
        <div class="text-[11px] font-bold text-slate-400 uppercase tracking-widest px-4 mb-3">Menu</div>

        <router-link to="/dashboard" custom v-slot="{ navigate, isActive }">
          <div @click="navigate" class="flex items-center gap-3 px-4 py-3.5 rounded-xl cursor-pointer transition-all duration-300 group relative overflow-hidden"
               :class="isActive ? 'bg-slate-100 text-slate-700' : 'text-slate-400 hover:bg-slate-50 hover:text-slate-600'">
            <el-icon :class="isActive ? 'text-slate-700' : 'text-slate-400 group-hover:text-slate-600'" :size="20"><Odometer /></el-icon>
            <span class="font-semibold relative z-10">Dashboard</span>
          </div>
        </router-link>

        <router-link to="/member/list" custom v-slot="{ navigate, isActive }">
          <div @click="navigate" class="flex items-center gap-3 px-4 py-3.5 rounded-xl cursor-pointer transition-all duration-300 group relative"
               :class="isActive ? 'bg-slate-100 text-slate-700' : 'text-slate-400 hover:bg-slate-50 hover:text-slate-600'">
            <el-icon :class="isActive ? 'text-slate-700' : 'text-slate-400 group-hover:text-slate-600'" :size="20"><User /></el-icon>
            <span class="font-semibold relative z-10">Members</span>
          </div>
        </router-link>

        <router-link to="/member/cards" custom v-slot="{ navigate, isActive }">
          <div @click="navigate" class="flex items-center gap-3 px-4 py-3.5 rounded-xl cursor-pointer transition-all duration-300 group relative"
               :class="isActive ? 'bg-slate-100 text-slate-700' : 'text-slate-400 hover:bg-slate-50 hover:text-slate-600'">
            <el-icon :class="isActive ? 'text-slate-700' : 'text-slate-400 group-hover:text-slate-600'" :size="20"><CreditCard /></el-icon>
            <span class="font-semibold relative z-10">Member Cards</span>
          </div>
        </router-link>
      </nav>

      <div class="p-6 border-t border-slate-50">
        <div class="flex items-center gap-3 px-5 py-4 rounded-xl bg-slate-50 border border-slate-100 cursor-pointer hover:bg-rose-50 hover:border-rose-100 transition-all group" @click="router.push('/')">
          <el-icon class="text-slate-400 group-hover:text-rose-500 transition-colors"><Back /></el-icon>
          <span class="text-sm font-bold text-slate-600 group-hover:text-rose-600 transition-colors">Back Home</span>
        </div>
      </div>
    </aside>

    <main class="flex-1 flex flex-col min-w-0 overflow-hidden relative">

      <header class="h-24 relative flex items-center justify-between px-10 z-10 shrink-0">
        <div class="absolute left-1/2 -translate-x-1/2 text-center">
          <h1 class="text-2xl font-bold text-slate-600 tracking-tight">Member List</h1>
          <p class="text-xs font-medium text-slate-400 tracking-wide mt-1">View and manage gym members</p>
        </div>

        <div class="absolute right-10">
          <button @click="handleAdd" class="h-10 px-6 bg-slate-800 text-white rounded-lg font-semibold text-sm shadow-md shadow-slate-200 hover:bg-slate-700 active:scale-95 transition-all flex items-center gap-2">
            <el-icon><Plus /></el-icon> Add Member
          </button>
        </div>
      </header>

      <div class="flex-1 overflow-y-auto px-10 pb-8 custom-scrollbar">
        <div class="mb-8 flex gap-4">
          <div class="relative flex-1 max-w-lg group">
            <el-icon class="absolute left-4 top-1/2 -translate-y-1/2 text-slate-400 group-focus-within:text-slate-600 transition-colors"><Search /></el-icon>
            <input v-model="queryParams.keyword" class="w-full h-12 pl-11 pr-4 rounded-xl bg-white border border-slate-200 outline-none focus:border-slate-300 focus:ring-4 focus:ring-slate-100 transition-all text-[15px] font-medium text-slate-600 placeholder-slate-300 shadow-sm" placeholder="Search by username or phone..." @keyup.enter="handleQuery" />
          </div>
          <button @click="handleQuery" class="h-12 px-8 bg-white text-slate-600 border border-slate-200 rounded-xl font-bold text-sm shadow-sm hover:bg-slate-50 hover:border-slate-300 transition-all flex items-center gap-2">
            <el-icon><Search /></el-icon> Search
          </button>
          <button @click="resetQuery" class="h-12 px-6 bg-transparent text-slate-400 rounded-xl font-bold text-sm hover:bg-slate-100 hover:text-slate-600 transition-all flex items-center gap-2">
            <el-icon><Refresh /></el-icon> Reset
          </button>
        </div>

        <div class="bg-white rounded-[24px] p-2 shadow-sm shadow-slate-200/60 border border-slate-100">
          <el-table :data="memberList" style="width: 100%" v-loading="loading" class="slate-table"
                    :header-cell-style="{ background: 'transparent', color: '#94a3b8', fontWeight: '600', fontSize: '11px', textTransform: 'uppercase', letterSpacing: '0.05em', borderBottom: '1px solid #f1f5f9', padding: '16px 24px' }"
                    :cell-style="{ padding: '16px 24px', borderBottom: '1px solid var(--ff-surface-raised)' }">
            <el-table-column prop="id" label="ID" width="80" align="center">
              <template #default="scope">
                <span class="text-slate-400 font-mono text-xs">#{{ scope.row.id }}</span>
              </template>
            </el-table-column>

            <el-table-column label="USER INFO" width="200">
              <template #default="scope">
                <div class="flex items-center gap-3">
                  <div class="w-9 h-9 rounded-full bg-slate-100 text-slate-500 flex items-center justify-center font-bold text-sm border border-white shadow-sm">
                    {{ scope.row.nickname ? scope.row.nickname.charAt(0).toUpperCase() : 'U' }}
                  </div>
                  <div>
                    <div class="font-bold text-slate-600 text-[13px]">{{ scope.row.nickname || 'Unknown' }}</div>
                    <div class="text-xs text-slate-400 font-mono">@{{ scope.row.username }}</div>
                  </div>
                </div>
              </template>
            </el-table-column>

            <el-table-column prop="phone" label="PHONE" width="140">
              <template #default="scope">
                <span class="text-slate-500 text-sm">{{ scope.row.phone }}</span>
              </template>
            </el-table-column>

            <el-table-column label="GENDER" width="100">
              <template #default="scope">
                <span v-if="scope.row.gender === 1" class="px-2.5 py-1 bg-blue-50 text-blue-500 rounded-md text-xs font-semibold">Male</span>
                <span v-else class="px-2.5 py-1 bg-rose-50 text-rose-500 rounded-md text-xs font-semibold">Female</span>
              </template>
            </el-table-column>

            <el-table-column prop="cardType" label="CARD" width="120">
              <template #default="scope">
                <span v-if="scope.row.cardType" class="text-xs font-medium bg-slate-50 px-2 py-1 rounded text-slate-500 border border-slate-100">{{scope.row.cardType}}</span>
              </template>
            </el-table-column>

            <el-table-column prop="balance" label="BALANCE" width="120">
              <template #default="scope">
                <span class="text-emerald-500 font-bold font-mono">¥{{ scope.row.balance || 0 }}</span>
              </template>
            </el-table-column>

            <el-table-column label="STATUS" width="100">
              <template #default="scope">
                <div class="flex items-center gap-1.5">
                  <div class="w-1.5 h-1.5 rounded-full" :class="scope.row.status === '0' ? 'bg-emerald-400' : 'bg-rose-400'"></div>
                  <span class="text-sm font-medium" :class="scope.row.status === '0' ? 'text-emerald-600' : 'text-rose-500'">{{ scope.row.status === '0' ? 'Active' : 'Off' }}</span>
                </div>
              </template>
            </el-table-column>

            <el-table-column label="ACTIONS" align="right" width="220">
              <template #default="scope">
                <div class="flex justify-end gap-1">
                  <button @click="handleEdit(scope.row)" class="w-8 h-8 rounded-lg hover:bg-slate-100 text-slate-400 hover:text-slate-600 transition-all flex items-center justify-center">
                    <el-icon><Edit /></el-icon>
                  </button>
                  <button @click="handleRecharge(scope.row)" class="w-8 h-8 rounded-lg hover:bg-emerald-50 text-slate-400 hover:text-emerald-600 transition-all flex items-center justify-center">
                    <el-icon><Wallet /></el-icon>
                  </button>
                  <button @click="handleCard(scope.row)" class="w-8 h-8 rounded-lg hover:bg-indigo-50 text-slate-400 hover:text-indigo-600 transition-all flex items-center justify-center">
                    <el-icon><CreditCard /></el-icon>
                  </button>
                  <button @click="handleDelete(scope.row)" class="w-8 h-8 rounded-lg hover:bg-rose-50 text-slate-400 hover:text-rose-500 transition-all flex items-center justify-center">
                    <el-icon><Delete /></el-icon>
                  </button>
                </div>
              </template>
            </el-table-column>
          </el-table>

          <div class="mt-6 flex justify-center pb-4">
            <el-pagination
                background
                layout="prev, pager, next"
                :total="total"
                v-model:current-page="queryParams.pageNum"
                v-model:page-size="queryParams.pageSize"
                @current-change="handleQuery"
                @size-change="handleSizeChange"
                class="slate-pagination"
            />
          </div>
        </div>
      </div>

      <MemberModal v-model="modalVisible" :data="modalData" @success="handleQuery" />
      <RechargeModal v-model="rechargeVisible" :member="currentMember" @success="handleQuery" />
      <CardModal v-model="cardVisible" :member="currentMember" @success="handleQuery" />
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import {
  getMemberList,
  deleteMember,
  type Member
} from '@/api/member'
import {
  Search,
  Plus,
  Monitor,
  Odometer,
  User,
  Back,
  Edit,
  Delete,
  Refresh,
  Wallet,
  CreditCard
} from '@element-plus/icons-vue'
import { ElMessageBox, ElMessage } from 'element-plus'
import MemberModal from './components/MemberModal.vue'
import RechargeModal from './components/RechargeModal.vue'
import CardModal from './components/CardModal.vue'

const router = useRouter()
const loading = ref(false)
const memberList = ref<Member[]>([])
const total = ref(0)
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  keyword: ''
})

const modalVisible = ref(false)
const modalData = ref<Member | null>(null)
const rechargeVisible = ref(false)
const cardVisible = ref(false)
const currentMember = ref<Member | null>(null)

const handleSizeChange = (size: number) => {
  queryParams.pageSize = size
  handleQuery()
}

const resetQuery = () => {
  queryParams.pageNum = 1
  queryParams.keyword = ''
  handleQuery()
}

const handleAdd = () => {
  modalData.value = null
  modalVisible.value = true
}

const handleEdit = (row: Member) => {
  modalData.value = { ...row }
  modalVisible.value = true
}

const handleRecharge = (row: Member) => {
  currentMember.value = { ...row }
  rechargeVisible.value = true
}

const handleCard = (row: Member) => {
  currentMember.value = { ...row }
  cardVisible.value = true
}

const handleDelete = (row: Member) => {
  ElMessageBox.confirm(`Delete member ${row.username}?`, 'Warning', { type: 'warning' })
      .then(async () => {
        await deleteMember(row.id!)
        ElMessage.success('Deleted successfully')
        handleQuery()
      })
      .catch(() => {})
}

const handleQuery = async () => {
  loading.value = true
  try {
    const res: any = await getMemberList(queryParams)

    if (res && typeof res === 'object') {
      if (res.records) {
        memberList.value = res.records
        total.value = res.total || 0
      }
      else if (res.rows) {
        memberList.value = res.rows
        total.value = res.total || 0
      }
      else if (res.data && res.data.records) {
        memberList.value = res.data.records
        total.value = res.data.total || 0
      }
      else if (res.data && res.data.rows) {
        memberList.value = res.data.rows
        total.value = res.data.total || 0
      }
      else if (Array.isArray(res)) {
        memberList.value = res
        total.value = res.length
      }
      else {
        console.error('Unknown response format:', res)
        ElMessage.error('数据格式错误，请查看控制台')
        memberList.value = []
        total.value = 0
      }
    } else {
      console.error('Invalid response:', res)
      memberList.value = []
      total.value = 0
    }

  } catch (e) {
    console.error('Fetch error:', e)
    ElMessage.error('Failed to load member list')
    memberList.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

onMounted(() => handleQuery())
</script>

<style scoped>
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

:deep(.slate-pagination .el-pager li) { background: transparent; border-radius: 8px; color: #94a3b8; font-weight: 500; }
:deep(.slate-pagination .el-pager li.is-active) { background: #e2e8f0; color: #475569; }
:deep(.slate-pagination .btn-prev), :deep(.slate-pagination .btn-next) { background: transparent; }

.custom-scrollbar::-webkit-scrollbar { width: 0; }
</style>
