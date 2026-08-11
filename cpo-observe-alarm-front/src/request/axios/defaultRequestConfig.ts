import type { RequestConfig } from './types'


export const defaultRequestConfig: RequestConfig = {
  // baseURL: '/api', // 外网调试
  baseURL: import.meta.env.VITE_API_BASEURL,
  timeout: 60 * 1000,
  globalLoading: true, // 全局loading,默认为true,如果需要自定义loading,可在api中传入false
}
