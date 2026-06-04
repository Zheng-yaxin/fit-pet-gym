<template>
  <div class="feature-page">
    <section class="feature-hero">
      <div>
        <p class="eyebrow">Training Plan</p>
        <h1>智能训练计划</h1>
        <p class="summary">根据目标和每周训练频率生成计划框架，后续可接入动作库和健康数据。</p>
      </div>
      <el-button type="primary" @click="handleGenerate">生成计划</el-button>
    </section>

    <el-card shadow="never" class="panel">
      <el-form :model="form" label-width="100px">
        <el-form-item label="训练目标">
          <el-select v-model="form.goal" class="w-full">
            <el-option label="减脂塑形" value="减脂塑形" />
            <el-option label="增肌力量" value="增肌力量" />
            <el-option label="提升体能" value="提升体能" />
          </el-select>
        </el-form-item>
        <el-form-item label="每周次数">
          <el-input-number v-model="form.weeklyFrequency" :min="1" :max="7" />
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="never" class="panel">
      <template #header>当前计划</template>
      <el-descriptions v-if="currentPlan" :column="2" border>
        <el-descriptions-item label="目标">{{ currentPlan.goal }}</el-descriptions-item>
        <el-descriptions-item label="每周次数">{{ currentPlan.weeklyFrequency }}</el-descriptions-item>
        <el-descriptions-item label="来源">{{ currentPlan.source }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ currentPlan.status === '0' ? '启用' : '停用' }}</el-descriptions-item>
      </el-descriptions>
      <el-empty v-else description="暂无训练计划" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { generateTrainingPlan, getCurrentTrainingPlan } from '@/api/training'

const form = reactive({ goal: '提升体能', weeklyFrequency: 3 })
const currentPlan = ref<any>(null)

const loadPlan = async () => {
  currentPlan.value = await getCurrentTrainingPlan()
}

const handleGenerate = async () => {
  currentPlan.value = await generateTrainingPlan(form)
  ElMessage.success('训练计划已生成')
}

onMounted(loadPlan)
</script>

<style scoped>
.feature-page { min-height: 100%; padding: 32px; background: #f7f8fb; color: #334155; }
.feature-hero { display: flex; justify-content: space-between; gap: 24px; align-items: center; margin-bottom: 24px; }
.eyebrow { margin: 0 0 8px; color: #64748b; font-size: 12px; font-weight: 700; letter-spacing: .08em; text-transform: uppercase; }
h1 { margin: 0; font-size: 28px; color: #1f2937; }
.summary { margin: 10px 0 0; color: #64748b; }
.panel { margin-bottom: 18px; border-radius: 8px; border: 1px solid #e5e7eb; }
.w-full { width: 100%; }
</style>
