<template>
  <div class="basic-home">
    <el-menu :defaultActive="activeMenu" class="auth-menu">
      <el-menu-item
        v-for="(item, index) in usermenuList"
        :key="index"
        :index="item.menuPath"
        @click="goPage(item)"
      >
        <div class="auth-menu-item">
          <FontIcon :icon="item.icon" v-if="item.icon" />
          <div v-tooltip class="ell">{{ item.menuName }}</div>
        </div>
      </el-menu-item>
    </el-menu>
    <router-view />
  </div>
</template>

<script lang="ts" setup>
// import { usermenuList } from "@/menu";
import { ref, watch, computed } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useUserStore } from "@/stores/modules/user";
import { useAuthStore } from "@/stores/modules/authStore";
const userStore = useUserStore();
const authStore = useAuthStore();
const route = useRoute();

const usermenuList = computed(() => {
  return userStore.currentMenu?.menuChild;
});

const activeMenu = ref();
watch(
  () => route.path,
  () => {
    usermenuList.value?.forEach((item, index) => {
      if (route.path.includes(item.menuPath)) {
        if (Array.isArray(item.children)) {
          authStore.setSecondMenu(item.children);
        }
        activeMenu.value = item.menuPath;
      }
    });
  },
  {
    immediate: true,
  }
);

function goPage(item) {
  if (Array.isArray(item.children)) {
    authStore.setSecondMenu(item.children);
  }
  window.history.pushState({}, null, item.menuPath);
}
</script>

<style lang="scss" scoped>
.basic-home {
  padding: 0;
  overflow-y: auto;
  height: 100%;
  display: flex;
  .auth-menu {
    width: 64px;
    :deep(.basic-el-menu-item) {
      margin: 8px 8px 0 !important;
      width: 48px;
      height: 61px;
      padding: 0 !important;
      line-height: 0;
      border-radius: 4px;
      // flex-direction: column;
      .auth-menu-item {
        width: 48px;
        height: 100%;
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        .iconfont {
          width: 16px;
          margin: 20px 0 8px;
          height: 0;
          flex: 1;
        }
        div {
          width: 100%;
          height: 22px;
          line-height: 22px;
          text-align: center;
          margin-bottom: 6px;
        }
      }
    }
    .basic-el-menu-item.is-active {
      background-color: #f7f8fa !important;
    }
  }
}
</style>

<style>
.user-content {
  width: calc(100% - 64px);
}

.user-content-wrap {
  height: 100%;
  overflow: auto;
  padding: 16px;
}

.bg-bg_color {
  background: #ffffff;
}

.p16 {
  padding: 16px;
}

.minHeight {
  min-height: 100%;
}
</style>
