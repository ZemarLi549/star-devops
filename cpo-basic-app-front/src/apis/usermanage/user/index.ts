import Request from '@/config/axios/Request'
import userUrl from './userUrl'

const domain = '/auth'
// const domain = ''

class UserApi extends Request {
  getUserList(data) {
    return this.request({
      url: domain + userUrl.getUserList,
      method: 'POST',
      globalLoading: false,
      data,
    })
  }
  getUserOption(params) {
    return this.request({
      url: domain + userUrl.getUserOption,
      method: 'GET',
      globalLoading: false,
      params,
    })
  }
  getAddUserDetail(params) {
    return this.request({
      url: domain + userUrl.getAddUserDetail,
      method: 'GET',
      globalLoading: false,
      params,
    })
  }
  getRoleList(params) {
    return this.request({
      url: domain + userUrl.getRoleList,
      method: 'GET',
      globalLoading: false,
      params,
    })
  }
  getRoleDetail(params) {
    return this.request({
      url: domain + userUrl.getRoleDetail,
      method: 'GET',
      globalLoading: false,
      params
    })
  }
  getAuthGroupTree(params) {
    return this.request({
      url: domain + userUrl.getAuthGroupTree,
      method: 'GET',
      globalLoading: false,
      params
    })
  }
  saveUser(data) {
    return this.request({
      url: domain + userUrl.saveUser,
      method: 'POST',
      globalLoading: false,
      data
    })
  }
  removeUser(data) {
    return this.request({
      url: domain + userUrl.removeUser,
      method: 'POST',
      globalLoading: false,
      data
    })
  }

  addUser(data) {
    return this.request({
      url: domain + userUrl.addUser,
      method: 'POST',
      globalLoading: false,
      data
    })
  }
}

export default new UserApi({
  baseURL: '/'
})