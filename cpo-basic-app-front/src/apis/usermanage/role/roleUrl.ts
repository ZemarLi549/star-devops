enum roleUrl {
  addRole = '/sys/role/insert', //新增角色
  editRole = '/sys/role/update', //编辑角色
  delRole = '/sys/role/delete', //删除角色
  getRoleTemplate = '/sys/role/listTemplate', //查询角色模板列表
  getRoleDetail = '/sys/role/detail', //查询角色详情
  getRoleList = '/sys/role/page', //角色列表
  getAuthMenuTree = '/sys/role/getAuthMenuTree', //查询菜单树，标记选中状态，可传角色id
}

export default roleUrl