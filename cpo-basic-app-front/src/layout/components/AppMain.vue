<template>
  <div class="base-application-content">
    <router-view v-slot="{ Component }">
      <keep-alive exclude="login,control">
        <component :is="Component" />
      </keep-alive>
    </router-view>
    <!-- 子应用渲染区域 -->
    <div v-show="showChild" id="sub_app" style="height: 100%"></div>
  </div>
</template>
<script setup lang="ts">
import { start } from "qiankun";
import { onMounted, defineProps, onBeforeUnmount } from "vue";
defineProps({
  showChild: Boolean,
});
onMounted(() => {
  // 工作台首页当前只展示规划内容，禁用全量预取避免提前拉起旧微应用请求。
  start({ prefetch: false }); // 3. 启动微服务
});
onBeforeUnmount(() => {});
</script>

<style lang="scss" scoped>
.base-application-content {
  height: 100%;
  flex: 1;
  width: 0;
  background: #ecf1f8;
}
</style>
