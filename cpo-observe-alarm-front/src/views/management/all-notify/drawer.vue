<template>
  <el-drawer
    v-model="drawerVisible"
    modal-class="alarm-model-drawer"
    :open="initDrawerData"
    size="800"
    destroy-on-close
  >
    <template #header>
      <div class="drawer-title">所有通知详情</div>
    </template>
    <template #default>
      <ul class="flex-v" style="gap: 16px; overflow: auto; padding: 20px">
        <li v-for="item in Object.keys(keyMap)" class="info-row">
          <div class="info-key">{{ keyMap[item] }}</div>
          <template v-if="item === 'level'">
            <div class="info-value">
              <div
                :class="`alarm-level-tag alarm-level-tag-${notifyInfo[item]}`"
              >
                {{ getAlarmLevel(notifyInfo[item]) }}
              </div>
            </div>
          </template>
          <template v-else-if="item === 'status'">
            <div class="info-value">
              {{
                statusOptions.find((v) => v.value == notifyInfo[item])?.label
              }}
            </div>
          </template>
          <template v-else-if="item === 'notifyInfos'">
            <div
              class="info-value flex-v"
              style="gap: 8px"
              v-if="notifyInfo[item]?.length"
            >
              <div class="info-value-rule" v-for="rule in notifyInfo[item]">
                <div class="rule-name" @click="goNoticePolice(rule.notifyId)">
                  {{ rule.notifyName }}
                </div>
                <div class="rule-remark">
                  {{ rule.notifyRemark }}
                </div>
              </div>
            </div>
            <div v-else>
              <div class="info-value">-</div>
            </div>
          </template>
          <template v-else-if="item === 'alarmId'">
            <div class="info-value" style="display: flex; align-items: center">
              <span style="margin-right: 20px">{{ notifyInfo[item] }}</span>
              <span class="copy-icon" @click="handleCopy(notifyInfo[item])">
                <DocumentCopy />
              </span>
            </div>
          </template>
          <template v-else-if="item === 'isNotify'">
            {{
              notifyInfo[item] == 1 ? "是" : notifyInfo[item] == 0 ? "否" : "-"
            }}
          </template>
          <template v-else>
            <div class="info-value">{{ notifyInfo[item] || "-" }}</div>
          </template>
        </li>
      </ul>
    </template>
  </el-drawer>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { getAlarmLevel, goNoticePolice, handleCopy } from "@/utils/alarm.ts";
import alarmManageApi from "@/request/api/alarm-management/index.ts";
import { BaseInfo } from "@/globals/interface";
import { DocumentCopy } from "@element-plus/icons-vue";
import { statusOptions } from "../constant.ts";
const noticeId = ref();
const notifyInfo = ref<BaseInfo>({});
const keyMap = ref({
  alarmId: "告警ID",
  level: "告警级别",
  cate: "类型",
  sources: "告警来源",
  notifyTime: "通知时间",
  status: "通知节点",
  isNotify: "是否发送通知",
  notifyChannel: "通知渠道",
  notifyInfos: "通知策略",
});

const drawerVisible = ref(false);
const openDrawer = (id) => {
  noticeId.value = id;
  drawerVisible.value = true;
  initDrawerData();
};

const initDrawerData = () => {
  alarmManageApi.getNoticeDetail(noticeId.value).then((res) => {
    notifyInfo.value = res;
  });
};

defineExpose({
  openDrawer,
});
</script>
