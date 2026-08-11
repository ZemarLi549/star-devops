<template>
  <div class="user-content">
    <div class="user-content-wrap">
      <div class="inner-wrap">
        <div class="header-wrap">
          <el-input
            style="width: 250px; margin: 0 0 8px"
            v-model="searchValue"
            placeholder="请输入账号或用户名"
            clearable
            @input="
              () => {
                pagination.current = 1;
                getTableList();
              }
            "
          >
            <template #prefix>
              <el-icon style="cursor: pointer" @click="getTableList">
                <Search />
              </el-icon>
            </template>
          </el-input>
          <div>
            <el-button type="primary" @click="handleEditMerchant()"
              >新增用户</el-button
            >
          </div>
        </div>
        <div>
          <el-table
            ref="multipleTableRef"
            :data="tableData"
            @selection-change="handleSelectionChange"
          >
            <template #empty><Empty /></template>
            <el-table-column type="selection" width="50" />
            <el-table-column label="登录账号" show-overflow-tooltip width="120">
              <template #default="scope">{{ scope.row.account }}</template>
            </el-table-column>
            <el-table-column
              property="nickName"
              label="用户名"
              width="120"
              show-overflow-tooltip
            />
            <el-table-column
              property="spaceRole"
              label="所属空间(角色)"
              min-width="150"
            >
              <template #default="scope">
                <el-tooltip
                  placement="top"
                  :disabled="!scope.row.spaceRole || !scope.row.showtip"
                >
                  <div
                    class="text-ellipsis"
                    @mouseenter="handleMouse($event, scope.row)"
                    @mouseleave="leveMouse(scope.row)"
                  >
                    <el-tag
                      v-if="scope.row.spaceRole"
                      v-for="(item, index) in scope.row.spaceRole"
                      :key="index"
                      style="margin-right: 8px"
                    >
                      {{ item }}
                    </el-tag>
                    <span v-else>-</span>
                  </div>
                  <template #content>
                    <el-tag
                      v-for="(item, index) in scope.row.spaceRole"
                      :key="index"
                      style="margin-right: 8px"
                    >
                      {{ item }}
                    </el-tag>
                  </template>
                </el-tooltip>
                <!-- <el-tag
                  v-if="scope.row.spaceRole"
                  v-for="(item, index) in scope.row.spaceRole"
                  :key="index"
                  style="margin-right: 8px"
                >
                  {{ item }}
                </el-tag>
                <span v-else>-</span> -->
              </template>
            </el-table-column>
            <el-table-column
              property="isloggedin"
              width="90"
              show-overflow-tooltip
            >
              <template #header>
                <div>状态</div>
              </template>
              <template #default="scope">
                <!-- <el-tag
                  :type="
                    scope.row.isactive
                      ? scope.row.isloggedin
                        ? 'sucess'
                        : 'warning'
                      : 'info'
                  "
                >
                  {{
                    scope.row.isactive
                      ? scope.row.isloggedin
                        ? "正常"
                        : "未激活"
                      : "禁用"
                  }}
                </el-tag> -->
                <span
                  :class="[
                    'circle',
                    `circle-${
                      scope.row.isactive
                        ? scope.row.isloggedin
                          ? '1'
                          : '2'
                        : '0'
                    }`,
                  ]"
                ></span>
                {{
                  scope.row.isactive
                    ? scope.row.isloggedin
                      ? "正常"
                      : "未激活"
                    : "禁用"
                }}
              </template>
            </el-table-column>
            <el-table-column
              property="phone"
              label="联系手机号"
              show-overflow-tooltip
              width="140"
              :formatter="(a, b, c) => c || '-'"
            />
            <el-table-column
              property="email"
              label="邮箱"
              show-overflow-tooltip
              width="160"
              :formatter="(a, b, c) => c || '-'"
            />
            <el-table-column
              label="飞书 user_id"
              min-width="160"
              show-overflow-tooltip
            >
              <template #default="scope">
                {{ getDisplayValue(scope.row, ["feishuUserId", "feishu_user_id"]) }}
              </template>
            </el-table-column>
            <el-table-column
              label="IT 工作台 ID"
              min-width="160"
              show-overflow-tooltip
            >
              <template #default="scope">
                {{
                  getDisplayValue(scope.row, [
                    "itWorkbenchUserId",
                    "it_workbench_user_id",
                    "thirdWorkbenchUserId",
                  ])
                }}
              </template>
            </el-table-column>
            <el-table-column
              label="身份来源"
              width="110"
              show-overflow-tooltip
            >
              <template #default="scope">
                {{ getIdentitySource(scope.row) }}
              </template>
            </el-table-column>
            <el-table-column
              property="loginTime"
              label="最后登录时间"
              :formatter="
                (a, b, c) => (c ? dayjs(c).format('YYYY-MM-DD HH:mm:ss') : '-')
              "
              width="170"
            />
            <el-table-column label="操作" fixed="right" width="220">
              <template #default="scope">
                <el-button
                  type="text"
                  @click="changeStatus(scope.row)"
                  :disabled="scope.row.issuperadmin === 1"
                  v-btn
                  >{{ scope.row.isactive ? "禁用" : "启用" }}</el-button
                >
                <el-button
                  type="text"
                  @click="handleEditMerchant(scope.row)"
                  v-btn
                  >编辑</el-button
                >
                <el-button
                  type="text"
                  @click="handleEditPassword(scope.row)"
                  v-btn
                  >重置密码</el-button
                >
                <el-button
                  type="text"
                  @click="handleDeleteItem(scope.row)"
                  :disabled="scope.row.issuperadmin === 1"
                  v-btn
                  >删除</el-button
                >
              </template>
            </el-table-column>
          </el-table>
          <el-pagination
            v-if="tableData.length !== 0"
            style="justify-content: right; margin-top: 8px; position: relative"
            :page-sizes="[10, 20, 50, 100]"
            layout=" prev, pager, next, sizes, slot"
            :total="pagination.total"
            @size-change="sizeChange"
            @current-change="currentChage"
            v-model:current-page="pagination.current"
            v-model:page-size="pagination.pageSize"
          >
            <div
              style="position: absolute; left: 0"
              v-if="multipleSelection.length > 0"
            >
              {{ `已选 ${multipleSelection.length} 条数据` }}
            </div>
            <div v-else style="position: absolute; left: 0">
              {{ `共 ${pagination.total} 条数据` }}
            </div>
          </el-pagination>
        </div>
      </div>
      <DialogForm ref="editDialogRef" @refreshList="getTableList" />

      <Resetform ref="editPasswordDialogRef" @refreshList="getTableList" />
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, onMounted, reactive } from "vue";
import { Search } from "@element-plus/icons-vue";
import DialogForm from "./components/dialogform.vue";
import Resetform from "./components/resetform.vue";
import PoolApi from "@/apis/usermanage/pool";

