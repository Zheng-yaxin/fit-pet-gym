<template>
  <div class="auth-container">
    <div class="liquid-background">
      <div class="blob blob-1"></div>
      <div class="blob blob-2"></div>
      <div class="blob blob-3"></div>
    </div>

    <div
        class="clean-panel"
        :class="{ 'animate-enter': !hasEntered }"
        :style="{ height: containerHeight + 'px' }"
    >
      <div ref="contentRef" class="panel-content">

        <div class="header-section">
          <div class="header-action left">
            <Transition name="fade">
              <a v-if="mode !== 'member'" @click="switchMode('member')" class="icon-link">
                <el-icon><Back /></el-icon>
              </a>
            </Transition>
          </div>

          <div class="header-center">
            <div class="minimal-icon">
              <el-icon :size="32" :color="getIconColor">
                <component :is="currentIcon" />
              </el-icon>
            </div>
            <h1 class="title">{{ currentTitle }}</h1>
            <p class="subtitle">智慧健身系统</p>
          </div>

          <div class="header-action right"></div>
        </div>

        <TransitionGroup name="fade-slide" tag="div" class="form-container">

          <div v-if="mode === 'member'" key="member" class="form-group">
            <div class="input-wrapper">
              <el-icon class="input-icon" :size="18"><Iphone /></el-icon>
              <input
                  v-model="loginForm.phone"
                  type="text"
                  class="slate-input"
                  placeholder="手机号码"
              />
            </div>

            <div class="input-wrapper">
              <el-icon class="input-icon" :size="18"><Lock /></el-icon>
              <input
                  v-model="loginForm.password"
                  type="password"
                  class="slate-input"
                  placeholder="密码"
                  @keyup.enter="handleMemberLogin"
              />
            </div>

            <div class="action-area">
              <AppleButton
                  type="primary"
                  block
                  size="large"
                  :loading="loading"
                  @click="handleMemberLogin"
                  class="btn-slate"
              >
                登录
              </AppleButton>
            </div>
          </div>

          <div v-if="mode === 'coach'" key="coach" class="form-group">
            <div class="input-wrapper">
              <el-icon class="input-icon" :size="18"><Avatar /></el-icon>
              <input
                  v-model="loginForm.phone"
                  type="text"
                  class="slate-input"
                  placeholder="教练手机号"
              />
            </div>

            <div class="input-wrapper">
              <el-icon class="input-icon" :size="18"><Lock /></el-icon>
              <input
                  v-model="loginForm.password"
                  type="password"
                  class="slate-input"
                  placeholder="密码"
                  @keyup.enter="handleCoachLogin"
              />
            </div>

            <div class="action-area">
              <AppleButton
                  type="primary"
                  block
                  size="large"
                  :loading="loading"
                  @click="handleCoachLogin"
                  style="--system-blue: #64748B;"
                  class="btn-slate"
              >
                工作台登录
              </AppleButton>
            </div>
          </div>

          <div v-if="mode === 'register'" key="register" class="form-group">
            <div class="input-wrapper">
              <el-icon class="input-icon" :size="18"><Iphone /></el-icon>
              <input
                  v-model="registerForm.phone"
                  class="slate-input"
                  placeholder="手机号"
              />
            </div>

            <div class="input-wrapper">
              <el-icon class="input-icon" :size="18"><User /></el-icon>
              <input
                  v-model="registerForm.name"
                  class="slate-input"
                  placeholder="姓名"
              />
            </div>

            <div class="slate-segmented-control">
              <div
                  class="segment-slider"
                  :style="{ transform: registerForm.gender === 1 ? 'translateX(2px)' : 'translateX(calc(100% - 2px))' }"
              ></div>
              <button
                  class="segment-item"
                  :class="{ active: registerForm.gender === 1 }"
                  @click="registerForm.gender = 1"
              >
                先生
              </button>
              <button
                  class="segment-item"
                  :class="{ active: registerForm.gender === 0 }"
                  @click="registerForm.gender = 0"
              >
                女士
              </button>
            </div>

            <div class="input-wrapper">
              <el-icon class="input-icon" :size="18"><Lock /></el-icon>
              <input
                  v-model="registerForm.password"
                  type="password"
                  class="slate-input"
                  placeholder="设置密码"
              />
            </div>

            <div class="action-area">
              <AppleButton
                  type="primary"
                  block
                  size="large"
                  :loading="loading"
                  @click="handleRegister"
                  class="btn-slate"
              >
                创建账户
              </AppleButton>
            </div>
          </div>

          <div v-if="mode === 'admin'" key="admin" class="form-group">
            <div class="admin-badge">
              <div class="badge-dot"></div>
              <span>环境已验证</span>
            </div>

            <div class="input-wrapper">
              <el-icon class="input-icon" :size="18"><Key /></el-icon>
              <input
                  v-model="loginForm.username"
                  type="text"
                  class="slate-input"
                  placeholder="管理员ID"
              />
            </div>

            <div class="input-wrapper">
              <el-icon class="input-icon" :size="18"><Lock /></el-icon>
              <input
                  v-model="loginForm.password"
                  type="password"
                  class="slate-input"
                  placeholder="访问密钥"
                  @keyup.enter="handleAdminLogin"
              />
            </div>

            <div class="action-area">
              <AppleButton
                  type="primary"
                  block
                  size="large"
                  :loading="loading"
                  @click="handleAdminLogin"
                  style="--system-blue: #0F172A;"
                  class="btn-dark"
              >
                进入控制台
              </AppleButton>
            </div>
          </div>

        </TransitionGroup>

        <div class="footer-clean">
          <div class="footer-links h-full flex-1">

            <template v-if="mode === 'member'">
              <a @click="switchMode('coach')" class="clean-link">我是教练</a>
              <span class="dot-separator"></span>
              <a @click="switchMode('register')" class="clean-link">注册新账号</a>
            </template>

            <template v-if="mode === 'coach'">
              <a @click="switchMode('member')" class="clean-link">普通会员登录</a>
              <span class="dot-separator"></span>
              <a @click="switchMode('register')" class="clean-link">新用户注册</a>
            </template>

            <template v-if="mode === 'register'">
              <a @click="switchMode('member')" class="clean-link">已有账号? 立即登录</a>
            </template>

            <template v-if="mode === 'admin'">
              <a @click="switchMode('member')" class="clean-link">返回前台系统</a>
            </template>

          </div>

          <a v-if="mode !== 'admin'" @click="switchMode('admin')" class="admin-trigger">
            ● ● ●
          </a>
        </div>

      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, nextTick, onMounted, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/store/modules/user'
