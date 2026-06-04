<template>
  <div class="min-h-screen w-full bg-slate-100 flex flex-col items-center justify-center p-6">
    <div class="w-full max-w-[1200px] flex flex-col gap-4 animate-fadeIn">
      <div class="flex">
        <button
          @click="goBack"
          class="flex items-center gap-2 px-5 py-2.5 bg-white rounded-full text-slate-600 font-bold hover:text-slate-800 hover:bg-white hover:scale-105 hover:shadow-md transition-all duration-300 shadow-sm ring-1 ring-slate-200 group"
        >
          <el-icon class="group-hover:-translate-x-1 transition-transform"><ArrowLeft /></el-icon>
          <span>Back</span>
        </button>
      </div>

      <div class="flex h-[80vh] min-h-[600px] bg-slate-50 rounded-[32px] overflow-hidden shadow-2xl ring-1 ring-slate-100">
        <div class="w-[320px] flex flex-col bg-slate-50 relative z-10">
          <div class="h-24 relative flex items-center justify-center">
            <span class="text-lg font-bold text-slate-600 tracking-tight">Messages</span>
          </div>

          <div class="flex-1 overflow-y-auto px-4 pb-4 space-y-2 custom-scrollbar">
            <div
              v-for="contact in contacts"
              :key="contact.id"
              @click="handleSelectContact(contact)"
              class="group relative flex items-center gap-4 p-4 rounded-[20px] cursor-pointer transition-all duration-300"
              :class="currentContact?.id === contact.id
                ? 'bg-white shadow-md shadow-slate-200/50 scale-[1.02]'
                : 'hover:bg-white/60 hover:shadow-sm'"
            >
              <div class="relative shrink-0">
                <el-avatar :size="48" :src="contact.avatar || defaultAvatar" class="bg-slate-200 ring-4 ring-slate-50 group-hover:ring-white transition-all" />
                <div
                  class="absolute bottom-0 right-0 w-3 h-3 border-[2px] border-white rounded-full"
                  :class="contact.online ? 'bg-emerald-300' : 'bg-slate-300'"
                ></div>
              </div>

              <div class="flex flex-col flex-1 min-w-0">
                <div class="flex justify-between items-baseline mb-1">
                  <span class="text-[15px] font-bold text-slate-600 truncate group-hover:text-slate-800 transition-colors">{{ contact.name }}</span>
                  <span class="text-[10px] text-slate-400 font-medium">{{ contact.lastMessageTime ? formatTimeShort(contact.lastMessageTime) : '' }}</span>
                </div>
                <div class="flex items-center justify-between gap-2">
                  <span class="text-[11px] px-2 py-0.5 rounded-full bg-slate-100 text-slate-400 font-medium group-hover:bg-slate-200/50 transition-colors">
                    {{ contact.role }}
                  </span>
                  <span v-if="contact.unreadCount" class="text-[11px] font-bold px-2 py-0.5 rounded-full bg-rose-100 text-rose-500">
                    {{ contact.unreadCount }}
                  </span>
                </div>
              </div>
            </div>

            <div v-if="contacts.length === 0" class="flex flex-col items-center justify-center py-24 text-slate-300">
              <div class="w-14 h-14 bg-slate-100 rounded-full flex items-center justify-center mb-3">
                <el-icon class="text-2xl text-slate-300"><ChatDotRound /></el-icon>
              </div>
              <span class="text-xs font-medium text-slate-400">No messages yet</span>
            </div>
          </div>
        </div>

        <div class="flex-1 flex flex-col bg-white rounded-l-[32px] shadow-[-10px_0_30px_-15px_rgba(0,0,0,0.03)] relative z-20 overflow-hidden">
          <template v-if="currentContact">
            <div class="h-24 relative flex items-center justify-center shrink-0 border-b border-slate-50">
              <div class="flex flex-col items-center z-10">
                <span class="text-xl font-bold text-slate-600 tracking-tight">{{ currentContact.name }}</span>
                <div class="flex items-center gap-1.5 mt-1">
                  <span class="w-1.5 h-1.5 rounded-full" :class="currentContact.online ? 'bg-emerald-300 animate-pulse' : 'bg-slate-300'"></span>
                  <span class="text-[10px] text-slate-400 font-bold uppercase tracking-widest">
                    {{ currentContact.online ? 'Active Member' : 'Offline' }}
                  </span>
                </div>
              </div>
            </div>

            <div class="flex-1 overflow-y-auto px-10 py-6 space-y-8 custom-scrollbar bg-white" ref="messageListRef">
              <div v-if="messages.length === 0" class="flex flex-col items-center justify-center h-full">
                <p class="text-xs font-medium bg-slate-50 px-6 py-2 rounded-full text-slate-400 tracking-wide">Start a new conversation</p>
              </div>

              <div v-for="msg in messages" :key="msg.requestId || msg.id || msg.createTime" class="flex w-full group animate-fadeIn" :class="{ 'flex-row-reverse': msg.isSelf }">
                <el-avatar
                  :src="msg.isSelf ? (userAvatar || defaultAvatar) : (msg.senderAvatar || currentContact.avatar || defaultAvatar)"
                  :size="40"
                  class="shrink-0 mb-1 ring-4 ring-slate-50 mt-6"
                />

                <div class="flex flex-col max-w-[60%] mx-4" :class="{ 'items-end': msg.isSelf, 'items-start': !msg.isSelf }">
                  <div class="mb-1.5 flex items-center gap-2 opacity-80" :class="{ 'flex-row-reverse': msg.isSelf }">
                    <span class="text-[11px] font-bold text-slate-500">
                      {{ msg.isSelf ? 'Me' : currentContact.name }}
                    </span>
                    <span class="text-[10px] text-slate-300 font-medium">
                      {{ formatTime(msg.createTime) }}
                    </span>
                  </div>

                  <div
                    class="px-6 py-3.5 text-[15px] leading-relaxed shadow-sm break-words whitespace-pre-wrap transition-all duration-200"
                    :class="msg.isSelf
                      ? 'bg-slate-600 text-white rounded-[24px] rounded-tr-md shadow-slate-200'
                      : 'bg-slate-50 text-slate-600 rounded-[24px] rounded-tl-md border border-slate-100/50'"
                  >
                    {{ msg.content }}
                  </div>

                  <div v-if="msg.isSelf" class="mt-1 text-[10px] font-medium text-slate-400">
                    {{ msg.status === 'FAILED' ? 'Failed' : msg.status === 'READ' ? 'Read' : msg.status === 'SENT' ? 'Sent' : 'Sending' }}
                  </div>
                </div>
              </div>
            </div>

            <div class="p-8 bg-white/90 backdrop-blur-sm relative z-30">
              <div class="flex items-end gap-3 bg-slate-50 rounded-[28px] p-2 pl-4 ring-1 ring-slate-100 transition-all duration-300 focus-within:ring-2 focus-within:ring-slate-200 focus-within:bg-white focus-within:shadow-lg focus-within:shadow-slate-100">
                <textarea
                  v-model="inputContent"
                  @keydown.enter.exact.prevent="handleSend"
                  @input="handleTyping"
                  placeholder="Type your message..."
                  rows="1"
                  class="flex-1 py-3 bg-transparent border-none outline-none text-slate-600 placeholder-slate-300 resize-none max-h-[120px] min-h-[44px] text-[15px] leading-6 custom-scrollbar"
                ></textarea>

                <button
                  @click="handleSend"
                  :disabled="!inputContent.trim() || sending"
                  class="h-11 w-11 flex items-center justify-center rounded-full bg-slate-800 hover:bg-slate-700 text-white disabled:opacity-30 disabled:cursor-not-allowed transition-all duration-300 shadow-md transform active:scale-95"
                >
                  <el-icon v-if="!sending" :size="18"><Top /></el-icon>
                  <el-icon v-else class="is-loading"><Loading /></el-icon>
                </button>
              </div>

              <div class="mt-3 flex items-center justify-between text-[11px] text-slate-400">
                <span>{{ typingHint }}</span>
                <span v-if="connected" class="text-emerald-500">Connected</span>
                <span v-else-if="reconnecting" class="text-amber-500">Reconnecting</span>
                <span v-else class="text-rose-400">Offline</span>
              </div>
            </div>
          </template>

          <div v-else class="h-full flex flex-col items-center justify-center bg-white">
            <div class="w-24 h-24 bg-slate-50 rounded-[32px] flex items-center justify-center mb-6 shadow-sm rotate-3">
              <el-icon class="text-4xl text-slate-300"><ChatDotRound /></el-icon>
            </div>
            <p class="text-lg font-bold text-slate-600">Select a conversation</p>
            <p class="text-sm text-slate-400 mt-2">Choose a member from the left to start chatting</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, nextTick, onMounted, onUnmounted, ref, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { storeToRefs } from 'pinia'
