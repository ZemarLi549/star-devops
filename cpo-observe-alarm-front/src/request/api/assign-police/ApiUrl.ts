enum ApiUrl {
  getNotifyList = '/alarm-manager/notify/group/list',  // 获取通知列表
  getConditionList = '/alarm-manager/decision/condition/list',  // 获取判断条件
  getDataGroupTree = '/auth/sys/dataGroup/dataGroupTree',//数据单元
  getMetricsList = '/alarm-manager/decision/condition/list/metrics',//判断条件选项
  
  getNotifyPolicy = '/alarm-manager/notify/policy/list',//获取通知策略
  createAssignPolice = '/alarm-manager/assign/policy/create',//创建分派策略
  getAssignPoliceList = '/alarm-manager/assign/policy/page',//获取分派策略列表
  delAssignPoliceList = '/alarm-manager/assign/policy/delete',//删除分派策略
  disableAssignPoliceList = '/alarm-manager/assign/policy/disable',//停用分派策略
  enableAssignPoliceList = '/alarm-manager/assign/policy/enable',//启用分派策略
  assignPoliceListDetail = '/alarm-manager/assign/policy/detail',//分派策略详情
  editPoliceListDetail = '/alarm-manager/assign/policy/edit',//编辑策略详情
}
export default ApiUrl
