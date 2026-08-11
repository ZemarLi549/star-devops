<template>
  <div class="label-template">
    <div style="display: flex; gap: 8px">
      <SelectorWithLabel
        v-model="currentMonitorType.source"
        @change="handleMonitorChange"
        :options="monitorTypeOptions"
        placeholder="请选择"
        clearable
        defaultLabelWidth
      >
        <template v-slot:label>监控系统</template>
      </SelectorWithLabel>

      <el-select
        v-model="currentMonitorType.cate"
        placeholder="Select"
        style="width: 100px"
        @change="handleMonitorChange"
        v-if="currentMonitorType.source === StarTraceMonitor"
      >
        <el-option
          v-for="item in cateOptions"
          :key="item.value"
          :label="item.label"
          :value="item.value"
        />
      </el-select>
    </div>
    <div class="template-container">
      <div
        v-if="currentMonitorType.source === undefined"
        class="empty-container"
      >
        <img src="@/assets/images/noData.png" alt="暂无数据" />
        <div class="text">请在上方先选择监控系统类型</div>
      </div>
      <div class="empty-container" v-else-if="!template">
        <img src="@/assets/images/noData.png" alt="暂无数据" />
        <div class="text">该监控未提供模板，您可以自己完成添加进行校验</div>
        <el-button
          type="primary"
          style="margin-top: 8px"
          @click="emits('create')"
        >
          添加模板
        </el-button>
      </div>
      <vue-json-pretty
        v-else
        class="label-json-pretty"
        :data="template"
        @nodeClick="handleNodeClick"
        @bracketsClick="handleBracketsClick"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, watch } from "vue";
import SelectorWithLabel from "@/components/SelectorWithLabel.vue";
import { useAlarmStore } from "@/stores/modules/alarm";
import { monitorTypeOptions, cateOptions, StarTraceMonitor } from "../constant";
const emits = defineEmits(["create"]);
const alarmStore = useAlarmStore();
const {
  setMonitorType,
  setCurrentTemplate,
  requestTemplate,
  clearValidResult,
  setGenerateExp,
} = alarmStore;
const template = ref(null);
const currentMonitorType = reactive({
  source: undefined,
  cate: 1,
});
setCurrentTemplate(template.value);
setMonitorType(currentMonitorType);

const handleMonitorChange = () => {
  setMonitorType(currentMonitorType);
  requestTemplate();
  clearValidResult();
};
const isBracketsClick = ref(false); // 折叠也会触发nodeClick, 通过变量进行过滤
const handleNodeClick = (node) => {
  if (isBracketsClick.value) return;
  setGenerateExp(node.path.replace(/root/, "$"));
};
let timer;
const handleBracketsClick = () => {
  isBracketsClick.value = true;
  timer = setTimeout(() => {
    isBracketsClick.value = false;
    timer && clearTimeout(timer);
  });
};

watch(
  () => alarmStore.currentTemplate,
  () => {
    template.value = alarmStore.currentTemplate
      ? JSON.parse(alarmStore.currentTemplate)
      : alarmStore.currentTemplate;
  }
);
</script>

<style scoped lang="scss">
.label-template {
  flex: 1;
  height: 0;
  display: flex;
  flex-direction: column;
  gap: 16px;
  .template-container {
    flex: 1;
    overflow: auto;
    background: #f7f8fa;
    border: 1px solid #f0f0f0;
    border-radius: 2px;
    padding: 8px 20px;
    .empty-container {
      height: 100%;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      gap: 8px;
      img {
        width: 44px;
        height: 40px;
      }
      .text {
        color: #626471;
        font-size: 14px;
      }
    }
  }
  :deep(.base-selector-with-label) {
    width: 100%;
    .observer-alarm-el-select__wrapper {
      width: 100%;
    }
  }
}
</style>
