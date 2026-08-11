import { defineStore } from 'pinia'
import { ref } from 'vue'
import { findAndRemove } from '@/utils'
import { RequestType } from '@/globals/enums'
import type { RequestingList } from '@/config/axios/types'
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
