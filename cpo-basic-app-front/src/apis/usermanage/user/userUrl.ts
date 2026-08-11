enum userUrl {
  getUserList = '/sys/workSpace/user/page', //用户列表
  getUserOption = '/sys/workSpace/user/listUserDrown', //下拉用户
  getAddUserDetail = '/sys/workSpace/user/getDetail', // 新增成员详情
  getRoleList = '/sys/role/listSpaceTotal', // 角色列表
  getRoleDetail = '/sys/role/detail', //查询角色详情
  getAuthGroupTree = '/sys/workSpace/user/getAuthGroupTree', //查询业务组数
  saveUser = '/sys/workSpace/user/save', //查询业务组数
  removeUser = '/sys/workSpace/user/batchRemove', //移除
  addUser = '/sys/workSpace/user/insertUser',

}

export default userUrl