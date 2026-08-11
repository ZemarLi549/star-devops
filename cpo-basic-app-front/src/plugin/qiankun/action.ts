import { initGlobalState } from "qiankun"
import { useConfigStore } from "@/stores/modules/microConfig";
import {  LOGIN_TYPES } from '@/globals/constant'
import { sleep } from "@/utils/sleep";
import { ElMessage } from "element-plus";
import router from '@/router'
import { cookie } from "@/utils/cookie"

// 初始化需要传递的对象
export const action = initGlobalState({
  isBackLogin: false,
  noRouteAuth: false,
})

// 监听actions全局公共状态数据的变化
action.onGlobalStateChange((state, prevState) => {
  if (state.isBackLogin) {
    redirectLogout(true)
  }
  if (state.noRouteAuth) {    
    action.setGlobalState({
      noRouteAuth: false
    })
    // router.push({
    //   name: 'error_404',
    // })
    history.pushState({},'','/404')
  }
})

// 返回登录页面
export const redirectLogout = async (delay = false) => {
  action.setGlobalState({
    isBackLogin:false
  })
  const { loginConfig } = useConfigStore();
  cookie.clear()
  switch(loginConfig.mode) {
    case(LOGIN_TYPES.NORMAL):
    // router.push({ 
    //   name: 'login', 
    //   replace: true
    // })
    window.location.replace(`${window.location.origin}/login`)
      break
    case(LOGIN_TYPES.SSO):
      if (delay) {
        ElMessage.info('即将跳转至登录页面');
        await sleep(2000)
      }
      window.location.replace(`${loginConfig.sso_url}/logout?service=${window.location.origin}`)
      break
    case(LOGIN_TYPES.UAP):
      if (delay) {
        ElMessage.info('即将跳转至登录页面');
        await sleep(2000)
      }
      const { uap_url, app_code, client_platform } = loginConfig
      window.location.replace(`${uap_url}/logout?service=${window.location.origin}&at=${client_platform}&appCode=${app_code}`)
      break
  }
}
export const redirectLogin = () => {  
  const { loginConfig } = useConfigStore();
  switch(loginConfig.mode) {
    case(LOGIN_TYPES.NORMAL):
    // router.push({ 
    //   name: 'login', 
    //   replace: true
    // })
    window.location.replace(`${window.location.origin}/login`)
      break
    case (LOGIN_TYPES.SSO):
      window.location.replace(`${loginConfig.sso_url}/login?service=${window.location.origin}`)
      break
    case(LOGIN_TYPES.UAP):
      const { uap_url, app_code, client_platform } = loginConfig
      window.location.replace(`${uap_url}/login?service=${window.location.origin}&at=${client_platform}&appCode=${app_code}`)
      break
  }
}