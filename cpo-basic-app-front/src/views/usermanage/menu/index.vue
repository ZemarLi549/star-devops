<template>
  <div class="manage-menu-page table-box">
    <!-- <div class="page-top">
      <div class="page-title">菜单管理</div>
    </div> -->
    <el-container class="page-container">
      <el-aside width="250px" v-loading="menuloading">
        <el-container>
          <el-header>
            <el-form>
              <el-form-item label="服务模块" class="platform-select">
                <el-select v-model="platform" @change="handlePlaformChange">
                  <el-option
                    v-for="item in platformList"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                  />
                </el-select>
              </el-form-item>
            </el-form>
          </el-header>
          <el-main class="!p-0">
            <el-scrollbar style="max-height: 100%">
              <el-tree
                ref="menu"
                class="menu"
                node-key="menuId"
                :data="menuList"
                :props="menuProps"
                highlight-current
                check-strictly
                :expand-on-click-node="false"
                :default-expand-all="true"
                @node-click="menuClick"
              >
                <template #default="{ node, data }">
                  <span class="custom-tree-node">
                    <span class="label">
                      <FontIcon
                        style="margin-right: 8px"
                        v-if="data.icon"
                        :icon="data.icon"
                      />
                      <span v-tooltip>{{ node.label }}</span>
                    </span>
                    <span class="do">
                      <el-button
                        link
                        plain
                        :icon="Plus"
                        size="small"
                        @click.stop="add(node, data)"
                      />
                      <el-button
                        link
                        plain
                        size="small"
                        v-if="data.menuId != '0'"
                        :icon="Delete"
                        @click.stop="delMenu(node, data)"
                      />
                    </span>
                  </span>
                </template>
              </el-tree>
            </el-scrollbar>
          </el-main>
        </el-container>
      </el-aside>
      <el-container>
        <el-main class="!p-[16px] main-content" ref="main">
          <saveCom
            ref="save"
            :menu="menuList"
            :platform="platform"
            @save="getMenu"
          />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>
<script setup lang="ts">
import { ref, onMounted, nextTick } from "vue";
import saveCom from "./save.vue";

import { ElMessageBox, ElMessage } from "element-plus";
import { Plus, Delete } from "@element-plus/icons-vue";
import MenuApi from "@/apis/usermanage/menu";
import { PROJECT_MAP } from "@/globals/constant";
import isMicroProjectDeploy from "@/utils/isMicroProjectDeploy";
import { useConfigStore } from "@/stores/modules/microConfig";
const configStore = useConfigStore();

const platformList = [
  { label: "工作台", value: "WORK_BENCH" },
  { label: "账号权限", value: "RESOURCE_CONTROL" },
  {
    label: "服务中心",
    value: "BUSINESS_OBSERVATION",
    key: [
      PROJECT_MAP.OBSERVE_TRACE,
      PROJECT_MAP.OBSERVE_METRIC,
      PROJECT_MAP.OBSERVE_LOG,
    ],
  },
  {
    label: "AI 自动化",
    value: "ALARM_MANAGEMENT",
    key: PROJECT_MAP.OBSERVE_ALARM,
  },
  {
    label: "微服务接入",
    value: "ENVIRONMENT_CONFIGURATION",
    key: PROJECT_MAP.OBSERVE_CONFIG,
  },
].filter((item) => {
  if (configStore.singleDepoly && item.value === "WORK_BENCH") {
    return false;
  } else {
    return !item.key || isMicroProjectDeploy(item.key);
  }
});

// 平台
const platform = ref(platformList[0].value);
let newMenuIndex = 1;

const menuloading = ref(false);
const menuList = ref([]);
const menuProps = ref({
  label: (data) => {
    return data.menuName;
  },
});

onMounted(() => {
  getMenu();
});
// 切换平台
const handlePlaformChange = () => {
  getMenu();
  save.value.setData("");
};

