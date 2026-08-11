<template>
  <div
    class="base-application-el-menu-vertical"
    :style="{
      width: !isCollapse ? '200px' : '64px',
    }"
  >
    <el-menu
      :collapse="isCollapse"
      :default-active="activeMenuItem"
      :default-openeds="defaultOpenedsMenu"
    >
      <template
        v-for="(group, index) in props.activeMenu?.menuChild"
        :key="index"
      >
        <div
          :class="{
            'base-tag': true,
            'collapse-base-tag': isCollapse,
            ell: true,
          }"
          v-if="group.isgroup"
          v-tooltip
        >
          {{ group.menuName }}
        </div>
        <el-menu-item
          v-if="group.ismenu"
          @click="goPage(group)"
          :index="group.menuPath"
        >
          <FontIcon :icon="group.icon" />
          <template #title>
            <span class="ml-8 ell" v-tooltip>{{
              group.menuName
            }}</span></template
          >
        </el-menu-item>
        <template
          v-for="(menu, i) in group.isgroup ? group.children : [group]"
          :key="i"
          v-else
        >
          <el-menu-item
            v-if="menu.ismenu"
            :index="menu.menuPath"
            @click="goPage(menu)"
          >
            <FontIcon :icon="menu.icon" />
            <template #title>
              <span class="ml-8 ell" v-tooltip>{{ menu.menuName }}</span>
            </template>
          </el-menu-item>
          <el-sub-menu :index="menu.menuId" v-else>
            <template #title>
              <FontIcon :icon="menu.icon" />
              <span v-if="!isCollapse" class="ml-8 ell" v-tooltip>{{
                menu.menuName
              }}</span>
            </template>
            <el-menu-item
              v-for="subMenu in menu.children"
              :key="subMenu.menuPath"
              :index="subMenu.menuPath"
              @click="goPage(subMenu)"
              class="base-menu-item ell"
              v-tooltip
            >
              {{ subMenu.menuName }}
            </el-menu-item>
          </el-sub-menu>
        </template>
      </template>
    </el-menu>
    <div
      class="menu-footer"
      :style="{
        width: !isCollapse ? '200px' : '64px',
        left:
          userStore.currentMenu?.moduleId === 'RESOURCE_CONTROL'
            ? '64px'
            : '0px',
      }"
    >
      <div class="collapse-bg" @click="isCollapse = !isCollapse">
        <FontIcon :icon="isCollapse ? 'icon-suojin2' : 'icon-suojin'" />
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, onMounted, watch, nextTick, onUnmounted } from "vue";
import type { PropType } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useUserStore } from "@/stores/modules/user";
import { useConfigStore } from "@/stores/modules/microConfig";
import { useMenuStore } from "@/stores/modules/menu";
const configStore = useConfigStore();
const { menuClosed } = configStore;

const userStore = useUserStore();
const menutore = useMenuStore();
const route = useRoute();

const router = useRouter();

const props = defineProps({
  activeMenu: {
    type: Object as PropType<any>,
  },
  isAuth: {
    type: Boolean,
    default: false,
  },
});

function travel(menuItems) {
  let result = [];
  for (const item of menuItems) {
    if (!item.ismenu && !item.isgroup) {
      result.push(item.menuId);
    }
    if (item.children && item.children.length > 0) {
      const subResult = travel(item.children);
      result = result.concat(subResult);
    }
  }
  return result;
}
const defaultOpenedsMenu = menuClosed
  ? []
  : travel(props.activeMenu?.menuChild || []);

onMounted(() => {
  window.onresize = () => {
    if (window.innerWidth < 1280) {
      isCollapse.value = true;
    } else {
      isCollapse.value = false;
    }
  };
});

onUnmounted(() => {
  menutore.setActiveMenuItem("");
})

const activeMenuItem = ref(""); //当前激活的菜单路径

const isCollapse = ref(false);

router.isReady().then(() => {
  if (route.fullPath === "/control" || route.fullPath === "/404"|| route.fullPath === "/401" ) return;
  menutore.setActiveMenuItem(route.fullPath);
  activeMenuItem.value = route.fullPath;
  getMenuLocation(route.fullPath); //详情页定位菜单
});

const goPage = (item) => {
  nextTick(() => {
    if (item.isoutlink) {
      window.open(item.menuPath);
      return;
    }
    item.path = item.menuPath;
    activeMenuItem.value = item.path;
    menutore.setActiveMenuItem(item.path);
    // if (location.pathname.split("#")[0] === item.path.split("#")[0]) {
    //   window.location.hash = item.path.split("#")[1];
    // } else {
    window.history.pushState({}, null, item.path);
    // }
  });
};
// 查找激活的submenu
const getMenuLocation = (path) => {
  if (!props.activeMenu) return;
  const menuList = userStore.menuList;
  menuList.some((menu) => {
    if (path.includes(menu)) {
      activeMenuItem.value = menu;
    }
  });
};

watch(
  () => route.fullPath,
  () => {
    getMenuLocation(route.fullPath);
  }
);
</script>

<style scoped lang="scss">
.base-tag {
  padding-left: 12px;
  font-size: 12px;
  margin-top: 8px;
  height: 36px;
  color: var(--basic-el-menu-base-tag-text-color);
  opacity: var(--basic-el-menu-base-tag-text-opacity);
  line-height: 36px;
  &:nth-child(1) {
    margin-top: 0;
  }
}
.collapse-base-tag {
  padding-left: 0;
  white-space: nowrap;
  position: relative;
  text-align: center;
  width: 50px;
  left: 50%;
  transform: translateX(-50%);
}
.base-application-el-menu-vertical {
  overflow-y: auto;
  background-color: var(--basic-el-menu-bg-color-custom);
  &::-webkit-scrollbar {
    display: none;
  }
  &:hover {
    &::-webkit-scrollbar {
      display: block;
    }
  }
  span,
  li {
    -webkit-user-select: none; /*webkit浏览器*/
    user-select: none;
  }
  .menu-footer {
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    background-image: url("@/assets/imgs/menu_footer_bg.png");
    background-size: cover;
    width: var(--basic-el-menu-footer-width);
    height: var(--basic-el-menu-footer-height);
    &:hover {
      // background: var(--basic-el-menu-item-hover-bg-color);
    }
    .collapse-bg {
      width: 40px;
      height: 40px;
      border-radius: 4px;
      position: absolute;
      cursor: pointer;
      bottom: 12px;
      left: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
      color: var(--basic-el-menu-text-color-custom);
    }
  }
}
</style>
