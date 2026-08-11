<template>
  <ChildLayout>
    <div class="business-group-container">
      <!-- <div class="toast">
        <div
          class="title-tag align-items-start"
          v-if="showTag"
          style="margin-bottom: 16px"
        >
          <el-icon size="16" class="icon-tips"><InfoFilled /></el-icon>
          <div class="title-tag-content">
            数据单元是定义数据访问权限的最小隔离级别，创建数据单元会自动生成一个token，配置token到数据采集端进行数据接入。业务组反应的是当前工作空间下业务组织架构或产品线的划分可以分配数据单元到业务组节点下，通过业务树去管理数据访问。
          </div>
          <el-icon class="icon-close" size="16" @click="showTag = false"
            ><Close
          /></el-icon>
        </div>
      </div> -->
      <el-container>
        <el-aside
          :width="asideWidthString"
          :class="['aside', { 'aside-hide': !asideWidth }]"
        >
          <div class="aside-wrapper" v-show="asideWidth">
            <div class="aside-wrapper-title">业务组</div>
            <div class="aside-wrapper-operator">
              <el-button @click="handleDataUnitClick('create', true)" v-btn
                >新建数据单元</el-button
              >
              <el-button
                @click="handleDataUnitClick('create', false)"
                v-btn
                :disabled="!treeData || !treeData.length"
                >创建分组</el-button
              >
            </div>
            <el-input
              v-model="filterText"
              :prefix-icon="Search"
              placeholder="请输入关键词"
              class="mb-16"
            ></el-input>
            <div class="tree-container">
              <el-tree
                class="tree"
                ref="treeRef"
                :data="treeData"
                :props="defaultProps"
                :allow-drop="allowDrop"
                :allow-drag="allowDrag"
                @node-drag-end="nodeDragEnd"
                @node-click="handleNodeClick"
                :filter-node-method="filterNode"
                node-key="dataGroupId"
                :default-checked-keys="[currentDataGroupId]"
                v-loading="treeLoading"
                draggable
                highlight-current
                :default-expanded-keys="defaultExpandedArr"
              >
                <template #default="{ node, data }">
                  <span
                    class="custom-tree-node"
                    @mouseover.stop="handleTreeNodeMouseover(node)"
                  >
                    <div class="label-group" :title="node.label">
                      <Icon
                        class="icon"
                        :icon-name="getTreeIconName(data)"
                        size="middle"
                      ></Icon>
                      <span class="label">{{ node.label }}</span>
                    </div>
                    <span
                      class="btn-group"
                      v-if="node.id === currentHoverTreeId"
                    >
                      <el-icon
                        @click.stop="
                          handleDataUnitClick('edit', data.iselement, data)
                        "
                        ><EditPen
                      /></el-icon>
                      <el-icon
                        style="margin-left: 8px"
                        @click.stop="handleDeleteDataUnit(data)"
                        ><Delete
                      /></el-icon>
                    </span>
                  </span>
                </template>
              </el-tree>
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
        <el-main class="table-container">
          <el-row justify="space-between" class="mb-16">
            <el-row :span="4" class="table-container-unit">
              <template v-if="currentDataGroupName">
                <span>{{ currentDataGroupName }}</span>
                <el-tooltip
                  class="box-item"
                  effect="dark"
                  :content="currentDataGroupToken"
                >
                  <div
                    v-if="currentDataIsEle"
                    style="margin: 0 12px 2px 0; cursor: pointer"
                    size="small"
                    @click="copyToken(currentDataGroupToken)"
                    class="copy-token"
                  >
                    Token
                    <el-icon><DocumentCopy /></el-icon>
                  </div>
                </el-tooltip>
              </template>
            </el-row>
            <el-row
              :span="4"
              class="table-container-tips"
              @click="tipsDialog.open()"
            >
              如何使用
            </el-row>
          </el-row>
          <template v-if="currentDataIsEle && treeData.length">
            <el-row justify="space-between" class="mb-16">
              <el-row :span="4">
                <el-input
                  v-model="name"
                  placeholder="请输入应用名称"
                  :prefix-icon="Search"
                ></el-input>
              </el-row>
              <el-row :span="4">
                <el-button
                  type="primary"
                  @click="handleApplicationClick('create')"
                  :disabled="!currentDataIsEle"
                  v-btn
                  >新建应用</el-button
                >
                <el-button @click="handleDeleteApplication(null)"
                  >批量删除</el-button
                >
              </el-row>
            </el-row>
            <el-table
              :data="tableData"
              v-if="tableData"
              style="width: 100%"
              v-loading="tableLoading"
              @selection-change="handleTableSelect"
              show-overflow-tooltip
            >
              <template #empty><Empty /></template>
              <el-table-column type="selection" width="55" />
              <el-table-column
                prop="applicationName"
                label="应用名称"
                min-width="200"
              />
              <el-table-column prop="remark" label="应用描述" min-width="300">
                <template #default="scope">
                  {{ scope.row.remark || "-" }}
                </template>
              </el-table-column>
              <el-table-column
                prop="createUserName"
                label="创建人"
                min-width="100"
              />
              <el-table-column
                prop="createTime"
                label="创建时间"
                min-width="160"
              />
              <el-table-column fixed="right" label="操作" width="120">
                <template #default="{ row }">
                  <el-button
                    link
                    type="primary"
                    @click="handleApplicationClick('edit', row)"
                    >编辑</el-button
                  >
                  <el-button
                    link
                    type="primary"
                    @click="handleDeleteApplication(row)"
                    >删除</el-button
                  >
                </template>
              </el-table-column>
              <!-- <template #empty>
                <EmptyVue />
              </template> -->
            </el-table>
            <el-pagination
              v-if="tableData && tableData.length !== 0"
              class="pagination mt-16"
              v-model:current-page="currentPage"
              v-model:page-size="pageSize"
              :page-sizes="[10, 20, 50, 100]"
              layout="prev, pager, next, sizes, slot"
              :total="total"
              @size-change="handleSizeChange"
              @current-change="handleCurrentChange"
            >
              <div
                style="position: absolute; left: 0"
                v-if="tableChecked.length > 0"
              >
                {{ `已选 ${tableChecked.length} 条数据` }}
              </div>
              <div v-else style="position: absolute; left: 0">
                {{ `共 ${total} 条数据` }}
              </div>
            </el-pagination>
          </template>
          <div
            class="empty-wrapper-business"
            v-if="!currentDataIsEle && treeData && treeData.length"
          >
            <img src="@/assets/imgs/usermanage/noBusiness.png" alt="暂无数据" />
            <div class="text">业务分组用于管理数据单元的层级</div>
          </div>

          <div v-if="treeData && treeData.length === 0" class="empty-wrapper">
            <img src="@/assets/imgs/noData.png" alt="暂无数据" />
            <div class="text">请先添加一个数据单元（Token）为应用接入准备</div>
            <el-button
              type="primary"
              @click="handleDataUnitClick('create', true)"
              v-btn
              >新建数据单元</el-button
            >
          </div>
        </el-main>
      </el-container>
      <DataUnitDialog
        ref="dataUnit"
        @success="handleDataUnitSuccess"
      ></DataUnitDialog>
      <ApplicationDialog
        ref="application"
        :data-group-id="currentDataGroupId"
        @success="handleApplicationSuccess"
      ></ApplicationDialog>
      <TipsDialog ref="tipsDialog" />
    </div>
  </ChildLayout>
