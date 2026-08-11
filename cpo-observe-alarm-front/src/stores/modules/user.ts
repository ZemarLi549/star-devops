import { defineStore } from "pinia"
import { reactive } from "vue"
import { cookie } from "@/utils/cookie"
import actions from "@/plugin/qiankun/action";
import axios from 'axios'

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
    guideState:'',
  }
  const setUserInfo = (value?: any) => {
    value = value || defaultUserInfo;
    Object.assign(userInfo, value);
    cookie.set(SEESION_KEY, userInfo);
  }

  const getUserInfoFromCookie = () => {
    const cookies = cookie.get(SEESION_KEY)
    return cookies ? cookies : actions.setGlobalState({ isBackLogin:true })
  }

  const userInfo = reactive(cookie.get(SEESION_KEY) || { ...defaultUserInfo });

  const fatherInfo = JSON.parse(localStorage.getItem('userinfo') || null)
  fatherInfo?.value && setUserInfo(fatherInfo.value)

  const refreshToken = async () => {
    const { refresh_token } = userInfo;
    try {
      const res = await axios.post('/auth/refreshToken', { refreshToken: refresh_token });
      const { token, refreshToken, tokenExpireTime } = res.data?.data
      const data = {
        token,
        refresh_token: refreshToken,
        expire_time: Date.now() + tokenExpireTime
      }
      setUserInfo(data);
      return token;
    } catch (error) {
      setUserInfo();
      cookie.clear();
      actions.setGlobalState({ isBackLogin:true });
    }
  }



  return {
    userInfo,
    setUserInfo,
    refreshToken,
    getUserInfoFromCookie
  };
});
