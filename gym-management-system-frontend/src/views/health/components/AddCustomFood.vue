<template>
  <section class="ff-food-form">
    <header class="form-header">
      <p>FOOD LIBRARY</p>
      <h3>新增食物</h3>
      <span>按 100g 录入营养数据</span>
    </header>

    <div class="form-grid">
      <label class="field field-wide">
        <span>食物名称</span>
        <input v-model="form.name" placeholder="例如: 煮玉米" />
      </label>

      <label class="field">
        <span>热量 · kcal</span>
        <input v-model.number="form.calories" type="number" placeholder="0" />
      </label>

      <label class="field">
        <span>蛋白质 · g</span>
        <input v-model.number="form.protein" type="number" placeholder="0" />
      </label>

      <label class="field">
        <span>脂肪 · g</span>
        <input v-model.number="form.fat" type="number" placeholder="0" />
      </label>

      <label class="field">
        <span>碳水 · g</span>
        <input v-model.number="form.carbohydrate" type="number" placeholder="0" />
      </label>
    </div>

    <footer class="action-area">
      <button type="button" class="ghost-btn" @click="$emit('cancel')">取消</button>
      <button type="button" class="power-btn" :disabled="!isValid" @click="handleSubmit">
        添加到库
      </button>
    </footer>
  </section>
</template>

<script setup lang="ts">
import { reactive, computed } from 'vue'
import { addCustomFood, type Food } from '@/api/health'
import { ElMessage } from 'element-plus'

const emit = defineEmits(['cancel', 'success'])

const form = reactive<Food>({
  name: '',
  calories: undefined as any,
  protein: undefined as any,
  fat: undefined as any,
  carbohydrate: undefined as any
})

const isValid = computed(() => form.name.length > 0 && form.calories > 0)

const handleSubmit = async () => {
  try {
    const res = await addCustomFood(form)
    const newFood = { ...form, id: (res as any)?.id || Date.now() }
    ElMessage.success('自定义食物已添加')
    emit('success', newFood)
  } catch (e) {
    console.error(e)
  }
}
</script>

<style scoped lang="scss">
.ff-food-form {
  padding: 22px;
  color: var(--ff-text);
  font-family: var(--ff-font-ui);
  border: 1px solid var(--ff-border);
  border-radius: 8px;
  background:
    linear-gradient(145deg, rgba(32, 35, 29, 0.94), rgba(15, 17, 13, 0.98)),
    repeating-linear-gradient(135deg, rgba(255, 255, 255, 0.025) 0 1px, transparent 1px 8px);
}

.form-header {
  margin-bottom: 18px;
}

.form-header p {
  margin: 0 0 6px;
  color: var(--ff-accent-cool);
  font-family: var(--ff-font-metric);
  font-size: 11px;
  font-weight: 900;
}

.form-header h3 {
  margin: 0;
  font-size: 22px;
  font-weight: 900;
}

.form-header span {
  display: block;
  margin-top: 6px;
  color: var(--ff-text-secondary);
  font-size: 13px;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.field {
  display: grid;
  gap: 8px;
  padding: 12px;
  border: 1px solid var(--ff-border);
  border-radius: 8px;
  background: rgba(41, 45, 37, 0.72);
}

.field-wide {
  grid-column: 1 / -1;
}

.field span {
  color: var(--ff-text-muted);
  font-size: 12px;
  font-weight: 800;
}

.field input {
  width: 100%;
  min-height: 44px;
  border: 0;
  outline: 0;
  color: var(--ff-text);
  background: transparent;
  font-size: 16px;
  font-weight: 700;
}

.field input[type="number"] {
  font-family: var(--ff-font-metric);
  font-variant-numeric: tabular-nums;
}

.field input::placeholder {
  color: var(--ff-text-muted);
}

.action-area {
  display: flex;
  gap: 12px;
  margin-top: 18px;
}

.ghost-btn,
.power-btn {
  flex: 1;
  min-height: 46px;
  border-radius: 8px;
  font-weight: 900;
  cursor: pointer;
}

.ghost-btn {
  color: var(--ff-text);
  border: 1px solid var(--ff-border);
  background: rgba(32, 35, 29, 0.78);
}

.power-btn {
  color: #10120d;
  border: 1px solid rgba(184, 255, 44, 0.42);
  background: var(--ff-accent-power);
}

.power-btn:disabled {
  opacity: 0.45;
  cursor: not-allowed;
}

.ghost-btn:active,
.power-btn:active:not(:disabled) {
  transform: scale(0.97);
}
</style>

