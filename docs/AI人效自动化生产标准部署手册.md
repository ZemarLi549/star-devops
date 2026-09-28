# AI 人效自动化生产标准部署手册

更新时间：2026-08-18

## 0. 文档拆分说明

从 2026-08-18 起，生产部署说明按实施角色拆分为三份独立文档：

- 《生产部署-后端微服务部署》
- 《生产部署-微前端Nginx配置部署》
- 《生产部署-中间件二进制部署》

本文继续保留为总览手册，适合在项目启动阶段统一了解整体部署边界；实际实施时，优先按上面三份拆分文档执行。

## 1. 目标

本文用于指导以下组件的标准生产部署：

- `cpo-basic-app-front`
- `cpo-ai-productivity-front`
- `cpo-ai-productivity-service`
- `Nacos`
- `AstrBot`
- `Elasticsearch`
- 可选 `KrakenD CE`

适用目标：

- 单组织独立部署
- 多组织 / 多空间复用共享用户主数据
- 前端微应用通过 Nginx 静态托管
- 宿主通过 qiankun 加载 AI 人效微前端

依赖前提：

- `auth-all` 已作为统一登录与 `/auth/meta` 元数据来源运行
- 基础 MySQL、Nacos、Elasticsearch 已有标准环境
- 前端对外统一经由 Nginx / LB 暴露

## 2. 当前实现边界

截至 2026-08-15，当前代码的真实边界如下：

- `cpo-ai-productivity-front`
  - 已完成独立微前端落地
  - 可独立访问，也可被宿主 qiankun 挂载
  - 已内置 `人效概览` 与 `采集配置` 两个子菜单
- `cpo-ai-productivity-service`
  - 已可提供人效概览、最新采集报告、AstrBot 状态、日报生成、通知接口
  - 当前配置加载方式是：
    - 本地 `config/application.yaml`
    - 环境变量覆盖
  - 代码中保留了 `nacos.server-addr / namespace / group / data-id` 等坐标
  - 但 **当前版本尚未直接通过 Nacos SDK 主动拉取配置**
- `cpo-feishu-collector-plugin`
  - 已支持用户独立 OAuth 授权
  - 已支持聊天采集、过滤和本地报告
  - 已支持一次性采集与守护运行

因此，当前推荐的标准生产做法是：

- **Nacos 作为统一配置源**
- **部署流程负责把 Nacos 配置下发或渲染到服务实际运行环境**
- 服务进程读取环境变量 / `application.yaml`

这比在当前版本里强行宣称“服务已运行时直连 Nacos 热更新”更准确。

## 3. 推荐生产拓扑

### 3.1 单组织标准拓扑

```text
浏览器
  |
  v
Nginx / LB
  ├─ /                         -> cpo-basic-app-front 静态资源
  ├─ /ai-productivity/         -> cpo-ai-productivity-front 静态资源
  ├─ /productivity-api/        -> cpo-ai-productivity-service:9060
  ├─ /auth/                    -> auth-all
  └─ 其他业务代理              -> 现有平台后端

cpo-ai-productivity-service
  ├─ 读取 application.yaml / ENV
  ├─ 接收采集器上报
  ├─ 写入本地事件目录
  ├─ 写入 Elasticsearch
  └─ 调用 AstrBot 生成日报

Nacos
  └─ 存放公共配置源

Elasticsearch
  ├─ 聊天原始事件
  ├─ 汇总统计
  ├─ AI 摘要
  └─ 事项结果

AstrBot
  └─ 对接 DeepSeek 等模型，生成日报 / 周报 / 事项
```

### 3.2 多组织 / 多空间拓扑

```text
共享：
  ├─ cpo-basic-app-front
  ├─ 用户主数据 MySQL
  └─ 统一登录 auth-all

按空间独立：
  ├─ cpo-ai-productivity-service
  ├─ Elasticsearch
  ├─ AstrBot 配置空间
  └─ 空间侧 Nacos 配置分组 / dataId
```

建议每个空间登记：

- `spaceId`
- `spaceName`
- `backendBaseUrl`
- `gatewayRoutePrefix`
- `elasticsearchBaseUrl`
- `isBuiltin`
- `status`

## 4. 标准端口规划

| 组件 | 建议端口 | 说明 |
| --- | ---: | --- |
| Nginx | 80 / 443 | 对外统一入口 |
| cpo-basic-app-front | 静态托管 | 不建议直接暴露 Vite |
| cpo-ai-productivity-front | 静态托管 | 建议挂到 `/ai-productivity/` |
| cpo-ai-productivity-service | 9060 | Tornado 后端 |
| Nacos | 8848 | 配置中心 |
| Elasticsearch | 9200 | REST |
| AstrBot | 6185 | AI 摘要服务 |
| auth-all | 按现网 | 宿主登录与菜单 |

