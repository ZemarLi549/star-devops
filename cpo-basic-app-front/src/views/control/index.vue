<template>
  <div class="basic-home">
    <div
      class="content-wrapper"
      :class="isDragging ? 'disable-user-select' : ''"
    >
      <div class="left-content">
        <Introduce></Introduce>
        <VueDraggable
          v-model="moduleList"
          :animation="150"
          handle=".handle"
          class="drag-list"
          ghostClass="control-sortable-ghost"
          fallbackClass="control-sortable-fallback"
          :forceFallback="true"
          :fallbackOnBody="true"
          :onStart="startDrag"
          :onEnd="endDrag"
          :onMove="update"
        >
          <template
            v-for="(moduleItem, index) in moduleList"
            :key="moduleItem.index"
            :id="moduleItem.id"
          >
            <component
              :is="moduleItem.component"
              :isDragging="isDragging"
            ></component>
          </template>
        </VueDraggable>
      </div>
      <div class="right-content">
        <UserInfo></UserInfo>
        <DocsLink></DocsLink>
      </div>
    </div>
  </div>
</template>
<script lang="ts">
export default {
  name: "control",
};
</script>
<script lang="ts" setup>
import { ref, onMounted, computed, markRaw } from "vue";
import UserInfo from "./components/UserInfo.vue";
import DocsLink from "./components/DocsLink.vue";
import Introduce from "./components/Introduce.vue";
import Entrance from "./components/Entrance.vue";
import PersonalOverview from "./components/PersonalOverview.vue";
// import NoData from "./components/NoData.vue";
import { VueDraggable } from "vue-draggable-plus";
import userApi from "@/apis/user";
import { useUserStore } from "@/stores/modules/user";
import { basicSteps } from "./constant";
import Driver from "@/plugin/driver";
import isMicroProjectDeploy from "@/utils/isMicroProjectDeploy";

const update = () => {};
const userStore = useUserStore();

const workspace = computed(() => {
  const { workspace_id, workSpaceList } = userStore.userInfo;
  return workSpaceList.find((item) => item.workSpaceId === workspace_id) || {};
});

let interval;
const guide = () => {
  const driver: Driver = new Driver({
    onHighlightStarted: ({ node }) => {
      if (!node) return;
      (node as HTMLElement).scrollIntoView();
      // if ((node as HTMLElement).className.includes("user-info-card")) {
      //   (node as HTMLElement).scrollIntoView();
      // }
    },
  });

  driver.defineSteps(
    basicSteps.filter((item) => {
      if (item.deploy) {
        return isMicroProjectDeploy(item.deploy);
      } else {
        return true;
      }
    })
  );
  driver.start();
  driver.steps.forEach((el) => {
    el.node.style.pointerEvents = "none";
  });
  interval = setInterval(() => {
    if (!driver.isActivated) {
      driver.steps.forEach((el) => {
        el.node.style.pointerEvents = "auto";
      });
      clearInterval(interval);
      interval = null;
    }
  }, 100);
};

onMounted(async () => {
  userApi.validateIntegrality();
  const guideState = JSON.parse(userStore.userInfo.guideState);
  const control = guideState.control;
  if (control && workspace.value.workSpaceId) {
    let timeout = setTimeout(() => {
      guide();
      clearTimeout(timeout);
      timeout = null;
    });
    guideState.control = false;
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
  }
});

const moduleList = ref(
  [
    {
      title: "服务入口",
      component: markRaw(Entrance),
      index: 1,
      id: "entry-box-container",
    },
    {
      title: "个人运营",
      component: markRaw(PersonalOverview),
      index: 2,
      id: "overview-box-container",
    },
  ]
);

const isDragging = ref(false);
const startDrag = () => {
  isDragging.value = true;
};
const endDrag = () => {
  isDragging.value = false;
};
</script>
<style scoped lang="scss">
.basic-home {
  overflow-y: auto;
  height: 100%;
  background-color: #ecf1f8;

  .content-wrapper {
    display: flex;
    gap: 16px;
    justify-content: center;
    padding: 16px;
  }

  :deep(.control-card) {
    background-color: #ffffff;
    border-radius: 8px;
    padding: 24px;

    .card-header {
      display: flex;
      align-items: center;
      justify-content: space-between;

      .card-header-title {
        line-height: 26px;
        font-size: 18px;
        font-weight: 600;
        color: #232a35;
      }

      .handle {
        cursor: move;
      }
    }

    .card-header__adjust {
      margin-top: -3px;
    }

    .link {
      font-size: 12px;
      color: #5f7292;
      cursor: pointer;
      display: flex;
      align-items: center;

      &:hover {
        color: #1f69ff;
      }
    }

    .card-cover {
      border-radius: 8px;
      height: calc(100% + 40px);
      width: calc(100% + 40px);
      position: relative;
      z-index: 99999;
      background-color: #e1e7f0;
      transform: translate(-20px, calc(-100% + 20px));
      display: none;
    }
  }

  .left-content {
    width: 960px;
    display: flex;
    flex-direction: column;
    overflow: hidden;
    gap: 16px;

    .drag-list {
      width: 100%;
      display: flex;
      flex-direction: column;
      gap: 16px;
    }
  }

  .right-content {
    width: 432px;
    display: flex;
    flex-direction: column;
    gap: 16px;
  }
}
</style>

<style lang="scss">
.disable-user-select {
  -webkit-user-select: none; /*webkit浏览器*/
  user-select: none;
}
.control-sortable-ghost {
  &.control-card {
    border-radius: 8px;
    background: #e1e7f0 !important;
  }
  .card-cover {
    display: block !important;
  }
}
.control-sortable-fallback {
  opacity: 1 !important;
  background: #fff;
  &.control-card {
    background-color: #ffffff;
    border-radius: 8px;
    padding: 24px;
    box-shadow: 0px 2px 6px 0px rgba(12, 14, 49, 0.1);
    .card-header {
      display: flex;
      align-items: center;
      justify-content: space-between;

      .card-header-title {
        line-height: 26px;
        font-size: 18px;
        font-weight: 600;
        color: #232a35;
      }

      .handle {
        cursor: move;
      }
    }

    .card-header__adjust {
      margin-top: -3px;
    }

    .link {
      font-size: 12px;
      color: #5f7292;
      cursor: pointer;
      display: flex;
      align-items: center;

      &:hover {
        color: #1f69ff;
      }
    }
  }
}
</style>
