<template>
  <div class="feature-page">
    <section class="feature-hero">
      <div>
        <p class="eyebrow">Course Feedback</p>
        <h1>Post-session feedback</h1>
        <p class="summary">Review completed group classes and personal training sessions from one place.</p>
      </div>
      <el-button type="primary" :disabled="!pendingList.length" @click="submit">Submit feedback</el-button>
    </section>

    <el-card shadow="never" class="panel">
      <el-form :model="form" label-width="120px">
        <el-form-item label="Waiting item">
          <el-select
            v-model="selectedPendingKey"
            class="full"
            placeholder="Select a completed session"
            @change="applyPending"
          >
            <el-option
              v-for="item in pendingList"
              :key="pendingKey(item)"
              :label="formatPendingLabel(item)"
              :value="pendingKey(item)"
            />
          </el-select>
          <p v-if="!pendingList.length" class="empty-tip">No completed session is waiting for feedback.</p>
        </el-form-item>
        <el-form-item label="Rating">
          <el-rate v-model="form.rating" />
        </el-form-item>
        <el-form-item label="Intensity">
          <el-slider v-model="form.intensity" :min="1" :max="10" />
        </el-form-item>
        <el-form-item label="Feedback">
          <el-input v-model="form.content" type="textarea" :rows="4" placeholder="What helped, and what should improve?" />
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="never" class="panel">
      <template #header>My feedback</template>
      <el-table :data="feedbackList">
        <el-table-column label="Type" width="120">
          <template #default="{ row }">
            <el-tag :type="row.feedbackType === 'personal_training' ? 'success' : 'primary'">
              {{ row.feedbackType === 'personal_training' ? 'PT' : 'Course' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="Target" min-width="160">
          <template #default="{ row }">
            {{ row.feedbackType === 'personal_training' ? `Booking #${row.bookingId || '-'}` : `Schedule #${row.scheduleId || '-'}` }}
          </template>
        </el-table-column>
        <el-table-column prop="coachId" label="Coach ID" width="100" />
        <el-table-column prop="rating" label="Rating" width="100" />
        <el-table-column prop="intensity" label="Intensity" width="110" />
        <el-table-column prop="content" label="Content" min-width="220" />
        <el-table-column prop="createTime" label="Created" min-width="170" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getMyCourseFeedback, getPendingFeedback, submitCourseFeedback } from '@/api/feedback'

const form = reactive({
  scheduleId: undefined as number | undefined,
  bookingId: undefined as number | undefined,
  courseId: undefined as number | undefined,
  coachId: undefined as number | undefined,
  feedbackType: 'course',
  rating: 5,
  intensity: 6,
  content: ''
})
const feedbackList = ref<any[]>([])
const pendingList = ref<any[]>([])
const selectedPendingKey = ref('')

const pendingKey = (item: any) => item?.bookingId ? `pt-${item.bookingId}` : `course-${item?.scheduleId || item?.enrollmentId || 'unknown'}`

const loadList = async () => {
  const [pending, feedback] = await Promise.all([getPendingFeedback(), getMyCourseFeedback()])
  pendingList.value = pending as any[]
  feedbackList.value = feedback as any[]
  if (!selectedPendingKey.value && pendingList.value.length) {
    selectedPendingKey.value = pendingKey(pendingList.value[0])
    applyPending()
  }
}

const formatPendingLabel = (item: any) => {
  const type = item.feedbackType === 'personal_training' ? 'PT' : 'Course'
  const time = item.classTime
    ? String(item.classTime).slice(0, 16).replace('T', ' ')
    : [item.startTime, item.endTime].filter(Boolean).join('-')
  return `${type} | ${item.courseName || 'Session'} | ${item.coachName || 'Coach'} | ${time || 'TBD'}`
}

const applyPending = () => {
  const item = pendingList.value.find((pending) => pendingKey(pending) === selectedPendingKey.value)
  form.scheduleId = item?.scheduleId
  form.bookingId = item?.bookingId
  form.courseId = item?.courseId
  form.coachId = item?.coachId
  form.feedbackType = item?.feedbackType || 'course'
}

const submit = async () => {
  if (!form.scheduleId && !form.bookingId) {
    ElMessage.warning('Select a completed session first')
    return
  }
  await submitCourseFeedback(form)
  ElMessage.success('Feedback submitted')
  form.content = ''
  selectedPendingKey.value = ''
  form.scheduleId = undefined
  form.bookingId = undefined
  form.courseId = undefined
  form.coachId = undefined
  form.feedbackType = 'course'
  await loadList()
}

onMounted(loadList)
</script>

<style scoped>
.feature-page { min-height: 100%; padding: 32px; background: #f7f8fb; color: #334155; }
.feature-hero { display: flex; justify-content: space-between; gap: 24px; align-items: center; margin-bottom: 24px; }
.eyebrow { margin: 0 0 8px; color: #64748b; font-size: 12px; font-weight: 700; letter-spacing: .08em; text-transform: uppercase; }
h1 { margin: 0; font-size: 28px; color: #1f2937; }
.summary { margin: 10px 0 0; color: #64748b; }
.panel { margin-bottom: 18px; border-radius: 8px; border: 1px solid #e5e7eb; }
.full { width: 100%; }
.empty-tip { margin: 8px 0 0; color: #94a3b8; font-size: 13px; }
</style>