## 5. 目录规范

建议生产目录如下：

```text
/data/apps/star-devops/
├─ cpo-basic-app-front/
│  └─ dist/
├─ cpo-ai-productivity-front/
│  └─ dist/
├─ cpo-ai-productivity-service/
│  ├─ app.py
│  ├─ config/application.yaml
│  ├─ logs/
│  └─ data/
├─ cpo-api-gateway/
└─ scripts_dir/
```

## 6. 前端微应用生产部署

### 6.1 构建微前端

```bash
cd cpo-ai-productivity-front
PATH=/home/kali/.hermes/node/bin:$PATH npm install
PATH=/home/kali/.hermes/node/bin:$PATH npm run build
```

构建产物：

```text
cpo-ai-productivity-front/dist/
```

### 6.2 构建宿主

```bash
cd cpo-basic-app-front
PATH=/home/kali/.hermes/node/bin:$PATH npm run build
```

### 6.3 Nginx 静态托管要求

生产环境不建议长期使用：

- `vite dev`
- `vite preview`

标准方式：

1. 宿主构建后托管到站点根路径 `/`
2. AI 人效微前端构建后托管到 `/ai-productivity/`
3. 宿主通过 qiankun 加载微前端静态入口

### 6.4 宿主微应用注册要求

宿主当前开发态已写死：

- `moduleName: ai-productivity`
- `moduleUrl: http://localhost:3011/`

生产态建议由菜单 / 元数据接口返回：

```json
{
  "moduleName": "ai-productivity",
  "moduleUrl": "https://portal.example.com/ai-productivity/"
}
```

注意：

- `moduleUrl` 必须是可访问的微前端入口地址
- 结尾建议保留 `/`
- `moduleName` 必须与宿主 `PROJECT_MAP.AI_PRODUCTIVITY` 对齐

### 6.5 auth-all 生产元数据接入

生产态宿主不会读取本地开发 `metaList`，而是调用 `auth-all` 的 `/auth/meta`。

因此必须保证：

1. `auth-all` 初始化配置里已注册 `DEPLOY_MODULE = ai-productivity`
2. `auth-all` 类路径中存在 `menuAndMetaInfo/ai-productivity.json`
3. `sys_deploy_meta_info` 已写入 `moduleName=ai-productivity`

当前仓库已补齐前两项。已有环境建议执行一次增量 SQL：

```sql
INSERT INTO sys_config
  (id, parent_id, property_type, property_key, property_value, sort_num, isvalid, remark)
SELECT 12, 0, 'DEPLOY_MODULE', 'ai-productivity', 'AI人效自动化', 0, 1, '600'
WHERE NOT EXISTS (
  SELECT 1 FROM sys_config
  WHERE property_type = 'DEPLOY_MODULE' AND property_key = 'ai-productivity'
);
```

然后在 `auth-all` 所在环境触发一次：

```bash
curl -X POST http://127.0.0.1:<auth-port>/metaAndMenu/ai-productivity
```

触发后，`/auth/meta` 返回的 `metaList` 中应包含：

```json
{
  "moduleName": "ai-productivity",
  "moduleUrl": "aHR0cDovL3BvcnRhbC5leGFtcGxlLmNvbS9haS1wcm9kdWN0aXZpdHkv"
}
```

说明：

- 返回值中的 `moduleUrl` 默认会被 `auth-all` 做 Base64 编码
- 原始 URL 由 `nginx.url + /ai-productivity/` 组成
- `nginx.url` 需要在 `auth-all` 的生产配置中设置为门户域名

## 7. Nginx 生产配置

### 7.1 推荐职责

Nginx 负责：

- 宿主前端静态资源
- AI 人效微前端静态资源
- 后端 API 反向代理
- gzip / 缓存 / TLS

Nginx 不负责：

- 业务逻辑聚合
- 微前端运行时协调

### 7.2 推荐路径

```text
/                     -> 宿主前端
/ai-productivity/     -> AI 人效微前端
/productivity-api/    -> AI 人效后端
```

### 7.3 配置样例

完整样例已放到：

```text
docs/templates/nginx-ai-productivity-prod.conf
```

核心要求：

- `location /ai-productivity/` 使用独立静态根目录
- `try_files $uri $uri/ /ai-productivity/index.html;`
- `location /productivity-api/` 反代到 `127.0.0.1:9060`
- 反代时保留：
  - `Host`
  - `X-Real-IP`
  - `X-Forwarded-For`
  - `X-Forwarded-Proto`

