# 鑫图平台 AI 人效自动化系统设计

## 1. 建设目标

AI 人效自动化系统作为鑫图平台的后续微服务，面向个人和组织提供可授权、可审阅、可追溯的协同工作数据分析能力。第一阶段聚焦飞书协同数据，不以单一指标评价个人，不默认生成公开排名。

系统需要支持：

- 工单运营统一纳入 AI 人效自动化系统子项目，在同一入口内承接处理工单、已办工单和 SLA 纵览。
- 个人飞书沟通次数、发送消息数和被 `@` 次数统计。
- 按群聊、关键字、时间范围和用户维度配置统计范围。
- 飞书会议次数、时长、参会结构和会议参与情况统计。
- 飞书文档创建、编写、修改和协作贡献统计。
- 对已授权的工作话题进行分类、聚合和趋势分析。
- 生成可编辑的日报草稿，由用户确认后发布或导出。
- 提供公共个人下载统计插件，用于导出个人授权范围内的统计数据。

## 2. 推荐微服务边界

后端建议拆分为以下服务。服务之间通过网关和内部鉴权通信，禁止前端直接访问飞书凭据或第三方管理接口。

```text
xintu-gateway             统一入口、鉴权、限流、审计关联
xintu-auth                用户、租户、角色和登录会话
xintu-assistant-api       智能助手平台统一 API
xintu-astrbot-adapter     AstrBot 适配、会话和插件编排
xintu-feishu-connector    飞书 OAuth、数据同步、游标和重试
xintu-productivity-api    人效查询、过滤、聚合和报表接口
xintu-productivity-worker 异步同步、计算和导出任务
xintu-summary-service     话题分析、日报草稿和 LLM 编排
xintu-export-plugin       公共个人下载统计插件
xintu-audit-service       数据访问、授权、导出和模型调用审计
xintu-workbench-api       工单、飞书、CMDB、Zabbix 个人总览聚合
xintu-zabbix-gateway      多数据中心 Zabbix 和 zabbix-watcher 适配
```

对应的前端子应用建议命名为：

```text
cpo-assistant-front
cpo-astrbot-front
cpo-ai-productivity-front
cpo-feishu-insight-front
cpo-plugin-center-front
cpo-workbench-front
```

当前先在 `cpo-basic-app-front` 中以 `/control/ai-productivity` 子页面落地，后续独立拆分为 `cpo-ai-productivity-front`。前端通过 `xintu-gateway` 访问业务 API，统一复用鑫图平台的登录态、菜单、租户和权限模型。服务入口暂时保持清空，待各微服务完成接入契约后再逐项开放。

当前后端主实现建议先按独立的 `Python 3 + Tornado` 服务推进：

```text
cpo-api-gateway
cpo-ai-productivity-service
```

根目录统一提供：

```text
scripts_dir/   通用启动、停止、环境初始化脚本
docs/          前后端接入步骤、微服务模板、网关说明
```

## 3. 飞书连接器

### 3.0 配置中心原则

所有通用配置统一走 Nacos 配置中心，避免把环境差异写死在代码仓库中。建议拆分为以下 dataId：

```text
cpo-productivity-common.yaml
cpo-feishu-connector.yaml
cpo-feishu-collector-plugin.yaml
cpo-summary-service.yaml
cpo-astrbot-adapter.yaml
```

建议配置项边界：

- 通用配置放 Nacos：网关地址、上报批量大小、同步窗口、ES 索引名前缀、保留天数、限流阈值、日报模板版本、AstBot 路由开关。
- 敏感配置不落前端源码：飞书应用密钥、机器人密钥、数据库密码、ES 凭据、Nacos token 等只放服务端密文配置或环境变量。
- 用户本机插件只读取最小配置：Nacos 地址、namespace、dataId、平台租户/用户标识、上报令牌和本地采集范围。

### 3.1 连接器职责

