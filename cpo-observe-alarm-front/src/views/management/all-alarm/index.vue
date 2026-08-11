<template>
  <div class="alarm-manage-container card">
    <el-tabs v-model="activeTab" @tab-change="handleTabChange">
      <el-tab-pane
        v-for="(tabItem, index) in TabList"
        :name="index"
        :key="tabItem.key"
      >
        <template #label>
          <span>{{ tabItem.value }}</span>
          <span class="number">({{ alarmDataCount[tabItem.key] }})</span>
        </template>
      </el-tab-pane>
    </el-tabs>
    <AlarmTable
      :columns="tableColumn"
      :requestApi="requestTableData"
      :searchParam="searchGroup"
      isShowHandle
      isShowSetting
      ref="alarmTableRef"
    >
      <template #tableHeaderSearch>
        <el-input
          v-model="searchGroup.alarmId"
          style="width: 240px"
          placeholder="搜索告警ID"
          :prefix-icon="Search"
          @input="handleSearch"
          clearable
        />
        <SelectorWithLabel
          v-model="searchGroup.assignPolicyId"
          @change="handleSearch"
          :options="assignPolicyIdOptions"
          placeholder="请选择"
          clearable
          defaultLabelWidth
        >
          <template v-slot:label>分派策略</template>
        </SelectorWithLabel>
        <SelectorWithLabel
          v-model="searchGroup.level"
          @change="handleSearch"
          :options="alarmLevelOptions"
          placeholder="请选择"
          clearable
          defaultLabelWidth
        >
          <template v-slot:label>告警级别</template>
        </SelectorWithLabel>
        <ds-datetime-range-picker
          format="YYYY-MM-DD HH:mm"
          v-model="observeDate"
          type="datetimerange"
          unlink-panels
          range-separator="至"
          placeholder="选择日期"
          start-placeholder="首次触发日期"
          end-placeholder="首次触发日期"
          :disabled-date="disabledDate"
          :prefix-icon="Calendar"
          @change="changeDate"
          popper-class="data-picker-console"
        />
        <el-checkbox
          v-if="activeTab === AlarmStatus.TOTAL"
          v-model="showNotClosed"
          label="仅看未关闭"
          @change="handleSearch"
        />
      </template>
    </AlarmTable>
  </div>

  <Drawer
    ref="drawerElemRef"
    @close="() => alarmTableRef.getTableList()"
  ></Drawer>
</template>

<script setup lang="ts">
import { ref, h, nextTick, onMounted } from "vue";
import SelectorWithLabel from "@/components/SelectorWithLabel.vue";
import { Search, Calendar } from "@element-plus/icons-vue";
import AlarmTable from "@/components/AlarmTable/index.vue";
import { ColumnProps } from "@/components/AlarmTable/interface.ts";
import Drawer from "./drawer.vue";
import { getAlarmLevel, fixTimestamp, getTaskStatus } from "@/utils/alarm.ts";
import { AlarmStatus } from "@/globals/enums";
import alarmManageApi from "@/request/api/alarm-management/index.ts";
// @ts-ignore
import { DsDatetimeRangePicker } from "ds-datetime-picker-plus";

import { SearchParams, AlarmCount } from "@/globals/interface";
import { TabList, alarmLevelOptions } from "../constant";
import { useDebounceFn } from "@vueuse/core";
import { useRoute } from "vue-router";
import BaseRequest from "@/request/axios/BaseRequest";
import ApiUrl from "@/request/api/alarm-management/ApiUrl";
const route = useRoute();
const alarmTableRef = ref(null); // 表格组件
const drawerElemRef = ref(null); // 抽屉组件

// tab切换
const activeTab = ref(
  route.query.status ? Number(route.query.status) : AlarmStatus.PENDING
);
const alarmDataCount = ref<AlarmCount>({});
const handleTabChange = (tab) => {
  BaseRequest.cancelRequest(ApiUrl.getAlarmPage);
  // 修改column配置
  tableColumn.value =
    tab === AlarmStatus.PENDING
      ? [...commonTableColumn, ...hiddenTableColumn]
      : [...commonTableColumn, ...addTableColumn, ...hiddenTableColumn];
  // 初始化查询参数
  searchGroup.value = {};
  observeDate.value = [];
  showNotClosed.value = false;
  // 重新加载数据
  alarmTableRef.value.resetCurrentChange();
  nextTick(() => {
    alarmTableRef.value.getTableList();
  });
};

