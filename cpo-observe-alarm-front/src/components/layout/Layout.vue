<template>
  <div class="observer-alarm-container">
    <div class="observer-alarm-header">
      <Breadcrumb></Breadcrumb>
    </div>
    <div class="observer-alarm-body">
      <router-view></router-view>
    </div>
  </div>
</template>

<script setup>
import Breadcrumb from "./Breadcrumb.vue";
</script>

<style scoped lang="scss">
.observer-alarm-container {
  --base-bg-color: #ecf1f8;
  height: 100%;
  background: var(--base-bg-color);
  .observer-alarm-header {
    height: 48px;
    border-bottom: 1px solid #f0f0f0;
    background-color: var(--base-bg-color);
    align-items: center;
    padding: 0 16px;
    display: flex;
  }
  .observer-alarm-body {
    height: calc(100% - 48px);
    padding: 2px 16px 16px;
    overflow-y: auto;
  }
}
</style>
