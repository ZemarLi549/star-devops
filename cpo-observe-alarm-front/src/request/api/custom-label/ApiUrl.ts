enum ApiUrl {
  getCustomLabel = '/alarm-manager/custom/label/page', // 自定义标签分页查询
  getCustomLabelDetail = '/alarm-manager/custom/label/detail', // 自定义标签详情查询
  addCustomLabel = '/alarm-manager/custom/label/add', // 自定义标签新增
  deleteCustomLabel = '/alarm-manager/custom/label/delete', // 自定义标签删除
  editCustomLabel = '/alarm-manager/custom/label/edit', // 自定义标签更新

  enableCustomLabel = '/alarm-manager/custom/label/enable', // 自定义标签启用
  disableCustomLabel = '/alarm-manager/custom/label/disable', // 自定义标签禁用

  testCustomLabel = '/alarm-manager/custom/label/test', // 自定义标签校验JSON表达式
  getLabelTemplate = '/alarm-manager/label/tpl/get', // 自定义标签模板查询
  createLabelTemplate = '/alarm-manager/label/tpl/create', // 自定义标签模板创建
  editLabelTemplate = '/alarm-manager/label/tpl/edit', // 自定义标签模板创建
}

export default ApiUrl
