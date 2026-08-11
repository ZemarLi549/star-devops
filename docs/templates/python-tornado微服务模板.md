# Python Tornado 微服务模板

## 目录

```text
service-name/
  app.py
  requirements.txt
  config/
    application.example.yaml
  logs/
```

## 最小接口

- `/healthz`
- 1 个业务接口

## 接入要求

- 配置通过 Nacos / 环境变量注入
- 通过 `cpo-api-gateway` 暴露
- 在 `scripts_dir/` 中提供启动脚本
