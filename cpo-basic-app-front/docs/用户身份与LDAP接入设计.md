# 鑫图平台用户身份与 LDAP 接入设计

## 1. 目标

鑫图平台用户管理需要同时承接本地账号、飞书用户和三方 IT 工作台用户。后续用户数据可能由外部同步任务写入，因此身份标识必须与登录账号解耦，支持一人多标识、来源追踪和撤销绑定。

当前前端用户管理已预留以下字段：

```text
feishuUserId          飞书 user_id
itWorkbenchUserId     三方 IT 工作台用户 ID
identitySource        LOCAL / LDAP / SYNC
```

当前阶段已经按最小可落地方案直接扩展 `auth-all.sys_user`，前端提交的以下字段可随用户新增、编辑一起落库：

```text
feishu_user_id
it_workbench_user_id
identity_source
ldap_account
```

其中 `ldap_account` 不再作为前端单独维护字段，而是在后端按以下规则自动处理：

- `identitySource=LDAP` 时，`ldap_account = account`
- `identitySource=LOCAL` 或 `SYNC` 时，不要求单独填写 LDAP 账号

## 2. 当前实现与后续演进

### 2.1 当前实现

第一阶段先不引入独立身份表，而是在 `sys_user` 上直接补充字段，优先解决：

- 用户池新增/编辑可保存扩展身份字段
- 用户列表可回显扩展身份字段
- 当前登录用户基础信息可回显飞书 ID、IT 工作台 ID
- 旧用户默认 `identity_source=LOCAL`

当前字段建议如下：

```sql
ALTER TABLE sys_user
  ADD COLUMN feishu_user_id varchar(128) NULL,
  ADD COLUMN it_workbench_user_id varchar(128) NULL,
  ADD COLUMN identity_source varchar(20) NOT NULL DEFAULT 'LOCAL',
  ADD COLUMN ldap_account varchar(128) NULL;
```

### 2.2 后续演进

当一个本地用户需要绑定多个三方身份时，再从 `sys_user` 迁移到独立身份表：

不要继续向 `sys_user` 无限增加第三方字段，建议新增用户扩展表：

```sql
CREATE TABLE sys_user_identity (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  identity_source VARCHAR(20) NOT NULL,
  external_user_id VARCHAR(128) NULL,
  external_account VARCHAR(128) NULL,
  tenant_id VARCHAR(128) NULL,
  is_primary TINYINT NOT NULL DEFAULT 0,
  is_active TINYINT NOT NULL DEFAULT 1,
  last_sync_time DATETIME NULL,
  create_time DATETIME NOT NULL,
  update_time DATETIME NOT NULL,
  UNIQUE KEY uk_user_source_external (user_id, identity_source, external_user_id),
  KEY idx_source_external (identity_source, external_user_id),
  CONSTRAINT fk_user_identity_user FOREIGN KEY (user_id) REFERENCES sys_user(user_id)
);
```

LDAP 连接配置不建议放在用户表或用户扩展表中，建议独立为租户级配置，并且敏感字段使用密钥管理或密文存储：

```text
ldap_connection
  tenant_id
  enabled
  host / port
  use_tls
  base_dn
  user_search_base
  user_search_filter
  bind_account_ciphertext
  bind_password_ciphertext
  user_attribute
  display_name_attribute
  email_attribute
  last_test_time
  last_test_status
```

## 3. LDAP 备用登录流程

```text
用户打开登录页
  -> auth-all 返回 loginMode=LOCAL / LDAP / MIXED
  -> LOCAL: 仅展示本地登录
  -> LDAP: 仅展示 LDAP 登录
  -> MIXED: 允许用户切换本地 / LDAP
  -> 用户输入账号和密码
  -> 若选择 LOCAL，前端沿用当前本地密码摘要流程
  -> 若选择 LDAP，前端提交明文密码到 auth-all 做 LDAP bind
  -> auth-all 查本地用户账号是否已存在
  -> 按平台 LDAP 配置搜索并认证
  -> 认证通过后签发鑫图平台会话 token
```

