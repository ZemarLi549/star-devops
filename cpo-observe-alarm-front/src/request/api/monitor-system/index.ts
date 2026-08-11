import Request from '@/request/axios/Request'
import ApiUrl from './ApiUrl'

class monitorSystem extends Request {
  getSysList() {
    return this.request({
      url: ApiUrl.getSysList,
      method: 'POST',
    })
  }

  createToken(data) {
    return this.request({
      url: ApiUrl.createToken,
      method: 'POST',
      data
    })
  }

  getGroupList(data) {
    return this.request({
      url: ApiUrl.getGroupList,
      method: 'POST',
      data
    })
  }
  esightSubscribe(data) {
    return this.request({
      url: ApiUrl.esightSubscribe,
      method: 'POST',
      data
    })
  }
  esightCancelSubscribe() {
    return this.request({
      url: ApiUrl.esightCancelSubscribe,
      method: 'GET',
    })
  }
  fdSubscribe(data, id?) {
    const url = id ? ApiUrl.updateFdSubscribe + id : ApiUrl.fdSubscribe
    return this.request({
      url: url,
      method: 'POST',
      data
    })
  }
  getFdSubscribeList() {
    return this.request({
      url: ApiUrl.getFdSubscribeList,
      method: 'GET',
    })
  }
  deleteFdSubscribe(id) {
    return this.request({
      url: ApiUrl.deleteFdSubscribe + id,
      method: 'GET',
    })
  }

}

export default new monitorSystem()
