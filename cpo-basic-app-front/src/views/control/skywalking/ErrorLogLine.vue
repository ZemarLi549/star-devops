<template>
  <div class="graph flex-v">
    <div
      class="chart"
      :id="`errLogLine${props.index}`"
      style="width: 100%; height: 100%"
    ></div>
  </div>
</template>

<script lang="ts" setup>
import { defineProps, watch } from "vue";
import * as echarts from "echarts";
import type { LineConfig } from "@/types/dashboard";
const props = defineProps({
  config: {
    type: Object as PropType<
      LineConfig & {
        id?: string;
      }
    >,
    default: () => ({
      step: false,
      smooth: false,
      showSymbol: false,
      opacity: 0.4,
      showXAxis: true,
      showYAxis: true,
      smallTips: false,
      showlabels: true,
    }),
  },
  index: {
    type: Number,
  },
});
let myChart;
const init = (errLogsMap) => {
  // const data = { ["错误日志数"]: errLogsMap["metric"] };
  myChart?.dispose();
  myChart = null;
  const chartDom = document.getElementById(`errLogLine${props.index}`);
  myChart = echarts.init(chartDom);

  const serie: any = {
    data: errLogsMap["metric"],
    name: "错误日志数",
    type: "line",
    symbol: "circle",
    symbolSize: 8,
    showSymbol: props.config.showSymbol,
    step: props.config.step,
    smooth: props.config.smooth,
    lineStyle: {
      width: 1.5,
      type: "solid",
    },
    color: "#1f69ff",
  };

  const option = {
    tooltip: {
      trigger: "axis",
      textStyle: {
        fontSize: 12,
        color: "#333",
      },
      enterable: true,
      confine: true,
      extraCssText: "max-height: 300px; overflow: auto; border: none;",
    },
    legend: {
      show: false,
      type: "scroll",
      icon: "circle",
      top: 0,
      left: 0,
      itemWidth: 12,
      textStyle: {
        color: "#fff",
      },
    },
    grid: {
      top: 10,
      left: 0,
      right: 10,
      bottom: 5,
      containLabel:
        props.config.showlabels === undefined ? true : props.config.showlabels,
    },
    xAxis: {
      type: "category",
      show: props.config.showXAxis,
      axisTick: {
        lineStyle: { color: "#c1c5ca41" },
        alignWithLabel: true,
      },
      splitLine: { show: false },
      axisLine: { lineStyle: { color: "rgba(0,0,0,0)" } },
      axisLabel: {
        color: "#9da5b2",
        fontSize: "11",
        formatter: function (value, index) {
          const val = value.split(" ");
          let date = val[0].split("-");
          date.shift();
          date = date.join("-");
          return `${val[1]}\n${date}`;
        },
      },
      data: errLogsMap.intervalTime,
    },
    yAxis: {
      show: props.config.showYAxis,
      type: "value",
      axisLine: { show: false },
      axisTick: { show: false },
      splitLine: { lineStyle: { color: "#c1c5ca41", type: "dashed" } },
      axisLabel: {
        color: "#9da5b2",
        fontSize: "11",
        show: props.config.showYAxis,
      },
    },
    series: serie,
  };
  option && myChart.setOption(option);
};

defineExpose({
  init,
});
</script>
<style scoped lang="scss">
.graph {
  width: 100%;
  height: 100%;
  .chart {
    overflow: hidden;
    flex: 1;
  }
}
</style>
