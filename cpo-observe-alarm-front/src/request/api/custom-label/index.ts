import Request from '@/request/axios/Request'
import ApiUrl from './ApiUrl'
class customLabelApi extends Request {
  // 自定义标签
  getCustomLabel(params) {
    return this.request({
      url: ApiUrl.getCustomLabel,
      method: 'POST',
      data: params,
      globalLoading: false
    })
  }
  getCustomLabelDetail(labelId) {
    return this.request({
      url: ApiUrl.getCustomLabelDetail,
      method: 'POST',
      data: { labelId },
      globalLoading: false
    })
  }
  addCustomLabel(data) {
    return this.request({
      url: ApiUrl.addCustomLabel,
      method: 'POST',
      data,
      globalLoading: false
    })
  }
  deleteCustomLabel(labelId) {
    return this.request({
      url: ApiUrl.deleteCustomLabel,
      method: 'POST',
      data: { labelId },
      globalLoading: false
    })
  }
  editCustomLabel(data) {
    return this.request({
      url: ApiUrl.editCustomLabel,
      method: 'POST',
      data,
      globalLoading: false
    })
  }
  enableCustomLabel(labelId) {
    return this.request({
      url: ApiUrl.enableCustomLabel,
      method: 'POST',
      data: { labelId },
      globalLoading: false
    })
  }
  disableCustomLabel(labelId) {
    return this.request({
      url: ApiUrl.disableCustomLabel,
      method: 'POST',
      data: { labelId },
      globalLoading: false
    })
  }
  testCustomLabel(data) {
    return this.request({
      url: ApiUrl.testCustomLabel,
      method: 'POST',
      data,
      globalLoading: false
    })
  }
  getLabelTemplate(data) {
    return this.request({
      url: ApiUrl.getLabelTemplate,
      method: 'POST',
      data,
      globalLoading: false
    })
  }
  createLabelTemplate(data) {
    return this.request({
      url: ApiUrl.createLabelTemplate,
      method: 'POST',
      data,
      globalLoading: false
    })
  }
  editLabelTemplate(data) {
    return this.request({
      url: ApiUrl.editLabelTemplate,
      method: 'POST',
      data,
      globalLoading: false
    })
  }
}

export default new customLabelApi()
