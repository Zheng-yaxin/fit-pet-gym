<template>
  <div class="feature-page">
    <section class="feature-hero">
      <div>
        <p class="eyebrow">Training Log</p>
        <h1>训练打卡与日志</h1>
        <p class="summary">记录训练时长、强度、消耗和主观感受，后续支撑智能计划调整。</p>
      </div>
      <div class="actions">
        <el-button @click="startCheckin">开始打卡</el-button>
        <el-button type="primary" @click="saveLog">保存日志</el-button>
      </div>
    </section>

    <el-card shadow="never" class="panel">
      <el-form :model="form" label-width="100px">
        <el-form-item label="训练时长">
          <el-input-number v-model="form.durationMinutes" :min="1" />
        </el-form-item>
        <el-form-item label="训练强度">
          <el-slider v-model="form.intensity" :min="1" :max="10" />
        </el-form-item>
        <el-form-item label="消耗热量">
          <el-input-number v-model="form.caloriesBurned" :min="0" />
        </el-form-item>
        <el-form-item label="训练感受">
          <el-input v-model="form.feeling" placeholder="例如：状态不错，腿部疲劳" />
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="never" class="panel">
      <template #header>最近日志</template>
      <el-table :data="logs">
        <el-table-column prop="trainingDate" label="日期" />
        <el-table-column prop="durationMinutes" label="时长" />
        <el-table-column prop="intensity" label="强度" />
        <el-table-column prop="caloriesBurned" label="热量" />
        <el-table-column prop="feeling" label="感受" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { addTrainingLog, getMyTrainingLogs, startTrainingCheckin } from '@/api/training'

const logs = ref<any[]>([])
const form = reactive({ durationMinutes: 60, intensity: 6, caloriesBurned: 250, feeling: '' })

const loadLogs = async () => {
  logs.value = (await getMyTrainingLogs()) as any[]
}

const startCheckin = async () => {
  await startTrainingCheckin()
  ElMessage.success('已开始训练打卡')
}

const saveLog = async () => {
  await addTrainingLog({ ...form, trainingDate: new Date().toISOString() })
  ElMessage.success('训练日志已保存')
  await loadLogs()
}

onMounted(loadLogs)
</script>

<style scoped>
.feature-page { min-height: 100%; padding: 32px; background: #f7f8fb; color: #334155; }
.feature-hero { display: flex; justify-content: space-between; gap: 24px; align-items: center; margin-bottom: 24px; }
.actions { display: flex; gap: 12px; }
.eyebrow { margin: 0 0 8px; color: #64748b; font-size: 12px; font-weight: 700; letter-spacing: .08em; text-transform: uppercase; }
h1 { margin: 0; font-size: 28px; color: #1f2937; }
.summary { margin: 10px 0 0; color: #64748b; }
.panel { margin-bottom: 18px; border-radius: 8px; border: 1px solid #e5e7eb; }
</style>
