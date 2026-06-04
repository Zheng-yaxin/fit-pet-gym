import { computed, ref } from 'vue'
import { defineStore } from 'pinia'
import { chatSocket, type ChatSocketEnvelope } from '@/services/chatSocket'
import {
    getContacts,
    getMessageHistory,
    sendMessage,
    type ChatContactVO,
    type ChatMessageVO
} from '@/api/chat'

function buildTempMessage(content: string): ChatMessageVO {
    return {
        id: -Date.now(),
        senderId: 0,
        senderRole: '',
        senderName: 'Me',
        senderAvatar: '',
        content,
        createTime: new Date().toISOString(),
        isSelf: true,
        status: 'PENDING',
        requestId: `${Date.now()}-${Math.random().toString(16).slice(2)}`
    }
}

export const useChatStore = defineStore('chat', () => {
    const contacts = ref<ChatContactVO[]>([])
    const messages = ref<ChatMessageVO[]>([])
    const currentContact = ref<ChatContactVO | null>(null)
    const connected = ref(false)
    const reconnecting = ref(false)
    const lastError = ref('')
    const pendingMessages = ref<ChatMessageVO[]>([])
    const typingUserId = ref<number | null>(null)
    const typingAt = ref<number | null>(null)
    let typingClearTimer: number | null = null

    let unsubscribe: (() => void) | null = null

    const conversationKey = computed(() => {
        if (!currentContact.value) return ''
        return `${currentContact.value.role}:${currentContact.value.id}`
    })

    function connect() {
        if (!unsubscribe) {
            unsubscribe = chatSocket.subscribe(handleSocketEvent)
        }
        chatSocket.connect()
    }

    function disconnect() {
        unsubscribe?.()
        unsubscribe = null
        chatSocket.disconnect()
        connected.value = false
        reconnecting.value = false
        clearTypingState()
    }

    async function loadContacts(silent = false) {
        try {
            const list = await getContacts()
            contacts.value = list || []
        } catch (error) {
            if (!silent) {
                lastError.value = '联系人加载失败'
            }
        }
    }

    async function selectContact(contact: ChatContactVO) {
        currentContact.value = contact
        const history = await getMessageHistory(contact.id, contact.role)
        messages.value = history || []
        contact.unreadCount = 0
        contact.online = Boolean(contact.online)
        sendReadReceipt(contact)
    }

    async function sendText(content: string) {
        if (!currentContact.value || !content.trim()) return null

        const optimistic = buildTempMessage(content.trim())
        messages.value.push(optimistic)
        pendingMessages.value.push(optimistic)

        const payload = {
            receiverId: currentContact.value.id,
            receiverRole: currentContact.value.role,
            content: content.trim()
        }

        try {
            const sentBySocket = chatSocket.send({
                type: 'CHAT_MESSAGE',
                requestId: optimistic.requestId,
                receiverId: payload.receiverId,
                receiverRole: payload.receiverRole,
                payload: { content: payload.content }
            })

            if (!sentBySocket) {
                const saved = await sendMessage(payload) as ChatMessageVO
                applySentMessage(optimistic, saved)
                await loadContacts(true)
            }
            return optimistic
        } catch (error) {
            optimistic.status = 'FAILED'
            lastError.value = '发送失败'
            return null
        }
    }

    function applySentMessage(target: ChatMessageVO, saved: Partial<ChatMessageVO>) {
        Object.assign(target, saved, {
            requestId: target.requestId,
            status: 'SENT',
            isSelf: true
        })
        pendingMessages.value = pendingMessages.value.filter(item => item.requestId !== target.requestId)
    }

    async function retryMessage(message: ChatMessageVO) {
        if (!message.content) return null
        messages.value = messages.value.filter(item => item !== message)
        pendingMessages.value = pendingMessages.value.filter(item => item !== message)
        return sendText(message.content)
    }

    function sendTyping(isTyping: boolean) {
        if (!currentContact.value) return
        chatSocket.send({
            type: 'TYPING',
            receiverId: currentContact.value.id,
            receiverRole: currentContact.value.role,
            payload: { isTyping }
        })
    }

    function sendReadReceipt(contact = currentContact.value) {
        if (!contact) return
        chatSocket.send({
            type: 'READ_RECEIPT',
            receiverId: contact.id,
            receiverRole: contact.role,
            payload: { readAt: Date.now() }
        })
    }

    function markMessagesRead() {
        if (!currentContact.value) return
        messages.value = messages.value.map(item => {
            if (item.isSelf && item.status !== 'FAILED') {
                return { ...item, status: 'READ' }
            }
            return item
        })
    }

    function clearTypingState() {
        if (typingClearTimer) {
            window.clearTimeout(typingClearTimer)
            typingClearTimer = null
        }
        typingUserId.value = null
        typingAt.value = null
    }

    function upsertIncomingMessage(event: ChatSocketEnvelope) {
        const payload = event.payload as any
        const incoming: ChatMessageVO = {
            id: payload?.messageId || Date.now(),
            senderId: event.senderId || 0,
            senderRole: event.senderRole || '',
            senderName: '',
            senderAvatar: '',
            content: payload?.content || '',
            createTime: new Date(event.timestamp || Date.now()).toISOString(),
            isSelf: false,
            status: 'DELIVERED'
        }

        if (currentContact.value && event.senderId === currentContact.value.id && event.senderRole === currentContact.value.role) {
            messages.value.push(incoming)
            sendReadReceipt()
            markMessagesRead()
            return
        }

        const contact = contacts.value.find(item => item.id === event.senderId && item.role === event.senderRole)
        if (contact) {
            contact.unreadCount = (contact.unreadCount || 0) + 1
            contact.latestMessage = incoming.content
            contact.latestMessageTime = incoming.createTime
            contact.lastMessageTime = incoming.createTime
        }
    }

    function handleSocketEvent(event: ChatSocketEnvelope) {
        if (event.type === 'ONLINE_STATUS') {
            const payload = event.payload as any
            const onlineState = payload?.connected ?? payload?.online ?? false
            if (!event.senderId || !event.senderRole) {
                connected.value = Boolean(onlineState)
                reconnecting.value = !connected.value
                return
            }

            const online = Boolean(onlineState)
            const contact = contacts.value.find(item => item.id === event.senderId && item.role === event.senderRole)
            if (contact) {
                contact.online = online
            }

            if (currentContact.value && currentContact.value.id === event.senderId && currentContact.value.role === event.senderRole) {
                currentContact.value.online = online
            }
            return
        }
        if (event.type === 'ERROR') {
            lastError.value = (event.payload as any)?.content || '聊天连接异常'
            if (event.requestId) {
                const pending = pendingMessages.value.find(item => item.requestId === event.requestId)
                if (pending) {
                    pending.status = 'FAILED'
                }
            }
            return
        }

        if (event.type === 'TYPING') {
            if ((event.payload as any)?.isTyping === false) {
                clearTypingState()
            } else {
                typingUserId.value = event.senderId || null
                typingAt.value = Date.now()
                if (typingClearTimer) {
                    window.clearTimeout(typingClearTimer)
                }
                typingClearTimer = window.setTimeout(() => {
                    clearTypingState()
                }, 2500)
            }
            return
        }

        if (event.type === 'READ_RECEIPT') {
            if (currentContact.value && event.senderId === currentContact.value.id && event.senderRole === currentContact.value.role) {
                messages.value = messages.value.map(item => item.isSelf ? { ...item, status: 'READ' } : item)
            }
            return
        }

        if (event.type === 'CHAT_MESSAGE') {
            const pending = event.requestId
                ? pendingMessages.value.find(item => item.requestId === event.requestId)
                : null
            if (pending) {
                applySentMessage(pending, {
                    id: (event.payload as any)?.messageId || pending.id,
                    content: (event.payload as any)?.content || pending.content,
                    createTime: new Date(event.timestamp || Date.now()).toISOString()
                })
                loadContacts(true)
                return
            }
            upsertIncomingMessage(event)
        }
    }

    return {
        contacts,
        messages,
        currentContact,
        connected,
        reconnecting,
        lastError,
        pendingMessages,
        typingUserId,
        typingAt,
        conversationKey,
        connect,
        disconnect,
        loadContacts,
        selectContact,
        sendText,
        retryMessage,
        sendTyping,
        sendReadReceipt,
        markMessagesRead,
        clearTypingState
    }
})
