<template>
  <el-drawer
    v-model="drawerVisible"
    modal-class="alarm-model-drawer"
    @close="emits('close')"
    size="800"
    destroy-on-close
  >
    <template #header>
      <div class="drawer-title">告警详情</div>
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
                  <div :class="`task-state-tag task-state-tag-${baseInfo[item]}`">
                    {{ getTaskStatus(baseInfo[item]) }}
                  </div>
                </div>
              </template>
              <template v-else-if="item === 'uniqueAgg'">
                <div class="info-value">{{ `${baseInfo.uniqueNum}/${baseInfo.eventNum}` }}</div>
              </template>
              <template v-else-if="item === 'assignRuleInfos'">
                <div class="info-value flex-v" style="gap: 8px">
                  <div class="info-value-rule" v-for="rule in baseInfo[item]">
                    <div
                      class="rule-name"
                      @click="handleClick(rule)"
                    >
                      {{ rule.assignRuleName }}
                    </div>
                    <div class="rule-remark">
                      {{ rule.assignRuleRemark }}
                    </div>
                  </div>
                </div>
              </template>
              <template v-else-if="item === 'content'">
                <div class="info-value">
                  <div class="info-value-rule">
                    <div
                      class="rule-name"
                      @click="()=>activeName = 'uniqueTable'"
                    >
                      查看内容
                    </div>
                    <div class="rule-remark">
                      {{ baseInfo[item] }}
                    </div>
                  </div>
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
                </div>
              </template>
              <template v-else>
                <div class="info-value">{{ baseInfo[item] || '-' }}</div>
              </template>
            </li>
            <!-- 更新信息 -->
            <template v-if="baseInfo.state !== AlarmStatus.PENDING">
              <li class="info-row updater-row">
                <div class="info-key">更新人</div>
                <div class="info-value">{{ baseInfo.updater || '-'  }}</div>
              </li>
              <li class="info-row">
                <div class="info-key">更新时间</div>
                <div class="info-value">{{ baseInfo.updateTime || '-' }}</div>
              </li>
            </template>
            <!-- 关闭原因 -->
            <template v-if="baseInfo.state === AlarmStatus.CLOSED">
              <li class="info-row">
                <div class="info-key">关闭原因</div>
                <div class="info-value content">{{ baseInfo.closeReason || '-'  }}</div>
              </li>
            </template>
            <!-- 认领原因 -->
            <template v-if="baseInfo.state === AlarmStatus.DEALING">
              <li class="info-row">
                <div class="info-key">认领原因</div>
                <div class="info-value content">{{ baseInfo.ackReason || '-'  }}</div>
              </li>
            </template>
          </ul>
        </el-tab-pane>
        <UniqueTabPane ref="uniqueTabRef" :id="alarmId" @show-event="handleShowEvent"></UniqueTabPane>
        <EventTabPane ref="eventTabRef" :id="alarmId" ></EventTabPane>
      </el-tabs>
    </template>
    <template #footer v-if="baseInfo.state !== AlarmStatus.CLOSED">
      <div style="flex: auto">
        <el-button @click="closeDrawer">取消</el-button>
        <el-button type="primary" @click="handleAlarmClose">手动关闭</el-button>
        <el-button
          type="primary"
          @click="handleAlarmAck"
          v-if="baseInfo.state === AlarmStatus.PENDING"
        >
          认领
        </el-button>
      </div>
    </template>
  </el-drawer>
</template>

<script setup lang="ts">
import { ref, nextTick } from "vue";
import {
  getAlarmLevel,
  getTaskStatus,
  goAssignPolice,
  handleCopy
} from "@/utils/alarm.ts";
import { DocumentCopy } from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import { AlarmStatus } from "@/globals/enums";
import { BaseInfo } from "@/globals/interface";
import alarmManageApi from "@/request/api/alarm-management/index.ts";
import UniqueTabPane from "../components/UniqueTabPane.vue"
import EventTabPane from "../components/EventAggTabPane.vue"
const emits = defineEmits(["confirm", "cancel", "close"]);
const alarmId = ref();
const activeName = ref("baseInfo");
const baseInfo = ref<BaseInfo>({});
const keyMap = ref({
  alarmId: "告警ID",
  state: "任务状态",
  duration: "持续时长",
  uniqueAgg: "去重/事件量",
  firstTriggerTime: "首次触发",
  notifyTime: "通知时间",
  level: "告警级别",
  cate: "类型",
  sources: "告警来源",
  assignRuleInfos: "分派策略",
  content: "告警内容",
});

const drawerVisible = ref(false);
const openDrawer = (id, activePane = "baseInfo") => {
  alarmId.value = id;
  activeName.value = activePane;
  drawerVisible.value = true;
  initDrawerData();
};
const closeDrawer = () => {
  drawerVisible.value = false;
};
const handleAlarmAck = () => {
  emits("confirm", [alarmId.value]);
};
const handleAlarmClose = () => {
  emits("cancel", [alarmId.value]);
};

const initDrawerData = () => {
  alarmManageApi.getAlarmDetail(alarmId.value).then((res) => {
    baseInfo.value = res;
  });
  nextTick(() => {
    uniqueTabRef.value.getTableList()
    eventTabRef.value.getTableList()
  })
  
};
const handleClick = (rule) => {
  if (rule.isDeleted) {
    ElMessage({
      message: "分派策略被删除，无法查看",
      type: "warning",
    });
    return
  }
  goAssignPolice(rule.assignRuleId)
}

const handleTabChange = () => {
  eventTabRef.value.clearSearchValue()
  uniqueTabRef.value.clearSearchValue()
}

const handleShowEvent = (id) => {
  activeName.value = 'eventTable'
  eventTabRef.value.setSearchValue(id)
}

const uniqueTabRef = ref(null)
const eventTabRef = ref(null)
defineExpose({
  openDrawer,
  closeDrawer,
});
</script>
