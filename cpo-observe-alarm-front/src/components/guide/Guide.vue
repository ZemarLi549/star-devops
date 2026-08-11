<template>
  <div class="alarm-guide">
    <div class="alarm-info alarm-card">
      <h2 class="mb-8 alarm-info-title">日志检索</h2>
      <div class="mb-16 alarm-info-text">
        支持日志的统一采集、处理、海量存储与分析等能力，支持日志处理和日志关联查询，快速查询锁定故障。实时查询与分析日志，自动识别日志状态，快速筛选、过滤、关联相关日志，高度聚合近似文本，帮助用户及时发现异常日志并在故障发生时全面分析问题，更快的排除故障。
      </div>
      <div class="alarm-info-btn">
        <div>高性能海量日志存储</div>
        <div>实时和离线日志查询与分析</div>
        <div>自定义多维度聚合</div>
      </div>
      <div class="close" @click="onHide"><img :src="hideSvg" alt="" />隐藏</div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import hideSvg from "@/assets/icons/icon_hide.svg";
const emits = defineEmits(["hide"]);
const onHide = () => {
  emits("hide", "false");
};
</script>
<style scoped lang="scss">
.alarm-guide {
  padding: 16px;

  .alarm-info {
    position: relative;
    padding: 34px 40px;
    background-image: url("@/assets/imgs/log-guide-wide.png");
    background-position: right;
    background-size: cover;
    background-repeat: no-repeat;
    border-radius: 4px;
    box-shadow: 0px 2px 4px 0px #e5e5e5;
    height: 220px;

    &-title {
      font-size: 36px;
      font-weight: 600;
      letter-spacing: 2px;
      margin-bottom: 8px;
      height: 44px;
    }
    &-text {
      width: 700px;
      font-weight: 400;
      line-height: 22px;
    }
    &-btn {
      display: flex;
      gap: 16px;
      div {
        padding: 0 16px;
        height: 40px;
        background: linear-gradient(
          180deg,
          rgba(255, 255, 255, 0.4) 2%,
          rgba(255, 255, 255, 0)
        );
        border: 1px solid #ffffff;
        border-radius: 4px;
        box-shadow: 0px 6px 20px 0px rgba(195, 199, 208, 0.19);
        backdrop-filter: blur(8px);
        line-height: 40px;
        text-align: center;
      }
    }
    .close {
      position: absolute;
      right: 20px;
      top: 16px;
      display: flex;
      align-items: center;
      gap: 5px;
      color: #595959;
      font-size: 12px;
      cursor: pointer;
    }
  }
}
</style>
