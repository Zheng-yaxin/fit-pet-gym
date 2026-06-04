<template>
  <button class="ff-theme-chip" type="button" aria-label="ForgeFit 深色主题已启用">
    <span></span>
    ForgeFit OS
  </button>
</template>

<style scoped>
.ff-theme-chip {
  min-height: 36px;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 0 12px;
  border: 1px solid rgba(184, 255, 44, 0.3);
  border-radius: 999px;
  color: var(--ff-accent-power, #b8ff2c);
  background: rgba(184, 255, 44, 0.08);
  font-weight: 800;
}

.ff-theme-chip span {
  width: 8px;
  height: 8px;
  border-radius: 999px;
  background: var(--ff-accent-power, #b8ff2c);
  box-shadow: 0 0 14px rgba(184, 255, 44, 0.6);
}
</style>
