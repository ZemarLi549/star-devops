/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import dayjs from "dayjs";
import { RespFields, MetricQueryTypes, Calculations } from "./data";
import { ElMessage } from "element-plus";
import type { Instance, Endpoint, Service } from "@/types/selector";
import type { MetricConfigOpt } from "@/types/dashboard.d.ts";
import { useAppStoreWithOut } from "@/stores/modules/app";

export function useQueryPodsMetrics(
  pods: Array<Instance | Endpoint | Service | any>,
  config: {
    metrics: string[];
    metricTypes: string[];
    metricConfig: MetricConfigOpt[];
  },
  scope: string,
) {
  const appStore = useAppStoreWithOut();
  const metricTypes = (config.metricTypes || []).filter((m: string) => m);
  if (!metricTypes.length) {
    return;
  }
  const metrics = (config.metrics || []).filter((m: string) => m);
  if (!metrics.length) {
    return;
  }
  const conditions: { [key: string]: unknown } = {
    duration: appStore.durationTime,
  };
  const variables: string[] = [`$duration: Duration!`];
  const fragmentList = pods.map((d: (Instance | Endpoint | Service) & { normal: boolean }, index: number) => {
    const param = {
      scope,
      serviceName: d.value,
      normal: d.normal,
    };
    const f = metrics.map((name: string, idx: number) => {
      const metricType = metricTypes[idx] || "";
      variables.push(`$condition${index}${idx}: MetricsCondition!`);
      conditions[`condition${index}${idx}`] = {
        name,
        entity: param,
      };
      let labelStr = "";
      if (metricType === MetricQueryTypes.ReadLabeledMetricsValues) {
        const c = config.metricConfig[idx] || {};
        variables.push(`$labels${index}${idx}: [String!]!`);
        labelStr = `labels: $labels${index}${idx}, `;
        const labels = (c.labelsIndex || "").split(",").map((item: string) => item.replace(/^\s*|\s*$/g, ""));
        conditions[`labels${index}${idx}`] = labels;
      }
      return `${name}${index}${idx}: ${metricType}(condition: $condition${index}${idx}, ${labelStr}duration: $duration)${RespFields[metricType]}`;
    });
    return f;
  });
  const fragment = fragmentList.flat(1).join(" ");
  const queryStr = `query queryData(${variables}) {${fragment}}`;

  return { queryStr, conditions };
}

export function usePodsSource(
  pods: Array<Instance | Endpoint>,
  resp: { errors: string; data: { [key: string]: any } },
  config: {
    metrics: string[];
    metricTypes: string[];
    metricConfig: MetricConfigOpt[];
  },
): any {
  if (resp.errors) {
    ElMessage.error(resp.errors);
    return {};
  }
  const names: string[] = [];
  const metricConfigArr: MetricConfigOpt[] = [];
  const metricTypesArr: string[] = [];
  const data = pods.map((d: Instance | any, idx: number) => {
    config.metrics.forEach((name: string, index: number) => {
      const c: any = (config.metricConfig && config.metricConfig[index]) || {};
      const key = name + idx + index;
      if (config.metricTypes[index] === MetricQueryTypes.ReadMetricsValue) {
        d[name] = aggregation(resp.data[key], c);
        if (idx === 0) {
          names.push(name);
          metricConfigArr.push(c);
          metricTypesArr.push(config.metricTypes[index]);
        }
      }
      if (config.metricTypes[index] === MetricQueryTypes.ReadMetricsValues) {
        d[name] = {};
        if (
          [Calculations.Average, Calculations.ApdexAvg, Calculations.PercentageAvg, Calculations.CPM5DAvg].includes(
            c.calculation,
          )
        ) {
          d[name]["avg"] = calculateExp(resp.data[key].values.values, c);
        }
        d[name]["values"] = resp.data[key].values.values.map((val: { value: number }) => aggregation(val.value, c));
        if (idx === 0) {
          names.push(name);
          metricConfigArr.push(c);
          metricTypesArr.push(config.metricTypes[index]);
        }
      }
      if (config.metricTypes[index] === MetricQueryTypes.ReadLabeledMetricsValues) {
        const resVal = resp.data[key] || [];
        const labels = (c.label || "").split(",").map((item: string) => item.replace(/^\s*|\s*$/g, ""));
        const labelsIdx = (c.labelsIndex || "").split(",").map((item: string) => item.replace(/^\s*|\s*$/g, ""));
        for (let i = 0; i < resVal.length; i++) {
          const item = resVal[i];
          const values = item.values.values.map((d: { value: number }) => aggregation(Number(d.value), c));
          const indexNum = labelsIdx.findIndex((d: string) => d === item.label);
          let key = item.label;
          if (labels[indexNum] && indexNum > -1) {
            key = labels[indexNum];
          }
          if (!d[key]) {
            d[key] = {};
          }
          if (
            [Calculations.Average, Calculations.ApdexAvg, Calculations.PercentageAvg, Calculations.CPM5DAvg].includes(
              c.calculation,
            )
          ) {
            d[key]["avg"] = calculateExp(item.values.values, c);
          }
          d[key]["values"] = values;
          if (idx === 0) {
            names.push(key);
            metricConfigArr.push({ ...c, index: i });
            metricTypesArr.push(config.metricTypes[index]);
          }
        }
      }
    });
    return d;
  });
  return { data, names, metricConfigArr, metricTypesArr };
}

