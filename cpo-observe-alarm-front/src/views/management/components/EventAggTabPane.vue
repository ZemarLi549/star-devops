<template>
  <el-tab-pane :label="`事件列表(${pagingData.total})`" name="eventTable">
    <div class="table-header">
      <el-input
        v-model="searchGroup.uniqueOrigin"
        style="width: 240px;"
        placeholder="搜索告警事件ID或去重告警ID"
        :prefix-icon="Search"
        @input="handleSearch"
        clearable
      />
    </div>
    <div class="event-table">
      <el-table :data="tableData">  
        <el-table-column label="告警事件ID" min-width="200">
          <template #default="scope">
            <div class="copy-cell">
              <div class="copy-content" v-tooltip>{{ scope.row.eventId }}</div>
              <div class="copy-icon" @click="handleCopy(scope.row.eventId)">
                <DocumentCopy />
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="告警事件级别" width="120">
          <template #default="scope">
            <div :class="`alarm-level-tag alarm-level-tag-${scope.row.level}`">
              {{ getAlarmLevel(scope.row.level) }}
            </div>
          </template>
        </el-table-column>
        <el-table-column label="类型" prop="cate" width="60">
          <template #default="scope">
            {{ getAlarmType(scope.row.cate) }}
          </template>
        </el-table-column>
        <el-table-column label="事件触发时间" prop="triggerTime" width="176" />
        <el-table-column label="去重告警ID" show-overflow-tooltip prop="alarmId" min-width="200" />
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="scope">
            <el-button type="primary" link @click="handleShowContent(scope.row)">事件原文</el-button>
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
    title="事件原文"
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
import { getAlarmLevel, getAlarmType, handleCopy } from "@/utils/alarm.ts";
import { DocumentCopy } from "@element-plus/icons-vue";
import alarmManageApi from "@/request/api/alarm-management/index.ts";
import { SearchParams } from "@/globals/interface";
import { Search } from "@element-plus/icons-vue";
import { useDebounceFn } from "@vueuse/core";

const props = defineProps(["id"]);
const searchGroup = reactive<SearchParams>({});

// 分页数据
const pagingData = reactive({
  pageNo: 1,
  pageSize: 10,
  total: 0,
});
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

const getTableList = () => {
  alarmManageApi.getAlarmEventPage({...pagingData, ...searchGroup}, props.id).then((res) => {
    tableData.value = res.records;
    pagingData.total = res.total;
  });
};

const tableData = ref([]);
const content = ref("")
const dialogVisible = ref(false)
const handleShowContent = (row) => {
  alarmManageApi.getUniqueContent(row.eid).then(res => {
    // eid不展示, 用来查询事件原文
    content.value = res ? JSON.parse(res) : res;
    dialogVisible.value = true;
  })
}
const setSearchValue = (id) => {
  searchGroup.uniqueOrigin = id
  getTableList()
}
const clearSearchValue = () => {
  searchGroup.uniqueOrigin = undefined
  getTableList()
}

defineExpose({
  getTableList,
  setSearchValue,
  clearSearchValue
});
</script>

<style scoped lang="scss">
.table-header {
  margin-bottom: 16px;
  display: flex;
  justify-content: space-between;
}
</style>
