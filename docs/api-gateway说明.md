# api-gateway 说明

## 它是干嘛的

`api-gateway` 就是所有后端服务前面的统一入口。

前端、飞书采集插件、AstrBot 调用方不直接访问多个后端，而是统一访问网关，再由网关按路径转发到具体服务。

## 解决的问题

### 1. 统一入口

只暴露一个网关地址，例如：

```text
https://api.xintu.local
```

而不是让前端分别记住：

- `auth-all`
- `cpo-ai-productivity-service`
- `AstrBot`
- 未来的 CMDB / Zabbix 聚合服务

### 2. 统一鉴权

登录 token、LDAP、SSO、本地账号、采集插件上报 token 都可以先经过网关校验。

### 3. 统一治理

网关最适合统一做：

- 超时
- 限流
- 熔断
- 重试
- 灰度
- 黑白名单
- 访问审计

### 4. 统一安全

跨域、Header 白名单、下载鉴权、插件签名校验、IP 限制都可以集中做。

## 当前项目里的建议路由

- `/auth/**` -> `auth-all`
- `/api/productivity/**` -> `cpo-ai-productivity-service`
- `/api/astrbot/**` -> `AstrBot`

## 当前选型

- 产品：`KrakenD Community Edition`
- 当前版本口径：`2.13.8`
- 运行方式：官方 Docker 镜像 / 官方二进制包
- 配置方式：声明式 `krakend.json`

项目目录：

```text
cpo-api-gateway/
```

当前已经接入：

- 路由转发
- 端点级限流
- 后端熔断
- 统一错误体
- Docker 化部署
- 官方 `krakend check` 配置校验
- 官方二进制部署脚本

## 当前技术选型

- 网关：`KrakenD CE`
- AI 人效后端：`Python 3 + Tornado`
- 飞书采集插件：`Go`

这样分工的原因：

- 网关更看重并发、连接转发和稳定性，适合直接用成熟的开源网关。
- 业务聚合服务更看重快速迭代，适合 Python。
- 客户端采集器需要跨平台交付，适合 Go。

## 为什么你这个项目需要它

因为你后面一定会有多服务：

- 登录认证
- AI 人效自动化系统
- 飞书采集插件上报
- AstrBot
- CMDB / Zabbix 聚合

如果没有网关，前端和采集器会直接耦合多个服务地址，后面扩展会越来越乱。

## 新子系统接入时要做什么

接入不会变复杂，复杂度会集中在统一规约里：

1. 后端先提供 `/healthz`。
2. 后端业务接口统一规划前缀，再挂到 `cpo-api-gateway/krakend.json`。
3. 前端只请求网关地址，不直接写后端内网地址。
4. 登录继续统一走 `auth-all`。
5. 网关负责路由、限流、熔断、Header 透传。
6. 业务系统自己负责页面、按钮、数据粒度权限。

## Docker 和二进制两种部署

### Docker 版

适合本地联调和容器化环境：

```bash
bash scripts_dir/start-cpo-api-gateway.sh
```

### 二进制版

适合服务器直装和轻量部署：

```bash
bash scripts_dir/install-krakend-binary.sh
bash scripts_dir/start-cpo-api-gateway-binary.sh
```

KrakenD 官方同时提供 Docker 镜像和 Linux generic 二进制发行包，所以这两种方式都可以长期保留。

## 第三方完整系统接入建议

像 `Nightingale` 这种自带前后端的完整开源系统，建议拆成两段接入：

1. 前端静态资源由 `Nginx` 或前端微应用容器承接。
2. 后端 API 再通过 `KrakenD` 或 Nginx 反向代理接到统一入口。

原因很简单：`KrakenD` 更适合 API 网关，不适合承接整套第三方前端静态站点和任意静态资源分发。