function calculateExp(arr: { value: number }[], config: { calculation?: string }): (number | string)[] {
  const sum = arr.map((d: { value: number }) => d.value).reduce((a, b) => a + b);
  let data: (number | string)[] = [];
  switch (config.calculation) {
    case Calculations.Average:
      data = [(sum / arr.length).toFixed(2)];
      break;
    case Calculations.PercentageAvg:
      data = [(sum / arr.length / 100).toFixed(2)];
      break;
    case Calculations.ApdexAvg:
      data = [(sum / arr.length / 10000).toFixed(2)];
      break;
    case Calculations.CPM5DAvg:
      data = [
        sum / arr.length / 100000 < 1 && sum / arr.length / 100000 !== 0
          ? (sum / arr.length / 100000).toFixed(5)
          : (sum / arr.length / 100000).toFixed(2),
      ];
      break;
    default:
      data = arr.map((d) => aggregation(d.value, config));
      break;
  }
  return data;
}

export function aggregation(val: number, config: { calculation?: string }): number | string {
  let data: number | string = Number(val);

  switch (config.calculation) {
    case Calculations.Percentage:
      data = (val / 100).toFixed(2);
      break;
    case Calculations.PercentageAvg:
      data = (val / 100).toFixed(2);
      break;
    case Calculations.ByteToKB:
      data = (val / 1024).toFixed(2);
      break;
    case Calculations.ByteToMB:
      data = (val / 1024 / 1024).toFixed(2);
      break;
    case Calculations.ByteToGB:
      data = (val / 1024 / 1024 / 1024).toFixed(2);
      break;
    case Calculations.Apdex:
      data = (val / 10000).toFixed(2);
      break;
    case Calculations.CPM5D:
      data = val / 100000 < 1 && val / 100000 !== 0 ? (val / 100000).toFixed(5) : (val / 100000).toFixed(2);
      break;
    case Calculations.ConvertSeconds:
      data = dayjs(val * 1000).format("YYYY-MM-DD HH:mm:ss");
      break;
    case Calculations.ConvertMilliseconds:
      data = dayjs(val).format("YYYY-MM-DD HH:mm:ss");
      break;
    case Calculations.MsToS:
      data = (val / 1000).toFixed(2);
      break;
    case Calculations.SecondToDay:
      data = (val / 86400).toFixed(2);
      break;
    case Calculations.NanosecondToMillisecond:
      data = (val / 1000 / 1000).toFixed(2);
      break;
    case Calculations.ApdexAvg:
      data = (val / 10000).toFixed(2);
      break;
    case Calculations.CPM5DAvg:
      data = val / 100000 < 1 && val / 100000 !== 0 ? (val / 100000).toFixed(5) : (val / 100000).toFixed(2);
      break;
    default:
      data;
      break;
  }

  return data;
}
