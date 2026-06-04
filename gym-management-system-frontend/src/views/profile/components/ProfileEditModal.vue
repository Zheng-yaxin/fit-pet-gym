<template>
  <Teleport to="body">
    <div v-if="visible" class="fixed inset-0 z-50 flex items-end sm:items-center justify-center sm:p-6">
      <div
          class="absolute inset-0 bg-slate-200/40 backdrop-blur-sm transition-opacity duration-500"
          @click="handleClose"
      ></div>

      <div class="relative w-full max-w-md bg-white sm:rounded-[32px] rounded-t-[32px] shadow-2xl overflow-hidden animate-spring-up">

        <div class="px-6 py-4 flex items-center justify-center relative bg-white/80 backdrop-blur-md z-10">
          <button
              @click="handleClose"
              class="absolute left-6 text-[16px] text-slate-400 hover:text-slate-600 transition-colors font-medium"
          >
            取消
          </button>

          <span class="text-[17px] font-bold text-slate-600 tracking-tight">编辑资料</span>

          <button
              @click="handleSubmit"
              :disabled="loading"
              class="absolute right-6 text-[16px] font-bold text-blue-500 hover:text-blue-600 transition-colors disabled:opacity-30"
          >
            <span v-if="loading" class="animate-pulse">保存中</span>
            <span v-else>完成</span>
          </button>
        </div>

        <div class="p-8 space-y-10">
          <div class="flex justify-center">
            <div class="w-24 h-24 rounded-full bg-gradient-to-b from-slate-50 to-slate-100 flex items-center justify-center text-4xl font-bold text-slate-300 shadow-inner">
              {{ form.name?.[0]?.toUpperCase() || 'U' }}
            </div>
          </div>

          <div class="space-y-6">
            <div class="bg-slate-50 rounded-[24px] overflow-hidden">
              <div class="relative flex items-center px-6 py-5 group hover:bg-slate-100/50 transition-colors">
                <label class="w-20 text-[15px] font-bold text-slate-500">姓名</label>
                <div class="h-4 w-px bg-slate-200 mx-2"></div>
                <input
                    v-model="form.name"
                    class="flex-1 bg-transparent text-[16px] text-slate-600 placeholder-slate-300 outline-none text-right font-medium"
                    placeholder="请输入姓名"
                />
              </div>

              <div class="h-px bg-slate-200/50 mx-6"></div>

              <div class="relative flex items-center px-6 py-5 group hover:bg-slate-100/50 transition-colors">
                <label class="w-20 text-[15px] font-bold text-slate-500">手机号</label>
                <div class="h-4 w-px bg-slate-200 mx-2"></div>
                <input
                    v-model="form.phone"
                    class="flex-1 bg-transparent text-[16px] text-slate-600 placeholder-slate-300 outline-none text-right font-medium font-mono tracking-wide"
                    placeholder="联系方式"
                />
              </div>
            </div>

            <div class="space-y-3">
              <div class="text-[12px] font-bold text-slate-400 uppercase tracking-widest ml-4">性别</div>
              <div class="bg-slate-50 rounded-2xl p-1.5 flex relative">
                <div
                    class="absolute top-1.5 bottom-1.5 w-[calc(50%-6px)] bg-white rounded-xl shadow-sm transition-all duration-300 ease-[cubic-bezier(0.19,1,0.22,1)]"
                    :style="{ left: form.gender === 1 ? '6px' : 'calc(50% + 0px)' }"
                ></div>
                <button
                    v-for="opt in [{v:1, l:'男'}, {v:0, l:'女'}]" :key="opt.v"
                    @click="form.gender = opt.v"
                    class="relative z-10 flex-1 py-3 text-[15px] font-medium transition-colors duration-300"
                    :class="form.gender === opt.v ? 'text-slate-600 font-bold' : 'text-slate-400 hover:text-slate-500'"
                >
                  {{ opt.l }}
                </button>
              </div>
            </div>
          </div>
        </div>

        <div class="h-6 bg-transparent"></div>
      </div>
    </div>
  </Teleport>
</template>

<script setup lang="ts">
import { ref, reactive, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { updateMember, type Member } from '@/api/member'

const props = defineProps<{ modelValue: boolean; data?: Member | null }>()
const emit = defineEmits(['update:modelValue', 'success'])
const visible = ref(false)
const loading = ref(false)
const form = reactive<Member>({ name: '', phone: '', gender: 1 } as any)

watch(() => props.modelValue, (val) => {
  visible.value = val
  if (val && props.data) Object.assign(form, props.data)
})

const handleClose = () => { visible.value = false; emit('update:modelValue', false) }
const handleSubmit = async () => {
  loading.value = true
  try {
    await updateMember(form)
    ElMessage.success('已保存')
    emit('success')
    handleClose()
  } catch (e) { ElMessage.error('保存失败') }
  finally { loading.value = false }
}
</script>

<style scoped>
.animate-spring-up { animation: springUp 0.6s cubic-bezier(0.19, 1, 0.22, 1); }
@keyframes springUp {
  from { transform: translateY(100%); opacity: 0; }
  to { transform: translateY(0); opacity: 1; }
}
</style>