import Request from '@/request/axios/Request'
import ApiUrl from './ApiUrl'

class assignPolice extends Request {
  getNotifyList() {
    return this.request({
      url: ApiUrl.getNotifyList,
      method: 'POST',
    })
  }
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
  getNotifyPolicy() {
    return this.request({
      url: ApiUrl.getNotifyPolicy,
      method: 'POST',
    })
  }
  createAssignPolice(data) {
    return this.request({
      url: ApiUrl.createAssignPolice,
      method: 'POST',
      data
    })
  }
  getAssignPoliceList(data) {
    return this.request({
      url: ApiUrl.getAssignPoliceList,
      method: 'POST',
      data
    })
  }
  delAssignPoliceList(data) {
    return this.request({
      url: ApiUrl.delAssignPoliceList,
      method: 'POST',
      data
    })
  }
  disableAssignPoliceList(data) {
    return this.request({
      url: ApiUrl.disableAssignPoliceList,
      method: 'POST',
      data
    })
  }
  enableAssignPoliceList(data) {
    return this.request({
      url: ApiUrl.enableAssignPoliceList,
      method: 'POST',
      data
    })
  }
  assignPoliceListDetail(data) {
    return this.request({
      url: ApiUrl.assignPoliceListDetail,
      method: 'POST',
      data
    })
  }
  editPoliceListDetail(data) {
    return this.request({
      url: ApiUrl.editPoliceListDetail,
      method: 'POST',
      data
    })
  }
}

export default new assignPolice()
