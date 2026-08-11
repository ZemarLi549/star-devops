# ADR-001: AI人效统计结果统一进入 Elasticsearch，并按组织独立部署空间

## Status
Accepted

## Date
2026-08-11

## Context

AI 人效自动化系统后续不只是给单个试点团队使用，而是计划扩展到全公司。

当前已经明确的需求包括：

- 飞书聊天、会议、文档等原始事件需要统一检索
- 最终统计结果也需要支持自由分析、趋势查询和横向扩展
- 后续 AI 会基于沟通、会议和文档继续抽取事项、待办、日报和周报
- 不同组织 / 部门可能需要自己部署一套后端和 ES 存储
- 用户身份不希望每套系统都重复维护，希望公共复用一套 MySQL 用户表

如果继续把“最终统计结果”主要存关系库，而原始事件在 ES，会带来几个问题：

- 检索能力和聚合能力分散在两套存储
- 后续事项抽取、日报、周报、趋势分析还要跨库读写
- 多组织独立部署时，关系库结构和扩容成本更重
- 需要做大量二次同步，维护复杂度偏高

## Decision

做以下统一架构决策：

1. 聊天、会议、文档等原始事件统一进入 Elasticsearch。
2. 最终统计结果也统一进入 Elasticsearch。
3. AI 产出的事项抽取结果、日报摘要、周报摘要、趋势快照优先进入 Elasticsearch。
4. 每个组织 / 部门允许独立部署一套：
   - `cpo-ai-productivity-service`
   - 对应的 Elasticsearch 集群或独立 index 空间
5. 全公司统一复用一套 MySQL 用户表，作为身份主数据源。
6. 各组织后端通过共享用户表中的稳定身份字段完成用户关联，推荐至少使用：
   - `employee_no`
   - `account`
   - `feishu_open_id`
   - `department_id`
   - `organization_id`
7. 平台侧引入“空间”概念，每个空间记录自己的后端与 ES 连接信息。
8. 用户切换空间时，前端和网关按空间路由到对应后端。
9. 当前默认使用内置空间，作为第一套标准空间。
10. 每套 AI 人效后端自己的业务数据不共享：
   - 原始事件
   - 统计结果
   - AI 摘要
   - 通知记录
   - 事项 / 待办

## Alternatives Considered

### 方案 A：原始事件进 ES，最终统计只进 MySQL / PostgreSQL

- 优点：
  - 关系模型更直观
  - 事务型更新容易理解
- 缺点：
  - 原始分析和最终分析分裂
  - 后续事项、日报、周报仍然需要跨库汇总
  - 多组织部署时迁移和扩容不够轻

Rejected:
最终统计本身就是面向检索、聚合、时间窗口和趋势分析，继续主要放关系库不利于后续扩展。

### 方案 B：全公司只共用一套后端和一套 ES

- 优点：
  - 初期部署简单
  - 管理面少
- 缺点：
  - 租户边界弱
  - 组织间隔离差
  - 后期容量和权限治理容易耦合

Rejected:
不满足“每个组织部门可以自己搭建一套后端 + ES 存储统计”的要求。

### 方案 C：所有数据都只落本地文件或对象存储

- 优点：
  - 实现简单
- 缺点：
  - 无法支撑复杂检索和聚合
  - 统计分析、事项抽取、管理页查询成本高

Rejected:
不满足正式平台化能力要求。

## Consequences

### 正向影响

- 原始事件、最终统计、AI 摘要都能统一检索和聚合
- 后续“事项统计”可以直接作为 ES 文档类型扩展
- 每个组织独立部署时边界清晰
- 后端水平扩展和按组织拆分更自然

### 约束

- ES 索引设计要明确区分：
  - 原始事件索引
  - 聚合统计索引
  - AI 摘要索引
  - 事项索引
- 必须在文档中明确数据保留策略
- 共享 MySQL 用户表只能承载身份主数据，不承载各组织的人效业务统计

## Implementation Notes

推荐的索引前缀策略：

- `cpo-productivity-chat-raw-*`
- `cpo-productivity-summary-*`
- `cpo-productivity-action-items-*`
- `cpo-productivity-ai-report-*`

推荐的隔离字段：

- `organizationId`
- `departmentId`
- `spaceId`
- `platformUserId`
- `feishuOpenId`

推荐增加的空间注册信息：

- `spaceId`
- `spaceName`
- `organizationId`
- `departmentId`
- `backendBaseUrl`
- `gatewayRoutePrefix`
- `elasticsearchClusterName`
- `elasticsearchBaseUrl`
- `status`
- `isBuiltin`
- `sortOrder`

组织独立部署时，推荐两种方式二选一：

1. 每个组织独立 ES 集群
2. 共用 ES 集群但完全独立 index 前缀 + 权限隔离

共享 MySQL 用户表建议只保留：

- 用户基础资料
- 组织/部门关系
- 飞书身份关联
- LDAP / 本地账号映射

不建议把组织特定的人效统计结果反写回共享用户表。
