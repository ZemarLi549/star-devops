enum ApiUrl {
  getNotifyList = '/alarm-manager/notify/group/list',  // 获取通知列表
  getConditionList = '/alarm-manager/decision/condition/list',  // 获取判断条件
  getDataGroupTree = '/auth/sys/dataGroup/dataGroupTree',//数据单元
  getMetricsList = '/alarm-manager/decision/condition/list/metrics',//判断条件选项

  createShieldRule = '/alarm-manager/block/rule/create',//创建屏蔽规则
  getShieldRuleList = '/alarm-manager/block/rule/page',//获取屏蔽规则列表
  delShieldRuleList = '/alarm-manager/block/rule/delete',//删除屏蔽规则
  disableShieldRuleList = '/alarm-manager/block/rule/disable',//停用屏蔽规则
  enableShieldRuleList = '/alarm-manager/block/rule/enable',//启用屏蔽规则
  shieldRuleListDetail = '/alarm-manager/block/rule/detail',//屏蔽规则详情
  editShieldRule = '/alarm-manager/block/rule/edit',//编辑策略详情
}
export default ApiUrl
