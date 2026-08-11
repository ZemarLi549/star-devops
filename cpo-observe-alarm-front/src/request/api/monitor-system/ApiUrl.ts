enum ApiUrl {
  getSysList = '/alarm-manager/monitor/system/list',  // 获取系统列表
  // createToken = '/auth/permission/insertElement',  // 快捷创建业务单元
  createToken = '/alarm-manager/auth/createToken',  // 快捷创建业务单元
  
  getGroupList = '/auth/sys/dataGroup/dataGroupTree',//数据单元
  // getGroupList = '/auth/permission/dataGroupTree',  // 获取业务组单元树结构
  // getGroupList = '/alarm-manager/auth/dataGroupTree'
  esightSubscribe = '/alarm-manager/monitor/system/esight/subscribe', // esight订阅
  esightCancelSubscribe = '/alarm-manager/monitor/system/esight/cancel', // esight取消订阅

  fdSubscribe = '/alarm-manager/monitor/system/fd/subscribe', // fd订阅
  getFdSubscribeList = '/alarm-manager/monitor/system/fd/list', // fd查询列表
  updateFdSubscribe = '/alarm-manager/monitor/system/fd/update/', // fd更新
  deleteFdSubscribe = '/alarm-manager/monitor/system/fd/delete/', // fd删除
}
export default ApiUrl