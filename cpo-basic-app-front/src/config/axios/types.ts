/*
 * @Author: zhewu4 
 * @Date: 2023-11-28 17:17:28 
 * @Last Modified by:   zhewu4 
 * @Last Modified time: 2023-11-28 17:17:28 
 */

import type { AxiosRequestConfig, AxiosResponse, AxiosError } from 'axios'
import { RequestType } from '@/globals/enums'
export interface RequestingList {
  url?: string
  globalLoading?: boolean
  type: RequestType
}

export interface CancelRequestSource {
  [index: string]: () => void
}
export interface Response {
  success: boolean
  errorCode: string;
  errorMessage: string
  err: string
  // traceId: string
}

interface SuccessResponse<T = any> extends Response {
  data: T
  content: string
}

export interface RequestInterceptors {
  // 请求拦截
  requestInterceptors?: (config: AxiosRequestConfig) => AxiosRequestConfig
  requestInterceptorsCatch?: (err: any) => Promise<any>
  // 响应拦截
  responseInterceptors?: <T>(
    config: AxiosResponse<SuccessResponse<T>>
  ) => Promise<T | SuccessResponse>
  responseInterceptorsCatch?: (err: AxiosError) => Promise<any>
}

// 自定义传入的参数

export interface RequestConfig extends AxiosRequestConfig {
  interceptors?: RequestInterceptors
  globalLoading?: boolean
}
