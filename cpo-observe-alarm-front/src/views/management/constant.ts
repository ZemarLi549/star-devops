export const TabList = [
  { key: "pending", value: "待处理" },
  { key: "dealing", value: "处理中" },
  { key: "closed", value: "已关闭" },
  { key: "total", value: "全部" },
];

export const alarmLevelOptions = [
  { value: 1, label: "信息" },
  { value: 2, label: "警告" },
  { value: 3, label: "次要" },
  { value: 4, label: "重要" },
  { value: 5, label: "严重" },
];

export const alarmCateOptions = [
  { value: 0, label: "其他" },
  { value: 1, label: "指标" },
  { value: 2, label: "链路" },
  { value: 3, label: "日志" },
  { value: 4, label: "拨测" },
];

export const alarmStateOptions = [
  { value: 0, label: "告警中" },
  { value: 2, label: "已关闭" },
];

export const taskStateOptions = [
  { value: 0, label: "待处理" },
  { value: 1, label: "处理中" },
  { value: 2, label: "已关闭" },
];

export const statusOptions = [
  { label: '发生时', value: 0 },
  { label: '认领时', value: 1 },
  { label: '关闭时', value: 2 },
]

export enum HandleType {
  ACK,
  CLOSE
}

export enum AckResultType {
  SUCCESS, // 手动操作成功
  IS_CHANGED, // 告警状态已经变化
  NO_ASSIGNS, // 没有可用的分派策略
  AUTO_CLOSE, // 自动关闭成功
}

export enum AlarmHandleType {
  PASS, // 可以认领
  CHANGED, // 状态已被改变化
  NO_ASSIGNS, // 没有启用的分派策略
}