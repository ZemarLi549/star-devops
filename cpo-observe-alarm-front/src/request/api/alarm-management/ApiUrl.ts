enum ApiUrl {
  
  ackAlarms = '/alarm-manager/alarm/agg/ack', // 我的/所有告警—批量认领
  closeAlarms = '/alarm-manager/alarm/agg/close', // 我的/所有告警—批量关闭
  getAlarmPage = '/alarm-manager/alarm/agg/page', // 我的/所有告警—分页查询列表
  getAlarmDetail = '/alarm-manager/alarm/agg/detail', // 我的/所有告警—查询详情
  getAlarmCount = '/alarm-manager/alarm/agg/count', // 查询各个类别的告警数量

  alarmCanClose = '/alarm-manager/alarm/agg/canClose', // 查询单条告警是否支持关闭
  alarmCanAck = '/alarm-manager/alarm/agg/canAck', // 查询单条告警是否支持认领

  getNoticePage = '/alarm-manager/notice/page', // 所有通知列表分页查询
  getNoticeDetail = '/alarm-manager/notice/detail', // 通知详情查询

  getUnassignPage = '/alarm-manager/alarm/unique/page/unassign', // 未分派告警分页查询
  getUnassignDetail = '/alarm-manager/alarm/unique/detail/unassign', // 未分派告警详情查询

  getBlockPage = '/alarm-manager/alarm/unique/page/block', // 屏蔽告警分页查询
  getBlockDetail = '/alarm-manager/alarm/unique/detail/block', // 屏蔽告警详情查询
  
  getAlarmEventPage = '/alarm-manager/event/page/agg', // 我的/所有告警—事件列表
  getUniqueEventPage = '/alarm-manager/event/page/unique', // 未分派告警和屏蔽告警-事件列表
  getUniquePage = '/alarm-manager/alarm/unique/page', // 我的/所有告警—去重告警列表
  
  getUniqueLabels = '/alarm-manager/alarm/unique/labels', // 查询指定去重告警的自定义标签
  getUniqueContent = '/alarm-manager/alarm/unique/content', // 查询指定去重告警下的事件原文

  getAssignPolicy = '/alarm-manager/assign/policy/list', // 查询所有告警分派策略
  getNotifyPolicy = '/alarm-manager/notify/policy/list',//获取通知策略
}

export default ApiUrl
