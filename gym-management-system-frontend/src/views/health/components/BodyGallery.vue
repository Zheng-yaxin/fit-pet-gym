<template>
  <Teleport to="body">
    <Transition name="liquid-sheet">
      <div v-if="visible" class="fixed inset-0 z-[100] flex flex-col liquid-bg">

        <div class="sticky top-0 z-20 glass-header pt-safe-top">
          <div class="flex items-center justify-between px-6 h-16">
            <button
                @click="handleClose"
                class="nav-btn-glass text-slate-600 hover:bg-slate-100"
            >
              <ChevronLeft class="w-6 h-6 stroke-[2.5]" />
              <span class="text-[16px] font-semibold leading-none mb-[1px]">返回</span>
            </button>

            <span class="text-[17px] font-bold text-slate-800 tracking-tight">体态相册</span>

            <button
                @click="triggerUpload"
                class="nav-btn-glass text-slate-800 font-bold text-[16px] hover:bg-slate-100"
            >
              添加
            </button>
          </div>
          <div class="absolute bottom-0 left-0 right-0 h-[1px] bg-slate-200/50"></div>
        </div>

        <div class="flex-1 overflow-y-auto pb-safe-bottom custom-scrollbar">
          <div class="px-4 py-6">
            <div v-if="images.length === 0" class="h-[60vh] flex flex-col items-center justify-center">
              <div class="w-20 h-20 rounded-full bg-slate-100 flex items-center justify-center mb-6">
                <Camera class="w-8 h-8 text-slate-300" />
              </div>
              <p class="text-[16px] text-slate-500 font-medium">暂无照片</p>
              <p class="text-[14px] text-slate-400 mt-2">记录身材变化的过程</p>
            </div>

            <div class="grid grid-cols-3 gap-3">
              <div
                  v-for="img in sortedImages"
                  :key="img.id"
                  @click="openPreview(img)"
                  class="relative aspect-[3/4] rounded-2xl overflow-hidden cursor-pointer group shadow-sm bg-slate-50"
              >
                <img
                    :src="img.imageUrl"
                    class="w-full h-full object-cover transition-all duration-700 ease-out group-hover:scale-105"
                    loading="lazy"
                />
                <div class="absolute inset-0 bg-black/0 group-hover:bg-black/5 transition-colors duration-300"></div>
              </div>
            </div>

            <div class="py-10 text-center" v-if="images.length > 0">
              <span class="px-4 py-1.5 rounded-full bg-white border border-slate-100 text-[12px] font-medium text-slate-400 shadow-sm">
                共 {{ images.length }} 张照片
              </span>
            </div>
          </div>
        </div>

        <Transition name="fade-blur">
          <div v-if="previewImg" class="fixed inset-0 z-[110] flex flex-col glass-dark-overlay">

            <div class="absolute top-0 left-0 right-0 z-20 flex justify-between items-center px-6 pt-safe-top h-24 bg-gradient-to-b from-black/60 to-transparent">
              <button @click="closePreview" class="action-circle-btn">
                <X class="w-5 h-5 text-white/90" />
              </button>

              <span class="text-white/80 text-[14px] font-medium tracking-wide font-mono bg-white/10 px-3 py-1 rounded-lg backdrop-blur-md">
                {{ formatDate(previewImg.recordTime) }}
              </span>

              <button @click="handleDelete(previewImg)" class="action-circle-btn destructive">
                <Trash2 class="w-5 h-5 text-white" />
              </button>
            </div>

            <div class="flex-1 flex items-center justify-center overflow-hidden relative p-4" @click="closePreview">
              <img
                  :src="previewImg.imageUrl"
                  class="max-w-full max-h-full object-contain rounded-lg shadow-2xl"
              />
            </div>
          </div>
        </Transition>

        <input type="file" ref="fileInput" class="hidden" accept="image/*" @change="onFileChange" />

      </div>
    </Transition>
  </Teleport>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { ChevronLeft, Camera, X, Trash2 } from 'lucide-vue-next'
import type { BodyImage } from '@/api/health'
import dayjs from 'dayjs'

const props = defineProps<{
  modelValue: boolean
  images: BodyImage[]
}>()

const emit = defineEmits(['update:modelValue', 'upload', 'delete'])

const visible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

const fileInput = ref<HTMLInputElement | null>(null)
const previewImg = ref<BodyImage | null>(null)

const sortedImages = computed(() => {
  return [...props.images].sort((a, b) =>
      new Date(b.recordTime).getTime() - new Date(a.recordTime).getTime()
  )
})

const handleClose = () => {
  emit('update:modelValue', false)
}

const triggerUpload = () => {
  fileInput.value?.click()
}

const onFileChange = (e: Event) => {
  const files = (e.target as HTMLInputElement).files
  if (files && files.length > 0) {
    emit('upload', files[0])
    if(fileInput.value) fileInput.value.value = ''
  }
}

const openPreview = (img: BodyImage) => {
  previewImg.value = img
}

const closePreview = () => {
  previewImg.value = null
}

const handleDelete = (img: BodyImage) => {
  emit('delete', img.id)
  closePreview()
}

const formatDate = (date: string) => {
  return dayjs(date).format('YYYY年M月D日 HH:mm')
}
</script>

<style scoped>
.liquid-bg {
  background: rgba(248, 250, 252, 0.95); /* Slate-50 */
  backdrop-filter: blur(20px);
}

.glass-header {
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(20px);
}

.nav-btn-glass {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 8px 12px;
  border-radius: 99px;
  transition: background 0.2s ease;
}
.nav-btn-glass:active {
  background: #F1F5F9;
  opacity: 0.8;
}

.glass-dark-overlay {
  background: rgba(0, 0, 0, 0.85);
  backdrop-filter: blur(30px);
}

.action-circle-btn {
  width: 40px; height: 40px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(12px);
  display: flex; align-items: center; justify-content: center;
  transition: transform 0.2s;
  border: 1px solid rgba(255,255,255,0.1);
}
.action-circle-btn:active { transform: scale(0.92); background: rgba(255, 255, 255, 0.25); }

.action-circle-btn.destructive { background: rgba(239, 68, 68, 0.6); }

.liquid-sheet-enter-active, .liquid-sheet-leave-active { transition: transform 0.5s cubic-bezier(0.32, 0.72, 0, 1); }
.liquid-sheet-enter-from, .liquid-sheet-leave-to { transform: translateY(100%); }

.fade-blur-enter-active, .fade-blur-leave-active { transition: opacity 0.3s ease; }
.fade-blur-enter-from, .fade-blur-leave-to { opacity: 0; }

.pt-safe-top { padding-top: max(env(safe-area-inset-top), 20px); }
.pb-safe-bottom { padding-bottom: max(env(safe-area-inset-bottom), 20px); }
.custom-scrollbar::-webkit-scrollbar { display: none; }
</style>