import Request from '@/request/axios/Request'
import ApiUrl from './ApiUrl'

class noticeGroup extends Request {
  getGroupList() {
    return this.request({
      url: ApiUrl.getGroupList,
      method: 'POST',
    })
  }
  createGroup(data) {
    return this.request({
      url: ApiUrl.createGroup,
      method: 'POST',
      data
    })
  }
  editGroup(data) {
    return this.request({
      url: ApiUrl.editGroup,
      method: 'POST',
      data
    })
  }
  delGroup(data) {
    return this.request({
      url: ApiUrl.delGroup,
      method: 'POST',
      data
    })
  }
  getGroupUserList(data) {
    return this.request({
      url: ApiUrl.getGroupUserList,
      method: 'POST',
      data
    })
  }
  addUser(data) {
    return this.request({
      url: ApiUrl.addUser,
      method: 'POST',
      data
    })
  }
  delUser(data) {
    return this.request({
      url: ApiUrl.delUser,
      method: 'POST',
      data
    })
  }
  delUsers(data) {
    return this.request({
      url: ApiUrl.delUsers,
      method: 'POST',
      data
    })
  }
  getUsers(groupId) {
    return this.request({
      url: ApiUrl.getUsers,
      method: 'POST',
      data: {
        groupId: groupId
      }
    })
  }

  getGroupByName(name) {
    return this.request({
      url: ApiUrl.getGroupByName,
      method: 'POST',
      data: {
        groupName: name
      }
    })
  }
}

export default new noticeGroup()
