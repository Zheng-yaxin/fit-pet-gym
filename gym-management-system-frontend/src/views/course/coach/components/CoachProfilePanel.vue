<template>
  <div class="bg-white rounded-[32px] p-10 shadow-sm border border-slate-100 max-w-4xl mx-auto">

    <div class="flex flex-col md:flex-row items-center md:items-start gap-10 mb-12">
      <div class="relative group">
        <div class="w-32 h-32 rounded-full bg-slate-50 flex items-center justify-center overflow-hidden shadow-sm border border-slate-100">
          <img v-if="form.avatar" :src="form.avatar" class="w-full h-full object-cover" />
          <div v-else class="text-4xl font-bold text-slate-300">{{ form.name?.[0] || 'C' }}</div>
        </div>
        <div class="absolute bottom-1 right-1 bg-slate-800 text-white p-2.5 rounded-full cursor-pointer hover:bg-black transition-all shadow-lg hover:scale-105" title="更换头像">
          <el-icon :size="16"><Camera /></el-icon>
        </div>
      </div>

      <div class="text-center md:text-left flex-1">
        <div class="flex items-center justify-center md:justify-start gap-3 mb-2">
          <h2 class="text-3xl font-bold text-slate-700 tracking-tight">
            {{ form.name || '设置昵称' }}
          </h2>
          <span v-if="form.gender === 1" class="px-2.5 py-1 rounded-full bg-blue-50 text-blue-500 text-xs font-bold uppercase tracking-wider">男</span>
          <span v-else-if="form.gender === 0" class="px-2.5 py-1 rounded-full bg-rose-50 text-rose-500 text-xs font-bold uppercase tracking-wider">女</span>
        </div>
        <p class="text-slate-500 text-lg font-medium">{{ form.phone || '暂无手机号' }}</p>
        <p class="text-slate-400 text-sm mt-4 max-w-md mx-auto md:mx-0 leading-relaxed">
          管理您的公开资料信息。所有预约您课程的会员都将看到此信息。
        </p>
      </div>
    </div>

    <el-form :model="form" :rules="rules" ref="formRef" label-position="top" class="grid grid-cols-1 md:grid-cols-2 gap-x-8 gap-y-8">

      <el-form-item prop="name" class="clean-input-group">
        <template #label>
          <span class="text-xs font-bold text-slate-400 uppercase tracking-wider ml-1">真实姓名</span>
        </template>
        <el-input v-model="form.name" placeholder="请输入姓名" class="clean-input" />
      </el-form-item>

      <el-form-item prop="gender" class="clean-input-group">
        <template #label>
          <span class="text-xs font-bold text-slate-400 uppercase tracking-wider ml-1">性别</span>
        </template>
        <el-select v-model="form.gender" class="w-full clean-select" placeholder="选择性别">
          <el-option label="男" :value="1" />
          <el-option label="女" :value="0" />
        </el-select>
      </el-form-item>

      <el-form-item prop="hourlyRate" class="clean-input-group">
        <template #label>
          <span class="text-xs font-bold text-slate-400 uppercase tracking-wider ml-1">课时费 (元)</span>
        </template>
        <el-input-number v-model="form.hourlyRate" :min="0" :step="50" controls-position="right" class="!w-full clean-number" />
      </el-form-item>

      <el-form-item prop="experienceYears" class="clean-input-group">
        <template #label>
          <span class="text-xs font-bold text-slate-400 uppercase tracking-wider ml-1">从业经验 (年)</span>
        </template>
        <el-input-number v-model="form.experienceYears" :min="0" :max="50" controls-position="right" class="!w-full clean-number" />
      </el-form-item>

      <el-form-item prop="certification" class="md:col-span-2 clean-input-group">
        <template #label>
          <span class="text-xs font-bold text-slate-400 uppercase tracking-wider ml-1">认证证书</span>
        </template>
        <el-input v-model="form.certification" placeholder="例如：ACE认证、国家一级教练" class="clean-input" />
      </el-form-item>

      <el-form-item prop="specialties" class="md:col-span-2 clean-input-group">
        <template #label>
          <span class="text-xs font-bold text-slate-400 uppercase tracking-wider ml-1">擅长项目 (逗号分隔)</span>
        </template>
        <el-input v-model="form.specialties" placeholder="例如：HIIT、普拉提、瑜伽" class="clean-input" />
      </el-form-item>

      <el-form-item prop="bio" class="md:col-span-2 clean-input-group">
        <template #label>
          <span class="text-xs font-bold text-slate-400 uppercase tracking-wider ml-1">个人简介</span>
        </template>
        <el-input
            v-model="form.bio"
            type="textarea"
            :rows="5"
            placeholder="向会员介绍您的教学风格..."
            maxlength="500"
            show-word-limit
            class="clean-textarea"
        />
      </el-form-item>

      <div class="md:col-span-2 flex justify-end pt-8">
        <el-button
            type="primary"
            :loading="loading"
            @click="save"
            class="!h-12 !px-8 !rounded-full !bg-slate-800 !border-none !text-base !font-medium hover:!bg-black hover:!scale-105 transition-all shadow-lg shadow-slate-200"
        >
          保存更改
        </el-button>
      </div>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { getCoachInfo, updateCoachInfo } from '@/api/coach'
