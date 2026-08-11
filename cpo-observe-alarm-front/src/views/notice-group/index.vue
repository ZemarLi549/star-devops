<template>
  <div class="main-container">
    <el-container>
      <el-aside
        :width="asideWidthString"
        :class="['aside', { 'aside-hide': !asideWidth }]"
      >
        <div class="aside-wrapper" v-show="asideWidth">
          <div class="aside-inner">
            <div class="aside-title">
              <span style="font-weight: bold; font-size: 16px">通知组</span>
              <span style="color: #8c8c8c">共 {{ cards.length }} 组</span>
            </div>
            <div class="aside-btn">
              <div>
                <el-button style="width: 100%" @click="handleEditMerchant()"
                  >+ 创建分组</el-button
                >
              </div>
              <div>
                <el-input
                  v-model="searchGroup"
                  style="width: 100%; margin-top: 12px"
                  placeholder="请输入通知组名"
                  :prefix-icon="Search"
                  @input="debounceSearch"
                  clearable
                />
              </div>
            </div>
            <!-- <div class="aside-cards"> -->
            <el-scrollbar class="aside-cards">
              <Card
                v-for="item in cards"
                :key="item.groupId"
                @click="selectCard(item)"
                :current="current"
                :card="item"
                @edit-group="handleEditGroup(item)"
                @del-group="handleDelGroup(item)"
              />
              <!-- </div> -->
            </el-scrollbar>
          </div>
        </div>
        <Icon
          class="icon-fold-btn"
          icon-name="icon-fold-btn"
          size="lg"
          @click="handleFoldAside"
          :style="foldBtnStyle"
        ></Icon>
      </el-aside>
      <el-main class="main-wrapper" v-loading="groupLoading">
        <div v-if="current != null && !current" :class="['unselect']">
          <div class="unselect-content">
            <h1>如何使用 通知组</h1>
            <div style="margin: 12px 0">
              预设的通知小组可以根据人员职责进行灵活划分，方便统一管理和组群快速选择。
            </div>
            <div>
              <img
                style="width: 417px"
                src="@/assets/images/grouptip.png"
                alt=""
              />
            </div>
            <div class="unselect-bottom">
              <el-button type="primary" @click="handleEditMerchant()"
                >+ 创建分组</el-button
              >
            </div>
          </div>
        </div>
        <div v-if="current">
          <div class="main-title">
            <div>{{ currentTitle }}</div>
          </div>
          <div
            style="
              display: flex;
              justify-content: space-between;
              margin-bottom: 16px;
            "
          >
            <el-input
              v-model="keyWord"
              style="width: 240px"
              placeholder="请输入账号或用户名"
              :prefix-icon="Search"
              @input="debounceFunc"
              clearable
            />
            <div>
              <el-button type="primary" @click="handleAddUser()"
                >添加成员</el-button
              >
              <el-button @click="handleDelUsers">批量移除</el-button>
            </div>
          </div>
          <div>
            <el-table
              ref="multipleTableRef"
              :data="tableData"
              @selection-change="handleSelectionChange"
              v-loading="loading"
            >
              <template #empty><Empty /></template>
              <el-table-column type="selection" width="50" />
              <el-table-column
                property="userName"
                label="用户名"
                show-overflow-tooltip
              />
              <el-table-column
                property="account"
                label="账号"
                show-overflow-tooltip
              />
              <el-table-column
                property="role"
                label="角色"
                show-overflow-tooltip
                :formatter="(a, b, c) => c || '-'"
              />
              <el-table-column prop="isActive" label="状态" width="104">
                <template #default="scope">
                  <!-- <el-tag :type="scope.row.isActive ? 'primary' : 'info'">{{
                    scope.row.isActive ? "正常" : "禁用"
                  }}</el-tag> -->
                  <span
                    :class="[
                      'circle',
                      `circle-${scope.row.isActive ? '1' : '0'}`,
                    ]"
                  ></span
                  >{{ scope.row.isActive ? "正常" : "禁用" }}
                </template>
              </el-table-column>
              <el-table-column
                property="email"
                label="邮箱"
                show-overflow-tooltip
                width="184"
                :formatter="(a, b, c) => c || '-'"
              />
              <el-table-column
                property="phone"
                label="联系手机号"
                show-overflow-tooltip
                width="127"
                :formatter="(a, b, c) => c || '-'"
              />

              <el-table-column
                property="updateTime"
                label="更新时间"
                :formatter="(a, b, c) => c || '-'"
                width="174"
              />
              <el-table-column label="操作" fixed="right" width="80">
                <template #default="scope">
                  <el-button
                    type="primary"
                    link
                    @click="handleDelUser(scope.row)"
                    >移除</el-button
                  >
                </template>
              </el-table-column>
            </el-table>
            <el-pagination
              v-if="tableData.length !== 0"
              style="
                justify-content: right;
                margin-top: 8px;
                position: relative;
              "
              :page-sizes="[10, 20, 50, 100]"
              layout=" prev, pager, next, sizes, slot"
              :total="pagination.total"
              @size-change="sizeChange"
              @current-change="currentChage"
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

        <DialogForm ref="editDialogRef" @refreshList="refreshList" />
        <UserForm ref="userDialogRef" @refreshList="refreshTable" />
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { ref, computed, reactive, onMounted } from "vue";
import Icon from "@/components/Icon.vue";
import Card from "./components/card.vue";
import DialogForm from "./components/dialogform.vue";
import UserForm from "./components/userform.vue";

