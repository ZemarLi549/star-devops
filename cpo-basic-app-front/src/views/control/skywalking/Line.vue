<!-- Licensed to the Apache Software Foundation (ASF) under one or more
contributor license agreements.  See the NOTICE file distributed with
this work for additional information regarding copyright ownership.
The ASF licenses this file to You under the Apache License, Version 2.0
(the "License"); you may not use this file except in compliance with
the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License. -->
<template>
  <div class="graph flex-v" :class="setRight ? 'flex-h' : 'flex-v'">
    <Graph :option="option" @select="clickEvent" />
    <!-- <Legend :config="config.legend" :data="data" :intervalTime="intervalTime" /> -->
  </div>
</template>
<script lang="ts" setup>
import { computed, ref } from "vue";
import type { PropType } from "vue";
import type { LineConfig, EventParams } from "@/types/dashboard";
import Graph from "./Graph.vue";
import { useAppStoreWithOut } from "@/stores/modules/app";
import dateFormatStep, { dateFormatTime } from "@/utils/dateFormat";
const appStore = useAppStoreWithOut();

/*global defineProps, defineEmits */
const emits = defineEmits(["click"]);
const props = defineProps({
  data: {
    type: Object as PropType<{ [key: string]: number[] }>,
    default: () => ({}),
  },
  intervalTime: { type: Array as PropType<string[]>, default: () => [] },
  theme: { type: String, default: "light" },
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
});
const setRight = ref<boolean>(false);
const option = computed(() => getOption());
function getOption() {
  const keys = Object.keys(props.data || {}).filter(
    (i: any) => Array.isArray(props.data[i]) && props.data[i].length
  );
  const color: string[] = [
    "#1f69ff",
    "#14b6ed",
    "#ffca2b",
    "#fa830c",
    "#7a48f0",
    "#d561f6",
  ];
  // 时间处理弥补缺少的时间节点
  let interval = 0;
  switch (appStore.duration.step) {
    case "MINUTE":
      interval = 60000;
      break;
    case "HOUR":
      interval = 3600000;
      break;
    case "DAY":
      interval = 86400000;
      break;
    case "MONTH":
      interval =
        (appStore.duration.end.getTime() - appStore.duration.start.getTime()) /
        (appStore.duration.end.getFullYear() * 12 +
          appStore.duration.end.getMonth() -
          appStore.duration.start.getFullYear() * 12 -
          appStore.duration.start.getMonth());
      break;
  }
  const temp = keys.map((i: any, index: number) => {
    const data = props.data[i].map((item: any, itemIndex: number) => {
      if (props.intervalTime[itemIndex]) {
        return [props.intervalTime[itemIndex], item];
      } else {
        return [
          dateFormatTime(
            new Date(appStore.intervalUnix[itemIndex - 1] + interval),
            appStore.duration.step
          ),
          item,
        ];
      }
    });
    const serie: any = {
      // data: props.data[i].map((item: any, itemIndex: number) => [props.intervalTime[itemIndex], item]),
      data: data,
      name: i,
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
      color: color[index],
    };
    return serie;
  });
  const tooltip = {
    trigger: "axis",
    textStyle: {
      fontSize: 12,
      color: "#333",
    },
    enterable: true,
    confine: true,
    extraCssText: "max-height: 300px; overflow: auto; border: none;",
  };
  const tips = {
    formatter(params: any) {
      return `${params[0].value[1]}`;
    },
    confine: true,
    extraCssText: `height: 20px; padding:0 2px;`,
    trigger: "axis",
    textStyle: {
      fontSize: 12,
      color: "#333",
    },
  };

  return {
    tooltip: props.config.smallTips ? tips : tooltip,
    legend: {
      show: false,
      type: "scroll",
      icon: "circle",
      top: 0,
      left: 0,
      itemWidth: 12,
      textStyle: {
        color: props.theme === "dark" ? "#fff" : "#333",
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
      axisLabel: { color: "#9da5b2", fontSize: "11" },
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
    series: temp,
  };
}

function clickEvent(params: EventParams) {
  emits("click", params);
}
</script>
<style lang="scss" scoped>
.graph {
  width: 100%;
  height: 100%;
}
</style>
