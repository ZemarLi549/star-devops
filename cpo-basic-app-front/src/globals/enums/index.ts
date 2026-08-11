/*
 * @Author: zhewu4 
 * @Date: 2023-11-28 16:28:48 
 * @Last Modified by: zhewu4
 * @Last Modified time: 2023-11-28 16:57:29
 */

// 增删改查动作
export enum OperationAction {
  ADD,
  EDIT,
  LOCK,
  CANCEl,
  DELETE,
  COPY
}

// 状态
export enum Status {
  CLOSE,
  OPEN
}

// 请求列表动作
export enum RequestListAction {
  SEARCH,
  PAGING,
  RESET
}

export enum MenuType {
  MENU, // 菜单, 直接用RouterView作为出口(作为菜单树展示)
  MENU_ITEM, // 菜单子路由(作为菜单树展示)
  FULL_SCREEN, // 点击父级路由,跳转子级路由,需要全屏显示,隐藏父级路由(不会展示在菜单树中)
  PART, // 父子路由嵌套显示(不会展示在菜单树中)
  PAGE_BTN // 页面按钮(不会展示在菜单树中)
}

// loading类型
export enum RequestType {
  REDUCE = -1,
  RESET,
  ADD
}

// 角色类型
export enum RoleType {
  SUPER_ADMIN = -1
}

export enum SearchNodeType {
  INPUT,
  SWITCH,
  SELECT,
  TEXTAREA,
  TREESELECT
}

export enum UrlSchemeEnum {
  HTTP = 'http',
  HTTPS = 'https'
}