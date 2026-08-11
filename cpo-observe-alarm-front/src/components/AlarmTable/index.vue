<template>
  <div class="alarm-table-container" v-loading="loading">
    <div class="table-header-wrapper" v-if="isShowSearch || isShowHandle">
      <div class="table-header-search" v-if="isShowSearch">
        <!-- 筛选查询组 -->
        <slot name="tableHeaderSearch" />
      </div>
      <div class="table-header-handle" v-if="isShowHandle">
        <!-- 顶部右侧操作组 -->
        <slot name="tableHeaderHandle" />
        <!-- 表格列设置 -->
        <el-popover
          placement="bottom-start"
          :width="200"
          trigger="hover"
          v-if="isShowSetting"
        >
          <template #reference>
            <el-button :icon="Setting" style="width: 32px" />
          </template>
          <div class="column-setting-popover">
            <div class="column-setting-header">
              <el-checkbox
                v-model="checkAll"
                :indeterminate="isIndeterminate"
                @change="handleCheckAllChange"
                label="列展示"
              />
              <el-button type="primary" link @click="handleReset">
                重置
              </el-button>
            </div>
            <VueDraggable
              ref="el"
              v-model="tableColumns"
              :animation="150"
              filter=".disDraggable"
              @move="onMove"
              class="drag-container"
            >
              <div
                v-for="column in tableColumns"
                :key="column.property"
                :class="{ disDraggable: column.disDraggable }"
                class="drag-item"
              >
                <span class="drag-handle">
                  <FontIcon icon="icon-tuozhuai" />
                </span>
                <el-checkbox
                  v-model="column.show"
                  :label="column.label"
                  :disabled="column.disDraggable"
                  @change="handleCheckChange"
                />
              </div>
            </VueDraggable>
          </div>
        </el-popover>
      </div>
    </div>
    <el-table
      :data="tableData"
      @selection-change="handleSelectionChange"
      @sort-change="sortChange"
      class="table-content"
    >
      <template #empty><Empty /></template>
      <!-- 多选列 -->
      <el-table-column v-if="isShowSelection" type="selection" width="55" />
      <el-table-column
        v-for="column in tableColumns.filter((item) => item.show !== false)"
        :key="column.property"
        :label="column.label"
        :property="column.property"
        :width="column.width"
        :min-width="column['min-width']"
        :formatter="column.formatter || defaultFormatter(column)"
        :sortable="column.sortable"
        :sort-method="column['sort-method']"
        :fixed="column.fixed"
        show-overflow-tooltip
      >
        <template v-if="column.headerInfo" #header="scope">
          <div style="display: flex; align-items: center; gap: 4px">
            <span>{{ scope.column.label }}</span>
            <el-tooltip
              effect="dark"
              :content="column.headerInfo"
              placement="right"
            >
              <span class="info-icon">
                <FontIcon icon="icon-yiwen" />
              </span>
            </el-tooltip>
          </div>
        </template>
      </el-table-column>
      <!-- 操作列 -->
      <el-table-column
        v-if="isShowOpration"
        label="操作"
        :width="oprationWidth"
        fixed="right"
      >
        <template #default="{ row, column }">
          <slot name="tableOprationColumn" :row="row" :column="column" />
        </template>
      </el-table-column>
    </el-table>
    <div class="table-footer" v-if="pagination && tableData.length !== 0">
      <el-pagination
        v-model:current-page="pagingData.pageNo"
        v-model:page-size="pagingData.pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :layout="`${
          isShowSelection ? 'slot' : 'total'
        }, ->, prev, pager, next, sizes`"
        :total="pagingData.total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      >
        <template v-if="isShowSelection">
          <div v-if="multipleSelection.length > 0">
            {{ `已选 ${multipleSelection.length} 条数据` }}
          </div>
          <div v-else>
            {{ `共 ${pagingData.total} 条数据` }}
          </div>
        </template>
      </el-pagination>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive, watch } from "vue";
import { Setting } from "@element-plus/icons-vue";
import { ColumnProps } from "./interface";
import { VueDraggable } from "vue-draggable-plus";
import { deepClone } from "@/utils";
import Empty from "@/components/empty/Empty.vue";
// import { AxiosError } from 'axios'

