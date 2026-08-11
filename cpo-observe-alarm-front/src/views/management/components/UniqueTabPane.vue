<template>
  <el-tab-pane :label="`去重(${pagingData.total})`" name="uniqueTable">
    <div class="table-header">
      <el-input
        v-model="searchGroup.uniqueId"
        style="width: 240px;"
        placeholder="搜索去重告警ID"
        :prefix-icon="Search"
        @input="handleSearch"
        clearable
      />
      <el-tooltip
          effect="dark"
          :content="contentCellLines === 1 ? '多行显示' : '单行显示'"
          placement="bottom"
        >
        <el-button @click="changeCellLines" style="width: 32px">
          <FontIcon style="font-size: 14px;" :icon="contentCellLines === 1 ? 'icon-a-bianzubeifen5' : 'icon-a-bianzubeifen6'"/>
        </el-button>
      </el-tooltip>
    </div>
    <div class="unique-table">
      <el-table :data="tableData" @sort-change="handleSortChange" border class="table-content" row-class-name="testsss">
        <el-table-column label="去重告警ID" width="218">
          <template #default="scope">
            <div class="copy-cell">
              <div class="copy-content" v-tooltip>{{ scope.row.uniqueId }}</div>
              <div class="copy-icon" @click="handleCopy(scope.row.uniqueId)">
                <DocumentCopy />
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="告警内容" prop="content" width="238">
          <template #default="scope">
            <div class="content-cell" v-tooltip :style="{ '--word-lines': contentCellLines }">
              {{ scope.row.content }}
            </div>
          </template>
        </el-table-column>
        <el-table-column label="事件量" width="96" sortable="custom">
          <template #default="scope">
            <div class="drawer-link-text" @click="() => emits('showEvent', scope.row.uniqueId)">
              {{ scope.row.num }}
            </div>
          </template>
        </el-table-column>
        <el-table-column label="告警级别" width="120">
          <template #default="scope">
            <div :class="`alarm-level-tag alarm-level-tag-${scope.row.level}`">
              {{ getAlarmLevel(scope.row.level) }}
            </div>
          </template>
          <template #header="scope">
            <div style="display: flex; align-items: center; gap: 4px">
              <span>{{ scope.column.label }}</span>
              <el-tooltip
                effect="dark"
                content="当前显示的告警级别为告警事件列表中的最高级别"
                placement="right"
              >
                <span class="info-icon">
                  <FontIcon icon="icon-yiwen" />
                </span>
              </el-tooltip>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="告警状态" width="104">
          <template #default="scope">
            <div :class="`alarm-state-tag alarm-state-tag-${scope.row.state}`">
              {{ getAlarmStatus(scope.row.state) }}
            </div>
          </template>
          <!-- <template #header="scope">
            <div style="display: flex; align-items: center; gap: 4px">
              <span>{{ scope.column.label }}</span>
              <el-tooltip
                effect="dark"
                content="若去重告警中的某条告警自动恢复，它的状态则自动变为已关闭"
                placement="right"
              >
                <span class="info-icon">
                  <FontIcon icon="icon-yiwen" />
                </span>
              </el-tooltip>
            </div>
          </template> -->
        </el-table-column>
        <el-table-column label="类型" prop="cate" width="60">
          <template #default="scope">
            {{ getAlarmType(scope.row.cate) }}
          </template>
        </el-table-column>
        <el-table-column label="触发时间" width="176">
          <template #default="scope">
            {{ dayjs(scope.row.triggerTime).format("YYYY-MM-DD HH:mm:ss") }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="60" fixed="right">
          <template #default="scope">
            <el-button type="primary" link @click="handleShowLabels(scope.row)">
              标签
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        v-model:current-page="pagingData.pageNo"
        v-model:page-size="pagingData.pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :layout="`total, ->, prev, pager, next, sizes`"
        :total="pagingData.total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        style="margin-top: 16px"
      ></el-pagination>
    </div>
  </el-tab-pane>

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
import { ref, reactive } from "vue";
import { getAlarmLevel, getAlarmType, handleCopy, getAlarmStatus } from "@/utils/alarm.ts";
import { DocumentCopy } from "@element-plus/icons-vue";
import alarmManageApi from "@/request/api/alarm-management/index.ts";
import { SearchParams } from "@/globals/interface";
import { Search } from "@element-plus/icons-vue";
import { useDebounceFn } from "@vueuse/core";
import OverviewApi from "@/request/api/alarm-overview";
import { useUserStore } from "@/stores/modules/user";
import dayjs from 'dayjs'

// 按事件数量排序方式
enum SortType {
  descending,
  ascending,
  default,
}
const maxLines = 7; // 表格最大显示行数
const userStore = useUserStore();
const guideState = JSON.parse(userStore.userInfo.guideState);
const props = defineProps(["id"]);
const emits = defineEmits(["showEvent"]);
const searchGroup = reactive<SearchParams>({});

// 分页数据
const pagingData = reactive({
  pageNo: 1,
  pageSize: 10,
  total: 0,
});
const sortType = ref<SortType>(SortType.default)
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
const handleSearch = useDebounceFn(() => {
  getTableList();
}, 300);
const clearSearchValue = () => {  
  searchGroup.uniqueId = undefined
  getTableList()
}
const handleSortChange = ({order}) => {
  if (!order) {
    sortType.value = SortType.default
  } else {
    sortType.value = order === 'descending' ? SortType.descending : SortType.ascending
  }
  getTableList();
}

const getTableList = () => {
  alarmManageApi.getUniquePage({...pagingData, ...searchGroup}, props.id, sortType.value).then((res) => {
    tableData.value = res.records;
    pagingData.total = res.total;
  });
};

const tableData = ref([]);
const content = ref({});
const dialogVisible = ref(false);
const handleShowLabels = (row) => {
  alarmManageApi.getUniqueLabels(row.uniqueId).then((res) => {
    content.value = res ? JSON.parse(res) : res;
    dialogVisible.value = true;
  });
};

// 表格切换单行/多行
const contentCellLines = ref(guideState.cellLines === 1 ? 1 : maxLines); // 如果后端没有该字段则初始化为maxLines
const changeCellLines = () => {
  contentCellLines.value = contentCellLines.value === 1 ? maxLines : 1;
  guideState.cellLines = contentCellLines.value;
  OverviewApi.guideState({
    guideState: JSON.stringify(guideState),
  }).then(() => {
    userStore.setUserInfo({
      guideState: JSON.stringify(guideState),
    });
  });
};

defineExpose({
  getTableList,
  clearSearchValue
});


</script>

<style scoped lang="scss">
.table-header {
  margin-bottom: 16px;
  display: flex;
  justify-content: space-between;
}

.content-cell {
  word-wrap: break-word;
  word-break: break-all;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: var(--word-lines);
  -webkit-box-orient: vertical;
}

.table-content {
  :deep(tr:hover) {
    .drawer-link-text {
    color: #1f69ff;
   }
  }
  :deep(.observer-alarm-el-table__cell) {
    border-right: none !important;
  }
  &::after {
    width: 0px !important;
  }
  &::before {
    width: 0px !important;
  }
  :deep(.observer-alarm-el-table__border-left-patch) {
    width: 0px !important;
  }
  :deep(.observer-alarm-el-table__inner-wrapper::after) {
    height: 0 !important;
  }
}

</style>