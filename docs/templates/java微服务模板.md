# Java 微服务模板

## 适用场景

- 现有 `auth-all` 体系内扩模块
- 需要复用 Spring Cloud / Nacos / Feign

## 模板要求

- `bootstrap.yml`
- `/healthz`
- 统一响应体
- Nacos 配置
- 网关路由接入

## 当前建议

如果是新业务优先独立服务，可不强绑到 `auth-all`。
