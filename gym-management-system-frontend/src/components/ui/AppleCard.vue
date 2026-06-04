<template>
  <div
    class="ff-data-panel"
    :class="{ 'is-hoverable': hover, 'is-clickable': clickable }"
    @click="handleClick"
  >
    <div class="panel-energy" aria-hidden="true"></div>
    <div class="panel-content">
      <slot />
    </div>
  </div>
</template>

<script setup lang="ts">
const props = defineProps({
  hover: { type: Boolean, default: true },
  clickable: { type: Boolean, default: false }
});

const emit = defineEmits(['click']);

const handleClick = (event: MouseEvent) => {
  if (props.clickable) emit('click', event);
};
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
  transition:
    transform var(--ff-motion-base, 180ms) var(--ff-ease-enter, cubic-bezier(0.16, 1, 0.3, 1)),
    border-color var(--ff-motion-base, 180ms) var(--ff-ease-enter, cubic-bezier(0.16, 1, 0.3, 1));
}

.panel-energy {
  position: absolute;
  left: 0;
  top: 0;
  width: 4px;
  height: 100%;
  background: linear-gradient(180deg, var(--ff-accent-power, #b8ff2c), var(--ff-accent-heat, #ff6a1a));
  opacity: 0;
  transform: scaleY(0.2);
  transform-origin: bottom;
  transition:
    opacity var(--ff-motion-base, 180ms) var(--ff-ease-enter, cubic-bezier(0.16, 1, 0.3, 1)),
    transform var(--ff-motion-base, 180ms) var(--ff-ease-enter, cubic-bezier(0.16, 1, 0.3, 1));
}

.panel-content {
  position: relative;
  z-index: 1;
  height: 100%;
}

.is-clickable {
  cursor: pointer;
}

.is-hoverable:hover {
  border-color: rgba(184, 255, 44, 0.44);
  transform: translateY(-3px);
}

.is-hoverable:hover .panel-energy {
  opacity: 1;
  transform: scaleY(1);
}

.is-hoverable:active {
  transform: scale(0.99);
}
</style>
