<template>
  <main class="ff-error-page">
    <section class="error-panel">
      <div class="status-rail" aria-hidden="true"></div>
      <p class="eyebrow">ROUTE DISCONNECTED</p>
      <h1>404</h1>
      <h2>训练入口未接入</h2>
      <p class="description">
        当前路径没有可用模块，返回指挥台继续处理会员、课程、器材和训练任务。
      </p>

      <button type="button" class="return-btn" @click="router.push('/')">
        返回首页
      </button>
    </section>

    <footer class="footer">ForgeFit OS · Operations Command</footer>
  </main>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'

const router = useRouter()
</script>

<style scoped lang="scss">
.ff-error-page {
  min-height: 100vh;
  display: grid;
  place-items: center;
  position: relative;
  overflow: hidden;
  padding: 32px;
  color: var(--ff-text);
  font-family: var(--ff-font-ui);
  background:
    linear-gradient(rgba(184, 255, 44, 0.08) 1px, transparent 1px),
    linear-gradient(90deg, rgba(184, 255, 44, 0.06) 1px, transparent 1px),
    radial-gradient(circle at 24% 18%, rgba(184, 255, 44, 0.14), transparent 30%),
    radial-gradient(circle at 82% 12%, rgba(255, 106, 26, 0.1), transparent 26%),
    var(--ff-canvas);
  background-size: 56px 56px, 56px 56px, auto, auto, auto;
}

.error-panel {
  width: min(520px, 100%);
  position: relative;
  overflow: hidden;
  padding: 34px;
  border: 1px solid var(--ff-border);
  border-radius: 8px;
  background:
    linear-gradient(145deg, rgba(32, 35, 29, 0.94), rgba(15, 17, 13, 0.98)),
    repeating-linear-gradient(135deg, rgba(255, 255, 255, 0.025) 0 1px, transparent 1px 8px);
  box-shadow: var(--ff-shadow-panel);
  animation: panelIn var(--ff-motion-feature) var(--ff-ease-enter) both;
}

.status-rail {
  position: absolute;
  inset: 0 auto 0 0;
  width: 4px;
  background: linear-gradient(180deg, var(--ff-accent-power), var(--ff-accent-heat));
}

.eyebrow {
  color: var(--ff-accent-cool);
  font-family: var(--ff-font-metric);
  font-size: 12px;
  font-weight: 900;
}

h1 {
  margin-top: 12px;
  font-family: var(--ff-font-metric);
  font-size: clamp(84px, 16vw, 136px);
  line-height: 0.86;
  color: var(--ff-accent-power);
  text-shadow: 0 0 34px rgba(184, 255, 44, 0.2);
}

h2 {
  margin-top: 18px;
  font-size: 28px;
  font-weight: 900;
}

.description {
  margin-top: 12px;
  color: var(--ff-text-secondary);
  line-height: 1.75;
}

.return-btn {
  min-height: 46px;
  margin-top: 28px;
  padding: 0 22px;
  border: 1px solid rgba(184, 255, 44, 0.42);
  border-radius: 8px;
  color: #10120d;
  background: var(--ff-accent-power);
  font-weight: 900;
  cursor: pointer;
}

.return-btn:active {
  transform: scale(0.97);
}

.footer {
  position: absolute;
  left: 32px;
  bottom: 24px;
  color: var(--ff-text-muted);
  font-family: var(--ff-font-metric);
  font-size: 12px;
  font-weight: 800;
}

@keyframes panelIn {
  from {
    opacity: 0;
    transform: translateY(18px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>

