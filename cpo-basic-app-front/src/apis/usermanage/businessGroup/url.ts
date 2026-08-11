enum businessGroupUrls {
  // 删除业务组
  dataGroupDelete = '/sys/dataGroup/delete',
  // 新增应用
  applicationInsert = '/sys/application/insert',
  // 更新应用
  applicationUpdate = '/sys/application/update',
  // 删除应用
  applicationDelete = '/sys/application/delete',
  // 更新业务组/数据单元
  dataGroupUpdate = '/sys/dataGroup/update',
  // 新增业务组/数据单元
  dataGroupInsert = '/sys/dataGroup/insert',
  // 应用分页列表
  page = '/sys/application/page',
  // 业务树(不带数据单元)
  bizTree = '/sys/dataGroup/bizTree',
  // 业务熟(带数据单元)
  dataGroupTree = '/sys/dataGroup/dataGroupTree',
  // 拖拽
  moveDataGroup = '/sys/dataGroup/moveDataGroup'
}

export default businessGroupUrls;