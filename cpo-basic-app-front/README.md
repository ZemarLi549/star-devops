# cpo-basic-app-front

鑫图平台的前端基座，负责本地登录、工作空间、菜单、品牌入口和后续微服务编排。

## 快速开始

本项目当前使用本机 Node 运行。若 Node 不在默认 `PATH` 中，先执行：

```bash
export PATH=/home/kali/.hermes/node/bin:$PATH
```

安装依赖：

```bash
npm ci
```

启动开发服务：

```bash
npm run dev
```

默认地址为 `http://127.0.0.1/`，默认端口为 `80`。本地开发代理配置在 [`vite.config.ts`](./vite.config.ts)，默认将 `/auth`、`/graphql`、`/alarm-manager` 等请求转发到本机后端服务。

## 常用命令

| 命令 | 用途 |
| --- | --- |
| `npm ci` | 按 `package-lock.json` 安装依赖 |
| `npm run dev` | development 模式启动 Vite |
| `npm run build` | 构建 production 产物 |
| `npm run preview` | 预览构建产物 |

在当前 Linux 环境中，如果 `node_modules/.bin/vite` 没有执行位，使用以下等价启动方式：

```bash
/home/kali/.hermes/node/bin/node \
  node_modules/vite/bin/vite.js \
  --mode development \
  --host 0.0.0.0 \
  --port 80
```

## 文档

- [本地部署文档](./docs/本地部署文档.md)
- [前端微服务接入文档](./docs/前端微服务接入文档.md)

## 目录命名

当前前端工程目录统一使用 `cpo-` 前缀：

```text
cpo-basic-app-front
cpo-config-front
cpo-observe-alarm-front
cpo-observe-log-front
cpo-observe-metric-front
cpo-observe-trace-front
```

旧的 `stc-observe-basic-app-front` 已移除。Nacos namespace `st-observable` 属于现有运行配置标识，不是工程目录名，暂不改动。
