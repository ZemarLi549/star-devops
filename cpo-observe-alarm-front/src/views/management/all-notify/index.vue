<template>
  <div class="alarm-manage-container card">
    <AlarmTable
      :columns="tableColumn"
      :requestApi="requestApi"
      :searchParam="searchGroup"
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
          v-model="searchGroup.notifyId"
          @change="handleSearch"
          :options="notifyIdOptions"
          placeholder="请选择"
          clearable
          defaultLabelWidth
        >
          <template v-slot:label>通知策略</template>
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
        <SelectorWithLabel
          v-model="searchGroup.status"
          @change="handleSearch"
          :options="statusOptions"
          placeholder="请选择"
          clearable
          defaultLabelWidth
        >
          <template v-slot:label>通知节点</template>
        </SelectorWithLabel>
        <ds-datetime-range-picker
          format="YYYY-MM-DD HH:mm"
          v-model="observeDate"
          type="datetimerange"
          unlink-panels
          range-separator="至"
          placeholder="选择日期"
          start-placeholder="通知日期"
          end-placeholder="通知日期"
          :disabled-date="disabledDate"
          :prefix-icon="Calendar"
          @change="changeDate"
        />
      </template>
    </AlarmTable>
  </div>
  <Drawer ref="drawerElemRef"></Drawer>
</template>

<script setup lang="ts">
import { ref, h, onMounted } from "vue";
import SelectorWithLabel from "@/components/SelectorWithLabel.vue";
import AlarmTable from "@/components/AlarmTable/index.vue";
import { ColumnProps } from "@/components/AlarmTable/interface.ts";
import { Search, Calendar } from "@element-plus/icons-vue";
import { getAlarmLevel, fixTimestamp } from "@/utils/alarm.ts";
import alarmManageApi from "@/request/api/alarm-management/index.ts";
import Drawer from "./drawer.vue";
import { SearchParams } from "@/globals/interface";
import { alarmLevelOptions, statusOptions } from "../constant";
import { useDebounceFn } from "@vueuse/core";
// @ts-ignore
import { DsDatetimeRangePicker } from "ds-datetime-picker-plus";

const drawerElemRef = ref(null); // 抽屉组件
const alarmTableRef = ref(null); // 表格组件

const disabledDate = (time: Date) => {
  return time.getTime() > Date.now();
};

// 查询条件
const observeDate = ref([]);
const searchGroup = ref<SearchParams>({});

const handleSearch = useDebounceFn(() => {
  alarmTableRef.value.resetCurrentChange();
  alarmTableRef.value.getTableList();
}, 300);

const changeDate = () => {
  searchGroup.value.from = observeDate.value[0]
    ? fixTimestamp(observeDate.value[0].getTime(), "start")
    : undefined;
  searchGroup.value.to = observeDate.value[1]
    ? fixTimestamp(observeDate.value[1].getTime(), "end")
    : undefined;
  handleSearch();
};

// 表格配置
const tableColumn = ref<ColumnProps[]>([
  {
    label: "告警ID",
    property: "alarmId",
    formatter: ({ alarmId, noticeId }) => {
      return h(
        "sapn",
        {
          class: "drawer-link-text",
          onClick: () => {
            drawerElemRef.value.openDrawer(noticeId);
          },
        },
        alarmId
      );
    },
    "min-width": 208,
  },
  {
    label: "告警级别",
    property: "level",
    formatter: ({ level }) => {
      return h(
        "div",
        { class: `alarm-level-tag alarm-level-tag-${level}` },
        getAlarmLevel(level)
      );
    },
    "min-width": 128,
  },
  {
    label: "通知策略",
    property: "notifyPolicies",
    formatter: ({ notifyPolicies }) => notifyPolicies || "-",
    "min-width": 208,
  },
  {
    label: "类型",
    property: "cate",
    "min-width": 160,
  },
  {
    label: "通知节点",
    property: "alarmStatus",
    "min-width": 152,
  },
  {
    label: "通知时间",
    property: "notifyTime",
    "min-width": 192,
  },
  {
    label: "是否发送通知",
    property: "isNotify",
    "min-width": 140,
    sortable: "custom",
    formatter: ({ isNotify }) => {
      return isNotify == 1 ? "是" : isNotify == 0 ? "否" : "-";
    },
  },
]);

const notifyIdOptions = ref([]);
onMounted(async () => {
  alarmManageApi.getNotifyPolicy().then((res) => {
    notifyIdOptions.value =
      res.map((item) => {
        return { label: item.notifyName, value: item.notifyId };
      }) || [];
  });
});
const requestApi = (params) => {
  return alarmManageApi.getNoticePage(params);
};
</script>