import noticeGroupApi from "@/request/api/notice-group/index";
import { ElMessage, ElMessageBox } from "element-plus";
import { Search } from "@element-plus/icons-vue";

import { debounce } from "@/utils";
import Empty from "@/components/empty/Empty.vue";

const cards = ref([]);
const current = ref(null);
const currentTitle = ref("");
const loading = ref(false);
const keyWord = ref("");
const searchGroup = ref("");
const groupLoading = ref(false);

const selectCard = (item) => {
  current.value = item.groupId;
  Object.assign(pagination, {
    current: 1,
    pageSize: 10,
    total: 0,
  });
  getTableList();
};

const asideWidth = ref(256);
const asideWidthString = computed(() => asideWidth.value + "px");
const handleFoldAside = () => {
  asideWidth.value = asideWidth.value ? 0 : 256;
};
const foldBtnStyle = computed(() => {
  const deg = asideWidth.value ? 0 : 180;
  return {
    transform: `rotate(${deg}deg)`,
  };
});

const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
});
const multipleSelection = ref([]);
const tableData = ref([]);
const handleSelectionChange = (val) => {
  multipleSelection.value = val;
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

const getTableList = async () => {
  loading.value = true;
  multipleSelection.value = [];
  if (!current.value) return;
  currentTitle.value = cards.value.find(
    (item) => item.groupId == current.value
  )?.groupName;
  const res = await noticeGroupApi.getGroupUserList({
    pageNo: pagination.current,
    pageSize: pagination.pageSize,
    groupId: current.value,
    nameOrAccount: keyWord.value,
  });

  tableData.value = res.records || [];
  pagination.total = res.total || 0;
  loading.value = false;
};

const debounceSearch = debounce(() => {
  groupLoading.value = true;
  noticeGroupApi
    .getGroupByName(searchGroup.value)
    .then((res) => {
      cards.value = res || [];
      current.value = "";
    })
    .finally(() => {
      groupLoading.value = false;
    });
});
const debounceFunc = debounce(() => {
  Object.assign(pagination, {
    current: 1,
    pageSize: 10,
    total: 0,
  });
  getTableList();
});

const getGroupList = async () => {
  multipleSelection.value = [];
  groupLoading.value = true;
  const res = await noticeGroupApi.getGroupList();
  cards.value = res || [];
  groupLoading.value = false;

  if (!current.value) {
    current.value = cards.value[0]?.groupId || "";
    getTableList();
  }
};

const handleEditGroup = (group) => {
  handleEditMerchant(group);
};

const handleDelGroup = (group) => {
  ElMessageBox.confirm(`确定要删除通知组${group.groupName}吗？`, "提示", {
    type: "warning",
    center: true,
    autofocus: false,
  })
    .then(async () => {
      noticeGroupApi
        .delGroup({
          groupId: group.groupId,
        })
        .then(() => {
          ElMessage.success("删除成功");
          current.value = "";
          multipleSelection.value = [];
          Object.assign(pagination, {
            current: 1,
            pageSize: 10,
            total: 0,
          });
          getGroupList();
        });
    })
    .catch(() => {});
};

const editDialogRef = ref();
const handleEditMerchant = (rowData) => {
  const params = {
    title: rowData ? "编辑分组" : "创建分组",
    rowData: rowData ? { ...rowData } : "",
  };

  editDialogRef.value.open(params);
};

const userDialogRef = ref();
const handleAddUser = () => {
  const params = {
    title: `添加${currentTitle.value}团队成员`,
    rowData: {
      userIds: [],
      groupId: current.value,
    },
  };

  userDialogRef.value.open(params);
};

const handleDelUser = (group) => {
  ElMessageBox.confirm(`确定要移除成员${group.userName}吗？`, "提示", {
    type: "warning",
    center: true,
    autofocus: false,
  })
    .then(async () => {
      noticeGroupApi
        .delUser({
          groupId: current.value,
          userId: group.userId,
        })
        .then(() => {
          ElMessage.success("移除成功");
          pagination.current = 1;
          refreshTable();
        });
    })
    .catch(() => {});
};

const handleDelUsers = () => {
  if (!multipleSelection.value.length) {
    return ElMessage.warning("请选择批量移除的用户");
  }
  ElMessageBox.confirm(`确定要进行批量移除成员吗？`, "提示", {
    type: "warning",
    center: true,
    autofocus: false,
  })
    .then(async () => {
      noticeGroupApi
        .delUsers({
          groupId: current.value,
          userIds: multipleSelection.value.map((item) => {
            return item.userId;
          }),
        })
        .then(() => {
          ElMessage.success("批量移除成功");
          pagination.current = 1;
          refreshTable();
        });
    })
    .catch(() => {});
};

const refreshList = async () => {
  await getGroupList();
  await getTableList();
};

const refreshTable = async () => {
  await getTableList();
  await getGroupList();
};

onMounted(() => {
  getGroupList();
});
</script>

<style lang="scss" scoped>
.main-container {
  width: 100%;
  height: 100%;

  .observer-alarm-el-container {
    height: 100%;
  }

  .aside {
    border-right: 1px solid #f0f0f0;
    position: relative;
    overflow: visible;

    .aside-wrapper {
      height: 100%;
      background-color: white;

      .aside-inner {
        display: flex;
        flex-direction: column;
        height: 100%;
        padding: 20px 10px 16px 20px;

        .aside-title {
          display: flex;
          justify-content: space-between;
          padding-right: 10px;
        }
        .aside-btn {
          margin: 16px 0;
          padding-right: 10px;
        }

        .aside-cards {
          flex: 1;
          overflow: auto;
          padding-right: 10px;
        }
      }
    }

    .icon-fold-btn {
      position: absolute;
      top: 0;
      bottom: 0;
      right: -10px;
      margin: auto;
      cursor: pointer;
    }
  }

  .aside-hide {
    border-right: none;
    padding: 0;
  }

  .main-wrapper {
    background-color: white;
    padding: 16px 20px;

    .main-title {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 16px;

      div:first-child {
        font-weight: bold;
        font-size: 16px;
      }
    }
  }
}

.unselect {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  opacity: 0;
  transition: opacity 1.5s ease-in;

  .unselect-content {
    text-align: center;

    .unselect-tips {
      display: flex;

      div:nth-child(2) {
        margin-left: 20px;
      }
    }

    .unselect-bottom {
      margin-top: 20px;
    }
  }
}

.show-guide {
  opacity: 1;
}
.card-status {
  display: flex;
  align-items: center;
  position: relative;

  i {
    display: inline-block;
    width: 6px;
    height: 6px;
    margin-right: 6px;
    background: #bfbfbf;
    border-radius: 50%;
  }
  span {
    font-size: 14px;
    color: #262626;
  }

  .enable {
    background: #12b312;
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
