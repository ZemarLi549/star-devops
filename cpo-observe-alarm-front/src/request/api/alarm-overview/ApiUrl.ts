enum ApiUrl {
  levelStatistics =  '/alarm-manager/statistics/level', // 告警级别分析
  cateStatistics = '/alarm-manager/statistics/cate', // 告警类型分析
  originalTend = '/alarm-manager/analysis/source', // 告警来源趋势图
  alarmCount =  '/alarm-manager/analysis/classification', // 不同类别告警数量
  levelTend = '/alarm-manager/analysis/level', // 告警级别趋势图
  taskStatistics = '/alarm-manager/statistics/task', // 告警任务分析
  eventCompress = '/alarm-manager/event/compression', // 事件压缩比
  alarmOverview = '/alarm-manager/alarm/overview', // 告警概览
  dataGroupRank = '/alarm-manager/top/dataGroup', // 数据单元排行
  guideState = '/auth/guideState', // 新手指引
}
export default ApiUrl