- 发起 OAuth 授权、刷新和撤销授权。
- 将 access token 和 refresh token 仅保存在后端密文或密钥管理系统中。
- 同步聊天事件、被 `@` 元数据、会议日历/会议记录和文档贡献元数据。
- 使用游标或时间窗口增量同步，记录同步水位。
- 对失败任务进行指数退避重试，使用幂等键避免重复计数。
- 隔离租户、用户、群聊和授权连接。
- 对所有第三方响应进行后端 schema 校验，不能直接信任飞书返回的 JSON。

### 3.2 同步原则

默认只保存完成统计所需的最小元数据，不保存完整聊天正文。若话题分类确实需要读取内容，必须同时满足：

1. 用户明确授权，并且管理员配置了允许分析的范围。
2. 只读取被过滤规则命中的最小数据集。
3. 在进入模型前完成脱敏、截断和敏感信息过滤。
4. 明确数据保留期限、模型供应商和跨境处理边界。
5. 记录访问、分析、导出和删除审计。
6. 支持撤回授权并删除对应数据。

### 3.3 个人部署采集插件

第一阶段不要求平台后端直连抓取所有个人聊天数据，而是提供一个“个人协同统计采集插件”给用户下载并部署在自己机器上。插件定位如下：

- 部署位置：员工个人办公机或跳板机，不部署在浏览器扩展中。
- 采集范围：仅采集当前登录用户授权范围内的个人单聊、群聊、被 `@`、会议和文档贡献元数据。
- 上报方式：插件按批次将脱敏后的事件或聚合结果上报到平台网关。
- 本地缓存：插件仅保留短期本地缓存和失败重试队列，不保存长期明文消息库。
- 配置来源：插件启动时从 Nacos 拉取公共采集配置，再合并平台侧按用户保存的采集规则、本地私有凭据和用户过滤规则。当前前端已提供 `/control/productivity-config` 用户自助配置入口，先本地保存后续再接后端接口。

插件建议以独立工程形式交付，例如：

```text
cpo-feishu-collector-plugin
  README.md
  config/application.example.yaml
  bin/start.sh
  bin/stop.sh
```

### 3.4 工单运营归并原则

- 工单运营不再单独作为工作台服务入口展示。
- 工单处理、已办工单、SLA、协办和详情入口统一归入 AI 人效自动化系统子项目。
- 后端由 `cpo-ai-productivity-service` 统一聚合 PostgreSQL 工单库、飞书统计、CMDB 和 Zabbix 结果。
- 前端子页负责展示个人工单概览和跳转入口，详细工单 SQL 口径后续按你提供的查询脚本接入。

## 4. 数据模型建议

第一阶段建议使用“关系库存配置状态 + ES 存短期事件/摘要索引”的双存储模型：

- MySQL / PostgreSQL：保存用户授权、过滤规则、同步任务、日报草稿、插件实例、审计日志。
- Elasticsearch：保存 7 天内的聊天采集事件、关键词命中、话题摘要和日报检索索引。

原因：

- 个人聊天协同数据有明显的时间窗口特征，适合按天滚动索引。
- 需要按用户、群聊、关键词、时间范围做检索和聚合，ES 比单纯关系库更适合。
- 过滤后的原始事件只保留 7 天，避免无限积累敏感数据。
- 长期口径报表不直接依赖 ES，而由聚合表沉淀，避免索引过期后统计断层。

推荐索引与表如下：

```text
feishu_connection             授权连接、租户、用户和权限范围
collector_agent              用户本机插件实例、版本、最近心跳和机器信息
sync_job                      同步任务、水位、重试次数和状态
chat_event_stat               沟通事件按日聚合
mention_stat                  被 @ 事件按日聚合
meeting_stat                  会议次数、时长和参与结构
document_contribution_stat    文档创建、编辑和协作者贡献
topic_daily_stat              工作话题按日聚合
filter_rule                   群聊、关键字、时间范围和时区规则
daily_summary                 日报草稿、确认状态、版本和发布记录
export_job                    下载任务、文件范围、有效期和状态
audit_log                     授权、查询、导出和模型调用审计

es: cpo-feishu-chat-event-YYYY.MM.DD
es: cpo-feishu-topic-summary-YYYY.MM.DD
es: cpo-daily-report-search-YYYY.MM.DD
```

