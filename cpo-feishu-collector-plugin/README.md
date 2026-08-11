# cpo-feishu-collector-plugin

`cpo-feishu-collector-plugin` 是鑫图平台的个人部署式飞书协同采集插件。

当前 Go 版客户端已经能做这些事：

- 每个用户自己配置自己的飞书应用 `App ID / App Secret`。
- 启动前先生成合理的飞书授权链接，由用户自己完成授权。
- 第一版优先采集工作相关聊天，会议和文档保持独立开关，不互相影响。
- 每天默认 `17:30` 触发一次，统计窗口默认最近 `1` 天。
- 默认统一排除机器人、工作流、应用通知交互，不计入个人工作统计。
- 支持群聊 ID、群聊名称、群聊正则、关键词等多维过滤。
- 生成本地 JSON 和 Markdown 报告。
- 预留回传服务端的接口。
- 普通用户入口走 shell 脚本和 Go 可执行程序，不要求手动执行 `npm`。

当前聊天采集默认需要的飞书用户授权权限：

- `offline_access`
- `im:chat:readonly`
- `im:message:readonly`
- `im:message.p2p_msg:get_as_user`
- `im:message.group_msg:get_as_user`

应用后台还需要：

- 配置重定向 URL：`http://127.0.0.1:17891/callback`
- 打开刷新 `user_access_token` 开关
- 配置后发布应用

## 快速开始

```bash
bash bin/install.sh E0028517
bash bin/authorize.sh E0028517
bash bin/collect-e0028517.sh
```

查看输出：

```bash
bash bin/show-report.sh E0028517
```

停止服务：

```bash
bash bin/stop.sh
```

一键部署：

```bash
bash bin/quick-deploy.sh E0028517
```

## 文档

- [架构设计](./docs/架构设计.md)
- [使用说明](./docs/使用说明.md)
- [安装部署与防火墙说明](./docs/安装部署与防火墙说明.md)
- [脚本集合](./docs/脚本集合.md)

## 当前说明

- 当前试点用户：`E0028517`
- 当前第一版优先落真实聊天授权采集
- 会议和文档采集先保留独立开关，拿不到也不阻塞聊天采集
- 每个用户只能使用自己的飞书应用授权，不走统一公共机器人
