<template>
  <div class="chart-wrapper">
    <div class="block-container">
      <div class="card-tip-header">
        <div class="card-tip-title">
          <span>{{ title }}</span>
          <el-tooltip v-if="tip.length" placement="right">
            <template #content>
              <div v-for="(item, index) in tip" :key="index">{{ item }}</div>
            </template>
            <img class="tip-img" src="@/assets/icons/icon_info.svg" alt="" />
          </el-tooltip>
        </div>
        <el-radio-group
          v-if="hasSwitch"
          class="card-tip-radio"
          v-model="active"
          @change="fetchData"
        >
          <el-radio-button label="所有告警" value="所有告警" />
          <el-radio-button label="我的告警" value="我的告警" />
        </el-radio-group>
      </div>

      <div class="chart-container" ref="chartRef">
        <el-table
          v-if="type == 'table'"
          ref="multipleTableRef"
          style="margin-top: 12px"
          :data="chartData"
          v-loading="loading"
        >
          <template #empty><Empty /></template>
          <el-table-column label="TOP" show-overflow-tooltip width="60">
            <template #default="scope">
              {{ scope.$index + 1 }}
            </template>
          </el-table-column>
          <el-table-column
            property="dataGroupName"
            label="数据单元"
            show-overflow-tooltip
          />
          <el-table-column
            property="alarmNum"
            label="告警量"
            show-overflow-tooltip
            :formatter="(a, b, c) => c || '-'"
            width="128"
          />
          <el-table-column
            property="referNum"
            label="分派策略引用量"
            show-overflow-tooltip
            width="144"
            :formatter="(a, b, c) => c || '-'"
          />
        </el-table>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch, defineProps } from "vue";
import * as echarts from "echarts";
import { dynaminicPieTaskOption } from "./assists";
import OverviewApi from "@/request/api/alarm-overview";
import { useOverviewStore } from "@/stores/modules/overview";
import { useEventListener, useDebounceFn } from "@vueuse/core";
import Empty from "@/components/empty/Empty.vue";

const store = useOverviewStore();

const props = defineProps({
  title: {
    type: String,
    default: () => "",
  },
  tip: {
    type: Array,
    default: () => [],
  },
  type: {
    type: String,
    default: () => "chart",
  },
  hasSwitch: {
    type: Boolean,
    default: () => false,
  },
  fetchFunc: {
    type: Function,
  },
  handleOption: {
    type: Function,
    default: () => () => {},
  },
  mockData: {
    type: Function,
  },
});

const chartData = ref([]);

const loading = ref(false);

// mock数据
const mockData = () => {
  chartData.value = props.mockData();
};

const active = ref("所有告警");

const fetchData = () => {
  if (store.timeRange) {
    loading.value = true;
    props
      .fetchFunc(
        store.timeRange[0].getTime().toString(),
        store.timeRange[1].getTime().toString(),
        props.hasSwitch ? (active.value == "所有告警" ? false : true) : false
      )
      .then((res) => {
        res && (chartData.value = res);
        // mockData()
        loading.value = false;
      })
      .finally(() => {
        loading.value = false;
      });
  }
};

const resize = () => {
  chartInstance?.resize();
};
const resizeFn = useDebounceFn(resize, 200);
const cleanup = useEventListener(window, "resize", resizeFn);

const chartRef = ref(null);
let chartInstance = null;

// 饼图高亮突出效果
const pieHoverAffect = () => {
  chartInstance.on("mouseover", function (params) {
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
      chartInstance.dispatchAction({
        type: "highlight",
        seriesIndex: params.seriesIndex ? 1 : 0,
        dataIndex: params.seriesIndex ? innerDataIndexArr : outDataIndexArr,
      });
      chartInstance.dispatchAction({
        type: "highlight",
        seriesIndex: params.seriesIndex ? 0 : 1,
        dataIndex: params.seriesIndex ? outDataIndexArr : innerDataIndexArr,
      });
    }
  });
  chartInstance.on("mouseout", function (params) {
    if (params.componentType === "series") {
      chartInstance.dispatchAction({
        // 取消设置样式
        type: "downplay",
        seriesIndex: 0,
      });
      chartInstance.dispatchAction({
        // 取消设置样式
        type: "downplay",
        seriesIndex: 1,
      });
    }
  });
};

onMounted(() => {
  if (props.type == "chart") {
    chartInstance = echarts.init(chartRef.value);

    if (props.title == "任务分析") {
      pieHoverAffect();
    }
  }
});

onUnmounted(() => {
  cleanup();
  chartInstance?.dispose();
});

watch(chartData, () => {
  if (props.type == "chart") {
    const option = props.handleOption(chartData.value);
    chartInstance.clear();
    chartInstance.setOption(option);
  }
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
.block-container {
  :deep(.observer-alarm-el-radio-button) {
    --observer-alarm-el-radio-button-checked-bg-color: white !important;
    --observer-alarm-el-radio-button-checked-border-color: #f1f2f5 !important;
    --observer-alarm-el-radio-button-checked-text-color: #1f69ff !important;
    --observer-alarm-el-fill-color-blank: #f1f2f5 !important;
    --observer-alarm-el-border: 4px solid #f1f2f5 !important;
  }
  :deep(.observer-alarm-el-radio-button__inner) {
    padding: 5px 12px;
    font-weight: 500;
  }

  .chart-container {
    width: 100%;
    height: calc(100% - 30px);
  }

  .card-tip-title {
    span {
      font-size: 18px;
      font-weight: 600;
      color: #232a35;
      margin-right: 8px;
      vertical-align: middle;
    }
    .tip-img {
      display: inline-block;
      height: 16px;
      vertical-align: middle;
    }
  }
}
</style>