interface TableProps {
  columns: ColumnProps[]; // 列配置项
  requestApi: (params: any) => Promise<any>; // 请求表格数据的api ==> 必传
  pagination?: boolean; // 是否需要分页组件 ==> 非必传（默认为true）
  searchParam?: any; // 查询参数 ==> 非必传（默认为{}）
  isShowSearch?: boolean; // 是否显示数据查询组 ==> 非必传（默认为true）
  isShowHandle?: boolean; // 是否显示表格处理按钮 ==> 非必传（默认为false）
  isShowSelection?: boolean; // 是否显示多选列 ==> 非必传（默认为false）
  isShowOpration?: boolean; // 是否显示操作列 ==> 非必传（默认为false）
  isShowSetting?: boolean; // 是否显示表格设置按钮==> 非必传（默认为false）
  oprationWidth?: number; // 操作列宽度 ==> 非必传（默认为140）
}
const props = withDefaults(defineProps<TableProps>(), {
  columns: () => [],
  pagination: true,
  searchParam: {},
  isShowSearch: true,
  isShowHandle: false,
  isShowSelection: false,
  isShowOpration: false,
  isShowSetting: false,
  oprationWidth: 160,
});
// 表格数据
const tableData = ref([]);
const tableColumns = ref([]);
const tableColumnsBak = ref([]); // 表格列备份
const loading = ref(false);
const multipleSelection = ref([]);
watch(
  () => props.columns,
  () => {
    tableColumns.value = deepClone(props.columns);
    tableColumns.value.forEach((columns) => {
      columns.show !== false &&
        columns.disDraggable !== true &&
        (columns.show = true);
    });
    tableColumnsBak.value = deepClone(tableColumns.value);
  },
  {
    immediate: true,
  }
);

// 分页数据
const pagingData = reactive({
  pageNo: 1,
  pageSize: 10,
  total: 0,
});

// 表格行多选
const handleSelectionChange = (val) => {
  multipleSelection.value = val;
};

// 排序改变
const sortParams = reactive({
  isAggAsc: undefined,
  isNotifyAsc: undefined,
});
const sortChange = (row) => {
  const { prop, order } = row;
  if (prop === "aggType" || prop === "isNotify") {
    const map = {
      aggType: "isAggAsc",
      isNotify: "isNotifyAsc",
    };
    if (order) {
      sortParams[map[prop]] = order === "ascending" ? true : false;
    } else {
      sortParams[map[prop]] = undefined;
    }
    getTableList();
  }
};
// 分页切换
const handleSizeChange = (val: number) => {
  pagingData.pageNo = 1;
  pagingData.pageSize = val;
  getTableList();
};
const handleCurrentChange = (val: number) => {
  pagingData.pageNo = val;
  getTableList();
};
const resetCurrentChange = () => {
  pagingData.pageNo = 1;
};
// 加载表格数据
const getTableList = async () => {
  loading.value = true;
  const params = {};
  try {
    Object.assign(
      params,
      props.searchParam,
      props.pagination ? pagingData : {},
      sortParams
    );
    const res = await props.requestApi(params);
    tableData.value = res.records;
    pagingData.total = res.total;
    loading.value = false;
  } catch (e) {
    if (!e.message?.includes("AxiosError")) {
      // 如果是手动取消请求则不取消loading状态
      tableData.value = [];
      loading.value = false;
    }
  }
};

// 表格列操作
const checkAll = ref(false);
const isIndeterminate = ref(true);
const handleCheckAllChange = (checked) => {
  isIndeterminate.value = false;
  let disdrag = false;
  tableColumns.value.forEach((columns) => {
    if (!columns.disDraggable) {
      columns.show = checked;
      disdrag = true;
    }
  });
  // 存在不可拖拽且取消全选时, 开启indeterminate
  if (disdrag && !checked) isIndeterminate.value = true;
  checkAll.value = checked;
};
const handleCheckChange = () => {
  let count = 0;
  tableColumns.value.forEach((columns) => {
    columns.show === false && count++;
  });
  checkAll.value = !count;
  // 非全选和全不选状态
  isIndeterminate.value = !(count === tableColumns.value.length || count === 0);
};
const handleReset = () => {
  tableColumns.value = deepClone(tableColumnsBak.value);
};
const onMove = (e) => {
  if (e.related.classList.value.includes("disDraggable")) return false;
  return true;
};

const defaultFormatter = (column) => {
  return (row) => {
    return row[column.property] ? row[column.property] : "-";
  };
};

onMounted(() => {
  getTableList();
});

defineExpose({
  getTableList,
  multipleSelection,
  loading,
  resetCurrentChange,
});
</script>

<style lang="scss" scoped>
.alarm-table-container {
  .table-header-wrapper {
    display: flex;
    justify-content: space-between;
    margin-bottom: 16px;
    flex-wrap: nowrap;
    row-gap: 8px;
    .table-header-search,
    .table-header-handle {
      display: flex;
      gap: 12px;
    }
    .table-header-handle {
      height: 32px;
    }
    .table-header-search {
      flex-wrap: wrap;
    }
  }
  .table-footer {
    margin-top: 16px;
  }
}
.column-setting-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
}
.drag-container {
  max-height: 390px;
  overflow-y: auto;
}
:deep(
    .observer-alarm-el-checkbox__input.is-checked
      + .observer-alarm-el-checkbox__label
  ) {
  color: #262626;
}
.drag-item {
  display: flex;
  align-items: center;
  .drag-handle {
    width: 16px;
    height: 16px;
    line-height: 16px;
    font-size: 16px;
    color: #bfbfbf;
    margin-right: 8px;
  }
}
.info-icon {
  font-size: 14px;
  // height: 14px;
  line-height: 14px;
  color: #bfbfbf;
}
.table-content {
  :deep(tr:hover) {
    .drawer-link-text {
      color: #1f69ff;
    }
  }
}
</style>