LDAP 登录必须是租户级显式开关，建议提供：

- 是否启用 LDAP。
- 是否允许本地账号作为回退。
- 是否允许 LDAP 首次登录自动创建用户。
- 首次登录默认工作空间。
- LDAP 用户的默认角色和权限模板。
- 用户离职或 LDAP 禁用后的本地账号处理策略。

LDAP 连接测试必须在后端完成。前端不得连接 LDAP 服务器，不得保存 bind password，不得把密码写入日志或浏览器本地存储。

## 4. API 契约建议

### 4.1 用户身份

```text
GET    /auth/sys/user/:userId/identities
POST   /auth/sys/user/:userId/identities
PATCH  /auth/sys/user/:userId/identities/:identityId
DELETE /auth/sys/user/:userId/identities/:identityId
```

示例请求：

```json
{
  "identitySource": "FEISHU",
  "externalUserId": "ou_xxx",
  "externalAccount": "user@example.com",
  "tenantId": "tenant_xxx",
  "isPrimary": true
}
```

### 4.2 LDAP 配置

```text
GET   /auth/tenants/:tenantId/ldap
PUT   /auth/tenants/:tenantId/ldap
POST  /auth/tenants/:tenantId/ldap/test
POST  /auth/tenants/:tenantId/ldap/preview-user
```

`GET` 接口只能返回脱敏后的配置状态，不返回 bind password、密钥或完整连接凭据。

### 4.3 登录

本地登录和 LDAP 登录统一返回鑫图平台会话结构：

```json
{
  "loginSource": "LOCAL",
  "userId": 10001,
  "account": "zhangsan",
  "identityBindings": [
    {
      "source": "LDAP",
      "externalUserId": "uid=zhangsan"
    }
  ],
  "tokenInfo": {}
}
```

登录错误统一返回机器可识别的错误码，例如：

```text
LDAP_DISABLED
LDAP_CONNECTION_FAILED
LDAP_USER_NOT_FOUND
LDAP_PASSWORD_INVALID
IDENTITY_CONFLICT
```

## 5. 同步约束

- 外部同步以 `identitySource + externalUserId + tenantId` 做幂等键。
- 不使用邮箱作为唯一身份键，邮箱可能变化或被复用。
- 一个本地用户可以绑定多个外部身份，但同一租户下同一外部 ID 不能绑定多个本地用户。
- 同步任务只能更新声明允许更新的字段，不得覆盖本地管理员设置的角色和权限。
- 用户删除、禁用和身份解绑必须记录审计。
- 飞书、LDAP 和三方 IT 工作台的用户标识不得在前端作为授权依据，最终权限必须由后端根据会话和租户上下文判断。

## 6. 当前接入状态

当前 `cpo-basic-app-front` 已完成：

- 用户编辑弹窗中预留飞书 `user_id`。
- 用户编辑弹窗中预留三方 IT 工作台 ID。
- 用户编辑弹窗中预留身份来源。
- LDAP 用户不再单独填写 LDAP 账号，直接复用登录账号。
- 工作台账号卡预留飞书 ID 和 IT 工作台 ID 展示。
- 登录页已支持读取 `/auth/loginMode` 并切换本地 / LDAP 登录。

当前 `auth-all` 已完成：

- `sys_user` 扩展字段落库。
- 用户池新增/编辑接口支持扩展字段。
- 用户分页查询返回扩展字段。
- `auth/userBaseInfo` 返回飞书 ID、IT 工作台 ID、身份来源和 LDAP 账号。
- `auth/loginMode` 返回当前登录模式和可选项。
- `authentication.login-mode`、`authentication.ldap.*` 已纳入 Nacos 配置样例。

尚待后续阶段完成：

- LDAP 独立连接配置表和连接测试接口。
- LDAP 回退登录和首次登录映射。
- 多身份绑定表 `sys_user_identity`。
- 字段级权限、租户隔离和审计。
