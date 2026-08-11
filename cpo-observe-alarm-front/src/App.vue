<template>
  <el-config-provider namespace="observer-alarm-el" :locale="zhCn">
    <router-view v-slot="{ Component }">
      <component :is="Component" />
    </router-view>
  </el-config-provider>
</template>

<script setup lang="ts">
import zhCn from "element-plus/dist/locale/zh-cn.mjs";
</script>

<style scoped></style>
<style>
#app {
  color: #262626;
  height: 100%;
}
</style>