import { ElMessage } from 'element-plus'
import { Camera } from '@element-plus/icons-vue'

const loading = ref(false)
const formRef = ref()
// 对应数据库 gym_coach 表字段
const form = reactive({
  id: undefined,
  name: '',
  phone: '',
  gender: 1, // 1男 0女
  avatar: '',
  bio: '', // 对应 bio 字段
  specialties: '', // 对应 specialties 字段
  experienceYears: 0, // 对应 experience_years
  certification: '', // 对应 certification
  hourlyRate: 0 // 对应 hourly_rate
})

const rules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  hourlyRate: [{ required: true, message: '请设置课时费', trigger: 'blur' }]
}

const loadInfo = async () => {
  try {
    const res: any = await getCoachInfo()
    if (res) {
      Object.assign(form, res)
    }
  } catch (e) {
    console.error(e)
  }
}

const save = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid: boolean) => {
    if (valid) {
      loading.value = true
      try {
        await updateCoachInfo(form)
        ElMessage.success('个人资料已更新')
        // 触发父组件刷新用户信息（可选）
      } catch (e) {
        ElMessage.error('保存失败')
      } finally {
        loading.value = false
      }
    }
  })
}

onMounted(() => loadInfo())
</script>

<style lang="scss" scoped>
:deep(.el-input__wrapper) {
  background-color: var(--ff-surface-raised); /* slate-50 */
  box-shadow: none !important;
  border-radius: 12px;
  padding: 8px 16px;
  transition: all 0.2s ease;
  border: 1px solid transparent;

  &:hover {
    background-color: #f1f5f9; /* slate-100 */
  }

  &.is-focus {
    background-color: var(--ff-surface);
    box-shadow: 0 0 0 2px #e2e8f0 !important; /* slate-200 */
  }
}

:deep(.el-input__inner) {
  font-size: 15px;
  font-weight: 500;
  color: #334155; /* slate-700 */
  height: 44px;
}

:deep(.el-textarea__inner) {
  background-color: var(--ff-surface-raised);
  border: none;
  box-shadow: none;
  border-radius: 16px;
  padding: 16px;
  font-size: 15px;
  color: #334155;
  transition: all 0.2s;

  &:focus {
    background-color: var(--ff-surface);
    box-shadow: 0 0 0 2px #e2e8f0;
  }
}

:deep(.el-input-number__decrease), :deep(.el-input-number__increase) {
  background: transparent;
  border: none;
  &:hover { color: #475569; }
}

:deep(.el-select) {
  .el-input__wrapper {
    box-shadow: none !important;
    background-color: var(--ff-surface-raised);
  }
}
</style>