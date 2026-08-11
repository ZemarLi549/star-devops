<template>
  <div class="assign-police card">
    <div class="header">
      <div class="form">
        <!-- 筛选空格 -->
        <el-input
          v-model="form.assignName"
          style="width: 240px"
          placeholder="搜索分派名称"
          :prefix-icon="Search"
          @input="debouncedInput"
        />
        <SelectorWithLabel
          placeholder="请选择"
          :options="assignOptions"
          v-model="form.status"
          @change="search"
          defaultLabelWidth
        >
          <template #label> 状态 </template>
        </SelectorWithLabel>
      </div>
      <el-button type="primary" class="btn" @click="createAssignPolice"
        >新建分派策略</el-button
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
      <el-table-column prop="assignName" label="分派策略" show-overflow-tooltip>
        <template #default="scope">
          <div
            :class="[
              'ell',
              activeTableId == scope.row.assignName
                ? 'table-active-text'
                : 'table-text',
            ]"
            :style="{ fontSize: `14px` }"
            @click="viewAssignPolice(scope.row)"
          >
            {{ scope.row.assignName }}
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="remark" label="描述" show-overflow-tooltip>
        <template #default="scope">
          {{ scope.row.remark || "-" }}
        </template></el-table-column
      >
      <el-table-column prop="notifyGroups" label="通知组" show-overflow-tooltip>
        <template #default="scope">
          {{ scope.row.notifyGroups || "-" }}
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="scope">
          <span :class="['circle', `circle-${scope.row.status}`]"></span
          >{{ scope.row.status === 1 ? "已启用" : "已停用" }}
        </template>
      </el-table-column>
      <el-table-column prop="agg" label="告警聚合" width="100" />
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
      >
        <template #default="scope">
          {{ scope.row.updater || "-" }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="scope">
          <el-button
            link
            type="primary"
            @click="handleAssignPoliceList(scope.row)"
            >{{ scope.row.status === 1 ? "停用" : "启用" }}</el-button
          >
          <el-button link type="primary" @click="editAssignPolice(scope.row)"
            >编辑</el-button
          >
          <el-button link type="primary" @click="cloneAssignPolice(scope.row)"
            >克隆</el-button
          >
          <el-button link type="primary" @click="delAssignPoliceList(scope.row)"
            >删除</el-button
          >
          <!-- <el-dropdown class="el-dropdown">
            <span class="el-dropdown-link"> ··· </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="delAssignPoliceList(scope.row)"
                  >删除</el-dropdown-item
                >
              </el-dropdown-menu>
            </template>
          </el-dropdown> -->
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
      @current-change="getAssignPoliceList"
    >
      <div>
        {{ `共 ${total} 条数据` }}
      </div>
    </el-pagination>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";
import { Search } from "@element-plus/icons-vue";
import SelectorWithLabel from "@/components/SelectorWithLabel.vue";
import { assignOptions, AssignPoliceList } from "./assist.ts";
import { useRouter } from "vue-router";
import assignPolice from "@/request/api/assign-police/index";
import { ElMessage, ElMessageBox } from "element-plus";
import dayjs from "dayjs";
import { useDebounceFn } from "@vueuse/core";
import Empty from "@/components/empty/Empty.vue";

const router = useRouter();
const loading = ref(false);

const form = reactive({
  assignName: "",
  status: 2,
});
const tableData = ref<AssignPoliceList[]>([]);
const total = ref(0);
const currentPage = ref(1);
const pageSize = ref(10);
const handleSizeChange = () => {
  currentPage.value = 1;
  getAssignPoliceList();
};

const createAssignPolice = () => {
  router.push("/assign-police/create");
};
const editAssignPolice = (row: AssignPoliceList) => {
  router.push(`/assign-police/edit/${row.policyId}`);
};
const cloneAssignPolice = (row: AssignPoliceList) => {
  router.push(`/assign-police/clone/${row.policyId}`);
};

const viewAssignPolice = (row: AssignPoliceList) => {
  router.push(`/assign-police/view/${row.policyId}`);
};

const debouncedInput = useDebounceFn(() => {
  currentPage.value = 1;
  getAssignPoliceList();
}, 500);

const search = () => {
  currentPage.value = 1;
  getAssignPoliceList();
};

const getAssignPoliceList = () => {
  loading.value = true;
  assignPolice
    .getAssignPoliceList({
      assignName: form.assignName,
      pageNo: currentPage.value,
      pageSize: pageSize.value,
      status: form.status === 2 ? undefined : form.status,
    })
    .then((res) => {
      total.value = res.total;
      tableData.value = res.records;
    })
    .catch((err) => {
      console.log(err);
    })
    .finally(() => {
      loading.value = false;
    });
};

const delAssignPoliceList = (row: AssignPoliceList) => {
  ElMessageBox.confirm(`确认要删除${row.assignName}吗？`, "提示", {
    type: "warning",
    center: true,
    autofocus: false,
  }).then(async () => {
    try {
      assignPolice
        .delAssignPoliceList({
          assignId: row.policyId,
        })
        .then(() => {
          ElMessage.success("删除成功");
          currentPage.value = 1;
          getAssignPoliceList();
        });
    } catch (err) {
      console.log(err);
    }
  });
};

const handleAssignPoliceListRequest = (row: AssignPoliceList) => {
  const reqName =
    row.status === 1 ? "disableAssignPoliceList" : "enableAssignPoliceList";
  assignPolice[reqName]({ assignIds: [row.policyId] }).then(() => {
    ElMessage.success(row.status === 1 ? "停用成功" : "启用成功");
    getAssignPoliceList();
  });
};
const handleAssignPoliceList = (row: AssignPoliceList) => {
  if (row.status === 0) {
    handleAssignPoliceListRequest(row);
  } else {
    ElMessageBox.confirm(`确认要停用${row.assignName}吗？`, "提示", {
      type: "warning",
      center: true,
      autofocus: false,
    }).then(async () => {
      handleAssignPoliceListRequest(row);
    });
  }
};

const activeTableId = ref("");
const cellMouseEnter = (row: AssignPoliceList) => {
  activeTableId.value = row.assignName;
};

const cellMouseLeave = () => {
  activeTableId.value = null;
};

onMounted(() => {
  getAssignPoliceList();
});
</script>

<style lang="scss" scoped>
.assign-police {
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
