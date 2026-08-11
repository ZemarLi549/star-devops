<template>
  <div class="alarm-manage-container card">
    <AlarmTable
      :columns="tableColumn"
      :requestApi="requestTableData"
      :searchParam="searchGroup"
      ref="alarmTableRef"
    >
      <template #tableHeaderSearch>
        <el-input
          v-model="searchGroup.blockRuleName"
          style="width: 240px"
          placeholder="搜索屏蔽规则名称"
          :prefix-icon="Search"
          @input="handleSearch"
          clearable
        />
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
        <SelectorWithLabel
          v-model="searchGroup.cate"
          @change="handleSearch"
          :options="alarmCateOptions"
          placeholder="请选择"
          clearable
          defaultLabelWidth
        >
          <template v-slot:label>类型</template>
        </SelectorWithLabel>
        <SelectorWithLabel
          v-model="searchGroup.state"
          @change="handleSearch"
          :options="alarmStateOptions"
          placeholder="请选择"
          clearable
          defaultLabelWidth
        >
          <template v-slot:label>告警状态</template>
        </SelectorWithLabel>
      </template>
    </AlarmTable>
  </div>

  <Drawer
    ref="drawerElemRef"
    @close="() => alarmTableRef.getTableList()"
  ></Drawer>
</template>

<script setup lang="ts">
import { ref, h } from "vue";
import SelectorWithLabel from "@/components/SelectorWithLabel.vue";
import AlarmTable from "@/components/AlarmTable/index.vue";
import { ColumnProps } from "@/components/AlarmTable/interface.ts";
import { Search } from "@element-plus/icons-vue";
import { getAlarmLevel, getAlarmType, getAlarmStatus } from "@/utils/alarm.ts";
import alarmManageApi from "@/request/api/alarm-management/index.ts";
import Drawer from "./drawer.vue";
import {
  alarmLevelOptions,
  alarmCateOptions,
  alarmStateOptions,
} from "../constant";
import { useDebounceFn } from "@vueuse/core";
import { SearchParams } from "@/globals/interface";

const alarmTableRef = ref(null); // 表格组件
const drawerElemRef = ref(null); // 抽屉组件

// 查询条件
const searchGroup = ref<SearchParams>({});

const handleSearch = useDebounceFn(() => {
  alarmTableRef.value.resetCurrentChange();
  alarmTableRef.value.getTableList();
}, 300);

// 表格
const tableColumn = ref<ColumnProps[]>([
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
  },
  { label: "屏蔽规则名称", property: "blockRuleName", "min-width": 204 },
  {
    label: "告警内容",
    property: "content",
    "min-width": 204,
  },
  {
    label: "事件量",
    "min-width": 96,
    property: "unique",
    formatter: ({ alarmId, unique }) => {
      return h(
        "span",
        {
          class: "drawer-link-text",
          onClick: () => {
            drawerElemRef.value.openDrawer(alarmId, "eventTable");
          },
        },
        unique
      );
    },
    headerInfo: "事件量指告警事件数量的累计之和",
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
    // sortable: true,
    // "sort-method": (a, b) => a.level - b.level,
  },
  {
    label: "告警状态",
    property: "state",
    formatter: ({ state }) => {
      return h(
        "div",
        { class: `alarm-state-tag alarm-state-tag-${state}` },
        getAlarmStatus(state)
      );
    },
    "min-width": 120,
  },
  {
    label: "类型",
    property: "cate",
    formatter: ({ cate }) => getAlarmType(cate),
    "min-width": 108,
  },
  {
    label: "首次触发",
    property: "firstTriggerTime",
    "min-width": 176,
  },
  {
    label: "屏蔽时间",
    property: "blockTime",
    "min-width": 176,
  },
]);
const requestTableData = (params) => {
  return alarmManageApi.getBlockPage(params);
};
</script>
