/*
 * @Author: zhewu4 
 * @Date: 2023-11-28 15:56:32 
 * @Last Modified by: zhewu4
 * @Last Modified time: 2023-11-29 18:27:01
 */
import { defineStore } from "pinia"
import { ElLoading } from "element-plus"
import { ref, watch, reactive } from "vue"
import { cookie } from "@/utils/cookie"
import { action } from "@/plugin/qiankun/action"
import userApi from "@/apis/user"
import md5 from 'crypto-js/md5'
import { ERROR_CODE } from "@/config/axios/statusCode"
import { menuTitleMap } from "../../menu"
import { LOGIN_TYPES } from '@/globals/constant'
import { useConfigStore } from "@/stores/modules/microConfig";
import { redirectLogout } from "@/plugin/qiankun/action"
import { MODULE_ID } from "@/globals/constant";

export const useUserStore = defineStore("user", () => {

  const SEESION_KEY = 'userinfo'
  const defaultUserInfo = {
    user_id: '',
    user_name: '',
    phone: '',
    token: '',
    refresh_token: '',
    expire_time: '',
    menus: [],
    workspace_id: '',
    workSpaceList: [],
    nickName: '',
    guideState: '',
  }

  const userInfo = reactive(cookie.get(SEESION_KEY) || { ...defaultUserInfo });

  const setUserInfo = (value?: any) => {
    value = value || defaultUserInfo;
    Object.assign(userInfo, value);
    cookie.set(SEESION_KEY, userInfo);
    action.setGlobalState({ userInfo });
    setMenuList(userInfo.menus)
  }

  const getUserInfoFromCookie = () => {
    return cookie.get(SEESION_KEY) || {};
  }

  const login = async (username, password, verifyInfo = {}, loginMode = 'LOCAL') => {
    // console.log("登录中", username, password);
    const loadingInstance = ref(null);
    try {

      loadingInstance.value = ElLoading.service({
        fullscreen: true,
        text: "登录中，请稍候...",
      });
      const loginResult = await userApi.login({
        account: username,
        password: loginMode === 'LDAP' ? password : md5(password).toString(),
        loginMode,
        // 移除验证码相关参数
      });
      
      // 直接使用loginResult中的数据，因为responseInterceptors已经处理了成功/失败的情况
      const { tokenInfo, userId, firstLogin } = loginResult;

      const userInfo = {
        user_id: userId,
        user_name: username,
        token: tokenInfo.token,
        refresh_token: tokenInfo.refreshToken,
        expire_time: Date.now() + tokenInfo.tokenExpireTime,
        firstLogin: firstLogin
      }

      setUserInfo(userInfo);
      // 更新用户信息
      await getWorkspaceList();
      await getUserInfo();
      await getMenus();

    } catch (error) {
      return Promise.reject(error);
    } finally {
      loadingInstance.value.close();
    }
  };

  // 单点登录
  const singleLogin = async (ticket, serverUrl, clientUrl) => {
    try {
      let { tokenInfo, userId, firstLogin, uapUserId } = await userApi.singleLogin({ ticket, serverUrl, clientUrl });
      const userInfo = {
        uap_user_id: uapUserId,
        user_id: userId,
        user_name: '',
        token: tokenInfo.token,
        refresh_token: tokenInfo.refreshToken,
        expire_time: Date.now() + tokenInfo.tokenExpireTime,
        firstLogin: firstLogin
      }

      setUserInfo(userInfo);

      // 更新用户信息
      await getWorkspaceList();
      await getUserInfo();
      await getMenus();

    } catch (error) {
      return Promise.reject(error);
    }
  };

  const logout = async () => {
    const { loginConfig, resetDepolyConfig } = useConfigStore();
    await userApi.logout(userInfo.uap_user_id, loginConfig.app_code);
    setUserInfo();
    cookie.clear();
    resetDepolyConfig()
    redirectLogout()
  };

  const refreshToken = async () => {
    const { refresh_token } = userInfo;
    try {
      const { token, refreshToken, tokenExpireTime } = await userApi.refreshToken(refresh_token);
      const data = {
        token,
        refresh_token: refreshToken,
        expire_time: Date.now() + tokenExpireTime
      }
      setUserInfo(data);
      return token;
    } catch (error) {
      if (![
        ERROR_CODE.REFRESH_TOKEN_EXCEED_TIME
      ].includes(error.errorCode)) return Promise.reject(error);
      setUserInfo();
      cookie.clear();
      redirectLogout()
    }
  }

  // 独立部署菜单处理
  const menusHandler = (menus) => {
    const menusTemplate = [{
      module: MODULE_ID.SINGLE_MENU,
      menuId: MODULE_ID.SINGLE_MENU,
      menuName: "独立部署",
      parentId: "0",
      menuPath: null,
      icon: null,
      isgroup: false,
      ismenu: false,
      isoutlink: false,
      sortNum: null,
      children: []
    }, {
      module: MODULE_ID.RESOURCE_CONTROL,
      menuId: MODULE_ID.RESOURCE_CONTROL,
      menuName: "权限管理",
      parentId: "0",
      menuPath: null,
      icon: null,
      isgroup: false,
      ismenu: false,
      isoutlink: false,
      sortNum: null,
      children: []
    }]
    // 菜单处理
    menus.forEach(menu => {
      if (menu.menuPath === '/usermanage/space' || menu.menuPath === '/usermanage/pool' || menu.menuPath === '/usermanage/menu') {
        menusTemplate[1].children.push(menu)
      } else {
        menusTemplate[0].children.push(menu)
      }
    })
    return menusTemplate;
  };

  const getMenus = async () => {

    const { workspace_id } = userInfo;
    if (workspace_id === undefined) return;
    try {
      const menus = await userApi.menu(workspace_id);

      const configStore = useConfigStore()
      // 如果独立部署
      if (configStore.singleDepoly) {
        setUserInfo({
          menus: menusHandler(menus)
        });
        return
      }
      setUserInfo({ menus });
    } catch (error) {
      console.log(error);
    }
  }

  const currentMenu = ref();
  const majorPathName = ref('');

  const setActiveMenu = (path) => {
    setMajorPathName(path);
    currentMenu.value = {};
    const { menus } = userInfo;
    if (!menus) return;
    const whiteList = ['login'];
    if (whiteList.includes(majorPathName.value)) return;
    const mapItem = menuTitleMap.find(item => item.key === majorPathName.value);
    // 如果独立部署
    const configStore = useConfigStore()
    if (configStore.singleDepoly && mapItem.moduleId !== MODULE_ID.RESOURCE_CONTROL) {
      const menu = menus.find(item => {
        return item.module === MODULE_ID.SINGLE_MENU
      })
      if (!menu) return;
      currentMenu.value = { moduleId: MODULE_ID.SINGLE_MENU, menuChild: menu.children };

      return
    }

    if (!mapItem) return;
    const moduleId = mapItem.moduleId;
    const menu = menus.find(item => item.module === moduleId)
    if (!menu) return;
    document.title = `${mapItem.title} | 鑫图平台`
    currentMenu.value = { ...mapItem, menuChild: menu.children };
  }

  const setMajorPathName = (path) => {
    majorPathName.value = path.split("/")[1];
  }

  const getWorkspaceList = async () => {
    try {
      let data = await userApi.workspace() || [];
      data = data.map(item => {
        return {
          ...item,
          workSpaceId: item?.workSpaceId.toString()
        }
      })
      const currentWorkspaceId = userInfo.workspace_id;
      const workspace_id = currentWorkspaceId || data?.at(0)?.workSpaceId;
      setUserInfo({
        workSpaceList: data,
        workspace_id
      });
    } catch (error) {
      return Promise.reject(error);
    }
  }

  const updateWorkspaceId = async () => {
    try {
      let data = await userApi.workspace() || [];
      // 将空间ID转化为字符串，解决权限管理切换空间后ID对不上的问题
      data = data.map(item => {
        return {
          ...item,
          workSpaceId: item?.workSpaceId.toString()
        }
      })
      setUserInfo({
        workSpaceList: data,
      });
      const { workspace_id, workSpaceList } = userInfo;
      if (!workSpaceList.find((item) => item.workSpaceId === workspace_id)) {
        setUserInfo({ workspace_id: workSpaceList[0]?.workSpaceId })
      }
    } catch (error) {
      return Promise.reject(error);
    }
  }

  const getUserInfo = async () => {
    try {
      const data = await userApi.getUserInfo();
      const info = {
        nickName: data.nickName,
        guideState: data.guideState,
      }
      setUserInfo(info);
    } catch (error) {
      return Promise.reject(error);
    }
  }

  const menuList = ref([])
  /**
 * 这是一个判断路径是否具有菜单权限的函数。
 *
 * @param {String} path - 需要判断的路径。
 * @param {Boolean} isBaseApp - 是否是基座应用，默认为false。
 * @return {Boolean} 如果路径具有权限，返回true；否则返回false。
 */
  const hasPathAuth = (path, isBaseApp = false) => {
    /**
     * 如果是基座应用，则全匹配
     * 如果是子应用，则通过includes判断，因为存在详情页
     */
    return !!menuList.value.find(menu => isBaseApp ? path == menu : path.includes(menu))
  }
  const setMenuList = (menus) => {
    const dfsMenu = (menus, path = []) => {
      if (menus && menus.length) {
        menus.forEach((item) => {
          if (item.ismenu && item.menuPath) {
            path.push(item.menuPath);
          }
          if (item.children) {
            path.push(...dfsMenu(item.children, []));
          }
        });
      }
      return path;
    };
    menuList.value = dfsMenu(menus)
  }
  setMenuList(userInfo.menus)
  return {
    userInfo,
    login,
    logout,
    refreshToken,
    getUserInfo,
    getMenus,
    setUserInfo,
    getUserInfoFromCookie,
    currentMenu,
    majorPathName,
    setActiveMenu,
    setMajorPathName,
    hasPathAuth,
    singleLogin,
    getWorkspaceList,
    updateWorkspaceId,
    menuList,
    menusHandler,
  };
});