import { loginMember, loginAdmin, loginCoach, register } from '@/api/auth'
import { getMemberProfile } from '@/api/member'
import { getCurrentCoachInfo } from '@/api/course'
import { ElMessage } from 'element-plus'
import AppleButton from '@/components/ui/AppleButton.vue'
import {
  Iphone, Lock, Key, Trophy, Postcard, User, Avatar, Back
} from '@element-plus/icons-vue'

// --- 类型定义 ---
type Mode = 'member' | 'register' | 'admin' | 'coach'
type HandoffPayload = {
  token: string
  userType: 'COACH' | 'SYS_USER'
  redirect: string
  issuedAt: number
}

const ACCEPTED_HANDOFF_REDIRECTS: Record<HandoffPayload['userType'], string> = {
  COACH: '/course/coach',
  SYS_USER: '/dashboard'
}

// --- 状态与 Hooks ---
const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const loading = ref(false)
const mode = ref<Mode>('member')
const contentRef = ref<HTMLElement | null>(null)
const containerHeight = ref(500) // 初始高度
const hasEntered = ref(false)
let resizeObserver: ResizeObserver | null = null

const loginForm = reactive({ username: '', phone: '', password: '' })
const registerForm = reactive({ phone: '', name: '', password: '', gender: 1 })

const decodeBase64UrlJson = <T,>(value: string): T => {
  const normalized = value.replace(/-/g, '+').replace(/_/g, '/')
  const padded = normalized.padEnd(Math.ceil(normalized.length / 4) * 4, '=')
  const binary = atob(padded)
  const bytes = Uint8Array.from(binary, (char) => char.charCodeAt(0))
  return JSON.parse(new TextDecoder().decode(bytes)) as T
}

const clearHandoffHash = () => {
  if (window.location.hash.startsWith('#handoff=')) {
    window.history.replaceState(null, document.title, `${window.location.pathname}${window.location.search}`)
  }
}

