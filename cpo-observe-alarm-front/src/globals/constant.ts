/*
 * @Author: zhewu4 
 * @Date: 2023-11-28 16:27:27 
 * @Last Modified by: zhewu4
 * @Last Modified time: 2023-11-28 19:56:09
 */

interface IRoutingParams {
  path: string
  params: Array<string>
  props?: boolean
}

export const ACCESS_TOKEN = 'Access-Token'

export const AUTHORIZATION = 'token'

export const WORKSPACEID = 'Workspace-Id'

export const OPTION_MAX_LENGTH = 50

export const DES_MAX_LENGTH = 200

export const MENU_TREE_ROOT_ID = 0

// 空字符串
export const EMPTY_STRING: string = ''

// 空值默认显示
export const DEFAULT_EMPTY_STR: string = '-'

export const TRUE_STR: string = 'true'

export const FALSE_STR: string = 'false'

// 这里不能加数据类型,会导致遍历Promise数组时,检测不到对象存在value属性,从而编译器报错
export const FULFILLED = 'fulfilled'

export const TIME_FORMAT = 'YYYY-MM-DD HH:mm:ss'

// 首次访问页面默认重定向路由
export const REDIRECT_ROUTE = {
  originPath: '/control',
  path: '/control',
  title: '可观测平台',
  query: {}
}

export const MonitorTypes = [
  { label: "星迹可观测", color: [31,105,255] },
  { label: "Prometheus", color: [0, 199, 255] },
  { label: "华为ESight", color: [255, 114, 207] },
  { label: "华为FusionDirector", color: [255, 217, 64] },
];