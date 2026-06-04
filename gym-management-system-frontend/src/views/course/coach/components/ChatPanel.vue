<template>
  <div class="flex h-[calc(100vh-320px)] min-h-[600px] bg-white rounded-[24px] overflow-hidden shadow-sm border border-slate-100">
    <div class="w-[320px] flex flex-col border-r border-slate-100 bg-slate-50/50">
      <div class="h-20 px-6 flex items-center justify-center relative border-b border-slate-100">
        <span class="text-lg font-bold text-slate-600 tracking-tight">消息列表</span>
      </div>

      <div class="flex-1 overflow-y-auto px-3 py-3 space-y-1 custom-scrollbar">
        <div
          v-for="contact in contacts"
          :key="contact.id"
          @click="handleSelectContact(contact)"
          class="group relative flex items-center gap-3 p-3 rounded-xl cursor-pointer transition-all duration-200"
          :class="currentContact?.id === contact.id
            ? 'bg-white shadow-sm ring-1 ring-slate-100'
            : 'hover:bg-slate-100'"
        >
          <div class="relative shrink-0">
            <el-avatar :size="44" :src="contact.avatar || defaultAvatar" class="bg-slate-200 ring-2 ring-white" />
            <div
              class="absolute bottom-0 right-0 w-2.5 h-2.5 rounded-full border-2 border-white"
              :class="contact.online ? 'bg-emerald-400' : 'bg-slate-300'"
            ></div>
          </div>

          <div class="flex flex-col flex-1 min-w-0">
            <div class="flex justify-between items-baseline mb-1">
              <span class="text-[14px] font-semibold text-slate-700 truncate">{{ contact.name }}</span>
              <span class="text-[10px] text-slate-400 font-medium">{{ contact.lastMessageTime ? formatTimeShort(contact.lastMessageTime) : '' }}</span>
            </div>
            <div class="flex items-center justify-between gap-2">
              <span class="text-[12px] text-slate-400 truncate font-medium">{{ contact.role }}</span>
              <span v-if="contact.unreadCount" class="text-[10px] font-bold text-rose-500 bg-rose-50 px-2 py-0.5 rounded-full">
                {{ contact.unreadCount }}
              </span>
            </div>
          </div>
        </div>

        <div v-if="contacts.length === 0" class="flex flex-col items-center justify-center py-20 text-slate-300">
          <div class="w-12 h-12 bg-slate-100 rounded-full flex items-center justify-center mb-3">
            <el-icon class="text-xl"><ChatDotRound /></el-icon>
          </div>
          <span class="text-xs font-medium">暂无消息</span>
        </div>
      </div>
    </div>

    <div class="flex-1 flex flex-col bg-white relative">
      <template v-if="currentContact">
        <div class="h-20 px-8 flex items-center justify-center relative border-b border-slate-50">
          <div class="flex flex-col items-center">
            <div class="flex items-center gap-2">
              <div class="w-2 h-2 rounded-full" :class="currentContact.online ? 'bg-emerald-400' : 'bg-slate-300'"></div>
              <span class="text-lg font-bold text-slate-700 tracking-tight">{{ currentContact.name }}</span>
            </div>
            <span class="text-[11px] text-slate-400 font-medium uppercase tracking-wider mt-0.5">
              {{ currentContact.online ? '在线会员' : '离线会员' }}
            </span>
          </div>
        </div>

        <div class="flex-1 overflow-y-auto p-8 space-y-6 custom-scrollbar bg-slate-50/30" ref="messageListRef">
          <div v-if="messages.length === 0" class="flex flex-col items-center justify-center h-full text-slate-400">
            <p class="text-sm font-medium bg-slate-100 px-4 py-2 rounded-full text-slate-500">暂无历史记录</p>
          </div>

          <div v-for="msg in messages" :key="msg.requestId || msg.id || msg.createTime" class="flex gap-4 w-full group" :class="{ 'flex-row-reverse': msg.isSelf }">
            <el-avatar
              v-if="!msg.isSelf"
              :src="msg.senderAvatar || currentContact.avatar || defaultAvatar"
              :size="32"
              class="shrink-0 self-end mb-1 ring-2 ring-white"
            />
            <div v-else class="w-8"></div>

            <div class="flex flex-col max-w-[65%]" :class="{ 'items-end': msg.isSelf, 'items-start': !msg.isSelf }">
              <div
                class="px-5 py-3 text-[14px] leading-relaxed shadow-sm break-words whitespace-pre-wrap transition-all duration-200"
                :class="msg.isSelf
                  ? 'bg-slate-700 text-white rounded-[20px] rounded-tr-sm'
                  : 'bg-white text-slate-600 rounded-[20px] rounded-tl-sm border border-slate-100'"
              >
                {{ msg.content }}
              </div>
              <div class="text-[10px] text-slate-300 mt-1.5 px-2 opacity-0 group-hover:opacity-100 transition-opacity">
                {{ formatTime(msg.createTime) }}
                <span v-if="msg.isSelf" class="ml-2">{{ formatStatus(msg.status) }}</span>
              </div>
            </div>
          </div>
        </div>

        <div class="p-6 bg-white border-t border-slate-50">
          <div class="flex items-end gap-3 bg-slate-50 rounded-[24px] p-2 pr-2 ring-1 ring-slate-100">
            <textarea
              v-model="inputContent"
              @keydown.enter.exact.prevent="handleSend"
              @input="handleTyping"
              placeholder="输入消息..."
              rows="1"
              class="flex-1 px-4 py-3 bg-transparent border-none outline-none text-slate-700 placeholder-slate-400 resize-none max-h-[120px] min-h-[48px] text-[14px]"
            ></textarea>

            <button
              @click="handleSend"
              :disabled="!inputContent.trim() || sending"
              class="h-10 w-10 flex items-center justify-center rounded-full bg-slate-800 hover:bg-black text-white disabled:opacity-50 disabled:cursor-not-allowed transition-all duration-300 mb-1"
            >
              <el-icon v-if="!sending" :size="16" class="ml-0.5"><Top /></el-icon>
              <el-icon v-else class="is-loading"><Loading /></el-icon>
            </button>
          </div>

          <div class="mt-2 flex items-center justify-between text-[11px] text-slate-400">
            <span>{{ typingHint }}</span>
            <span v-if="connected" class="text-emerald-500">实时连接中</span>
            <span v-else-if="reconnecting" class="text-amber-500">正在重连</span>
            <span v-else class="text-rose-400">离线</span>
          </div>
        </div>
      </template>

      <div v-else class="h-full flex flex-col items-center justify-center text-slate-300 bg-slate-50/30">
        <div class="w-20 h-20 bg-white rounded-full border border-slate-100 flex items-center justify-center mb-4">
          <el-icon class="text-3xl text-slate-200"><ChatDotRound /></el-icon>
        </div>
        <p class="text-base font-medium text-slate-400">请选择一个对话</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, nextTick, onMounted, onUnmounted, ref, watch } from 'vue'