const handleHandoff = async () => {
  const hash = window.location.hash
  if (!hash.startsWith('#handoff=')) return false

  loading.value = true
  try {
    const payload = decodeBase64UrlJson<HandoffPayload>(hash.slice('#handoff='.length))
    const acceptedRedirect = ACCEPTED_HANDOFF_REDIRECTS[payload.userType]
    const isFresh = Math.abs(Date.now() - Number(payload.issuedAt)) < 5 * 60 * 1000

    if (!payload.token || !acceptedRedirect || payload.redirect !== acceptedRedirect || !isFresh) {
      throw new Error('登录交接已失效，请重新登录')
    }

    userStore.setToken(payload.token, payload.userType)

    if (payload.userType === 'COACH') {
      const coach = await getCurrentCoachInfo()
      userStore.setUserInfo(coach)
      ElMessage.success('欢迎回来，教练')
    } else {
      userStore.setUserInfo({ role: 'SYS_USER', name: '系统管理员' })
      ElMessage.success('管理控制台已连接')
    }

    clearHandoffHash()
    router.replace(payload.redirect)
    return true
  } catch (error) {
    console.error(error)
    userStore.clearLocalSession()
    clearHandoffHash()
    ElMessage.error(error instanceof Error ? error.message : '登录交接失败，请重新登录')
    return false
  } finally {
    loading.value = false
  }
}

// --- 动态计算属性 ---
const currentTitle = computed(() => {
  const titles: Record<Mode, string> = {
    member: '欢迎回来',
    coach: '教练工作台',
    register: '加入智慧健身',
    admin: '系统管理'
  }
  return titles[mode.value]
})

const currentIcon = computed(() => {
  if (mode.value === 'admin') return Key
  if (mode.value === 'coach') return Avatar
  if (mode.value === 'register') return Postcard
  return Trophy
})

const getIconColor = computed(() => {
  if (mode.value === 'admin') return '#475569' // Slate-600
  if (mode.value === 'coach') return '#10B981' // Emerald-500
  if (mode.value === 'register') return '#3B82F6' // Blue-500
  return '#64748B' // Slate-500
})

// --- 核心：高度自适应逻辑 ---

// 手动更新高度 (作为备用)
const updateHeight = () => {
  nextTick(() => {
    if (contentRef.value) {
      containerHeight.value = contentRef.value.offsetHeight
    }
  })
}

// 自动监听高度变化 (主要逻辑)
const initResizeObserver = () => {
  if (contentRef.value) {
    resizeObserver = new ResizeObserver((entries) => {
      for (const entry of entries) {
        containerHeight.value = entry.contentRect.height + 80 // + padding
        containerHeight.value = contentRef.value!.offsetHeight
      }
    })
    resizeObserver.observe(contentRef.value)
  }
}

// --- 生命周期 ---
onMounted(() => {
  handleHandoff()

  if (route.query.mode === 'register') {
    mode.value = 'register'
  }

  setTimeout(() => { hasEntered.value = true }, 100)

  // 初始化高度监听
  updateHeight()
  initResizeObserver()
})

onUnmounted(() => {
  if (resizeObserver) resizeObserver.disconnect()
})

// --- 切换逻辑 ---
const switchMode = (newMode: Mode) => {
  mode.value = newMode
  loginForm.password = ''
  if (newMode !== 'register') registerForm.password = ''

  if (newMode === 'register') {
    router.replace({ query: { ...route.query, mode: 'register' } })
  } else {
    const query = { ...route.query }
    delete query.mode
    router.replace({ query })
  }
  updateHeight()
}

