<template>
  <button
    class="ff-command-button"
    :class="[`type-${type}`, `size-${size}`, { 'is-block': block, 'is-loading': loading }]"
    :disabled="disabled || loading"
    @click="handleClick"
  >
    <span v-if="loading" class="spinner" aria-hidden="true"></span>
    <slot v-else />
  </button>
</template>

<script setup lang="ts">
type ButtonType = 'primary' | 'secondary' | 'glass' | 'danger' | 'text';
type ButtonSize = 'small' | 'medium' | 'large';

const props = defineProps({
  type: { type: String as () => ButtonType, default: 'primary' },
  size: { type: String as () => ButtonSize, default: 'medium' },
  block: { type: Boolean, default: false },
  disabled: { type: Boolean, default: false },
  loading: { type: Boolean, default: false }
});

const emit = defineEmits(['click']);

const handleClick = (event: MouseEvent) => {
  if (!props.disabled && !props.loading) emit('click', event);
};
</script>

<style scoped lang="scss">
.ff-command-button {
  min-height: 44px;
  border: 1px solid rgba(184, 255, 44, 0.42);
  border-radius: 8px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  position: relative;
  overflow: hidden;
  color: #10120d;
  background: var(--ff-accent-power, #b8ff2c);
  box-shadow: 0 0 30px rgba(184, 255, 44, 0.12);
  cursor: pointer;
  font-family: inherit;
  font-weight: 900;
  letter-spacing: 0;
  user-select: none;
  transition:
    transform var(--ff-motion-fast, 120ms) var(--ff-ease-press, cubic-bezier(0.2, 0.8, 0.2, 1)),
    border-color var(--ff-motion-base, 180ms) var(--ff-ease-enter, cubic-bezier(0.16, 1, 0.3, 1)),
    background var(--ff-motion-base, 180ms) var(--ff-ease-enter, cubic-bezier(0.16, 1, 0.3, 1));

  &::after {
    content: "";
    position: absolute;
    inset: 0;
    background: linear-gradient(110deg, transparent 0%, rgba(255, 255, 255, 0.35) 45%, transparent 70%);
    transform: translateX(-140%);
    transition: transform 420ms var(--ff-ease-enter, cubic-bezier(0.16, 1, 0.3, 1));
    pointer-events: none;
  }

  &:hover:not(:disabled)::after {
    transform: translateX(140%);
  }

  &:active:not(:disabled) {
    transform: scale(0.97);
  }

  &:focus-visible {
    outline: 2px solid var(--ff-accent-power, #b8ff2c);
    outline-offset: 3px;
  }

  &:disabled {
    opacity: 0.55;
    cursor: not-allowed;
  }

  &.size-small { min-height: 36px; padding: 0 12px; font-size: 13px; }
  &.size-medium { padding: 0 18px; font-size: 15px; }
  &.size-large { padding: 0 24px; font-size: 16px; }
  &.is-block { width: 100%; }

  &.type-secondary,
  &.type-glass,
  &.type-text {
    color: var(--ff-text, #f3f1e8);
    background: rgba(32, 35, 29, 0.78);
    border-color: var(--ff-border, #343a30);
  }

  &.type-danger {
    color: var(--ff-text, #f3f1e8);
    background: var(--ff-danger, #ff3b30);
    border-color: rgba(255, 59, 48, 0.65);
  }

  .spinner {
    width: 16px;
    height: 16px;
    border: 2px solid rgba(16, 18, 13, 0.25);
    border-top-color: #10120d;
    border-radius: 999px;
    animation: ffSpin 0.8s linear infinite;
  }
}

@keyframes ffSpin {
  to { transform: rotate(360deg); }
}
</style>