</template>
<script lang="ts" setup>
import { Search, DocumentCopy } from "@element-plus/icons-vue";
import { ref, watch, onMounted, onUnmounted, computed, nextTick } from "vue";
import DataUnitDialog from "./components/DataUnitDialog.vue";
import ApplicationDialog from "./components/ApplicationDialog.vue";
import { ElMessageBox } from "element-plus";
import ChildLayout from "../ChildLayout.vue";
import {
  ApplicationRow,
  BusinessTree,
  BusinessTreeRes,
  PageDto,
  PageRes,
} from "@/apis/usermanage/businessGroup/types";
import businessGroupApi from "@/apis/usermanage/businessGroup";
import userApi from "@/apis/user";
import { ElMessage } from "element-plus";
import { useAuthStore } from "@/stores/modules/authStore";
import { useUserStore } from "@/stores/modules/user";
import Icon from "@/components/Icon.vue";
import TipsDialog from "./components/TipsDialog.vue";
import Empty from "@/components/empty/TableEmpty.vue";

const authStore = useAuthStore();
const userStore = useUserStore();

const tipsDialog = ref(null);

const showTag = ref(true);
const filterText = ref("");
const currentDataGroupId = ref<number | string>("");
const currentDataIsEle = ref(false);
const currentDataGroupToken = ref("");
const currentDataGroupName = ref("");
const defaultExpandedArr = ref([]); //默认展开的节点
const name = ref("");
watch(name, () => {
  currentPage.value = 1;
  initTable();
});

