<!-- 一些上传到iconfont显示有问题的icon采用此组件 -->
<template>
  <img :src="svgUrl" alt="" class="icon" :class="size"/>
</template>

<script lang="ts">
export default {
  name: "Icon",
};
</script>

<script lang="ts" setup>
import { computed } from "vue";
const props = defineProps({
  iconName: { type: String, default: "" },
  size: { type: String, default: "sm" },
  loading: { type: Boolean, default: false },
});

const svgUrl = computed(() => {
  return new URL(`../assets/imgs/${props.iconName}.svg`, import.meta.url)
    .pathname;
});
</script>
<style lang="scss" scoped>
.icon {
  width: 16px;
  height: 16px;
  vertical-align: middle;
  fill: currentColor;

  &.sm {
    width: 14px;
    height: 14px;
  }

  &.middle {
    width: 18px;
    height: 18px;
  }

  &.lg {
    width: 22px;
    height: 22px;
  }

  &.loading {
    animation: loading 1.5s linear infinite;
  }

  &.logo {
    height: 30px;
    width: 110px;
  }

  &.xl {
    height: 30px;
    width: 30px;
  }
}
@keyframes loading {
  0% {
    -webkit-transform: rotate(0deg);
    transform: rotate(0deg);
  }

  to {
    -webkit-transform: rotate(1turn);
    transform: rotate(1turn);
  }
}
</style>
