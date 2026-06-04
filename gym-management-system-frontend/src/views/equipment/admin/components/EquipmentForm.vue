<template>
  <el-dialog
      v-model="visible"
      width="460px"
      class="clean-form-dialog"
      destroy-on-close
      :close-on-click-modal="false"
      :show-close="false"
      align-center
  >
    <template #header>
      <div class="relative h-10 flex items-center justify-center">
        <h3 class="text-[17px] font-bold text-slate-600 tracking-tight">{{ isEdit ? '编辑器材' : '新增器材' }}</h3>
      </div>
    </template>

    <el-form ref="formRef" :model="formData" :rules="rules" label-position="top" class="mt-2">

      <div class="flex justify-center mb-6">
        <el-upload
            class="avatar-uploader"
            :show-file-list="false"
            :http-request="customUpload"
            accept="image/*"
        >
          <div v-if="formData.imageUrl" class="relative group w-32 h-32 rounded-3xl overflow-hidden shadow-sm border border-slate-100">
            <img :src="formData.imageUrl" class="w-full h-full object-cover" />
            <div class="absolute inset-0 bg-black/30 flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity">
              <span class="text-white text-xs font-bold">更换图片</span>
            </div>
          </div>
          <div v-else class="w-32 h-32 rounded-3xl bg-slate-50 border-2 border-dashed border-slate-200 hover:border-blue-300 hover:bg-blue-50/50 flex flex-col items-center justify-center transition-all cursor-pointer">
            <div class="text-slate-300 mb-1">+</div>
            <span class="text-[10px] text-slate-400 font-bold">上传图片</span>
          </div>
        </el-upload>
      </div>

      <div class="bg-slate-50 rounded-[20px] p-1 mb-5 border border-slate-100/50">
        <el-form-item prop="name" class="mb-0 !border-b border-slate-200/50 last:border-0">
          <div class="w-full px-4 pt-2 pb-1">
            <div class="text-[10px] font-bold text-slate-400 uppercase tracking-wider mb-1">器材名称</div>
            <el-input v-model="formData.name" placeholder="请输入名称" class="ios-input-clean" />
          </div>
        </el-form-item>
      </div>

      <div class="grid grid-cols-2 gap-4 mb-5">
        <div class="bg-slate-50 rounded-[20px] p-4 border border-slate-100/50 transition-colors">
          <div class="text-[10px] font-bold text-slate-400 uppercase tracking-wider mb-2">器材编号</div>
          <el-form-item prop="code" class="mb-0">
            <el-input v-model="formData.code" placeholder="Unique ID" class="ios-input-transparent" />
          </el-form-item>
        </div>

        <div class="bg-slate-50 rounded-[20px] p-4 border border-slate-100/50 transition-colors">
          <div class="text-[10px] font-bold text-slate-400 uppercase tracking-wider mb-2">所属分类</div>
          <el-form-item prop="categoryId" class="mb-0">
            <el-select v-model="formData.categoryId" placeholder="选择分类" class="ios-select-transparent w-full">
              <el-option
                  v-for="i in categoryList"
                  :key="i.id"
                  :label="i.displayName || i.name"
                  :value="i.id"
              />
            </el-select>
          </el-form-item>
        </div>
      </div>

      <div class="bg-slate-50 rounded-[20px] p-1 border border-slate-100/50">
        <el-form-item prop="location" class="mb-0 border-b border-slate-200/50">
          <div class="w-full px-4 py-2">
            <div class="text-[10px] font-bold text-slate-400 uppercase tracking-wider mb-1">放置位置</div>
            <el-input v-model="formData.location" placeholder="例如：有氧区" class="ios-input-clean" />
          </div>
        </el-form-item>

        <el-form-item class="mb-0">
          <div class="w-full px-4 py-2">
            <div class="text-[10px] font-bold text-slate-400 uppercase tracking-wider mb-2">详细描述</div>
            <el-input
                v-model="formData.description"
                type="textarea"
                :rows="3"
                placeholder="添加规格、型号等备注信息..."
                class="ios-textarea-clean"
                resize="none"
            />
          </div>
        </el-form-item>
      </div>

    </el-form>

    <template #footer>
      <div class="grid grid-cols-2 gap-3 pt-2">
        <button
            @click="visible = false"
            class="py-3 rounded-xl bg-slate-100 text-slate-500 font-bold text-[14px] hover:bg-slate-200 transition-colors active:scale-[0.98]"
        >
          取消
        </button>
        <button
            @click="handleSubmit"
            :disabled="loading"
            class="py-3 rounded-xl bg-slate-800 text-white font-bold text-[14px] shadow-lg shadow-slate-300/50 active:scale-[0.98] transition-all hover:bg-slate-900 disabled:opacity-50"
        >
          {{ loading ? '保存中...' : '保存' }}
        </button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { addEquipment, updateEquipment } from '@/api/equipment'
