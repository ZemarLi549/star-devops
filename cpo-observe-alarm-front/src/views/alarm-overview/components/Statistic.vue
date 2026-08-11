<template>
  <div class="statistic-container">
    <div class="block-header">
      <div class="today-content">
        <div class="today-alarm">
          <!-- <img class="today-img-alarm" src="@/assets/images/shujvdanyuan.png" alt=""> -->
          <div>
            <div class="card-tip-title">
              <span>今日去重告警</span>
              <el-tooltip placement="top">
                <template #content>
                  <div>今日所有告警：当日0时到24时，去重后的告警量</div>
                  <div>今日事件量：当日0时到24时，告警事件的量</div>
                </template>
                <img
                  class="tip-img"
                  src="@/assets/icons/icon_info.svg"
                  alt=""
                />
              </el-tooltip>
            </div>
            <div style="font-size: 40px; font-weight: 700; margin: 5px 0 20px">
              {{ overviewData.totalAlarmNum }}
            </div>
          </div>
        </div>
        <div class="today-event">
          <!-- <img class="today-img-event" src="@/assets/images/shujvdanyuan.png" alt=""> -->
          <div>
            <div class="card-tip-event">
              <span
                >今日事件量&nbsp;&nbsp;&nbsp;&nbsp;{{
                  overviewData.totalEventNum
                }}</span
              >
            </div>
          </div>
        </div>
      </div>
      <div class="child-card">
        <StatisticCard
          class="child-item"
          title="所有去重告警"
          thumb="所有去重告警"
          :count="overviewData.alarm.num"
          tip="所有去重告警：告警的首次触发时间在选择区间内的去重告警量"
          :percent="overviewData.alarm.ratio"
        />
        <StatisticCard
          class="child-item"
          title="所有事件量"
          thumb="所有事件量"
          :count="overviewData.event.num"
          tip="所有事件量：告警的首次触发时间在选择区间内的原始告警量"
          :percent="overviewData.event.ratio"
        />
        <StatisticCard
          class="child-item"
          title="平均响应时长MTTA"
          thumb="MTTA"
          :count="overviewData.mtta.value"
          tip="MTTA：认领时长"
          :percent="overviewData.mtta.ratio"
        />
        <StatisticCard
          class="child-item"
          title="平均处理时长MTTR"
          thumb="MTTR"
          :count="overviewData.mttr.value"
          tip="MTTR：平均处理或关闭时长"
          :percent="overviewData.mttr.ratio"
        />
      </div>
    </div>
    <div class="alarm-collpase-container">
      <div class="alarm-collapse">
        <div class="block-btn" @click="collapseChange">
          <img
            src="@/assets/images/icon-collapse.png"
            style="height: 20px; margin-right: 4px"
            alt=""
          />
          <span>查看告警趋势</span>
          <el-icon :class="['arrow-btn', { 'arrow-btn-active': !showChart }]"
            ><ArrowUp
          /></el-icon>
        </div>
      </div>
      <Transition name="slide-fade">
        <div
          v-show="showChart"
          class="statistic-chart"
          ref="statisticChart"
        ></div>
      </Transition>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch } from "vue";
import StatisticCard from "./StatisticCard.vue";
import { ArrowUp } from "@element-plus/icons-vue";
import * as echarts from "echarts";
import { dynaminicHisLineOption, getDaysInRange } from "./assists";
import OverviewApi from "@/request/api/alarm-overview";

import { useEventListener, useDebounceFn } from "@vueuse/core";
import { useOverviewStore } from "@/stores/modules/overview";

const store = useOverviewStore();

let dateRange = getDaysInRange(store.timeRange, true);

watch(
  () => store.timeRange,
  (val) => {
    dateRange = getDaysInRange(val, true);
  }
);

const showChart = ref(true);

const overviewData = ref({
  alarm: {
    num: 0,
    rise: true,
    ratio: 0, // 去重告警
  },
  event: {
    num: 0,
    rise: true,
    ratio: 0, // 原始告警
  },
  mainAlarm: {
    num: 0,
    rise: true,
    ratio: 0, // 主告警
  },
  mtta: {
    value: 0,
    rise: true,
    ratio: 0, // 平均响应时间
  },
  mttr: {
    value: 0,
    rise: true,
    ratio: 0, // 平均处理时间
  },
  totalAlarmNum: 0, // 当日主告警量
  totalEventNum: 0, // 当日事件量
});

const alert_day_list = ref([]);

const collapseChange = () => {
  showChart.value = !showChart.value;
};

