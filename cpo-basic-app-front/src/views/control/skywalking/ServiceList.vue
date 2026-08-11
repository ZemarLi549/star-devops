<template>
  <el-table
    :data="services"
    @cell-mouse-enter="cellMouseEnter"
    @cell-mouse-leave="cellMouseLeave"
    :cell-style="setCellStyle"
    @sort-change="sortChange"
    v-loading="loading"
  >
    <el-table-column
      label="应用名称"
      min-width="150"
      show-overflow-tooltip
      fixed
    >
      <template #default="scope">
        <span
          class="service-link"
          @click="clickService(scope)"
          :class="{ 'table-active-text': activeTableId == scope.row.id }"
          >{{ scope.row.label }}</span
        >
      </template>
    </el-table-column>
    <el-table-column label="数据单元" min-width="150" show-overflow-tooltip>
      <template #default="scope">
        {{ scope.row.groupName ? scope.row.groupName : "-" }}
      </template>
    </el-table-column>
    <el-table-column
      v-if="isMicroProjectDeploy(PROJECT_MAP.OBSERVE_LOG)"
      label="错误日志数"
      width="120"
      show-overflow-tooltip
      :sortable="true"
      :sort-method="
        (a, b) => {
          return a.errLogsCount - b.errLogsCount;
        }
      "
    >
      <template #default="scope">
        <div class="cell-content" v-if="scope.row.isErrLogsShow">
          <span
            class="link-number"
            :class="{ 'link-number-red': scope.row.errLogsCount > 0 }"
            @click="goLogAnalysis(scope.row)"
          >
            <span class="link-number-text link-number-text-ell">{{
              scope.row.errLogsCount
            }}</span>
            <span class="link-number-icon">
              <FontIcon icon="icon-biaoge-youjiantou" />
            </span>
          </span>
          <el-popover
            placement="left"
            :width="400"
            trigger="hover"
            :show-after="100"
            @after-enter="showErrLogs(scope.row)"
          >
            <template #reference>
              <span class="trend">
                <FontIcon icon="icon-qushiicon" />
              </span>
            </template>
            <div class="view-line">
              <ErrorLogLine :index="scope.$index" ref="errorLogLine" />
            </div>
          </el-popover>
        </div>
        <div v-else>-</div>
      </template>
    </el-table-column>
    <el-table-column
      label="错误数"
      width="120"
      :sortable="true"
      :sort-method="
        (a, b) => {
          return a.errorsTotal - b.errorsTotal;
        }
      "
    >
      <template #default="scope">
        <div class="cell-content" @click="clickErrors(scope)">
          <span
            class="link-number"
            :class="{ 'link-number-red': scope.row.errorsTotal > 0 }"
          >
            <span class="link-number-text">{{ scope.row.errorsTotal }}</span>
            <span class="link-number-icon">
              <FontIcon icon="icon-biaoge-youjiantou" />
            </span>
          </span>
        </div>
      </template>
    </el-table-column>
    <template v-if="services.length === 0">
      <el-table-column
        v-for="(item, index) in config.metricConfig"
        :key="index"
        :label="`${item.label} ${item.unit ? `(${item.unit})` : ''}`"
        width="150"
        :sortable="true"
      >
        {{ "-" }}
      </el-table-column>
    </template>
    <el-table-column
      v-for="(metric, index) in colMetrics"
      :label="`${metricConfig[index].label} ${
        metricConfig[index].unit ? `(${metricConfig[index].unit})` : ''
      }`"
      :key="metric + index"
      width="150"
      :sortable="true"
      :sort-method="
        (a, b) => {
          return a[metric].avg[0] - b[metric].avg[0];
        }
      "
    >
      <template #default="scope">
        <div class="cell-content">
          <span class="text-number">
            {{ scope.row[metric].avg[0] }}
          </span>
          <el-popover placement="left" :width="400" trigger="hover">
            <template #reference>
              <span class="trend">
                <FontIcon icon="icon-qushiicon" />
              </span>
            </template>
            <div class="view-line">
              <Line
                :data="{
                  [metric]: scope.row[metric] && scope.row[metric].values,
                }"
                :intervalTime="appStore.intervalTime"
              />
            </div>
          </el-popover>
        </div>
      </template>
    </el-table-column>

    <template #empty>
      <TableEmpty />
    </template>
  </el-table>
</template>

<script setup lang="ts">
import graphql from "@/apis/graphql";
import type { AxiosResponse } from "axios";
import { onMounted, reactive, ref, watch, nextTick } from "vue";
import type { Service } from "@/types/selector";
import query from "@/apis/graphql/fetch";
import {
  useQueryPodsMetrics,
  usePodsSource,
} from "@/hooks/useMetricsProcessor";
import { useAppStoreWithOut } from "@/stores/modules/app";
import Line from "./Line.vue";
import { useUserStore } from "@/stores/modules/user";
import { ElMessage } from "element-plus";
import type { MetricConfigOpt } from "@/types/dashboard";
import ControlApi from "@/apis/control/index";
import dayjs from "dayjs";
import ErrorLogLine from "./ErrorLogLine.vue";
import TableEmpty from "@/components/empty/TableEmpty.vue";
import isMicroProjectDeploy from "@/utils/isMicroProjectDeploy";
import { PROJECT_MAP } from "@/globals/constant";

const emits = defineEmits(["changeTitle"]);

const props = defineProps({
  observeDate: {
    type: Array,
  },
});

const userStore = useUserStore();

const sortChange = ({ column }) => {
  emits("changeTitle", column.label.split("(")[0]);
};
const services = ref<any[]>([]);
const colMetrics = ref<string[]>([]);
const metricConfig = ref<any[]>([]);
const metricTypes = ref<string[]>([]);
const appStore = useAppStoreWithOut();
const activeTableId = ref();
// 错误日志趋势图
const errLogsMap: any = ref({});
const errorLogLine = ref();
const logService = ref([]); //日志的应用
const loading = ref(false);

const showErrLogs = (row) => {
  const app = logService.value.find(
    (item) => item.applicationName === row.label
  );
  const reqData = {
    applicationCode: app.applicationCode,
    startTime: dayjs(Number(props.observeDate[0])).startOf("minute").unix(),
    endTime: dayjs(Number(props.observeDate[1])).endOf("minute").unix(),
  };
  if (!errLogsMap.value[row.value]) {
    errLogsMap.value[row.value] = {
      intervalTime: [],
      metric: [],
    };
    ControlApi.getErrorLogDistribution(reqData)
      .then((res) => {
        errLogsMap.value[row.value]["intervalTime"] = res.logDistribution.map(
          (item: any) => dayjs(item.startTime * 1000).format("YYYY-MM-DD HH:mm")
        );
        errLogsMap.value[row.value]["metric"] = res.logDistribution.map(
          (item: any) => {
            if (item.totalCount === 0) return 0;
            return item.groupList.find((item) => item.groupBy === "ERROR")
              .groupCount;
          }
        );
        errorLogLine.value?.init(errLogsMap.value[row.value]);
      })
      .catch((err) => {
        console.log(err);
      });
  } else {
    errorLogLine.value?.init(errLogsMap.value[row.value]);
  }
};
// 跳转日志
const goLogAnalysis = (row) => {
  if (!userStore.hasPathAuth("/business-observe/observe-log/log-analysis"))
    return ElMessage.warning("暂无使用权限，请联系相关管理员");

  window.open(
    `/business-observe/observe-log/log-analysis?searchContent=level : "ERROR" &start=${
      appStore.durationRow.start.getTime() / 1000
    }&end=${appStore.durationRow.end.getTime() / 1000}&serviceName=${row.label}`
  );
};

const cellMouseEnter = (row: any) => {
  activeTableId.value = row.id;
};
const cellMouseLeave = (row: any) => {
  activeTableId.value = null;
};

async function queryServices() {
  const userStore = useUserStore();
  const { workspace_id } = userStore.userInfo;
  loading.value = true;
  try {
    const res: AxiosResponse = await graphql
      .query("queryServices")
      .params({ layer: "GENERAL", workspaceId: workspace_id });
    const services = res.data.data.services;

    // 查询错误数
    const serviceList = services.map((item) => item.id);
    // if (!serviceList.length) {
    //   return;
    // }

    const res_errors: AxiosResponse = await graphql
      .query("getServiceErrorsPm")
      .params({
        serviceIds: serviceList,
        duration: useAppStoreWithOut().durationTime,
      });
    services.forEach((service, index) => {
      const errors = res_errors.data.data.services[index].values.values;
      service.errorsTotal = errors.pop()["value"];
      service.errors = errors;
    });
    services.sort((a, b) => b.errorsTotal - a.errorsTotal);
    await queryServiceMetrics(services.slice(0, 10));
  } finally {
    loading.value = false;
  }
}

onMounted(async () => {
  await queryServices();
});
const config = reactive({
  metricConfig: ref<MetricConfigOpt[]>([
    {
      label: "请求量",
      unit: "次/分",
      calculation: "average",
      labelsIndex: "1",
      sortOrder: "",
    },
    {
      calculation: "percentageAvg",
      unit: "%",
      label: "成功率",
      labelsIndex: "2",
      sortOrder: "",
    },
    {
      calculation: "average",
      label: "平均耗时",
      unit: "毫秒",
      labelsIndex: "3",
      sortOrder: "",
    },
    {
      calculation: "apdexAvg",
      label: "健康度",
      labelsIndex: "4",
      sortOrder: "",
    },
  ]),
  metrics: ["service_cpm", "service_sla", "service_resp_time", "service_apdex"],
  metricTypes: [
    "readMetricsValues",
    "readMetricsValues",
    "readMetricsValues",
    "readMetricsValues",
  ],
});
async function queryServiceMetrics(currentServices: Service[]) {
  if (!currentServices.length) {
    return;
  }

  try {
    if (isMicroProjectDeploy(PROJECT_MAP.OBSERVE_LOG)) {
      logService.value = (await ControlApi.getLogsList()) || [];
      // 查询错误日志数
      // const currentServicesValue = currentServices.map((item) => item.value);
      let currentServicesValue: any = JSON.parse(
        JSON.stringify(currentServices)
      );
      currentServicesValue = currentServicesValue.map((service: any) => {
        const arr: any = logService.value.find(
          (item: any) => item.applicationName === service.label
        );
        return arr?.applicationCode;
      });
      currentServicesValue = currentServicesValue.filter(
        (item: any) => item !== undefined
      );
      let ApplicationErrorResult = [];
      if (currentServicesValue.length) {
        const reqData = {
          applicationCodeList: currentServicesValue,
          startTime: dayjs(Number(props.observeDate[0]))
            .startOf("minute")
            .unix(),
          endTime: dayjs(Number(props.observeDate[1])).endOf("minute").unix(),
        };
        ApplicationErrorResult = await ControlApi.getApplicationErrorLogCount(
          reqData
        );
      }
      const logsList = logService.value.map((item) => item.applicationName);
      currentServices = currentServices.map((item) => {
        const isErrLogsShow = logsList.includes(item.label);
        let app;
        if (isErrLogsShow) {
          app = logService.value.find(
            (val) => val.applicationName === item.label
          );
        }

        return {
          ...item,
          errLogsCount: isErrLogsShow
            ? ApplicationErrorResult[app.applicationCode]
            : 0,
          isErrLogsShow: isErrLogsShow,
        };
      });
    }
  } catch (error) {
    console.log(error);
  }

  const metrics = config.metrics || [];
  const types = config.metricTypes || [];
  if (metrics.length && metrics[0] && types.length && types[0]) {
    const params = await useQueryPodsMetrics(
      currentServices,
      { ...config },
      "Service"
    );

    const json = await query(params);

    const { data, names, metricConfigArr, metricTypesArr } = usePodsSource(
      currentServices,
      json.data,
      { ...config }
    );
    services.value = fixServiceUseErrors(data);
    colMetrics.value = names;
    metricTypes.value = metricTypesArr;
    metricConfig.value = metricConfigArr;

    return;
  }
  services.value = currentServices;
}

function fixServiceUseErrors(data) {
  data.forEach((service) => {
    fixMetrics(service, "service_sla", "100"); // 修改成功率指标
    fixMetrics(service, "service_apdex", "1"); // 修改健康度指标
  });
  return data;
}

function fixMetrics(service, metric, fixValue) {
  const errors = service.errors;
  const metricValue = service[metric]?.values || [];
  if (!metricValue.length) return;
  metricValue.forEach((item, index) => {
    if (errors[index]["value"] === 0 && Number(item) === 0) {
      metricValue[index] = fixValue;
    }
  });
  const metric_sum = metricValue.reduce((a, b) => Number(a) + Number(b));
  service[metric].avg[0] = (metric_sum / metricValue.length).toFixed(2);
}

function clickService(scope: any) {
  const path = "/business-observe/observe-trace/general";
  if (!userStore.hasPathAuth(path))
    return ElMessage.warning("暂无使用权限，请联系相关管理员");
  const url = `/business-observe/observe-trace/general/dashboard/GENERAL/Service/${
    scope.row.id
  }/General-Service?start=${appStore.durationRow.start.getTime()}&end=${appStore.durationRow.end.getTime()}`;
  window.open(url);
}

function clickErrors(scope: any) {
  const path = "/business-observe/observe-trace/trace";
  if (!userStore.hasPathAuth(path))
    return ElMessage.warning("暂无使用权限，请联系相关管理员");
  const url = `${path}?id=${
    scope.row.id
  }&start=${appStore.durationRow.start.getTime()}&end=${appStore.durationRow.end.getTime()}`;
  window.open(url);
}

const setCellStyle = ({ row, columnIndex }) => {
  switch (columnIndex) {
    // case 2:
    //   if (Number(row.errLogsCount) > 0) {
    //     return { color: "red" };
    //   }
    //   return {};
    // case 3:
    //   if (Number(row.errorsTotal) > 0) {
    //     return { color: "red" };
    //   }
    //   return {};
    case 5:
      if (Number(row.service_sla.avg[0]) < 95) {
        return { color: "red" };
      }
      return {};
    case 6:
      if (Number(row.service_resp_time.avg[0]) > 2000) {
        return { color: "red" };
      }
      return {};
    default:
      return {};
  }
};

watch(
  () => appStore.intervalTime,
  () => {
    errLogsMap.value = {};
    queryServices();
  }
);
</script>
<style scoped lang="scss">
.basic-el-table {
  --basic-el-table-row-hover-bg-color: #f7f8fa !important;
}
.view-line {
  width: 380px;
  height: 200px;
}

.item {
  // display: inline-block;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
}
.cell-content {
  height: 24px;
  width: 98%; // 设置为100%在某些分辨率下回导致误触发tooltip
  display: flex;
  .link-number {
    cursor: pointer;
    border-radius: 2px;
    padding: 3px 6px;
    display: flex;
    align-items: center;
    &:hover {
      background: #eaecf0;
      .link-number-icon {
        color: #1f69ff;
      }
    }
    &.link-number-red {
      color: #fa3946;
    }
    &.link-number-red:hover {
      .link-number-icon {
        color: #fa3946;
      }
    }
    .link-number-icon {
      color: #bfbfbf;
    }
    .link-number-text-ell {
      max-width: 40px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
  }
  .text-number {
    line-height: 24px;
    margin-right: 4px;
    &.text-number-red {
      color: #fa3946;
    }
  }
}
.trend {
  width: 24px;
  height: 24px;
  line-height: 24px;
  border-radius: 2px;
  display: inline-block;
  cursor: pointer;
  flex-shrink: 0;
  text-align: center;
  color: #bfbfbf;
  &:hover {
    color: #8c8c8c;
    background: #eaecf0;
  }
}

.value {
  display: inline-block;
  // flex-grow: 2;
  height: 100%;
  margin-right: 6px;
  // width: calc(100% - 30px);
}
.service-link:hover {
  color: #1f69ff;
  cursor: pointer;
}

.red {
  color: #fa3946 !important;
}

.table-active-text {
  cursor: pointer;
  color: #1f69ff;
}

// .link-number {
//   cursor: pointer;
//   // margin-right: 5px;
//   // max-width: 85px;
//   // overflow: hidden;
//   // text-overflow: ellipsis;
//   // white-space: nowrap;
//   border-radius: 2px;
//   padding: 3px 6px;
//   &:hover {
//     background: #eaecf0;
//     .link-number-icon {
//       color: #fa3946;
//     }
//   }
//   .link-number-icon {
//     color: #bfbfbf;
//   }
// }
</style>
