enum ApiUrl {
  getGroupList = '/alarm-manager/notify/group/list', // 获取通知组list
  createGroup = '/alarm-manager/notify/group/create', // 创建分组
  editGroup = '/alarm-manager/notify/group/edit', // 编辑分组
  delGroup = '/alarm-manager/notify/group/delete', //  删除分组
  getGroupUserList = '/alarm-manager/notify/group/pageUser', // 获取组下成员分页list（带搜索）
  addUser = '/alarm-manager/notify/group/addUsers', // 添加成员
  delUser = '/alarm-manager/notify/group/deleteUser', // 移除单成员
  delUsers = '/alarm-manager/notify/group/removeUsers', // 批量移除
  // getUsers = '/alarm-manager/auth/listUsers',
  getUsers = '/alarm-manager/auth/listExternalUsers',
  getGroupByName = '/alarm-manager/notify/group/listByName'
}
export default ApiUrl