<template>
  <el-drawer
    v-model="drawerVisible"
    modal-class="alarm-model-drawer"
    @close="emits('close')"
    size="800"
    destroy-on-close
  >
    <template #header>
      <div class="drawer-title">屏蔽告警详情</div>
    </template>
    <template #default>
      <el-tabs v-model="activeName" @tab-change="handleTabChange">
        <el-tab-pane label="基本信息" name="baseInfo">
          <ul class="flex-v" style="gap: 16px; overflow: auto">
            <li v-for="item in Object.keys(keyMap)" class="info-row">
              <div class="info-key">{{ keyMap[item] }}</div>
              <template v-if="item === 'level'">
                <div class="info-value">
                  <div
                    :class="`alarm-level-tag alarm-level-tag-${baseInfo[item]}`"
                  >
                    {{ getAlarmLevel(baseInfo[item]) }}
                  </div>
                </div>
              </template>
              <template v-else-if="item === 'state'">
                <div class="info-value">
                  <div :class="`alarm-state-tag alarm-state-tag-${baseInfo[item]}`">
                    {{ getAlarmStatus(baseInfo[item]) }}
                  </div>
                </div>
              </template>
              <template v-else-if="item === 'cate'">
                <div class="info-value">
                  {{ getAlarmType(baseInfo[item]) }}
                </div>
              </template>
              <template v-else-if="item === 'blockRuleName'">
                <div class="info-value flex-v" style="gap: 8px">
                  <div class="info-value-rule">
                    <div
                      class="rule-name"
                      @click="goShieldRule(baseInfo.blockId)"
                    >
                      {{ baseInfo.blockRuleName }}
                    </div>
                    <div class="rule-remark">
                      {{ baseInfo.blockRuleRemark }}
                    </div>
                  </div>
                </div>
              </template>
              <template v-else-if="item === 'content'">
                <div class="info-value content">
                  {{ baseInfo[item] }}
                </div>
              </template>
              <template v-else-if="item === 'alarmId'">
                <div class="info-value alarm-id-info">
                  <span class="alarm-id">{{ baseInfo[item] }}</span>
                  <span
                    class="copy-icon"
                    @click="handleCopy(baseInfo[item])"
                  >
                    <DocumentCopy />
                  </span>
                  <span class="label-link" @click="handleShowLabels">标签</span>
                </div>
              </template>
              <template v-else>
                <div class="info-value">{{ baseInfo[item] ?? '-' }}</div>
              </template>
            </li>
          </ul>
        </el-tab-pane>
        <EventTabPane ref="eventTabRef" :id="alarmId"></EventTabPane>
      </el-tabs>
    </template>
  </el-drawer>

  <el-dialog
    v-model="dialogVisible"
    title="查看标签"
    width="500"
    modal-class="label-event-dialog"
  >
    <div class="label-container">
      <vue-json-pretty class="label-json-pretty" :data="content" />
    </div>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, nextTick } from "vue";
import {
  getAlarmLevel,
  getAlarmStatus,
  getAlarmType,
  goShieldRule,
  handleCopy
} from "@/utils/alarm.ts";
import { BaseInfo } from "@/globals/interface";
import alarmManageApi from "@/request/api/alarm-management/index.ts";
import { DocumentCopy } from "@element-plus/icons-vue";
import EventTabPane from "../components/EventUniqueTabPane.vue"
const emits = defineEmits(['close'])
const alarmId = ref();
const activeName = ref("baseInfo");
const baseInfo = ref<BaseInfo>({});
const keyMap = ref({
  alarmId: "告警ID",
  state: "告警状态",
  eventNum: "事件量",
  firstTriggerTime: "首次触发",
  blockTime: "屏蔽时间",
  level: "告警级别",
  cate: "类型",
  source: "告警来源",
  blockRuleName: "屏蔽规则",
  content: "告警内容",
});
const drawerVisible = ref(false);
const openDrawer = (id, activePane = "baseInfo") => {
  alarmId.value = id;
  activeName.value = activePane;
  drawerVisible.value = true;
  initDrawerData();
};

const handleTabChange = () => {
  eventTabRef.value.clearSearchValue()
}

const eventTabRef = ref(null)
const initDrawerData = () => {
  alarmManageApi.getBlockDetail(alarmId.value).then((res) => {
    baseInfo.value = res;
  });
  nextTick(() => {
    eventTabRef.value.getTableList()
  })
};

const content = ref({});
const dialogVisible = ref(false);
const handleShowLabels = () => {
  alarmManageApi.getUniqueLabels(alarmId.value).then((res) => {    
    content.value = res ? JSON.parse(res) : res;
    dialogVisible.value = true;
  });
};

defineExpose({
  openDrawer,
});
</script>
