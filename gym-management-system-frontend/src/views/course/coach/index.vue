<template>
  <div class="min-h-screen bg-slate-50 relative font-sans text-slate-600 selection:bg-slate-200 selection:text-slate-700">

    <header class="fixed top-0 left-0 right-0 z-50 h-[72px] bg-white border-b border-slate-100 px-8 flex items-center justify-between">
      <div class="flex items-center gap-4">
        <div class="w-10 h-10 rounded-xl bg-slate-800 flex items-center justify-center text-white shadow-md shadow-slate-200">
          <el-icon :size="20"><Monitor /></el-icon>
        </div>
        <span class="text-xl font-bold text-slate-600 tracking-tight">教练<span class="text-slate-400">中心</span></span>
      </div>

      <div class="flex items-center gap-6">
        <div class="text-right hidden sm:block">
          <div class="text-sm font-bold text-slate-600">{{ coachInfo.name || '教练' }}</div>
          <div class="text-[11px] font-medium text-slate-400 tracking-wide uppercase">{{ coachInfo.certification || '已认证' }}</div>
        </div>
        <div class="p-1 rounded-full border border-slate-100 bg-slate-50">
          <el-avatar :size="36" :src="coachInfo.avatar" class="bg-white text-slate-300">{{ coachInfo.name?.[0] }}</el-avatar>
        </div>
        <div class="h-6 w-px bg-slate-200 mx-1"></div>
        <button @click="handleLogout" class="w-9 h-9 rounded-full flex items-center justify-center hover:bg-rose-50 text-slate-300 hover:text-rose-400 transition-colors duration-300">
          <el-icon :size="18"><SwitchButton /></el-icon>
        </button>
      </div>
    </header>

    <main class="relative z-10 max-w-7xl mx-auto pt-28 pb-12 px-6 space-y-10">

      <div class="relative w-full rounded-[32px] overflow-hidden p-10 bg-white shadow-sm border border-slate-100 group">
        <div class="relative z-10 flex flex-col md:flex-row justify-between items-center gap-8">
          <div class="text-center md:text-left">
            <h1 class="text-4xl font-bold text-slate-600 tracking-tight mb-3">
              你好，<span class="text-slate-800">{{ coachInfo.name || '教练' }}</span>
            </h1>
            <p class="text-lg text-slate-400 max-w-xl leading-relaxed font-medium mx-auto md:mx-0">
              {{ coachInfo.bio || '准备好挥洒汗水了吗？您的日程已同步，随时准备开始今天的课程。' }}
            </p>

            <div class="flex flex-wrap gap-2 mt-6 justify-center md:justify-start">
              <span v-for="tag in specialtyTags" :key="tag" class="px-3 py-1 bg-slate-50 border border-slate-100 rounded-full text-xs font-semibold text-slate-500">
                # {{ tag }}
              </span>
            </div>
          </div>

          <div class="flex gap-10 items-center bg-slate-50 px-8 py-5 rounded-[24px] border border-slate-100">
            <div class="flex flex-col items-center">
              <span class="text-3xl font-bold text-slate-700 tracking-tight">{{ coachInfo.experienceYears || 0 }}</span>
              <span class="text-[10px] font-bold text-slate-400 uppercase tracking-wider mt-1">从业年限</span>
            </div>
            <div class="w-px h-10 bg-slate-200"></div>
            <div class="flex flex-col items-center">
              <span class="text-3xl font-bold text-slate-700">¥{{ coachInfo.hourlyRate || 0 }}</span>
              <span class="text-[10px] font-bold text-slate-400 uppercase tracking-wider mt-1">课时费</span>
            </div>
          </div>
        </div>
      </div>

      <div class="slate-tabs-wrapper">
        <el-tabs v-model="activeTab" class="slate-tabs">
          <el-tab-pane name="group">
            <template #label>
              <div class="tab-pill">
                <el-icon class="tab-icon"><Calendar /></el-icon>
                <span>团课</span>
              </div>
            </template>
            <div class="mt-8">
              <GroupClassPanel />
            </div>
          </el-tab-pane>

          <el-tab-pane name="pt">
            <template #label>
              <div class="tab-pill">
                <el-icon class="tab-icon"><User /></el-icon>
                <span>私教</span>
              </div>
            </template>
            <div class="mt-8">
              <PersonalTrainingPanel />
            </div>
          </el-tab-pane>

          <el-tab-pane name="chat">
            <template #label>
              <div class="tab-pill">
                <el-icon class="tab-icon"><ChatDotRound /></el-icon>
                <span>消息</span>
              </div>
            </template>
            <div class="mt-8">
              <ChatPanel />
            </div>
          </el-tab-pane>

          <el-tab-pane name="profile">
            <template #label>
              <div class="tab-pill">
                <el-icon class="tab-icon"><Setting /></el-icon>
                <span>我的</span>
              </div>
            </template>
            <div class="mt-8">
              <CoachProfilePanel @refresh="fetchCoachInfo" />
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/modules/user'
import { ElMessageBox } from 'element-plus'
import { Monitor, SwitchButton, Calendar, User, Setting, ChatDotRound } from '@element-plus/icons-vue'
import { getCoachInfo } from '@/api/coach'

// 引入子组件
import GroupClassPanel from './components/GroupClassPanel.vue'
import PersonalTrainingPanel from './components/PersonalTrainingPanel.vue'
import CoachProfilePanel from './components/CoachProfilePanel.vue'
import ChatPanel from './components/ChatPanel.vue'

const router = useRouter()
const userStore = useUserStore()
const activeTab = ref('group')
const coachInfo = ref<any>({})

// 计算属性：将逗号分隔的字符串转换为数组
const specialtyTags = computed(() => {
  if (coachInfo.value.specialties) {
    return coachInfo.value.specialties.split(/[,，]/).filter((s: string) => s.trim())
  }
  return []
})

const fetchCoachInfo = async () => {
  try {
    const res: any = await getCoachInfo()
    if (res) {
      coachInfo.value = res
    }
  } catch (e) {
    console.error(e)
  }
}

onMounted(() => {
  fetchCoachInfo()
})

const handleLogout = () => {
  ElMessageBox.confirm('确定退出登录吗？', '提示', {
    confirmButtonText: '退出',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    userStore.logout()
    router.push('/login?redirect=/course/coach')
  })
}
</script>

<style lang="scss">
.slate-tabs {
  .el-tabs__header {
    background: transparent;
    margin-bottom: 0;

    .el-tabs__nav-wrap::after { display: none; }
    .el-tabs__active-bar { display: none; }

    .el-tabs__nav {
      display: inline-flex;
      background: #f1f5f9; /* slate-100 */
      padding: 4px;
      border-radius: 99px;
    }

    .el-tabs__item {
      padding: 0 24px !important;
      height: 40px;
      line-height: 40px;
      border-radius: 99px;
      color: #94a3b8; /* slate-400 */
      font-weight: 600;
      font-size: 14px;
      transition: all 0.2s ease;
      border: none !important;

      &.is-active {
        background: white;
        color: #475569; /* slate-600 */
        box-shadow: 0 2px 4px rgba(0,0,0,0.05);
      }

      .tab-pill {
        display: flex;
        align-items: center;
        gap: 8px;
      }
    }
  }

  .el-tabs__content {
    overflow: visible;
  }
}
</style>