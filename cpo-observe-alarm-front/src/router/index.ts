import { createRouter,createWebHistory } from "vue-router";
import actions from "@/plugin/qiankun/action";

const layout = () => import("@/components/layout/Layout.vue");
const assignPolice = () => import("@/views/assign-police/AssignPolice.vue");
const newAssignPolice = () =>
  import("@/views/assign-police/NewAssignPolice.vue");
const shieldRule = () => import("@/views/shield-rule/ShieldRule.vue");
const newShieldRule = () => import("@/views/shield-rule/NewShieldRule.vue");
const pushPolice = () => import("@/views/push-police/PushPolice.vue");
const newPushPolice = () => import("@/views/push-police/NewPushPolice.vue");

const noticePolice = () => import("@/views/notice-police/index.vue");
const addNoticePolice = () =>
  import("@/views/notice-police/AddNoticePolice.vue");
const noticeChannel = () => import("@/views/notice-channel/index.vue");
const noticeGroup = () => import("@/views/notice-group/index.vue");

const monitorSys = () => import("@/views/monitor-system/index.vue");
const selfAlarm = () => import("@/views/management/self-alarm/index.vue");
const allAlarm = () => import("@/views/management/all-alarm/index.vue");
const unassignAlarm = () =>
  import("@/views/management/unassign-alarm/index.vue");
const blockAlarm = () => import("@/views/management/block-alarm/index.vue");
const allNotify = () => import("@/views/management/all-notify/index.vue");

const overviewLayout = () => import("@/views/alarm-overview/components/Layout.vue")
const alarmOverview = () => import("@/views/alarm-overview/index.vue")

