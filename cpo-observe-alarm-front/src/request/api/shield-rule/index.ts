import Request from '@/request/axios/Request'
import ApiUrl from './ApiUrl'

class shieldRule extends Request {
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
  createShieldRule(data) {
    return this.request({
      url: ApiUrl.createShieldRule,
      method: 'POST',
      data
    })
  }
  getShieldRuleList(data) {
    return this.request({
      url: ApiUrl.getShieldRuleList,
      method: 'POST',
      data
    })
  }
  delShieldRuleList(data) {
    return this.request({
      url: ApiUrl.delShieldRuleList,
      method: 'POST',
      data
    })
  }
  disableShieldRuleList(data) {
    return this.request({
      url: ApiUrl.disableShieldRuleList,
      method: 'POST',
      data
    })
  }
  enableShieldRuleList(data) {
    return this.request({
      url: ApiUrl.enableShieldRuleList,
      method: 'POST',
      data
    })
  }
  shieldRuleListDetail(data) {
    return this.request({
      url: ApiUrl.shieldRuleListDetail,
      method: 'POST',
      data
    })
  }
  editShieldRule(data) {
    return this.request({
      url: ApiUrl.editShieldRule,
      method: 'POST',
      data
    })
  }
}

export default new shieldRule()
