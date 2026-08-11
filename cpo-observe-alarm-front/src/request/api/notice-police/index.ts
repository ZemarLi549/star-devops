import Request from '@/request/axios/Request'
import ApiUrl from './ApiUrl'

class noticePolice extends Request {
  getNoticePoliceList(data) {
    return this.request({
      url: ApiUrl.getNoticePoliceList,
      method: 'POST',
      data
    })
  }
  createNoticePolice(data) {
    return this.request({
      url: ApiUrl.createNoticePolice,
      method: 'POST',
      data
    })
  }
  editNoticePolice(data) {
    return this.request({
      url: ApiUrl.editNoticePolice,
      method: 'POST',
      data
    })
  }
  delNotice(data) {
    return this.request({
      url: ApiUrl.delNotice,
      method: 'POST',
      data
    })
  }
  disableNoticePolice(data) {
    return this.request({
      url: ApiUrl.disableNoticePolice,
      method: 'POST',
      data
    })
  }
  enableNoticePolice(data) {
    return this.request({
      url: ApiUrl.enableNoticePolice,
      method: 'POST',
      data
    })
  }
  noticePoliceListDetail(data) {
    return this.request({
      url: ApiUrl.noticePoliceListDetail,
      method: 'POST',
      data
    })
  }

}

export default new noticePolice()
