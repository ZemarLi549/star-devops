<template>
  <div class="alarm-manage-container card">
    <AlarmTable
      :columns="tableColumn"
      :requestApi="requestTableData"
      :searchParam="searchGroup"
      ref="alarmTableRef"
      isShowOpration
      :oprationWidth="160"
      isShowHandle
    >
      <template #tableHeaderSearch>
        <el-input
          v-model="searchGroup.name"
          style="width: 240px"
          placeholder="搜索标签名称"
          :prefix-icon="Search"
          @input="handleSearch"
          clearable
        />
        <SelectorWithLabel
          v-model="searchGroup.status"
          @change="handleSearch"
          :options="labelStatusOptions"
          placeholder="请选择"
          clearable
          defaultLabelWidth
        >
          <template v-slot:label>标签状态</template>
        </SelectorWithLabel>
      </template>
      <template #tableHeaderHandle>
        <el-button type="primary" @click="createLabel">创建标签</el-button>
      </template>
      <template #tableOprationColumn="{ row }">
        <div v-if="row.isDefault">-</div>
        <template v-else>
          <el-button type="primary" link @click="handleEnable(row)">{{
            row.status === 1 ? "停用" : "启用"
          }}</el-button>
          <el-button type="primary" link @click="editLabel(row)"
            >编辑</el-button
          >
          <el-button type="primary" link @click="handleDelete(row)"
            >删除</el-button
          >
        </template>
      </template>
    </AlarmTable>
  </div>
</template>

<script setup lang="ts">
import { ref, h } from "vue";
import SelectorWithLabel from "@/components/SelectorWithLabel.vue";
import AlarmTable from "@/components/AlarmTable/index.vue";
import { ColumnProps } from "@/components/AlarmTable/interface.ts";
import { Search } from "@element-plus/icons-vue";
import customLabelApi from "@/request/api/custom-label/index.ts";
import dayjs from "dayjs";
import { useDebounceFn } from "@vueuse/core";
import { useRouter } from "vue-router";
import { getLabelStatus } from "@/utils/alarm";
import { ElMessage, ElMessageBox } from "element-plus";
interface SearchParams {
  name?: string;
  status?: number;
}
const router = useRouter();
const labelStatusOptions = [
  { value: 1, label: "已启用" },
  { value: 0, label: "已停用" },
];
const alarmTableRef = ref(null); // 表格组件

// 查询条件
const searchGroup = ref<SearchParams>({});

const handleSearch = useDebounceFn(() => {
  alarmTableRef.value.resetCurrentChange();
  alarmTableRef.value.getTableList();
}, 300);

// 表格
const tableColumn = ref<ColumnProps[]>([
  {
    label: "标签名称",
    property: "name",
  },
  {
    label: "标签Key",
    property: "labelKey",
  },
  {
    label: "标签默认值",
    property: "labelValue",
  },
  {
    label: "标签状态",
    property: "status",
    formatter: ({ status }) => {
      return h(
        "div",
        { class: `alarm-state-tag alarm-state-tag-label-${status}` },
        getLabelStatus(status)
      );
    },
  },
  {
    label: "更新时间",
    property: "updateTime",
    formatter: ({ updateTime }) =>
      dayjs(updateTime).format("YYYY-MM-DD HH:mm:ss"),
  },
  {
    label: "更新人",
    property: "updater",
  },
]);
const requestTableData = (params) => {
  return customLabelApi.getCustomLabel(params);
};

// 启用停用
const handleEnableReq = async (row) => {
  try {
    const { disableCustomLabel, enableCustomLabel } = customLabelApi;
    const request = row.status === 1 ? disableCustomLabel : enableCustomLabel;
    await request.bind(customLabelApi)(row.labelId);
    ElMessage.success(row.status === 1 ? "停用成功" : "启用成功");
  } finally {
    alarmTableRef.value.getTableList();
  }
};
const handleEnable = (row) => {
  if (row.status === 1) {
    ElMessageBox.confirm(`确认要停用 ${row.name} 吗？`, "提示", {
      type: "warning",
      center: true,
      autofocus: false,
    }).then(async () => {
      handleEnableReq(row);
    });
  } else {
    handleEnableReq(row);
  }
};

// 删除
const handleDelete = (row) => {
  ElMessageBox.confirm(`确认要删除 ${row.name} 吗？`, "提示", {
    type: "warning",
    center: true,
    autofocus: false,
  }).then(async () => {
    try {
      await customLabelApi.deleteCustomLabel(row.labelId);
      ElMessage.success("删除成功");
    } finally {
      alarmTableRef.value.resetCurrentChange();
      alarmTableRef.value.getTableList();
    }
  });
};

const createLabel = () => {
  router.push("/custom-label/create");
};
const editLabel = (row) => {
  router.push(`/custom-label/edit/${row.labelId}`);
};
</script>
