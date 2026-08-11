import ParentRequest from "@/config/axios/Request";
import businessGroupUrls from "./url";
import { ApplicationDeleteDto, ApplicationDto, BusinessTreeDto, BusinessTreeRes, DataGroupDeleteDto, DataGroupDto, PageDto, PageRes } from "./types";

class BusinessGroupApi extends ParentRequest {
  dataGroupDelete(data: DataGroupDeleteDto) {
    return this.request({
      url: businessGroupUrls.dataGroupDelete,
      method: 'post',
      data
    });
  } 

  dataGroupUpdate(data: DataGroupDto) {
    return this.request({
      url: businessGroupUrls.dataGroupUpdate,
      method: 'post',
      data,
    });
  }

  dataGroupInsert(data: DataGroupDto) {
    return this.request({
      url: businessGroupUrls.dataGroupInsert,
      method: 'post',
      data
    })
  }

  page(data: PageDto) {
    return this.request<PageRes>({
      url: businessGroupUrls.page,
      method: 'post',
      data
    })
  }

  applicationInsert(data: ApplicationDto) {
    return this.request({
      url: businessGroupUrls.applicationInsert,
      method: 'post',
      data
    });
  }

  applicationUpdate(data: ApplicationDto) {
    return this.request({
      url: businessGroupUrls.applicationUpdate,
      method: 'post',
      data
    })
  }

  applicationDelete(data: ApplicationDeleteDto) {
    return this.request({
      url: businessGroupUrls.applicationDelete,
      method: 'post',
      data
    })
  }

  bizTree(data: BusinessTreeDto) {
    return this.request<BusinessTreeRes>({
      url: businessGroupUrls.bizTree,
      method: 'post',
      data
    })
  }

  dataGroupTree(data: BusinessTreeDto) {
    return this.request<BusinessTreeRes>({
      url: businessGroupUrls.dataGroupTree,
      method: 'post',
      data
    })
  }
  moveDataGroup(data: BusinessTreeDto) {
    return this.request<BusinessTreeRes>({
      url: businessGroupUrls.moveDataGroup,
      method: 'post',
      data
    })
  }
}

const businessGroupApi =  new BusinessGroupApi({
  baseURL: '/auth'
})

export default businessGroupApi;