## 8. 后端生产部署

### 8.1 基本启动

当前项目已提供脚本：

```bash
bash scripts_dir/start-cpo-ai-productivity-service.sh
bash scripts_dir/stop-python-service.sh cpo-ai-productivity-service
```

### 8.2 后端目录要求

至少保证：

- `config/application.yaml`
- `logs/`
- `data/`
- `.venv/`

### 8.3 当前配置来源

当前后端真实读取顺序：

1. `config/application.yaml`
2. 环境变量

建议生产环境通过如下方式之一注入：

- Nacos 发布 -> CI 渲染 -> 下发 `application.yaml`
- Nacos 发布 -> 容器环境变量注入
- 配置平台 -> ENV 注入

### 8.4 关键配置项

后端生产重点配置：

- `server.port`
- `collector.data-root`
- `storage.local-data-root`
- `storage.event-data-root`
- `storage.raw-retention-days`
- `storage.summary-retention-days`
- `elasticsearch.enabled`
- `elasticsearch.base-url`
- `elasticsearch.username / password / api-key`
- `astrbot.base-url`
- `astrbot.api-key`
- `reporting.default-user-id`
- `notification.webhook-url`

### 8.5 建议的 systemd 管理

后端长期运行建议交给 `systemd`，不要直接前台挂起。

示例：

```ini
[Unit]
Description=cpo-ai-productivity-service
After=network.target

[Service]
Type=simple
WorkingDirectory=/data/apps/star-devops/cpo-ai-productivity-service
Environment=PYTHONUNBUFFERED=1
ExecStart=/data/apps/star-devops/cpo-ai-productivity-service/.venv/bin/python app.py
Restart=always
RestartSec=5
User=star
Group=star

[Install]
WantedBy=multi-user.target
```

## 9. Nacos 标准生产部署

### 9.1 推荐部署模式

推荐最少 3 节点集群：

```text
nacos-1:8848
nacos-2:8848
nacos-3:8848
```

前面挂一层内网 LB / Nginx：

```text
nacos.example.internal:8848
```

### 9.2 元数据存储

生产环境不要用嵌入式存储，建议使用独立 MySQL：

- 专用数据库
- 专用账号
- 定期备份

### 9.3 建议的命名规范

建议：

- `namespace`
  - 按环境区分：`dev / test / prod`
- `group`
  - 按系统区分：`cpo-ai-productivity`
- `dataId`
  - 按配置文件区分：`cpo-ai-productivity-common.yaml`

例如：

```text
namespace = prod
group     = cpo-ai-productivity
dataId    = cpo-ai-productivity-common.yaml
```

### 9.4 建议存放到 Nacos 的配置

适合放 Nacos：

- Elasticsearch 地址
- AstrBot 地址
- 默认保留周期
- 默认空间配置
- 通知 Webhook
- 组织级开关
- 前端模块 URL

不建议直接明文开放给普通页面：

- App Secret
- 用户 token
- 数据库 root 账号
- 组织内部高敏密钥

### 9.5 当前服务的 Nacos 落地建议

由于当前 `cpo-ai-productivity-service` 尚未内置 Nacos SDK 主动拉取逻辑，生产建议采用：

1. 在 Nacos 维护标准配置
2. 由发布系统拉取配置
3. 渲染到 `config/application.yaml`
4. 再启动 Tornado 服务

这是当前版本最稳妥的标准生产方案。

### 9.6 推荐 Nacos 配置样例

建议在 Nacos 中维护如下配置内容，再由发布系统渲染到服务运行目录：

```yaml
server:
  port: 9060

storage:
  local-data-root: /data/apps/star-devops/cpo-ai-productivity-service/data
  event-data-root: /data/apps/star-devops/cpo-ai-productivity-service/data/events
  raw-retention-days: 15
  summary-retention-days: 180

collector:
  data-root: /data/apps/star-devops/cpo-ai-productivity-service/data/uploads

elasticsearch:
  enabled: true
  base-url: http://es-prod.internal:9200
  raw-index-prefix: cpo-productivity-chat-raw
  summary-index-prefix: cpo-productivity-summary

astrbot:
  base-url: http://astrbot-prod.internal:6185
  api-key: ${ASTRBOT_API_KEY}
  selected-provider: deepseek
  selected-model: deepseek-v4-flash

reporting:
  default-user-id: E0028517

notification:
  webhook-url: http://notify.internal/webhook/productivity
```

## 10. Elasticsearch 标准生产部署

### 10.1 建议索引分类

