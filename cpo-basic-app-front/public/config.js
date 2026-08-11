window.__config__ = {
  // 线上生产环境
    "production": {
        "VITE_DOC_URL": "http://127.0.0.1/sop_doc/", // 部署文档中心地址
        "VITE_LOGIN_CONFIG": {
            "mode": "normal", // "normal": 本地登陆 / "sso": sso登陆 / "uap": uap登陆
            "sso_url": "https://ssotest.iflytek.com/sso",
            "uap_url": "http://127.0.0.1:8380/uap-server",
            "app_code": "yy_1704868151449",
            "client_platform": "tenant"
        },
  },
  // 本地开发环境
    "development": {
        "VITE_DOC_URL": "http://127.0.0.1/sop_doc/",
        "VITE_LOGIN_CONFIG": {
            "mode": "normal",
            "sso_url": "https://ssotest.iflytek.com/sso",
            "uap_url": "http://127.0.0.1:8380/uap-server",
            "app_code": "yy_1715594922068",
            "client_platform": "tenant"
        },
    },
}