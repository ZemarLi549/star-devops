<template>
  <div class="control-card" id="user-info-card">
    <div class="card-header">
      <span class="card-header-title">账号与权限</span>
      <div class="link" @click="goUserManage('/usermanage/space')">
        查看更多
        <FontIcon
          style="font-size: 18px;"
          icon="icon-you"
        />
      </div>
    </div>
    <div class="card-content">
      <div class="user-info">
        <div class="user-info-avatar"></div>
        <div class="user-info-detail">
          <div class="username">{{ userInfo.nickName || "-" }}</div>
          <div class="account">账号ID: {{ userInfo.account || "-" }}</div>
          <div class="account">飞书ID: {{ userInfo.feishuUserId || "-" }}</div>
          <div class="account">
            IT 工作台ID: {{ userInfo.itWorkbenchUserId || "-" }}
          </div>
        </div>
        <div class="user-info-tag" v-tooltip>{{ workspace.roleName || "-" }}</div>
      </div>
      <el-row :gutter="15" class="user-role">
        <el-col :span="8" v-for="item in roleAuthorityMsg" :key="item.title">
          <div class="user-role-item" @click="goUserManage(item.link)">
            <div class="text">
              {{ item.title }}
              <div class="icon-hover">
                <FontIcon
                  style="font-size: 18px;"
                  icon="icon-you"
                />
              </div>
            </div>
            <div class="number">{{ getAuthorityNumber(item.type) }}</div>
          </div>
        </el-col>
      </el-row>
      <el-row :gutter="15" class="user-handle">
        <el-col :span="8" v-for="item in roleAuthorityMsg">
          <div class="user-handle-item" @click="goUserManage(item.link)">{{ item.btn_text }}</div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup lang="ts">
import { roleAuthorityMsg } from "../constant";
import { computed } from "vue";
import { useUserStore } from "@/stores/modules/user";
import { ElMessage } from "element-plus";
const userStore = useUserStore();

const userInfo = computed(() => {
  return {
    account: userStore.userInfo.user_name,
    nickName: userStore.userInfo.nickName,
    feishuUserId:
      userStore.userInfo.feishuUserId || userStore.userInfo.feishu_user_id,
    itWorkbenchUserId:
      userStore.userInfo.itWorkbenchUserId ||
      userStore.userInfo.it_workbench_user_id ||
      userStore.userInfo.thirdWorkbenchUserId,
  };
});
const workspace = computed(() => {
  const { workspace_id, workSpaceList } = userStore.userInfo;
  return workSpaceList.find((item) => item.workSpaceId === workspace_id) || {};
});

const goUserManage = (path) => {
  if (!userStore.hasPathAuth(path))
    return ElMessage.warning("暂无使用权限，请联系相关管理员");
  window.open(path);
  // window.history.pushState({}, null, path);
};

const getAuthorityNumber = (title: string) => {
  switch (title) {
    case "user":
      return workspace.value.userCount || 0;
    case "role":
      return workspace.value.roleCount || 0;
    case "space":
      return userStore.userInfo.workSpaceList.length || 0;
    default:
      return 0;
  }
};
</script>

<style scoped lang="scss">
.card-content {
  margin-top: 24px;

  .user-info {
    padding: 20px 24px;
    border-radius: 8px;
    overflow: hidden;
    background-image: url("@/assets/imgs/control/role_bg.png");
    background-size: 384px 88px;
    position: relative;
    display: flex;
    .user-info-avatar {
      width: 48px;
      height: 48px;
      background-image: url("@/assets/imgs/control/avatar.png");
      background-size: 48px 48px;
    }
    .user-info-detail {
      flex: 1;
      display: flex;
      flex-direction: column;
      justify-content: center;
      margin-left: 12px;
      gap: 4px;
      color: #232a35;
      .username {
        font-size: 16px;
        font-weight: 500;
      }
      .account {
        opacity: 0.6;
        font-size: 12px;
        font-weight: 400;
        line-height: 1.5;
      }
    }
    .user-info-tag {
      position: absolute;
      top: 0;
      right: 0;
      color: #fff;
      background: #1f69ff;
      width: 72px;
      height: 22px;
      line-height: 22px;
      text-align: center;
      font-size: 12px;
      font-weight: 500;
      border-bottom-left-radius: 8px;
      box-shadow: -5px 8px 20px 0px rgba(39, 103, 232, 0.2),
        0px 1px 1px 0px rgba(255, 255, 255, 0.5) inset;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
      padding: 0 4px;
    }
  }

  .user-role-item {
    padding: 16px;
    padding-right: 0;
    border-radius: 2px;
    margin: 16px 0;
    cursor: pointer;
    &:hover {
      background: #f5f7fc;
      .icon-hover {
        display: inline-block;
      }
    }
    .text {
      font-size: 12px;
      color: #5f7292;
      display: flex;
      align-items: center;
      gap: 5px;
      height: 20px;
      line-height: 20px;
    }
    .number {
      font-size: 24px;
      font-weight: 700;
      line-height: 28px;
      margin-top: 12px;
    }
    .icon-hover {
      display: none;
    }
  }

  .user-handle-item {
    height: 32px;
    line-height: 32px;
    text-align: center;
    border: 1px solid #dfe2ea;
    border-radius: 2px;
    cursor: pointer;
    &:hover {
      background: #f5f7fc;
    }
  }
}
</style>
