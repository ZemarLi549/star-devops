<template>
  <div class="base-application-app-wrapper">
    <component
      :is="isSingleDeploy ? NavBarSingleDeploy : NavBar"
      v-if="!['login'].includes(key) && !isfullscreen"
      :activeMenu="activeMenu"
    ></component>
    <div
      class="base-application-main-container"
      :style="`height: calc(100vh - ${isfullscreen ? 0 : 56}px);`"
    >
      <SideBar
        v-if="
          !['control', 'login', 'usermanage', '404', '401'].includes(key) &&
          !isfullscreen
        "
        :activeMenu="activeMenu"
      />
      <AppMain
        :show-child="
          !['control', 'login', 'usermanage', '404', '401'].includes(key)
        "
      />
    </div>
  </div>
</template>
<script lang="ts">
export default {
  name: "Index",
};
</script>

<script lang="ts" setup>
import { useUserStore } from "@/stores/modules/user";
import { useConfigStore } from "@/stores/modules/microConfig";
import { AppMain, SideBar, NavBar } from "./components";
import { NavBarSingleDeploy } from "./singleDeployComponents";
import { computed, ref, watch, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useMenuStore } from "@/stores/modules/menu";
const menutore = useMenuStore();

const route = useRoute();
const router = useRouter();
const userStore = useUserStore();

const configStore = useConfigStore();

const isSingleDeploy = computed(() => {
  return configStore.singleDepoly;
});

const key = computed(() => {
  return userStore.majorPathName || "login";
});

const activeMenu = computed(() => {
  return userStore.currentMenu;
});

const isfullscreen = ref(false);
isfullscreen.value = route.fullPath.includes("isfullscreen");


watch(
  () => route.fullPath,
  () => {
    isfullscreen.value = route.fullPath.includes("isfullscreen");
    if (isfullscreen.value && route.fullPath.includes("isDrawer")) {
      document.body.style.minWidth = "600px";
    } else {
      document.body.style.minWidth = "1280px";
    }
  }
);

onMounted(() => {
  // onMounted在router.beforeEach之前执行导致问题，因此添加判断
  const { userInfo, updateWorkspaceId, getMenus, setActiveMenu } =
    useUserStore();
  if (userInfo?.token) {
    updateWorkspaceId().then(async () => {
      if (useUserStore().userInfo?.workspace_id) {
        await getMenus();
        router.isReady().then(() => {
          setActiveMenu(route.path); //更新菜单 路由拦截里的更新菜单执行过早 所以这里需要再次执行
        });
      } else {
        history.pushState({}, null, "/401");
      }
    });
  }
});
</script>
<style lang="scss" scoped>
.base-application-app-wrapper {
  height: 100%;
}

.base-application-main-container {
  height: calc(100vh - 57px);
  display: flex;
}
</style>
