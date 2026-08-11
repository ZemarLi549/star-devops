/*
 * @Author: zhewu4 
 * @Date: 2023-11-28 16:25:06 
 * @Last Modified by: zhewu4
 * @Last Modified time: 2023-11-28 16:35:51
 */

import { isNotEmpty } from './index'

export const sessionCache = {
  set (key:string, value:string) {
    isNotEmpty(key) && isNotEmpty(value) && sessionStorage.setItem(key, value)
  },

  get (key:string) {
    return isNotEmpty(key) ? sessionStorage.getItem(key) : null
  },

  setJSON (key:string, jsonValue:object) {
    isNotEmpty(jsonValue) && this.set(key, JSON.stringify(jsonValue))
  },

  getJSON (key:string) {
    const value = this.get(key)
    return isNotEmpty(value) && JSON.parse(value as any)
  },

  remove (key:string) {
    sessionStorage.removeItem(key)
  }
}

export const localCache = {
  set (key:string, value:string) {
    isNotEmpty(key) && isNotEmpty(value) && localStorage.setItem(key, value)
  },

  get (key:string) {
    return isNotEmpty(key) ? localStorage.getItem(key) : null
  },

  setJSON (key:string, jsonValue:object) {
    isNotEmpty(jsonValue) && this.set(key, JSON.stringify(jsonValue))
  },

  getJSON (key:string) {
    const value = this.get(key)
    return isNotEmpty(value) && JSON.parse(value as any)
  },

  remove (key:string) {
    localStorage.removeItem(key)
  }
}

export default {
  /**
   * 会话级缓存
   */
  session: sessionCache, /**
   * 本地缓存
   */
  local: localCache
}
