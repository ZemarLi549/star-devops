<template>
  <Transition name="guid">
    <OverviewGuid v-if="showGuid" @hide="onHideGuid" />
  </Transition>
  <img
    src="@/assets/images/show-guide.png"
    v-if="!showGuid"
    alt=""
    class="show-guide"
    @click="onShowGuid"
    draggable="true"
    @dragend="dragend($event)"
    :style="{ left: `${elLeft - 40}px `, top: `${elTop - 40}px` }"
  />
  <div v-if="!showGuid" class="overview-container">
    <Statistic />
    <div class="chart-container">
      <!-- <ChartTable v-for="(item, index) in chartList" class="item" v-bind="item" :key="index" /> -->
      <ChartTable class="item" v-bind="chartList[0]" />
      <ChartTable class="item" v-bind="chartList[1]" />
      <ChartTable class="item" v-bind="chartList[2]" />
      <ChartTable class="item" v-bind="chartList[3]" />
      <ChartTable class="item" v-bind="chartList[4]" />
      <ChartTable class="item" v-bind="chartList[5]" />
      <ChartTable class="item item-h" v-bind="chartList[6]" />
      <ChartTable class="item item-h" v-bind="chartList[7]" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, watch } from "vue";
import OverviewGuid from "./components/OverviewGuid.vue";

import Statistic from "./components/Statistic.vue";

import ChartTable from "./components/ChartTable.vue";

import OverviewApi from "@/request/api/alarm-overview";
import {
  dynaminicPieTaskOption,
  dynaminicPieLevelOption,
  getDaysInRange,
} from "./components/assists";
import { useOverviewStore } from "@/stores/modules/overview";
import { useUserStore } from "@/stores/modules/user";
import { MonitorTypes } from "@/globals/constant";

const store = useOverviewStore();
const userStore = useUserStore();
const { setUserInfo, getUserInfoFromCookie } = userStore;
const guideState = JSON.parse(userStore.userInfo.guideState);

let dateRange = getDaysInRange(store.timeRange, true);

watch(
  () => store.timeRange,
  (val) => {
    dateRange = getDaysInRange(val, true);
  }
);

const showGuid = ref(guideState?.overviewGuide !== false ? true : false);

const onHideGuid = () => {
  showGuid.value = false;
  modeChange(false);
};
const onShowGuid = () => {
  showGuid.value = true;
  modeChange(true);
};

const modeChange = (val) => {
  const currentGuideState = JSON.parse(getUserInfoFromCookie().guideState);
  currentGuideState.overviewGuide = val;
  OverviewApi.guideState({
    guideState: JSON.stringify(currentGuideState),
  }).then(() => {
    setUserInfo({
      guideState: JSON.stringify(currentGuideState),
    });
  });
};

const elLeft = ref(0); // 元素的左偏移量
const elTop = ref(0); // 元素的右偏移量
// 页面初始化
const initBodySize = () => {
  elLeft.value = document.body.clientWidth - 40;
  elTop.value = document.body.clientHeight - 40;
};
const dragend = (e) => {
  elLeft.value = e.clientX;
  elTop.value = e.clientY;
  if (elLeft.value + 40 >= document.body.clientWidth) {
    elLeft.value = document.body.clientWidth - 40;
  }
  if (elTop.value + 40 >= document.body.clientHeight) {
    elTop.value = document.body.clientHeight - 40;
  }
  if (elLeft.value - 40 <= 0) {
    elLeft.value = 40;
  }
  if (elTop.value - 40 <= 0) {
    elTop.value = 40;
  }
};

