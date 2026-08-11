<template>
  <div class="introduce-container" id="introduce-card" v-if="showGuideBox">
    <div class="banner-box">
      <div class="banner-text-title">
        <span style="color: #1f69ff">鑫图</span>平台
      </div>
      <span class="banner-content">
        {{
          "面向企业智能协作与自动化执行的统一平台。\n当前聚焦 AstrBot 智能助手接入、AI 人效自动化、飞书协同数据治理，以及工单、CMDB、Zabbix 资源总览。\n后续将按微服务方式逐步开放能力入口。"
        }}
      </span>
      <img class="banner-right-bg" src="@/assets/brand/hero-illustration.svg" alt="" />
      <div class="hidden-btn-img" @click="showGuideBox = false">
        <div>收起</div>
      </div>
    </div>
    <div class="tabs-box">
      <el-tabs v-model="activeName" class="tabs" @tab-change="tabChange">
        <template v-for="tab in tabDetailData" :key="tab.title">
          <el-tab-pane :label="tab.title" :name="tab.title">
            <div
              class="scroll-handle left-handle"
              v-if="tab.cards.length > 3 && translateX !== 0"
              @click="lastPage"
            >
              <FontIcon
                style="font-size: 16px; line-height: 1"
                icon="icon-zuojiantou"
              />
              <span class="scroll-handle-shadow"></span>
            </div>
            <div
              class="tab-card-list"
              :style="{ transform: `translateX(${translateX}%)` }"
            >
              <div
                v-for="(data, index) in tab.cards"
                :key="data.key"
                class="tab-card-item"
                :class="{
                  noBorder:
                    index === tab.cards.length - 1 || (index + 1) % 3 === 0,
                }"
              >
                <div class="item-avatar">
                  <img
                    :src="
                      getImgFromDirectory(
                        'imgs/control',
                        `${data.avatar_img}.png`
                      )
                    "
                    alt=""
                  />
                </div>
                <div class="item-content">
              <div class="item-content-title" @click="goDocument(data.link)">
                {{ data.title }}
                <div class="icon-hover">
                  <FontIcon
                        style="font-size: 16px; line-height: 1"
                        icon="icon-bianzu"
                      />
                    </div>
                  </div>
                  <div class="item-content-info">{{ data.info }}</div>
                </div>
              </div>
            </div>
            <div
              class="scroll-handle right-handle"
              v-if="tab.cards.length > 3 && translateX !== -100"
              @click="nextPage"
            >
              <span class="scroll-handle-shadow"></span>
              <FontIcon
                style="font-size: 16px; line-height: 1"
                icon="icon-youjiantou"
              />
            </div>
          </el-tab-pane>
        </template>
      </el-tabs>
    </div>
  </div>
  <div
    class="guide-box-show"
    @click="showGuideBox = true"
    draggable="true"
    @dragend="dragend($event)"
    :style="{ left: `${elLeft - 40}px `, top: `${elTop - 40}px` }"
    v-else
  >
    <img src="@/assets/imgs/show_guide.png" alt="" width="78" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import { ElMessage } from "element-plus";
import { tabDetailData } from "../constant";
import { useConfigStore } from "@/stores/modules/microConfig";
import { getImgFromDirectory } from "@/utils/img";
const configStore = useConfigStore();
const activeName = ref(tabDetailData[0].title);
const getUrl = (filename) => {
  const url = new URL(
    `../../../assets/imgs/control/${filename}.png`,
    import.meta.url
  ).href;
  return `url(${url})`;
};
const showGuideBox = ref(true);
const elLeft = ref(0); // 元素的左偏移量
const elTop = ref(0); // 元素的右偏移量
// 页面初始化
function initBodySize() {
  elLeft.value = document.body.clientWidth - 40;
  elTop.value = document.body.clientHeight - 40;
}
function dragend(e: any) {
  elLeft.value = e.clientX;
  elTop.value = e.clientY;
  if (elLeft.value + 40 >= document.body.clientWidth) {
    elLeft.value = document.body.clientWidth - 40;
  }
  if (elTop.value + 40 >= document.body.clientHeight) {
    elTop.value = document.body.clientHeight - 40;
  }
  if (elLeft.value - 40 <= 0) {
    elLeft.value = 40;
  }
  if (elTop.value - 40 <= 0) {
    elTop.value = 40;
  }
}

window.addEventListener("resize", () => {
  initBodySize();
});

const goDocument = (linkPath) => {
  if (!linkPath) {
    ElMessage.info("规划文档整理中，稍后开放");
    return;
  }
  window.open(configStore.$state.documentUrl + linkPath);
};
const translateX = ref(0);
const lastPage = () => {
  console.log(translateX.value);

  translateX.value !== 0 && (translateX.value = 0);
};
const nextPage = () => {
  console.log(translateX.value);
  translateX.value !== -100 && (translateX.value = -100);
};
const tabChange = () => {
  translateX.value = 0;
};

