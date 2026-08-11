enum ApiUrl {
  getConditionList = '/alarm-manager/decision/condition/list',  // 获取判断条件
  getDataGroupTree = '/auth/sys/dataGroup/dataGroupTree',//数据单元
  getMetricsList = '/alarm-manager/decision/condition/list/metrics',//判断条件选项

  createPushPolice = '/alarm-manager/push/policy/create',//创建推送策略
  getPushPolice = '/alarm-manager/push/policy/page',//获取推送策略列表
  delPushPolice = '/alarm-manager/push/policy/delete',//删除推送策略
  disablePushPolice = '/alarm-manager/push/policy/disable',//停用推送策略
  enablePushPolice = '/alarm-manager/push/policy/enable',//启用推送策略
  pushPoliceDetail = '/alarm-manager/push/policy/detail',//推送策略详情
  editPushPolice = '/alarm-manager/push/policy/edit',//编辑策略详情
}
export default ApiUrl