import { ElMessage } from 'element-plus'
import { ChatDotRound, Top, Loading, ArrowLeft } from '@element-plus/icons-vue'
import defaultAvatarImg from '@/assets/images/empty.png'
import { useChatStore } from '@/store/modules/chat'
import type { ChatContactVO } from '@/api/chat'

const router = useRouter()
const route = useRoute()
const defaultAvatar = defaultAvatarImg
const userAvatar = ref('')
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
    return `${currentContact.value.name} is typing...`
  }
  return lastError.value || ' '
})

onMounted(async () => {
  chatStore.connect()
  await chatStore.loadContacts()

  const { targetId, targetName, role, avatar } = route.query
  if (targetId) {
    const tId = Number(targetId)
    const tRole = (role as string) || 'COACH'
    const existingContact = contacts.value.find(c => c.id === tId && c.role === tRole)

    if (existingContact) {
      await handleSelectContact(existingContact)
    } else {
      const newContact: ChatContactVO = {
        id: tId,
        name: (targetName as string) || `User ${tId}`,
        avatar: (avatar as string) || '',
        role: tRole,
        unreadCount: 0
      }
      contacts.value.unshift(newContact)
      currentContact.value = newContact
      messages.value = []
    }
  }
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

const goBack = () => {
  router.back()
}

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
  if (messageDate.getTime() === yesterday.getTime()) return `Yesterday ${timeString}`
  if (date.getFullYear() === now.getFullYear()) return `${date.getMonth() + 1}/${date.getDate()} ${timeString}`
  return `${date.getFullYear()}/${date.getMonth() + 1}/${date.getDate()} ${timeString}`
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
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(5px); }
  to { opacity: 1; transform: translateY(0); }
}

.animate-fadeIn {
  animation: fadeIn 0.3s ease-out forwards;
}

.custom-scrollbar::-webkit-scrollbar {
  width: 4px;
}

.custom-scrollbar::-webkit-scrollbar-track {
  background: transparent;
}

.custom-scrollbar::-webkit-scrollbar-thumb {
  background: rgba(203, 213, 225, 0.4);
  border-radius: 10px;
}

.custom-scrollbar::-webkit-scrollbar-thumb:hover {
  background: rgba(148, 163, 184, 0.6);
}
</style>
