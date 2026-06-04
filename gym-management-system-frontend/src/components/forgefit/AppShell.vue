<template>
  <main class="ff-app-shell">
    <slot />
  </main>
</template>

<style scoped>
.ff-app-shell {
  min-height: 100vh;
  color: var(--ff-text, #f3f1e8);
  background:
    radial-gradient(circle at 14% 0%, rgba(184, 255, 44, 0.12), transparent 28%),
    radial-gradient(circle at 92% 10%, rgba(255, 106, 26, 0.1), transparent 30%),
    linear-gradient(135deg, #090a08 0%, #11140f 56%, #070806 100%);
}
</style>
