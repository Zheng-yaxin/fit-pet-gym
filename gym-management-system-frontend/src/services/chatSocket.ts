import { useUserStore } from '@/store/modules/user'

export type ChatSocketMessageType = 'CHAT_MESSAGE' | 'READ_RECEIPT' | 'TYPING' | 'ONLINE_STATUS' | 'ERROR'

export interface ChatSocketEnvelope<T = any> {
    type: ChatSocketMessageType
    requestId?: string
    senderId?: number
    senderRole?: string
    receiverId?: number
    receiverRole?: string
    payload?: T
    timestamp?: number
}

type Listener = (event: ChatSocketEnvelope) => void

class ChatSocketService {
    private socket: WebSocket | null = null
    private listeners = new Set<Listener>()
    private reconnectTimer: number | null = null
    private reconnectAttempts = 0
    private manuallyClosed = false

    connect() {
        const userStore = useUserStore()
        if (!userStore.token || this.socket?.readyState === WebSocket.OPEN || this.socket?.readyState === WebSocket.CONNECTING) {
            return
        }

        this.manuallyClosed = false
        const protocol = window.location.protocol === 'https:' ? 'wss' : 'ws'
        const base = `${protocol}://${window.location.host}`
        this.socket = new WebSocket(`${base}/ws/chat?token=${encodeURIComponent(userStore.token)}`)

        this.socket.onopen = () => {
            this.reconnectAttempts = 0
            this.emit({ type: 'ONLINE_STATUS', payload: { connected: true }, timestamp: Date.now() })
        }

        this.socket.onmessage = (event) => {
            try {
                this.emit(JSON.parse(event.data))
            } catch (error) {
                this.emit({ type: 'ERROR', payload: { content: 'Invalid socket message' }, timestamp: Date.now() })
            }
        }

        this.socket.onerror = () => {
            this.emit({ type: 'ERROR', payload: { content: 'Socket connection error' }, timestamp: Date.now() })
        }

        this.socket.onclose = () => {
            this.emit({ type: 'ONLINE_STATUS', payload: { connected: false }, timestamp: Date.now() })
            if (!this.manuallyClosed) {
                this.scheduleReconnect()
            }
        }
    }

    disconnect() {
        this.manuallyClosed = true
        if (this.reconnectTimer) {
            window.clearTimeout(this.reconnectTimer)
            this.reconnectTimer = null
        }
        this.socket?.close()
        this.socket = null
    }

    send(envelope: ChatSocketEnvelope) {
        if (this.socket?.readyState !== WebSocket.OPEN) {
            return false
        }
        this.socket.send(JSON.stringify({ ...envelope, timestamp: Date.now() }))
        return true
    }

    isOpen() {
        return this.socket?.readyState === WebSocket.OPEN
    }

    subscribe(listener: Listener) {
        this.listeners.add(listener)
        return () => this.listeners.delete(listener)
    }

    private emit(event: ChatSocketEnvelope) {
        this.listeners.forEach(listener => listener(event))
    }

    private scheduleReconnect() {
        if (this.reconnectTimer) return
        const delay = Math.min(1000 * 2 ** this.reconnectAttempts, 15000)
        this.reconnectAttempts += 1
        this.reconnectTimer = window.setTimeout(() => {
            this.reconnectTimer = null
            this.connect()
        }, delay)
    }
}

export const chatSocket = new ChatSocketService()
