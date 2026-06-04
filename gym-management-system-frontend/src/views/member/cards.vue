<template>
  <div class="flex flex-col h-full bg-slate-50 font-sans selection:bg-slate-200">
    <header class="h-24 relative flex items-center justify-center px-10 z-10 sticky top-0 bg-slate-50/90 backdrop-blur-xl transition-all shrink-0">
      <div class="absolute left-1/2 -translate-x-1/2 text-center">
        <h1 class="text-2xl font-bold text-slate-600 tracking-tight">会员卡</h1>
        <p class="text-xs font-medium text-slate-400 tracking-wide mt-1">Manage Subscriptions & Passes</p>
      </div>
    </header>

    <div class="flex-1 overflow-y-auto px-10 pb-8 custom-scrollbar flex flex-col">
      <div class="mb-6 flex gap-4 items-center shrink-0">
        <div class="relative flex-1 max-w-md group">
          <el-icon class="absolute left-4 top-1/2 -translate-y-1/2 text-slate-400 group-focus-within:text-slate-600 transition-colors"><Search /></el-icon>
          <input
              v-model="queryParams.keyword"
              class="w-full h-12 pl-11 pr-4 rounded-xl bg-white border border-slate-200 outline-none focus:border-slate-300 focus:ring-4 focus:ring-slate-100 transition-all text-[15px] font-medium text-slate-600 placeholder-slate-300 shadow-sm"
              placeholder="搜索卡号或会员姓名..."
              @keyup.enter="handleQuery"
          />
        </div>

        <div class="relative w-40 slate-select-wrapper">
          <el-select v-model="queryParams.status" placeholder="状态筛选" class="w-full" :teleported="false">
            <el-option label="全部状态" value="" />
            <el-option label="正常" value="0" />
            <el-option label="挂失" value="1" />
            <el-option label="过期" value="2" />
          </el-select>
        </div>

        <button @click="handleQuery" class="h-12 px-6 bg-white text-slate-600 border border-slate-200 rounded-xl font-semibold text-sm shadow-sm hover:bg-slate-50 hover:border-slate-300 transition-all flex items-center gap-2">
          <el-icon><Search /></el-icon> 搜索
        </button>
        <button @click="resetQuery" class="h-12 px-6 bg-transparent text-slate-400 rounded-xl font-semibold text-sm hover:text-slate-600 hover:bg-slate-100 transition-all flex items-center gap-2">
          <el-icon><Refresh /></el-icon> 重置
        </button>
      </div>

      <div class="flex-1 bg-white rounded-[24px] p-2 shadow-sm shadow-slate-200/60 border border-slate-100 overflow-hidden flex flex-col">
        <el-table :data="cardList" style="width: 100%" v-loading="loading" class="slate-table"
                  :header-cell-style="{ background: 'transparent', color: '#94a3b8', fontWeight: '600', fontSize: '11px', textTransform: 'uppercase', letterSpacing: '0.05em', borderBottom: '1px solid #f1f5f9', padding: '16px 24px' }"
                  :cell-style="{ padding: '16px 24px', borderBottom: '1px solid var(--ff-surface-raised)' }">

          <el-table-column prop="cardNo" label="CARD NO" min-width="180">
            <template #default="scope">
              <div class="flex items-center gap-3">
                <div class="w-10 h-6 bg-slate-200 rounded-md shadow-inner border border-white/50"></div>
                <span class="font-mono font-medium text-slate-600 tracking-wide">{{ scope.row.cardNo }}</span>
              </div>
            </template>
          </el-table-column>

          <el-table-column label="MEMBER" min-width="150">
            <template #default="scope">
              <div>
                <div class="font-bold text-slate-600 text-[14px]">{{ scope.row.memberName || '未知' }}</div>
                <div class="text-[11px] text-slate-400 font-mono mt-0.5">ID: {{ scope.row.memberId }}</div>
              </div>
            </template>
          </el-table-column>

          <el-table-column prop="cardType" label="TYPE" width="120">
            <template #default="scope">
               <span class="px-3 py-1 bg-slate-50 border border-slate-100 rounded-lg text-xs font-bold text-slate-500">
                 {{ scope.row.cardType }}
               </span>
            </template>
          </el-table-column>

          <el-table-column prop="expireDate" label="EXPIRY" width="160">
            <template #default="scope">
              <div class="flex items-center gap-2">
                <div class="w-1.5 h-1.5 rounded-full" :class="isExpired(scope.row.expireDate) ? 'bg-rose-400' : 'bg-emerald-400'"></div>
                <span :class="isExpired(scope.row.expireDate) ? 'text-rose-500 font-medium' : 'text-slate-600 font-medium'">
                  {{ scope.row.expireDate ? scope.row.expireDate.split(' ')[0] : '-' }}
                </span>
              </div>
            </template>
          </el-table-column>

          <el-table-column prop="remainingTimes" label="REMAINING" width="130" v-if="cardList.some(c => c.cardType === '次卡')">
            <template #default="scope">
              <span class="text-slate-600 font-mono">{{ scope.row.remainingTimes }}</span>
            </template>
          </el-table-column>

          <el-table-column label="STATUS" width="120">
            <template #default="scope">
              <span v-if="scope.row.status === '0'" class="px-2.5 py-1 bg-emerald-50 text-emerald-600 rounded-lg text-xs font-bold">正常</span>
              <span v-if="scope.row.status === '1'" class="px-2.5 py-1 bg-amber-50 text-amber-600 rounded-lg text-xs font-bold">挂失</span>
              <span v-if="scope.row.status === '2'" class="px-2.5 py-1 bg-rose-50 text-rose-600 rounded-lg text-xs font-bold">过期</span>
            </template>
          </el-table-column>

          <el-table-column label="ACTIONS" align="right" width="180">
            <template #default="scope">
              <div class="flex justify-end gap-2">
                <el-tooltip content="续费/续期" placement="top" :hide-after="0" effect="light">
                  <button @click="handleRenew(scope.row)" class="w-8 h-8 rounded-lg hover:bg-slate-100 text-slate-400 hover:text-slate-600 transition-all flex items-center justify-center" v-if="scope.row.status === '0'">
                    <el-icon><RefreshLeft /></el-icon>
                  </button>
                </el-tooltip>
                <el-tooltip content="权益与场馆资产" placement="top" :hide-after="0" effect="light">
                  <button @click="handleShowBenefits(scope.row)" class="w-8 h-8 rounded-lg hover:bg-sky-50 text-slate-400 hover:text-sky-500 transition-all flex items-center justify-center">
                    <el-icon><InfoFilled /></el-icon>
                  </button>
                </el-tooltip>
                <el-tooltip content="挂失" placement="top" :hide-after="0" effect="light">
                  <button @click="handleReportLoss(scope.row)" class="w-8 h-8 rounded-lg hover:bg-rose-50 text-slate-400 hover:text-rose-500 transition-all flex items-center justify-center" v-if="scope.row.status === '0'">
                    <el-icon><WarningFilled /></el-icon>
                  </button>
                </el-tooltip>
              </div>
            </template>
          </el-table-column>
        </el-table>

        <div class="mt-auto pt-6 pb-2 flex justify-center border-t border-slate-50">
          <el-pagination
              background
              layout="prev, pager, next, total"
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

    <el-drawer v-model="benefitVisible" title="会员权益与场馆资产" size="420px">
      <div v-loading="benefitLoading" class="benefit-drawer">
        <template v-if="currentBenefit">
          <section class="benefit-hero">
            <span>{{ currentBenefit.active ? 'ACTIVE BENEFITS' : 'NO ACTIVE CARD' }}</span>
            <h2>{{ currentBenefit.card?.cardType || '未激活' }}</h2>
            <p>
              余额 ¥{{ Number(currentBenefit.walletBalance || 0).toFixed(2) }}
              <template v-if="currentBenefit.daysLeft !== undefined"> · 剩余 {{ currentBenefit.daysLeft }} 天</template>
            </p>
          </section>

          <section class="benefit-metrics">
            <article><span>团课额度</span><strong>{{ currentBenefit.groupCourseQuota || 0 }}</strong></article>
            <article><span>私教额度</span><strong>{{ currentBenefit.privateTrainingQuota || 0 }}</strong></article>
            <article><span>储物柜</span><strong>{{ currentBenefit.lockerAccess ? '可用' : '无' }}</strong></article>
          </section>

          <section class="benefit-section">
            <h3>权益动作</h3>
            <p v-for="item in currentBenefit.entitlements || []" :key="item">{{ item }}</p>
            <p v-for="item in currentBenefit.actions || []" :key="item" class="accent">{{ item }}</p>
          </section>

          <section class="benefit-section">
            <h3>绑定器材</h3>
            <p v-for="asset in (currentBenefit.availableAssets || []).slice(0, 6)" :key="asset.equipmentId">
              {{ asset.name || '器材' }} · {{ asset.location || '待确认' }} · {{ asset.statusDesc || '可用' }}
            </p>
          </section>

          <section class="benefit-section">
            <h3>推荐场区</h3>
            <p v-for="area in currentBenefit.recommendedAreas || []" :key="area.areaId">
              {{ area.areaName || '场区' }} · {{ area.occupancyPercent || 0 }}% · {{ area.action || '适合下一组训练' }}
            </p>
          </section>
        </template>
      </div>
    </el-drawer>

    <CardRenewModal v-model="renewVisible" :card="currentCard" @success="handleQuery" />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import {
  Search, Refresh, RefreshLeft, WarningFilled, InfoFilled
} from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import CardRenewModal from './components/CardRenewModal.vue'
import { getMemberBenefitSummary, getMemberCardList, reportLossCard, type MemberBenefitSummary, type MemberCard } from '@/api/member'

