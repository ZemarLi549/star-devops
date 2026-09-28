# ai-productivity-service 微服务说明

## 定位

`ai-productivity-service` 是 AI 人效自动化系统的后端聚合服务。

当前职责先明确为：

- 聚合工单运营、飞书协同、会议文档、CMDB 和 Zabbix 的个人纵览接口。
- 统一承接 `工单运营` 子模块，避免它继续以独立服务入口暴露在工作台首页。
- 对接 Nacos 通用配置，后续再逐步接 PostgreSQL 工单 SQL、飞书采集结果、ES 摘要索引和资源中心接口。

## 当前已落骨架

- Maven 模块：`auth-all/ai-productivity-service`
- 启动类：`com.iflytek.itsc.auth.productivity.AiProductivityApplication`
- Nacos 配置：`src/main/resources/bootstrap.yml`
- 预留接口：
  - `GET /api/productivity/overview/self`
  - `GET /api/productivity/workorder/self`

## 接口职责

### `GET /api/productivity/overview/self`

用于工作台 `AI 人效自动化系统` 子页展示个人总览，后续统一返回：

- 工单数量
- 已办数量
- 处理中数量
- 飞书沟通数量
- 会议数量
- 文档数量
- 资源数量

### `GET /api/productivity/workorder/self`

用于工单运营子模块，后续统一返回：

- 个人处理工单数
- 已办工单数
- 协办工单数
- SLA 超时 / 即将超时数量
- 工单详情入口所需的查询参数

## Nacos 规划

建议统一拆以下 dataId：

- `cpo-ai-productivity-common.yaml`
- `cpo-feishu-collector-plugin.yaml`
- `cpo-summary-service.yaml`

建议放在 Nacos 的通用配置：

- 网关地址
- PostgreSQL 工单库连接配置
- ES 索引前缀和保留期
- Feishu 汇总开关
- AI 日报 / 周报模板版本
- 资源中心 / Zabbix 接入地址

## auth-all 生产接入

宿主生产态要能正常加载 `ai-productivity`，需要同时满足两件事：

1. `SYS_CONFIG` 中注册 `DEPLOY_MODULE = ai-productivity`
2. `auth-all` 类路径中存在 `menuAndMetaInfo/ai-productivity.json`

当前仓库已补齐：

- `starter/src/main/resources/menuAndMetaInfo/ai-productivity.json`
- `pack/config/menuAndMetaInfo/ai-productivity.json`
- 初始化 SQL 中的 `ai-productivity` 模块注册

对于已运行环境，建议执行一次：

```sql
INSERT INTO sys_config
  (id, parent_id, property_type, property_key, property_value, sort_num, isvalid, remark)
SELECT 12, 0, 'DEPLOY_MODULE', 'ai-productivity', 'AI人效自动化', 0, 1, '600'
WHERE NOT EXISTS (
  SELECT 1 FROM sys_config
  WHERE property_type = 'DEPLOY_MODULE' AND property_key = 'ai-productivity'
);
```

然后由管理员触发一次：

```text
POST /metaAndMenu/ai-productivity
```

该接口会把 `moduleUrl=/ai-productivity/` 写入 `sys_deploy_meta_info`，供宿主前端通过 `/auth/meta` 读取。

## 后续接入顺序

1. 接入 PostgreSQL 工单库 SQL。
2. 接入飞书采集插件上报结果。
3. 接入 ES 7 天摘要索引。
4. 接入 CMDB 与 Zabbix 聚合接口。
5. 接入 AstrBot / 摘要模型生成日报周报。
