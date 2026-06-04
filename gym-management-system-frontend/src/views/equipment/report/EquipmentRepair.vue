<template>
  <div class="min-h-screen bg-slate-50 p-6 flex flex-col items-center justify-center relative overflow-hidden font-sans">

    <div class="fixed top-1/4 left-1/4 w-96 h-96 bg-blue-100/40 rounded-full blur-[100px] pointer-events-none"></div>
    <div class="fixed bottom-1/4 right-1/4 w-96 h-96 bg-emerald-50/50 rounded-full blur-[100px] pointer-events-none"></div>

    <div class="max-w-xl w-full relative z-10 animate-fade-in-up">
      <div class="text-center mb-10 relative">
        <h2 class="text-2xl font-bold text-slate-600 tracking-tight">器材报修申请</h2>
        <p class="text-slate-400 mt-1 font-medium text-xs uppercase tracking-widest">Report Equipment Issues</p>
      </div>

      <div class="bg-white rounded-[36px] p-8 shadow-xl shadow-slate-200/50 border border-slate-100">
        <el-form ref="formRef" :model="form" label-position="top" class="space-y-8">

          <div>
            <label class="block text-[11px] font-bold text-slate-400 uppercase tracking-wider mb-3 ml-2">选择器材</label>
            <el-select
                v-model="form.equipmentId"
                filterable
                remote
                :remote-method="searchEquipment"
                placeholder="搜索器材名称 (如: 跑步机)"
                class="w-full ios-select-large"
                no-data-text="请输入关键词搜索"
            >
              <el-option
                  v-for="item in equipmentOptions"
                  :key="item.id"
                  :label="item.name + ' (' + item.code + ')'"
                  :value="item.id"
              >
                <div class="flex justify-between items-center w-full">
                  <span class="font-bold text-slate-600">{{ item.name }}</span>
                  <span class="text-xs font-mono text-slate-400 bg-slate-50 px-1.5 py-0.5 rounded border border-slate-100">{{ item.code }}</span>
                </div>
              </el-option>
            </el-select>
          </div>

          <div>
            <label class="block text-[11px] font-bold text-slate-400 uppercase tracking-wider mb-3 ml-2">故障描述</label>
            <div class="relative group">
              <el-input
                  v-model="form.faultDesc"
                  type="textarea"
                  :rows="6"
                  placeholder="请详细描述故障现象、发生时间等信息..."
                  class="ios-textarea-large"
                  resize="none"
              />
            </div>
          </div>

          <div class="pt-2">
            <button
                type="button"
                @click="submit"
                class="w-full h-14 bg-slate-800 text-white rounded-[20px] font-bold text-[16px] shadow-lg shadow-slate-300/50 active:scale-[0.98] transition-all hover:bg-slate-900 flex items-center justify-center gap-2 group"
            >
              <span>提交申请</span>
              <span class="opacity-70 group-hover:translate-x-1 transition-transform">→</span>
            </button>
          </div>
        </el-form>
      </div>

      <p class="text-center text-slate-400 text-[11px] mt-8 font-medium">我们会尽快安排专业人员进行检修</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { getEquipmentPage, addRepair } from '@/api/equipment'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/modules/user'

const userStore = useUserStore()
const form = reactive({ equipmentId: undefined, faultDesc: '' })
const equipmentOptions = ref<any[]>([])

const searchEquipment = async (query: string) => {
  if (query) {
    try {
      const res: any = await getEquipmentPage({ pageNum: 1, pageSize: 20, keyword: query })
      // 兼容 records(MyBatis-Plus) 和 rows(PageHelper)
      if (Array.isArray(res)) {
        equipmentOptions.value = res
      } else {
        equipmentOptions.value = res.records || res.rows || []
      }
    } catch (e) {
      console.error(e)
    }
  }
}

const submit = async () => {
  if (!form.equipmentId || !form.faultDesc) return ElMessage.warning('请填写完整信息')
  if (!userStore.userId) return ElMessage.error('无法获取用户信息，请重新登录')

  try {
    // 注入 reporterId
    const data = {
      ...form,
      reporterId: userStore.userId
    }
    await addRepair(data as any)
    ElMessage.success('提交成功')
    form.equipmentId = undefined
    form.faultDesc = ''
  } catch (e) {
    console.error(e)
  }
}
</script>

<style scoped>
.animate-fade-in-up {
  animation: fadeInUp 0.8s cubic-bezier(0.16, 1, 0.3, 1);
}
@keyframes fadeInUp {
  from { opacity: 0; transform: translateY(40px); }
  to { opacity: 1; transform: translateY(0); }
}

/* Custom Large Inputs - ForgeFit OS Style */
:deep(.ios-select-large .el-input__wrapper) {
  background-color: var(--ff-surface-raised) !important; /* slate-50 */
  border-radius: 20px;
  box-shadow: none !important;
  padding: 8px 20px;
  height: 56px;
  transition: all 0.3s ease;
}
:deep(.ios-select-large .el-input.is-focus .el-input__wrapper) {
  background-color: white !important;
  box-shadow: 0 0 0 2px var(--ff-border-strong) !important; /* slate-300 */
}
:deep(.ios-select-large .el-input__inner) {
  font-size: 16px;
  font-weight: 500;
  color: #475569; /* slate-600 */
}
:deep(.ios-select-large .el-input__inner::placeholder) {
  color: #94a3b8; /* slate-400 */
}

:deep(.ios-textarea-large .el-textarea__inner) {
  background-color: var(--ff-surface-raised) !important; /* slate-50 */
  border-radius: 20px;
  box-shadow: none !important;
  padding: 20px;
  font-size: 15px;
  color: #475569; /* slate-600 */
  font-weight: 500;
  transition: all 0.3s ease;
}
:deep(.ios-textarea-large .el-textarea__inner:focus) {
  background-color: white !important;
  box-shadow: 0 0 0 2px var(--ff-border-strong) !important; /* slate-300 */
}
:deep(.ios-textarea-large .el-textarea__inner::placeholder) {
  color: #94a3b8; /* slate-400 */
}
</style>