<template>
  <div :class="{'hidden': hidden}" class="pagination-wrapper">
    <div class="ff-pagination-toolbar">
      <el-pagination
          :background="background"
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :layout="layout"
          :page-sizes="pageSizes"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script setup>
import {computed} from 'vue'

const props = defineProps({
  total: {
    required: true,
    type: Number
  },
  page: {
    type: Number,
    default: 1
  },
  limit: {
    type: Number,
    default: 20
  },
  pageSizes: {
    type: Array,
    default() {
      return [10, 20, 30, 50]
    }
  },
  layout: {
    type: String,
    default: 'total, sizes, prev, pager, next, jumper'
  },
  background: {
    type: Boolean,
    default: true
  },
  hidden: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:page', 'update:limit', 'pagination'])

const currentPage = computed({
  get() {
    return props.page
  },
  set(val) {
    emit('update:page', val)
  }
})

const pageSize = computed({
  get() {
    return props.limit
  },
  set(val) {
    emit('update:limit', val)
  }
})

function handleSizeChange(val) {
  emit('pagination', {page: currentPage.value, limit: val})
}

function handleCurrentChange(val) {
  emit('pagination', {page: val, limit: pageSize.value})
}
</script>

<style scoped>
.pagination-wrapper {
  padding: 24px 0;
  display: flex;
  justify-content: center; /* Center for more elegant look, or flex-end if preferred */
}

.pagination-wrapper.hidden {
  display: none;
}

.ff-pagination-toolbar {
  display: inline-flex;
  background: rgba(32, 35, 29, 0.82);
  padding: 8px 16px;
  border-radius: 8px;
  border: 1px solid var(--ff-border, #343a30);
  box-shadow: var(--ff-shadow-panel, 0 24px 80px rgba(0, 0, 0, 0.42));
}

:deep(.el-pagination) {
  --el-pagination-bg-color: transparent;
  --el-pagination-button-bg-color: transparent;
  --el-pagination-hover-color: var(--ff-accent-power, #b8ff2c);
  font-weight: 500;
}

:deep(.el-pagination.is-background .el-pager li:not(.is-disabled).is-active) {
  background-color: var(--ff-accent-power, #b8ff2c);
  color: #10120d;
  border-radius: 6px;
  font-weight: 600;
  box-shadow: 0 0 18px rgba(184, 255, 44, 0.18);
}

:deep(.el-pagination.is-background .el-pager li) {
  background-color: transparent;
  border-radius: 6px;
  min-width: 32px;
  height: 32px;
  line-height: 32px;
  transition: all 0.2s;
}

:deep(.el-pagination.is-background .el-pager li:hover:not(.is-active)) {
  color: var(--ff-accent-power, #b8ff2c);
  background-color: rgba(184, 255, 44, 0.08);
}

:deep(.el-pagination__total) {
  margin-right: 16px;
  color: var(--ff-text-secondary, #a7a99e);
  font-size: 13px;
}
</style>