const handleNodeClick = (data: BusinessTree) => {
  currentDataGroupId.value = data.dataGroupId;
  currentDataIsEle.value = data.iselement;
  currentDataGroupToken.value = data.dataGroupToken;
  currentDataGroupName.value = data.dataGroupName;
  initTable();
};

const filterNode = (value: string, data: BusinessTree) => {
  if (!value) return true;
  return data.dataGroupName.includes(value);
};
const treeRef = ref(null);
watch(filterText, (val) => {
  treeRef.value!.filter(val);
});
const treeData = ref<BusinessTreeRes | null>(null);
const treeLoading = ref(false);
const workSpaceId = ref(authStore.$state.authSpaceId);
// const workSpaceId = computed(() => {
//   return authStore.authSpaceId;
// });

// watch(workSpaceId, () => {
//   currentDataGroupId.value = "";
//   initTree();
//   initTable();
// });

const initTree = async () => {
  treeLoading.value = true;
  try {
    const data = await businessGroupApi.dataGroupTree({
      workSpaceId: workSpaceId.value,
    });
    treeData.value = data || [];
  } catch (error) {
  } finally {
    treeLoading.value = false;
  }
};

const findUnitData = (arr, dataGroupName = undefined) => {
  return arr.some((item) => {
    if (dataGroupName) {
      if (item.dataGroupName === dataGroupName) {
        // setTimeout(() => {
        currentDataGroupId.value = item.dataGroupId;
        currentDataIsEle.value = item.iselement;
        currentDataGroupToken.value = item.dataGroupToken;
        currentDataGroupName.value = item.dataGroupName;
        treeRef.value.setCurrentKey(currentDataGroupId.value);
        // }, 0);
        return item.iselement;
      } else {
        return findUnitData(item["child"], dataGroupName);
      }
    } else {
      if (item.iselement) {
        // setTimeout(() => {
        currentDataGroupId.value = item.dataGroupId;
        currentDataIsEle.value = item.iselement;
        currentDataGroupToken.value = item.dataGroupToken;
        currentDataGroupName.value = item.dataGroupName;
        treeRef.value.setCurrentKey(currentDataGroupId.value);
        // }, 0);
        return item.iselement;
      } else {
        return findUnitData(item["child"]);
      }
    }
  });
};

const defaultProps = {
  children: "child",
  label: "dataGroupName",
};

const tableData = ref<ApplicationRow[] | null>(null);

const tableParams = computed<PageDto>(() => ({
  name: name.value,
  pageNo: currentPage.value,
  pageSize: pageSize.value,
  dataGroupId: currentDataGroupId.value,
  workSpaceId: workSpaceId.value,
}));
const tableLoading = ref(false);
const initTable = async () => {
  tableLoading.value = true;
  tableChecked.value = [];
  tableData.value = null; //解决暂无数据一闪而过
  try {
    const data = await businessGroupApi.page({ ...tableParams.value });
    total.value = data.total;
    tableData.value = data.records || [];
  } catch (error) {
  } finally {
    tableLoading.value = false;
  }
};

