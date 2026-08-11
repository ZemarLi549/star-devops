# AI人效自动化部署与操作指导

更新时间：2026-08-11

## 1. 组件清单

当前涉及组件：

- `cpo-feishu-collector-plugin`
- `cpo-ai-productivity-service`
- `cpo-basic-app-front`
- `Nacos`
- `AstrBot`
- `Elasticsearch`

## 1.1 后续全公司扩展说明

如果后续要把 AI 人效自动化系统推广给全公司使用，当前推荐的扩展方式是：

- 每个组织 / 部门可以独立部署一套 `cpo-ai-productivity-service`
- 每个组织 / 部门可以独立部署一套 ES，或者使用共享 ES 中的独立 index 空间
- 全公司统一复用一套 MySQL 用户表

边界说明：

- 共享 MySQL：只存用户主数据、组织关系、飞书身份映射、LDAP / 本地账号映射
- 组织独立 ES：存原始事件、最终统计、事项抽取、AI 日报 / 周报、通知等业务数据
- 组织独立后端：只负责本组织的数据接收、AI 汇总和分析展示
- 平台统一维护空间注册信息，切换空间时调用对应空间后端
- 每个空间自己维护本空间 ES 生命周期和历史数据清理

建议的空间配置字段：

- `spaceId`
- `spaceName`
- `backendBaseUrl`
- `elasticsearchBaseUrl`
- `isBuiltin`
- `status`

当前约定：

- 默认使用内置空间
- 后续新增组织空间时，只需要登记该空间的后端和 ES 信息

## 2. 部署顺序

推荐顺序：

1. 准备飞书应用权限与安全设置
2. 部署 `cpo-feishu-collector-plugin`
3. 完成用户授权
4. 部署 `cpo-ai-productivity-service`
5. 部署 `cpo-basic-app-front`
6. 配置 AstrBot
7. 配置 Elasticsearch

## 3. 用户侧采集器操作

### 3.1 初始化

```bash
cd cpo-feishu-collector-plugin
cp config/application.example.yaml config/application.yaml
bash bin/install.sh E0028517
```

### 3.2 首次授权

```bash
bash bin/authorize.sh E0028517
```

### 3.3 单次采集

```bash
bash bin/collect-e0028517.sh
```

### 3.4 守护运行

```bash
bash bin/start.sh
bash bin/stop.sh
```

说明：

- 当前默认不要求用户自己写 `crontab`
- 插件内置守护模式默认每天 `17:30` 执行一次，统计窗口默认最近 `1` 天

## 4. 服务端部署

### 4.1 AI 人效服务

```bash
cd cpo-ai-productivity-service
cp config/application.example.yaml config/application.yaml
python3 -m pip install -r requirements.txt
python3 app.py
```

### 4.2 前端

```bash
cd cpo-basic-app-front
PATH=/home/kali/.local/bin:/home/kali/.hermes/node/bin:$PATH npm run build
```

## 5. Elasticsearch 配置建议

当前配置项位于：

- `cpo-ai-productivity-service/config/application.yaml`

建议至少配置：

- `elasticsearch.enabled=true`
- `elasticsearch.base-url=http://<es-host>:9200`
- `elasticsearch.raw-index-prefix=cpo-productivity-chat-raw`
- `elasticsearch.summary-index-prefix=cpo-productivity-summary`

## 6. 默认保留策略

- 原始事件：15 天
- 聚合统计：180 天

## 7. 验证步骤

### 7.1 验证采集器

检查：

- `data/<userId>/latest-report.json`
- `data/<userId>/raw-chat/*.json`

### 7.2 验证 AI 人效服务

检查：

- `GET /healthz`
- `GET /api/productivity/report/daily/latest`
- `GET /api/productivity/plugin/upload/chat-events/latest`

### 7.3 验证日报生成

检查：

- `POST /api/productivity/report/daily/summary/generate`
- `GET /api/productivity/report/daily/summary/latest`

## 8. 运维建议

- 用户侧默认只保留采集器运行权限
- 密钥不写入前端
- 上传 token 由服务端控制
- ES 只开放给服务端
- 用户表维护飞书 `open_id` 关联
- 全公司推广时，不建议把所有组织的人效统计全部塞进一个后端进程里
- 推荐按组织独立部署后端 + ES，共享 MySQL 用户表
- 用户本地清理脚本不会删除服务端 ES 数据
- 服务端清理任务必须携带 `spaceId`，禁止跨空间删除
