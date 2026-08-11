import axios from "axios"
import type { AxiosInstance, AxiosRequestConfig, AxiosResponse } from 'axios'
import type { RequestConfig, CancelRequestSource,ResponseDataError } from './types'
import { ElMessage } from "element-plus";
import {HTTP_SUCCESS_STATUS,NO_AUTH_STATUS,ERROR_CODE} from './statusCode'
import actions from "@/plugin/qiankun/action";
import { useUserStore } from "@/stores/modules/user"
import { AUTHORIZATION, WORKSPACEID } from '@/globals/constant'

let requestQueue:any = [];
let isRefreshing = false;
/**
 * @description 底层基类,定义request方法,基类为整个项目共用
 * @description 私有请求拦截器 --> 全局请求拦截器 --> 私有响应拦截器 --> 全局响应拦截器
 * @param config RequestConfig配置对象
 */
class BaseRequest {
  // axios 实例
  private instance: AxiosInstance

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

    // 全局请求拦截器
    this.instance.interceptors.request.use(
      (config: AxiosRequestConfig) => {

        // url存在保存取消请求方法和当前请求url
        if (config.url) {
          BaseRequest.requestUrlList.push(config.url)
          config.cancelToken = new axios.CancelToken((c) =>
            BaseRequest.cancelRequestSourceList.push({
              [config.url as string]: c
            })
          )
        }
 
        // refreshToken直接放行
        const whiteList = ['/refreshToken'];
        const inWhiteList = whiteList.some(key => config.url?.indexOf(key) !== -1);
        if (inWhiteList) {
          return config
        }

        const { userInfo,refreshToken } = useUserStore();

        const { token, workspace_id, expire_time } = userInfo

        const isExpire = expire_time > Date.now();

        (config['headers'] as any)[AUTHORIZATION] = token;
        (config['headers'] as any)[WORKSPACEID] = workspace_id;

        // 已登录 时效校验
        if (isExpire) return config
         // 失效
        if (!isRefreshing) {
          // 只进来一个请求
          isRefreshing = true;
          refreshToken().then((token) => {
            requestQueue.forEach(cb => cb(token));
            requestQueue = [];
            isRefreshing = false;
          });;
        }
        const retry = new Promise(resolve => {
          requestQueue.push((token:string) => {
            (config['headers'] as any)[AUTHORIZATION] = token;
            resolve(config);
          })
        });
        return retry;
      },
      (AxiosError) => {
        Promise.reject(AxiosError)
      }
    )

    // 全局响应拦截器
    this.instance.interceptors.response.use(
      (response: AxiosResponse) => {
        const {
          config,
          data,
          status,
        } = response
        
        return new Promise((resolve, reject) => {
          // 文件下载 直接放行
          if (config.responseType === 'blob') {
            return resolve(data)
           }
          // status不为200
          if (status !== HTTP_SUCCESS_STATUS) {
            ElMessage.error(response.statusText);
            return reject(response.statusText)
          }
          
            // 日志接口响应
            const { success, errorMessage } = data;
            // 接口异常
            if (success === false) {
                BaseRequest.specialHandle(data)
                return reject(errorMessage);
              }
            return resolve(data.data)
        })
      },
      ( AxiosError) => {
        const { status, statusText } = AxiosError.response
        ElMessage.error(statusText);
          // 状态码处理401
        if (status === NO_AUTH_STATUS) {
          // debugger
          actions.setGlobalState({ isBackLogin: true });
          BaseRequest.cancelAllRequest()
        }
        Promise.reject(AxiosError)
      } 
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

    // 错误处理
    static specialHandle(error:ResponseDataError) {
      const { errorCode, errorMessage } = error;
      if (ERROR_CODE[errorCode] === 'BACK_LOGIN') {  
        actions.setGlobalState({ isBackLogin: true });
        BaseRequest.cancelAllRequest()
      } else {
        ElMessage({
          showClose: true,
          message: errorMessage,
          type: "error",
        })
      }
    }

  request<T = any>(config: RequestConfig): Promise<T> {
    return this.instance
      .request<any, T>(config)
      .then((value) => value)
      .catch((reason) => Promise.reject(reason))
      .finally(() => config.url && BaseRequest.delUrl(config.url))
  }
}

export default BaseRequest
