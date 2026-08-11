# cpo-ai-productivity-service

`cpo-ai-productivity-service` 是 AI 人效自动化系统的后端服务，采用 `Python 3 + Tornado`。

## 当前职责

- 承接 AI 人效自动化系统子项目的后端 API。
- 统一收敛工单运营、飞书统计、会议文档统计、日报周报分析。
- 后续接 PostgreSQL 工单库、飞书采集插件、ES、CMDB、Zabbix。

## 当前接口

- `GET /healthz`
- `GET /api/productivity/overview/self`
- `GET /api/productivity/workorder/self`
- `GET /api/productivity/report/daily/latest`
- `POST /api/productivity/report/daily/summary/generate`
- `GET /api/productivity/report/daily/summary/latest`

## 本地启动

```bash
cd cpo-ai-productivity-service
python3 -m pip install -r requirements.txt
cp config/application.example.yaml config/application.yaml
python3 app.py
```

## 配置原则

- 通用配置统一走 Nacos。
- 敏感信息不进入前端源码、不进入普通用户下载包。
- 工单运营已统一归入 AI 人效自动化系统，不再单独作为首页服务入口。
- 这版支持直接连 `AstrBot` OpenAPI 生成 AI 日报，不强依赖 `api-gateway`。
- `DeepSeek` 等模型密钥应配置在 `AstrBot` 或环境变量中，不写入仓库文件。
