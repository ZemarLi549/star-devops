<template>
  <div class="control-card">
    <div class="card-header card-header__adjust">
      <span class="card-header-title">
        告警总量趋势
        <!-- <el-tooltip
          content="通过观察告警总量的变化趋势，可以了解系统的稳定性和可用性情况"
          placement="right"
        >
          <span style="margin-left: 8px; font-weight: 400">
            <FontIcon icon="icon-yiwen" />
          </span>
        </el-tooltip> -->
      </span>
      <div class="handle-group">
        <div
          style="position: relative"
          @mouseenter="shorttemp.show = false"
          @mouseleave="shorttemp.show = true"
        >
          <el-date-picker
            style="width: 256px; height: 32px"
            v-model="trendDate"
            type="daterange"
            unlink-panels
            range-separator="至"
            placeholder="选择日期"
            start-placeholder="选择开始日期"
            end-placeholder="选择结束日期"
            :disabled-date="disabledDate"
            :shortcuts="trendShortcuts"
            :prefix-icon="Calendar"
            @change="changeDate"
            :default-time="[
              new Date(2000, 1, 1, 0, 0, 0),
              new Date(2000, 2, 1, 23, 59, 59),
            ]"
          />
          <div v-if="shorttemp.show && shorttemp.text" class="short-text">
            {{ shorttemp.text }}
          </div>
        </div>
        <el-tooltip
          content="按住拖拽可移动"
          placement="top"
          :disabled="isDragging"
        >
          <div class="handle">
            <FontIcon icon="icon-tuozhuai" />
          </div>
        </el-tooltip>
      </div>
    </div>

    <div class="card-content">
      <div class="alarm-line" ref="lineRef"></div>
      <div class="alarm-pie">
        <div class="alarm-pie-level">
          <div class="title">告警级别占比</div>
          <div ref="pieRefLevel" style="height: 260px"></div>
        </div>
        <div class="alarm-pie-task">
          <div class="title">任务分析</div>
          <div ref="pieRefTask" style="height: 260px"></div>
          <div class="alarm-pie-task-footer">
            <span
              ><span v-if="!isSmallScreen">平均响应时长</span>MTTA：{{
                alert_overview ? alert_overview["mtta"]["value"] : 0
              }}</span
            ><span
              ><span v-if="!isSmallScreen">平均处理时长</span>MTTR：{{
                alert_overview ? alert_overview["mttr"]["value"] : 0
              }}</span
            >
          </div>
        </div>
      </div>
    </div>

    <div class="card-cover"></div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from "vue";
import { Calendar } from "@element-plus/icons-vue";
import * as echarts from "echarts";
import { onMounted, onUnmounted } from "vue";
import { useEventListener, useDebounceFn } from "@vueuse/core";
import {
  dynaminicHisLineOption,
  dynaminicPieLevelOption,
  dynaminicPieTaskOption,
} from "../assists";
import { trendShortcuts } from "../constant";
import ControlApi from "@/apis/control";
defineProps(["isDragging"]);
window.__isshort_console_alarm__ = null;

const shorttemp = ref({
  show: true,
  text: "",
});
const trendDate = ref([
  new Date(new Date().getTime() - 3600 * 1000 * 24 * 30),
  new Date(),
]);
const alert_day_list = ref();
const alert_interval = ref();
const alert_task = ref();
const alert_overview = ref();
const isSmallScreen = ref(false);

const requestData = async () => {
  const reqData = {
    startTime: trendDate.value[0].getTime().toString(),
    endTime: trendDate.value[1].getTime().toString(),
    isSelf: false,
  };
  try {
    alert_day_list.value = await ControlApi.getAlarmLevelTrend(reqData);
    alert_interval.value = await ControlApi.getAlarmLevelAnalysis(reqData);
    alert_task.value = await ControlApi.getAlarmLevelTask(reqData);
    alert_overview.value = await ControlApi.getAlarmOverview(reqData);
  } catch (error) {
    console.log(error);
  }
};

