import Request from '@/request/axios/Request'
import ApiUrl from './ApiUrl'

class alarmManageApi extends Request {

  // 我的告警/所有告警
  getAlarmPage(params, isSelf: boolean, state?: number, onlyNotClosed?: boolean) {
    return this.request({
      url: ApiUrl.getAlarmPage,
      method: 'POST',
      data: {...params, isSelf, state, onlyNotClosed},
      globalLoading: false
    })
  }
  getAlarmDetail(alarmId) {
    return this.request({
      url: ApiUrl.getAlarmDetail,
      method: 'POST',
      data: {alarmId},
      globalLoading: false
    })
  }
  getAlarmEventPage(params, aggId) {
    return this.request({
      url: ApiUrl.getAlarmEventPage,
      method: 'POST',
      data: { aggId, ...params },
      globalLoading: false
    })
  }
  getAlarmCount(isSelf: boolean) {
    return this.request({
      url: ApiUrl.getAlarmCount,
      method: 'POST',
      data: {isSelf},
      globalLoading: false
    })
  }
  ackAlarms(params) {
    return this.request({
      url: ApiUrl.ackAlarms,
      method: 'POST',
      data: {...params},
      globalLoading: false
    })
  }
  closeAlarms(params) {
    return this.request({
      url: ApiUrl.closeAlarms,
      method: 'POST',
      data: {...params},
      globalLoading: false
    })
  }
  alarmCanAck(alarmId) {
    return this.request({
      url: ApiUrl.alarmCanAck,
      method: 'POST',
      data: {alarmId},
      globalLoading: false
    })
  }
  alarmCanClose(alarmId) {
    return this.request({
      url: ApiUrl.alarmCanClose,
      method: 'POST',
      data: {alarmId},
      globalLoading: false
    })
  }
  getUniquePage(params, aggId, sortType) {
    return this.request({
      url: ApiUrl.getUniquePage,
      method: 'POST',
      data: {...params, aggId, sortType},
      globalLoading: false
    })
  }
  // 未分派告警
  getUnassignPage(params) {
    return this.request({
      url: ApiUrl.getUnassignPage,
      method: 'POST',
      data: params,
      globalLoading: false
    })
  }
  getUnassignDetail(alarmId) {
    return this.request({
      url: ApiUrl.getUnassignDetail,
      method: 'POST',
      data: {alarmId},
      globalLoading: false
    })
  }
  getUniqueEventPage(params, uniqueId) {
    return this.request({
      url: ApiUrl.getUniqueEventPage,
      method: 'POST',
      data: {uniqueId, ...params},
      globalLoading: false
    })
  }
  // 屏蔽告警
  getBlockPage(params) {
    return this.request({
      url: ApiUrl.getBlockPage,
      method: 'POST',
      data: params,
      globalLoading: false
    })
  }
  getBlockDetail(alarmId) {
    return this.request({
      url: ApiUrl.getBlockDetail,
      method: 'POST',
      data: {alarmId},
      globalLoading: false
    })
  }

  // 所有通知
  getNoticePage(params) {
    return this.request({
      url: ApiUrl.getNoticePage,
      method: 'POST',
      data: params,
      globalLoading: false
    })
  }
  getNoticeDetail(noticeId) {
    return this.request({
      url: ApiUrl.getNoticeDetail,
      method: 'POST',
      data: {noticeId},
      globalLoading: false
    })
  }
  getUniqueLabels(id) {
    return this.request({
      url: `${ApiUrl.getUniqueLabels}/${id}`,
      method: 'GET',
      globalLoading: false
    })
  }
  getUniqueContent(id) {
    return this.request({
      url: `${ApiUrl.getUniqueContent}/${id}`,
      method: 'GET',
      globalLoading: false
    })
  }
  getAssignPolicy() {
    return this.request({
      url: ApiUrl.getAssignPolicy,
      method: 'GET',
      globalLoading: false
    })
  }
  getNotifyPolicy() {
    return this.request({
      url: ApiUrl.getNotifyPolicy,
      method: 'POST',
    })
  }
}

export default new alarmManageApi()
