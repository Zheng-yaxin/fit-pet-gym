<template>
  <div class="feature-page">
    <section class="feature-hero">
      <div>
        <p class="eyebrow">Exercise Library</p>
        <h1>动作教学库</h1>
        <p class="summary">按肌群、器材和难度查找动作，后续可接入视频和替代动作推荐。</p>
      </div>
      <el-input v-model="query.keyword" placeholder="搜索动作" class="search" @keyup.enter="loadList" />
    </section>

    <div class="exercise-grid">
      <el-card v-for="item in exercises" :key="item.id" shadow="never" class="exercise-card">
        <h3>{{ item.name }}</h3>
        <p>{{ item.targetMuscle || '全身' }} · {{ item.equipment || '不限器材' }}</p>
        <el-tag>{{ item.difficulty || '基础' }}</el-tag>
        <div class="tips">{{ item.tips || '保持动作稳定，控制节奏。' }}</div>
      </el-card>
    </div>
    <el-empty v-if="exercises.length === 0" description="暂无动作数据" />
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { getExerciseList } from '@/api/exercise'

const query = reactive({ pageNum: 1, pageSize: 20, keyword: '' })
const exercises = ref<any[]>([])

const loadList = async () => {
  const res: any = await getExerciseList(query)
  exercises.value = res?.rows || res?.records || []
}

onMounted(loadList)
</script>

<style scoped>
.feature-page { min-height: 100%; padding: 32px; background: #f7f8fb; color: #334155; }
.feature-hero { display: flex; justify-content: space-between; gap: 24px; align-items: center; margin-bottom: 24px; }
.eyebrow { margin: 0 0 8px; color: #64748b; font-size: 12px; font-weight: 700; letter-spacing: .08em; text-transform: uppercase; }
h1 { margin: 0; font-size: 28px; color: #1f2937; }
.summary { margin: 10px 0 0; color: #64748b; }
.search { max-width: 280px; }
.exercise-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(260px, 1fr)); gap: 16px; }
.exercise-card { border-radius: 8px; border: 1px solid #e5e7eb; }
.exercise-card h3 { margin: 0 0 8px; color: #1f2937; }
.exercise-card p { margin: 0 0 12px; color: #64748b; }
.tips { margin-top: 14px; color: #475569; font-size: 14px; line-height: 1.6; }
</style>
