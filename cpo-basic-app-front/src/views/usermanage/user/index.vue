<template>
  <ChildLayout>
    <div class="content">
      <div class="top">
        <el-input
          v-model="searchContent"
          class="search-input"
          placeholder="请输入账号或用户名"
          :prefix-icon="Search"
          @input="search"
        />
        <div class="btn">
          <el-button type="primary" @click="addUser">添加成员</el-button>
          <el-button @click="batchRemove">批量移除</el-button>
        </div>
      </div>
      <!--v-if="tableData"去除暂无数据的闪烁问题  -->
      <el-table
        :data="tableData"
        v-if="tableData"
        style="width: 100%"
        v-loading="loading"
        @selection-change="handleSelectionChange"
        @cell-mouse-enter="cellMouseEnter"
        @cell-mouse-leave="cellMouseLeave"
      >
        <template #empty><Empty /></template>
        <el-table-column type="selection" width="55" />
        <el-table-column
          prop="account"
          label="登录账号"
          min-width="120"
          show-overflow-tooltip
        >
          <template #default="scope">
            <div
              :class="[
                activeTableId == scope.row.account
                  ? 'table-active-text'
                  : 'table-text',
              ]"
              :style="{ fontSize: `14px` }"
              @click="goUserDetail(scope.row)"
            >
              {{ scope.row.account }}
            </div>
          </template>
        </el-table-column>
        <el-table-column
          prop="nickName"
          label="用户名"
          min-width="120"
          show-overflow-tooltip
        />
        <el-table-column
          prop="roleName"
          label="角色"
          min-width="120"
          show-overflow-tooltip
        />
        <!-- <el-table-column prop="dataGroupNameList" label="数据单元">
          <template #default="scope">
            <el-tooltip
              placement="top"
              v-if="scope.row.dataGroupNameList"
              :disabled="!scope.row.dataGroupNameList.length"
            >
              <div class="text-ellipsis">
                <span v-if="scope.row.dataGroupNameList.length">
                  {{ scope.row.dataGroupNameList.length }}
                </span>
                <span v-else>-</span>
              </div>
              <template #content>
                <el-tag
                  v-for="item in scope.row.dataGroupNameList"
                  style="margin-right: 8px"
                >
                  {{ item }}
                </el-tag>
              </template>
            </el-tooltip>
            <span v-else>-</span>
          </template>
        </el-table-column> -->
        <el-table-column
          prop="phone"
          label="联系手机号"
          min-width="120"
          show-overflow-tooltip
        >
          <template #default="scope">
            {{ scope.row.phone || "-" }}
          </template>
        </el-table-column>
        <el-table-column
          prop="email"
          label="邮箱"
          min-width="120"
          show-overflow-tooltip
        >
          <template #default="scope">
            {{ scope.row.email || "-" }}
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="更新时间" width="170">
          <template #default="scope">
            {{ scope.row.createTime || "-" }}
          </template>
        </el-table-column>
        <el-table-column fixed="right" label="操作" width="140">
          <template #default="scope">
            <el-button link type="primary" @click="handleClick(scope.row)"
              >更改授权</el-button
            >
            <el-button link type="primary" @click="removeUser(scope.row)"
              >移除</el-button
            >
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        v-if="tableData && tableData.length !== 0"
        class="pagination"
        v-model:current-page="pageNo"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50, 100]"
        layout="prev, pager, next, sizes, slot"
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      >
        <div
          style="position: absolute; left: 0"
          v-if="multipleSelection.length > 0"
        >
          {{ `已选 ${multipleSelection.length} 条数据` }}
        </div>
        <div v-else style="position: absolute; left: 0">
          {{ `共 ${total} 条数据` }}
        </div>
      </el-pagination>
    </div>
  </ChildLayout>
  <EditDrawer ref="editDrawer" @save="refreshList" :authSpaceId="authSpaceId" />
</template>

<script lang="ts" setup>
import { ref, onMounted, watch, nextTick } from "vue";
import ChildLayout from "../ChildLayout.vue";
import { Search } from "@element-plus/icons-vue";
import userApi from "@/apis/usermanage/user";
import EditDrawer from "./components/editDrawer.vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { useDebounceFn } from "@vueuse/core";
import { useAuthStore } from "@/stores/modules/authStore";
import Empty from "@/components/empty/TableEmpty.vue";

