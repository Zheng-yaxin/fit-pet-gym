<template>
  <section class="ff-data-panel" :class="{ 'is-hoverable': hover }">
    <span class="energy-line" aria-hidden="true"></span>
    <slot />
  </section>
</template>

<script setup lang="ts">
defineProps({ hover: { type: Boolean, default: true } })
</script>

<style scoped>
.ff-data-panel {
  position: relative;
  overflow: hidden;
  border: 1px solid var(--ff-border, #343a30);
  border-radius: 8px;
  color: var(--ff-text, #f3f1e8);
  background:
    linear-gradient(145deg, rgba(32, 35, 29, 0.94), rgba(15, 17, 13, 0.98)),
    repeating-linear-gradient(135deg, rgba(255, 255, 255, 0.025) 0 1px, transparent 1px 8px);
  box-shadow: var(--ff-shadow-panel, 0 24px 80px rgba(0, 0, 0, 0.42));
}

.energy-line {
  position: absolute;
  inset: 0 auto 0 0;
  width: 4px;
  background: linear-gradient(180deg, var(--ff-accent-power, #b8ff2c), var(--ff-accent-heat, #ff6a1a));
  opacity: 0;
  transform: scaleY(0.2);
  transform-origin: bottom;
  transition: opacity 180ms cubic-bezier(0.16, 1, 0.3, 1), transform 180ms cubic-bezier(0.16, 1, 0.3, 1);
}

.is-hoverable:hover {
  border-color: rgba(184, 255, 44, 0.44);
}

.is-hoverable:hover .energy-line {
  opacity: 1;
  transform: scaleY(1);
}
</style>
