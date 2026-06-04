<template>
  <section class="ff-filter-bar">
    <slot />
  </section>
</template>

<style scoped>
.ff-filter-bar {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 12px;
  padding: 14px;
  border: 1px solid var(--ff-border, #343a30);
  border-radius: 8px;
  background: rgba(21, 23, 19, 0.78);
}
</style>
