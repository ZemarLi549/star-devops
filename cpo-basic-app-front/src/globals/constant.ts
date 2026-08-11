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

export const AUTHORIZATION = 'token'

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
  title: '鑫图平台',
  query: {}
}

export const HOUR = 60 * 60 * 1000;

export enum MODULE_ID {
  WORK_BENCH = 'WORK_BENCH', // 工作台
  RESOURCE_CONTROL = 'RESOURCE_CONTROL', // 权限控制
  BUSINESS_OBSERVATION = 'BUSINESS_OBSERVATION', // 业务观测
  DATA_EXPLORATION = 'DATA_EXPLORATION', // 数据探索
  ACCESS_CENTER = 'ACCESS_CENTER', // 接入中心
  ALARM_MANAGEMENT = 'ALARM_MANAGEMENT', // 告警管理
  ENVIRONMENT_CONFIGURATION = 'ENVIRONMENT_CONFIGURATION', // 环境配置
  SINGLE_MENU = 'SINGLE_MENU', // 独立部署模块
}

export enum LOGIN_TYPES {
  NORMAL = 'normal',
  SSO = 'sso',
  UAP = 'uap'
}

export enum PROJECT_MAP {
  OBSERVE_TRACE = 'observe-trace',
  OBSERVE_METRIC = 'observe-metric',
  OBSERVE_CONFIG = 'observe-config',
  OBSERVE_LOG = 'observe-log',
  OBSERVE_ALARM = 'observe-alarm',
}

interface MicroAppDataAll {
  name: string;
  activeRule: string[];
  entry?: string;
}

export const microAppsDataAll: MicroAppDataAll[] = [
  {
    name: PROJECT_MAP.OBSERVE_TRACE,
    activeRule: ["/business-observe/observe-trace"],
  }, {
    name: PROJECT_MAP.OBSERVE_METRIC,
    activeRule: ["/business-observe/observe-metric"],
  }, {
    name: PROJECT_MAP.OBSERVE_CONFIG,
    activeRule: ["/environment-config/observe-config"],
  }, {
    name: PROJECT_MAP.OBSERVE_LOG,
    activeRule: ["/business-observe/observe-log"],
  }, {
    name: PROJECT_MAP.OBSERVE_ALARM,
    activeRule: ["/alarm-center/observe-alarm"],
  }
]
