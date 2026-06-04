<template>
  <el-config-provider :locale="zhCn">
    <div class="forgefit-app-shell">
      <router-view v-slot="{ Component }">
        <transition name="ff-route" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </div>
  </el-config-provider>
</template>

<script setup lang="ts">
import { ElConfigProvider } from 'element-plus'
// @ts-ignore
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
</script>
