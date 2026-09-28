import {
  createRouter,
  createWebHistory,
  RouteRecordRaw,
  createWebHashHistory,
} from "vue-router";
import { REDIRECT_ROUTE, LOGIN_TYPES } from "@/globals/constant";
// const Control = () => import('@/views/Control.vue')

import usermanageRouter from "./modules/usermanage";
// 路由白名单
export const routingWhiteList: Array<RouteRecordRaw> = [
  // {
  //   path: "/",
  //   redirect: REDIRECT_ROUTE.path,
  // },
  ...usermanageRouter,
  {
    path: "/login",
    name: "login",
    component: () => import("@/views/login/index.vue"),
  },
  {
    path: "/control",
    name: "control",
    component: () => import("@/views/control/index.vue"),
  },
  {
    path: "/control/productivity-config",
    name: "productivity-config",
    redirect: "/ai-productivity?tab=collector",
  },
  {
    path: "/control/ai-productivity",
    name: "ai-productivity",
    redirect: "/ai-productivity",
  },
  {
    path: "/ai-productivity/:pathMatch(.*)*",
    name: "ai-productivity-micro-app",
    component: () => import("@/views/Container.vue"),
  },
  {
    path: "/404",
    name: "error_404",
    component: () => import("@/components/errorPage/error_404.vue"),
    meta: {
      title: "error_404",
    },
  },
  {
    path: "/401",
    name: "error_401",
    component: () => import("@/components/errorPage/error_401.vue"),
    meta: {
      title: "error_401",
    },
  },
];

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  // history: createWebHashHistory(import.meta.env.BASE_URL),
  routes: routingWhiteList,
});

export const redirectNoAuth = () => {
  router.push({
    name: "error_401",
    replace: true,
  });
};

export default router;