const customLabel = () => import("@/views/custom-label/CustomLabel.vue")
const newCustomLabel = () => import("@/views/custom-label/NewCustomLabel.vue")
const routes = [
  {
    path: "/",
    component: layout,
    children: [
      // 分派策略
      {
        path: "/assign-police",
        component: assignPolice,
        name: "assignPolice",
        meta: {
          breadcrumb: [{ title: "分派策略" }],
        },
      },
      {
        path: "/assign-police/create",
        component: newAssignPolice,
        name: "newAssignPolice",
        meta: {
          breadcrumb: [
            { title: "分派策略", path: "/assign-police" },
            { title: "新建分派策略" },
          ],
        },
      },
      {
        path: "/assign-police/edit/:id",
        component: newAssignPolice,
        name: "editAssignPolice",
        meta: {
          breadcrumb: [
            { title: "分派策略", path: "/assign-police" },
            { title: "编辑分派策略" },
          ],
        },
      },
      {
        path: "/assign-police/clone/:id",
        component: newAssignPolice,
        name: "cloneAssignPolice",
        meta: {
          breadcrumb: [
            { title: "分派策略", path: "/assign-police" },
            { title: "克隆分派策略" },
          ],
        },
      },
      {
        path: "/assign-police/view/:id",
        component: newAssignPolice,
        name: "viewAssignPolice",
        meta: {
          breadcrumb: [
            { title: "分派策略", path: "/assign-police" },
            { title: "查看分派策略" },
          ],
        },
      },
      // 屏蔽规则
      {
        path: "/shield-rule",
        component: shieldRule,
        name: "shieldRule",
        meta: {
          breadcrumb: [{ title: "屏蔽规则" }],
        },
      },
      {
        path: "/shield-rule/create",
        component: newShieldRule,
        name: "newShieldRule",
        meta: {
          breadcrumb: [
            { title: "屏蔽规则", path: "/shield-rule" },
            { title: "新建屏蔽规则" },
          ],
        },
      },
      {
        path: "/shield-rule/edit/:id",
        component: newShieldRule,
        name: "editShieldRule",
        meta: {
          breadcrumb: [
            { title: "屏蔽规则", path: "/shield-rule" },
            { title: "编辑屏蔽规则" },
          ],
        },
      },
      {
        path: "/shield-rule/clone/:id",
        component: newShieldRule,
        name: "cloneShieldRule",
        meta: {
          breadcrumb: [
            { title: "屏蔽规则", path: "/shield-rule" },
            { title: "克隆屏蔽规则" },
          ],
        },
      },
      {
        path: "/shield-rule/views/:id",
        component: newShieldRule,
        name: "viewShieldRule",
        meta: {
          breadcrumb: [
            { title: "屏蔽规则", path: "/shield-rule" },
            { title: "查看屏蔽规则" },
          ],
        },
      },
      // 推送策略
      {
        path: "/push-police",
        component: pushPolice,
        name: "pushPolice",
        meta: {
          breadcrumb: [{ title: "推送策略" }],
        },
      },
      {
        path: "/push-police/create",
        component: newPushPolice,
        name: "newPushPolice",
        meta: {
          breadcrumb: [
            { title: "推送策略", path: "/push-police" },
            { title: "新建推送策略" },
          ],
        },
      },
      {
        path: "/push-police/edit/:id",
        component: newPushPolice,
        name: "editPushPolice",
        meta: {
          breadcrumb: [
            { title: "推送策略", path: "/push-police" },
            { title: "编辑推送策略" },
          ],
        },
      },
      {
        path: "/push-police/clone/:id",
        component: newPushPolice,
        name: "clonePushPolice",
        meta: {
          breadcrumb: [
            { title: "推送策略", path: "/push-police" },
            { title: "克隆推送策略" },
          ],
        },
      },
      {
        path: "/push-police/views/:id",
        component: newPushPolice,
        name: "viewPushPolice",
        meta: {
          breadcrumb: [
            { title: "推送策略", path: "/push-police" },
            { title: "查看推送策略" },
          ],
        },
      },
      // 通知策略
      {
        path: "/notice-police",
        component: noticePolice,
        name: "noticePolice",
        meta: {
          breadcrumb: [{ title: "通知策略" }],
        },
      },
      {
        path: "/notice-police/create",
        component: addNoticePolice,
        name: "addNoticePolice",
        meta: {
          breadcrumb: [
            { title: "通知策略", path: "/notice-police" },
            { title: "新建通知策略" },
          ],
        },
      },
      {
        path: "/notice-police/detail/:id",
        component: addNoticePolice,
        name: "detailNoticePolice",
        meta: {
          breadcrumb: [
            { title: "通知策略", path: "/notice-police" },
            { title: "通知策略详情" },
          ],
        },
      },
      {
        path: "/notice-police/edit/:id",
        component: addNoticePolice,
        name: "editNoticePolice",
        meta: {
          breadcrumb: [
            { title: "通知策略", path: "/notice-police" },
            { title: "编辑通知策略" },
          ],
        },
      },
      {
        path: "/notice-police/clone/:id",
        component: addNoticePolice,
        name: "cloneNoticePolice",
        meta: {
          breadcrumb: [
            { title: "通知策略", path: "/notice-police" },
            { title: "克隆通知策略" },
          ],
        },
      },
      {
        path: "/notice-channel",
        component: noticeChannel,
        name: "noticeChannel",
        meta: {
          breadcrumb: [{ title: "通知方式" }],
        },
      },
      {
        path: "/notice-group",
        component: noticeGroup,
        meta: {
          breadcrumb: [{ title: "通知组" }],
        },
      },
      {
        path: "/monitor-sys",
        component: monitorSys,
        meta: {
          breadcrumb: [{ title: "监控系统" }],
        },
      },
      // 告警管理
      {
        path: "/self-alarm",
        component: selfAlarm,
        name: "selfAlarm",
        meta: {
          breadcrumb: [{ title: "我的告警" }],
        },
      },
      {
        path: "/all-alarm",
        component: allAlarm,
        name: "allAlarm",
        meta: {
          breadcrumb: [{ title: "所有告警" }],
        },
      },
      {
        path: "/not-assign-alarm",
        component: unassignAlarm,
        name: "unassignAlarm",
        meta: {
          breadcrumb: [{ title: "未分派告警" }],
        },
      },
      {
        path: "/block-alarm",
        component: blockAlarm,
        name: "blockAlarm",
        meta: {
          breadcrumb: [{ title: "屏蔽告警" }],
        },
      },
      {
        path: "/all-notify",
        component: allNotify,
        name: "allNotify",
        meta: {
          breadcrumb: [{ title: "所有通知" }],
        },
      },

      // 自定义标签
      {
        path: "/custom-label",
        component: customLabel,
        name: "customLabel",
        meta: {
          breadcrumb: [{ title: "自定义标签" }],
        },
      },
      {
        path: "/custom-label/create",
        component: newCustomLabel,
        name: "newCustomLabel",
        meta: {
          breadcrumb: [
            { title: "自定义标签", path: "/custom-label" },
            { title: "新建标签" },
          ],
        },
      },
      {
        path: "/custom-label/edit/:labelId",
        component: newCustomLabel,
        name: "editCustomLabel",
        meta: {
          breadcrumb: [
            { title: "自定义标签", path: "/custom-label" },
            { title: "编辑标签" },
          ],
        },
      },
    ],
  },
  {
    path: '/alarm-overview',
    component: overviewLayout,
    children: [
      {
        // 告警概览
        path: "index",
        component: alarmOverview,
        name: "alarmOverview",
        meta: {
          breadcrumb: [{ title: "告警概览" }]
        }
      },
    ]
  },
  {
    path: '/:pathMatch(.*)',
    component: null,
    name: 'errorPage',
    children: []
  }
  // {
  //   path: '/test',
  //   component: layout,
  //   children: [
  //     {
  //       path: 'alarm/',
  //       component: TestAlarm,
  //       name: 'alarm',
  //       meta: {
  //         title: '告警',
  //         nav: 'alarm'
  //       }
  //     }
  //   ]
  // }
];
const router = createRouter({
  history: createWebHistory('/alarm-center/observe-alarm'),
  routes,
});

// to, from, next
router.beforeEach((to, from, next) => {
  if (to.path === "/") {
    next({ path: "/alarm-overview/index" });
    return
  }
  if (to.name === "errorPage") {
    actions.setGlobalState({ noRouteAuth: true });
    return
  }

  // if (actions.hasPathAuth(window.location.pathname)) {
  if (actions.hasPathAuth('/alarm-center/observe-alarm' + to.path)) {
    next()
  } else {
    actions.setGlobalState({ noRouteAuth: true });
  }
});

export default router;
