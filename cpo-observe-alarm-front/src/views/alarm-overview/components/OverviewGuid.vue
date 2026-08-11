<template>
  <div class="guide-container">
    <div class="guide-container-wrapper" >
      <div class="guide-hide-btn" @click="onHide">
        <FontIcon style="font-size: 14px;" icon="icon-quanjingtushouqi" />
      </div>
      <div class="guide-inner">
        <div class="guide-text">
          <div class="guide-title">智能告警</div>
          <p class="guide-tip">
            平台统一告警接收、去重、降噪、分派、触达、解决与分析，实现了高效的故障响应与快速的问题定位，保障故障处理全程可观测
          </p>
          <div class="guide-card">
            <p class="guide-card-title">不错过关键告警</p>
            <p class="guide-card-tip">灵活的告警规则和分派让通知准确传达责任人</p>
          </div>
          <div class="guide-card">
            <p class="guide-card-title">跨平台告警汇聚集成</p>
            <p class="guide-card-tip">
              支持三方监控集成，覆盖指标、链路、日志等多种数据类型
            </p>
          </div>
        </div>
        <img class="guide-inner-img" src="@/assets/imgs/overview-bg.png" alt="" />
      </div>
  
      <img class="guide-bg" src="@/assets/imgs/overview-color.png" alt="" />
    </div>
  </div>
</template>

<script setup>
import { defineEmits } from "vue";
const emits = defineEmits(["hide"]);
const onHide = () => {
  emits("hide");
};
</script>

<style lang="scss" scoped>
.guide-container {
  height: 100%;
  background: linear-gradient(269deg, #e9f1ff 1%, #fafbff 99%);
  // padding: 16px;
  position: absolute;
  overflow: auto;
  border-radius: 4px;
  // min-height: 700px;
  // min-width: 1208px;
  left: 0;
  right: 0;
  top: 0;
  bottom: 0;
  z-index: 999;
  .guide-container-wrapper {
    position: relative;
    min-height: 762px;
    height: 100%;
    .guide-hide-btn {
      width: 32px;
      height: 32px;
      display: flex;
      align-items: center;
      justify-content: center;
      position: absolute;
      right: 32px;
      top: 32px;
      z-index: 99;
      cursor: pointer;
      background: #f4faff;
      border-radius: 4px;
      box-shadow: 2px 2px 4px 0px rgba(189,210,248,0.60) inset, -2px -2px 4px 0px rgba(255,255,255,0.50) inset; 
      &:hover {
        background: #d4e3fc;
        border-radius: 4px;
        box-shadow: 2px 2px 4px 0px rgba(189,210,248,0.80) inset; 
      }
    }
    .guide-inner {
      transform: translateY(-40px);
      height: 100%;
      width: 100%;
      display: flex;
      justify-content: center;
      align-items: center;
      // gap: 63px;
      padding-top: 40px;
      overflow-y: scroll;
      .guide-text {
        width: 352px;
        // margin-top: 140px;
        flex-shrink: 0;
      }
      .guide-title {
        font-size: 44px;
        font-weight: 600;
        color: #262626;
      }
      .guide-tip {
        opacity: 0.9;
        font-size: 14px;
        font-weight: 400;
        color: #2c3645;
        line-height: 24px;
      }
  
      .guide-card {
        background: linear-gradient(
          270deg,
          rgba(255, 255, 255, 0.54),
          #ffffff 58%
        );
        border-radius: 6px;
        box-shadow: 0px 4px 10px 0px rgba(133, 188, 255, 0.1);
        backdrop-filter: blur(3px);
        padding: 20px 20px 20px 40px;
        margin-top: 20px;
  
        .guide-card-title {
          font-size: 16px;
          font-weight: 600;
          color: #2c3645;
          line-height: 24px;
          position: relative;
          margin-top: 0;
          margin-bottom: 8px;
        }
        .guide-card-title::before {
          content: "";
          width: 8px;
          height: 8px;
          background: #2f76ff;
          border-radius: 50%;
          position: absolute;
          top: 8px;
          left: -16px;
        }
        .guide-card-tip {
          font-size: 14px;
          font-weight: 400;
          color: #757f92;
          line-height: 22px;
          margin-bottom: 0;
          margin-top: 8px;
        }
      }
    }
    .guide-inner-img {
      width: 737px;
      // height: 675px;
      margin-left: 63px;
    }
    .guide-bg {
      width: 640px;
      position: absolute;
      left: 0;
      bottom: 0;
      z-index: -1;
    }
    @media screen and (max-width: 1440px) {
      .guide-inner-img {
        width: 600px;
        // margin-top: 60px;
        margin-left: 30px;
        margin-right: -30px;
      }
    }
    @media screen and (max-height: 762px) {
      .guide-inner {
        transform: translateY(-100px);
      }
      .guide-inner-img {
        width: 600px;
      }
    }
  }
}
</style>
