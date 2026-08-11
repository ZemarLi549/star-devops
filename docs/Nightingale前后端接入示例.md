# Nightingale 前后端接入示例

本文给一个完整示例，说明像 `Nightingale` 这种自带前后端的开源系统，如何按“前端微应用 + 后端微服务 + 统一网关 + 统一认证”的方式接入到鑫图平台。

## 1. 先说明结论

`Nightingale` 这类系统接入时，不建议把整套前端静态资源直接塞给 `KrakenD`。

更合理的拆法是：

1. 前端静态资源交给 `Nginx` 或独立前端容器。
2. 后端 API 接到统一网关。
3. 登录能力统一对接 `auth-all`。
4. 权限做分层处理。

## 2. 为什么要拆成两段

原因有两个：

1. `KrakenD` 的职责是 API Gateway，不是前端静态资源托管器。
2. 第三方系统通常会有大量静态资源、路由刷新、前端打包路径、`/api` 约定，这部分用 `Nginx` 处理更稳。

## 3. Nightingale 官方结构参考

截至 2026-08-10，可参考以下官方仓库：

- 后端仓库：`https://github.com/ccfos/nightingale`
- 前端仓库：`https://github.com/n9e/fe`

从官方仓库可以看出：

- 后端是独立项目，可单独部署。
- 前端是独立项目，开发时通过 `vite.config.ts` 将 `/api` 代理到后端。

这意味着它天然就适合拆成“前端单独接入、后端单独接入”的模式。

## 4. 推荐接入架构

### 方案 A：最稳妥

1. Nightingale 后端独立部署。
2. Nightingale 前端打包后由 `Nginx` 托管，例如 `/apps/nightingale/`。
3. Nightingale 前端请求的 `/api` 由 Nginx 反向代理到 Nightingale 后端。
4. 鑫图平台首页只提供一个“服务入口”跳转到 Nightingale。
5. 如需统一入口，可在企业域名层做统一子域或统一反代。

这个方案最适合先跑通系统，不强改第三方代码。

### 方案 B：接入到统一网关路径

1. Nightingale 后端仍独立部署。
2. 在 `KrakenD` 中为 Nightingale 暴露固定 API 路径前缀。
3. Nightingale 前端通过反向代理或构建配置，把 `/api` 指到网关地址。
4. 前端再作为一个微应用入口挂到鑫图平台。

这个方案统一性更好，但会多一层 API 适配。

## 5. 接入步骤

### 第一步：先部署 Nightingale 后端

建议先把 Nightingale 后端独立跑起来，至少保证：

- 能访问健康检查接口或最小查询接口
- 能用本地账号先登录
- 能独立提供监控页面和数据

如果 Nightingale 自身没有你需要的 `/healthz`，可以在反向代理层补一个健康检查路径。

### 第二步：部署 Nightingale 前端

有两种方式：

1. 直接用 Nightingale 官方前端独立部署。
2. 将前端打包产物挂载到你的统一前端静态资源服务中。

建议先独立部署，跑通后再考虑微应用壳接入。

### 第三步：接网关

如果要接入 `KrakenD`，建议只代理 Nightingale 后端 API，不要把前端静态资源也走 `KrakenD`。

示意路径可以是：

- `/api/nightingale/login`
- `/api/nightingale/user`
- `/api/nightingale/targets`
- `/api/nightingale/alerts`

如果 Nightingale API 数量很多，而且路径层级不固定，那么更建议在 `Nginx` 层做完整反代，再把外层统一入口交给企业域名。

原因是 `KrakenD CE` 更适合显式声明 API 路由，不适合拿来兜整个第三方系统的所有前端和后端路径。

### 第四步：统一登录

统一登录有三种做法：

1. 最快方式：保持 Nightingale 自身登录，鑫图平台只提供服务入口。
2. 中间方式：在网关前增加统一登录态校验，没有平台登录态不允许进入 Nightingale。
3. 深度方式：改造 Nightingale 登录流程，对接 `auth-all` 或统一 SSO。

建议顺序：

1. 先保留 Nightingale 自带登录，快速接入。
2. 再做统一 SSO。
3. 最后再做权限映射。

## 6. 权限怎么接

权限不要全压在网关里。

建议分三层：

1. 平台层：
   - 谁能看到 Nightingale 入口
   - 谁能跳转进去
2. 网关层：
   - 哪些来源可以访问 Nightingale API
   - 是否需要统一 Header / Token
3. Nightingale 业务层：
   - 业务组权限
   - 告警规则权限
   - 指标查看权限
   - 用户角色权限

也就是说，网关解决“进门”，业务系统自己解决“进门后能做什么”。

## 7. 如果要做成前端微应用

如果你后续要把 Nightingale 真正接成前端微应用，建议顺序如下：

1. 先保证 Nightingale 前后端独立可用。
2. 再把前端挂到统一门户菜单。
3. 再处理 iframe、子路径部署、静态资源前缀、跨域、登录态共享。
4. 最后才考虑把页面能力拆成平台原生页面。

不要一开始就强行把第三方整套前端改造成平台原生页面，成本高，升级也难。

## 8. 推荐的最小落地方案

如果目标是最快落地，建议按下面做：

1. Nightingale 后端独立部署。
2. Nightingale 前端独立部署。
3. 鑫图平台首页保留一个服务入口跳转。
4. 登录先允许 Nightingale 自己管。
5. 后续再逐步接统一登录和权限。

这样你能先把能力接进平台，再决定哪些地方值得深度改造。

## 9. 这份示例能怎么套用到别的系统

除了 Nightingale，像下面这类完整开源系统也适合套这个思路：

- Zabbix Web
- Grafana
- JumpServer
- SonarQube
- Superset

统一原则都一样：

1. 静态前端和 API 分开看。
2. 网关优先管 API。
3. 登录统一逐步收敛，不要第一天就全量重构第三方鉴权。