const changeDate = async () => {
  if (window.__isshort_console_alarm__) {
    shorttemp.value.text = window.__isshort_console_alarm__;
  } else {
    shorttemp.value.text = "";
  }
  window.__isshort_console_alarm__ = null;
  requestData();
};
const disabledDate = (time: Date) => {
  return time.getTime() > Date.now();
};
const lineRef = ref(null);
const pieRefLevel = ref(null);
const pieRefTask = ref(null);
let lineInstance;
let pieLevelInstance;
let pieTaskInstance;
const resize = () => {
  judgeIsSmallScreen();
  lineInstance?.resize();
  pieLevelInstance?.resize();
  pieTaskInstance?.resize();
};
const resizeFn = useDebounceFn(resize, 200);
const cleanup = useEventListener(window, "resize", resizeFn);
onMounted(() => {
  judgeIsSmallScreen();
  lineInstance = echarts.init(lineRef.value);
  pieLevelInstance = echarts.init(pieRefLevel.value);
  pieTaskInstance = echarts.init(pieRefTask.value);
  lineHoverAffect();
  // 饼图高亮突出效果
  pieHoverAffect();
  requestData();
});

const judgeIsSmallScreen = () => {
  const footElement = document.querySelector(".alarm-pie-task-footer");
  if (footElement) {
    const width = footElement?.offsetWidth;
    if (width < 420) {
      isSmallScreen.value = true;
    } else {
      isSmallScreen.value = false;
    }
  }
};

// 折线图图标高亮效果
let dataIndex = ref(-1);
const lineHoverAffect = () => {
  lineInstance.on("highlight", function (params) {
    if (params.batch) {
      dataIndex.value = params.batch[0].dataIndex;
    }
    const option = dynaminicHisLineOption(
      alert_day_list.value,
      dataIndex.value,
      trendDate.value
    );
    lineInstance.setOption(option);
  });
  lineInstance.on("downplay", function (params) {
    dataIndex.value = -1;
    const option = dynaminicHisLineOption(
      alert_day_list.value,
      dataIndex.value,
      trendDate.value
    );
    lineInstance.setOption(option);
  });
};

// 饼图高亮突出效果
const pieHoverAffect = () => {
  pieTaskInstance.on("mouseover", function (params) {
    if (params.componentType === "series") {
      let dataIndex = params.dataIndex; // 获取当前扇形索引
      let outDataIndexArr = [];
      let innerDataIndexArr = [];
      if (params.seriesIndex) {
        // hover内圈
        innerDataIndexArr = [dataIndex];
        outDataIndexArr = [dataIndex * 3, dataIndex * 3 + 1, dataIndex * 3 + 2];
      } else {
        // hover外圈
        const index = Math.floor(dataIndex / 3);
        outDataIndexArr = [index * 3, index * 3 + 1, index * 3 + 2];
        innerDataIndexArr = [index];
      }

      // 设置当前扇形
      pieTaskInstance.dispatchAction({
        type: "highlight",
        seriesIndex: params.seriesIndex ? 1 : 0,
        dataIndex: params.seriesIndex ? innerDataIndexArr : outDataIndexArr,
      });
      pieTaskInstance.dispatchAction({
        type: "highlight",
        seriesIndex: params.seriesIndex ? 0 : 1,
        dataIndex: params.seriesIndex ? outDataIndexArr : innerDataIndexArr,
      });
    }
  });
  pieTaskInstance.on("mouseout", function (params) {
    if (params.componentType === "series") {
      pieTaskInstance.dispatchAction({
        // 取消设置样式
        type: "downplay",
        seriesIndex: 0,
      });
      pieTaskInstance.dispatchAction({
        // 取消设置样式
        type: "downplay",
        seriesIndex: 1,
      });
    }
  });
};

onUnmounted(() => {
  cleanup();
  lineInstance && lineInstance.dispose();
  lineInstance = null;
  pieLevelInstance && pieLevelInstance.dispose();
  pieLevelInstance = null;
  pieTaskInstance && pieTaskInstance.dispose();
  pieTaskInstance = null;
});
watch(alert_day_list, () => {
  const option = dynaminicHisLineOption(
    alert_day_list.value,
    dataIndex.value,
    trendDate.value
  );
  lineInstance.clear();
  lineInstance.setOption(option);
});
watch(alert_interval, () => {
  const option = dynaminicPieLevelOption(alert_interval.value);
  pieLevelInstance.clear();
  pieLevelInstance.setOption(option);
});
watch(alert_task, () => {
  const option = dynaminicPieTaskOption(alert_task.value);
  pieTaskInstance.clear();
  pieTaskInstance.setOption(option);
});

