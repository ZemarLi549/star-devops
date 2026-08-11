<template>
  <div :class="cardClass">
    <div class="list-card-item_detail bg-bg_color">
      <div class="list-card-item_detail_body" @click="handleMemberManage()">
        <el-row justify="space-between">
          <div class="list-card-item_detail--title items-center">
            <div :class="cardLogoClass">
              <img
                style="width: 22px; height: 22px"
                src="@/assets/imgs/usermanage/gongzuokongjian.png"
                alt=""
              />
            </div>
            <p
              class="list-card-item_detail--name text-text_color_primary"
              :title="product.workSpaceName"
            >
              {{ product.workSpaceName }}
            </p>
          </div>
        </el-row>
        <div>
          <el-row>
            <el-col :span="12"
              ><span style="color: #8c8c8c">成员：</span
              >{{ product.count ? product.count + "个" : "0" }}</el-col
            >
            <el-col :span="12"
              ><span style="color: #8c8c8c">空间ID：</span
              >{{ product.workSpaceId }}</el-col
            >
          </el-row>
          <el-row>
            <el-col class="text-ellipsis"
              ><span style="color: #8c8c8c">创建时间：</span
              >{{ product.createTime || "-" }}</el-col
            >
          </el-row>
          <el-row>
            <el-col>
              <div class="text-ellipsis" :title="product.remark">
                <span style="color: #8c8c8c">描述：</span
                >{{ product.remark || "-" }}
              </div>
            </el-col>
          </el-row>
        </div>
      </div>
      <el-row class="list-card-item_detail--route" :gutter="5">
        <el-col :span="8" @click="handleMemberManage()">
          <div style="transform: rotate(90deg); margin-right: 4px">
            <!-- <Icon iconName="dashboard_active" /> -->
            <FontIcon icon="icon-daochu" />
          </div>
          进入空间
        </el-col>
        <el-col :span="8" @click="handleEditMerchant()">
          <div style="margin-right: 4px">
            <!-- <Icon iconName="update" /> -->
            <FontIcon icon="icon-bianji" />
          </div>
          编辑
        </el-col>
        <el-col
          :span="8"
          :class="{ disable: product.isdefault }"
          @click="handleClickDelete()"
        >
          <div style="margin-right: 4px">
            <!-- <Icon iconName="delete-four" /> -->
            <FontIcon icon="icon-shanchu1" />
          </div>
          删除
        </el-col>
      </el-row>
    </div>
  </div>
</template>
<script lang="ts" setup>
import { computed, PropType } from "vue";
import { useAuthStore } from "@/stores/modules/authStore";
import { useUserStore } from "@/stores/modules/user";
import userApi from "@/apis/user";
import { MODULE_ID } from "@/globals/constant";
import { ElMessage } from "element-plus";
import { FontIcon } from "@/components/ReIcon";
import { useConfigStore } from "@/stores/modules/microConfig";

const userStore = useUserStore();

const authStore = useAuthStore();
defineOptions({
  name: "ReCard",
});

interface CardProductType {
  workSpaceId: string;
  remark: string;
  workSpaceName: string;
  count: string;
  createTime: string;
  isdefault: boolean;
}

const props = defineProps({
  product: {
    type: Object as PropType<CardProductType>,
  },
});

const emit = defineEmits([
  "manage-item",
  "delete-item",
  "view-key",
  "member-item",
]);

const handleClickManage = () => {
  emit("manage-item", props.product);
};

const handleClickDelete = () => {
  !props.product.isdefault && emit("delete-item", props.product);
};

const handleEditMerchant = () => {
  emit("manage-item", props.product);
};
// 进入空间
const handleMemberManage = async () => {
  // 更新store
  const { workSpaceList } = userStore.userInfo;
  if (
    !workSpaceList.find(
      (item) => item.workSpaceId === String(props.product.workSpaceId)
    )
  ) {
    await userStore.getWorkspaceList();
  }

  const configStore = useConfigStore();

  const isChangeSpaceSuccess = configStore.singleDepoly
    ? await getMenusSingleDepoly(props.product.workSpaceId)
    : await getMenus(props.product.workSpaceId);
  if (isChangeSpaceSuccess) {
    authStore.setAuthSpaceId(props.product.workSpaceId);
    authStore.setAuthSpaceName(props.product.workSpaceName);
    emit("member-item", props.product);
  }
};

