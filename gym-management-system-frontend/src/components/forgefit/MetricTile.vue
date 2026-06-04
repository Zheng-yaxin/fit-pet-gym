<template>
  <article class="ff-metric">
    <p>{{ label }}</p>
    <strong>{{ value }}</strong>
    <span>{{ hint }}</span>
  </article>
</template>

<script setup lang="ts">
defineProps({
  label: { type: String, required: true },
  value: { type: [String, Number], required: true },
  hint: { type: String, default: '' }
})
</script>

<style scoped>
.ff-metric {
  padding: 18px;
  border: 1px solid var(--ff-border, #343a30);
  border-radius: 8px;
  background: rgba(21, 23, 19, 0.84);
}

p,
span {
  margin: 0;
  color: var(--ff-text-muted, #71766c);
  font-size: 13px;
}

strong {
  display: block;
  margin: 8px 0;
  color: var(--ff-text, #f3f1e8);
  font-family: "Bahnschrift", "DIN Alternate", "Rajdhani", sans-serif;
  font-size: 42px;
  line-height: 1;
  font-variant-numeric: tabular-nums;
}
</style>
