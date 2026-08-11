import Request from '@/request/axios/Request'
import ApiUrl from './ApiUrl'

class noticeChannel extends Request {
  createChannel(data) {
    return this.request({
      url: ApiUrl.createChannel,
      method: 'POST',
      data
    })
  }
  editChannel(data) {
    return this.request({
      url: ApiUrl.editChannel,
      method: 'POST',
      data
    })
  }
  getTemplateList(data) {
    return this.request({
      url: ApiUrl.getTemplateList,
      method: 'POST',
      data
    })
  }
  editTemplate(data) {
    return this.request({
      url: ApiUrl.editTemplate,
      method: 'POST',
      data
    })
  }
  getNoticeChannel() {
    return this.request({
      url: ApiUrl.getNoticeChannel,
      method: 'POST',
    })
  }
}

export default new noticeChannel()