- 聊天原始事件
- 汇总统计结果
- AI 日报 / 周报
- AI 事项结果
- 通知记录

### 10.2 建议生命周期

- 原始事件：15 天
- 汇总统计：180 天
- AI 摘要：180 天
- 事项结果：180 天

### 10.3 建议安全策略

- 内网访问
- 独立账号
- 最小权限
- 不对普通用户浏览器直连 ES

## 11. AstrBot 标准生产部署

### 11.1 职责

AstrBot 用于：

- 日报生成
- 周报生成
- 事项抽取
- 聊天摘要

### 11.2 生产要求

- 独立 API Key
- 配置可用模型
- 限流与超时
- 独立日志

### 11.3 当前调用要求

当前后端依赖：

- `astrbot.base-url`
- `astrbot.api-key`
- `astrbot.selected-provider`
- `astrbot.selected-model`

若未配置，前端仍可访问，但 AI 日报按钮只会显示失败提示或未就绪状态。

## 12. 生产启停脚本

### 12.1 前端

```bash
FRONT_MODE=preview bash scripts_dir/start-cpo-ai-productivity-front.sh
bash scripts_dir/stop-cpo-ai-productivity-front.sh
```

说明：

- `preview` 适合预发布验收
- 正式生产建议由 Nginx 托管 `dist/`

### 12.2 后端

```bash
bash scripts_dir/start-cpo-ai-productivity-service.sh
bash scripts_dir/stop-python-service.sh cpo-ai-productivity-service
```

### 12.3 网关

如启用 KrakenD：

```bash
bash scripts_dir/start-cpo-api-gateway-binary.sh
bash scripts_dir/stop-cpo-api-gateway-binary.sh
```

## 13. 发布顺序

推荐生产发布顺序：

1. 发布 Nacos 配置
2. 发布或重启 `auth-all`
3. 确认 `auth-all` 已加载 `ai-productivity.json`
4. 首次环境执行 `POST /metaAndMenu/ai-productivity`
5. 发布 `cpo-ai-productivity-service`
6. 检查 `9060/healthz`
7. 发布 `cpo-ai-productivity-front` 静态资源
8. 确认 `/auth/meta` 已返回 `ai-productivity`
9. 灰度验证 `/ai-productivity`

## 14. 验证清单

### 14.1 后端

```bash
curl http://127.0.0.1:9060/healthz
curl http://127.0.0.1:9060/api/productivity/astrbot/status
```

### 14.2 前端

```text
https://portal.example.com/ai-productivity/
https://portal.example.com/ai-productivity/?tab=collector
```

### 14.3 宿主挂载

```text
https://portal.example.com/ai-productivity
```

### 14.3.1 auth-all 元数据验证

```bash
curl http://127.0.0.1:<auth-port>/auth/meta
```

验证要点：

- `metaList` 中存在 `moduleName=ai-productivity`
- `moduleUrl` 解码后为 `https://portal.example.com/ai-productivity/`

### 14.4 采集链路

验证项：

- 用户完成授权
- 采集器生成 `latest-report.json`
- 服务端收到上传
- 前端可以看到采集摘要
- AstrBot 可以生成日报

## 15. 回滚建议

### 15.1 前端回滚

- 回滚 `/ai-productivity/` 静态目录到上一版本
- 保持宿主 `moduleUrl` 不变

### 15.2 后端回滚

- 回滚 `cpo-ai-productivity-service`
- 保持数据目录不删
- 保持 ES 索引不删

### 15.3 Nacos 回滚

- 回滚对应 `dataId`
- 重新渲染部署配置
- 重启服务实例

### 15.4 auth-all 回滚

- 如仅需临时摘除微应用，可执行 `DELETE /metaAndMenu/ai-productivity`
- 如需彻底回滚初始化配置，再删除 `SYS_CONFIG` 中对应 `DEPLOY_MODULE`
- 回滚后宿主刷新 `/auth/meta` 即不会继续加载该微应用

## 16. 当前推荐结论

当前生产交付最稳妥的组合是：

- 前端：
  - `cpo-basic-app-front` + `cpo-ai-productivity-front`
  - 统一由 Nginx 静态托管
- 后端：
  - `cpo-ai-productivity-service`
  - Tornado 独立进程运行
- 配置：
  - Nacos 作为标准配置源
  - 发布时渲染到服务实际运行环境
- AI：
  - AstrBot 独立承接日报与事项生成
- 数据：
  - Elasticsearch 保存原始事件与统计结果

这套方式最接近你现在的代码状态，也最适合先落生产，再逐步演进到真正的 Nacos 动态配置与更完整的空间治理。