const authStore = useAuthStore();

const authSpaceId = ref(authStore.$state.authSpaceId);

const searchContent = ref("");
const editDrawer = ref(null);

const tableData = ref(null);
const multipleSelection = ref([]);

const pageNo = ref(1);
const pageSize = ref(10);
const total = ref(0);

const handleSizeChange = (val: number) => {
  pageNo.value = 1;
  getUserList();
};
const handleCurrentChange = (val: number) => {
  getUserList();
};

const handleSelectionChange = (val) => {
  multipleSelection.value = val;
};

const loading = ref(false);
// 获取角色列表
const getUserList = () => {
  loading.value = true;
  const reqData = {
    nickName: searchContent.value,
    workSpaceId: authSpaceId.value,
    pageSize: pageSize.value,
    pageNo: pageNo.value,
  };
  userApi
    .getUserList(reqData)
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

// 新增成员
const addUser = () => {
  editDrawer?.value.open({ title: "添加成员" });
};

const handleClick = (row) => {
  editDrawer?.value.open({ title: "更改授权", rowData: row });
};

const goUserDetail = (row) => {
  editDrawer?.value.open({ title: "用户详情", rowData: row, detail: true });
};

const refreshList = () => {
  pageNo.value = 1;
  getUserList();
};

const activeTableId = ref("");
const cellMouseEnter = (row: any) => {
  activeTableId.value = row.account;
};

const cellMouseLeave = (row: any) => {
  activeTableId.value = null;
};

const removeUser = async (row) => {
  await ElMessageBox.confirm(`确认删除用户吗？`, "提示", {
    type: "warning",
    autofocus: false,
    center: true,
    beforeClose: (action, instance, done) => {
      if (action === "confirm") {
        instance.confirmButtonLoading = true;
        userApi
          .removeUser({
            workSpaceId: authSpaceId.value,
            userIdList: [row.userId],
          })
          .then((res) => {
            done();
            ElMessage.success("移除成功");
            refreshList();
          })
          .catch((err) => {
            console.log(err);
          })
          .finally(() => {
            instance.confirmButtonLoading = false;
          });
      } else {
        done();
      }
    },
  }).catch(() => {});
};

const batchRemove = async () => {
  const userIdArr = multipleSelection.value.map((item) => {
    return item.userId;
  });
  if (userIdArr.length === 0) {
    return ElMessage.warning("请勾选要移除的用户");
  }
  await ElMessageBox.confirm(`确认移除用户吗？`, "提示", {
    type: "warning",
    autofocus: false,
    center: true,
    beforeClose: (action, instance, done) => {
      if (action === "confirm") {
        instance.confirmButtonLoading = true;
        userApi
          .removeUser({
            workSpaceId: authSpaceId.value,
            userIdList: userIdArr,
          })
          .then((res) => {
            done();
            ElMessage.success("批量移除成功");
            refreshList();
          })
          .catch((err) => {
            console.log(err);
          })
          .finally(() => {
            instance.confirmButtonLoading = false;
          });
      } else {
        done();
      }
    },
  }).catch(() => {});
};

const search = useDebounceFn(refreshList, 200);

onMounted(() => {
  if (authSpaceId.value) {
    getUserList();
  }
});

watch(
  () => authStore.$state.authSpaceId,
  () => {
    authSpaceId.value = authStore.$state.authSpaceId;
    nextTick(() => {
      refreshList();
    });
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
  .pagination {
    margin-top: 20px;
    justify-content: end;
    position: relative;
  }
  .table-text {
    color: "#262626";
    cursor: pointer;
  }
  .table-active-text {
    cursor: pointer;
    color: var(--color-primary);
  }
}

.text-ellipsis {
  width: 100%;
  text-overflow: ellipsis;
  overflow: hidden;
  white-space: nowrap;
  cursor: pointer;
  &:hover {
    width: fit-content;
    padding: 6px;
    height: 24px;
    background: #ebf5ff;
    border-radius: 2px;
    color: var(--color-primary);
    display: flex;
    align-items: center;
    justify-content: center;
  }
}
</style>
