import { MODULE_ID } from "@/globals/constant";
import alarm_error from "@/assets/imgs/control/alarm_error.png";
import alarm_warn from "@/assets/imgs/control/alarm_warn.png";
import alarm_safe from "@/assets/imgs/control/alarm_safe.png";
import { PROJECT_MAP } from "@/globals/constant";
// 引导页tab栏
export const tabDetailData = [
  {
    title: "平台定位",
    cards: [
      {
        title: "鑫图平台总览",
        key: '01',
        info: "统一承接智能助手、自动化执行和协同分析能力，作为后续微服务的统一门户。",
        link: "",
        avatar_img: "1_1"
      },
      {
        title: "统一身份与权限",
        key: '02',
        info: "复用当前账号、工作空间和菜单体系，为 AstrBot 与 AI 自动化子系统提供统一权限边界。",
        link: "",
        avatar_img: "1_2"
      },
      {
        title: "微服务接入规范",
        key: '03',
        info: "后续所有服务按网关、鉴权和前端基座约定接入，避免出现重复登录与多套入口。",
        link: "",
        avatar_img: "1_3"
      },
    ],
  },
  {
    title: "智能助手",
    cards: [
      {
        title: "AstrBot 前端控制台",
        key: '01',
        info: "提供智能助手配置、插件管理、知识库接入和运维状态查看等统一界面。",
        link: "",
        avatar_img: "2_1"
      },
      {
        title: "AstrBot 后端能力",
        key: '02',
        info: "规划接入会话编排、任务执行、工具调用与插件分发，作为平台第一批核心微服务。",
        link: "",
        avatar_img: "2_2"
      },
    ],
  },
  {
    title: "AI 人效",
    cards: [
      {
        title: "沟通活跃度统计",
        key: '01',
        info: "统计个人飞书聊天次数、被@次数，并支持群聊范围、关键字和时间窗口过滤。",
        link: "",
        avatar_img: "3_1"
      },
      {
        title: "会议效率分析",
        key: '02',
        info: "采集会议次数、总时长、参会结构和时段分布，为团队协作效率提供量化视图。",
        link: "",
        avatar_img: "3_2"
      },
      {
        title: "文档贡献度分析",
        key: '03',
        info: "追踪飞书文档的编写、修改和协作贡献，形成个人和团队维度的内容产出画像。",
        link: "",
        avatar_img: "3_3"
      },
    ],
  },
  {
    title: "数据采集",
    cards: [
      {
        title: "聊天事件接入",
        key: '01',
        info: "通过飞书开放平台或用户授权插件采集消息元数据，不默认保存原始内容。",
        link: "",
        avatar_img: "4_1"
      },
      {
        title: "会议事件同步",
        key: '02',
        info: "同步会议开始、结束、时长与参会人结构，支撑自动化统计与时间利用率分析。",
        link: "",
        avatar_img: "4_2"
      },
      {
        title: "文档变更同步",
        key: '02',
        info: "按授权范围同步文档变更摘要与贡献记录，为日报总结和贡献度看板提供输入。",
        link: "",
        avatar_img: "4_3"
      },
    ],
  },
  {
    title: "自动化工作流",
    cards: [
      {
        title: "日报总结",
        key: '01',
        info: "围绕工作话题、会议摘要和文档贡献自动生成日报草稿，保留人工审阅确认环节。",
        link: "",
        avatar_img: "5_1"
      },
      {
        title: "个人下载统计插件",
        key: '02',
        info: "提供公共插件能力，支持个人维度报表下载、过滤条件配置与审计记录留存。",
        link: "",
        avatar_img: "5_2"
      },
      {
        title: "预警与提醒",
        key: '03',
        info: "基于沟通、会议和文档活动趋势，输出工作负载异常提醒和协作风险提示。",
        link: "",
        avatar_img: "5_3"
      },
    ],
  },
  {
    title: "安全治理",
    cards: [
      {
        title: "最小权限采集",
        key: '01',
        info: "只在用户授权和管理员许可范围内采集必要字段，默认以统计元数据优先。",
        link: "",
        avatar_img: "6_1"
      },
      {
        title: "脱敏与审计",
        key: '02',
        info: "对消息、会议和文档字段执行脱敏存储、访问审计和可追溯下载控制。",
        link: "",
        avatar_img: "6_2"
      },
      {
        title: "保留与删除策略",
        key: '03',
        info: "按个人、团队和租户维度配置数据保留期，支持撤回授权、导出与删除申请。",
        link: "",
        avatar_img: "6_3"
      },
      {
        title: "LLM 总结边界",
        key: '04',
        info: "限制模型读取范围和输出内容，避免在未授权情况下暴露敏感协作信息。",
        link: "",
        avatar_img: "6_4"
      },
    ],
  },
];