const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);
// watch([currentPage, pageSize], () => {
//   initTable();
// });
const handleSizeChange = (val: number) => {
  currentPage.value = 1;
  initTable();
  console.log(`${val} items per page`);
};
const handleCurrentChange = (val: number) => {
  console.log(`current page: ${val}`);
  initTable();
};

const dataUnit = ref(null);
const handleDataUnitClick = (type: string, isElement: boolean, data?: any) => {
  const config = { type, isElement, data };
  dataUnit.value.open(config);
};
const application = ref(null);
const handleApplicationClick = (action: string, data?: ApplicationRow) => {
  application.value.open(action, data);
};

const handleDeleteDataUnit = (node: BusinessTree) => {
  const { iselement } = node;
  let title = "提示";
  let message =
    "业务分组删除后，其下所有数据单元将移动到第一层级，确定要删除该业务分组吗？";
  if (iselement) {
    title = "删除数据单元";
    message = "删除选中的数据单元，将同时删除关联的应用，删除后无法撤销";
  }
  ElMessageBox.confirm(message, title, {
    type: "warning",
    autofocus: false,
    center: true,
  }).then(() => {
    deleteDataUnit(node.dataGroupId, iselement);
  });
};

const deleteDataUnit = async (dataGroupId: number, iselement: boolean) => {
  const params = {
    dataGroupId,
    workSpaceId: workSpaceId.value,
    iselement,
  };
  try {
    await businessGroupApi.dataGroupDelete(params);
    ElMessage.success("删除成功");
    currentDataGroupName.value = "";
    initTree().then(() => {
      if (dataGroupId == currentDataGroupId.value) {
        findUnitData(treeData.value);
      } else {
        treeRef.value.setCurrentKey(currentDataGroupId.value);
      }
      initTable();
    });
  } catch (error) {
    ElMessage.error("删除失败");
  }
};

const currentHoverTreeId = ref(-1);
const handleTreeNodeMouseover = (node: any) => {
  currentHoverTreeId.value = node.id;
};
const event = () => {
  currentHoverTreeId.value = -1;
  currentHoverIcon.value = "";
};
onMounted(() => {
  if (workSpaceId.value) {
    initTree().then(() => {
      findUnitData(treeData.value);
      initTable();
    });
    isGuideShow();
  }
  document.addEventListener("mouseover", event, false);
});
onUnmounted(() => {
  document.removeEventListener("mouseover", event, false);
});

const handleDeleteApplication = (row?: ApplicationRow) => {
  let applicationId = tableChecked.value;
  let str = "已选";
  let title = "提示";
  if (row) {
    str = "当前";
    applicationId = [row.applicationId];
    title = "提示";
  }
  if (!row && !applicationId.length) return ElMessage.warning("请选择数据");
  ElMessageBox.confirm(`删除后不可恢复,确认要删除${str}应用吗？`, title, {
    type: "warning",
    center: true,
    autofocus: false,
  }).then(async () => {
    try {
      await businessGroupApi.applicationDelete({ applicationId });
      ElMessage.success("删除成功");
      currentPage.value = 1;
      initTable();
    } catch (error) {}
  });
};

const handleDataUnitSuccess = (formData, type) => {
  const { parentId, dataGroupName } = formData;
  initTree().then(() => {
    findUnitData(treeData.value, dataGroupName);
    treeRef.value.setCurrentKey(currentDataGroupId.value);
    changeTreeNodeStatus(treeRef.value.store.root);
    defaultExpandedArr.value = [parentId];
    initTable();
  });
};

const handleApplicationSuccess = () => {
  initTable();
};

const tableChecked = ref([]);
const handleTableSelect = (selection) => {
  tableChecked.value = selection.map((item) => item.applicationId);
};

