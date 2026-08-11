<template>
  <el-menu
    :style="{
      width: !isCollapse ? '200px' : '64px',
      overflowY: 'auto',
      padding: '8px 8px 40px',
    }"
    :class="['is-auth-el-menu']"
    :collapse="isCollapse"
    :default-active="activeMenuItem"
  >
    <template
      v-for="(group, index) in props.activeMenu?.menuChild"
      :key="index"
    >
      <!-- <div class="base-tag" v-if="!isCollapse && !group.ismenu">
        {{ group.menuName }}
      </div> -->
      <div class="base-tag ell" v-tooltip v-if="!isCollapse && group.isgroup">
        {{ group.menuName }}
      </div>
      <el-menu-item
        v-if="group.ismenu"
        @click="goPage(group)"
        :index="group.menuPath"
      >
        <FontIcon :icon="group.icon" />
        <template #title>
          <span class="ml-8 ell" v-tooltip>{{ group.menuName }}</span></template
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
    <div
      class="menu-footer"
      @click="isCollapse = !isCollapse"
      :style="{
        width: !isCollapse ? '200px' : '64px',
        left: currentMenu.moduleId === 'RESOURCE_CONTROL' ? '64px' : '0px',
      }"
    >
      <span class="menu-footer-icon">
        <FontIcon :icon="isCollapse ? 'icon-suojin2' : 'icon-suojin'" />
      </span>
    </div>
  </el-menu>
</template>

<script lang="ts" setup>
import { ref, onMounted, watch, nextTick } from "vue";
import type { PropType } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useUserStore } from "@/stores/modules/user";
const { currentMenu } = useUserStore();

const route = useRoute();

const router = useRouter();

const props = defineProps({
  activeMenu: {
    type: Object as PropType<any>,
  },
});

onMounted(() => {
  window.onresize = () => {
    if (window.innerWidth < 1200) {
      isCollapse.value = true;
    } else {
      isCollapse.value = false;
    }
  };
});

const activeMenuItem = ref(""); //当前激活的菜单路径

const isCollapse = ref(false);

router.isReady().then(() => {
  if (route.fullPath === "/control") return;
  activeMenuItem.value = route.fullPath;
});

const goPage = (item) => {
  nextTick(() => {
    if (item.isoutlink) {
      window.open(item.menuPath);
      return;
    }
    item.path = item.menuPath;
    activeMenuItem.value = item.path;
    // if (location.pathname.split("#")[0] === item.path.split("#")[0]) {
    //   window.location.hash = item.path.split("#")[1];
    // } else {
    window.history.pushState({}, null, item.path);
    // }
  });
};
</script>

<style scoped lang="scss">
.base-tag {
  padding-left: 16px;
  font-size: 12px;
  margin-top: 16px;
  height: 32px;
  color: #8c8c8c;
  line-height: 32px;
  &:nth-child(1) {
    margin-top: 0;
  }
}
.is-auth-el-menu {
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
  .basic-el-menu-item.is-active {
    background-color: #f7f8fa !important;
  }
  .basic-el-menu-item,
  :deep(.basic-el-sub-menu__title) {
    height: 40px;
    --basic-el-menu-base-level-padding: 16px;
    // gap: 8px;
    // i {
    //   margin-right: 8px;
    // }
    .ml-8 {
      margin-left: 8px;
    }
  }
  .submenu {
    display: flex;
  }
  .active-menu {
    display: flex;
  }

  .menu-footer {
    position: fixed;
    height: 40px;
    bottom: 0;
    left: 0;
    right: 0;
    background-color: #fff;
    border-right: solid 1px var(--basic-el-menu-border-color);
    .menu-footer-icon {
      position: absolute;
      top: 12px;
      left: 24px;
      cursor: pointer;
    }
  }
}
</style>