import { ElMessageBox, ElMessage } from "element-plus";
import dayjs from "dayjs";
import Empty from "@/components/empty/TableEmpty.vue";

const searchValue = ref("");

const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
});
const multipleSelection = ref([]);

// const timeId = ref(null);

// watch(
//   () => searchValue.value,
//   (val, valOld) => {
//     if (val) {
//       if (timeId.value) {
//         clearTimeout(timeId.value);
//       }
//       timeId.value = setTimeout(() => {
//         getTableList();
//       }, 500);
//     } else {
//       clearTimeout(timeId.value);
//     }
//   }
// );

const editDialogRef = ref();
const handleEditMerchant = (rowData?) => {
  const params = {
    title: rowData ? "编辑用户" : "新增用户",
    rowData: rowData
      ? { ...rowData, workSpaceIdList: rowData.workSpaceIdList || [] }
      : null,
  };

  editDialogRef.value.open(params);
};

const editPasswordDialogRef = ref();
const handleEditPassword = (rowData) => {
  const params = {
    title: "重置密码",
    rowData: rowData ? { ...rowData } : null,
  };

  editPasswordDialogRef.value.open(params);
};

const handleSelectionChange = (val) => {
  multipleSelection.value = val;
};

const tableData = ref([]);

onMounted(() => {
  getTableList();
});

