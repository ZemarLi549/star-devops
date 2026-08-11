# cpo-api-gateway

`cpo-api-gateway` 是鑫图平台当前规划中的统一 API 网关服务，当前主实现采用 `KrakenD Community Edition`。

## 当前选型

- 实现：`KrakenD CE`
- 运行方式：官方 Docker 镜像 / 官方二进制包
- 配置方式：声明式 `krakend.json`
- 管理方式：`scripts_dir/start-cpo-api-gateway.sh` / `scripts_dir/start-cpo-api-gateway-binary.sh`

## 作用

- 为前端微应用、飞书采集插件、AstrBot、后续微服务提供统一入口。
- 按路径将请求转发到 `auth-all`、`cpo-ai-productivity-service`、AstrBot 等后端。
- 统一承接路由、限流、超时、熔断和错误处理。
- 让业务服务不直接暴露给前端。

## 当前路由

- `/auth/{resource}`
- `/auth/{resource}/{id}`
- `/auth/{resource}/{id}/{action}`
- `/api/productivity/{module}/{scope}`
- `/api/productivity/{module}/{scope}/{action}`
- `/api/astrbot/{module}`
- `/api/astrbot/{module}/{action}`

## 本地启动

Docker 版：

```bash
bash scripts_dir/start-cpo-api-gateway.sh
```

二进制版：

```bash
bash scripts_dir/install-krakend-binary.sh
bash scripts_dir/start-cpo-api-gateway-binary.sh
```

停止：

```bash
bash scripts_dir/stop-cpo-api-gateway.sh
bash scripts_dir/stop-cpo-api-gateway-binary.sh
```

配置校验：

```bash
bash scripts_dir/validate-cpo-api-gateway.sh
bash scripts_dir/validate-cpo-api-gateway-binary.sh
```