const fetchData = () => {
  if (store.timeRange) {
    OverviewApi.levelTend(
      store.timeRange[0].getTime().toString(),
      store.timeRange[1].getTime().toString(),
      false
    ).then((res) => {
      res && (alert_day_list.value = res);
    });

    OverviewApi.alarmOverview(
      store.timeRange[0].getTime().toString(),
      store.timeRange[1].getTime().toString(),
      false
    ).then((res) => {
      res && (overviewData.value = res);
    });
  }
};

const resize = () => {
  lineInstance?.resize();
};
const resizeFn = useDebounceFn(resize, 200);
const cleanup = useEventListener(window, "resize", resizeFn);

const hoverIndex = ref(-1);
const lineHoverAffect = () => {
  lineInstance.on("highlight", function (params) {
    if (params.batch) {
      hoverIndex.value = params.batch[0].dataIndex;
    }

    const option = dynaminicHisLineOption(
      alert_day_list.value,
      dateRange,
      hoverIndex.value
    );
    lineInstance.setOption(option);
  });
  lineInstance.on("downplay", function (params) {
    hoverIndex.value = -1;
    const option = dynaminicHisLineOption(
      alert_day_list.value,
      dateRange,
      hoverIndex.value
    );
    lineInstance.setOption(option);
  });
};

const statisticChart = ref(null);
let lineInstance = null;

onMounted(() => {
  lineInstance = echarts.init(statisticChart.value);
  lineHoverAffect();
});

watch(alert_day_list, () => {
  const option = dynaminicHisLineOption(
    alert_day_list.value,
    dateRange,
    hoverIndex.value
  );
  lineInstance.clear();
  lineInstance.setOption(option);
});

watch(
  () => store.timeRange,
  (val) => {
    fetchData();
  },
  {
    immediate: true,
  }
);
</script>

<style lang="scss" scoped>
.statistic-container {
  --m-16: 16px;
  --m-24: 24px;
  --m-12: 12px;
  --m-20: 20px;
  background: white;
  padding: var(--m-24);
  border-radius: 8px;
  .block-header {
    display: flex;
    background: url("@/assets/images/statistics-bg.png");
    background-size: cover;
    background-repeat: no-repeat;
    border-radius: 8px;
    padding: 20px;

    .today-content {
      display: flex;
      width: 306px;
      margin-right: var(--m-16);
      margin-top: 10px;
      flex-direction: column;
      .today-img-alarm {
        width: 50px;
        height: 50px;
      }
      .today-img-event {
        width: 30px;
        height: 30px;
      }

      .today-alarm {
        // display: flex;
        // border-bottom: 1px solid #d9d9d9;

        .card-tip-title {
          color: #262626;
          font-size: 16px;

          span {
            vertical-align: middle;
            font-size: 16px;
            font-weight: 400;
            color: #262626;
            margin-right: 4px;
          }

          .tip-img {
            height: 16px;
            vertical-align: middle;
            display: inline-block;
          }
        }
      }
      .today-event {
        display: flex;

        .card-tip-event {
          background: #ffffff;
          border-radius: 16px;
          padding: 5px 16px;
          color: #2c3645;
        }
      }
    }
  }

  .child-card {
    display: flex;
    width: calc(100% - 306px);
    background: rgb(255, 255, 255);
    border-radius: 8px;

    .child-item {
      width: 25%;
      padding: 20px 0;
      // border-radius: 8px;
    }
    .child-item:first-child {
      border-radius: 8px 0 0 8px;
    }
    .child-item:last-child {
      border-radius: 0 8px 8px 0;
      background: url("@/assets/images/card-bg.png");
      background-position: 100% 0;
      background-repeat: no-repeat;
      background-size: contain;
    }
    .child-item:not(:last-child) {
      :deep(.inner-card) {
        border-right: 1px solid #ebedf1;
      }
    }
  }

  .alarm-collpase-container {
    border: 1px solid #ebedf1;
    margin-top: 20px;
    border-radius: 8px;
    .alarm-collapse {
      padding: 12px 20px;

      background: #f5f7fc;
    }
  }

  .block-btn {
    display: flex;
    align-items: center;
    cursor: pointer;
    font-weight: 500;
    color: #232a35;

    .arrow-btn {
      margin-left: 4px;
      transition: all 0.3s;
    }
    .arrow-btn-active {
      transform: rotateZ(180deg);
    }
  }

  .statistic-chart {
    width: 100%;
    height: 260px;
  }
  .slide-fade-enter-active {
    transition: all 0.3s ease-out;
  }

  .slide-fade-leave-active {
    transition: all 0.3s cubic-bezier(1, 0.5, 0.8, 1);
  }

  .slide-fade-enter-from,
  .slide-fade-leave-to {
    transform: translateY(20px);
    opacity: 0;
  }
}
</style>
