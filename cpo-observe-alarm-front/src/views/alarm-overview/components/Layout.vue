<template>
  <div class="observer-alarm-container">
    <div class="observer-alarm-header">
      <Breadcrumb></Breadcrumb>
      <div class="timepicker-box">
        <div
          style="position: relative"
          @mouseenter="shorttemp.show = false"
          @mouseleave="shorttemp.show = true"
        >
          <el-date-picker
            style="width: 256px; height: 32px"
            v-model="timeRange"
            type="daterange"
            unlink-panels
            range-separator="至"
            placeholder="选择日期"
            start-placeholder="选择开始日期"
            end-placeholder="选择结束日期"
            :disabled-date="disabledDate"
            :shortcuts="shortcuts"
            :prefix-icon="Calendar"
            popper-class="time-popper"
            @change="change"
          />
          <div v-if="shorttemp.show && shorttemp.text" class="short-text">
            {{ shorttemp.text }}
          </div>
        </div>
        <el-button style="margin-left: 8px; padding: 8px" @click="refresh">
          <img
            style="width: 16px; transform: rotateZ(55deg)"
            src="@/assets/icons/refresh.svg"
            alt=""
          />
        </el-button>
      </div>
    </div>
    <div class="observer-alarm-body">
      <router-view></router-view>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import Breadcrumb from "@/components/layout/Breadcrumb.vue";
import { useOverviewStore } from "@/stores/modules/overview";
import { storeToRefs } from "pinia";
import { RefreshRight, Calendar } from "@element-plus/icons-vue";

const { timeRange } = storeToRefs(useOverviewStore());

const shorttemp = ref({
  show: true,
  text: "",
});

const disabledDate = (time) => {
  return time.getTime() > Date.now();
};

const shortcuts = [
  {
    text: "近7天",
    value: () => {
      const end = new Date(new Date().setHours(23, 59, 59, 59));
      const start = new Date(
        new Date().setHours(24, 0, 0, 0) - 3600 * 1000 * 24 * 7
      );
      window.overview_shortcuts = "近7天";
      return [start, end];
    },
  },
  {
    text: "近14天",
    value: () => {
      const end = new Date(new Date().setHours(23, 59, 59, 59));
      const start = new Date(
        new Date().setHours(24, 0, 0, 0) - 3600 * 1000 * 24 * 14
      );
      window.overview_shortcuts = "近14天";
      return [start, end];
    },
  },
  {
    text: "近30天",
    value: () => {
      const end = new Date(new Date().setHours(23, 59, 59, 59));
      const start = new Date(
        new Date().setHours(24, 0, 0, 0) - 3600 * 1000 * 24 * 30
      );
      window.overview_shortcuts = "近30天";
      return [start, end];
    },
  },
  {
    text: "近90天",
    value: () => {
      const end = new Date(new Date().setHours(23, 59, 59, 59));
      const start = new Date(
        new Date().setHours(24, 0, 0, 0) - 3600 * 1000 * 24 * 90
      );
      window.overview_shortcuts = "近90天";
      return [start, end];
    },
  },
];

const refresh = () => {
  if (timeRange.value) {
    console.log(timeRange, "time");
    const startTime = new Date(timeRange.value[0].getTime());
    const endTime = new Date(timeRange.value[1].getTime());

    timeRange.value = [startTime, endTime];
  }
};

const change = (val) => {
  console.log(val, timeRange);
  if (window.overview_shortcuts) {
    shorttemp.value.text = window.overview_shortcuts;
  } else {
    shorttemp.value.text = "";
  }
  window.overview_shortcuts = null;
};
</script>

<style scoped lang="scss">
.observer-alarm-container {
  --base-bg-color: #ecf1f8;
  height: 100%;
  background: var(--base-bg-color);
  position: relative;
  .observer-alarm-header {
    height: 48px;
    border-bottom: 1px solid #f0f0f0;
    background-color: var(--base-bg-color);
    align-items: center;
    padding: 0 16px;
    display: flex;
    position: relative;

    .timepicker-box {
      display: inline-flex;
      position: absolute;
      right: 20px;

      .short-text {
        position: absolute;
        top: 1px;
        left: 28px;
        padding-left: 5px;
        background: white;
        width: 220px;
        height: 30px;
        line-height: 30px;
      }
    }
  }
  .observer-alarm-body {
    height: calc(100% - 48px);
    padding: 2px 16px 16px;
    overflow-y: auto;
  }
}
</style>