所有业务表应带有 `tenant_id`、`subject_user_id`、创建时间、更新时间和数据来源字段。统计结果应支持按授权连接和同步批次回溯，删除授权时能够级联清理或匿名化。

### 4.1 ES 7 天保留建议

建议对 `cpo-feishu-chat-event-*` 和 `cpo-feishu-topic-summary-*` 建 ILM：

```text
hot: 0-2 天，可写可查
warm: 3-7 天，只读可查
delete: >7 天自动删除
```

日报草稿和最终确认结果不建议只存 ES，应保留一份关系库主记录，ES 仅做检索和全文摘要命中加速。

## 5. 过滤规则

过滤规则只作用于用户明确选择的统计范围。建议契约如下：

```json
{
  "groupIds": [],
  "excludeGroupIds": [],
  "includeKeywords": [],
  "excludeKeywords": [],
  "timeZone": "Asia/Shanghai",
  "startAt": "2026-01-01T00:00:00+08:00",
  "endAt": "2026-01-31T23:59:59+08:00"
}
```

关键字过滤必须在产品界面中明确标识两种模式：

- **元数据过滤**：只按群聊、事件类型、标题或已有标签过滤，不读取全部聊天正文。
- **内容过滤**：读取获得授权的消息正文进行匹配或话题分析，必须单独授权，并受保留期和脱敏策略约束。

默认使用元数据过滤。空的 `groupIds` 不应被解释为“读取所有群聊”，而应根据授权范围和显式选择结果确定数据边界。所有时间计算统一转换到规则中的时区，服务端校验 `startAt <= endAt`、最大查询窗口和关键字数量上限。

管理页面建议为每个用户提供：

- 群聊白名单 / 黑名单。
- 关键字包含 / 排除规则。
- 是否允许读取正文做话题分析。
- 会议统计是否包含特定会议室或组织者。
- 文档统计是否包含特定知识空间或文档目录。

## 6. 日报流程

```text
飞书数据增量同步
  -> schema 校验、去重和脱敏
  -> 沟通、会议、文档统计聚合
  -> 工作话题分类和趋势分析
  -> 合并当天工作信号
  -> 生成可编辑日报草稿
  -> 用户审阅和修改
  -> 用户确认后发布或导出
```

日报必须保留人工确认状态，模型输出不能直接作为绩效结论或自动发布内容。每个结论应尽可能关联来源统计、时间范围和置信度；模型失败时仍应能展示原始聚合指标。

建议日报内容包括：

- 今日沟通、被 `@`、会议和文档贡献概览。
- 工作话题及其来源数量，不展示不必要的原始消息。
- 会议和文档中的待跟进事项草稿。
- 用户可编辑的工作总结、风险和明日计划。
- 数据范围、过滤规则、生成时间和模型版本。

## 7. API 接入契约

建议由 `xintu-productivity-api` 对外提供以下接口：

```text
POST   /api/productivity/feishu/connections
GET    /api/productivity/feishu/connections
DELETE /api/productivity/feishu/connections/:id
POST   /api/productivity/sync/jobs
GET    /api/productivity/sync/jobs/:id
GET    /api/productivity/stats/communication
GET    /api/productivity/stats/meetings
GET    /api/productivity/stats/documents
POST   /api/productivity/reports/daily/draft
GET    /api/productivity/reports/daily/:id
POST   /api/productivity/reports/daily/:id/confirm
POST   /api/productivity/exports
GET    /api/productivity/exports/:id
GET    /api/productivity/filters/self
PUT    /api/productivity/filters/self
GET    /api/productivity/plugin/package/latest
POST   /api/productivity/plugin/heartbeat
POST   /api/productivity/plugin/upload/chat-events
POST   /api/productivity/plugin/upload/meeting-stats
POST   /api/productivity/plugin/upload/document-stats
```

列表和统计接口至少支持以下查询参数：

```text
page
pageSize
startAt
endAt
userId
groupIds
excludeGroupIds
keywords
```

