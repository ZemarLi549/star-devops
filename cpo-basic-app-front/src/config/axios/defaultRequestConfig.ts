/*
 * @Author: zhewu4 
 * @Date: 2023-11-28 16:56:12 
 * @Last Modified by: zhewu4
 * @Last Modified time: 2023-11-28 17:06:16
 */

import { ElMessage } from "element-plus";
import { 
  HTTP_SUCCESS_CODE, 
  HTTP_CODE, 
  HTTP_SUCCESS_STATUS,
  HTTP_UNLOGIN_CODE,
  ERROR_CODE,
} from './statusCode'
import requestStatusStore from '@/stores/modules/requestStatusStore'
import { RequestType } from '@/globals/enums'
import type { RequestConfig, RequestingList, Response } from './types'
import { AxiosError } from "axios";
import { redirectLogout } from "@/plugin/qiankun/action"
import BaseRequest from './BaseRequest'

export const calculateQuantity = (config: RequestConfig, number: RequestType) => {
  if (config) {
    const params: RequestingList = {
      url: config.url as string,
      globalLoading: config.globalLoading as boolean,
      type: number
    }
    const { updateRequestRecord } = requestStatusStore()
    updateRequestRecord(params)
  }
}

export const defaultRequestConfig: RequestConfig = {
  // baseURL: '/api', // 外网调试
  baseURL: import.meta.env.VITE_API_BASEURL,
  timeout: 60 * 1000,
  globalLoading: true, // 全局loading,默认为true,如果需要自定义loading,可在api中传入false
  interceptors: {
    // 请求拦截器
    requestInterceptors: (config) => {
      calculateQuantity(config, RequestType.ADD)
      return config
    },

    requestInterceptorsCatch(err) {
      errorHandle(err);
      return Promise.reject(err);
    },

    // 响应拦截器
    responseInterceptors: (response) => {
      const {
        config,
        data,
        data: { errorCode, errorMessage, success },
        status
      } = response
      calculateQuantity(config, RequestType.REDUCE)

      return new Promise(async (resolve, reject) => {

        if (status !== HTTP_SUCCESS_STATUS) {
          ElMessage.error(response.statusText);
          return reject(response)
        }

        if (success === false) {
          ElMessage.error(errorMessage)
          await specialHandle(response.data)
          return reject(response.data);
        }

        if (config.url.includes('/graphql')) {
          return resolve(response)
        }
        
        resolve(data.data);
      });
    },

    responseInterceptorsCatch: (reason) => {
      errorHandle(reason);
      calculateQuantity(reason.config, RequestType.REDUCE)
      return Promise.reject(reason)
    }
  }
}

async function errorHandle (err) {
  let message;

  if (err.response) {
    switch(err.response.status) {
      case 401:
        // if (err.config.url.includes('n9e')) {
        //   ElMessage.error(err.response.data.errorMessage)
        //   BaseRequest.cancelAllRequest()
        //   await redirectLogout(true)
        //   return
        // }
        message = '未登录，请重新登录';
        break;
      case 404:
        message = '请求的资源不存在';
        break;
      case 500:
      case 502:
        message = '服务器内部错误';
        break;
      default:
        break;
    }
  } else if (
    [AxiosError.ECONNABORTED, AxiosError.ERR_NETWORK].includes(err.code) ||
    err.message === 'Network Error' ||
    err.message.includes('timeout') ||
    !window.navigator.onLine
  ) {
    message = '网络错误，请检查您的网络连接';
  } else {
    message = '未知错误';
    console.log(message);
    return;
  }

  ElMessage.error(message);
}

export async function specialHandle(error) {
  // debugger
  const { errorCode } = error;
  switch(errorCode) {
    case ERROR_CODE.PASSWORD_MODIFIED:
    case ERROR_CODE.TOKEN_LOGOUT:
    case ERROR_CODE.USER_NOT_FOUND:
    case ERROR_CODE.TOKEN_EXCEED_TIME:
    case ERROR_CODE.NEED_LOGIN:
    case ERROR_CODE.REFRESH_TOKEN_EXCEED_TIME:
      BaseRequest.cancelAllRequest()
      await redirectLogout()
      break
    // 用户被禁用时如果单点登录需要提示, 因此添加延迟
    case ERROR_CODE.USER_IS_DISABLED:
      BaseRequest.cancelAllRequest()
      await redirectLogout(true)
      break;
    default:
      break;
  }
}