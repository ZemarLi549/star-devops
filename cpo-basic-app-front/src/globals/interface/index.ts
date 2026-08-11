/*
 * @Author: zhewu4 
 * @Date: 2023-11-28 16:28:00 
 * @Last Modified by: zhewu4
 * @Last Modified time: 2023-11-28 16:28:38
 */

export interface CommonResponseGetList<T extends object> {
  current: number
  maxLimit: number
  orders: Array<string>
  pages: number
  records: Array<T>
  size: number
  total: number
}

export interface Options<T = string | number> {
  label: string
  value: T
}

export type BasicType = number | string | boolean