// 只允许同级拖拽
const allowDrop = (draggingNode, dropNode, type) => {
  // 被放置数据单元
  if (dropNode.data.iselement) {
    return type === "prev" || type === "next";
  } else {
    return type === "inner";
  }
};
const allowDrag = (draggingNode) => {
  return draggingNode.data.iselement;
};

const nodeDragEnd = (before, after, inner) => {
  const reqData = {
    workSpaceId: workSpaceId.value,
    dataGroupId: before.data.dataGroupId,
  };
  // 放置业务组下
  if (!after.data.iselement) {
    reqData["parentId"] = after.data.dataGroupId;
  } else {
    // 放置数据单元
    reqData["parentId"] = after.data.parentId;
  }
  businessGroupApi
    .moveDataGroup(reqData)
    .then((res) => {
      console.log(res);
    })
    .catch((err) => {
      console.log(err);
    });
};

const copyToken = (token) => {
  const textarea = document.createElement("textarea");
  // textarea.value = JSON.stringify(token);
  textarea.value = token;
  document.body.appendChild(textarea);
  textarea.select();
  document.execCommand("copy");
  document.body.removeChild(textarea);
  ElMessage.success("复制成功");
};

const currentHoverIcon = ref("");
const handleMouseoverIcon = (type) => {
  currentHoverIcon.value = type;
};
const getIconName = (type) => {
  return currentHoverIcon.value === type ? `${type}-active` : type;
};
const getTreeIconName = (data: BusinessTree) => {
  return data.iselement ? "icon-dataunit" : "icon-business-group";
};
const asideWidthString = computed(() => asideWidth.value + "px");
const asideWidth = ref(260);
const handleFoldAside = () => {
  asideWidth.value = asideWidth.value ? 0 : 260;
};
const foldBtnStyle = computed(() => {
  const deg = asideWidth.value ? 0 : 180;
  return {
    transform: `rotate(${deg}deg)`,
  };
});
// 点击收起
// const shouqi = () => {
//   changeTreeNodeStatus(treeRef.value.store.root);
// };
const changeTreeNodeStatus = (node) => {
  node.expanded = false;
  for (let i = 0; i < node.childNodes.length; i++) {
    //改变节点的自身expanded状态
    node.childNodes[i].expanded = false;
    if (node.childNodes[i].childNodes.length > 0) {
      changeTreeNodeStatus(node.childNodes[i]);
    }
  }
};

const isGuideShow = () => {
  // 判断提示框是否展示
  const guideState = JSON.parse(userStore.userInfo.guideState);
  const dataGroupManager = guideState.dataGroupManager;
  if (dataGroupManager) {
    tipsDialog.value?.open();
  }
  guideState.dataGroupManager = false;
  userApi
    .guideState({
      guideState: JSON.stringify(guideState),
    })
    .then((res) => {
      userStore.setUserInfo({
        guideState: JSON.stringify(guideState),
      });
      console.log(res);
    });
};