import { storeToRefs } from 'pinia'
import { ElMessage } from 'element-plus'
import { ChatDotRound, Top, Loading } from '@element-plus/icons-vue'
import defaultAvatarImg from '@/assets/images/empty.png'
import { useChatStore } from '@/store/modules/chat'
import type { ChatContactVO, ChatMessageStatus } from '@/api/chat'

const defaultAvatar = defaultAvatarImg
const inputContent = ref('')
const sending = ref(false)
const messageListRef = ref<HTMLElement | null>(null)
const chatStore = useChatStore()

const {
  contacts,
  messages,
  currentContact,
  connected,
  reconnecting,
  lastError
} = storeToRefs(chatStore)

const typingHint = computed(() => {
  if (chatStore.typingUserId && currentContact.value && chatStore.typingUserId === currentContact.value.id) {
    return `${currentContact.value.name} 正在输入...`
  }
  return lastError.value || ' '
})

onMounted(async () => {
  chatStore.connect()
  await chatStore.loadContacts()
})

onUnmounted(() => {
  chatStore.disconnect()
})

watch(
  () => chatStore.lastError,
  (value) => {
    if (value) {
      ElMessage.error(value)
    }
  }
)

const handleSelectContact = async (contact: ChatContactVO) => {
  if (currentContact.value?.id === contact.id && currentContact.value?.role === contact.role) return
  await chatStore.selectContact(contact)
  scrollToBottom()
}

const handleSend = async () => {
  const content = inputContent.value.trim()
  if (!content || !currentContact.value) return

  sending.value = true
  try {
    const optimistic = await chatStore.sendText(content)
    if (optimistic) {
      inputContent.value = ''
      await nextTick()
      scrollToBottom()
    }
  } finally {
    sending.value = false
  }
}

let typingTimer: number | null = null
const handleTyping = () => {
  chatStore.sendTyping(true)
  if (typingTimer) {
    window.clearTimeout(typingTimer)
  }
  typingTimer = window.setTimeout(() => {
    chatStore.sendTyping(false)
  }, 1200)
}

const scrollToBottom = () => {
  nextTick(() => {
    if (messageListRef.value) {
      messageListRef.value.scrollTo({
        top: messageListRef.value.scrollHeight,
        behavior: 'smooth'
      })
    }
  })
}

const formatStatus = (status?: ChatMessageStatus) => {
  if (status === 'FAILED') return '发送失败'
  if (status === 'READ') return '已读'
  if (status === 'SENT') return '已发送'
  if (status === 'PENDING') return '发送中'
  return ''
}

const formatTime = (timeStr: string) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  const now = new Date()
  const today = new Date(now.getFullYear(), now.getMonth(), now.getDate())
  const yesterday = new Date(today)
  yesterday.setDate(yesterday.getDate() - 1)
  const messageDate = new Date(date.getFullYear(), date.getMonth(), date.getDate())
  const timeString = date.toLocaleTimeString('zh-CN', { hour12: false, hour: '2-digit', minute: '2-digit' })

  if (messageDate.getTime() === today.getTime()) return timeString
  if (messageDate.getTime() === yesterday.getTime()) return `昨天 ${timeString}`
  if (date.getFullYear() === now.getFullYear()) return `${date.getMonth() + 1}月${date.getDate()}日 ${timeString}`
  return `${date.getFullYear()}年${date.getMonth() + 1}月${date.getDate()}日 ${timeString}`
}

const formatTimeShort = (timeStr: string) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  const now = new Date()
  if (date.toDateString() === now.toDateString()) {
    return date.toLocaleTimeString('zh-CN', { hour12: false, hour: '2-digit', minute: '2-digit' })
  }
  return date.toLocaleDateString('zh-CN', { month: 'numeric', day: 'numeric' })
}
</script>

<style scoped>
.custom-scrollbar::-webkit-scrollbar {
  width: 5px;
}

.custom-scrollbar::-webkit-scrollbar-track {
  background: transparent;
}

.custom-scrollbar::-webkit-scrollbar-thumb {
  background: rgba(148, 163, 184, 0.2);
  border-radius: 10px;
}

.custom-scrollbar::-webkit-scrollbar-thumb:hover {
  background: rgba(148, 163, 184, 0.4);
}
</style>
