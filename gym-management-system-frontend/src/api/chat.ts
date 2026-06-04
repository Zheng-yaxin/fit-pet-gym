import request from '@/utils/request'

export interface ChatContactVO {
    id: number
    name: string
    avatar: string
    role: string
    unreadCount?: number
    latestMessage?: string
    latestMessageTime?: string
    lastMessageTime?: string
    online?: boolean
}

export type ChatMessageStatus = 'PENDING' | 'SENT' | 'DELIVERED' | 'READ' | 'FAILED'

export interface ChatMessageVO {
    id: number
    senderId: number
    senderRole: string
    senderName: string
    senderAvatar: string
    content: string
    createTime: string
    isSelf: boolean
    status?: ChatMessageStatus
    requestId?: string
}

export interface SendMessageDTO {
    receiverId: number
    receiverRole: string
    content: string
}

export const getContacts = () => {
    return request<ChatContactVO[]>({
        url: '/chat/contacts',
        method: 'get'
    })
}

export const getMessageHistory = (contactId: number, contactRole: string) => {
    return request<ChatMessageVO[]>({
        url: '/chat/list',
        method: 'get',
        params: { contactId, contactRole }
    })
}

export const sendMessage = (data: SendMessageDTO) => {
    return request<ChatMessageVO>({
        url: '/chat/send',
        method: 'post',
        data
    })
}

export const getUnreadCount = () => {
    return request<number>({
        url: '/chat/unread-count',
        method: 'get'
    })
}