watch(
  () => authStore.$state.authSpaceId,
  () => {
    workSpaceId.value = authStore.$state.authSpaceId;
    initTree().then(() => {
      findUnitData(treeData.value);
      initTable();
    });
  },
  {
    deep: true,
  }
);
</script>
<style lang="scss" scoped>
.business-group-container {
  flex: 1;
  // padding: 10px;
  background: #fff;
  border-radius: 4px;
  margin: 20px;
  min-width: 0;
  display: flex;
  flex-direction: column;
  // border-radius: 4px;
  // box-shadow: 0px 2px 4px 0px #e5e5e5;
  .basic-el-container {
    flex: 1;
    min-height: 0;
    border-radius: 4px;
    box-shadow: 0px 2px 4px 0px #e5e5e5;
  }

  .toast {
    background-color: rgb(246 248 255);
    .align-items-start {
      height: 68px;

      line-height: 22px;
      .icon-tips {
        color: #1f69ff;
        margin: 6px 0 0 8px;
        font-size: 16px;
      }
      .icon-close {
        margin: 6px 8px 0 24px;
      }
    }
    .title-tag-content {
      align-self: center;
      margin-right: 56px;
    }
  }
  :deep(.basic-el-timeline) {
    .basic-el-timeline-item__wrapper {
      padding-left: 18px;
    }
    // ul li {
    //   &::nth-child(2) {
    //     padding-bottom: 0 !important;
    //   }
    // }
    li:nth-child(3) {
      padding-bottom: 0;
    }
  }

  :deep(.basic-el-dialog__header) {
    .basic-el-dialog__title {
      height: 24px;
      font-size: 16px;
      // font-family: PingFangSC, PingFangSC-Medium;
      font-weight: 600;
      line-height: 24px;
    }
  }
  :deep(.basic-el-dialog__body) {
    padding-top: 14px;
  }
  .empty-wrapper {
    margin: auto;
    position: relative;
    width: 300px;
    text-align: center;
    img {
      width: 61px;
      height: 55px;
      margin-top: 50px;
    }
    .text {
      font-size: 12px;
      line-height: 30px;
      color: #595959;
      margin-bottom: 10px;
    }
  }
}
.aside {
  border-right: 1px solid #ccc;
  padding: 16px;
  position: relative;
  overflow: visible;
  max-width: 240px;
  .aside-wrapper {
    height: 100%;
    display: flex;
    flex-direction: column;
    .aside-wrapper-title {
      font-size: 16px;
      font-weight: 600;
    }
    .aside-wrapper-operator {
      display: flex;
      justify-content: space-between;
      margin: 12px 0 8px 0;
      :deep(.basic-el-button + .basic-el-button) {
        margin-left: 8px;
        width: 84px;
      }
    }
  }

  .tree-container {
    overflow: auto;
    flex: 1;
    min-height: 0;
    &::-webkit-scrollbar {
      display: none;
    }
    &:hover {
      &::-webkit-scrollbar {
        display: block;
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
.table-container {
  display: flex;
  flex-direction: column;
  .table-container-unit {
    font-size: 16px;
    font-weight: 600;
    .copy-token {
      font-size: 12px;
      margin: 2px 0 0 12px !important;
      color: #595959;
      line-height: 22px;
      &:hover {
        color: var(--color-primary);
      }
    }
  }
  .table-container-tips {
    font-size: 12px;
    color: var(--color-primary);
    cursor: pointer;
    &:hover {
      // color: var(--color-primary);
      // font-weight: 550;
    }
  }
}
.pagination {
  justify-content: end;
  position: relative;
}
.mt-16 {
  margin-top: 16px;
}
.mb-16 {
  margin-bottom: 16px;
}
.icons {
  font-size: 20px;
  text-align: right;
  .icon {
    margin: 0 6px;
    cursor: pointer;
  }
}
.custom-tree-node {
  flex: 1;
  min-width: 0;
  display: flex;
  justify-content: space-between;
  align-items: center;
  .btn-group {
    flex: none;
    margin-bottom: -2px;
    margin-right: 4px;
  }
  .label-group {
    text-overflow: ellipsis;
    display: box;
    overflow: hidden;
    .icon {
      margin-right: 6px;
    }
  }
}
.cell-container {
  display: flex;
  width: 100%;
  overflow: hidden;
  span {
    text-overflow: ellipsis;
    overflow: hidden;
  }
  .icon {
    flex: none;
    margin-left: 6px;
    cursor: pointer;
  }
}

:deep(.basic-el-tree) {
  font-weight: 400;
  .basic-el-tree-node.is-current > .basic-el-tree-node__content {
    color: #1f69ff;
  }
}
.empty-wrapper-business {
  margin: auto;
  position: relative;
  width: 300px;
  text-align: center;

  img {
    width: 145px;
    height: 116px;
    margin-top: 50px;
  }
  .text {
    font-size: 12px;
    line-height: 30px;
    color: #595959;
    margin-bottom: 50px;
  }
}
</style>
