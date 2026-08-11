import Request from '@/config/axios/Request'
import spaceUrl from './spaceUrl'

const domain = '/auth'

class SpaceApi extends Request {
  list() {
    return this.request({
      url: domain + spaceUrl.list,
      method: 'POST',
      globalLoading: false,
      data: {}
    })
  }
  add(workSpaceName, remark) {
    return this.request({
      url: domain + spaceUrl.add,
      method: 'POST',
      globalLoading: false,
      data: {
        workSpaceName: workSpaceName,
        remark: remark
      }
    })
  }
  update(workSpaceId, workSpaceName, remark) {
    return this.request({
      url: domain + spaceUrl.update,
      method: 'POST',
      globalLoading: false,
      data: {
        workSpaceId: workSpaceId,
        workSpaceName: workSpaceName,
        remark: remark
      }
    })
  }

  delete(workSpaceId) {
    return this.request({
      url: domain + spaceUrl.delete,
      method: 'POST',
      globalLoading: false,
      data: {
        workSpaceId: workSpaceId
      }
    })
  }

  listDrown() {
    return this.request({
      url: domain + spaceUrl.listDrown,
      method: 'GET',
      globalLoading: false,
    })
  }
}

export default new SpaceApi({
  baseURL: '/'
})