const chartList = [
  {
    title: "告警级别占比",
    fetchFunc: OverviewApi.levelStatistics.bind(OverviewApi),
    handleOption: dynaminicPieLevelOption,
  },
  {
    title: "任务分析",
    hasSwitch: true,
    fetchFunc: OverviewApi.taskStatistics.bind(OverviewApi),
    handleOption: dynaminicPieTaskOption,
  },
  {
    title: "告警级别趋势",
    fetchFunc: OverviewApi.levelTend.bind(OverviewApi),
    handleOption: (data) => {
      let tempSeries: any = [
        { name: "信息", color: "#1f69ff", type: 1 },
        { name: "警告", color: "#f7bb21", type: 2 },
        { name: "次要", color: "#fa830c", type: 3 },
        { name: "重要", color: "#f4319d", type: 4 },
        { name: "严重", color: "#fa3946", type: 5 },
      ];
      tempSeries = tempSeries.map((item) => {
        return {
          name: item.name,
          type: "line",
          color: item.color,
          smooth: true,
          showAllSymbol: false,
          areaStyle: {
            color: {
              type: "linear",
              x: 0,
              y: 0,
              x2: 0,
              y2: 1,
              colorStops: [
                {
                  offset: 0,
                  color: "rgba(31,105,255,0.12)", // 渐变起始颜色
                },
                {
                  offset: 1,
                  color: "rgba(31,105,255,0.06)", // 渐变结束颜色
                },
              ],
            },
          },
          data: data.map((val) => {
            const arr = val.statisticsItems.find(
              (value) => value.type == item.type
            );
            return arr ? arr["num"] : 0;
          }),
        };
      });
      return {
        tooltip: {
          trigger: "axis",
          className: "control-alarm-total-tooltip",
          formatter: function (params: any) {
            const tooltipHtml = `
          <div class="date">${params[0]["axisValueLabel"]}</div>
             <div class="gap"></div>
          <div class="flex-bt text">
            <span class="point alarm-level-5">严重</span><span>${params[4]["value"]}</span>
          </div>
          <div class="flex-bt text">
            <span class="point alarm-level-4">重要</span><span>${params[3]["value"]}</span>
          </div>
          <div class="flex-bt text">
            <span class="point alarm-level-3">次要</span><span>${params[2]["value"]}</span>
          </div>
          <div class="flex-bt text">
            <span class="point alarm-level-2">警告</span><span>${params[1]["value"]}</span>
          </div>
          <div class="flex-bt text">
            <span class="point alarm-level-1">信息</span><span>${params[0]["value"]}</span>
          </div>
              `;
            return tooltipHtml;
          },
        },
        legend: {
          selectedMode: false,
          data: ["严重", "重要", "次要", "警告", "信息"],
          bottom: 0,
          formatter: function (name) {
            return name + "     ";
          },
          icon: "path://M1536 0a512.256 512.256 0 0 1 495.872 384H3072v256h-1040.128a512.256 512.256 0 0 1-991.744 0H0V384h1040.128c56.96-220.928 257.28-384 495.872-384z m0 256a256 256 0 0 0-222.848 129.92l1.152-2.048a254.72 254.72 0 0 0-20.096 43.776l2.048-5.632c-2.56 6.784-4.864 13.696-6.784 20.736l4.736-15.104a253.952 253.952 0 0 0-6.144 20.352l1.408-5.12a254.208 254.208 0 0 0-4.48 18.304l3.072-13.184a254.592 254.592 0 0 0-4.608 21.76l1.536-8.576a257.28 257.28 0 0 0-0.128 100.736l-1.28-7.296c1.28 7.296 2.688 14.464 4.48 21.504l-3.2-14.208c1.28 6.528 2.816 12.928 4.608 19.328l-1.28-5.12c1.664 6.784 3.712 13.568 6.016 20.224l-4.736-15.104c1.92 7.04 4.224 13.952 6.784 20.736l-2.048-5.632a256 256 0 0 0 464.64 41.728L1757.696 640a254.72 254.72 0 0 0 20.096-43.52l-2.048 5.376c2.56-6.656 4.864-13.568 6.784-20.608l-4.736 15.104c2.304-6.656 4.352-13.312 6.144-20.224l-1.28 5.12c1.664-6.4 3.2-12.8 4.48-19.2l-3.2 14.08c1.792-7.04 3.328-14.208 4.48-21.504l-1.28 7.296a257.28 257.28 0 0 0-0.128-100.736l1.536 8.576a254.592 254.592 0 0 0-4.608-21.76l3.072 13.184a254.208 254.208 0 0 0-4.48-18.432l1.408 5.248a253.952 253.952 0 0 0-6.144-20.352l4.736 15.104a253.952 253.952 0 0 0-6.784-20.736l2.048 5.632A256 256 0 0 0 1536 256z",
        },
        grid: {
          top: "15%",
          left: "3%",
          right: "4%",
          bottom: "12%",
          containLabel: true,
        },
        toolbox: {
          feature: {
            saveAsImage: {
              show: false,
            },
          },
        },
        xAxis: {
          type: "category",
          boundaryGap: false,
          axisLine: {
            lineStyle: {
              color: "#f2f3f5",
            },
          },
          axisTick: {
            show: false,
            alignWithLabel: true,
          },
          axisLabel: {
            formatter: function (value, index) {
              if (index === 0) {
                return ``;
              } else if (index === data.length - 1) {
                return ``;
              }

              const dateArr = value.split("-");

              dateArr.shift();
              return dateArr.join("-");
            },
            textStyle: {
              color: "#8E939E",
            },
          },
          data: dateRange,
        },
        yAxis: {
          type: "value",
        },
        series: tempSeries,
      };
    },
  },
  {
    title: "告警数据类型占比",
    hasSwitch: true,
    fetchFunc: OverviewApi.cateStatistics.bind(OverviewApi),
    handleOption: (data) => {
      const category = {
        0: "其他",
        1: "指标",
        2: "调用链",
        3: "日志",
        4: "拨测",
      };
      const arrange = [
        data.statisticsItems[1],
        data.statisticsItems[2],
        data.statisticsItems[3],
        data.statisticsItems[4],
        data.statisticsItems[0],
      ];
      const tempData = arrange.map((item) => {
        return {
          value: item.num,
          name: category[item.type],
          percent: Number(item.proportion / 100).toFixed(2),
        };
      });
      return {
        title: {
          show: false,
        },
        tooltip: {
          trigger: "item",
          formatter: function (params) {
            return `${params.marker}${params.name}: ${params.data.percent}`;
          },
          borderColor: "#ffffff",
        },
        legend: {
          show: true,
          selectedMode: false,
          orient: "vertical",
          left: "55%",
          height: "80%",
          top: "center",
          itemGap: 25,
          itemWidth: 10, // 设置图例项的宽度
          itemHeight: 10, // 设置图例项的高度
          icon: "circle",
          formatter: function (name) {
            for (let i = 0; i < tempData.length; i++) {
              if (name == tempData[i].name) {
                return `{name|${name}}{string|${tempData[i].percent}%}`;
              }
            }
          },
          textStyle: {
            rich: {
              name: {
                fontSize: 14,
                padding: [3, 0, 0, 0],
                width: 100,
              },
              num: {
                fontSize: 14,
                padding: [3, 0, 0, 0],
                width: 50,
              },
              string: {
                fontSize: 14,
                padding: [3, 0, 0, 0],
              },
            },
          },
        },
        series: [
          {
            name: "告警数据类型占比",
            type: "pie",
            radius: [35, 90],
            center: ["30%", "50%"],

            label: {
              show: true,
              position: "inside",
              formatter: function (d) {
                return `{a|${d.name}}`;
              },
              rich: {
                a: {
                  fontSize: 10,
                  color: "#fff",
                  lineHeight: 14,
                },
              },
            },
            color: ["#1f69ff", "#9b6bff", "#00c7ff", "#ff72cf", "#30f0f0"],
            data: arrange.map((item) => {
              return {
                value: item.num,
                name: category[item.type],
                percent: Number((item.proportion || 0) / 100).toFixed(2) + "%",
              };
            }),
            emphasis: {
              scaleSize: 12,
              // itemStyle: {
              //   shadowBlur: 10,
              //   shadowOffsetX: 0,
              //   shadowColor: 'rgba(0, 0, 0, 0.5)'
              // }
            },
          },
        ],
      };
    },
  },
  {
    title: "事件压缩比",
    tip: [
      "（原始告警数量-去重告警数量）/原始告警数量=事件压缩比",
      "提示：高压缩比可能意味着处理效率较慢",
    ],
    fetchFunc: OverviewApi.eventCompress.bind(OverviewApi),
    handleOption: (data) => {
      return {
        tooltip: {
          trigger: "axis",
          className:
            "control-alarm-total-tooltip control-alarm-statics-tooltip w180",
          formatter: function (params: any) {
            const tooltipHtml = `
          <div class="date">${params[0]["axisValueLabel"]}</div>
             <div class="gap"></div>
          <div class="flex-bt text">
            <span class="point color1">${params[0]["seriesName"]}</span><span>${params[0]["value"]}%</span>
          </div>
              `;
            return tooltipHtml;
          },
        },
        legend: {
          data: ["事件压缩比"],
          selectedMode: false,
          bottom: 0,
          icon: "path://M1536 0a512.256 512.256 0 0 1 495.872 384H3072v256h-1040.128a512.256 512.256 0 0 1-991.744 0H0V384h1040.128c56.96-220.928 257.28-384 495.872-384z m0 256a256 256 0 0 0-222.848 129.92l1.152-2.048a254.72 254.72 0 0 0-20.096 43.776l2.048-5.632c-2.56 6.784-4.864 13.696-6.784 20.736l4.736-15.104a253.952 253.952 0 0 0-6.144 20.352l1.408-5.12a254.208 254.208 0 0 0-4.48 18.304l3.072-13.184a254.592 254.592 0 0 0-4.608 21.76l1.536-8.576a257.28 257.28 0 0 0-0.128 100.736l-1.28-7.296c1.28 7.296 2.688 14.464 4.48 21.504l-3.2-14.208c1.28 6.528 2.816 12.928 4.608 19.328l-1.28-5.12c1.664 6.784 3.712 13.568 6.016 20.224l-4.736-15.104c1.92 7.04 4.224 13.952 6.784 20.736l-2.048-5.632a256 256 0 0 0 464.64 41.728L1757.696 640a254.72 254.72 0 0 0 20.096-43.52l-2.048 5.376c2.56-6.656 4.864-13.568 6.784-20.608l-4.736 15.104c2.304-6.656 4.352-13.312 6.144-20.224l-1.28 5.12c1.664-6.4 3.2-12.8 4.48-19.2l-3.2 14.08c1.792-7.04 3.328-14.208 4.48-21.504l-1.28 7.296a257.28 257.28 0 0 0-0.128-100.736l1.536 8.576a254.592 254.592 0 0 0-4.608-21.76l3.072 13.184a254.208 254.208 0 0 0-4.48-18.432l1.408 5.248a253.952 253.952 0 0 0-6.144-20.352l4.736 15.104a253.952 253.952 0 0 0-6.784-20.736l2.048 5.632A256 256 0 0 0 1536 256z",
        },
        grid: {
          top: "15%",
          left: "3%",
          right: "4%",
          bottom: "12%",
          containLabel: true,
        },
        toolbox: {
          feature: {
            saveAsImage: {
              show: false,
            },
          },
        },
        xAxis: {
          type: "category",
          boundaryGap: false,
          axisLine: {
            lineStyle: {
              color: "#f2f3f5",
            },
          },
          axisTick: {
            show: false,
            alignWithLabel: true,
          },
          axisLabel: {
            formatter: function (value, index) {
              if (index === 0) {
                return ``;
              } else if (index === data.length - 1) {
                return ``;
              }

              const dateArr = value.split("-");

              dateArr.shift();
              return dateArr.join("-");
            },
            textStyle: {
              color: "#8E939E",
            },
          },
          data: dateRange,
        },
        yAxis: {
          type: "value",
        },
        series: [
          {
            name: "事件压缩比",
            type: "line",
            stack: "Total",
            color: "#1f69ff",
            smooth: true,
            showAllSymbol: false,
            areaStyle: {
              color: {
                type: "linear",
                x: 0,
                y: 0,
                x2: 0,
                y2: 1,
                colorStops: [
                  {
                    offset: 0,
                    color: "rgba(31,105,255,0.12)", // 渐变起始颜色
                  },
                  {
                    offset: 1,
                    color: "rgba(31,105,255,0.06)", // 渐变结束颜色
                  },
                ],
              },
            },
            data: data.map((item) => Number(item / 100).toFixed(2)),
          },
        ],
      };
    },
  },
  {
    title: "分类统计",
    tip: [
      "该数据统计提供了工作空间下三类的告警数量，数据会根据规则产生动态变化。",
      "所有告警：指命中分派策略的去重告警量",
      "未分派告警：指未命中任何分派策略的去重告警量",
      "屏蔽告警：指命中屏蔽规则的去重告警量",
    ],
    fetchFunc: OverviewApi.alarmCount.bind(OverviewApi),
    handleOption: (data) => {
      return {
        tooltip: {
          trigger: "axis",
          axisPointer: {
            // Use axis to trigger tooltip
            type: "shadow", // 'shadow' as default; can also be 'line' or 'shadow'
          },
          className:
            "control-alarm-total-tooltip control-alarm-statics-tooltip  w210",
          formatter: function (params: any) {
            const tooltipHtml = `
          <div class="date">${params[0]["axisValueLabel"]}</div>
             <div class="gap"></div>
          <div class="flex-bt text">
            <span class="point color1">${params[0]["seriesName"]}</span><span>${params[0]["value"]}</span>
          </div>
          <div class="flex-bt text">
            <span class="point color2">${params[1]["seriesName"]}</span><span>${params[1]["value"]}</span>
          </div>
          <div class="flex-bt text">
            <span class="point color3">${params[2]["seriesName"]}</span><span>${params[2]["value"]}</span>
          </div>
              `;
            return tooltipHtml;
          },
        },
        legend: {
          selectedMode: false,
          bottom: 0,
          itemWidth: 12, // 设置图例项的宽度
          itemHeight: 12, // 设置图例项的高度
          icon: 'path://M 0 0 L 50 0 L 50 50 L 0 50 L 0 0 Z" id="蓝色圆圈-信息"',
        },
        grid: {
          top: "15%",
          left: "3%",
          right: "4%",
          bottom: "12%",
          containLabel: true,
        },
        yAxis: {
          type: "value",
        },
        xAxis: {
          type: "category",
          axisLine: {
            lineStyle: {
              color: "#f2f3f5",
            },
          },
          axisTick: {
            show: false,
            alignWithLabel: true,
          },
          axisLabel: {
            formatter: function (value, index) {
              const dateArr = value.split("-");
              dateArr.shift();
              return dateArr.join("-");
            },
            textStyle: {
              color: "#8E939E",
            },
          },
          data: dateRange,
        },
        series: [
          {
            name: "所有告警(已分派告警)",
            type: "bar",
            stack: "total",
            color: "#1f69ff",
            data: data.reduce(
              (total, item) => total.concat(item.statisticsItems[2].num),
              []
            ),
          },
          {
            name: "未分派告警",
            type: "bar",
            stack: "total",
            color: "#00c7ff",
            data: data.reduce(
              (total, item) => total.concat(item.statisticsItems[1].num),
              []
            ),
          },
          {
            name: "屏蔽告警",
            type: "bar",
            stack: "total",
            color: "#ecf2fe",
            data: data.reduce(
              (total, item) => total.concat(item.statisticsItems[0].num),
              []
            ),
          },
        ],
      };
    },
  },
  {
    title: "监控来源",
    tip: ["分别统计不同监控来源产生的去重告警量"],
    fetchFunc: OverviewApi.originalTend.bind(OverviewApi),
    handleOption: (data) => {
      return {
        tooltip: {
          trigger: "axis",
          className:
            "control-alarm-total-tooltip control-alarm-statics-tooltip w180",
          formatter: function (params: any) {
            let dataContent = "";
            params.forEach((item, index) => {
              dataContent += `
              <div class="flex-bt text">
                <span class="point color_${index}">${item["seriesName"]}</span><span>${item["value"]}</span>
              </div>`;
            });

            const timeHeader = `
              <div class="date">${params[0]["axisValueLabel"]}</div>
              <div class="gap"></div>`;
            return timeHeader + dataContent;
          },
        },
        legend: {
          selectedMode: false,
          data: MonitorTypes,
          bottom: 0,
          formatter: function (name) {
            return name + "     ";
          },

          icon: "path://M1536 0a512.256 512.256 0 0 1 495.872 384H3072v256h-1040.128a512.256 512.256 0 0 1-991.744 0H0V384h1040.128c56.96-220.928 257.28-384 495.872-384z m0 256a256 256 0 0 0-222.848 129.92l1.152-2.048a254.72 254.72 0 0 0-20.096 43.776l2.048-5.632c-2.56 6.784-4.864 13.696-6.784 20.736l4.736-15.104a253.952 253.952 0 0 0-6.144 20.352l1.408-5.12a254.208 254.208 0 0 0-4.48 18.304l3.072-13.184a254.592 254.592 0 0 0-4.608 21.76l1.536-8.576a257.28 257.28 0 0 0-0.128 100.736l-1.28-7.296c1.28 7.296 2.688 14.464 4.48 21.504l-3.2-14.208c1.28 6.528 2.816 12.928 4.608 19.328l-1.28-5.12c1.664 6.784 3.712 13.568 6.016 20.224l-4.736-15.104c1.92 7.04 4.224 13.952 6.784 20.736l-2.048-5.632a256 256 0 0 0 464.64 41.728L1757.696 640a254.72 254.72 0 0 0 20.096-43.52l-2.048 5.376c2.56-6.656 4.864-13.568 6.784-20.608l-4.736 15.104c2.304-6.656 4.352-13.312 6.144-20.224l-1.28 5.12c1.664-6.4 3.2-12.8 4.48-19.2l-3.2 14.08c1.792-7.04 3.328-14.208 4.48-21.504l-1.28 7.296a257.28 257.28 0 0 0-0.128-100.736l1.536 8.576a254.592 254.592 0 0 0-4.608-21.76l3.072 13.184a254.208 254.208 0 0 0-4.48-18.432l1.408 5.248a253.952 253.952 0 0 0-6.144-20.352l4.736 15.104a253.952 253.952 0 0 0-6.784-20.736l2.048 5.632A256 256 0 0 0 1536 256z",
        },
        grid: {
          top: "10%",
          left: "3%",
          right: "4%",
          bottom: "8%",
          containLabel: true,
        },
        toolbox: {
          feature: {
            saveAsImage: {
              show: false,
            },
          },
        },
        xAxis: {
          type: "category",
          boundaryGap: false,
          axisLine: {
            lineStyle: {
              color: "#f2f3f5",
            },
          },
          axisTick: {
            show: false,
            alignWithLabel: true,
          },
          axisLabel: {
            formatter: function (value, index) {
              if (index === 0) {
                return ``;
              } else if (index === data.length - 1) {
                return ``;
              }

              const dateArr = value.split("-");

              dateArr.shift();
              return dateArr.join("-");
            },
            textStyle: {
              color: "#8E939E",
            },
          },
          data: dateRange,
        },
        yAxis: {
          type: "value",
        },
        series: MonitorTypes.map((item, index) => {
          return {
            name: item.label,
            type: "line",
            smooth: true,
            showAllSymbol: false,
            color: `rgb(${item.color})`,
            areaStyle: {
              color: {
                type: "linear",
                x: 0,
                y: 0,
                x2: 0,
                y2: 1,
                colorStops: [
                  {
                    offset: 0,
                    color: `rgba(${item.color},0.12)`, // 渐变起始颜色
                  },
                  {
                    offset: 1,
                    color: `rgba(${item.color},0.06)`, // 渐变结束颜色
                  },
                ],
              },
            },
            data: data.map((item) => item.statisticsItems[index]?.num || 0),
          };
        }),
      };
    },
  },
  {
    title: "TOP10 高频数据单元排行",
    tip: [
      "按照数据单元的维度进行统计告警量，且根据告警量进行排行。",
      "告警量：指命中分派策略的去重告警量",
      "分派策略引用量：统计被命中的分派策略分类",
    ],
    type: "table",
    fetchFunc: OverviewApi.dataGroupRank.bind(OverviewApi),
    mockData: () => {
      return [
        {
          top: 1,
          unit: 2,
          alertCount: 3,
          strategy: 4,
        },
        {
          top: 2,
          unit: 2,
          alertCount: 3,
          strategy: 4,
        },
        {
          top: 3,
          unit: 2,
          alertCount: 3,
          strategy: 4,
        },
      ];
    },
  },
];