// 工作台跳转文档中心链接映射
export const docLinkArray = [
  {
    title: 'SOP Doc 文档中心',
    link: '/sop-doc',
    tag: 'SOP',
  },
  {
    title: 'SOP VDOC 可视化文档',
    link: '/sop-vdoc',
    tag: 'VDOC',
  },
  {
    title: '前端微服务接入文档',
    link: '',
    tag: '接入',
  },
  {
    title: 'AI 人效数据采集规范',
    link: '',
    tag: '治理',
  },
  {
    title: '用户身份与 LDAP 接入设计',
    link: '',
    tag: '身份',
  },
  {
    title: '工单与资源总览接入设计',
    link: '',
    tag: '运营',
  },
  {
    title: '飞书数据授权与安全说明',
    link: '',
    tag: '安全',
  },
]

// 新手指引steps
export const basicSteps = [
  {
    element: "#introduce-card",
    popover: {
      title: "平台规划速览",
      description:
        "这里展示鑫图平台的能力规划与接入路线，可作为后续微服务改造的首页说明。",
      position: "right-center",
    },
  },
  {
    element: "#entrance-card",
    popover: {
      title: "服务入口",
      description:
        "这里展示可直接进入的服务入口，后续新的微服务会按卡片形式独立接入。",
      position: "bottom-center",
    },
  },
  {
    element: "#overview-card",
    popover: {
      title: "个人运营总览",
      description:
        "这里展示工单、飞书、文档、会议、资源和 AI 待办等个人纵览位。",
      position: "bottom-center",
    },
  },
  {
    element: "#user-info-card",
    popover: {
      title: "用户管理",
      description: "通过该模块进行用户、权限、工作空间的查看与管理。",
      position: "bottom-center",
    },
  },
  {
    element: "#alarm-info-card",
    popover: {
      title: "我的告警任务",
      description:
        "通过快捷面板可以一目了然地看到自己需要完成的任务。",
      position: "left-center",
    },
    deploy: PROJECT_MAP.OBSERVE_ALARM,
  },
  {
    element: "#workspace-box",
    popover: {
      title: "当前的位置",
      description:
        "可以从该区域查看您当前所在的平台位重, 同时支持快速切换不同工作空间。",
      position: "bottom-center",
    },
  },
  {
    element: "#document-center-link",
    popover: {
      title: "学习更多知识",
      description: "介绍各模块功能和专业知识, 帮助您快速上手、提供操作指南。",
      position: "bottom-center",
    },
  },
];

// 时间选择控件shortcuts配置
export const trendShortcuts = [
  {
    text: "近7天",
    value: () => {
      const end = new Date(new Date().setHours(23, 59, 59, 59));
      const start = new Date(new Date().setHours(24, 0, 0, 0) - 3600 * 1000 * 24 * 7);
      window.__isshort_console_alarm__ = "近7天"
      return [start, end];
    },
  },
  {
    text: "近14天",
    value: () => {
      const end = new Date(new Date().setHours(23, 59, 59, 59));
      const start = new Date(new Date().setHours(24, 0, 0, 0) - 3600 * 1000 * 24 * 14);
      window.__isshort_console_alarm__ = "近14天"
      return [start, end];
    },
  },
  {
    text: "近30天",
    value: () => {
      const end = new Date(new Date().setHours(23, 59, 59, 59));
      const start = new Date(new Date().setHours(24, 0, 0, 0) - 3600 * 1000 * 24 * 30);
      window.__isshort_console_alarm__ = "近30天"
      return [start, end];
    },
  },
  {
    text: "近90天",
    value: () => {
      const end = new Date(new Date().setHours(23, 59, 59, 59));
      const start = new Date(new Date().setHours(24, 0, 0, 0) - 3600 * 1000 * 24 * 90);
      window.__isshort_console_alarm__ = "近90天"
      return [start, end];
    },
  },
];

