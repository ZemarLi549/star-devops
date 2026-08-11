enum ApiUrl {
    getShortcutsMenu = '/auth/shortcutMenu', // 获取快捷入口
    delectShortcutsMenu = '/auth/deleteShortcutMenu', // 删除快捷入口
    addShortcutsMenu = '/auth/saveShortcutMenu',  // 添加快捷入口
    getFirstLevelMenu = '/auth/firstLevelMenuSelect', // 获取快捷入口下拉菜单
    getUserInfo = '/auth/userBaseInfo', // 获取用户信息
    // alertHis = '/api/n9e/alert-his-count',

    getUerWorkspace = '/auth/workspace',

    update = '/auth/updateUser',
    updatepasswd = '/auth/updatePassword',

    getApplicationErrorLogCount = '/log-platform/log/getApplicationErrorLogCount', //获取错误日志数
    getErrorLogDistribution = '/log-platform/analysis/getErrorLogDistribution', //获取错误日志趋势
    getLogsList = '/log-platform/application/list', //获取应用列表
    getAlarmLevelTrend = '/alarm-manager/analysis/level', //获取告警趋势图
    getAlarmLevelAnalysis = '/alarm-manager/statistics/level', //获取告警级别
    getAlarmLevelTask = '/alarm-manager/statistics/task', //获取告警任务
    getAlarmOverview = '/alarm-manager/alarm/overview', //获取告警概览

    getAlarmOverviewData = '/alarm-manager/statistics/task', //获取应用列表
    
    getDeployConfig = '/auth/meta', //获取应用列表

    getProductivityOverview = '/api/productivity/overview/self',
    getProductivityLatestReport = '/api/productivity/report/daily/latest',
    getProductivityLatestSummary = '/api/productivity/report/daily/summary/latest',
    generateProductivitySummary = '/api/productivity/report/daily/summary/generate',
    getProductivityNotifications = '/api/productivity/plugin/notification/latest',
    getAstrBotStatus = '/api/productivity/astrbot/status',
  }
  
  export default ApiUrl
  
