enum ApiUrl {
  getNoticePoliceList = '/alarm-manager/notify/policy/page',  // 通知策略分页列表
  createNoticePolice = '/alarm-manager/notify/policy/create', // 创建通知策略
  editNoticePolice = '/alarm-manager/notify/policy/edit',  // 编辑通知策略
  delNotice = '/alarm-manager/notify/policy/delete', // 删除通知策略
  disableNoticePolice = '/alarm-manager/notify/policy/disable',
  enableNoticePolice = '/alarm-manager/notify/policy/enable',
  noticePoliceListDetail = '/alarm-manager/notify/policy/clone', // 根据notifyId获取详情
  
}
export default ApiUrl