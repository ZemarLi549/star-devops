import Request from '@/config/axios/Request'
import roleUrl from './roleUrl'

const domain = '/auth'
// const domain = ''

class RoleApi extends Request {
  getRoleList(data) {
    return this.request({
      url: domain + roleUrl.getRoleList,
      method: 'POST',
      globalLoading: false,
      data,
    })
  }
  getRoleTemplate() {
    return this.request({
      url: domain + roleUrl.getRoleTemplate,
      method: 'GET',
      globalLoading: false,
    })
  }
  getRoleDetail(params) {
    return this.request({
      url: domain + roleUrl.getRoleDetail,
      method: 'GET',
      globalLoading: false,
      params
    })
  }
  addRole(data) {
    return this.request({
      url: domain + roleUrl.addRole,
      method: 'POST',
      globalLoading: false,
      data
    })
  }
  delRole(data) {
    return this.request({
      url: domain + roleUrl.delRole,
      method: 'POST',
      globalLoading: false,
      data
    })
  }
  getAuthMenuTree(params) {
    return this.request({
      url: domain + roleUrl.getAuthMenuTree,
      method: 'GET',
      globalLoading: false,
      params
    })
  }
  editRole(data) {
    return this.request({
      url: domain + roleUrl.editRole,
      method: 'POST',
      globalLoading: false,
      data
    })
  }
}

export default new RoleApi({
  baseURL: '/'
})