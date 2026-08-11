/*
 * @Author: zhewu4 
 * @Date: 2023-11-28 16:08:39 
 * @Last Modified by: zhewu4
 * @Last Modified time: 2023-11-29 18:26:51
 */
import axios from "axios"
import type { AxiosInstance, AxiosRequestConfig, AxiosResponse } from 'axios'
import type { RequestConfig, RequestInterceptors, CancelRequestSource } from './types'
import { AUTHORIZATION } from '@/globals/constant'
import { useUserStore } from "@/stores/modules/user"
import { redirectLogout } from "@/plugin/qiankun/action"

let requestQueue = [];
let isRefreshing = false;
/**
 * @description 底层基类,定义request方法,基类为整个项目共用
 * @description 私有请求拦截器 --> 全局请求拦截器 --> 私有响应拦截器 --> 全局响应拦截器
 * @param config RequestConfig配置对象
 */
class BaseRequest {
  // axios 实例
  private instance: AxiosInstance
  // 拦截器对象
  private interceptorsObj?: RequestInterceptors

  /*
  存放取消方法的集合
  * 在创建请求后将取消请求方法 push 到该集合中
  * 封装一个方法，可以取消请求，传入 url: string|string[] 
  * 在请求之前判断同一URL是否存在，如果存在就取消请求
  */
  private static cancelRequestSourceList: CancelRequestSource[]

  /*
  存放所有请求URL的集合
  * 请求之前需要将url push到该集合中
  * 请求完毕后将url从集合中删除
  * 添加在发送请求之前完成，删除在响应之后删除
  */
  private static requestUrlList: string[]

  constructor(config: RequestConfig) {
    // 数据初始化
    BaseRequest.requestUrlList = []
    BaseRequest.cancelRequestSourceList = []
    this.instance = axios.create(config)
    this.interceptorsObj = config.interceptors

    // 全局请求拦截器
    this.instance.interceptors.request.use(
      (config: AxiosRequestConfig) => {
        // if (config.headers) {
        //   config.headers.hash = location.origin
        //   if (userInfo.token) {
        //   }
        // }

        // url存在保存取消请求方法和当前请求url
        if (config.url) {
          BaseRequest.requestUrlList.push(config.url)
          config.cancelToken = new axios.CancelToken((c) =>
            BaseRequest.cancelRequestSourceList.push({
              [config.url as string]: c
            })
          )
        }

        const whiteList = ['/login', '/loginMode', '/logout', '/refreshToken', '/captcha'];
        const { userInfo, refreshToken } = useUserStore();
        const { token, expire_time, workspace_id } = userInfo;
        config.headers[AUTHORIZATION] = token;
        if (workspace_id !== null && workspace_id !== undefined && !config.headers['Workspace-Id']) {                    
          config.headers['Workspace-Id'] = workspace_id
        }
        // config.headers['Workspace-Id'] = workspace_id;
        const inWhiteList = whiteList.some(key => config.url.indexOf(key) !== -1);
        const isExpire = expire_time > Date.now();
        // 白名单跳过
        if (inWhiteList) return config;
        // 未登录状态
        if (!token) {
          redirectLogout();
          return;
        }
        // 已登录 时效校验
        if (isExpire) return config;
        // 失效
        if (!isRefreshing) {
          // 只进来一个请求
          isRefreshing = true;
          refreshToken().then((token) => {
            requestQueue.forEach(cb => cb(token));
            requestQueue = [];
            isRefreshing = false;
          });
        }
        const retry = new Promise(resolve => {
          requestQueue.push((token) => {
            config.headers[AUTHORIZATION] = token;
            resolve(config);
          })
        });
        return retry;
      },
      (reason) => Promise.reject(reason)
    )
    // 使用实例拦截器
    this.instance.interceptors.request.use(
      this.interceptorsObj?.requestInterceptors,
      this.interceptorsObj?.requestInterceptorsCatch
    )
    this.instance.interceptors.response.use(
      this.interceptorsObj?.responseInterceptors,
      this.interceptorsObj?.responseInterceptorsCatch
    )

    // 全局响应拦截器
    this.instance.interceptors.response.use(
      (res: AxiosResponse) => res,
      (reason) => Promise.reject(reason)
    )
  }

  /**
   * @description: 获取指定 url 在 cancelRequestSourceList 中的索引
   * @param {string} url
   * @returns {number} 索引位置
   */
  private static getSourceIndex(url: string): number {
    return BaseRequest.cancelRequestSourceList.findIndex((item: CancelRequestSource) => {
      return Object.keys(item)[0] === url
    })
  }

  /**
   * @description: 删除 requestUrlList 和 cancelRequestSourceList
   * @param {string} url
   * @returns {*}
   */
  private static delUrl(url: string) {
    const urlIndex = BaseRequest.requestUrlList.findIndex((u) => u === url)
    const sourceIndex = BaseRequest.getSourceIndex(url)
    // 删除url和cancel方法
    urlIndex !== -1 && BaseRequest.requestUrlList.splice(urlIndex, 1)
    sourceIndex !== -1 && BaseRequest.cancelRequestSourceList.splice(sourceIndex, 1)
  }

  /**
   * @description: 取消全部请求
   * @param {*}
   * @returns {*}
   */
  static cancelAllRequest() {
    BaseRequest.cancelRequestSourceList.forEach((source) => {
      const key = Object.keys(source)[0]
      source[key]()
    })
  }

  // 取消请求
  static cancelRequest(url: string | string[]) {
    if (typeof url === 'string') {
      // 取消单个请求
      const sourceIndex = BaseRequest.getSourceIndex(url)
      sourceIndex >= 0 && BaseRequest.cancelRequestSourceList?.[sourceIndex][url]()
    } else {
      // 存在多个需要取消请求的地址
      url.forEach((u) => {
        const sourceIndex = BaseRequest.getSourceIndex(u)
        sourceIndex >= 0 && BaseRequest.cancelRequestSourceList?.[sourceIndex][u]()
      })
    }
  }

  request<T = any>(config: RequestConfig): Promise<T> {
    // if (config.url) {
    //   const sourceIndex = BaseRequest.getSourceIndex(config.url)
    //   if (sourceIndex >= 0) {
    //     return Promise.reject(Error('请求重复'))
    //   }
    // }
    return this.instance
      .request<any, T>(config)
      .then((value) => value)
      .catch((reason) => Promise.reject(reason))
      .finally(() => config.url && BaseRequest.delUrl(config.url))
  }
}

export default BaseRequest
