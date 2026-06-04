<template>
  <section class="ff-empty">
    <strong>{{ title }}</strong>
    <p>{{ description }}</p>
    <slot />
  </section>
</template>

<script setup lang="ts">
defineProps({
  title: { type: String, default: '暂无数据' },
  description: { type: String, default: '当前没有可显示的训练或运营记录。' }
})
</script>

<style scoped>
.ff-empty {
  padding: 32px;
  text-align: center;
  color: var(--ff-text-secondary, #a7a99e);
  border: 1px dashed var(--ff-border, #343a30);
  border-radius: 8px;
  background: rgba(21, 23, 19, 0.72);
}

strong {
  display: block;
  color: var(--ff-text, #f3f1e8);
  font-size: 18px;
}

p {
  margin: 8px 0 0;
}
</style>
