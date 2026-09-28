# cpo-ai-productivity-front

`cpo-ai-productivity-front` 是鑫图平台中的 AI 人效自动化系统独立微前端。

当前定位：

- 独立运行的人效统计前端
- 可被 `cpo-basic-app-front` 通过 qiankun 挂载
- 内含两个一级子菜单：
  - `人效概览`
  - `采集配置`

## 当前能力

- 展示飞书采集上报后的聊天、会议、文档统计摘要
- 展示 AstrBot 状态与 AI 日报生成入口
- 展示采集失败 / 授权失败 / 日报失败通知
- 展示用户侧采集器安装、授权、启停与过滤规则说明
- 兼容旧入口：
  - `/control/ai-productivity` -> `/ai-productivity`
  - `/control/productivity-config` -> `/ai-productivity?tab=collector`

## 本地开发

```bash
cd cpo-ai-productivity-front
PATH=/home/kali/.hermes/node/bin:$PATH npm install
PATH=/home/kali/.hermes/node/bin:$PATH npm run dev -- --host 0.0.0.0 --port 3011
```

访问：

- 独立页面：`http://127.0.0.1:3011/`
- 采集配置页签：`http://127.0.0.1:3011/?tab=collector`

## 构建

```bash
cd cpo-ai-productivity-front
PATH=/home/kali/.hermes/node/bin:$PATH npm run build
```

构建产物输出到：

```text
dist/
```

## 挂载到宿主

宿主 `cpo-basic-app-front` 当前通过 qiankun 加载：

- 微应用名：`ai-productivity`
- 开发地址：`http://localhost:3011/`
- 宿主路径：`/ai-productivity`

本地宿主入口：

```text
http://127.0.0.1/ai-productivity
```

## 启停脚本

统一脚本放在根目录 `scripts_dir/`：

- `bash scripts_dir/start-cpo-ai-productivity-front.sh`
- `bash scripts_dir/stop-cpo-ai-productivity-front.sh`

支持两种模式：

- `FRONT_MODE=dev`
  - 启动 Vite 开发服务
- `FRONT_MODE=preview`
  - 启动 `vite preview`

示例：

```bash
FRONT_MODE=dev bash scripts_dir/start-cpo-ai-productivity-front.sh
FRONT_MODE=preview bash scripts_dir/start-cpo-ai-productivity-front.sh
bash scripts_dir/stop-cpo-ai-productivity-front.sh
```

## 后端接口

默认通过前端代理访问：

```text
/productivity-api -> http://127.0.0.1:9060
```

如需改动，可修改：

```text
.env.example
vite.config.ts
```

## 注意事项

- 这个前端页面只展示采集器操作说明，不保存用户的 App Secret、token 或本地过滤配置。
- 正式生产环境建议将 `dist/` 挂到 Nginx，并配置 `/ai-productivity/` 作为静态前缀。
- 采集器下载说明已经下沉到该微前端的 `采集配置` 页签，不再作为工作台一级服务入口。