接口响应应统一包含 `requestId`、数据范围、统计口径和分页信息。请求体和查询参数必须在网关或服务边界进行 schema 校验；用户只能访问自己或被授权的租户数据。导出任务应使用短期、一次性或可撤销下载地址，并记录下载审计。

插件上报接口必须额外校验：

- 插件实例 ID
- 平台下发的短期上报 token
- 用户 ID / 租户 ID 是否匹配
- 请求时间戳和签名
- 批次幂等键

## 7.5 与工作台个人总览的关系

AI 人效统计不是独立的个人评价系统，而是工作台聚合总览的一类数据源。工作台还会并列展示：

- 三方工单数据库中的处理中、已办和协办工单。
- 个人负责的应用系统、服务器和 CMDB 资源数量。
- 多套 Zabbix 数据中心中的个人机器、内存、磁盘和告警概览。

这些数据使用同一时间范围筛选，但详情入口必须回到各自领域服务。工单 SQL、CMDB 和 Zabbix 接入边界见 [`工单与资源总览接入设计.md`](./工单与资源总览接入设计.md)。

## 8. 公共个人下载统计插件

插件中心可以提供一个“个人协同统计导出”插件，插件只读取当前用户已经授权并且符合过滤规则的聚合数据：

- 支持按平台页面下载插件包，插件包中不内置任何企业密钥。
- 支持 CSV、JSON 和 Markdown 日报导出。
- 默认不导出聊天正文、token 或第三方原始响应。
- 导出任务异步执行，限制时间范围、行数和文件大小。
- 下载链接短期有效，下载后记录用户、范围、文件摘要和时间。
- 插件 manifest 中声明所需权限、数据类型、保留期和导出格式。
- 插件运行在受限沙箱中，不允许访问前端 token、任意文件路径或未声明的网络地址。

## 9. 安全与隐私边界

该系统处理个人聊天、被 `@`、会议和文档贡献，属于敏感工作数据，必须默认最小化采集：

- 个人自主授权、可查看授权范围、可随时撤销。
- 企业管理员授权不能替代个人知情授权。
- 申请最小化飞书权限，按聊天、会议、文档能力拆分授权。
- 默认不保存原始消息正文，不采集未选择的群聊。
- 不把个人排名、强制比较或自动绩效结论作为默认功能。
- 按租户、用户和授权连接进行访问控制与数据隔离。
- 配置数据保留期、删除策略和撤销授权后的清理任务。
- 所有访问、查询、分析、导出和删除动作写入审计日志。
- 飞书 token 只存后端密文或密钥管理系统，禁止进入前端、日志和下载文件。
- 使用 HTTPS；会话使用 `httpOnly`、`secure`、`sameSite` cookie。
- 外部接口和导出接口启用速率限制、请求体大小限制和任务幂等。
- LLM 输入使用脱敏后的最小数据，记录模型、提示模板版本、输入范围和输出审阅状态。

## 10. 分阶段交付

### 阶段一：接入基础

- 完成微服务命名、网关路由、统一鉴权和租户上下文。
- 完成 Nacos 配置拆分、插件下载入口、插件注册/心跳和撤销授权审计。
- 完成飞书 OAuth、连接管理、撤销授权和审计。
- 只接入聚合元数据和 7 天 ES 事件索引，不做原始聊天正文分析。

### 阶段二：统计与导出

- 完成聊天、被 `@`、会议和文档贡献的增量同步。
- 完成群聊、关键字、时间范围过滤。
- 完成个人统计看板和公共下载插件。

### 阶段三：智能总结

- 完成话题分类、日报草稿和来源追溯。
- 支持用户编辑、确认、导出和可选发布。
- 增加模型调用审计、成本限制和失败降级。

### 阶段四：AstrBot 与智能助手

- 通过 `xintu-astrbot-adapter` 暴露受控工具。
- 工具调用必须继承当前用户授权和过滤规则。
- 助手只能读取已授权统计结果，不得绕过连接器直接查询飞书。
