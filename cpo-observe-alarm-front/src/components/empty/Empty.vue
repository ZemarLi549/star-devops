<template>
  <div class="empty-wrapper">
    <img
      src="@/assets/images/noData.png"
      :class="size === 'large' ? 'big-img' : ''"
      alt="暂无数据"
    />
    <div class="text">暂无数据</div>
  </div>
</template>

<script lang="ts" setup>
const props = defineProps({
  size: {
    type: String,
  },
});
</script>

<style lang="scss" scoped>
.empty-wrapper {
  margin: 0 auto;
  text-align: center;
  line-height: 22px;
  img {
    width: 44px;
    height: 40px;
    vertical-align: middle;
    margin-top: 0;
  }
  .big-img {
    width: 66px;
    height: 60px;
  }
  .text {
    height: 22px;
    line-height: 22px;
    font-size: 14px;
    color: #626471;
    margin: 8px 0 0 0;
  }
}
</style>
