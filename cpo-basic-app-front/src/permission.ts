import router from "./router"
import { useUserStore } from "@/stores/modules/user"
import { LOGIN_TYPES, microAppsDataAll } from '@/globals/constant'
import { cookie } from "@/utils/cookie"
import { useConfigStore } from "@/stores/modules/microConfig";
import Nprogress from "nprogress";
import { redirectLogin } from "@/plugin/qiankun/action"
import { microAppLoad } from '@/plugin/qiankun/micro-app'

const getUrlKey = (query: string) => {
  const url = new URL(window.location.href);
  return url.searchParams.get(query);
}
const deleteUrlKey = (query: string) => {
  const url = new URL(window.location.href);
  url.searchParams.delete(query);
  return url.toString();
}

const hasAuth = (fullPath) => {
  const pathArr = ['/control']
  getPath(cookie.get('userinfo').menus, pathArr)
  for (let i = 0; i < pathArr.length; i++) {
    // 去除hash和query参数
    if (pathArr[i].indexOf(fullPath.split('#')[0].split('?')[0]) > -1) {
      return true
    }
  }
  return false
}

const getPath = (obj, arr) => {
  if (obj.length) {
    obj.forEach((item) => {
      if (item.menuPath) { arr.push(item.menuPath) }
      if (item.children) { getPath(item.children, arr) }
    })
  }
}

router.beforeEach(async (to, from) => {
  const { loginConfig } = useConfigStore();
  Nprogress.start();
  if (['/401', '/404', '/500'].includes(to.path)) return true;

  // 本地登录放行login
  if (to.path === '/login') {
    if (loginConfig.mode === LOGIN_TYPES.NORMAL) return true;
    else return { path: "/404" };
  }

  const { userInfo, setActiveMenu, singleLogin, hasPathAuth, menuList } = useUserStore();
  
  const ticket = getUrlKey('ticket')
  const newHref = window.location.origin
  if (ticket && loginConfig.mode !== LOGIN_TYPES.NORMAL) { // 单点登录验证ticket
    const serverUrl = loginConfig.mode === LOGIN_TYPES.SSO ? loginConfig.sso_url : loginConfig.uap_url
    // todo 异常捕获
    try {
      await singleLogin(ticket, serverUrl, newHref)
    } catch (e) {
      // ElMessage.error(e);
    } finally {
      window.location.href = newHref;
      return
    }
  } else if (!userInfo.token) { // 未登录
    redirectLogin()
    return;
  }
  
  if (!userInfo?.workspace_id) {
    window.location.href = newHref + '/401';
    return
    // return { name: 'error_401' }
  }

  // 微前端加载
  await microAppLoad()

  //独立部署跳转至路由首页
  if (to.path === '/') {
    // 触发重定向
    window.location.href = newHref + menuList[0];
    return
  }

  // 全屏放行
  if (to.fullPath.includes('isfullscreen')) {
    return true;
  }
  // 判断加载的路径是否是子应用的
  const isLoadMicroApp = microAppsDataAll.find(item => item.activeRule.includes(to.fullPath.split('/').splice(0, 3).join('/')))
  if (!isLoadMicroApp) {
    if (!hasPathAuth(to.fullPath, true)) {
      return { name: 'error_404' }
    }
  }
  setActiveMenu(to.path);
  return true;
});

router.afterEach((to) => {
  const { setMajorPathName } = useUserStore();
  setMajorPathName(to.path);
  // 路由加载完成后关闭进度条
  document.getElementsByClassName("main-cont main-right")[0]?.scrollTo(0, 0);
  Nprogress.done();
});

router.onError(() => {
  // 路由发生错误后销毁进度条
  Nprogress.remove();
});