import { uploadFile } from '@/api/common'
import { ElMessage } from 'element-plus'

const props = defineProps({ modelValue: Boolean, categoryList: Array })
const emit = defineEmits(['update:modelValue', 'success'])

const visible = computed({
  get: () => props.modelValue,
  set: (v) => emit('update:modelValue', v)
})

const loading = ref(false)
const isEdit = ref(false)
const formRef = ref()

// 定义初始数据结构
const initialForm = {
  id: undefined,
  name: '',
  code: '',
  categoryId: undefined,
  location: '',
  description: '',
  imageUrl: ''
}

const formData = reactive({ ...initialForm })

const rules = {
  name: [{ required: true, message: '请输入名称', trigger: 'blur' }],
  code: [{ required: true, message: '请输入编号', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }]
}

const setData = (row) => {
  isEdit.value = !!row
  if (formRef.value) formRef.value.clearValidate()

  Object.keys(formData).forEach(key => delete formData[key])

  if (row) {
    Object.assign(formData, row)
  } else {
    Object.assign(formData, { ...initialForm })
    delete formData.id
  }
}

// 图片上传处理
const customUpload = async (options) => {
  try {
    const res = await uploadFile(options.file)
    // 假设后端返回结构是 R<String>，data字段为url
    // 这里根据 CommonController 逻辑，res 应该是 { code: 200, msg: '...', data: 'http://...' }
    // 具体取决于 request.ts 的封装，这里假设直接返回了 data 或者需要 .data
    // 如果 request.ts 响应拦截器直接返回了 res.data，则如下：
    formData.imageUrl = res || ''
    // 如果 request.ts 返回完整 response：
    // formData.imageUrl = res.data
    ElMessage.success('上传成功')
  } catch (e) {
    ElMessage.error('图片上传失败')
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        isEdit.value ? await updateEquipment(formData) : await addEquipment(formData)
        ElMessage.success(isEdit.value ? '更新成功' : '创建成功')
        visible.value = false
        emit('success')
      } catch (e) {
        console.error(e)
      } finally {
        loading.value = false
      }
    }
  })
}

defineExpose({ setData })
</script>

<style scoped>
/* Clean Modal Base */
:deep(.clean-form-dialog) {
  background: white;
  border-radius: 32px;
  box-shadow: 0 25px 50px rgba(0,0,0,0.1);
  padding: 24px;
}
:deep(.el-dialog__header) { margin: 0; padding: 0 0 10px 0; }
:deep(.el-dialog__body) { padding: 0 !important; }
:deep(.el-dialog__footer) { padding: 0; }

/* Custom Clean Inputs */
:deep(.ios-input-clean .el-input__wrapper),
:deep(.ios-input-transparent .el-input__wrapper),
:deep(.ios-textarea-clean .el-textarea__inner) {
  box-shadow: none !important;
  background-color: transparent !important;
  padding: 0;
  font-size: 15px;
  color: #475569; /* slate-600 */
  font-weight: 500;
}

:deep(.ios-input-transparent .el-input__inner) { font-family: monospace; letter-spacing: -0.5px; }

:deep(.ios-select-transparent .el-input__wrapper) {
  box-shadow: none !important;
  background-color: transparent !important;
  padding: 0;
}
:deep(.ios-select-transparent .el-input__inner) {
  font-weight: 600;
  color: #3b82f6; /* blue-500 */
}

/* Upload styles */
:deep(.avatar-uploader .el-upload) {
  cursor: pointer;
  position: relative;
  overflow: hidden;
}
</style>