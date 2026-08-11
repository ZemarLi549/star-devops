export const monitorTypeOptions = [
  { value: 0, label: "星迹可观测" },
  { value: 1, label: "Prometheus" },
  { value: 2, label: "华为ESight" },
  { value: 3, label: "华为FusionDirector" },
];
export const cateOptions = [
  { value: 1, label: "指标" },
  { value: 2, label: "链路" },
  { value: 3, label: "日志" },
  { value: 4, label: "拨测" },
];

export const StarTraceMonitor = monitorTypeOptions[0].value; // 星迹告警来源
export const OtherMonitorCate = 0; // 非星迹告警来源时类型的默认传值
