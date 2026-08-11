/**
 * 左侧菜单栏
 * -------------------------------------------------------------------------- */
export function sidebar() {
  return {
    // 产品概述
    "/product/": [
      {
        base: "/product/",
        text: "产品概述",
        link: "README.md",
      },
    ],
    // 快速入门
    "/quick/": [
      {
        text: "接入指南",
        base: "/quick/",
        items: [
          { text: "采控接入", link: "采控接入.md" },
          { text: "时序指标接入", link: "时序指标接入.md" },
          { text: "链路数据接入", link: "链路数据接入.md" },
          { text: "日志sdk接入", link: "日志sdk接入.md" },
          { text: "日志系统接入", link: "日志系统接入.md" },
          {
            text: "调用链自定义埋点",
            link: "调用链自定义埋点场景下的接入案例.md",
          },
          { text: "拨测点接入", link: "拨测点接入.md" },
          { text: "用户访问监控接入", link: "用户访问监控接入.md" },
        ],
        collapsed: false,
      },
      {
        text: "组件接入",
        base: "/quick/metric/",
        items: [
          { text: "Activemq", link: "Activemq.md" },
          { text: "Container", link: "Container.md" },
          { text: "Dialingtest", link: "Dialingtest.md" },
          { text: "DM-EXPORTER", link: "DM-EXPORTER.md" },
          { text: "DM8-DEM", link: "DM8-DEM.md" },
          { text: "Doris", link: "Doris.md" },
          { text: "Elasticsearch", link: "Elasticsearch.md" },
          { text: "Etcd", link: "Etcd.md" },
          { text: "Flink", link: "Flink.md" },
          { text: "Influxdb", link: "Influxdb.md" },
          { text: "JVM", link: "JVM.md" },
          { text: "Kafka", link: "Kafka.md" },
          { text: "Keepalived", link: "Keepalived.md" },
          { text: "Memcached", link: "Memcached.md" },
          { text: "Minio", link: "Minio.md" },
          { text: "Mongodb", link: "Mongodb.md" },
          { text: "Mysql", link: "Mysql.md" },
          { text: "Nacos", link: "Nacos.md" },
          { text: "Nginx", link: "Nginx.md" },
          { text: "NPU", link: "NPU.md" },
          { text: "GPU", link: "Nvidia.md" },
          { text: "Oracle", link: "Oracle.md" },
          { text: "PostgreSQL", link: "PostgreSQL.md" },
          { text: "Rabbitmq", link: "Rabbitmq.md" },
          { text: "Redis", link: "Redis.md" },
          { text: "Rocketmq", link: "Rocketmq.md" },
          { text: "Statsd", link: "Statsd.md" },
          { text: "Tomcat", link: "Tomcat.md" },
          { text: "Zookeeper", link: "Zookeeper.md" },
          { text: "Snmp", link: "Snmp.md" },
          { text: "进程", link: "进程.md" },
          { text: "Redis-Cluster", link: "Redis-Cluster.md" },
        ],
        collapsed: false,
      },
    ],
    // 操作指南
    "/guide/": [
      { text: "工作台", base: "/guide/", link: "工作台.md" },
      { text: "环境配置", base: "/guide/", link: "环境配置操作文档.md" },
      { 
        text: "业务观测",
        base: "/guide/",
        collapsed: false,
        items: [
          { text: "仪表盘", link: "仪表盘.md" },
          {
            text: "告警事件",
            base: "/guide/",
            items: [
              { text: "日志告警规则操作文档", link: "日志告警规则操作文档.md" },
              { text: "链路告警规则操作文档", link: "链路告警规则操作文档.md" },
              { text: "指标告警操作文档", link: "指标告警操作文档.md" },
            ],
            collapsed: true,
          },
          {
            text: "作业管理",
            base: "/guide/",
            items: [
              { text: "脚本操作文档", link: "作业脚本操作文档.md" },
              { text: "作业任务操作文档", link: "作业任务操作文档.md" },
              { text: "执行结果操作文档", link: "执行结果操作文档.md" },
            ],
            collapsed: true,
          },
          { text: "应用性能监控", link: "应用性能监控操作文档.md" },
          {
            text: "基础设施监控",
            base: "/guide/",
            items: [
              { text: "机器列表操作文档", link: "机器列表操作文档.md" },
              { text: "容器列表操作文档", link: "容器列表操作文档.md" },
              { text: "网络设备列表操作文档", link: "网络设备列表操作文档.md" },
            ],
            collapsed: true,
          },
          { text: "可用性监控", link: "可用性监控操作文档.md" },
          {
            text: "用户访问监控",
            base: "/guide/",
            items: [
              { text: "应用接入操作文档", link: "用户访问监控应用接入操作文档.md"},
              { text: "查看器操作文档", link: "查看器操作文档.md"},
              { text: "分析看板",
                items: [
                  { text: "概览操作文档", link: "用户访问监控分析看板概览.md"},
                  { text: "性能分析操作文档", link: "用户访问监控性能分析看板.md" },
                  { text: "资源分析操作文档", link: "用户访问监控资源分析看板.md" },
                  { text: "错误分析操作文档", link: "用户访问监控错误分析看板.md" },
                ],
              },
            ],
            collapsed: true,
          },
          {
            text: "时序指标",
            base: "/guide/",
            items: [
              { text: "即时查询操作文档", link: "即时查询操作文档.md" },
              { text: "快捷视图操作文档", link: "快捷视图操作文档.md" },
              { text: "记录规则操作文档", link: "记录规则操作文档.md" },
            ],
            collapsed: true,
          },
          { text: "链路追踪", link: "链路追踪操作文档.md" },
          {
            text: "日志分析",
            base: "/guide/",
            items: [
              { text: "日志检索操作文档", link: "日志检索操作文档.md" },
              {
                text: "Grafana菜单配置操作文档",
                link: "Grafana菜单配置操作文档.md",
              },
            ],
            collapsed: true,
          },
          {
            text: "数据接入",
            base: "/guide/",
            items: [
              { text: "指标接入操作文档", link: "指标接入操作文档.md" },
              { text: "链路接入操作文档", link: "链路接入操作文档.md" },
              { text: "日志接入操作文档", link: "日志接入操作文档.md" },
              { text: "拨测点接入操作文档", link: "拨测点接入操作文档.md" },
            ],
            collapsed: true,
          },
        ]
      },
      {
        text: "智能告警",
        base: "/guide/",
        collapsed: true,
        items: [
          { text: "告警概览操作文档", link: "告警概览操作文档.md" },
          { text: "我的告警操作文档", link: "我的告警操作文档.md" },
          { text: "所有告警操作文档", link: "所有告警操作文档.md" },
          { text: "未分派告警操作文档", link: "未分派告警操作文档.md" },
          { text: "屏蔽告警操作文档", link: "屏蔽告警操作文档.md" },
          { text: "所有通知操作文档", link: "所有通知操作文档.md" },
          { text: "分派策略操作文档", link: "分派策略操作文档.md" },
          { text: "屏蔽规则操作文档", link: "屏蔽规则操作文档.md" },
          { text: "通知策略操作文档", link: "通知策略操作文档.md" },
          { text: "通知方式操作文档", link: "通知方式操作文档.md" },
          { text: "通知组操作文档", link: "通知组操作文档.md" },
          { text: "自定义标签操作文档", link: "自定义标签操作文档.md" },
          { text: "监控系统操作文档", link: "监控系统操作文档.md" },
        ],
      }
    ],
    // 部署文档
    "/deploy/": [
      { text: "部署资源参考", link: "/deploy/部署资源参考.md" },
      { text: "中间件部署", link: "/deploy/中间件部署.md" },
      { text: "权限系统部署", link: "/deploy/权限系统部署.md" },
      { text: "调用链系统部署", link: "/deploy/调用链系统部署.md" },
      { text: "日志系统部署", link: "/deploy/日志系统部署.md" },
      { text: "指标监控系统部署", link: "/deploy/指标监控系统部署.md" },
      { text: "告警系统部署", link: "/deploy/告警系统部署.md" },
      { text: "采控系统部署", link: "/deploy/采控系统部署.md" },
      { text: "服务自动拉起", link: '/deploy/服务自动拉起.md' },
      { text: "前端部署", link: "/deploy/前端部署.md" },
      { text: "容器化部署", link: "/deploy/容器化部署.md" },
    ],
    // 升级文档
    "/upgrade/": [
      { text: "升级V4.1.0到V4.1.1", link: "/upgrade/升级V4.1.0到V4.1.1.md" },
      { text: "升级V4.0.9到V4.1.0", link: "/upgrade/升级V4.0.9到V4.1.0.md" },
      { text: "升级V4.0.8到V4.0.9", link: "/upgrade/升级V4.0.8到V4.0.9.md" },
      { text: "升级V4.0.7到V4.0.8", link: "/upgrade/升级V4.0.7到V4.0.8.md" },
      { text: "升级V4.0.6到V4.0.7", link: "/upgrade/升级V4.0.6到V4.0.7.md" },
      { text: "升级V4.0.4到V4.0.6", link: "/upgrade/升级V4.0.4到V4.0.6.md" },
      { text: "升级V4.0.3到V4.0.4", link: "/upgrade/升级V4.0.3到V4.0.4.md" },
      { text: "升级V4.0.2到V4.0.3", link: "/upgrade/升级V4.0.2到V4.0.3.md" },
      { text: "升级V4.0.0到V4.0.2", link: "/upgrade/升级V4.0.0到V4.0.2.md" },
    ],
    // 更新日志
    "/changeLog/": [
      {
        base: "/changeLog/",
        text: "更新日志",
        link: "README.md",
      },
    ],
    // 常见问题
    "/question/": [
      {
        base: "/question/",
        text: "常见问题",
        link: "README.md",
      },
    ],
  };
}

/**
 * 顶部导航栏
 * -------------------------------------------------------------------------- */
export function nav() {
  return [
    { text: "产品概述", link: "/product/README.md", activeMatch: "/product/" },
    { text: "快速入门", link: "/quick/采控接入.md", activeMatch: "/quick/" },
    {
      text: "操作指南",
      link: "/guide/环境配置操作文档.md",
      activeMatch: "/guide/",
    },
    {
      text: "部署文档",
      link: "/deploy/部署资源参考.md",
      activeMatch: "/deploy/",
    },
    {
      text: "升级文档",
      link: `${sidebar()['/upgrade/'][0].link}`,
      activeMatch: "/upgrade/",
    },
    {
      text: "更新日志",
      link: "/changeLog/README.md",
      activeMatch: "/changeLog/",
    },
    // {
    //   text: "常见问题",
    //   link: "/question/README.md",
    //   activeMatch: "/question/",
    // },
  ];
}
