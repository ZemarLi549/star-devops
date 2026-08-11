<template>
  <div class="base-wrapper">
    <div class="base-header">
      <div @click="goSpace" class="first-title">空间</div>
      <el-icon class="icon"><ArrowRight /></el-icon>
      <span class="second-title">{{ `${spaceName} (id:${spaceId})` }}</span>
    </div>
    <div class="base-container">
      <SideBar :activeMenu="secondeMenu" :isAuth="true" />
      <slot></slot>
    </div>
  </div>
</template>

<script lang="ts" setup>
import SideBar from "./SideBar.vue";
import { ref, onMounted, computed } from "vue";
import SpaceApi from "@/apis/usermanage/space";
import { ArrowRight } from "@element-plus/icons-vue";
import { useAuthStore } from "@/stores/modules/authStore";
import { useUserStore } from "@/stores/modules/user";
const userStore = useUserStore();

const authStore = useAuthStore();

const spaceList = ref([]);
const spaceId = ref("");
const spaceName = ref("");

const secondeMenu = computed(() => {
  return {
    menuChild: authStore.$state.secondMenu,
  };
});

const getCardListData = async () => {
  const res = await SpaceApi.list();
  spaceList.value = res || [];
  spaceId.value = userInfo.workspace_id;
  const temp = spaceList.value.find(
    (item) => item.workSpaceId == spaceId.value
  );
  spaceName.value = temp.workSpaceName;
  authStore.setAuthSpaceName(temp.workSpaceName || "");
  authStore.setAuthSpaceId(spaceId.value);
};

const { userInfo, setUserInfo } = userStore;

const goSpace = () => {
  window.history.pushState({}, null, "/usermanage/space");
};

onMounted(() => {
  getCardListData();
});
</script>
<style scoped lang="scss">
.base-wrapper {
  height: 100%;
  flex: 1;
  min-width: 0;
}
.base-header {
  font-size: 14px;
  padding: 0 16px;
  height: 46px;
  line-height: 46px;
  border-bottom: 1px solid var(--basic-el-border-color);
  display: flex;
  // justify-content: space-between;\
  align-items: center;
  background-color: #fff;
  font-weight: 600;
  :deep(.basic-el-input__wrapper) {
    border: none;
    box-shadow: none;
    width: auto;
  }
  .icon {
    font-size: 14px;
    color: #8c8c8c;
    margin: 0 4px;
  }
  .first-title {
    cursor: pointer;
    color: #8c8c8c;
    &:hover {
      color: var(--color-primary);
    }
  }
  .second-title {
    color: #262626;
  }
}
.base-container {
  height: calc(100vh - 102px);
  display: flex;
}
</style>