const getMenus = async (workspace_id) => {
  try {
    const menus = await userApi.menu(workspace_id);
    const menu = menus.find(
      (item) => item.module === MODULE_ID.RESOURCE_CONTROL
    );
    if (!menu) {
      ElMessage.warning("该空间暂无权限管理！");
      return false;
    } else {
      const spaceMenu = menu.children.find(
        (item) => item.menuPath === "/usermanage/space"
      );
      if (!spaceMenu) {
        ElMessage.warning("该空间暂无空间管理！");
        return false;
      } else {
        const path = findFirstNonNullMenuPath(spaceMenu.children);
        const { setUserInfo } = userStore;
        setUserInfo({
          workspace_id: workspace_id + "",
          menus,
        });
        window.history.pushState({}, null, path);
        return true;
      }
    }
  } catch (error) {
    console.log(error);
  }
};
// 独立部署
const getMenusSingleDepoly = async (workspace_id) => {
  try {
    const menus = await userApi.menu(workspace_id);
    const menusTemplate = userStore.menusHandler(menus);
    const menu = menusTemplate.find(
      (item) => item.module === MODULE_ID.RESOURCE_CONTROL
    );
    if (!menu) {
      ElMessage.warning("该空间暂无权限管理！");
      return false;
    } else {
      const spaceMenu = menu.children.find(
        (item) => item.menuPath === "/usermanage/space"
      );
      if (!spaceMenu) {
        ElMessage.warning("该空间暂无空间管理！");
        return false;
      } else {
        const path = findFirstNonNullMenuPath(spaceMenu.children);
        const { setUserInfo } = userStore;
        setUserInfo({
          workspace_id: workspace_id + "",
          menus: menusTemplate,
        });
        window.history.pushState({}, null, path);
        return true;
      }
    }
  } catch (error) {
    console.log(error);
  }
};

// 递归函数，用于遍历菜单树
function findFirstNonNullMenuPath(menuTree) {
  for (const menu of menuTree) {
    if (menu.menuPath !== null) {
      return menu.menuPath;
    }
    if (menu.children && menu.children.length > 0) {
      const path = findFirstNonNullMenuPath(menu.children);
      if (path !== null) {
        if (menu.menuPath !== null) {
          return menu.menuPath + path;
        }
        return path;
      }
    }
  }
  return null;
}

const cardClass = computed(() => [
  "list-card-item",
  // { "list-card-item__disabled": !props.product.isSetup }
]);

const cardLogoClass = computed(() => [
  "list-card-item_detail--logo",
  // { "list-card-item_detail--logo__disabled": !props.product.isSetup }
]);

// const { toClipboard } = useClipboard();
const copyText = async (e) => {
  // await toClipboard(props.product.projectId);
};
</script>
<style scoped lang="scss">
p {
  margin: 0;
}

.basic-el-col {
  margin: 4px 0;
}
.list-card-item {
  display: flex;
  flex-direction: column;
  margin-bottom: 12px;
  border-radius: 4px;
  overflow: hidden;
  border: 1px solid #ececec;
  .list-card-item_detail--name {
    font-size: 18px;
  }

  &:hover {
    box-shadow: 0px 4px 10px 0px rgba(23, 24, 32, 0.12);
    border: 1px solid var(--color-primary);
    transition: box-shadow 0.3s;
  }

  &_detail {
    flex: 1;
    min-height: 140px;
    &_body {
      padding: 20px 20px 15px;
      cursor: pointer;
      &:hover {
        .list-card-item_detail--name {
          color: var(--color-primary);
        }
      }
    }
    &--title {
      display: flex;
      width: calc(100% - 30px);
      align-items: center;
      flex: 1 1 0%;
      margin-bottom: 20px;
    }

    &--title:hover {
      color: var(--color-primary);
    }

    &--logo {
      width: 23px;
      height: 22px;

      &__disabled {
        color: #a1c4ff;
      }
    }

    &--operation {
      display: flex;
      height: 100%;
      svg:hover {
        color: var(--el-color-primary);
      }
      &--tag {
        border: 0;
      }
    }

    &--name {
      font-size: 14px;
      margin-left: 8px;
      text-overflow: ellipsis;
      font-weight: 550;
      width: calc(100% - 65px);
      overflow: hidden;
      white-space: nowrap;
    }

    &--items {
      font-size: 14px;
      line-height: 14px;
      margin: 16px 0;
    }

    &--desc {
      font-size: 12px;
      line-height: 20px;
      overflow: hidden;
      text-overflow: ellipsis;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      margin: 10px 0;
      height: 40px;
    }

    &--route {
      // border-top: 1px solid #eee;
      text-align: center;
      font-size: 14px;
      color: var(--el-text-color-regular);
      .basic-el-col {
        color: #595959;
        padding: 10px 0;
        display: flex;
        justify-content: center;
        align-items: center;
        &:not(:last-child) {
          position: relative;
          border-right: 1px solid #fff;
        }
        &:not(:last-child)::after {
          content: "";
          position: absolute;
          right: -1px;
          width: 1px;
          height: 16px;
          background-color: #ececec;
        }
      }
      .basic-el-col:hover {
        color: #1f69ff;
        cursor: pointer;
        .pay-channel-tips {
          color: var(--el-text-color-secondary);
        }
      }
      // svg,
      // i {
      //   font-size: 15px;
      //   display: block;
      //   margin: 0 auto;
      // }

      :deep(.el-col) {
        cursor: pointer;
      }
      .pay-channel-tips {
        color: var(--el-text-color-secondary);
        padding-left: 20px;
        font-size: 14px;
        height: 40px;
        line-height: 40px;
        text-align: left;
        cursor: default;
        display: flex;
        .el-button {
          margin-left: 10px;
        }
      }
    }
  }

  &__disabled {
    cursor: not-allowed;
    opacity: 0.6;
  }
}
.app-id {
  cursor: text;
}

.disable {
  cursor: not-allowed !important;
  color: rgb(179, 179, 179) !important;
}

.text-ellipsis {
  width: 100%;
  text-overflow: ellipsis;
  overflow: hidden;
  white-space: nowrap;
}
</style>
