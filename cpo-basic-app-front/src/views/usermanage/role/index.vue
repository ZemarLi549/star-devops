<template>
  <ChildLayout>
    <div class="content">
      <div class="top">
        <el-input
          v-model="searchContent"
          class="search-input"
          placeholder="请输入角色"
          :prefix-icon="Search"
          @input="search"
        />
        <div class="btn">
          <el-button type="primary" @click="addRole">新增角色</el-button>
        </div>
      </div>
      <el-table
        :data="tableData"
        v-if="tableData"
        v-loading="loading"
        style="width: 100%"
        @cell-mouse-enter="cellMouseEnter"
        @cell-mouse-leave="cellMouseLeave"
      >
        <template #empty><Empty /></template>
        <el-table-column
          fixed
          prop="roleName"
          label="角色"
          show-overflow-tooltip
        >
          <template #default="scope">
            <div
              :class="[
                activeTableId == scope.row.roleId
                  ? 'table-active-text'
                  : 'table-text',
              ]"
              :style="{ fontSize: `14px` }"
              @click="goRoleDetail(scope.row)"
            >
              {{ scope.row.roleName }}
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="描述" show-overflow-tooltip>
          <template #default="scope">
            {{ scope.row.remark || "-" }}
          </template>
        </el-table-column>
        <el-table-column prop="userCount" label="用户数量" />
        <el-table-column prop="createTime" label="创建时间" />
        <el-table-column fixed="right" label="操作" width="120">
          <template #default="scope">
            <div v-if="!scope.row.isdefault">
              <el-button link type="primary" @click="handleClick(scope.row)"
                >编辑</el-button
              >
              <el-button @click="delRole(scope.row)" link type="primary"
                >删除</el-button
              >
            </div>
            <div v-else>-</div>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        v-if="tableData && tableData.length !== 0"
        class="pagination"
        v-model:current-page="pageNo"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50, 100]"
        layout="slot, ->,prev, pager, next, sizes"
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      >
        <div>
          {{ `共 ${total} 条数据` }}
        </div></el-pagination
      >
    </div>
  </ChildLayout>
  <EditDrawer ref="editDrawer" @save="refreshList" :authSpaceId="authSpaceId" />
</template>

<script lang="ts" setup>
import { ref, onMounted, watch } from "vue";
import ChildLayout from "../ChildLayout.vue";
import { Search } from "@element-plus/icons-vue";
import EditDrawer from "./components/editDrawer.vue";
import roleApi from "@/apis/usermanage/role";
import { ElMessageBox, ElMessage } from "element-plus";
import { useDebounceFn } from "@vueuse/core";
import { useAuthStore } from "@/stores/modules/authStore";
import Empty from "@/components/empty/TableEmpty.vue";

const authStore = useAuthStore();
const authSpaceId = ref(authStore.$state.authSpaceId);

const searchContent = ref("");
const editDrawer = ref(null);
const tableData = ref(null);

const pageNo = ref(1);
const pageSize = ref(10);
const total = ref(0);

const handleSizeChange = (val: number) => {
  pageNo.value = 1;
  getRoleList();
};
const handleCurrentChange = (val: number) => {
  getRoleList();
};

const loading = ref(false);
// 获取角色列表
const getRoleList = () => {
  loading.value = true;
  const reqData = {
    roleName: searchContent.value,
    workSpaceId: authSpaceId.value,
    pageSize: pageSize.value,
    pageNo: pageNo.value,
  };
  roleApi
    .getRoleList(reqData)
    .then((res) => {
      tableData.value = res.records;
      total.value = res.total;
    })
    .catch((err) => {
      console.log(err);
    })
    .finally(() => {
      loading.value = false;
    });
};

// 新增角色
const addRole = () => {
  editDrawer?.value.open({ title: "新增角色" });
};

const handleClick = (row) => {
  editDrawer?.value.open({ title: "编辑角色", rowData: row });
};

const refreshList = () => {
  pageNo.value = 1;
  getRoleList();
};

// 删除角色
const delRole = async (row) => {
  await ElMessageBox.confirm(
    `删除后不可恢复，确认删除角色${row.roleName}吗？`,
    "提示",
    {
      type: "warning",
      autofocus: false,
      center: true,
      beforeClose: (action, instance, done) => {
        if (action === "confirm") {
          instance.confirmButtonLoading = true;
          roleApi
            .delRole({
              roleId: row.roleId,
              roleName: row.roleName,
            })
            .then(() => {
              done();
              ElMessage.success("删除成功");
              refreshList();
            })
            .finally(() => {
              instance.confirmButtonLoading = false;
            });
        } else {
          done();
        }
      },
    }
  ).catch(() => {});
};
const search = useDebounceFn(refreshList, 200);

const goRoleDetail = (row) => {
  editDrawer?.value.open({ title: "角色详情", rowData: row, detail: true });
};

const activeTableId = ref("");
const cellMouseEnter = (row: any) => {
  activeTableId.value = row.roleId;
};

const cellMouseLeave = (row: any) => {
  activeTableId.value = null;
};
onMounted(() => {
  if (authSpaceId.value) {
    getRoleList();
  }
});

watch(
  () => authStore.$state.authSpaceId,
  () => {
    authSpaceId.value = authStore.$state.authSpaceId;
    setTimeout(() => {
      refreshList();
    }, 0);
  },
  {
    deep: true,
  }
);
</script>
<style scoped lang="scss">
.content {
  margin: 16px;
  background-color: #fff;
  padding: 24px;
  width: 0;
  flex: 1;
  border-radius: 4px;
  box-shadow: 0px 2px 4px 0px #e5e5e5;
  overflow: auto;
  .top {
    display: flex;
    justify-content: space-between;
    margin-bottom: 16px;
    .search-input {
      width: 240px;
    }
    .btn {
      display: flex;
    }
  }
  // .pagination {
  //   margin-top: 20px;
  //   float: right;
  // }
  .table-text {
    color: "#262626";
    cursor: pointer;
    text-overflow: ellipsis;
    overflow: hidden;
    white-space: nowrap;
  }
  .table-active-text {
    cursor: pointer;
    color: var(--color-primary);
    text-overflow: ellipsis;
    overflow: hidden;
    white-space: nowrap;
  }
}
</style>
