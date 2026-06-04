<template>
  <div class="ff-kpi-chart">
    <slot />
  </div>
</template>

<style scoped>
.ff-kpi-chart {
  min-height: 260px;
  border: 1px solid var(--ff-border, #343a30);
  border-radius: 8px;
  background:
    linear-gradient(90deg, rgba(243, 241, 232, 0.04) 1px, transparent 1px),
    linear-gradient(rgba(243, 241, 232, 0.04) 1px, transparent 1px),
    rgba(11, 12, 10, 0.28);
  background-size: 22px 22px;
}
</style>