const getTableList = async () => {
  const res = await PoolApi.list({
    pageNo: pagination.current,
    pageSize: pagination.pageSize,
    nickName: searchValue.value,
  });

  tableData.value = res.records || [];
  pagination.total = res.total || 0;
  console.log(res);
};

const getDisplayValue = (row, keys: string[]) => {
  const key = keys.find((item) => row?.[item] !== undefined && row?.[item] !== null && row?.[item] !== "");
  return key ? row[key] : "-";
};

const getIdentitySource = (row) => {
  const source = row?.identitySource || row?.identity_source;
  switch (source) {
    case "LDAP":
      return "LDAP";
    case "SYNC":
      return "外部同步";
    case "LOCAL":
      return "本地账号";
    default:
      return "-";
  }
};

const paginationChange = (currentPage, pageSize) => {
  console.log(currentPage, pageSize);
};

const currentChage = (currentPage) => {
  pagination.current = currentPage;
  getTableList();
};

const sizeChange = (pageSize) => {
  pagination.pageSize = pageSize;
  pagination.current = 1;
  getTableList();
};

function batchDelete() {
  const temp = multipleSelection.value.map((item) => item.userId);
  if (temp.join(",")) {
    handleDeleteItem({ userId: temp.join(","), batch: true });
  } else {
    ElMessage({
      message: "请选择删除对象",
      type: "warning",
      showClose: true,
    });
  }
}

const changeStatus = async (row) => {
  const res = await PoolApi.change({
    userId: row.userId,
    isactive: !row.isactive,
  });
  ElMessage({
    message: row.isactive ? "禁用成功" : "启用成功",
    type: "success",
    showClose: true,
  });
  getTableList();
};

function handleDeleteItem(item) {
  ElMessageBox.confirm("删除后不可恢复，确认要删除该用户吗？", "提示", {
    type: "warning",
    center: true,
    autofocus: false,
    cancelButtonText: "取消",
    confirmButtonText: "确认",
    confirmButtonClass: "delete-btn",
    beforeClose: (action, instance, done) => {
      if (action === "confirm") {
        instance.confirmButtonLoading = true;

        PoolApi.delete(item.userId)
          .then(() => {
            done();
            ElMessage({
              showClose: true,
              message: "操作成功",
              type: "success",
            });
            pagination.current = 1;
            getTableList();
          })
          .finally(() => {
            instance.confirmButtonLoading = false;
          });
      } else {
        done();
      }
    },
  }).catch(() => {});
}

const handleMouse = (event, row) => {
  if (event.target.clientWidth < event.target.scrollWidth) {
    row.showtip = true;
  } else {
    row.showtip = false;
  }
};

const leveMouse = (row) => {
  row.showtip = false;
};
</script>

<style lang="scss" scoped>
.header-wrap {
  display: flex;
  justify-content: space-between;
}
.inner-wrap {
  height: 100%;
  padding: 16px;
  background-color: #ffffff;
  border-radius: 4px;
  box-shadow: 0px 2px 4px 0px #e5e5e5;
  overflow: auto;
  :deep(.basic-el-table__body-wrapper) {
    .basic-el-scrollbar__wrap {
      overflow-y: hidden;
    }
  }
}

.text-ellipsis {
  width: 100%;
  text-overflow: ellipsis;
  overflow: hidden;
  white-space: nowrap;
}
.circle {
  display: inline-block;
  width: 6px;
  height: 6px;
  border-radius: 50%;
  margin-right: 4px;
  margin-bottom: 2px;
}
.circle-1 {
  background: #22b522;
}
.circle-2 {
  background: #ffa54c;
}
.circle-0 {
  background: #97a8cb;
}
</style>
