import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useOverviewStore = defineStore('overview', () => {
  const start = new Date()
  const end = new Date()
  start.setDate(start.getDate() - 6)
  const timeRange = ref([start, end])

  return {
    timeRange
  }
});

