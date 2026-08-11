<template>
  <div class="basic-home" style="padding: 0">
    <div class="no-data-wrap">
      <div class="no-data-top">
        <img class="no-data-img" src="@img/no-data-img-min.png" alt="" />
        <div class="no-data-content">
          <div class="no-data-text">当前暂无可使用的服务入口</div>
          <div class="no-data-text-s">
            当前平台处于微服务接入准备阶段，待授权或服务上线后可刷新页面查看
          </div>
          <el-button type="primary" @click="refresh">刷新页面</el-button>
        </div>
      </div>
      <div class="no-data-bottom">
        <p>最新文档</p>
        <ul>
          <li
            v-for="(docLink, index) in docLinkArray"
            :key="index"
            @click="goDocument(docLink.link)"
          >
            <img src="@/assets/imgs/docIcon.png" alt="" />
            <span>{{ docLink.title }}</span>
            <span class="link-icon">
              <FontIcon icon="icon-you" />
            </span>
          </li>
        </ul>
      </div>
    </div>
    <img class="no-data-banner" src="@img/no-data-banner-min.png" alt="" />
    <img class="no-data-bg" src="@img/no-data-bg-min.png" alt="" />
  </div>
</template>

<script lang="ts" setup>
import { ElMessage } from "element-plus";
import { useConfigStore } from "@/stores/modules/microConfig";
import { useUserStore } from "@/stores/modules/user";
import { onMounted } from 'vue';
// 工作台跳转文档中心链接映射
const docLinkArray = [
  {
    title: "鑫图平台规划概览",
    link: "",
    tag: "产品概述",
  },
  {
    title: "前端微服务接入文档",
    link: "",
    tag: "接入",
  },
  {
    title: "AstrBot 集成规划",
    link: "",
    tag: "规划",
  },
  {
    title: "AI 人效数据采集规范",
    link: "",
    tag: "治理",
  },
  {
    title: "飞书数据授权与安全说明",
    link: "",
    tag: "安全",
  },
];

const configStore = useConfigStore();
const userStore = useUserStore()
const updateUserInfo = async () => {
  await userStore.updateWorkspaceId()
  if (useUserStore().userInfo?.workspace_id) {
      await userStore.getMenus()
      window.location.replace('/')
  }
}
const refresh = () => {
  window.location.reload()
};
onMounted(() => {
  updateUserInfo()
})
const goDocument = (linkPath) => {
  if (!linkPath) {
    ElMessage.info("规划文档整理中，稍后开放");
    return;
  }
  window.open(configStore.$state.documentUrl + linkPath);
};
</script>

<style lang="scss" scoped>
.basic-home {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 844px;
  overflow: auto;
  background: linear-gradient(270deg, #eef4ff, #e0e8fb);
  position: relative;
}

.no-data-banner {
  position: absolute;
  width: 100%;
  top: 0;
  margin: 0 auto;
  max-width: 1440px;
  z-index: 10;
}

.no-data-bg {
  position: absolute;
  width: 640px;
  bottom: 0;
  left: 0;
  z-index: 10;
}

.no-data-wrap {
  margin-top: -40px;
  width: 1072px;
  display: flex;
  flex-direction: column;
  position: relative;
  z-index: 99;

  .no-data-top {
    display: flex;
    align-items: center;

    .no-data-img {
      width: 559px;
      margin-left: -33px;
    }

    .no-data-content {
      width: 408px;
      margin-left: -30px;
    }

    .no-data-text {
      font-size: 24px;
      font-weight: 600;
      line-height: 32px;
    }

    .no-data-text-s {
      font-size: 16px;
      color: #596376;
      margin: 10px 0 24px 0;
    }
  }

  .no-data-bottom {
    width: 100%;
    background: #ffffff;
    border-radius: 8px;
    height: 240px;
    padding: 0 32px;
    margin-top: -16px;

    p {
      font-size: 18px;
      color: #262626;
      font-weight: bold;
      margin: 24px 0;
    }

    ul {
      display: flex;
      flex-wrap: wrap;
      width: 100%;
      position: relative;
      gap: 16px;

      li {
        display: flex;
        align-items: center;
        width: 284px;
        height: 54px;
        background: #f7f8fa;
        border-radius: 4px;
        list-style: none;
        position: relative;
        color: #262626;
        padding: 0 16px;

        &:hover {
          background: #f2f3f5;

          .link-icon {
            display: inline-block;
          }
        }

        img {
          width: 21px;
          margin-right: 10px;
        }

        span {
          font-size: 14px;
          line-height: 54px;
        }

        .link-icon {
          font-weight: 600;
          margin-left: 8px;
          display: none;
        }
      }

      li:hover {
        color: #1f69ff;
        cursor: pointer;
      }
    }
  }
}
</style>
