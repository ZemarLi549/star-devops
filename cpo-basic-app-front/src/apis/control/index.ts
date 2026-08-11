import Request from '@/config/axios/Request'
import controlUrl from './controlUrl'
// const domain = 'http://10.4.149.33:3000/mock/338'
import { ElMessage } from "element-plus";
import { RequestType } from '@/globals/enums'
import { calculateQuantity, specialHandle } from '@/config/axios/defaultRequestConfig';
import md5 from 'crypto-js/md5'


class ControlApi extends Request {
  
  getShortcutsMenu(workspace_id) {
    return this.request({
      url: controlUrl.getShortcutsMenu,
      method: 'GET',
      globalLoading: false,
    })
  }
  delectShortcutsMenu(menuId, workspace_id) {
    return this.request({
      url: controlUrl.delectShortcutsMenu + '/' + menuId,
      method: 'POST',
      globalLoading: false,
    })
  }
  addShortcutsMenu(data, workspace_id) {
    return this.request({
      url: controlUrl.addShortcutsMenu,
      method: 'POST',
      data,
      globalLoading: false,
    })
  }
  getFirstLevelMenu(workspace_id) {
    return this.request({
      url: controlUrl.getFirstLevelMenu,
      method: 'GET',
      globalLoading: false,
    })
  }
  getUserInfo(workspace_id) {
    return this.request({
      url: controlUrl.getUserInfo,
      method: 'GET',
      globalLoading: false,
    })
  }
  // getHistoryAlert(params) {
  //   return this.request({
  //     url: controlUrl.alertHis,
  //     method: 'GET',
  //     params,
  //     globalLoading: false,
  //   })
  // }

  getUerWorkspace(params?) {
    return this.request({
      url: controlUrl.getUerWorkspace,
      method: 'GET',
      params,
      globalLoading: false,
    })
  }

  update(params) {
    return this.request({
      url: controlUrl.update,
      method: 'POST',
      data: params
    })
  }

  updatepasswd(params) {
    return this.request({
      url: controlUrl.updatepasswd,
      method: 'POST',
      data: {
        userId: params.userId,
        passwd: md5(params.passwd).toString(),
        oldPwd: md5(params.oldPwd).toString(),
      }
    })
  }
  getApplicationErrorLogCount(data) {
    return this.request({
      url: controlUrl.getApplicationErrorLogCount,
      method: 'POST',
      data
    })
  }
  getErrorLogDistribution(params) {
    return this.request({
      url: controlUrl.getErrorLogDistribution,
      method: 'GET',
      params
    })
  }
  getLogsList() {
    return this.request({
      url: controlUrl.getLogsList,
      method: 'GET',
    })
  }
  // 告警相关
  getAlarmLevelTrend(data) {
    return this.request({
      url: controlUrl.getAlarmLevelTrend,
      method: 'POST',
      data
    })
  }
  getAlarmLevelAnalysis(data) {
    return this.request({
      url: controlUrl.getAlarmLevelAnalysis,
      method: 'POST',
      data
    })
  }
  getAlarmLevelTask(data) {
    return this.request({
      url: controlUrl.getAlarmLevelTask,
      method: 'POST',
      data
    })
  }
  getAlarmOverview(data) {
    return this.request({
      url: controlUrl.getAlarmOverview,
      method: 'POST',
      data
    })
  }

  getAlarmOverviewData(params, isSelf) {
    return this.request({
      url: controlUrl.getAlarmOverviewData,
      method: 'POST',
      data: {...params, isSelf},
    })
  }
  getDeployConfig() {
    return this.request({
      url: controlUrl.getDeployConfig,
      method: 'GET',
    })
  }

  getProductivityOverview(params) {
    return this.request({
      url: controlUrl.getProductivityOverview,
      method: 'GET',
      params,
      globalLoading: false,
    })
  }

  getProductivityLatestReport(params) {
    return this.request({
      url: controlUrl.getProductivityLatestReport,
      method: 'GET',
      params,
      globalLoading: false,
    })
  }

  getProductivityLatestSummary(params) {
    return this.request({
      url: controlUrl.getProductivityLatestSummary,
      method: 'GET',
      params,
      globalLoading: false,
    })
  }

  generateProductivitySummary(data) {
    return this.request({
      url: controlUrl.generateProductivitySummary,
      method: 'POST',
      data,
      globalLoading: false,
    })
  }

  getProductivityNotifications(params) {
    return this.request({
      url: controlUrl.getProductivityNotifications,
      method: 'GET',
      params,
      globalLoading: false,
    })
  }

  getAstrBotStatus() {
    return this.request({
      url: controlUrl.getAstrBotStatus,
      method: 'GET',
      globalLoading: false,
    })
  }
}

export default new ControlApi()
