import Request from '@/config/axios/Request'
import poolUrl from './poolUrl'
import md5 from 'crypto-js/md5'

const domain = '/auth'

class PoolApi extends Request {
  list(param) {
    return this.request({
      url: domain + poolUrl.list,
      method: 'POST',
      globalLoading: false,
      data: param
    })
  }
  add(param) {
    return this.request({
      url: domain + poolUrl.add,
      method: 'POST',
      globalLoading: false,
      data: param
    })
  }
  update(param) {
    return this.request({
      url: domain + poolUrl.update,
      method: 'POST',
      globalLoading: false,
      data: param
    })
  }

  delete(userId) {
    return this.request({
      url: domain + poolUrl.delete,
      method: 'POST',
      globalLoading: false,
      data: {
        userId: userId
      }
    })
  }

  reset(param) {
    return this.request({
      url: domain + poolUrl.reset,
      method: 'POST',
      globalLoading: false,
      data: {
        userId: param.userId,
        passwd: md5(param.passwd).toString(),
        oldPwd: md5(param.oldPwd).toString(),
      }
    })
  }

  change(param) {
    return this.request({
      url: domain + poolUrl.change,
      method: 'POST',
      globalLoading: false,
      data: param
    })
  }

  check(param) {
    return this.request({
      url: domain + poolUrl.check,
      method: 'POST',
      globalLoading: false,
      data: param
    })
  }

  getDefault() {
    return this.request({
      url: domain + poolUrl.getDefault,
      method: 'GET',
      globalLoading: false
    })
  }

  resetpasswd(param) {
    return this.request({
      url: domain + poolUrl.resetpasswd,
      method: 'POST',
      globalLoading: false,
      data: {
        userId: param.userId
      }
    })
  }
}

export default new PoolApi({
  baseURL: '/'
})