<template>
  <div class="ff-admin-layout">
    <aside class="ff-sidebar" aria-label="后台导航">
      <div class="brand-block">
        <div class="brand-mark">
          <LayoutDashboard :size="22" />
        </div>
        <div>
          <strong>ForgeFit</strong>
          <span>OPS COMMAND</span>
        </div>
      </div>

      <nav class="sidebar-nav">
        <section v-for="group in navGroups" :key="group.title" class="nav-group">
          <p>{{ group.title }}</p>
          <router-link
            v-for="item in group.items"
            :key="item.path"
            :to="item.path"
            custom
            v-slot="{ navigate, isActive }"
          >
            <a
              class="nav-item"
              :class="{ 'is-active': isActive }"
              :aria-current="isActive ? 'page' : undefined"
              @click="navigate"
            >
              <span class="nav-icon">
                <component :is="item.icon" :size="18" />
              </span>
              <span>{{ item.label }}</span>
              <i aria-hidden="true"></i>
            </a>
          </router-link>
        </section>
      </nav>

      <footer class="operator-card">
        <div class="operator-avatar">A</div>
        <div class="operator-info">
          <strong>管理员</strong>
          <span>System Admin · 在线</span>
        </div>
        <button type="button" aria-label="退出登录" @click="handleLogout">
          <LogOut :size="16" />
        </button>
      </footer>
    </aside>

    <main class="ff-admin-main">
      <router-view v-slot="{ Component }">
        <transition name="ff-panel" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </main>
  </div>
</template>

<script setup lang="ts">
import { markRaw } from 'vue'
import { useUserStore } from '@/store/modules/user'
import { ElMessageBox } from 'element-plus'
import {
  BookOpenCheck,
  CalendarDays,
  ClipboardCheck,
  ClipboardList,
  Dumbbell,
  Gauge,
  LayoutDashboard,
  Library,
  LogOut,
  MapPinned,
  MessageSquare,
  Users,
  WalletCards,
  Wrench
} from 'lucide-vue-next'

const userStore = useUserStore()

const navGroups = [
  {
    title: 'OVERVIEW',
    items: [
      { label: '控制台', path: '/dashboard', icon: markRaw(Gauge) },
      { label: '会员管理', path: '/member/list', icon: markRaw(Users) },
      { label: '会员卡管理', path: '/member/cards', icon: markRaw(WalletCards) }
    ]
  },
  {
    title: 'FACILITIES',
    items: [
      { label: '器材列表', path: '/equipment/manage', icon: markRaw(Dumbbell) },
      { label: '报修处理', path: '/equipment/repair-admin', icon: markRaw(Wrench) }
    ]
  },
  {
    title: 'COURSES',
    items: [
      { label: '课程库', path: '/course/manage', icon: markRaw(Library) },
      { label: '排课管理', path: '/course/schedule', icon: markRaw(CalendarDays) },
      { label: '报名记录', path: '/course/enrollment', icon: markRaw(ClipboardList) }
    ]
  },
  {
    title: 'TRAINING',
    items: [
      { label: '动作库管理', path: '/exercise/admin', icon: markRaw(BookOpenCheck) },
      { label: '训练日志', path: '/training/admin/logs', icon: markRaw(ClipboardCheck) },
      { label: '课后反馈', path: '/feedback/admin', icon: markRaw(MessageSquare) }
    ]
  },
  {
    title: 'OPERATIONS',
    items: [
      { label: '人流热力', path: '/gym/admin/traffic', icon: markRaw(MapPinned) }
    ]
  }
]

const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗?', '退出确认', {
    confirmButtonText: '退出',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => userStore.logout()).catch(() => {})
}
</script>

