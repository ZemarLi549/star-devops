/*
 * @Author: zhewu4 
 * @Date: 2023-11-28 17:07:03 
 * @Last Modified by: zhewu4
 * @Last Modified time: 2023-11-28 17:17:11
 */

import BaseRequest from './BaseRequest'
import defaultsDeep from 'lodash/defaultsDeep'
import { defaultRequestConfig } from './defaultRequestConfig'
import type { RequestConfig } from './types'

/**
 * @description 父类,项目内模块共用,父类可以有多个
 * @param config RequestConfig配置对象,父类中有默认配置,子类传入会合并在父类默认配置中
 */
class ParentRequest extends BaseRequest {
  // 子类如果传入了配置,合并在父类配置中
  constructor(config?: RequestConfig) {
    super(config ? defaultsDeep(config, defaultRequestConfig) : defaultRequestConfig)
  }

  // 此处可定义公共请求方法
}

export default ParentRequest
