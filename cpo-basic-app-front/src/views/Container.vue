<template>
  <!-- 子应用渲染区域 -->
  <div id="sub_app" style="height: 100%"></div>
</template>
<script setup lang="ts">
import { start } from "qiankun";
import { onMounted } from "vue";
onMounted(() => {
  // 禁用全量预取，避免未进入子应用前触发旧服务请求。
  start({ prefetch: false }); // 3. 启动微服务
});
</script>
