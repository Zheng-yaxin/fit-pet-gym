<template>
  <div class="min-h-screen bg-slate-50 pb-32 font-sans selection:bg-blue-200">
    <header class="sticky top-0 z-40 px-6 py-4 transition-all duration-300">
      <div class="absolute inset-0 bg-white/80 backdrop-blur-xl border-b border-slate-100/50"></div>
      <div class="relative max-w-2xl mx-auto flex items-center justify-center">
        <button
            @click="router.back()"
            class="absolute left-0 group w-10 h-10 rounded-full bg-white flex items-center justify-center text-slate-500 hover:text-slate-800 transition-all shadow-sm ring-1 ring-slate-100 active:scale-95"
        >
          <ArrowLeft :size="20" class="opacity-80 group-hover:opacity-100 transition-opacity" />
        </button>

        <span class="text-[17px] font-bold tracking-tight text-slate-600">个人中心</span>

      </div>
    </header>

    <main class="max-w-2xl mx-auto px-6 pt-8 space-y-6 animate-fade-in">

      <div class="relative group">
        <AppleCard class="relative overflow-hidden !bg-white !rounded-[32px] !shadow-sm p-6 transition-all hover:translate-y-[-2px]">
          <div class="flex items-center gap-6">
            <div class="relative shrink-0">
              <div class="w-24 h-24 rounded-full bg-slate-100 text-slate-400 flex items-center justify-center text-3xl font-bold shadow-inner border-[3px] border-white">
                {{ userInfo?.nickname?.[0]?.toUpperCase() || 'U' }}
              </div>
              <button
                  @click="handleEdit"
                  class="absolute bottom-0 right-0 w-8 h-8 bg-white rounded-full shadow-md border border-slate-100 flex items-center justify-center text-slate-400 hover:text-blue-500 hover:scale-110 transition-all active:scale-90"
              >
                <Edit :size="14" stroke-width="2.5" />
              </button>
            </div>

            <div class="flex-1 min-w-0">
              <h2 class="text-2xl font-bold text-slate-700 tracking-tight truncate">{{ userInfo?.nickname || '尊敬的会员' }}</h2>
              <p class="text-[15px] text-slate-400 font-medium mt-1 tracking-wide">{{ userInfo?.phone || '138****0000' }}</p>

              <div class="flex flex-wrap gap-2 mt-4">
                <div class="px-3 py-1 rounded-full bg-slate-50 border border-slate-100">
                  <span class="text-[11px] font-bold text-slate-500 uppercase tracking-wider">ID {{ userInfo?.id }}</span>
                </div>
                <div class="px-3 py-1 rounded-full bg-blue-50 border border-blue-100 text-blue-600">
                  <span class="text-[11px] font-bold uppercase tracking-wider">
                    {{ userValidCard ? userValidCard.cardType : '普通会员' }}
                  </span>
                </div>
              </div>
            </div>
          </div>
        </AppleCard>
      </div>

      <div class="grid grid-cols-1 md:grid-cols-2 gap-5">
        <AppleCard
            clickable
            @click="handleRecharge"
            class="group relative overflow-hidden !bg-white !rounded-[28px] !shadow-sm p-6 hover:!shadow-md transition-all active:scale-[0.98]"
        >
          <div class="relative z-10 flex flex-col h-full justify-between min-h-[160px]">
            <div class="flex items-center justify-center gap-2 text-slate-400 font-bold text-sm">
              <div class="w-8 h-8 rounded-full bg-slate-50 flex items-center justify-center text-slate-500">
                <Wallet :size="16" />
              </div>
              <span>账户余额</span>
            </div>

            <div class="mt-4 text-center">
              <div class="text-4xl font-bold text-slate-700 tracking-tighter">
                <span class="text-2xl align-top mr-0.5 text-slate-400">¥</span>{{ userInfo?.balance || '0.00' }}
              </div>
            </div>

            <div class="mt-auto pt-4 flex items-center justify-center text-slate-600 text-sm font-bold gap-1 group-hover:text-blue-600 transition-colors">
              立即充值 <ChevronRight :size="16" class="opacity-60" />
            </div>
          </div>
        </AppleCard>

        <div
            @click="handleCard"
            class="relative h-[200px] rounded-[28px] p-6 text-white shadow-lg shadow-slate-200 cursor-pointer transition-all duration-500 hover:-translate-y-1 overflow-hidden group active:scale-[0.98]"
            :class="userValidCard ? 'bg-slate-800' : 'bg-slate-400'"
        >
          <div class="absolute inset-0 bg-gradient-to-tr from-white/5 to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-700"></div>

          <div class="relative z-10 flex flex-col h-full justify-between">
            <div class="flex justify-between items-start">
              <div class="flex items-center gap-2.5">
                <div class="p-2 bg-white/10 backdrop-blur-md rounded-xl border border-white/5">
                  <CreditCard :size="18" />
                </div>
                <span class="font-bold text-sm tracking-widest opacity-90">GYM PASS</span>
              </div>
              <div class="px-2 py-1 rounded-lg bg-white/10 backdrop-blur-md border border-white/5">
                <span class="text-[10px] font-bold font-mono opacity-80">
                  {{ userValidCard ? 'ACTIVE' : 'INACTIVE' }}
                </span>
              </div>
            </div>

            <div class="space-y-1">
              <template v-if="userValidCard">
                <div class="text-3xl font-semibold tracking-tight text-white">
                  {{ userValidCard.cardType }}
                </div>
                <div class="text-sm text-white/60 font-medium">有效期至 {{ formatDate(userValidCard.expireDate) }}</div>
              </template>
              <template v-else>
                <div class="text-2xl font-semibold">暂无会员卡</div>
                <div class="text-sm text-white/60">点击前往办理</div>
              </template>
            </div>

            <div class="flex justify-between items-end">
              <span class="text-[10px] font-bold opacity-40 tracking-[0.2em]">MEMBER EXCLUSIVE</span>
              <div class="flex gap-1">
                <div class="w-1.5 h-1.5 rounded-full bg-white/40"></div>
                <div class="w-1.5 h-1.5 rounded-full bg-white/20"></div>
                <div class="w-1.5 h-1.5 rounded-full bg-white/20"></div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <AppleCard class="!rounded-[28px] overflow-hidden !bg-white !shadow-sm">
        <div class="px-6 py-4 bg-slate-50 border-b border-slate-100">
          <span class="text-[13px] font-bold text-slate-400 uppercase tracking-widest ml-1">详细信息</span>
        </div>
        <div class="divide-y divide-slate-100">
          <div class="flex justify-between items-center px-7 py-5 hover:bg-slate-50 transition-colors group">
            <span class="text-[15px] font-bold text-slate-600">注册时间</span>
            <span class="text-[15px] text-slate-400 font-mono group-hover:text-slate-600 transition-colors">{{ formatDate(userInfo?.createTime) }}</span>
          </div>
          <div class="flex justify-between items-center px-7 py-5 hover:bg-slate-50 transition-colors group">
            <span class="text-[15px] font-bold text-slate-600">性别</span>
            <span class="text-[15px] text-slate-400 group-hover:text-slate-600 transition-colors">{{ userInfo?.gender === 1 ? '男' : '女' }}</span>
          </div>
        </div>
      </AppleCard>

    </main>

    <ProfileEditModal v-model="editVisible" :data="userInfo" @success="fetchLatestInfo" />
    <ProfileRechargeModal v-model="rechargeVisible" :member="userInfo" @success="fetchLatestInfo" />
    <ProfileCardModal v-model="cardVisible" :member="userInfo" @success="fetchLatestInfo" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowLeft, Edit, Wallet, CreditCard, ChevronRight } from 'lucide-vue-next'
