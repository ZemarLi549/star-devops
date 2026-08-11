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
  <div class="chart" ref="chartRef" :style="`height:${height};width:${width};`">
    <div v-if="!available" class="no-data">No Data</div>
  </div>
</template>
<script lang="ts" setup>
  import { watch, ref, onMounted, onBeforeUnmount, unref, computed, reactive } from "vue";
  import type { PropType, Ref } from "vue";
  import type { EventParams } from "@/types/app";
  import { useECharts } from "@/hooks/useEcharts";
  import { addResizeListener, removeResizeListener } from "@/utils/event";

  /*global Nullable, defineProps, defineEmits*/
  const emits = defineEmits(["select"]);
  const chartRef = ref<HTMLDivElement | null>(null);
  const visMenus = ref<boolean>(false);
  const { setOptions, resize, getInstance } = useECharts(chartRef as Ref<HTMLDivElement>);
  const currentParams = ref<EventParams | null>(null);

  const menuPos = reactive<{ x: number; y: number }>({ x: NaN, y: NaN });
  const props = defineProps({
    height: { type: String, default: "100%" },
    width: { type: String, default: "100%" },
    option: {
      type: Object as PropType<{ [key: string]: any }>,
      default: () => ({}),
    },
    associate: {
      type: Array as PropType<{ widgetId: string }[]>,
      default: () => [],
    },
  });  
  const available = computed(
    () =>
      (Array.isArray(props.option.series) && props.option.series[0] && props.option.series[0].data) ||
      (Array.isArray(props.option.series.data) && props.option.series.data[0]),
  );
  onMounted(async () => {
    await setOptions(props.option);
    chartRef.value && addResizeListener(unref(chartRef), resize);
    instanceEvent();
  });

  function instanceEvent() {
    setTimeout(() => {
      const instance = getInstance();

      if (!instance) {
        return;
      }
      instance.on("click", (params: EventParams) => {
        currentParams.value = params;
        if (props.option.series.type === "sankey") {
          emits("select", currentParams.value);
          return;
        }
        instance.dispatchAction({
          type: "hideTip",
        });
        visMenus.value = true;
        if (!chartRef.value) {
          return;
        }
        const w = chartRef.value.getBoundingClientRect().width || 0;
        const h = chartRef.value.getBoundingClientRect().height || 0;
        if (w - params.event.offsetX > 120) {
          menuPos.x = params.event.offsetX;
        } else {
          menuPos.x = params.event.offsetX - 120;
        }
        if (h - params.event.offsetY < 50) {
          menuPos.y = params.event.offsetY - 40;
        } else {
          menuPos.y = params.event.offsetY;
        }
      });
      if (props.option.series.type === "sankey") {
        return;
      }
      instance.on("mouseover", () => {
        visMenus.value = false;
      });
      instance.on("mouseout", () => {
        instance.dispatchAction({
          type: "hideTip",
        });
      });
      document.addEventListener(
        "click",
        () => {
          if (instance.isDisposed()) {
            return;
          }
          visMenus.value = false;
          instance.dispatchAction({
            type: "hideTip",
          });
          instance.dispatchAction({
            type: "updateAxisPointer",
            currTrigger: "leave",
          });
        },
        true,
      );
    }, 1000);
  }

  watch(
    () => props.option,
    (newVal, oldVal) => {
      if (!available.value) {
        return;
      }
      if (JSON.stringify(newVal) === JSON.stringify(oldVal)) {
        return;
      }
      setOptions(props.option);
    },
  );

  onBeforeUnmount(() => {
    removeResizeListener(unref(chartRef), resize);
  });
</script>
<style lang="scss" scoped>
  .no-data {
    font-size: 12px;
    height: 100%;
    box-sizing: border-box;
    display: -webkit-box;
    -webkit-box-orient: horizontal;
    -webkit-box-pack: center;
    -webkit-box-align: center;
    color: #666;
  }

  .chart {
    overflow: hidden;
    flex: 1;
  }

  .menus {
    position: absolute;
    display: block;
    white-space: nowrap;
    z-index: 9999999;
    box-shadow: #ddd 1px 2px 10px;
    transition: all cubic-bezier(0.075, 0.82, 0.165, 1) linear;
    background-color: rgb(255, 255, 255);
    border-radius: 4px;
    color: rgb(51, 51, 51);
    padding: 5px;
  }

  .tools {
    padding: 5px;
    color: #999;
    cursor: pointer;

    &:hover {
      color: var(--el-color-primary);
      background-color: #eee;
    }
  }
</style>
