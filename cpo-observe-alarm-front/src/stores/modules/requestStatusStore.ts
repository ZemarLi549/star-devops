/*
 * @Author: zhewu4 
 * @Date: 2023-11-28 17:17:55 
 * @Last Modified by:   zhewu4 
 * @Last Modified time: 2023-11-28 17:17:55 
 */

import { defineStore } from 'pinia'
import { ref } from 'vue'
import { findAndRemove } from '@/utils'
import { RequestType } from '@/globals/enums'
import type { RequestingList } from '@/request/axios/types'
export default defineStore('requestStatus', () => {
  const requestingList = ref<Array<RequestingList>>([])

  const updateRequestRecord = (curRequest: RequestingList) => {
    switch (curRequest.type) {
      case RequestType.REDUCE:
        findAndRemove(requestingList.value, curRequest.url, 'url')
        break
      case RequestType.RESET:
        requestingList.value = []
        break
      case RequestType.ADD:
        requestingList.value.push(curRequest)
        break
    }
  }
  return { requestingList, updateRequestRecord }
})
