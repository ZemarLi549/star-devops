enum ApiUrl {
  login = '/auth/login',
  loginMode = '/auth/loginMode',
  singleLogin = '/auth/loginInfo',
  logout = '/auth/logout',
  authorization = '/auth/authentication/authorization',
  captcha = '/auth/captcha',
  refreshToken = '/auth/refreshToken',
  menu = '/auth/menu',
  workspace = '/auth/workspace',
  getUserInfo = '/auth/userBaseInfo', // 获取用户信息
  guideState = '/auth/guideState', // 新手指引
  validateIntegrality = '/auth/validateIntegrality', // 数据完整性校验（利川用）
}

export default ApiUrl