// --- 业务逻辑 ---
const handleMemberLogin = async () => {
  if (!loginForm.phone || !loginForm.password) return ElMessage.warning('请输入手机号和密码')
  loading.value = true
  try {
    // 1. 获取 Token
    const res: any = await loginMember({ phone: loginForm.phone, password: loginForm.password })
    const token = res.token || res
    userStore.setToken(token, 'MEMBER')

    // 2. 获取并存储用户信息
    const user = await getMemberProfile()
    userStore.setUserInfo(user)

    ElMessage.success('登录成功')
    // 修改：跳转至首页 /home，而不是健康中心 /health
    router.push('/home')
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const handleCoachLogin = async () => {
  if (!loginForm.phone || !loginForm.password) return ElMessage.warning('请输入教练手机号和密码')
  loading.value = true
  try {
    const res: any = await loginCoach({ phone: loginForm.phone, password: loginForm.password })
    const token = res.token || res
    userStore.setToken(token, 'COACH')

    const coach = await getCurrentCoachInfo()
    userStore.setUserInfo(coach)

    ElMessage.success('欢迎回来，教练')
    router.push('/course/coach')
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const handleAdminLogin = async () => {
  if (!loginForm.username || !loginForm.password) return ElMessage.warning('请输入管理员账号')
  loading.value = true
  try {
    const res: any = await loginAdmin({ username: loginForm.username, password: loginForm.password })
    const token = res.token || res
    userStore.setToken(token, 'SYS_USER')
    ElMessage.success('管理控制台已连接')
    router.push('/dashboard')
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const handleRegister = async () => {
  if (!registerForm.phone || !registerForm.name || !registerForm.password) return ElMessage.warning('请填写完整信息')
  loading.value = true
  try {
    await register(registerForm)
    ElMessage.success('注册成功，请登录')
    loginForm.phone = registerForm.phone
    switchMode('member')
  } catch (e) { console.error(e) } finally { loading.value = false }
}
</script>

<style scoped lang="scss">
/* --- 全局变量 - ForgeFit OS Palette --- */
:root {
  --color-bg: var(--ff-surface-raised);
  --color-surface: var(--ff-surface);
  --color-text-main: #475569;
  --color-text-sub: #94A3B8;
  --color-border: #E2E8F0;
  --color-input-bg: #F1F5F9;
}

.auth-container {
  position: relative;
  width: 100vw;
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: var(--ff-surface-raised);
  overflow: hidden;
  font-family: var(--ff-font-ui);
  color: #475569;
}

/* --- 背景动画 --- */
.liquid-background {
  position: absolute;
  inset: -20%;
  filter: blur(80px);
  z-index: 0;
  opacity: 0.6;
}

.blob {
  position: absolute;
  border-radius: 50%;
  mix-blend-mode: normal;
  animation: liquidMove 25s infinite ease-in-out alternate;
}

.blob-1 {
  top: 10%; left: 15%; width: 45vw; height: 45vw;
  background: #E2E8F0;
  animation-duration: 30s;
}
.blob-2 {
  bottom: 15%; right: 10%; width: 40vw; height: 40vw;
  background: #DBEAFE;
  animation-delay: -5s;
  animation-duration: 28s;
}
.blob-3 {
  top: 35%; right: 35%; width: 30vw; height: 30vw;
  background: #D1FAE5;
  animation-delay: -10s;
}

@keyframes liquidMove {
  0% { transform: translate(0, 0) scale(1); }
  100% { transform: translate(20px, -20px) scale(1.05); }
}

/* --- 卡片容器 (带高度动画) --- */
.clean-panel {
  position: relative;
  z-index: 10;
  width: 90%;
  max-width: 380px;
  background: var(--ff-surface);
  border-radius: 32px;
  box-shadow: 0 20px 40px -10px rgba(71, 85, 105, 0.1);
  /* 关键：高度过渡动画 */
  transition: height 0.5s cubic-bezier(0.16, 1, 0.3, 1);
  overflow: hidden;
  will-change: height;

  &.animate-enter {
    animation: springEnter 0.8s cubic-bezier(0.16, 1, 0.3, 1) backwards;
  }
}

@keyframes springEnter {
  from { opacity: 0; transform: translateY(40px); }
  to { opacity: 1; transform: translateY(0); }
}

.panel-content {
  padding: 40px 32px;
  /* 确保内容撑开容器，不要设置固定高度 */
  box-sizing: border-box;
}

/* --- Header --- */
.header-section {
  position: relative;
  display: flex;
  align-items: flex-start;
  justify-content: center;
  margin-bottom: 40px;
  min-height: 100px;
}

.header-center {
  display: flex;
  flex-direction: column;
  align-items: center;
  z-index: 2;
  position: relative;
}

.header-action {
  position: absolute;
  top: 0;
  z-index: 10;
  &.left { left: 0; }
  &.right { right: 0; }
}

.icon-link {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px; height: 36px;
  border-radius: 50%;
  color: #94A3B8;
  background: transparent;
  transition: all 0.2s;
  cursor: pointer;

  &:hover { background: #F1F5F9; color: #475569; }
}

.minimal-icon {
  margin-bottom: 16px;
  width: 56px; height: 56px;
  display: flex; align-items: center; justify-content: center;
  background: var(--ff-surface-raised);
  border-radius: 16px;
}

.title {
  font-size: 24px; font-weight: 600; color: #475569;
  letter-spacing: -0.02em; margin: 0 0 4px 0;
}
.subtitle { font-size: 14px; color: #94A3B8; font-weight: 500; }

/* --- 表单 --- */
.form-group { display: flex; flex-direction: column; gap: 16px; }

.input-wrapper {
  position: relative;
  transition: all 0.2s;
  display: flex; align-items: center;
}

.input-icon {
  position: absolute; left: 16px;
  color: #94A3B8; z-index: 2;
  pointer-events: none; transition: color 0.3s;
}

.slate-input {
  width: 100%; height: 52px;
  padding: 0 16px 0 46px;
  background: #F1F5F9;
  border: 1px solid transparent; border-radius: 14px;
  font-size: 16px; color: #475569;
  transition: all 0.2s ease; outline: none;

  &::placeholder { color: var(--ff-border-strong); }
  &:focus {
    background: var(--ff-surface); border-color: #E2E8F0;
    box-shadow: 0 4px 12px rgba(0,0,0,0.03);
    & + .input-icon { color: #64748B; }
  }
}

.action-area { margin-top: 12px; }

.btn-slate {
  background-color: #475569 !important;
  box-shadow: 0 4px 12px rgba(71, 85, 105, 0.2) !important;
  font-weight: 500 !important;
  &:hover { background-color: #334155 !important; }
}
.btn-dark { background-color: #1E293B !important; }

/* --- 分段控制器 --- */
.slate-segmented-control {
  background: #F1F5F9; border-radius: 12px;
  padding: 4px; display: flex; position: relative;
  height: 44px; margin-bottom: 8px;
}

.segment-slider {
  position: absolute; top: 4px; bottom: 4px; left: 0; width: 50%;
  background: var(--ff-surface); border-radius: 10px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.05);
  transition: transform 0.3s cubic-bezier(0.2, 0.8, 0.2, 1);
  z-index: 1;
}

.segment-item {
  flex: 1; position: relative; z-index: 2;
  border: none; background: none;
  font-size: 14px; font-weight: 500; color: #64748B;
  cursor: pointer; transition: color 0.2s;
  &.active { color: #475569; font-weight: 600; }
}

.admin-badge {
  display: flex; align-items: center; justify-content: center; gap: 8px;
  padding: 6px; margin-bottom: 8px;
  background: #FEF3C7; border-radius: 99px;
  color: #D97706; font-size: 12px; font-weight: 600;
  width: fit-content; margin-left: auto; margin-right: auto;
  .badge-dot { width: 6px; height: 6px; border-radius: 50%; background: #D97706; }
}

/* --- 底部 --- */
.footer-clean {
  margin-top: 48px; padding-top: 24px;
  border-top: 1px solid #F1F5F9;
  display: flex; justify-content: space-between; align-items: center;
}

.footer-links { display: flex; align-items: center; gap: 12px; flex-wrap: wrap; }

.clean-link {
  font-size: 14px; color: #94A3B8; cursor: pointer;
  font-weight: 500; transition: color 0.2s;
  &:hover { color: #475569; }
}

.dot-separator {
  width: 4px; height: 4px;
  background: #D1FAE5; border-radius: 50%;
}

.admin-trigger {
  font-size: 8px; color: #E2E8F0; cursor: pointer;
  transition: color 0.3s;
  &:hover { color: #94A3B8; }
}

/* --- 动画 --- */
/* fade-slide 用于表单内容的左右切换 */
.fade-slide-move,
.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: all 0.4s cubic-bezier(0.25, 1, 0.5, 1);
}

.fade-slide-enter-from { opacity: 0; transform: translateX(10px); }
/* 关键：绝对定位让离开的元素不占据高度，实现高度平滑过渡 */
.fade-slide-leave-to { opacity: 0; transform: translateX(-10px); position: absolute; width: 100%; top: 0; left: 0; }
/* 确保相对定位上下文 */
.form-container { position: relative; }

/* fade 用于返回按钮的简单显隐 */
.fade-enter-active, .fade-leave-active { transition: opacity 0.3s ease; }
.fade-enter-from, .fade-leave-to { opacity: 0; }
</style>
