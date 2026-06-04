<template>
  <header class="ff-page-header">
    <div>
      <p v-if="eyebrow" class="eyebrow">{{ eyebrow }}</p>
      <h1>{{ title }}</h1>
      <p v-if="description" class="description">{{ description }}</p>
    </div>
    <div v-if="$slots.actions" class="actions">
      <slot name="actions" />
    </div>
  </header>
</template>

<script setup lang="ts">
defineProps({
  title: { type: String, required: true },
  eyebrow: { type: String, default: '' },
  description: { type: String, default: '' }
})
</script>

<style scoped>
.ff-page-header {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 18px;
  padding: 18px 0;
  border-bottom: 1px solid var(--ff-border, #343a30);
}

.eyebrow {
  margin: 0 0 6px;
  color: var(--ff-accent-power, #b8ff2c);
  font-family: "Bahnschrift", "DIN Alternate", "Rajdhani", sans-serif;
  font-size: 12px;
  font-weight: 900;
}

h1 {
  margin: 0;
  color: var(--ff-text, #f3f1e8);
  font-size: clamp(28px, 5vw, 54px);
  line-height: 1;
}

.description {
  margin: 8px 0 0;
  color: var(--ff-text-secondary, #a7a99e);
}

.actions {
  display: flex;
  align-items: center;
  gap: 10px;
}
</style>
