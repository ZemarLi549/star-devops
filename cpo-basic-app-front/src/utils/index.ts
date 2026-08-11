/*
 * @Author: zhewu4 
 * @Date: 2023-11-28 16:32:58 
 * @Last Modified by:   zhewu4 
 * @Last Modified time: 2023-11-28 16:32:58 
 */
import { reactive, unref } from 'vue'
import { TIME_FORMAT } from '@/globals/constant'
import { BasicType } from '@/globals/interface'

export const isNotEmpty = (val: any): val is BasicType | object =>
  val !== null && val !== undefined && val !== ''

export const isNotEmptyObj = (val: any): val is object =>
  !!(typeof val === 'object' && val !== null && Object.keys(val).length)

export const isArray = (array: any): array is Array<any> => !!(Array.isArray(array) && array.length)

export const getCssVariables = (name: string) =>
  getComputedStyle(document.documentElement).getPropertyValue(name).trim()

export const isOfType = <T>(target: unknown, prop: keyof T): target is T =>
  (target as T)[prop] !== undefined

export const getKeys = <T extends object>(obj: T) => {
  return Object.keys(obj) as Array<keyof T>
}

export const mergeWith = (target: object, sources: object) => {
  getKeys(target).forEach((key) => {
    if (sources[key] !== undefined) {
      target[key] = sources[key]
    }
  })
}

/**
 * map方法对象数组去重
 * @description 深度合并对象
 * @param arr 目标数组对象
 * @param key 唯一判断key值
 * @returns 目标对象
 */
export const filterDuplicate = (arr: any, key: string) => {
  const map = new Map();
  arr.forEach((item: any) => {
      if (!map.has(item[key])) {
          map.set(item[key], item)
      }
  })
  return [...map.values()]
}

// 反向取值
export const reverseValue = <T = any>(array: Array<T>, index?: number) => {
  const length = array.length
  return isNotEmpty(index) ? array[length - index] : array[length - 1]
}

export const getUrlParams = (getName: string | undefined) => {
  const url = window.location.search
  if (url === '') return false
  const searchArr = url.substr(1).split('&')
  const newArr = []

  for (let i = 0; i < searchArr.length; i++) {
    const info = searchArr[i].split('=')
    const obj: { [key: string]: string } = {}
    obj[info[0]] = decodeURI(info[1])
    newArr[i] = obj
  }
  // 如果传入一个参数名称，就匹配其值
  if (getName) {
    for (const iterator of newArr) {
      if (Object.keys(iterator).includes(getName)) return iterator[getName]
    }
    return false
  } else {
    return newArr
  }
}

// 获取hash传参
export const getUrlParamsHash = (getName?: string | undefined) => {
  const url = window.location.hash
  if (url === '') return false
  const searchArr = url.substr(1).split('&')
  const newObj = {}

  for (let i = 0; i < searchArr.length; i++) {
    const info = searchArr[i].split('=')
    if (i === 0) {
      if (info[0].includes('?')) {
        newObj[info[0].split('?')[1]] = isNotEmpty(info[1]) ? decodeURI(info[1]) : null
      }
    } else {
      newObj[info[0]] = isNotEmpty(info[1]) ? decodeURI(info[1]) : null
    }
  }
  // 如果传入一个参数名称，就匹配其值
  if (getName) {
    return newObj[getName]
  } else {
    return newObj
  }
}

export const deepClone = <T>(obj: T, skip: Array<string> = []): T => {
  if (typeof obj !== 'object' || obj == null) {
    return obj
  }
  let result: any
  if (obj instanceof Array) {
    result = []
  } else {
    result = {}
  }
  for (const key in obj) {
    if (Object.hasOwnProperty.call(obj, key)) {
      result[key] = skip.includes(key) ? obj[key] : deepClone(obj[key], skip)
    }
  }
  // 返回结果
  return result
}

export const toRawType = (value: any) => {
  return Object.prototype.toString.call(value).slice(8, -1)
}

/**
 * @description 深度合并对象
 * @param target 目标对象
 * @param source 源对象
 * @param skip 源对象
 * @returns 目标对象
 */
export const deepObjectMerge = (target: object, source: object, skip: Array<string> = []) => {
  if (Array.isArray(source)) {
    if (!source.length) return []

    return deepClone(source)
  }
  for (const key in source) {
    if (Object.prototype.hasOwnProperty.call(source, key) && !skip.includes(key)) {
      target[key] =
        target[key] && typeof target[key] === 'object'
          ? deepObjectMerge(target[key], source[key])
          : (target[key] = source[key])
    }
  }
  return target
}

// 封装reactive,复杂组件恢复默认值比较麻烦,此处保留一下原始数据,并暴露重置方法
export const convertAndKeep = <T extends object>(data: T, skip: Array<string> = []) => {
  if (toRawType(data) !== 'Object') {
    throw Error('Data must be an object')
  }
  const rawState = deepClone(data, skip)
  const state = reactive<T>(data)
  const resetState = () => deepObjectMerge(state, rawState, skip)

  return {
    state,
    rawState,
    resetState
  }
}

