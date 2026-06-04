<template>
  <div class="admin-page">
    <header>
      <div>
        <p>Feedback</p>
        <h1>Feedback analytics</h1>
      </div>
      <el-button @click="loadData">Refresh</el-button>
    </header>

    <div class="stats">
      <el-card shadow="never">
        <p>Total feedback</p>
        <strong>{{ stats.total || 0 }}</strong>
      </el-card>
      <el-card shadow="never">
        <p>Average rating</p>
        <strong>{{ Number(stats.avgRating || 0).toFixed(1) }}</strong>
      </el-card>
      <el-card shadow="never">
        <p>Course</p>
        <strong>{{ stats.courseTotal || 0 }}</strong>
      </el-card>
      <el-card shadow="never">
        <p>PT</p>
        <strong>{{ stats.personalTrainingTotal || 0 }}</strong>
      </el-card>
      <el-card shadow="never">
        <p>Pending</p>
        <strong>{{ stats.pendingTotal || 0 }}</strong>
      </el-card>
      <el-card shadow="never">
        <p>Handled</p>
        <strong>{{ stats.handledTotal || 0 }}</strong>
      </el-card>
      <el-card shadow="never">
        <p>Follow-up chat</p>
        <strong>{{ stats.followUpTotal || 0 }}</strong>
      </el-card>
    </div>

    <el-table :data="list" class="table" border>
      <el-table-column label="Status" width="140">
        <template #default="{ row }">
          <el-tag :type="row.handleStatus === 'handled' ? 'success' : row.followUpRequired ? 'warning' : 'info'">
            {{ row.handleStatus === 'handled' ? 'Handled' : row.followUpRequired ? 'Follow up' : 'Pending' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="Type" width="120">
        <template #default="{ row }">
          <el-tag :type="row.feedbackType === 'personal_training' ? 'success' : 'primary'">
            {{ row.feedbackType === 'personal_training' ? 'PT' : 'Course' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="Target" min-width="220">
        <template #default="{ row }">
          <strong>{{ row.targetTitle || row.courseName || `#${row.scheduleId || row.bookingId || '-'}` }}</strong>
          <p class="muted-row">
            {{ row.feedbackType === 'personal_training' ? `Booking #${row.bookingId || '-'}` : `Schedule #${row.scheduleId || '-'}` }}
          </p>
        </template>
      </el-table-column>
      <el-table-column label="Member" min-width="160">
        <template #default="{ row }">
          {{ row.memberName || `Member #${row.memberId || '-'}` }}
        </template>
      </el-table-column>
      <el-table-column label="Coach" min-width="160">
        <template #default="{ row }">
          {{ row.coachName || `Coach #${row.coachId || '-'}` }}
        </template>
      </el-table-column>
      <el-table-column prop="rating" label="Rating" width="100" />
      <el-table-column prop="intensity" label="Intensity" width="110" />
      <el-table-column prop="content" label="Content" min-width="220" />
      <el-table-column prop="adminReply" label="Admin reply" min-width="220" />
      <el-table-column prop="createTime" label="Created" min-width="170" />
      <el-table-column label="Action" width="120" fixed="right">
        <template #default="{ row }">
          <el-button size="small" type="primary" plain @click="openHandle(row)">Handle</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" title="Handle feedback" width="520px">
      <el-form :model="handleForm" label-width="120px">
        <el-form-item label="Status">
          <el-select v-model="handleForm.handleStatus" class="full">
            <el-option label="Pending" value="pending" />
            <el-option label="Handled" value="handled" />
          </el-select>
        </el-form-item>
        <el-form-item label="Follow up">
          <el-switch v-model="followUpSwitch" />
          <p class="form-hint">When enabled, the member and assigned coach can open a real chat thread from this feedback.</p>
        </el-form-item>
        <el-form-item label="Admin reply">
          <el-input
            v-model="handleForm.adminReply"
            type="textarea"
            :rows="4"
            placeholder="Summarize the action for front desk, coach, or member follow-up."
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">Cancel</el-button>
        <el-button type="primary" :loading="saving" @click="submitHandle">Save</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getAdminFeedbackList, getAdminFeedbackStats, handleAdminFeedback } from '@/api/feedback'

const list = ref<any[]>([])
const stats = ref<any>({})
const dialogVisible = ref(false)
const saving = ref(false)
const currentFeedbackId = ref<number | null>(null)
const handleForm = reactive({
  handleStatus: 'handled',
  adminReply: '',
  followUpRequired: 0
})

const followUpSwitch = computed({
  get: () => Boolean(handleForm.followUpRequired),
  set: (value: boolean) => { handleForm.followUpRequired = value ? 1 : 0 }
})

const loadData = async () => {
  list.value = (await getAdminFeedbackList()) as any[]
  stats.value = await getAdminFeedbackStats()
}

const openHandle = (row: any) => {
  currentFeedbackId.value = row.id
  handleForm.handleStatus = row.handleStatus || 'handled'
  handleForm.adminReply = row.adminReply || ''
  handleForm.followUpRequired = row.followUpRequired || 0
  dialogVisible.value = true
}

const submitHandle = async () => {
  if (!currentFeedbackId.value) return
  saving.value = true
  try {
    await handleAdminFeedback(currentFeedbackId.value, handleForm)
    ElMessage.success('Feedback handled')
    dialogVisible.value = false
    await loadData()
  } finally {
    saving.value = false
  }
}

onMounted(loadData)
</script>

<style scoped>
.admin-page { padding: 32px; min-height: 100%; background: var(--ff-surface-raised); }
header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
header p { margin: 0 0 6px; color: #64748b; font-size: 12px; font-weight: 700; text-transform: uppercase; }
h1 { margin: 0; color: #1f2937; font-size: 24px; }
.stats { display: grid; grid-template-columns: repeat(auto-fit, minmax(120px, 1fr)); gap: 16px; margin-bottom: 20px; max-width: 1180px; }
.stats p { margin: 0 0 8px; color: #64748b; }
.stats strong { font-size: 30px; color: #0f172a; }
.table { border-radius: 8px; overflow: hidden; }
.muted-row { margin: 4px 0 0; color: #94a3b8; font-size: 12px; }
.form-hint { margin: 8px 0 0; color: #94a3b8; font-size: 12px; line-height: 1.5; }
.full { width: 100%; }
@media (max-width: 980px) {
  .stats { grid-template-columns: repeat(2, minmax(0, 1fr)); }
}
</style>
