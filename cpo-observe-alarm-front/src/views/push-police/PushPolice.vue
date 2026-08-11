<template>
  <div class="shield-rule card">
    <div class="header">
      <div class="form">
        <el-input
          v-model="form.policyName"
          style="width: 240px"
          placeholder="搜索推送名称"
          :prefix-icon="Search"
          @input="debouncedInput"
        />
        <SelectorWithLabel
          placeholder="请选择"
          :options="statusOptions"
          v-model="form.status"
          @change="search"
          defaultLabelWidth
        >
          <template #label> 状态 </template>
        </SelectorWithLabel>
      </div>
      <el-button type="primary" class="btn" @click="createPushPolice"
        >新建推送策略</el-button
      >
    </div>
    <el-table
      :data="tableData"
      style="width: 100%"
      v-loading="loading"
      @cell-mouse-enter="cellMouseEnter"
      @cell-mouse-leave="cellMouseLeave"
    >
      <template #empty><Empty /></template>
      <el-table-column
        prop="policyName"
        label="推送策略名称"
        show-overflow-tooltip
      >
        <template #default="scope">
          <div
            :class="[
              'ell',
              activeTableId == scope.row.policyName
                ? 'table-active-text'
                : 'table-text',
            ]"
            :style="{ fontSize: `14px` }"
            @click="viewPushPolice(scope.row)"
          >
            {{ scope.row.policyName }}
          </div>
        </template></el-table-column
      >

      <el-table-column prop="remark" label="描述" show-overflow-tooltip>
        <template #default="scope">
          {{ scope.row.remark || "-" }}
        </template></el-table-column
      >
      <el-table-column prop="status" label="状态" width="120">
        <template #default="scope">
          <span :class="['circle', `circle-${scope.row.status}`]"></span
          >{{ scope.row.status === 1 ? "已启用" : "已停用" }}
        </template>
      </el-table-column>
      <el-table-column prop="updateTime" label="更新时间" width="180">
        <template #default="scope">
          {{ dayjs(scope.row.updateTime).format("YYYY-MM-DD HH:mm:ss") }}
        </template>
      </el-table-column>
      <el-table-column
        prop="updater"
        label="更新人"
        show-overflow-tooltip
        width="100"
      />
      <el-table-column label="操作" width="200">
        <template #default="scope">
          <el-button link type="primary" @click="handlePushPolice(scope.row)">{{
            scope.row.status === 1 ? "停用" : "启用"
          }}</el-button>
          <el-button link type="primary" @click="editPushPolice(scope.row)"
            >编辑</el-button
          >
          <el-button link type="primary" @click="clonePushPolice(scope.row)"
            >克隆</el-button
          >
          <el-button link type="primary" @click="delPushPolice(scope.row)"
            >删除</el-button
          >
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      v-if="tableData.length !== 0"
      class="pagination mt-16"
      v-model:current-page="currentPage"
      v-model:page-size="pageSize"
      :page-sizes="[10, 20, 50, 100]"
      layout="slot, ->,prev, pager, next, sizes"
      :total="total"
      @size-change="handleSizeChange"
      @current-change="getPushPolice"
    >
      <div>
        {{ `共 ${total} 条数据` }}
      </div>
    </el-pagination>
  </div>
</template>

<script lang="ts" setup>
import { ref, reactive, onMounted } from "vue";
import { Search } from "@element-plus/icons-vue";
import SelectorWithLabel from "@/components/SelectorWithLabel.vue";
import { statusOptions, PushPolice } from "./assist.ts";
import { useRouter } from "vue-router";
import PushPoliceRequest from "@/request/api/push-police/index";
import { ElMessage, ElMessageBox } from "element-plus";
import dayjs from "dayjs";
import { useDebounceFn } from "@vueuse/core";
import Empty from "@/components/empty/Empty.vue";

const router = useRouter();
const loading = ref(false);

const form = reactive({
  policyName: "",
  status: 2,
});
const tableData = ref<PushPolice[]>([]);
const total = ref(0);
const currentPage = ref(1);
const pageSize = ref(10);
const handleSizeChange = () => {
  currentPage.value = 1;
  getPushPolice();
};

const createPushPolice = () => {
  router.push("/push-police/create");
};
const editPushPolice = (row) => {
  router.push(`/push-police/edit/${row.id}`);
};
const clonePushPolice = (row) => {
  router.push(`/push-police/clone/${row.id}`);
};
const viewPushPolice = (row) => {
  router.push(`/push-police/views/${row.id}`);
};
const search = () => {
  currentPage.value = 1;
  getPushPolice();
};

const debouncedInput = useDebounceFn(() => {
  currentPage.value = 1;
  getPushPolice();
}, 500);

const getPushPolice = () => {
  loading.value = true;

  PushPoliceRequest.getPushPolice({
    policyName: form.policyName,
    pageNo: currentPage.value,
    pageSize: pageSize.value,
    status: form.status === 2 ? undefined : form.status,
  })
    .then((res) => {
      total.value = res.total;
      tableData.value = res.records;
    })
    .finally(() => {
      loading.value = false;
    });
};

const delPushPolice = (row: PushPolice) => {
  ElMessageBox.confirm(`确认要删除${row.policyName}吗？`, "提示", {
    type: "warning",
    center: true,
    autofocus: false,
  }).then(async () => {
    try {
      PushPoliceRequest.delPushPolice(row.id).then(() => {
        ElMessage.success("删除成功");
        currentPage.value = 1;
        getPushPolice();
      });
    } catch (error) {}
  });
};

const handlePushPoliceRequset = (row: PushPolice) => {
  const reqName = row.status === 1 ? "disablePushPolice" : "enablePushPolice";
  PushPoliceRequest[reqName]({ ids: [row.id] }).then(() => {
    ElMessage.success(row.status === 1 ? "停用成功" : "启用成功");
    getPushPolice();
  });
};
const handlePushPolice = (row: PushPolice) => {
  if (row.status === 0) {
    handlePushPoliceRequset(row);
  } else {
    ElMessageBox.confirm(`确认要停用${row.policyName}吗？`, "提示", {
      type: "warning",
      center: true,
      autofocus: false,
    }).then(async () => {
      handlePushPoliceRequset(row);
    });
  }
};

const activeTableId = ref("");
const cellMouseEnter = (row: PushPolice) => {
  activeTableId.value = row.policyName;
};

const cellMouseLeave = () => {
  activeTableId.value = null;
};

onMounted(() => {
  getPushPolice();
});
</script>

<style lang="scss" scoped>
.shield-rule {
  height: 100%;
  overflow-y: auto;
}
.header {
  display: flex;
  gap: 8px;
  margin-bottom: 16px;
  justify-content: space-between;
  .form {
    display: flex;
    gap: 8px;
  }
}
.pagination {
  margin-top: 20px;
  justify-content: end;
  position: relative;
}
.el-dropdown {
  display: inline-block;
  line-height: unset;
  cursor: pointer;
  .el-dropdown-link {
    margin-left: 12px;
  }
}
.circle {
  display: inline-block;
  width: 6px;
  height: 6px;
  border-radius: 50%;
  margin-right: 8px;
  margin-bottom: 2px;
}
.circle-1 {
  background: #22b522;
}
.circle-0 {
  background: #97a8cb;
}
</style>
