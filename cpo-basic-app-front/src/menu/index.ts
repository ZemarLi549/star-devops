import { MODULE_ID } from "@/globals/constant";

export const menuTitleMap = [
  {
    title: '服务中心',
    key: 'business-observe',
    moduleId: MODULE_ID.BUSINESS_OBSERVATION
  },
  {
    title: '微服务接入',
    key: 'environment-config',
    moduleId: MODULE_ID.ENVIRONMENT_CONFIGURATION
  },
  {
    title: 'AI 自动化',
    key: 'alarm-center',
    moduleId: MODULE_ID.ALARM_MANAGEMENT
  },
  {
    title: '用户管理',
    key: 'usermanage',
    moduleId: MODULE_ID.RESOURCE_CONTROL
  },
  {
    title: '工作台',
    key: 'control',
    moduleId: MODULE_ID.WORK_BENCH
  },
  {
    title: '独立部署',
    key: 'single-deploy',
    moduleId: MODULE_ID.SINGLE_MENU
  }
]




export const usermenuList = [
  {
    name: '空间',
    parent: '/usermanage',
    path: '/space'
  },
  {
    name: '用户池',
    parent: '/usermanage',
    path: '/pool'
  },
  {
    name: '菜单',
    parent: '/usermanage',
    path: '/menu'
  }
]
