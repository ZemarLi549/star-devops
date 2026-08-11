<template>
  <div :class="['empty-wrapper', { 'empty-wrapper-center': isNotTableCenter }]">
    <img src="@/assets/imgs/noData.png" alt="暂无数据" />
    <div class="text">{{ text }}</div>
  </div>
</template>
<script lang="ts" setup>
const props = defineProps({
  text: {
    type: String,
    default: "暂无数据",
  },
  isNotTableCenter: {
    default: false,
  },
});
</script>
<style lang="scss" scoped>
.empty-wrapper {
  margin: auto;
  position: relative;
  width: 300px;
  text-align: center;

  img {
    width: 61px;
    height: 55px;
    margin-top: 50px;
    vertical-align: middle;
  }
  .text {
    font-size: 14px;
    line-height: 30px;
    color: #626471;
    margin-bottom: 50px;
    margin-top: 8px;
  }
}
.empty-wrapper-center {
  top: calc(50% - 50px);
  transform: translateY(-50%);
}
</style>
