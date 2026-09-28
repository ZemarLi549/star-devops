# AI人效自动化用户操作指引

更新时间：2026-08-11

## 1. 适用对象

本文用于指导普通用户完成以下操作：

- 安装飞书采集器
- 完成飞书授权
- 启动定时采集
- 查看采集结果
- 查看 AI 日报

## 2. 操作步骤

### 步骤 1：准备飞书应用权限

请联系管理员或自行确认飞书应用已开通：

- `offline_access`
- `im:chat:readonly`
- `im:message:readonly`
- `im:message.p2p_msg:get_as_user`
- `im:message.group_msg:get_as_user`

注意：

- 这些权限必须由用户本人在飞书授权页手动确认。
- 平台不会绕过用户代授权，也不会默认采集未授权用户的聊天内容。
- 如果后续用户手动撤销授权、应用权限被关闭，或者 `refresh_token` 过期，就需要重新走一次授权。

并确认安全设置中已配置：

- `http://127.0.0.1:17891/callback`

### 步骤 2：初始化采集器

```bash
cd cpo-feishu-collector-plugin
cp config/application.example.yaml config/application.yaml
bash bin/install.sh E0028517
```

### 步骤 3：完成授权

```bash
bash bin/authorize.sh E0028517
```

浏览器打开授权链接，授权成功后即可返回。

如果授权失败：

- 先检查飞书开放平台中配置的重定向地址是否与本地回调地址完全一致。
- 再检查应用是否已发布，所需 scope 是否已全部开通。
- 授权失败、令牌刷新失败、采集失败、回传失败，都会记录到平台通知中心。

### 步骤 4：执行一次采集验证

```bash
bash bin/collect-once.sh --user E0028517
```

### 步骤 5：查看结果

查看：

- `data/E0028517/latest-report.json`
- `data/E0028517/raw-chat/*.json`

### 步骤 6：启动定时守护

```bash
bash bin/start.sh
```

查看状态：

```bash
bash bin/status.sh
```

停止：

```bash
bash bin/stop.sh
```

## 3. 默认运行规则

- 默认每天 `17:30` 自动执行一次，统计窗口默认最近 `1` 天
- 默认不需要用户自己写 `crontab`
- 原始消息明细默认保留 15 天
- 聚合统计默认保留 180 天
- 需要手动关闭时，直接执行 `bash bin/stop.sh`

## 4. 本地历史数据清理

用户侧采集器只负责清理本机缓存，不会删除服务端空间 ES 数据。

清理本机历史文件：

```bash
cd cpo-feishu-collector-plugin
bash bin/clean-history.sh
```

默认清理范围：

- 本地聊天原始记录：15 天以前
- 本地日报、会议、文档历史：按空间配置的统计保留期处理

服务端 ES 历史数据由所属空间管理员维护，普通用户不要直接操作 ES。

## 5. 常见问题

### 4.1 授权成功但采集不到消息

检查：

- 是否已重新授权最新权限
- 是否群聊被正则或名称排除了
- 是否消息被识别为机器人 / 系统模板 / 闲聊噪声

### 4.2 想换统计范围

修改：

- `filters.include-group-names`
- `filters.exclude-group-names`
- `filters.include-group-regexes`
- `filters.exclude-group-regexes`

### 4.3 想立即生成日报

确认 AI 人效服务和 AstrBot 已部署完成后，进入平台页面即可生成。
