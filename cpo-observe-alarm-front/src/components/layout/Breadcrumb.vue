<template>
  <div class="bread">
    <el-breadcrumb
      separator-class="el-icon-arrow-right"
      :separator-icon="ArrowRight"
    >
      <el-breadcrumb-item
        v-for="(item, index) in breadcrumbList"
        :key="index"
        :to="item.path ? { path: item.path } : {}"
      >
        {{ item.title }}
      </el-breadcrumb-item>
    </el-breadcrumb>
  </div>
</template>

<script setup>
import { ref, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
// import { ArrowRight } from '@element-plus/icons-vue'
import ArrowRight from "./arrow.vue";

const breadcrumbList = ref([]);

const route = useRoute();

watch(
  () => route,
  (val, oldVal) => {
    // console.log(val, 'route')
    breadcrumbList.value = val.meta.breadcrumb;
  },
  {
    deep: true,
    immediate: true,
  }
);
</script>

<style lang="scss" scoped>
.bread {
  font-size: 14px;
  :deep(span) {
    font-size: 14px;
  }
  .title {
    font-weight: 600;
  }
  :deep(.observer-alarm-el-breadcrumb__item) {
    font-size: 14px;
    font-weight: 600;
    .observer-alarm-el-breadcrumb__inner {
      color: #8c8c8c;
      &:hover {
        color: #1f69ff;
      }
    }
    &:last-child {
      .observer-alarm-el-breadcrumb__inner {
        &.is-link {
          color: #262626;
          font-weight: 600;
        }
      }
    }
  }

  :deep(.observer-alarm-el-breadcrumb__separator) {
    color: #7f8289 !important;
    font-weight: 400 !important;
    font-size: 12px !important;
  }
}
</style>