const assignPolicyIdOptions = ref([]); //分派策略选项
onMounted(() => {
  alarmManageApi.getAssignPolicy().then((res) => {
    assignPolicyIdOptions.value = Object.keys(res).map((key) => {
      return {
        label: res[key],
        value: key,
      };
    });
  });
});

// 查询条件
const observeDate = ref([]);
const searchGroup = ref<SearchParams>({});
const showNotClosed = ref(false);
const disabledDate = (time: Date) => {
  return time.getTime() > Date.now();
};

const changeDate = () => {
  searchGroup.value.startTime = observeDate.value[0]
    ? fixTimestamp(observeDate.value[0].getTime(), "start")
    : undefined;
  searchGroup.value.endTime = observeDate.value[1]
    ? fixTimestamp(observeDate.value[1].getTime(), "end")
    : undefined;
  handleSearch();
};
const handleSearch = useDebounceFn(() => {
  alarmTableRef.value.resetCurrentChange();
  alarmTableRef.value.getTableList();
}, 300);

// 表格列配置&表格数据查询
const commonTableColumn: ColumnProps[] = [
  {
    label: "告警ID",
    property: "alarmId",
    "min-width": 192,
    formatter: ({ alarmId }) => {
      return h(
        "span",
        {
          class: "drawer-link-text",
          onClick: () => {
            drawerElemRef.value.openDrawer(alarmId);
          },
        },
        alarmId
      );
    },
    disDraggable: true,
  },
  { label: "分派策略", property: "assignRuleNames", "min-width": 216 },
  {
    label: "告警内容",
    property: "content",
    "min-width": 216,
  },
  {
    label: "去重/事件量",
    "min-width": 128,
    formatter: ({ alarmId, uniqueNum, eventNum }) => {
      return h("span", {}, [
        h(
          "span",
          {
            class: "drawer-link-text",
            onClick: () => {
              drawerElemRef.value.openDrawer(alarmId, "uniqueTable");
            },
          },
          `${uniqueNum}/`
        ),
        h(
          "span",
          {
            class: "drawer-link-text",
            onClick: () => {
              drawerElemRef.value.openDrawer(alarmId, "eventTable");
            },
          },
          eventNum
        ),
      ]);
    },
    headerInfo:
      "告警去重指将同一个监控源、同一告警对象在单位时间内产生的多次原始告警进行去重合为同一个告警；事件量指告警事件数量的累计之和",
  },
  {
    label: "告警级别",
    property: "level",
    "min-width": 120,
    formatter: ({ level }) => {
      return h(
        "div",
        { class: `alarm-level-tag alarm-level-tag-${level}` },
        getAlarmLevel(level)
      );
    },
    headerInfo: "当前显示的告警级别为告警事件列表中的最高级别",
  },
  {
    label: "类型",
    property: "cate",
    "min-width": 116,
  },
  {
    label: "任务状态",
    property: "state",
    formatter: ({ state }) => {
      return h(
        "div",
        { class: `task-state-tag task-state-tag-${state}` },
        getTaskStatus(state)
      );
    },
    "min-width": 104,
  },
  {
    label: "告警聚合",
    "min-width": 110,
    property: "aggType",
    sortable: "custom",
  },
  {
    label: "首次触发",
    property: "firstTriggerTime",
    "min-width": 176,
  },
  {
    label: "通知时间",
    property: "notifyTime",
    "min-width": 176,
  },
];
const hiddenTableColumn: ColumnProps[] = [
  {
    label: "告警来源",
    property: "sources",
    show: false,
    "min-width": 120,
  },
  // {
  //   label: "持续时间",
  //   property: "duration",
  //   show: false,
  //   "min-width": 120,
  // },
];
const addTableColumn: ColumnProps[] = [
  {
    label: "更新时间",
    property: "updateTime",
    "min-width": 176,
  },
  {
    label: "更新人",
    property: "updater",
    "min-width": 120,
    formatter: ({ updater }) => {
      return updater || "-";
    },
  },
];
const tableColumn = ref([...commonTableColumn, ...hiddenTableColumn]);

const requestTableData = (params) => {
  alarmManageApi.getAlarmCount(false).then((res) => {
    alarmDataCount.value = res;
  });
  return activeTab.value === AlarmStatus.TOTAL
    ? alarmManageApi.getAlarmPage(params, false, undefined, showNotClosed.value)
    : alarmManageApi.getAlarmPage(params, false, activeTab.value);
};
</script>

<style scoped lang="scss">
:deep(.observer-alarm-el-tabs__nav-wrap::after) {
  height: 1px;
}
</style>