watch(alert_task, () => {
  const option = dynaminicPieTaskOption(alert_task.value);
  pieTaskInstance.clear();
  pieTaskInstance.setOption(option);
});
</script>
<style scoped lang="scss">
.handle-group {
  height: 32px;
  display: flex;
  align-items: center;
  gap: 12px;
}
.card-content {
  .alarm-line {
    height: 293px;
  }
  .alarm-pie {
    display: flex;
    margin-top: 24px;
    gap: 16px;

    .alarm-pie-level,
    .alarm-pie-task {
      padding: 20px;
      background-color: #f5f7fc;
      flex: 1;
      min-width: 314px;
      height: 326px;
      border-radius: 4px;
    }
    .alarm-pie-task {
      position: relative;
      .alarm-pie-task-footer {
        height: 55px;
        background: #e8effc;
        border-radius: 0px 0px 4px 4px;
        display: flex;
        position: absolute;
        bottom: 0;
        left: 0;
        width: 100%;
        align-items: center;

        span:nth-child(1) {
          display: inline-block;
          flex: 1;
          color: #232a35;
          line-height: 24px;
          text-align: center;
        }
        span:nth-child(2) {
          display: inline-block;
          // margin-left: 36px;
          flex: 1;
          color: #232a35;
          line-height: 24px;
          text-align: center;
        }
      }
    }
    .title {
      line-height: 24px;
      font-size: 16px;
      font-weight: 600;
    }
  }
}

.short-text {
  position: absolute;
  top: 1px;
  left: 28px;
  background: white;
  width: 220px;
  height: 30px;
  line-height: 30px;
}
</style>
<style lang="scss">
.control-alarm-total-tooltip {
  width: 144px;
  height: 234px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 4px;
  box-shadow: 0px 2px 10px 0px rgba(23, 24, 32, 0.1);
  padding: 8px 12px;
  font-size: 12px;
  position: absolute;
  opacity: 0.95;
  z-index: -1;

  .date {
    list-style: 20px;
    color: #2c3645;
  }
  .gap {
    width: 118px;
    height: 1px;
    background: #ebedf1;
    margin: 8px 1px;
  }
  .flex-bt {
    display: flex;
    justify-content: space-between;
    line-height: 32px;
    color: #2c3645;
    align-items: center;
  }
  .total-alarm {
    line-height: 20px;
    margin-bottom: 4px;
    color: #2c3645;
    font-weight: 500;
  }
  .text {
    color: #4b5b76;
  }
  .point::before {
    content: "";
    display: inline-block;
    height: 6px;
    width: 6px;
    border-radius: 50%;
    margin: 1px 5px 0 0;
    position: relative;
    top: 0px;
  }

  .point.alarm-level-5::before {
    border: 2px solid #fa3946;
  }
  .point.alarm-level-4::before {
    border: 2px solid #f4319d;
  }
  .point.alarm-level-3::before {
    border: 2px solid #fa830c;
  }
  .point.alarm-level-2::before {
    border: 2px solid #f7bb21;
  }
  .point.alarm-level-1::before {
    border: 2px solid #1f69ff;
  }
}
.control-alarm-pie-level-tooltip,
.control-alarm-pie-task-tooltip {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 4px;
  box-shadow: 0px 2px 10px 0px rgba(23, 24, 32, 0.1);
  padding: 8px 12px;
  font-size: 12px;
  position: absolute;
  opacity: 0.95;
  z-index: -1;
  border-color: #fff !important;
  .point::before {
    content: "";
    display: inline-block;
    height: 6px;
    width: 6px;
    border-radius: 50%;
    margin: 1px 5px 0 0;
    position: relative;
    top: 0px;
  }
  .alarm-level-5::before {
    border: 2px solid #fa3946;
  }
  .alarm-level-4::before {
    border: 2px solid #f4319d;
  }
  .alarm-level-3::before {
    border: 2px solid #fa830c;
  }
  .alarm-level-2::before {
    border: 2px solid #f7bb21;
  }
  .alarm-level-1::before {
    border: 2px solid #1f69ff;
  }

  .point-state-0::before {
    border: 2px solid #ffa54c;
  }
  .point-state-1::before {
    border: 2px solid #2353b5;
  }
  .point-state-2::before {
    border: 2px solid #97a8cb;
  }
}
</style>
