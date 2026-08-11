enum ApiUrl {
  createChannel = '/alarm-manager/notify/channel/create', // 配置通知方式
  editChannel = '/alarm-manager/notify/channel/edit', // 编辑配置
  getTemplateList = '', // 通知模板list
  editTemplate = '', // 编辑模板
  getNoticeChannel ='/alarm-manager/notify/channel/list', // 获取通知方式, 带状态（是否配置）
}
export default ApiUrl