onMounted(() => {
  initBodySize();
  window.addEventListener("resize", initBodySize);
});

onUnmounted(() => {
  window.removeEventListener("resize", initBodySize);
});
</script>

<style lang="scss" scoped>
.guid-enter-active {
  -webkit-animation: guid-in 0.5s ease-in-out;
  animation: guid-in 0.5s ease-in-out;
}

.guid-leave-active {
  animation: guid-in 0.5s ease-in-out reverse;
}

@keyframes guid-in {
  0% {
    -webkit-transform: scale(0);
    transform: scale(0);
    -webkit-transform-origin: bottom right;
    transform-origin: bottom right;
    right: 24px;
    bottom: 70px;
  }

  to {
    -webkit-transform: scale(1);
    transform: scale(1);
    -webkit-transform-origin: bottom right;
    transform-origin: bottom right;
    right: 0;
    bottom: 0;
  }
}

.show-guide {
  position: fixed;
  right: 20px;
  bottom: 0px;
  z-index: 999;
  width: 80px;
  height: 80px;
  cursor: pointer;
}
</style>
<style lang="scss">
.overview-container {
  position: relative;
  --gap-16: 16px;

  .chart-container {
    // display: grid;
    // grid-template-columns: 1fr 1fr;
    // gap: var(--gap-16);
    // margin-top: var(--gap-16);
    display: flex;
    // gap: var(--gap-16);
    width: 100%;
    flex-wrap: wrap;

    .item {
      // width: 100%;
      width: 50%;
      height: 372px;
    }
    .item-h {
      height: 530px;
    }
    .item:nth-child(odd) {
      padding-top: var(--gap-16);
      padding-right: calc(var(--gap-16) / 2);
    }
    .item:nth-child(even) {
      padding-top: var(--gap-16);
      padding-left: calc(var(--gap-16) / 2);
    }
    .block-container {
      width: 100%;
      height: 100%;
      background: white;
      padding: 24px;
      border-radius: 8px;
    }
  }
}

