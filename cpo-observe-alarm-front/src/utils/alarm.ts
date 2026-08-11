import dayjs from "dayjs";
import { ElMessage } from "element-plus";
import useClipboard from "vue-clipboard3";
const { toClipboard } = useClipboard();
export const getAlarmLevel = (level) => {
  switch (level) {
    case 1:
      return "信息";
    case 2:
      return "警告";
    case 3:
      return "次要";
    case 4:
      return "重要";
    case 5:
      return "严重";
  }
};
export const getAlarmType = (type) => {
  switch (type) {
    case 0:
      return "其他";
    case 1:
      return "指标";
    case 2:
      return "链路";
    case 3:
      return "日志";
    case 4:
      return "拨测";
  }
};
export const getAlarmStatus = (status) => {
  switch (status) {
    case 0:
      return "告警中";
    case 2:
      return "已关闭";
  }
};
export const getTaskStatus = (status) => {
  switch (status) {
    case 0:
      return "待处理";
    case 1:
      return "处理中";
    case 2:
      return "已关闭";
  }
};

// 跳转分派策略
export const goAssignPolice = (id) => {
  window.open(`/alarm-center/observe-alarm/assign-police/view/${id}`);
};
// 跳转屏蔽规则
export const goShieldRule = (id) => {
  if(!id) return ElMessage({
    message: "屏蔽规则被删除，无法查看",
    type: "warning",
  });
  window.open(`/alarm-center/observe-alarm/shield-rule/views/${id}`);
};
// 跳转通知策略
export const goNoticePolice = (id) => {
  if(!id) return ElMessage({
    message: "通知策略被删除，无法查看",
    type: "warning",
  });
  window.open(`/alarm-center/observe-alarm/notice-police/detail/${id}`);
};

export const getLabelStatus = (status) => {
  switch (status) {
    case 0:
      return "已停用";
    case 1:
      return "已启用";
  }
};

// 修复时间戳秒, 开始时间00秒 - 结束时间59秒
export const fixTimestamp = (timestamp, type = "end") => {
  return type === "end"
    ? dayjs(timestamp).endOf("minute").valueOf()
    : dayjs(timestamp).startOf("minute").valueOf();
};

// 复制id
export const handleCopy = async (id) => {
  await toClipboard(id);
  ElMessage({
    message: "复制成功",
    type: "success",
  });
};
