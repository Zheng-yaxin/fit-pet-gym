<template>
  <Teleport to="body">
    <div v-if="visible" class="fixed inset-0 z-[100] flex items-center justify-center p-6">
      <div class="absolute inset-0 bg-slate-200/40 backdrop-blur-sm transition-opacity duration-500" @click="handleClose"></div>

      <div class="relative w-full max-w-[500px] bg-white rounded-[40px] shadow-2xl border border-slate-100 overflow-hidden animate-spring-up">

        <div class="relative flex items-center justify-center px-8 py-8 border-b border-slate-50">
          <div class="text-center">
            <h3 class="text-xl font-bold text-slate-600 tracking-tight">{{ isEdit ? 'Edit Profile' : 'New Member' }}</h3>
            <p class="text-xs text-slate-400 mt-1 font-medium">Fill in the member details below</p>
          </div>
          <button @click="handleClose" class="absolute right-8 w-9 h-9 rounded-full bg-slate-50 hover:bg-slate-100 shadow-sm flex items-center justify-center text-slate-400 transition-all">
            <X :size="18" />
          </button>
        </div>

        <div class="p-8 space-y-6">
          <div v-if="!isEdit" class="space-y-4">
            <div class="text-[11px] font-bold text-slate-400 uppercase tracking-widest pl-1">Account Credentials</div>
            <div class="flex gap-4">
              <div class="flex-1 bg-slate-50 rounded-[20px] p-1 border border-slate-100 focus-within:ring-2 ring-blue-100 transition-all">
                <input v-model="form.username" class="w-full h-12 bg-transparent px-4 outline-none text-[15px] font-medium text-slate-600 placeholder:text-slate-300" placeholder="Username" />
              </div>
              <div class="flex-1 bg-slate-50 rounded-[20px] p-1 border border-slate-100 focus-within:ring-2 ring-blue-100 transition-all">
                <input v-model="form.password" type="password" class="w-full h-12 bg-transparent px-4 outline-none text-[15px] font-medium text-slate-600 placeholder:text-slate-300" placeholder="Password (Def: 123456)" />
              </div>
            </div>
          </div>

          <div class="space-y-4">
            <div class="text-[11px] font-bold text-slate-400 uppercase tracking-widest pl-1">Personal Information</div>
            <div class="space-y-3">
              <div class="bg-slate-50 rounded-[20px] p-1 border border-slate-100 focus-within:ring-2 ring-blue-100 transition-all flex items-center">
                <span class="pl-4 text-[13px] font-bold text-slate-400 w-20">Name</span>
                <input v-model="form.name" class="flex-1 h-12 bg-transparent px-2 outline-none text-[15px] font-semibold text-slate-600" />
              </div>
              <div class="bg-slate-50 rounded-[20px] p-1 border border-slate-100 focus-within:ring-2 ring-blue-100 transition-all flex items-center">
                <span class="pl-4 text-[13px] font-bold text-slate-400 w-20">Phone</span>
                <input v-model="form.phone" class="flex-1 h-12 bg-transparent px-2 outline-none text-[15px] font-semibold text-slate-600" />
              </div>
            </div>
          </div>

          <div class="grid grid-cols-2 gap-5">
            <div>
              <label class="block text-[11px] font-bold text-slate-400 uppercase tracking-widest mb-3 pl-1">Gender</label>
              <div class="flex bg-slate-100 p-1 rounded-full relative isolate">
                <div class="absolute inset-y-1 w-[calc(50%-4px)] bg-white rounded-full shadow-sm transition-all duration-300 ease-spring"
                     :class="form.gender === 1 ? 'left-1' : 'left-[calc(50%+2px)]'"></div>
                <button v-for="opt in [{v:1, l:'Male'}, {v:0, l:'Female'}]" :key="opt.v"
                        @click="form.gender = opt.v"
                        class="flex-1 py-2 text-[13px] font-bold rounded-full relative z-10 transition-colors"
                        :class="form.gender === opt.v ? 'text-slate-600' : 'text-slate-400'"
                >{{ opt.l }}</button>
              </div>
            </div>

            <div>
              <label class="block text-[11px] font-bold text-slate-400 uppercase tracking-widest mb-3 pl-1">Status</label>
              <div class="flex bg-slate-100 p-1 rounded-full relative isolate">
                <div class="absolute inset-y-1 w-[calc(50%-4px)] bg-white rounded-full shadow-sm transition-all duration-300 ease-spring"
                     :class="form.status === '0' ? 'left-1' : 'left-[calc(50%+2px)]'"></div>
                <button @click="form.status = '0'" class="flex-1 py-2 text-[13px] font-bold rounded-full relative z-10 transition-colors" :class="form.status === '0' ? 'text-emerald-500' : 'text-slate-400'">Active</button>
                <button @click="form.status = '1'" class="flex-1 py-2 text-[13px] font-bold rounded-full relative z-10 transition-colors" :class="form.status === '1' ? 'text-rose-500' : 'text-slate-400'">Disabled</button>
              </div>
            </div>
          </div>
        </div>

        <div class="p-8 pt-2 flex gap-4">
          <button @click="handleClose" class="flex-1 h-12 rounded-full bg-slate-100 hover:bg-slate-200 text-slate-500 font-bold text-[15px] transition-colors">Cancel</button>
          <button @click="handleSubmit" :disabled="loading" class="flex-1 h-12 rounded-full bg-slate-800 hover:bg-slate-700 text-white font-bold text-[15px] shadow-lg shadow-slate-300 hover:scale-[1.02] transition-all flex items-center justify-center">
            <span v-if="loading" class="loading-spin"></span>
            <span v-else>Save Changes</span>
          </button>
        </div>
      </div>
    </div>
  </Teleport>