.card-tip-header {
  display: flex;
  justify-content: space-between;
}
// .card-tip-title {
//   display: flex;
//   align-items: center;
// }

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
  // line-height: 32px;
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
  margin: 2px 5px 0 0;
  position: relative;
  top: 0.5px;
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

.control-alarm-pie-level-tooltip,
.control-alarm-pie-task-tooltip {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 4px;
  // box-shadow: 0px 2px 10px 0px rgba(23, 24, 32, 0.1);
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
    top: 1px;
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
.control-alarm-total-tooltip {
  width: 144px;
  // height: 174px;
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
    width: 100%;
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

  .point.severity::before {
    border: 2px solid #f55863;
  }
  .point.alarm::before {
    border: 2px solid #ffd940;
  }
  .point.info::before {
    border: 2px solid #1f69ff;
  }
}
.control-alarm-statics-tooltip {
  .point::before {
    content: "";
    display: inline-block;
    height: 6px;
    width: 6px;
    border-radius: 50%;
    margin: 1px 5px 0 0;
    position: relative;
    top: 0px;
    border: 2px solid;
  }
  .point.color1::before {
    border-color: #1f69ff;
  }
  .point.color2::before {
    border-color: #00c7ff;
  }
  .point.color3::before {
    border-color: #ecf2fe;
  }
  // 监控系统取色
  .point.color_0::before {
    border-color: #1f69ff;
  }
  .point.color_1::before {
    border-color: #00c7ff;
  }
  .point.color_2::before {
    border-color: #ff72cf;
  }
  .point.color_3::before {
    border-color: #ffd940;
  }
  .point.color_4::before {
    border-color: #2353b5;
  }
  .point.color_5::before {
    border-color: #9b6bff;
  }
  .point.color_6::before {
    border-color: #30f0f0;
  }
  .point.color_7::before {
    border-color: #67c63a;
  }
}
.w210 {
  width: 210px;
}
.w180 {
  width: 180px;
}
</style>
