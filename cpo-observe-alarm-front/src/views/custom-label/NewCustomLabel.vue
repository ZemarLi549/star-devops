<template>
  <el-container class="create-label-container">
    <el-main class="main-wrapper">
      <div class="title">
        标签配置
        <el-tooltip placement="right">
          <span>
            <FontIcon icon="icon-yiwen" />
          </span>
          <template #content>
            <div style="line-height: 20px">
              <div>
                数据标签是丰富事件数据内容的一种方式，可以通过配置数据标签来丰富接入的时间数据内容
              </div>
              <div>
                1、标签创建后全局生效，可以通过页面右侧模版进行辅助校验是否为您需要的采集的内容
              </div>
              <div>
                2、若填写了标签默认值，则当自定义标签获取数据为空值时，自动填入该固定内容，谨慎填写
              </div>
            </div>
          </template>
        </el-tooltip>
      </div>
      <LabelCreateForm
        :is-edit="isEdit"
        :labelId="labelId"
        style="height: calc(100% - 37px); overflow: auto"
      />
    </el-main>
    <el-aside class="aside">
      <el-steps direction="vertical">
        <el-step style="height: 60%">
          <template v-slot:description>
            <div class="title">告警原始数据模板</div>
            <div class="info">
              选择监控系统后，可查看对应的告警数据模板；你可以通过点击下方告警数据中的Key值快速填写Json表达式
            </div>
            <el-button
              v-if="showUpdate"
              type="primary"
              link
              class="update-btn"
              @click="handleUpdateTemplate"
            >
              更新
            </el-button>
            <LabelTemplate @create="handleCreateTemplate" />
          </template>
        </el-step>
        <el-step style="height: 40%">
          <template v-slot:description>
            <div class="title">表达式校验结果</div>
            <div class="info">
              输入Json表达式点击【表达式校验】查看结果，仅支持单个结果
            </div>
            <LabelValidResult />
          </template>
        </el-step>
      </el-steps>
    </el-aside>
  </el-container>

  <el-dialog
    v-model="dialogVisible"
    :title="`${handleType === 0 ? '添加' : '更新'}告警原始数据模板`"
    width="500"
  >
    <el-input
      :spellcheck="false"
      v-model="newTemplate"
      :rows="18"
      type="textarea"
      placeholder="请在此处粘贴对应监控类型的告警原始数据，仅限Json格式"
    />
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveTemplate">
          保存模板
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, computed } from "vue";
import { useRoute } from "vue-router";
import LabelCreateForm from "./components/LabelCreateForm.vue";
import LabelTemplate from "./components/LabelTemplate.vue";
import LabelValidResult from "./components/LabelValidResult.vue";
import { useAlarmStore } from "@/stores/modules/alarm";
import { StarTraceMonitor } from "./constant";
import customLabelApi from "@/request/api/custom-label";
import { ElMessage, ElMessageBox } from "element-plus";

const route = useRoute();
const alarmStore = useAlarmStore();
const { labelId } = route.params;
const isEdit = ref(labelId !== undefined);
const { requestTemplate } = alarmStore;
const { createLabelTemplate, editLabelTemplate } = customLabelApi;

// 添加/更新模板
const showUpdate = computed(() => { // 非星迹且已有模板
  return (
    alarmStore.currentMonitorType.source !== StarTraceMonitor &&
    alarmStore.currentTemplate
  );
});
const dialogVisible = ref(false);
const newTemplate = ref("");
const handleType = ref(0); // 0 添加模板; 1 更新模板
const handleSaveTemplate = () => {
  try {
    JSON.parse(newTemplate.value);
    const request =
      handleType.value === 0 ? createLabelTemplate : editLabelTemplate;
    request
      .bind(customLabelApi)({
        content: newTemplate.value,
        source: alarmStore.currentMonitorType.source,
      })
      .finally(() => {
        dialogVisible.value = false;
        requestTemplate();
        // ElMessage.success(handleType.value === 0 ? "更新成功" : "更新成功");
      });
  } catch {
    ElMessage.error("仅支持Json格式");
  }
};
const handleUpdateTemplate = () => {
  ElMessageBox.confirm(
    `更新后会覆盖历史模版，但不会对历史自定义标签产生影响，是否确认更新？`,
    "提示",
    {
      type: "warning",
      center: true,
      autofocus: false,
    }
  ).then(async () => {
    handleType.value = 1;
    dialogVisible.value = true;
  });
};
const handleCreateTemplate = () => {
  handleType.value = 0;
  dialogVisible.value = true;
};
</script>

<style lang="scss" scoped>
.create-label-container {
  height: 100%;
  background-color: #fff;

  .main-wrapper {
    padding: 16px 16px 16px 20px;
    flex-basis: 731px;

    .title {
      font-size: 16px;
      font-weight: 550;
      margin-bottom: 12px;
      display: flex;
      align-items: center;
      gap: 8px;
    }
  }

  .aside {
    border-left: 1px solid #f0f0f0;
    position: relative;
    overflow: visible;
    padding: 16px;
    flex: 1;
    flex-basis: 436px;
    max-width: 520px;
  }
}

:deep(.observer-alarm-el-step.is-vertical) {
  .observer-alarm-el-step__line {
    width: 1px;
    top: 30px;
    bottom: 6px;
    left: 11px;
    background: #e5e5e5;
  }

  .observer-alarm-el-step__title {
    display: none;
  }

  .observer-alarm-el-step__description {
    color: #262626;
    padding-right: 0;
    margin-top: 0;
    height: 100%;
    display: flex;
    flex-direction: column;
    line-height: 22px;

    .title {
      font-weight: 550;
      font-size: 14px;
    }

    .info {
      color: #595959;
      margin: 4px 0 12px 0;
    }
  }

  .observer-alarm-el-step__icon.is-text {
    background: #f5f5f5;
    border: 1px solid #e5e5e5;

    .observer-alarm-el-step__icon-inner {
      font-weight: 500;
      color: #262626;
    }
  }

  &:first-child {
    .observer-alarm-el-step__main {
      margin-bottom: 20px;

      .observer-alarm-el-step__description {
        .update-btn {
          position: absolute;
          right: 1px;
        }
      }
    }
  }
}
</style>