export const formatDate = (date: number | Date, fmt: string = TIME_FORMAT) => {
  if (!(date instanceof Date)) {
    date = new Date(date)
  }

  if (/(Y+)/.test(fmt)) {
    fmt = fmt.replace(RegExp.$1, (date.getFullYear() + '').substr(4 - RegExp.$1.length))
  }
  const o = {
    'M+': date.getMonth() + 1,
    'D+': date.getDate(),
    'H+': date.getHours(),
    'm+': date.getMinutes(),
    's+': date.getSeconds()
  }
  for (const k in o) {
    if (new RegExp(`(${k})`).test(fmt)) {
      const str = o[k] + ''
      fmt = fmt.replace(RegExp.$1, RegExp.$1.length === 1 ? str : ('00' + str).substr(str.length))
    }
  }
  return fmt
}

export const findAndRemove = <T>(array: Array<T>, value: T | number | string, key?: string) => {
  const index = array.findIndex((item) => {
    const x = key ? item[key] : item
    return x === value
  })
  if (index > -1) array.splice(index, 1)

  // 支持链式调用
  return findAndRemove
}

export const customToRaw = (state: object) => {
  type TParams = { [key: string]: string | number | null }
  const params: TParams = {}
  Object.keys(state).forEach((key) => (params[key] = unref(state[key])))
  return params
}

export const base64Encoding = (obj: object) => {
  const params: { [key: string]: any } = {}
  for (const key in obj) {
    if (Object.prototype.hasOwnProperty.call(obj, key)) {
      params[window.btoa(encodeURIComponent(key))] =
        obj[key] && obj[key] !== null && typeof obj[key] === 'object'
          ? window.btoa(encodeURIComponent(JSON.stringify(base64Encoding(obj[key]))))
          : window.btoa(encodeURIComponent(obj[key]))
    }
  }
  return params
}

export const base64Decoding = <T = any>(obj: object) => {
  const params = {}
  for (const key in obj) {
    try {
      if (Object.prototype.hasOwnProperty.call(obj, key)) {
        const value = decodeURIComponent(window.atob(obj[key]))
        params[decodeURIComponent(window.atob(key))] =
          value && value !== 'null' && typeof JSON.parse(value) === 'object'
            ? base64Decoding(JSON.parse(value))
            : JSON.parse(value)
      }
    } catch {
      params[decodeURIComponent(window.atob(key))] = decodeURIComponent(window.atob(obj[key]))
    }
  }

  return params as T
}


export const ComNameToLocaleLowerCase = (componentName: string) => {
  let pathName: string = ''
  if (!/^[A-Za-z]+$/.test(componentName)) {
    return pathName
  }

  const componentNames = componentName.split('')

  for (let i = 0; i < componentNames.length; i++) {
    const strCode = componentNames[i].charCodeAt(0)
    if (strCode >= 65 && strCode <= 90 && i !== 0) {
      pathName += '-'
    }
    pathName += componentNames[i].toLocaleLowerCase()
  }

  return pathName
}

export const ComNameToLocaleUpperCase = (path: string) => {
  if (!/^[A-Za-z]+[A-Za-z-]+[A-Za-z]+$/.test(path)) {
    return path
  }

  const componentNames = path.split('')
  componentNames[0] = componentNames[0].toLocaleUpperCase()
  for (let i = 0; i < componentNames.length; i++) {
    if (componentNames[i] === '-') {
      componentNames[i + 1] = componentNames[i + 1].toLocaleUpperCase()
      componentNames.splice(i, 1)
      i--
    }
  }
  return componentNames.join('')
}

// 从父节点找到元素并缓慢滑动指定元素到屏幕中间位置(仅针对于从页面上找到的第一个DOM元素)
export const scrollToDOMView = (parentEl: HTMLElement, selector: string): void => {
  const el = parentEl.querySelector(selector)
  el
    && el.scrollIntoView({
          block: 'center',
          behavior: 'smooth'
        })
}

// 千位分隔符格式化方法
export const formatNumberThousand = (num: number | string) => {
  if (typeof num !== 'number' && (typeof num !== 'string' || isNaN(Number(num)))) {
    return num
  }

  const parsedNum = Number(num);

  if (!isFinite(parsedNum)) {
    return num
  }

  return parsedNum.toLocaleString();
}

// 阿拉伯数字转换中文数字
export const generateChineseNumber = (number: number) => {
  const chineseNumbers = ['', '一', '二', '三', '四', '五', '六', '七', '八', '九', '十'];
  const chineseUnits = ['', '十', '百', '千', '万'];
  
  if (number <= 10) {
    return chineseNumbers[number];
  } else {
    const numberString = number.toString();
    let result = '';
    
    for (let i = 0; i < numberString.length; i++) {
      const digit = parseInt(numberString[i]);
      
      if (digit !== 0) {
        if (numberString.length === 2 && i === 0 && digit === 1) {
          result += chineseUnits[numberString.length - i - 1];
        } else {
          result += chineseNumbers[digit] + chineseUnits[numberString.length - i - 1];
        }
      } else {
        if (i === 0 || numberString[i - 1] !== '0') {
          result += chineseNumbers[digit];
        }
      }
    }
    
    return result;
  }
}

// 判断是否是base64编码的字符
export const isBase64 = (str: string) => {
  const base64Pattern = /^(?:[A-Za-z0-9+\/]{4})*(?:[A-Za-z0-9+\/]{2}==|[A-Za-z0-9+\/]{3}=)?$/;
  return base64Pattern.test(str);
}