<style scoped lang="scss">
.ff-admin-layout {
  display: flex;
  width: 100vw;
  height: 100vh;
  overflow: hidden;
  color: var(--ff-text);
  background:
    radial-gradient(circle at 20% 0%, rgba(184, 255, 44, 0.1), transparent 28%),
    linear-gradient(135deg, #090a08, #11140f 55%, #070806);
}

.ff-sidebar {
  width: 292px;
  height: 100%;
  display: flex;
  flex-direction: column;
  border-right: 1px solid var(--ff-border);
  background:
    linear-gradient(180deg, rgba(21, 23, 19, 0.96), rgba(11, 12, 10, 0.98)),
    repeating-linear-gradient(135deg, rgba(255, 255, 255, 0.025) 0 1px, transparent 1px 8px);
}

.brand-block {
  display: flex;
  align-items: center;
  gap: 12px;
  min-height: 92px;
  padding: 22px;
  border-bottom: 1px solid rgba(243, 241, 232, 0.08);
}

.brand-mark {
  width: 44px;
  height: 44px;
  display: grid;
  place-items: center;
  border: 1px solid rgba(184, 255, 44, 0.32);
  border-radius: 8px;
  color: var(--ff-accent-power);
  background: rgba(184, 255, 44, 0.08);
}

.brand-block strong,
.operator-info strong {
  display: block;
  color: var(--ff-text);
  font-size: 18px;
  line-height: 1;
  letter-spacing: 0;
}

.brand-block span,
.operator-info span,
.nav-group p {
  color: var(--ff-text-muted);
  font-family: "Bahnschrift", "DIN Alternate", "Rajdhani", sans-serif;
  font-size: 11px;
  font-weight: 800;
  letter-spacing: 0;
}

.sidebar-nav {
  flex: 1;
  overflow-y: auto;
  padding: 18px 14px;
}

.nav-group + .nav-group {
  margin-top: 22px;
}

.nav-group p {
  margin: 0 0 8px;
  padding: 0 10px;
  color: var(--ff-accent-cool);
}

.nav-item {
  position: relative;
  display: flex;
  align-items: center;
  gap: 12px;
  min-height: 46px;
  margin-bottom: 4px;
  padding: 0 12px;
  overflow: hidden;
  border: 1px solid transparent;
  border-radius: 8px;
  color: var(--ff-text-secondary);
  text-decoration: none;
  transition:
    transform var(--ff-motion-fast) var(--ff-ease-press),
    background var(--ff-motion-base) var(--ff-ease-enter),
    border-color var(--ff-motion-base) var(--ff-ease-enter),
    color var(--ff-motion-base) var(--ff-ease-enter);
}

.nav-item i {
  position: absolute;
  left: 0;
  top: 0;
  width: 4px;
  height: 100%;
  background: linear-gradient(180deg, var(--ff-accent-power), var(--ff-accent-heat));
  opacity: 0;
  transform: scaleY(0.24);
  transform-origin: bottom;
  transition: opacity var(--ff-motion-base) var(--ff-ease-enter), transform var(--ff-motion-base) var(--ff-ease-enter);
}

.nav-icon {
  width: 28px;
  height: 28px;
  display: grid;
  place-items: center;
  border: 1px solid rgba(243, 241, 232, 0.08);
  border-radius: 6px;
  color: inherit;
  background: rgba(0, 0, 0, 0.18);
}

.nav-item:hover,
.nav-item.is-active {
  color: var(--ff-text);
  border-color: rgba(184, 255, 44, 0.28);
  background: rgba(184, 255, 44, 0.08);
}

.nav-item:hover i,
.nav-item.is-active i {
  opacity: 1;
  transform: scaleY(1);
}

.nav-item.is-active .nav-icon {
  color: var(--ff-accent-power);
  border-color: rgba(184, 255, 44, 0.32);
  background: rgba(184, 255, 44, 0.08);
}

.operator-card {
  display: flex;
  align-items: center;
  gap: 10px;
  margin: 14px;
  padding: 12px;
  border: 1px solid var(--ff-border);
  border-radius: 8px;
  background: rgba(32, 35, 29, 0.7);
}

.operator-avatar {
  width: 40px;
  height: 40px;
  display: grid;
  place-items: center;
  border: 1px solid rgba(184, 255, 44, 0.32);
  border-radius: 8px;
  color: var(--ff-accent-power);
  font-weight: 900;
  background: rgba(184, 255, 44, 0.08);
}

.operator-info {
  min-width: 0;
  flex: 1;
}

.operator-card button {
  width: 38px;
  height: 38px;
  display: grid;
  place-items: center;
  border: 1px solid var(--ff-border);
  border-radius: 8px;
  color: var(--ff-text-secondary);
  background: rgba(0, 0, 0, 0.18);
}

.operator-card button:hover {
  color: var(--ff-danger);
  border-color: rgba(255, 59, 48, 0.45);
}

.ff-admin-main {
  flex: 1;
  min-width: 0;
  height: 100%;
  overflow: auto;
  background:
    radial-gradient(circle at 80% 0%, rgba(255, 106, 26, 0.08), transparent 28%),
    transparent;
}

.ff-panel-enter-active {
  transition: opacity var(--ff-motion-panel) var(--ff-ease-enter), transform var(--ff-motion-panel) var(--ff-ease-enter);
}

.ff-panel-leave-active {
  transition: opacity 160ms var(--ff-ease-exit), transform 160ms var(--ff-ease-exit);
}

.ff-panel-enter-from {
  opacity: 0;
  transform: translateY(14px);
}

.ff-panel-leave-to {
  opacity: 0;
  transform: translateY(-8px);
}

@media (max-width: 900px) {
  .ff-sidebar {
    width: 88px;
  }

  .brand-block {
    justify-content: center;
    padding-inline: 12px;
  }

  .brand-block > div:last-child,
  .nav-item span:last-of-type,
  .operator-info {
    display: none;
  }

  .nav-item {
    justify-content: center;
    padding: 0;
  }

  .operator-card {
    justify-content: center;
  }
}
</style>