onMounted(async () => {
  initBodySize();
});
</script>
<style scoped lang="scss">
:deep(.basic-el-tabs) {
  --basic-el-tabs-header-height: 48px;
  .basic-el-tabs__header {
    background-color: #fff;
    margin-bottom: 0;
  }
  .basic-el-tabs__nav-scroll {
    padding-left: 20px;
  }
  .basic-el-tabs__item {
    font-weight: 400;
    color: #4b5b76;
    padding: 0 16px;
    &.is-active {
      font-weight: 600;
      color: #2f76ff;
    }
  }
  .basic-el-tabs__nav-wrap::after {
    height: 1px;
  }
}

.introduce-container {
  border-radius: 8px;
  overflow: hidden;
    .banner-box {
      background:
        radial-gradient(circle at 18% 18%, rgba(255, 255, 255, 0.8) 0, rgba(255, 255, 255, 0) 30%),
        linear-gradient(135deg, #dce9ff 0%, #edf4ff 48%, #e8f9f5 100%);
      background-size: cover;
      background-position: center;
      position: relative;
      height: 318px;
      overflow: hidden;
      &::before {
      content: "";
      position: absolute;
      inset: 0;
      background:
        radial-gradient(circle at 76% 18%, rgba(255, 255, 255, 0.58) 0, rgba(255, 255, 255, 0) 26%),
        radial-gradient(circle at 52% 68%, rgba(86, 159, 255, 0.16) 0, rgba(86, 159, 255, 0) 32%);
      pointer-events: none;
    }
    .banner-text-title {
      font-size: 48px;
      line-height: 48px;
      padding: 76px 0 24px 40px;
      vertical-align: middle;
      color: #262626;
      font-weight: 500;
    }
    .banner-right-bg {
      position: absolute;
      width: 400px;
      right: 18px;
      height: 304px;
      top: 12px;
      opacity: 0.98;
    }
    .banner-content {
      display: block;
      line-height: 24px;
      width: 440px;
      margin-left: 40px;
      font-size: 13px;
      white-space: pre-line;
      color: #4b5b76;
    }
    .hidden-btn-img {
      position: absolute;
      top: 23px;
      right: 21px;
      z-index: 1;
      cursor: pointer;
      img {
        height: 32px;
        width: 32px;
      }
      div {
        width: 44px;
        height: 26px;
        display: -webkit-box;
        display: -ms-flexbox;
        display: flex;
        -webkit-box-pack: center;
        -ms-flex-pack: center;
        justify-content: center;
        -webkit-box-align: center;
        -ms-flex-align: center;
        align-items: center;
        cursor: pointer;
        background: rgba(47, 79, 103, 0.5);
        border-radius: 3px;
        color: #fff;
      }
      div:hover {
        background-color: rgba(47, 79, 103, 0.8);
      }
    }
  }
  .tabs-box {
    background-color: #fff;
    margin-top: -48px;
    position: relative;
    border-top-left-radius: 8px;
    border-top-right-radius: 8px;
    overflow: hidden;
    .tab-card-list {
      display: flex;
      transition: all 0, 3s;
      .tab-card-item {
        position: relative;
        height: 108px;
        width: calc(100% / 3);
        flex-grow: 0;
        flex-shrink: 0;
        padding: 20px 24px;
        display: flex;
        gap: 8px;
        &:hover {
          .item-content-title {
            color: #2f76ff;
            .icon-hover {
              display: inline-block;
            }
          }
        }
        .item-avatar {
          width: 44px;
          height: 44px;
          background: #f0f6ff;
          border-radius: 50%;
          flex-shrink: 0;
          img {
            width: 44px;
            height: 44px;
          }
        }
        .item-content-title {
          cursor: pointer;
          display: flex;
          align-content: center;
          justify-content: space-between;
          font-size: 16px;
          font-weight: 600;
          color: #232a35;
          line-height: 24px;
          .icon-hover {
            display: none;
            line-height: 24px;
          }
        }
        .item-content-info {
          font-size: 12px;
          color: #757f92;
          line-height: 20px;
          margin-top: 4px;
        }
      }

      .tab-card-item::after {
        content: "";
        display: block;
        height: 108px;
        width: 1px;
        position: absolute;
        right: 0;
        top: 2px;
        background-color: #e8e8e8;
        border-radius: 50%;
      }

      .tab-card-item.noBorder:after {
        height: 0;
      }
    }
    .scroll-handle {
      width: 21px;
      height: 100%;
      position: absolute;
      display: flex;
      align-items: center;
      cursor: pointer;
      top: 0;
      z-index: 999;
      .scroll-handle-shadow {
        display: inline-block;
        width: 4px;
        height: 108px;
      }
      &.left-handle {
        left: 0;
        .scroll-handle-shadow {
          background: linear-gradient(
            to right,
            #ebedf1,
            rgba(235, 237, 241, 0)
          );
        }
      }
      &.right-handle {
        right: 0;
        .scroll-handle-shadow {
          background: linear-gradient(to left, #ebedf1, rgba(235, 237, 241, 0));
        }
      }
    }
  }
}

.guide-box-show {
  position: fixed;
  // right: 0;
  // bottom: 0px;
  cursor: pointer;
  z-index: 999;
}
</style>