// 获取平台菜单
const getMenu = async (selectMenu?) => {
  menuloading.value = true;
  return await MenuApi.list(platform.value)
    .then((res: any) => {
      menuList.value = [
        {
          menuId: "0",
          menuName: "全部",
          children: res || [],
        },
      ];
      if (selectMenu) {
        nextTick(() => {
          menu.value.setCurrentKey(selectMenu.menuId);
        });
      } else {
        nextTick(() => {
          menu.value.setCurrentKey(0);
        });
      }
    })
    .finally(() => {
      menuloading.value = false;
    });
};
const menu = ref();
const save = ref();
//树点击
const currentId = ref("");
const menuClick = (data, node) => {
  currentId.value = data.menuId;
  save.value.setData(data.menuId);
};
//增加
const add = async (node?, data?) => {
  const newMenuName = "未命名" + newMenuIndex;
  const parentId = data ? data.menuId : "0";
  MenuApi.add({
    moduleType: platform.value,
    parentId,
    menuName: newMenuName,
    isgroup: false,
    ismenu: true,
  }).then((res: any) => {
    getMenu().then(() => {
      // currentId.value = res.data;
      // menu.value.setCurrentKey(res.data);
      // save.value.setData(res.data);
      newMenuIndex++;
    });
  });
};
//删除菜单
const delMenu = async (node?, data?) => {
  const confirm = await ElMessageBox.confirm(
    `确认删除菜单${data.menuName}吗？`,
    "提示",
    {
      type: "warning",
      center: true,
      autofocus: false,
      confirmButtonText: "确认",
      cancelButtonText: "取消",
      beforeClose: (action, instance, done) => {
        if (action === "confirm") {
          instance.confirmButtonLoading = true;
          MenuApi.delete(data.menuId)
            .then(() => {
              done();
              ElMessage({
                message: "操作成功",
                type: "success",
                showClose: true,
              });
              getMenu();
              if (data.menuId == currentId.value) {
                save.value.setData("");
              }
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
</script>
<style scoped lang="scss">
.basic-el-main {
  --basic-el-main-padding: 16px !important;
}
.manage-menu-page {
  margin: 0 !important;
  // width: calc(100% - 75px);
  width: 100%;
  .basic-el-container {
    height: 100%;
    .basic-el-header,
    .basic-el-footer {
      display: flex;
      align-items: center;
    }
    .basic-el-header {
      border-bottom: 1px solid #e4e7ed;
    }
    .basic-el-footer {
      border-top: 1px solid #e4e7ed;
    }
  }
  .basic-el-aside {
    background-color: #ffffff;
  }
  .page-title {
    height: 48px;
    line-height: 48px;
    padding: 0 16px;
    font-size: 16px;
    background: #ffffff;
    border-bottom: 1px solid rgba(0, 0, 0, 0.06);
  }

  .platform-select {
    margin-bottom: 0 !important;
  }
  .page-container {
    height: 100%;
    :deep(.el-aside) {
      height: 100%;
      background: #fff;
    }
  }
  .menu:deep(.el-tree-node__label) {
    display: flex;
    flex: 1;
    height: 100%;
  }
  .menu:deep(.el-tree-node__content) {
    height: 36px;
  }
  .custom-tree-node {
    display: flex;
    flex: 1;
    align-items: center;
    justify-content: space-between;
    font-size: 14px;
    height: 100%;
    padding-right: 12px;
  }
  .custom-tree-node .label {
    display: flex;
    align-items: center;
    height: 100%;
    flex: 1;
    width: 0;

    span {
      display: inline-block;
      overflow: hidden;
      white-space: nowrap;
      text-overflow: ellipsis;
      width: 100%;
    }
  }
  .custom-tree-node .label .el-tag {
    margin-left: 5px;
  }
  .custom-tree-node .do {
    display: none;
    .basic-el-button {
      margin-left: 2px;
      margin-right: 0;
    }
  }
  .custom-tree-node .do i {
    margin-left: 3px;
    color: #999;
  }
  .custom-tree-node .do i:hover {
    color: #333;
  }

  .custom-tree-node:hover .do {
    display: inline-block;
  }

  .main-content {
    :deep(.basic-el-card) {
      border-radius: 4px;
      box-shadow: 0px 2px 4px 0px #e5e5e5;
    }
  }
}
</style>
