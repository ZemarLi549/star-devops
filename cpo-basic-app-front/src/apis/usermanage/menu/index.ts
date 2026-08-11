import Request from '@/config/axios/Request'
import menuUrl from './menuUrl'

// const domain = '/aiops/resource'
const domain = '/auth'

class MenuApi extends Request {
  list(module) {
    return this.request({
      url: domain + menuUrl.list + '?module=' + module,
      method: 'GET',
      globalLoading: false,
    })
  }
  add(param) {
    return this.request({
      url: domain + menuUrl.add,
      method: 'POST',
      globalLoading: false,
      data: param
    })
  }
  delete(id) {
    return this.request({
      url: domain + menuUrl.delete,
      method: 'POST',
      globalLoading: false,
      data: {
        menuId: id
      }
    })
  }
  update(param) {
    return this.request({
      url: domain + menuUrl.update,
      method: 'POST',
      globalLoading: false,
      data: param
    })
  }
  getDetail(id) {
    return this.request({
      url: domain + menuUrl.detail + '?menuId=' + id,
      method: 'GET',
      globalLoading: false,
    })
  }
}

export default new MenuApi({
  baseURL: '/'
})