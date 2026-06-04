<template>
  <div class="admin-page">
    <header>
      <div>
        <p>Exercise Admin</p>
        <h1>动作库管理</h1>
      </div>
      <el-button type="primary" @click="createDemo">新增示例动作</el-button>
    </header>

    <el-table :data="list" class="table" border>
      <el-table-column prop="name" label="动作名称" />
      <el-table-column prop="targetMuscle" label="目标肌群" />
      <el-table-column prop="equipment" label="器材" />
      <el-table-column prop="difficulty" label="难度" />
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { addExercise, getExerciseList } from '@/api/exercise'

const list = ref<any[]>([])

const loadList = async () => {
  const res: any = await getExerciseList({ pageNum: 1, pageSize: 50 })
  list.value = res?.rows || res?.records || []
}

const createDemo = async () => {
  await addExercise({ name: '平板支撑', targetMuscle: '核心', equipment: '自重', difficulty: '初级', tips: '保持骨盆稳定。' })
  ElMessage.success('示例动作已新增')
  await loadList()
}

onMounted(loadList)
</script>

<style scoped>
.admin-page { padding: 32px; min-height: 100%; background: var(--ff-surface-raised); }
header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
header p { margin: 0 0 6px; color: #64748b; font-size: 12px; font-weight: 700; text-transform: uppercase; }
h1 { margin: 0; color: #1f2937; font-size: 24px; }
.table { border-radius: 8px; overflow: hidden; }
</style>
