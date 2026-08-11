/*
 * @Author: zhewu4 
 * @Date: 2023-11-28 16:00:34 
 * @Last Modified by: zhewu4
 * @Last Modified time: 2023-11-29 16:20:27
 */
import Request from '@/config/axios/Request'
import ApiUrl from './ApiUrl'

interface iCaptcha {
	backgroundImage: string; // 背景图
	sliderImage: string; // 移动图
	backgroundImageWidth: number; // 背景图片宽度
	backgroundImageHeight: number; // 背景图片高度
	sliderImageWidth: number; // 滑动图片宽度
	sliderImageHeight: number; // 滑动图片高度
}

export interface IVeriCode {
	id: string;
	captcha: iCaptcha;
}

export interface IVeriCodeRes {
	data: IVeriCode;
}

type LoginForm = {
  account: string;
  password: string;
  loginMode: 'LOCAL' | 'LDAP';
  captchaId: string;
  captchaTrack: any;
}

type LoginModeConfig = {
  mode: 'LOCAL' | 'LDAP' | 'MIXED';
  options: Array<'LOCAL' | 'LDAP'>;
  captchaEnabled: boolean;
}

type TokenInfo = {
  token: string;
  refreshToken: string;
  tokenExpireTime: number;
}

type LoginRes = {
  firstLogin: boolean;
  userId: string;
  tokenInfo: TokenInfo
}

type WorkspaceRes = {
  workSpaceId: string;
  workSpaceName: string;
  roleName: string;
  userCount: number;
  roleCount: number;
}[]

type UserInfoRes = {
  userId: string;
  account: string;
  passwd: string;
  nickName: string;
  email: string;
  headImg: string;
  phone: string;
  isactive: boolean;
  isloggedin: boolean;
  guideState?: string;
}

class userApi extends Request {
  login(data: LoginForm) {
    return this.request<LoginRes>({
      url: ApiUrl.login,
      method: 'POST',
      globalLoading: false,
      data
    })
  }
  loginMode() {
    return this.request<LoginModeConfig>({
      url: ApiUrl.loginMode,
      method: 'GET',
      globalLoading: false
    })
  }
  singleLogin(data) {
    return this.request<any>({
      url: ApiUrl.singleLogin,
      method: 'POST',
      globalLoading: false,
      data
    })
  }
  // getUserInfo() {
  //   return this.request({
  //     url: ApiUrl.getUserInfo,
  //     method: 'GET',
  //     globalLoading: false
  //   })
  // }
  captcha() {
    return this.request<IVeriCode>({
      url: ApiUrl.captcha,
      method: 'GET',
      globalLoading: false
    })
  }

  logout(uapUserId:string, appCode:string) {
    const url = uapUserId ? `${ApiUrl.logout}/?userId=${uapUserId}&appCode=${appCode}` : ApiUrl.logout
    return this.request({
      url,
      method: 'post',
      globalLoading: false
    });
  }

  refreshToken(refreshToken: string) {
    return this.request<TokenInfo>({
      url: ApiUrl.refreshToken,
      method: 'post',
      data: { refreshToken }
    })
  }

  menu(workSpaceId: string) {
    return this.request({
      url: ApiUrl.menu,
      method: 'get',
      headers: {
        'Workspace-Id': workSpaceId
      }
    })
  }
  workspace() {
    return this.request<WorkspaceRes>({
      url: ApiUrl.workspace,
      method: 'get'
    })
  }

  getUserInfo() {
    return this.request<UserInfoRes>({
      url: ApiUrl.getUserInfo,
      method: 'get'
    })
  }
  guideState(data) {
    return this.request({
      url: ApiUrl.guideState,
      method: 'post',
      data
    })
  }
  validateIntegrality() {
    return this.request({
      url: ApiUrl.validateIntegrality,
      method: 'get',
    })
  }
}

export default new userApi()
