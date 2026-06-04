<template>
  <section class="ff-drawer-form">
    <slot />
  </section>
</template>

<style scoped>
.ff-drawer-form {
  padding: 20px;
  color: var(--ff-text, #f3f1e8);
  border: 1px solid var(--ff-border, #343a30);
  border-radius: 8px;
  background: var(--ff-surface, #151713);
}
</style>