import AppleCard from '@/components/ui/AppleCard.vue'
import ProfileEditModal from './components/ProfileEditModal.vue'
import ProfileRechargeModal from './components/ProfileRechargeModal.vue'
import ProfileCardModal from './components/ProfileCardModal.vue'
import { getMemberProfile, getValidCard, type Member, type MemberCard } from '@/api/member'

const router = useRouter()
const userInfo = ref<Member | null>(null)
const userValidCard = ref<MemberCard | null>(null)
const editVisible = ref(false)
const rechargeVisible = ref(false)
const cardVisible = ref(false)

const fetchLatestInfo = async () => {
  try {
    const res: any = await getMemberProfile()
    userInfo.value = res
    if (res?.id) {
      try {
        const card: any = await getValidCard(res.id)
        userValidCard.value = card
      } catch { userValidCard.value = null }
    }
  } catch (e) { console.error(e) }
}

const formatDate = (d?: string) => d?.split(' ')[0] || '--'
const handleEdit = () => editVisible.value = true
const handleRecharge = () => rechargeVisible.value = true
const handleCard = () => cardVisible.value = true

onMounted(fetchLatestInfo)
</script>

<style scoped>
/* 确保 smooth scrolling 和 字体抗锯齿 */
.font-sans {
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

@keyframes fade-in-up {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}
.animate-fade-in-up {
  animation: fade-in-up 0.8s cubic-bezier(0.16, 1, 0.3, 1) forwards;
}
</style>