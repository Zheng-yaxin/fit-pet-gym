import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { logout as logoutApi } from '@/api/auth'

export const useUserStore = defineStore('user', () => {
    // 1. 初始化时尝试从 localStorage 恢复 userInfo
    const storedUserInfo = localStorage.getItem('userInfo')

    const token = ref<string>(localStorage.getItem('token') || '')
    const userType = ref<string>(localStorage.getItem('userType') || '')
    const userInfo = ref<any>(storedUserInfo ? JSON.parse(storedUserInfo) : null)

    // 2. 新增 userId 计算属性，供页面调用
    const userId = computed(() => userInfo.value?.id)

    function setToken(newToken: string, type: string = '') {
        token.value = newToken
        localStorage.setItem('token', newToken)
        if (type) {
            userType.value = type
            localStorage.setItem('userType', type)
        }
    }

    function setUserInfo(info: any) {
        userInfo.value = info
        // 3. 保存用户信息到本地，防止刷新丢失
        localStorage.setItem('userInfo', JSON.stringify(info))
    }

    function clearLocalSession() {
        token.value = ''
        userType.value = ''
        userInfo.value = null
        localStorage.removeItem('token')
        localStorage.removeItem('userType')
        localStorage.removeItem('userInfo') // 清除缓存
    }

    function logout() {
        logoutApi().catch(() => {})
        clearLocalSession()
        window.location.href = '/login'
    }

    return {
        token,
        userType,
        userInfo,
        userId, // 导出 userId
        setToken,
        setUserInfo,
        clearLocalSession,
        logout
    }
})
