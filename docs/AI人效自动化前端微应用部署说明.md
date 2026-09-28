# AI 人效自动化前端微应用部署说明

更新时间：2026-08-15

## 1. 组件说明

当前独立微前端项目：

- `cpo-ai-productivity-front`

宿主项目：

- `cpo-basic-app-front`

后端接口：

- `cpo-ai-productivity-service`

## 2. 当前接入关系

```text
浏览器
  ├─ 直接访问 cpo-ai-productivity-front
  │   └─ http://127.0.0.1:3011/
  └─ 通过 cpo-basic-app-front qiankun 挂载
      └─ http://127.0.0.1/ai-productivity
```

旧入口兼容：

- `/control/ai-productivity` 自动跳到 `/ai-productivity`
- `/control/productivity-config` 自动跳到 `/ai-productivity?tab=collector`

## 3. 本地开发启动

### 3.1 启动后端

```bash
bash scripts_dir/start-cpo-ai-productivity-service.sh
```

### 3.2 启动独立微前端

```bash
FRONT_MODE=dev bash scripts_dir/start-cpo-ai-productivity-front.sh
```

### 3.3 停止独立微前端

```bash
bash scripts_dir/stop-cpo-ai-productivity-front.sh
```

## 4. 生产构建

```bash
cd cpo-ai-productivity-front
PATH=/home/kali/.hermes/node/bin:$PATH npm run build
```

构建产物：

```text
cpo-ai-productivity-front/dist/
```

## 5. 建议部署方式

生产环境建议：

1. 构建 `dist/`
2. 使用 Nginx 托管到 `/ai-productivity/`
3. 宿主 `cpo-basic-app-front` 的微应用配置中，将 `moduleUrl` 指向该发布地址

例如：

```text
https://your-domain/ai-productivity/
```

## 6. 当前前端页面结构

### 6.1 人效概览

- 工作沟通
- 处理工单
- 会议参与
- 文档贡献
- AI 日报
- 链路状态
- 采集通知

### 6.2 采集配置

- 下载插件包说明
- 安装与初始化
- 用户授权
- 单次验证与长期运行
- 本地 YAML 关键配置
- 默认过滤与安全边界

## 7. 当前脚本说明

### 7.1 前端启停

- `scripts_dir/start-cpo-ai-productivity-front.sh`
- `scripts_dir/stop-cpo-ai-productivity-front.sh`

环境变量：

- `FRONT_MODE=dev|preview`
- `FRONT_PORT=3011`
- `NODE_BIN_DIR=/home/kali/.hermes/node/bin`

### 7.2 后端启停

- `scripts_dir/start-cpo-ai-productivity-service.sh`
- `scripts_dir/stop-python-service.sh cpo-ai-productivity-service`

## 8. 验证项

前端验证：

- `http://127.0.0.1:3011/`
- `http://127.0.0.1:3011/?tab=collector`

宿主验证：

- `http://127.0.0.1/ai-productivity`

后端验证：

- `http://127.0.0.1:9060/healthz`

## 9. 注意事项

- 采集配置说明已经下沉到 AI 人效系统内部，不再作为工作台一级服务入口。
- `SOP Doc / SOP VDOC` 不再出现在服务入口卡片中。
- 页面只展示说明，不保存用户本地飞书机器人密钥和 token。
- 正式生产环境请把前端静态资源挂到反向代理，不建议长期使用 `vite preview`。
