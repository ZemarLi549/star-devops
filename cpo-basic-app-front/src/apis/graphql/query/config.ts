// 查询kibana部署域名
export const queryKibanaUrl = `query queryKibanaUrl {
  services: getKibanaUrl {
    key
    value
  }
}`

// 获取索引配置
export const queryIndexConfig = `query queryIndexConfig {
  services: getIndexConfig {
    key
    value
  }
}`
//查询openId和项目名称信息
export const queryCompaniesConfig = `query queryCompaniesConfig {
  services: getCompaniesConfig {
    key
    value
  }
}`

// 查询Loki部署域名
export const queryLokiUrl = `query queryKibanaUrl {
  services: getLokiUrl {
    key
    value
  }
}`

//查询Loki中服务对应的 datasource 和job
export const queryServiceLokiConfig = `query getLokiConfig ($serviceName:String!){
  services: getLokiConfig(serviceName:$serviceName) {
    datasource
    job
  }
}`