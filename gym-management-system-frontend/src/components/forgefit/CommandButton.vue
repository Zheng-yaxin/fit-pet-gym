<template>
  <button class="ff-command" :class="variant" :disabled="disabled || loading" type="button">
    <span v-if="loading" class="spinner"></span>
    <slot v-else />
  </button>
</template>

<script setup lang="ts">
defineProps({
  variant: { type: String, default: 'primary' },
  loading: { type: Boolean, default: false },
  disabled: { type: Boolean, default: false }
})
</script>

<style scoped>
.ff-command {
  min-height: 44px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 0 18px;
  border: 1px solid rgba(184, 255, 44, 0.42);
  border-radius: 8px;
  color: #10120d;
  background: var(--ff-accent-power, #b8ff2c);
  font-weight: 900;
  cursor: pointer;
}

.secondary {
  color: var(--ff-text, #f3f1e8);
  border-color: var(--ff-border, #343a30);
  background: rgba(32, 35, 29, 0.82);
}

.danger {
  color: var(--ff-text, #f3f1e8);
  border-color: rgba(255, 59, 48, 0.55);
  background: var(--ff-danger, #ff3b30);
}

.ff-command:active { transform: scale(0.97); }
.ff-command:disabled { opacity: 0.55; cursor: not-allowed; }

.spinner {
  width: 16px;
  height: 16px;
  border: 2px solid rgba(16, 18, 13, 0.25);
  border-top-color: #10120d;
  border-radius: 999px;
  animation: spin 0.8s linear infinite;
}

@keyframes spin { to { transform: rotate(360deg); } }
</style>