</template>

<script setup lang="ts">
import { ref, reactive, watch } from 'vue'
import { X } from 'lucide-vue-next'
import { ElMessage } from 'element-plus'
import { addMember, updateMember, type Member } from '@/api/member'

const props = defineProps<{ modelValue: boolean; data?: Member | null }>()
const emit = defineEmits(['update:modelValue', 'success'])
const visible = ref(false)
const isEdit = ref(false)
const loading = ref(false)
const form = reactive<Member>({ username: '', name: '', phone: '', gender: 1, status: '0', password: '' })

watch(() => props.modelValue, (val) => {
  visible.value = val
  if (val) {
    if (props.data) {
      isEdit.value = true
      Object.assign(form, props.data)
      form.password = ''
    } else {
      isEdit.value = false
      Object.assign(form, { id: undefined, username: '', name: '', phone: '', gender: 1, status: '0', password: '' })
    }
  }
})

const handleClose = () => { visible.value = false; emit('update:modelValue', false) }
const handleSubmit = async () => {
  if (!isEdit.value && !form.username) return ElMessage.warning('请输入用户名')
  loading.value = true
  try {
    await (isEdit.value ? updateMember(form) : addMember(form))
    ElMessage.success('操作成功')
    emit('success')
    handleClose()
  } catch (e) { ElMessage.error('操作失败') }
  finally { loading.value = false }
}
</script>

<style scoped>
.animate-spring-up { animation: springUp 0.5s cubic-bezier(0.19, 1, 0.22, 1); }
@keyframes springUp { from { transform: scale(0.95) translateY(20px); opacity: 0; } to { transform: scale(1) translateY(0); opacity: 1; } }
.loading-spin { width: 16px; height: 16px; border: 2px solid rgba(255,255,255,0.3); border-top-color: white; border-radius: 50%; animation: spin 1s infinite linear; }
@keyframes spin { to { transform: rotate(360deg); } }
.ease-spring { transition-timing-function: cubic-bezier(0.175, 0.885, 0.32, 1.275); }
</style>