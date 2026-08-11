<template>
  <div class="base-nav-bar">
    <div class="base-left">
      <img
        class="base-logo"
        :src="themeMode === 'dark' ? logoDarkImg : logoLightImg"
        @click="backConsole"
        alt=""
      />
      <el-divider direction="vertical" style="height: 18px; margin: 0 16px" />
      <div class="base-menu-title">
        {{ props.activeMenu?.title }}
      </div>
      <SelectorWithLabel
        id="workspace-box"
        :value="selectVal"
        @change="selectChange"
        :options="selectOptions"
        placeholder="请选择"
        v-if="!['usermanage'].includes(key)"
      >
        <template v-slot:label> 工作空间 </template>
      </SelectorWithLabel>
    </div>
    <div class="base-right">
      <div
        class="base-console"
        id="console-back-link"
        @click="backConsole"
        v-if="props.activeMenu?.key !== 'control' && userInfo.workSpaceList.length"
      >
        工作台
      </div>

      <div class="base-document" id="document-center-link" @click="goDocument">
        文档中心
      </div>
      <el-divider direction="vertical" style="height: 16px" />
      <div class="base-user" v-if="token">
        <div class="user-icon">{{ firstChar }}</div>
        <el-popover
          placement="bottom"
          :width="256"
          :show-arrow="false"
          popper-class="user-operate-popper"
          v-model:visible="popperVisible"
        >
          <template #reference>
            <span class="el-dropdown-link" @mouseenter="popperVisible = true">
              {{ username }}
              <FontIcon :icon="popperVisible ? 'icon-shang' : 'icon-xia'" />
            </span>
          </template>
          <ul class="user-operate">
            <li class="version">
              <div style="margin-right: 2px">版本</div>
              V4.1.1
            </li>
            <li @click="handleEditMerchant(userInfo.workspace_id)">账号管理</li>
            <li @click="handleEditPassword({ userId: userInfo.user_id })">
              密码修改
            </li>
            <li class="gap"></li>
            <li class="theme">
              <div class="title">主题配置</div>
              <div>
                <img
                  src="@/assets/imgs/control/light.png"
                  alt=""
                  :style="`margin-right: 14px;border-color: ${
                    themeMode === 'light' ? '#1F69FF' : '#fff'
                  };`"
                  @click="themeModeChange('light')"
                />
                <img
                  src="@/assets/imgs/control/dark.png"
                  alt=""
                  :style="`border-color: ${
                    themeMode === 'dark' ? '#1F69FF' : '#fff'
                  };`"
                  @click="themeModeChange('dark')"
                />
              </div>
              <el-radio-group v-model="themeMode" @change="themeModeChange">
                <el-radio label="light">浅色模式</el-radio>
                <el-radio label="dark">深色模式</el-radio>
              </el-radio-group>
            </li>
            <li class="gap"></li>
            <li @click="logout">退出登录</li>
          </ul>
        </el-popover>
      </div>
      <el-link v-else @click="handleLoginClick">登录</el-link>
    </div>

    <Passwordform ref="editPasswordDialogRef" @refreshList="() => {}" />
    <DialogForm ref="editDialogRef" @refreshList="() => {}" :hideSpace="true" />
  </div>
</template>

<script lang="ts" setup>
import { PropType, computed, ref, watch, onMounted } from "vue";
import SelectorWithLabel from "@/components/SelectorWithLabel.vue";
import { useUserStore } from "@/stores/modules/user";
import ControlApi from "@/apis/control";
import Passwordform from "@/views/usermanage/pool/components/passwordform.vue";
import DialogForm from "@/views/usermanage/pool/components/dialogform.vue";
import { useConfigStore } from "@/stores/modules/microConfig";
import { redirectLogout } from "@/plugin/qiankun/action";
import logoDarkImg from "@/assets/brand/logo-dark.svg";
import logoLightImg from "@/assets/brand/logo-light.svg";
import userApi from "@/apis/user";
import { useMenuStore } from "@/stores/modules/menu";
const menutore = useMenuStore();
const userStore = useUserStore();
const { userInfo, setUserInfo, getUserInfoFromCookie } = userStore;
const configStore = useConfigStore();
const guideState = JSON.parse(userStore.userInfo.guideState || "{}");

const props = defineProps({
  activeMenu: {
    type: Object as PropType<any>,
  },
});

const themeMode = ref("light"); //主题样式
const popperVisible = ref(false);

const key = computed(() => {
  return userStore.majorPathName;
});
const selectVal = computed(() => userInfo.workspace_id);
const selectOptions = computed(() => {
  const { userInfo } = userStore;
  return userInfo.workSpaceList.map((item) => ({
    label: item.workSpaceName,
    value: item.workSpaceId,
    ...item,
  }));
});

function selectChange(option) {
  setUserInfo({
    ...getUserInfoFromCookie(),
    workspace_id: option[0].value,
  });
  location.replace(menutore.activeMenuItem);
  // reload();
}
const backConsole = () => {
  history.pushState({}, "", "/control");
};

const logout = () => {
  userStore.logout();
};
const handleLoginClick = () => {
  redirectLogout();
};

const token = computed(() => {
  return userInfo.token;
});
const username = computed(() => {
  return userInfo.nickName;
});
const firstChar = computed(() => {
  if (!username.value) return "无";
  return username.value[0];
});

const editPasswordDialogRef = ref();
const handleEditPassword = (rowData) => {
  const params = {
    title: "修改密码",
    rowData: rowData ? { ...rowData } : null,
  };

  editPasswordDialogRef.value.open(params);
};

const editDialogRef = ref();
const handleEditMerchant = (workspaceId) => {
  ControlApi.getUserInfo(workspaceId).then((res) => {
    const params = {
      title: "账号管理",
      key: "acount",
      workSpaceList: userInfo.workSpaceList ? userInfo.workSpaceList : [],
      rowData: {
        userId: res.userId,
        account: res.account,
        nickName: res.nickName,
        phone: res.phone,
        email: res.email,
      },
    };

    editDialogRef.value.open(params);
  });
};

const goDocument = () => {
  window.open(configStore.$state.documentUrl);
};

const themeModeChange = (val) => {
  // 从cookie中获取最新的guideState
  const currentGuideState = JSON.parse(
    getUserInfoFromCookie().guideState || {}
  );
  currentGuideState.themeMode = val;
  userApi
    .guideState({
      guideState: JSON.stringify(currentGuideState),
    })
    .then((res) => {
      userStore.setUserInfo({
        guideState: JSON.stringify(currentGuideState),
      });
      themeMode.value = val;
      document.body.className = val;
    });
};

function reload(delay = 10) {
  setTimeout(() => {
    window.location.reload();
  }, delay);
}
onMounted(() => {
  themeMode.value = guideState.themeMode ? guideState.themeMode : "light";
  document.body.className = themeMode.value;
});
</script>

<style lang="scss" scoped>
.base-nav-bar {
  display: flex;
  align-items: center;
  height: 56px;
  justify-content: space-between;
  padding: 18px 24px 18px 20px;
  box-sizing: border-box;
  background-color: var(--theme-background);
  color: var(--theme-color-primary-1);
  box-shadow: var(--theme-boxshadow);
  position: relative;
  z-index: 999;
  :deep(.basic-el-divider--vertical) {
    border-color: var(--theme-divider-vertical-border-color);
  }
}

.base-logo {
  width: 110px;
  height: 20px;
  margin-right: 2px;
  cursor: pointer;
}

.base-menu-title {
  font-size: 16px;
  font-weight: 550;
  margin: 0 32px 0 0;
  line-height: 32px;
  color: var(--theme-color);
}

.base-left {
  display: flex;
  align-items: center;
  :deep(.base-selector-with-label) {
    .base-selector-label {
      color: var(--theme-color-primary-1);
      background-color: var(--theme-base-selector-label-background);
      border-color: var(--theme-base-selector-border-color);
      width: 80px;
    }
    .basic-el-input__wrapper {
      background-color: var(--theme-background);
      border: 1px solid var(--theme-base-selector-border-color);
      box-shadow: none;
      height: 32px;
      width: 160px;

      .basic-el-input__inner {
        color: var(--theme-color-primary-1);
      }
    }
  }
}

.base-right {
  display: flex;
  justify-content: space-between;
  align-items: center;
  .base-console {
    text-align: center;
    width: 66px;
    height: 32px;
    border-radius: 2px;
    line-height: 32px;
    cursor: pointer;
    &:hover {
      background-color: var(--hover-theme-background);
      color: var(--hover-theme-color);
    }
  }
  .base-document {
    text-align: center;
    background-color: var(--theme-background);
    width: 76px;
    height: 32px;
    line-height: 32px;
    border-radius: 2px;
    margin-right: 4px;
    cursor: pointer;
    &:hover {
      background-color: var(--hover-theme-background);
      color: var(--hover-theme-color);
    }
  }
  .base-user {
    margin-left: 12px;
    display: flex;
    align-items: center;
    .user-icon {
      width: 32px;
      height: 32px;
      border-radius: 16px;
      text-align: center;
      line-height: 32px;
      background-color: #1f69ff;
      margin-right: 8px;
      color: #fff;
    }
    img {
      margin-left: 3px;
    }
  }
}
</style>
<style lang="scss">
.user-operate-popper {
  margin-top: 4px;
  .user-operate {
    margin-bottom: 0;
    li {
      height: 36px;
      width: 232px;
      border-radius: 2px;
      padding: 0 12px;
      line-height: 36px;
      cursor: pointer;
      color: #232a35;
    }
    li:hover {
      background: #f0f3fa;
    }
    .version {
      color: #cfcfcf;
      cursor: default;
      display: flex;
      height: 20px;
      line-height: 20px;
      margin-bottom: 6px;
    }
    .version:hover {
      background: #fff;
    }
    .gap {
      width: 232px;
      height: 1px;
      background: #ebedf1;
      margin: 12px 0;
      line-height: 0;
      padding: 0;
    }
    .theme {
      height: auto;
      .title {
        font-weight: 500;
        line-height: 22px;
        margin: 7px 0 15px 0;
      }
      &:hover {
        background: none;
      }
      img {
        width: 96;
        height: 72px;
        vertical-align: bottom;
        border: 2px solid #1f69ff;
        border-radius: 4px;
      }
      .basic-el-radio-group {
        gap: 14px;
        width: 100%;
        flex-wrap: nowrap;
        .basic-el-radio {
          font-size: 12px;
          height: 20px;
          width: 96px;
          justify-content: center;
          margin: 0;
          .basic-el-radio__inner {
            border-color: #d6dbe3;
          }
          .basic-el-radio__label {
            padding-left: 4px;
            font-size: 12px;
            color: #232a35;
          }
        }
      }
    }
  }
}
// 修改用户信息弹框样式
.basic-app-workspace-dialog {
  .basic-app-workspace-tag {
    background: #f1f2f5;
    border: 0.5px solid #d9d9d9;
    color: #262626;
    height: 24px;
    line-height: 24px;
    border-radius: 2px;
    padding: 0 7px;
    text-align: center;
    font-size: 12px;
    display: inline-block;
    margin-right: 8px;
    margin-bottom: 8px;
  }
}
</style>
