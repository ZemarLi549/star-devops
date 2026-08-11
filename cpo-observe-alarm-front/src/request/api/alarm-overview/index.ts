import Request from '@/request/axios/Request'
import ApiUrl from './ApiUrl'

class OverviewApi extends Request {
  /**
   * 告警概览
   */
  alarmOverview(startTime, endTime, isSelf = false) {
    return this.request({
      url: ApiUrl.alarmOverview,
      method: 'POST',
      data: {
        startTime: startTime,
        endTime: endTime,
        isSelf: isSelf
      }
    })
  }


  /**
   * 告警级别分析
   */
  levelStatistics(startTime, endTime, isSelf = false) {
    return this.request({
      url: ApiUrl.levelStatistics,
      method: 'POST',
      data: {
        startTime: startTime,
        endTime: endTime,
        isSelf: isSelf
      }
    })
  }

  /**
   * 告警任务分析
   */
  taskStatistics(startTime, endTime, isSelf = false) {
    return this.request({
      url: ApiUrl.taskStatistics,
      method: 'POST',
      data: {
        startTime: startTime,
        endTime: endTime,
        isSelf: isSelf
      }
    })
  }

  /**
   * 告警级别趋势
   */
  levelTend(startTime, endTime, isSelf = false) {
    return this.request({
      url: ApiUrl.levelTend,
      method: 'POST',
      data: {
        startTime: startTime,
        endTime: endTime,
        isSelf: isSelf
      }
    })
  }

  /**
   * 告警数据类型占比
   */
  cateStatistics(startTime, endTime, isSelf = false) {
    return this.request({
      url: ApiUrl.cateStatistics,
      method: 'POST',
      data: {
        startTime: startTime,
        endTime: endTime,
        isSelf: isSelf
      }
    })
  }

  /**
   * 事件压缩比
   */
  eventCompress(startTime, endTime, isSelf = false) {
    return this.request({
      url: ApiUrl.eventCompress,
      method: 'POST',
      data: {
        startTime: startTime,
        endTime: endTime,
        isSelf: isSelf
      }
    })
  }

  /**
   * 分类统计
   */
  alarmCount(startTime, endTime, isSelf = false) {
    return this.request({
      url: ApiUrl.alarmCount,
      method: 'POST',
      data: {
        startTime: startTime,
        endTime: endTime,
        isSelf: isSelf
      }
    })
  }

  /**
   * 监控来源
   */
  originalTend(startTime, endTime, isSelf = false) {
    return this.request({
      url: ApiUrl.originalTend,
      method: 'POST',
      data: {
        startTime: startTime,
        endTime: endTime,
        isSelf: isSelf
      }
    })
  }

  /**
   * 数据单元排行
   */
  dataGroupRank(startTime, endTime, isSelf = false) {
    return this.request({
      url: ApiUrl.dataGroupRank,
      method: 'POST',
      data: {
        startTime: startTime,
        endTime: endTime,
        isSelf: isSelf
      }
    })
  }

  /**
   * 新手指引
   */
  guideState(data) {
    return this.request({
      url: ApiUrl.guideState,
      method: 'post',
      data
    })
  }
}

export default new OverviewApi()
