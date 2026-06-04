<template>
  <span class="ff-status" :class="tone">
    <i></i>
    <slot />
  </span>
</template>

<script setup lang="ts">
defineProps({ tone: { type: String, default: 'power' } })
</script>

<style scoped>
.ff-status {
  min-height: 26px;
  display: inline-flex;
  align-items: center;
  gap: 7px;
  padding: 0 10px;
  border: 1px solid rgba(184, 255, 44, 0.28);
  border-radius: 999px;
  color: var(--ff-accent-power, #b8ff2c);
  background: rgba(184, 255, 44, 0.08);
  font-size: 12px;
  font-weight: 800;
}

i {
  width: 7px;
  height: 7px;
  border-radius: 999px;
  background: currentColor;
  box-shadow: 0 0 12px currentColor;
}

.heat {
  color: var(--ff-accent-heat, #ff6a1a);
  border-color: rgba(255, 106, 26, 0.32);
  background: rgba(255, 106, 26, 0.08);
}

.cool {
  color: var(--ff-accent-cool, #31d8c8);
  border-color: rgba(49, 216, 200, 0.32);
  background: rgba(49, 216, 200, 0.08);
}

.danger {
  color: var(--ff-danger, #ff3b30);
  border-color: rgba(255, 59, 48, 0.32);
  background: rgba(255, 59, 48, 0.08);
}
</style>