const loading = ref(false)
const cardList = ref<MemberCard[]>([])
const total = ref(0)
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  keyword: '',
  status: ''
})

const renewVisible = ref(false)
const currentCard = ref<MemberCard | null>(null)
const benefitVisible = ref(false)
const benefitLoading = ref(false)
const currentBenefit = ref<MemberBenefitSummary | null>(null)

const isExpired = (dateString?: string) => {
  if (!dateString) return true
  const date = new Date(dateString)
  return date < new Date()
}

const handleSizeChange = (size: number) => {
  queryParams.pageSize = size
  handleQuery()
}

const resetQuery = () => {
  queryParams.pageNum = 1
  queryParams.keyword = ''
  queryParams.status = ''
  handleQuery()
}

const handleRenew = (row: MemberCard) => {
  currentCard.value = { ...row }
  renewVisible.value = true
}

const handleShowBenefits = async (row: MemberCard) => {
  if (!row.memberId) return
  benefitVisible.value = true
  benefitLoading.value = true
  currentBenefit.value = null
  try {
    currentBenefit.value = await getMemberBenefitSummary(row.memberId) as any
  } catch (e) {
    console.error('Benefit fetch error:', e)
    ElMessage.error('权益摘要加载失败')
  } finally {
    benefitLoading.value = false
  }
}

