export type ApplicationDto = {
  workSpaceId?: number | string;
  dataGroupId?: string;
  applicationName: string;
  remark?: string;
  applicationId: string;
  applicationCode?: string;
}

export type ApplicationDeleteDto = {
  applicationId: string[];
}

export type DataGroupDto = {
  workSpaceId?: string;
  dataGroupName?: string;
  iselement?: boolean;
  parentId?: string;
  dataGroupToken: string;
  remark: string;
}

export type PageDto = {
  name: string;
  pageNo: number;
  pageSize: number;
  workSpaceId: number;
  dataGroupId: number | string;
}

type ListRes<T> = {
  records: T[];
  total: number;
  size: number;
  current: number;
  pages: number;
  orders: string[];
}

export type ApplicationRow = {
  applicationId: string;
  workSpaceId: string;
  dataGroupId: string;
  applicationName: string;
  remark: string;
  isValid: boolean;
  createTime: string;
  applicationCode: string;
  createUser: string;
  modifyUser: string;
  modifyTime: string;
  createUserName: string;
}

export type PageRes = ListRes<ApplicationRow>

export type DataGroupDeleteDto = {
  dataGroupId: number | string;
  workSpaceId: number;
}

export type BusinessTreeDto = {
  workSpaceId: number;
}

export type BusinessTree = {
  dataGroupId: number;
  dataGroupName: string;
  workSpaceId: number;
  parentId: number;
  iselement: boolean;
  sortNum: number;
  child: BusinessTreeRes;
  dataGroupToken?: string;
}

export type BusinessTreeRes = BusinessTree[]