export const observerShortCuts = [
  {
    text: "近15分钟",
    value: () => {
      const end = new Date();
      const start = new Date();
      start.setTime(start.getTime() - 60 * 1000 * 15);
      window.__isshort_console_sw__ = "近15分钟"
      return [start, end];
    },
  },
  {
    text: "近30分钟",
    value: () => {
      const end = new Date();
      const start = new Date();
      start.setTime(start.getTime() - 60 * 1000 * 30);
      window.__isshort_console_sw__ = "近30分钟"
      return [start, end];
    },
  },
  {
    text: "近1小时",
    value: () => {
      const end = new Date();
      const start = new Date();
      start.setTime(start.getTime() - 3600 * 1000);
      window.__isshort_console_sw__ = "近1小时"
      return [start, end];
    },
  },
  {
    text: "近1天",
    value: () => {
      const end = new Date();
      const start = new Date();
      start.setTime(start.getTime() - 3600 * 1000 * 24);
      window.__isshort_console_sw__ = "近1天"
      return [start, end];
    },
  },
  {
    text: "近7天",
    value: () => {
      const end = new Date();
      const start = new Date();
      start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);
      window.__isshort_console_sw__ = "近7天"
      return [start, end];
    },
  },
  {
    text: "近30天",
    value: () => {
      const end = new Date();
      const start = new Date();
      start.setTime(start.getTime() - 3600 * 1000 * 24 * 30);
      window.__isshort_console_sw__ = "近30天"
      return [start, end];
    },
  },
];

// 服务入口列表
export const menuTitleMap = [
  {
    title: "AI 人效自动化系统",
    key: MODULE_ID.BUSINESS_OBSERVATION,
    iconName: "icon_analyse",
    bg_img_name: "entry_observe",
    text_color: "#200a51",
    deploy: [PROJECT_MAP.OBSERVE_TRACE, PROJECT_MAP.OBSERVE_METRIC, PROJECT_MAP.OBSERVE_LOG]
  },
  {
    title: "CMDB 资源",
    key: MODULE_ID.ENVIRONMENT_CONFIGURATION,
    iconName: "icon_config",
    bg_img_name: "entry_config",
    text_color: "#003278",
    deploy: PROJECT_MAP.OBSERVE_CONFIG
  },
  {
    title: "Zabbix 集中管理",
    key: MODULE_ID.ALARM_MANAGEMENT,
    iconName: "icon_alarm",
    bg_img_name: "entry_alarm",
    text_color: "#004249",
    deploy: PROJECT_MAP.OBSERVE_ALARM
  },
];

// 账号与权限显示信息
export const roleAuthorityMsg = [
  {
    title: '成员',
    type: 'user',
    link: '/usermanage/space/user',
    btn_text: '新增成员'
  },
  {
    title: '角色',
    type: 'role',
    link: '/usermanage/space/role',
    btn_text: '新增角色'
  },
  {
    title: '工作空间',
    type: 'space',
    link: '/usermanage/space',
    btn_text: '管理空间'
  },
]
// 告警统计显示信息
export const AlarmInfo = {
  error: {
    bgImg: alarm_error,
    text: "当前有告警中的任务, 请您及时处理！",
    bgColor: 'red'
  },
  warn: {
    bgImg: alarm_warn,
    text: "您暂时没有告警任务，空间中尚有告警需处理！",
    bgColor: 'yellow'
  },
  safe: {
    bgImg: alarm_safe,
    text: "没有告警任务，您的团队效率很高！",
    bgColor: 'green'
  },
}