const handleReportLoss = (row: MemberCard) => {
  ElMessageBox.confirm(
      `确定要将卡号 ${row.cardNo} 挂失吗?`,
      '警告',
      { type: 'warning', confirmButtonText: '确定挂失', cancelButtonText: '取消' }
  ).then(async () => {
    await reportLossCard(row.id!)
    ElMessage.success('挂失成功')
    handleQuery()
  }).catch(() => {})
}

const handleQuery = async () => {
  loading.value = true
  try {
    const res: any = await getMemberCardList(queryParams)
    if (res && typeof res === 'object') {
      if (res.records) {
        cardList.value = res.records
        total.value = res.total || 0
      } else if (res.rows) {
        cardList.value = res.rows
        total.value = res.total || 0
      } else {
        cardList.value = []
        total.value = 0
      }
    } else {
      cardList.value = []
    }
  } catch (e) {
    console.error('Fetch error:', e)
    ElMessage.error('加载会员卡列表失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => handleQuery())
</script>

<style scoped>
/* Scrollbar */
.custom-scrollbar::-webkit-scrollbar { width: 0px; background: transparent; }

/* Slate Table Overrides */
:deep(.slate-table) {
  --el-table-border-color: transparent;
  --el-table-header-bg-color: transparent;
  --el-table-tr-bg-color: transparent;
  background: transparent !important;
  --el-fill-color-lighter: var(--ff-surface-raised); /* Slate-50 */
}

:deep(.el-table__inner-wrapper::before) { display: none; }
:deep(.el-table--enable-row-hover .el-table__body tr:hover > td.el-table__cell) {
  background-color: var(--ff-surface-raised) !important; /* Slate-50 */
}

/* Select Override */
:deep(.slate-select-wrapper .el-input__wrapper) {
  box-shadow: none !important;
  background-color: white;
  border: 1px solid #e2e8f0; /* Slate-200 */
  border-radius: 12px;
  height: 48px;
  padding: 0 16px;
}
:deep(.slate-select-wrapper .el-input__inner) {
  font-weight: 500;
  color: #475569; /* Slate-600 */
}

/* Pagination */
:deep(.slate-pagination .el-pager li) {
  background: transparent;
  border-radius: 8px;
  color: #94a3b8;
  font-weight: 500;
}
:deep(.slate-pagination .el-pager li.is-active) {
  background: #e2e8f0;
  color: #475569;
}
:deep(.slate-pagination .btn-prev), :deep(.slate-pagination .btn-next) { background: transparent; }

.benefit-drawer {
  display: grid;
  gap: 16px;
}

.benefit-hero {
  border: 1px solid #e2e8f0;
  border-radius: 16px;
  padding: 18px;
  background: #f8fafc;
}

.benefit-hero span,
.benefit-section h3,
.benefit-metrics span {
  color: #64748b;
  font-size: 11px;
  font-weight: 800;
  letter-spacing: .08em;
  text-transform: uppercase;
}

.benefit-hero h2 {
  margin: 8px 0;
  color: #0f172a;
  font-size: 26px;
}

.benefit-hero p,
.benefit-section p {
  margin: 0;
  color: #475569;
  line-height: 1.6;
}

.benefit-metrics {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 8px;
}

.benefit-metrics article {
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  padding: 12px;
  background: #fff;
}

.benefit-metrics strong {
  display: block;
  margin-top: 6px;
  color: #0f172a;
  font-size: 20px;
}

.benefit-section {
  border-top: 1px solid #e2e8f0;
  padding-top: 14px;
}

.benefit-section h3 {
  margin: 0 0 8px;
}

.benefit-section .accent {
  color: #0f766e;
  font-weight: 700;
}
</style>
