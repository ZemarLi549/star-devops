<template>
  <div class="label-valid-result">
    <div v-if="!status" class="empty-container">
      <div class="text">等待校验</div>
    </div>
    <div v-else-if="!label" class="empty-container">
      <img src="@/assets/images/noData.png" alt="暂无数据" />
      <div class="text">暂无匹配数据</div>
      <div class="text-info">
        <div>1、请检查Json表达式是否正确</div>
        <div>2、请检查监控系统类型是否正确</div>
      </div>
    </div>
    <vue-json-pretty v-else class="label-json-pretty" :data="label" />
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from "vue";
import { useAlarmStore } from "@/stores/modules/alarm";
const { validResult } = useAlarmStore();
const status = ref(false);
const label = ref(null);
watch(
  () => validResult,
  () => {
    status.value = !!validResult.status;
    label.value = validResult.result
      ? JSON.parse(validResult.result)
      : validResult.result;
  },
  {
    immediate: true,
    deep: true,
  }
);
</script>

<style scoped lang="scss">
.label-valid-result {
  background: #f7f8fa;
  border: 1px solid #f0f0f0;
  border-radius: 2px;
  padding: 8px 20px;
  overflow: auto;
  flex: 1;
  .empty-container {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    height: 100%;
    img {
      width: 48px;
      height: 44px;
      margin-bottom: 8px;
    }
    .text {
      font-size: 14px;
      color: #595959;
    }
    .text-info {
      margin-top: 16px;
      color: #8c8c8c;
    }
  }
}
</style>
