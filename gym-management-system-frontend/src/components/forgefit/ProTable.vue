<template>
  <div class="ff-pro-table">
    <slot />
  </div>
</template>

<style scoped>
.ff-pro-table {
  overflow: auto;
  border: 1px solid var(--ff-border, #343a30);
  border-radius: 8px;
  background: rgba(21, 23, 19, 0.78);
}
</style>
