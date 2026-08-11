import Request from '@/request/axios/Request'
import ApiUrl from './ApiUrl'

class pushPolice extends Request {
  getConditionList(data) {
    return this.request({
      url: ApiUrl.getConditionList,
      method: 'POST',
      data
    })
  }
  getMetricsList() {
    return this.request({
      url: ApiUrl.getMetricsList,
      method: 'POST',
    })
  }
  getDataGroupTree(data) {
    return this.request({
      url: ApiUrl.getDataGroupTree,
      method: 'POST',
      data
    })
  }
  createPushPolice(data) {
    return this.request({
      url: ApiUrl.createPushPolice,
      method: 'POST',
      data
    })
  }
  getPushPolice(data) {
    return this.request({
      url: ApiUrl.getPushPolice,
      method: 'POST',
      data
    })
  }
  delPushPolice(id) {
    return this.request({
      url: `${ApiUrl.delPushPolice}?id=${id}`,
      method: 'POST',
    })
  }
  disablePushPolice(data) {
    return this.request({
      url: ApiUrl.disablePushPolice,
      method: 'POST',
      data
    })
  }
  enablePushPolice(data) {
    return this.request({
      url: ApiUrl.enablePushPolice,
      method: 'POST',
      data
    })
  }
  pushPoliceDetail(id) {
    return this.request({
      url: `${ApiUrl.pushPoliceDetail}?id=${id}`,
      method: 'POST',
    })
  }
  editPushPolice(data) {
    return this.request({
      url: ApiUrl.editPushPolice,
      method: 'POST',
      data
    })
  }
}

export default new pushPolice()
